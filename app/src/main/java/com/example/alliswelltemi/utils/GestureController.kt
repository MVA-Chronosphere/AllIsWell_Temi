package com.example.alliswelltemi.utils

import android.util.Log
import kotlinx.coroutines.CoroutineScope

/**
 * Gesture Controller - State Machine for Avatar Animation States
 *
 * Manages contextual animations based on conversation flow:
 * - IDLE: Default resting state (idle animation)
 * - LISTENING: Patient speaking (listening animation)
 * - SPEAKING: Avatar responding with lip sync (talking animation)
 * - GREETING: Welcome gesture (wave animation)
 * - EXPLAINING: Providing directions or details (pointing animation)
 * - THINKING: Processing information (thinking animation)
 *
 * Usage Pattern:
 *   val gestureController = GestureController(avatarController, coroutineScope)
 *   gestureController.setState(GestureState.LISTENING)      // Patient starts speaking
 *   gestureController.setState(GestureState.SPEAKING)       // Avatar responds
 *   gestureController.gestureForIntent("navigation")        // Smart gesture for intent
 *   gestureController.reset()                               // Back to idle
 */
class GestureController(
    private val avatarController: AvatarController,
    private val coroutineScope: CoroutineScope
) {
    private companion object {
        const val TAG = "GestureController"
    }

    /**
     * Avatar gesture states
     * Each state maps to specific animations and behaviors
     */
    enum class GestureState {
        IDLE,      // Neutral standing pose
        LISTENING, // Patient speaking - attentive pose
        SPEAKING,  // Avatar speaking - talking animation + lip sync
        GREETING,  // Initial welcome - wave gesture
        EXPLAINING,// Giving directions - pointing gesture
        THINKING   // Processing - thinking pose
    }

    private var currentState = GestureState.IDLE

    /**
     * Change to a new gesture state
     * Internally maps state to appropriate animation
     *
     * No-op if already in target state (prevents animation loops)
     */
    fun setState(newState: GestureState) {
        if (currentState == newState) {
            Log.d(TAG, "Already in state: $newState, skipping")
            return
        }

        val previousState = currentState
        currentState = newState

        Log.d(TAG, "🎭 Gesture state transition: $previousState → $newState")

        when (newState) {
            GestureState.IDLE -> {
                Log.d(TAG, "↙️  Idle: returning to neutral pose")
                avatarController.playAnimation("idle")
            }

            GestureState.LISTENING -> {
                Log.d(TAG, "👂 Listening: attentive pose")
                // Some models may have a "listening" animation
                // Fall back to idle if not available
                avatarController.playAnimation("listening")
            }

            GestureState.SPEAKING -> {
                Log.d(TAG, "💬 Speaking: talking animation + lip sync")
                avatarController.startSpeaking("talking")
            }

            GestureState.GREETING -> {
                Log.d(TAG, "👋 Greeting: wave gesture")
                avatarController.playAnimation("wave")
            }

            GestureState.EXPLAINING -> {
                Log.d(TAG, "🎯 Explaining: pointing gesture")
                // Some models may have a "pointing" animation
                // Fall back to idle if not available
                avatarController.playAnimation("pointing")
            }

            GestureState.THINKING -> {
                Log.d(TAG, "🤔 Thinking: processing pose")
                // Some models may have a "thinking" animation
                // Fall back to idle if not available
                avatarController.playAnimation("thinking")
            }
        }
    }

    /**
     * Select appropriate gesture based on detected intent
     *
     * Maps high-level intents to specific gestures:
     * - "greeting" → GREETING (welcome patient)
     * - "navigation" → EXPLAINING (user is being guided)
     * - "question" → SPEAKING (answering query)
     * - "listening" → LISTENING (waiting for user input)
     * - default → SPEAKING
     */
    fun gestureForIntent(intent: String) {
        val normalizedIntent = intent.lowercase().trim()

        Log.d(TAG, "🎯 Gesture for intent: '$normalizedIntent'")

        when {
            normalizedIntent.contains("greet") || normalizedIntent.contains("welcome") -> {
                setState(GestureState.GREETING)
            }
            normalizedIntent.contains("navigate") || normalizedIntent.contains("direction") || normalizedIntent.contains("go to") -> {
                setState(GestureState.EXPLAINING)
            }
            normalizedIntent.contains("listen") || normalizedIntent.contains("wait") -> {
                setState(GestureState.LISTENING)
            }
            normalizedIntent.contains("think") || normalizedIntent.contains("process") -> {
                setState(GestureState.THINKING)
            }
            else -> {
                // Default to speaking for questions and general responses
                setState(GestureState.SPEAKING)
            }
        }
    }

    /**
     * Get current gesture state
     */
    fun getCurrentState(): GestureState = currentState

    /**
     * Reset to idle state
     * Call when conversation ends or user moves away
     */
    fun reset() {
        Log.d(TAG, "🔄 Resetting to idle")
        setState(GestureState.IDLE)
    }

    /**
     * Release underlying avatar resources
     */
    fun release() {
        Log.d(TAG, "Releasing GestureController")
        reset()
        avatarController.release()
    }
}

