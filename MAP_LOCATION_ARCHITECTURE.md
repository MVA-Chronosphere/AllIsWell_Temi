# Map Location Integration - Architecture Diagram

## High-Level Data Flow

```
┌──────────────────────────────────────────────────────────────────────────┐
│                         USER INTERACTION FLOW                             │
└──────────────────────────────────────────────────────────────────────────┘

User Opens Navigation Screen
        │
        ▼
NavigationScreen Composable Renders
        │
        └─► LaunchedEffect(robot) triggered
            │
            ▼
        Check if robot != null
            │
        ├─► YES ──► viewModel.loadLocationsFromMap(robot)
        │           │
        │           ▼
        │       MapLocationService.fetchSavedLocations(robot)
        │           │
        │           ├─► Access robot.locations property
        │           │   (e.g., ["Pharmacy", "ICU", "Reception", ...])
        │           │
        │           ├─► Convert each location:
        │           │   "Pharmacy" ──►  Location(
        │           │                     id = "pharmacy",
        │           │                     name = "Pharmacy",
        │           │                     nameHi = "फार्मेसी",
        │           │                     icon = "💊"
        │           │                   )
        │           │
        │           ▼
        │       Return List<Location>
        │           │
        │           ▼
        │       ViewModel caches in _allLocationsFromMap
        │       Sets _filteredPopularLocations (first 4)
        │       Sets _filteredAllLocations (all)
        │           │
        │           ▼
        │       Compose recomposes with new locations
        │           │
        │           ▼
        │       UI displays location cards
        │
        └─► NO ──► Use fallback defaults
                   LocationData.MOST_USED_LOCATIONS (hardcoded)
```

## Component Interaction Diagram

```
                            ┌─────────────────────┐
                            │  MainActivity.kt    │
                            │  (Robot Init)       │
                            └──────────┬──────────┘
                                       │
                                       │ passes robot instance
                                       ▼
                    ┌──────────────────────────────────────┐
                    │    NavigationScreen Composable       │
                    │  • Displays location cards           │
                    │  • Handles user clicks               │
                    │  • Shows navigation overlay          │
                    └──────────────┬───────────────────────┘
                                   │
                   ┌───────────────┴────────────────┐
                   │ uses                           │ passes robot to
                   │                                └──────┐
                   ▼                                       ▼
    ┌───────────────────────────────────────┐  ┌─────────────────────────┐
    │  NavigationViewModel                  │  │  MapLocationService     │
    │  ────────────────────────────────────  │  │  ─────────────────────  │
    │  State:                               │  │  Function:              │
    │  • _filteredPopularLocations          │  │  • fetchSavedLocations()│
    │  • _filteredAllLocations              │  │  • searchLocations()    │
    │  • _allLocationsFromMap (cache)       │  │  • getLocation()        │
    │  • selectedLocation                   │  │  Private:               │
    │  • isLoading                          │  │  • convertLocation()    │
    │                                       │  │  • getLocationMetadata()│
    │  Functions:                           │  │                         │
    │  • loadLocationsFromMap()  ◄──────────┼──► calls this             │
    │  • onSearchTextChanged()               │  • Reads robot.locations│
    │  • selectLocation()                    │  • Returns Location[]    │
    │  • clearSelection()                    │                         │
    │  • filterLocations()                   │                         │
    └───────────────────────────────────────┘  └─────────────────────────┘
                   │                                       │
                   └───────────────┬───────────────────────┘
                                   │ reads from
                                   ▼
                    ┌──────────────────────────────────────┐
                    │  Temi Robot SDK (robot instance)     │
                    │  ────────────────────────────────    │
                    │  • robot.locations: List<String>     │
                    │    (saved map locations)              │
                    │  • robot.goTo(location: String)      │
                    │  • robot.speak(request: TtsRequest)  │
                    └──────────────────────────────────────┘
```

## Sequence Diagram - Loading Locations

```
ActivityCreated          NavigationScreen       NavigationViewModel       MapLocationService       Robot SDK
    │                          │                        │                       │                     │
    ├─ init robot ─────────────►                        │                       │                     │
    │                          │                        │                       │                     │
    ├─ navigate to nav screen──►                        │                       │                     │
    │                          │                        │                       │                     │
    │                          ├─ LaunchedEffect(robot) │                       │                     │
    │                          │                        │                       │                     │
    │                          ├──── loadLocations ────►│                       │                     │
    │                          │                        │                       │                     │
    │                          │                        ├─ fetchSavedLocations ─►                     │
    │                          │                        │                       │                     │
    │                          │                        │                       ├─ read.locations ───►│
    │                          │                        │                       │                     │
    │                          │                        │                       │◄─ ["Pharmacy",...] ─┤
    │                          │                        │                       │                     │
    │                          │                        │◄─ convert to Locations─┤                     │
    │                          │                        │                       │                     │
    │                          │◄─ return List<Loc> ───┤                       │                     │
    │                          │                        │                       │                     │
    │                          ├─ cache locations       │                       │                     │
    │                          │                        │                       │                     │
    │                          ├─ setState              │                       │                     │
    │                          │                        │                       │                     │
    │                          ├─ recompose ───────────┤                       │                     │
    │                          │                        │                       │                     │
    │                          ├─ show location cards ──┤                       │                     │
    │                          │                        │                       │                     │
```

