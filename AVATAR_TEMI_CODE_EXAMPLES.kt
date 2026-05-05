/**
 * AVATAR + TEMI TTS INTEGRATION - CODE EXAMPLES
 *
 * This file shows concrete code snippets for integrating the avatar
 * into your MainActivity and screens. Copy-paste ready!
 */

// ═════════════════════════════════════════════════════════════════════════
// PART 1: MainActivity.kt Hook - Respond to Temi TTS
// ═════════════════════════════════════════════════════════════════════════

// Add to your MainActivity class:

package com.example.alliswelltemi

import com.example.alliswelltemi.utils.AvatarController
import android.webkit.WebView

class MainActivity : ComponentActivity(),
    Robot.TtsListener {

    // ... existing code ...

    private var avatarController: AvatarController? = null
    private var webView: WebView? = null

    // STEP 1: Initialize avatar controller when WebView is ready
    // Call this from your Compose screen's onWebViewReady callback
    fun initializeAvatar(webView: WebView) {
        this.webView = webView
        avatarController = AvatarController(webView, lifecycleScope)

        // Check if avatar is ready (model loaded, HeadAudio initialized)
        lifecycleScope.launch {
            delay(1000)  // Wait for page to fully load
            avatarController?.checkReadiness()
        }
    }

    // STEP 2: Hook into Temi TTS lifecycle
    override fun onTtsStatusChanged(ttsRequest: TtsRequest?) {
        when (ttsRequest?.status) {
            TtsRequest.Status.STARTED -> {
                // Speech is starting
                Log.d("AvatarTTS", "TTS started: ${ttsRequest.speech}")

                // Estimate speech duration (rough heuristic)
                val estimatedDuration = estimateSpeechDuration(ttsRequest.speech)

                // Optional: Wave on first response
                if (isFirstResponse) {
                    avatarController?.wave()
                    isFirstResponse = false
                }
            }

            TtsRequest.Status.COMPLETED -> {
                // Speech finished
                Log.d("AvatarTTS", "TTS completed")
                avatarController?.stopSpeech()
            }

            TtsRequest.Status.ERROR -> {
                Log.e("AvatarTTS", "TTS error: ${ttsRequest.errorBody}")
                avatarController?.stopSpeech()
            }

            else -> {}
        }
    }

    // Estimate speech duration from text (very rough)
    private fun estimateSpeechDuration(text: String): Float {
        // Average speaking speed: ~150 words/minute = 2.5 words/second
        // Average word length: ~5 characters
        // So: 0.4 chars/second ≈ 1 second per 2.5 words
        return (text.length / 15f).coerceAtLeast(0.5f)
    }

    private var isFirstResponse = true

    // STEP 3: If Temi SDK provides audio bytes (check your version),
    // hook them here:
    /*
    private fun onAudioBytesFromTemi(audioBytes: ByteArray, sampleRate: Int) {
        // Wrap raw PCM in WAV format if needed
        val wavAudio = if (shouldWrapInWav) {
            WavAudioFormat.createWavFile(
                sampleRate = sampleRate,
                numChannels = 1,
                bitsPerSample = 16,
                audioData = audioBytes
            )
        } else {
            audioBytes
        }

        // Play in avatar with estimated duration
        val duration = audioBytes.size.toFloat() / (sampleRate * 2)
        avatarController?.playTemiAudioBytes(
            audioBytes = wavAudio,
            mimeType = "audio/wav",
            speechDuration = duration,
            mouthCuesJson = "[]"
        )
    }
    */
}

// ═════════════════════════════════════════════════════════════════════════
// PART 2: Compose Screen Integration - Replace Model3DViewer
// ═════════════════════════════════════════════════════════════════════════

// In your main Compose screen (e.g., TemiMainScreen.kt or a new AvatarScreen.kt):

package com.example.alliswelltemi.ui.screens

import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import com.example.alliswelltemi.MainActivity
import com.example.alliswelltemi.ui.components.AvatarWebViewComponent

@Composable
fun AvatarDisplayScreen(
    modifier: Modifier = Modifier,
    onAvatarReady: (() -> Unit)? = null
) {
    val context = LocalContext.current

    // Reference to MainActivity for avatar initialization
    // (In a real app, use dependency injection or ViewModel)
    val mainActivity = remember { context as? MainActivity }

    AvatarWebViewComponent(
        modifier = modifier.fillMaxSize(),
        onWebViewReady = { webView ->
            // Initialize AvatarController when WebView is ready
            mainActivity?.initializeAvatar(webView)

            // Optional callback to parent
            onAvatarReady?.invoke()

            Log.d("AvatarScreen", "WebView ready, AvatarController initialized")
        }
    )
}

// ─── Usage in your main screen ───
@Composable
fun TemiMainScreen(
    robot: Robot? = null,
    onNavigate: (String) -> Unit = {}
) {
    Box(modifier = Modifier.fillMaxSize()) {
        // Show avatar in the background
        AvatarDisplayScreen(
            modifier = Modifier
                .fillMaxWidth()
                .fillMaxHeight(0.7f)  // 70% of screen height
        )

        // Your existing UI overlaid on top
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(top = 200.dp)  // Below avatar
        ) {
            // Your menu cards, buttons, etc.
        }
    }
}

// ═════════════════════════════════════════════════════════════════════════
// PART 3: Testing - Standalone Test Fragment/Activity
// ═════════════════════════════════════════════════════════════════════════

package com.example.alliswelltemi.test

