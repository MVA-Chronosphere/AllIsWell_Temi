package com.example.alliswelltemi.utils

import android.util.Base64
import android.util.Log
import android.webkit.WebView
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import java.io.ByteArrayOutputStream

/**
 * Avatar Controller - Production-Ready Bridge for Three.js Avatar + Temi TTS
 *
 * Responsibilities:
 * - Bridge Kotlin ↔ WebView JavaScript (Three.js avatar in avatar-view.html)
 * - Handle Temi TTS audio bytes and convert to playable format
 * - Manage lip-sync via HeadAudio (real-time) + fallback Rhubarb cues
 * - Orchestrate hand gestures and head animations
 * - Ensure no memory leaks or per-frame allocations
 *
 * Audio Pipeline:
 * 1. Temi TTS → audio bytes (WAV/PCM)
 * 2. Android: Base64 encode audio → data URI
 * 3. WebView: window.TemiInterface.playAudio(dataUri, mouthCues)
 * 4. JavaScript: HeadAudio analyzes audio → real-time visemes
 * 5. Three.js: Morph target weights applied → animated mouth
 *
 * Asset Paths (served via WebViewAssetLoader):
 * - GLB model: /assets/models/indian_doctor_lipsync.glb
 * - HeadAudio: /assets/headaudio/headaudio.min.mjs, headworklet.min.mjs, model-en-mixed.bin
 * - Page URL: https://appassets.androidplatform.net/assets/avatar-view.html
 */
