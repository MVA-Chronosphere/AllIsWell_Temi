# Doctor Integration - Strapi + Hospital Knowledge Base (Complete Summary)

**Date:** April 23, 2026  
**Status:** ✅ Documentation Complete | Integration Pattern Established  
**Audience:** Developers working on doctor-related features

---

## 📋 Executive Summary

The AlliswellTemi application uses a **dual-source architecture** for doctor information:

| Source | Purpose | Data Types |
|--------|---------|-----------|
| **Strapi CMS** | Dynamic, real-time doctor profiles | Names, specializations, cabins, experience, profile images |
| **Hospital Knowledge Base** | Rich context and credentials | 30+ doctor Q&A pairs with specialties, awards, institutional info |

**Golden Rule:** When building doctor features, **combine both sources** to provide complete, contextual, and credible information to users.

---

## 🏗️ Implementation Architecture

### Data Layer
```
Strapi API
    ↓
StrapiApiService (Retrofit interface)
    ↓
StrapiDoctorModels (JSON parsing)
    ↓
DoctorModel (Kotlin data class)
    ↓
DoctorCache (SharedPreferences)
```

### State Management Layer
```
DoctorsViewModel
    ├─ Fetches from Strapi API
    ├─ Falls back to cache
    ├─ Falls back to static data
    └─ Provides filtering & search
```

### Context & Response Layer
```
RagContextBuilder
    ├─ Takes doctor list from ViewModel
    ├─ Searches HospitalKnowledgeBase
    └─ Combines both for LLM/voice

DoctorRAGService
    ├─ Generates voice responses
    └─ Uses combined context
```

### UI Layer
```
DoctorsScreen (Compose)
    ├─ Displays from DoctorsViewModel (Strapi data)
    ├─ Shows doctor cards with images
    ├─ Filters by department
    └─ Enables search
```

---

## 📊 Data Sources at a Glance

### Strapi Doctor Object
```kotlin
data class Doctor(
    val id: String,                     // "doc_001" or from Strapi ID
    val name: String,                   // "Dr. Abhishek Sharma"
    val department: String,             // "Cosmetic Surgery"
    val yearsOfExperience: Int,        // 12
    val aboutBio: String,              // "Expert in cosmetic and..."
    val cabin: String,                 // "5B"
    val specialization: String,        // "Cosmetic & Plastic Surgery"
    val profileImageUrl: String        // "https://aiwcms.chronosphere.in/..."
)
```
**Source:** Fetched from Strapi API via RetrofitClient → Cached in DoctorCache

### Hospital Knowledge Base Q&A Example
```
qa_45: "Dr. Abhishek sharma"
Answer: "Speciality: Consultant Cosmetic & Plastic Surgeon, 
         Micro-Vascular Surgery (TMC MUMBAI)"
```
**Coverage:** 30+ doctor-specific Q&A pairs (qa_20 through qa_50+)

---

## 🔄 Integration Workflow

### Scenario 1: User Taps "Doctors" Menu
```
1. DoctorsScreen loads
   ↓
2. DoctorsViewModel.fetchDoctors() called
   - Tries Strapi API
   - Falls back to cache
   - Falls back to static DoctorData.DOCTORS
   ↓
3. UI displays list from _doctors.value
   - Shows name, department, cabin
   - Shows profile image from profileImageUrl
   - Enables filtering by department
   ↓
4. User sees complete doctor list with Strapi data
```

### Scenario 2: User Asks "Who is Dr. Sharma?"
```
1. Voice input captured: "Who is Dr. Sharma?"
   ↓
2. DoctorsViewModel.searchDoctors("Sharma")
   - Returns matching doctors from Strapi
   ↓
3. RagContextBuilder.buildContext()
   - Filters relevant Strapi doctors
   - Prepares doctor summary
   ↓
4. HospitalKnowledgeBase.search("Sharma")
   - Finds qa_45 with credentials
   - Returns: "Consultant Cosmetic & Plastic Surgeon, TMC MUMBAI"
   ↓
5. DoctorRAGService.generateDetailedResponse()
   - Combines Strapi data + KB context
   - Returns enriched response
   ↓
6. Robot speaks:
   "Found doctor: Dr. Abhishek Sharma. They are a Consultant Cosmetic & 
    Plastic Surgeon with expertise in Micro-Vascular Surgery from TMC 
    Mumbai. With 12 years of experience, you can find them at cabin 5B."
```

