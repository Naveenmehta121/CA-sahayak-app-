package com.casahayak.app.data.repository

import com.casahayak.app.data.model.User
import com.casahayak.app.util.Constants
import com.casahayak.app.util.DateUtils
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.tasks.await
import javax.inject.Inject
import javax.inject.Singleton

/**
 * Manages user profile data in Firestore /users/{uid}.
 */
@Singleton
class UserRepository @Inject constructor(
    private val firestore: FirebaseFirestore
) {

    private val usersCollection = firestore.collection(Constants.COLLECTION_USERS)

    /**
     * Creates a new user document with a 7-day trial.
     * Called once after successful sign-up.
     */
    suspend fun createUser(uid: String, email: String, displayName: String): Result<Unit> {
        return try {
            val trialEndDate = DateUtils.trialEndDate()
            val user = User(
                id = uid,
                email = email,
                displayName = displayName,
                subscriptionType = Constants.PLAN_TRIAL,
                trialEndDate = trialEndDate
            )
            usersCollection.document(uid).set(user).await()
            Result.success(Unit)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    /**
     * Fetches the user document from Firestore.
     * If the document doesn't exist, creates it (handles Google sign-in new users).
     */
    suspend fun getUser(uid: String, email: String = "", displayName: String = ""): Result<User> {
        return try {
            val doc = usersCollection.document(uid).get().await()
            if (doc.exists()) {
                val user = doc.toObject(User::class.java)!!
                // Auto-downgrade trial to free if trial has expired
                val finalUser = if (user.subscriptionType == Constants.PLAN_TRIAL &&
                    DateUtils.isTrialExpired(user.trialEndDate)
                ) {
                    val downgraded = user.copy(subscriptionType = Constants.PLAN_FREE)
                    usersCollection.document(uid).update("subscriptionType", Constants.PLAN_FREE).await()
                    downgraded
                } else {
                    user
                }
                Result.success(finalUser)
            } else {
                // First-time Google sign-in user
                createUser(uid, email, displayName)
                getUser(uid, email, displayName)
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    /**
     * Updates the user's subscription type (called after Play Billing purchase).
     */
    suspend fun updateSubscription(uid: String, subscriptionType: String): Result<Unit> {
        return try {
            usersCollection.document(uid)
                .update("subscriptionType", subscriptionType)
                .await()
            Result.success(Unit)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
}
