# 🎨 VISUAL REFERENCE - TEMI UI LAYOUT

## Full Screen Layout (1920x1080 Landscape)

```
╔══════════════════════════════════════════════════════════════════════════════════════╗
║                                                                                      ║
║  All Is Well Hospital                              Online          Battery           ║  ← 80dp HEIGHT
║                                                                                      ║
╚══════════════════════════════════════════════════════════════════════════════════════╝

╔══════════════════════════════════════════════════════════════════════════════════════╗
║                                                                                      ║
║                                                                                      ║
║                        ╔═══════════════════════════════════╗                         ║
║                        ║   Hello! I'm Temi                ║  ← 48sp Bold           ║
║                        ║   (Dark Gray #3A3A3A)            ║                         ║
║                        ╚═══════════════════════════════════╝                         ║
║                                                                                      ║
║           Your smart hospital assistant. How can I help you today?                  ║  ← 20sp Gray
║                                                                                      ║
║                  ╔═════════════════════════════════════════╗                         ║
║                  ║   TALK TO ASSISTANT                    ║  ← 420x90dp             ║
║                  ║   (Blue #2D4A9D, 30dp radius)          ║     22sp Bold White      ║
║                  ╚═════════════════════════════════════════╝                         ║
║                                                                                      ║
║                         ← Vertically Centered →                                     ║
║                                                                                      ║
╚══════════════════════════════════════════════════════════════════════════════════════╝

╔══════════════════════════════════════════════════════════════════════════════════════╗
║                                                                                      ║
║   ┌────────────────────────────────────────┐  ┌─────────────────────────────────┐   ║
║   │                                        │  │                                 │   ║
║   │         Find Doctor                   │  │       Departments               │   ║  ← 180dp Height
║   │                                        │  │                                 │   ║     24dp Radius
║   │     (Blue #2D4A9D)                     │  │   (Blue #2D4A9D)                │   ║     22sp Bold White
║   │     22sp Bold White, Centered          │  │   22sp Bold White, Centered     │   ║
║   │                                        │  │                                 │   ║
║   └────────────────────────────────────────┘  └─────────────────────────────────┘   ║
║      ← Equal Width (weight=1) →            ← 24dp spacing →                        ║
║   ← 32dp h-padding →                                          ← 32dp h-padding →   ║
║                                                                                      ║
║                         ← 40dp vertical padding →                                    ║
║                                                                                      ║
╚══════════════════════════════════════════════════════════════════════════════════════╝

```

---

## Component Breakdown

### HEADER BAR (80dp)
```
┌─────────────────────────────────────────────────────────────────────────┐
│ All Is Well Hospital (28sp Bold #2D4A9D)     Online (14sp) Battery      │
│ ← 24dp padding →                                      ← 12dp → ← padding→│
└─────────────────────────────────────────────────────────────────────────┘
```

### MAIN CONTENT (Vertically Centered)
```
┌────────────────────────────────────────┐
│ Hello! I'm Temi                        │  ← 48sp Bold
│ (48sp Bold #3A3A3A, Centered)          │
│                                        │
│ ↑ 8dp ↑                                │
│                                        │
│ Your smart hospital...                 │  ← 20sp Gray #808080
│ (20sp #808080, Centered)               │
│                                        │
│ ↑ 32dp ↑                               │
│                                        │
│  ┌──────────────────────────────┐     │
│  │ TALK TO ASSISTANT            │     │  ← 420x90dp
│  │ (22sp Bold White #2D4A9D)   │     │     Radius: 30dp
│  └──────────────────────────────┘     │
│                                        │
└────────────────────────────────────────┘
```

### BOTTOM ACTION CARDS (180dp)
```
┌─────────────────────────┐  ┌──────────────────────┐
│   Find Doctor           │  │    Departments       │
│   (22sp Bold White)     │  │  (22sp Bold White)   │
│                         │  │                      │
│  Height: 180dp          │  │  Height: 180dp       │
│  Radius: 24dp           │  │  Radius: 24dp        │
│  Color: #2D4A9D         │  │  Color: #2D4A9D      │
│                         │  │                      │
└─────────────────────────┘  └──────────────────────┘
  ← weight: 1 →            ← 24dp →  ← weight: 1 →
```

---

## Color Palette

