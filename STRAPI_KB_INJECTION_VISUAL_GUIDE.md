# Strapi → Knowledge Base Doctor Injection - Visual Guide

**Date:** April 23, 2026 | **Status:** Complete

---

## 🔄 Complete Data Flow Diagram

```
┌─────────────────────────────────────────────────────────────────┐
│                      APP INITIALIZATION                         │
└─────────────────────────────────────────────────────────────────┘
                              ↓
┌─────────────────────────────────────────────────────────────────┐
│                    MainActivity.onCreate()                      │
└─────────────────────────────────────────────────────────────────┘
                              ↓
┌─────────────────────────────────────────────────────────────────┐
│            DoctorsViewModel initialized                         │
│                   (by viewModel())                             │
└─────────────────────────────────────────────────────────────────┘
                              ↓
                         ┌────┴────┐
                         │ init {} │
                         └────┬────┘
                              ↓
              ┌───────────────┬───────────────┐
              ↓               ↓               ↓
    ┌──────────────┐  ┌──────────────┐  ┌──────────────┐
    │ Cache Valid? │  │   No Cache   │  │ Cache Stale? │
    │     No       │  └──────────────┘  │      Yes     │
    └──────────────┘         ↓           └──────────────┘
         ↓                    ↓                ↓
         └────────┬──────────┴────────┬───────┘
                  ↓
    ┌─────────────────────────────────┐
    │   fetchDoctors()               │
    │   (Launch coroutine)           │
    └─────────────────────────────────┘
                  ↓
    ┌─────────────────────────────────┐
    │  Try Strapi API                │
    │  GET /api/doctors              │
    └─────────────────────────────────┘
                  ↓
         ┌────────┴────────┐
         ↓                 ↓
    ┌─────────┐      ┌──────────┐
    │ Success │      │ Failure  │
    └────┬────┘      └────┬─────┘
         ↓                ↓
    Convert to       Try Cache
    Doctor list           ↓
         ↓           ┌────────┐
         └─┬─────────┤ found? │
           ↓         └────────┘
    ┌────────────────────────────┐
    │ Update _doctors state      │
    │ Save to cache              │
    └────────────────────────────┘
         ↓
    ┌─────────────────────────────────────┐
    │ ⭐ INJECT INTO KB ⭐               │
    │ HospitalKnowledgeBase.             │
    │ injectDoctorQAs(doctorList)        │
    └─────────────────────────────────────┘
         ↓
    ┌─────────────────────────────────────┐
    │ For each doctor:                    │
    │ - Clear dynamicDoctorQAs list       │
    │ - Create 2 Q&A pairs                │
    │ - Add to dynamicDoctorQAs           │
    └─────────────────────────────────────┘
         ↓
    ┌─────────────────────────────────────┐
    │ Log: "Injected X dynamic Q&As"      │
    └─────────────────────────────────────┘
         ↓
    ┌─────────────────────────────────────┐
    │ Extract unique departments          │
    │ Update _departments state           │
    └─────────────────────────────────────┘
         ↓
    ┌─────────────────────────────────────┐
    │ App Ready!                          │
    │ KB has: 294 static + 2N dynamic QAs │
    │ DoctorsScreen can display doctors   │
    │ Voice can find doctors              │
    └─────────────────────────────────────┘
```

---

## 📊 Knowledge Base Structure Before & After

### BEFORE (Static Only)
```
┌─────────────────────────────────────────────────┐
│       Hospital Knowledge Base                   │
├─────────────────────────────────────────────────┤
│ qaDatabase = [                                  │
│   qa_1: "What is hospital name?"               │
│   qa_2: "Do you have insurance?"               │
│   qa_3: "What health packages?"                │
│   ...                                          │
│   qa_294: "Where is visitor locker?"           │
│ ]                                              │
│                                                │
│ Total: 294 Q&A pairs                          │
│ Doctor info: ❌ NONE                          │
├─────────────────────────────────────────────────┤
│ search(query) → Returns from qaDatabase only   │
└─────────────────────────────────────────────────┘
```

### AFTER (Static + Dynamic Doctors)
```
┌─────────────────────────────────────────────────────┐
│       Hospital Knowledge Base                       │
├─────────────────────────────────────────────────────┤
│ qaDatabase = [                                      │
│   qa_1: "What is hospital name?"                   │
│   qa_2: "Do you have insurance?"                   │
│   ...                                              │
│   qa_294: "Where is visitor locker?"               │
│   (294 static Q&As)                               │
│ ]                                                  │
│                                                    │
│ dynamicDoctorQAs = [  ✅ NEW!                     │
│   dynamic_doc_doc_001_name: "Who is Dr. Sharma?"  │
│   dynamic_doc_doc_001_dept: "Cosmetic specialist?"│
│   dynamic_doc_doc_002_name: "Who is Dr. Gupta?"   │
│   dynamic_doc_doc_002_dept: "Cardiology doctor?"  │
│   ...                                              │
│   (2 Q&A pairs per doctor)                        │
│ ]                                                  │
│                                                    │
│ Total: 294 + 2N Q&A pairs                         │
│ (Example: 294 + 60 = 354 with 30 doctors)         │
│ Doctor info: ✅ CURRENT FROM STRAPI              │
├─────────────────────────────────────────────────────┤
│ search(query) → Searches qaDatabase + dynamic QAs │
│                → Returns both types                │
└─────────────────────────────────────────────────────┘
```

