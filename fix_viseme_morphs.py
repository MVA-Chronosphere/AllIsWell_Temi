import bpy
from mathutils import Vector

def get_avatar_head_mesh():
    """Find the AvatarHead mesh object"""
    for obj in bpy.data.objects:
        if obj.type == 'MESH' and obj.name in ['AvatarHead', 'Object_9']:
            return obj
    print("ERROR: AvatarHead mesh not found!")
    return None

def ensure_basis_shape():
    """Ensure Basis shape key exists and is active"""
    obj = bpy.context.object
    if not obj.data.shape_keys:
        obj.shape_key_add(name='Basis', from_mix=False)
    return obj.data.shape_keys.key_blocks.get('Basis')

def get_or_create_shape_key(name):
    """Get existing shape key or create new one"""
    obj = bpy.context.object
    if not obj.data.shape_keys:
        obj.shape_key_add(name='Basis', from_mix=False)

    shape_key = obj.data.shape_keys.key_blocks.get(name)
    if not shape_key:
        shape_key = obj.shape_key_add(name=name, from_mix=False)
        print(f"Created new shape key: {name}")
    return shape_key

def reset_shape_key_to_basis(shape_key_name):
    """Reset a shape key to match the basis (neutral) shape"""
    obj = bpy.context.object
    basis = obj.data.shape_keys.key_blocks.get('Basis')
    target = obj.data.shape_keys.key_blocks.get(shape_key_name)

    if not basis or not target:
        print(f"ERROR: Cannot reset {shape_key_name} - basis or target not found")
        return False

    # Copy basis vertex positions to target
    for i, vert in enumerate(basis.data):
        target.data[i].co = vert.co.copy()

    print(f"✓ Reset {shape_key_name} to basis")
    return True

def fix_viseme_aa():
    """
    Fix viseme_aa to use natural jaw opening instead of upper lip stretching

    TARGET: Natural "ah" mouth opening
    - Lower jaw opens downward (2-3cm)
    - Lower lip follows jaw naturally
    - Upper lip remains mostly stable (slight relaxation allowed)
    - Teeth become subtly visible
    - NO sideways mouth corner stretching
    - NO upward upper lip movement
    """
    obj = bpy.context.object
    mesh = obj.data

    # Get or create shape key
    shape_key = get_or_create_shape_key('viseme_aa')
    basis = obj.data.shape_keys.key_blocks.get('Basis')

    if not basis:
        print("ERROR: Basis shape key not found!")
        return False

    print("\n" + "="*60)
    print("FIXING viseme_aa")
    print("="*60)

    # Reset to basis first
    reset_shape_key_to_basis('viseme_aa')

    # Ensure we're in object mode for shape key editing
    if bpy.context.mode != 'OBJECT':
        bpy.ops.object.mode_set(mode='OBJECT')

    # Identify mouth region vertices by proximity to expected mouth center
    # Adjust these coordinates based on your model's mouth position
    mouth_center = Vector((0.0, 3.15, 0.2))  # Approximate - adjust if needed

    modified_count = 0

    for i, vert in enumerate(mesh.vertices):
        basis_pos = Vector(basis.data[i].co)
        distance_to_mouth = (basis_pos - mouth_center).length

        # Only affect vertices near the mouth (within ~8cm radius)
        if distance_to_mouth > 0.08:
            continue

        # Calculate relative position from mouth center
        rel_x = basis_pos.x - mouth_center.x
        rel_y = basis_pos.y - mouth_center.y
        rel_z = basis_pos.z - mouth_center.z

        # Identify vertex regions
        is_lower_lip = rel_y < -0.01 and abs(rel_z - 0.2) < 0.02
        is_upper_lip = rel_y > 0.01 and rel_y < 0.03 and abs(rel_z - 0.2) < 0.02
        is_jaw = rel_y < -0.02
        is_mouth_corner = abs(rel_x) > 0.02 and abs(rel_y) < 0.02
        is_philtrum = abs(rel_x) < 0.01 and rel_y > 0.01 and rel_y < 0.04

        # Start with basis position
        new_pos = basis_pos.copy()

        # LOWER JAW & LOWER LIP: Move downward significantly
        if is_jaw or is_lower_lip:
            jaw_influence = 1.0 - min(distance_to_mouth / 0.05, 1.0)
            jaw_drop = 0.025 * jaw_influence  # 2.5cm max drop
            new_pos.y -= jaw_drop
            # Slight forward movement for natural lip curl
            new_pos.z += 0.003 * jaw_influence
            modified_count += 1

        # UPPER LIP: Keep mostly stable, allow tiny relaxation
        elif is_upper_lip:
            upper_influence = 1.0 - min(distance_to_mouth / 0.03, 1.0)
            # Very subtle downward movement (max 2mm)
            new_pos.y -= 0.002 * upper_influence
            modified_count += 1

        # MOUTH CORNERS: Minimal movement, NO sideways stretching
        elif is_mouth_corner:
            corner_influence = 1.0 - min(distance_to_mouth / 0.04, 1.0)
            # Slight downward follow (max 5mm)
            new_pos.y -= 0.005 * corner_influence
            # NO horizontal movement
            modified_count += 1

        # PHILTRUM: Keep stable to prevent upper lip stretching
        elif is_philtrum:
            # No movement - preserve natural philtrum shape
            pass

        # Apply the new position
        shape_key.data[i].co = new_pos

    print(f"✓ Modified {modified_count} vertices for viseme_aa")
    print("  - Lower jaw opens downward")
    print("  - Upper lip remains stable")
    print("  - Natural 'ah' mouth shape")

    return True

