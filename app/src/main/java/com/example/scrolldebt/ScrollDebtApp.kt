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
import com.example.scrolldebt.data.workers.TrackingScheduler
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

    @Inject
    lateinit var prefs: com.example.scrolldebt.data.repository.PreferencesManager

    override val workManagerConfiguration: Configuration
        get() = Configuration.Builder()
            .setWorkerFactory(workerFactory)
            .build()

    override fun onCreate() {
        super.onCreate()
        notificationHelper.createChannel()
        scheduleSyncWorker()

        // onCreate also runs when the process is woken in the background (WorkManager, the
        // widget, a broadcast), where Android 12+ refuses foreground-service starts. Flagged
        // as such so TrackingScheduler logs instead of taking the process down.
        TrackingScheduler.sync(this, prefs, fromBackground = true)
    }

    private fun scheduleSyncWorker() {
        val syncRequest = PeriodicWorkRequestBuilder<DailySyncWorker>(SYNC_INTERVAL_HOURS, TimeUnit.HOURS)
            .setInitialDelay(15, TimeUnit.MINUTES)
            .build()

        WorkManager.getInstance(this).enqueueUniquePeriodicWork(
            "ScrollDebtDailySync",
            ExistingPeriodicWorkPolicy.KEEP,
            syncRequest
        )
    }

    companion object {
        /** How often usage totals are written to the database. */
        const val SYNC_INTERVAL_HOURS = 4L
    }
}
