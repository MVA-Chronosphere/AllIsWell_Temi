# Testing Guide - Critical Intent Detection & RAG Cache Fix

## Test Setup

### Prerequisites
- Android device or emulator with Ollama running on `http://10.1.90.159:11434/`
- Doctors loaded in the system
- Internet connectivity

---

## Test Cases

### ✅ Test 1: Greeting Query (GENERAL Intent)

**Test Input:** `"how can you help me"`

**Expected Behavior:**
```
Intent: GENERAL
KB Injected: NO
Doctors Loaded: NO
Response Time: <100ms (no Ollama call)
Response: "I can help you with doctor information, hospital services, and guidance."
```

**Verification:**
```bash
adb logcat | grep "DEBUG_"
# Expected output:
# DEBUG_QUERY: how can you help me
# DEBUG_INTENT: GENERAL
# DEBUG_KB: 0
# DEBUG_DOCTORS: 0
```

**Pass Criteria:**
- ✅ Immediate response (no spinning wheel)
- ✅ Time < 100ms
- ✅ Correct greeting response
- ✅ No Ollama log entries

---

### ✅ Test 2: Doctor Query (DOCTOR Intent)

**Test Input:** `"cardiology doctor"`

**Expected Behavior:**
```
Intent: DOCTOR
KB Injected: YES (0-2 items)
Doctors Loaded: YES (filtered for cardiology)
Response Time: 1-2s (Ollama call)
Response: "Dr. [Cardiologist Name], Cardiology, Cabin [X]"
```

**Verification:**
```bash
adb logcat | grep "DEBUG_"
# Expected output:
# DEBUG_QUERY: cardiology doctor
# DEBUG_INTENT: DOCTOR
# DEBUG_KB: 1 or 2
# DEBUG_DOCTORS: 1-5 (cardiology doctors)
```

**Pass Criteria:**
- ✅ Ollama called (response time 1-2s)
- ✅ KB has 1-2 items
- ✅ Doctors list has cardiology doctors only
- ✅ Response includes doctor names + cabin

---

### ✅ Test 3: Health Query (HEALTH Intent)

**Test Input:** `"fever"`

**Expected Behavior:**
```
Intent: HEALTH
KB Injected: YES (0-2 items)
Doctors Loaded: YES (general practitioners only)
Response Time: 1-2s (Ollama call)
Response: Medical advice + general doctor recommendation
```

**Verification:**
```bash
adb logcat | grep "DEBUG_"
# Expected output:
# DEBUG_QUERY: fever
# DEBUG_INTENT: HEALTH
# DEBUG_KB: 0-2
# DEBUG_DOCTORS: 1-2 (general practitioners)
```

**Pass Criteria:**
- ✅ Ollama called
- ✅ KB has relevant health info
- ✅ Only general doctors (not specialists)
- ✅ Response includes medical guidance

---

### ✅ Test 4: Specific Doctor Query (DOCTOR Intent)

**Test Input:** `"Dr. Sharma"`

**Expected Behavior:**
```
Intent: DOCTOR
KB Injected: YES
Doctors Loaded: YES (matched by name)
Response: "Dr. Sharma, [Specialty], Cabin [X]"
```

**Pass Criteria:**
- ✅ Matches doctor by name
- ✅ Returns correct doctor details
- ✅ Cabinet number shown

---

### ✅ Test 5: Hindi Greeting (GENERAL Intent, Hindi)

**Test Input:** `"नमस्ते, आप कैसे हैं"`

**Expected Behavior:**
```
Intent: GENERAL
Language: HINDI
Response: "मैं आपकी अस्पताल से संबंधित जानकारी..."
Time: <100ms
```

**Pass Criteria:**
- ✅ Language detected as Hindi
- ✅ Hindi response returned
- ✅ No Ollama call
- ✅ <100ms response time

---

### ✅ Test 6: Cache Test (Same Query Twice)

**Test Input (First):** `"cardiology doctor"`
**Test Input (Second):** `"cardiology doctor"`

**Expected Behavior:**
```
First Call:
- Time: 1-2s (Ollama call)
- Response: "[doctor list]"
- Log: "Cache MISS"

Second Call (within 1 hour):
- Time: <10ms (cached response)
- Response: "[same doctor list]"
- Log: "Cache HIT"
```

