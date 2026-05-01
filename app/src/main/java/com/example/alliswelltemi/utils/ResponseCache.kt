package com.example.alliswelltemi.utils

import android.util.Log
import kotlin.math.abs

/**
 * Response Cache - Caches LLM responses to avoid redundant API calls
 * Implements TTL (Time To Live) for automatic cache expiration
 * Thread-safe implementation for concurrent access
 */
object ResponseCache {
    private const val TAG = "ResponseCache"
    private const val DEFAULT_TTL_MS = 3600000L  // 1 hour
    private const val MAX_CACHE_SIZE = 100

    /**
     * Represents a cached response entry
     */
    private data class CacheEntry(
        val response: String,
        val timestamp: Long,
        val ttlMs: Long
    ) {
        fun isExpired(): Boolean {
            val age = System.currentTimeMillis() - timestamp
            return age > ttlMs
        }
    }

    // Thread-safe cache store
    private val cache = mutableMapOf<String, CacheEntry>()
    private val cacheLock = Object()

    /**
     * Generate cache key from query
     * Uses fuzzy matching for similar queries
     */
    private fun generateCacheKey(query: String): String {
        return query.lowercase()
            .replace(Regex("[^a-z0-9]+"), " ")
            .trim()
            .take(100)  // Limit key length
            .hashCode()
            .toString()
    }

    /**
     * Get cached response if available and not expired
     */
    fun get(query: String): String? {
        val key = generateCacheKey(query)

        synchronized(cacheLock) {
            val entry = cache[key] ?: return null

            if (entry.isExpired()) {
                Log.d(TAG, "Cache entry expired for key: $key")
                cache.remove(key)
                return null
            }

            Log.d(TAG, "Cache HIT for query: '${query.take(50)}...'")
            return entry.response
        }
    }

    /**
     * Store response in cache with TTL
     */
    fun put(query: String, response: String, ttlMs: Long = DEFAULT_TTL_MS) {
        val key = generateCacheKey(query)

        synchronized(cacheLock) {
            // Evict old entry if it exists
            if (cache.containsKey(key)) {
                cache.remove(key)
            }

            // Check cache size and evict oldest if needed
            if (cache.size >= MAX_CACHE_SIZE) {
                val oldestKey = cache.minByOrNull { it.value.timestamp }?.key
                if (oldestKey != null) {
                    Log.d(TAG, "Cache FULL, evicting oldest entry: $oldestKey")
                    cache.remove(oldestKey)
                }
            }

            cache[key] = CacheEntry(response, System.currentTimeMillis(), ttlMs)
            Log.d(TAG, "Cache MISS, stored new entry for query: '${query.take(50)}...' (TTL: ${ttlMs}ms)")
        }
    }

    /**
     * Check if query might have cached result (fuzzy matching)
     * Returns true if a similar query exists in cache
     */
    fun hasSimilarCachedQuery(query: String, similarityThreshold: Float = 0.7f): Boolean {
        val queryTokens = query.lowercase()
            .split(Regex("[^a-z0-9]+"))
            .filter { it.length > 2 }
            .toSet()

        if (queryTokens.isEmpty()) return false

        synchronized(cacheLock) {
            for ((_, entry) in cache) {
                if (entry.isExpired()) continue

                // This is a simplification - a real implementation would use embeddings
                // For now, check if most tokens overlap
                val matchCount = queryTokens.count { token ->
                    cache.keys.any { key ->
                        key.contains(token)
                    }
                }

                val similarity = matchCount.toFloat() / queryTokens.size
                if (similarity >= similarityThreshold) {
                    return true
                }
            }
        }

        return false
    }

    /**
     * Clear all expired entries from cache
     */
    fun cleanup() {
        synchronized(cacheLock) {
            val expiredKeys = cache.filter { (_, entry) ->
                entry.isExpired()
            }.keys

            expiredKeys.forEach { key ->
                cache.remove(key)
                Log.d(TAG, "Cleaned up expired cache entry: $key")
            }

            Log.d(TAG, "Cache cleanup complete. Removed ${expiredKeys.size} expired entries. Cache size: ${cache.size}")
        }
    }

