# Lip-Sync Mesh Fix Summary

## Problem Identified
The lip-sync system was not working because the code was looking for **incorrect mesh names** that didn't match your actual 3D model structure.

### Old (Incorrect) Mesh Names
```javascript
// These mesh names don't exist in indian_doctor_lipsync.glb
'Object_9'   // Face mesh
'Object_14'  // Upper teeth
'Object_15'  // Lower teeth
```

### New (Correct) Mesh Names
Based on your model analysis, the actual mesh names are:
```javascript
'AvatarHead'        // Face mesh with morph targets for visemes
'AvatarTeethUpper'  // Upper teeth
'AvatarTeethLower'  // Lower teeth
'Head_08'           // Head bone for subtle head movement
'Neck2_07'          // Neck bone
```

---

## Changes Made to `TemiComponents.kt`

### 1. **Fixed Face Mesh Detection** (Line ~1172-1182)
```javascript
// OLD: Only looked for 'Object_9'
if (node.name === 'Object_9') { ... }

// NEW: Looks for both 'AvatarHead' AND 'Object_9' (backward compatible)
if (node.name === 'AvatarHead' || node.name === 'Object_9') {
  console.log('✓ Found face mesh:', node.name);
  // Apply material fixes
  // Check for morph targets (visemes)
  if (node.morphTargetDictionary) {
    console.log('✓ Face mesh has morph targets:', Object.keys(node.morphTargetDictionary));
    morphMeshes.push(node);
  }
}
```

### 2. **Fixed Teeth Mesh Detection** (Line ~1185-1210)
```javascript
// Upper teeth
if (node.name === 'AvatarTeethUpper' || node.name === 'Object_14') {
  console.log('✓ Found upper teeth:', node.name);
  // Apply transparency and material fixes
  teethMeshes.push(node);
}

// Lower teeth
if (node.name === 'AvatarTeethLower' || node.name === 'Object_15') {
  console.log('✓ Found lower teeth:', node.name);
  // Apply transparency and material fixes
  teethMeshes.push(node);
}
```

### 3. **Fixed Bone Detection** (Line ~1214-1225)
```javascript
// OLD: Generic 'includes' check
if (node.name.toLowerCase().includes('head')) { ... }

// NEW: Specific bone name checks
if (node.name === 'Head_08' || node.name.toLowerCase().includes('head')) {
  headBone = node;
  console.log('✓ Found head bone:', node.name);
}
if (node.name === 'Neck2_07' || node.name.toLowerCase().includes('neck')) {
  neckBone = node;
  console.log('✓ Found neck bone:', node.name);
}
```

### 4. **Added Comprehensive Diagnostics** (Line ~1230-1244)
After model loads, console now shows:
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
```

### 5. **Enhanced Viseme Update Logging** (Line ~1101-1117)
```javascript
window.updateViseme = function(viseme, intensity) {
  // ... existing logic ...
  
  // Log significant viseme changes
  if (intensity > 0.1) {
    console.log('🎤 Viseme update:', viseme, 'intensity:', intensity.toFixed(2), 'adjusted:', adjustedIntensity.toFixed(2));
  }
};
```

### 6. **Added Morph Target Application Diagnostics** (Line ~1116-1175)
```javascript
function applySmoothedWeights() {
  // ... smoothing logic ...
  
  // Warn if no morph meshes found (every 5 seconds)
  if (morphMeshes.length === 0) {
    const now = Date.now();
    if (!window.lastMorphWarning || now - window.lastMorphWarning > 5000) {
      console.warn('⚠️ No morph meshes found - lip-sync will not work!');
      window.lastMorphWarning = now;
    }
  }
  
  // Log active visemes periodically (1% chance each frame)
  if (appliedCount > 0 && Math.random() < 0.01) {
    const activeVisemes = OCULUS_VISEMES
      .filter(v => currentWeights[v] > 0.1)
      .map(v => v + ':' + currentWeights[v].toFixed(2));
    console.log('👄 Active visemes:', activeVisemes.join(', '));
  }
}
```

---

## How It Works Now

### Model Loading Flow
```
1. Load indian_doctor_lipsync.glb
   ↓
2. Traverse all nodes in the scene
   ↓
3. Find meshes:
   - AvatarHead → morphMeshes[] (has viseme morph targets)
   - AvatarTeethUpper → teethMeshes[]
   - AvatarTeethLower → teethMeshes[]
   ↓
4. Find bones:
   - Head_08 → headBone
   - Neck2_07 → neckBone
   ↓
5. Log diagnostic summary to confirm detection
```

### Lip-Sync Runtime Flow
```
Kotlin TtsLipSyncManager
   ↓ (sends viseme updates ~30 FPS)
WebView.evaluateJavascript("window.updateViseme(...)")
   ↓
JavaScript updateViseme() function
   ↓ (sets target weights)
applySmoothedWeights() [called every frame]
   ↓ (smooth interpolation)
