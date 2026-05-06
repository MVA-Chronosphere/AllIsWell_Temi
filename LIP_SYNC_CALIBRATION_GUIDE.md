# Lip Sync Parameter Calibration — Visual Guide

## The Problem → The Fix Flow

```
SYMPTOM: Upper lip stretch only, no middle lip opening, broken teeth
         ↓
ROOT CAUSES (stacked suppressions):
  1. VISEME_MAX too low
  2. Jaw damping too aggressive (0.55)
  3. Vowel suppression too harsh (0.05/0.08 static)
  4. Global clamping limits (0.16, 0.18)
  5. Teeth opacity too dim (1.2× cap 0.45)
         ↓
SOLUTION: Calibrate each parameter
         ↓
RESULT: Natural mouth opening with teeth visible + gentle smile blending
```

---

## Mouth Opening Physics

### BEFORE: Jaw Opening Too Weak
```
Input intensity: 1.0
    ↓ (× VISEME_MAX 0.16)
Adjusted: 0.16
    ↓ (× jaw damping 0.55)
Final: 0.088 ← WEAK (8.8% intensity)
    ↓ (hard clamp 0.16)
Applied: 0.088
    Result: Barely visible jaw drop ❌
```

### AFTER: Natural Jaw Opening
```
Input intensity: 1.0
    ↓ (× VISEME_MAX 0.28)
Adjusted: 0.28
    ↓ (× jaw damping 0.75)
Final: 0.21 ← STRONG (21% intensity)
    ↓ (clamp at 0.28)
Applied: 0.21
    Result: Noticeable, natural jaw drop ✅
    
Effect: ~2.4× stronger opening (0.21 vs 0.088)
```

---

## Smile Morphing During Speech

### BEFORE: Smile Collapses When Jaw Opens
```
Light Speaking (openAmount = 0.08):
  viseme_I weight: 0.03 × 0.05 = 0.0015 ← CRUSHED (0.15% strength)
  viseme_E weight: 0.04 × 0.08 = 0.0032 ← CRUSHED (0.32% strength)
  Result: Smile disappears → Mouth looks dead ❌

Wide Speaking (openAmount = 0.20):
  viseme_I weight: 0.03 × 0.05 = 0.0015 ← STILL CRUSHED
  viseme_E weight: 0.04 × 0.08 = 0.0032 ← STILL CRUSHED
  Result: No smile expression ❌
```

### AFTER: Dynamic Smile Blending
```
Light Speaking (openAmount = 0.08):
  suppressScale = 0.08 / 0.25 = 0.32 (32% of max suppression)
  viseme_I weight: 0.03 × max(0.2, 1.0 - 0.32×0.8) = 0.03 × 0.744 = 0.0223 ✓ (74% active)
  viseme_E weight: 0.04 × max(0.25, 1.0 - 0.32×0.75) = 0.04 × 0.76 = 0.0304 ✓ (76% active)
  Result: Gentle smile visible during speech ✅

Wide Speaking (openAmount = 0.20):
  suppressScale = 0.20 / 0.25 = 0.80 (80% of max suppression)
  viseme_I weight: 0.03 × max(0.2, 1.0 - 0.80×0.8) = 0.03 × 0.36 = 0.0108 (36% active)
  viseme_E weight: 0.04 × max(0.25, 1.0 - 0.80×0.75) = 0.04 × 0.40 = 0.016 (40% active)
  Result: Smile fades naturally as mouth opens wider ✅
```

**Key:** Smile doesn't vanish abruptly; it **gradually** fades as mouth opens.

---

## Vowel Range Expansion

### Visible Intensity Differences

```
BEFORE (OLD VISEME_MAX)             AFTER (NEW VISEME_MAX)
━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━
viseme_aa: ⬜⬜⬜⬜⬜⬜░░░░░░░░░░░░░░   viseme_aa: ⬜⬜⬜⬜⬜⬜⬜⬜⬜⬜⬜⬜⬜⬜⬜░
           (16% capacity)                        (28% capacity)
           
viseme_E:  ⬜⬜░░░░░░░░░░░░░░░░░░   viseme_E:  ⬜⬜⬜⬜░░░░░░░░░░░░░░
           (2% capacity)                        (4% capacity)
           
viseme_I:  ⬜░░░░░░░░░░░░░░░░░░░   viseme_I:  ⬜⬜⬜░░░░░░░░░░░░░░░
           (1% capacity)                        (3% capacity)
           
viseme_O:  ⬜⬜⬜⬜⬜⬜░░░░░░░░░░░░   viseme_O:  ⬜⬜⬜⬜⬜⬜⬜⬜⬜⬜⬜░░░░░
           (12% capacity)                       (22% capacity)
           
viseme_U:  ⬜⬜⬜⬜⬜░░░░░░░░░░░░░   viseme_U:  ⬜⬜⬜⬜⬜⬜⬜⬜⬜⬜░░░░░░
           (10% capacity)                       (20% capacity)

Key: ⬜ = allocated range | ░ = unused headroom
```

