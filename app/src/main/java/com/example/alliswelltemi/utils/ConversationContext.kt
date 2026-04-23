package com.example.alliswelltemi.utils

import android.util.Log

/**
 * Conversation Context - Maintains conversation history across multiple questions
 * Allows the LLM to maintain context and reference previous answers in the session
 *
 * Features:
 * - Stores conversation history (question-answer pairs)
 * - Provides formatted context for LLM (remembers previous questions)
 * - Respects session timeout (auto-clears old conversations)
 * - Limits history size to prevent token overflow
 */
class ConversationContext(
    private val maxHistoryItems: Int = 5, // Remember last 5 question-answer pairs
    private val maxContextLength: Int = 2000 // Max context length in characters
) {
    private val conversationHistory = mutableListOf<ConversationTurn>()
    private var lastInteractionTime = System.currentTimeMillis()
    private val SESSION_TIMEOUT = 30 * 1000L // 30 seconds session timeout
    private val TAG = "ConversationContext"

    /**
     * Data class representing a single conversation turn
     */
    data class ConversationTurn(
        val question: String,
        val answer: String,
        val timestamp: Long = System.currentTimeMillis()
    )

    /**
     * Add a new question-answer pair to conversation history
     */
    fun addTurn(question: String, answer: String) {
        // Check if session has timed out
        if (isSessionExpired()) {
            Log.d(TAG, "Session expired, clearing conversation history")
            conversationHistory.clear()
        }

        lastInteractionTime = System.currentTimeMillis()
        conversationHistory.add(ConversationTurn(question, answer))

        // Keep only recent turns to avoid token overflow
        if (conversationHistory.size > maxHistoryItems) {
            conversationHistory.removeAt(0)
        }

        Log.d(TAG, "Added turn. History size: ${conversationHistory.size}")
    }

    /**
     * Get formatted context of conversation history for LLM
     * Includes previous questions and answers to help model understand context
     */
    fun getContextString(): String {
        if (conversationHistory.isEmpty()) {
            return ""
        }

        val contextBuilder = StringBuilder()
        contextBuilder.append("## Previous Conversation Context:\n")

        conversationHistory.forEach { turn ->
            contextBuilder.append("Q: ${turn.question}\n")
            contextBuilder.append("A: ${turn.answer}\n\n")
        }

        // Truncate if context gets too long
        val fullContext = contextBuilder.toString()
        return if (fullContext.length > maxContextLength) {
            fullContext.take(maxContextLength) + "\n... [context truncated]"
        } else {
            fullContext
        }
    }

    /**
     * Get number of turns in current conversation
     */
    fun getTurnCount(): Int = conversationHistory.size

    /**
     * Check if session has expired (no interaction for SESSION_TIMEOUT period)
     */
    fun isSessionExpired(): Boolean {
        return System.currentTimeMillis() - lastInteractionTime > SESSION_TIMEOUT
    }

    /**
     * Clear conversation history (start new conversation)
     */
    fun clearHistory() {
        conversationHistory.clear()
        lastInteractionTime = System.currentTimeMillis()
        Log.d(TAG, "Conversation history cleared")
    }

    /**
     * Get last answer in conversation (useful for follow-ups)
     */
    fun getLastAnswer(): String? {
        return conversationHistory.lastOrNull()?.answer
    }

    /**
     * Get full conversation history as list
     */
    fun getFullHistory(): List<ConversationTurn> {
        return conversationHistory.toList()
    }

    /**
     * Get formatted conversation history for display (e.g., in chat UI)
     */
    fun getFormattedHistory(): String {
        if (conversationHistory.isEmpty()) {
            return "No conversation history yet."
        }

        val formatted = conversationHistory.joinToString("\n\n") { turn ->
            "Patient: ${turn.question}\nTemi: ${turn.answer}"
        }

        return "### Conversation History\n\n$formatted"
    }

    /**
     * Reset session timer (call after user interaction)
     */
    fun resetSessionTimer() {
        lastInteractionTime = System.currentTimeMillis()
    }
}

