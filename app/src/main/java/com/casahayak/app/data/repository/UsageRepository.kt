package com.casahayak.app.data.repository

import com.casahayak.app.data.model.UsageRecord
import com.casahayak.app.util.Constants
import com.casahayak.app.util.DateUtils
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.tasks.await
import javax.inject.Inject
import javax.inject.Singleton

/**
 * Tracks and enforces per-feature usage limits for FREE plan users.
 *
 * Firestore document ID format: {userId}_{featureType}_{YYYY-MM}
 * e.g., "uid123_notice_reply_2025-01"
 */
@Singleton
class UsageRepository @Inject constructor(
    private val firestore: FirebaseFirestore
) {

    private val usageCollection = firestore.collection(Constants.COLLECTION_USAGE)

    private fun docId(userId: String, featureType: String): String {
        val month = DateUtils.currentMonthKey()
        return "${userId}_${featureType}_$month"
    }

    /**
     * Returns the current usage count for a user + feature in the current month.
     */
    suspend fun getUsageCount(userId: String, featureType: String): Int {
        return try {
            val doc = usageCollection.document(docId(userId, featureType)).get().await()
            if (doc.exists()) {
                doc.getLong("usageCount")?.toInt() ?: 0
            } else {
                0
            }
        } catch (e: Exception) {
            0
        }
    }

    /**
     * Checks if the user can use the given feature based on their plan.
     * - Trial and Premium: always permitted
     * - Free: check against monthly limit
     */
    suspend fun canUseFeature(
        userId: String,
        featureType: String,
        subscriptionType: String
    ): Boolean {
        // Trial and premium have unlimited access
        if (subscriptionType == Constants.PLAN_TRIAL ||
            subscriptionType == Constants.PLAN_PREMIUM
        ) return true

        val limit = Constants.FREE_MONTHLY_LIMITS[featureType] ?: return false
        val used = getUsageCount(userId, featureType)
        return used < limit
    }

    /**
     * Gets remaining uses for a free user this month. Returns null for trial/premium.
     */
    suspend fun getRemainingUses(
        userId: String,
        featureType: String,
        subscriptionType: String
    ): Int? {
        if (subscriptionType != Constants.PLAN_FREE) return null
        val limit = Constants.FREE_MONTHLY_LIMITS[featureType] ?: return 0
        val used = getUsageCount(userId, featureType)
        return maxOf(0, limit - used)
    }

    /**
     * Increments the usage count for a user + feature.
     * Creates the document if it doesn't exist.
     */
    suspend fun incrementUsage(userId: String, featureType: String): Result<Unit> {
        return try {
            val id = docId(userId, featureType)
            val doc = usageCollection.document(id).get().await()
            if (doc.exists()) {
                val current = doc.getLong("usageCount")?.toInt() ?: 0
                usageCollection.document(id)
                    .update("usageCount", current + 1)
                    .await()
            } else {
                val record = UsageRecord(
                    id = id,
                    userId = userId,
                    featureType = featureType,
                    usageCount = 1,
                    month = DateUtils.currentMonthKey()
                )
                usageCollection.document(id).set(record).await()
            }
            Result.success(Unit)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
}