def fix_viseme_O():
    """
    Fix viseme_O to create natural rounded lips without over-protrusion

    TARGET: Natural conversational "oh"
    - Lips form gentle round shape
    - Upper and lower lips move slightly forward (5-8mm)
    - Slight vertical compression
    - NO duck-face effect
    - NO excessive rounding
    """
    obj = bpy.context.object
    mesh = obj.data

    shape_key = get_or_create_shape_key('viseme_O')
    basis = obj.data.shape_keys.key_blocks.get('Basis')

    if not basis:
        print("ERROR: Basis shape key not found!")
        return False

    print("\n" + "="*60)
    print("FIXING viseme_O")
    print("="*60)

    # Reset to basis first
    reset_shape_key_to_basis('viseme_O')

    mouth_center = Vector((0.0, 3.15, 0.2))
    modified_count = 0

    for i, vert in enumerate(mesh.vertices):
        basis_pos = Vector(basis.data[i].co)
        distance_to_mouth = (basis_pos - mouth_center).length

        if distance_to_mouth > 0.06:
            continue

        rel_x = basis_pos.x - mouth_center.x
        rel_y = basis_pos.y - mouth_center.y
        rel_z = basis_pos.z - mouth_center.z

        is_lip = abs(rel_y) < 0.02 and abs(rel_z - 0.2) < 0.03
        is_upper_lip = rel_y > 0.00 and is_lip
        is_lower_lip = rel_y < 0.00 and is_lip

        new_pos = basis_pos.copy()

        if is_lip:
            lip_influence = 1.0 - min(distance_to_mouth / 0.04, 1.0)

            # Forward protrusion (subtle, 5-8mm max)
            forward_amount = 0.007 * lip_influence
            new_pos.z += forward_amount

            # Vertical compression toward center (creates rounding)
            if is_upper_lip:
                new_pos.y -= 0.004 * lip_influence
            elif is_lower_lip:
                new_pos.y += 0.004 * lip_influence

            # Horizontal rounding (move corners inward slightly)
            if abs(rel_x) > 0.01:
                inward_amount = 0.003 * lip_influence
                if rel_x > 0:
                    new_pos.x -= inward_amount
                else:
                    new_pos.x += inward_amount

            modified_count += 1

        shape_key.data[i].co = new_pos

    print(f"✓ Modified {modified_count} vertices for viseme_O")
    print("  - Gentle rounded lip shape")
    print("  - Subtle forward movement")
    print("  - Natural 'oh' expression")

    return True

