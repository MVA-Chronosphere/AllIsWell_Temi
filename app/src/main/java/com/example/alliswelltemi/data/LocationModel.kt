package com.example.alliswelltemi.data

/**
 * Data model for hospital locations with Temi map integration
 *
 * @param id Unique identifier for the location
 * @param name English name of the location
 * @param nameHi Hindi name of the location (for voice recognition)
 * @param mapName Temi robot map name - must match pre-registered locations on Temi's control system
 * @param isPopular Whether location should appear in quick access
 * @param icon Emoji icon for UI display
 * @param floor Floor number where location is located (0, 1, 2, 3, etc.)
 */
data class Location(
    val id: String,
    val name: String,
    val nameHi: String = "",  // Hindi translation for voice recognition
    val mapName: String = "",  // Temi robot map identifier
    val isPopular: Boolean = false,
    val icon: String = "📍",
    val floor: Int = 0  // Floor number for location display
) {
    /**
     * Get location name in specified language
     */
    fun getNameInLanguage(language: String): String {
        return if (language == "hi" && nameHi.isNotEmpty()) nameHi else name
    }
}

/**
 * Predefined hospital locations with bilingual support and Temi map integration
 *
 * NOTE: mapName values MUST match locations pre-registered on your Temi robot's map.
 * If a location is not on your map, the robot cannot navigate to it.
 * To add new locations: Use Temi Admin Panel → Map Management → Register Location
 */
object LocationData {
    val MOST_USED_LOCATIONS = listOf(
        Location(
            id = "main_pharmacy",
            name = "Main Pharmacy",
            nameHi = "मुख्य फार्मेसी",
            mapName = "main_pharmacy",
            isPopular = true,
            icon = "💊",
            floor = 0
        ),
        Location(
            id = "main_reception",
            name = "Main Reception",
            nameHi = "मुख्य रिसेप्शन",
            mapName = "main_reception",
            isPopular = true,
            icon = "🔔",
            floor = 0
        ),
        Location(
            id = "pathology_department",
            name = "Pathology Department",
            nameHi = "पैथोलॉजी विभाग",
            mapName = "pathology_department",
            isPopular = true,
            icon = "🔬",
            floor = 1
        ),
        Location(
            id = "pathology_reception",
            name = "Pathology Reception",
            nameHi = "पैथोलॉजी रिसेप्शन",
            mapName = "pathology_reception",
            isPopular = true,
            icon = "🔬",
            floor = 1
        ),
        Location(
            id = "opd",
            name = "OPD",
            nameHi = "ओ.पी.डी",
            mapName = "opd",
            isPopular = true,
            icon = "🏥",
            floor = 2
        )
    )

