package com.example.alliswelltemi.utils

import android.content.Context
import android.content.SharedPreferences

/**
 * Configuration manager for Ollama server
 * Reads from SharedPreferences with fallback to defaults
 * Supports runtime URL changes and timeout configuration
 */
object OllamaConfig {
    private const val PREF_NAME = "ollama_config"
    private const val KEY_SERVER_URL = "ollama_server_url"
    private const val KEY_TIMEOUT_SECONDS = "ollama_timeout_secs"
    private const val KEY_ENABLE_CACHE = "ollama_enable_cache"

    // Network URLs with fallback strategy
    private const val PRIMARY_SERVER_URL = "http://192.168.1.82:11434/"
    private const val FALLBACK_SERVER_URL = "http://10.1.90.189:11434/"
    private const val DEFAULT_TIMEOUT = 30
    const val ENABLE_CACHE = true  // Fix 7: SAFE CACHE USAGE

    private var preferences: SharedPreferences? = null
    private var activeServerUrl: String = PRIMARY_SERVER_URL

    /**
     * Initialize config with application context
     * Must be called once in MainActivity.onCreate()
     */
    fun init(context: Context) {
        preferences = context.getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE)
    }

    /**
     * Get Ollama server URL with fallback logic
     * Priority: Environment variable → SharedPreferences → Primary (192.168.1.82) → Fallback (10.1.90.189)
     */
    fun getServerUrl(): String {
        // Try environment variable first (for testing/deployment)
        val envUrl = System.getenv("OLLAMA_BASE_URL")
        if (envUrl != null && envUrl.isNotBlank()) {
            android.util.Log.d("OllamaConfig", "Using Ollama URL from environment: $envUrl")
            activeServerUrl = envUrl
            return envUrl
        }

        // Try SharedPreferences
        val savedUrl = preferences?.getString(KEY_SERVER_URL, null)
        if (savedUrl != null && savedUrl.isNotBlank()) {
            android.util.Log.d("OllamaConfig", "Using Ollama URL from preferences: $savedUrl")
            activeServerUrl = savedUrl
            return savedUrl
        }

        // Default to primary (192.168.1.82) - will fallback to 10.1.90.189 if primary fails
        android.util.Log.d("OllamaConfig", "Using primary Ollama URL: $PRIMARY_SERVER_URL (fallback: $FALLBACK_SERVER_URL)")
        activeServerUrl = PRIMARY_SERVER_URL
        return PRIMARY_SERVER_URL
    }

    /**
     * Get fallback server URL for when primary fails
     */
    fun getFallbackServerUrl(): String {
        return FALLBACK_SERVER_URL
    }

    /**
     * Get primary server URL
     */
    fun getPrimaryServerUrl(): String {
        return PRIMARY_SERVER_URL
    }

    /**
     * Set Ollama server URL at runtime
     * Validates URL format before storing
     */
    fun setServerUrl(url: String) {
        if (url.isNotBlank() && (url.startsWith("http://") || url.startsWith("https://"))) {
            preferences?.edit()?.putString(KEY_SERVER_URL, url)?.apply()
            activeServerUrl = url
            android.util.Log.i("OllamaConfig", "Ollama server URL updated: $url")
        } else {
            android.util.Log.e("OllamaConfig", "Invalid URL format: $url")
        }
    }

    /**
     * Get currently active server URL
     */
    fun getActiveServerUrl(): String {
        return activeServerUrl
    }

    /**
     * Get timeout in seconds for Ollama requests
     */
    fun getTimeoutSeconds(): Int {
        return preferences?.getInt(KEY_TIMEOUT_SECONDS, DEFAULT_TIMEOUT)
            ?: DEFAULT_TIMEOUT
    }

    /**
     * Set timeout in seconds (must be 5-300)
     */
    fun setTimeoutSeconds(seconds: Int) {
        if (seconds in 5..300) {
            preferences?.edit()?.putInt(KEY_TIMEOUT_SECONDS, seconds)?.apply()
            android.util.Log.i("OllamaConfig", "Ollama timeout updated: ${seconds}s")
        } else {
            android.util.Log.e("OllamaConfig", "Invalid timeout: $seconds (must be 5-300)")
        }
    }
}
