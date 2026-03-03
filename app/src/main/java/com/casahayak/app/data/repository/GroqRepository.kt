package com.casahayak.app.data.repository

import com.casahayak.app.data.model.GroqMessage
import com.casahayak.app.data.model.GroqRequest
import com.casahayak.app.data.prompt.PromptTemplates
import com.casahayak.app.data.remote.GroqApiService
import com.casahayak.app.util.Constants
import javax.inject.Inject
import javax.inject.Singleton

/**
 * Orchestrates AI text generation using the Groq API.
 *
 * Uses a two-message structure:
 *  1. System message: feature-specific professional prompt context
 *  2. User message: the raw input from the CA user
 */
@Singleton
class GroqRepository @Inject constructor(
    private val groqApiService: GroqApiService
) {

    /**
     * Generates AI text for the specified [featureType] with [userInput].
     *
     * @param featureType One of Constants.FEATURE_* constants
     * @param userInput The text entered by the CA user
     * @return Result containing the generated text or an exception
     */
    suspend fun generate(featureType: String, userInput: String): Result<String> {
        return try {
            val systemPrompt = PromptTemplates.getSystemPrompt(featureType)

            val request = GroqRequest(
                model = Constants.GROQ_MODEL,
                messages = listOf(
                    GroqMessage(role = "system", content = systemPrompt),
                    GroqMessage(role = "user", content = userInput)
                ),
                maxTokens = 1500,
                temperature = 0.7
            )

            val response = groqApiService.generateCompletion(request)
            val generatedText = response.choices
                ?.firstOrNull()
                ?.message
                ?.content
                ?: throw Exception("Empty response from Groq API")

            Result.success(generatedText.trim())
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
}
