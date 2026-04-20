package com.example.alliswelltemi.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.alliswelltemi.R
import com.example.alliswelltemi.data.Doctor
import com.example.alliswelltemi.data.DoctorData
import com.example.alliswelltemi.data.TimeSlot
import com.example.alliswelltemi.viewmodel.AppointmentViewModel
import com.example.alliswelltemi.viewmodel.DoctorsViewModel
import com.robotemi.sdk.Robot
import com.robotemi.sdk.TtsRequest
import java.time.LocalDate
import java.time.YearMonth

/**
 * Appointment Booking Screen with multi-step flow
 * Steps: 1=doctor, 2=date, 3=time, 4=details, 5=confirm
 */
@Composable
fun AppointmentBookingScreen(
    robot: Robot? = null,
    onBackPress: () -> Unit = {},
    viewModel: AppointmentViewModel = viewModel(),
    doctorsViewModel: DoctorsViewModel = viewModel()
) {
    var currentLanguage by remember { mutableStateOf("en") }
    val darkBg = colorResource(id = R.color.dark_bg)

    LaunchedEffect(Unit) {
        viewModel.initializeDepartments(doctorsViewModel.departments.value)
        viewModel.loadTimeSlots(DoctorData.getAvailableTimeSlots())
        
        // Load initial doctors if a department is available
        doctorsViewModel.departments.value.firstOrNull()?.let {
            viewModel.loadDoctorsByDepartment(it, doctorsViewModel.doctors.value)
        }
    }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(darkBg)
            .padding(horizontal = 64.dp, vertical = 40.dp)
    ) {
        Column(modifier = Modifier.fillMaxSize()) {
            // Header
            AppointmentHeader(
                currentStep = viewModel.currentStep.value,
                language = currentLanguage,
                onLanguageToggle = { currentLanguage = if (currentLanguage == "en") "hi" else "en" },
                onBackPress = onBackPress
            )

            Spacer(modifier = Modifier.height(32.dp))

            // Progress Indicator
            AppointmentProgressBar(currentStep = viewModel.currentStep.value)

            Spacer(modifier = Modifier.height(32.dp))

            // Step Content
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .weight(1f)
                    .verticalScroll(rememberScrollState())
            ) {
                if (viewModel.isLoading.value) {
                    CircularProgressIndicator(
                        modifier = Modifier.align(Alignment.Center),
                        color = Color(0xFF00D9FF)
                    )
                } else {
                    when (viewModel.currentStep.value) {
                        1 -> StepSelectDoctor(
                            viewModel = viewModel,
                            doctorsViewModel = doctorsViewModel,
                            language = currentLanguage,
                            robot = robot,
                            departments = viewModel.availableDepartments.value
                        )
                    2 -> StepSelectDate(
                        viewModel = viewModel,
                        language = currentLanguage,
                        robot = robot
                    )
                    3 -> StepSelectTime(
                        viewModel = viewModel,
                        language = currentLanguage,
                        robot = robot,
                        timeSlots = viewModel.availableTimeSlots.value
                    )
                    4 -> StepPatientDetails(
                        viewModel = viewModel,
                        language = currentLanguage,
                        robot = robot
                    )
                    5 -> StepConfirmation(
                        viewModel = viewModel,
                        language = currentLanguage,
                        robot = robot,
                        onHome = onBackPress
                    )
                }
            }
        }

        Spacer(modifier = Modifier.height(32.dp))

        // Navigation Buttons
        if (viewModel.currentStep.value < 5) {
                AppointmentNavigationButtons(
                    viewModel = viewModel,
                    currentLanguage = currentLanguage,
                    canProceed = canProceedToNextStep(viewModel),
                    robot = robot
                )
            }
        }
    }
}

/**
 * Step 1: Select Doctor from list
 */