---

## 🎯 Doctor Q&A Generation

```
┌───────────────────────────────────┐
│  Doctor from Strapi/Cache/Static  │
├───────────────────────────────────┤
│ id: "doc_001"                     │
│ name: "Abhishek Sharma"           │
│ department: "Cosmetic Surgery"    │
│ specialization: "Cosmetic &       │
│                 Plastic Surgery"  │
│ yearsOfExperience: 12             │
│ cabin: "5B"                       │
│ aboutBio: "Consultant..."         │
│ profileImageUrl: "https://..."    │
└───────────────────────────────────┘
         ↓
         ├─→ Process doctor data
         │
         ├─ Extract fields
         ├─ Format specialization
         ├─ Build keywords list
         ├─ Create answers
         │
         ↓
    ┌─────────────────────────────────────┐
    │     Generate Q&A Pair #1           │
    ├─────────────────────────────────────┤
    │ id: "dynamic_doc_doc_001_name"     │
    │ question: "Who is Dr. Abhishek     │
    │           Sharma?"                 │
    │ answer: "Speciality: Dr. Abhishek  │
    │         Sharma is a Cosmetic &     │
    │         Plastic Surgery specialist.│
    │         Experience: 12 years.      │
    │         Location: Cabin 5B.        │
    │         Bio: Consultant..."        │
    │ keywords: [abhishek, sharma,       │
    │           cosmetic, plastic,       │
    │           surgery, doctor,         │
    │           specialist]              │
    │ category: "general"                │
    └─────────────────────────────────────┘
              ↓
    ┌─────────────────────────────────────┐
    │     Generate Q&A Pair #2           │
    ├─────────────────────────────────────┤
    │ id: "dynamic_doc_doc_001_dept"     │
    │ question: "Is there a Cosmetic     │
    │           Surgery specialist?"     │
    │ answer: "Dr. Abhishek Sharma is    │
    │         available for Cosmetic     │
    │         Surgery.                  │
    │         Specialization: Cosmetic & │
    │         Plastic Surgery.           │
    │         Experience: 12 years.      │
    │         Cabin: 5B"                 │
    │ keywords: [cosmetic, surgery,      │
    │           specialist, doctor]      │
    │ category: "departments"            │
    └─────────────────────────────────────┘
              ↓
    ┌─────────────────────────────────────┐
    │ Add both to dynamicDoctorQAs list  │
    │ (Process next doctor...)            │
    └─────────────────────────────────────┘
```

---

## 🔍 Search & Retrieve Flow

```
┌──────────────────────────────────┐
│    User asks about doctor        │
│  "Who is Dr. Sharma?"            │
└──────────────────────────────────┘
         ↓
┌──────────────────────────────────┐
│ HospitalKnowledgeBase.search()   │
│ (query = "Who is Dr. Sharma?")   │
└──────────────────────────────────┘
         ↓
┌──────────────────────────────────────────┐
│ Combine search domains:                  │
│ - qaDatabase (294 static Q&As)          │
│ - dynamicDoctorQAs (60 dynamic Q&As)    │
│ Total: 354 Q&As to search               │
└──────────────────────────────────────────┘
         ↓
┌────────────────────────────────────────────┐
│ For each Q&A pair:                        │
│ Score = count of keyword matches          │
│                                            │
│ "Who is Dr. Abhishek Sharma?" score:     │
│  ✓ "sharma" in keywords → +1              │
│  ✓ "doctor" in keywords → +1              │
│  ✓ "specialist" in keywords → +1          │
│  Score = 3 ⭐⭐⭐ (HIGH)                  │
│                                            │
│ "What is hospital name?" score:           │
│  ✗ No keyword matches                     │
│  Score = 0                                │
└────────────────────────────────────────────┘
         ↓
┌────────────────────────────────────────────┐
│ Filter:                                    │
│ Only keep matches with score > 0           │
│ (Only 30-50 Q&As typically match)         │
└────────────────────────────────────────────┘
         ↓
┌────────────────────────────────────────────┐
│ Sort by relevance:                         │
│ Highest score first                        │
│ Results: [                                 │
│   (Q&A_doctor_sharma, score=3),            │
│   (Q&A_doctor_dept, score=2),              │
│   ...                                      │
│ ]                                          │
└────────────────────────────────────────────┘
         ↓
┌────────────────────────────────────────────┐
│ Take top N results (default: 3)            │
│ Return:                                    │
│ [                                          │
│   KnowledgeBaseQA(                         │
│     question: "Who is Dr. Sharma?",        │
│     answer: "Speciality: Cosmetic & ...    │
│     "                                      │
│   )                                        │
│   ...                                      │
│ ]                                          │
└────────────────────────────────────────────┘
         ↓
┌────────────────────────────────────────────┐
│ Voice/UI uses results:                     │
│ Robot speaks: "Dr. Abhishek Sharma is a    │
│   Cosmetic & Plastic Surgery specialist    │
│   with 12 years of experience at Cabin 5B" │
└────────────────────────────────────────────┘
```

