# Quick Reference - Intent-Based Query System

## How to Test

### Basic Test Commands
```bash
# Test cardiology
"who are the cardiology office"
"tell me about heart doctors"
"show me cardiologists"

# Test ophthalmology  
"eye doctors"
"who is the ophthalmology specialist"
"vision doctors"

# Should see in logs:
# 🎯 Intent detected: 'cardiology' → stem='cardio'
# ✓ Matched: Dr. [Name] ([Department])
```

---

## Intent Mappings Cheat Sheet

| User Says | Intent Stem | Finds |
|-----------|-------------|-------|
| cardiology, heart, cardio | `cardio` | Cardiologists |
| eye, vision, ophthal | `ophthal` | Ophthalmologists |
| bone, joint, ortho | `ortho` | Orthopedists |
| skin, dermat | `dermat` | Dermatologists |
| child, kids, pediatr | `pediatr` | Pediatricians |
| lung, respiratory, pulmo | `pulmo` | Pulmonologists |
| brain, neuro | `neuro` | Neurologists |
| mental, psychiat | `psychiat` | Psychiatrists |
| therapy, physio, rehab | `physio` | Physiotherapists |

---

## What Changed

### 1. TTS (MainActivity.kt)
**Before:** Chunked speech (2 parts)
**After:** Complete speech (1 part)

### 2. Doctor Info (RagContextBuilder.kt)
**Before:** Mentions experience years
**After:** Mentions specialization

### 3. Query Understanding (RagContextBuilder.kt)
**Before:** Keyword matching only
**After:** Intent detection + matching

---

## Debugging

### Check Intent Detection
```bash
adb logcat | grep "🎯"
```

### Check Doctor Matching
```bash
adb logcat | grep "✓ Matched"
```

### Check TTS
```bash
adb logcat | grep "🔊"
```

---

## Common Issues & Solutions

### Issue: "No information available" 
**Check:** Are doctors loaded?
```bash
adb logcat | grep "doctors loaded"
```

### Issue: Wrong doctors returned
**Check:** Intent detection logs
```bash
adb logcat | grep "Intent detected"
```

### Issue: TTS still chunked
**Check:** MainActivity changes applied
```bash
adb logcat | grep "Speaking complete response"
```

---

## Success Indicators

✅ Single TTS event per response
✅ Intent detection logs show correct stem
✅ Multiple doctors matched for department queries
✅ Responses mention specialization, not experience
✅ "Cardiology office" finds cardiologists

---

## File Locations

**Intent Detection:** `RagContextBuilder.kt` line 186-254
**Doctor Filtering:** `RagContextBuilder.kt` line 300-370
**TTS Fix:** `MainActivity.kt` line 305-340

---

## Deployment

```bash
./gradlew clean installDebug
```

**Time:** 2-3 minutes
**Risk:** LOW (all backward compatible)

---

**Last Updated:** April 25, 2026
**Status:** PRODUCTION READY ✅

