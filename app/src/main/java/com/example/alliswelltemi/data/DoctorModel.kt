package com.example.alliswelltemi.data

import java.time.LocalDateTime

/**
 * Doctor data model with profile information
 */
data class Doctor(
    val id: String,
    val name: String,
    val department: String,
    val yearsOfExperience: Int,
    val aboutBio: String,
    val cabin: String,  // e.g., "3A", "5B"
    val email: String = "",
    val phone: String = "",
    val specialization: String = "",
    val profileImageUrl: String = ""
)

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
 */
object DoctorData {
    val DOCTORS = listOf(
        Doctor(
            id = "doc_001",
            name = "Dr. Rajesh Sharma",
            department = "Cardiology",
            yearsOfExperience = 15,
            aboutBio = "Experienced cardiologist with specialization in interventional cardiology and cardiac surgery.",
            cabin = "3A"
        ),
        Doctor(
            id = "doc_002",
            name = "Dr. Priya Verma",
            department = "Neurology",
            yearsOfExperience = 12,
            aboutBio = "Specialist in neurological disorders with expertise in stroke management and epilepsy.",
            cabin = "4B"
        ),
        Doctor(
            id = "doc_003",
            name = "Dr. Amit Patel",
            department = "Orthopedics",
            yearsOfExperience = 18,
            aboutBio = "Orthopedic surgeon specializing in joint replacement and sports medicine.",
            cabin = "2C"
        ),
        Doctor(
            id = "doc_004",
            name = "Dr. Sneha Gupta",
            department = "Dermatology",
            yearsOfExperience = 10,
            aboutBio = "Dermatologist with expertise in cosmetic and clinical dermatology.",
            cabin = "5D"
        ),
        Doctor(
            id = "doc_005",
            name = "Dr. Vikram Singh",
            department = "General Surgery",
            yearsOfExperience = 20,
            aboutBio = "Senior surgeon with extensive experience in general and laparoscopic surgery.",
            cabin = "1E"
        ),
        Doctor(
            id = "doc_006",
            name = "Dr. Anjali Nair",
            department = "Pediatrics",
            yearsOfExperience = 14,
            aboutBio = "Pediatrician specializing in neonatal care and child development.",
            cabin = "6F"
        )
    )

    val DEPARTMENTS = listOf(
        "Cardiology",
        "Neurology",
        "Orthopedics",
        "Dermatology",
        "General Surgery",
        "Pediatrics"
    )

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



