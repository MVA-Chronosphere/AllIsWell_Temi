package com.example.alliswelltemi.network

import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import java.util.concurrent.TimeUnit

/**
 * Retrofit Client for Ollama LLM API
 * Configured for local network communication with Ollama server
 *
 * Usage:
 *   val ollama = OllamaClient.api
 *   val response = ollama.generate(OllamaRequest(prompt = "Hello"))
 */
object OllamaClient {
    // IMPORTANT: For Temi robot on local network, use the Ollama server IP
    // Default: http://localhost:11434/ (change to your actual server IP)
    // For emulator/testing: http://10.0.2.2:11434/
    private const val BASE_URL = "http://10.1.90.89:11434/"

    private val loggingInterceptor = HttpLoggingInterceptor().apply {
        level = HttpLoggingInterceptor.Level.BODY
    }

    private val httpClient = OkHttpClient.Builder()
        .addInterceptor(loggingInterceptor)
        .connectTimeout(60, TimeUnit.SECONDS)  // Long timeout for LLM generation
        .readTimeout(120, TimeUnit.SECONDS)    // LLM may take time to generate
        .writeTimeout(60, TimeUnit.SECONDS)
        .build()

    val api: OllamaApiService by lazy {
        Retrofit.Builder()
            .baseUrl(BASE_URL)
            .client(httpClient)
            .addConverterFactory(GsonConverterFactory.create())
            .build()
            .create(OllamaApiService::class.java)
    }

    /**
     * Change the Ollama server IP (useful for testing different environments)
     * Example: "http://10.0.2.2:11434/" for Android emulator
     */
    fun setBaseUrl(newBaseUrl: String) {
        // This would require rebuilding the client
        // For now, use the constant above
    }

    /**
     * Generate streaming response from Ollama
     * Returns Flow<String> for real-time text chunks
     */
    suspend fun generateStreaming(request: OllamaRequest): kotlinx.coroutines.flow.Flow<String> {
        return kotlinx.coroutines.flow.flow {
            try {
                val response = api.generateStream(request.copy(stream = true))
                val responseBody = response.body()

                responseBody?.let { body ->
                    val source = body.source()
                    val buffer = okio.Buffer()

                    while (!source.exhausted()) {
                        val read = source.read(buffer, 8192)
                        if (read > 0) {
                            val chunk = buffer.readUtf8()
                            // Parse NDJSON chunks
                            chunk.lines().forEach { line ->
                                if (line.isNotBlank()) {
                                    try {
                                        val streamResponse = com.google.gson.Gson().fromJson(line, OllamaStreamResponse::class.java)
                                        emit(streamResponse.response)
                                        if (streamResponse.done) {
                                            return@flow
                                        }
                                    } catch (e: Exception) {
                                        // Skip malformed JSON lines
                                        android.util.Log.w("OllamaClient", "Skipping malformed chunk: $line")
                                    }
                                }
                            }
                        }
                    }
                }
            } catch (e: Exception) {
                android.util.Log.e("OllamaClient", "Streaming error: ${e.message}", e)
                throw e
            }
        }
    }
}
