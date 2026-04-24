package com.example.alliswelltemi.data

import com.google.gson.annotations.SerializedName

/**
 * Strapi appointment request/response models
 */

data class AppointmentRequest(
    val data: AppointmentData
)

data class AppointmentData(
    @SerializedName("doctor_name")
    val doctorName: String,
    val department: String,
    val date: String,
    val time: String,
    @SerializedName("patient_name")
    val patientName: String,
    @SerializedName("patient_phone")
    val patientPhone: String,
    val status: String = "pending",
    val token: String
)

data class AppointmentResponse(
    val data: Any? = null,
    val error: Map<String, Any>? = null
)

data class AppointmentDocument(
    val id: Int? = null,
    val attributes: Map<String, Any>? = null
)
