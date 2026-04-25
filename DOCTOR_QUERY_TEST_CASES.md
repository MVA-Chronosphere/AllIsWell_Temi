# Doctor Details Recognition - Test Cases

## Quick Test Checklist

### ✅ Test 1: Direct Doctor Name Query
**Query:** "Who is Dr. Apurva Yadav?"

**Expected Response Should Include:**
- ✅ Doctor name: "Dr. Apurva Yadav"
- ✅ Department: "Ophthalmology"
- ✅ Specialization: "Cataract Surgery" or similar
- ✅ Experience: "9 years"
- ✅ Cabin location: "Cabin 3C" or cabin number
- ✅ Bio details: "Consultant Ophthalmologist & Cataract Surgeon"

---

### ✅ Test 2: Department Query (Exact Match)
**Query:** "Is there an Ophthalmology specialist?"

**Expected Response Should Include:**
- ✅ Doctor name: "Dr. Apurva Yadav"
- ✅ Department confirmation
- ✅ Years of experience
- ✅ Cabin location

---

### ✅ Test 3: Department Query (Common Name)
**Query:** "Is there an eye doctor?" OR "Who is the eye specialist?"

**Expected Response Should Include:**
- ✅ Matches "Ophthalmology" (not exact keyword)
- ✅ Doctor name
- ✅ Department
- ✅ Location/cabin

---

### ✅ Test 4: Partial Name Query
**Query:** "Tell me about Dr. Yadav" OR "Who is Yadav?"

**Expected Response Should Include:**
- ✅ Full name: "Dr. Apurva Yadav"
- ✅ Department
- ✅ Specialization
- ✅ Cabin

---

### ✅ Test 5: Cabin Location Query
**Query:** "Which doctor is in cabin 3C?" OR "Doctor cabin 3C"

**Expected Response Should Include:**
- ✅ Doctor name
- ✅ Cabin confirmation: "Cabin 3C"
- ✅ Department

---

### ✅ Test 6: Specialization Query (Bio Keyword)
**Query:** "Who does cataract surgery?" OR "Cataract specialist"

**Expected Response Should Include:**
- ✅ Dr. Apurva Yadav (from bio keywords)
- ✅ Specialization mention
- ✅ Department: Ophthalmology

---

### ✅ Test 7: Experience Query
**Query:** "How many years experience does Dr. Yadav have?"

**Expected Response Should Include:**
- ✅ Experience: "9 years"
- ✅ Doctor name
- ✅ Department

---

### ✅ Test 8: Multiple Doctors in Same Department
**Query:** "List all cardiology doctors" OR "Cardiology specialists"

**Expected Response Should Include:**
- ✅ All cardiologists (if multiple)
- ✅ Each with name, experience, cabin
- ✅ Distinguishing details

---

### ✅ Test 9: General Doctor List
**Query:** "Tell me about the doctors" OR "Show me all doctors"

**Expected Response Should Include:**
- ✅ Sample of doctors (5-10)
- ✅ Each with department
- ✅ Brief details for each

---

### ✅ Test 10: Department Inquiry
**Query:** "What departments do you have?" OR "Types of specialists"

**Expected Response Should Include:**
- ✅ List of departments
- ✅ Sample doctor names
- ✅ Invitation to ask more

---

## Debugging Commands

### Check Knowledge Base Injection
Look for logs:
```
HospitalKnowledgeBase: Starting injection of X doctors
HospitalKnowledgeBase: Injecting doctor: Dr. [Name] ([Department])
HospitalKnowledgeBase: Successfully injected XX dynamic doctor Q&As from X doctors
```

### Check Search Matching
Look for logs:
```
RagContextBuilder: KB search for '[query]' returned X results (limit=Y)
RagContextBuilder:   - KB Match: [Question] (category=[cat], id=[id])
```

### Check Doctor Context Building
Look for logs:
```
RagContextBuilder: Building context for query: '[query]' with X total doctors
RagContextBuilder: Found X relevant doctors for query (general=[bool], followUp=[bool])
RagContextBuilder:   - Dr. [Name] ([Department])
```

### Check Full Prompt
Look for logs containing:
```
Doctors: Dr. [Name]: Department=[Dept], Specialization=[Spec], Experience=[Y]y, Cabin=[X], Bio: [text]
```

---

## Expected Logs for Successful Operation

### On App Start (Doctor Injection):
```
DoctorsViewModel: ⚡ Loaded 30 doctors from cache (instant)
HospitalKnowledgeBase: Starting injection of 30 doctors
HospitalKnowledgeBase: Injecting doctor: Dr. Apurva Yadav (Ophthalmology)
HospitalKnowledgeBase: Injecting doctor: Dr. Rajesh Sharma (Cardiology)
...
HospitalKnowledgeBase: Successfully injected 90 dynamic doctor Q&As from 30 doctors
```

### On Voice Query (Doctor Question):
```
VoiceInteraction: Speech recognized: 'who is the eye doctor'
RagContextBuilder: ========== LANGUAGE DETECTION ==========
RagContextBuilder: Input text: 'who is the eye doctor'
RagContextBuilder: Detected language: en
RagContextBuilder: KB search for 'who is the eye doctor' returned 2 results (limit=5)
RagContextBuilder:   - KB Match: Who is Dr. Apurva Yadav? (category=departments, id=dynamic_doc_007_name)
RagContextBuilder:   - KB Match: Who specializes in Ophthalmology? (category=departments, id=dynamic_doc_007_spec)
RagContextBuilder: Found 1 relevant doctors for query (general=false, followUp=false)
RagContextBuilder:   - Dr. Apurva Yadav (Ophthalmology)
```

