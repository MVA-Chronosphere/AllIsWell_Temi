# AGENTS.md - AI Agent Guide for AlliswellTemi Codebase

## Project Overview
**AlliswellTemi** is a production-ready **Jetpack Compose** Android application for a Temi robot hospital assistant running on 1920×1080 landscape displays. The architecture separates concerns across theme, components, screens, utilities, and viewmodels with Kotlin DSL build configuration and Temi Robot SDK v1.137.1 integration.

---

## Big Picture Architecture

### Global Interface Standards (Hospital Kiosk Mode)
**These elements are persistent across ALL screens:**
1. **Language System:** English / Hindi toggle (entire UI updates instantly)
2. **Voice Interaction States:**
   - `IDLE`: Default state, "Tap or speak" displayed
   - `LISTENING`: Active listening pulse, "Listening..." shown
   - `PROCESSING`: Thinking animation, "Processing..." shown
   - `FALLBACK`: "Sorry, I didn't understand. Please try again."
3. **Auto-Reset Behavior:** System returns to Home Page after **30 seconds of inactivity** (required for multi-patient kiosk use)
4. **Display Standards:** 1920×1080 landscape, NABH accreditation indicator in header, date/time display in top bar

### Layered Architecture (read ARCHITECTURE_GUIDE.md for diagrams)
```
Presentation (UI) → Integration (Utils) → Resources → Configuration
```

**Key insight:** The app uses a **tight Temi SDK integration** within Compose composables rather than a ViewModel-based architecture. State is managed locally in screens using `remember` + `mutableStateOf`. This is intentional for this single-screen, real-time voice/navigation robot application.

