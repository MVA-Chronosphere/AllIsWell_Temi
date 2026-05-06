# Temi Robot Location Registration Checklist

**Objective:** Get voice navigation working
**Status:** App code ready ✅, Need robot setup ⚠️

---

## Quick Summary

When you say **"फार्मेसी लेके चलो"** right now:
- ✅ Robot hears it correctly
- ✅ App detects it as navigation command  
- ✅ App says "ठीक है, आपको फार्मेसी ले जा रहे हैं"
- ❌ But robot doesn't actually move

**Why?** Because "Pharmacy" location isn't registered in the Temi robot's map.

**Solution?** Register locations manually on the robot.

---

## Checklist: Setup Locations on Temi Robot

### Phase 1: Prepare (5 minutes)
- [ ] You have access to Temi robot admin controls
- [ ] You have the list of 22 locations to register (see below)
- [ ] You have robot battery >50%
- [ ] You have clear space to drive robot

### Phase 2: Register Locations (20-30 minutes)

For each location in the list below:

**Steps to register ONE location:**
1. [ ] Drive robot to the physical location
2. [ ] Open Temi admin settings
   - **Option A:** Robot screen: Menu → Administration → Map Settings
   - **Option B:** Temi Companion App (web)
   - **Option C:** Temi Admin Control Panel
3. [ ] Click "Save Location" or "Add Location"
4. [ ] Enter exact name from list below
5. [ ] Confirm save
6. [ ] Return to starting point

**Location Registration Checklist:**

CORE LOCATIONS (Register First):
- [ ] Main Pharmacy
- [ ] Main Reception
- [ ] Pathology Department
- [ ] Pathology Reception

DEPARTMENTS:
- [ ] Ayushman Department
- [ ] Ophthalmology Department
- [ ] Dialysis Department

OTHER LOCATIONS:
- [ ] OPD
- [ ] Phlebotomy
- [ ] Opticals
- [ ] Radiology
- [ ] Rotary Dialysis Center
- [ ] Health Shop
- [ ] IPD Billing
- [ ] Common Washroom
- [ ] Home Base
- [ ] Main Entrance
- [ ] Back Entrance

**Total: 18 locations**

### Phase 3: Verify Setup (5 minutes)
- [ ] Open app on Temi
- [ ] Say: "फार्मेसी लेके चलो" (Take me to Pharmacy)
- [ ] Check logcat:
```bash
adb logcat | grep "Robot has"
```
- [ ] You should see: "Robot has 23 saved locations:"
- [ ] Pharmacy is in the list

### Phase 4: Test Navigation (5 minutes)
- [ ] Say: "फार्मेसी लेके चलो"
  - [ ] Robot speaks confirmation ✅
  - [ ] Robot moves/navigates ✅
  
- [ ] Say: "आईसीयू कहाँ है?" (Where is ICU?)
  - [ ] Robot speaks confirmation ✅
  - [ ] Robot moves/navigates ✅
  
- [ ] Try English: "Take me to Reception"
  - [ ] Robot speaks confirmation ✅
  - [ ] Robot moves/navigates ✅

### Phase 5: Test Doctor Search (2 minutes)
- [ ] Say: "कार्डियोलॉजी के डॉक्टर" (Show Cardiology doctors)
  - [ ] Should show Cardiology doctors (no navigation) ✅
  
- [ ] Say: "न्यूरोलॉजी" (Neurology)
  - [ ] Should show Neurology doctors (no navigation) ✅

---

## Expected Results

### After Setup is Complete:

| Command | Expected Behavior |
|---------|-------------------|
| "फार्मेसी लेके चलो" | ✅ Says "ठीक है..." & navigates to Pharmacy |
| "ले के चलो आईसीयू" | ✅ Says confirmation & navigates to ICU |
| "कार्डियोलॉजी के डॉक्टर" | ✅ Shows Cardiology doctors (no nav) |
| "कहाँ है एक्सिट?" | ✅ Says "ठीक है..." & navigates to Exit |
| "काफेटेरिया लेके जाओ" | ✅ Says confirmation & navigates to Cafeteria |

---

## Troubleshooting

### If Robot Says "Location Not Found"

**Check 1:** Verify location name
- Exact spelling needed (case-insensitive, but spelling matters)
- "pharmacy" NOT "farm" or "phar"

