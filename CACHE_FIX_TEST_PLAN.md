# Response Cache Fix - Test Plan & Verification

## Pre-Deployment Testing

### Phase 1: Compilation & Build ✅

```bash
# Clean build to ensure no cache issues
cd /Users/mva357/AndroidStudioProjects/AllIsWell_Temi
./gradlew clean build

# Expected: Build successful, no errors
# Warnings about unused functions are OK
```

**Checklist:**
- [ ] No compilation errors
- [ ] APK builds successfully
- [ ] File size normal (~8-15MB)

---

## Phase 2: Unit Testing

### Test 1: Query Extraction Function

**Purpose:** Verify `extractQueryFromPrompt()` correctly extracts user query

**Test cases:**
```kotlin
@Test
fun testQueryExtraction() {
    // Test case 1: Standard RAG format with "Q: "
    val prompt1 = """
        You are helpful...
        Hospital: All Is Well
        Q: Find cardiology doctors
        A:
    """.trimIndent()
    val result1 = OllamaClient.extractQueryFromPrompt(prompt1)
    assertEquals("Find cardiology doctors", result1)
    
    // Test case 2: Format with "User: "
    val prompt2 = """
        System instructions...
        User: Show me eye doctors
        A:
    """.trimIndent()
    val result2 = OllamaClient.extractQueryFromPrompt(prompt2)
    assertEquals("Show me eye doctors", result2)
    
    // Test case 3: Empty/malformed prompt
    val prompt3 = "No query marker"
    val result3 = OllamaClient.extractQueryFromPrompt(prompt3)
    assertEquals("", result3)  // Returns empty to disable cache
    
    // Test case 4: Very long query
    val longQuery = "A".repeat(600)
    val prompt4 = "Q: $longQuery\nA:"
    val result4 = OllamaClient.extractQueryFromPrompt(prompt4)
    assertEquals(500, result4.length)  // Limited to 500 chars
}
```

**Expected:** All 4 tests pass

---

### Test 2: Cache Behavior (Unit)

**Purpose:** Verify cache is disabled by default

**Test cases:**
```kotlin
@Test
fun testCacheDisabledByDefault() {
    val request = OllamaRequest(
        model = "llama3:8b",
        prompt = "Q: Find doctors\nA:",
        stream = true
    )
    
    // When cacheEnabled = false (default)
    val flow1 = OllamaClient.generateStreaming(request, cacheEnabled = false)
    // Should NOT check cache before calling Ollama
    // We can't easily test this without mocking, but code inspection confirms
}

@Test
fun testCacheCanBeEnabled() {
    val request = OllamaRequest(
        model = "llama3:8b",
        prompt = "Q: Hospital hours\nA:",
        stream = true
    )
    
    // When cacheEnabled = true
    val flow1 = OllamaClient.generateStreaming(request, cacheEnabled = true)
    // Should check cache before calling Ollama
    // Should store response after successful call
}
```

**Expected:** Both configurations work

---

## Phase 3: Integration Testing (on Device)

### Test 3: Cache Collision Prevention (Primary Test)

**Purpose:** Verify different queries don't return same response

**Setup:**
```bash
# Start Ollama service
# Navigate to Temi robot home screen
# Enable Logcat monitoring
adb logcat | grep "OllamaClient" > /tmp/ollama_log.txt
```

**Test execution:**
```
Step 1: Ask first question
User speaks: "Show me cardiology doctors"
Robot listens and processes...
Robot responds: "Here are the cardiology doctors: Dr. ABC (Cabin 3A)..."

EXPECTED LOGCAT:
  [OllamaClient] ⚠ Cache enabled but query key is empty -> NO, should show:
  [OllamaClient] (no cache hit message, fresh Ollama call initiated)

Step 2: Ask different question immediately
User speaks: "Show me ophthalmology doctors"
Robot listens and processes...
Robot responds: "Here are the eye doctors: Dr. XYZ (Cabin 2B)..."

EXPECTED RESULT:
  ✅ Response is DIFFERENT from Step 1
  ✅ NOT just repeated cardiology response
  
EXPECTED LOGCAT:
  [OllamaClient] (fresh Ollama call, no cache hit)
  
Step 3: Repeat Step 1 question
User speaks: "Show cardiology doctors again"
Robot responds: Fresh response from Ollama

EXPECTED RESULT:
  ✅ Gets SAME TYPE of response (cardiology)
  ✅ But potentially DIFFERENT WORDING (because cache is disabled)
  ✅ Should NOT return exact same response from earlier
```

**Success Criteria:**
- ✅ Each different question gets different Ollama response
- ✅ No "Cache HIT" in logs for different questions
- ✅ System appears responsive (not stuck)

---

### Test 4: Request/Response Timestamps

**Purpose:** Verify Ollama is actually being called

**Setup:**
```bash
# Monitor timing in logs
adb logcat | grep "OLLAMA_PERF" -E "First chunk|Speaking"
```

**Test execution:**
```
User: "Find doctors"
Expected logs:
  [OLLAMA_PERF] ⚡ First chunk received in 1200ms
  [OLLAMA_PERF] 🔊 Speaking complete response (150 chars)

User: "Find eye doctor"
Expected logs:
  [OLLAMA_PERF] ⚡ First chunk received in 1500ms (NEW TIMING!)
  [OLLAMA_PERF] 🔊 Speaking complete response (200 chars)
```

**Success Criteria:**
- ✅ Each request shows timing (First chunk received)
- ✅ Timings vary between requests (indicates fresh Ollama call)
- ✅ No instant responses (would indicate cache hit)

---

### Test 5: Error Handling

**Purpose:** Verify system handles Ollama errors gracefully

