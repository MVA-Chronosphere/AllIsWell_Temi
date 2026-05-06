package com.example.alliswelltemi.data

/**
 * Doctor data model with profile information
 */
data class Doctor(
    val id: String,
    val name: String,
    val department: String,
    val departmentHi: String = "",  // Hindi name for department (for voice recognition)
    val yearsOfExperience: Int,
    val aboutBio: String,
    val cabin: String,  // e.g., "3A", "5B"
    val gender: String = "unspecified", // "male", "female", "other", "unspecified"
    val email: String = "",
    val phone: String = "",
    val specialization: String = "",
    val profileImageUrl: String = ""
) {
    /**
     * Get department name in specified language
     */
    fun getDepartmentInLanguage(language: String): String {
        return if (language == "hi" && departmentHi.isNotEmpty()) departmentHi else department
    }
}

/**
 * Time slot for appointment availability
 */
data class TimeSlot(
    val startTime: String,  // e.g., "10:00 AM"
    val endTime: String,    // e.g., "10:30 AM"
    val available: Boolean
)

/**
 * Sample doctor data for the hospital
 * Doctors include bilingual department support for voice recognition
 */
object DoctorData {
    val DOCTORS = listOf(
        Doctor(
            id = "doc_001",
            name = "Dr. Rajesh Sharma",
            department = "Cardiology",
            departmentHi = "कार्डियोलॉजी",
            yearsOfExperience = 15,
            aboutBio = "Experienced cardiologist with specialization in interventional cardiology and cardiac surgery.",
            cabin = "3A",
            gender = "male"
        ),
        Doctor(
            id = "doc_002",
            name = "Dr. Priya Verma",
            department = "Neurology",
            departmentHi = "न्यूरोलॉजी",
            yearsOfExperience = 12,
            aboutBio = "Specialist in neurological disorders with expertise in stroke management and epilepsy.",
            cabin = "4B",
            gender = "female"
        ),
        Doctor(
            id = "doc_003",
            name = "Dr. Amit Patel",
            department = "Orthopedics",
            departmentHi = "ऑर्थोपेडिक्स",
            yearsOfExperience = 18,
            aboutBio = "Orthopedic surgeon specializing in joint replacement and sports medicine.",
            cabin = "2C",
            gender = "male"
        ),
        Doctor(
            id = "doc_004",
            name = "Dr. Sneha Gupta",
            department = "Dermatology",
            departmentHi = "त्वचा विज्ञान",
            yearsOfExperience = 10,
            aboutBio = "Dermatologist with expertise in cosmetic and clinical dermatology.",
            cabin = "5D",
            gender = "female"
        ),
        Doctor(
            id = "doc_005",
            name = "Dr. Vikram Singh",
            department = "General Surgery",
            departmentHi = "सामान्य सर्जरी",
            yearsOfExperience = 20,
            aboutBio = "Senior surgeon with extensive experience in general and laparoscopic surgery.",
            cabin = "1E",
            gender = "male"
        ),
        Doctor(
            id = "doc_006",
            name = "Dr. Anjali Nair",
            department = "Pediatrics",
            departmentHi = "बाल चिकित्सा",
            yearsOfExperience = 14,
            aboutBio = "Pediatrician specializing in neonatal care and child development.",
            cabin = "6F",
            gender = "female"
        ),
        Doctor(
            id = "doc_007",
            name = "Dr. Apurva Yadav",
            department = "Ophthalmology",
            departmentHi = "नेत्र विज्ञान",
            yearsOfExperience = 9,
            aboutBio = "Consultant Ophthalmologist & Cataract Surgeon.",
            cabin = "",
            gender = "female"
        )
    )

    /**
     * Departments in both English and Hindi with normalized keys for matching
     */
    data class DepartmentPair(
        val english: String,
        val hindi: String,
        val normalizedKey: String = english.lowercase()
    )

    val DEPARTMENTS = listOf(
        "Cardiology",
        "Neurology",
        "Orthopedics",
        "Dermatology",
        "General Surgery",
        "Pediatrics"
    )

    /**
     * Bilingual department mapping for voice recognition and filtering
     */
    val DEPARTMENT_TRANSLATIONS = listOf(
        DepartmentPair("Cardiology", "कार्डियोलॉजी"),
        DepartmentPair("Neurology", "न्यूरोलॉजी"),
        DepartmentPair("Orthopedics", "ऑर्थोपेडिक्स"),
        DepartmentPair("Dermatology", "त्वचा विज्ञान"),
        DepartmentPair("General Surgery", "सामान्य सर्जरी"),
        DepartmentPair("Pediatrics", "बाल चिकित्सा"),
        DepartmentPair("Ophthalmology", "नेत्र विज्ञान")
    )

    /**
     * Find English department name from either English or Hindi input
     * Supports partial matching (e.g., "कार्डियो" matches "कार्डियोलॉजी")
     */
    fun findDepartmentByName(query: String): String? {
        val normalizedQuery = query.lowercase().trim()

        // Check exact match in English
        val englishMatch = DEPARTMENT_TRANSLATIONS.find {
            it.normalizedKey == normalizedQuery
        }
        if (englishMatch != null) return englishMatch.english

        // Check partial match in English
        val englishPartial = DEPARTMENT_TRANSLATIONS.find {
            it.english.lowercase().contains(normalizedQuery)
        }
        if (englishPartial != null) return englishPartial.english

        // Check exact match in Hindi
        val hindiMatch = DEPARTMENT_TRANSLATIONS.find {
            it.hindi == query.trim()
        }
        if (hindiMatch != null) return hindiMatch.english

        // Check partial match in Hindi
        val hindiPartial = DEPARTMENT_TRANSLATIONS.find {
            it.hindi.contains(query.trim())
        }
        if (hindiPartial != null) return hindiPartial.english

        return null
    }

    /**
     * Get all possible names (English and Hindi) for a department
     */
    fun getDepartmentAliases(englishDepartment: String): List<String> {
        val dept = DEPARTMENT_TRANSLATIONS.find { it.english == englishDepartment } ?: return listOf(englishDepartment)
        return listOf(dept.english, dept.hindi)
    }

    fun getDoctorsByDepartment(department: String): List<Doctor> {
        return DOCTORS.filter { it.department == department }
    }

    fun getDoctorById(id: String): Doctor? {
        return DOCTORS.find { it.id == id }
    }

    // Sample time slots - in production, these would come from backend
    fun getAvailableTimeSlots(): List<TimeSlot> {
        return listOf(
            TimeSlot("09:00 AM", "09:30 AM", true),
            TimeSlot("09:30 AM", "10:00 AM", true),
            TimeSlot("10:00 AM", "10:30 AM", false),
            TimeSlot("10:30 AM", "11:00 AM", true),
            TimeSlot("11:00 AM", "11:30 AM", true),
            TimeSlot("02:00 PM", "02:30 PM", true),
            TimeSlot("02:30 PM", "03:00 PM", true),
            TimeSlot("03:00 PM", "03:30 PM", false),
            TimeSlot("03:30 PM", "04:00 PM", true),
            TimeSlot("04:00 PM", "04:30 PM", true)
        )
    }
}



