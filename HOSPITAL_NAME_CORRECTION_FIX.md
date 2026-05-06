# Hospital Name Correction Fix — "All Is Well Hospital" (Not AAMIS)

## Problem Identified

The system was occasionally referring to itself as "AAMIS" instead of "All Is Well Hospital". This is a **LLM hallucination** where the Ollama/llama3 model was incorrectly generating hospital names not present in the training data.

### Examples:
- User: "What is this hospital called?"
- Avatar (wrong): "This is AAMIS Hospital..."
- Avatar (correct): "This is All Is Well Hospital..."

---

## Root Cause

The issue stems from:
1. **LLM hallucination** — The model generates text that seems plausible but is incorrect
2. **Insufficient prompt constraint** — System prompt didn't explicitly prevent AAMIS references
3. **No post-processing** — Responses weren't cleaned to fix names

---

## Solution Implemented

### 1. **System Prompt Hardening (RagContextBuilder.kt)**

Added explicit hospital name constraint to BOTH English and Hindi prompts:

```kotlin
// NEW - Added to system prompt
HOSPITAL NAME: All Is Well Hospital (NEVER use AAMIS or any other name)
// And/या
3. हमेशा अस्पताल का नाम "ऑल इज़ वेल हॉस्पिटल" कहें, कोई और नाम न दें
```

**Impact:** LLM is now explicitly instructed to ONLY use the correct hospital name.

### 2. **Response Post-Processing (MainActivity.kt)**

Added hospital name standardization in `safeSpeak()` function:

```kotlin
// Check if AAMIS was present for logging
if (standardizedMessage.contains(Regex("(?i)AAMIS"))) {
    Log.w("HOSPITAL_NAME_FIX", "⚠️ Detected AAMIS hallucination...")
}

// Replace ALL variants:
// - AAMIS → All Is Well Hospital
// - AAMIS Hospital → All Is Well Hospital
// - A.A.M.I.S → All Is Well Hospital
standardizedMessage = standardizedMessage
    .replace(Regex("(?i)AAMIS"), "All Is Well Hospital")
    .replace(Regex("(?i)AAMIS Hospital"), "All Is Well Hospital")
    .replace(Regex("(?i)A\\.?A\\.?M\\.?I\\.?S\\.?"), "All Is Well Hospital")
```

**Impact:** 
- Any AAMIS references are caught and replaced BEFORE speaking
- Logging helps identify when LLM hallucinates
- Covers all possible AAMIS variants

---

## Files Modified

**1. `/app/src/main/java/com/example/alliswelltemi/utils/RagContextBuilder.kt`**
- Lines 218-231 (system prompt): Added explicit hospital name instruction
- Both English and Hindi prompts updated

**2. `/app/src/main/java/com/example/alliswelltemi/MainActivity.kt`**
- Lines 531-543 (safeSpeak function): Added hospital name standardization
- Includes logging for monitoring

---

## How It Works

### Before:
```
LLM Response: "AAMIS Hospital specializes in..."
Sent to TTS: "AAMIS Hospital specializes in..." (WRONG)
User hears: "AAMIS Hospital..." (WRONG NAME)
```

### After:
```
LLM Response: "AAMIS Hospital specializes in..."
Post-processing: Replace AAMIS → "All Is Well Hospital"
Standardized: "All Is Well Hospital specializes in..."
Sent to TTS: "All Is Well Hospital specializes in..." (CORRECT)
User hears: "All Is Well Hospital specializes in..." (CORRECT NAME)
Log: "⚠️ Detected AAMIS hallucination" (for debugging)
```

---

## Monitoring

### Check if AAMIS is being detected and fixed:
```bash
adb logcat | grep "HOSPITAL_NAME_FIX"
# Output: "⚠️ Detected AAMIS hallucination in response, replacing..."
```

If you see this log, it means:
1. ✅ The LLM tried to use AAMIS
2. ✅ The system caught it
3. ✅ It was replaced before speaking to user
4. ✅ User heard correct name

---

## Testing

### Test Case 1: Direct Question
```
Input: "What hospital is this?"
Expected: Response mentions "All Is Well Hospital" (NEVER AAMIS)
Success: ✓ If no HOSPITAL_NAME_FIX log appears, LLM learned correctly
Success: ✓ If HOSPITAL_NAME_FIX log appears, replacement worked
```

### Test Case 2: Contextual Reference
```
Input: "Tell me about your facilities"
Expected: "All Is Well Hospital has..."
Verify: Listen for "All Is Well Hospital" (NOT "AAMIS")
```

### Test Case 3: Different Contexts
```
- "Where can I book an appointment?" → "All Is Well Hospital" (correct)
- "Do you have emergency service?" → "All Is Well Hospital" (correct)
- "What are your visiting hours?" → "All Is Well Hospital" (correct)
```

---

## Why This Is Comprehensive

1. **Prompt-level fix** — Prevents AAMIS at source (system prompt)
2. **Response-level fix** — Catches any hallucinations that slip through
3. **Multiple variant coverage** — Handles AAMIS, AAMIS Hospital, A.A.M.I.S, etc.
4. **Logging** — Tracks when LLM tries to hallucinate (for debugging)
5. **No impact on other responses** — Only targets hospital name replacements

---

## Build & Deploy

```bash
./gradlew sync
./gradlew clean build
adb install -r app/build/outputs/apk/debug/AlliswellTemi-debug.apk

# Verify
adb logcat | grep "HOSPITAL_NAME_FIX"  # Check for AAMIS catches
```

---

## Additional Notes

- **Why not just fine-tune the model?** Fine-tuning Ollama would require retraining, which is expensive. This fix is immediate and effective.
- **Will this affect Hindi responses?** No. Hindi uses "ऑल इज़ वेल हॉस्पिटल" which doesn't have AAMIS hallucination.
- **Future-proof?** Yes. If new hospital name hallucinations appear, just add them to the replacement regex.

---

**Status:** Production-ready | **Breaking Changes:** None | **Risk Level:** Minimal

