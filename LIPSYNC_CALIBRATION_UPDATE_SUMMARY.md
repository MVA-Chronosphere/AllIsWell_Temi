# Lip-Sync Calibration Update Summary

**Date:** May 6, 2026  
**File Modified:** `app/src/main/java/com/example/alliswelltemi/ui/components/TemiComponents.kt`  
**Model:** `indian_doctor_lipsync.glb`

---

## Problem Statement

The avatar's facial animations exhibited several issues:

1. **Upper lip stretching unnaturally** during speech
2. **Smile morphs staying active** during conversation
3. **Vowels blending together excessively** (aa + E + I + O simultaneously)
4. **Rubbery/cartoonish mouth deformation** instead of natural speech

---

## Changes Applied

### 1. ✅ VISEME_MAX Recalibration

**Location:** Line 1034-1050

Reduced maximum viseme intensities to eliminate exaggerated facial stretching:

```javascript
const VISEME_MAX = {
  'viseme_sil': 0.00,
  'viseme_PP':  0.30,  // was 0.42
  'viseme_FF':  0.22,  // was 0.35
  'viseme_TH':  0.18,  // was 0.35
  'viseme_DD':  0.20,  // was 0.35
  'viseme_kk':  0.16,  // was 0.28
  'viseme_CH':  0.18,  // was 0.32
  'viseme_SS':  0.12,  // was 0.22
  'viseme_nn':  0.15,  // was 0.30
  'viseme_RR':  0.15,  // was 0.30
  'viseme_aa':  0.28,  // was 0.45
  'viseme_E':   0.04,  // was 0.10
  'viseme_I':   0.03,  // was 0.08
  'viseme_O':   0.22,  // was 0.40
  'viseme_U':   0.20,  // was 0.38
};
```

**Impact:**
- Consonants: 20-40% reduction → sharper, cleaner articulation
- Vowels: 30-70% reduction → natural mouth shapes instead of exaggerated openings
- Smile visemes (E, I): 50-70% reduction → eliminates stretched upper lip

---

### 2. ✅ Idle Smile Morph Removal

**Location:** Line 1112 in `window.updateViseme`

**Removed:**
```javascript
targetWeights['viseme_I'] = IDLE_SMILE_WEIGHT;
targetWeights[viseme] = Math.max(adjustedIntensity, (viseme === 'viseme_I' ? IDLE_SMILE_WEIGHT : 0));
```

**Added:**
```javascript
// NO idle smile - remove all idle morph application
targetWeights[viseme] = adjustedIntensity;
```

**Impact:**
- Avatar no longer smiles while silent
- Upper lip stays neutral when not speaking
- Eliminates unnatural mouth corner pulling

---

### 3. ✅ Vowel Conflict Suppression

**Location:** Lines 1116-1141 in `window.updateViseme`

**Added logic to suppress conflicting vowel morphs:**

```javascript
// Prevent conflicting vowel visemes
const vowelVisemes = [
  'viseme_aa', 'viseme_E', 'viseme_I', 'viseme_O', 'viseme_U'
];

let strongest = null;
let maxWeight = 0;

// Find strongest vowel
vowelVisemes.forEach(v => {
  if (targetWeights[v] > maxWeight) {
    maxWeight = targetWeights[v];
    strongest = v;
  }
});

// Suppress weaker vowels
vowelVisemes.forEach(v => {
  if (v !== strongest) {
    targetWeights[v] *= 0.15;  // 85% reduction for non-dominant vowels
  }
});
```

**Impact:**
- Only one dominant vowel active at a time
- Prevents rubber-face deformation from multiple vowel overlaps
- Cleaner vowel transitions (no aa+E+I blending)

---

### 4. ✅ Smile Clamping During Jaw Opening

**Location:** Lines 1155-1161 in `applySmoothedWeights`

**Added clamping logic:**

```javascript
// Prevent smile stretching during jaw open
const jawOpen = currentWeights['viseme_aa'] || 0;

if (jawOpen > 0.15) {
  currentWeights['viseme_I'] *= 0.2;  // 80% reduction
  currentWeights['viseme_E'] *= 0.3;  // 70% reduction
}
```

**Impact:**
- Prevents smiling during open-mouth speech (aa, O, U)
- Keeps upper lip from pulling upward during vowels
- Maintains natural jaw-lip coordination

---

### 5. ✅ Reduced Smoothing Speed

**Location:** Line 1033

**Changed:**
```javascript
const LERP_SPEED = 0.10;  // was 0.18
```

**Impact:**
- 44% slower transitions
- Softer facial movements
- Less snapping between visemes
- More realistic easing

---

### 6. ✅ Reduced Teeth Opacity Aggressiveness

**Location:** Line 1169

**Changed:**
```javascript
const teethOpacity = Math.min(1.0, mouthOpen * 3);  // was * 8
```

