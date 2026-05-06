# Avatar Mesh Structure Reference

## indian_doctor_lipsync.glb - Complete Mesh Hierarchy

### 🎭 Facial Components

#### AvatarHead (Primary Face Mesh)
```
Name: AvatarHead
Type: SkinnedMesh
Purpose: Contains all facial geometry including lips, cheeks, nose, eyes
Morph Targets: 15 Oculus visemes

Required Morph Targets (Shape Keys):
├── viseme_sil  (silence/neutral)
├── viseme_PP   (P, B, M - lip closure)
├── viseme_FF   (F, V - lips against teeth)
├── viseme_TH   (TH - tongue between teeth)
├── viseme_DD   (T, D, N - tongue to roof)
├── viseme_kk   (K, G - back of throat)
├── viseme_CH   (CH, J, SH - soft palate)
├── viseme_SS   (S, Z - lips slightly apart)
├── viseme_nn   (N, NG - nasal)
├── viseme_RR   (R - tongue curl)
├── viseme_aa   (AH - open jaw)
├── viseme_E    (EH - slight smile)
├── viseme_I    (EE - wide smile)
├── viseme_O    (OH - round lips)
└── viseme_U    (OO - pucker lips)
```

**Vertex Bounds (Approximate)**
- X: -0.120 → 0.120 (width)
- Y: 2.800 → 3.100 (height - chin to forehead)
- Z: -0.050 → 0.200 (depth - back of head to nose tip)

**Lip Region** (subset of AvatarHead vertices)
- X: -0.055 → 0.055 (mouth width)
- Y: 2.860 → 2.950 (upper/lower lip)
- Z: 0.100 → 0.190 (lip protrusion)

---

#### AvatarTeethUpper (Upper Teeth)
```
Name: AvatarTeethUpper
Type: Mesh
Purpose: Upper teeth geometry, visible when mouth opens
Material: Transparent (controlled dynamically)
```

**Vertex Bounds**
- X: -0.055 → 0.055
- Y: 2.871 → 2.920 (upper gum line)
- Z: 0.035 → 0.195 (depth)

**Dynamic Properties**
- `opacity`: 0.0 (closed mouth) → 1.0 (open mouth)
- `transparent`: true when opacity < 0.9
- `renderOrder`: 30 (renders after face to prevent z-fighting)
- `position.z`: +0.02 offset (moved forward from lips)

---

#### AvatarTeethLower (Lower Teeth)
```
Name: AvatarTeethLower
Type: Mesh
Purpose: Lower teeth geometry, visible when mouth opens
Material: Transparent (controlled dynamically)
```

**Vertex Bounds**
- X: -0.053 → 0.053
- Y: 2.845 → 2.902 (lower gum line)
- Z: 0.036 → 0.190 (depth)

**Dynamic Properties**
- Same as AvatarTeethUpper
- Coupled visibility (both teeth use same opacity calculation)

---

### 👀 Eye Components

#### AvatarLeftEyeball
```
Name: AvatarLeftEyeball
Type: Mesh
Purpose: Left eye sphere
```

#### AvatarRightEyeball
```
Name: AvatarRightEyeball
Type: Mesh
Purpose: Right eye sphere
```

#### AvatarLeftCornea
```
Name: AvatarLeftCornea
Type: Mesh (transparent overlay)
Purpose: Left eye reflection/highlights
```

#### AvatarRightCornea
```
Name: AvatarRightCornea
Type: Mesh (transparent overlay)
Purpose: Right eye reflection/highlights
```

---

### 🦴 Skeleton Structure

#### Head_08 (Head Bone)
```
Name: Head_08
Type: Bone
Purpose: Controls head rotation and position
Parent: Neck2_07

Animation Uses:
- Subtle head tilt during speech
- Jaw coupling (tilts back when mouth opens wide)
- Idle breathing animation
```

**Rest Pose**
- Rotation: Quaternion stored in restPose object
- Coupled to viseme_aa, viseme_O for natural jaw movement

**Dynamic Tilt**
```javascript
jawInfluence = (viseme_aa * 0.6) + (viseme_O * 0.4) + (viseme_E * 0.2)
if (jawInfluence > 0.02) {
  tiltBack = jawInfluence * 0.06 radians  // ~3.4 degrees max
}
```

---

