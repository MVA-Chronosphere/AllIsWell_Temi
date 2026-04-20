package com.example.alliswelltemi.utils

import com.example.alliswelltemi.data.Doctor
import android.util.Log

/**
 * RAG (Retrieval-Augmented Generation) Service for doctor knowledge base
 * Provides semantic understanding and context-aware responses
 */
object DoctorRAGService {
    private val tag = "DoctorRAGService"

    /**
     * Generate a comprehensive knowledge base from doctor data
     * Optimized for LLM/NLP consumption
     */
    fun generateKnowledgeBase(doctors: List<Doctor>): String {
        if (doctors.isEmpty()) return "No doctor information available."

        val sb = StringBuilder()
        sb.append("Hospital Doctors and Departments Information:\n\n")

        // Group doctors by department
        val doctorsByDept = doctors.groupBy { it.department }

        doctorsByDept.forEach { (dept, deptDoctors) ->
            sb.append("## $dept\n")
            deptDoctors.forEach { doctor ->
                val name = if (doctor.name.startsWith("Dr.", ignoreCase = true)) doctor.name else "Dr. ${doctor.name}"
                sb.append("- $name:\n")
                sb.append("  Specialization: ${doctor.specialization}\n")
                sb.append("  Experience: ${doctor.yearsOfExperience} years\n")
                sb.append("  Location: Cabin ${doctor.cabin}\n")
                sb.append("  Bio: ${doctor.aboutBio}\n")
                sb.append("\n")
            }
        }

        Log.d(tag, "Generated knowledge base with ${doctors.size} doctors")
        return sb.toString()
    }

    /**
     * Generate structured JSON-like format for semantic understanding
     */
    fun generateStructuredKnowledge(doctors: List<Doctor>): List<String> {
        return doctors.map { doctor ->
            """
            Doctor: ${doctor.name}
            Department: ${doctor.department}
            Specialization: ${doctor.specialization}
            Experience: ${doctor.yearsOfExperience} years
            Location: ${doctor.cabin}
            Bio: ${doctor.aboutBio}
            """
        }
    }

    /**
     * Get appropriate response for a doctor query
     */
    fun getResponseForDoctor(doctor: Doctor, queryType: String = "general"): String {
        val name = if (doctor.name.startsWith("Dr.", ignoreCase = true)) doctor.name else "Dr. ${doctor.name}"
        return when (queryType.lowercase()) {
            "location", "cabin", "where" -> {
                "$name is in cabin ${doctor.cabin}."
            }
            "department", "specialty" -> {
                "$name is a specialist in ${doctor.department}."
            }
            "experience", "years", "bio" -> {
                "$name has ${doctor.yearsOfExperience} years of experience. ${doctor.aboutBio}"
            }
            "full", "info", "details" -> {
                "$name is in the ${doctor.department} department with ${doctor.yearsOfExperience} years of experience. " +
                "Cabin: ${doctor.cabin}. ${doctor.aboutBio}"
            }
            else -> {
                "$name is a specialist in ${doctor.department}. ${doctor.aboutBio}"
            }
        }
    }

    /**
     * Get appropriate response for department filter
     */
    fun getResponseForDepartment(dept: String, doctorCount: Int): String {
        return when {
            doctorCount == 0 -> "No doctors found in the $dept department."
            doctorCount == 1 -> "Opening the $dept department. We have 1 specialist."
            else -> "Opening the $dept department. We have $doctorCount specialists available."
        }
    }

    /**
     * Extract intent type from query
     */
    fun extractIntentType(query: String): String {
        val normalized = query.lowercase()
        return when {
            normalized.contains("where") || normalized.contains("cabin") || normalized.contains("room") -> "location"
            normalized.contains("department") || normalized.contains("specialty") || normalized.contains("field") -> "department"
            normalized.contains("experience") || normalized.contains("years") || normalized.contains("bio") -> "experience"
            normalized.contains("book") || normalized.contains("appointment") -> "booking"
            normalized.contains("navigate") || normalized.contains("take me") -> "navigation"
            else -> "general"
        }
    }

    /**
     * Generate fallback response with search results
     */
    fun generateFallbackResponse(searchResults: List<Doctor>, originalQuery: String): String {
        return when {
            searchResults.isEmpty() -> {
                "I'm sorry, I couldn't find a doctor or department matching \"$originalQuery\". " +
                "You can say 'find doctor' to see our full list."
            }
            searchResults.size == 1 -> {
                val doc = searchResults.first()
                val name = if (doc.name.startsWith("Dr.", ignoreCase = true)) doc.name else "Dr. ${doc.name}"
                "I found $name in the ${doc.department} department. ${doc.aboutBio}"
            }
            else -> {
                val names = searchResults.take(3).joinToString(", ") { doc ->
                    if (doc.name.startsWith("Dr.", ignoreCase = true)) doc.name else "Dr. ${doc.name}"
                }
                "I found several matches: $names. Please be more specific."
            }
        }
    }

    /**
     * Get contextual greeting based on doctors available
     */
    fun generateContextualGreeting(doctors: List<Doctor>): String {
        if (doctors.isEmpty()) return "Welcome to All Is Well Hospital. I'm Temi, your medical assistant."

        val deptCount = doctors.map { it.department }.distinct().size
        val doctorCount = doctors.size

        return when {
            doctorCount == 0 -> "Welcome to All Is Well Hospital. I'm Temi. No doctors are currently available."
            doctorCount == 1 -> "Welcome to All Is Well Hospital. I'm Temi. We have 1 specialist available today."
            deptCount == 1 -> "Welcome to All Is Well Hospital. I'm Temi. We have $doctorCount specialists in ${doctors.first().department} ready to help you."
            else -> "Welcome to All Is Well Hospital. I'm Temi. We have $doctorCount specialists across $deptCount departments available to assist you."
        }
    }

    /**
     * Generate rich response with multiple info points
     */
    fun generateDetailedResponse(doctor: Doctor): String {
        val displayName = if (doctor.name.startsWith("Dr.", ignoreCase = true)) doctor.name else "Dr. ${doctor.name}"
        return buildString {
            append("Here is some information about $displayName. ")
            if (doctor.department.isNotBlank()) {
                append("They are part of the ${doctor.department} department. ")
            }
            if (doctor.specialization.isNotBlank()) {
                append("Their specialization is in ${doctor.specialization}. ")
            }
            if (doctor.yearsOfExperience > 0) {
                append("They have ${doctor.yearsOfExperience} years of experience. ")
            }
            if (doctor.cabin.isNotBlank()) {
                append("You can find them at cabin location ${doctor.cabin}. ")
            }
            if (doctor.aboutBio.isNotBlank()) {
                // Ensure bio is cleaned of characters that might trip up TTS
                val cleanBio = doctor.aboutBio
                    .replace(Regex("(?i)M\\.D\\."), "MD")
                    .replace(Regex("(?i)M\\.B\\.B\\.S\\."), "MBBS")
                    .replace(Regex("(?i)B\\.D\\.S\\."), "BDS")
                    .replace(Regex("(?i)M\\.S\\."), "MS")
                
                append("Regarding their background. $cleanBio ")
            }
            if (doctor.email.isNotBlank()) append("The email address is ${doctor.email}. ")
            if (doctor.phone.isNotBlank()) append("The phone number is ${doctor.phone}. ")
        }.trim()
    }
}

