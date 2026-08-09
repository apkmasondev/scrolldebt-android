package com.example.scrolldebt.utils

import com.example.scrolldebt.R

object TimeFormatUtils {

    /**
     * Formats total minutes into a compact String representation (e.g., "1y 3mo" or "5d 14h")
     * showing at most the top two most significant non-zero units.
     */
    fun formatSmartTime(context: android.content.Context, totalMinutes: Long, language: String): String {
        if (totalMinutes == 0L) {
            return "0" + context.getString(R.string.time_m)
        }

        val y = totalMinutes / (365 * 24 * 60)
        val remAfterY = totalMinutes % (365 * 24 * 60)

        val mo = remAfterY / (30 * 24 * 60)
        val remAfterMo = remAfterY % (30 * 24 * 60)

        val d = remAfterMo / (24 * 60)
        val remAfterD = remAfterMo % (24 * 60)

        val h = remAfterD / 60
        val m = remAfterD % 60

        val parts = mutableListOf<String>()

        if (y > 0) {
            val yKey = if (y == 1L) "time_y_1" else "time_y_many"
            parts.add("$y${context.getString(context.resources.getIdentifier(yKey, "string", context.packageName))}")
        }
        if (mo > 0) {
            parts.add("$mo${context.getString(R.string.time_mo)}")
        }
        if (d > 0 && parts.size < 2) {
            parts.add("$d${context.getString(R.string.time_d)}")
        }
        if (h > 0 && parts.size < 2) {
            parts.add("$h${context.getString(R.string.time_h)}")
        }
        if (m > 0 && parts.size < 2) {
            parts.add("$m${context.getString(R.string.time_m)}")
        }

        // If after everything we still only have 1 part and we're not at minutes, we just return the 1 part.
        // E.g. exactly 2 years -> "2l", exactly 14 hours -> "14h".
        return parts.take(2).joinToString(" ")
    }
}
