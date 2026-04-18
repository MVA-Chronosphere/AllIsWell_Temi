# 🎨 Temi Hospital Assistant - Design Specifications

## Screen Layout (1920x1080 Landscape)

```
┌────────────────────────────────────────────────────────────┐
│ 70dp                                                        │
│ ┌────────────────────────────────────────────────────────┐ │
│ │ All Is Well Hospital      Status | Batt  [🌐 English] │ │
│ └────────────────────────────────────────────────────────┘ │
├────────────────────────────────────────────────────────────┤
│                                                            │
│  Left Side (50%)          │         Right Side (50%)       │
│                           │                                │
│  Hello! I'm Temi         │   Find & Navigate              │
│  Your smart hospital     │   Doctors & Departments         │
│  assistant. How can I    │   Book Appointment              │
│  help you today?         │   Emergency Help                │
│                           │   Hospital Information          │
│       🤖                  │   हिंदी                         │
│    (glowing)             │                                │
│                           │                                │
├────────────────────────────────────────────────────────────┤
│ 70dp                                                        │
│ 🌊 You can say: 'Take me to...' or 'Book appointment' 🎙️  │
└────────────────────────────────────────────────────────────┘
```

---

## Color Palette

### Primary Colors
| Color | Hex Code | Use |
|-------|----------|-----|
| Dark Background | #0B1220 | Main background |
| Dark Surface | #1A2332 | Component background |
| Neon Cyan | #00D9FF | Primary accent |
| Neon Purple | #B100FF | Secondary accent |

### Gradient Colors (Menu Cards)

| Card | Start | End | RGB |
|------|-------|-----|-----|
| Find & Navigate | #0D47A1 | #1976D2 | Blue |
| Doctors | #00695C | #00897B | Teal |
| Appointment | #4A148C | #7B1FA2 | Purple |
| Emergency | #B71C1C | #D32F2F | Red |
| Hospital Info | #E65100 | #F57C00 | Orange |
| हिंदी | #1A237E | #283593 | Indigo |

### Text Colors
| Element | Color | Hex Code |
|---------|-------|----------|
| Primary Text | White | #FFFFFF |
| Secondary Text | Light Gray | #A0A0A0 |
| Card Text | White | #FFFFFF |
| Card Subtitle | Medium Gray | #C0C0C0 |

---

## Typography

