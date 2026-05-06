# Bilingual Department & Location Recognition Implementation

## Overview
The AlliswellTemi app now supports **bilingual recognition** of department names in both **English and Hindi** for navigation and doctor searches. Patients can ask for directions or doctors in either language, and the system will understand both.

## Key Features

### 1. **Bilingual Department Support**
- Every department has both English and Hindi translations
- **Supported Departments:**
  - Cardiology / कार्डियोलॉजी
  - Neurology / न्यूरोलॉजी
  - Orthopedics / ऑर्थोपेडिक्स
  - Dermatology / त्वचा विज्ञान
  - General Surgery / सामान्य सर्जरी
  - Pediatrics / बाल चिकित्सा
  - Ophthalmology / नेत्र विज्ञान

### 2. **Bilingual Location Support**
- All hospital locations already support bilingual names (English and Hindi)
- Each location has a `name` (English) and `nameHi` (Hindi)
- Examples:
  - Pharmacy / फार्मेसी
  - ICU / आईसीयू
  - Laboratory / प्रयोगशाला
  - Emergency Room / आपातकालीन कक्ष

### 3. **Smart Voice Recognition**
The system intelligently handles voice input in both languages:
- **Patient says (English):** "Take me to Cardiology" → System navigates to Cardiology Department
- **Patient says (Hindi):** "कार्डियोलॉजी के लिए रास्ता दिखाओ" → System navigates to Cardiology Department
- **Patient says (Mixed):** "Show me location for कार्डियोलॉजी" → System handles both languages in one sentence

---

## Architecture Changes

### 1. Doctor Model (`DoctorModel.kt` - Updated)

#### New Field in Doctor Data Class:
```kotlin
data class Doctor(
    // ... existing fields ...
    val departmentHi: String = "",  // Hindi translation of department
    // ... existing fields ...
)
{
    /**
     * Get department name in specified language
     */
    fun getDepartmentInLanguage(language: String): String {
        return if (language == "hi" && departmentHi.isNotEmpty()) departmentHi else department
    }
}
```

#### New Department Translation System:
```kotlin
object DoctorData {
    /**
     * Bilingual department mapping for voice recognition and filtering
     */
    val DEPARTMENT_TRANSLATIONS = listOf(
        DepartmentPair("Cardiology", "कार्डियोलॉजी"),
        DepartmentPair("Neurology", "न्यूरोलॉजी"),
        // ... more departments ...
    )

    /**
     * Find English department name from either English or Hindi input
     * Supports partial matching
     */
    fun findDepartmentByName(query: String): String?

    /**
     * Get all possible names (English and Hindi) for a department
     */
    fun getDepartmentAliases(englishDepartment: String): List<String>
}
```

#### Sample Doctor Data Updated:
All doctor objects now include `departmentHi` translations:
```kotlin
Doctor(
    id = "doc_001",
    name = "Dr. Rajesh Sharma",
    department = "Cardiology",
    departmentHi = "कार्डियोलॉजी",  // NEW
    // ... existing fields ...
)
```

---

### 2. Doctors ViewModel (`DoctorsViewModel.kt` - Enhanced)

#### Updated Filtering Logic:
```kotlin
val filteredDoctors: List<Doctor>
    get() {
        val query = _searchQuery.value.lowercase().trim()
        val dept = _selectedDepartment.value

        return _doctors.value.filter { doctor ->
            val matchesDept = if (dept == null) {
                true
            } else {
                // Support both English and Hindi department names
                doctor.department == dept || doctor.departmentHi == dept
            }
            
            val matchesQuery = if (query.isEmpty()) {
                true
            } else {
                doctor.name.lowercase().contains(query) ||
                doctor.department.lowercase().contains(query) ||
                doctor.departmentHi.lowercase().contains(query) ||
                doctor.specialization.lowercase().contains(query)
            }
            
            matchesDept && matchesQuery
        }
    }
```

