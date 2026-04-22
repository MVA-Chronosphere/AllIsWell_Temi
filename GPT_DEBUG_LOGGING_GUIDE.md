# GPT Debug Logging & Conversation Flow Guide

## 🎯 Objective
Track the complete GPT conversation pipeline from user input → prompt generation → GPT processing → response → TTS.

---

## 📊 Complete Log Flow (Expected Sequence)

### 1. **User Speaks** (ASR Input)
```
TemiSpeech: ASR Result: 'who is Dr Akshay Sharma' (language: EN_US)
GPT_DEBUG: Inactivity timer RESTARTED
```

### 2. **Speech Processing Starts**
```
PERF: Orchestrator.analyze() starting on background thread
TemiSpeech: Intent: GENERAL, Confidence: 0.5, Doctor: null, Dept: null
```

### 3. **Prompt Generation**
```
PERF: ContextBuilder.buildGptPrompt() starting
PERF: Background processing completed in 62ms
GPT_PROMPT: ========== GPT PROMPT START ==========
GPT_PROMPT: You are Temi, the AI hospital assistant. Be concise (max 2 sentences).
GPT_PROMPT: HOSPITAL: All Is Well Hospital | 9:00 AM - 5:00 PM | Emergency: 24/7 | Today: Tuesday, April 22, 2026
GPT_PROMPT: DOCTORS:
GPT_PROMPT: Dr. Akshay Sharma - Cardiology, 8y exp, Cabin 3A
GPT_PROMPT: Dr. Priya Patel - Neurology, 12y exp, Cabin 5B
GPT_PROMPT: Dr. Rajesh Kumar - Orthopedics, 15y exp, Cabin 2C
GPT_PROMPT: USER: who is Dr Akshay Sharma
GPT_PROMPT: RESPONSE:
GPT_PROMPT: ========== GPT PROMPT END (350 chars) ==========
```

### 4. **GPT Call Initiated**
```
GPT_DEBUG: ========== STARTING GPT CONVERSATION ==========
GPT_DEBUG: isConversationActive=true
GPT_DEBUG: Inactivity timer CANCELLED during GPT
GPT_DEBUG: Cleaned prompt length: 340 chars (original: 350)
GPT_DEBUG: Calling robot.askQuestion() now...
GPT_DEBUG: robot.askQuestion() called successfully
GPT_DEBUG: Timeout handler set for 12000ms
```

### 5. **Conversation Attachment Status**
```
TemiSpeech: Conversation attached: true  ✅ (GOOD - conversation active)
```
**OR**
```
TemiSpeech: Conversation attached: false  ❌ (BAD - conversation detached, GPT will timeout)
```

### 6. **GPT Processing Updates**
```
GPT_DEBUG: ========== CONVERSATION STATUS CHANGED ==========
GPT_DEBUG: Status: LISTENING (1)
GPT_DEBUG: Text: '<empty>'
GPT_DEBUG: isConversationActive: true
GPT_DEBUG: isGptProcessing: false
GPT_DEBUG: GPT is listening...
```

```
GPT_DEBUG: ========== CONVERSATION STATUS CHANGED ==========
GPT_DEBUG: Status: THINKING (2)
GPT_DEBUG: Text: '<empty>'
GPT_DEBUG: isConversationActive: true
GPT_DEBUG: isGptProcessing: true
GPT_DEBUG: GPT is thinking/processing...
```

### 7. **GPT Response Received** ✅
```
GPT_DEBUG: ========== CONVERSATION STATUS CHANGED ==========
GPT_DEBUG: Status: SPEAKING (3)
GPT_DEBUG: Text: 'Dr. Akshay Sharma is a Cardiologist with 8 years of experience. His cabin is located at 3A.'
GPT_DEBUG: isConversationActive: true
GPT_DEBUG: isGptProcessing: true
GPT_DEBUG: ========== GPT RESPONSE RECEIVED ==========
GPT_DEBUG: Response time: 2340ms
GPT_DEBUG: Response length: 98 chars
GPT_RESPONSE: ========== GPT RESPONSE START ==========
GPT_RESPONSE: Dr. Akshay Sharma is a Cardiologist with 8 years of experience. His cabin is located at 3A.
GPT_RESPONSE: ========== GPT RESPONSE END ==========
GPT_DEBUG: Conversation lock RELEASED
GPT_DEBUG: Speaking response via TTS...
GPT_DEBUG: Timeout handler CANCELLED
GPT_DEBUG: Inactivity timer RESTARTED after GPT response
```

### 8. **GPT Timeout (Fallback)** ❌
```
GPT_DEBUG: ========== GPT TIMEOUT ==========
GPT_DEBUG: No response after 12000ms
GPT_DEBUG: Conversation still active, generating fallback...
GPT_DEBUG: Fallback response: I'm having trouble processing your request. Please try again.
GPT_DEBUG: Inactivity timer RESTARTED after timeout
```

---

## 🔍 Diagnostic Checklist

