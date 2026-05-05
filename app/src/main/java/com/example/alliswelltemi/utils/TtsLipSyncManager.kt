package com.example.alliswelltemi.utils

import android.util.Log
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import kotlin.math.sin

/**
 * TTS-Based Lip Sync Manager with Rhubarb Phoneme → Oculus Viseme Mapping
 *
 * Maps speech phonemes to industry-standard Oculus/Meta visemes for realistic lip sync.
 * Uses text analysis to estimate phoneme timing and map to appropriate mouth shapes.
 *
 * Full Oculus Viseme Set (15 total):
 * - viseme_sil: Silence/rest
 * - viseme_PP: P, B, M - lips pressed together
 * - viseme_FF: F, V - lip to teeth
 * - viseme_TH: TH - tongue between teeth
 * - viseme_DD: D, T, N - tongue tip to palate
 * - viseme_kk: K, G - back of tongue
 * - viseme_CH: CH, J, SH - tongue raised
 * - viseme_SS: S, Z - tongue tip near palate
 * - viseme_nn: NG - nasal
 * - viseme_RR: R - tongue curl
 * - viseme_aa: A (father) - wide open
 * - viseme_E: E (bed) - mid open
 * - viseme_I: I (beat) - wide spread
 * - viseme_O: O (boat) - rounded
 * - viseme_U: U (boot) - tight round
 *
 * Rhubarb Phoneme Mapping (9 key shapes):
 * A (viseme_PP): MBP sounds - lips pressed
 * B (viseme_I): EE sounds - wide spread
 * C (viseme_E): EH sounds - mid open
 * D (viseme_aa): AI/AH sounds - wide open jaw
 * E (viseme_O): OH sounds - rounded lips
 * F (viseme_U): OO/W sounds - tight rounded
 * G (viseme_FF): F/V sounds - lip to teeth
 * H (viseme_TH): L/TH sounds - tongue visible
 * X (viseme_sil): Silence/rest
 */
