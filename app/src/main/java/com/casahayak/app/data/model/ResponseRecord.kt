package com.casahayak.app.data.model

/**
 * A saved AI-generated response stored in Firestore under /responses/{id}.
 */
data class ResponseRecord(
    val id: String = "",
    val userId: String = "",
    val inputText: String = "",
    val outputText: String = "",
    /**
     * Feature type that generated this response.
     */
    val featureType: String = "",
    val featureLabel: String = "",
    val createdAt: Long = System.currentTimeMillis()
)