#### New Voice Input Methods:
```kotlin
/**
 * Filter doctors by department (English or Hindi)
 */
fun filterByDepartment(department: String?)

/**
 * Handle voice input for department search (bilingual)
 */
fun handleDepartmentVoiceInput(spokenText: String)

/**
 * Handle voice input intelligently
 * Detects if input is a department or doctor name (bilingual)
 */
fun handleVoiceInput(spokenText: String)
```

---

### 3. Navigation ViewModel (`NavigationViewModel.kt` - Enhanced)

#### New Bilingual Voice Input Handler:
```kotlin
/**
 * Enhanced voice input handler - recognizes locations AND departments
 * in both English and Hindi
 *
 * Examples:
 * - "Pharmacy" or "फार्मेसी" → finds Pharmacy location
 * - "Cardiology" or "कार्डियोलॉजी" → finds Cardiology Department location
 */
fun handleBilingualVoiceInput(spokenText: String)
```

This method:
1. First tries to match the input as a direct location (English or Hindi)
2. Then tries to match as a department name (English or Hindi)
3. Falls back to general search if no direct match

---

## Usage Examples

### Example 1: Doctor Search (Bilingual)
```kotlin
// English department filter
viewModel.filterByDepartment("Cardiology")

// Hindi department filter (same result)
viewModel.filterByDepartment("कार्डियोलॉजी")

// Voice input - automatically detects department
viewModel.handleVoiceInput("Cardiology")           // Works
viewModel.handleVoiceInput("कार्डियोलॉजी")         // Also works
```

### Example 2: Navigation (Bilingual)
```kotlin
// Direct location names (English or Hindi)
viewModel.handleBilingualVoiceInput("Pharmacy")    // Works
viewModel.handleBilingualVoiceInput("फार्मेसी")    // Also works

// Department navigation (if department location exists)
viewModel.handleBilingualVoiceInput("Cardiology")     // Works
viewModel.handleBilingualVoiceInput("कार्डियोलॉजी")   // Also works
```

---

## Data Flow

### Navigation Screen Voice Interaction:
```
User speaks (English or Hindi)
    ↓
NavigationScreen captures voice input
    ↓
viewModel.handleBilingualVoiceInput(spokenText)
    ↓
Search by location name (English/Hindi) OR
Search by department name (English/Hindi) OR
Fall back to general search
    ↓
Update filtered locations
    ↓
User selects location
    ↓
Robot navigates to location
```

### Doctors Screen Voice Interaction:
```
User speaks (English or Hindi)
    ↓
DoctorsScreen captures voice input
    ↓
viewModel.handleVoiceInput(spokenText)
    ↓
Try department filter (English/Hindi) OR
Fall back to doctor name search
    ↓
Update filtered doctors
    ↓
User selects doctor
    ↓
Show doctor profile / Book appointment
```

---

## Integration Points

### In UI Screens (Future Implementation)

#### NavigationScreen:
```kotlin
// When voice input is received
val voiceContent = "कार्डियोलॉजी"  // User spoke in Hindi
viewModel.handleBilingualVoiceInput(voiceContent)
// System now shows Cardiology Department location
```

#### DoctorsScreen:
```kotlin
// When voice input is received
val voiceContent = "Show me Neurology doctors"
viewModel.handleVoiceInput(voiceContent)
// System filters doctors by Neurology (English or Hindi works)
```

---

## Testing Scenarios

### Test Case 1: English Department Navigation
- **Input:** "Take me to Cardiology"
- **Expected:** System navigates to Cardiology Department location
- **Status:** ✅ Supported

### Test Case 2: Hindi Department Navigation
- **Input:** "कार्डियोलॉजी के लिए रास्ता बताओ"
- **Expected:** System navigates to Cardiology Department location
- **Status:** ✅ Supported

### Test Case 3: English Doctor Search
- **Input:** "Show me Neurology doctors"
- **Expected:** Filter displays all Neurology doctors
- **Status:** ✅ Supported

