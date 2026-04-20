package com.example.alliswelltemi.viewmodel

import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.State
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.alliswelltemi.data.Doctor
import com.example.alliswelltemi.data.TimeSlot
import com.example.alliswelltemi.data.AppointmentRequest
import com.example.alliswelltemi.data.AppointmentData
import com.example.alliswelltemi.network.RetrofitClient
import kotlinx.coroutines.launch
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

    private val _statusResult = mutableStateOf<String?>(null)
    val statusResult: State<String?> = _statusResult

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
        android.util.Log.d("AppointmentViewModel", "Selected doctor: ${doctor.name}, moving to step 2")
        _selectedDoctor.value = doctor
        _currentStep.value = 2
    }

    /**
     * Set selected doctor from voice command (without moving to next step)
     * Allows customization of when to move to next step
     */
    fun setSelectedDoctor(doctor: Doctor) {
        android.util.Log.d("AppointmentViewModel", "Set doctor (voice): ${doctor.name}")
        _selectedDoctor.value = doctor
    }

    /**
     * Set selected date and move to next step
     */
    fun selectDate(date: LocalDate) {
        android.util.Log.d("AppointmentViewModel", "Selected date: $date, moving to step 3")
        _selectedDate.value = date
        _currentStep.value = 3
    }

    /**
     * Set selected time slot and move to next step
     */
    fun selectTimeSlot(timeSlot: TimeSlot) {
        android.util.Log.d("AppointmentViewModel", "Selected time: ${timeSlot.startTime}, moving to step 4")
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
            android.util.Log.w("AppointmentViewModel", "Validation failed: Name or phone is empty")
            return false
        }

        if (phone.length < 10) {
            android.util.Log.w("AppointmentViewModel", "Validation failed: Phone number too short")
            return false
        }

        android.util.Log.d("AppointmentViewModel", "Details confirmed for $name, moving to step 5")
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
        android.util.Log.d("AppointmentViewModel", "Generated token: ${_appointmentToken.value}")
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
        android.util.Log.d("AppointmentViewModel", "Resetting booking process")
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
     * Submit appointment to Strapi backend
     */
    fun submitAppointment(onComplete: (Boolean, String) -> Unit) {
        val doctor = _selectedDoctor.value ?: return
        val date = _selectedDate.value ?: return
        val time = _selectedTimeSlot.value ?: return
        val name = _patientName.value
        val phone = _patientPhone.value
        val token = _appointmentToken.value

        android.util.Log.i("AppointmentViewModel", "Submitting appointment: Doctor=${doctor.name}, Token=$token")

        viewModelScope.launch {
            try {
                _isLoading.value = true
                
                val appointmentRequest = AppointmentRequest(
                    data = AppointmentData(
                        doctorName = doctor.name,
                        department = doctor.department,
                        date = date.toString(),
                        time = time.startTime,
                        patientName = name,
                        patientPhone = phone,
                        token = token
                    )
                )

                val response = RetrofitClient.apiService.createAppointment(appointmentRequest)
                
                if (response.error != null) {
                    android.util.Log.e("AppointmentViewModel", "Submission failed: ${response.error["message"]}")
                    onComplete(false, "API Error: ${response.error["message"]}")
                } else {
                    android.util.Log.i("AppointmentViewModel", "Appointment successfully created in Strapi")
                    onComplete(true, "Appointment confirmed! Your token is $token")
                }
            } catch (e: Exception) {
                android.util.Log.e("AppointmentViewModel", "Error submitting appointment", e)
                onComplete(false, "Error: ${e.message}")
            } finally {
                _isLoading.value = false
            }
        }
    }

    /**
     * Check appointment status by token
     */
    fun checkAppointmentStatus(token: String, onComplete: (Boolean, String) -> Unit) {
        android.util.Log.d("AppointmentViewModel", "Checking status for token: $token")
        viewModelScope.launch {
            try {
                _isLoading.value = true
                _statusResult.value = null
                
                val response = RetrofitClient.apiService.getAppointmentByToken(token)
                
                if (response.error != null) {
                    android.util.Log.e("AppointmentViewModel", "Status check failed: ${response.error["message"]}")
                    onComplete(false, "API Error: ${response.error["message"]}")
                } else {
                    // Strapi returns an array for get requests with filters
                    val list = response.data as? List<Map<String, Any>>
                    if (list != null && list.isNotEmpty()) {
                        val appointment = list[0]
                        // Handle both v4 (attributes) and v5 (direct fields)
                        val fields = (appointment["attributes"] as? Map<String, Any>) ?: appointment
                        
                        val doctor = fields["doctor_name"]?.toString() ?: "Unknown"
                        val date = fields["date"]?.toString() ?: "Unknown"
                        val time = fields["time"]?.toString() ?: "Unknown"
                        val status = fields["status"]?.toString() ?: "pending"
                        
                        val result = "Your appointment with $doctor on $date at $time is $status."
                        android.util.Log.d("AppointmentViewModel", "Status retrieved: $status")
                        _statusResult.value = result
                        onComplete(true, result)
                    } else {
                        android.util.Log.w("AppointmentViewModel", "No appointment found for token: $token")
                        onComplete(false, "No appointment found with token $token")
                    }
                }
            } catch (e: Exception) {
                android.util.Log.e("AppointmentViewModel", "Error checking appointment status", e)
                onComplete(false, "Error: ${e.message}")
            } finally {
                _isLoading.value = false
            }
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
        """.trimIndent()
    }
}

