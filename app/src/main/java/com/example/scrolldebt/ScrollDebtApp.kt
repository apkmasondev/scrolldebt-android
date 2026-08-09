package com.example.scrolldebt

import android.app.Application
import com.example.scrolldebt.data.workers.ThresholdWorker
import android.app.NotificationChannel
import android.app.NotificationManager
import android.content.Context
import android.os.Build
import androidx.work.ExistingPeriodicWorkPolicy
import androidx.work.PeriodicWorkRequestBuilder
import androidx.work.WorkManager
import com.example.scrolldebt.data.workers.DailySyncWorker
import java.util.concurrent.TimeUnit

import dagger.hilt.android.HiltAndroidApp
import androidx.hilt.work.HiltWorkerFactory
import androidx.work.Configuration
import javax.inject.Inject

@HiltAndroidApp
class ScrollDebtApp : Application(), Configuration.Provider {

    @Inject
    lateinit var workerFactory: HiltWorkerFactory
    
    @Inject
    lateinit var notificationHelper: com.example.scrolldebt.utils.NotificationHelper

    override val workManagerConfiguration: Configuration
        get() = Configuration.Builder()
            .setWorkerFactory(workerFactory)
            .build()

    override fun onCreate() {
        super.onCreate()
        notificationHelper.createChannel()
        scheduleSyncWorker()
        scheduleThresholdWorker()
    }

    private fun scheduleSyncWorker() {
        val syncRequest = PeriodicWorkRequestBuilder<DailySyncWorker>(4, TimeUnit.HOURS)
            .setInitialDelay(15, TimeUnit.MINUTES)
            .build()

        WorkManager.getInstance(this).enqueueUniquePeriodicWork(
            "ScrollDebtDailySync",
            ExistingPeriodicWorkPolicy.KEEP,
            syncRequest
        )
    }

    private fun scheduleThresholdWorker() {
        val prefs = com.example.scrolldebt.data.repository.PreferencesManager(this)
        val pushEnabled = prefs.isPushNotificationsEnabled()
        val mode = prefs.getTrackingMode()

        if (pushEnabled && mode == com.example.scrolldebt.data.repository.TrackingMode.REALTIME) {
            val intent = android.content.Intent(this, com.example.scrolldebt.data.workers.DoomTrackerService::class.java)
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                startForegroundService(intent)
            } else {
                startService(intent)
            }
        } else if (pushEnabled && mode == com.example.scrolldebt.data.repository.TrackingMode.BATTERY_SAVER) {
            val thresholdRequest = PeriodicWorkRequestBuilder<com.example.scrolldebt.data.workers.ThresholdWorker>(15, TimeUnit.MINUTES).build()
            WorkManager.getInstance(this).enqueueUniquePeriodicWork(
                "ScrollDebtThresholdCheck",
                ExistingPeriodicWorkPolicy.KEEP,
                thresholdRequest
            )
        }
    }
}
