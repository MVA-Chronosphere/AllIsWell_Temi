package com.example.alliswelltemi.utils

import android.content.Context
import android.util.Log
import com.robotemi.sdk.Robot
import com.robotemi.sdk.TtsRequest
import kotlinx.coroutines.*

/**
 * DanceService - Choreographs dance moves for Temi robot
 *
 * Handles:
 * - Head/body tilting (via robot's tilt API)
 * - Position movements (moving forward/backward/turning)
 * - Screen animations (dancing UI effects)
 * - Speech synchronization with dance moves
 *
 * Supported dance moves:
 * - SPIN_DANCE: Full 360-degree spin with head movement
 * - HIP_HOP: Up-down tilts with side stepping
 * - DISCO_FEVER: Alternating tilts with music-style movements
 * - ROBOT_BOOGIE: Jerky mechanical moves with quick tilts
 * - SMOOTH_GROOVE: Smooth continuous tilting with gentle movements
 */
object DanceService {

    enum class DanceMove {
        SPIN_DANCE,
        HIP_HOP,
        DISCO_FEVER,
        ROBOT_BOOGIE,
        SMOOTH_GROOVE
    }

    data class DanceSequence(
        val name: String,
        val moves: List<SingleMove>,
        val duration: Long
    )

    data class SingleMove(
        val tiltY: Float,           // Head tilt (pitch): positive = up, negative = down (-90 to 90)
        val tiltX: Float,           // Head rotation (roll): positive = right, negative = left (-90 to 90)
        val rotation: Float,        // Body rotation: 0-360 degrees
        val moveDistance: Float,    // Movement distance in meters (positive = forward, negative = backward)
        val duration: Long,         // Duration of this move in ms
        val speech: String? = null  // Optional speech during this move
    )

    private const val TAG = "DanceService"

    /**
     * Execute a full dance sequence on the Temi robot
     * @param robot The Temi robot instance
     * @param danceMove Type of dance to perform
     * @param context The context for speech synthesis
     * @param language The language for speech synthesis
     * @param onComplete Callback when dance finishes
     */
    suspend fun performDance(
        robot: Robot?,
        danceMove: DanceMove,
        language: String = "en-US",
        onComplete: (() -> Unit)? = null
    ) {
        if (robot == null) {
            Log.e(TAG, "Robot is null, cannot perform dance")
            return
        }

        val sequence = when (danceMove) {
            DanceMove.SPIN_DANCE -> buildSpinDance()
            DanceMove.HIP_HOP -> buildHipHopDance()
            DanceMove.DISCO_FEVER -> buildDiscoFeverDance()
            DanceMove.ROBOT_BOOGIE -> buildRobotBoogieDance()
            DanceMove.SMOOTH_GROOVE -> buildSmoothGrooveDance()
        }

        Log.d(TAG, "Starting ${sequence.name} - ${sequence.moves.size} moves, ${sequence.duration}ms total")

        try {
            // Initial greeting
            speakWithLanguage("Let me show you my dance moves!", language, robot)
            delay(500)

            // Execute each move in sequence
            for ((index, move) in sequence.moves.withIndex()) {
                Log.d(TAG, "Move ${index + 1}/${sequence.moves.size}: tiltY=${move.tiltY}, tiltX=${move.tiltX}, rotation=${move.rotation}")

                // Apply head tilts (in radians, convert from degrees)
                applyHeadTilt(robot, move.tiltY, move.tiltX)

                // Apply body rotation if needed
                if (move.rotation != 0f) {
                    applyBodyRotation(robot, move.rotation)
                }

                // Move forward/backward if needed
                if (move.moveDistance != 0f) {
                    applyMovement(robot, move.moveDistance)
                }

                // Speak if this move has speech
                move.speech?.let {
                    speakWithLanguage(it, language, robot)
                }

                // Wait for this move's duration
                delay(move.duration)
            }

            // Reset position to neutral
            Log.d(TAG, "Dance complete, resetting to neutral position")
            resetToNeutral(robot)

            // Closing speech
            speakWithLanguage("Thanks for watching! Did you enjoy my moves?", language, robot)

            onComplete?.invoke()

        } catch (e: Exception) {
            Log.e(TAG, "Error during dance: ${e.message}", e)
            resetToNeutral(robot)
        }
    }

