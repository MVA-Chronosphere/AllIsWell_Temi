package com.example.alliswelltemi.utils

import android.content.Context
import android.speech.tts.TextToSpeech
import android.util.Log
import com.robotemi.sdk.Robot
import com.robotemi.sdk.TtsRequest
import java.util.Locale

/**
 * Singleton TTS manager for Google TTS Hindi support
 */
object TemiTTSManager {
                    fun speakEnglish(text: String, queueMode: Int = TextToSpeech.QUEUE_FLUSH) {
                        if (!isInitialized || tts == null) {
                            Log.w("TemiTTSManager", "speakEnglish called before initialization")
                            return
                        }
                        val utteranceId = "english_${System.currentTimeMillis()}"
                        // Use Indian English locale for better Indian name pronunciation
                        tts?.language = Locale("en", "IN")
                        val result = tts?.speak(text, queueMode, null, utteranceId)
                        if (result == TextToSpeech.ERROR) {
                            Log.e("TemiTTSManager", "Error calling tts.speak for: $text")
                        }
                    }
    private var tts: TextToSpeech? = null
    private var isInitialized = false
    private var isHindiAvailable = false
    private var currentEngine: String? = null
    private val initCallbacks = mutableListOf<() -> Unit>()
    private var onCompletionListener: (() -> Unit)? = null

    fun initialize(context: Context, onReady: (() -> Unit)? = null) {
        if (isInitialized && tts != null) {
            onReady?.invoke()
            return
        }

        if (onReady != null) {
            initCallbacks.add(onReady)
        }

        if (tts != null) return // Already initializing

        // 1. First, find the Google TTS engine package name
        val tempTts = TextToSpeech(context.applicationContext, null)
        val googleEngine = tempTts.engines.firstOrNull { it.name.contains("google", ignoreCase = true) }?.name
        tempTts.shutdown()

        // 2. Initialize with the preferred engine
        tts = TextToSpeech(context.applicationContext, { status ->
            if (status == TextToSpeech.SUCCESS) {
                val result = tts?.isLanguageAvailable(Locale("hi", "IN"))
                isHindiAvailable = result == TextToSpeech.LANG_COUNTRY_AVAILABLE || result == TextToSpeech.LANG_AVAILABLE
                
                tts?.language = if (isHindiAvailable) Locale("hi", "IN") else Locale.US
                
                // Set completion listener
                tts?.setOnUtteranceProgressListener(object : android.speech.tts.UtteranceProgressListener() {
                    override fun onStart(utteranceId: String?) {
                        Log.d("TemiTTSManager", "Speech started: $utteranceId")
                    }
                    override fun onDone(utteranceId: String?) {
                        Log.d("TemiTTSManager", "Speech completed: $utteranceId")
                        onCompletionListener?.invoke()
                    }
                    override fun onError(utteranceId: String?) {
                        Log.e("TemiTTSManager", "Speech error: $utteranceId")
                        onCompletionListener?.invoke()
                    }
                })

                isInitialized = true
                Log.d("TemiTTSManager", "TTS Initialized. Hindi supported: $isHindiAvailable")
                
                // Fire all queued callbacks
                val callbacks = ArrayList(initCallbacks)
                initCallbacks.clear()
                callbacks.forEach { it.invoke() }
            } else {
                Log.e("TemiTTSManager", "TTS init failed with status: $status")
            }
        }, googleEngine)
        
        currentEngine = googleEngine
    }

    fun speakHindi(text: String, queueMode: Int = TextToSpeech.QUEUE_FLUSH) {
        if (!isInitialized || tts == null) {
            Log.w("TemiTTSManager", "speakHindi called before initialization")
            return
        }
        val utteranceId = "hindi_${System.currentTimeMillis()}"
        tts?.language = Locale("hi", "IN")
        val result = tts?.speak(text, queueMode, null, utteranceId)
        if (result == TextToSpeech.ERROR) {
            Log.e("TemiTTSManager", "Error calling tts.speak for: $text")
        }
    }

    fun stop() {
        tts?.stop()
    }

    fun shutdown() {
        tts?.shutdown()
        tts = null
        isInitialized = false
        initCallbacks.clear()
    }

    fun isHindiSupported(): Boolean = isHindiAvailable

    fun setOnCompletionListener(listener: () -> Unit) {
        this.onCompletionListener = listener
    }
}

/**
 * Detect if text contains Hindi characters
 */
fun isHindi(text: String): Boolean {
    return text.any { Character.UnicodeBlock.of(it) == Character.UnicodeBlock.DEVANAGARI }
}

/**
 * Speak with Google TTS for Hindi, Temi SDK for English
 * Automatically detects language if "auto" is provided
 */
fun speakWithLanguage(
    context: Context,
    text: String,
    language: String = "auto",
    robot: Robot? = null,
    queueMode: Int = TextToSpeech.QUEUE_FLUSH
) {
    val finalLanguage = if (language == "auto") {
        if (isHindi(text)) "hi" else "en"
    } else {
        language
    }

    TemiTTSManager.initialize(context) {
        if (finalLanguage == "hi") {
            if (TemiTTSManager.isHindiSupported()) {
                TemiTTSManager.speakHindi(text, queueMode)
            } else {
                robot?.speak(TtsRequest.create(text, false))
            }
        } else {
            TemiTTSManager.speakEnglish(text, queueMode)
        }
    }
}

/**
 * Temi SDK Utility Functions
 */
object TemiUtils {
    fun Robot?.speakSimple(text: String, language: String = "en") {
        if (this == null) return
        val ttsLanguage = if (language == "hi") TtsRequest.Language.HI_IN else TtsRequest.Language.EN_US
        this.speak(TtsRequest.create(speech = text, isShowOnConversationLayer = false, language = ttsLanguage))
    }
}