**Check 2:** Verify location is saved
- Go back to robot admin
- Check if location appears in saved locations list
- If missing, register again

**Check 3:** Check robot's saved locations
```bash
adb logcat | grep "Robot has"
# Example output:
# 📍 Robot has 3 saved locations:
#   - Pharmacy    ← If you see this, it's saved
#   - ICU
```

### If Robot Doesn't Move After Saying "Taking you to..."

**Option 1:** Check location exists
```bash
adb logcat | grep "✅ Location found"
# If you see this, location was found
```

**Option 2:** Check available locations
```bash
adb logcat | grep "Available locations:"
# Lists what robot knows about
```

**Option 3:** Re-save the location
- Be very careful with spelling
- Use same name as what's in the app

---

## Command Examples to Test

**After Setup, Try These Phrases:**

### Hindi Examples:
```
"फार्मेसी लेके चलो"        → Goes to Pharmacy
"आईसीयू कहाँ है?"          → Goes to ICU
"कार्डियोलॉजी दिखाना"     → Shows doctors (no nav)
"न्यूरोलॉजी के डॉक्टर"     → Shows Neuro doctors (no nav)
"एक्सिट लेके जाओ"          → Goes to Exit
"काफेटेरिया में ले जाना"   → Goes to Cafeteria
"शौचालय कहाँ?"             → Goes to Washroom
"पार्किंग लेके चलो"        → Goes to Parking
"लिफ्ट कहाँ है?"            → Goes to Elevator
```

### English Examples:
```
"Take me to Pharmacy"        → Goes to Pharmacy
"Where is the ICU?"          → Goes to ICU
"Show Cardiology doctors"    → Shows doctors (no nav)
"Find Neurology specialists" → Shows doctors (no nav)
"Go to Exit"                 → Goes to Exit
"Take me to Cafeteria"       → Goes to Cafeteria
"Where is the Washroom?"     → Goes to Washroom
"Navigate to Parking"        → Goes to Parking
"Where is the Elevator?"     → Goes to Elevator
```

### Mixed Language Examples:
```
"फार्मेसी ले के चलो" (Take me to Pharmacy - mixed)      → Works
"Take me to आईसीयू" (Take me to ICU - mixed)          → Works
"कहाँ है Emergency Room?" (Where is? - mixed)         → Works
```

---

## Success Criteria

✅ **Phase 1 Success:** Locations registered on robot
- [ ] 22 locations saved in Temi admin
- [ ] Verified in saved locations list

✅ **Phase 2 Success:** App recognizes locations
- [ ] Logcat shows "Robot has 23 saved locations"
- [ ] Lists include: Pharmacy, ICU, Cardiology, etc.

✅ **Phase 3 Success:** Navigation works
- [ ] Say "फार्मेसी लेके चलो"
- [ ] Robot moves to Pharmacy
- [ ] NOT just speaking confirmation

✅ **Phase 4 Success:** All features work
- [ ] Hindi navigation works
- [ ] English navigation works  
- [ ] Doctor search works (no navigation)
- [ ] Department filtering works

---

## Estimated Timeline

| Phase | Time | Status |
|-------|------|--------|
| App Code Development | ✅ Done | Complete |
| Robot Location Setup | ⏳ Needed | 30 minutes |
| Testing & Verification | ✅ Ready | 10 minutes |
| Production Deploy | ✅ Ready | Immediate |

**Total Time to Full Feature:** ~45 minutes

---

## Support

### Need Help?

**Check These Resources:**
1. `ROBOT_LOCATION_SETUP_REQUIRED.md` - Detailed setup guide
2. `COMPLETE_VOICE_NAVIGATION_SUMMARY.md` - Full technical overview
3. Logcat output - Detailed debugging info

### Debug Commands:
```bash
# Check navigation detection
adb logcat | grep "NAVIGATE"

# Check available locations
adb logcat | grep "Robot has"

# Full trace
adb logcat | grep -E "NAVIGATE|LOCATION_NAV|SpeechOrchestrator"
```

---

## Ready to Start?

✅ **App code:** Complete and ready
⚠️ **Next step:** Start Phase 1 preparation
⏱️ **Expected duration:** 45 minutes total
✅ **Expected outcome:** Full voice navigation in Hindi & English

**Go ahead and start registering locations on your Temi robot!**

---

**Last Updated:** May 6, 2026  
**Status:** Ready for location registration






