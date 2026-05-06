# Quick Deploy Checklist — Lip Sync Fix

## Changes Applied
✅ **File:** `app/src/main/java/com/example/alliswelltemi/ui/components/TemiComponents.kt`

### 5 Precise Parameter Changes:

| Parameter | Old Value | New Value | Location | Effect |
|-----------|-----------|-----------|----------|--------|
| `viseme_aa` VISEME_MAX | 0.16 | **0.28** | Line 1045 | 75% increase in jaw opening range |
| `viseme_E` VISEME_MAX | 0.02 | **0.04** | Line 1046 | 2x smile morph (doubled flexibility) |
| `viseme_I` VISEME_MAX | 0.01 | **0.03** | Line 1047 | 3x smile morph (tripled flexibility) |
| `viseme_O` VISEME_MAX | 0.12 | **0.22** | Line 1048 | 83% increase in rounded-mouth shape |
| `viseme_U` VISEME_MAX | 0.10 | **0.20** | Line 1049 | 2x increase in pursed-lips shape |
| `viseme_aa` damping | 0.55 | **0.75** | Line 1117 | 36% less suppression (fuller mouth open) |
| Vowel suppression | `0.05 / 0.08` (static) | **Dynamic 0.2–1.0** | Lines 1179–1181 | Gradual smile fade (natural blending) |
| `viseme_aa` clamp | 0.16 | **0.28** | Line 1192 | Aligns with VISEME_MAX (no artificial limit) |
| Global morph clamp | 0.18 | **0.25** | Line 1196 | Calibrated to max viseme values |
| Teeth opacity | `mouthOpen * 1.2`, cap 0.45 | **`mouthOpen * 2.0`, cap 0.65** | Line 1205 | 2x scaling + 44% brighter |

---

## What This Fixes
- ❌ Upper lip only → ✅ Full mouth opening (jaw, lips, middle)
- ❌ Broken teeth display → ✅ Visible teeth during natural speech
- ❌ Over-suppressed smile → ✅ Natural smile morphs during moderate opening
- ❌ Stiff mouth → ✅ Smooth, natural lip sync

---

## Build Steps
```bash
cd /Users/mva357/AndroidStudioProjects/AllIsWell_Temi

# 1. Sync Gradle (refreshes cache and dependencies)
./gradlew sync

# 2. Clean old builds
./gradlew clean

# 3. Build debug APK
./gradlew build

# 4. Install to device (Temi robot or emulator)
adb connect <TEMI_IP>:5555
adb install -r app/build/outputs/apk/debug/AlliswellTemi-debug.apk

# 5. Monitor logs for lip-sync verification
adb logcat | grep "👄 Active visemes"
```

---

## Verification
After install, test with:
- **Vowel-heavy TTS:** "Amazing audio quality"
- **Watch for:**
  - Jaw drops visibly on /ə/ /ɑ/ sounds
  - Teeth become visible when mouth opens
  - Smile activates on /i/ /e/ (even with moderate jaw open)
  - No sudden shape changes (smooth LERP)

---

## Rollback (If Needed)
Revert only these lines:
```javascript
// Line 1045-1049: VISEME_MAX
'viseme_aa':  0.16,   // (originally 0.16)
'viseme_E':   0.02,   // (originally 0.02)
'viseme_I':   0.01,   // (originally 0.01)
'viseme_O':   0.12,   // (originally 0.12)
'viseme_U':   0.10,   // (originally 0.10)

// Line 1117
finalIntensity *= 0.55;  // (originally 0.55)

// Lines 1177-1183 (revert to simple suppression)
if (openAmount > 0.08) {
    currentWeights['viseme_I'] *= 0.05;
    currentWeights['viseme_E'] *= 0.08;
    currentWeights['viseme_SS'] *= 0.4;
    currentWeights['viseme_CH'] *= 0.5;
}

// Line 1192
currentWeights['viseme_aa'] = Math.min(currentWeights['viseme_aa'], 0.16);

// Line 1196
for (const name of OCULUS_VISEMES) {
  currentWeights[name] = Math.max(0, Math.min(currentWeights[name], 0.18));
}

// Line 1205
const teethOpacity = Math.min(0.45, mouthOpen * 1.2);
```

---

## File: LIP_SYNC_FIX_SUMMARY.md
Detailed analysis with before/after code blocks and visual improvement checklist.

---

**Last updated:** May 6, 2026 | **Status:** Ready for deployment

