package com.example.alliswelltemi.viewmodel

import androidx.lifecycle.ViewModel
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.State
import com.example.alliswelltemi.data.Location
import com.example.alliswelltemi.data.LocationData

/**
 * ViewModel for Managing Navigation Screen State
 * Handles search, filtering, and navigation logic
 */
class NavigationViewModel : ViewModel() {

    // Search text state
    private val _searchText = mutableStateOf("")
    val searchText: State<String> = _searchText

    // Is voice listening state
    private val _isListening = mutableStateOf(false)
    val isListening: State<Boolean> = _isListening

    // Filtered popular locations
    private val _filteredPopularLocations = mutableStateOf(LocationData.MOST_USED_LOCATIONS)
    val filteredPopularLocations: State<List<Location>> = _filteredPopularLocations

    // Filtered all locations
    private val _filteredAllLocations = mutableStateOf(LocationData.ALL_LOCATIONS)
    val filteredAllLocations: State<List<Location>> = _filteredAllLocations

    // Loading state
    private val _isLoading = mutableStateOf(false)
    val isLoading: State<Boolean> = _isLoading

    // Selected location
    private val _selectedLocation = mutableStateOf<Location?>(null)
    val selectedLocation: State<Location?> = _selectedLocation

    /**
     * Update search text and filter locations
     */
    fun onSearchTextChanged(text: String) {
        _searchText.value = text
        filterLocations(text)
    }

    /**
     * Clear search text
     */
    fun clearSearch() {
        _searchText.value = ""
        _filteredPopularLocations.value = LocationData.MOST_USED_LOCATIONS
        _filteredAllLocations.value = LocationData.ALL_LOCATIONS
    }

    /**
     * Filter locations based on search query
     */
    private fun filterLocations(query: String) {
        val lowerCaseQuery = query.lowercase().trim()

        if (lowerCaseQuery.isEmpty()) {
            _filteredPopularLocations.value = LocationData.MOST_USED_LOCATIONS
            _filteredAllLocations.value = LocationData.ALL_LOCATIONS
        } else {
            // Filter popular locations
            _filteredPopularLocations.value = LocationData.MOST_USED_LOCATIONS.filter {
                it.name.lowercase().contains(lowerCaseQuery)
            }

            // Filter all locations
            _filteredAllLocations.value = LocationData.ALL_LOCATIONS.filter {
                it.name.lowercase().contains(lowerCaseQuery)
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
}