@Composable
fun StepSelectDoctor(
    viewModel: AppointmentViewModel,
    doctorsViewModel: DoctorsViewModel,
    language: String,
    robot: Robot?,
    departments: List<String>
) {
    var selectedDepartment by remember { mutableStateOf(departments.firstOrNull() ?: "") }

    Column(modifier = Modifier.fillMaxWidth()) {
        Text(
            text = if (language == "en") "Step 1: Select Doctor" else "चरण 1: डॉक्टर चुनें",
            color = Color.White,
            fontSize = 24.sp,
            fontWeight = FontWeight.Bold
        )

        Spacer(modifier = Modifier.height(24.dp))

        // Department Selection
        Text(
            text = if (language == "en") "Select Department:" else "विभाग चुनें:",
            color = Color.White,
            fontSize = 16.sp,
            fontWeight = FontWeight.Medium
        )

        Spacer(modifier = Modifier.height(12.dp))

        Row(
            modifier = Modifier
                .fillMaxWidth()
                .horizontalScroll(rememberScrollState()),
            horizontalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            departments.forEach { dept ->
                Surface(
                    modifier = Modifier
                        .clickable {
                            selectedDepartment = dept
                            viewModel.loadDoctorsByDepartment(dept, doctorsViewModel.doctors.value)
                        },
                    shape = androidx.compose.foundation.shape.RoundedCornerShape(8.dp),
                    color = if (selectedDepartment == dept)
                        Color(0xFF00D9FF)
                    else
                        Color.White.copy(alpha = 0.1f)
                ) {
                    Text(
                        text = dept,
                        color = if (selectedDepartment == dept) Color.Black else Color.White,
                        fontSize = 13.sp,
                        fontWeight = FontWeight.Medium,
                        modifier = Modifier.padding(horizontal = 12.dp, vertical = 8.dp)
                    )
                }
            }
        }

        Spacer(modifier = Modifier.height(24.dp))

        // Doctor List
        Text(
            text = if (language == "en") "Available Doctors:" else "उपलब्ध डॉक्टर:",
            color = Color.White,
            fontSize = 16.sp,
            fontWeight = FontWeight.Medium
        )

        Spacer(modifier = Modifier.height(12.dp))

        viewModel.doctorsInDepartment.value.forEach { doctor ->
            AppointmentDoctorCard(
                doctor = doctor,
                language = language,
                onSelect = {
                    viewModel.selectDoctor(doctor)
                    val speech = if (language == "en")
                        "You selected ${doctor.name} from ${doctor.department}. Please select a date."
                    else
                        "आपने ${doctor.name} को ${doctor.department} से चुना। कृपया एक तारीख चुनें।"
                    robot?.speak(TtsRequest.create(speech = speech, isShowOnConversationLayer = false))
                }
            )

            Spacer(modifier = Modifier.height(12.dp))
        }
    }
}

/**
 * Doctor Card Component
 */
@Composable
fun AppointmentDoctorCard(
    doctor: Doctor,
    language: String,
    onSelect: () -> Unit
) {
    Surface(
        modifier = Modifier
            .fillMaxWidth()
            .clickable(onClick = onSelect),
        shape = androidx.compose.foundation.shape.RoundedCornerShape(12.dp),
        color = Color.White.copy(alpha = 0.08f)
    ) {
        Row(
            modifier = Modifier
                .padding(16.dp)
                .fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = doctor.name,
                    color = Color.White,
                    fontSize = 16.sp,
                    fontWeight = FontWeight.Bold
                )

                Spacer(modifier = Modifier.height(4.dp))

                Text(
                    text = "${doctor.department} • ${doctor.yearsOfExperience} years",
                    color = Color.White.copy(alpha = 0.7f),
                    fontSize = 12.sp
                )

                Spacer(modifier = Modifier.height(8.dp))

                Text(
                    text = doctor.aboutBio,
                    color = Color.White.copy(alpha = 0.6f),
                    fontSize = 11.sp,
                    maxLines = 2
                )

                Spacer(modifier = Modifier.height(8.dp))

                Text(
                    text = if (language == "en") "Cabin: ${doctor.cabin}" else "कैबिन: ${doctor.cabin}",
                    color = Color(0xFF00D9FF),
                    fontSize = 11.sp,
                    fontWeight = FontWeight.Bold
                )
            }

            Icon(
                imageVector = Icons.Default.ChevronRight,
                contentDescription = null,
                tint = Color(0xFF00D9FF),
                modifier = Modifier.size(24.dp)
            )
        }
    }
}

/**
 * Step 2: Select Date
 */