```
HEADER BACKGROUND
┌─────────────────┐
│     WHITE       │ #FFFFFF
└─────────────────┘

HOSPITAL TITLE
┌─────────────────┐
│  DARK BLUE      │ #2D4A9D
└─────────────────┘

STATUS TEXT
┌─────────────────┐
│     GRAY        │ #808080
└─────────────────┘

MAIN TITLE
┌─────────────────┐
│  DARK GRAY      │ #3A3A3A
└─────────────────┘

SUBTITLE
┌─────────────────┐
│     GRAY        │ #808080
└─────────────────┘

BUTTONS & CARDS
┌─────────────────┐
│  DARK BLUE      │ #2D4A9D
└─────────────────┘

BUTTON TEXT
┌─────────────────┐
│     WHITE       │ #FFFFFF
└─────────────────┘

MAIN BACKGROUND
┌─────────────────┐
│     WHITE       │ #FFFFFF (85% opacity)
└─────────────────┘
```

---

## Spacing & Sizing Reference

```
HEADER
├─ Height: 80dp
├─ Title Size: 28sp (Bold)
├─ Status Size: 14sp
├─ Horizontal Padding: 24dp
└─ Status Row Spacing: 12dp

MAIN CONTENT
├─ Title Size: 48sp (Bold)
├─ Subtitle Size: 20sp
├─ Title-Subtitle Space: 8dp
├─ Button Margin Top: 32dp
├─ Button Width: 420dp
├─ Button Height: 90dp
├─ Button Radius: 30dp
└─ Button Text Size: 22sp (Bold)

ACTION CARDS
├─ Height: 180dp
├─ Radius: 24dp
├─ Text Size: 22sp (Bold)
├─ Card Spacing: 24dp
├─ Horizontal Padding: 32dp
├─ Vertical Padding: 40dp
└─ Card Weight: 1 (equal width)
```

---

## Responsive Behavior

### Landscape 1920x1080 (ONLY MODE)
- ✅ Fixed orientation
- ✅ Full screen utilization
- ✅ No scrolling
- ✅ Vertically centered content
- ✅ Horizontally centered layout
- ✅ Large touch targets
- ✅ Clear visual hierarchy

---

## Interactive Flows

### TALK TO ASSISTANT Button Flow
```
User Taps Button
        ↓
Robot speaks: "I'm listening. How can I help?"
        ↓
Navigate to "talk" destination
        ↓
MainActivity.handleNavigation("talk")
        ↓
Implement voice input logic
```

### FIND DOCTOR Button Flow
```
User Taps Button
        ↓
Robot speaks: "Opening doctor finder"
        ↓
Navigate to "doctors" destination
        ↓
MainActivity.handleNavigation("doctors")
        ↓
Open doctor finder screen
```

### DEPARTMENTS Button Flow
```
User Taps Button
        ↓
Robot speaks: "Opening departments"
        ↓
Navigate to "departments" destination
        ↓
MainActivity.handleNavigation("departments")
        ↓
Open departments screen
```

---

## Accessibility Features

✓ **Large Text**: 48sp title, 22sp button text
✓ **Large Touch Targets**: 180dp tall action cards
✓ **High Contrast**: Dark blue on white background
✓ **Clear Hierarchy**: Title > Subtitle > Buttons > Cards
✓ **Voice Feedback**: Text-to-speech on interactions

---

## Implementation Details

### Compose Tree Structure
```
Box(fullSize)
├─ Box(background overlay, alpha 0.85)
└─ Column(fullSize)
   ├─ TemiHeaderBar()
   │  └─ Row(header content)
   │     ├─ Text("All Is Well Hospital")
   │     └─ Row(status)
   │        ├─ Text("Online")
   │        └─ Text("Battery")
   │
   ├─ Box(weight=1, centered)
   │  └─ Column(centered)
   │     ├─ Text("Hello! I'm Temi")
   │     ├─ Text(subtitle)
   │     └─ Button("TALK TO ASSISTANT")
   │
   └─ Row(bottom padding)
      ├─ ActionCard("Find Doctor")
      └─ ActionCard("Departments")
```

---

**Device Target:** Temi Robot 13.3-inch Display  
**Resolution:** 1920x1080 Landscape  
**Framework:** Jetpack Compose  
**Status:** ✅ Production Ready

