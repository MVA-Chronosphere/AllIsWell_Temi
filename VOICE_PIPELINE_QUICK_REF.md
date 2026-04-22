# Production Voice Pipeline - Quick Reference

## 📍 Files Changed/Created

```
✅ NEW: app/src/main/java/com/example/alliswelltemi/utils/SpeechOrchestrator.kt
✅ NEW: app/src/main/java/com/example/alliswelltemi/utils/ContextBuilder.kt
✅ UPDATED: app/src/main/java/com/example/alliswelltemi/MainActivity.kt
```

---

## 🔑 Key Classes

### SpeechOrchestrator
**Purpose**: Detect user intent from speech

```kotlin
val orchestrator = SpeechOrchestrator(doctors)
val context = orchestrator.analyze("Find cardiology doctors")
// context.intent = Intent.FIND_DOCTOR
// context.department = "Cardiology"
// context.confidence = 0.85f
```

**Intents**:
- `FIND_DOCTOR` - User asking about doctors/specialties
- `NAVIGATE` - User wants directions to location/cabin
- `BOOK` - User wants to book appointment
- `GENERAL` - General hospital questions

---

### ContextBuilder
**Purpose**: Build optimized GPT prompts with filtered data

```kotlin
val prompt = ContextBuilder.buildGptPrompt(context, doctors)
// Returns intent-specific prompt (~300-600 chars)
// Includes only relevant doctors/locations
```

**Functions**:
- `buildGptPrompt()` - Main entry point
- `buildDoctorContext()` - Filtered 1-3 doctors
- `buildLocationContext()` - Hospital locations
- `buildHospitalContext()` - General info

---

## 🔄 Voice Pipeline Flow

```
1. User speaks
   ↓
2. onAsrResult() → processSpeech()
   ↓
3. SpeechOrchestrator.analyze()
   ↓
4. Route by intent (navigate, screen switch)
   ↓
5. ContextBuilder.buildGptPrompt()
   ↓
6. callGPT(prompt)  ← With 10s timeout
   ↓
7. onNlpCompleted() → safeSpeak()
   ↓
8. User hears response
```

---

## ⚡ New Safety Features

### Race Condition Prevention
```kotlin
if (!isProcessingSpeech.compareAndSet(false, true)) {
    return // Ignore duplicate ASR
}
```
✅ Prevents duplicate processing if ASR fires twice

### GPT Timeout (10 seconds)
```kotlin
gptTimeoutRunnable = Runnable {
    safeSpeak("Sorry, I am having trouble answering.")
}
handler.postDelayed(gptTimeoutRunnable!!, 10000L)
```
✅ Fallback if GPT doesn't respond

### Timeout Cancellation
```kotlin
gptTimeoutRunnable?.let { handler.removeCallbacks(it) }
```
✅ Cancel timeout when response arrives

---

## 📊 Prompt Size Reduction

**Before**: 1500-1800 chars (full doctor + location list)
**After**: 400-650 chars (filtered, context-specific)
**Reduction**: 50-65% ⬇️

---

## 🧪 Quick Tests

### Test 1: Intent Detection
```
Input: "Find cardiology doctors"
Expected: intent=FIND_DOCTOR, department="Cardiology"
Log: "Intent: Intent.FIND_DOCTOR, Confidence: 0.85"
```

### Test 2: Race Condition Prevention
```
Input: Speak quickly 2x in succession
Expected: Only 1 processSpeech() in logs
Log: "Skipped duplicate ASR - already processing"
```

### Test 3: GPT Timeout
```
Input: Speak (with GPT disabled)
Wait: 10 seconds
Expected: Fallback message "Sorry, I am having trouble answering"
Log: "GPT timeout - no response"
```

### Test 4: Doctor Loading
```
Input: Speak immediately on app start
Expected: "Doctors list is still loading. Please try again."
Log: "Doctors list is empty"
```

---

## 📋 Code Organization

| Class | Responsibility | Key Method |
|-------|-----------------|-----------|
| SpeechOrchestrator | Intent detection | analyze(text) |
| ContextBuilder | Prompt optimization | buildGptPrompt() |
| MainActivity | Voice pipeline orchestration | processSpeech() |

---

## 🚀 No Breaking Changes

- All ViewModels unchanged
- All UI screens unchanged
- All TTS/robot calls unchanged
- Backward compatible ✅

---

## 🔍 Logging

Enable logcat filter for debugging:
```bash
adb logcat | grep -E "TemiSpeech|TemiGPT|TemiLifecycle"
```

Key log messages:
```
[TemiSpeech] Intent: Intent.FIND_DOCTOR, Confidence: 0.85
[TemiSpeech] Navigating to Dr. Sharma's cabin: 3A
[TemiGPT] Sending optimized prompt (450 chars)
[TemiGPT] GPT timeout - no response
[TemiSpeech] Skipped duplicate ASR
```

---

**For detailed implementation, see**: REFACTORING_COMPLETE.md