AvatarHead.morphTargetInfluences[viseme_aa] = weight
AvatarHead.morphTargetInfluences[viseme_O] = weight
... (all 15 Oculus visemes)
```

---

## Expected Console Logs During Runtime

### On Model Load
```
✓ Found face mesh: AvatarHead
✓ Face mesh has morph targets: [viseme_aa, viseme_E, ...]
✓ Found upper teeth: AvatarTeethUpper
✓ Found lower teeth: AvatarTeethLower
✓ Found head bone: Head_08
✓ Found neck bone: Neck2_07
═══════════════════════════════════════
📊 MODEL LOAD SUMMARY
═══════════════════════════════════════
✓ Morph meshes found: 1
  - AvatarHead has 15 morph targets
    Visemes: viseme_aa, viseme_E, viseme_I, viseme_O, viseme_U, ...
✓ Teeth meshes found: 2
✓ Head bone: Head_08
✓ Neck bone: Neck2_07
═══════════════════════════════════════
Three.js Model Loaded
```

### During Speech (TTS Active)
```
🎤 Viseme update: viseme_aa intensity: 0.85 adjusted: 0.38
🎤 Viseme update: viseme_O intensity: 0.72 adjusted: 0.29
👄 Active visemes: viseme_aa:0.35, viseme_I:0.09
🎤 Viseme update: viseme_PP intensity: 0.90 adjusted: 0.38
```

### If Something Goes Wrong
```
⚠️ Unknown viseme: viseme_XYZ
⚠️ No morph meshes found - lip-sync will not work!
⚠️ Mesh missing morph data: SomeMeshName
```

---

## Debugging Tips

### Check Chrome DevTools (Remote Debugging)
1. Connect device via USB
2. Open `chrome://inspect` in Chrome desktop
3. Find your WebView instance
4. Click "Inspect" to see console logs in real-time

### Verify Morph Targets Exist
If logs show "No morph meshes found", the model may not have morph targets. Verify in Blender:
```python
import bpy
obj = bpy.data.objects['AvatarHead']
print(obj.data.shape_keys.key_blocks.keys())
# Should show: ['viseme_aa', 'viseme_E', 'viseme_I', ...]
```

### Test Individual Visemes
```javascript
// In Chrome DevTools console
window.updateViseme('viseme_aa', 1.0);  // Mouth open wide (ah)
window.updateViseme('viseme_O', 1.0);   // Mouth round (oh)
window.updateViseme('viseme_PP', 1.0);  // Lips closed (p/b)
```

---

## Mesh Structure Reference

### AvatarHead (Face Mesh)
- **Located**: Root mesh in model hierarchy
- **Morph Targets**: 15 Oculus visemes
  - `viseme_sil` (silence/idle)
  - `viseme_PP` (p, b, m)
  - `viseme_FF` (f, v)
  - `viseme_TH` (th)
  - `viseme_DD` (t, d, n)
  - `viseme_kk` (k, g)
  - `viseme_CH` (ch, j, sh)
  - `viseme_SS` (s, z)
  - `viseme_nn` (n, ng)
  - `viseme_RR` (r)
  - `viseme_aa` (ah - open jaw)
  - `viseme_E` (eh)
  - `viseme_I` (ee - smile)
  - `viseme_O` (oh - round lips)
  - `viseme_U` (oo - pucker)

### AvatarTeethUpper & AvatarTeethLower
- **Purpose**: Show/hide based on mouth openness
- **Transparency Logic**: `opacity = min(1.0, mouthOpen * 8)`
- **Z-Offset**: +0.02 units forward (prevents z-fighting with lips)

### Head_08 Bone
- **Purpose**: Subtle head tilt during speech
- **Coupling**: Tilts back slightly when jaw opens (viseme_aa, viseme_O)
- **Max Rotation**: ±0.06 radians (~3.4 degrees)

---

## Performance Notes

- **Frame Rate**: 60 FPS rendering
- **Viseme Update Rate**: ~30 updates/second from Kotlin
- **Smoothing**: LERP_SPEED = 0.18 (smooth transitions to avoid jittery movement)
- **Morph Target Max Weights**: Calibrated to prevent over-exaggerated lip movement
  - `viseme_aa`: 0.45 (was causing too much jaw drop at 0.52)
  - `viseme_PP`: 0.42 (was too tight at 0.48)
  - `viseme_O`: 0.40 (was too rounded at 0.48)

---

## Build Status
✅ **Build Successful** (assembleDebug)
- Only warnings (unused variables/imports)
- No compilation errors
- Ready for deployment

---

## Next Steps

1. **Deploy to Device**
   ```bash
   export JAVA_HOME=/Applications/Android\ Studio.app/Contents/jbr/Contents/Home
   ./gradlew installDebug
   ```

2. **Test Lip-Sync**
   - Trigger TTS speech
   - Check logcat for diagnostic logs:
     ```bash
     adb logcat | grep -E "Model3DViewer|TtsLipSync"
     ```

3. **Use Chrome DevTools**
   - Verify morph targets are being applied
   - Watch console for viseme update logs

4. **Fine-Tune Viseme Weights** (if needed)
   - Adjust `VISEME_MAX` values in TemiComponents.kt
   - Lower values = more subtle lip movements
   - Higher values = more exaggerated expressions

---

## Files Modified
- ✅ `/app/src/main/java/com/example/alliswelltemi/ui/components/TemiComponents.kt`

## Summary
The lip-sync system now correctly targets the **AvatarHead**, **AvatarTeethUpper**, and **AvatarTeethLower** meshes from your `indian_doctor_lipsync.glb` model. Comprehensive diagnostics have been added to verify mesh detection and viseme application in real-time.

