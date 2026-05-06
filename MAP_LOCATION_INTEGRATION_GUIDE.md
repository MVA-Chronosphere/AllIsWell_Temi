# Map Location Integration - Implementation Guide

## Overview
The Navigation Screen now fetches locations from the **locally saved map on the Temi robot** instead of using hardcoded location lists. This allows the app to dynamically display any location that has been registered on the Temi robot's map system.

## Key Components

### 1. **MapLocationService.kt** (New)
Located at: `app/src/main/java/com/example/alliswelltemi/utils/MapLocationService.kt`

A singleton service that handles all map-related operations:

**Main Functions:**
- `fetchSavedLocations(robot: Robot?): List<Location>` - Downloads all saved locations from the robot's map
- `fetchMostUsedLocations(robot: Robot?): List<Location>` - Returns the first 4 locations (for "Most Used" tab)
- `searchLocations(robot: Robot?, query: String): List<Location>` - Searches saved locations by name
- `getLocation(robot: Robot?, locationName: String): Location?` - Retrieves a specific location

**How It Works:**
1. Accesses `robot.locations` - a list of location names saved on the Temi robot
2. Converts each location name to our `Location` data model with metadata (icons, Hindi names)
3. Uses `getLocationMetadata()` to intelligently assign icons and translations based on location name patterns
4. Falls back to default locations if the robot instance is null or unavailable

### 2. **NavigationViewModel.kt** (Updated)
Located at: `app/src/main/java/com/example/alliswelltemi/viewmodel/NavigationViewModel.kt`

**New Functions:**
- `loadLocationsFromMap(robot: Robot?)` - Loads all saved locations from the robot's map (called once on screen load)
- `onSearchTextChanged(text: String)` - Filters locations from the loaded map based on search query
- `clearSearch(robot: Robot? = null)` - Resets filters to show all loaded locations

**Updated Logic:**
- `_filteredPopularLocations` and `_filteredAllLocations` now populate from `MapLocationService` instead of hardcoded `LocationData`
- Caches all loaded locations in `_allLocationsFromMap` to avoid repeated API calls
- Falls back to default `LocationData` if no locations are found on the robot map

### 3. **NavigationScreen.kt** (Updated)
Located at: `app/src/main/java/com/example/alliswelltemi/ui/screens/NavigationScreen.kt`

**Key Change:**
```kotlin
// Load locations from Temi's saved map when screen is first composed
LaunchedEffect(robot) {
    if (robot != null) {
        viewModel.loadLocationsFromMap(robot)
    }
}
```

This `LaunchedEffect` ensures:
- Locations are fetched once when the robot becomes available
- The UI automatically updates with the loaded locations
- If the robot is not ready, fallback locations are used

## Data Flow

```
[Temi Robot Map] 
    ↓
[robot.locations property]
    ↓
[MapLocationService.fetchSavedLocations()]
    ↓
[Converts to Location objects with metadata]
    ↓
[NavigationViewModel caches result]
    ↓
[NavigationScreen displays locations from ViewModel]
```

## How to Add Locations to the Robot Map

1. **On the Temi Robot:**
   - Use Temi Admin Panel → Map Management
   - Register new locations with their names (e.g., "Pharmacy", "Reception", "ICU")

2. **In the App:**
   - No code changes needed! The service will automatically detect and display the new location
   - The `getLocationMetadata()` function will intelligently assign an icon based on the location name
   - If your location doesn't match any pattern, it defaults to 📍 icon

3. **To Customize Metadata (Optional):**
   - If you want a specific icon for a location, add it to the pattern matching in `getLocationMetadata()`
   - Example: Add `lowerName.contains("surgery")` to assign a medical icon

## Fallback Behavior

If the Temi robot is not available or no locations are saved:
- The app falls back to `LocationData.MOST_USED_LOCATIONS` and `LocationData.ALL_LOCATIONS`
- This ensures the app remains functional even if the robot SDK is unavailable
- Once the robot becomes available, it automatically loads the real map locations

## Usage in Other Screens

To fetch map locations in other parts of the app:

```kotlin
// In any Composable or ViewModel
val robot: Robot? = // ... get robot instance
val allLocations = MapLocationService.fetchSavedLocations(robot)
val searchResults = MapLocationService.searchLocations(robot, "pharmacy")
val specificLocation = MapLocationService.getLocation(robot, "Pharmacy")
```

## Testing

To test this feature:

1. Build and install the app on the Temi robot
2. Navigate to the Navigation Screen
3. You should see location cards for all locations registered on the Temi map
4. The locations should include English/Hindi names and appropriate icons
5. Search filtering should work across both languages

## Performance Considerations

- Locations are **loaded once** on screen composition and cached in `_allLocationsFromMap`
- Subsequent searches use the cached list, minimizing API calls
- The service is null-safe and handles missing robot gracefully
- Fallback to default locations is automatic if loading fails

## Future Enhancements

- Add "Most Used" tracking to persist actual usage patterns
- Store location metadata (floor, building, description) on the robot
- Add location availability status (open/closed)
- Support location groups/departments
- Add voice-controlled location selection

## Troubleshooting

**Issue: No locations displayed**
- Check that locations are registered on the Temi robot's map
- Verify the robot instance is properly initialized in MainActivity
- Check Logcat for errors: `adb logcat | grep "MapLocationService"`

**Issue: Incorrect icons for locations**
- Add a pattern matching rule in `getLocationMetadata()` for your location name
- Example: If location is named "Main Pharmacy", the pattern will match "pharmacy"

**Issue: App crashes when robot is unavailable**
- The code is null-safe; if robot is null, it falls back gracefully
- Check MainActivityError logs for robot initialization issues

