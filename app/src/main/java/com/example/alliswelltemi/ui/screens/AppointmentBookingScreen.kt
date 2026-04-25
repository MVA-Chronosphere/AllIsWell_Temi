package com.example.alliswelltemi.ui.screens

import androidx.compose.foundation.Image
import androidx.compose.ui.res.painterResource
import com.example.alliswelltemi.R
import androidx.compose.animation.*
import androidx.compose.animation.core.*
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.interaction.collectIsFocusedAsState
import androidx.compose.material.ripple.rememberRipple
import androidx.compose.ui.focus.FocusDirection
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.alliswelltemi.ui.components.*
import com.example.alliswelltemi.ui.theme.HospitalColors
import com.example.alliswelltemi.data.Doctor
import com.example.alliswelltemi.data.DoctorData
import com.example.alliswelltemi.data.TimeSlot
import com.example.alliswelltemi.viewmodel.AppointmentViewModel
import com.example.alliswelltemi.viewmodel.DoctorsViewModel
import com.robotemi.sdk.Robot
import com.robotemi.sdk.TtsRequest
import java.time.LocalDate
import java.time.YearMonth
import androidx.compose.ui.text.intl.Locale
import androidx.compose.ui.text.toUpperCase
import androidx.activity.compose.BackHandler
import com.robotemi.sdk.SttLanguage

@Composable
fun AppointmentBookingScreen(
    robot: Robot? = null,
    onBackPress: () -> Unit = {},
    viewModel: AppointmentViewModel = viewModel(),
    doctorsViewModel: DoctorsViewModel = viewModel(),
    currentLanguage: String = "en"
) {
    DisposableEffect(Unit) {
        onDispose {
            viewModel.resetBooking()
        }
    }

    BackHandler {
        if (viewModel.currentStep.value > 1) {
            viewModel.goToPreviousStep()
        } else {
            viewModel.resetBooking()
            onBackPress()
        }
    }

    val departments by doctorsViewModel.departments
    val doctorsFromApi by doctorsViewModel.doctors
    val isDoctorsLoading by doctorsViewModel.isLoading
    val doctorsError by doctorsViewModel.error

    LaunchedEffect(Unit) {
        if (doctorsFromApi.isEmpty()) {
            doctorsViewModel.refresh()
        }
    }

    LaunchedEffect(departments, doctorsFromApi) {
        if (departments.isNotEmpty()) {
            viewModel.initializeDepartments(departments)
            viewModel.loadTimeSlots(DoctorData.getAvailableTimeSlots())
            
            if (viewModel.doctorsInDepartment.value.isEmpty()) {
                departments.firstOrNull()?.let { dept ->
                    val doctorsForDept = doctorsFromApi.filter { it.department == dept }
                    if (doctorsForDept.isNotEmpty()) {
                        viewModel.loadDoctorsByDepartment(dept, doctorsForDept)
                    }
                }
            }
        }
    }

    // --- Insert TemiNavBar at the top ---
    Column(modifier = Modifier.fillMaxSize()) {
        TemiNavBar(currentLanguage = currentLanguage)

        Column(
            modifier = Modifier
                .fillMaxSize()
                .background(Color(0xFFEDE7D8))
                .verticalScroll(rememberScrollState()),
            horizontalAlignment = Alignment.Start,
            verticalArrangement = Arrangement.Top
        ) {
            // GLOBAL HEADER
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 40.dp, vertical = 24.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                // BACK BUTTON (Circular, soft shadow)
                Surface(
                    modifier = Modifier
                        .size(48.dp)
                        .shadow(elevation = 4.dp, shape = CircleShape)
                        .clip(CircleShape)
                        .clickable {
                            if (viewModel.currentStep.value > 1) {
                                viewModel.goToPreviousStep()
                            } else {
                                viewModel.resetBooking()
                                onBackPress()
                            }
                        },
                    color = Color.White,
                    shape = CircleShape
                ) {
                    Box(contentAlignment = Alignment.Center) {
                        Icon(
                            imageVector = Icons.Default.ArrowBack,
                            contentDescription = "Back",
                            tint = HospitalColors.Carob,
                            modifier = Modifier.size(24.dp)
                        )
                    }
                }

                Spacer(modifier = Modifier.width(24.dp))

                // HOSPITAL LOGO
                Image(
                    painter = painterResource(id = R.drawable.hospital_logo),
                    contentDescription = "Hospital Logo",
                    modifier = Modifier
                        .height(100.dp)
                        .wrapContentWidth(),
                    alignment = Alignment.CenterStart
                )

                // CENTERED TITLE
                Text(
                    text = (if (currentLanguage == "en") "BOOK APPOINTMENT" else "अपॉइंटमेंट बुक करें").toUpperCase(Locale.current),
                    style = MaterialTheme.typography.titleLarge.copy(
                        fontWeight = FontWeight.Black,
                        fontSize = 30.sp,
                        letterSpacing = 1.sp
                    ),
                    modifier = Modifier.weight(1f),
                    textAlign = TextAlign.Center,
                    color = HospitalColors.Carob
                )

                // Placeholder to balance title
                Spacer(modifier = Modifier.width(72.dp))
            }

            // PROGRESS BAR
            Box(modifier = Modifier.fillMaxWidth(), contentAlignment = Alignment.Center) {
                AppointmentProgressBar(currentStep = viewModel.currentStep.value)
            }

            Spacer(modifier = Modifier.height(40.dp))

            // MAIN CONTENT CONTAINER
            Box(
                modifier = Modifier
                    .widthIn(max = 1100.dp)
                    .fillMaxWidth(0.95f)
                    .align(Alignment.CenterHorizontally),
                contentAlignment = Alignment.TopCenter
            ) {
                if (viewModel.isLoading.value || isDoctorsLoading) {
                    CircularProgressIndicator(
                        color = HospitalColors.Chai,
                        modifier = Modifier.size(64.dp)
                    )
                } else if (doctorsError != null && doctorsFromApi.isEmpty()) {
                    ErrorState(error = doctorsError, onRetry = { doctorsViewModel.refresh() })
                } else {
                    AnimatedContent(
                        targetState = viewModel.currentStep.value,
                        label = "step_transition",
                        transitionSpec = {
                            slideInHorizontally(initialOffsetX = { it }) togetherWith
                            slideOutHorizontally(targetOffsetX = { -it })
                        }
                    ) { step ->
                        when (step) {
                            1 -> StepSelectDoctor(
                                viewModel = viewModel,
                                language = currentLanguage,
                                robot = robot,
                                departments = departments,
                                allDoctorsFromApi = doctorsFromApi,
                                onBack = {
                                    viewModel.resetBooking()
                                    onBackPress()
                                }
                            )
                            2 -> StepSelectDateAndTime(
                                viewModel = viewModel,
                                language = currentLanguage,
                                robot = robot,
                                timeSlots = viewModel.availableTimeSlots.value,
                                onBack = { viewModel.goToPreviousStep() }
                            )
                            3 -> StepPatientDetails(
                                viewModel = viewModel,
                                language = currentLanguage,
                                robot = robot,
                                onBack = { viewModel.goToPreviousStep() }
                            )
                            4 -> StepConfirmation(
                                viewModel = viewModel,
                                language = currentLanguage,
                                robot = robot,
                                onHome = onBackPress
                            )
                            else -> Unit
                        }
                    }
                }
            }

            Spacer(modifier = Modifier.height(40.dp))
        }
    }
}

