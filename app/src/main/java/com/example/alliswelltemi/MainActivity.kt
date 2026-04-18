package com.example.alliswelltemi

import android.os.Bundle
import android.view.WindowManager
import androidx.activity.compose.setContent
import androidx.appcompat.app.AppCompatActivity
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.alliswelltemi.ui.screens.TemiMainScreen
import com.example.alliswelltemi.ui.screens.NavigationScreen
import com.example.alliswelltemi.ui.screens.AppointmentBookingScreen
import com.example.alliswelltemi.ui.screens.DoctorsScreen
import com.example.alliswelltemi.ui.screens.FeedbackScreen
import com.example.alliswelltemi.ui.theme.TemiTheme
import com.example.alliswelltemi.viewmodel.NavigationViewModel
import com.example.alliswelltemi.viewmodel.AppointmentViewModel
import com.example.alliswelltemi.viewmodel.DoctorsViewModel
import com.robotemi.sdk.Robot
import com.robotemi.sdk.TtsRequest
import com.robotemi.sdk.listeners.OnRobotReadyListener

class MainActivity : AppCompatActivity(), OnRobotReadyListener {

    private var robot: Robot? = null
    private val isRobotReady = mutableStateOf(false)
    private val currentScreen = mutableStateOf("main")

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        // Set up fullscreen and landscape
        window.addFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN)
        window.addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON)

        // Initialize Temi Robot SDK
        Robot.getInstance().addOnRobotReadyListener(this)

        // Set Compose content
        setContent {
            TemiTheme(darkTheme = true) {
                when (currentScreen.value) {
                    "navigation" -> {
                        val navViewModel: NavigationViewModel = viewModel()
                        NavigationScreen(
                            robot = robot,
                            viewModel = navViewModel,
                            onBackPress = {
                                currentScreen.value = "main"
                            },
                            onNavigationComplete = { _ ->
                                // Optional: Do something after navigation completes
                                // For now, stay on navigation screen
                            }
                        )
                    }
                    "appointment" -> {
                        val appointmentViewModel: AppointmentViewModel = viewModel()
                        AppointmentBookingScreen(
                            robot = robot,
                            viewModel = appointmentViewModel,
                            onBackPress = {
                                currentScreen.value = "main"
                            }
                        )
                    }
                    "doctors" -> {
                        val doctorsViewModel: DoctorsViewModel = viewModel()
                        DoctorsScreen(
                            robot = robot,
                            viewModel = doctorsViewModel,
                            onBackPress = {
                                currentScreen.value = "main"
                            },
                            onSelectDoctor = { doctor ->
                                // TODO: Handle doctor selection for booking
                                currentScreen.value = "appointment"
                            }
                        )
                    }
                    "feedback" -> {
                        FeedbackScreen(
                            robot = robot,
                            onBackPress = {
                                currentScreen.value = "main"
                            }
                        )
                    }
                    else -> {
                        TemiMainScreen(
                            robot = robot,
                            onNavigate = { destination ->
                                handleNavigation(destination)
                            }
                        )
                    }
                }
            }
        }
    }

    override fun onRobotReady(isReady: Boolean) {
        if (isReady) {
            this.robot = Robot.getInstance()
            isRobotReady.value = true

            // Initialization message
            this.robot?.speak(
                TtsRequest.create(
                    speech = "Hello, I am Temi and I am ready to assist you",
                    isShowOnConversationLayer = false
                )
            )
        }
    }

    /**
     * Navigation handler for menu items
     * This can be extended to open different screens or trigger specific actions
     */
    private fun handleNavigation(destination: String) {
        // Implement navigation based on destination
        when (destination) {
            "navigation" -> {
                currentScreen.value = "navigation"
            }
            "appointment" -> {
                currentScreen.value = "appointment"
            }
            "doctors" -> {
                currentScreen.value = "doctors"
            }
            "feedback" -> {
                currentScreen.value = "feedback"
            }
            "emergency" -> {
                // TODO: Trigger emergency alert system
                // You can integrate with hospital alert system here
            }
            "info" -> {
                // TODO: Open hospital information screen
            }
            "language" -> {
                // Language already handled in composable
            }
        }
    }

    override fun onPause() {
        super.onPause()
        Robot.getInstance().removeOnRobotReadyListener(this)
    }

    override fun onResume() {
        super.onResume()
        Robot.getInstance().addOnRobotReadyListener(this)
    }

    override fun onDestroy() {
        super.onDestroy()
        Robot.getInstance().removeOnRobotReadyListener(this)
    }
}