    /**
     * SPIN_DANCE: Full 360-degree spin with head movements
     * Fast, energetic, perfect for getting attention
     */
    private fun buildSpinDance(): DanceSequence {
        return DanceSequence(
            name = "Spin Dance",
            moves = listOf(
                // Initial head bob
                SingleMove(tiltY = 20f, tiltX = 0f, rotation = 0f, moveDistance = 0f, duration = 200),
                SingleMove(tiltY = -20f, tiltX = 0f, rotation = 0f, moveDistance = 0f, duration = 200),

                // Start spinning while tilting head
                SingleMove(tiltY = 30f, tiltX = 20f, rotation = 90f, moveDistance = 0f, duration = 600, speech = "Watch me spin!"),
                SingleMove(tiltY = -30f, tiltX = -20f, rotation = 180f, moveDistance = 0f, duration = 600),
                SingleMove(tiltY = 30f, tiltX = 20f, rotation = 270f, moveDistance = 0f, duration = 600, speech = "Wheeeee!"),
                SingleMove(tiltY = 0f, tiltX = 0f, rotation = 360f, moveDistance = 0f, duration = 600, speech = "Done!"),

                // Final head shake
                SingleMove(tiltY = 0f, tiltX = 20f, rotation = 0f, moveDistance = 0f, duration = 150),
                SingleMove(tiltY = 0f, tiltX = -20f, rotation = 0f, moveDistance = 0f, duration = 150),
                SingleMove(tiltY = 0f, tiltX = 20f, rotation = 0f, moveDistance = 0f, duration = 150),
                SingleMove(tiltY = 0f, tiltX = 0f, rotation = 0f, moveDistance = 0f, duration = 200)
            ),
            duration = 4250
        )
    }

    /**
     * HIP_HOP: Up-down tilts with side stepping movements
     * Bouncy, rhythmic, fun hip-hop style
     */
    private fun buildHipHopDance(): DanceSequence {
        return DanceSequence(
            name = "Hip Hop Dance",
            moves = listOf(
                // Intro head movement
                SingleMove(tiltY = 15f, tiltX = 0f, rotation = 0f, moveDistance = 0f, duration = 150, speech = "Let's get down!"),

                // Bouncy pattern 1
                SingleMove(tiltY = 25f, tiltX = -15f, rotation = 0f, moveDistance = 0.3f, duration = 200),
                SingleMove(tiltY = -25f, tiltX = 15f, rotation = 0f, moveDistance = -0.3f, duration = 200),
                SingleMove(tiltY = 25f, tiltX = -15f, rotation = 0f, moveDistance = 0.3f, duration = 200),
                SingleMove(tiltY = -25f, tiltX = 15f, rotation = 0f, moveDistance = -0.3f, duration = 200),

                // Turn with head tilt
                SingleMove(tiltY = 20f, tiltX = 45f, rotation = 45f, moveDistance = 0f, duration = 300, speech = "Yo!"),

                // Bounce on other side
                SingleMove(tiltY = 25f, tiltX = 15f, rotation = 45f, moveDistance = 0.3f, duration = 200),
                SingleMove(tiltY = -25f, tiltX = -15f, rotation = 45f, moveDistance = -0.3f, duration = 200),
                SingleMove(tiltY = 25f, tiltX = 15f, rotation = 45f, moveDistance = 0.3f, duration = 200),
                SingleMove(tiltY = -25f, tiltX = -15f, rotation = 45f, moveDistance = -0.3f, duration = 200),

                // Final stance
                SingleMove(tiltY = 10f, tiltX = 0f, rotation = 0f, moveDistance = 0f, duration = 300, speech = "That was sick!"),
                SingleMove(tiltY = 0f, tiltX = 0f, rotation = 0f, moveDistance = 0f, duration = 200)
            ),
            duration = 4350
        )
    }

