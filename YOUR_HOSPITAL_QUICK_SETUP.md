# Your Hospital Setup - Quick Reference

**Hospital Locations:** 18  
**Status:** ✅ Code Updated | ⏳ Awaiting Robot Registration  

---

## Your Locations (Ready to Register)

```
1. HOME BASE
2. PATHOLOGY DEPARTMENT
3. AYUSHMAN DEPARTMENT
4. MAIN PHARMACY
5. BACK ENTRANCE
6. ROTARY DIALYSIS CENTER
7. PHLEBOTOMY
8. PATHOLOGY RECEPTION
9. OPTICALS
10. OPD
11. DIALYSIS DEPARTMENT
12. MAIN ENTRANCE
13. OPHTHALMOLOGY DEPARTMENT
14. HEALTH SHOP
15. IPD BILLING
16. RADIOLOGY
17. MAIN RECEPTION
18. COMMON WASHROOM
```

---

## Voice Commands That Will Work

### Hindi Commands (After Robot Setup)
```
"पैथोलॉजी ले चल"                    → Pathology Department
"आयुष्मान विभाग में जाना है"         → Ayushman Department
"आँखों के डॉक्टर के पास ले जाओ"     → Ophthalmology Department
"फार्मेसी कहाँ है?"                 → Main Pharmacy
"ओ.पी.डी में ले चल"                 → OPD
"डायलिसिस सेंटर"                   → Dialysis/Rotary Dialysis Center
"रिसेप्शन ले चल"                    → Main Reception
"शौचालय कहाँ है?"                   → Common Washroom
```

### English Commands (After Robot Setup)
```
"Take me to Pathology"              → Pathology Department
"Where is OPD?"                     → OPD
"Go to Main Pharmacy"               → Main Pharmacy
"Navigate to Ophthalmology"         → Ophthalmology Department
"Where is Radiology?"               → Radiology
"Take me to Dialysis Center"        → Rotary Dialysis Center
"Show me the entrance"              → Main Entrance
"Washroom please"                   → Common Washroom
```

---

## Next Steps (In Order)

### Step 1: Verify Code is Ready ✅
- All 18 locations configured in code
- Bilingual support enabled
- No compilation errors

### Step 2: Register Locations (⏳ YOUR ACTION NEEDED)
On Temi Robot:
1. Menu → Administration → Map Management
2. Drive robot to each location
3. Save with **EXACT** name from the list above

**Important:** Use exact names:
- "PATHOLOGY DEPARTMENT" (not "pathology" or "Pathology Department")
- "MAIN PHARMACY" (not "pharmacy" or "Main Pharmacy")
- etc.

### Step 3: Verify Registration
Run:
```bash
adb logcat | grep "Robot has"
```
Should show: "Robot has 18 saved locations"

### Step 4: Test Voice Navigation
Say any command from the examples above → Robot should navigate

---

## Timeline

| Task | Time | Status |
|------|------|--------|
| Code Update | ✅ Done | Complete |
| Location Registration | ⏳ Pending | ~30 min |
| Testing | ⏳ Ready | ~10 min |
| **TOTAL** | | **~40 min** |

---

## Department Locations (For Doctor Visits)

These are where patients go to see doctors:
- **PATHOLOGY DEPARTMENT** (with separate PATHOLOGY RECEPTION)
- **AYUSHMAN DEPARTMENT**
- **OPHTHALMOLOGY DEPARTMENT**
- **DIALYSIS DEPARTMENT** (also ROTARY DIALYSIS CENTER)

---

## Service Locations

These are supporting services:
- **MAIN PHARMACY** - Medicine pickup
- **OPTICALS** - Eyewear/lenses
- **OPD** - General outpatient
- **PHLEBOTOMY** - Blood tests
- **RADIOLOGY** - Imaging services
- **HEALTH SHOP** - Medical supplies
- **IPD BILLING** - Billing counter

---

## Access Points

- **MAIN ENTRANCE** - Primary entry
- **BACK ENTRANCE** - Secondary entry
- **HOME BASE** - Robot starting location

---

## Support

**Have a 18-location hospital = SIMPLER SETUP!**

Once you register these 18 locations:
- Patients can ask in Hindi OR English
- Department names are automatically recognized
- "पैथोलॉजी" or "Pathology" both navigate to same place
- No custom setup needed per department

---

**Status:** 🟢 **READY TO DEPLOY**  
**Action Needed:** Register 18 locations on Temi robot  
**Estimated Time:** 30 minutes  

