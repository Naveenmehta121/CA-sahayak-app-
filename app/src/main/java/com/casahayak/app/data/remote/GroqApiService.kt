package com.casahayak.app.data.remote

import com.casahayak.app.data.model.GroqRequest
import com.casahayak.app.data.model.GroqResponse
import retrofit2.http.Body
import retrofit2.http.POST

/**
 * Retrofit interface for Groq's OpenAI-compatible Chat Completions API.
 * Base URL: https://api.groq.com/
 */
interface GroqApiService {

    @POST("openai/v1/chat/completions")
    suspend fun generateCompletion(
        @Body request: GroqRequest
    ): GroqResponse
}