#### Neck2_07 (Neck Bone)
```
Name: Neck2_07
Type: Bone
Purpose: Supports head bone
Parent: Upper spine hierarchy
Children: Head_08
```

---

#### HeadTop_End_011 (Head Top Marker)
```
Name: HeadTop_End_011
Type: Bone (end effector)
Purpose: Marks top of head for IK/measurements
Parent: Head_08
```

---

### 💪 Arm Bones (Idle Pose Animation)

#### Right Arm Chain
```
RightArm_039 (UpperArm)
└── RightForeArm_040
    └── RightHand_043
```

**Idle Pose Configuration**
```javascript
rightUpper: [1.36, 0, -0.12] // Elbow bent, arm at side
rightFore:  [0.10, 0, 0]
rightHand:  [0, 0, 0]
```

#### Left Arm Chain
```
LeftArm_013 (UpperArm)
└── LeftForeArm_014
    └── LeftHand_017
```

**Idle Pose Configuration**
```javascript
leftUpper: [1.36, 0, 0.12] // Elbow bent, arm at side
leftFore:  [0.10, 0, 0]
leftHand:  [0, 0, 0]
```

---

## Camera Framing Configuration

### Current Setup (Chest-Up Portrait)
```javascript
Target (lookAt point):
  X: 0.05
  Y: 3.04    // Face center
  Z: 0.12

Camera Position (spherical coordinates):
  Yaw:    12.54°  // Slight right angle
  Pitch:  88.48°  // Nearly horizontal
  Radius: 2.0     // Distance from target

Field of View: 40°
```

**Visible Area**
- Head (full)
- Shoulders (partial)
- Upper chest (partial)
- Total height: ~1.5 meters from chest to top of head

---

## Material Properties

### Face Material (AvatarHead)
```javascript
side: THREE.FrontSide
frustumCulled: false
castShadow: true
receiveShadow: true
```

### Teeth Materials (Upper & Lower)
```javascript
side: THREE.DoubleSide            // Visible from both sides
transparent: true                  // Enables opacity animation
opacity: 0.0 (default)             // Invisible when mouth closed
depthWrite: true                   // Writes to depth buffer
renderOrder: 30                    // Renders after face mesh
```

---

## Morph Target Influence Ranges

### Calibrated for Natural Movement
```javascript
VISEME_MAX = {
  'viseme_sil': 0.00,  // Neutral (no movement)
  'viseme_PP':  0.42,  // Lip closure
  'viseme_FF':  0.35,  // Lip-teeth contact
  'viseme_TH':  0.35,  // Tongue protrusion
  'viseme_DD':  0.35,  // Tongue-roof contact
  'viseme_kk':  0.28,  // Back of throat
  'viseme_CH':  0.32,  // Soft palate
  'viseme_SS':  0.22,  // Slight opening
  'viseme_nn':  0.30,  // Nasal
  'viseme_RR':  0.30,  // Tongue curl
  'viseme_aa':  0.45,  // Wide open jaw
  'viseme_E':   0.10,  // Slight smile
  'viseme_I':   0.08,  // Wide smile (also idle)
  'viseme_O':   0.40,  // Round lips
  'viseme_U':   0.38,  // Pucker
}
```

**Idle Smile Weight**: 0.09 (applied to viseme_I when silent)

---

## Animation Timeline

### Per-Frame Operations (60 FPS)
```
1. Update idle gestures (breathing, arm sway)
   - Head: sin wave breathing (0.8 Hz)
   - Arms: subtle sway (0.6 Hz)

2. Apply smoothed morph targets
   - Current weights LERP toward target weights
   - LERP_SPEED = 0.18 (smooth transitions)

3. Update teeth opacity
   - Calculate mouth openness from vowel visemes
   - Opacity = min(1.0, mouthOpen * 8)

4. Apply head tilt coupling
   - If jaw influence > 0.02, tilt head back
   - Smooth interpolation (slerp factor 0.1)

5. Render scene
   - Update mixer (animation clips)
   - Render with Three.js
```

---

## Blender Export Settings