### Scenario 3: Filter by Department
```
1. User selects "Cardiology" department filter
   ↓
2. DoctorsViewModel.filterByDepartment("Cardiology")
   - Filters from _doctors.value (Strapi data)
   ↓
3. DoctorsScreen displays filtered list
   - All cardiologists with images and details
   ↓
4. Optional: RagContextBuilder prepares summary
   - Could reference KB doctors in that specialty
```

---

## 🗂️ Key Files & Responsibilities

| File | Type | Key Methods/Properties | Integration Role |
|------|------|------------------------|------------------|
| `StrapiApiService.kt` | Interface | `getDoctors()`, `createAppointment()` | Defines API endpoints |
| `StrapiDoctorModels.kt` | Data Classes | `DoctorDocument.toDomain()` | Maps Strapi JSON → Doctor |
| `DoctorModel.kt` | Data Class | `Doctor` | Core domain model |
| `DoctorCache.kt` | Cache Mgmt | `saveDoctors()`, `getDoctors()` | Caches Strapi data locally |
| `DoctorsViewModel.kt` | ViewModel | `fetchDoctors()`, `searchDoctors()` | State + data fetching |
| `DoctorsScreen.kt` | UI | Doctor list display | Shows Strapi doctors |
| `RagContextBuilder.kt` | Context | `buildContext()`, `buildOllamaPrompt()` | Combines Strapi + KB |
| `DoctorRAGService.kt` | Service | `generateDetailedResponse()` | Generates voice responses |
| `HospitalKnowledgeBase.kt` | Knowledge Base | 294 Q&A pairs, `search()` | Provides doctor context |
| `RetrofitClient.kt` | HTTP Client | `apiService` property | Makes API calls to Strapi |

---

## 🎯 Implementation Checklist

### When Displaying Doctors
- [ ] Use `DoctorsViewModel.doctors` as data source
- [ ] Display: `name`, `department`, `yearsOfExperience`, `cabin`, `profileImageUrl`
- [ ] Implement search using `searchDoctors(query)` with fuzzy matching
- [ ] Support filtering by `department`
- [ ] Show profile images from `profileImageUrl`
- [ ] Handle loading states (isLoading, error)

### When Speaking About Doctors
- [ ] Get doctor from Strapi via `DoctorsViewModel.getDoctorById(id)`
- [ ] Call `DoctorRAGService.generateDetailedResponse(doctor)`
- [ ] Inside that service, it calls `HospitalKnowledgeBase.search(doctor.name)`
- [ ] Result combines:
  - From Strapi: name, department, yearsOfExperience, cabin, specialization
  - From KB: credentials, awards, institutional affiliations
- [ ] Robot speaks enhanced response

### When Searching for Specific Doctor
- [ ] Use `DoctorsViewModel.searchDoctors(query)`
- [ ] Fuzzy matching handles typos (e.g., "Sharme" → finds "Sharma")
- [ ] Falls back to Knowledge Base if Strapi search fails
- [ ] Present top 1-3 matches to user

### When Building LLM Prompts
- [ ] Call `RagContextBuilder.buildContext(query, doctors)`
- [ ] It searches `HospitalKnowledgeBase` for query
- [ ] Combines relevant doctors from Strapi with KB context
- [ ] Limits to 2-3 most relevant doctors (performance)
- [ ] Sends to Ollama with full context

---

## ⚙️ Configuration & Fallbacks

### Fetch Priority
```
1. Strapi API (fresh, dynamic)
   ↓ [API fail?]
2. DoctorCache (up to 10 minutes old)
   ↓ [Cache empty/stale?]
3. Static DoctorData.DOCTORS (hardcoded)
```

