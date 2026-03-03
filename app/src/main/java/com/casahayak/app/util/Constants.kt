package com.casahayak.app.util

/**
 * App-wide constants for feature types, Firestore collections,
 * subscription SKUs, usage limits, and AdMob ad unit IDs.
 */
object Constants {

    // ─── Groq API ────────────────────────────────────────────────────────────
    const val GROQ_BASE_URL = "https://api.groq.com/"
    val GROQ_API_KEY = com.casahayak.app.BuildConfig.GROQ_API_KEY
    const val GROQ_MODEL = "llama3-8b-8192"

    // ─── Feature Types ───────────────────────────────────────────────────────
    const val FEATURE_NOTICE_REPLY = "notice_reply"
    const val FEATURE_GST_EXPLANATION = "gst_explanation"
    const val FEATURE_CLIENT_REPLY = "client_reply"
    const val FEATURE_ENGAGEMENT_LETTER = "engagement_letter"

    val ALL_FEATURES = listOf(
        FEATURE_NOTICE_REPLY,
        FEATURE_GST_EXPLANATION,
        FEATURE_CLIENT_REPLY,
        FEATURE_ENGAGEMENT_LETTER
    )

    // ─── Firestore Collections ───────────────────────────────────────────────
    const val COLLECTION_USERS = "users"
    const val COLLECTION_RESPONSES = "responses"
    const val COLLECTION_USAGE = "usage"

    // ─── Subscription Types ──────────────────────────────────────────────────
    const val PLAN_TRIAL = "trial"
    const val PLAN_FREE = "free"
    const val PLAN_PREMIUM = "premium"

    // Trial duration in days
    const val TRIAL_DURATION_DAYS = 7L

    // ─── Usage Limits (Free Plan) ─────────────────────────────────────────────
    // -1 = unlimited (for trial/premium), else monthly cap
    val FREE_MONTHLY_LIMITS = mapOf(
        FEATURE_NOTICE_REPLY to 3,
        FEATURE_GST_EXPLANATION to 5,
        FEATURE_CLIENT_REPLY to 5,
        FEATURE_ENGAGEMENT_LETTER to 2
    )

    // ─── Play Billing ────────────────────────────────────────────────────────
    // Must match the Product ID in Google Play Console
    const val PREMIUM_SUBSCRIPTION_ID = "premium_monthly"

    // ─── AdMob ──────────────────────────────────────────────────────────────
    // Use Google test IDs during development.
    // Replace with your real Ad Unit ID before release.
    const val ADMOB_BANNER_AD_UNIT_ID = "ca-app-pub-3940256099942544/6300978111" // Test banner
}
