package com.example.scrolldebt.utils

import java.time.LocalDate

/**
 * The single source of truth for the `YYYY-MM-DD` strings used as the primary key of
 * `usage_records` and as the "already notified today" marker in preferences.
 *
 * Why this exists: the app previously built these keys with
 * `SimpleDateFormat("yyyy-MM-dd", Locale.getDefault())`. Under a locale whose default
 * calendar is not Gregorian (th-TH is Buddhist, ar-SA is Hijri) that formatter emits a
 * different year entirely - "2569-06-09" instead of "2026-06-09". Writes went through
 * that formatter while reads used Locale.US, so on those devices history and streaks
 * silently stopped matching.
 *
 * [LocalDate] is proleptic-Gregorian and its `toString()` is ISO-8601 by contract, so the
 * key is stable regardless of locale. It still uses the device's *time zone*, which is what
 * we want - "today" should mean the user's local day.
 */
object DateKeys {

    /** The local calendar day, as stored in the database. */
    fun today(): String = LocalDate.now().toString()

    /** [daysAgo] days before the local calendar day. Handles month/year and DST rollover. */
    fun daysAgo(daysAgo: Long): String = LocalDate.now().minusDays(daysAgo).toString()
}
