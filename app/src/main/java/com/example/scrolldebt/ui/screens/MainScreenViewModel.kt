package com.example.scrolldebt.ui.screens

import com.example.scrolldebt.R

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.scrolldebt.utils.AppUsageInfo
import com.example.scrolldebt.data.repository.PreferencesManager
import com.example.scrolldebt.utils.UsageStatsHelper
import com.example.scrolldebt.data.db.ScrollDebtDatabase
import com.example.scrolldebt.data.models.UsageRecord
import com.example.scrolldebt.domain.usecases.BrutalTruthEngine
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import kotlinx.coroutines.Dispatchers
import android.content.Context
import android.content.pm.PackageManager
import android.os.Build
import android.content.Intent
import androidx.lifecycle.ViewModel
import dagger.hilt.android.lifecycle.HiltViewModel
import dagger.hilt.android.qualifiers.ApplicationContext
import javax.inject.Inject

data class MainUiState(
    val hasPermission: Boolean = false,
    val isLoading: Boolean = true, // Added for P32 loading state
    val todayTotalTimeMs: Long = 0L,
    val appBreakdown: List<AppUsageInfo> = emptyList(),
    val installedApps: List<AppUsageInfo> = emptyList(),
    val brutalTruthMessage: String = "",
    val historicalRecords: List<UsageRecord> = emptyList(),
    val trackedApps: Set<String> = emptySet(),
    val thresholdMinutes: Int = 60,
    val streakThresholdMinutes: Int = 60,
    val brutalTruthEnabled: Boolean = true,
    val language: String = "en",
    val pushNotificationsEnabled: Boolean = false,
    val currentStreakDays: Int = 0,
    val weeklyTotalTimeMs: Long = 0L,
    // Must match PreferencesManager.getTrackingMode()'s default, otherwise Settings briefly
    // renders "Sniper" selected before loadSettings() replaces it with the stored value.
    val trackingMode: com.example.scrolldebt.data.repository.TrackingMode = com.example.scrolldebt.data.repository.TrackingMode.BATTERY_SAVER,
    val themeMode: Int = 2
)