class TtsLipSyncManager(
    private val coroutineScope: CoroutineScope,
    private val onVisemeUpdate: (viseme: String, intensity: Float) -> Unit
) {
    private companion object {
        const val TAG = "TtsLipSyncManager"
        const val FRAME_INTERVAL_MS = 33L  // ~30 FPS
        const val SPEECH_RATE_CHARS_PER_SEC = 15f

        // Rhubarb phoneme to Oculus viseme mapping (9 core shapes)
        val PHONEME_TO_VISEME = mapOf(
            'A' to "viseme_PP",   // MBP - lips pressed
            'B' to "viseme_I",    // EE - wide spread
            'C' to "viseme_E",    // EH - mid open
            'D' to "viseme_aa",   // AI - wide open
            'E' to "viseme_O",    // OH - rounded open
            'F' to "viseme_U",    // OO/W - tight round
            'G' to "viseme_FF",   // F/V - labiodental
            'H' to "viseme_TH",   // L/TH - tongue tip
            'X' to "viseme_sil"   // rest/silence
        )

        // Full Oculus viseme list (all 15 for reset/traversal)
        val ALL_OCULUS_VISEMES = listOf(
            "viseme_sil", "viseme_PP", "viseme_FF", "viseme_TH", "viseme_DD",
            "viseme_kk", "viseme_CH", "viseme_SS", "viseme_nn", "viseme_RR",
            "viseme_aa", "viseme_E", "viseme_I", "viseme_O", "viseme_U"
        )
    }

    private var isAnimating = false
    private var ttsText = ""
    private var startTimeMs = 0L

    /**
     * Start TTS-based lip sync animation
     * @param speech The text being spoken by TTS
     */
    fun startLipSync(speech: String) {
        if (isAnimating) {
            Log.d(TAG, "Lip sync already running, ignoring start request")
            return
        }

        ttsText = speech
        isAnimating = true
        startTimeMs = System.currentTimeMillis()

        Log.d(TAG, "✅ Starting TTS lip sync with visemes: '${speech.take(50)}...'")

        coroutineScope.launch(Dispatchers.Main) {
            animateMouthFromTts()
        }
    }

    /**
     * Stop TTS-based lip sync and reset mouth to neutral
     */
    fun stopLipSync() {
        if (!isAnimating) return

        isAnimating = false
        Log.d(TAG, "🛑 Stopping TTS lip sync")

        // Reset to silence viseme
        coroutineScope.launch(Dispatchers.Main) {
            onVisemeUpdate("viseme_sil", 0f)
        }
    }

    /**
     * Main animation loop - drives mouth shapes based on text analysis
     * Estimates phoneme timing from character count and speech rate
     */
    private suspend fun animateMouthFromTts() {
        // Estimate total duration: chars / (chars per second) = duration in seconds
        val estimatedDurationMs = ((ttsText.length / SPEECH_RATE_CHARS_PER_SEC) * 1000).toLong()

        Log.d(TAG, "📊 Viseme animation: ${ttsText.length} chars, ${estimatedDurationMs}ms duration")

        var frameCount = 0
        while (isAnimating) {
            val elapsedMs = System.currentTimeMillis() - startTimeMs

            // Stop if we've exceeded estimated duration + 500ms buffer
            if (elapsedMs > estimatedDurationMs + 500) {
                stopLipSync()
                break
            }

            // Calculate mouth movement based on:
            // 1. Position in speech (0.0 to 1.0)
            // 2. Phoneme analysis of current position
            val progress = (elapsedMs.toFloat() / estimatedDurationMs).coerceIn(0f, 1f)

            // Estimate current character position
            val currentCharIndex = (progress * ttsText.length).toInt().coerceIn(0, ttsText.length - 1)
            val currentChar = if (currentCharIndex < ttsText.length) ttsText[currentCharIndex] else ' '

            // Map character to phoneme, then to viseme
            val (viseme, intensity) = mapCharToViseme(currentChar, progress)
            onVisemeUpdate(viseme, intensity)

            frameCount++
            delay(FRAME_INTERVAL_MS)
        }

        Log.d(TAG, "Viseme animation ended (${frameCount} frames)")
    }

    /**
     * Map character to appropriate viseme with intensity
     * Returns (visemeName, intensity 0-1)
     */
    private fun mapCharToViseme(char: Char, progress: Float): Pair<String, Float> {
        val lower = char.lowercaseChar()

        // LIPSYNC REFINEMENT: Reduce base intensity to prevent "alien" wide-open mouth.
        // Fluctuates between 0.35 and 0.65 for a more natural human range.
        val baseIntensity = 0.5f + (sin(progress * 6.28f * 4) * 0.15f)

        val phoneme = when {
            // Vowels mapping
            lower == 'a' -> 'D'  // AA - wide open (damped in WebView)
            lower == 'e' -> 'C'  // EH - mid open
            lower == 'i' -> 'B'  // EE - wide spread
            lower == 'o' -> 'E'  // OH - rounded
            lower == 'u' -> 'F'  // OO - tight round

            // Consonants mapping
            lower == 'p' || lower == 'b' || lower == 'm' -> 'A'  // Bilabial - lips pressed
            lower == 'f' || lower == 'v' -> 'G'  // Labiodental - lip to teeth
            lower == 't' || lower == 'd' || lower == 'n' || lower == 'l' -> 'H'  // Dental/Alveolar - tongue tip
            lower == 'h' -> 'H'  // Also use tongue tip position for H
            lower == 'w' -> 'F'  // Rounded like U
            lower == 'y' || lower == 'i' -> 'B'  // Like EE

            // Sibilants and fricatives
            lower == 's' || lower == 'z' -> 'B'  // Teeth together, smile-like
            lower == 'r' -> 'E'  // Rounded position
            lower == 'c' || lower == 'k' || lower == 'g' || lower == 'q' -> 'D'  // Back consonants
            lower == 'j' -> 'B'  // Like EE with friction
            lower == 'x' -> 'C'  // Mid position

            // Space/punctuation
            lower == ' ' || !lower.isLetter() -> 'X'  // Silence

            // Default - mid position
            else -> 'C'
        }

        val viseme = PHONEME_TO_VISEME[phoneme] ?: "viseme_sil"
        
        // Final intensity with a slight random jitter for realism
        val randomJitter = (Math.random().toFloat() - 0.5f) * 0.05f
        val intensity = if (phoneme == 'X') 0f else (baseIntensity + randomJitter).coerceIn(0f, 1f)

        return Pair(viseme, intensity)
    }

    /**
     * Get current animation state
     */
    fun isActive(): Boolean = isAnimating

    /**
     * Release resources
     */
    fun release() {
        stopLipSync()
    }
}
