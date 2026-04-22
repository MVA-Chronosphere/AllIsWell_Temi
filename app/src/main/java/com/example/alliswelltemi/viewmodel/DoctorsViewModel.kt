package com.example.alliswelltemi.viewmodel

import android.app.Application
import androidx.compose.runtime.State
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.alliswelltemi.data.Doctor
import com.example.alliswelltemi.data.DoctorCache
import com.example.alliswelltemi.data.DoctorData
import com.example.alliswelltemi.network.RetrofitClient
import kotlinx.coroutines.launch
import android.util.Log

/**
 * ViewModel for managing doctors from Strapi CMS with caching support
 */
class DoctorsViewModel(application: Application) : AndroidViewModel(application) {

    private val _doctors = mutableStateOf<List<Doctor>>(emptyList())
    val doctors: State<List<Doctor>> = _doctors

    private val _isLoading = mutableStateOf(false)
    val isLoading: State<Boolean> = _isLoading

    private val _error = mutableStateOf<String?>(null)
    val error: State<String?> = _error

    private val _selectedDepartment = mutableStateOf<String?>(null)
    val selectedDepartment: State<String?> = _selectedDepartment

    private val _departments = mutableStateOf<List<String>>(emptyList())
    val departments: State<List<String>> = _departments

    private val cache = DoctorCache(getApplication())
    private val tag = "DoctorsViewModel"

    init {
        // Only fetch if data is not already loaded or if cache is stale
        if (_doctors.value.isEmpty()) {
            if (isDataFromCache()) {
                loadFromCache()
                // Optionally refresh in background if cache is valid but older than some threshold
                if (cache.getCacheAge() > 600000L) { // 10 minutes
                   Log.d(tag, "Cache is valid but > 10 mins old, refreshing in background")
                   fetchDoctors()
                }
            } else {
                fetchDoctors()
            }
        }
    }

    /**
     * Fetch all doctors from Strapi CMS with caching fallback
     */
    fun fetchDoctors() {
        viewModelScope.launch {
            try {
                _isLoading.value = true
                _error.value = null

                // Try to fetch from API
                val response = RetrofitClient.apiService.getDoctors()
                val doctorList = response.data?.mapNotNull { doctorDoc ->
                    doctorDoc.toDomain()
                } ?: emptyList()

                if (doctorList.isNotEmpty()) {
                    _doctors.value = doctorList
                    cache.saveDoctors(doctorList)
                    Log.d(tag, "Fetched and cached ${doctorList.size} doctors from API")
                } else {
                    // API returned empty, try cache
                    loadFromCache()
                }

                // Extract unique departments
                val uniqueDepartments = _doctors.value
                    .map { it.department }
                    .distinct()
                    .sorted()
                _departments.value = uniqueDepartments

            } catch (e: Exception) {
                Log.e(tag, "Error fetching doctors from API", e)
                _error.value = "Network error. Loading cached data..."
                // Try to load from cache as fallback
                loadFromCache()
            } finally {
                _isLoading.value = false
            }
        }
    }

    /**
     * Load doctors from cache
     */
    private fun loadFromCache() {
        try {
            val cachedDoctors = cache.getDoctors()
            if (cachedDoctors != null && cachedDoctors.isNotEmpty()) {
                _doctors.value = cachedDoctors
                _error.value = null
                Log.d(tag, "Loaded ${cachedDoctors.size} doctors from cache")

                // Extract unique departments
                val uniqueDepartments = cachedDoctors
                    .map { it.department }
                    .distinct()
                    .sorted()
                _departments.value = uniqueDepartments
            } else {
                // No cache available, use static fallback
                loadStaticFallback()
            }
        } catch (e: Exception) {
            Log.e(tag, "Error loading from cache", e)
            loadStaticFallback()
        }
    }

