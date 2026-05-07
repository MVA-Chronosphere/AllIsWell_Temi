# Available Soon Screen Implementation

## Overview
A new "Available Soon" screen has been added as a placeholder for future features like Appointment Booking. This screen displays an animated coming soon message with bilingual support (English/Hindi).

## Changes Made

### 1. New Screen Created
**File:** `/app/src/main/java/com/example/alliswelltemi/ui/screens/AvailableSoonScreen.kt`

Features:
- Full-screen animated UI with pulsing icon
- Bilingual support (English / Hindi)
- Clean navigation bar integration
- Animated schedule icon (1.5s infinite pulse)
- Professional hospital theme colors (Sky Blue accent)
- Responsive layout
- Back button to return to main screen

### 2. Updated MainActivity
**File:** `/app/src/main/java/com/example/alliswelltemi/MainActivity.kt`

Changes:
- Added new route handler for "availableSoon" screen in the when statement (lines 217-222)
- Routes to `AvailableSoonScreen` component when `currentScreen.value = "availableSoon"`
- Changed `DoctorsScreen` navigation: `onSelectDoctor` callback no longer navigates to appointment screen (line 214)
  - Previously: `onSelectDoctor = { currentScreen.value = "appointment" }`
  - Now: `onSelectDoctor = { /* Just navigate to doctor, don't go to appointment */ }`
  - This means clicking on a doctor name now only navigates to their cabin, without forcing the appointment screen

### 3. Updated Main Menu
**File:** `/app/src/main/java/com/example/alliswelltemi/ui/screens/TemiMainScreen.kt`

Changes:
- Updated "Book Appointment" button to navigate to "availableSoon" screen (line 143)
- Previously: `HeroMenuCard(gridItems[2], Modifier.weight(1f), { onNavigate("appointment") })`
- Now: `HeroMenuCard(gridItems[2], Modifier.weight(1f), { onNavigate("availableSoon") })`

## User Experience Flow

### Before
1. User clicks "Book Appointment" in main menu → Goes to Appointment Booking Screen
2. User clicks doctor name in Doctors screen → Automatically navigates to Appointment Screen
3. User clicks doctor name → Navigates to cabin AND shows appointment screen

### After
1. User clicks "Book Appointment" in main menu → Shows "Available Soon" placeholder screen
2. User clicks doctor name in Doctors screen → Only navigates to doctor's cabin (no screen change)
3. User can explore doctor listings and navigate to them without being forced to book appointments

## Screen Details

### AvailableSoonScreen Component
```
Location: ui/screens/AvailableSoonScreen.kt
Props:
  - onBackPress: () -> Unit = {} (callback to return to previous screen)
  - currentLanguage: String = "en" (language setting)
  - featureName: String = "This Feature" (customizable feature name)

Can be reused for other "coming soon" features by changing the featureName parameter
```

## Bilingual Support
The screen automatically shows content in the selected language:
- **English:** "Coming Soon" + "This feature will be available soon. Please check back later."
- **Hindi:** "जल्द ही आ रहा है" + "यह सुविधा शीघ्र ही उपलब्ध होगी। कृपया जल्द ही वापस आएं।"

## Color Theme
- Uses Hospital Theme Colors (from `HospitalColors` object)
- Primary accent: Sky Blue (`HospitalColors.SkyBlue`)
- Text: Deep Slate (`HospitalColors.DeepSlate`)
- Background: Light background (`HospitalColors.Background`)
- Maintains consistency with the rest of the app

## Navigation Routes
The app now supports these screen routes:
- `"main"` → TemiMainScreen (home)
- `"navigation"` → NavigationScreen (location navigation)
- `"doctors"` → DoctorsScreen (doctor listing)
- `"availableSoon"` → AvailableSoonScreen (placeholder for features)
- `"appointment"` → AppointmentBookingScreen (still available if needed later)
- `"feedback"` → FeedbackScreen (feedback submission)

## Future Integration
When Appointment Booking is ready to be enabled:
1. Change TemiMainScreen.kt line 143 back to: `{ onNavigate("appointment") }`
2. Change MainActivity.kt line 214 back to: `onSelectDoctor = { currentScreen.value = "appointment" }`
3. The AvailableSoonScreen can be deleted or kept for other future features

## Testing
- Verify "Book Appointment" button shows the coming soon screen
- Verify doctor click navigates to cabin only (no automatic appointment screen)
- Test bilingual support by toggling language
- Verify back button returns to home screen
- Check animations work smoothly

