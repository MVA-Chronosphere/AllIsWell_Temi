
package com.example.alliswelltemi.utils

import com.robotemi.sdk.Robot
import com.robotemi.sdk.TtsRequest



/**
 * Detect if text contains Hindi characters
 */
fun isHindi(text: String): Boolean {
    return text.any { Character.UnicodeBlock.of(it) == Character.UnicodeBlock.DEVANAGARI }
}

/**
 * Speak using Temi's built-in TTS for both English and Hindi.
 * Automatically detects language if "auto" is provided.
 */
fun speakWithLanguage(
    text: String,
    language: String = "auto",
    robot: Robot? = null
) {
    val finalLanguage = if (language == "auto") {
        if (isHindi(text)) "hi" else "en"
    } else {
        language
    }
    val ttsLanguage = if (finalLanguage == "hi") TtsRequest.Language.HI_IN else TtsRequest.Language.EN_US
    robot?.speak(TtsRequest.create(speech = text, isShowOnConversationLayer = false, language = ttsLanguage))
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