@Composable
fun StepSelectDate(
    viewModel: AppointmentViewModel,
    language: String,
    robot: Robot?
) {
    var displayMonth by remember { mutableStateOf(YearMonth.now()) }

    Column(modifier = Modifier.fillMaxWidth()) {
        Text(
            text = if (language == "en") "Step 2: Select Date" else "चरण 2: तारीख चुनें",
            color = Color.White,
            fontSize = 24.sp,
            fontWeight = FontWeight.Bold
        )

        Spacer(modifier = Modifier.height(24.dp))

        // Month/Year Navigation
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Icon(
                imageVector = Icons.Default.ChevronLeft,
                contentDescription = "Previous",
                tint = Color.White,
                modifier = Modifier
                    .size(32.dp)
                    .clickable {
                        displayMonth = displayMonth.minusMonths(1)
                        robot?.speak(TtsRequest.create(
                            speech = if (language == "en") "Showing ${displayMonth.month}" else "${displayMonth.month} दिखा रहा है",
                            isShowOnConversationLayer = false
                        ))
                    }
            )

            Text(
                text = "${displayMonth.month} ${displayMonth.year}",
                color = Color.White,
                fontSize = 18.sp,
                fontWeight = FontWeight.Bold
            )

            Icon(
                imageVector = Icons.Default.ChevronRight,
                contentDescription = "Next",
                tint = Color.White,
                modifier = Modifier
                    .size(32.dp)
                    .clickable {
                        displayMonth = displayMonth.plusMonths(1)
                        robot?.speak(TtsRequest.create(
                            speech = if (language == "en") "Showing ${displayMonth.month}" else "${displayMonth.month} दिखा रहा है",
                            isShowOnConversationLayer = false
                        ))
                    }
            )
        }

        Spacer(modifier = Modifier.height(24.dp))

        // Calendar Grid
        CalendarGrid(
            yearMonth = displayMonth,
            onDateSelected = { date ->
                viewModel.selectDate(date)
                val speech = if (language == "en")
                    "Date selected: $date. Please select a time slot."
                else
                    "तारीख चुनी गई: $date। कृपया एक समय स्लॉट चुनें।"
                robot?.speak(TtsRequest.create(speech = speech, isShowOnConversationLayer = false))
            }
        )
    }
}

/**
 * Calendar Grid Component
 */
@Composable
fun CalendarGrid(
    yearMonth: YearMonth,
    onDateSelected: (LocalDate) -> Unit
) {
    val firstDay = yearMonth.atDay(1)
    val daysInMonth = yearMonth.lengthOfMonth()
    val firstDayOfWeek = firstDay.dayOfWeek.value % 7
    val today = LocalDate.now()

    Column(modifier = Modifier.fillMaxWidth()) {
        // Days of week header
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            listOf("Mon", "Tue", "Wed", "Thu", "Fri", "Sat", "Sun").forEach { day ->
                Text(
                    text = day,
                    color = Color.White.copy(alpha = 0.5f),
                    fontSize = 12.sp,
                    modifier = Modifier.weight(1f),
                    textAlign = androidx.compose.ui.text.style.TextAlign.Center
                )
            }
        }

        Spacer(modifier = Modifier.height(12.dp))

        // Calendar days
        var dayCounter = 1
        repeat((daysInMonth + firstDayOfWeek + 6) / 7) { week ->
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                repeat(7) { dayOfWeek ->
                    if (week == 0 && dayOfWeek < firstDayOfWeek || dayCounter > daysInMonth) {
                        Box(modifier = Modifier.weight(1f))
                    } else {
                        val date = yearMonth.atDay(dayCounter)
                        val isToday = date == today
                        val isPast = date.isBefore(today)

                        Surface(
                            modifier = Modifier
                                .weight(1f)
                                .aspectRatio(1f)
                                .clickable(enabled = !isPast) {
                                    onDateSelected(date)
                                },
                            shape = androidx.compose.foundation.shape.RoundedCornerShape(8.dp),
                            color = when {
                                isToday -> Color(0xFF00D9FF)
                                isPast -> Color.White.copy(alpha = 0.1f)
                                else -> Color.White.copy(alpha = 0.08f)
                            }
                        ) {
                            Text(
                                text = dayCounter.toString(),
                                color = if (isPast) Color.White.copy(alpha = 0.3f) else Color.White,
                                fontSize = 14.sp,
                                fontWeight = if (isToday) FontWeight.Bold else FontWeight.Normal,
                                modifier = Modifier.fillMaxSize(),
                                textAlign = androidx.compose.ui.text.style.TextAlign.Center
                            )
                        }
                        dayCounter++
                    }
                }
            }
        }
    }
}

