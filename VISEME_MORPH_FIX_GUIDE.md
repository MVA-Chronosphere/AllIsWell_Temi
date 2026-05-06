# Viseme Morph Target Fix Guide

## Problem Summary

The `indian_doctor_lipsync.glb` avatar has incorrectly authored viseme morph targets that cause:

- ❌ Upper lip stretches upward unnaturally
- ❌ Mouth corners stretch sideways (smile effect)
- ❌ Teeth expose too early
- ❌ Philtrum stretches excessively
- ❌ Mouth looks like a stretched smile instead of natural speech

**Main problematic visemes:** `viseme_aa`, `viseme_O`, `viseme_U`

---

## Solution Overview

We need to edit the shape keys (morph targets) in Blender to change the deformation from:

**BAD (current):**
- Upper lip lifts ↑
- Corners stretch sideways →←
- Smile-like deformation

**GOOD (target):**
- Lower jaw drops ↓
- Lower lip follows naturally
- Upper lip stays stable
- Conversational speech appearance

---

## Prerequisites

1. **Blender 3.0 or newer** (Download: https://www.blender.org/download/)
2. **indian_doctor_lipsync.glb** file (should be in `app/src/main/assets/models/`)
3. **fix_viseme_morphs.py** script (created in project root)

---

## Step-by-Step Instructions

### Option A: Automated Script Fix (Recommended)

#### 1. Open Blender

Launch Blender and create a new project (or use default startup file).

#### 2. Import the GLB Model

1. **File** → **Import** → **glTF 2.0 (.glb/.gltf)**
2. Navigate to: `AllIsWell_Temi/app/src/main/assets/models/`
3. Select: `indian_doctor_lipsync.glb`
4. Click **Import glTF 2.0**

Wait for the import to complete (you should see the doctor avatar in the viewport).

#### 3. Verify the Import

1. In the **Outliner** panel (top-right), look for:
   - `AvatarHead` mesh
   - `AvatarTeethUpper` mesh
   - `AvatarTeethLower` mesh
   - Armature bones (Head_08, Neck2_07, etc.)

2. Select `AvatarHead` in the Outliner

3. Check the **Object Data Properties** panel (green triangle icon on right sidebar)
   - Expand **Shape Keys**
   - You should see: Basis, viseme_aa, viseme_O, viseme_U, etc.

#### 4. Run the Fix Script

1. Switch to **Scripting** workspace (top menu bar)

2. Click **+ New** to create a new text block

3. Open the Python script:
   - Click **Open** (folder icon)
   - Navigate to project root: `AllIsWell_Temi/`
   - Select: `fix_viseme_morphs.py`
   - Click **Open Text**

4. **IMPORTANT:** Make sure `AvatarHead` is selected in the viewport

5. Click **Run Script** button (▶ icon) or press **Alt+P**

6. Check the **Console** area (bottom of Scripting workspace) for output:
   ```
   ✓ Found mesh: AvatarHead
   ✓ Modified XX vertices for viseme_aa
   ✓ Modified XX vertices for viseme_O
   ✓ Modified XX vertices for viseme_U
   ✓ ALL VISEME FIXES COMPLETED SUCCESSFULLY
   ```

#### 5. Verify the Changes

1. Switch back to **Layout** workspace (top menu bar)

2. Select `AvatarHead` in the Outliner

3. In **Object Data Properties** → **Shape Keys**:
   - Select `viseme_aa`
   - Move the **Value** slider to **1.0**
   - **Check:** Lower jaw should open downward, upper lip should stay mostly stable

4. Test `viseme_O`:
   - Set `viseme_aa` value back to **0**
   - Select `viseme_O`
   - Move **Value** slider to **1.0**
   - **Check:** Lips should form gentle rounded "oh" shape, not duck-face

5. Test `viseme_U`:
   - Set `viseme_O` value back to **0**
   - Select `viseme_U`
   - Move **Value** slider to **1.0**
   - **Check:** Lips should pucker gently forward, not collapse

#### 6. Export the Fixed Model

1. **File** → **Export** → **glTF 2.0 (.glb)**

2. **CRITICAL:** In the export settings (right sidebar), ensure these are checked:
   - ✅ **Include** → **Shape Keys**
   - ✅ **Include** → **Armature**
   - ✅ **Include** → **Animations**
   - ✅ **Format:** glTF Binary (.glb)

3. Navigate to: `AllIsWell_Temi/app/src/main/assets/models/`

4. Filename: `indian_doctor_lipsync_fixed.glb` (don't overwrite original yet)

5. Click **Export glTF 2.0**

#### 7. Test in Your App

1. **Backup the original:**
   ```bash
   cd /Users/mva357/AndroidStudioProjects/AllIsWell_Temi/app/src/main/assets/models/
   cp indian_doctor_lipsync.glb indian_doctor_lipsync_backup.glb
   ```

2. **Replace with fixed version:**
   ```bash
   mv indian_doctor_lipsync_fixed.glb indian_doctor_lipsync.glb
   ```

3. **Rebuild and test:**
   - Clean build: `./gradlew clean`
   - Build and install: `./gradlew installDebug`
   - Test the lip-sync on device

4. **Verify:**
   - Upper lip should stay mostly stable
   - Jaw should open naturally downward
   - No smile-stretching effect
   - Teeth visibility should be subtle
   - Vowels (aa, O, U) should look conversational

#### 8. If Satisfied, Commit Changes

If the fix looks good:
```bash
# Remove backup
rm app/src/main/assets/models/indian_doctor_lipsync_backup.glb

# Commit to git
git add app/src/main/assets/models/indian_doctor_lipsync.glb
git commit -m "Fix viseme morph targets: natural jaw opening instead of smile stretch"
```

---

### Option B: Manual Shape Key Editing

If the automated script doesn't work perfectly for your model's topology, you can manually edit the shape keys:

#### 1. Import model (same as Option A, steps 1-3)

#### 2. Select AvatarHead mesh

#### 3. Enter Edit Mode
   - Press **Tab** or click **Edit Mode** in top-left viewport

#### 4. Fix viseme_aa Shape Key

1. In **Object Data Properties** → **Shape Keys**:
   - Select `viseme_aa`
   - Click the **eye icon** to hide selection visibility (optional)

2. **Enable Proportional Editing:**
   - Press **O** or click the proportional editing icon (circle)
   - Set falloff to **Smooth**

3. **Select lower jaw vertices:**
   - Switch to **Vertex Select** mode (press **1**)
   - Select vertices in the lower lip/jaw area
   - You can use **Box Select** (B) or **Circle Select** (C)

4. **Move vertices downward:**
   - Press **G** (Grab/Move)
   - Press **Y** (constrain to Y-axis for vertical movement)
   - Type **-0.025** (moves down 2.5cm)
   - Press **Enter**

5. **Select upper lip vertices:**
   - Clear selection (Alt+A)
   - Select upper lip vertices

6. **Keep upper lip stable:**
   - Press **G**, then **Y**, type **-0.002** (tiny 2mm downward relaxation)
   - Press **Enter**

7. **Test the deformation:**
   - Switch back to **Object Mode** (Tab)
   - Adjust `viseme_aa` Value slider to see the result

#### 5. Fix viseme_O Shape Key

1. Select `viseme_O` in Shape Keys

2. Enter **Edit Mode**

3. Select lip vertices (upper + lower)

4. Move forward:
   - Press **G**, then **Z**, type **0.007** (7mm forward)

5. Select mouth corner vertices

6. Move inward slightly:
   - Press **S** (Scale), then **X** (X-axis), type **0.95** (scale inward 5%)

#### 6. Fix viseme_U Shape Key

1. Select `viseme_U` in Shape Keys

2. Enter **Edit Mode**

3. Select lip vertices

4. Move forward:
   - Press **G**, then **Z**, type **0.009** (9mm forward)

5. Move inward (pucker):
   - Press **S**, then **Shift+Z** (scale on XY plane), type **0.85** (pucker 15%)

#### 7. Export (same as Option A, step 6)

---

## Troubleshooting

### "AvatarHead mesh not found!"

**Solution:**
- Make sure you imported the GLB file correctly
- Check Outliner for objects named `AvatarHead` or `Object_9`
- If using different naming, edit the script line 27-28 to match your mesh name

### "No shape keys found"

**Solution:**
- The GLB may not have embedded shape keys
- Check if the original export included shape keys
- You may need to recreate viseme morphs from scratch

### Script runs but no visible changes

**Solution:**
- Make sure `AvatarHead` is selected before running script
- Check Console output for error messages
- The mouth center coordinates may need adjustment (line 102 in script)
- Try manual editing instead

### Exported GLB has no shape keys

**Solution:**
- In glTF export settings, ensure **Shape Keys** is checked
- Blender version must be 3.0+ for proper shape key export
- Check the exported file in Blender to verify shape keys are present

### Lip-sync still looks wrong after fix

**Possible causes:**
1. **Mouth center coordinates wrong:** Adjust `mouth_center` variable in script (line 102)
2. **Vertex selection radius too small:** Increase the distance check (line 112: `0.08`)
3. **Deformation amounts too strong/weak:** Adjust the movement values (lines 144-159)
4. **Wrong mesh selected:** Ensure you're editing `AvatarHead`, not teeth or other meshes

---

## Technical Details

### What the Script Does

The script modifies vertex positions in three shape keys:

#### viseme_aa (Open mouth "ah")
- **Lower jaw vertices:** Move down 2.5cm (Y-axis: -0.025)
- **Lower lip vertices:** Follow jaw movement
- **Upper lip vertices:** Minimal movement (2mm down for relaxation)
- **Mouth corners:** Stay mostly stable (no sideways stretch)
- **Philtrum:** No movement (preserves natural shape)

#### viseme_O (Rounded "oh")
- **All lip vertices:** Move forward 7mm (Z-axis: +0.007)
- **Vertical compression:** Upper lip down 4mm, lower lip up 4mm
- **Horizontal rounding:** Corners move inward 3mm
- **Result:** Gentle "oh" shape without duck-face

#### viseme_U (Pucker "oo")
- **All lip vertices:** Move forward 9mm (Z-axis: +0.009)
- **Horizontal compression:** Inward 6mm (pucker effect)
- **Minimal vertical compression:** 2mm
- **Result:** Relaxed "oo" pucker with volume

### Coordinate System

Blender uses **Z-up** coordinate system:
- **X-axis:** Left/Right (+ = right, - = left)
- **Y-axis:** Forward/Back (+ = forward/front, - = back)
- **Z-axis:** Up/Down (+ = up, - = down)

**But GLB export may use Y-up:**
- Blender automatically converts during export
- In the script, we use Blender's coordinate system

### Vertex Selection Strategy

The script identifies mouth vertices by:
1. **Distance from mouth center:** Within 8cm radius
2. **Relative position:** Above/below/left/right of center
3. **Region classification:** Lower jaw, lower lip, upper lip, corners, philtrum

This allows targeted deformation without manual vertex selection.

---

## Verification Checklist

After applying the fix, verify:

✅ **viseme_aa:**
- [ ] Lower jaw opens downward naturally
- [ ] Upper lip stays mostly stable
- [ ] No sideways mouth stretching
- [ ] Teeth become subtly visible
- [ ] Looks like saying "ah" conversationally

✅ **viseme_O:**
- [ ] Lips form gentle rounded shape
- [ ] No excessive duck-face protrusion
- [ ] Upper and lower lips move together
- [ ] Looks like saying "oh" naturally

✅ **viseme_U:**
- [ ] Lips pucker forward gently
- [ ] No collapse or sharp point
- [ ] Maintains lip volume
- [ ] Looks like saying "oo" softly

✅ **General:**
- [ ] All other visemes still work (PP, SS, TH, DD, FF, kk, nn, RR, CH, sil)
- [ ] Armature bones not affected
- [ ] Animations still play correctly
- [ ] Teeth meshes not affected
- [ ] Materials and textures intact

---

## Rollback Instructions

If the fix makes things worse:

```bash
cd /Users/mva357/AndroidStudioProjects/AllIsWell_Temi/app/src/main/assets/models/

# Restore original
cp indian_doctor_lipsync_backup.glb indian_doctor_lipsync.glb

# Rebuild
cd ../../../..
./gradlew clean installDebug
```

---

## Advanced Tweaking

If the automated values don't perfectly match your model:

### Adjust Mouth Center Location

Edit `fix_viseme_morphs.py` line 102:
```python
mouth_center = Vector((0.0, 3.15, 0.2))  # X, Y, Z coordinates
```

To find the correct coordinates:
1. In Blender, select `AvatarHead`
2. Enter **Edit Mode**
3. Select a vertex at the mouth center
4. Check **Vertex** panel (press **N** to show sidebar)
5. Copy the **X, Y, Z** values

### Adjust Deformation Amounts

In the script functions, tweak these values:

**viseme_aa** (lines 144-159):
```python
jaw_drop = 0.025 * jaw_influence  # Increase/decrease jaw opening
new_pos.y -= 0.002 * upper_influence  # Upper lip movement
```

**viseme_O** (lines 218-230):
```python
forward_amount = 0.007 * lip_influence  # Lip protrusion
new_pos.y -= 0.004 * lip_influence  # Vertical compression
```

**viseme_U** (lines 287-304):
```python
forward_amount = 0.009 * lip_influence  # Pucker protrusion
inward_amount = 0.006 * lip_influence  # Horizontal compression
```

---

## Support

If you encounter issues:

1. **Check Blender version:** Must be 3.0 or newer
2. **Verify GLB structure:** Ensure mesh names match (AvatarHead, etc.)
3. **Review Console output:** Look for error messages in Blender's Python Console
4. **Try manual editing:** If script fails, use Option B
5. **Check coordinates:** Mouth center location may need adjustment for your model

---

## Summary

This guide fixes the unnatural upper lip stretching in the `indian_doctor_lipsync.glb` avatar by reauthoring three key viseme morph targets. The fix changes the mouth deformation from a smile-like stretch to natural jaw opening, making the lip-sync look realistic during conversational speech.

**Expected outcome:**
- ✅ Natural jaw opening for vowels
- ✅ Stable upper lip
- ✅ Realistic lip separation
- ✅ Subtle teeth visibility
- ✅ Conversational speech appearance

**File modified:** `app/src/main/assets/models/indian_doctor_lipsync.glb`

**Compatibility:** Runtime Three.js lip-sync system remains unchanged and fully compatible.

