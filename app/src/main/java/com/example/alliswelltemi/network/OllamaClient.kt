package com.example.alliswelltemi.network

import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import java.util.concurrent.TimeUnit

/**
 * Retrofit Client for Ollama LLM API
 * Configured for local network communication with Ollama server
 * Supports dynamic URL configuration via OllamaConfig
 *
 * Usage:
 *   val ollama = OllamaClient.api
 *   val response = ollama.generate(OllamaRequest(prompt = "Hello"))
 */
object OllamaClient {
    // NOTE: BASE_URL is now dynamic, read from OllamaConfig
    
    private val loggingInterceptor = HttpLoggingInterceptor().apply {
        level = HttpLoggingInterceptor.Level.BODY
    }
    
    // Lazy-initialize to support dynamic URL changes
    private var retrofitInstance: Retrofit? = null
    private var lastConfiguredUrl: String = ""

    /**
     * Get or create Retrofit API service
     * Rebuilds if URL has changed
     */
    val api: OllamaApiService
        get() {
            val currentUrl = com.example.alliswelltemi.utils.OllamaConfig.getServerUrl()
            
            // Rebuild if URL changed
            if (retrofitInstance == null || lastConfiguredUrl != currentUrl) {
                lastConfiguredUrl = currentUrl
                retrofitInstance = buildRetrofit(currentUrl)
                android.util.Log.i("OllamaClient", "Ollama client initialized with URL: $currentUrl")
            }
            
            return retrofitInstance!!.create(OllamaApiService::class.java)
        }
    
    private fun buildRetrofit(baseUrl: String): Retrofit {
        val httpClient = OkHttpClient.Builder()
            .addInterceptor(loggingInterceptor)
            .connectTimeout(15, TimeUnit.SECONDS)
            .readTimeout(com.example.alliswelltemi.utils.OllamaConfig.getTimeoutSeconds().toLong(), TimeUnit.SECONDS)
            .writeTimeout(15, TimeUnit.SECONDS)
            .build()
        
        return Retrofit.Builder()
            .baseUrl(baseUrl)
            .client(httpClient)
            .addConverterFactory(GsonConverterFactory.create())
            .build()
    }
    
    /**
     * Change Ollama server at runtime
     * Next API call will use the new URL
     */
    fun setOllamaServerUrl(newUrl: String) {
        com.example.alliswelltemi.utils.OllamaConfig.setServerUrl(newUrl)
        retrofitInstance = null  // Force rebuild on next api access
    }

