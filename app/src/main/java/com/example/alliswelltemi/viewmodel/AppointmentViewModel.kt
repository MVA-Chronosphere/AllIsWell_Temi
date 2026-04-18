package com.example.alliswelltemi.viewmodel

import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.State
import androidx.lifecycle.ViewModel
import com.example.alliswelltemi.data.Doctor
import com.example.alliswelltemi.data.TimeSlot
import java.time.LocalDate
import java.util.UUID

/**
 * ViewModel for managing appointment booking state across multiple steps
 * Steps: 1=doctor, 2=date, 3=time, 4=details, 5=confirm
 */
class AppointmentViewModel : ViewModel() {

    // Current step in the booking process (1-5)
    private val _currentStep = mutableStateOf(1)
    val currentStep: State<Int> = _currentStep

    // Selected doctor
    private val _selectedDoctor = mutableStateOf<Doctor?>(null)
    val selectedDoctor: State<Doctor?> = _selectedDoctor

    // Selected date
    private val _selectedDate = mutableStateOf<LocalDate?>(null)
    val selectedDate: State<LocalDate?> = _selectedDate

    // Selected time slot
    private val _selectedTimeSlot = mutableStateOf<TimeSlot?>(null)
    val selectedTimeSlot: State<TimeSlot?> = _selectedTimeSlot

    // Patient name (manual or voice input)
    private val _patientName = mutableStateOf("")
    val patientName: State<String> = _patientName

    // Patient phone (manual or voice input)
    private val _patientPhone = mutableStateOf("")
    val patientPhone: State<String> = _patientPhone

    // Generated appointment token
    private val _appointmentToken = mutableStateOf("")
    val appointmentToken: State<String> = _appointmentToken

    // Loading state
    private val _isLoading = mutableStateOf(false)
    val isLoading: State<Boolean> = _isLoading

    // Available departments list
    private val _availableDepartments = mutableStateOf(emptyList<String>())
    val availableDepartments: State<List<String>> = _availableDepartments

    // Doctors in selected department
    private val _doctorsInDepartment = mutableStateOf(emptyList<Doctor>())
    val doctorsInDepartment: State<List<Doctor>> = _doctorsInDepartment

    // Available time slots
    private val _availableTimeSlots = mutableStateOf(emptyList<TimeSlot>())
    val availableTimeSlots: State<List<TimeSlot>> = _availableTimeSlots

    // Voice input handling
    private val _voiceInput = mutableStateOf("")
    val voiceInput: State<String> = _voiceInput

    /**
     * Initialize departments (call when screen loads)
     */
    fun initializeDepartments(departments: List<String>) {
        _availableDepartments.value = departments
    }

    /**
     * Set selected doctor and move to next step
     */
    fun selectDoctor(doctor: Doctor) {
        _selectedDoctor.value = doctor
        _currentStep.value = 2
    }

    /**
     * Set selected date and move to next step
     */
    fun selectDate(date: LocalDate) {
        _selectedDate.value = date
        _currentStep.value = 3
    }

    /**
     * Set selected time slot and move to next step
     */
    fun selectTimeSlot(timeSlot: TimeSlot) {
        _selectedTimeSlot.value = timeSlot
        _currentStep.value = 4
    }

    /**
     * Update patient name
     */
    fun setPatientName(name: String) {
        _patientName.value = name
    }

    /**
     * Update patient phone
     */
    fun setPatientPhone(phone: String) {
        _patientPhone.value = phone
    }

    /**
     * Validate and move to confirmation step
     */
    fun confirmDetails(): Boolean {
        val name = _patientName.value.trim()
        val phone = _patientPhone.value.trim()

        if (name.isEmpty() || phone.isEmpty()) {
            return false
        }

        if (phone.length < 10) {
            return false
        }

        _currentStep.value = 5
        generateAppointmentToken()
        return true
    }

    /**
     * Generate unique appointment token
     */
    private fun generateAppointmentToken() {
        val timestamp = System.currentTimeMillis()
        val uuid = UUID.randomUUID().toString().take(6).uppercase()
        _appointmentToken.value = "APT-$uuid-$timestamp".take(20)
    }

    /**
     * Get doctors by department
     */
    fun loadDoctorsByDepartment(department: String, doctorList: List<Doctor>) {
        _doctorsInDepartment.value = doctorList.filter { it.department == department }
    }

    /**
     * Load available time slots
     */
    fun loadTimeSlots(timeSlots: List<TimeSlot>) {
        _availableTimeSlots.value = timeSlots
    }

    /**
     * Go back one step
     */
    fun goToPreviousStep() {
        if (_currentStep.value > 1) {
            _currentStep.value -= 1
        }
    }

    /**
     * Reset booking process (start over)
     */
    fun resetBooking() {
        _currentStep.value = 1
        _selectedDoctor.value = null
        _selectedDate.value = null
        _selectedTimeSlot.value = null
        _patientName.value = ""
        _patientPhone.value = ""
        _appointmentToken.value = ""
        _voiceInput.value = ""
    }

    /**
     * Handle voice input for appointment booking
     */
    fun handleVoiceInput(spokenText: String) {
        _voiceInput.value = spokenText
        // Voice input processing logic would go here based on current step
        // For now, we just capture it
    }

    /**
     * Set loading state
     */
    fun setLoading(loading: Boolean) {
        _isLoading.value = loading
    }

    /**
     * Get formatted appointment summary
     */
    fun getAppointmentSummary(): String {
        val doctor = _selectedDoctor.value ?: return ""
        val date = _selectedDate.value ?: return ""
        val time = _selectedTimeSlot.value ?: return ""

        return """
            Doctor: ${doctor.name}
            Department: ${doctor.department}
            Date: $date
            Time: ${time.startTime}
            Patient: ${_patientName.value}
            Phone: ${_patientPhone.value}
        """.trimIndent()
    }
}

