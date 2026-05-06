package com.example.alliswelltemi.utils

import android.util.Log
import com.example.alliswelltemi.data.Location
import com.robotemi.sdk.Robot

/**
 * Service for fetching and managing locations from Temi's locally saved map
 *
 * The Temi robot stores locations in its map database which can be accessed
 * via the Robot SDK. This service retrieves those saved locations and converts
 * them to our Location data model.
 */
object MapLocationService {

    private const val TAG = "MapLocationService"

    /**
     * Fetch all saved locations from Temi's local map
     *
     * @param robot The Robot instance from Temi SDK
     * @return List of Location objects from the saved map, or empty list if robot is null/unavailable
     */
    fun fetchSavedLocations(robot: Robot?): List<Location> {
        return try {
            if (robot == null) {
                Log.w(TAG, "Robot instance is null, returning empty locations list")
                return emptyList()
            }

            // Get all saved locations from robot's map
            // The robot.locations property returns a list of location names that have been saved
            val locations = robot.locations ?: emptyList()

            if (locations.isEmpty()) {
                Log.i(TAG, "No saved locations found on robot map")
            } else {
                Log.i(TAG, "Found ${locations.size} saved locations: $locations")
            }

            // Convert robot locations to our Location data model
            locations.mapNotNull { locationName ->
                convertRobotLocationToLocation(locationName)
            }
        } catch (e: Exception) {
            Log.e(TAG, "Error fetching saved locations from robot: ${e.message}", e)
            emptyList()
        }
    }

    /**
     * Fetch popular/most used locations from the saved map
     * For now, returns first 4 locations as "most used"
     * Can be enhanced with usage tracking in future
     *
     * @param robot The Robot instance from Temi SDK
     * @return List of most-used Location objects
     */
    fun fetchMostUsedLocations(robot: Robot?): List<Location> {
        val allLocations = fetchSavedLocations(robot)
        return allLocations.take(4) // Return first 4 as most used
    }

    /**
     * Convert Temi robot location name to our Location data model
     * Maps common location names to appropriate icons and metadata
     *
     * @param locationName The name of the location from Temi's map
     * @return Location object with metadata and icon, or null if name is invalid
     */
    private fun convertRobotLocationToLocation(locationName: String): Location? {
        return try {
            val normalizedName = locationName.trim()
            if (normalizedName.isEmpty()) return null

            // Get icon and hindi name based on location name
            val (icon, nameHi) = getLocationMetadata(normalizedName)

            Location(
                id = normalizedName.lowercase().replace(" ", "_"),
                name = normalizedName,
                nameHi = nameHi,
                mapName = normalizedName, // The actual name saved on Temi map
                isPopular = false,
                icon = icon
            )
        } catch (e: Exception) {
            Log.e(TAG, "Error converting location '$locationName': ${e.message}")
            null
        }
    }

    /**
     * Get icon emoji and Hindi translation for a location name
     * This provides a fallback in case metadata isn't stored with the location
     *
     * @param locationName The English name of the location
     * @return Pair of (icon emoji, Hindi name)
     */
    private fun getLocationMetadata(locationName: String): Pair<String, String> {
        val lowerName = locationName.lowercase()

        return when {
            lowerName.contains("pharmacy") -> "💊" to "फार्मेसी"
            lowerName.contains("reception") -> "🔔" to "कार्यालय"
            lowerName.contains("icu") -> "🏥" to "आईसीयू"
            lowerName.contains("lab") || lowerName.contains("laboratory") -> "🧪" to "प्रयोगशाला"
            lowerName.contains("cardio") -> "❤️" to "कार्डियोलॉजी"
            lowerName.contains("neuro") -> "🧠" to "न्यूरोलॉजी"
            lowerName.contains("ortho") -> "🦴" to "ऑर्थोपेडिक्स"
            lowerName.contains("imaging") || lowerName.contains("radiology") -> "📷" to "इमेजिंग"
            lowerName.contains("emergency") -> "🚨" to "आपातकालीन"
            lowerName.contains("general") && lowerName.contains("ward") -> "🛏️" to "सामान्य वार्ड"
            lowerName.contains("private") && lowerName.contains("ward") -> "🏨" to "निजी वार्ड"
            lowerName.contains("cafe") || lowerName.contains("cafeteria") -> "☕" to "कैफेटेरिया"
            lowerName.contains("wash") || lowerName.contains("restroom") || lowerName.contains("toilet") -> "🚻" to "शौचालय"
            lowerName.contains("waiting") -> "💺" to "प्रतीक्षा क्षेत्र"
            lowerName.contains("exit") -> "🚪" to "निकास"
            lowerName.contains("entrance") -> "🚪" to "प्रवेश"
            else -> "📍" to locationName // Default: use location as hindi name fallback
        }
    }

    /**
     * Search saved locations by name (case-insensitive, partial match)
     *
     * @param robot The Robot instance from Temi SDK
     * @param query Search query
     * @return List of matching locations
     */
    fun searchLocations(robot: Robot?, query: String): List<Location> {
        val allLocations = fetchSavedLocations(robot)
        val lowerQuery = query.lowercase().trim()

        return allLocations.filter { location ->
            location.name.lowercase().contains(lowerQuery) ||
            location.nameHi.lowercase().contains(lowerQuery) ||
            location.id.lowercase().contains(lowerQuery)
        }
    }

    /**
     * Get a specific location by name from the saved map
     *
     * @param robot The Robot instance from Temi SDK
     * @param locationName The name of the location to find
     * @return Location object if found, null otherwise
     */
    fun getLocation(robot: Robot?, locationName: String): Location? {
        return try {
            val allLocations = fetchSavedLocations(robot)
            allLocations.find {
                it.name.equals(locationName, ignoreCase = true) ||
                it.mapName.equals(locationName, ignoreCase = true)
            }
        } catch (e: Exception) {
            Log.e(TAG, "Error getting location '$locationName': ${e.message}")
            null
        }
    }
}

