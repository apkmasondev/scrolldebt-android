package com.example.scrolldebt.data.workers

import android.app.Notification
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.app.Service
import android.content.Context
import android.content.Intent
import android.os.Build
import android.os.IBinder
import androidx.core.app.NotificationCompat
import com.example.scrolldebt.MainActivity
import com.example.scrolldebt.data.repository.PreferencesManager
import com.example.scrolldebt.domain.usecases.BrutalTruthEngine
import com.example.scrolldebt.utils.AppUsageInfo
import com.example.scrolldebt.utils.UsageStatsHelper
import kotlinx.coroutines.*
import java.text.SimpleDateFormat
import java.util.*

import dagger.hilt.android.AndroidEntryPoint
import javax.inject.Inject
import com.example.scrolldebt.utils.NotificationHelper

@AndroidEntryPoint
class DoomTrackerService : Service() {

    @Inject lateinit var prefs: PreferencesManager
    @Inject lateinit var helper: UsageStatsHelper
    @Inject lateinit var notificationHelper: NotificationHelper

    private var job: Job? = null
    private var scope: CoroutineScope? = null
    
    companion object {
        private const val CHANNEL_ID = "doomscroll_tracker"
        private const val NOTIFICATION_ID = 1002
    }

    override fun onCreate() {
        super.onCreate()
        createNotificationChannel()
        startForeground(NOTIFICATION_ID, buildForegroundNotification())
    }

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        // Re-initialize scope on start/restart to fix P23
        job?.cancel()
        job = SupervisorJob()
        scope = CoroutineScope(Dispatchers.IO + job!!)
        startTracking()
        
        // Keeps the service running if system kills it
        return START_STICKY
    }

    override fun onBind(intent: Intent?): IBinder? {
        return null
    }

    private fun createNotificationChannel() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val channel = NotificationChannel(
                CHANNEL_ID,
                "ScrollDebt Tracker (Live)",
                NotificationManager.IMPORTANCE_LOW // Low priority so it doesn't ring but stays in status bar
            ).apply {
                description = "Keeps ScrollDebt alive to provide real-time alerts."
            }
            val manager = getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
            manager.createNotificationChannel(channel)
        }
    }

    private fun buildForegroundNotification(): Notification {
        val lang = prefs.getLanguage()
        val title = when (lang) {
            "en" -> "ScrollDebt Tracker"
            "es" -> "Rastreador ScrollDebt"
            "fr" -> "Suivi ScrollDebt"
            "de" -> "ScrollDebt Tracker"
            else -> "Śledzenie ScrollDebt"
        }
        val text = when (lang) {
            "en" -> "Monitoring screen time..."
            "es" -> "Monitoreando el tiempo de pantalla..."
            "fr" -> "Surveillance du temps d'écran..."
            "de" -> "Bildschirmzeit wird überwacht..."
            else -> "Monitorowanie czasu przed ekranem..."
        }
        
        val intent = Intent(this, MainActivity::class.java).apply {
            flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
        }
        val pendingIntent = PendingIntent.getActivity(
            this, 0, intent,
            PendingIntent.FLAG_IMMUTABLE
        )

        return NotificationCompat.Builder(this, CHANNEL_ID)
            .setContentTitle(title)
            .setContentText(text)
            .setSmallIcon(com.example.scrolldebt.R.drawable.ic_hourglass_notification)
            .setContentIntent(pendingIntent)
            .setOngoing(true)
            .build()
    }

    private fun startTracking() {
        scope?.launch {
            while (isActive) {
                try {
                    if (!prefs.isPushNotificationsEnabled() || prefs.getTrackingMode() != com.example.scrolldebt.data.repository.TrackingMode.REALTIME) {
                        stopSelf()
                        break
                    }

                    if (helper.hasUsageStatsPermission()) {
                        checkLimits()
                    }
                } catch (e: Exception) {
                    android.util.Log.e("DoomTracker", "Error in tracking loop", e)
                }

                delay(60_000L) // Wait exactly 1 minute
            }
        }
    }

    private fun checkLimits() {
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
    }

    override fun onDestroy() {
        super.onDestroy()
        job?.cancel()
    }
}
