package com.example.alliswelltemi.viewmodel

import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.State
import androidx.lifecycle.ViewModel
import com.example.alliswelltemi.data.Doctor
import com.example.alliswelltemi.data.TimeSlot
import java.time.LocalDate
import java.util.UUID

/**
 * ViewModel for managing appointment booking state across 4 steps
 * Steps: 1=Doctor Selection, 2=Date/Time Selection, 3=Patient Details, 4=Confirmation
 */
class AppointmentViewModel : ViewModel() {

    // Current step in the booking process (1-4)
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

    // Patient age
    private val _patientAge = mutableStateOf("")
    val patientAge: State<String> = _patientAge

    // Patient date of birth (Removed for simplification)
    private val _patientDOB = mutableStateOf("")
    val patientDOB: State<String> = _patientDOB

    // Patient gender
    private val _patientGender = mutableStateOf("")
    val patientGender: State<String> = _patientGender

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

    // Submission state - controls whether to show form or summary
    private val _isSubmitted = mutableStateOf(false)
    val isSubmitted: State<Boolean> = _isSubmitted

    // Validation error message
    private val _validationError = mutableStateOf("")
    val validationError: State<String> = _validationError

    // Selected department
    private val _selectedDepartment = mutableStateOf<String?>(null)
    val selectedDepartment: State<String?> = _selectedDepartment

    /**
     * Initialize departments (call when screen loads)
     */
    fun initializeDepartments(departments: List<String>) {
        _availableDepartments.value = departments
    }

    /**
     * Set selected department and move to next step
     */
    fun selectDepartment(department: String, doctorList: List<Doctor>) {
        _selectedDepartment.value = department
        loadDoctorsByDepartment(department, doctorList)
    }

    /**
     * Set selected doctor
     */
    fun selectDoctor(doctor: Doctor) {
        _selectedDoctor.value = doctor
    }

    /**
     * Set selected date
     */
    fun selectDate(date: LocalDate) {
        _selectedDate.value = date
    }

    /**
     * Set selected time slot
     */
    fun selectTimeSlot(timeSlot: TimeSlot) {
        _selectedTimeSlot.value = timeSlot
    }

    fun setPatientName(name: String) {
        _patientName.value = name
    }

    fun setPatientPhone(phone: String) {
        if (phone.all { it.isDigit() } && phone.length <= 10) {
            _patientPhone.value = phone
        }
    }

    fun setPatientAge(age: String) {
        if (age.all { it.isDigit() } && age.length <= 3) {
            _patientAge.value = age
        }
    }

    fun setPatientDOB(dob: String) {
        _patientDOB.value = dob
    }

    fun setPatientGender(gender: String) {
        _patientGender.value = gender
    }

    /**
     * Validate and move to confirmation step
     */
    fun confirmDetails(): Boolean {
        val name = _patientName.value.trim()
        val phone = _patientPhone.value.trim()
        val age = _patientAge.value.trim()
        val gender = _patientGender.value.trim()

        // Validate required fields
        return when {
            name.isEmpty() -> {
                _validationError.value = "Name is required"
                false
            }
            name.length < 2 -> {
                _validationError.value = "Name must be at least 2 characters"
                false
            }
            phone.isEmpty() -> {
                _validationError.value = "Mobile Number is required"
                false
            }
            phone.length < 10 -> {
                _validationError.value = "Mobile Number must be at least 10 digits"
                false
            }
            age.isEmpty() -> {
                _validationError.value = "Age is required"
                false
            }
            !age.all { it.isDigit() } -> {
                _validationError.value = "Age must be a valid number"
                false
            }
            age.toIntOrNull()?.let { it < 1 || it > 120 } == true -> {
                _validationError.value = "Age must be between 1 and 120"
                false
            }
            gender.isEmpty() -> {
                _validationError.value = "Gender is required"
                false
            }
            selectedDate.value == null -> {
                _validationError.value = "Appointment Date is required"
                false
            }
            selectedTimeSlot.value == null -> {
                _validationError.value = "Appointment Time is required"
                false
            }
            selectedDoctor.value == null -> {
                _validationError.value = "Doctor selection is required"
                false
            }
            else -> {
                // All validations passed
                _validationError.value = ""
                generateAppointmentToken()
                _isSubmitted.value = true
                _currentStep.value = 4
                true
            }
        }
    }

