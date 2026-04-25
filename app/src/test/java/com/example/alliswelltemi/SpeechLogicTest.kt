package com.example.alliswelltemi

import org.junit.Test
import org.junit.Assert.*

class SpeechLogicTest {

    data class Doctor(
        val name: String,
        val department: String,
        val specialization: String = "",
        val aboutBio: String = "",
        val cabin: String = "",
        val gender: String = "unspecified" // "male", "female", "other", "unspecified"
    )

    @Test
    fun testDoctorMatching() {
        val doctors = listOf(
            Doctor("Dr. Rajesh Sharma", "Cardiology", "Heart Surgery", "Expert", "3A"),
            Doctor("Dr. Priya Verma", "Neurology", "Stroke", "Expert", "4B")
        )

        val query = "who is priya verma"
        val normalizedSpeech = query.lowercase().replace("dr.", "doctor").replace("dr ", "doctor ").trim()
        
        val matchedDoctor = doctors.find { doctor ->
            val fullName = doctor.name.lowercase()
                .replace("dr.", "doctor")
                .replace("dr ", "doctor ")
                .trim()
            
            val names = fullName.split(" ").filter { it.length > 2 && it != "doctor" }
            
            normalizedSpeech.contains(fullName) || names.any { normalizedSpeech.contains(it) }
        }

        assertNotNull(matchedDoctor)
        assertEquals("Dr. Priya Verma", matchedDoctor?.name)
    }

    @Test
    fun testDoctorMatchingWithAbhey() {
        val doctors = listOf(
            Doctor("Dr. Rajesh Sharma", "Cardiology"),
            Doctor("Dr. Abhey Joshi", "Pediatrics")
        )

        val query = "who is abhey joshi"
        val normalizedSpeech = query.lowercase().replace("dr.", "doctor").replace("dr ", "doctor ").trim()
        
        val matchedDoctor = doctors.find { doctor ->
            val fullName = doctor.name.lowercase()
                .replace("dr.", "doctor")
                .replace("dr ", "doctor ")
                .trim()
            
            val names = fullName.split(" ").filter { it.length > 2 && it != "doctor" }
            
            normalizedSpeech.contains(fullName) || names.any { normalizedSpeech.contains(it) }
        }

        assertNotNull("Should match Abhey Joshi", matchedDoctor)
        assertEquals("Dr. Abhey Joshi", matchedDoctor?.name)
    }

    @Test
    fun testNonExistentDoctor() {
        val doctors = listOf(
            Doctor("Dr. Rajesh Sharma", "Cardiology")
        )

        val query = "who is abhey joshi"
        val normalizedSpeech = query.lowercase().replace("dr.", "doctor").replace("dr ", "doctor ").trim()
        
        val matchedDoctor = doctors.find { doctor ->
            val fullName = doctor.name.lowercase()
                .replace("dr.", "doctor")
                .replace("dr ", "doctor ")
                .trim()
            
            val names = fullName.split(" ").filter { it.length > 2 && it != "doctor" }
            
            normalizedSpeech.contains(fullName) || names.any { normalizedSpeech.contains(it) }
        }

        assertNull("Should not match if doctor is not in list", matchedDoctor)
    }
}