/**
 * Modern Clinical Header with Title and Progress Bar (Top Aligned)
 */
@Composable
fun AppointmentHeader(
    currentStep: Int,
    language: String
) {
    // Header is now handled by TemiScreenScaffold
    // Progress bar only
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 32.dp),
        horizontalArrangement = Arrangement.Center
    ) {
        AppointmentProgressBar(currentStep = currentStep)
    }
}

@Composable
fun ErrorState(error: String?, onRetry: () -> Unit) {
    Surface(
        modifier = Modifier
            .widthIn(max = 600.dp)
            .shadow(
                elevation = 12.dp,
                shape = RoundedCornerShape(24.dp),
                ambientColor = HospitalColors.Carob.copy(alpha = 0.1f)
            ),
        shape = RoundedCornerShape(24.dp),
        color = HospitalColors.Vanilla,
        border = BorderStroke(2.dp, HospitalColors.ErrorRed.copy(alpha = 0.2f))
    ) {
        Column(
            modifier = Modifier.padding(48.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            Icon(
                imageVector = Icons.Default.Error,
                contentDescription = null,
                tint = HospitalColors.ErrorRed,
                modifier = Modifier.size(56.dp)
            )
            
            Text(
                text = "UNABLE TO LOAD DOCTORS",
                style = MaterialTheme.typography.headlineSmall.copy(
                    fontWeight = FontWeight.Black,
                    fontSize = 18.sp,
                    color = HospitalColors.ErrorRed
                )
            )
            
            Text(
                text = error ?: "Please check your connection",
                style = MaterialTheme.typography.bodyLarge.copy(
                    color = HospitalColors.Carob.copy(alpha = 0.7f)
                ),
                textAlign = TextAlign.Center
            )
            
            Spacer(modifier = Modifier.height(16.dp))
            
            ClinicalButton(
                text = if (error != null) "RETRY" else "RETRY",
                onClick = onRetry,
                modifier = Modifier
                    .width(200.dp)
                    .height(48.dp),
                containerColor = HospitalColors.Chai
            )
        }
    }
}

@Composable
fun StepSelectDoctor(
    viewModel: AppointmentViewModel,
    language: String,
    robot: Robot?,
    departments: List<String>,
    allDoctorsFromApi: List<Doctor>,
    onBack: () -> Unit
) {
    var selectedDepartment by remember(departments, viewModel.selectedDepartment.value) { 
        mutableStateOf(viewModel.selectedDepartment.value ?: departments.firstOrNull() ?: "")
    }

    LaunchedEffect(selectedDepartment, allDoctorsFromApi) {
        if (selectedDepartment.isNotEmpty() && allDoctorsFromApi.isNotEmpty()) {
            val doctorsForDept = allDoctorsFromApi.filter { it.department == selectedDepartment }
            viewModel.loadDoctorsByDepartment(selectedDepartment, doctorsForDept)
        }
    }

    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(bottom = 40.dp),
        verticalArrangement = Arrangement.spacedBy(40.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        // DEPARTMENT ROW
        Column(
            modifier = Modifier.fillMaxWidth(),
            verticalArrangement = Arrangement.spacedBy(16.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(
                text = (if (language == "en") "SELECT DEPARTMENT" else "विभाग चुनें").toUpperCase(Locale.current),
                style = MaterialTheme.typography.labelSmall.copy(
                    fontSize = 18.sp,
                    fontWeight = FontWeight.Black,
                    letterSpacing = 2.sp,
                    color = HospitalColors.Carob
                ),
                modifier = Modifier.fillMaxWidth(),
                textAlign = TextAlign.Center
            )

            // Single Row for Departments
            androidx.compose.foundation.lazy.LazyRow(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(12.dp, Alignment.CenterHorizontally),
                contentPadding = PaddingValues(horizontal = 16.dp)
            ) {
                items(departments.size) { index ->
                    val dept = departments[index]
                    val isSelected = selectedDepartment == dept
                    DepartmentChip(
                        department = dept,
                        isSelected = isSelected,
                        onClick = {
                            selectedDepartment = dept
                            viewModel.selectDepartment(dept, allDoctorsFromApi.filter { it.department == dept })
                        },
                        modifier = Modifier.widthIn(min = 160.dp)
                    )
                }
            }
        }

        // DOCTORS LIST
        Column(
            modifier = Modifier.fillMaxWidth(),
            verticalArrangement = Arrangement.spacedBy(24.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(
                text = (if (language == "en") "SELECT DOCTOR" else "डॉक्टर चुनें").toUpperCase(Locale.current),
                style = MaterialTheme.typography.labelSmall.copy(
                    fontSize = 18.sp,
                    fontWeight = FontWeight.Black,
                    letterSpacing = 2.sp,
                    color = HospitalColors.Carob
                ),
                modifier = Modifier.fillMaxWidth(),
                textAlign = TextAlign.Center
            )

            val doctorsInDept = viewModel.doctorsInDepartment.value
            if (doctorsInDept.isEmpty()) {
                Text(
                    text = if (language == "en") "No doctors available" else "कोई डॉक्टर उपलब्ध नहीं है",
                    style = MaterialTheme.typography.bodyLarge.copy(
                        color = HospitalColors.Carob.copy(alpha = 0.5f)
                    )
                )
            } else {
                doctorsInDept.forEach { doctor ->
                    AppointmentDoctorCard(
                        doctor = doctor,
                        isSelected = viewModel.selectedDoctor.value?.id == doctor.id,
                        language = language,
                        onClick = {
                            viewModel.selectDoctor(doctor)
                            val speech = if (language == "en")
                                "You selected ${doctor.name}. Please select a date."
                            else
                                "आपने ${doctor.name} को चुना। कृपया तारीख चुनें।"
                            robot?.speak(TtsRequest.create(speech = speech, isShowOnConversationLayer = false))
                        }
                    )
                }
            }
        }

        Spacer(modifier = Modifier.height(16.dp))

        // NEXT BUTTON
        ClinicalButton(
            text = if (language == "en") "NEXT" else "अगला",
            onClick = { viewModel.goToNextStep() },
            modifier = Modifier
                .width(200.dp)
                .height(56.dp),
            enabled = viewModel.selectedDoctor.value != null,
            containerColor = HospitalColors.Chai
        )
    }
}

@Composable
fun DepartmentChip(
    department: String,
    isSelected: Boolean,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    val bgColor by animateColorAsState(
        targetValue = if (isSelected) HospitalColors.Chai else Color(0xFFF1F5F9),
        label = "dept_bg"
    )
    val textColor by animateColorAsState(
        targetValue = if (isSelected) Color.White else HospitalColors.Carob,
        label = "dept_text"
    )

    Surface(
        modifier = modifier
            .height(68.dp)
            .clip(RoundedCornerShape(34.dp))
            .clickable { onClick() },
        shape = RoundedCornerShape(34.dp),
        color = bgColor,
        border = if (!isSelected) BorderStroke(1.dp, HospitalColors.Chai.copy(alpha = 0.1f)) else null
    ) {
        Box(contentAlignment = Alignment.Center) {
            Text(
                text = department.toUpperCase(Locale.current),
                style = MaterialTheme.typography.labelLarge.copy(
                    fontWeight = FontWeight.Bold,
                    fontSize = 18.sp,
                    color = textColor
                ),
                modifier = Modifier.padding(horizontal = 24.dp)
            )
        }
    }
}

@Composable
fun AppointmentDoctorCard(
    doctor: Doctor,
    isSelected: Boolean,
    language: String,
    onClick: () -> Unit
) {
    val scale by animateFloatAsState(
        targetValue = if (isSelected) 1.03f else 1f,
        label = "card_scale",
        animationSpec = spring(
            dampingRatio = Spring.DampingRatioMediumBouncy,
            stiffness = Spring.StiffnessLow
        )
    )
    val bgColor = Color.White
    val contentColor = HospitalColors.Carob
    val elevation by animateFloatAsState(
        targetValue = if (isSelected) 16.dp.value else 4.dp.value,
        label = "card_elevation"
    )

    Surface(
        modifier = Modifier
            .fillMaxWidth(0.9f) // Slightly wider for better presence
            .height(145.dp) // Increased height for better interactivity
            .shadow(
                elevation = elevation.dp,
                shape = RoundedCornerShape(28.dp),
                ambientColor = HospitalColors.Carob.copy(alpha = 0.15f)
            )
            .graphicsLayer {
                scaleX = scale
                scaleY = scale
            }
            .clip(RoundedCornerShape(28.dp))
            .clickable(
                indication = rememberRipple(bounded = true, color = HospitalColors.Chai.copy(alpha = 0.1f)),
                interactionSource = remember { MutableInteractionSource() }
            ) { onClick() },
        shape = RoundedCornerShape(28.dp),
        color = bgColor,
        border = BorderStroke(
            width = if (isSelected) 3.dp else 1.dp,
            color = if (isSelected) Color(0xFF2563EB) else HospitalColors.Chai.copy(alpha = 0.1f)
        )
    ) {
        Row(
            modifier = Modifier
                .padding(20.dp)
                .fillMaxSize(),
            horizontalArrangement = Arrangement.spacedBy(20.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            // Avatar Circle with dynamic background
            Box(
                modifier = Modifier
                    .size(72.dp)
                    .background(
                        HospitalColors.Chai.copy(alpha = 0.08f),
                        CircleShape
                    ),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = Icons.Default.Person,
                    contentDescription = null,
                    tint = contentColor,
                    modifier = Modifier.size(44.dp)
                )
            }

            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = doctor.name.toUpperCase(Locale.current),
                    style = MaterialTheme.typography.titleLarge.copy(
                        color = contentColor,
                        fontWeight = FontWeight.Black,
                        fontSize = 25.sp,
                        letterSpacing = 0.5.sp
                    ),
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )
                Spacer(modifier = Modifier.height(4.dp))
                Text(
                    text = doctor.specialization.toUpperCase(Locale.current),
                    style = MaterialTheme.typography.labelSmall.copy(
                        color = HospitalColors.Carob.copy(alpha = 0.5f),
                        fontWeight = FontWeight.Bold,
                        fontSize = 16.sp,
                        letterSpacing = 1.2.sp
                    )
                )
            }

            // Status Indicator
            if (isSelected) {
                Surface(
                    shape = CircleShape,
                    color = Color(0xFF2563EB),
                    modifier = Modifier.size(32.dp)
                ) {
                    Icon(
                        imageVector = Icons.Default.Check,
                        contentDescription = "Selected",
                        tint = Color.White,
                        modifier = Modifier.padding(6.dp)
                    )
                }
            } else {
                Icon(
                    imageVector = Icons.Default.ChevronRight,
                    contentDescription = null,
                    tint = HospitalColors.Chai.copy(alpha = 0.3f),
                    modifier = Modifier.size(24.dp)
                )
            }
        }
    }
}

@Composable
fun StepSelectDateAndTime(
    viewModel: AppointmentViewModel,
    language: String,
    robot: Robot?,
    timeSlots: List<TimeSlot>,
    onBack: () -> Unit
) {
    var displayMonth by remember { mutableStateOf(YearMonth.now()) }

    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(bottom = 40.dp),
        verticalArrangement = Arrangement.spacedBy(40.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        // TWO-COLUMN LAYOUT: 60% Calendar, 40% Time
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(64.dp),
            verticalAlignment = Alignment.Top
        ) {
            // LEFT COLUMN: Calendar (60%)
            Column(
                modifier = Modifier.weight(0.6f),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.spacedBy(24.dp)
            ) {
                Text(
                    text = (if (language == "en") "CHOOSE DATE" else "तारीख चुनें").toUpperCase(Locale.current),
                    style = MaterialTheme.typography.labelSmall.copy(
                        fontSize = 18.sp,
                        fontWeight = FontWeight.Black,
                        letterSpacing = 2.sp,
                        color = HospitalColors.Carob
                    ),
                    modifier = Modifier.fillMaxWidth(),
                    textAlign = TextAlign.Center
                )

                Surface(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(24.dp),
                    color = Color.White,
                    shadowElevation = 2.dp
                ) {
                    Column(
                        modifier = Modifier.padding(24.dp),
                        horizontalAlignment = Alignment.CenterHorizontally,
                        verticalArrangement = Arrangement.spacedBy(20.dp)
                    ) {
                        // Month Navigation
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            MonthNavigationButton(
                                direction = "prev",
                                onClick = { displayMonth = displayMonth.minusMonths(1) }
                            )

                            Text(
                                text = "${displayMonth.month.name} ${displayMonth.year}".toUpperCase(Locale.current),
                                style = MaterialTheme.typography.titleMedium.copy(
                                    fontWeight = FontWeight.Black,
                                    color = HospitalColors.Carob,
                                    fontSize = 22.sp
                                )
                            )

                            MonthNavigationButton(
                                direction = "next",
                                onClick = { displayMonth = displayMonth.plusMonths(1) }
                            )
                        }

                        CalendarGrid(
                            yearMonth = displayMonth,
                            selectedDate = viewModel.selectedDate.value,
                            onDateSelected = { date ->
                                if (date >= LocalDate.now()) {
                                    viewModel.selectDate(date)
                                }
                            }
                        )
                    }
                }
            }

            // RIGHT COLUMN: Time Slots (40%)
            Column(
                modifier = Modifier.weight(0.4f),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.spacedBy(24.dp)
            ) {
                Text(
                    text = (if (language == "en") "SELECT THE TIME" else "समय चुनें").toUpperCase(Locale.current),
                    style = MaterialTheme.typography.labelSmall.copy(
                        fontSize = 18.sp,
                        fontWeight = FontWeight.Black,
                        letterSpacing = 2.sp,
                        color = HospitalColors.Carob
                    ),
                    modifier = Modifier.fillMaxWidth(),
                    textAlign = TextAlign.Center
                )

                if (viewModel.selectedDate.value == null) {
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(300.dp)
                            .background(Color.White.copy(alpha = 0.5f), RoundedCornerShape(24.dp)),
                        contentAlignment = Alignment.Center
                    ) {
                        Text("Please select a date first", color = HospitalColors.Carob.copy(alpha = 0.5f))
                    }
                } else {
                    // Time slot grid (2 columns)
                    Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
                        timeSlots.chunked(2).forEach { rowSlots ->
                            Row(horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                                rowSlots.forEach { slot ->
                                    TimeSlotButton(
                                        slot = slot,
                                        isSelected = viewModel.selectedTimeSlot.value == slot,
                                        onClick = { viewModel.selectTimeSlot(slot) },
                                        modifier = Modifier.weight(1f)
                                    )
                                }
                                if (rowSlots.size == 1) Spacer(modifier = Modifier.weight(1f))
                            }
                        }
                    }
                }
            }
        }

        // BUTTONS
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(24.dp, Alignment.CenterHorizontally)
        ) {
            ClinicalButton(
                text = if (language == "en") "BACK" else "वापस",
                onClick = onBack,
                modifier = Modifier.width(200.dp).height(56.dp),
                containerColor = Color.Black
            )

            ClinicalButton(
                text = if (language == "en") "NEXT" else "अगला",
                onClick = { viewModel.goToNextStep() },
                modifier = Modifier.width(200.dp).height(56.dp),
                enabled = viewModel.selectedDate.value != null && viewModel.selectedTimeSlot.value != null,
                containerColor = HospitalColors.Chai
            )
        }
    }
}