    /**
     * ✅ FIXED: Generate unique appointment token with timestamp and sequence
     * Format: AIW-YYYYMMDD-XXXX (where XXXX is random 4 digits)
     */
    private fun generateAppointmentToken() {
        val dateFormat = java.text.SimpleDateFormat("yyyyMMdd", java.util.Locale.US)
        val dateStr = dateFormat.format(java.util.Date())
        val randomNum = (1000..9999).random()
        val prefix = "AIW"
        _appointmentToken.value = "$prefix-$dateStr-$randomNum"
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

    fun goToNextStep() {
        if (_currentStep.value < 4) {
            _currentStep.value += 1
        }
    }

    /**
     * Reset booking process (start over)
     */
    fun resetBooking() {
        _currentStep.value = 1
        _selectedDepartment.value = null
        _selectedDoctor.value = null
        _selectedDate.value = null
        _selectedTimeSlot.value = null
        _patientName.value = ""
        _patientPhone.value = ""
        _patientAge.value = ""
        _patientDOB.value = ""
        _patientGender.value = ""
        _appointmentToken.value = ""
        _voiceInput.value = ""
        _isSubmitted.value = false
        _validationError.value = ""
    }

    /**
     * ✅ FIXED: Edit the appointment (returns to Step 3 Form)
     */
    fun editAppointment() {
        _isSubmitted.value = false
        _validationError.value = ""
        _currentStep.value = 3 // Go back to Patient Details
    }

    /**
     * ✅ FIXED: Cancel appointment completely and reset
     */
    fun cancelAppointment() {
        resetBooking()
    }

    /**
     * ✅ NEW: Get appointment details for display/sharing
     */
    fun getAppointmentDetails(): Map<String, String> {
        return mapOf(
            "token" to (_appointmentToken.value.ifEmpty { "N/A" }),
            "doctor" to (selectedDoctor.value?.name ?: "N/A"),
            "department" to (selectedDoctor.value?.department ?: "N/A"),
            "date" to (selectedDate.value?.toString() ?: "N/A"),
            "time" to (selectedTimeSlot.value?.startTime ?: "N/A"),
            "name" to (patientName.value.ifEmpty { "N/A" }),
            "phone" to (patientPhone.value.ifEmpty { "N/A" }),
            "cabin" to (selectedDoctor.value?.cabin ?: "N/A")
        )
    }

    /**
     * ✅ FIXED: Handle voice input for appointment booking
     * Voice commands based on current step
     */
    fun handleVoiceInput(spokenText: String) {
        _voiceInput.value = spokenText
        val text = spokenText.lowercase()
        
        when (_currentStep.value) {
            1 -> {
                val selectedDoctor = doctorsInDepartment.value.find { 
                    text.contains(it.name.lowercase()) || 
                    text.contains(it.department.lowercase())
                }
                selectedDoctor?.let { selectDoctor(it) }
            }
            2 -> {
                if (selectedDate.value != null && text.contains("confirm")) {
                    goToNextStep()
                }
            }
            3 -> {
                when {
                    text.contains("name") -> {
                        val name = text.substringAfter("name").trim()
                        if (name.isNotEmpty()) setPatientName(name)
                    }
                    text.contains("phone") -> {
                        val phone = text.substringAfter("phone").trim()
                        if (phone.isNotEmpty()) setPatientPhone(phone)
                    }
                    text.contains("male") -> setPatientGender("Male")
                    text.contains("female") -> setPatientGender("Female")
                }
            }
            else -> {}
        }
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
            Age: ${_patientAge.value}
            Gender: ${_patientGender.value}
        """.trimIndent()
    }
}
