package com.example.alliswelltemi.utils

import retrofit2.Call
import retrofit2.http.Body
import retrofit2.http.Headers
import retrofit2.http.POST
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import okhttp3.OkHttpClient

// Data classes for Google Translate API

data class TranslateRequest(
    val q: String,
    val target: String,
    val source: String? = null,
    val format: String = "text"
)

data class TranslateResponse(
    val data: TranslationsData
)

data class TranslationsData(
    val translations: List<Translation>
)

data class Translation(
    val translatedText: String
)

interface GoogleTranslateApi {
    @Headers("Content-Type: application/json")
    @POST("/language/translate/v2")
    fun translate(@Body request: TranslateRequest): Call<TranslateResponse>
}

object TranslationService {
    private const val BASE_URL = "https://translation.googleapis.com"
    // TODO: Replace with your actual API key
    private const val API_KEY = "YOUR_GOOGLE_TRANSLATE_API_KEY"

    private val retrofit: Retrofit = Retrofit.Builder()
        .baseUrl(BASE_URL)
        .addConverterFactory(GsonConverterFactory.create())
        .client(OkHttpClient.Builder().build())
        .build()

    private val api: GoogleTranslateApi = retrofit.create(GoogleTranslateApi::class.java)

    fun translateText(text: String, targetLang: String, sourceLang: String? = null): String {
        try {
            val req = TranslateRequest(q = text, target = targetLang, source = sourceLang)
            val call = api.translate(req)
            val response = call.execute()
            if (response.isSuccessful) {
                val translations = response.body()?.data?.translations
                if (!translations.isNullOrEmpty()) {
                    return translations[0].translatedText
                }
            }
        } catch (e: Exception) {
            e.printStackTrace()
        }
        return text // fallback to original text on failure
    }

    fun getApiKeyParam(): String = "?key=$API_KEY"
}