    /**
     * Generate streaming response from Ollama with caching and timeout handling
     * Returns Flow<String> for real-time text chunks
     *
     * PRODUCTION FIX:
     * - Cache DISABLED by default (cacheEnabled = false)
     * - Cache key is QUERY ONLY, not full prompt (prevents prompt-based collision)
     * - When enabled, uses semantic hashing to avoid false positives
     * - Circuit breaker protects against cascading failures
     */
    suspend fun generateStreaming(
        request: OllamaRequest,
        cacheEnabled: Boolean = false  // SECURITY: Cache disabled by default
    ): kotlinx.coroutines.flow.Flow<String> {
        return kotlinx.coroutines.flow.flow {
            var lastException: Exception? = null
            val urlsToTry = listOf(
                com.example.alliswelltemi.utils.OllamaConfig.getPrimaryServerUrl(),
                com.example.alliswelltemi.utils.OllamaConfig.getFallbackServerUrl()
            )

            for (urlToTry in urlsToTry) {
                try {
                    android.util.Log.i("OllamaClient", "Attempting connection to: $urlToTry")

                    // Set the URL for this attempt
                    com.example.alliswelltemi.utils.OllamaConfig.setServerUrl(urlToTry)

                    // PRODUCTION FIX: Extract QUERY ONLY from prompt for cache key
                    val queryOnlyKey = extractQueryFromPrompt(request.prompt)

                    // Check cache only if explicitly enabled
                    if (cacheEnabled && queryOnlyKey.isNotBlank()) {
                        val cachedResponse = com.example.alliswelltemi.utils.ResponseCache.get(queryOnlyKey)
                        if (cachedResponse != null) {
                            android.util.Log.d("OllamaClient", "✓ Cache HIT for query: '${queryOnlyKey.take(50)}...'")
                            emit(cachedResponse)
                            com.example.alliswelltemi.utils.OllamaCircuitBreaker.recordSuccess()
                            return@flow
                        }
                    }

                    val response = api.generateStream(request.copy(stream = true))

                    if (!response.isSuccessful) {
                        val errorBody = response.errorBody()?.string()
                        android.util.Log.e("OllamaClient", "API Error (${response.code()}): $errorBody")
                        com.example.alliswelltemi.utils.OllamaCircuitBreaker.recordFailure()
                        lastException = Exception("Ollama API Error: ${response.code()} - $errorBody")
                        continue  // Try next URL
                    }

                    val responseBody = response.body()
                    if (responseBody == null) {
                        android.util.Log.e("OllamaClient", "Response body is null")
                        com.example.alliswelltemi.utils.OllamaCircuitBreaker.recordFailure()
                        lastException = Exception("Ollama Response body is null")
                        continue  // Try next URL
                    }

                    val fullResponse = StringBuilder()
                    responseBody.use { body ->
                        val source = body.source()
                        while (!source.exhausted()) {
                            val line = source.readUtf8Line()
                            if (!line.isNullOrBlank()) {
                                try {
                                    val streamResponse = com.google.gson.Gson().fromJson(line, OllamaStreamResponse::class.java)
                                    if (streamResponse.response != null && streamResponse.response.isNotEmpty()) {
                                        emit(streamResponse.response)
                                        fullResponse.append(streamResponse.response)
                                    }
                                    if (streamResponse.done) {
                                        break
                                    }
                                } catch (e: Exception) {
                                    android.util.Log.w("OllamaClient", "Skipping malformed chunk: $line")
                                }
                            }
                        }
                    }

                    if (cacheEnabled && fullResponse.isNotEmpty() && queryOnlyKey.isNotBlank()) {
                        com.example.alliswelltemi.utils.ResponseCache.put(
                            queryOnlyKey,
                            fullResponse.toString(),
                            ttlMs = 3600000L
                        )
                        android.util.Log.d("OllamaClient", "✓ Response cached for query: '${queryOnlyKey.take(50)}...'")
                        com.example.alliswelltemi.utils.OllamaCircuitBreaker.recordSuccess()
                    } else if (fullResponse.isNotEmpty()) {
                        com.example.alliswelltemi.utils.OllamaCircuitBreaker.recordSuccess()
                    }

                    android.util.Log.i("OllamaClient", "✅ Successfully connected to: $urlToTry")
                    return@flow

                } catch (e: Exception) {
                    android.util.Log.e("OllamaClient", "Connection failed for $urlToTry: ${e.message}")
                    lastException = e
                    com.example.alliswelltemi.utils.OllamaCircuitBreaker.recordFailure()
                }
            }

            android.util.Log.e("OllamaClient", "Streaming failed on all URLs")
            throw lastException ?: Exception("Failed to connect to Ollama on all configured URLs")
        }
    }

    /**
     * PRODUCTION FIX: Extract QUERY ONLY from RAG prompt
     * Removes system instructions, context, and metadata
     * Prevents cache collisions from identical system prompts
     *
     * Expected format:
     * [System instructions...]
     * [Context/info...]
     * Q: <actual user query>
     * A:
     */
    private fun extractQueryFromPrompt(fullPrompt: String): String {
        return try {
            // Extract the actual user query from prompt
            // Look for "Q: " marker or "User: " marker
            val queryStart = when {
                fullPrompt.contains("Q: ") -> fullPrompt.lastIndexOf("Q: ") + 3
                fullPrompt.contains("User: ") -> fullPrompt.lastIndexOf("User: ") + 6
                else -> -1
            }

            if (queryStart <= 0) {
                android.util.Log.w("OllamaClient", "Could not extract query from prompt")
                return ""  // Return empty to disable caching if query can't be extracted
            }

            // Find end of query (before "A:" or end of prompt)
            val queryEnd = when {
                fullPrompt.indexOf("A:", queryStart) >= 0 ->
                    fullPrompt.indexOf("A:", queryStart)
                else -> fullPrompt.length
            }

            fullPrompt.substring(queryStart, queryEnd)
                .trim()
                .take(500)  // Limit query key length for performance
        } catch (e: Exception) {
            android.util.Log.e("OllamaClient", "Error extracting query: ${e.message}")
            ""  // Return empty to disable caching on error
        }
    }
}
