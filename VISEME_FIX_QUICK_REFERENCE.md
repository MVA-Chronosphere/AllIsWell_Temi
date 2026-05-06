# Viseme Morph Fix - Quick Reference

## Problem
Upper lip stretches upward → Need natural jaw opening downward

## Files Created
1. **fix_viseme_morphs.py** - Automated Blender script
2. **VISEME_MORPH_FIX_GUIDE.md** - Complete instructions

## Quick Steps

### 1. Open Blender
```bash
# Download from: https://www.blender.org/download/
# Version required: 3.0+
```

### 2. Import GLB
- File → Import → glTF 2.0 (.glb/.gltf)
- Select: `app/src/main/assets/models/indian_doctor_lipsync.glb`

### 3. Run Script
- Switch to **Scripting** workspace
- Open: `fix_viseme_morphs.py`
- Select `AvatarHead` mesh in viewport
- Click **Run Script** (▶) or press **Alt+P**

### 4. Verify
Check each viseme in Shape Keys panel:
- **viseme_aa**: Jaw opens down ✓
- **viseme_O**: Gentle round lips ✓
- **viseme_U**: Soft pucker ✓

### 5. Export
- File → Export → glTF 2.0 (.glb)
- ✅ **Check "Shape Keys"** in export settings
- Save as: `indian_doctor_lipsync_fixed.glb`

### 6. Replace Original
```bash
cd app/src/main/assets/models/
cp indian_doctor_lipsync.glb indian_doctor_lipsync_backup.glb
mv indian_doctor_lipsync_fixed.glb indian_doctor_lipsync.glb
```

### 7. Test
```bash
./gradlew clean installDebug
```

## What Changes

| Viseme | Before | After |
|--------|--------|-------|
| viseme_aa | ❌ Upper lip ↑ | ✅ Jaw ↓ |
| viseme_O | ❌ Duck-face | ✅ Gentle round |
| viseme_U | ❌ Excessive pucker | ✅ Soft "oo" |

## Expected Results

✅ Natural jaw opening downward  
✅ Upper lip stays stable  
✅ No smile stretching  
✅ Subtle teeth visibility  
✅ Conversational speech look  

## Rollback
```bash
cd app/src/main/assets/models/
cp indian_doctor_lipsync_backup.glb indian_doctor_lipsync.glb
```

## Compatibility

✅ **Preserved (DO NOT TOUCH):**
- Mesh names (AvatarHead, AvatarTeethUpper, AvatarTeethLower)
- Skeleton/Armature (Head_08, Neck2_07, etc.)
- Animations
- Materials/Textures
- All other visemes (E, I, PP, SS, TH, DD, FF, kk, nn, RR, CH, sil)
- Scene hierarchy
- Object transforms

✅ **Runtime system unchanged:**
- Three.js lip-sync integration
- TtsLipSyncManager
- Viseme name mapping
- Model3DViewer component

## Troubleshooting

### Script fails?
- Ensure AvatarHead is selected
- Check Blender version (3.0+)
- Try manual editing (see full guide)

### Export has no shape keys?
- Verify "Shape Keys" checked in export settings
- Use glTF Binary (.glb) format

### Still looks wrong?
- Adjust `mouth_center` coordinates in script
- Increase vertex selection radius
- Tweak deformation amounts

## Full Documentation
See: **VISEME_MORPH_FIX_GUIDE.md** for detailed instructions

