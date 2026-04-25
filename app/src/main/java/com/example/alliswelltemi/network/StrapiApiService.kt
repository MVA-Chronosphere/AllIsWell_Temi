package com.example.alliswelltemi.network

import com.example.alliswelltemi.data.AppointmentRequest
import com.example.alliswelltemi.data.AppointmentResponse
import com.example.alliswelltemi.data.DoctorsApiResponse
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.POST
import retrofit2.http.Query

/**
 * Strapi API Service for fetching doctors
 */
interface StrapiApiService {

    @GET("api/doctors")
    suspend fun getDoctors(
        @Query("populate[profile_image][fields]") imageFields: String = "url,formats",  // Reduced fields
        @Query("fields[0]") field0: String = "name",
        @Query("fields[1]") field1: String = "department",
        @Query("fields[2]") field2: String = "specialization",
        @Query("fields[3]") field3: String = "yearsOfExperience",
        @Query("fields[4]") field4: String = "cabin",
        @Query("fields[5]") field5: String = "aboutBio",
        @Query("pagination[limit]") limit: Int = 100,  // Reduced from 1000 for faster response
        @Query("sort") sort: String = "name:asc"
    ): DoctorsApiResponse

    @POST("api/appointments")
    suspend fun createAppointment(
        @Body request: AppointmentRequest
    ): AppointmentResponse

    @GET("api/appointments")
    suspend fun getAppointmentByToken(
        @Query("filters[token][\$eq]") token: String
    ): AppointmentResponse
}

