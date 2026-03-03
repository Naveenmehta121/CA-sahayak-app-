package com.casahayak.app.data.model

import com.google.gson.annotations.SerializedName

// ─── Request ──────────────────────────────────────────────────────────────────

/**
 * Request body for Groq's Chat Completions API.
 * Endpoint: POST https://api.groq.com/openai/v1/chat/completions
 */
data class GroqRequest(
    @SerializedName("model") val model: String = "llama3-8b-8192",
    @SerializedName("messages") val messages: List<GroqMessage>,
    @SerializedName("max_tokens") val maxTokens: Int = 1024,
    @SerializedName("temperature") val temperature: Double = 0.7
)

data class GroqMessage(
    @SerializedName("role") val role: String,   // "system" | "user" | "assistant"
    @SerializedName("content") val content: String
)

// ─── Response ─────────────────────────────────────────────────────────────────

data class GroqResponse(
    @SerializedName("id") val id: String?,
    @SerializedName("choices") val choices: List<GroqChoice>?,
    @SerializedName("usage") val usage: GroqUsage?
)

data class GroqChoice(
    @SerializedName("message") val message: GroqMessage?,
    @SerializedName("finish_reason") val finishReason: String?
)

data class GroqUsage(
    @SerializedName("prompt_tokens") val promptTokens: Int,
    @SerializedName("completion_tokens") val completionTokens: Int,
    @SerializedName("total_tokens") val totalTokens: Int
)