    /**
     * Load static fallback doctor data
     */
    private fun loadStaticFallback() {
        try {
            val staticDoctors = DoctorData.DOCTORS
            _doctors.value = staticDoctors
            _error.value = null
            Log.d(tag, "Loaded ${staticDoctors.size} doctors from static data")

            // Extract unique departments
            val uniqueDepartments = staticDoctors
                .map { it.department }
                .distinct()
                .sorted()
            _departments.value = uniqueDepartments
        } catch (e: Exception) {
            Log.e(tag, "Error loading static data", e)
            _error.value = "Could not load doctor information. Please try again."
        }
    }

    /**
     * Filter doctors by department
     */
    fun filterByDepartment(department: String?) {
        _selectedDepartment.value = department
    }

    /**
     * Get filtered doctors list
     */
    fun getFilteredDoctors(): List<Doctor> {
        return if (_selectedDepartment.value == null) {
            _doctors.value
        } else {
            _doctors.value.filter { it.department == _selectedDepartment.value }
        }
    }

     /**
      * Search doctors by name or department with fuzzy matching
      */
     fun searchDoctors(query: String): List<Doctor> {
         val searchQuery = query.lowercase().trim()
         if (searchQuery.isEmpty()) {
             return getFilteredDoctors()
         }

         return getFilteredDoctors().filter { doctor ->
             doctor.name.lowercase().contains(searchQuery) ||
             doctor.department.lowercase().contains(searchQuery) ||
             doctor.specialization.lowercase().contains(searchQuery) ||
             // Fuzzy matching for similar names
             hasSimilarName(doctor.name, searchQuery)
         }
     }

     /**
      * Check if doctor name is similar to search query (fuzzy matching)
      */
     private fun hasSimilarName(doctorName: String, query: String): Boolean {
         val nameTokens = doctorName.lowercase()
             .replace("dr.", "")
             .replace("dr ", "")
             .split(" ")
             .filter { it.isNotEmpty() }

         return nameTokens.any { token ->
             levenshteinDistance(token, query) <= 2 && token.length > 2
         }
     }

     /**
      * Calculate Levenshtein distance for fuzzy matching
      */
     private fun levenshteinDistance(s1: String, s2: String): Int {
         if (s1 == s2) return 0
         if (s1.isEmpty()) return s2.length
         if (s2.isEmpty()) return s1.length

         val dp = Array(s1.length + 1) { IntArray(s2.length + 1) }

         for (i in 0..s1.length) dp[i][0] = i
         for (j in 0..s2.length) dp[0][j] = j

         for (i in 1..s1.length) {
             for (j in 1..s2.length) {
                 val cost = if (s1[i - 1] == s2[j - 1]) 0 else 1
                 dp[i][j] = minOf(
                     dp[i - 1][j] + 1,      // deletion
                     dp[i][j - 1] + 1,      // insertion
                     dp[i - 1][j - 1] + cost // substitution
                 )
             }
         }

         return dp[s1.length][s2.length]
     }

    /**
     * Get knowledge base string for RAG (Retrieval-Augmented Generation)
     */
    fun getKnowledgeBase(): String {
        val doctorList = _doctors.value
        if (doctorList.isEmpty()) return "No doctor information available."

        val sb = StringBuilder()
        sb.append("Hospital Doctors and Departments Information: ")
        doctorList.forEach { doctor ->
            sb.append("Dr. ${doctor.name} is in the ${doctor.department} department. ")
            sb.append("Specialization: ${doctor.specialization}. ")
            sb.append("Experience: ${doctor.yearsOfExperience} years. ")
            sb.append("Cabin: ${doctor.cabin}. ")
            sb.append("Bio: ${doctor.aboutBio} ")
            sb.append("| ")
        }
        return sb.toString()
    }

    /**
     * Get doctor by ID
     */
    fun getDoctorById(id: String): Doctor? {
        return _doctors.value.find { it.id == id }
    }

    /**
     * Retry fetching doctors
     */
    fun retry() {
        cache.clearCache()
        fetchDoctors()
    }

    /**
     * Clear cache manually
     */
    fun clearCache() {
        cache.clearCache()
        Log.d(tag, "Cache cleared by user")
    }

    /**
     * Check if current data is from cache
     */
    fun isDataFromCache(): Boolean {
        return cache.getDoctors() != null && cache.isCacheValid()
    }
}
