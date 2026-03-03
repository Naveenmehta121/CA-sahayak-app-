package com.casahayak.app.data.model

/**
 * Represents a CA Sahayak user stored in Firestore under /users/{uid}.
 */
data class User(
    val id: String = "",
    val email: String = "",
    val displayName: String = "",
    /**
     * Subscription type: "trial", "free", or "premium"
     */
    val subscriptionType: String = "trial",
    /**
     * Trial end date as epoch milliseconds. Null if not on trial.
     */
    val trialEndDate: Long? = null,
    val createdAt: Long = System.currentTimeMillis()
) {
    companion object {
        const val TRIAL = "trial"
        const val FREE = "free"
        const val PREMIUM = "premium"
    }
}
