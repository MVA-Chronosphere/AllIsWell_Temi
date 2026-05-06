# TemiFeedback - AllIsWell Hospital Feedback System

**Status:** Production Ready  
**Version:** 1.0  
**Date:** May 6, 2026

---

## 📁 Folder Structure

```
TemiFeedback/
├── feedback_api.php          ← API endpoint to receive feedback
├── feedback_dashboard.php    ← Admin dashboard to view feedback
├── config.php               ← Database configuration (create this!)
└── README.md                ← This file
```

---

## 🚀 Quick Setup (3 Steps)

### Step 1: Upload to Hosting India

Upload the entire **TemiFeedback** folder to your hosting:

```
/public_html/TemiFeedback/
  ├── feedback_api.php
  ├── feedback_dashboard.php
  └── config.php (you'll create this)
```

**Upload using:**
- FTP (FileZilla, WinSCP)
- cPanel File Manager
- Hosting India Control Panel

### Step 2: Update Database Credentials

In **both PHP files**, find and update these lines:

```php
define('DB_HOST', 'localhost');
define('DB_USER', 'YOUR_DATABASE_USER');      // ← Change this
define('DB_PASS', 'YOUR_DATABASE_PASSWORD');  // ← Change this
define('DB_NAME', 'alliswellhospital');
```

**Get credentials from Hosting India:**
1. Login to Control Panel
2. Go to MySQL Database
3. Copy your DB User, Password, Host

### Step 3: Test

Open in browser:
```
https://alliswellhospital.com/TemiFeedback/feedback_dashboard.php
```

**Should show:** Dashboard with 0 feedbacks (or your existing data)

---

## 📱 Android App Integration

Update **FeedbackService.kt** URL to:

```kotlin
private const val FEEDBACK_API_URL = "https://alliswellhospital.com/TemiFeedback/feedback_api.php"
```

Then use in your feedback screen:

```kotlin
lifecycleScope.launch {
    val response = FeedbackService.submitFeedback(
        rating = selectedRating,
        comment = userComment,
        patientId = patientId
    )
    
    if (response.success) {
        robot?.speak("Thank you for your feedback!")
    }
}
```

---

## 📄 File Descriptions

### feedback_api.php
- **Purpose:** Receives feedback from Temi Robot app
- **Method:** POST
- **Input:** JSON with rating (1-5) and comment
- **Output:** JSON success/error response
- **Auto-creates:** MySQL feedback table

### feedback_dashboard.php
- **Purpose:** Admin dashboard to view all feedback
- **Features:**
  - Filter by rating (1-5 stars)
  - Filter by date
  - Pagination (20 per page)
  - Statistics (total & average rating)
- **Access:** https://alliswellhospital.com/TemiFeedback/feedback_dashboard.php

### config.php (Optional)
You can create a separate config file to avoid updating both PHP files:

```php
<?php
// config.php
define('DB_HOST', 'localhost');
define('DB_USER', 'your_username');
define('DB_PASS', 'your_password');
define('DB_NAME', 'alliswellhospital');
?>
```

Then in both PHP files, replace the database defines with:
```php
require_once 'config.php';
```

---

## 🔒 Security Features

✅ **Input Validation**
- Rating: 1-5 only
- Comment: max 500 characters
- SQL injection prevention

✅ **Error Handling**
- Graceful error messages
- No sensitive data exposed

✅ **CORS Support**
- Can be called from any domain

✅ **Logging**
- All submissions logged with IP & user agent

---

## 📊 Dashboard Features

### View Feedback
- See all submitted feedback
- Display: ID, Rating (stars), Comment, Patient ID, Date & Time

### Statistics
- **Total Feedbacks:** Count of all submissions
- **Average Rating:** Average of all ratings

### Filter
- **By Rating:** Filter 1-5 stars
- **By Date:** Filter by specific date

### Pagination
- 20 feedbacks per page
- Navigate through pages

---

## 🗄️ Database

### Automatic Setup
The system **automatically creates** the feedback table on first use:

```sql
CREATE TABLE feedback (
    id INT AUTO_INCREMENT PRIMARY KEY,
    rating INT NOT NULL CHECK (rating >= 1 AND rating <= 5),
    comment TEXT,
    patient_id VARCHAR(100),
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    ip_address VARCHAR(45),
    user_agent VARCHAR(255),
    INDEX idx_rating (rating),
    INDEX idx_created (created_at)
);
```

No manual database setup needed!

---

## 🔗 API Endpoints

### Submit Feedback
**URL:** `https://alliswellhospital.com/TemiFeedback/feedback_api.php`  
**Method:** POST  
**Content-Type:** application/json

**Example Request:**
```json
{
    "rating": 5,
    "comment": "Great service!",
    "patient_id": "P123"
}
```

**Example Response (Success):**
```json
{
    "status": "success",
    "message": "Feedback submitted successfully",
    "feedback_id": 42,
    "timestamp": "2026-05-06 12:30:45"
}
```

**Example Response (Error):**
```json
{
    "status": "error",
    "message": "Rating must be between 1 and 5"
}
```

### View Dashboard
**URL:** `https://alliswellhospital.com/TemiFeedback/feedback_dashboard.php`  
**Method:** GET  

**Parameters:**
- `?rating=5` - Show only 5-star feedback
- `?date=2026-05-06` - Show only feedback from specific date
- `?page=2` - Show page 2
- Combine: `?rating=5&date=2026-05-06&page=1`

---

## 📝 Troubleshooting

### "Database connection failed"
✅ **Check:** DB_USER, DB_PASS, DB_NAME correct in PHP files  
✅ **Verify:** Database exists in Hosting India  
✅ **Test:** Try connecting via PHPMyAdmin

### "Permission denied" uploading
✅ **Check:** FTP credentials correct  
✅ **Upload to:** `/public_html/` directory  
✅ **Verify:** File permissions (644 for .php)

### Feedback not appearing
✅ **Check:** PHP files have correct database credentials  
✅ **Verify:** Table `feedback` was created (in PHPMyAdmin)  
✅ **Test:** Are you calling FeedbackService.submitFeedback() in Android app?

### HTTPS/SSL errors
✅ **Check:** Using `https://` not `http://`  
✅ **Verify:** Hosting India SSL certificate active  
✅ **Test:** Visit https://alliswellhospital.com in browser

---

## 📞 Support

### Quick Checks
1. **Is folder accessible?**
   ```
   https://alliswellhospital.com/TemiFeedback/feedback_api.php
   Should show: {"status":"error","message":"Only POST requests are allowed"}
   ```

2. **Can you access dashboard?**
   ```
   https://alliswellhospital.com/TemiFeedback/feedback_dashboard.php
   Should show: Dashboard page
   ```

3. **Is database connected?**
   - Open PHPMyAdmin in Hosting India control panel
   - Check if `feedback` table exists

4. **Check error logs**
   ```bash
   FTP → /var/log/php-errors.log
   Check for MySQL connection errors
   ```

---

## 🎯 Next Steps

1. ✅ Upload TemiFeedback folder to `/public_html/`
2. ✅ Update database credentials in PHP files
3. ✅ Test dashboard: `https://alliswellhospital.com/TemiFeedback/feedback_dashboard.php`
4. ✅ Update FeedbackService.kt URL in Android app
5. ✅ Deploy Android app
6. ✅ Submit test feedback
7. ✅ Verify it appears in dashboard

---

**Status:** ✅ **PRODUCTION READY**

Your TemiFeedback system is fully configured and ready to use!

