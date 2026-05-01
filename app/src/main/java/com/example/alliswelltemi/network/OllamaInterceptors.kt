package com.example.alliswelltemi.network

import android.util.Log
import okhttp3.Interceptor
import okhttp3.Response
import okhttp3.ResponseBody.Companion.toResponseBody
import java.io.IOException
import java.util.concurrent.TimeUnit

/**
 * Timeout + Circuit Breaker Interceptor
 * Handles timeouts and prevents cascading failures to Ollama
 */
class OllamaTimeoutInterceptor : Interceptor {
    private companion object {
        const val TAG = "OllamaTimeout"
        const val REQUEST_TIMEOUT_SECONDS = 30
        const val MAX_RETRIES = 2
    }

    override fun intercept(chain: Interceptor.Chain): Response {
        // Build request with custom timeout
        val originalRequest = chain.request()
        val timeoutRequest = originalRequest.newBuilder()
            .build()

        var attempt = 0
        var lastException: IOException? = null

        while (attempt <= MAX_RETRIES) {
            try {
                attempt++
                Log.d(TAG, "Ollama request attempt $attempt/${MAX_RETRIES + 1}")

                // Check circuit breaker
                if (!com.example.alliswelltemi.utils.OllamaCircuitBreaker.canProceed()) {
                    Log.w(TAG, "Circuit breaker OPEN - denying request")
                    return Response.Builder()
                        .request(originalRequest)
                        .protocol(okhttp3.Protocol.HTTP_1_1)
                        .code(503)  // Service Unavailable
                        .message("Circuit breaker open - Ollama temporarily unavailable")
                        .body("{}".toResponseBody())
                        .build()
                }

                val response = chain.withReadTimeout(REQUEST_TIMEOUT_SECONDS, TimeUnit.SECONDS)
                    .proceed(timeoutRequest)

                if (response.isSuccessful) {
                    Log.d(TAG, "Ollama request successful")
                    com.example.alliswelltemi.utils.OllamaCircuitBreaker.recordSuccess()
                    return response
                } else {
                    Log.w(TAG, "Ollama error response: ${response.code}")
                    lastException = IOException("HTTP ${response.code}: ${response.message}")
                }
            } catch (e: IOException) {
                lastException = e
                Log.w(TAG, "Ollama request failed (attempt $attempt): ${e.message}", e)

                // Record failure for circuit breaker
                com.example.alliswelltemi.utils.OllamaCircuitBreaker.recordFailure()

                if (attempt <= MAX_RETRIES) {
                    Log.d(TAG, "Retrying in ${attempt * 1000}ms...")
                    Thread.sleep((attempt * 1000).toLong())
                }
            }
        }

        // All retries exhausted
        Log.e(TAG, "All $MAX_RETRIES retries exhausted")
        throw lastException ?: IOException("Unknown error after $MAX_RETRIES retries")
    }
}

/**
 * Request/Response Logging Interceptor (for debugging)
 * Can be disabled in production to reduce overhead
 */
class OllamaLoggingInterceptor(private val logLevel: LogLevel = LogLevel.BASIC) : Interceptor {

    enum class LogLevel {
        NONE, BASIC, VERBOSE
    }

    private companion object {
        const val TAG = "OllamaLog"
    }

    override fun intercept(chain: Interceptor.Chain): Response {
        if (logLevel == LogLevel.NONE) {
            return chain.proceed(chain.request())
        }

        val request = chain.request()
        val startTime = System.currentTimeMillis()

        Log.d(TAG, "--> REQUEST: ${request.method} ${request.url}")

        if (logLevel == LogLevel.VERBOSE && request.body != null) {
            Log.d(TAG, "Request Body: ${request.body}")
        }

        val response = try {
            chain.proceed(request)
        } catch (e: Exception) {
            Log.e(TAG, "<-- REQUEST FAILED: ${e.message}", e)
            throw e
        }

        val duration = System.currentTimeMillis() - startTime
        Log.d(TAG, "<-- RESPONSE: ${response.code} (${duration}ms)")

        return response
    }
}

