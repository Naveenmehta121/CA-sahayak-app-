package com.casahayak.app.data.model

/**
 * Tracks how many times a user has used each feature for the current month.
 * Stored in Firestore under /usage/{userId}_{featureType}_{month}.
 */
data class UsageRecord(
    val id: String = "",
    val userId: String = "",
    /**
     * One of: "notice_reply", "gst_explanation", "client_reply", "engagement_letter"
     */
    val featureType: String = "",
    val usageCount: Int = 0,
    /**
     * Month key in format "YYYY-MM" e.g. "2025-01"
     */
    val month: String = ""
)
