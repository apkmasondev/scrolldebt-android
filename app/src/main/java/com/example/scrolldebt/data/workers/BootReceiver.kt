package com.example.scrolldebt.data.workers

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import com.example.scrolldebt.data.repository.PreferencesManager
import dagger.hilt.android.AndroidEntryPoint
import javax.inject.Inject

/**
 * Restores Sniper mode after a reboot.
 *
 * WorkManager reschedules its own periodic work across reboots, so Battery Saver mode needs
 * no help. A foreground service does not come back on its own - START_STICKY only covers the
 * system killing it while running - so without this, a user on Sniper mode silently stopped
 * getting alerts after every restart until they next opened the app.
 */
@AndroidEntryPoint
class BootReceiver : BroadcastReceiver() {

    @Inject lateinit var prefs: PreferencesManager

    override fun onReceive(context: Context, intent: Intent) {
        if (intent.action != Intent.ACTION_BOOT_COMPLETED) return

        // BOOT_COMPLETED does not grant a foreground-service start window on API 31+, so this
        // will usually be refused and logged; TrackingScheduler handles that without crashing.
        TrackingScheduler.sync(context, prefs, fromBackground = true)
    }
}