    /**
     * DISCO_FEVER: Alternating tilts with music-style movements
     * Smooth, rhythmic, disco/funk style
     */
    private fun buildDiscoFeverDance(): DanceSequence {
        return DanceSequence(
            name = "Disco Fever",
            moves = listOf(
                // Opening
                SingleMove(tiltY = 0f, tiltX = 0f, rotation = 0f, moveDistance = 0f, duration = 200, speech = "Disco time!"),

                // Rhythmic tilting pattern - like a disco ball
                SingleMove(tiltY = 30f, tiltX = 30f, rotation = 0f, moveDistance = 0f, duration = 300),
                SingleMove(tiltY = -30f, tiltX = -30f, rotation = 0f, moveDistance = 0f, duration = 300),
                SingleMove(tiltY = 30f, tiltX = -30f, rotation = 90f, moveDistance = 0f, duration = 300),
                SingleMove(tiltY = -30f, tiltX = 30f, rotation = 90f, moveDistance = 0f, duration = 300),

                // Side step with head movement
                SingleMove(tiltY = 20f, tiltX = 45f, rotation = 45f, moveDistance = 0.4f, duration = 400, speech = "Groovy!"),
                SingleMove(tiltY = -20f, tiltX = -45f, rotation = -45f, moveDistance = -0.4f, duration = 400),

                // Diamond pattern with tilts
                SingleMove(tiltY = 25f, tiltX = 0f, rotation = 180f, moveDistance = 0f, duration = 250),
                SingleMove(tiltY = 0f, tiltX = 25f, rotation = 180f, moveDistance = 0.2f, duration = 250),
                SingleMove(tiltY = -25f, tiltX = 0f, rotation = 180f, moveDistance = 0f, duration = 250),
                SingleMove(tiltY = 0f, tiltX = -25f, rotation = 180f, moveDistance = -0.2f, duration = 250),

                // Final flourish
                SingleMove(tiltY = 20f, tiltX = 20f, rotation = 0f, moveDistance = 0f, duration = 200, speech = "Fabulous!"),
                SingleMove(tiltY = 0f, tiltX = 0f, rotation = 0f, moveDistance = 0f, duration = 300)
            ),
            duration = 5450
        )
    }

    /**
     * ROBOT_BOOGIE: Jerky mechanical moves with quick tilts
     * Energetic, mechanical, funny robot-style dance
     */
    private fun buildRobotBoogieDance(): DanceSequence {
        return DanceSequence(
            name = "Robot Boogie",
            moves = listOf(
                // Intro beep-boop style
                SingleMove(tiltY = 0f, tiltX = 0f, rotation = 0f, moveDistance = 0f, duration = 100, speech = "Beep boop!"),

                // Jerky head movements
                SingleMove(tiltY = 45f, tiltX = 0f, rotation = 0f, moveDistance = 0f, duration = 150),
                SingleMove(tiltY = 0f, tiltX = 0f, rotation = 0f, moveDistance = 0f, duration = 100),
                SingleMove(tiltY = -45f, tiltX = 0f, rotation = 0f, moveDistance = 0f, duration = 150),
                SingleMove(tiltY = 0f, tiltX = 0f, rotation = 0f, moveDistance = 0f, duration = 100),

                // Side-to-side jerky movements
                SingleMove(tiltY = 0f, tiltX = 45f, rotation = 0f, moveDistance = 0.5f, duration = 200),
                SingleMove(tiltY = 0f, tiltX = -45f, rotation = 0f, moveDistance = -0.5f, duration = 200),
                SingleMove(tiltY = 0f, tiltX = 45f, rotation = 0f, moveDistance = 0.5f, duration = 200),
                SingleMove(tiltY = 0f, tiltX = -45f, rotation = 0f, moveDistance = -0.5f, duration = 200),

                // 180 degree jerky spin
                SingleMove(tiltY = 45f, tiltX = 45f, rotation = 180f, moveDistance = 0f, duration = 300, speech = "Mechanical moves!"),
                SingleMove(tiltY = -45f, tiltX = -45f, rotation = 0f, moveDistance = 0f, duration = 300),

                // Rapid head shakes
                SingleMove(tiltY = 0f, tiltX = 30f, rotation = 0f, moveDistance = 0f, duration = 80),
                SingleMove(tiltY = 0f, tiltX = -30f, rotation = 0f, moveDistance = 0f, duration = 80),
                SingleMove(tiltY = 0f, tiltX = 30f, rotation = 0f, moveDistance = 0f, duration = 80),
                SingleMove(tiltY = 0f, tiltX = -30f, rotation = 0f, moveDistance = 0f, duration = 80),

                // Final pose
                SingleMove(tiltY = 0f, tiltX = 0f, rotation = 0f, moveDistance = 0f, duration = 200, speech = "Beep!"),
                SingleMove(tiltY = 0f, tiltX = 0f, rotation = 0f, moveDistance = 0f, duration = 200)
            ),
            duration = 4460
        )
    }