## Search/Filter Flow

```
User Types in Search Box
        │
        ▼
onSearchTextChanged(text) called in ViewModel
        │
        ▼
_searchText.value = text
        │
        ▼
filterLocations(text) called
        │
        ├─► Is text empty?
        │   ├─ YES ──► Reset to _filteredPopularLocations = cached first 4
        │   │          Reset to _filteredAllLocations = cached all
        │   │
        │   └─ NO ──► Filter cached locations:
        │             _filteredPopularLocations = 
        │               cached[0:4].filter { contains(text) }
        │             _filteredAllLocations = 
        │               cached.filter { contains(text) }
        │
        ▼
State updates
        │
        ▼
Compose recomposes
        │
        ▼
UI shows filtered results
```

## Select Location & Navigation Flow

```
User Clicks Location
        │
        ▼
onLocationClick(location) called
        │
        ▼
selectLocation(location) in ViewModel
        │
        ▼
_selectedLocation.value = location
        │
        ▼
LaunchedEffect(selectedLocation) triggered
        │
        ├─► selectedLocation?.let { ... }
        │
        ├─► robot?.speak("Taking you to Pharmacy")
        │
        ├─► setLoading(true)
        │
        ├─► delay(2000)
        │
        ├─► robot?.goTo(location.name)
        │
        ├─► setLoading(false)
        │
        └─► clearSelection()
                │
                ▼
        LaunchedEffect also gets cleared
                │
                ▼
        Ready for next location selection
```

## State Management Flow

```
                    NavigationViewModel State Tree
                            │
                ┌───────────┼───────────────┐
                │           │               │
                ▼           ▼               ▼
            _searchText   _isListening   _isLoading
              ("")         (false)        (false)
                │           │               │
                │           │               │
        ┌───────┴──────┬────┴────┬──────────┴────┐
        │              │         │               │
        ▼              ▼         ▼               ▼
    _allLocationsFromMap    _filteredPopularLocations
    (cached map locations)   (first 4 or filtered)
         (empty)                  (empty)
         │                         │
         ├─ LaunchedEffect(robot)─►│
         │                         │
         ├─ fetchSaved("Pharmacy",  │
         │   "ICU", "Reception")    │
         │                         │
         ├─ Cache result            │
         │  ["Pharmacy":Location,    │
         │   "ICU":Location,         │
         │   "Reception":Location]   │
         │                         │
         └─────── set ───────────►│
                                  │
                                  ├─ Compose recomposes
                                  │
                                  ├─ Cards appear in UI
                                  │
                                  └─ _filteredAllLocations
                                      also populated
```

## Error Handling & Fallback

```
loadLocationsFromMap(robot)
        │
        ├─► robot == null?
        │   └─ YES ──► Fall back to LocationData defaults
        │
        ├─► API call fails?
        │   └─ Catch exception ──► Fall back to defaults
        │
        ├─► robot.locations empty?
        │   └─ Use LocationData defaults
        │
        └─ SUCCESS ──► Cache locations, display from map


Fallback Chain:
1. Try: Load from robot.locations
2. If empty/null: Use LocationData.MOST_USED_LOCATIONS
3. If error: Use LocationData.MOST_USED_LOCATIONS
4. Always: Show something to user (never blank)
```

## File Structure

```
app/src/main/java/com/example/alliswelltemi/
├── MainActivity.kt (robot initialized here)
├── viewmodel/
│   └── NavigationViewModel.kt (UPDATED - uses MapLocationService)
├── ui/
│   └── screens/
│       └── NavigationScreen.kt (UPDATED - calls loadLocationsFromMap)
├── utils/
│   └── MapLocationService.kt (NEW - fetches from robot.locations)
├── data/
│   └── LocationModel.kt (unchanged - Location data class)
│   └── LocationData.kt (or similar - fallback locations)
└── ...other files...
```

## Key Concepts