---

## 🔗 Integration Points

```
┌─────────────────────────────────────────┐
│   STRAPI DOCTORS                        │
│   (Dynamic, Real-time)                  │
└─────────────────────────────────────────┘
         ↓
┌─────────────────────────────────────────┐
│   DoctorsViewModel.fetchDoctors()       │
│   - Fetches from Strapi API             │
│   - Caches locally                      │
│   - Falls back to static data           │
└─────────────────────────────────────────┘
         ↓
    ┌────┴────┬─────────┐
    ↓         ↓         ↓
API OK    Cache     Static
         Success   Fallback
    ↓         ↓         ↓
    └────┬────┴────┬────┘
         ↓
┌──────────────────────────────────────────┐
│ HospitalKnowledgeBase.injectDoctorQAs() │
│ (INJECTION HAPPENS HERE) ⭐             │
│ - Clears previous dynamic Q&As          │
│ - Creates 2 Q&A pairs per doctor        │
│ - Adds to dynamicDoctorQAs list         │
└──────────────────────────────────────────┘
         ↓
┌──────────────────────────────────────────┐
│   HOSPITAL KNOWLEDGE BASE                │
│   294 static + 2N dynamic Q&A pairs      │
└──────────────────────────────────────────┘
         ↓
    ┌────┴────┬─────────┐
    ↓         ↓         ↓
DoctorsScreen  Voice   RagContextBuilder
(UI Display)   Input    (LLM Context)
```

---

## ⚡ Performance Timeline

```
App Start: T=0ms
  ├─ DoctorsViewModel created
  ├─ init {} block runs
  ├─ isDataFromCache() checked
  │
  ├─ Scenario A: API Available
  │  ├─ fetchDoctors() starts (async)
  │  ├─ Strapi API called: ~500ms
  │  ├─ Response received: ~1000ms
  │  ├─ Convert to Doctor: ~10ms
  │  ├─ HospitalKnowledgeBase.injectDoctorQAs(): ~50ms ⭐
  │  ├─ Log: "Injected 60 dynamic Q&As"
  │  └─ Total: ~1060ms
  │
  ├─ Scenario B: Cache Available
  │  ├─ loadFromCache() called
  │  ├─ Read from SharedPreferences: ~20ms
  │  ├─ Parse JSON: ~30ms
  │  ├─ HospitalKnowledgeBase.injectDoctorQAs(): ~50ms ⭐
  │  └─ Total: ~100ms
  │
  └─ Scenario C: Static Fallback
     ├─ loadStaticFallback() called
     ├─ Load DoctorData.DOCTORS: ~5ms
     ├─ HospitalKnowledgeBase.injectDoctorQAs(): ~50ms ⭐
     └─ Total: ~55ms

User Query: T=1500ms
  ├─ User asks "Who is Dr. Sharma?"
  ├─ HospitalKnowledgeBase.search(): ~20ms ⭐
  │  ├─ Combine: qaDatabase + dynamicDoctorQAs: ~5ms
  │  ├─ Score 354 Q&As: ~10ms
  │  ├─ Sort by score: ~3ms
  │  └─ Take top 3: ~2ms
  ├─ Build context: ~10ms
  ├─ Robot speak: ~100ms
  └─ Total end-to-end: ~130ms
```

---

## 📊 Memory Impact

```
Static Knowledge Base: ~294 Q&A pairs × 100 bytes = ~30KB

Dynamic Doctor Q&As (with 30 doctors):
  ├─ Doctor list: 30 × 200 bytes = ~6KB
  ├─ Q&A pairs: 60 × 100 bytes = ~6KB
  └─ Keywords: 60 × 50 bytes = ~3KB
  
Dynamic total: ~15KB

Total KB memory: ~30KB + ~15KB = ~45KB
(Negligible on modern devices with GB of RAM)
```

---

## ✅ Summary

The diagram shows that **doctor details are injected into the Knowledge Base automatically** at multiple points:

1. **When API succeeds** → Inject fresh Strapi doctors
2. **When API fails, cache available** → Inject cached doctors
3. **When all else fails** → Inject static fallback doctors

Result: **KB always has current doctor information** for search and voice interactions.


