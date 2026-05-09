package com.example.alliswelltemi.data

import com.google.gson.annotations.SerializedName

/**
 * Strapi API response models for doctors
 */

// Image format from Strapi
data class ImageFormat(
    val url: String? = null,
    val name: String? = null,
    val width: Int? = null,
    val height: Int? = null
)

// Image with formats
data class ProfileImage(
    val id: Int? = null,
    val url: String? = null,
    val name: String? = null,
    val formats: Map<String, ImageFormat>? = null
) {
    fun getImageUrl(): String {
        // Try to get thumbnail first, then medium, then original
        return formats?.get("thumbnail")?.url
            ?: formats?.get("small")?.url
            ?: formats?.get("medium")?.url
            ?: url
            ?: ""
    }
}

// Doctor attributes from Strapi (supporting both v4 and v5 structures)
data class DoctorAttributes(
    val name: String? = null,
    val specialty: String? = null,
    @SerializedName("experience_years")
    val experienceYears: Int? = null,
    val about: String? = null,
    val experience: String? = null,
    @SerializedName("expertise_summary")
    val expertiseSummary: String? = null,
    val location: String? = null,
    @SerializedName("profile_image")
    val profileImage: Map<String, Any>? = null,
    // Legacy fields if any
    val department: String? = null,
    val specialization: String? = null,
    val aboutBio: String? = null,
    val cabin: String? = null,
    val yearsOfExperience: Int? = null
)

