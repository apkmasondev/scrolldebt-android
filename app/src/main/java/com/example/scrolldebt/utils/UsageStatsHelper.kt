package com.example.scrolldebt.utils

import android.app.AppOpsManager
import android.app.usage.UsageStatsManager
import android.content.Context
import android.os.Process
import java.time.LocalDate
import java.time.ZoneId
import com.example.scrolldebt.utils.AppConstants
import com.example.scrolldebt.data.repository.PreferencesManager

import javax.inject.Inject
import dagger.hilt.android.qualifiers.ApplicationContext

data class AppUsageInfo(
    val packageName: String,
    val appName: String,
    val timeSpentMs: Long
)

class UsageStatsHelper @Inject constructor(@ApplicationContext private val context: Context) {

    private val usageStatsManager = context.getSystemService(Context.USAGE_STATS_SERVICE) as UsageStatsManager

    // Held rather than rebuilt per call: getTodayUsageStats() runs once a minute in Sniper
    // mode and on every UI refresh, and each call was allocating a fresh manager.
    private val preferencesManager by lazy { PreferencesManager(context) }

    fun hasUsageStatsPermission(): Boolean {
        val appOps = context.getSystemService(Context.APP_OPS_SERVICE) as AppOpsManager
        val mode = if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.Q) {
            appOps.unsafeCheckOpNoThrow(
                AppOpsManager.OPSTR_GET_USAGE_STATS,
                Process.myUid(),
                context.packageName
            )
        } else {
            @Suppress("DEPRECATION")
            appOps.checkOpNoThrow(
                AppOpsManager.OPSTR_GET_USAGE_STATS,
                Process.myUid(),
                context.packageName
            )
        }
        return mode == AppOpsManager.MODE_ALLOWED
    }

    fun getTodayUsageStats(): List<AppUsageInfo> {
        if (!hasUsageStatsPermission()) {
            return emptyList()
        }

        // Start of the local day. Deliberately not Calendar.set(HOUR_OF_DAY, 0): in time zones
        // whose DST jump happens at midnight (e.g. America/Santiago, Asia/Beirut) 00:00 does
        // not exist on the transition day, and Calendar silently rolls to a different instant.
        // atStartOfDay(zone) resolves that gap to the first instant the day actually has.
        val startTime = LocalDate.now()
            .atStartOfDay(ZoneId.systemDefault())
            .toInstant()
            .toEpochMilli()
        val endTime = System.currentTimeMillis()

        val usageEvents = usageStatsManager.queryEvents(startTime, endTime)
        val event = android.app.usage.UsageEvents.Event()
        
        val totalTimesMs = mutableMapOf<String, Long>()
        val currentSessionStart = mutableMapOf<String, Long>()
        val seenPackages = mutableSetOf<String>()

        val trackedApps = preferencesManager.getTrackedApps()

        while (usageEvents.hasNextEvent()) {
            usageEvents.getNextEvent(event)
            val pkg = event.packageName
            if (trackedApps.contains(pkg)) {
                val isFirstEvent = seenPackages.add(pkg)

                if (event.eventType == android.app.usage.UsageEvents.Event.ACTIVITY_RESUMED) {
                    currentSessionStart[pkg] = event.timeStamp
                } else if (event.eventType == android.app.usage.UsageEvents.Event.ACTIVITY_PAUSED || 
                           event.eventType == android.app.usage.UsageEvents.Event.ACTIVITY_STOPPED) {
                    val sessionStart = currentSessionStart.remove(pkg)
                    if (sessionStart != null) {
                        val sessionDuration = event.timeStamp - sessionStart
                        totalTimesMs[pkg] = (totalTimesMs[pkg] ?: 0L) + sessionDuration
                    } else if (isFirstEvent && event.eventType == android.app.usage.UsageEvents.Event.ACTIVITY_PAUSED) {
                        // Pierwsze dzisiejsze zdarzenie dla tej apki to PAUSE, więc musiała być aktywna od północy.
                        val sessionDuration = event.timeStamp - startTime
                        if (sessionDuration > 0) {
                            totalTimesMs[pkg] = (totalTimesMs[pkg] ?: 0L) + sessionDuration
                        }
                    }
                }
            }
        }

        // Dodaj czas dla aplikacji, które są nadal uruchomione na ekranie
        for ((pkg, sessionStart) in currentSessionStart) {
            val sessionDuration = endTime - sessionStart
            if (sessionDuration > 0) {
                totalTimesMs[pkg] = (totalTimesMs[pkg] ?: 0L) + sessionDuration
            }
        }

        val usageList = mutableListOf<AppUsageInfo>()
        val packageManager = context.packageManager
        for (pkg in trackedApps) {
            val timeMs = totalTimesMs[pkg] ?: 0L
            val displayName = try {
                val appInfo = packageManager.getApplicationInfo(pkg, 0)
                packageManager.getApplicationLabel(appInfo).toString()
            } catch (e: Exception) {
                null // Not installed
            }
            if (displayName != null) {
                usageList.add(AppUsageInfo(packageName = pkg, appName = displayName, timeSpentMs = timeMs))
            }
        }

        return usageList.sortedByDescending { it.timeSpentMs }
    }
}