@Composable
fun MonthNavigationButton(
    direction: String,
    onClick: () -> Unit
) {
    val icon = if (direction == "prev") Icons.Default.ChevronLeft else Icons.Default.ChevronRight

    Surface(
        modifier = Modifier
            .size(44.dp)
            .shadow(
                elevation = 4.dp,
                shape = CircleShape,
                ambientColor = HospitalColors.Carob.copy(alpha = 0.1f)
            )
            .clip(CircleShape)
            .clickable(
                indication = rememberRipple(bounded = true),
                interactionSource = remember { MutableInteractionSource() }
            ) { onClick() },
        shape = CircleShape,
        color = HospitalColors.Vanilla,
        border = BorderStroke(2.dp, HospitalColors.Chai.copy(alpha = 0.15f))
    ) {
        Box(contentAlignment = Alignment.Center) {
            Icon(icon, "Navigate", tint = HospitalColors.Chai, modifier = Modifier.size(24.dp))
        }
    }
}

@Composable
fun TimeSlotButton(
    slot: TimeSlot,
    isSelected: Boolean,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    val bgColor by animateColorAsState(
        targetValue = if (isSelected) HospitalColors.Chai else Color.White,
        label = "slot_bg"
    )
    val textColor by animateColorAsState(
        targetValue = if (isSelected) Color.White else HospitalColors.Carob,
        label = "slot_text"
    )

    Surface(
        modifier = modifier
            .height(80.dp) // Matched to calendar day height if needed
            .clip(RoundedCornerShape(12.dp))
            .clickable(enabled = slot.available) { onClick() },
        shape = RoundedCornerShape(12.dp),
        color = if (slot.available) bgColor else Color.LightGray.copy(alpha = 0.3f),
        border = if (slot.available && !isSelected) BorderStroke(1.dp, HospitalColors.Chai.copy(alpha = 0.1f)) else null
    ) {
        Box(contentAlignment = Alignment.Center) {
            Text(
                text = slot.startTime,
                style = MaterialTheme.typography.bodyLarge.copy(
                    fontWeight = FontWeight.Bold,
                    color = if (slot.available) textColor else Color.Gray,
                    fontSize = 20.sp
                )
            )
        }
    }
}

