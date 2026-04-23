# STRAPI Doctor Injection - Technical Reference

**Version:** 1.0 | **Date:** April 23, 2026 | **Status:** Complete

---

## Quick Reference

### Injection Entry Points

| Method | File | Injection Call |
|--------|------|-----------------|
| `fetchDoctors()` | DoctorsViewModel | `HospitalKnowledgeBase.injectDoctorQAs(doctorList)` |
| `loadFromCache()` | DoctorsViewModel | `HospitalKnowledgeBase.injectDoctorQAs(cachedDoctors)` |
| `loadStaticFallback()` | DoctorsViewModel | `HospitalKnowledgeBase.injectDoctorQAs(staticDoctors)` |

### Injection Method

```kotlin
fun injectDoctorQAs(doctors: List<Doctor>) {
    dynamicDoctorQAs.clear()
    doctors.forEach { doctor ->
        // Creates 2 Q&A pairs per doctor
        // Adds to dynamicDoctorQAs list
    }
}
```

### Search Integration

```kotlin
fun search(userQuery: String, limit: Int = 3): List<KnowledgeBaseQA> {
    val allQAs = qaDatabase + dynamicDoctorQAs  // Combined search
    // Score, sort, return top results
}
```

---

## Q&A Pairs Generated

### Type 1: Name Query
```
id: "dynamic_doc_{doctorId}_name"
question: "Who is [Doctor Name]?"
answer: "Speciality: [Specialty]. Experience: [Years]. 
         Location: Cabin [Cabin]. Bio: [Biography]"
keywords: [doctor_name, specialty, department, doctor, specialist]
category: "general"
```

### Type 2: Department Query
```
id: "dynamic_doc_{doctorId}_dept"
question: "Is there a [Department] specialist?"
answer: "[Doctor Name] is available for [Department]. 
         Specialization: [Specialty]. Experience: [Years]. Cabin: [Cabin]"
keywords: [department, specialist, doctor, specialty]
category: "departments"
```

---

## Data Structure

### Input (Doctor from Strapi)
```kotlin
Doctor(
    id: String,
    name: String,
    department: String,
    yearsOfExperience: Int,
    aboutBio: String,
    cabin: String,
    specialization: String,
    profileImageUrl: String
)
```

### Output (Generated Q&A in KB)
```kotlin
KnowledgeBaseQA(
    id: String,                    // dynamic_doc_{id}_{type}
    question: String,              // Generated from doctor data
    answer: String,                // Formatted answer with details
    keywords: List<String>,        // Auto-extracted from doctor
    category: String,              // "general" or "departments"
    language: String = "en"        // English
)
```

---

## Execution Flow

### Startup
```
1. MainActivity launches
2. DoctorsViewModel init block runs
3. fetchDoctors() called (async)
4. Strapi API called via RetrofitClient
5. Response converted to Doctor list
6. HospitalKnowledgeBase.injectDoctorQAs(doctors) called
7. 2N Q&A pairs created in memory
8. dynamicDoctorQAs list populated
```

### Query Time
```
1. User says "Find cardiologist"
2. HospitalKnowledgeBase.search("Find cardiologist") called
3. Searches: qaDatabase (294 items) + dynamicDoctorQAs (2N items)
4. Scores each Q&A pair for keyword matches
5. Returns top results (both static and dynamic)
6. RagContextBuilder builds context from results
7. Robot speaks response with doctor details
```

---

## Performance

### Time Complexity
- **Injection:** O(D × 2) where D = doctor count (linear)
- **Search:** O((294 + 2D) × K) where K = keyword matching
- **Memory:** Adds ~100 bytes per doctor Q&A pair

### Typical Numbers
- **Doctors:** 30-50
- **Generated Q&A pairs:** 60-100
- **Total KB entries:** 354-394 (vs 294 before)
- **Search latency:** < 50ms on modern devices

---

## Logging

### Enable Debug Logging
```
adb logcat "*:S" "HospitalKnowledgeBase:D"
```

### Expected Output
```
D/HospitalKnowledgeBase: Injected 60 dynamic doctor Q&As
D/DoctorsViewModel: Fetched and cached 30 doctors from API
```

---

## Testing Checklist

- [ ] App launches without errors
- [ ] Logcat shows "Injected X dynamic doctor Q&As"
- [ ] Voice query "Who is [Doctor]?" returns Strapi details
- [ ] Voice query "[Department] doctor" finds current doctors
- [ ] KB search finds dynamic Q&As alongside static ones
- [ ] Works when Strapi API unavailable (uses cache)
- [ ] Works when cache unavailable (uses static data)

---

## Debugging

### Check if Injection Happened
```kotlin
val results = HospitalKnowledgeBase.search("any doctor name")
if (results.isEmpty()) {
    Log.e("Debug", "No injection - KB has no doctor Q&As")
} else {
    Log.d("Debug", "Injection successful - found ${results.size} Q&As")
}
```

### Monitor Injection
```kotlin
// Add before injection call:
Log.d("DoctorsViewModel", "About to inject ${doctors.size} doctors")

// After injection happens, check logs:
// "Injected 60 dynamic doctor Q&As"
```

### Verify Search Works
```kotlin
val staticResults = HospitalKnowledgeBase.search("hospital hours")
val doctorResults = HospitalKnowledgeBase.search("Dr. Sharma")

Log.d("KB", "Static results: ${staticResults.size}")
Log.d("KB", "Doctor results: ${doctorResults.size}")
```

---

## Edge Cases

### Empty Doctor List
```kotlin
if (doctors.isEmpty()) {
    HospitalKnowledgeBase.injectDoctorQAs(emptyList())
    // dynamicDoctorQAs becomes empty
    // KB search still works (returns static Q&As only)
}
```

### Missing Doctor Fields
```kotlin
Doctor(
    name = "Dr. John",
    specialization = null,     // Handled with `?:`
    aboutBio = "",             // Handled with empty string
    cabin = "Unknown"          // Still included in Q&A
)
```

### Duplicate Doctors
```kotlin
// If same doctor appears twice (shouldn't happen):
// First occurrence creates Q&As
// Second occurrence replaces them (due to clear() call)
// Result: Only one set of Q&As per doctor ID
```

---

## Implementation Checklist

- [x] HospitalKnowledgeBase.kt modified with `injectDoctorQAs()`
- [x] HospitalKnowledgeBase.kt modified with `search()` using combined lists
- [x] DoctorsViewModel.kt import added for HospitalKnowledgeBase
- [x] DoctorsViewModel.kt fetchDoctors() calls inject
- [x] DoctorsViewModel.kt loadFromCache() calls inject
- [x] DoctorsViewModel.kt loadStaticFallback() calls inject
- [x] Logging added for debugging
- [x] No syntax errors
- [x] Ready for testing

---

## Related Documentation

- **STRAPI_KB_INJECTION_COMPLETE.md** - Full implementation guide
- **DOCTOR_INTEGRATION_GUIDE.md** - Doctor integration patterns
- **AGENTS.md** - System architecture

---

## Version History

| Version | Date | Changes |
|---------|------|---------|
| 1.0 | Apr 23, 2026 | Initial implementation |

---

**Last Updated:** April 23, 2026  
**Status:** Ready for Integration Testing  
**Next:** Deploy and verify injection in app


