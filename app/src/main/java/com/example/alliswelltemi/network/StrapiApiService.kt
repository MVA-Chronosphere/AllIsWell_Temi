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
        @Query("populate") populate: String = "profile_image",
        @Query("pagination[limit]") limit: Int = 100,
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

