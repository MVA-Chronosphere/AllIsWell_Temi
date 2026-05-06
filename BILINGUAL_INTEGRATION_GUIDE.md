# Bilingual Department Recognition - Integration Guide

## Quick Reference for Developers

### For Navigation Screen (Location-Based)

```kotlin
// In NavigationScreen composable, when voice input is received:
val voiceInput = "कार्डियोलॉजी"  // User speaks in Hindi
viewModel.handleBilingualVoiceInput(voiceInput)
// ✅ System recognizes it as "Cardiology" and navigates if location exists

// Or English equivalent:
viewModel.handleBilingualVoiceInput("Pharmacy")  // Works with location names too
```

**What happens:**
1. Checks if input matches a location (English or Hindi) → navigates if found
2. If not, checks if input matches a department name (English or Hindi)
3. Searches for a location with that department name
4. Falls back to general search if no direct match

---

### For Doctors Screen (Doctor & Department Filtering)

```kotlin
// In DoctorsScreen composable, when department filter is clicked:

// English department filter
viewModel.filterByDepartment("Cardiology")

// Hindi department filter (will automatically convert to English internally)
viewModel.filterByDepartment("कार्डियोलॉजी")
// ✅ Both produce identical results - doctors filtered by Cardiology

// When voice input is received:
val voiceInput = "न्यूरोलॉजी"  // User speaks Hindi
viewModel.handleVoiceInput(voiceInput)
// ✅ System recognizes as "Neurology" department and filters doctors
```

**What happens:**
1. If voice input matches a department (English or Hindi) → filters by that department
2. Otherwise → treats input as doctor name search (full text search)

---

### Department Names (Bilingual Reference)

Use this lookup table for testing and documentation:

| English | Hindi |
|---------|-------|
| Cardiology | कार्डियोलॉजी |
| Neurology | न्यूरोलॉजी |
| Orthopedics | ऑर्थोपेडिक्स |
| Dermatology | त्वचा विज्ञान |
| General Surgery | सामान्य सर्जरी |
| Pediatrics | बाल चिकित्सा |
| Ophthalmology | नेत्र विज्ञान |

---

### Common Voice Input Scenarios

#### Scenario 1: Patient Looking for Pharmacy (English)
```
Patient: "Where is the Pharmacy?"
System: Navigates to Pharmacy location
Flow: handleBilingualVoiceInput() → LocationData.findByName() → ✅ Match found
```

#### Scenario 2: Patient Looking for Pharmacy (Hindi)
```
Patient: "फार्मेसी कहाँ है?"
System: Navigates to Pharmacy location
Flow: handleBilingualVoiceInput() → LocationData.findByName("फार्मेसी") → ✅ Match found
```

#### Scenario 3: Patient Looking for Cardiology Doctor (English)
```
Patient: "Show me Cardiology doctors"
System: Filters to show Cardiology doctors
Flow: handleVoiceInput() → DoctorData.findDepartmentByName("Cardiology") → ✅ Department match
```

#### Scenario 4: Patient Looking for Cardiology Doctor (Hindi)
```
Patient: "कार्डियोलॉजी के डॉक्टर दिखाओ"
System: Filters to show Cardiology doctors
Flow: handleVoiceInput() → DoctorData.findDepartmentByName("कार्डियोलॉजी") → ✅ Department match
```

#### Scenario 5: Patient Looking for Specific Doctor
```
Patient: "Find Dr. Sharma"
System: Searches for doctors with "Sharma" in name
Flow: handleVoiceInput() → DoctorData.findDepartmentByName() returns null → ✅ Search by doctor name
```

---

## Implementation Examples

### Example 1: Add Voice Search to DoctorsScreen

```kotlin
@Composable
fun DoctorsScreen(
    robot: Robot?,
    viewModel: DoctorsViewModel,
    // ... other parameters
) {
    // ... existing code ...
    
    // Add voice input handler
    LaunchedEffect(Unit) {
        robot?.addOnSttResultListener { spokenText ->
            Log.d("DoctorsScreen", "Voice input: $spokenText")
            // ✅ Handles both English and Hindi automatically
            viewModel.handleVoiceInput(spokenText)
        }
    }
    
    // ... rest of composable ...
}
```

### Example 2: Add Voice Navigation to NavigationScreen

```kotlin
@Composable
fun NavigationScreen(
    // ... existing parameters ...
) {
    // ... existing code ...
    
    // Add voice input handler
    LaunchedEffect(Unit) {
        robot?.addOnSttResultListener { spokenText ->
            Log.d("NavigationScreen", "Voice input: $spokenText")
            // ✅ Recognizes locations AND departments in both languages
            viewModel.handleBilingualVoiceInput(spokenText)
        }
    }
    
    // ... rest of composable ...
}
```

### Example 3: Add Department Filter Button with Bilingual Support

```kotlin
@Composable
fun DepartmentFilterButton(
    department: String,  // Can be English or Hindi
    viewModel: DoctorsViewModel,
    isSelected: Boolean
) {
    Button(
        onClick = {
            // ✅ Works with both English and Hindi department names
            viewModel.filterByDepartment(department)
        },
        modifier = Modifier
            .background(
                color = if (isSelected) HospitalColors.SkyBlue else HospitalColors.PureWhite
            )
    ) {
        Text(text = department)
    }
}
```

---

## Testing Checklist

