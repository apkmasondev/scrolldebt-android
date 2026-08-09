package com.example.scrolldebt.data.workers

import android.content.Context
import com.example.scrolldebt.utils.DateKeys
import com.example.scrolldebt.utils.UsageStatsHelper
import com.example.scrolldebt.data.repository.PreferencesManager
import androidx.work.CoroutineWorker
import androidx.work.WorkerParameters
import com.example.scrolldebt.data.db.ScrollDebtDatabase
import com.example.scrolldebt.data.models.UsageRecord
import com.example.scrolldebt.widget.DoomClockWidget
import androidx.glance.appwidget.updateAll

import androidx.hilt.work.HiltWorker
import dagger.assisted.Assisted
import dagger.assisted.AssistedInject
import kotlinx.serialization.json.Json
import kotlinx.serialization.json.JsonObject
import kotlinx.serialization.json.JsonPrimitive

@HiltWorker
class DailySyncWorker @AssistedInject constructor(
    @Assisted context: Context,
    @Assisted params: WorkerParameters,
    private val helper: UsageStatsHelper,
    private val db: ScrollDebtDatabase
) : CoroutineWorker(context, params) {

    override suspend fun doWork(): Result {
        if (!helper.hasUsageStatsPermission()) {
            return Result.failure()
        }

        // UsageStatsHelper already filters by tracked apps
        val filteredBreakdown = helper.getTodayUsageStats()
        val totalMs = filteredBreakdown.sumOf { it.timeSpentMs }

        // Serialize breakdown to JSON using kotlinx.serialization
        val jsonMap = filteredBreakdown.associate { it.packageName to JsonPrimitive(it.timeSpentMs) }
        val jsonBreakdown = Json.encodeToString(JsonObject.serializer(), JsonObject(jsonMap))

        val record = UsageRecord(
            DateKeys.today(),
            totalMs,
            jsonBreakdown
        )

        // Write to database
        db.usageDao().insertOrUpdateRecord(record)

        // Push the fresh total to the widget. Without this the widget only ever changed on
        // its own 30-minute system refresh, so right after a sync it could still be showing
        // a figure from before the one we just wrote.
        runCatching { DoomClockWidget().updateAll(applicationContext) }
            .onFailure { android.util.Log.w("DailySyncWorker", "Widget update failed", it) }

        return Result.success()
    }
}
