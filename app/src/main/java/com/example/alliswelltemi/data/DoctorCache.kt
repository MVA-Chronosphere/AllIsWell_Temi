package com.example.alliswelltemi.data

import android.content.Context
import android.content.SharedPreferences
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import android.util.Log

/**
 * Cache manager for doctor data using SharedPreferences
 * Reduces API calls and provides offline fallback
 */
class DoctorCache(context: Context) {
    private val sharedPreferences: SharedPreferences = context.getSharedPreferences(
        "doctor_cache",
        Context.MODE_PRIVATE
    )
    private val gson = Gson()
    private val tag = "DoctorCache"

    companion object {
        private const val KEY_DOCTORS = "cached_doctors"
        private const val KEY_LAST_UPDATED = "last_updated"
        private const val CACHE_VALIDITY_MS = 3600000L // 1 hour
    }

    /**
     * Save doctors to cache
     */
    fun saveDoctors(doctors: List<Doctor>) {
        try {
            val json = gson.toJson(doctors)
            sharedPreferences.edit().apply {
                putString(KEY_DOCTORS, json)
                putLong(KEY_LAST_UPDATED, System.currentTimeMillis())
                apply()
            }
            Log.d(tag, "Saved ${doctors.size} doctors to cache")
        } catch (e: Exception) {
            Log.e(tag, "Error saving doctors to cache", e)
        }
    }

    /**
     * Retrieve doctors from cache
     */
    fun getDoctors(): List<Doctor>? {
        return try {
            val json = sharedPreferences.getString(KEY_DOCTORS, null) ?: return null
            val type = object : TypeToken<List<Doctor>>() {}.type
            gson.fromJson(json, type)
        } catch (e: Exception) {
            Log.e(tag, "Error reading doctors from cache", e)
            null
        }
    }

    /**
     * Check if cache is valid (not older than CACHE_VALIDITY_MS)
     */
    fun isCacheValid(): Boolean {
        val lastUpdated = sharedPreferences.getLong(KEY_LAST_UPDATED, 0L)
        val isValid = System.currentTimeMillis() - lastUpdated < CACHE_VALIDITY_MS
        Log.d(tag, "Cache valid: $isValid (age: ${System.currentTimeMillis() - lastUpdated}ms)")
        return isValid
    }

    /**
     * Clear cache
     */
    fun clearCache() {
        sharedPreferences.edit().apply {
            remove(KEY_DOCTORS)
            remove(KEY_LAST_UPDATED)
            apply()
        }
        Log.d(tag, "Cache cleared")
    }

    /**
     * Get age of cache in milliseconds
     */
    fun getCacheAge(): Long {
        val lastUpdated = sharedPreferences.getLong(KEY_LAST_UPDATED, 0L)
        return System.currentTimeMillis() - lastUpdated
    }
}

