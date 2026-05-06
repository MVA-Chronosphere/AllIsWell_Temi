# Lip-Sync Testing Guide

## Quick Test Checklist

### 1. Deploy the App
```bash
export JAVA_HOME=/Applications/Android\ Studio.app/Contents/jbr/Contents/Home
cd /Users/mva357/AndroidStudioProjects/AllIsWell_Temi
./gradlew installDebug
```

### 2. Enable Chrome Remote Debugging
```bash
# Connect device
adb devices

# Enable WebView debugging (if not already in code)
# Already enabled in TemiComponents.kt line 1328:
# WebView.setWebContentsDebuggingEnabled(true)
```

### 3. Open Chrome DevTools
1. Open Chrome on your desktop
2. Go to: `chrome://inspect`
3. Find "AlliswellTemi" WebView
4. Click **"Inspect"**

---

## Expected Console Output

### ✅ Successful Load (What You Should See)

```
═══════════════════════════════════════
📊 MODEL LOAD SUMMARY
═══════════════════════════════════════
✓ Morph meshes found: 1
  - AvatarHead has 15 morph targets
    Visemes: viseme_aa, viseme_E, viseme_I, viseme_O, viseme_U, viseme_PP, viseme_SS, viseme_TH, viseme_DD, viseme_FF, viseme_kk, viseme_nn, viseme_RR, viseme_CH, viseme_sil
✓ Teeth meshes found: 2
  - AvatarTeethUpper
  - AvatarTeethLower
✓ Head bone: Head_08
✓ Neck bone: Neck2_07
═══════════════════════════════════════
Three.js Model Loaded
```

### ✅ During Speech (What You Should See)

```
🎤 Viseme update: viseme_aa intensity: 0.85 adjusted: 0.38
👄 Active visemes: viseme_aa:0.35, viseme_I:0.09
🎤 Viseme update: viseme_PP intensity: 0.90 adjusted: 0.38
🎤 Viseme update: viseme_O intensity: 0.72 adjusted: 0.29
```

### ❌ Problem Indicators (What Means Trouble)

```
⚠️ No morph meshes found - lip-sync will not work!
```
**Solution**: Model missing morph targets. Re-export from Blender with shape keys.

```
⚠️ Unknown viseme: viseme_XYZ
```
**Solution**: TtsLipSyncManager sending wrong viseme names. Check phoneme mapping.

```
⚠️ Mesh missing morph data: AvatarHead
```
**Solution**: Mesh exists but has no morphTargetDictionary. Check GLTF export settings.

---

## Manual Testing in Chrome DevTools

### Test Individual Visemes
Open Console in Chrome DevTools and run:

```javascript
// Test "AH" sound (open mouth)
window.updateViseme('viseme_aa', 1.0);

// Test "OH" sound (round lips)
window.updateViseme('viseme_O', 1.0);

// Test "EE" sound (smile)
window.updateViseme('viseme_I', 1.0);

// Test lip closure (P, B, M)
window.updateViseme('viseme_PP', 1.0);

// Reset to silent
window.updateViseme('viseme_sil', 1.0);
```

### Inspect Morph Mesh Data
```javascript
// Check if morphMeshes array is populated
console.log('Morph meshes:', morphMeshes);

// Check morph target dictionary
console.log('Morph targets:', morphMeshes[0].morphTargetDictionary);

// Check current influences
console.log('Current influences:', morphMeshes[0].morphTargetInfluences);
```

### Verify Teeth Visibility
```javascript
// Check teeth meshes
console.log('Teeth meshes:', teethMeshes);

// Check teeth opacity
console.log('Upper teeth opacity:', teethMeshes[0].material.opacity);
console.log('Lower teeth opacity:', teethMeshes[1].material.opacity);
```

---

## Logcat Monitoring

### View All Logs
```bash
adb logcat | grep -E "Model3DViewer|TtsLipSync|AlliswellTemi"
```

### View Only Model Load Logs
```bash
adb logcat | grep "Model3DViewer"
```

### View Only Viseme Updates
```bash
adb logcat | grep "Viseme update"
```

### Clear Logs and Start Fresh
```bash
adb logcat -c
adb logcat | grep -E "Model3DViewer|TtsLipSync"
```

---

## Visual Verification Checklist

### ✅ What Good Lip-Sync Looks Like
- [ ] Lips move smoothly without jitter
- [ ] Mouth opens wide for "AH" sounds
- [ ] Lips pucker for "OO" sounds
- [ ] Teeth become visible when mouth opens
- [ ] Head tilts slightly back during jaw opening
- [ ] Mouth returns to subtle smile when silent

### ❌ What Bad Lip-Sync Looks Like
- [ ] Lips don't move at all (no morph targets applied)
- [ ] Mouth opens too wide (exaggerated)
- [ ] Lips move but with delay (smoothing too slow)
- [ ] Teeth always visible (transparency not working)
- [ ] Teeth disappear inside face (Z-fighting)
- [ ] Jaw pops/snaps instead of smooth movement

---

## Test Phrases