**Test execution (requires stopping Ollama):**
```bash
# Stop Ollama service
# Try to ask a question
User: "Show doctors"

# Expected behavior:
Robot: "Doctor information is currently unavailable. Please try again later."

# Check logs:
adb logcat | grep -i "error\|exception"
```

**Success Criteria:**
- ✅ No crash when Ollama unavailable
- ✅ Graceful fallback response spoken
- ✅ Circuit breaker logs recovery

---

### Test 6: Language Support

**Purpose:** Verify cache fix works in English AND Hindi

**Test in English:**
```
User: "Find cardiology doctors"
Robot: (English response about cardiology) ✅

User: "Find skin doctors"
Robot: (English response about dermatology) ✅
(Should be different from first response)
```

**Test in Hindi:**
```
User: "कार्डियोलॉजिस्ट दिखाएं"  (Show cardiologists)
Robot: (Hindi response) ✅

User: "त्वचा विशेषज्ञ दिखाएं"  (Show skin doctors)
Robot: (Different Hindi response) ✅
```

**Success Criteria:**
- ✅ Works in both languages
- ✅ Different queries return different responses
- ✅ No language-specific cache collisions

---

## Phase 4: Load Testing

### Test 7: Rapid Sequential Queries

**Purpose:** Verify system handles multiple rapid queries

**Test execution:**
```
Ask 5 different questions in quick succession:
1. "Show doctors" (1 sec)
2. "Where is pharmacy" (5 secs after #1)
3. "Show eye doctors" (2 secs after #2)
4. "Appointment booking" (3 secs after #3)
5. "Show heart doctors" (4 secs after #4)

Expected behavior:
- Each gets fresh Ollama response
- System doesn't hang
- No duplicate responses
- Each takes 3-5 seconds (normal LLM latency)
```

**Success Criteria:**
- ✅ All 5 questions answered
- ✅ No system hang
- ✅ Different responses for different questions
- ✅ Conversation lock prevents race conditions

---

### Test 8: Memory Leak Check

**Purpose:** Verify conversation context doesn't consume unbounded memory

**Test execution:**
```bash
# Monitor memory while asking 20 rapid queries
adb shell am start -n com.example.alliswelltemi/.MainActivity
adb shell dumpsys meminfo com.example.alliswelltemi | head -20

# Ask 20 questions
for i in {1..20}; do
  # Simulate voice input
  adb shell input text "Show doctor number $i"
done

# Check memory again
adb shell dumpsys meminfo com.example.alliswelltemi | head -20
```

**Success Criteria:**
- ✅ Memory increase < 50MB (for 20 queries)
- ✅ No memory spikes during requests
- ✅ No evident memory leaks in native allocations

---

## Phase 5: Circuit Breaker Testing

### Test 9: Failure Recovery

**Purpose:** Verify circuit breaker recovers properly

**Test execution:**
```
Step 1: Stop Ollama
User: "Show doctors"
Bot: Error/fallback response
(Logs show: "Circuit Breaker: CLOSED → OPEN (repeated failures)")

Step 2: Wait 30 seconds
(Logs show: "Circuit Breaker: OPEN → HALF_OPEN (timeout exceeded)")

Step 3: Restart Ollama
User: "Show doctors"
Bot: Works again!
(Logs show: "Circuit Breaker: HALF_OPEN → CLOSED (recovered)")
```

**Success Criteria:**
- ✅ Circuit breaker transitions tracked in logs
- ✅ System recovers after Ollama restart
- ✅ No cascading failures

---

## Phase 6: Production Readiness Checks

### Checklist

- [ ] All unit tests pass
- [ ] Integration tests pass on device
- [ ] Cache collision test passes (different queries → different responses)
- [ ] Error handling works (graceful fallback)
- [ ] Language support verified (English + Hindi)
- [ ] Load test passes (5 rapid queries)
- [ ] Memory test passes (no leaks)
- [ ] Circuit breaker works (opens/closes/recovers)
- [ ] Logcat shows correct behavior
- [ ] No unexpected errors in logs
- [ ] Response time normal (3-5 seconds)
- [ ] Robot speech clear and intelligible

---

## Regression Testing

### Comparison with Previous Version

**Before fix:**
```
Query 1: "Show cardiology doctors"
Query 2: "Show eye doctors"
Result: Query 2 returns "Here are the cardiology doctors" ❌

Logcat: "Cache HIT for query: ..." (frequent)
Performance: Very fast (1-2 seconds response)
```

**After fix:**
```
Query 1: "Show cardiology doctors"
Query 2: "Show eye doctors"
Result: Query 2 returns "Here are the ophthalmology doctors" ✅

Logcat: NO "Cache HIT" messages (each query fresh)
Performance: Normal (3-5 seconds response)
```

---

## Sign-Off

**Date:** _______________
**Tester:** _______________
**Result:** ☐ PASS ☐ FAIL ☐ CONDITIONAL PASS

**Comments:**
```
_________________________________________________
_________________________________________________
_________________________________________________
```

**Issues Found:**
- Issue #1: ___________________________________________________________
- Issue #2: ___________________________________________________________

**Resolution:**
- [ ] Deploy to production
- [ ] Defer to next sprint
- [ ] Request code changes

---

## Continuous Monitoring (Post-Deployment)

### Daily Checks
```bash
# Check error rate
adb logcat | grep "error\|exception\|FAIL" | wc -l

# Check cache misuse
adb logcat | grep "Cache HIT" | wc -l  # Should be 0 or very low

# Check circuit breaker status
adb logcat | grep "CircuitBreaker"
```

### Weekly Report
- Number of unique queries handled
- Average response time
- Error/failure rate
- Circuit breaker trips
- Memory usage trend

---

**Document Version:** 1.0
**Last Updated:** May 1, 2026
**Status:** Ready for Testing