**Verification:**
```bash
adb logcat | grep "ResponseCache"
# Expected output:
# Cache MISS, stored new entry for query
# Cache HIT for query
```

**Pass Criteria:**
- ✅ First call takes 1-2s
- ✅ Second call takes <10ms
- ✅ Responses are identical
- ✅ Cache logs show HIT

---

### ❌ Test 7: No Hallucination (Negative Test)

**Test Input:** `"unknown doctor xyz"`

**Expected Behavior:**
```
Intent: DOCTOR
Doctors Loaded: EMPTY (no match found)
Doctor Context: "" (empty)
Response: "Requested doctor not found. Please check the name."
```

**Pass Criteria:**
- ✅ No hallucinated doctor introduced
- ✅ Empty doctor context in prompt
- ✅ Honest "not found" response
- ❌ Should NOT return random doctors

---

## Log Monitoring

### Important Logs to Watch:

```bash
# Intent detection
adb logcat | grep "DEBUG_INTENT"

# Query sanitization
adb logcat | grep "DEBUG_QUERY"

# KB search results
adb logcat | grep "DEBUG_KB"

# Doctor context loading
adb logcat | grep "DEBUG_DOCTORS"

# Cache operations
adb logcat | grep "ResponseCache"

# Ollama calls
adb logcat | grep "Ollama"
```

---

## Manual Testing Checklist

- [ ] Test 1: Greeting (GENERAL)
  - [ ] Response immediate (<100ms)
  - [ ] Correct greeting text
  - [ ] No Ollama call

- [ ] Test 2: Doctor Query (DOCTOR)
  - [ ] Response in 1-2s
  - [ ] KB injected
  - [ ] Relevant doctors loaded

- [ ] Test 3: Health Query (HEALTH)
  - [ ] Response in 1-2s
  - [ ] General doctors only
  - [ ] Medical context included

- [ ] Test 4: Specific Doctor (DOCTOR)
  - [ ] Correct doctor matched
  - [ ] Cabin shown
  - [ ] Specialty shown

- [ ] Test 5: Hindi Greeting (GENERAL + HINDI)
  - [ ] Language detected
  - [ ] Hindi response
  - [ ] Immediate response

- [ ] Test 6: Cache (Same Query)
  - [ ] First: 1-2s
  - [ ] Second: <10ms
  - [ ] Responses identical

- [ ] Test 7: Unknown Doctor (Negative)
  - [ ] No hallucination
  - [ ] Empty doctor context
  - [ ] "Not found" response

---

## Performance Benchmarks

| Query Type | Expected Time | Accept Range |
|-----------|---------------|--------------|
| General/Greeting | <100ms | <200ms |
| Doctor (with cache) | <10ms | <50ms |
| Doctor (first call) | 1-2s | 1-3s |
| Health | 1-2s | 1-3s |
| Cache Hit | <10ms | <50ms |

---

## Debugging Tips

### If Response is Slow:
```bash
adb logcat | grep "Ollama\|ResponseCache"
# Check if Ollama server is responsive
# Verify network connectivity to 10.1.90.159:11434
```

### If Intent is Wrong:
```bash
adb logcat | grep "DEBUG_INTENT"
# Check if keywords in detectIntent() need updating
# Verify sanitization doesn't remove important words
```

### If KB Isn't Injected:
```bash
adb logcat | grep "DEBUG_KB"
# Check HospitalKnowledgeBase.search() results
# Verify KB data is loaded
```

### If Doctors Not Loaded:
```bash
adb logcat | grep "DEBUG_DOCTORS"
# Check if doctors list is empty
# Verify Strapi doctor data is fetched
```

---

## Success Criteria Summary

✅ **All Tests Pass If:**
1. Greeting queries respond in <100ms
2. Doctor queries return only relevant doctors
3. Health queries show general practitioners
4. No hallucinations (empty context = no response)
5. Cache prevents duplicate Ollama calls
6. Hindi/English responses are correct
7. Intent detection is accurate

✅ **System is READY for Production when:**
- All 7 tests pass
- Performance benchmarks met
- No hallucinations observed
- Cache working correctly
- Bilingual support verified

---

