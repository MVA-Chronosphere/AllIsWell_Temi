# Doctor Integration Guide - Strapi + Hospital Knowledge Base

## 📋 Overview

When the application displays doctor information or processes doctor-related queries, it should **combine data from TWO sources**:

1. **Strapi CMS** - Dynamic doctor profiles (names, specializations, cabins, experience, images)
2. **Hospital Knowledge Base** - Detailed doctor information and institutional context

---

## 🔄 Integration Flow

```
User Query About Doctors
    ↓
Detect Query Type (e.g., "Find Dr. Sharma")
    ↓
Search Strapi Doctors (via DoctorsViewModel)
    ↓
Enhance with Knowledge Base Context (via RagContextBuilder)
    ↓
Generate Response (using DoctorRAGService)
    ↓
Display to User (via DoctorsScreen or Voice)
```

---

## 📊 Data Source Mapping

### Strapi Doctor Model (Primary Source)
**File:** `StrapiDoctorModels.kt` → Maps to `DoctorModel.kt`

```kotlin
Doctor(
    id: String,                    // From Strapi documentId
    name: String,                  // e.g., "Dr. Abhishek Sharma"
    department: String,            // e.g., "Cosmetic Surgery"
    yearsOfExperience: Int,        // e.g., 12
    aboutBio: String,             // e.g., "Expert in..."
    cabin: String,                // e.g., "5B"
    specialization: String,       // e.g., "Cosmetic & Plastic Surgery"
    profileImageUrl: String       // From Strapi profile_image
)
```

**Fetching:** `DoctorsViewModel.fetchDoctors()` → Calls `RetrofitClient.apiService.getDoctors()`

### Knowledge Base QA Pairs (Secondary Context)
**File:** `HospitalKnowledgeBase.kt`

Doctor information is embedded in multiple QA pairs (qa_20 through qa_50 and beyond):

```kotlin
// Example: Dr. Abhishek Sharma
KnowledgeBaseQA(
    id = "qa_45",
    question = "Dr. Abhishek sharma",
    answer = "Speciality: Consultant Cosmetic & Plastic Surgeon, Micro-Vascular Surgery (TMC MUMBAI)",
    keywords = listOf("surgeon", "abhishek", "cosmetic", "speciality", ...),
    category = "general",
    language = "en"
)
```

**Total Doctors in KB:** 30+ doctor-specific QA pairs covering:
- Name
- Specialization
- Credentials
- Institutional affiliations
- Expertise areas

---

## 🎯 Implementation Points

### 1. **DoctorsScreen.kt** - UI Layer
When displaying doctor list, use data from:
- **Source:** `DoctorsViewModel.doctors` (from Strapi via cache/API)
- **Enhancement:** Show cabin numbers, experience years, and profile images

```kotlin
DoctorCard(
    doctor = doctor,  // From Strapi
    onClick = { 
        onSelectDoctor(doctor)
    }
)
```

### 2. **DoctorsViewModel.kt** - State Management
Manages fetching and filtering:
- **Primary:** Fetches from Strapi API
- **Fallback:** Uses cached data
- **Last Resort:** Uses static DoctorData.DOCTORS

```kotlin
fun fetchDoctors() {
    // Try API (Strapi) first
    val response = RetrofitClient.apiService.getDoctors()
    // Cache the result
    cache.saveDoctors(doctorList)
    // Update UI state
    _doctors.value = doctorList
}

fun getKnowledgeBase(): String {
    // Builds a formatted string from current doctors
    // This is used for LLM/voice responses
}
```

### 3. **RagContextBuilder.kt** - Context Building
When building prompts for Ollama or generating fallback responses:

```kotlin
fun buildContext(query: String, doctors: List<Doctor>): String {
    // Filter relevant doctors based on query
    val relevantDoctors = doctors.filter { doctor ->
        lowerQuery.contains(doctor.name.lowercase()) ||
        lowerQuery.contains(doctor.department.lowercase())
    }
    
    // Format for LLM consumption
    return """
        Doctors: ${relevantDoctors.joinToString("\n") { doctor ->
            "Dr. ${doctor.name} - ${doctor.department}, ${doctor.yearsOfExperience}y exp, Cabin ${doctor.cabin}"
        }}
    """.trimIndent()
}
```

