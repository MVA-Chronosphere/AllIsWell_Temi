# Lip Sync Calibration Fix — Summary

## Problem Identified
The 3D avatar lip sync exhibited:
- **Upper lip only stretching** (not full facial mouth opening)
- **Middle lip area too stiff** (weak jaw/mouth opening visemes)
- **Teeth showing broken/fragmented** (opacity too low + mouth not opening enough)
- **Overall mouth opening too subtle** (multiple dampening factors stacked)

## Root Causes
1. **VISEME_MAX vowel values too low:**
   - `viseme_aa: 0.16` → should be `0.28` (jaw opening)
   - `viseme_O: 0.12` → should be `0.22` (rounded mouth)
   - `viseme_U: 0.10` → should be `0.20` (pursed lips)
   - `viseme_E, viseme_I` left at `0.02` & `0.01` (smile morphs)

2. **Jaw damping too aggressive:**
   - Line 1117: `finalIntensity *= 0.55` crushed viseme_aa by 45%
   - Changed to `0.75` to allow 25% jaw opening instead of 45% suppression

3. **Vowel suppression too harsh:**
   - Lines 1177-1182: When mouth opening > 0.08, viseme_I & viseme_E multiplied by **0.05 & 0.08**
   - This completely collapsed smile/upper-lip morphs
   - Changed to **dynamic suppression** that scales from 0.2–1.0 based on actual mouth opening
   - Now: Light mouth → full smile possible; Wide mouth → smile 80% suppressed (gradual)

4. **Global clamping limits:**
   - Line 1190: Hard clamp at `0.16` prevented viseme_aa from using full 0.28 range
   - Line 1194: Global clamp at `0.18` limited all visemes to max 18% intensity
   - Changed to `0.25` to allow fuller morph range (calibrated against VISEME_MAX values)

5. **Teeth opacity too dim:**
   - Line 1203: `mouthOpen * 1.2` capped at `0.45` → teeth barely visible
   - Changed to `mouthOpen * 2.0` capped at `0.65` → teeth now show clearly when mouth opens

---

## Exact Changes Made

### Change 1: VISEME_MAX Calibration (Lines 1034–1050)
**Before:**
```javascript
const VISEME_MAX = {
  'viseme_aa':  0.16,
  'viseme_E':   0.02,
  'viseme_I':   0.01,
  'viseme_O':   0.12,
  'viseme_U':   0.10,
};
```

**After:**
```javascript
const VISEME_MAX = {
  'viseme_aa':  0.28,  // ← Jaw opening (2x stronger)
  'viseme_E':   0.04,  // ← Keep subtle for smile (doubled for flexibility)
  'viseme_I':   0.03,  // ← Keep subtle for smile (3x stronger)
  'viseme_O':   0.22,  // ← Rounded mouth shape (2x stronger)
  'viseme_U':   0.20,  // ← Pursed lips (2x stronger)
};
```

**Impact:** Mouth opening now has full range; vowel morphs have breathing room.

---

### Change 2: Jaw Damping Reduction (Lines 1114–1118)
**Before:**
```javascript
if (viseme === 'viseme_aa') {
    finalIntensity *= 0.55;  // 45% suppression
}
```

**After:**
```javascript
if (viseme === 'viseme_aa') {
    finalIntensity *= 0.75;  // 25% suppression
}
```

**Impact:** Jaw opens more naturally. Originally 0.28 × 0.55 = 0.154; now 0.28 × 0.75 = 0.21.

---

### Change 3: Dynamic Vowel Suppression (Lines 1176–1182)
**Before:**
```javascript
if (openAmount > 0.08) {
    currentWeights['viseme_I'] *= 0.05;   // Crush to 5%
    currentWeights['viseme_E'] *= 0.08;   // Crush to 8%
    currentWeights['viseme_SS'] *= 0.4;
    currentWeights['viseme_CH'] *= 0.5;
}
```