@HiltViewModel
class MainScreenViewModel @Inject constructor(
    @ApplicationContext private val context: Context,
    private val usageStatsHelper: UsageStatsHelper,
    private val prefsManager: PreferencesManager,
    private val db: ScrollDebtDatabase,
    private val truthEngine: BrutalTruthEngine
) : ViewModel() {

    private val _state = MutableStateFlow(MainUiState())
    val state: StateFlow<MainUiState> = _state.asStateFlow()

    init {
        viewModelScope.launch(Dispatchers.IO) {
            loadSettings()
            checkPermissionAndRefreshSync()
            observeDatabase()
            loadInstalledAppsSync()
            _state.update { it.copy(isLoading = false) }
        }
    }

    fun checkPermissionAndRefresh() {
        viewModelScope.launch(Dispatchers.IO) {
            checkPermissionAndRefreshSync()
        }
    }

    private fun checkPermissionAndRefreshSync() {
        val hasPerm = usageStatsHelper.hasUsageStatsPermission()
        _state.update { it.copy(hasPermission = hasPerm) }
        if (hasPerm) {
            refreshUsageStatsSync()
        }
    }

    private fun loadSettings() {
        _state.update {
            it.copy(
                trackedApps = prefsManager.getTrackedApps(),
                thresholdMinutes = prefsManager.getNotificationThresholdMinutes(),
                streakThresholdMinutes = prefsManager.getStreakThresholdMinutes(),
                brutalTruthEnabled = prefsManager.isBrutalTruthEnabled(),
                language = prefsManager.getLanguage(),
                pushNotificationsEnabled = prefsManager.isPushNotificationsEnabled(),
                trackingMode = prefsManager.getTrackingMode(),
                themeMode = prefsManager.getThemeMode()
            )
        }
    }

    fun loadHistoricalRecords() {
        viewModelScope.launch(Dispatchers.IO) {
            val records = db.usageDao().getAllRecordsSync()
            _state.update { it.copy(historicalRecords = records) }
            recalculateStreakAndWeekly(_state.value.todayTotalTimeMs, records, _state.value.streakThresholdMinutes)
        }
    }

    private fun observeDatabase() {
        loadHistoricalRecords()
    }

    fun refreshUsageStats() {
        viewModelScope.launch(Dispatchers.IO) {
            refreshUsageStatsSync()
        }
    }

    private fun refreshUsageStatsSync() {
        val hasPerm = usageStatsHelper.hasUsageStatsPermission()
        if (!hasPerm) {
            _state.update { it.copy(hasPermission = false) }
            return
        }

        val allStats = usageStatsHelper.getTodayUsageStats()
        val tracked = _state.value.trackedApps

        val filteredBreakdown = allStats.filter { tracked.contains(it.packageName) }
        val totalMs = filteredBreakdown.sumOf { it.timeSpentMs }

        val roast = if (_state.value.brutalTruthEnabled) {
            truthEngine.getRoastMessage(totalMs, filteredBreakdown, _state.value.language)
        } else {
            context.getString(R.string.brutal_truth_disabled)
        }

        _state.update {
            it.copy(
                hasPermission = true,
                todayTotalTimeMs = totalMs,
                appBreakdown = filteredBreakdown,
                brutalTruthMessage = roast
            )
        }
        recalculateStreakAndWeekly(totalMs, _state.value.historicalRecords, _state.value.streakThresholdMinutes)
        
        val records = db.usageDao().getAllRecordsSync()
        _state.update { it.copy(historicalRecords = records) }
    }

    fun refreshRoastMessage() {
        val stateVal = _state.value
        val newRoast = truthEngine.getRoastMessage(stateVal.todayTotalTimeMs, stateVal.appBreakdown, stateVal.language)
        _state.update { it.copy(brutalTruthMessage = newRoast) }
    }

    fun toggleTrackedApp(packageName: String) {
        val currentTracked = _state.value.trackedApps.toMutableSet()
        if (currentTracked.contains(packageName)) {
            currentTracked.remove(packageName)
        } else {
            currentTracked.add(packageName)
        }
        prefsManager.setTrackedApps(currentTracked)
        _state.update { it.copy(trackedApps = currentTracked) }
        refreshUsageStats()
    }

    fun loadInstalledApps() {
        viewModelScope.launch(Dispatchers.IO) {
            loadInstalledAppsSync()
        }
    }

    private fun loadInstalledAppsSync() {
        val pm = context.packageManager
        val intent = Intent(Intent.ACTION_MAIN, null)
        intent.addCategory(Intent.CATEGORY_LAUNCHER)
        val resolveInfos = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            pm.queryIntentActivities(intent, PackageManager.ResolveInfoFlags.of(0L))
        } else {
            @Suppress("DEPRECATION")
            pm.queryIntentActivities(intent, 0)
        }
        
        val apps = resolveInfos.mapNotNull { resolveInfo ->
            val pkg = resolveInfo.activityInfo.packageName
            if (pkg == context.packageName) return@mapNotNull null // ignore self
            val name = resolveInfo.loadLabel(pm).toString()
            AppUsageInfo(pkg, name, 0L)
        }.distinctBy { it.packageName }.sortedBy { it.appName }

        _state.update { it.copy(installedApps = apps) }
    }

    fun setThreshold(minutes: Int) {
        prefsManager.setNotificationThresholdMinutes(minutes)
        prefsManager.setLastNotificationDate("") // allow notification again if threshold changes
        _state.update { it.copy(thresholdMinutes = minutes) }
    }

    fun setStreakThreshold(minutes: Int) {
        prefsManager.setStreakThresholdMinutes(minutes)
        _state.update { it.copy(streakThresholdMinutes = minutes) }
        recalculateStreakAndWeekly(_state.value.todayTotalTimeMs, _state.value.historicalRecords, minutes)
    }

    fun toggleBrutalTruth(enabled: Boolean) {
        prefsManager.setBrutalTruthEnabled(enabled)
        _state.update { it.copy(
            brutalTruthEnabled = enabled,
            brutalTruthMessage = "" // clear to force new roast generation if enabled
        ) }
        refreshUsageStats()
    }

    fun togglePushNotifications(enabled: Boolean) {
        prefsManager.setPushNotificationsEnabled(enabled)
        if (enabled) {
            prefsManager.setLastNotificationDate("")
        }
        _state.update { it.copy(pushNotificationsEnabled = enabled) }
        updateServiceState(enabled, _state.value.trackingMode)
    }

    fun setTrackingMode(mode: com.example.scrolldebt.data.repository.TrackingMode) {
        prefsManager.setTrackingMode(mode)
        prefsManager.setLastNotificationDate("") // reset lock so new mode can fire
        _state.update { it.copy(trackingMode = mode) }
        updateServiceState(_state.value.pushNotificationsEnabled, mode)
    }

    private fun updateServiceState(pushEnabled: Boolean, mode: com.example.scrolldebt.data.repository.TrackingMode) {
        if (pushEnabled && mode == com.example.scrolldebt.data.repository.TrackingMode.REALTIME) {
            val intent = Intent(context, com.example.scrolldebt.data.workers.DoomTrackerService::class.java)
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                context.startForegroundService(intent)
            } else {
                context.startService(intent)
            }
            androidx.work.WorkManager.getInstance(context).cancelUniqueWork("ScrollDebtThresholdCheck")
        } else {
            val intent = Intent(context, com.example.scrolldebt.data.workers.DoomTrackerService::class.java)
            context.stopService(intent)
            
            if (pushEnabled && mode == com.example.scrolldebt.data.repository.TrackingMode.BATTERY_SAVER) {
                val thresholdRequest = androidx.work.PeriodicWorkRequestBuilder<com.example.scrolldebt.data.workers.ThresholdWorker>(
                    15, java.util.concurrent.TimeUnit.MINUTES
                )
                    .setInitialDelay(15, java.util.concurrent.TimeUnit.MINUTES)
                    .build()
                androidx.work.WorkManager.getInstance(context).enqueueUniquePeriodicWork(
                    "ScrollDebtThresholdCheck",
                    androidx.work.ExistingPeriodicWorkPolicy.CANCEL_AND_REENQUEUE,
                    thresholdRequest
                )
            } else {
                androidx.work.WorkManager.getInstance(context).cancelUniqueWork("ScrollDebtThresholdCheck")
            }
        }
    }

    fun setThemeMode(mode: Int) {
        prefsManager.setThemeMode(mode)
        _state.update { it.copy(themeMode = mode) }
    }

    fun changeLanguage(lang: String) {
        prefsManager.setLanguage(lang)
        _state.update { it.copy(language = lang) }
        refreshRoastMessage()
    }

    /**
     * A streak is the run of consecutive days, ending today, whose total stays at or under
     * the streak threshold. Today counts from the live figure; earlier days come from the
     * database.
     *
     * Days with no record are treated as under-threshold, because the sync worker simply may
     * not have run - but we only walk back as far as the oldest row we actually hold, so a
     * fresh install cannot claim a streak stretching into the past.
     *
     * Uses [java.time.LocalDate] rather than Calendar: the previous version compared a
     * Calendar carrying the current wall-clock time against a date parsed at midnight, so the
     * "older than the oldest record" guard depended on what time of day it happened to run.
     */
    private fun recalculateStreakAndWeekly(todayMs: Long, records: List<UsageRecord>, thresholdMin: Int) {
        val thresholdMs = thresholdMin * 60 * 1000L
        val recordsMap = records.associateBy { it.date }
        val today = java.time.LocalDate.now()

        var streak = 0
        if (todayMs <= thresholdMs) {
            streak = 1

            // records is ordered by date DESC, so the last row is the oldest one we have.
            val oldestDate = records.lastOrNull()?.date?.let {
                runCatching { java.time.LocalDate.parse(it) }.getOrNull()
            }

            if (oldestDate != null) {
                var day = today.minusDays(1)
                while (!day.isBefore(oldestDate)) {
                    val timeSpent = recordsMap[day.toString()]?.totalTimeMs ?: 0L
                    if (timeSpent > thresholdMs) break
                    streak++
                    day = day.minusDays(1)
                }
            }
        }

        // Rolling 7-day window: today plus the six preceding calendar days.
        var weeklyTotal = todayMs
        for (i in 1..6) {
            weeklyTotal += recordsMap[today.minusDays(i.toLong()).toString()]?.totalTimeMs ?: 0L
        }

        _state.update { it.copy(currentStreakDays = streak, weeklyTotalTimeMs = weeklyTotal) }
    }
}
