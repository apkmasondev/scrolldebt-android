package com.example.scrolldebt.data.workers

import android.content.Context
import com.example.scrolldebt.utils.UsageStatsHelper
import com.example.scrolldebt.data.repository.PreferencesManager
import androidx.work.CoroutineWorker
import androidx.work.WorkerParameters
import com.example.scrolldebt.data.db.ScrollDebtDatabase
import com.example.scrolldebt.data.models.UsageRecord
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

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

        // Current date in format YYYY-MM-DD
        val sdf = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault())
        val dateString = sdf.format(Date())

        val record = UsageRecord(
            dateString,
            totalMs,
            jsonBreakdown
        )

        // Write to database
        db.usageDao().insertOrUpdateRecord(record)

        return Result.success()
    }
}
