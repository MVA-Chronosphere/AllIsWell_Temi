package com.example.alliswelltemi.network

import com.google.gson.annotations.SerializedName

/**
 * Ollama LLM Request Model
 * Represents a request to the Ollama API for text generation
 */
data class OllamaRequest(
    val model: String = "llama3",
    val prompt: String,
    val stream: Boolean = false,
    val options: OllamaOptions = OllamaOptions()
)

/**
 * Ollama Options for generation
 * PERFORMANCE OPTIMIZED for fast response with quality control
 */
data class OllamaOptions(
    val temperature: Double = 0.7,           // Balanced creativity
    val top_k: Int = 40,                     // Token selection diversity
    val top_p: Double = 0.9,                 // Nucleus sampling
    val num_predict: Int = 150,              // CRITICAL: Limit response length for speed (150 tokens ≈ 2-3 sentences)
    val num_ctx: Int = 2048,                 // Context window size (smaller = faster)
    val num_thread: Int = 4,                 // CPU threads for parallel processing
    val repeat_penalty: Double = 1.1,        // Prevent repetition
    val stop: List<String> = listOf("\n\n", "User:", "Question:")  // Stop sequences to end generation early
)

/**
 * Ollama LLM Response Model
 * Contains the generated text response from Ollama
 */
data class OllamaResponse(
    val model: String,
    val response: String,
    @SerializedName("created_at")
    val createdAt: String,
    @SerializedName("done")
    val done: Boolean,
    @SerializedName("total_duration")
    val totalDuration: Long,
    @SerializedName("load_duration")
    val loadDuration: Long,
    @SerializedName("prompt_eval_count")
    val promptEvalCount: Int,
    @SerializedName("prompt_eval_duration")
    val promptEvalDuration: Long,
    @SerializedName("eval_count")
    val evalCount: Int,
    @SerializedName("eval_duration")
    val evalDuration: Long
)

/**
 * Ollama LLM Streaming Response Model
 * Contains streaming chunks from Ollama
 */
data class OllamaStreamResponse(
    val model: String,
    val response: String,
    @SerializedName("created_at")
    val createdAt: String,
    @SerializedName("done")
    val done: Boolean
)

/**
 * Voice pipeline state for tracking conversation progress
 */
enum class VoiceState {
    IDLE,        // Waiting for input
    LISTENING,   // Recording speech
    PROCESSING,  // Sending to Ollama
    THINKING,    // Ollama generating response
    SPEAKING,    // Temi speaking response
    ERROR        // Error occurred
}

/**
 * Result of voice processing with metadata
 */
data class VoiceResult(
    val spokenText: String,
    val llmResponse: String,
    val processingTimeMs: Long,
    val state: VoiceState = VoiceState.IDLE
)