/**
 * Step 3: Select Time Slot
 */
@Composable
fun StepSelectTime(
    viewModel: AppointmentViewModel,
    language: String,
    robot: Robot?,
    timeSlots: List<TimeSlot>
) {
    Column(modifier = Modifier.fillMaxWidth()) {
        Text(
            text = if (language == "en") "Step 3: Select Time" else "चरण 3: समय चुनें",
            color = Color.White,
            fontSize = 24.sp,
            fontWeight = FontWeight.Bold
        )

        Spacer(modifier = Modifier.height(24.dp))

        Text(
            text = if (language == "en")
                "Available Time Slots:"
            else
                "उपलब्ध समय स्लॉट:",
            color = Color.White,
            fontSize = 16.sp,
            fontWeight = FontWeight.Medium
        )

        Spacer(modifier = Modifier.height(16.dp))

        Column(
            modifier = Modifier.fillMaxWidth(),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            timeSlots.forEach { slot ->
                TimeSlotButton(
                    timeSlot = slot,
                    language = language,
                    onSelect = {
                        viewModel.selectTimeSlot(slot)
                        val speech = if (language == "en")
                            "Time selected: ${slot.startTime}. Please enter your details."
                        else
                            "समय चुना गया: ${slot.startTime}। कृपया अपने विवरण दर्ज करें।"
                        robot?.speak(TtsRequest.create(speech = speech, isShowOnConversationLayer = false))
                    }
                )
            }
        }
    }
}

/**
 * Time Slot Button Component
 */
@Composable
fun TimeSlotButton(
    timeSlot: TimeSlot,
    language: String,
    onSelect: () -> Unit
) {
    Surface(
        modifier = Modifier
            .fillMaxWidth()
            .height(56.dp)
            .clickable(enabled = timeSlot.available, onClick = onSelect),
        shape = androidx.compose.foundation.shape.RoundedCornerShape(8.dp),
        color = when {
            !timeSlot.available -> Color.White.copy(alpha = 0.05f)
            else -> Color.White.copy(alpha = 0.1f)
        }
    ) {
        Row(
            modifier = Modifier
                .fillMaxSize()
                .padding(horizontal = 16.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = "${timeSlot.startTime} - ${timeSlot.endTime}",
                color = if (timeSlot.available) Color.White else Color.White.copy(alpha = 0.3f),
                fontSize = 14.sp,
                fontWeight = FontWeight.Medium
            )

            Text(
                text = if (timeSlot.available)
                    if (language == "en") "Available" else "उपलब्ध"
                else
                    if (language == "en") "Booked" else "बुक किया गया",
                color = if (timeSlot.available) Color(0xFF00FF41) else Color(0xFFDC2626),
                fontSize = 12.sp,
                fontWeight = FontWeight.Bold
            )
        }
    }
}

/**
 * Step 4: Patient Details
 */