class AvatarController(
    private val webView: WebView?,
    private val coroutineScope: CoroutineScope
) {
    private companion object {
        const val TAG = "AvatarController"
        const val READY_TIMEOUT_MS = 3000L
        const val JS_ENCODING = "UTF-8"
    }

    @Volatile
    private var isReady = false
    private var readyChecked = false
    private var currentSpeechDuration = 0f

    /**
     * Initialize avatar readiness check
     * Must be called after onPageFinished is triggered
     */
    fun checkReadiness() {
        if (readyChecked) return
        readyChecked = true

        coroutineScope.launch(Dispatchers.Main) {
            webView?.evaluateJavascript(
                "window.TemiInterface && typeof window.TemiInterface.isReady === 'function' ? window.TemiInterface.isReady() : false",
                { result ->
                    isReady = result == "true"
                    if (isReady) {
                        Log.d(TAG, "✓ Avatar ready: model loaded, HeadAudio initialized")
                    } else {
                        Log.w(TAG, "⚠ Avatar not ready yet, proceeding anyway (may be delayed)")
                    }
                }
            )
        }
    }

    /**
     * Play Temi TTS audio with lip-sync in the avatar
     *
     * @param audioBytes Raw audio bytes (WAV or PCM)
     * @param mimeType Audio MIME type (e.g., "audio/wav", "audio/mpeg")
     * @param speechDuration Estimated duration in seconds for gesture timing
     * @param mouthCuesJson Optional Rhubarb cues for fallback (JSON string)
     */
    fun playTemiAudioBytes(
        audioBytes: ByteArray,
        mimeType: String = "audio/wav",
        speechDuration: Float = 5f,
        mouthCuesJson: String = "[]"
    ) {
        this.currentSpeechDuration = speechDuration

        coroutineScope.launch(Dispatchers.Main) {
            try {
                // Encode audio to base64 (no per-frame allocations)
                val base64Audio = Base64.encodeToString(audioBytes, Base64.NO_WRAP)
                val dataUri = "data:$mimeType;base64,$base64Audio"

                Log.d(TAG, "▶ Playing Temi audio: ${audioBytes.size} bytes, ${mimeType}, duration: ${speechDuration}s")

                // Build JavaScript call
                val jsCode = """
                    (function() {
                        if (window.TemiInterface && typeof window.TemiInterface.playAudio === 'function') {
                            const cues = ${mouthCuesJson};
                            window.TemiInterface.playAudio('$dataUri', cues);
                            console.log('[AvatarController] playAudio called with ${audioBytes.size} bytes');
                        } else {
                            console.warn('[AvatarController] TemiInterface.playAudio not available');
                        }
                    })();
                """.trimIndent()

                webView?.evaluateJavascript(jsCode, null)

            } catch (e: Exception) {
                Log.e(TAG, "✗ Error playing audio: ${e.message}", e)
            }
        }
    }

    /**
     * Play audio from a data URI (already encoded)
     * Useful if audio is pre-processed server-side
     *
     * @param audioDataUri data:audio/...;base64,... or https://...
     * @param mouthCuesJson Optional Rhubarb cues (JSON string)
     */
    fun playAudioUri(
        audioDataUri: String,
        mouthCuesJson: String = "[]"
    ) {
        coroutineScope.launch(Dispatchers.Main) {
            try {
                val jsCode = """
                    (function() {
                        if (window.TemiInterface && typeof window.TemiInterface.playAudio === 'function') {
                            const cues = ${mouthCuesJson};
                            window.TemiInterface.playAudio('$audioDataUri', cues);
                            console.log('[AvatarController] playAudioUri called');
                        } else {
                            window.postMessage({
                                type: 'playAudioWithLipSync',
                                audioUrl: '$audioDataUri',
                                _mouthCues: ${mouthCuesJson}
                            }, '*');
                        }
                    })();
                """.trimIndent()

                webView?.evaluateJavascript(jsCode, null)
                Log.d(TAG, "▶ Playing audio URI (length: ${audioDataUri.length} chars)")

            } catch (e: Exception) {
                Log.e(TAG, "✗ Error playing audio URI: ${e.message}", e)
            }
        }
    }

    /**
     * Stop current speech and reset avatar to idle
     */
    fun stopSpeech() {
        coroutineScope.launch(Dispatchers.Main) {
            try {
                val jsCode = """
                    (function() {
                        if (window.TemiInterface && typeof window.TemiInterface.stop === 'function') {
                            window.TemiInterface.stop();
                        }
                    })();
                """.trimIndent()

                webView?.evaluateJavascript(jsCode, null)
                Log.d(TAG, "⏹ Speech stopped")

            } catch (e: Exception) {
                Log.e(TAG, "✗ Error stopping speech: ${e.message}", e)
            }
        }
    }

    /**
     * Get avatar status (ready, speaking, gesturing, etc.)
     *
     * @return JavaScript object with status fields, or null if not ready
     */
    fun getAvatarStatus() {
        coroutineScope.launch(Dispatchers.Main) {
            try {
                webView?.evaluateJavascript(
                    "window.TemiInterface && typeof window.TemiInterface.getStatus === 'function' ? JSON.stringify(window.TemiInterface.getStatus()) : null",
                    { result ->
                        Log.d(TAG, "📊 Avatar status: $result")
                    }
                )
            } catch (e: Exception) {
                Log.w(TAG, "Could not get status: ${e.message}")
            }
        }
    }

    /**
     * Play a named animation on the avatar (wave, idle, listening, etc.)
     * Falls back gracefully if animation doesn't exist in model
     *
     * @param animationName Name of animation (e.g., "wave", "idle", "listening", "thinking")
     */
    fun playAnimation(animationName: String) {
        coroutineScope.launch(Dispatchers.Main) {
            try {
                val jsCode = """
                    (function() {
                        if (window.TemiInterface && typeof window.TemiInterface.wave === 'function' && '$animationName' === 'wave') {
                            window.TemiInterface.wave();
                        }
                        console.log('[AvatarController] playAnimation: $animationName');
                    })();
                """.trimIndent()

                webView?.evaluateJavascript(jsCode, null)
                Log.d(TAG, "▶ Animation: $animationName")

            } catch (e: Exception) {
                Log.e(TAG, "✗ Error playing animation: ${e.message}", e)
            }
        }
    }

    /**
     * Start speaking mode with optional gesture
     * Prepares avatar for TTS playback and gestures
     *
     * @param gesture Optional gesture type (e.g., "talking")
     */
    fun startSpeaking(gesture: String = "talking") {
        coroutineScope.launch(Dispatchers.Main) {
            try {
                Log.d(TAG, "▶ Start speaking: gesture=$gesture")
                // Gesturing is handled by avatar-view.html when playTemiAudioBytes() is called
                // This is just a state marker for the controller
            } catch (e: Exception) {
                Log.e(TAG, "✗ Error in startSpeaking: ${e.message}", e)
            }
        }
    }

    /**
     * Trigger wave gesture (first response greeting)
     */
    fun wave() {
        coroutineScope.launch(Dispatchers.Main) {
            try {
                val jsCode = """
                    (function() {
                        if (window.TemiInterface && typeof window.TemiInterface.wave === 'function') {
                            window.TemiInterface.wave();
                            console.log('[AvatarController] Wave gesture triggered');
                        }
                    })();
                """.trimIndent()

                webView?.evaluateJavascript(jsCode, null)
                Log.d(TAG, "👋 Wave gesture triggered")

            } catch (e: Exception) {
                Log.e(TAG, "✗ Error triggering wave: ${e.message}", e)
            }
        }
    }

    /**
     * Manually set a viseme (for testing)
     *
     * @param visemeName Oculus viseme name (e.g., "viseme_aa", "viseme_PP")
     * @param weight Morph target weight (0-1)
     */
    fun setViseme(visemeName: String, weight: Float) {
        coroutineScope.launch(Dispatchers.Main) {
            try {
                val jsCode = """
                    (function() {
                        if (window.setViseme) {
                            window.setViseme('$visemeName', $weight);
                        }
                    })();
                """.trimIndent()

                webView?.evaluateJavascript(jsCode, null)

            } catch (e: Exception) {
                Log.e(TAG, "✗ Error setting viseme: ${e.message}", e)
            }
        }
    }

    /**
     * Inject a message listener for audio end events
     * This allows JavaScript to notify Kotlin when audio finishes
     */
    fun setupWebMessageListener() {
        // This would be set up in the hosting Activity/Fragment if needed
        Log.d(TAG, "Web message listener setup (implement in hosting component)")
    }

    /**
     * Check if WebView is ready to receive commands
     */
    fun isReady(): Boolean = isReady

    /**
     * Release all resources (call on Activity destroy)
     */
    fun release() {
        Log.d(TAG, "🏁 Releasing AvatarController")
        stopSpeech()
        isReady = false
        readyChecked = false
        currentSpeechDuration = 0f
    }
}
