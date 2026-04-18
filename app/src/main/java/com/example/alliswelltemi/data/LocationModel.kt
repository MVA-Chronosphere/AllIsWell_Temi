package com.example.alliswelltemi.data

/**
 * Data model for hospital locations
 */
data class Location(
    val id: String,
    val name: String,
    val isPopular: Boolean = false,
    val icon: String = "📍"
)

/**
 * Predefined hospital locations
 */
object LocationData {
    val MOST_USED_LOCATIONS = listOf(
        Location(id = "pharmacy", name = "Pharmacy", isPopular = true, icon = "💊"),
        Location(id = "reception", name = "Reception", isPopular = true, icon = "🔔"),
        Location(id = "icu", name = "ICU", isPopular = true, icon = "🏥"),
        Location(id = "laboratory", name = "Laboratory", isPopular = true, icon = "🧪")
    )

    val ALL_LOCATIONS = listOf(
        Location(id = "pharmacy", name = "Pharmacy", icon = "💊"),
        Location(id = "reception", name = "Reception", icon = "🔔"),
        Location(id = "icu", name = "ICU", icon = "🏥"),
        Location(id = "laboratory", name = "Laboratory", icon = "🧪"),
        Location(id = "cardiology", name = "Cardiology Department", icon = "❤️"),
        Location(id = "neurology", name = "Neurology Department", icon = "🧠"),
        Location(id = "orthopedics", name = "Orthopedics Department", icon = "🦴"),
        Location(id = "imaging", name = "Imaging Department", icon = "📷"),
        Location(id = "emergency", name = "Emergency Room", icon = "🚨"),
        Location(id = "general_ward", name = "General Ward", icon = "🛏️"),
        Location(id = "private_ward", name = "Private Ward", icon = "🏨"),
        Location(id = "cafeteria", name = "Cafeteria", icon = "☕"),
        Location(id = "washroom", name = "Washroom", icon = "🚻"),
        Location(id = "waiting_area", name = "Waiting Area", icon = "💺"),
        Location(id = "exit", name = "Main Exit", icon = "🚪")
    )
}