@Composable
fun StepPatientDetails(
    viewModel: AppointmentViewModel,
    language: String,
    robot: Robot?
) {
    LaunchedEffect(Unit) {
        val speech = if (language == "en")
            "Please enter your name and phone number."
        else
            "कृपया अपना नाम और फोन नंबर दर्ज करें।"
        robot?.speak(TtsRequest.create(speech = speech, isShowOnConversationLayer = false))
    }
    Column(modifier = Modifier.fillMaxWidth()) {
        Text(
            text = if (language == "en") "Step 4: Your Details" else "चरण 4: आपके विवरण",
            color = Color.White,
            fontSize = 24.sp,
            fontWeight = FontWeight.Bold
        )

        Spacer(modifier = Modifier.height(24.dp))

        // Name Input
        Text(
            text = if (language == "en") "Full Name:" else "पूरा नाम:",
            color = Color.White,
            fontSize = 14.sp,
            fontWeight = FontWeight.Medium
        )

        Spacer(modifier = Modifier.height(8.dp))

        Surface(
            modifier = Modifier
                .fillMaxWidth()
                .height(48.dp),
            shape = androidx.compose.foundation.shape.RoundedCornerShape(8.dp),
            color = Color.White.copy(alpha = 0.08f)
        ) {
            TextField(
                value = viewModel.patientName.value,
                onValueChange = { viewModel.setPatientName(it) },
                modifier = Modifier.fillMaxSize(),
                singleLine = true,
                textStyle = LocalTextStyle.current.copy(color = Color.White),
                colors = TextFieldDefaults.colors(
                    focusedContainerColor = Color.Transparent,
                    unfocusedContainerColor = Color.Transparent,
                    focusedIndicatorColor = Color(0xFF00D9FF),
                    unfocusedIndicatorColor = Color.White.copy(alpha = 0.2f),
                    cursorColor = Color.White
                )
            )
        }

        Spacer(modifier = Modifier.height(20.dp))

        // Phone Input
        Text(
            text = if (language == "en") "Phone Number:" else "फोन नंबर:",
            color = Color.White,
            fontSize = 14.sp,
            fontWeight = FontWeight.Medium
        )

        Spacer(modifier = Modifier.height(8.dp))

        Surface(
            modifier = Modifier
                .fillMaxWidth()
                .height(48.dp),
            shape = androidx.compose.foundation.shape.RoundedCornerShape(8.dp),
            color = Color.White.copy(alpha = 0.08f)
        ) {
            TextField(
                value = viewModel.patientPhone.value,
                onValueChange = { viewModel.setPatientPhone(it) },
                modifier = Modifier.fillMaxSize(),
                singleLine = true,
                textStyle = LocalTextStyle.current.copy(color = Color.White),
                colors = TextFieldDefaults.colors(
                    focusedContainerColor = Color.Transparent,
                    unfocusedContainerColor = Color.Transparent,
                    focusedIndicatorColor = Color(0xFF00D9FF),
                    unfocusedIndicatorColor = Color.White.copy(alpha = 0.2f),
                    cursorColor = Color.White
                )
            )
        }

        Spacer(modifier = Modifier.height(20.dp))

        // Validation message
        if (viewModel.patientName.value.isNotEmpty() && viewModel.patientPhone.value.isEmpty()) {
            Text(
                text = if (language == "en")
                    "Please enter your phone number"
                else
                    "कृपया अपना फोन नंबर दर्ज करें",
                color = Color(0xFFDC2626),
                fontSize = 12.sp
            )
        }
    }
}

/**
 * Step 5: Confirmation with Token
 */