### English Test Phrases
```kotlin
// In your test code or manually trigger TTS:
robot?.speak(TtsRequest.create("Hello, how are you today?"))
robot?.speak(TtsRequest.create("Open wide and say ahhh"))
robot?.speak(TtsRequest.create("Peter Piper picked a peck of pickled peppers"))
robot?.speak(TtsRequest.create("She sells seashells by the seashore"))
```

### Hindi Test Phrases
```kotlin
robot?.speak(TtsRequest.create("नमस्ते, आप कैसे हैं?", language = "hi-IN"))
robot?.speak(TtsRequest.create("मेरा नाम क्रोनेक्सा है", language = "hi-IN"))
```

### Phoneme Coverage Test
```kotlin
// Tests all major viseme types
robot?.speak(TtsRequest.create(
    "Aah, Eeh, Ooh, Pah, Fah, Thah, Dah, Kah, Chah, Sah, Nah, Rah"
))
```

---

## Troubleshooting Steps

### Problem: No lip movement at all

**Step 1**: Check Chrome DevTools console for model load summary
```
Expected: ✓ Morph meshes found: 1
If you see: ⚠️ No morph meshes found
→ Model doesn't have morph targets
```

**Step 2**: Verify TtsLipSyncManager is running
```bash
adb logcat | grep "TtsLipSync"
```
```
Expected: Viseme update logs appearing during speech
If silent → TtsLipSyncManager not initialized
```

**Step 3**: Check WebView is ready
```
Expected: ✓ WebView ready for viseme updates
If missing → WebView failed to load
```

---

### Problem: Lips move but incorrectly

**Step 1**: Check viseme weights in Chrome DevTools
```javascript
console.log(VISEME_MAX);
```
```
Expected: Each viseme has a calibrated max weight (0.08 - 0.45)
```

**Step 2**: Adjust if too exaggerated
In `TemiComponents.kt`, reduce values in `VISEME_MAX`:
```javascript
const VISEME_MAX = {
  'viseme_aa': 0.30,  // Reduce from 0.45 if too much jaw drop
  'viseme_O':  0.30,  // Reduce from 0.40 if too rounded
  // ...
};
```

**Step 3**: Adjust smoothing speed
```javascript
const LERP_SPEED = 0.25;  // Increase from 0.18 for faster response
```

---

### Problem: Teeth look broken or disappear

**Step 1**: Check teeth mesh detection
```
Expected: ✓ Teeth meshes found: 2
If 0 → Mesh names don't match
```

**Step 2**: Check teeth Z-offset
```javascript
// In Chrome DevTools
console.log(teethMeshes[0].position.z);
console.log(teethMeshes[1].position.z);
```
```
Expected: 0.02 (moved forward from face)
If 0 → Z-fighting with lips
```

**Step 3**: Check renderOrder
```javascript
console.log(teethMeshes[0].renderOrder);
```
```
Expected: 30 (higher than face)
If 0 → Teeth rendered before face
```

---

## Performance Metrics

### Expected Frame Rates
- **3D Rendering**: 60 FPS
- **Viseme Updates**: ~30 Hz (from Kotlin)
- **Smooth Interpolation**: LERP_SPEED = 0.18

### Memory Usage
- WebView typically uses ~50-100 MB
- 3D Model: ~5 MB (indian_doctor_lipsync.glb)

---

## Success Criteria

### ✅ Lip-Sync Is Working If:
1. Console shows "Morph meshes found: 1"
2. Console shows "Teeth meshes found: 2"
3. Console shows "Head bone: Head_08"
4. During speech, console logs "🎤 Viseme update" entries
5. During speech, console logs "👄 Active visemes" entries
6. Visually: Mouth opens/closes in sync with audio
7. Visually: Teeth appear when mouth opens
8. Visually: Head tilts subtly during jaw movement

---

## Quick Fix Commands

### Rebuild and Redeploy
```bash
cd /Users/mva357/AndroidStudioProjects/AllIsWell_Temi
export JAVA_HOME=/Applications/Android\ Studio.app/Contents/jbr/Contents/Home
./gradlew clean assembleDebug installDebug
```

### Force Clear WebView Cache
```bash
adb shell pm clear com.example.alliswelltemi
```

### Restart App
```bash
adb shell am force-stop com.example.alliswelltemi
adb shell am start -n com.example.alliswelltemi/.MainActivity
```

---

## Contact Points

### Key Files
- **3D Model**: `/app/src/main/assets/models/indian_doctor_lipsync.glb`
- **WebView Code**: `/app/src/main/java/com/example/alliswelltemi/ui/components/TemiComponents.kt` (line ~900-1400)
- **TTS Manager**: `/app/src/main/java/com/example/alliswelltemi/ai/TtsLipSyncManager.kt`

### Important Constants
```javascript
// In TemiComponents.kt HTML
OCULUS_VISEMES = [15 viseme types]
VISEME_MAX = { viseme weights }
LERP_SPEED = 0.18
IDLE_SMILE_WEIGHT = 0.09
```

---

## Final Notes

- **Always check Chrome DevTools first** for JavaScript console logs
- **Logcat** shows Kotlin-side logs (TtsLipSyncManager)
- **Model structure** must match: AvatarHead, AvatarTeethUpper, AvatarTeethLower, Head_08
- **Morph target names** must be Oculus viseme standard (viseme_aa, viseme_O, etc.)

Good luck! 🚀