    /**
     * Clear entire cache
     */
    fun clear() {
        synchronized(cacheLock) {
            cache.clear()
            Log.d(TAG, "Cache cleared")
        }
    }

    /**
     * Get cache statistics
     */
    fun getStats(): String {
        synchronized(cacheLock) {
            val expiredCount = cache.count { (_, entry) -> entry.isExpired() }
            val validCount = cache.size - expiredCount
            return "Cache Stats: Size=$validCount, Expired=$expiredCount, Total=${cache.size}"
        }
    }
}

/**
 * Circuit Breaker for Ollama API failures
 * Prevents cascading failures by temporarily disabling requests after repeated errors
 */
object OllamaCircuitBreaker {
    private const val TAG = "CircuitBreaker"

    // Circuit breaker states
    enum class State { CLOSED, OPEN, HALF_OPEN }

    // Configuration
    private const val FAILURE_THRESHOLD = 3  // Open after 3 failures
    private const val SUCCESS_THRESHOLD = 2  // Close after 2 successes in HALF_OPEN
    private const val TIMEOUT_MS = 30000L    // Reset after 30 seconds

    private var state = State.CLOSED
    private var failureCount = 0
    private var successCount = 0
    private var lastFailureTime = 0L
    private val lock = Object()

    /**
     * Check if a request can proceed
     */
    fun canProceed(): Boolean {
        synchronized(lock) {
            return when (state) {
                State.CLOSED -> true
                State.OPEN -> {
                    // Try to transition to HALF_OPEN after timeout
                    if (System.currentTimeMillis() - lastFailureTime > TIMEOUT_MS) {
                        Log.i(TAG, "Circuit Breaker: OPEN → HALF_OPEN (timeout exceeded)")
                        state = State.HALF_OPEN
                        successCount = 0
                        true
                    } else {
                        Log.w(TAG, "Circuit Breaker: OPEN - requests denied")
                        false
                    }
                }
                State.HALF_OPEN -> true
            }
        }
    }

    /**
     * Record successful request
     */
    fun recordSuccess() {
        synchronized(lock) {
            when (state) {
                State.CLOSED -> {
                    failureCount = 0
                }
                State.HALF_OPEN -> {
                    successCount++
                    if (successCount >= SUCCESS_THRESHOLD) {
                        Log.i(TAG, "Circuit Breaker: HALF_OPEN → CLOSED (recovered)")
                        state = State.CLOSED
                        failureCount = 0
                        successCount = 0
                    }
                }
                State.OPEN -> {}
            }
        }
    }

    /**
     * Record failed request
     */
    fun recordFailure() {
        synchronized(lock) {
            lastFailureTime = System.currentTimeMillis()

            when (state) {
                State.CLOSED -> {
                    failureCount++
                    if (failureCount >= FAILURE_THRESHOLD) {
                        Log.w(TAG, "Circuit Breaker: CLOSED → OPEN (repeated failures)")
                        state = State.OPEN
                        successCount = 0
                    }
                }
                State.HALF_OPEN -> {
                    Log.w(TAG, "Circuit Breaker: HALF_OPEN → OPEN (failure during recovery)")
                    state = State.OPEN
                    successCount = 0
                    failureCount = FAILURE_THRESHOLD
                }
                State.OPEN -> {
                    failureCount = FAILURE_THRESHOLD  // Keep in OPEN state
                }
            }
        }
    }

    /**
     * Reset circuit breaker
     */
    fun reset() {
        synchronized(lock) {
            Log.i(TAG, "Circuit Breaker: RESET")
            state = State.CLOSED
            failureCount = 0
            successCount = 0
        }
    }

    /**
     * Get current state
     */
    fun getState(): State {
        synchronized(lock) {
            return state
        }
    }
}

