package com.example.alliswelltemi.viewmodel

import androidx.compose.runtime.State
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.alliswelltemi.data.Doctor
import com.example.alliswelltemi.network.RetrofitClient
import kotlinx.coroutines.launch

/**
 * ViewModel for managing doctors from Strapi CMS
 */
class DoctorsViewModel : ViewModel() {

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

    init {
        fetchDoctors()
    }

    /**
     * Fetch all doctors from Strapi CMS
     */
    fun fetchDoctors() {
        viewModelScope.launch {
            try {
                _isLoading.value = true
                _error.value = null

                val response = RetrofitClient.apiService.getDoctors()

                val doctorList = response.data?.mapNotNull { doctorDoc ->
                    doctorDoc.toDomain()
                } ?: emptyList()

                _doctors.value = doctorList

                // Extract unique departments
                val uniqueDepartments = doctorList
                    .map { it.department }
                    .distinct()
                    .sorted()

                _departments.value = uniqueDepartments

            } catch (e: Exception) {
                _error.value = "Failed to fetch doctors: ${e.message}"
                android.util.Log.e("DoctorsViewModel", "Error fetching doctors", e)
            } finally {
                _isLoading.value = false
            }
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
     * Search doctors by name or department
     */
    fun searchDoctors(query: String): List<Doctor> {
        val searchQuery = query.lowercase().trim()
        if (searchQuery.isEmpty()) {
            return getFilteredDoctors()
        }

        return getFilteredDoctors().filter { doctor ->
            doctor.name.lowercase().contains(searchQuery) ||
            doctor.department.lowercase().contains(searchQuery) ||
            doctor.specialization.lowercase().contains(searchQuery)
        }
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
        fetchDoctors()
    }
}

