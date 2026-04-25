package com.example.alliswelltemi.network

import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import java.util.concurrent.TimeUnit

/**
 * Retrofit Client for Strapi CMS API
 * PERFORMANCE OPTIMIZED: Connection pooling + reduced timeouts
 */
object RetrofitClient {
    private const val BASE_URL = "https://aiwcms.chronosphere.in/"

    private val loggingInterceptor = HttpLoggingInterceptor().apply {
        level = HttpLoggingInterceptor.Level.BODY  // Increased to BODY for detailed debugging of API responses
    }

    private val httpClient = OkHttpClient.Builder()
        .addInterceptor(loggingInterceptor)
        .connectTimeout(10, TimeUnit.SECONDS)  // Reduced from 30s - faster failure detection
        .readTimeout(15, TimeUnit.SECONDS)     // Reduced from 30s - typical API response time
        .writeTimeout(10, TimeUnit.SECONDS)    // Reduced from 30s
        .retryOnConnectionFailure(true)        // Auto-retry on connection failures
        .connectionPool(
            okhttp3.ConnectionPool(
                maxIdleConnections = 5,
                keepAliveDuration = 5,
                timeUnit = TimeUnit.MINUTES
            )
        )  // Reuse connections for faster subsequent requests
        .build()

    val apiService: StrapiApiService by lazy {
        Retrofit.Builder()
            .baseUrl(BASE_URL)
            .client(httpClient)
            .addConverterFactory(GsonConverterFactory.create())
            .build()
            .create(StrapiApiService::class.java)
    }
}
