# Doctor Integration - Quick Reference Card

## 🎯 TL;DR
**When working with doctors, use BOTH Strapi (dynamic data) + Knowledge Base (context/credentials)**

---

## 📦 Data Sources

### Strapi (Dynamic)
```kotlin
Doctor(
    id, name, department, yearsOfExperience, 
    aboutBio, cabin, specialization, profileImageUrl
)
```
**Usage:** UI display, search, filtering

### Knowledge Base (Context)
```kotlin
30+ Q&A pairs with doctor credentials, awards, specialties
```
**Usage:** Voice responses, LLM context, credentials display

---

## 🔄 One-Line Workflows

| Scenario | Code |
|----------|------|
| **Display doctors** | `DoctorsViewModel.doctors` → DoctorsScreen |
| **Search doctor** | `DoctorsViewModel.searchDoctors(query)` (fuzzy) |
| **Get doctor context** | `RagContextBuilder.buildContext(query, doctors)` |
| **Generate voice response** | `DoctorRAGService.generateDetailedResponse(doctor)` |
| **Find KB info** | `HospitalKnowledgeBase.search(doctorName)` |
| **Filter by dept** | `DoctorsViewModel.filterByDepartment(dept)` |

---

## 📱 Key Classes Map

```
User Input
    ↓
DoctorsViewModel ← DoctorCache ← Strapi API
    ↓
RagContextBuilder ← HospitalKnowledgeBase
    ↓
DoctorRAGService (generate responses)
    ↓
DoctorsScreen (UI) OR Robot (Voice)
```

---

## 🎤 Voice Command Examples

```
"Find Dr. Sharma"
→ Strapi search + KB lookup → "Dr. Abhishek Sharma, Consultant 
  Cosmetic & Plastic Surgeon, 12 years, Cabin 5B"

"Show cardiology doctors"
→ Filter Strapi by department → Display all cardiologists

"Tell me about doctors"
→ Combine Strapi count + KB context → "30+ expert specialists 
  across multiple departments"

"Navigate to Dr. Sharma"
→ Find cabin from Strapi → Call robot?.goTo(cabin)
```

---

## 📊 Data Priority

```
FETCH: Strapi API → Cache → Static Fallback
SEARCH: Strapi list → Fuzzy match → KB fallback
CONTEXT: Strapi data + KB enrichment
VOICE: Combine Strapi + KB for response
```

---

## ✅ Implementation Checklist

- [ ] Using `DoctorsViewModel.doctors` for list
- [ ] Displaying profile images from `profileImageUrl`
- [ ] Searching with `searchDoctors(query)` not exact matching
- [ ] Calling `HospitalKnowledgeBase.search()` for KB context
- [ ] Combining Strapi + KB in voice responses
- [ ] Caching Strapi data locally
- [ ] Handling network failures gracefully
- [ ] Supporting both English and Hindi strings

---

## 🐛 Quick Debugging

```kotlin
// Check if doctors loaded
Log.d("DoctorsVM", "Doctors: ${DoctorsViewModel.doctors.value.size}")

// Check KB search
val results = HospitalKnowledgeBase.search("sharma", limit = 2)
Log.d("KnowledgeBase", "Results: ${results.size}")

// Check context building
val context = RagContextBuilder.buildContext("sharma", doctors)
Log.d("RagContext", context)

// Check doctor found
val doctor = DoctorsViewModel.getDoctorById("doc_001")
Log.d("Doctor", "Found: ${doctor?.name}")
```

---

## 🚀 Common Implementations

### Display Doctor List
```kotlin
@Composable
fun DoctorsScreen(viewModel: DoctorsViewModel) {
    val doctors by viewModel.doctors
    
    LazyColumn {
        items(doctors) { doctor ->
            DoctorCard(
                doctor = doctor,
                onClick = { /* navigate */ }
            )
        }
    }
}
```

### Search Doctors
```kotlin
fun handleDoctorSearch(query: String) {
    val results = viewModel.searchDoctors(query)
    // Display results with fuzzy matching
}
```

### Generate Voice Response
```kotlin
fun speakAboutDoctor(doctor: Doctor) {
    val response = DoctorRAGService.generateDetailedResponse(doctor)
    robot?.speak(TtsRequest.create(response))
}
```

### Get Doctor Context
```kotlin
val context = RagContextBuilder.buildContext(query, doctors)
val kbResults = HospitalKnowledgeBase.search(query, limit = 2)
// Use both for LLM prompt
```

---

## 🔗 File References

| Feature | File |
|---------|------|
| State mgmt | DoctorsViewModel.kt |
| UI | DoctorsScreen.kt |
| Voice | DoctorRAGService.kt |
| Context | RagContextBuilder.kt |
| Knowledge | HospitalKnowledgeBase.kt |
| API | StrapiApiService.kt |
| Data | DoctorModel.kt |

---

## 📚 Full Guides

- Detailed: **DOCTOR_INTEGRATION_GUIDE.md**
- Architecture: **DOCTOR_STRAPI_KB_INTEGRATION.md**
- System: **AGENTS.md** (Doctor Integration section)
- General: **ARCHITECTURE_GUIDE.md**

---

## ❌ Don't Forget

- ❌ Don't hardcode doctor names
- ❌ Don't skip KB context for voice
- ❌ Don't ignore cache invalidation
- ❌ Don't use only one data source
- ❌ Don't forget fuzzy matching

---

**Last Updated:** April 23, 2026 | **Ready to Use:** Yes ✅