```
╔═══════════════════════════════════════════════════════════════╗
║  TEMI ROBOT LOCATIONS STRUCTURE                               ║
╠═══════════════════════════════════════════════════════════════╣
║                                                               ║
║  Temi Admin Panel (on robot)                                 ║
║      ↓                                                        ║
║  Map Management                                              ║
║      ↓                                                        ║
║  Registered Locations on Robot Map:                          ║
║  [                                                           ║
║    "Pharmacy",      ← SDK provides this list                 ║
║    "ICU",                                                    ║
║    "Reception",                                              ║
║    "Laboratory",                                             ║
║    "Cardiology Department",                                  ║
║    ...                                                       ║
║  ]                                                           ║
║  (Accessible via: robot.locations)                           ║
║                                                              ║
╚═══════════════════════════════════════════════════════════════╝

╔═══════════════════════════════════════════════════════════════╗
║  APP LOCATION CONVERSION                                      ║
╠═══════════════════════════════════════════════════════════════╣
║                                                               ║
║  From Temi SDK              To Our App Model                 ║
║  ───────────────────────────────────────────────             ║
║  "Pharmacy"          →  Location(                            ║
║  (string)               id="pharmacy",                       ║
║  (just a name)          name="Pharmacy",                     ║
║                         nameHi="फार्मेसी",                    ║
║                         icon="💊",                           ║
║                         mapName="Pharmacy"                   ║
║                     )                                        ║
║                                                              ║
║  Benefits: Get icons, bilingual names, searchable           ║
║                                                              ║
╚═══════════════════════════════════════════════════════════════╝
```

## Complete Request-Response Cycle

```
┌────────────────────────────────────────────────────────────────┐
│                                                                │
│ USER OPENS NAVIGATION SCREEN                                  │
│                                                                │
│ ┌─────────────────────────────────────────────────────────┐   │
│ │ REQUEST 1: Load Locations                              │   │
│ │ ─────────────────────────────────────────────────────── │   │
│ │ NavigationScreen initiates:                            │   │
│ │   viewModel.loadLocationsFromMap(robot)                │   │
│ │                                                         │   │
│ │ MapLocationService fetches from Temi:                 │   │
│ │   Access: robot.locations                             │   │
│ │   Returns: ["Pharmacy", "ICU", "Reception", ...]     │   │
│ │                                                         │   │
│ │ MapLocationService converts and returns:              │   │
│ │   [                                                    │   │
│ │     Location(..., name="Pharmacy", icon="💊"),         │   │
│ │     Location(..., name="ICU", icon="🏥"),              │   │
│ │     Location(..., name="Reception", icon="🔔"),        │   │
│ │     Location(..., name="Laboratory", icon="🧪")        │   │
│ │   ]                                                    │   │
│ │                                                         │   │
│ │ ViewModel stores result and updates state              │   │
│ │                                                         │   │
│ │ RESPONSE 1: ✅ Locations loaded and cached             │   │
│ │                                                         │   │
│ └─────────────────────────────────────────────────────────┘   │
│                                                                │
│ ┌─────────────────────────────────────────────────────────┐   │
│ │ STEP 2: Compose Recomposes                             │   │
│ │ ─────────────────────────────────────────────────────── │   │
│ │ UI reads from ViewModel state:                         │   │
│ │   _filteredPopularLocations (4 locations)              │   │
│ │   _filteredAllLocations (all locations)                │   │
│ │                                                         │   │
│ │ Creates location cards and displays them               │   │
│ │                                                         │   │
│ │ RESPONSE 2: ✅ Location cards visible in UI             │   │
│ │                                                         │   │
│ └─────────────────────────────────────────────────────────┘   │
│                                                                │
│ ┌─────────────────────────────────────────────────────────┐   │
│ │ REQUEST 3: User clicks "Pharmacy" card                 │   │
│ │ ─────────────────────────────────────────────────────── │   │
│ │ Calls: viewModel.selectLocation(Location(...))        │   │
│ │                                                         │   │
│ │ Updates: _selectedLocation.value = Location(...)      │   │
│ │                                                         │   │
│ │ LaunchedEffect(selectedLocation) executes:            │   │
│ │   • robot?.speak("Taking you to Pharmacy")            │   │
│ │   • delay(2000)                                        │   │
│ │   • robot?.goTo("Pharmacy")                           │   │
│ │                                                         │   │
│ │ RESPONSE 3: ✅ Navigation started                       │   │
│ │                                                         │   │
│ └─────────────────────────────────────────────────────────┘   │
│                                                                │
│ ┌─────────────────────────────────────────────────────────┐   │
│ │ REQUEST 4: User types in search "phar"                │   │
│ │ ─────────────────────────────────────────────────────── │   │
│ │ Calls: viewModel.onSearchTextChanged("phar")          │   │
│ │                                                         │   │
│ │ ViewModel filters cached locations:                    │   │
│ │   _filteredPopularLocations = [{Pharmacy}]             │   │
│ │   _filteredAllLocations = [{Pharmacy}]                 │   │
│ │                                                         │   │
│ │ UI recomposes showing only matching location           │   │
│ │                                                         │   │
│ │ RESPONSE 4: ✅ Search results displayed                 │   │
│ │                                                         │   │
│ └─────────────────────────────────────────────────────────┘   │
│                                                                │
└────────────────────────────────────────────────────────────────┘
```

This architecture ensures:
- ✅ **Dynamic**: Locations come from robot map, not hardcoded
- ✅ **Efficient**: Cached after first load, instant searches
- ✅ **Robust**: Fallback to defaults if robot unavailable
- ✅ **Maintainable**: Clear separation of concerns
- ✅ **Scalable**: Works with any number of locations on Temi map

