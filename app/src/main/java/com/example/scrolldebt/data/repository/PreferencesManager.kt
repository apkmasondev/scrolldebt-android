package com.example.scrolldebt.data.repository

import android.content.Context
import com.example.scrolldebt.utils.AppConstants
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow

enum class TrackingMode {
    REALTIME, BATTERY_SAVER
}

class PreferencesManager(context: Context) {
    private val prefs = context.getSharedPreferences("scroll_debt_prefs", Context.MODE_PRIVATE)

    companion object {
        private const val KEY_TRACKED_APPS = "tracked_apps"
        private const val KEY_THRESHOLD_MINUTES = "threshold_ms"
        private const val KEY_STREAK_THRESHOLD_MINUTES = "streak_threshold_ms"
        private const val KEY_BRUTAL_TRUTH_ENABLED = "brutal_truth_enabled"
        private const val KEY_LANGUAGE = "language"
        private const val KEY_PUSH_NOTIFICATIONS_ENABLED = "push_notifications_enabled"
        private const val KEY_LAST_NOTIFICATION_DATE = "last_notification_date"
        private const val KEY_TRACKING_MODE = "tracking_mode"
        private const val KEY_THEME_MODE = "theme_mode"
        
        private val DEFAULT_TRACKED_APPS = AppConstants.DEFAULT_SUGGESTED_APPS.keys
    }

    private val _themeModeFlow = MutableStateFlow(getThemeMode())
    val themeModeFlow: StateFlow<Int> = _themeModeFlow.asStateFlow()

    fun getThemeMode(): Int {
        return prefs.getInt(KEY_THEME_MODE, 2) // 1=Light, 2=Dark
    }

    fun setThemeMode(mode: Int) {
        prefs.edit().putInt(KEY_THEME_MODE, mode).apply()
        _themeModeFlow.value = mode
    }

    fun getTrackedApps(): Set<String> {
        return prefs.getStringSet(KEY_TRACKED_APPS, DEFAULT_TRACKED_APPS) ?: DEFAULT_TRACKED_APPS
    }

    fun setTrackedApps(apps: Set<String>) {
        prefs.edit().putStringSet(KEY_TRACKED_APPS, apps).apply()
    }

    fun getNotificationThresholdMinutes(): Int {
        // default 60 minutes
        return prefs.getInt(KEY_THRESHOLD_MINUTES, 60)
    }

    fun setNotificationThresholdMinutes(minutes: Int) {
        prefs.edit().putInt(KEY_THRESHOLD_MINUTES, minutes).apply()
    }

    fun getStreakThresholdMinutes(): Int {
        // default 60 minutes
        return prefs.getInt(KEY_STREAK_THRESHOLD_MINUTES, 60)
    }

    fun setStreakThresholdMinutes(minutes: Int) {
        prefs.edit().putInt(KEY_STREAK_THRESHOLD_MINUTES, minutes).apply()
    }

    fun isBrutalTruthEnabled(): Boolean {
        return prefs.getBoolean(KEY_BRUTAL_TRUTH_ENABLED, true)
    }

    fun setBrutalTruthEnabled(enabled: Boolean) {
        prefs.edit().putBoolean(KEY_BRUTAL_TRUTH_ENABLED, enabled).apply()
    }


    private fun getDefaultLanguage(): String {
        val sysLang = java.util.Locale.getDefault().language.lowercase()
        return if (sysLang in listOf("pl", "es", "fr", "de")) {
            sysLang
        } else {
            "en"
        }
    }

    fun getLanguage(): String {
        return prefs.getString(KEY_LANGUAGE, getDefaultLanguage()) ?: getDefaultLanguage()
    }

    fun setLanguage(lang: String) {
        prefs.edit().putString(KEY_LANGUAGE, lang).apply()
    }

    fun isPushNotificationsEnabled(): Boolean {
        return prefs.getBoolean(KEY_PUSH_NOTIFICATIONS_ENABLED, false)
    }

    fun setPushNotificationsEnabled(enabled: Boolean) {
        prefs.edit().putBoolean(KEY_PUSH_NOTIFICATIONS_ENABLED, enabled).apply()
    }

    fun getLastNotificationDate(): String {
        return prefs.getString(KEY_LAST_NOTIFICATION_DATE, "") ?: ""
    }

    fun setLastNotificationDate(date: String) {
        prefs.edit().putString(KEY_LAST_NOTIFICATION_DATE, date).apply()
    }

    fun getTrackingMode(): TrackingMode {
        val modeStr = prefs.getString(KEY_TRACKING_MODE, TrackingMode.BATTERY_SAVER.name)
        return try {
            TrackingMode.valueOf(modeStr ?: TrackingMode.BATTERY_SAVER.name)
        } catch (e: Exception) {
            TrackingMode.BATTERY_SAVER
        }
    }

    fun setTrackingMode(mode: TrackingMode) {
        prefs.edit().putString(KEY_TRACKING_MODE, mode.name).apply()
    }
}