@Composable
fun StepConfirmation(
    viewModel: AppointmentViewModel,
    language: String,
    robot: Robot?,
    onHome: () -> Unit
) {
    Column(
        modifier = Modifier
            .fillMaxWidth(),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        // Success Icon
        Icon(
            imageVector = Icons.Default.CheckCircle,
            contentDescription = null,
            tint = Color(0xFF00FF41),
            modifier = Modifier.size(80.dp)
        )

        Spacer(modifier = Modifier.height(24.dp))

        Text(
            text = if (language == "en") "Appointment Confirmed!" else "अपॉइंटमेंट की पुष्टि हुई!",
            color = Color.White,
            fontSize = 28.sp,
            fontWeight = FontWeight.Bold,
            textAlign = androidx.compose.ui.text.style.TextAlign.Center
        )

        Spacer(modifier = Modifier.height(32.dp))

        // Appointment Token (Prominent Display)
        Surface(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp),
            shape = androidx.compose.foundation.shape.RoundedCornerShape(12.dp),
            color = Color(0xFF00D9FF).copy(alpha = 0.2f)
        ) {
            Column(
                modifier = Modifier
                    .padding(24.dp)
                    .fillMaxWidth(),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Text(
                    text = if (language == "en") "Token Number" else "टोकन नंबर",
                    color = Color(0xFF00D9FF),
                    fontSize = 14.sp
                )

                Spacer(modifier = Modifier.height(12.dp))

                Text(
                    text = viewModel.appointmentToken.value,
                    color = Color.White,
                    fontSize = 32.sp,
                    fontWeight = FontWeight.Bold,
                    fontFamily = androidx.compose.ui.text.font.FontFamily.Monospace
                )

                Spacer(modifier = Modifier.height(16.dp))

                Text(
                    text = if (language == "en")
                        "Please take a picture of this screen"
                    else
                        "कृपया इस स्क्रीन की तस्वीर लें",
                    color = Color.White.copy(alpha = 0.7f),
                    fontSize = 12.sp,
                    fontStyle = androidx.compose.ui.text.font.FontStyle.Italic
                )
            }
        }

        Spacer(modifier = Modifier.height(32.dp))

        // Appointment Summary
        Surface(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp),
            shape = androidx.compose.foundation.shape.RoundedCornerShape(12.dp),
            color = Color.White.copy(alpha = 0.08f)
        ) {
            Column(modifier = Modifier.padding(20.dp)) {
                AppointmentSummaryRow(
                    label = if (language == "en") "Doctor:" else "डॉक्टर:",
                    value = viewModel.selectedDoctor.value?.name ?: "-"
                )

                Spacer(modifier = Modifier.height(12.dp))

                AppointmentSummaryRow(
                    label = if (language == "en") "Department:" else "विभाग:",
                    value = viewModel.selectedDoctor.value?.department ?: "-"
                )

                Spacer(modifier = Modifier.height(12.dp))

                AppointmentSummaryRow(
                    label = if (language == "en") "Date:" else "तारीख:",
                    value = viewModel.selectedDate.value?.toString() ?: "-"
                )

                Spacer(modifier = Modifier.height(12.dp))

                AppointmentSummaryRow(
                    label = if (language == "en") "Time:" else "समय:",
                    value = viewModel.selectedTimeSlot.value?.startTime ?: "-"
                )

                Spacer(modifier = Modifier.height(12.dp))

                AppointmentSummaryRow(
                    label = if (language == "en") "Patient:" else "रोगी:",
                    value = viewModel.patientName.value
                )
            }
        }

        Spacer(modifier = Modifier.height(32.dp))

        // Action Buttons
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            Button(
                onClick = {
                    val cabin = viewModel.selectedDoctor.value?.cabin ?: ""
                    val speech = if (language == "en")
                        "Taking you to doctor's cabin $cabin. Please follow me."
                    else
                        "आपको डॉक्टर की कैबिन $cabin में ले जा रहे हैं। कृपया मेरा अनुसरण करें।"
                    robot?.speak(TtsRequest.create(speech = speech, isShowOnConversationLayer = false))
                    
                    if (cabin.isNotBlank()) {
                        robot?.goTo(cabin)
                    } else {
                        viewModel.selectedDoctor.value?.let { doctor ->
                            robot?.goTo(doctor.name)
                        }
                    }
                },
                modifier = Modifier
                    .weight(1f)
                    .height(56.dp),
                colors = ButtonDefaults.buttonColors(
                    containerColor = Color(0xFF00D9FF)
                )
            ) {
                Text(
                    text = if (language == "en") "Navigate to Doctor" else "डॉक्टर के पास नेविगेट करें",
                    color = Color.Black,
                    fontWeight = FontWeight.Bold
                )
            }

            Button(
                onClick = {
                    robot?.speak(TtsRequest.create(
                        speech = if (language == "en") "Returning to home." else "घर लौट रहे हैं।",
                        isShowOnConversationLayer = false
                    ))
                    onHome()
                },
                modifier = Modifier
                    .weight(1f)
                    .height(56.dp),
                colors = ButtonDefaults.buttonColors(
                    containerColor = Color(0xFFB100FF)
                )
            ) {
                Text(
                    text = if (language == "en") "Return to Home" else "घर लौटें",
                    color = Color.White,
                    fontWeight = FontWeight.Bold
                )
            }
        }
    }
}

/**
 * Appointment Summary Row
 */
@Composable
fun AppointmentSummaryRow(
    label: String,
    value: String
) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(
            text = label,
            color = Color.White.copy(alpha = 0.6f),
            fontSize = 12.sp
        )

        Text(
            text = value,
            color = Color.White,
            fontSize = 14.sp,
            fontWeight = FontWeight.Bold
        )
    }
}

/**
 * Header Component
 */