### Prompt Content Check:
```
Doctors: Dr. Apurva Yadav: Department=Ophthalmology, Specialization=Cataract Surgery, Experience=9y, Cabin=3C, Bio: Consultant Ophthalmologist & Cataract Surgeon with expertise in...
```

---

## Common Issues & Solutions

### ❌ Issue: "Doctor not found" response
**Check:**
1. Is doctor data loaded? (Check `DoctorsViewModel` logs)
2. Is knowledge base injection running? (Check `HospitalKnowledgeBase` logs)
3. Are keywords matching? (Check `RagContextBuilder: KB search` logs)

**Solution:**
- Verify `injectDoctorQAs()` is called after doctors are fetched
- Check if doctors list is empty
- Verify Strapi API is returning doctor data

---

### ❌ Issue: Response lacks doctor details (experience, cabin, bio)
**Check:**
1. Prompt building logs - does doctor context include all fields?
2. Search for: `Department=`, `Specialization=`, `Experience=`, `Cabin=`, `Bio:`

**Solution:**
- Should be fixed by this update
- If not, check `RagContextBuilder.buildOllamaPrompt()` is using new format

---

### ❌ Issue: "Eye doctor" doesn't find "Ophthalmology"
**Check:**
1. Search algorithm logs - are partial matches working?
2. Look for: "Partial word matches" in scoring

**Solution:**
- Should be fixed by enhanced search algorithm
- Check bio keywords include "eye" for ophthalmologists

---

### ❌ Issue: Partial name doesn't work ("Yadav" doesn't find "Dr. Apurva Yadav")
**Check:**
1. Keyword extraction - are name parts being split correctly?
2. Look for: `nameKeywords` in injection logs

**Solution:**
- Should be fixed by name keyword extraction
- Verify `nameParts` split is working

---

## Performance Benchmarks

### Expected Metrics:

**Doctor Injection Time:**
- 30 doctors → 90 Q&As: < 100ms

**Knowledge Base Search:**
- Single query: < 10ms
- With 30 doctors (90 Q&As) + 294 static Q&As: < 20ms

**Total Voice Pipeline:**
- Speech → Text → LLM → TTS: 2-5 seconds

**Context Building:**
- 5 doctors with full details: < 5ms

---

## Manual Testing Script

```bash
# Test 1: Direct Name
Voice: "Who is Dr. Apurva Yadav?"
Expected: Full details with all 6 fields

# Test 2: Common Name
Voice: "Eye doctor"
Expected: Ophthalmology specialist

# Test 3: Partial Name
Voice: "Tell me about Yadav"
Expected: Dr. Apurva Yadav details

# Test 4: Cabin
Voice: "Doctor in cabin 3C"
Expected: Doctor with cabin 3C

# Test 5: Specialization
Voice: "Cataract surgery specialist"
Expected: Ophthalmology doctor

# Test 6: Experience
Voice: "How experienced is Dr. Yadav"
Expected: "9 years experience"

# Test 7: Department
Voice: "Ophthalmology specialist"
Expected: Dr. Apurva Yadav

# Test 8: General List
Voice: "Tell me about doctors"
Expected: List of 5+ doctors with details

# Test 9: Bio Keyword
Voice: "Consultant ophthalmologist"
Expected: Dr. Apurva Yadav (from bio)

# Test 10: Partial Department
Voice: "Ophthal doctor"
Expected: Ophthalmology specialist
```

---

## Testing Status Template

```
## Doctor Details Recognition Test Results

Date: [DATE]
Tester: [NAME]
Build: [VERSION]
Environment: [DEV/STAGING/PROD]

### Test Results:
- [ ] Test 1: Direct Name Query - PASS/FAIL
- [ ] Test 2: Department Query (Exact) - PASS/FAIL
- [ ] Test 3: Department Query (Common) - PASS/FAIL
- [ ] Test 4: Partial Name Query - PASS/FAIL
- [ ] Test 5: Cabin Location Query - PASS/FAIL
- [ ] Test 6: Specialization Query - PASS/FAIL
- [ ] Test 7: Experience Query - PASS/FAIL
- [ ] Test 8: Multiple Doctors - PASS/FAIL
- [ ] Test 9: General Doctor List - PASS/FAIL
- [ ] Test 10: Department Inquiry - PASS/FAIL

### Issues Found:
[LIST ANY ISSUES]

### Notes:
[ADDITIONAL OBSERVATIONS]
```

---

## Log Filters for Debugging

### Filter by Component:
```bash
# Knowledge Base Injection
adb logcat | grep "HospitalKnowledgeBase"

# RAG Context Building
adb logcat | grep "RagContextBuilder"

# Doctor Loading
adb logcat | grep "DoctorsViewModel"

# Voice Pipeline
adb logcat | grep "VoiceInteraction"
```

### Filter by Issue:
```bash
# Doctor not found
adb logcat | grep -E "(not found|no match|empty)"

# Incomplete details
adb logcat | grep -E "(Department=|Specialization=|Experience=)"

# Search failures
adb logcat | grep -E "(KB search|returned 0)"
```

---

**Test Plan Status:** READY
**Last Updated:** April 25, 2026
**Next Review:** After deployment