### 4. **DoctorRAGService.kt** - Response Generation
Generates doctor-specific responses:

```kotlin
fun generateDetailedResponse(doctor: Doctor): String {
    // Uses all fields from Doctor model
    return buildString {
        append("Found doctor: ${doctor.name}. ")
        append("They are an expert in ${doctor.specialization}. ")
        if (doctor.aboutBio.isNotBlank()) {
            append("${doctor.aboutBio}. ")
        }
        append("Located at cabin ${doctor.cabin}.")
    }
}
```

### 5. **HospitalKnowledgeBase.kt** - Enhanced Context
Knowledge base provides additional context for voice responses:

```kotlin
// When HospitalKnowledgeBase.search(query, limit = 2) is called
// it searches doctor-related QAs and returns relevant context
val relevantQAs = HospitalKnowledgeBase.search("Dr. Sharma", limit = 2)
// Returns QA pairs about Dr. Sharma with credentials & expertise
```

---

## 🗣️ Voice Interaction Examples

### Example 1: "Find Doctor Sharma"
```
Query: "Find Dr. Sharma"
    ↓
DoctorsViewModel searches Strapi doctors
    ↓
RagContextBuilder.buildContext() filters Dr. Sharma
    ↓
HospitalKnowledgeBase.search() finds qa_45 about Dr. Sharma
    ↓
DoctorRAGService.generateDetailedResponse(doctor) combines:
    - Name, specialization from Strapi
    - Credentials from Knowledge Base
    - Cabin location from Strapi
    ↓
Robot speaks: "Found doctor: Dr. Abhishek Sharma. 
              They are a Consultant Cosmetic & Plastic Surgeon 
              with expertise in Micro-Vascular Surgery. 
              You can find them at cabin 5B."
```

### Example 2: "Show me cardiology doctors"
```
Query: "Show me cardiology doctors"
    ↓
DoctorsViewModel.filterByDepartment("Cardiology")
    ↓
Returns list of cardiologists from Strapi
    ↓
DoctorsScreen displays all cardiologists with images & details
    ↓
RagContextBuilder.buildContext() prepares summary
    ↓
Robot speaks doctor list with counts and highlights
```

### Example 3: "Tell me about doctors"
```
Query: "Tell me about doctors"
    ↓
RagContextBuilder.buildContext() uses all doctors from Strapi
    ↓
HospitalKnowledgeBase.search() finds general doctor info (qa_85, qa_90)
    ↓
Response combines:
    - Count from Knowledge Base ("More than 30+ doctors")
    - Specializations from Strapi list
    - General context from Hospital KB
```

---

## 📝 Knowledge Base Doctor Categories

The hospital knowledge base contains doctors organized in these QA ranges:

| QA Range | Content | Source |
|----------|---------|--------|
| qa_20-qa_50 | Individual doctor profiles (name, specialty, credentials) | Hospital temi Dataset.json |
| qa_62-qa_63 | General "doctors are experienced" statements | Institutional |
| qa_85 | Full-time staff confirmation | Institutional |
| qa_90 | Doctor count ("30+ doctors") | Institutional |
| qa_131 | "Carry previous medical reports for consultation" | Process |

---

## 🔐 Data Consistency Rules

