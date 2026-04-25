package com.example.alliswelltemi.viewmodel

import android.content.Context
import androidx.compose.runtime.State
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.alliswelltemi.data.Doctor
import com.example.alliswelltemi.data.DoctorCache
import com.example.alliswelltemi.network.RetrofitClient
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

/**
 * ViewModel for managing doctors from Strapi CMS
 * PERFORMANCE OPTIMIZED: Eager loading + caching for instant access
 */
class DoctorsViewModel(private val context: Context? = null) : ViewModel() {

    private val _doctors = mutableStateOf<List<Doctor>>(emptyList())
    val doctors: State<List<Doctor>> = _doctors

    private val _isLoading = mutableStateOf(false)
    val isLoading: State<Boolean> = _isLoading

    private val _error = mutableStateOf<String?>(null)
    val error: State<String?> = _error

    private val _selectedDepartment = mutableStateOf<String?>(null)
    val selectedDepartment: State<String?> = _selectedDepartment

    private val _searchQuery = mutableStateOf("")
    val searchQuery: State<String> = _searchQuery

    private val _departments = mutableStateOf<List<String>>(emptyList())
    val departments: State<List<String>> = _departments

    private val _selectedDoctorForNav = mutableStateOf<Doctor?>(null)
    val selectedDoctorForNav: State<Doctor?> = _selectedDoctorForNav

    private val _isNavigating = mutableStateOf(false)
    val isNavigating: State<Boolean> = _isNavigating

    private val doctorCache: DoctorCache? = context?.let { DoctorCache(it) }

    /**
     * Get filtered and searched doctors list
     */
    val filteredDoctors: List<Doctor>
        get() {
            val query = _searchQuery.value.lowercase().trim()
            val dept = _selectedDepartment.value

            return _doctors.value.filter { doctor ->
                val matchesDept = dept == null || doctor.department == dept
                val matchesQuery = query.isEmpty() ||
                        doctor.name.lowercase().contains(query) ||
                        doctor.department.lowercase().contains(query) ||
                        doctor.specialization.lowercase().contains(query)
                matchesDept && matchesQuery
            }
        }

    init {
        // PERFORMANCE FIX: Load immediately with cache-first strategy
        fetchDoctorsWithCache()
    }

    /**
     * Update search query
     */
    fun onSearchQueryChanged(query: String) {
        _searchQuery.value = query
    }

    /**
     * Pre-fetch data or force refresh
     */
    fun refresh() {
        fetchDoctors(forceRefresh = true)
    }

    /**
     * PERFORMANCE OPTIMIZED: Fetch with cache-first strategy
     * 1. Load from cache immediately (instant UI)
     * 2. Fetch from API in background (fresh data)
     * 3. Update cache for next time
     */
    private fun fetchDoctorsWithCache() {
        viewModelScope.launch {
            try {
                // STEP 1: Try cache first for instant loading
                val cachedDoctors = withContext(Dispatchers.IO) {
                    doctorCache?.getDoctors()
                }

                if (cachedDoctors != null && doctorCache?.isCacheValid() == true) {
                    android.util.Log.d("DoctorsViewModel", "⚡ Loaded ${cachedDoctors.size} doctors from cache (instant)")
                    _doctors.value = cachedDoctors
                    extractDepartments(cachedDoctors)

                    // Still fetch in background to update cache
                    fetchDoctors(forceRefresh = false, silent = true)
                } else {
                    // No cache or expired - fetch from API
                    android.util.Log.d("DoctorsViewModel", "Cache miss or expired - fetching from API")
                    fetchDoctors(forceRefresh = true, silent = false)
                }
            } catch (e: Exception) {
                android.util.Log.e("DoctorsViewModel", "Error with cache strategy", e)
                fetchDoctors(forceRefresh = true, silent = false)
            }
        }
    }

    /**
     * Fetch all doctors from Strapi CMS
     * PERFORMANCE OPTIMIZED: Parallel processing + caching
     */
    fun fetchDoctors(forceRefresh: Boolean = false, silent: Boolean = false) {
        viewModelScope.launch {
            try {
                if (!silent) {
                    _isLoading.value = true
                }
                _error.value = null

                val startTime = System.currentTimeMillis()

                // Call API with optimized parameters
                val response = withContext(Dispatchers.IO) {
                    RetrofitClient.apiService.getDoctors()
                }

                val doctorList = withContext(Dispatchers.Default) {
                    response.data?.mapNotNull { doctorDoc ->
                        try {
                            doctorDoc.toDomain()
                        } catch (e: Exception) {
                            android.util.Log.w("DoctorsViewModel", "Failed to parse doctor: ${e.message}")
                            null
                        }
                    } ?: emptyList()
                }

                val elapsedMs = System.currentTimeMillis() - startTime
                android.util.Log.d("DoctorsViewModel", "⚡ Fetched ${doctorList.size} doctors in ${elapsedMs}ms")

                if (doctorList.isEmpty()) {
                    _error.value = "No doctors found. Please try again later."
                    return@launch
                }

                _doctors.value = doctorList
                extractDepartments(doctorList)

                // PERFORMANCE: Save to cache for next time
                withContext(Dispatchers.IO) {
                    doctorCache?.saveDoctors(doctorList)
                }

            } catch (e: java.net.SocketTimeoutException) {
                _error.value = "Connection timeout. Please check your network and try again."
                android.util.Log.e("DoctorsViewModel", "Timeout fetching doctors", e)
            } catch (e: java.net.UnknownHostException) {
                _error.value = "Unable to connect. Please check your internet connection."
                android.util.Log.e("DoctorsViewModel", "Network error fetching doctors", e)
            } catch (e: Exception) {
                _error.value = "Failed to load doctors. Please try again."
                android.util.Log.e("DoctorsViewModel", "Error fetching doctors", e)
            } finally {
                if (!silent) {
                    _isLoading.value = false
                }
            }
        }
    }

    /**
     * Extract unique departments from doctor list
     */
    private fun extractDepartments(doctorList: List<Doctor>) {
        val uniqueDepartments = doctorList
            .map { it.department }
            .filter { it.isNotBlank() }
            .distinct()
            .sorted()
        _departments.value = uniqueDepartments
    }

    /**
     * Filter doctors by department
     */
    fun filterByDepartment(department: String?) {
        _selectedDepartment.value = department
    }

    fun selectDoctorForNavigation(doctor: Doctor?) {
        _selectedDoctorForNav.value = doctor
    }

    fun setNavigating(navigating: Boolean) {
        _isNavigating.value = navigating
    }

    /**
     * Get doctor by ID
     */
    fun getDoctorById(id: String): Doctor? {
        return _doctors.value.find { id == it.id }
    }

    /**
     * Retry fetching doctors
     */
    fun retry() {
        fetchDoctors(forceRefresh = true)
    }

    /**
     * Clear cache and reload
     */
    fun clearCacheAndReload() {
        viewModelScope.launch {
            withContext(Dispatchers.IO) {
                doctorCache?.clearCache()
            }
            fetchDoctors(forceRefresh = true)
        }
    }
}

