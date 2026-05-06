# Map Location Integration - Quick Reference

## Summary
Navigation Screen now fetches locations from the **Temi robot's locally saved map** instead of hardcoded lists.

## Files Changed

| File | Change |
|------|--------|
| `MapLocationService.kt` | **NEW** - Service to fetch/convert robot map locations |
| `NavigationViewModel.kt` | Updated to load from MapLocationService |
| `NavigationScreen.kt` | Updated to call `loadLocationsFromMap()` on init |

## How It Works

1. **Screen loads** → Calls `viewModel.loadLocationsFromMap(robot?)`
2. **ViewModel calls** → `MapLocationService.fetchSavedLocations(robot)`
3. **Service accesses** → `robot.locations` property (list of location names on Temi map)
4. **Service converts** → Each location name → Location object with icon/Hindi name
5. **UI displays** → Locations from ViewModel state

## Key API

```kotlin
// In NavigationScreen or any Composable:
LaunchedEffect(robot) {
    viewModel.loadLocationsFromMap(robot)
}

// In NavigationViewModel:
fun loadLocationsFromMap(robot: Robot?)
fun onSearchTextChanged(text: String)
fun clearSearch(robot: Robot? = null)

// In MapLocationService (if needed):
MapLocationService.fetchSavedLocations(robot)
MapLocationService.searchLocations(robot, "pharmacy")
```

## Adding New Locations

1. **Register on Temi Robot**: Admin Panel → Map Management
2. **No code changes needed** - app auto-detects new locations
3. **(Optional) Add icon pattern** in `getLocationMetadata()` if icon is wrong

## Fallback

If robot is null or no locations found:
- Uses `LocationData.MOST_USED_LOCATIONS` and `LocationData.ALL_LOCATIONS`
- Graceful degradation - app still works

## Caching

- Locations loaded **once** when screen appears
- Cached in `_allLocationsFromMap`
- Subsequent searches use cache
- New robot instance triggers reload

## Testing

```
1. Build APK and install on Temi
2. Go to Navigation Screen
3. Should see all registered locations from Temi map
4. Search should work with English/Hindi
5. Click location → navigate
```

## Troubleshooting

| Problem | Solution |
|---------|----------|
| No locations showing | Check Temi map has registered locations |
| Wrong icon | Add pattern to `getLocationMetadata()` |
| Crashes | Check robot initialization in MainActivity |
| Empty grid | Verify robot.locations returns data via Logcat |

