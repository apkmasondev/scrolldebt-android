package com.example.scrolldebt.data.workers

import android.content.Context
import android.content.Intent
import android.os.Build
import android.util.Log
import androidx.work.ExistingPeriodicWorkPolicy
import androidx.work.PeriodicWorkRequestBuilder
import androidx.work.WorkManager
import com.example.scrolldebt.data.repository.PreferencesManager
import com.example.scrolldebt.data.repository.TrackingMode
import java.util.concurrent.TimeUnit

/**
 * Owns the "which background tracker should be running right now" decision.
 *
 * The two tracking modes are mutually exclusive and both used to be started from two
 * different places (Application.onCreate and the ViewModel) with subtly different rules -
 * one used KEEP, the other CANCEL_AND_REENQUEUE, and only one of them cancelled the mode it
 * was replacing. Centralising it here means enabling a mode always disables the other.
 */
object TrackingScheduler {

    private const val TAG = "TrackingScheduler"
    const val THRESHOLD_WORK_NAME = "ScrollDebtThresholdCheck"

    /** WorkManager's floor for periodic work; anything smaller is silently clamped to this. */
    private const val THRESHOLD_INTERVAL_MINUTES = 15L

    /**
     * Brings the background trackers in line with the current preferences.
     *
     * @param fromBackground true when called from a context that is not user-visible
     *        (Application.onCreate, boot). Android 12+ forbids starting a foreground service
     *        from there, so Sniper mode is left for the next time the user opens the app.
     */
    fun sync(context: Context, prefs: PreferencesManager, fromBackground: Boolean = false) {
        val pushEnabled = prefs.isPushNotificationsEnabled()
        val mode = prefs.getTrackingMode()

        if (pushEnabled && mode == TrackingMode.REALTIME) {
            cancelThresholdWorker(context)
            startTrackerService(context, fromBackground)
        } else {
            stopTrackerService(context)
            if (pushEnabled && mode == TrackingMode.BATTERY_SAVER) {
                enqueueThresholdWorker(context)
            } else {
                cancelThresholdWorker(context)
            }
        }
    }

    private fun startTrackerService(context: Context, fromBackground: Boolean) {
        val intent = Intent(context, DoomTrackerService::class.java)
        try {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                context.startForegroundService(intent)
            } else {
                context.startService(intent)
            }
        } catch (e: Exception) {
            // ForegroundServiceStartNotAllowedException (API 31+) when the process was woken
            // in the background, and BackgroundServiceStartNotAllowedException on some OEMs.
            // Neither is recoverable here; Sniper mode resumes when the user next opens the
            // app. Crashing the whole process over a notification would be far worse.
            Log.w(TAG, "Could not start tracker service (fromBackground=$fromBackground)", e)
        }
    }

    private fun stopTrackerService(context: Context) {
        runCatching { context.stopService(Intent(context, DoomTrackerService::class.java)) }
    }

    private fun enqueueThresholdWorker(context: Context) {
        val request = PeriodicWorkRequestBuilder<ThresholdWorker>(
            THRESHOLD_INTERVAL_MINUTES, TimeUnit.MINUTES
        ).build()

        // KEEP, not CANCEL_AND_REENQUEUE: re-enqueuing on every app start would reset the
        // period each time and, for a user who opens the app often, mean it never fires.
        WorkManager.getInstance(context).enqueueUniquePeriodicWork(
            THRESHOLD_WORK_NAME,
            ExistingPeriodicWorkPolicy.KEEP,
            request
        )
    }

    private fun cancelThresholdWorker(context: Context) {
        WorkManager.getInstance(context).cancelUniqueWork(THRESHOLD_WORK_NAME)
    }
}