---

## Teeth Visibility Curve

### Opacity Over Mouth Opening

```
BEFORE (old: mouthOpen × 1.2, cap 0.45)
Opacity
  │     
0.45├─────────────⌢───────  (hard ceiling)
  │              /
0.30├─────────/
  │        /
0.15├────/
  │   /
  0└──────────────────────────
    0  0.1  0.2  0.3  0.4  0.5  mouth opening
    
Result: Teeth show only at extreme opening ❌

AFTER (new: mouthOpen × 2.0, cap 0.65)
Opacity
  │     
0.65├──────────────────⌢   (higher ceiling)
  │              /
0.40├──────────/
  │       /
0.15├──/
  │/
  0└──────────────────────────
    0  0.1  0.2  0.3  0.4  0.5  mouth opening
    
Result: Teeth fade in smoothly during normal speech ✅
         2× steeper curve → faster fade-in
         0.65 cap → 44% brighter teeth
```

---

## Combined Effect: Speech Animation Sequence

### Saying "AH" (viseme_aa dominant)

```
BEFORE:
  Time  Jaw    Teeth    Smile     Result
  T0    0→0.1  0→0.12   active    Weak jaw, dead smile
  T1    0.1→0.15 0.18   fade      Smile collapses midway
  T2    0.15→max 0.2    gone      Max jaw, no smile expression
  
AFTER:
  Time  Jaw    Teeth    Smile     Result
  T0    0→0.08 0→0.16   active    Natural jaw, gentle smile
  T1    0.08→0.15 0.20  fading    Smile gracefully fades
  T2    0.15→0.21 0.24  minimal   Full jaw + subtle smile (natural)
```

### Saying "EEE" (viseme_E dominant)

```
BEFORE:
  Time  Mouth  Teeth    Smile     Result
  T0    closed 0        max       Big smile (no jaw open)
  T1    tiny   ~0.02    max       Strained smile with small mouth
  
AFTER:
  Time  Mouth  Teeth    Smile     Result
  T0    closed 0        max       Natural smile (no jaw open)
  T1    small  ~0.06    max       Comfortable smile with room to speak
  T2    med    ~0.12    strong    Balanced smile as mouth opens
```

---

## Parameter Summary Table

```
┌──────────────────────┬───────┬────────┬────────────────────┐
│ Parameter            │ Old   │ New    │ Change             │
├──────────────────────┼───────┼────────┼────────────────────┤
│ viseme_aa MAX        │ 0.16  │ 0.28   │ +75% (jaw opening) │
│ viseme_E MAX         │ 0.02  │ 0.04   │ +100% (smile)      │
│ viseme_I MAX         │ 0.01  │ 0.03   │ +200% (smile)      │
│ viseme_O MAX         │ 0.12  │ 0.22   │ +83% (round mouth) │
│ viseme_U MAX         │ 0.10  │ 0.20   │ +100% (pursed)     │
│ aa damping multiplier│ 0.55  │ 0.75   │ -27% suppression   │
│ vowel suppression    │ fixed │ dynamic│ gradual blending   │
│ aa hard clamp        │ 0.16  │ 0.28   │ aligned w/ MAX     │
│ global morph clamp   │ 0.18  │ 0.25   │ +39% headroom      │
│ teeth opacity curve  │ 1.2×  │ 2.0×   │ +67% steeper       │
│ teeth opacity cap    │ 0.45  │ 0.65   │ +44% brighter      │
└──────────────────────┴───────┴────────┴────────────────────┘
```

---

## Expected User Experience

### Before Fix ❌
- **Smile:** Disappears when speaking
- **Jaw:** Barely visible opening
- **Teeth:** Not visible unless extreme mouth opening
- **Mouth shape:** Upper lip only; middle/lower lips stiff
- **Feel:** Robotic, constrained, unnatural

### After Fix ✅
- **Smile:** Gracefully fades as mouth opens (natural blending)
- **Jaw:** Visible, proportional jaw drop during vowel sounds
- **Teeth:** Show naturally during normal speech
- **Mouth shape:** Full articulation (jaw + lips working together)
- **Feel:** Smooth, natural, human-like lip sync

---

## Technical Notes

1. **LERP_SPEED = 0.10** unchanged → Smooth transitions preserved
2. **Vowel suppression** now scales with mouth opening degree:
   - Light opening: smile stays active (80%)
   - Moderate opening: smile fades gradually (36–75%)
   - Wide opening: smile minimal (20%)
3. **Head tilt coupling** (jaw influence on head) preserved
4. **Idle pose & arm animation** untouched
5. **Camera framing** unchanged
6. All changes are **parameter-level only** — no algorithm refactoring

---

**Calibration approach:** Removed artificial ceilings, enabled full VISEME_MAX ranges, added dynamic blending instead of static crushing.