@Composable
fun CalendarGrid(
    yearMonth: YearMonth,
    selectedDate: LocalDate?,
    onDateSelected: (LocalDate) -> Unit
) {
    val firstDay = yearMonth.atDay(1)
    val daysInMonth = yearMonth.lengthOfMonth()
    val firstDayOfWeek = firstDay.dayOfWeek.value - 1
    val today = LocalDate.now()

    Column(
        modifier = Modifier.fillMaxWidth(),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        // WEEKDAY HEADERS
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(10.dp)
        ) {
            listOf("MON", "TUE", "WED", "THU", "FRI", "SAT", "SUN").forEach { day ->
                Text(
                    text = day,
                    style = MaterialTheme.typography.labelSmall.copy(
                        fontWeight = FontWeight.ExtraBold,
                        color = HospitalColors.Carob.copy(alpha = 0.5f),
                        fontSize = 12.sp,
                        letterSpacing = 1.sp
                    ),
                    modifier = Modifier.width(82.dp), // Matched to new box width
                    textAlign = TextAlign.Center
                )
            }
        }

        Spacer(modifier = Modifier.height(12.dp))

        // CALENDAR DAYS
        var dayCounter = 1
        repeat((daysInMonth + firstDayOfWeek + 6) / 7) { week ->
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                repeat(7) { dayOfWeek ->
                    if (week == 0 && dayOfWeek < firstDayOfWeek || dayCounter > daysInMonth) {
                        Spacer(modifier = Modifier.size(width = 82.dp, height = 72.dp))
                    } else {
                        val date = yearMonth.atDay(dayCounter)
                        val isSelected = date == selectedDate
                        val isToday = date == today
                        val isPast = date.isBefore(today)

                        CalendarDayButton(
                            day = dayCounter,
                            isSelected = isSelected,
                            isToday = isToday,
                            isPast = isPast,
                            onClick = { onDateSelected(date) }
                        )

                        dayCounter++
                    }
                }
            }
            Spacer(modifier = Modifier.height(10.dp))
        }
    }
}