### Search Priority
```
1. Strapi doctor list (primary)
2. Fuzzy matching (handles typos)
3. HospitalKnowledgeBase fallback (if Strapi empty)
```

### Context Building Priority
```
1. Relevant Strapi doctors (matched by query)
2. HospitalKnowledgeBase Q&As (up to 2 most relevant)
3. Location data (popular areas)
4. Time/date context
```

---

## 📱 User Experience Flow

### Example: Patient Looking for Cardiologist
```
Patient: "Show me cardiologists"
    ↓
App: Filters DoctorsViewModel.doctors by department="Cardiology"
    ↓
Display: 
    ┌─────────────────────────────────┐
    │ 👤 Dr. Lokendra Singh Thakur   │
    │ Consultant Interventional       │
    │ Cardiologist                    │
    │ 18 years experience            │
    │ Cabin: 2C                       │
    │ [Book Appointment] [Navigate]  │
    └─────────────────────────────────┘
    
    ┌─────────────────────────────────┐
    │ 👤 Dr. Basheeruddin Ansari     │
    │ Consultant Interventional      │
    │ Cardiologist                   │
    │ 15 years experience           │
    │ Cabin: 3A                      │
    │ [Book Appointment] [Navigate]  │
    └─────────────────────────────────┘
    
(All data from Strapi with profile images)
```

### Example: Voice Query About Doctor
```
Patient: "Tell me about Dr. Lokendra"
    ↓
Voice Processing:
  1. DoctorsViewModel.searchDoctors("Lokendra")
  2. HospitalKnowledgeBase.search("Lokendra Singh Thakur")
  3. RagContextBuilder combines both
  4. DoctorRAGService.generateDetailedResponse()
    ↓
Robot: "Found doctor: Dr. Lokendra Singh Thakur. They are a Consultant 
        Interventional Cardiologist at All Is Well Hospital with specialized 
        training in cardiac interventions. With extensive experience, they are 
        recognized for their expertise in interventional cardiology. You can 
        find them at cabin 2C. We are proud to have them on our team."
        
(Combines Strapi details: name, specialty, cabin
         + KB context: credentials, expertise, institutional pride)
```

---

## 🔍 Knowledge Base Doctor Coverage

The Hospital Knowledge Base includes:

### Doctor-Specific Q&A Pairs
- **qa_20 to qa_50+:** Individual doctor profiles with credentials
- **qa_62:** "How can I see which doctor treats my condition?" (system overview)
- **qa_63:** "Are your doctors experienced?" (institutional quality assurance)
- **qa_85:** "Are your doctors full-time staff?" (commitment/reliability)
- **qa_90:** "Doctors Count?" → "More than 30+ doctors available"

