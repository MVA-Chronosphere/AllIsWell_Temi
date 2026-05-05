# Blend Shapes Traverse Error Fix

## Issue
**Error:** `scene.traverse is not a function`

**Location:** AvatarController.kt line 182

**Root Cause:** The code was incorrectly trying to call `scene.traverse()` directly on `viewer.model`, which is a GLTF loader result object, not a Three.js Scene object.

## Solution
Updated the JavaScript code in `AvatarController.kt` to correctly access the Three.js scene through `viewer.model.scene`:

### Before (Incorrect)
```javascript
var scene = viewer.model;  // This is a GLTF object, not a Scene
var mesh = null;

scene.traverse(function(child) {  // ❌ GLTF object doesn't have traverse()
    // ...
});
```

### After (Correct)
```javascript
var gltf = viewer.model;
var scene = gltf.scene;  // ✅ Access the actual Three.js Scene

if (!scene || typeof scene.traverse !== 'function') {
    console.error('❌ Scene not available or invalid structure');
    return;
}

scene.traverse(function(child) {  // ✅ Scene has traverse()
    // ...
});
```

## Technical Details

### Model-Viewer Internal Structure
- **viewer.model** = GLTF loader result object containing:
  - `scene`: The actual Three.js Scene object
  - `scenes`: Array of scenes
  - `animations`: Array of animations
  - `cameras`: Array of cameras

- **viewer.model.scene** = Three.js Scene object with methods:
  - `traverse(callback)`: Recursively iterate through all children
  - `add(object)`: Add objects to scene
  - `remove(object)`: Remove objects from scene

### Morph Targets Access Pattern
```javascript
// 1. Get GLTF result
var gltf = viewer.model;

// 2. Access Three.js scene
var scene = gltf.scene;

// 3. Find mesh with morph targets
var mesh = null;
scene.traverse(function(child) {
    if (child.isMesh && child.morphTargetInfluences) {
        mesh = child;
    }
});

// 4. Access morph target dictionary
var morphDict = mesh.morphTargetDictionary;
// Example: { "jawOpen": 0, "mouthOpen": 1 }

// 5. Update blend shapes
var jawIndex = morphDict['jawOpen'];
mesh.morphTargetInfluences[jawIndex] = 0.5;
```

## Impact
- **Fixed:** Lip sync blend shape updates now work correctly
- **Fixed:** Console errors no longer spam during TTS playback
- **Improved:** Error handling with validation checks for scene structure

## Testing
After rebuilding and deploying:
1. Trigger TTS speech from any screen
2. Check logcat for blend shape update logs: `👅 Mouth: jaw=X, mouth=Y`
3. Verify no more `scene.traverse is not a function` errors
4. Observe avatar lip sync animation during speech

## Related Files
- `/app/src/main/java/com/example/alliswelltemi/utils/AvatarController.kt` (lines 165-225)
- `/app/src/main/java/com/example/alliswelltemi/utils/TtsLipSyncManager.kt`
- `/app/src/main/java/com/example/alliswelltemi/ui/components/TemiComponents.kt` (3D avatar component)

## References
- [Model-Viewer API Documentation](https://modelviewer.dev/docs/index.html)
- [Three.js Scene Documentation](https://threejs.org/docs/#api/en/scenes/Scene)
- [Three.js Morph Targets](https://threejs.org/docs/#api/en/core/BufferGeometry.morphAttributes)