@Composable
fun AppointmentHeader(
    currentStep: Int,
    language: String,
    onLanguageToggle: () -> Unit,
    onBackPress: () -> Unit
) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        // Back Button
        Icon(
            imageVector = Icons.Default.ArrowBack,
            contentDescription = "Back",
            tint = Color.White,
            modifier = Modifier
                .size(28.dp)
                .clickable(onClick = onBackPress)
        )

        // Title
        Text(
            text = if (language == "en") "Step $currentStep: Book Appointment" else "चरण $currentStep: अपॉइंटमेंट बुक करें",
            color = Color.White,
            fontSize = 22.sp,
            fontWeight = FontWeight.Bold
        )

        // Language Toggle
        Row(
            modifier = Modifier.clickable(onClick = onLanguageToggle),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(4.dp)
        ) {
            Icon(
                imageVector = Icons.Default.Language,
                contentDescription = "Language",
                tint = Color.White,
                modifier = Modifier.size(20.dp)
            )

            Text(
                text = if (language == "en") "EN" else "HI",
                color = Color.White,
                fontSize = 12.sp,
                fontWeight = FontWeight.Bold
            )
        }
    }
}

/**
 * Progress Bar Component
 */
@Composable
fun AppointmentProgressBar(currentStep: Int) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .height(4.dp),
        horizontalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        repeat(5) { step ->
            Box(
                modifier = Modifier
                    .weight(1f)
                    .height(4.dp)
                    .background(
                        color = if (step < currentStep)
                            Color(0xFF00D9FF)
                        else
                            Color.White.copy(alpha = 0.2f),
                        shape = androidx.compose.foundation.shape.RoundedCornerShape(2.dp)
                    )
            )
        }
    }
}

/**
 * Navigation Buttons
 */
@Composable
fun AppointmentNavigationButtons(
    viewModel: AppointmentViewModel,
    currentLanguage: String,
    canProceed: Boolean,
    robot: Robot?
) {
    Row(
        modifier = Modifier
            .fillMaxWidth(),
        horizontalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        // Back Button
        if (viewModel.currentStep.value > 1) {
            Button(
                onClick = { viewModel.goToPreviousStep() },
                modifier = Modifier
                    .weight(1f)
                    .height(48.dp),
                colors = ButtonDefaults.outlinedButtonColors(
                    contentColor = Color.White
                )
            ) {
                Text(if (currentLanguage == "en") "Back" else "वापस")
            }
        }

        // Next Button
        Button(
            onClick = {
                if (viewModel.currentStep.value == 4) {
                    if (viewModel.confirmDetails()) {
                        viewModel.submitAppointment { success, message ->
                            if (!success) {
                                // Handle error - could use a toast or snackbar
                                android.util.Log.e("Appointment", message)
                            } else {
                                robot?.speak(TtsRequest.create(
                                    speech = if (currentLanguage == "en") 
                                        "Appointment confirmed! Your token is ${viewModel.appointmentToken.value}" 
                                        else "अपॉइंटमेंट की पुष्टि हुई! आपका टोकन ${viewModel.appointmentToken.value} है",
                                    isShowOnConversationLayer = true
                                ))
                            }
                        }
                    }
                } else {
                    // Other steps move forward normally (handled by selecting items usually)
                }
            },
            enabled = canProceed,
            modifier = Modifier
                .weight(1f)
                .height(48.dp),
            colors = ButtonDefaults.buttonColors(
                containerColor = Color(0xFF00D9FF),
                disabledContainerColor = Color.White.copy(alpha = 0.1f)
            )
        ) {
            Text(
                text = if (currentLanguage == "en") "Next" else "अगला",
                color = if (canProceed) Color.Black else Color.White.copy(alpha = 0.3f),
                fontWeight = FontWeight.Bold
            )
        }
    }
}

/**
 * Check if user can proceed to next step
 */
fun canProceedToNextStep(viewModel: AppointmentViewModel): Boolean {
    return when (viewModel.currentStep.value) {
        1 -> viewModel.selectedDoctor.value != null
        2 -> viewModel.selectedDate.value != null
        3 -> viewModel.selectedTimeSlot.value != null
        4 -> viewModel.patientName.value.isNotEmpty() &&
             viewModel.patientPhone.value.isNotEmpty() &&
             viewModel.patientPhone.value.length >= 10
        else -> false
    }
}

