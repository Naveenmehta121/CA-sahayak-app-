package com.casahayak.app.util

import java.text.SimpleDateFormat
import java.util.*
import java.util.concurrent.TimeUnit

/**
 * Date and time utilities for trial management and usage tracking.
 */
object DateUtils {

    /**
     * Returns the trial end timestamp (7 days from now) as epoch milliseconds.
     */
    fun trialEndDate(): Long {
        return System.currentTimeMillis() + TimeUnit.DAYS.toMillis(7L)
    }

    /**
     * Returns true if the trial has expired.
     * @param trialEndDate epoch milliseconds, or null if no trial.
     */
    fun isTrialExpired(trialEndDate: Long?): Boolean {
        if (trialEndDate == null) return true
        return System.currentTimeMillis() > trialEndDate
    }

    /**
     * Returns the number of days remaining in the trial.
     * Returns 0 if expired or null.
     */
    fun trialDaysRemaining(trialEndDate: Long?): Int {
        if (trialEndDate == null) return 0
        val remaining = trialEndDate - System.currentTimeMillis()
        if (remaining <= 0) return 0
        return TimeUnit.MILLISECONDS.toDays(remaining).toInt()
    }

    /**
     * Returns the current month as a key string, e.g. "2025-01".
     * Used for usage tracking document IDs.
     */
    fun currentMonthKey(): String {
        val format = SimpleDateFormat("yyyy-MM", Locale.US)
        return format.format(Date())
    }

    /**
     * Formats a timestamp (epoch ms) as a readable date string.
     * e.g. "3 Mar 2025"
     */
    fun formatDate(epochMs: Long): String {
        val format = SimpleDateFormat("d MMM yyyy", Locale.getDefault())
        return format.format(Date(epochMs))
    }

    /**
     * Formats a timestamp as a date + time string.
     * e.g. "3 Mar 2025 at 10:30 AM"
     */
    fun formatDateTime(epochMs: Long): String {
        val format = SimpleDateFormat("d MMM yyyy 'at' h:mm a", Locale.getDefault())
        return format.format(Date(epochMs))
    }
}