**After:**
```javascript
if (openAmount > 0.08) {
    const suppressScale = Math.min(1.0, openAmount / 0.25);  // Ramp 0→1 as mouth opens
    currentWeights['viseme_I'] *= Math.max(0.2, 1.0 - suppressScale * 0.8);  // 80–20%
    currentWeights['viseme_E'] *= Math.max(0.25, 1.0 - suppressScale * 0.75); // 75–25%
    currentWeights['viseme_SS'] *= 0.4;
    currentWeights['viseme_CH'] *= 0.5;
}
```

**Impact:** 
- Slight mouth opening: smile 80% active, suppression 20% (natural)
- Wide mouth: smile 20% active, suppression 80% (prevents distortion)
- Gradual transition: no sudden shape collapse

---

### Change 4: Relax Global Clamping (Lines 1190 & 1194)
**Before:**
```javascript
currentWeights['viseme_aa'] = Math.min(currentWeights['viseme_aa'], 0.16);  // Never > 0.16
// Later...
for (const name of OCULUS_VISEMES) {
  currentWeights[name] = Math.max(0, Math.min(currentWeights[name], 0.18));  // Never > 0.18
}
```

**After:**
```javascript
currentWeights['viseme_aa'] = Math.min(currentWeights['viseme_aa'], 0.28);  // Match VISEME_MAX
// Later...
for (const name of OCULUS_VISEMES) {
  currentWeights[name] = Math.max(0, Math.min(currentWeights[name], 0.25));  // Calibrated to max visemes
}
```

**Impact:** Morphs can now reach their intended VISEME_MAX values; no artificial ceiling.

---

### Change 5: Teeth Opacity & Scaling (Lines 1198 & 1203)
**Before:**
```javascript
const teethOpacity = Math.min(0.45, mouthOpen * 1.2);  // Max 0.45, weak scaling
```

**After:**
```javascript
const teethOpacity = Math.min(0.65, mouthOpen * 2.0);  // Max 0.65, 2x scaling
```

**Impact:** 
- Teeth fade in faster as mouth opens (2.0× curve)
- Peak opacity 0.65 instead of 0.45 (44% brighter)
- Teeth now visible during natural speech

---

## What Was NOT Changed
✅ **Intentionally Preserved:**
- Camera framing, model position/scale
- Idle body animation (`updateIdleGesture`)
- Arm pose configuration (`IDLE_POSE`)
- Rendering pipeline, mesh traversal
- TTS event pipeline
- Smile clamping threshold (jawOpen > 0.15 still applies)
- Lip stabilization logic (viseme_PP boost)
- Head tilt coupling (subtle jaw influence)

---

## Expected Visual Improvements
1. **Mouth opening:** Fuller, more natural jaw drop during /a/ /o/ /u/ sounds
2. **Upper lip:** Stretches more on /i/ /e/ but doesn't collapse when jaw opens
3. **Middle lip:** Follows jaw opening smoothly; mid-mouth area now animated
4. **Teeth:** Visible during natural speech (especially /a/, /o/, /aa/)
5. **Transitions:** Smooth LERP between shapes (no sudden snapping)

---

## Testing Checklist
- [ ] Play TTS with vowel-heavy words: "Amazing", "Open", "India"
- [ ] Check jaw opens visibly during /aa/ sounds
- [ ] Verify upper lip stretches during /i/ /e/ (smile morphs)
- [ ] Confirm teeth show when mouth is open (not just at extreme)
- [ ] Watch for smooth transitions between visemes (no popping)
- [ ] Verify no broken/distorted lip geometry

---

## Build & Deploy
```bash
# Sync Gradle
./gradlew sync

# Rebuild
./gradlew clean build

# Deploy to device
adb install -r app/build/outputs/apk/debug/AlliswellTemi-debug.apk
```

Monitor `adb logcat | grep "👄 Active visemes"` to verify morphs are being applied.

