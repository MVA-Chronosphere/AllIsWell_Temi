package com.example.alliswelltemi.utils

import android.util.Log
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.OkHttpClient
import okhttp3.Request
import okhttp3.RequestBody.Companion.toRequestBody
import org.json.JSONObject
import java.io.IOException
import java.util.concurrent.TimeUnit

/**
 * Feedback Service for AllIsWell Hospital
 * Sends patient feedback to PHP backend at alliswellhospital.com
 */
object FeedbackService {

    private const val TAG = "FeedbackService"
    private const val FEEDBACK_API_URL = "https://alliswellhospital.com/feedback_api.php"

    private val httpClient = OkHttpClient.Builder()
        .connectTimeout(10, TimeUnit.SECONDS)
        .readTimeout(10, TimeUnit.SECONDS)
        .writeTimeout(10, TimeUnit.SECONDS)
        .build()

    /**
     * Submit feedback to backend
     *
     * @param rating Star rating (1-5)
     * @param comment Patient comment
     * @param patientId Optional patient ID
     * @return FeedbackResponse with success/error status
     */
    suspend fun submitFeedback(
        rating: Int,
        comment: String,
        patientId: String? = null
    ): FeedbackResponse = withContext(Dispatchers.IO) {
        return@withContext try {
            // Validate inputs
            if (rating < 1 || rating > 5) {
                return@withContext FeedbackResponse(
                    success = false,
                    message = "Rating must be between 1 and 5"
                )
            }

            if (comment.isBlank()) {
                return@withContext FeedbackResponse(
                    success = false,
                    message = "Comment cannot be empty"
                )
            }

            if (comment.length > 500) {
                return@withContext FeedbackResponse(
                    success = false,
                    message = "Comment is too long (max 500 characters)"
                )
            }

            // Build JSON payload
            val jsonBody = JSONObject().apply {
                put("rating", rating)
                put("comment", comment)
                put("timestamp", getCurrentTimestamp())
                if (!patientId.isNullOrEmpty()) {
                    put("patient_id", patientId)
                }
            }

            Log.d(TAG, "Submitting feedback: rating=$rating, comment=$comment")

            // Create request
            val requestBody = jsonBody.toString()
                .toRequestBody("application/json".toMediaType())

            val request = Request.Builder()
                .url(FEEDBACK_API_URL)
                .post(requestBody)
                .addHeader("Content-Type", "application/json")
                .addHeader("User-Agent", "AlliswellTemi/1.0")
                .build()

            // Execute request
            val response = httpClient.newCall(request).execute()

            return@withContext if (response.isSuccessful) {
                val responseBody = response.body?.string() ?: ""
                val jsonResponse = JSONObject(responseBody)
                val status = jsonResponse.getString("status")
                val message = jsonResponse.optString("message", "Feedback submitted")
                val feedbackId = jsonResponse.optInt("feedback_id", -1)

                Log.d(TAG, "✅ Feedback submitted successfully! ID: $feedbackId")
                FeedbackResponse(
                    success = status == "success",
                    message = message,
                    feedbackId = feedbackId
                )
            } else {
                val errorBody = response.body?.string() ?: "Unknown error"
                Log.e(TAG, "❌ Feedback submission failed: HTTP ${response.code} - $errorBody")
                FeedbackResponse(
                    success = false,
                    message = "Server error: ${response.code}"
                )
            }

        } catch (e: IOException) {
            Log.e(TAG, "❌ Network error: ${e.message}", e)
            FeedbackResponse(
                success = false,
                message = "Network error: ${e.message ?: "Unknown error"}"
            )
        } catch (e: Exception) {
            Log.e(TAG, "❌ Error submitting feedback: ${e.message}", e)
            FeedbackResponse(
                success = false,
                message = "Error: ${e.message ?: "Unknown error"}"
            )
        }
    }

    /**
     * Get current timestamp in MySQL format
     */
    private fun getCurrentTimestamp(): String {
        return java.text.SimpleDateFormat("yyyy-MM-dd HH:mm:ss").apply {
            timeZone = java.util.TimeZone.getTimeZone("UTC")
        }.format(java.util.Date())
    }
}

/**
 * Response data class for feedback submission
 */
data class FeedbackResponse(
    val success: Boolean,
    val message: String,
    val feedbackId: Int = -1
)