### Navigation Tests
- [ ] Speak "Pharmacy" → Shows Pharmacy location
- [ ] Speak "फार्मेसी" → Shows Pharmacy location
- [ ] Speak "Cardiology" → Shows Cardiology Department location
- [ ] Speak "कार्डियोलॉजी" → Shows Cardiology Department location
- [ ] Speak "Emergency" → Shows Emergency Room location
- [ ] Speak "आपातकालीन कक्ष" → Shows Emergency Room location

### Doctor Filtering Tests
- [ ] Click "Cardiology" button → Shows Cardiology doctors
- [ ] Speak "Cardiology" → Shows Cardiology doctors with no extra search
- [ ] Speak "कार्डियोलॉजी" → Shows Cardiology doctors
- [ ] Click "Neurology" button → Shows Neurology doctors
- [ ] Speak "न्यूरोलॉजी" → Shows Neurology doctors
- [ ] Speak "Dr. Sharma" → Shows doctors with "Sharma" in name (not department filter)

### Edge Cases
- [ ] Speak partial Hindi word "कार्डियो" → Should match "कार्डियोलॉजी" (Cardiology)
- [ ] Speak mixed case "CARDIOLOGY" → Should work (case-insensitive)
- [ ] Speak with extra spaces " Pharmacy " → Should work (trimmed)
- [ ] Empty search → Should show all doctors/locations

---

## Debug Commands

### Check if Department is Recognized
```kotlin
val query = "कार्डियोलॉजी"
val result = DoctorData.findDepartmentByName(query)
Log.d("Debug", "Query: $query → Result: $result")  // Should print "Cardiology"
```

### Check if Location is Recognized
```kotlin
val query = "फार्मेसी"
val location = LocationData.findByName(query)
Log.d("Debug", "Query: $query → Location: ${location?.name}")  // Should print "Pharmacy"
```

### Check Department Aliases
```kotlin
val aliases = DoctorData.getDepartmentAliases("Cardiology")
Log.d("Debug", "Aliases: $aliases")  // Should print [Cardiology, कार्डियोलॉजी]
```

---

## Migration Notes

### For Existing Screens (If Any)

If you have existing doctor/department filtering logic:

**Before:**
```kotlin
// Old way - only English support
viewModel.filterByDepartment(selectedDept)
```

**After:**
```kotlin
// New way - bilingual support with automatic normalization
viewModel.filterByDepartment(selectedDept)  // Still works the same!
// But now also supports Hindi input automatically
```

### No Breaking Changes
- All existing English-based filtering continues to work
- Hindi support is additive - no existing code needs to change
- Just start using new voice input methods for bilingual support

---

## Performance Considerations

### Department Lookup Performance
- `DoctorData.findDepartmentByName()` uses simple list iteration (O(n))
- For 7 departments, negligible performance impact
- If scaling to many departments (100+), consider:
  - Caching results
  - Using HashMap for O(1) lookup
  - Trie structure for fuzzy matching

### Voice Input Latency
```kotlin
val startTime = System.currentTimeMillis()
val deptMatch = DoctorData.findDepartmentByName(input)
val duration = System.currentTimeMillis() - startTime
// Typical duration: <5ms for local matching
```

---

## Future Enhancements

### 1. Fuzzy Matching
```kotlin
// Future: "कार्डियो" should match "कार्डियोलॉजी" (partial Hindi)
// Status: ✅ Already implemented with .contains()
```

### 2. Spoken Language Detection
```kotlin
// Future: Auto-detect if voice input is English or Hindi
// Then use appropriate translation
```

### 3. Contextual Disambiguation
```kotlin
// Future: If on Doctors screen and user says "Cardiology"
// → Filter doctors by department
// But if on Navigation screen and user says "Cardiology"
// → Navigate to Cardiology Department location
// Status: ✅ Already handled by different viewModels
```

### 4. Regional Language Support
```kotlin
// Future: Add Marathi, Bengali, Tamil, Telugu, Kannada (Indian regions)
// Just add more DepartmentPair objects to DEPARTMENT_TRANSLATIONS
```

---

## Support & Troubleshooting

### "Department input not recognized"
- **Check:** Is the Hindi spelling correct? Use the reference table above
- **Check:** Is the voice recognition accurate? Test with different pronunciation
- **Solution:** Add more partial matching or fuzzy matching

### "Voice input shows generic search instead of filtering"
- **Check:** Is input being passed to correct handler?
  - Use `viewModel.handleVoiceInput()` for Doctors screen
  - Use `viewModel.handleBilingualVoiceInput()` for Navigation screen
- **Check:** Is the department name in DEPARTMENT_TRANSLATIONS?

### "Hindi department names not showing in UI"
- **This is expected** - UI still displays English names for consistency
- **Hindi is used for:** Voice recognition, filtering, internal matching
- **To display Hindi:** Modify UI components to call `doctor.getDepartmentInLanguage("hi")`

---

## Questions?

Refer to the main documentation:
- `BILINGUAL_DEPARTMENT_RECOGNITION.md` - Full technical specification
- `AGENTS.md` - Architecture and patterns
- Code comments in:
  - `DoctorModel.kt` - Department data structure
  - `DoctorsViewModel.kt` - Doctor filtering logic
  - `NavigationViewModel.kt` - Location and department recognition

---

**Last Updated:** May 6, 2026
**Status:** ✅ Ready for Integration