### ✅ Success Indicators
- [ ] `Conversation attached: true` appears after `robot.askQuestion()` call
- [ ] `Status: LISTENING (1)` → `Status: THINKING (2)` → `Status: SPEAKING (3)`
- [ ] `GPT RESPONSE RECEIVED` appears within 2-5 seconds
- [ ] `Response time: XXXXms` shows reasonable latency (<5000ms)
- [ ] `Conversation lock RELEASED` appears
- [ ] `Inactivity timer RESTARTED after GPT response` appears

### ❌ Failure Indicators
- [ ] `Conversation attached: false` immediately after `robot.askQuestion()`
  - **Cause**: Prompt too long or malformed
  - **Fix**: Check `GPT_PROMPT` logs, simplify context
  
- [ ] `GPT TIMEOUT` appears after 12 seconds
  - **Cause**: No response from Temi GPT service
  - **Fix**: Check network, verify Temi GPT configuration
  
- [ ] `⚠️ Conversation went IDLE without response`
  - **Cause**: Conversation terminated prematurely
  - **Fix**: Check for competing speech/ASR events

---

## 🛠️ Troubleshooting Guide

### Problem: "Conversation attached: false"
**Diagnosis**: Prompt rejected by Temi SDK

**Steps**:
1. Check `GPT_PROMPT` logs for prompt content
2. Verify prompt length (<1000 chars recommended)
3. Check for special characters or excessive formatting
4. Simplify doctor/location context

**Fix Applied**:
- Simplified prompt format (removed `trimIndent()`)
- Added prompt cleaning (max 2 newlines, single spaces)
- Reduced context size (max 3 doctors)

---

### Problem: GPT Timeout After 12 Seconds
**Diagnosis**: No response from GPT service

**Steps**:
1. Verify Temi robot is connected to internet
2. Check if Temi GPT API is configured correctly
3. Test with simpler prompt (e.g., "Hello")
4. Check Temi system logs for GPT errors

**Current Timeout**: 12 seconds (configurable via `GPT_TIMEOUT_MS`)

---

### Problem: Multiple Conversations Overlapping
**Diagnosis**: Race condition bypassed lock

**Steps**:
1. Look for `Blocked: conversation already active` logs
2. Check `isConversationActive` state in logs
3. Verify ASR blocking: `Blocked ASR: conversation active`

**Protection**:
- `AtomicBoolean isConversationActive` ensures exclusivity
- ASR blocked during active conversation
- processSpeech() blocked during active conversation

---

## 📝 Key Log Tags

| Tag | Purpose | Example |
|-----|---------|---------|
| `GPT_PROMPT` | Full prompt sent to GPT | Multi-line prompt with context |
| `GPT_DEBUG` | Conversation state transitions | `STARTING GPT CONVERSATION` |
| `GPT_RESPONSE` | GPT's actual response text | Response from GPT API |
| `TemiSpeech` | Temi SDK conversation events | `Conversation attached: true` |
| `PERF` | Performance metrics | `Background processing completed in 62ms` |

---

## 🔬 Advanced Debugging

### Enable Verbose Temi SDK Logs
Add to `adb logcat` filter:
```bash
adb logcat | grep -E "(GPT_|TemiSpeech|PERF)"
```

### Check Full Logs
```bash
adb logcat | grep "com.example.alliswelltemi" > temi_full_logs.txt
```

### Monitor Real-Time
```bash
adb logcat -s GPT_DEBUG:D GPT_PROMPT:D GPT_RESPONSE:D TemiSpeech:D
```

---

## 📊 Prompt Size Guidelines

| Intent | Typical Size | Max Recommended |
|--------|-------------|-----------------|
| FIND_DOCTOR | 300-500 chars | 800 chars |
| NAVIGATE | 200-400 chars | 600 chars |
| BOOK | 150-300 chars | 500 chars |
| GENERAL | 400-700 chars | 1000 chars |

**Current Implementation**:
- Limits to 3 most relevant doctors
- Cleans excessive whitespace/newlines
- Uses concise formatting

---

## ✅ Success Metrics

**Healthy GPT Flow**:
- Prompt generation: <100ms
- GPT response time: 2-5 seconds
- No timeouts
- Conversation lock released properly
- No `Conversation attached: false` errors

**Target Performance**:
- 95%+ success rate for GPT responses
- <5% timeout rate
- 0 conversation detachment errors

---

## 🚀 Next Steps After Testing

1. **Monitor `GPT_PROMPT` logs** → Verify prompt quality
2. **Check `Conversation attached` status** → Must be `true`
3. **Track response times** → Should be 2-5 seconds
4. **Test edge cases**:
   - Very long doctor names
   - Multiple rapid queries
   - Complex medical questions
5. **Adjust `GPT_TIMEOUT_MS`** if needed (currently 12s)

---

**Last Updated**: April 22, 2026  
**Build Version**: Latest with comprehensive logging  
**Log Level**: DEBUG (production should use INFO/WARN)

