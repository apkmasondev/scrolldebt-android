package com.example.scrolldebt.data.workers

import android.app.NotificationChannel
import com.example.scrolldebt.utils.UsageStatsHelper
import com.example.scrolldebt.data.repository.PreferencesManager
import com.example.scrolldebt.utils.AppUsageInfo
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.os.Build
import androidx.core.app.NotificationCompat
import androidx.work.CoroutineWorker
import androidx.work.WorkerParameters
import com.example.scrolldebt.MainActivity
import com.example.scrolldebt.R
import com.example.scrolldebt.domain.usecases.BrutalTruthEngine
import com.example.scrolldebt.utils.Localization
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

import androidx.hilt.work.HiltWorker
import dagger.assisted.Assisted
import dagger.assisted.AssistedInject
import com.example.scrolldebt.utils.NotificationHelper

@HiltWorker
class ThresholdWorker @AssistedInject constructor(
    @Assisted context: Context,
    @Assisted params: WorkerParameters,
    private val prefs: PreferencesManager,
    private val helper: UsageStatsHelper,
    private val notificationHelper: NotificationHelper
) : CoroutineWorker(context, params) {

    override suspend fun doWork(): Result {
        if (!prefs.isPushNotificationsEnabled()) {
            return Result.success()
        }

        if (prefs.getTrackingMode() != com.example.scrolldebt.data.repository.TrackingMode.BATTERY_SAVER) {
            return Result.success()
        }

        if (!helper.hasUsageStatsPermission()) {
            return Result.failure()
        }

        // UsageStatsHelper already filters by tracked apps
        val filteredBreakdown = helper.getTodayUsageStats()
        val totalMs = filteredBreakdown.sumOf { it.timeSpentMs }
        val thresholdMs = prefs.getNotificationThresholdMinutes() * 60 * 1000L

        if (totalMs > thresholdMs) {
            val sdf = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault())
            val todayString = sdf.format(Date())
            
            if (prefs.getLastNotificationDate() != todayString) {
                notificationHelper.sendRoastNotification(totalMs, filteredBreakdown, prefs.getLanguage())
                prefs.setLastNotificationDate(todayString)
            }
        }

        return Result.success()
    }
}
