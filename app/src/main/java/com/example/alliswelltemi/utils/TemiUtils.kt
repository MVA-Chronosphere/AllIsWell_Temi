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

    /**
     * Intent detection for voice input: returns "COMMAND" or "QUESTION" or "UNKNOWN".
     * COMMAND: navigation, sequence, action (go, take me, navigate, start, stop, follow, etc)
     * QUESTION: what, where, how, who, when, why, can you, could you, etc
     */
    fun detectIntent(text: String): String {
        val lower = text.trim().lowercase()
        val commandKeywords = listOf(
            "go to", "take me", "navigate", "start", "stop", "follow", "bring", "move to", "show me the way", "lead me", "find", "book", "rate", "dance"
        )
        val questionKeywords = listOf(
            "what", "where", "how", "who", "when", "why", "can you", "could you", "would you", "is it", "are you", "do you", "does it", "should i", "tell me", "explain"
        )
        if (commandKeywords.any { lower.startsWith(it) || lower.contains(it) }) return "COMMAND"
        if (questionKeywords.any { lower.startsWith(it) || lower.contains(it) }) return "QUESTION"
        // Heuristic: ends with ?
        if (lower.endsWith("?")) return "QUESTION"
        return "UNKNOWN"
    }
}