### Required for Proper Lip-Sync
```
Format: glTF 2.0 (.glb)
Include:
  ✅ Geometry
  ✅ Materials
  ✅ Shape Keys (Morph Targets)
  ✅ Armature (Skeleton)
  ✅ Animations
  
Shape Keys Options:
  ✅ Export Shape Keys
  ✅ Normalize Weights
  
Naming Convention:
  - Shape keys MUST be named: viseme_aa, viseme_O, etc.
  - Meshes MUST be named: AvatarHead, AvatarTeethUpper, AvatarTeethLower
  - Bones MUST be named: Head_08, Neck2_07
```

---

## Verification Script (Blender Python)

### Check Mesh Structure
```python
import bpy

# Check if AvatarHead exists
if 'AvatarHead' in bpy.data.objects:
    head = bpy.data.objects['AvatarHead']
    print(f"✓ AvatarHead found: {len(head.data.vertices)} vertices")
    
    # Check shape keys
    if head.data.shape_keys:
        shape_keys = head.data.shape_keys.key_blocks.keys()
        print(f"✓ Shape keys: {len(shape_keys)}")
        
        # Check required visemes
        required = ['viseme_aa', 'viseme_E', 'viseme_I', 'viseme_O', 'viseme_U',
                    'viseme_PP', 'viseme_SS', 'viseme_TH', 'viseme_DD', 'viseme_FF',
                    'viseme_kk', 'viseme_nn', 'viseme_RR', 'viseme_CH', 'viseme_sil']
        
        missing = [v for v in required if v not in shape_keys]
        if missing:
            print(f"❌ Missing visemes: {missing}")
        else:
            print(f"✓ All 15 Oculus visemes present")
    else:
        print("❌ No shape keys found")
else:
    print("❌ AvatarHead mesh not found")

# Check teeth
if 'AvatarTeethUpper' in bpy.data.objects:
    print("✓ AvatarTeethUpper found")
else:
    print("❌ AvatarTeethUpper not found")

if 'AvatarTeethLower' in bpy.data.objects:
    print("✓ AvatarTeethLower found")
else:
    print("❌ AvatarTeethLower not found")

# Check bones
armature = bpy.data.armatures[0] if bpy.data.armatures else None
if armature:
    bones = [b.name for b in armature.bones]
    if 'Head_08' in bones:
        print("✓ Head_08 bone found")
    else:
        print("❌ Head_08 bone not found")
    
    if 'Neck2_07' in bones:
        print("✓ Neck2_07 bone found")
    else:
        print("❌ Neck2_07 bone not found")
```

---

## Troubleshooting Reference

### Symptom: "No morph meshes found"
**Cause**: AvatarHead mesh missing or has no shape keys
**Fix**: Re-export from Blender with shape keys enabled

### Symptom: "Teeth meshes found: 0"
**Cause**: Teeth meshes renamed or missing
**Fix**: Ensure meshes are named exactly "AvatarTeethUpper" and "AvatarTeethLower"

### Symptom: "Head bone: NOT FOUND"
**Cause**: Head bone renamed or missing from armature
**Fix**: Rename bone to "Head_08" or update detection logic

### Symptom: Lips move but teeth always visible
**Cause**: Teeth opacity calculation not working
**Fix**: Check teethMeshes array is populated, verify material.transparent = true

### Symptom: Lips barely move
**Cause**: VISEME_MAX values too low
**Fix**: Increase values in VISEME_MAX object (max 0.7 recommended)

### Symptom: Lips over-exaggerated
**Cause**: VISEME_MAX values too high
**Fix**: Decrease values in VISEME_MAX object (min 0.1 recommended)

---

## Summary

### Critical Naming Requirements
✅ **AvatarHead** - Face mesh with 15 viseme morph targets  
✅ **AvatarTeethUpper** - Upper teeth geometry  
✅ **AvatarTeethLower** - Lower teeth geometry  
✅ **Head_08** - Head bone for rotation  
✅ **Neck2_07** - Neck bone supporting head  

### Morph Target Requirements
✅ 15 Oculus visemes (viseme_aa, viseme_O, etc.)  
✅ Normalized weights (0.0 - 1.0 range)  
✅ Applied to AvatarHead mesh  

### Material Requirements
✅ Face: FrontSide rendering  
✅ Teeth: DoubleSide + Transparent + RenderOrder 30  
✅ Teeth: Z-offset +0.02 to prevent z-fighting  

---

**Document Version**: 1.0  
**Last Updated**: May 6, 2026  
**Model File**: indian_doctor_lipsync.glb  
**Verified On**: AllIsWell_Temi Android App