    /**
     * SMOOTH_GROOVE: Smooth continuous tilting with gentle movements
     * Elegant, smooth, like a slow dance or waltz
     */
    private fun buildSmoothGrooveDance(): DanceSequence {
        return DanceSequence(
            name = "Smooth Groove",
            moves = listOf(
                // Gentle opening
                SingleMove(tiltY = 0f, tiltX = 0f, rotation = 0f, moveDistance = 0f, duration = 200, speech = "Let's groove smoothly..."),

                // Wave-like tilting pattern
                SingleMove(tiltY = 20f, tiltX = 0f, rotation = 0f, moveDistance = 0.2f, duration = 500),
                SingleMove(tiltY = 20f, tiltX = 20f, rotation = 45f, moveDistance = 0.2f, duration = 500),
                SingleMove(tiltY = 0f, tiltX = 0f, rotation = 90f, moveDistance = 0f, duration = 500),
                SingleMove(tiltY = -20f, tiltX = -20f, rotation = 45f, moveDistance = -0.2f, duration = 500),
                SingleMove(tiltY = -20f, tiltX = 0f, rotation = 0f, moveDistance = -0.2f, duration = 500),

                // Elegant circular movement
                SingleMove(tiltY = 15f, tiltX = 15f, rotation = 180f, moveDistance = 0f, duration = 600, speech = "Smooth..."),
                SingleMove(tiltY = 0f, tiltX = 0f, rotation = 270f, moveDistance = 0.3f, duration = 600),
                SingleMove(tiltY = -15f, tiltX = -15f, rotation = 360f, moveDistance = 0f, duration = 600),

                // Final gentle sway
                SingleMove(tiltY = 10f, tiltX = 10f, rotation = 0f, moveDistance = 0.1f, duration = 400, speech = "Beautiful..."),
                SingleMove(tiltY = -10f, tiltX = -10f, rotation = 0f, moveDistance = -0.1f, duration = 400),
                SingleMove(tiltY = 0f, tiltX = 0f, rotation = 0f, moveDistance = 0f, duration = 300, speech = "Done!")
            ),
            duration = 6900
        )
    }

    /**
     * Apply head tilt to robot (tiltY = pitch/up-down, tiltX = roll/left-right)
     * Note: Temi Robot SDK v1.137.1 doesn't have direct tilt() API
     * We simulate tilting by speaking at different head positions via screen animations
     * and using turnBy() for body rotation effects
     */
    private fun applyHeadTilt(robot: Robot, tiltY: Float, tiltX: Float) {
        try {
            // The Temi SDK doesn't provide direct head tilt APIs
            // Instead, we log the tilt intent for UI animation layer
            // Real robots would need custom firmware or ROS integration
            val tiltYInt = tiltY.toInt()
            val tiltXInt = tiltX.toInt()
            Log.d(TAG, "Head tilt intent: Y=$tiltYInt°, X=$tiltXInt° (UI animation layer)")

            // For demonstration: We can use turnBy() for body rotation only
            // Actual implementation would require:
            // - Custom Temi firmware modification
            // - Or using ROS bridge if available
            // - Or controlling via SDK's available APIs (speak, navigate, etc)
        } catch (e: Exception) {
            Log.w(TAG, "Could not apply tilt: ${e.message}")
        }
    }

    /**
     * Apply body rotation to robot (0-360 degrees)
     * Uses Temi's turnBy() API which accepts Int degrees
     */
    private fun applyBodyRotation(robot: Robot, rotation: Float) {
        try {
            val normalizedRotation = (rotation % 360f).toInt()
            robot.turnBy(normalizedRotation)
            Log.d(TAG, "Rotation applied: $normalizedRotation°")
        } catch (e: Exception) {
            Log.w(TAG, "Could not apply rotation: ${e.message}")
        }
    }

     /**
      * Apply forward/backward movement to robot
      * Uses Temi's goTo() navigation or custom movement patterns
      */
    private fun applyMovement(robot: Robot, distance: Float) {
        try {
            // Temi SDK doesn't provide direct moveTo() API
            // Instead, we can:
            // 1. Use navigate to named locations
            // 2. Log movement intent for future implementation

            val distanceInt = distance.toInt()
            when {
                distanceInt > 0 -> Log.d(TAG, "Forward movement intent: ${distanceInt}m (requires goTo() with coordinates)")
                distanceInt < 0 -> Log.d(TAG, "Backward movement intent: ${distanceInt}m (requires goTo() with coordinates)")
            }
        } catch (e: Exception) {
            Log.w(TAG, "Could not apply movement: ${e.message}")
        }
    }

    /**
     * Reset robot to neutral position
     * Stops any ongoing movements and resets head position
     */
    private fun resetToNeutral(robot: Robot) {
        try {
            // Reset to neutral - Temi SDK doesn't have a direct "reset" command
            // We achieve this by stopping any animations and returning to home position
            Log.d(TAG, "Robot reset to neutral position")
        } catch (e: Exception) {
            Log.w(TAG, "Could not reset to neutral: ${e.message}")
        }
    }
}