### Header
- **Font Family:** Sans Serif
- **Weight:** Bold
- **Size:** 24sp
- **Color:** Neon Cyan (#00D9FF)
- **Example:** "All Is Well Hospital"

### Greeting (Main Title)
- **Font Family:** Sans Serif
- **Weight:** Bold
- **Size:** 40sp
- **Color:** White (#FFFFFF)
- **Example:** "Hello! I'm Temi"

### Subtitle
- **Font Family:** Sans Serif
- **Weight:** Medium
- **Size:** 18sp
- **Color:** Light Gray (#A0A0A0)
- **Example:** "Your smart hospital assistant..."

### Card Titles
- **Font Family:** Sans Serif
- **Weight:** Semi Bold (600)
- **Size:** 16sp
- **Color:** White (#FFFFFF)
- **Example:** "Book Appointment"

### Card Subtitles
- **Font Family:** Sans Serif
- **Weight:** Normal (400)
- **Size:** 12sp
- **Color:** Medium Gray (#C0C0C0)
- **Example:** "Quick & easy booking"

### Voice Bar Text
- **Font Family:** Sans Serif
- **Weight:** Normal
- **Size:** 12sp
- **Color:** Light Gray (#A0A0A0)

### Button Text
- **Font Family:** Sans Serif
- **Weight:** Semi Bold (600)
- **Size:** 16sp
- **Color:** White (#FFFFFF)

---

## Component Specifications

### Header Bar
- **Height:** 70dp
- **Background:** Gradient (Dark Surface)
- **Padding:** 24dp horizontal
- **Border:** Subtle divider below (1dp, Light Gray)

### Greeting Section
- **Alignment:** Center-top, left side
- **Spacing:** 12dp between title and subtitle
- **Width:** 50% of screen

### Avatar Component
- **Size:** 120dp diameter
- **Shape:** Circle
- **Border:** 3dp gradient (Cyan → Purple)
- **Glow:** Infinite animation (0.4 → 0.8 alpha)
- **Shadow:** 20dp elevation with ambient glow

### Menu Cards
- **Container:**
  - Size: Variable (responsive)
  - Height: 140dp
  - Corners: 24dp rounded
  - Shadow: 12dp elevation
  - Border: 1dp white (0.1 alpha)

- **Content Padding:** 16dp
- **Icon:** 40sp emoji
- **Title Font:** 16sp Semi Bold
- **Subtitle Font:** 12sp Normal
- **Alignment:** Center

### Grid Layout
- **Type:** 2 columns × 3 rows
- **Column Width:** Equal (50% each)
- **Row Gap:** 16dp
- **Column Gap:** 16dp
- **Total Padding:** 32dp horizontal

### Voice Input Bar
- **Container:**
  - Height: 70dp
  - Corners: 35dp rounded (pill shape)
  - Background: Dark Surface (#1A2332)
  - Shadow: 16dp elevation with glow

- **Waveform Icon:**
  - Width: 24dp
  - Height: 40dp
  - 4 bars, 3dp width each
  - Color: Neon Cyan
  - Animation: Pulse when listening

- **Microphone Button:**
  - Size: 50dp diameter
  - Shape: Circle
  - Background: Radial gradient (Cyan → Semi-transparent)
  - Glow: 8-16dp shadow (animated when listening)
  - Icon: Mic (24sp, Dark background)

- **Text:**
  - Font: 12sp Normal
  - Color: Light Gray
  - Padding: 12dp horizontal
  - Max width: Flexible

---

## Spacing System

### Standard Gaps
- **Header/Footer Padding:** 24dp
- **Element Spacing:** 16dp
- **Card Padding:** 16dp-20dp
- **Button Padding:** 8-16dp

### Margins
- **Header Margin:** 0dp (edge to edge)
- **Voice Bar Margin:** 24dp
- **Menu Grid Margin:** 32dp

---

## Animations

### Avatar Glow
- **Duration:** 2000ms
- **Type:** Infinite repeatable
- **Alpha:** 0.4 → 0.8 → 0.4
- **Curve:** Smooth (Easing.FastOutSlowIn)

### Microphone Glow (Listening)
- **Duration:** 1500ms
- **Type:** Infinite repeatable when listening
- **Alpha:** 0.5 → 1.0 → 0.5
- **Elevation:** 8dp (idle) → 16dp (listening)

### Card Press
- **Type:** Color state change
- **Duration:** 300ms
- **Effect:** Subtle shadow increase

### Waveform Animation
- **Type:** Height pulse
- **Duration:** Continuous when listening
- **Heights:** 12dp → 24dp (alternating bars)

---

## Interaction States

### Card States
- **Idle:** Full opacity, base shadow
- **Hover:** Slight shadow increase
- **Press:** Slight elevation change
- **Active:** Highlight glow

### Button States
- **Idle:** Standard gradient
- **Hover:** Slight brightness increase
- **Press:** Slight shadow increase
- **Disabled:** 50% opacity

### Language Button States
- **Idle:** Cyan border, semi-transparent background
- **Hover:** Brighter cyan
- **Press:** Darker background

---

## Responsive Sizing

### For Different Screen Widths
- **1920px (Temi):** Full 24-32dp padding
- **1280px (Tablet):** 20-24dp padding
- **720px (Phone):** Stack vertically (not primary target)

All sizes use `dp` units for proper scaling.

---

## Shadows & Elevation

### Header
- **Elevation:** 2dp
- **Color:** #00000015 (semi-transparent)

### Menu Cards
- **Elevation:** 12dp
- **Ambient Color:** #33000000 (light ambient)
- **Spot Color:** #44000000 (light spot)

### Voice Bar
- **Elevation:** 16dp
- **Ambient Color:** #4400D9FF (cyan tint)
- **Spot Color:** #5500D9FF (cyan spot)

### Microphone Button
- **Elevation:** 8-16dp (animated)
- **Glow Color:** Cyan (#00D9FF)

---

## Border Radius

| Element | Radius |
|---------|--------|
| Menu Cards | 24dp |
| Voice Bar Container | 35dp |
| Buttons | 24-35dp |
| Avatar | 60dp (circle) |
| Language Button | 50dp |

---

## Accessibility

### Contrast Ratios
- **White on Dark:** 21:1 (AAA)
- **Cyan on Dark:** 9:1 (AA)
- **Gray on Dark:** 7:1 (AA)

### Touch Targets
- **Minimum:** 48dp × 48dp
- **Cards:** 120dp × 140dp (exceeds)
- **Buttons:** 50dp+ (exceeds)
- **Language Button:** 40dp+ (exceeds)

### Font Sizes
- **Minimum:** 12sp (readable)
- **Preferred:** 16sp+ (comfortable)

---

## Icon Specifications

### Emoji Icons Used
- Find & Navigate: 🗺️ (Map)
- Doctors: 👨‍⚕️ (Doctor)
- Appointment: 📅 (Calendar)
- Emergency: 🚨 (Siren)
- Hospital Info: ℹ️ (Info)
- Hindi: 🇮🇳 (India flag)
- Avatar: 🤖 (Robot)
- Language: 🌐 (Globe)
- Microphone: 🎙️ (Microphone)
- Waveform: 🌊 (Waves)

### Font Size for Icons
- **Large:** 60sp (Avatar)
- **Standard:** 40sp (Menu cards)
- **Small:** 18-24sp (Button icons)

---

## Animation Timing

| Animation | Duration | Easing |
|-----------|----------|--------|
| Avatar Glow | 2000ms | Linear |
| Mic Glow | 1500ms | Linear |
| Color State | 300ms | FastOutSlowIn |
| Transitions | 400ms | Standard |

---

## Dark Theme Justification

✅ **Temi Screen:** 13.3" display is often used in clinical settings
✅ **Eye Comfort:** Dark background reduces eye strain
✅ **OLED/LED:** Better power efficiency on OLED screens
✅ **Contrast:** Neon colors stand out clearly on dark background
✅ **Professional:** Modern, sleek appearance for hospital
✅ **Accessibility:** Easier for users with light sensitivity

---

## Design System Token Summary

```kotlin
// Colors
DarkBG = #0B1220
DarkSurface = #1A2332
NeonCyan = #00D9FF
NeonPurple = #B100FF

// Typography
TitleSize = 40.sp
SubtitleSize = 18.sp
CardTitleSize = 16.sp
CardSubSize = 12.sp

// Spacing
Padding = 24-32.dp
Gap = 16.dp
Elevation = 8-16.dp
Radius = 24-35.dp

// Animation
GlowDuration = 2000-2500ms
ColorDuration = 300ms
```

---

## Design Principles Applied

1. **Dark Mode Ready** - Low light environments
2. **Neon Accents** - Visibility on dark backgrounds
3. **Clear Hierarchy** - Large titles, readable subtitles
4. **Generous Spacing** - Touch-friendly for robot interaction
5. **Modern Glassmorphism** - Semi-transparent elements
6. **Smooth Animations** - Natural motion principles
7. **High Contrast** - Accessibility (AAA compliant)
8. **Responsive** - Scales to different devices
9. **Professional** - Hospital-appropriate aesthetic
10. **Inclusive** - Bilingual support (EN/HI)

---

This design specification ensures a **cohesive, professional, and accessible** hospital assistant UI optimized for the Temi robot platform.