### Rule 1: Strapi as Source of Truth
- Always use Strapi for current doctor list (it's dynamic)
- Cache prevents API failures
- Static fallback only when both API and cache unavailable

### Rule 2: Knowledge Base for Context
- Use KB for institutional facts about doctors
- Use KB to enhance voice responses with credentials
- Use KB for fuzzy matching when exact doctor name not in Strapi

### Rule 3: Combined Responses
- Display: Use Strapi data (names, images, cabins)
- Voice: Enhance with KB context (credentials, specializations)
- Search: Use both to maximize relevance

---

## 🛠️ Implementation Checklist

### When Displaying Doctor List
- [ ] Fetch from DoctorsViewModel (connects to Strapi)
- [ ] Display doctor.name, doctor.department, doctor.profileImageUrl
- [ ] Show doctor.cabin for location
- [ ] Show doctor.yearsOfExperience

### When User Searches for Doctor
- [ ] Use DoctorsViewModel.searchDoctors(query)
- [ ] Pass results to DoctorRAGService.generateDetailedResponse()
- [ ] Enhance response using HospitalKnowledgeBase.search()
- [ ] Speak combined response via robot?.speak()

### When User Asks "Tell me about [doctor]"
- [ ] Search Strapi doctors by name
- [ ] Build context using RagContextBuilder.buildContext()
- [ ] Get KB context via HospitalKnowledgeBase.search()
- [ ] Combine both sources for voice response

### When Doctor Not Found
- [ ] Check Strapi list
- [ ] Check Knowledge Base for alternative spellings
- [ ] Use DoctorRAGService.generateFallbackResponse()
- [ ] Suggest browsing full doctor list

---

## 🔍 Knowledge Base Search Logic

```kotlin
// In RagContextBuilder
fun buildOllamaPrompt(query: String, doctors: List<Doctor>): String {
    // Relevant QAs are fetched smartly
    val relevantQAs = HospitalKnowledgeBase.search(query, limit = 2)
    
    // For "Tell me about cardiology", returns:
    // - Doctor list from Strapi (filtered by department)
    // - KB context about doctors in general
    // - Specific doctor credentials from KB
    
    return buildString {
        // Include language instruction
        // Include KB context (if found)
        // Include doctor context from Strapi
        // Include user query
        // Include constraints
    }
}
```

---

## 📱 Current Implementation Status

### ✅ Already Integrated
- Strapi API fetching (via RetrofitClient)
- Doctor caching (via DoctorCache)
- Doctor display (DoctorsScreen)
- Knowledge base context (RagContextBuilder)
- Doctor search with fuzzy matching (DoctorsViewModel)

### ⚠️ To Be Enhanced
- Real-time KB search integration in voice responses
- Doctor profile images (from Strapi) in UI
- Credential display from KB in doctor cards
- Advanced doctor-doctor comparison using KB

---

## 🎓 Examples of Combined Data Usage

### Example UI Card:
```
┌─────────────────────────────────────────┐
│ 👤 Dr. Abhishek Sharma                 │
│ (Image from Strapi)                     │
├─────────────────────────────────────────┤
│ Department:  Cosmetic Surgery          │
│ Speciality:  Cosmetic & Plastic Surgery│ ← From Strapi
│ Experience:  12 years                   │
│ Cabin:       5B                         │
├─────────────────────────────────────────┤
│ Bio: Consultant... (from Strapi)       │
│ Credentials: TMC MUMBAI (from KB)      │ ← From Knowledge Base
│ Micro-Vascular Surgery Expertise (KB)  │
├─────────────────────────────────────────┤
│ [Book Appointment] [Navigate to Cabin] │
└─────────────────────────────────────────┘
```

### Example Voice Response:
```
"Found doctor: Dr. Abhishek Sharma. 
They are a highly qualified Consultant Cosmetic & Plastic Surgeon 
with expertise in Micro-Vascular Surgery from TMC Mumbai. 
With 12 years of experience, they are recognized for their 
contributions to cosmetic and plastic surgery. 
You can find them at cabin 5B. 
We are proud to have them on our team."
```

---

## 📞 Integration Points Summary

| Component | Source | Purpose |
|-----------|--------|---------|
| DoctorsScreen | Strapi | Display doctor list with images & details |
| DoctorsViewModel | Strapi + Cache | Fetch and manage doctor state |
| RagContextBuilder | Strapi + KB | Build context for LLM prompts |
| DoctorRAGService | Strapi + KB | Generate voice responses |
| HospitalKnowledgeBase | KB Database | Provide doctor context & credentials |
| TemiUtils | Robot SDK | Execute voice commands |

---

## 🚀 Future Enhancements

1. **Real-time Doctor Availability** - Add availability status from Strapi
2. **Doctor Reviews** - Link KB patient testimonials to Strapi doctors
3. **Department Statistics** - Show KB-sourced stats alongside Strapi data
4. **Multi-language Support** - Localize KB doctor info to Hindi
5. **Doctor Recommendations** - AI-powered matching using both sources

---

**Last Updated:** April 23, 2026  
**Strapi Integration:** Complete  
**Knowledge Base Integration:** Complete  
**Next:** Real-time availability + voice optimization