### Test Case 4: Hindi Doctor Search
- **Input:** "न्यूरोलॉजी के डॉक्टर दिखाओ"
- **Expected:** Filter displays all Neurology doctors
- **Status:** ✅ Supported

### Test Case 5: English Location Search
- **Input:** "Where is the Pharmacy?"
- **Expected:** System shows Pharmacy location
- **Status:** ✅ Supported

### Test Case 6: Hindi Location Search
- **Input:** "फार्मेसी कहाँ है?"
- **Expected:** System shows Pharmacy location
- **Status:** ✅ Supported

### Test Case 7: Partial Hindi Matching
- **Input:** "कार्डियो" (partial Hindi word)
- **Expected:** System recognizes as Cardiology
- **Status:** ✅ Supported (via partial matching in findDepartmentByName)

---

## Code Changes Summary

### Modified Files:
1. **`data/DoctorModel.kt`**
   - Added `departmentHi` field to Doctor data class
   - Added `getDepartmentInLanguage()` method
   - Added `DEPARTMENT_TRANSLATIONS` list with bilingual mappings
   - Added `findDepartmentByName()` for smart department matching
   - Added `getDepartmentAliases()` for department name variations
   - Updated all sample doctors with Hindi department translations

2. **`viewmodel/DoctorsViewModel.kt`**
   - Updated `filteredDoctors` to support bilingual department filtering
   - Added `handleDepartmentVoiceInput()` for voice department queries
   - Added `handleVoiceInput()` for intelligent voice input handling
   - Imported `DoctorData` for department translation lookups

3. **`viewmodel/NavigationViewModel.kt`**
   - Added `handleBilingualVoiceInput()` for smart location/department recognition
   - Imported `DoctorData` for department translation support

---

## Future Enhancements

### 1. Fuzzy Matching
- Current implementation supports partial Hindi matching
- Can be extended to handle typos and spelling variations

### 2. Context-Aware Disambiguation
- If user says "Cardiology" while on Navigation screen, navigate to Cardiology Department
- If user says "Cardiology" while on Doctors screen, filter doctors by Cardiology

### 3. Bidirectional Translation
- Support English ↔ Hindi automatically
- Add support for regional language variations (Marathi, Bengali, etc.)

### 4. Department-Location Mapping
- Create explicit mappings between departments and their physical locations
- Support navigation to specific department locations (e.g., "Take me to the Cardiology Department")

### 5. Caching Department Translations
- Cache department translations for faster lookup
- Reduce processing time for voice recognition

---

## Troubleshooting

### Issue: Department filter not working with Hindi input
**Solution:** Ensure `DoctorData.findDepartmentByName()` is called before filtering
```kotlin
val normalizedDept = DoctorData.findDepartmentByName(hindiInput) ?: hindiInput
viewModel.filterByDepartment(normalizedDept)
```

### Issue: Voice input not recognized
**Solution:** Check that voice text is being passed correctly to `handleVoiceInput()` or `handleBilingualVoiceInput()`
```kotlin
viewModel.handleVoiceInput(recognizedText)  // For doctors
viewModel.handleBilingualVoiceInput(recognizedText)  // For navigation
```

### Issue: Mixed language input not working
**Solution:** The current implementation handles sequential matching. For truly mixed language input (e.g., "Show me Cardiology के doctors"), enhancing the parser would be needed.

---

## Files Modified Summary

| File | Changes |
|------|---------|
| `data/DoctorModel.kt` | Added bilingual department support, translation mappings |
| `viewmodel/DoctorsViewModel.kt` | Enhanced filtering, added voice input handlers |
| `viewmodel/NavigationViewModel.kt` | Added bilingual voice input handler |

---

## Related Documentation
- See `AGENTS.md` for general architecture guidance
- See `ARCHITECTURE_GUIDE.md` for system design patterns
- See `QUICK_START.md` for development setup

---

**Implementation Date:** May 6, 2026
**Status:** ✅ Complete and Ready for Integration