@Composable
fun CalendarDayButton(
    day: Int,
    isSelected: Boolean,
    isToday: Boolean,
    isPast: Boolean,
    onClick: () -> Unit
) {
    val bgColor by animateColorAsState(
        targetValue = if (isSelected) HospitalColors.Chai else Color.Transparent,
        label = "day_bg"
    )
    val textColor by animateColorAsState(
        targetValue = if (isSelected) Color.White else HospitalColors.Carob,
        label = "day_text"
    )

    Surface(
        modifier = Modifier
            .size(width = 82.dp, height = 72.dp)
            .clip(RoundedCornerShape(12.dp))
            .clickable(enabled = !isPast) { onClick() },
        shape = RoundedCornerShape(12.dp),
        color = bgColor,
        border = if (isToday && !isSelected) BorderStroke(2.dp, HospitalColors.Chai) else null
    ) {
        Box(contentAlignment = Alignment.Center) {
            Text(
                text = day.toString(),
                style = MaterialTheme.typography.bodyLarge.copy(
                    fontWeight = if (isSelected || isToday) FontWeight.Black else FontWeight.Medium,
                    color = if (isPast) Color.Gray.copy(alpha = 0.5f) else textColor,
                    fontSize = 20.sp
                )
            )
        }
    }
}

@Composable
fun StepPatientDetails(
    viewModel: AppointmentViewModel,
    language: String,
    robot: Robot?,
    onBack: () -> Unit
) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(bottom = 40.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(80.dp)
    ) {
        // FORM CONTAINER
        Column(
            modifier = Modifier.widthIn(max = 850.dp),
            verticalArrangement = Arrangement.spacedBy(24.dp)
        ) {
            ClinicalTextField(
                value = viewModel.patientName.value,
                onValueChange = { viewModel.setPatientName(it) },
                label = if (language == "en") "Full Name" else "पूरा नाम",
                leadingIcon = Icons.Default.Person,
                modifier = Modifier.height(120.dp),
                robot = robot
            )

            ClinicalTextField(
                value = viewModel.patientAge.value,
                onValueChange = { viewModel.setPatientAge(it) },
                label = if (language == "en") "Age" else "आयु",
                leadingIcon = Icons.Default.Cake,
                modifier = Modifier.height(120.dp),
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                robot = robot
            )

            ClinicalTextField(
                value = viewModel.patientPhone.value,
                onValueChange = { viewModel.setPatientPhone(it) },
                label = if (language == "en") "Mobile Number" else "मोबाइल नंबर",
                leadingIcon = Icons.Default.Phone,
                modifier = Modifier.height(120.dp),
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Phone),
                robot = robot
            )

            // GENDER
            Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
                Text(
                    text = (if (language == "en") "SELECT GENDER" else "लिंग चुनें").toUpperCase(Locale.current),
                    style = MaterialTheme.typography.labelSmall.copy(
                        fontWeight = FontWeight.Bold,
                        color = HospitalColors.DeepSlate.copy(alpha = 0.7f),
                        fontSize = 16.sp
                    )
                )
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    val genders = if (language == "en") listOf("Male", "Female", "Other") else listOf("पुरुष", "महिला", "अन्य")
                    genders.forEach { gender ->
                        GenderToggleButton(
                            label = gender,
                            isSelected = viewModel.patientGender.value == gender,
                            onClick = { viewModel.setPatientGender(gender) },
                            modifier = Modifier.weight(1f)
                        )
                    }
                }
            }
        }

        // VALIDATION MESSAGE
        if (viewModel.validationError.value.isNotEmpty()) {
            Text(
                text = viewModel.validationError.value,
                color = Color.Red,
                style = MaterialTheme.typography.bodySmall,
                fontWeight = FontWeight.Bold
            )
        }

        // BUTTONS
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(16.dp, Alignment.CenterHorizontally)
        ) {
            ClinicalButton(
                text = if (language == "en") "BACK" else "वापस",
                onClick = onBack,
                modifier = Modifier.width(200.dp).height(56.dp),
                containerColor = Color.Black
            )

            val isFormValid = viewModel.patientName.value.isNotBlank() &&
                    viewModel.patientAge.value.isNotBlank() &&
                    viewModel.patientPhone.value.length == 10 &&
                    viewModel.patientGender.value.isNotBlank()

            ClinicalButton(
                text = if (language == "en") "CONFIRM BOOKING" else "बुकिंग की पुष्टि करें",
                onClick = {
                    if (viewModel.confirmDetails()) {
                        val speech = if (language == "en")
                            "Appointment confirmed! Your token is ${viewModel.appointmentToken.value}"
                        else
                            "अपॉइंटमेंट की पुष्टि हो गई है! आपका टोकन ${viewModel.appointmentToken.value} है"
                        robot?.speak(TtsRequest.create(speech = speech, isShowOnConversationLayer = false))
                    }
                },
                modifier = Modifier.width(200.dp).height(56.dp),
                containerColor = HospitalColors.SuccessGreen,
                enabled = isFormValid
            )
        }
    }
}