def fix_viseme_U():
    """
    Fix viseme_U to create natural lip pucker without collapse

    TARGET: Gentle "oo" shape
    - Lips form relaxed pucker
    - Moderate forward protrusion (8-10mm)
    - Lips compress horizontally
    - Maintain lip volume
    - NO sharp point
    - NO collapse
    """
    obj = bpy.context.object
    mesh = obj.data

    shape_key = get_or_create_shape_key('viseme_U')
    basis = obj.data.shape_keys.key_blocks.get('Basis')

    if not basis:
        print("ERROR: Basis shape key not found!")
        return False

    print("\n" + "="*60)
    print("FIXING viseme_U")
    print("="*60)

    # Reset to basis first
    reset_shape_key_to_basis('viseme_U')

    mouth_center = Vector((0.0, 3.15, 0.2))
    modified_count = 0

    for i, vert in enumerate(mesh.vertices):
        basis_pos = Vector(basis.data[i].co)
        distance_to_mouth = (basis_pos - mouth_center).length

        if distance_to_mouth > 0.06:
            continue

        rel_x = basis_pos.x - mouth_center.x
        rel_y = basis_pos.y - mouth_center.y
        rel_z = basis_pos.z - mouth_center.z

        is_lip = abs(rel_y) < 0.025 and abs(rel_z - 0.2) < 0.03

        new_pos = basis_pos.copy()

        if is_lip:
            lip_influence = 1.0 - min(distance_to_mouth / 0.04, 1.0)

            # Forward protrusion (moderate, 8-10mm)
            forward_amount = 0.009 * lip_influence
            new_pos.z += forward_amount

            # Horizontal compression (pucker effect)
            if abs(rel_x) > 0.005:
                inward_amount = 0.006 * lip_influence
                if rel_x > 0:
                    new_pos.x -= inward_amount
                else:
                    new_pos.x += inward_amount

            # Minimal vertical compression
            compression = 0.002 * lip_influence
            if rel_y > 0:
                new_pos.y -= compression
            else:
                new_pos.y += compression

            modified_count += 1

        shape_key.data[i].co = new_pos

    print(f"✓ Modified {modified_count} vertices for viseme_U")
    print("  - Relaxed lip pucker")
    print("  - Moderate forward protrusion")
    print("  - Natural 'oo' shape")

    return True

def main():
    """Main execution function"""
    print("\n" + "="*60)
    print("VISEME MORPH TARGET FIX SCRIPT")
    print("="*60)

    # Ensure we're in object mode first
    if bpy.context.mode != 'OBJECT':
        try:
            bpy.ops.object.mode_set(mode='OBJECT')
        except:
            print("WARNING: Could not switch to OBJECT mode")

    # Find the AvatarHead mesh
    avatar_head = get_avatar_head_mesh()
    if not avatar_head:
        print("\nERROR: Could not find AvatarHead mesh!")
        print("Please ensure indian_doctor_lipsync.glb is imported.")
        return

    # Select and make active - use direct access instead of operators
    for obj in bpy.context.scene.objects:
        obj.select_set(False)
    avatar_head.select_set(True)
    bpy.context.view_layer.objects.active = avatar_head

    print(f"\n✓ Found mesh: {avatar_head.name}")

    # Check for shape keys
    if not avatar_head.data.shape_keys:
        print("\nWARNING: No shape keys found. Creating Basis...")
        avatar_head.shape_key_add(name='Basis', from_mix=False)

    # List existing shape keys
    if avatar_head.data.shape_keys:
        print("\nExisting shape keys:")
        for key in avatar_head.data.shape_keys.key_blocks:
            print(f"  - {key.name}")

    # Fix the problematic visemes
    print("\n" + "="*60)
    print("STARTING VISEME FIXES")
    print("="*60)

    success = True

    if not fix_viseme_aa():
        print("✗ Failed to fix viseme_aa")
        success = False

    if not fix_viseme_O():
        print("✗ Failed to fix viseme_O")
        success = False

    if not fix_viseme_U():
        print("✗ Failed to fix viseme_U")
        success = False

    if success:
        print("\n" + "="*60)
        print("✓ ALL VISEME FIXES COMPLETED SUCCESSFULLY")
        print("="*60)
        print("\nNEXT STEPS:")
        print("1. File → Export → glTF 2.0 (.glb)")
        print("2. IMPORTANT: Check 'Shape Keys' in export settings")
        print("3. Export as: indian_doctor_lipsync_fixed.glb")
        print("4. Test in your app")
        print("5. If satisfied, replace original file")
    else:
        print("\n✗ Some fixes failed. Check errors above.")

if __name__ == "__main__":
    main()