// Doctor document from Strapi
data class DoctorDocument(
    val id: Int? = null,
    val documentId: String? = null,
    val name: String? = null,
    val specialty: String? = null,
    @SerializedName("experience_years")
    val experienceYears: Int? = null,
    val about: String? = null,
    val experience: String? = null,
    @SerializedName("expertise_summary")
    val expertiseSummary: String? = null,
    val location: String? = null,
    @SerializedName("profile_image")
    val profileImage: Any? = null, // Can be ProfileImage or Map
    val attributes: DoctorAttributes? = null
) {
    fun toDomain(): Doctor? {
        // Handle Strapi v4 (attributes) and v5 (flat)
        val doctorName = (name ?: attributes?.name ?: "").trim()
        if (doctorName.isBlank()) {
            android.util.Log.w("DoctorDocument", "Skipping doctor with blank name: id=$id")
            return null
        }

        val rawSpecialty = (specialty ?: attributes?.specialty ?: attributes?.specialization ?: "").trim()
        
        // Extract actual department from descriptive specialty (e.g., "Consultant Obstetrician & Gynaecologist" → "Gynecology")
        val docSpecialty = extractDepartmentFromSpecialty(rawSpecialty)
        
        val docAbout = (about ?: attributes?.about ?: attributes?.aboutBio ?: "").trim()
        val docExpYears = experienceYears ?: attributes?.experienceYears ?: attributes?.yearsOfExperience ?: 0
        val docLocation = (location ?: attributes?.location ?: attributes?.cabin ?: "").trim()

        val imageData = profileImage ?: attributes?.profileImage
        val imageUrl = when (imageData) {
            is Map<*, *> -> {
                @Suppress("UNCHECKED_CAST")
                extractImageUrlFromMap(imageData as Map<String, Any>)
            }
            is ProfileImage -> imageData.getImageUrl()
            else -> ""
        }

        // Get Hindi translation for department
        val departmentHi = DoctorData.DEPARTMENT_TRANSLATIONS
            .find { it.english.equals(docSpecialty, ignoreCase = true) }
            ?.hindi ?: ""

        android.util.Log.d("DoctorDocument", "✅ Parsed doctor: name='$doctorName', raw_specialty='$rawSpecialty', extracted_dept='$docSpecialty', dept_hi='$departmentHi', cabin='$docLocation'")

        return Doctor(
            id = id?.toString() ?: documentId ?: "",
            name = doctorName,  // Already trimmed above
            department = docSpecialty, // Using extracted department
            departmentHi = departmentHi,  // Add Hindi translation
            yearsOfExperience = docExpYears,
            aboutBio = docAbout,
            cabin = docLocation,
            specialization = docSpecialty,
            profileImageUrl = if (imageUrl.startsWith("/")) "https://aiwcms.chronosphere.in$imageUrl" else imageUrl
        )
    }

    /**
     * Extract actual department from descriptive specialty titles
     * E.g., "Consultant Obstetrician & Gynaecologist" → "Gynecology"
     *       "Consultant Cardiologist" → "Cardiology"
     */
    private fun extractDepartmentFromSpecialty(specialty: String): String {
        if (specialty.isBlank()) return ""
        
        val lowerSpec = specialty.lowercase()
        
        // Map specialty keywords to standardized departments
        val departmentMappings = mapOf(
            "gynaecolog" to "Gynecology",  // Gynecologist, Gynaecologist, Obstetrician & Gynaecologist
            "obstetrician" to "Gynecology",  // Obstetrician (includes gynecology)
            "cardiolog" to "Cardiology",
            "cardio" to "Cardiology",
            "neurolog" to "Neurology",
            "neuro" to "Neurology",
            "orthopae" to "Orthopedics",  // Orthopaedic
            "orthoped" to "Orthopedics",
            "dermatolog" to "Dermatology",
            "dermatolo" to "Dermatology",
            "patholog" to "Pathology",
            "pediatrician" to "Pediatrics",
            "pediatri" to "Pediatrics",
            "ophthalmolog" to "Ophthalmology",
            "opthalm" to "Ophthalmology",
            "surgeon" to "General Surgery",
            "surgery" to "General Surgery",
            "anesthesiolog" to "Anesthesiology",
            "anaesthesiolog" to "Anesthesiology",
            "radiolog" to "Radiology",
            "radio" to "Radiology",
            "oncolog" to "Oncology",
            "psychiatr" to "Psychiatry",
            "urologis" to "Urology",
            "nephrol" to "Nephrology",
            "pulmon" to "Pulmonology",
            "gastro" to "Gastroenterology",
            "dental" to "Dentistry",
            "emergency" to "Emergency Medicine",
            "medicine" to "Internal Medicine",
            "nutrition" to "Nutrition & Dietetics"
        )
        
        for ((keyword, department) in departmentMappings) {
            if (lowerSpec.contains(keyword)) {
                return department
            }
        }
        
        // If no match found, return the original specialty (it might be a valid department name)
        return specialty
    }

    private fun extractImageUrlFromMap(imageData: Map<String, Any>?): String {
        if (imageData == null) return ""

        // Try Strapi v4 nested structure
        val data = imageData["data"]
        if (data != null) {
            return when (data) {
                is Map<*, *> -> {
                    @Suppress("UNCHECKED_CAST")
                    val attrs = (data as Map<String, Any>)["attributes"] as? Map<String, Any>
                    attrs?.get("url") as? String ?: ""
                }
                is List<*> -> {
                    @Suppress("UNCHECKED_CAST")
                    val firstItem = data.firstOrNull() as? Map<String, Any>
                    @Suppress("UNCHECKED_CAST")
                    val attrs = firstItem?.get("attributes") as? Map<String, Any>
                    attrs?.get("url") as? String ?: ""
                }
                else -> ""
            }
        }

        // Try Strapi v5 flat structure
        val url = imageData["url"] as? String
        if (url != null) return url

        // Try formats
        @Suppress("UNCHECKED_CAST")
        val formats = imageData["formats"] as? Map<String, Any>
        @Suppress("UNCHECKED_CAST")
        val thumbnail = formats?.get("thumbnail") as? Map<String, Any>
        return thumbnail?.get("url") as? String ?: ""
    }
}

// Strapi API response
data class DoctorsApiResponse(
    val data: List<DoctorDocument>? = null,
    val meta: Map<String, Any>? = null
)
