package com.example.alliswelltemi.viewmodel

import androidx.lifecycle.ViewModel
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.State
import com.example.alliswelltemi.data.Location
import com.example.alliswelltemi.data.LocationData
import com.example.alliswelltemi.data.DoctorData
import com.example.alliswelltemi.utils.MapLocationService
import com.robotemi.sdk.Robot

/**
 * ViewModel for Managing Navigation Screen State
 * Handles search, filtering, and navigation logic
 *
 * Locations are now fetched from the Temi robot's locally saved map
 */
class NavigationViewModel : ViewModel() {

    // Search text state
    private val _searchText = mutableStateOf("")
    val searchText: State<String> = _searchText

    // Is voice listening state
    private val _isListening = mutableStateOf(false)
    val isListening: State<Boolean> = _isListening

    // Filtered popular locations (now from saved map)
    private val _filteredPopularLocations = mutableStateOf<List<Location>>(emptyList())
    val filteredPopularLocations: State<List<Location>> = _filteredPopularLocations

    // Filtered all locations (now from saved map)
    private val _filteredAllLocations = mutableStateOf<List<Location>>(emptyList())
    val filteredAllLocations: State<List<Location>> = _filteredAllLocations

    // All locations from saved map (cached)
    private val _allLocationsFromMap = mutableStateOf<List<Location>>(emptyList())

    // Loading state
    private val _isLoading = mutableStateOf(false)
    val isLoading: State<Boolean> = _isLoading

    // Selected location
    private val _selectedLocation = mutableStateOf<Location?>(null)
    val selectedLocation: State<Location?> = _selectedLocation

    /**
     * Initialize and load locations from Temi's saved map
     * Must be called when the ViewModel is created and the robot becomes available
     */
    fun loadLocationsFromMap(robot: Robot?) {
        try {
            _isLoading.value = true

            // Fetch all locations from the robot's locally saved map
            val allLocations = MapLocationService.fetchSavedLocations(robot)
            _allLocationsFromMap.value = allLocations

            // If no locations found on robot map, fall back to defaults
            if (allLocations.isEmpty()) {
                _filteredPopularLocations.value = LocationData.MOST_USED_LOCATIONS
                _filteredAllLocations.value = LocationData.ALL_LOCATIONS
            } else {
                // Split into popular (first 4) and all locations
                _filteredPopularLocations.value = allLocations.take(4)
                _filteredAllLocations.value = allLocations
            }
        } catch (e: Exception) {
            android.util.Log.e("NavigationViewModel", "Error loading locations from map: ${e.message}")
            // Fall back to default locations if there's an error
            _filteredPopularLocations.value = LocationData.MOST_USED_LOCATIONS
            _filteredAllLocations.value = LocationData.ALL_LOCATIONS
        } finally {
            _isLoading.value = false
        }
    }

    /**
     * Update search text and filter locations from the loaded map
     */
    fun onSearchTextChanged(text: String) {
        _searchText.value = text
        filterLocations(text)
    }

    /**
     * Clear search text and reset filtered lists
     */
    fun clearSearch(robot: Robot? = null) {
        _searchText.value = ""
        // Use cached map locations if available, otherwise use defaults
        if (_allLocationsFromMap.value.isNotEmpty()) {
            _filteredPopularLocations.value = _allLocationsFromMap.value.take(4)
            _filteredAllLocations.value = _allLocationsFromMap.value
        } else {
            _filteredPopularLocations.value = LocationData.MOST_USED_LOCATIONS
            _filteredAllLocations.value = LocationData.ALL_LOCATIONS
        }
    }

    /**
     * Filter locations based on search query
     * Filters from the map locations if available, otherwise from defaults
     */
    private fun filterLocations(query: String) {
        val lowerCaseQuery = query.lowercase().trim()

        // Use cached map locations or fall back to defaults
        val locationsToFilter = if (_allLocationsFromMap.value.isNotEmpty()) {
            _allLocationsFromMap.value
        } else {
            LocationData.ALL_LOCATIONS
        }

        if (lowerCaseQuery.isEmpty()) {
            _filteredPopularLocations.value = locationsToFilter.take(4)
            _filteredAllLocations.value = locationsToFilter
        } else {
            // Filter popular locations
            _filteredPopularLocations.value = locationsToFilter.take(4).filter {
                it.name.lowercase().contains(lowerCaseQuery) ||
                it.nameHi.lowercase().contains(lowerCaseQuery)
            }

            // Filter all locations
            _filteredAllLocations.value = locationsToFilter.filter {
                it.name.lowercase().contains(lowerCaseQuery) ||
                it.nameHi.lowercase().contains(lowerCaseQuery)
            }
        }
    }

    /**
     * Select a location for navigation
     */
    fun selectLocation(location: Location) {
        _selectedLocation.value = location
    }

    /**
     * Clear selected location
     */
    fun clearSelection() {
        _selectedLocation.value = null
    }

    /**
     * Set listening state (when voice input is active)
     */
    fun setListening(isListening: Boolean) {
        _isListening.value = isListening
    }

    /**
     * Set loading state
     */
    fun setLoading(isLoading: Boolean) {
        _isLoading.value = isLoading
    }

    /**
     * Handle voice input result
     */
    fun onVoiceInputResult(spokenText: String) {
        onSearchTextChanged(spokenText)
        _isListening.value = false
    }

    /**
     * Enhanced voice input handler - recognizes locations AND departments in both languages
     * Examples that work:
     * - "Pharmacy" or "फार्मेसी" → finds Pharmacy location
     * - "Cardiology" or "कार्डियोलॉजी" → finds Cardiology Department location if available
     */
    fun handleBilingualVoiceInput(spokenText: String) {
        val trimmedText = spokenText.trim()

        // First, try to find as a direct location (English or Hindi)
        val locationMatch = LocationData.findByName(trimmedText)
        if (locationMatch != null) {
            selectLocation(locationMatch)
            _isListening.value = false
            return
        }

        // Second, try department name recognition (for department locations)
        val deptMatch = DoctorData.findDepartmentByName(trimmedText)
        if (deptMatch != null) {
            // Search for location with matching department name
            val departmentLocation = _allLocationsFromMap.value.find { location ->
                location.name.lowercase().contains(deptMatch.lowercase()) ||
                location.nameHi.lowercase().contains(DoctorData.DEPARTMENT_TRANSLATIONS.find {
                    it.english == deptMatch
                }?.hindi?.lowercase() ?: "")
            } ?: LocationData.ALL_LOCATIONS.find { location ->
                location.name.lowercase().contains(deptMatch.lowercase()) ||
                location.nameHi.lowercase().contains(DoctorData.DEPARTMENT_TRANSLATIONS.find {
                    it.english == deptMatch
                }?.hindi?.lowercase() ?: "")
            }

            if (departmentLocation != null) {
                selectLocation(departmentLocation)
                _isListening.value = false
                return
            }
        }

        // If no direct match, fall back to search in both languages
        onVoiceInputResult(trimmedText)
    }
}
