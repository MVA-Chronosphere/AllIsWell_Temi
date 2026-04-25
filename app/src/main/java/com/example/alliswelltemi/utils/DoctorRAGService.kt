package com.example.alliswelltemi.utils

import com.example.alliswelltemi.data.Doctor
import android.util.Log

/**
 * RAG (Retrieval-Augmented Generation) Service for doctor knowledge base
 * Provides semantic understanding and context-aware responses
 */
object DoctorRAGService {
            /**
             * Helper to get gender-appropriate pronouns for a doctor
             */
            private fun getPronouns(gender: String): Triple<String, String, String> {
                return when (gender.lowercase()) {
                    "male" -> Triple("he", "his", "him")
                    "female" -> Triple("she", "her", "her")
                    else -> Triple("they", "their", "them")
                }
            }
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
        val (pronoun, possessive, objective) = getPronouns(doctor.gender)
        return when (queryType.lowercase()) {
            "location", "cabin", "where" -> {
                "Found doctor: $name. $pronoun.capitalize() is currently seeing patients in cabin ${doctor.cabin} here at All Is Well hospital."
            }
            "department", "specialty" -> {
                "Found doctor: $name. $pronoun.capitalize() is one of our top experts in ${doctor.department} here at All Is Well."
            }
            "experience", "years", "bio" -> {
                "Found doctor: $name. $pronoun.capitalize() is a highly awarded specialist at our hospital with a focus on ${doctor.specialization}. ${doctor.aboutBio.split(". ").take(1).joinToString("")}"
            }
            "full", "info", "details" -> {
                "Found doctor: $name. A distinguished member of our ${doctor.department} team at All Is Well, $pronoun is recognized for $possessive expertise in ${doctor.specialization}. You can find $objective in cabin ${doctor.cabin}."
            }
            else -> {
                "Found doctor: $name. $pronoun.capitalize() is currently practicing at All Is Well hospital as an expert in ${doctor.department}."
            }
        }
    }

    /**
     * Get appropriate response for department filter
     */
    fun getResponseForDepartment(dept: String, doctorCount: Int): String {
        return when {
            doctorCount == 0 -> "I'm sorry, we don't have any doctors listed in the $dept department at the moment."
            doctorCount == 1 -> "I'm opening the $dept department for you. We have one wonderful specialist ready to help."
            else -> "Certainly! Here is our $dept department. We have $doctorCount expert specialists available to assist you."
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
                "I'm sorry, I couldn't find a match for \"$originalQuery\". " +
                "Would you like to see our full list of doctors? Just say 'find doctor'."
            }
            searchResults.size == 1 -> {
                val doc = searchResults.first()
                val name = if (doc.name.startsWith("Dr.", ignoreCase = true)) doc.name else "Dr. ${doc.name}"
                val (pronoun, _, _) = getPronouns(doc.gender)
                "Found doctor: $name. $pronoun.capitalize() is a dedicated specialist in ${doc.department}. ${doc.aboutBio}"
            }
            else -> {
                val names = searchResults.take(3).joinToString(", ") { doc ->
                    if (doc.name.startsWith("Dr.", ignoreCase = true)) doc.name else "Dr. ${doc.name}"
                }
                "I've found a few matches for you: $names. Could you please tell me which one you're interested in?"
            }
        }
    }

    /**
     * Get contextual greeting based on doctors available
     */
    fun generateContextualGreeting(doctors: List<Doctor>): String {
        if (doctors.isEmpty()) return "Welcome to All Is Well Hospital! I'm Temi, and I'm here to help you find the right care."

        val deptCount = doctors.map { it.department }.distinct().size
        val doctorCount = doctors.size

        return when {
            doctorCount == 0 -> "Welcome to All Is Well Hospital! I'm Chronexa. I'm here to assist you in any way I can."
            doctorCount == 1 -> "Welcome! I'm Chronexa. We have a dedicated specialist available today to provide you with excellent care."
            deptCount == 1 -> "Welcome to All Is Well Hospital! I'm Chronexa. We have $doctorCount wonderful specialists in our ${doctors.first().department} department ready to help you."
            else -> "Hello and welcome to All Is Well Hospital! I'm Chronexa. We're proud to have $doctorCount expert specialists across $deptCount departments here to care for you today."
        }
    }

    /**
     * Generate rich, warm, and helpful response with multiple info points
     * Focused on All Is Well hospital context, specialties, and awards.
     * Kept concise (< 30s speech).
     */
    fun generateDetailedResponse(doctor: Doctor): String {
        val displayName = if (doctor.name.startsWith("Dr.", ignoreCase = true)) doctor.name else "Dr. ${doctor.name}"
        val (pronoun, possessive, objective) = getPronouns(doctor.gender)
        return buildString {
            append("Found doctor: $displayName. ")
            append("$pronoun.capitalize() is currently practicing here at All Is Well hospital. ")

            if (doctor.specialization.isNotBlank()) {
                append("$pronoun.capitalize() is an award-winning expert specializing in ${doctor.specialization}. ")
            } else if (doctor.department.isNotBlank()) {
                append("$pronoun.capitalize() is a leading specialist in our ${doctor.department} department. ")
            }
            
            if (doctor.aboutBio.isNotBlank()) {
                // Prioritize summary/awards if present in bio, keep it concise
                val cleanBio = doctor.aboutBio
                    .replace(Regex("(?i)M\\.D\\."), "MD")
                    .replace(Regex("(?i)M\\.B\\.B\\.S\\."), "MBBS")
                    .replace(Regex("(?i)B\\.D\\.S\\."), "BDS")
                    .replace(Regex("(?i)M\\.S\\."), "MS")
                    .split(". ")
                    .take(2) // Take only first 2 sentences to keep it under 30s
                    .joinToString(". ")
                
                append("$pronoun.capitalize() is highly recognized for $possessive contributions: $cleanBio. ")
            }

            if (doctor.cabin.isNotBlank()) {
                append("You can find $objective at cabin ${doctor.cabin}. ")
            }
            
            append("We are proud to have $objective on our team.")
        }.trim()
    }
}

