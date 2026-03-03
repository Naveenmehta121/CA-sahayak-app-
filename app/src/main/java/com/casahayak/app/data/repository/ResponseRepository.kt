package com.casahayak.app.data.repository

import com.casahayak.app.data.model.ResponseRecord
import com.casahayak.app.util.Constants
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.Query
import kotlinx.coroutines.tasks.await
import java.util.UUID
import javax.inject.Inject
import javax.inject.Singleton

/**
 * Manages saved AI responses in Firestore /responses/{id}.
 */
@Singleton
class ResponseRepository @Inject constructor(
    private val firestore: FirebaseFirestore
) {
    private val responsesCollection = firestore.collection(Constants.COLLECTION_RESPONSES)

    /**
     * Saves a generated response to Firestore.
     */
    suspend fun saveResponse(response: ResponseRecord): Result<String> {
        return try {
            val id = UUID.randomUUID().toString()
            val withId = response.copy(id = id)
            responsesCollection.document(id).set(withId).await()
            Result.success(id)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    /**
     * Fetches all responses for a user, ordered by most recent first.
     */
    suspend fun getResponsesForUser(userId: String): Result<List<ResponseRecord>> {
        return try {
            val snapshot = responsesCollection
                .whereEqualTo("userId", userId)
                .orderBy("createdAt", Query.Direction.DESCENDING)
                .limit(50)
                .get()
                .await()
            val responses = snapshot.documents.mapNotNull { it.toObject(ResponseRecord::class.java) }
            Result.success(responses)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    /**
     * Deletes a saved response by ID.
     */
    suspend fun deleteResponse(responseId: String): Result<Unit> {
        return try {
            responsesCollection.document(responseId).delete().await()
            Result.success(Unit)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
}