    val ALL_LOCATIONS = listOf(
        Location(
            id = "home_base",
            name = "Home Base",
            nameHi = "होम बेस",
            mapName = "HOME BASE",
            icon = "🏠",
            floor = 0
        ),
        Location(
            id = "pathology_department",
            name = "Pathology Department",
            nameHi = "पैथोलॉजी विभाग",
            mapName = "PATHOLOGY DEPARTMENT",
            icon = "🔬",
            floor = 1
        ),
        Location(
            id = "ayushman_department",
            name = "Ayushman Department",
            nameHi = "आयुष्मान विभाग",
            mapName = "AYUSHMAN DEPARTMENT",
            icon = "🌿",
            floor = 2
        ),
        Location(
            id = "main_pharmacy",
            name = "Main Pharmacy",
            nameHi = "मुख्य फार्मेसी",
            mapName = "MAIN PHARMACY",
            icon = "💊",
            floor = 0
        ),
        Location(
            id = "back_entrance",
            name = "Back Entrance",
            nameHi = "पिछला प्रवेश द्वार",
            mapName = "BACK ENTRANCE",
            icon = "🚪",
            floor = 0
        ),
        Location(
            id = "rotary_dialysis_center",
            name = "Rotary Dialysis Center",
            nameHi = "रोटरी डायलिसिस सेंटर",
            mapName = "ROTARY DIALYSIS CENTER",
            icon = "🏥",
            floor = 1
        ),
        Location(
            id = "phlebotomy",
            name = "Phlebotomy",
            nameHi = "फलेबोटोमी",
            mapName = "PHLEBOTOMY",
            icon = "💉",
            floor = 1
        ),
        Location(
            id = "pathology_reception",
            name = "Pathology Reception",
            nameHi = "पैथोलॉजी रिसेप्शन",
            mapName = "PATHOLOGY RECEPTION",
            icon = "🔬",
            floor = 1
        ),
        Location(
            id = "opticals",
            name = "Opticals",
            nameHi = "ऑप्टिकल्स",
            mapName = "OPTICALS",
            icon = "👓",
            floor = 2
        ),
        Location(
            id = "opd",
            name = "OPD",
            nameHi = "ओ.पी.डी",
            mapName = "OPD",
            icon = "🏥",
            floor = 2
        ),
        Location(
            id = "dialysis_department",
            name = "Dialysis Department",
            nameHi = "डायलिसिस विभाग",
            mapName = "DIALYSIS DEPARTMENT",
            icon = "🏥",
            floor = 1
        ),
        Location(
            id = "main_entrance",
            name = "Main Entrance",
            nameHi = "मुख्य प्रवेश द्वार",
            mapName = "MAIN ENTRANCE",
            icon = "🚪",
            floor = 0
        ),
        Location(
            id = "ophthalmology_department",
            name = "Ophthalmology Department",
            nameHi = "नेत्र विज्ञान विभाग",
            mapName = "OPHTHALMOLOGY DEPARTMENT",
            icon = "👁️",
            floor = 3
        ),
        Location(
            id = "health_shop",
            name = "Health Shop",
            nameHi = "स्वास्थ्य दुकान",
            mapName = "HEALTH SHOP",
            icon = "🏪",
            floor = 0
        ),
        Location(
            id = "ipd_billing",
            name = "IPD Billing",
            nameHi = "आई.पी.डी बिलिंग",
            mapName = "IPD BILLING",
            icon = "💳",
            floor = 0
        ),
        Location(
            id = "radiology",
            name = "Radiology",
            nameHi = "रेडियोलॉजी",
            mapName = "RADIOLOGY",
            icon = "📊",
            floor = 2
        ),
        Location(
            id = "main_reception",
            name = "Main Reception",
            nameHi = "मुख्य रिसेप्शन",
            mapName = "MAIN RECEPTION",
            icon = "🔔",
            floor = 0
        ),
        Location(
            id = "common_washroom",
            name = "Common Washroom",
            nameHi = "सामान्य शौचालय",
            mapName = "COMMON WASHROOM",
            icon = "🚻",
            floor = 0
        )
    )

    /**
     * Find location by name (supports both English and Hindi)
     */
    fun findByName(name: String): Location? {
        val normalizedInput = name.lowercase().trim()
        return ALL_LOCATIONS.find { location ->
            location.name.lowercase().contains(normalizedInput) ||
            location.nameHi.lowercase().contains(normalizedInput) ||
            location.id.lowercase().contains(normalizedInput)
        }
    }

    /**
     * Find location by ID
     */
    fun findById(id: String): Location? {
        return ALL_LOCATIONS.find { it.id.lowercase() == id.lowercase() }
    }

    /**
     * Get list of all location names that should be registered on the Temi robot
     * Use this for debugging and setting up the robot's map
     */
    fun getAllLocationNamesToRegister(): List<String> {
        return ALL_LOCATIONS.map { it.name }.distinct().sorted()
    }

    /**
     * Check if a robot location matches any of our database locations
     * Useful for matching robot's saved locations to app database
     */
    fun matchRobotLocation(robotLocationName: String): Location? {
        return ALL_LOCATIONS.find { location ->
            location.name.lowercase() == robotLocationName.lowercase() ||
            location.mapName.lowercase() == robotLocationName.lowercase() ||
            location.id.lowercase() == robotLocationName.lowercase()
        }
    }
}
