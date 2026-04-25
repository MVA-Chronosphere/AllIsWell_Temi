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
    private const val BASE_URL = "http://192.168.1.82:11434/"

    private val loggingInterceptor = HttpLoggingInterceptor().apply {
        level = HttpLoggingInterceptor.Level.BODY
    }

    private val httpClient = OkHttpClient.Builder()
        .addInterceptor(loggingInterceptor)
        .connectTimeout(15, TimeUnit.SECONDS)  // Reduced from 60s - faster failure detection
        .readTimeout(30, TimeUnit.SECONDS)     // Reduced from 120s - reasonable for local network
        .writeTimeout(15, TimeUnit.SECONDS)    // Reduced from 60s
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
                
                if (!response.isSuccessful) {
                    val errorBody = response.errorBody()?.string()
                    android.util.Log.e("OllamaClient", "API Error (${response.code()}): $errorBody")
                    throw Exception("Ollama API Error: ${response.code()} - $errorBody")
                }

                val responseBody = response.body()
                if (responseBody == null) {
                    android.util.Log.e("OllamaClient", "Response body is null")
                    throw Exception("Ollama Response body is null")
                }

                responseBody.use { body ->
                    val source = body.source()
                    while (!source.exhausted()) {
                        val line = source.readUtf8Line()
                        if (!line.isNullOrBlank()) {
                            try {
                                val streamResponse = com.google.gson.Gson().fromJson(line, OllamaStreamResponse::class.java)
                                emit(streamResponse.response)
                                if (streamResponse.done) {
                                    break
                                }
                            } catch (e: Exception) {
                                // Skip malformed JSON lines
                                android.util.Log.w("OllamaClient", "Skipping malformed chunk: $line")
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