### Major Components & Their Purpose
- **MainActivity.kt** - Compose entry point, Robot SDK initialization, screen routing (main/navigation/doctors/appointment/feedback)
- **TemiMainScreen.kt** - Primary UI (Home page with 4 main menu items: Find/Navigate, Doctors/Departments, Book Appointment, Feedback)
- **NavigationScreen.kt** - Location search, filtering, navigation mode with "Taking you to..." messaging
- **DoctorsScreen.kt** (planned) - Doctors/Departments toggle view with profiles and booking
- **AppointmentBookingScreen.kt** (planned) - Multi-step booking (select doctor → date → time → patient details → confirmation with token)
- **FeedbackScreen.kt** (planned) - Star rating (1-5) + optional comment + submit confirmation
- **TemiComponents.kt** - Reusable composables (cards, gradients, animations, voice state indicators)
- **TemiUtils.kt** - Temi SDK wrappers (speak, navigate, emergency alerts, localization helpers)
- **Theme.kt** - Material3 dark theme with neon cyan/purple accents (#0B1220 background)
- **NavigationViewModel.kt** - State management for search, filtering, location selection, voice listening
- **LocationModel.kt** - Hospital location data (pharmacy, ICU, pathology lab, billing counter, OPD)

### Critical Data Flow
1. **User Input** (card click/voice) → TemiMainScreen state change or screen navigation
2. **Screen Navigation** → MainActivity updates `currentScreen.value` to switch between screens (main/navigation/doctors/appointment/feedback)
3. **ViewModel State** → NavigationViewModel/AppointmentViewModel manage local search, filtering, booking progress
4. **Robot Actions** → Temi SDK calls (TTS with bilingual strings, navigation to location, emergency alerts)
5. **30-Second Auto-Reset** → Timer in MainActivity returns to home screen on user inactivity

**Why this matters:** When adding features, always:
- Update state in the composable or ViewModel that triggers the change
- Call `robot?.speak()` directly in click handlers (no separate command layer)
- Reference bilingual strings from `R.string.*` (defined in strings.xml)
- Use `colorResource()` for theme colors (defined in colors.xml)
- Handle screen transitions via `currentScreen.value = "screen_name"` in MainActivity
- Implement auto-reset timer for 30-second inactivity (required for hospital kiosk mode)

---

## Developer Workflows

### Building & Running
```bash
# Sync Gradle (always first if dependencies change)
./gradlew sync

# Debug build + install
./gradlew installDebug

# Clean build if Compose caching issues
./gradlew clean build

# Deploy to Temi robot (requires ADB + IP)
adb connect <TEMI_IP>
adb install -r app/build/outputs/apk/debug/AlliswellTemi-debug.apk
```

**Key build knowledge:**
- Build config uses **Kotlin DSL** (settings.gradle.kts, build.gradle.kts) - no Groovy
- Compose compiler extension version: **1.5.3** (in app/build.gradle.kts)
- Min SDK: **26**, Target/Compile: **34**
- Temi Maven repo: `https://maven.robotemi.com/repository/maven-public/` (in settings.gradle.kts)

### Testing & Debugging
- **No unit tests yet** - architecture supports them (see ARCHITECTURE_GUIDE.md)
- **Manual testing:** Build debug APK, install on Temi or emulator
- **Logcat:** Check `adb logcat | grep "AlliswellTemi"` for robot SDK errors
- **Compose Preview:** Add `@Preview` annotation to composables to preview in Android Studio

### Gradle Troubleshooting
If Gradle sync fails:
1. Check internet (Temi Maven repo requires connectivity)
2. Run `./gradlew --refresh-dependencies`
3. In Android Studio: File → Invalidate Caches → Restart
4. Never edit `.gradle/` folder directly

---

## Project-Specific Conventions & Patterns

### Naming & Code Structure
| Category | Convention | Example |
|----------|-----------|---------|
| Composables | Prefix "Temi" | `TemiMenuCard`, `TemiAvatarComponent` |
| Screens | Suffix "Screen" | `TemiMainScreen`, `NavigationScreen` |
| Utils | Object singleton + extension functions | `TemiUtils.speak()`, `Robot?.speak()` |
| Colors | XML + colorResource() | `colorResource(R.color.neon_cyan)` |
| Strings | Bilingual in XML | `stringResource(R.string.hospital_title)` |

### State Management Pattern
```kotlin
// Local state in composables (NOT ViewModels for this app)
var currentLanguage by remember { mutableStateOf("en") }
var isListening by remember { mutableStateOf(false) }

// Pass down as parameters or use CompositionLocal if deeply nested
TemiMenuCard(onClick = { 
    // Direct robot SDK calls here
    robot?.speak(TtsRequest.create("..."))
})
```

### Temi SDK Integration Pattern
```kotlin
// Always null-safe - robot may not be ready immediately
robot?.speak(TtsRequest.create(speech = "text", isShowOnConversationLayer = false))
robot?.goTo(location) // Navigation to saved locations
robot?.navigateTo(location) // Custom wrapper in TemiUtils
```

### Color & Theme Pattern
- **Dark background:** Always use `colorResource(R.color.dark_bg)` (#0B1220)
- **Accent colors:** Use `getGradientBrush()` helper for menu cards (defined in TemiComponents.kt)
- **Material3 theme:** Applied globally in MainActivity via `TemiTheme(darkTheme = true)`

### String/Localization Pattern
All UI text lives in `res/values/strings.xml` with English/Hindi pairs:
```xml
<string name="hospital_title">All Is Well Hospital</string>
<string name="hospital_title_hi">ऑल इज़ वेल हॉस्पिटल</string>
```
Access via `stringResource(R.string.hospital_title)` - language toggle handled in TemiMainScreen state.

### Documentation Pattern
- **Do NOT create unnecessary `.md` files** - use existing documentation files (AGENTS.md, ARCHITECTURE_GUIDE.md, QUICK_START.md)
- Only create new `.md` files for major deliverables or specific setup instructions (e.g., TEMI_SETUP_COMPLETE.md)
- Update existing guides instead of fragmenting documentation across multiple files

### Voice Interaction & State Management Pattern
For apps with multiple screens and voice input:
```kotlin
// Global state in MainActivity or ViewModel
var currentLanguage by remember { mutableStateOf("en") }
var voiceState by remember { mutableStateOf(VoiceState.IDLE) } // IDLE, LISTENING, PROCESSING, FALLBACK

enum class VoiceState { IDLE, LISTENING, PROCESSING, FALLBACK }

// Parse voice input contextually based on current screen
fun handleVoiceInput(spokenText: String) {
    when (currentScreen.value) {
        "navigation" -> navViewModel.onVoiceInputResult(spokenText)
        "doctors" -> doctorsViewModel.handleVoiceSearch(spokenText)
        "appointment" -> appointmentViewModel.handleVoiceInput(spokenText)
        "feedback" -> parseRatingFromVoice(spokenText) // e.g., "rate 4 stars"
    }
}

// Voice command examples from requirements:
// Navigation: "Take me to Pharmacy", "Where is the ICU?"
// Doctors: "Show cardiology doctors", "Find Dr. Sharma"
// Booking: "Book Dr. [Name]", "I need an appointment"
// General: "Show my appointments", "Go back", "Help"
```

---

## Integration Points & Dependencies

### External Dependencies (app/build.gradle.kts)
- **Temi Robot SDK 1.137.1** - Core robot control (TTS, navigation, lifecycle)
- **Jetpack Compose 1.5.3** - UI framework
- **Material3 1.1.1** - Design system
- **androidx.lifecycle 2.6.2** - Lifecycle management for `viewModel()` in Compose
- **androidx.activity:activity-compose 1.7.2** - Compose in AppCompatActivity

### Cross-Component Communication
- **MainActivity ↔ TemiMainScreen:** Robot instance passed as parameter + screen routing via `currentScreen.value`
- **TemiMainScreen ↔ Components:** State lifted to TemiMainScreen, passed down as parameters
- **Any screen ↔ Robot:** Null-safe direct calls (robot initialized in onRobotReady callback)

### Permissions & Robot Lifecycle
```xml
<!-- AndroidManifest.xml required permissions -->
<uses-permission android:name="android.permission.INTERNET" />
<uses-permission android:name="android.permission.RECORD_AUDIO" />
<uses-permission android:name="android.permission.CAMERA" />
```
Robot lifecycle: `onRobotReady()` callback in MainActivity initializes robot, onResume/onPause/onDestroy cleanup handled automatically by SDK listeners.

---

## Critical Files & When to Edit Them

| File | When to Edit | What NOT to Do |
|------|-------------|----------------|
| `TemiMainScreen.kt` | Add menu cards, handle interactions, change layout | Don't make it reusable - it's the main screen |
| `TemiComponents.kt` | Create reusable UI components (cards, buttons, animations) | Don't put business logic here |
| `TemiUtils.kt` | Add Temi SDK wrapper functions, extend `Robot?` extension functions | Don't add UI code here |
| `Theme.kt` | Adjust Material3 colors, typography sizes | Don't hardcode colors elsewhere |
| `res/values/colors.xml` | Define color palette | Reference via `colorResource()` in code |
| `res/values/strings.xml` | Add UI text (bilingual) | Hardcode strings in Compose code |
| `app/build.gradle.kts` | Add dependencies, update compile SDK | Never manually edit .gradle folder |
| `AndroidManifest.xml` | Add permissions, activities, intent filters | Modify sparingly - SDK initialization depends on manifest order |
| `data/LocationModel.kt` | Define hospital locations, QuickAccessLocations (OPD, Pharmacy, Pathology Lab, Billing Counter, ICU) | Hardcode location IDs in UI |
| `data/DoctorModel.kt` (create) | Create Doctor data class with id, name, department, yearsOfExperience, aboutBio, cabin fields | Use strings directly in UI lists |
| `viewmodel/AppointmentViewModel.kt` (create) | Multi-step appointment state: currentStep, selectedDoctor, selectedDate, selectedTimeSlot, patientName, patientPhone | Handle all appointment logic in composables |

---

### Hospital-Specific Data Models
**LocationModel.kt** should include:
```kotlin
data class Location(
    val id: String,          // "pharmacy", "icu", "pathology_lab", "billing_counter", "opd"
    val name: String,        // "Pharmacy", "ICU", etc.
    val isPopular: Boolean,  // Quick access locations
    val icon: String = "📍"
)

// Quick access locations per requirements:
val QUICK_ACCESS_LOCATIONS = listOf(
    Location("opd", "OPD", true, "🏥"),
    Location("pharmacy", "Pharmacy", true, "💊"),
    Location("pathology_lab", "Pathology Lab", true, "🧪"),
    Location("billing_counter", "Billing Counter", true, "💰"),
    Location("icu", "ICU", true, "🚨")
)
```

**DoctorModel.kt** (planned) should include:
```kotlin
data class Doctor(
    val id: String,
    val name: String,
    val department: String,
    val yearsOfExperience: Int,
    val aboutBio: String,
    val cabin: String  // e.g., "3A", "5B"
)

data class TimeSlot(val startTime: String, val endTime: String, val available: Boolean)
```

---

## Common Tasks & Implementation Patterns

### Adding a New Menu Card
1. Define in `TemiMainScreen.kt` `menuItems` list:
   ```kotlin
   MenuItemData(id = "feature", title = "...", subtitle = "...", icon = "...", colorKey = "blue")
   ```
2. Add action in `handleMenuItemClick()`:
   ```kotlin
   "feature" -> {
       robot?.speak(TtsRequest.create("Action text"))
       onNavigate("feature")
   }
   ```
3. (Optional) Add new gradient in `getGradientBrush()` in TemiComponents.kt

### Adding a New Screen
1. Create `NewScreen.kt` in `ui/screens/` with signature:
   ```kotlin
   @Composable
   fun NewScreen(
       robot: Robot? = null,
       onBackPress: () -> Unit = {}
   )
   ```
2. Use `TemiScreenScaffold` template for consistent header (back button, title, language toggle)
3. Add route handling in MainActivity: `"new_screen" -> NewScreen(...)`
4. Add case in `handleNavigation()`: `"new_screen" -> currentScreen.value = "new_screen"`

### Implementing Appointment Booking (Multi-Step)
1. Create `AppointmentViewModel.kt` for multi-step state:
   ```kotlin
   class AppointmentViewModel : ViewModel() {
       val currentStep = mutableStateOf(1) // 1=doctor, 2=date, 3=time, 4=details, 5=confirm
       val selectedDoctor = mutableStateOf<Doctor?>(null)
       val selectedDate = mutableStateOf<LocalDate?>(null)
       val selectedTimeSlot = mutableStateOf<TimeSlot?>(null)
       val patientName = mutableStateOf("")
       val patientPhone = mutableStateOf("")
   }
   ```
2. Create `AppointmentBookingScreen.kt` with step-based UI switching
3. Each step should validate before advancing
4. Final confirmation displays token prominently with "Take a picture" instruction

### Implementing Doctor Profile & Filtering
1. Create `Doctor.kt` data model with: `id, name, department, yearsOfExperience, aboutBio, cabin`
2. Create `DoctorsScreen.kt` with toggle for "Doctors" vs "Departments" view
3. Use NavigationViewModel or create `DoctorsViewModel` for filtering by specialty
4. Profile page navigates to doctor's cabin or opens booking flow

### Implementing Feedback with Voice Input
1. Create `FeedbackScreen.kt` with:
   - 5-star rating system (tap or voice: "Rate 4 stars")
   - Optional text comment field (voice or manual input)
   - Submit button that speaks confirmation
2. Parse voice input: `"rate [number] stars"` or `"[number]"` → update star rating
3. Show "Thank you for your feedback" dialog with auto-dismiss after 3 seconds

### Implementing 30-Second Auto-Reset Timer
Add to MainActivity onCreate:
```kotlin
private var inactivityTimer: Timer? = null

private fun startInactivityTimer() {
    inactivityTimer?.cancel()
    inactivityTimer = Timer()
    inactivityTimer?.schedule(timerTask {
        if (currentScreen.value != "main") {
            currentScreen.value = "main"
        }
    }, 30000) // 30 seconds
}

// Call startInactivityTimer() after every user interaction (click, voice input, etc.)
```

### Making Robot Speak in Multiple Languages
Use language state + strings.xml:
```kotlin
val language by remember { mutableStateOf("en") } // or "hi"
val text = stringResource(
    if (language == "en") R.string.some_text 
    else R.string.some_text_hi
)
robot?.speak(TtsRequest.create(speech = text))
```

### Creating Custom Animations
Use Compose `AnimatedVisibility` or `animateColorAsState()`:
```kotlin
val scale by animateFloatAsState(targetValue = if (isGlowing) 1.1f else 1f)
Image(modifier = Modifier.scale(scale), ...)
```
See TemiAvatarComponent in TemiComponents.kt for example.

---

## Performance & Optimization Notes

- **Recomposition:** Only affected composables recompose when state changes (Compose automatically optimizes)
- **Memory:** Avoid `remember` for large objects; use static resources in TemiUtils object
- **Animations:** GPU-accelerated (safe to use freely); test on device for frame rate
- **Battery:** Dark theme saves power on OLED Temi displays
- **Temi SDK calls:** Non-blocking; no need for coroutines unless awaiting results

---

## When You Get Stuck

1. **Gradle sync fails:** Check internet, run `./gradlew --refresh-dependencies`
2. **Compose preview won't load:** Rebuild project or invalidate Android Studio cache
3. **Robot not speaking:** Verify Temi SDK initialized (check logcat for "onRobotReady")
4. **Colors look wrong:** Check Material3 theme application in MainActivity + colorResource() usage
5. **Strings not localizing:** Ensure both English and Hindi entries in strings.xml
6. **Navigation not working:** Verify `onNavigate()` callback updates `currentScreen.value` in MainActivity

For detailed architecture diagrams and file references, see: **ARCHITECTURE_GUIDE.md** | **QUICK_START.md** | **TEMI_SETUP_COMPLETE.md**

---

**Last Updated:** April 18, 2026 | **SDK Version:** Temi 1.137.1 | **Compose:** 1.5.3

