package com.example.alliswelltemi.utils

import android.media.AudioFormat
import android.media.AudioRecord
import android.media.MediaRecorder
import android.util.Log
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import kotlin.math.sqrt

/**
 * ⚠️ DEPRECATED: Use TtsLipSyncManager instead
 *
 * Real-time Lip Sync Manager (Microphone-Based - NOT RECOMMENDED)
 *
 * This class captures audio via microphone and analyzes amplitude to drive mouth animations.
 * However, it requires RECORD_AUDIO permission and does not work well with Temi robot's TTS pipeline.
 *
 * ✅ RECOMMENDED ALTERNATIVE: Use TtsLipSyncManager instead
 * - No microphone permission required
 * - Works perfectly with TTS speech
 * - Synchronized with actual robot speech output
 * - Simpler implementation
 *
 * This LipSyncManager is kept for reference only and should not be used in production.
 */
@Deprecated("Use TtsLipSyncManager instead for better TTS integration and no permission requirements")
class LipSyncManager(
    private val coroutineScope: CoroutineScope,
    private val onMouthUpdate: (jawOpen: Float, mouthOpen: Float) -> Unit
) {
    private companion object {
        const val TAG = "LipSyncManager"
        const val SAMPLE_RATE = 16000
        const val FRAME_SIZE = 512
        const val SMOOTHING_FACTOR = 0.7f
        const val UPDATE_INTERVAL_MS = 33L  // ~30 FPS throttle
        const val MIN_AMPLITUDE_THRESHOLD = 100f  // Avoid mouth movement on silence
    }

    private var audioRecord: AudioRecord? = null
    private var isListening = false
    private var smoothedAmplitude = 0f
    private var lastUpdateTime = 0L

    /**
     * Start capturing audio and analyzing for lip sync
     * Safe to call multiple times (checks isListening flag)
     */
    fun startLipSync() {
        if (isListening) {
            Log.d(TAG, "Lip sync already running, ignoring start request")
            return
        }

        isListening = true
        Log.d(TAG, "Starting lip sync audio capture")

        coroutineScope.launch(Dispatchers.Default) {
            try {
                initializeAudioCapture()
                captureAndProcessAudio()
            } catch (e: Exception) {
                Log.e(TAG, "Lip sync error: ${e.message}", e)
                stopLipSync()
            }
        }
    }

    /**
     * Stop lip sync and clean up audio resources
     * Resets mouth to neutral position (jaw=0, mouth=0)
     */
    fun stopLipSync() {
        isListening = false
        Log.d(TAG, "Stopping lip sync audio capture")

        try {
            audioRecord?.stop()
            audioRecord?.release()
            audioRecord = null
        } catch (e: Exception) {
            Log.w(TAG, "Error releasing audio record: ${e.message}")
        }

        // Reset mouth to neutral
        coroutineScope.launch(Dispatchers.Main) {
            onMouthUpdate(0f, 0f)
        }
    }

     /**
      * Initialize AudioRecord for microphone input
      * Uses 16-bit PCM mono at 16kHz (standard for speech)
      *
      * Handles permission issues gracefully with detailed logging.
      * NOTE: This is deprecated - use TtsLipSyncManager instead
      */
     private fun initializeAudioCapture() {
         try {
             val bufferSize = AudioRecord.getMinBufferSize(
                 SAMPLE_RATE,
                 AudioFormat.CHANNEL_IN_MONO,
                 AudioFormat.ENCODING_PCM_16BIT
             )

             Log.d(TAG, "AudioRecord buffer size: $bufferSize bytes")

             // Try to create AudioRecord with robust error handling
             try {
                 audioRecord = AudioRecord(
                     MediaRecorder.AudioSource.MIC,
                     SAMPLE_RATE,
                     AudioFormat.CHANNEL_IN_MONO,
                     AudioFormat.ENCODING_PCM_16BIT,
                     bufferSize * 2
                 )
             } catch (e: Exception) {
                 Log.e(TAG, "Failed to instantiate AudioRecord: ${e.message}", e)
                 audioRecord = null
                 throw e
             }

             // Verify AudioRecord was successfully created
             val createdRecord = audioRecord ?: throw IllegalStateException("Failed to create AudioRecord instance")

             // Check initialization state before starting
             if (createdRecord.state != AudioRecord.STATE_INITIALIZED) {
                 Log.e(
                     TAG,
                     "❌ AudioRecord state is not initialized: ${createdRecord.state}. " +
                     "This usually means RECORD_AUDIO permission is not granted at runtime. " +
                     "Consider using TtsLipSyncManager instead (no permission required)."
                 )
                 createdRecord.release()
                 audioRecord = null
                 throw IllegalStateException(
                     "AudioRecord failed to initialize (state=${createdRecord.state}). " +
                     "Consider using TtsLipSyncManager instead."
                 )
             }

             // Try to start recording
             try {
                 createdRecord.startRecording()
                 Log.d(TAG, "✅ AudioRecord initialized and recording started")
             } catch (e: IllegalStateException) {
                 Log.e(
                     TAG,
                     "Failed to start recording: ${e.message}. " +
                     "Device may not have microphone access or permission is denied.",
                     e
                 )
                 createdRecord.release()
                 audioRecord = null
                 throw e
             }
         } catch (e: SecurityException) {
             Log.e(
                 TAG,
                 "🔒 RECORD_AUDIO permission not granted: ${e.message}. " +
                 "This class requires microphone permission. Consider using TtsLipSyncManager instead.",
                 e
             )
             throw e
         } catch (e: Exception) {
             Log.e(TAG, "❌ Failed to initialize AudioRecord: ${e.message}", e)
             throw e
         }
     }

    /**
     * Main audio capture and processing loop
     * Runs on Dispatchers.Default (background thread)
     *
     * Process:
     * 1. Read audio frames from microphone
     * 2. Compute RMS amplitude (0-32768 range)
     * 3. Normalize to 0-1 range
     * 4. Apply smoothing filter
     * 5. Map to jaw and mouth blend shapes
     * 6. Post to Main thread via callback
     * 7. Throttle to ~30 FPS
     */
    private suspend fun captureAndProcessAudio() {
        val audioBuffer = ShortArray(FRAME_SIZE)

        while (isListening) {
            try {
                val record = audioRecord
                if (record == null) {
                    Log.w(TAG, "AudioRecord is null, stopping lip sync")
                    break
                }

                if (record.state != AudioRecord.STATE_INITIALIZED) {
                    Log.w(TAG, "AudioRecord is not in initialized state, stopping lip sync")
                    break
                }

                val bytesRead = try {
                    record.read(audioBuffer, 0, FRAME_SIZE)
                } catch (e: IllegalStateException) {
                    Log.w(TAG, "AudioRecord read failed: ${e.message}, stopping")
                    break
                }

                if (bytesRead > 0) {
                    // Compute RMS amplitude from audio samples
                    val amplitude = computeRMS(audioBuffer, bytesRead)

                    // Normalize: 16-bit PCM max is ±32768
                    val normalized = (amplitude / 32768f).coerceIn(0f, 1f)

                    // Apply exponential smoothing for jitter reduction
                    val smoothed = applySmoothing(normalized)

                    // Skip if below threshold (silence/noise)
                    if (normalized < MIN_AMPLITUDE_THRESHOLD / 32768f) {
                        smoothedAmplitude = smoothedAmplitude * 0.9f  // Decay smoothly to 0
                    }

                    // Throttle updates to ~30 FPS (33ms)
                    val now = System.currentTimeMillis()
                    if (now - lastUpdateTime >= UPDATE_INTERVAL_MS) {
                        // Map amplitude to blend shape ranges
                        // jawOpen (0-0.7): controls jaw drop
                        // mouthOpen (0-1.0): controls mouth opening
                        val jawOpen = (smoothed * 0.7f).coerceIn(0f, 0.7f)
                        val mouthOpen = (smoothed * 1.0f).coerceIn(0f, 1f)

                        lastUpdateTime = now

                        // Post update to Main thread
                        withContext(Dispatchers.Main) {
                            try {
                                onMouthUpdate(jawOpen, mouthOpen)
                            } catch (e: Exception) {
                                Log.e(TAG, "Error in mouth update callback: ${e.message}")
                            }
                        }
                    }
                } else if (bytesRead == AudioRecord.ERROR_INVALID_OPERATION) {
                    Log.w(TAG, "AudioRecord.read() returned ERROR_INVALID_OPERATION, stopping")
                    break
                } else if (bytesRead == AudioRecord.ERROR_BAD_VALUE) {
                    Log.w(TAG, "AudioRecord.read() returned ERROR_BAD_VALUE, stopping")
                    break
                } else {
                    Log.w(TAG, "Failed to read audio data (bytes: $bytesRead)")
                }

                // Allow coroutine to be cancelled
                kotlinx.coroutines.yield()
            } catch (e: Exception) {
                if (isListening) {  // Only log if we're still supposed to be listening
                    Log.e(TAG, "Error in audio capture loop: ${e.message}", e)
                }
            }
        }

        Log.d(TAG, "Audio capture loop ended")
    }

    /**
     * Compute RMS (Root Mean Square) amplitude of audio signal
     *
     * Formula: sqrt(sum(sample^2) / count)
     * Measures perceived loudness of audio
     *
     * @param audioBuffer Array of 16-bit PCM samples
     * @param sampleCount Number of valid samples to process
     * @return RMS amplitude (0-32768 for 16-bit)
     */
    private fun computeRMS(audioBuffer: ShortArray, sampleCount: Int): Float {
        var sumOfSquares = 0.0

        for (i in 0 until sampleCount) {
            val sample = audioBuffer[i].toDouble()
            sumOfSquares += sample * sample
        }

        val meanSquare = sumOfSquares / sampleCount
        return sqrt(meanSquare).toFloat()
    }

    /**
     * Apply exponential smoothing to reduce jitter
     *
     * Formula: smoothed = α * smoothed + (1-α) * current
     * Interpolates between current value and previous smoothed value
     *
     * α (SMOOTHING_FACTOR) = 0.7:
     * - 70% from previous value (history)
     * - 30% from current value (responsiveness)
     *
     * Higher α = more smoothing but less responsive
     * Lower α = more responsive but jittery
     */
    private fun applySmoothing(newValue: Float): Float {
        smoothedAmplitude = SMOOTHING_FACTOR * smoothedAmplitude +
                           (1 - SMOOTHING_FACTOR) * newValue
        return smoothedAmplitude
    }

    /**
     * Release all resources (called on app cleanup)
     */
    fun release() {
        stopLipSync()
    }
}