### Doctor-Relevant Q&As
- **qa_5:** Nutrition/diet (Dr. Kapil Chourasiya's domain)
- **qa_6:** Walk-in consultation policy
- **qa_61:** "Do I need a referral?" (accessibility)
- **qa_131:** "Do I need to carry previous medical reports?" (doctor consultation prep)

---

## 🚀 Best Practices

### Do ✅
- Always use Strapi as source for current doctor list
- Cache Strapi data to handle network delays
- Combine Strapi + KB for rich voice responses
- Use fuzzy matching for doctor search
- Provide fallback static data for emergencies
- Search KB when Strapi query returns empty
- Enhance UI with profile images from Strapi

### Don't ❌
- Hardcode doctor names in code
- Use only Strapi without KB context (misses credentials)
- Use only KB without Strapi (may be outdated)
- Skip caching (causes slow UI)
- Ignore fuzzy matching (limits searchability)
- Display KB responses without Strapi verification

---

## 🐛 Troubleshooting

| Issue | Cause | Solution |
|-------|-------|----------|
| Doctors not loading | Strapi API unreachable | Check network, verify cache, falls back to static data |
| Doctor images blank | URL formatting issue in StrapiDoctorModels.kt | Verify `getImageUrl()` logic, check CDN access |
| Search returns empty | Exact name matching only | Implement fuzzy matching, check Knowledge Base |
| Missing credentials in response | KB not being called | Verify RagContextBuilder calls HospitalKnowledgeBase.search() |
| Stale doctor data | Cache not refreshing | Check DoctorCache age threshold (10 minutes default) |
| Doctor appears in KB but not Strapi | Data source mismatch | Use KB as fallback, update Strapi data |

---

## 📚 Related Documentation

- **DOCTOR_INTEGRATION_GUIDE.md** - Detailed implementation guide with code examples
- **AGENTS.md** - General architecture and patterns (includes doctor integration section)
- **ARCHITECTURE_GUIDE.md** - System-wide architecture diagrams
- **HOSPITAL_KNOWLEDGE_BASE.md** - Knowledge base structure and integration
- **QUICK_START.md** - Getting started with the codebase

---

## 🔗 API Endpoints Reference

### Strapi Doctor API
```
GET /api/doctors?populate[profile_image][fields]=url,name,formats&pagination[limit]=1000&sort=name:asc
```

### Response Structure
```json
{
  "data": [
    {
      "id": 123,
      "documentId": "doc_123",
      "name": "Dr. Name",
      "specialty": "Department",
      "experience_years": 15,
      "about": "Bio...",
      "location": "Cabin 5B",
      "profile_image": {...}
    }
  ]
}
```

---

## 📈 Metrics & Monitoring

### Data Health Checks
- **Doctor Count:** Should be 30+
- **Cache Age:** Refresh if > 10 minutes old
- **Image URLs:** Should start with https://
- **Cabin Format:** Should be alphanumeric (e.g., "5B", "3A")
- **KB Doctor Coverage:** 30+ Q&A pairs for doctors

### User Interaction Points
- Doctor list views (DoctorsScreen)
- Doctor search (fuzzy matching)
- Doctor voice queries
- Appointment booking (doctor selection)
- Navigation to doctor's cabin

---

## 🎓 Learning Path

1. **Start:** Read this document (you're here!)
2. **Understand:** Review DOCTOR_INTEGRATION_GUIDE.md for detailed flows
3. **Examine:** Look at DoctorsViewModel.kt for state management
4. **Review:** Check DoctorsScreen.kt for UI implementation
5. **Study:** Explore RagContextBuilder.kt for context combining
6. **Integrate:** Use DoctorRAGService.kt for voice responses
7. **Reference:** Check HospitalKnowledgeBase.kt for doctor Q&As

---

## ✅ Implementation Status

| Component | Status | Last Updated |
|-----------|--------|--------------|
| Strapi API Integration | ✅ Complete | Apr 22, 2026 |
| Doctor Caching | ✅ Complete | Apr 22, 2026 |
| DoctorsScreen UI | ✅ Complete | Apr 22, 2026 |
| Doctor Search (Fuzzy) | ✅ Complete | Apr 22, 2026 |
| Hospital Knowledge Base | ✅ Complete | Apr 22, 2026 |
| RagContextBuilder | ✅ Complete | Apr 22, 2026 |
| DoctorRAGService | ✅ Complete | Apr 22, 2026 |
| Voice Integration | ✅ Complete | Apr 22, 2026 |
| Fallback Handling | ✅ Complete | Apr 22, 2026 |
| Documentation | ✅ Complete | Apr 23, 2026 |

---

## 🎯 Next Steps

1. **Verify Integration:** Run app, navigate to Doctors screen, verify data loads
2. **Test Search:** Search for different doctors using voice and text
3. **Check Caching:** Kill app, disable network, reopen - should still show doctors
4. **Voice Testing:** Ask questions about doctors, verify KB context is included
5. **Error Handling:** Test with Strapi offline, verify fallbacks work
6. **Performance:** Monitor doctor list load time with many doctors
7. **Localization:** Test Hindi language support for doctor names/specialties

---

**Document Version:** 1.0  
**Created:** April 23, 2026  
**Maintained by:** Development Team  
**Status:** Ready for Production  