@Composable
fun GenderToggleButton(
    label: String,
    isSelected: Boolean,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    val bgColor by animateColorAsState(
        targetValue = if (isSelected) Color(0xFF2563EB) else Color.White,
        label = "gender_bg",
        animationSpec = tween(durationMillis = 300)
    )
    val textColor by animateColorAsState(
        targetValue = if (isSelected) Color.White else HospitalColors.Carob,
        label = "gender_text",
        animationSpec = tween(durationMillis = 300)
    )
    val elevation by animateFloatAsState(
        targetValue = if (isSelected) 8.dp.value else 2.dp.value,
        label = "gender_elevation"
    )

    Surface(
        modifier = modifier
            .height(84.dp)
            .shadow(
                elevation = elevation.dp,
                shape = RoundedCornerShape(12.dp),
                ambientColor = HospitalColors.Carob.copy(alpha = 0.1f)
            )
            .clip(RoundedCornerShape(12.dp))
            .clickable(
                indication = rememberRipple(bounded = true),
                interactionSource = remember { MutableInteractionSource() }
            ) { onClick() },
        shape = RoundedCornerShape(12.dp),
        color = bgColor,
        border = BorderStroke(
            width = if (isSelected) 2.dp else 1.dp,
            color = if (isSelected) Color(0xFF2563EB) else HospitalColors.Chai.copy(alpha = 0.1f)
        )
    ) {
        Box(contentAlignment = Alignment.Center) {
            Text(
                text = label.toUpperCase(Locale.current),
                color = textColor,
                style = MaterialTheme.typography.labelLarge.copy(
                    fontWeight = FontWeight.Black,
                    fontSize = 18.sp,
                    letterSpacing = 0.5.sp
                )
            )
        }
    }
}

