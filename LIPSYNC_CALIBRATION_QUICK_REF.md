# Lip-Sync Calibration Quick Reference

## 🎯 Changes at a Glance

| Parameter | Old Value | New Value | Reduction | Impact |
|-----------|-----------|-----------|-----------|--------|
| **LERP_SPEED** | 0.18 | 0.10 | 44% | Smoother transitions |
| **viseme_aa** | 0.45 | 0.28 | 38% | Less jaw drop |
| **viseme_E** | 0.10 | 0.04 | 60% | Minimal smile |
| **viseme_I** | 0.08 | 0.03 | 62% | No smile stretch |
| **viseme_PP** | 0.42 | 0.30 | 29% | Natural lip closure |
| **viseme_FF** | 0.35 | 0.22 | 37% | Subtle lip bite |
| **viseme_O** | 0.40 | 0.22 | 45% | Natural rounding |
| **Teeth multiplier** | 8 | 3 | 62% | Gradual appearance |
| **Idle smile** | 0.09 | REMOVED | 100% | Neutral face at rest |

---

## 🔧 New Systems Added

### 1. Vowel Conflict Suppression
```javascript
// Only strongest vowel remains active
// Others reduced by 85%
vowelVisemes.forEach(v => {
  if (v !== strongest) {
    targetWeights[v] *= 0.15;
  }
});
```

### 2. Smile Clamping During Speech
```javascript
// Prevents smile during jaw open
if (jawOpen > 0.15) {
  currentWeights['viseme_I'] *= 0.2;
  currentWeights['viseme_E'] *= 0.3;
}
```

---

## ✅ Problem → Solution Map

| Problem | Root Cause | Solution Applied |
|---------|------------|------------------|
| Upper lip stretches | viseme_E/I too high | Reduced to 0.04/0.03 |
| Smile during speech | Idle smile morph active | Removed completely |
| Vowels blend together | Multiple vowels active | Suppress non-dominant vowels by 85% |
| Rubber-face effect | High viseme weights | Reduced all VISEME_MAX by 20-60% |
| Smile during jaw open | No anti-correlation | Added smile clamping when jawOpen > 0.15 |
| Snappy transitions | Fast LERP_SPEED (0.18) | Reduced to 0.10 |
| Overexposed teeth | High multiplier (8) | Reduced to 3 |

---

## 📊 Verification Console Output

**Expected logs during speech:**
```
AA: 0.25 I: 0.01 E: 0.02  ← Good: I and E stay very low
AA: 0.00 I: 0.00 E: 0.00  ← Good: All zeroed when silent
AA: 0.18 I: 0.00 E: 0.03  ← Good: No smile during vowels
```

**Bad patterns to watch for:**
```
AA: 0.25 I: 0.09 E: 0.08  ← BAD: Smile active during speech
AA: 0.20 O: 0.15 E: 0.12  ← BAD: Multiple vowels blending
```

---

## 🚨 Critical Thresholds

| Viseme | Max Allowed | Purpose |
|--------|-------------|---------|
| viseme_I | 0.03 | Prevent smile stretch |
| viseme_E | 0.04 | Prevent upper lip pull |
| viseme_aa | 0.28 | Natural jaw open (not exaggerated) |
| Jaw open | 0.15 | Trigger smile suppression |
| Vowel suppression | 0.15 | Keep non-dominant vowels minimal |

---

## 🔄 Quick Adjustments

If speech still looks wrong:

### Too much smile:
```javascript
'viseme_I': 0.02,  // reduce from 0.03
'viseme_E': 0.03,  // reduce from 0.04
```

### Vowels still blend:
```javascript
targetWeights[v] *= 0.10;  // reduce from 0.15
```

### Too slow transitions:
```javascript
const LERP_SPEED = 0.12;  // increase from 0.10
```

### Teeth too visible:
```javascript
const teethOpacity = Math.min(1.0, mouthOpen * 2);  // reduce from 3
```

---

## 📍 File Location

**Path:** `app/src/main/java/com/example/alliswelltemi/ui/components/TemiComponents.kt`

**Key Line Numbers:**
- Line 1033: `LERP_SPEED`
- Lines 1034-1050: `VISEME_MAX` object
- Lines 1100-1147: `window.updateViseme` function (vowel suppression)
- Lines 1149-1172: `applySmoothedWeights` function (smile clamping)

---

## 🧪 Test Commands

```bash
# Build and deploy
cd /Users/mva357/AndroidStudioProjects/AllIsWell_Temi
./gradlew assembleDebug
adb install -r app/build/outputs/apk/debug/AlliswellTemi-debug.apk

# View console logs
adb logcat | grep "Model3DViewer"

# Or use Chrome DevTools:
# chrome://inspect → Select WebView → Console tab
```

---

## 🎬 Expected Behavior

**Before Calibration:**
- 😁 Avatar smiles while silent
- 😬 Upper lip stretches during "aa" sounds
- 🤪 Multiple vowels blend (rubber-face)
- ⚡ Snappy, unnatural transitions
- 👄 Overexposed teeth

**After Calibration:**
- 😐 Neutral face when silent
- 😊 Natural vowel shapes
- 🎯 One dominant vowel at a time
- 🌊 Smooth, conversational transitions
- 😁 Gradual teeth visibility

---

**Status:** ✅ IMPLEMENTED  
**Build:** ✅ SUCCESSFUL  
**Ready for:** Deployment & Testing