**Impact:**
- 62.5% less aggressive teeth visibility
- Teeth appear gradually instead of flashing
- Reduces visual exaggeration of lip movement

---

### 7. ✅ Debug Logging Added

**Location:** Line 1172 in `applySmoothedWeights`

**Added temporary diagnostic logging:**

```javascript
console.log('AA:', currentWeights['viseme_aa'], 'I:', currentWeights['viseme_I'], 'E:', currentWeights['viseme_E']);
```

**Purpose:**
- Verify viseme_I stays low during speech
- Confirm viseme_E remains subtle
- Validate viseme_aa becomes dominant during open vowels

**Note:** Can be removed after verification testing is complete.

---

## What Was NOT Changed

As per requirements, the following systems remain **untouched**:

✓ Camera framing (lines 1294-1308)  
✓ Model position and scale  
✓ Idle body animation (`updateIdleGesture()`)  
✓ Arm pose configuration (`IDLE_POSE`)  
✓ Rendering pipeline (Three.js setup)  
✓ Mesh traversal logic (morph mesh detection)  
✓ TTS event pipeline  

---

## Expected Results

After these calibration changes:

### ✅ Fixed Issues
- **Upper lip stretching** → eliminated by reduced E/I viseme weights + jaw open clamping
- **Persistent smile** → removed idle smile morph completely
- **Vowel blending** → only dominant vowel remains strong (85% suppression of others)
- **Rubbery deformation** → reduced VISEME_MAX values produce natural mouth shapes
- **Cartoonish appearance** → slower LERP_SPEED + reduced teeth opacity = realistic speech

### 🎯 Verification Checklist
- [ ] viseme_I stays below 0.05 during speech (check console logs)
- [ ] viseme_E stays below 0.10 during speech (check console logs)
- [ ] viseme_aa becomes dominant (0.15-0.28) during open vowels
- [ ] No smile visible when avatar is silent
- [ ] Mouth transitions are smooth and natural
- [ ] Teeth visibility is gradual, not sudden
- [ ] Upper lip no longer pulls upward during "aa" sounds

---

## Testing Instructions

1. **Deploy updated build** to Temi robot
2. **Trigger TTS speech** with varied phonemes:
   - Test "Hello, how are you?" (mixed consonants + vowels)
   - Test "I am fine" (heavy 'I' vowel sound)
   - Test "Open the door" (heavy 'aa' and 'O' vowels)
   - Test "Please wait" (long 'E' sound)
3. **Observe console logs** in Chrome DevTools (connect to WebView):
   - Confirm AA/I/E values match expected ranges
   - Verify vowel suppression is working
4. **Visual verification:**
   - No smile during silence
   - Clean vowel shapes (no blending)
   - Natural upper lip movement
   - Gradual teeth appearance

---

## Rollback Instructions

If calibration produces unexpected results, revert to previous values:

```javascript
// OLD VISEME_MAX (before calibration)
const VISEME_MAX = {
  'viseme_PP':  0.42, 'viseme_FF':  0.35, 'viseme_TH':  0.35,
  'viseme_DD':  0.35, 'viseme_kk':  0.28, 'viseme_CH':  0.32,
  'viseme_SS':  0.22, 'viseme_nn':  0.30, 'viseme_RR':  0.30,
  'viseme_aa':  0.45, 'viseme_E':   0.10, 'viseme_I':   0.08,
  'viseme_O':   0.40, 'viseme_U':   0.38
};

const LERP_SPEED = 0.18;
const teethOpacity = Math.min(1.0, mouthOpen * 8);

// Re-add idle smile:
targetWeights['viseme_I'] = 0.09;

// Remove vowel conflict suppression block (lines 1116-1141)
// Remove smile clamping block (lines 1155-1161)
```

---

## Commit Information

**Files Changed:** 1  
**Lines Changed:** ~50  
**Build Status:** ✅ No compilation errors (only existing warnings)  
**Breaking Changes:** None  
**Backward Compatible:** Yes  

---

## Next Steps

1. **Production Testing:**
   - Test with bilingual TTS (English + Hindi)
   - Test with varied speech speeds
   - Test with different Temi SDK TtsRequest parameters

2. **Fine-Tuning (if needed):**
   - If vowels still blend, reduce vowel suppression factor (0.15 → 0.10)
   - If speech is too slow, increase LERP_SPEED slightly (0.10 → 0.12)
   - If teeth are still too prominent, reduce multiplier further (3 → 2)

3. **Remove Debug Logs:**
   - After verification, delete console.log at line 1172
   - Keep diagnostic logs for morph mesh detection

---

**Implementation Status:** ✅ COMPLETE  
**Tested:** Awaiting deployment  
**Documentation:** AGENTS.md, 3D_MODEL_TTS_LIPSYNC_QUICK_REFERENCE.md (to be updated)

