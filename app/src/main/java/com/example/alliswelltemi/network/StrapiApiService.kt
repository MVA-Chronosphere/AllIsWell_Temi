package com.example.alliswelltemi.network

import com.example.alliswelltemi.data.DoctorsApiResponse
import retrofit2.http.GET
import retrofit2.http.Query

/**
 * Strapi API Service for fetching doctors
 */
interface StrapiApiService {

    @GET("api/doctors")
    suspend fun getDoctors(
        @Query("populate[profile_image][fields]") imageFields: String = "url,name,formats",
        @Query("pagination[limit]") limit: Int = 1000,
        @Query("sort") sort: String = "name:asc"
    ): DoctorsApiResponse
}