import android.webkit.WebView
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.viewModels
import androidx.compose.foundation.layout.*
import androidx.compose.material3.Button
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import com.example.alliswelltemi.ui.components.AvatarWebViewComponent
import com.example.alliswelltemi.utils.AvatarController
import com.robotemi.sdk.Robot
import kotlinx.coroutines.delay

// Test Activity to verify avatar plays audio
class AvatarTestActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            AvatarTestScreen()
        }
    }

    @Composable
    fun AvatarTestScreen() {
        var avatarController by remember { mutableStateOf<AvatarController?>(null) }
        var status by remember { mutableStateOf("Initializing...") }

        Column(modifier = Modifier.fillMaxSize()) {
            // Avatar display (80% of screen)
            Box(
                modifier = Modifier
                    .weight(0.8f)
                    .fillMaxWidth()
            ) {
                AvatarWebViewComponent(
                    modifier = Modifier.fillMaxSize(),
                    onWebViewReady = { webView ->
                        avatarController = AvatarController(webView, lifecycleScope)
                        avatarController?.checkReadiness()
                        status = "Avatar ready"
                    }
                )
            }

            // Control buttons (20% of screen)
            Column(
                modifier = Modifier
                    .weight(0.2f)
                    .fillMaxWidth()
                    .padding(16.dp),
                verticalArrangement = Arrangement.SpaceEvenly
            ) {
                Text(status)

                Button(onClick = {
                    // Simulate Temi TTS with a test audio file
                    lifecycleScope.launch {
                        status = "Playing test audio..."

                        // Create a simple beep (1 second, 440 Hz sine wave)
                        val audioBytes = createTestAudio()

                        avatarController?.playTemiAudioBytes(
                            audioBytes = audioBytes,
                            mimeType = "audio/wav",
                            speechDuration = 1.0f,
                            mouthCuesJson = "[]"
                        )

                        delay(2000)
                        status = "Done"
                    }
                }) {
                    Text("Play Test Audio")
                }

                Button(onClick = {
                    avatarController?.wave()
                    status = "Wave gesture"
                }) {
                    Text("Wave")
                }

                Button(onClick = {
                    avatarController?.stopSpeech()
                    status = "Stopped"
                }) {
                    Text("Stop")
                }
            }
        }
    }

    private fun createTestAudio(): ByteArray {
        // Create a simple 1-second WAV file (sine wave, 440 Hz)
        val sampleRate = 16000
        val duration = 1.0f
        val frequency = 440.0  // A4 note

        val numSamples = (sampleRate * duration).toInt()
        val pcmData = ShortArray(numSamples)
        val amplitude = 32767 * 0.3f  // 30% amplitude

        for (i in 0 until numSamples) {
            val angle = 2.0 * Math.PI * frequency * i / sampleRate
            pcmData[i] = (amplitude * Math.sin(angle)).toShort()
        }

        return WavAudioFormat.createWavFile(
            sampleRate = sampleRate,
            numChannels = 1,
            bitsPerSample = 16,
            audioData = pcmData.asByteArray()
        )
    }
}

// ═════════════════════════════════════════════════════════════════════════
// PART 4: Advanced - Custom Rhubarb Cues
// ═════════════════════════════════════════════════════════════════════════

// If you have pre-computed Rhubarb mouth cues from an external service:
// (see: https://github.com/DanielStuhlman/Rhubarb-Lip-Sync)

fun playAudioWithRhubarbCues(
    avatarController: AvatarController,
    audioBytes: ByteArray,
    rhubarbJson: String  // [{"start": 0.0, "end": 0.12, "value": "X"}, ...]
) {
    avatarController.playTemiAudioBytes(
        audioBytes = audioBytes,
        mimeType = "audio/wav",
        speechDuration = 5f,
        mouthCuesJson = rhubarbJson
    )
}

// Example Rhubarb output
val RHUBARB_EXAMPLE = """
[
  {"start": 0.00, "end": 0.12, "value": "X"},
  {"start": 0.12, "end": 0.28, "value": "A"},
  {"start": 0.28, "end": 0.44, "value": "D"},
  {"start": 0.44, "end": 0.58, "value": "B"},
  {"start": 0.58, "end": 0.74, "value": "C"},
  {"start": 0.74, "end": 0.90, "value": "E"},
  {"start": 0.90, "end": 1.06, "value": "F"},
  {"start": 1.06, "end": 1.22, "value": "G"},
  {"start": 1.22, "end": 1.38, "value": "H"},
  {"start": 1.38, "end": 1.54, "value": "D"},
  {"start": 1.54, "end": 2.00, "value": "X"}
]
"""

// In avatar-view.html, these map to Oculus visemes:
// A → viseme_PP (lips pressed, M/B/P)
// B → viseme_I (wide spread, EE)
// C → viseme_E (mid open, EH)
// D → viseme_aa (wide open, AH)
// E → viseme_O (rounded open, OH)
// F → viseme_U (tight round, OO/W)
// G → viseme_FF (labiodental, F/V)
// H → viseme_TH (tongue tip, L/TH)
// X → viseme_sil (silence/rest)

// ═════════════════════════════════════════════════════════════════════════
// PART 5: Cleanup - Call on Activity destroy
// ═════════════════════════════════════════════════════════════════════════

// In MainActivity.onDestroy():
override fun onDestroy() {
    super.onDestroy()

    // Release avatar resources
    avatarController?.release()
    avatarController = null
    webView = null

    Log.d("MainActivity", "Avatar resources released")
}