@Composable
fun StepConfirmation(
    viewModel: AppointmentViewModel,
    language: String,
    robot: Robot?,
    onHome: () -> Unit
) {
    var visible by remember { mutableStateOf(false) }
    LaunchedEffect(Unit) {
        visible = true
    }

    AnimatedVisibility(
        visible = visible,
        enter = fadeIn(animationSpec = tween(600)) + expandVertically(animationSpec = tween(600))
    ) {
        ConfirmationSummary(
            viewModel = viewModel,
            language = language,
            robot = robot,
            onHome = onHome
        )
    }
}

@Composable
fun ConfirmationSummary(
    viewModel: AppointmentViewModel,
    language: String,
    robot: Robot?,
    onHome: () -> Unit
) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(bottom = 40.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(32.dp)
    ) {
        // SUCCESS ICON
        Icon(
            imageVector = Icons.Default.CheckCircle,
            contentDescription = null,
            tint = HospitalColors.SuccessGreen,
            modifier = Modifier.size(80.dp)
        )

        Text(
            text = (if (language == "en") "BOOKING SUCCESSFUL" else "बुकिंग सफल रही").toUpperCase(Locale.current),
            style = MaterialTheme.typography.headlineLarge.copy(fontWeight = FontWeight.Black),
            color = HospitalColors.SuccessGreen
        )

        // TOKEN CARD
        Surface(
            modifier = Modifier.width(360.dp),
            shape = RoundedCornerShape(24.dp),
            color = HospitalColors.RoyalBlue,
            shadowElevation = 8.dp
        ) {
            Column(
                modifier = Modifier.padding(24.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Text(
                    text = if (language == "en") "APPOINTMENT TOKEN" else "अपॉइंटमेंट टोकन",
                    style = MaterialTheme.typography.labelMedium.copy(
                        fontWeight = FontWeight.Bold,
                        color = Color.White.copy(alpha = 0.8f),
                        fontSize = 16.sp
                    )
                )
                Spacer(modifier = Modifier.height(8.dp))
                Text(
                    text = viewModel.appointmentToken.value,
                    style = MaterialTheme.typography.headlineMedium.copy(
                        fontWeight = FontWeight.Black,
                        letterSpacing = 1.sp,
                        fontSize = 40.sp
                    ),
                    color = Color.White
                )
            }
        }

        // SUMMARY CARD
        Surface(
            modifier = Modifier.fillMaxWidth(0.8f),
            shape = RoundedCornerShape(24.dp),
            color = Color.White,
            shadowElevation = 2.dp
        ) {
            Column(modifier = Modifier.padding(24.dp), verticalArrangement = Arrangement.spacedBy(12.dp)) {
                SummaryRow(label = if (language == "en") "Doctor" else "डॉक्टर", value = viewModel.selectedDoctor.value?.name ?: "")
                SummaryRow(label = if (language == "en") "Date" else "तारीख", value = viewModel.selectedDate.value.toString())
                SummaryRow(label = if (language == "en") "Time" else "समय", value = viewModel.selectedTimeSlot.value?.startTime ?: "")
                SummaryRow(label = if (language == "en") "Patient" else "मरीज", value = viewModel.patientName.value)
            }
        }

        // BUTTONS
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(16.dp, Alignment.CenterHorizontally)
        ) {
            ClinicalButton(
                text = if (language == "en") "CANCEL" else "रद्द करें",
                onClick = { viewModel.resetBooking(); onHome() },
                modifier = Modifier.width(180.dp).height(64.dp),
                containerColor = HospitalColors.ErrorRed
            )
            ClinicalButton(
                text = if (language == "en") "EDIT" else "संपादित करें",
                onClick = { viewModel.editAppointment() },
                modifier = Modifier.width(180.dp).height(64.dp),
                containerColor = Color.Black
            )
            ClinicalButton(
                text = if (language == "en") "HOME" else "होम",
                onClick = { viewModel.resetBooking(); onHome() },
                modifier = Modifier.width(200.dp).height(64.dp),
                containerColor = HospitalColors.Chai
            )
        }
    }
}

@Composable
fun SummaryRow(
    label: String,
    value: String
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 4.dp),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(
            text = label,
            style = MaterialTheme.typography.bodyMedium.copy(
                color = HospitalColors.DeepSlate.copy(alpha = 0.6f),
                fontWeight = FontWeight.Medium,
                fontSize = 18.sp
            )
        )
        Text(
            text = value,
            style = MaterialTheme.typography.bodyLarge.copy(
                fontSize = 20.sp
            ),
            color = HospitalColors.DeepSlate,
            fontWeight = FontWeight.Bold,
            textAlign = TextAlign.End
        )
    }
}

/**
 * Modern Segmented Progress Bar (Horizontal slide transition)
 * - 4 equal steps with active blue, inactive light gray
 * - Smooth color animation
 */
@Composable
fun AppointmentProgressBar(currentStep: Int) {
    Row(
        modifier = Modifier
            .width(400.dp)
            .height(6.dp),
        horizontalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        repeat(4) { step ->
            val isActive = (step + 1) <= currentStep
            Box(
                modifier = Modifier
                    .weight(1f)
                    .fillMaxHeight()
                    .background(
                        color = if (isActive) Color(0xFF2563EB) else Color.LightGray.copy(alpha = 0.5f),
                        shape = RoundedCornerShape(3.dp)
                    )
            )
        }
    }
}
