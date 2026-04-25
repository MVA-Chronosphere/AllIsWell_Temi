package com.example.alliswelltemi.utils

import com.example.alliswelltemi.data.Doctor
import com.example.alliswelltemi.data.LocationData
import com.example.alliswelltemi.data.HospitalKnowledgeBase
import java.text.SimpleDateFormat
import java.util.*

/**
 * RagContextBuilder - Enhanced RAG (Retrieval Augmented Generation) Context Builder
 * Builds optimized prompts with filtered, relevant context for Ollama LLM
 * Includes language detection, smart follow-ups, and performance optimizations
 */
object RagContextBuilder {

    /**
     * Detect language from user input text
     * Returns "hi" for Hindi, "en" for English
     * Enhanced with logging and better detection
     */
    fun detectLanguage(text: String): String {
        // Check for Devanagari script (Hindi Unicode range: U+0900 to U+097F)
        val hasHindiChars = text.matches(Regex(".*[\\u0900-\\u097F].*"))

        // Additional check for common Hindi words
        val hindiKeywords = listOf(
            "डॉक्टर", "कहां", "कहाँ", "क्या", "कैसे", "मुझे", "बताओ", "बताइए",
            "अस्पताल", "अपॉइंटमेंट", "बुक", "विशेषज्ञ", "चिकित्सक"
        )
        val hasHindiKeywords = hindiKeywords.any { text.contains(it) }

        val detectedLanguage = if (hasHindiChars || hasHindiKeywords) "hi" else "en"

        android.util.Log.d("RagContextBuilder", "========== LANGUAGE DETECTION ==========")
        android.util.Log.d("RagContextBuilder", "Input text: '$text'")
        android.util.Log.d("RagContextBuilder", "Has Hindi chars: $hasHindiChars")
        android.util.Log.d("RagContextBuilder", "Has Hindi keywords: $hasHindiKeywords")
        android.util.Log.d("RagContextBuilder", "Detected language: $detectedLanguage")
        android.util.Log.d("RagContextBuilder", "========================================")

        return detectedLanguage
    }

    /**
     * Medical terminology translation guide for Hindi responses
     * Provides common English medical terms and their Hindi equivalents
     */
    private fun getMedicalTerminologyGuide(): String {
        return """
        Medical Terms Translation (USE EXACT SPECIALTY FROM CONTEXT):
        Cardiology = हृदय रोग विशेषज्ञ/कार्डियोलॉजी
        Plastic Surgery = प्लास्टिक सर्जन/प्लास्टिक सर्जरी
        Cosmetic Surgery = कॉस्मेटिक सर्जन/सौंदर्य शल्य चिकित्सा
        Neurology = मस्तिष्क रोग विशेषज्ञ/न्यूरोलॉजी
        Orthopedics = हड्डी रोग विशेषज्ञ/ऑर्थोपेडिक्स
        Dermatology = त्वचा रोग विशेषज्ञ/डर्मेटोलॉजी
        Pediatrics = बाल रोग विशेषज्ञ/पीडियाट्रिक्स
        Ophthalmology = नेत्र रोग विशेषज्ञ/ऑप्थेल्मोलॉजी
        General Surgery = सामान्य शल्य चिकित्सा/जनरल सर्जरी
        Psychiatry = मनोचिकित्सा/मनोरोग विशेषज्ञ
        Gynecology = स्त्री रोग विशेषज्ञ/गायनेकोलॉजी
        ENT = कान, नाक, गला विशेषज्ञ
        Pulmonology = फेफड़े के रोग विशेषज्ञ
        Urology = मूत्र रोग विशेषज्ञ
        Gastroenterology = पाचन तंत्र रोग विशेषज्ञ
        Oncology = कैंसर विशेषज्ञ
        Cabin = केबिन
        Department = विभाग
        Specialization = विशेषज्ञता
        Consultant = सलाहकार
        Surgeon = सर्जन/शल्य चिकित्सक
        """.trimIndent()
    }

    /**
     * Build comprehensive RAG context with hospital data
     * For general doctor queries, include ALL doctors
     * For specific queries, filter to relevant doctors
     */
    fun buildContext(query: String, doctors: List<Doctor>): String {
        val language = detectLanguage(query)
        val lowerQuery = query.lowercase()

        android.util.Log.d("RagContextBuilder", "Building context for query: '$query' with ${doctors.size} total doctors")

        // Check if this is a general "tell me doctors" type query
        val isGeneralDoctorQuery = isGeneralDoctorQuery(lowerQuery)
        val isFollowUp = isFollowUpQuery(lowerQuery)

        // For general queries, include ALL doctors. For specific queries, filter to relevant ones
        val relevantDoctors = if (isGeneralDoctorQuery) {
            // General "tell me doctors" query - include ALL
            doctors
        } else {
            // Specific query - filter to relevant doctors
            val filtered = doctors.filter { doctor ->
                val doctorName = doctor.name.lowercase().replace("dr.", "").replace("dr ", "").trim()
                val department = doctor.department.lowercase()
                val specialty = doctor.specialization?.lowercase() ?: ""

                lowerQuery.contains(doctorName) ||
                lowerQuery.contains(department) ||
                lowerQuery.contains(specialty)
            }
            
            if (filtered.isEmpty() && (lowerQuery.contains("doctor") || lowerQuery.contains("specialist"))) {
                listOf() // Removed fallback to all doctors to prevent hallucinations
            } else {
                filtered.take(10) // Increase limit from 5 to 10 for better context
            }
        }

        android.util.Log.d("RagContextBuilder", "Found ${relevantDoctors.size} relevant doctors for query (general=$isGeneralDoctorQuery, followUp=$isFollowUp)")
        relevantDoctors.forEach { doctor ->
            android.util.Log.d("RagContextBuilder", "  - ${doctor.name} (${doctor.department})")
        }

        // Build doctor context with COMPLETE details - SPECIALIZATION-FOCUSED
        val doctorContext = if (relevantDoctors.isNotEmpty()) {
            relevantDoctors.joinToString("\n") { doctor ->
                val name = if (doctor.name.startsWith("Dr", ignoreCase = true)) doctor.name else "Dr. ${doctor.name}"
                buildString {
                    append("$name | ")

                    // CRITICAL: Use specialization as PRIMARY field if available
                    if (doctor.specialization.isNotBlank() && !doctor.specialization.equals(doctor.department, ignoreCase = true)) {
                        append("SPECIALTY: ${doctor.specialization}")
                        append(" | Department: ${doctor.department}")
                    } else {
                        append("SPECIALTY: ${doctor.department}")
                    }

                    append(" | Cabin: ${doctor.cabin}")
                    if (doctor.aboutBio.isNotBlank()) append(" | Bio: ${doctor.aboutBio.take(100)}")
                }
            }
        } else if (isFollowUp) {
            "Relevant doctor mentioned in previous turn."
        } else {
            "Requested doctor or department not found in database."
        }

        // Build location context (popular locations only)
        val locationContext = LocationData.ALL_LOCATIONS
            .filter { it.isPopular }
            .take(3)
            .joinToString(", ") { it.name }

        // Hospital info (keep brief)
        val today = SimpleDateFormat("EEEE, MMMM dd, yyyy", Locale.getDefault()).format(Date())
        val hospitalInfo = "All Is Well Hospital | 9AM-5PM | Emergency: 24/7 | $today"

        return """
        Hospital: $hospitalInfo
        Popular Locations: $locationContext
        Doctors: $doctorContext
        """.trimIndent()
    }

    /**
     * Build context with ALL doctors (for comprehensive doctor list queries)
     */
    fun buildContextWithAllDoctors(query: String, doctors: List<Doctor>): String {
        android.util.Log.d("RagContextBuilder", "Building context with ALL ${doctors.size} doctors for query: '$query'")

        // Build doctor context with ALL doctors and COMPLETE details - SPECIALIZATION-FOCUSED
        val doctorContext = if (doctors.isNotEmpty()) {
            doctors.joinToString("\n") { doctor ->
                val name = if (doctor.name.startsWith("Dr", ignoreCase = true))
                    doctor.name else "Dr. ${doctor.name}"
                buildString {
                    append("$name | ")

                    // CRITICAL: Use specialization as PRIMARY field if available
                    if (doctor.specialization.isNotBlank() && !doctor.specialization.equals(doctor.department, ignoreCase = true)) {
                        append("SPECIALTY: ${doctor.specialization}")
                        append(" | Department: ${doctor.department}")
                    } else {
                        append("SPECIALTY: ${doctor.department}")
                    }

                    append(" | Cabin: ${doctor.cabin}")
                    if (doctor.aboutBio.isNotBlank()) append(" | Bio: ${doctor.aboutBio.take(100)}")
                }
            }
        } else {
            "No doctors available"
        }

        // Build location context
        val locationContext = LocationData.ALL_LOCATIONS
            .filter { it.isPopular }
            .take(3)
            .joinToString(", ") { it.name }

        // Hospital info
        val today = SimpleDateFormat("EEEE, MMMM dd, yyyy", Locale.getDefault()).format(Date())
        val hospitalInfo = "All Is Well Hospital | 9AM-5PM | Emergency: 24/7 | $today"

        return """
        Hospital: $hospitalInfo
        Popular Locations: $locationContext
        Doctors (Total: ${doctors.size}): $doctorContext
        """.trimIndent()
    }

    /**
     * Helper to detect if query is asking for a list of doctors
     * Refined to avoid treating specialty queries as general lists
     */
    private fun isGeneralDoctorQuery(query: String): Boolean {
        val q = query.lowercase()
        
        // If it's a follow-up query, it's NOT a general query
        if (isFollowUpQuery(q)) return false
        
        // If it contains a specific department or doctor name, it's NOT a general query
        val specificSpecialties = listOf("eye", "ophthalmologist", "heart", "cardiologist", "dental", "dentist", "skin", "dermatologist", "bone", "orthopedic")
        if (specificSpecialties.any { q.contains(it) }) return false
        
        return (q.contains("list") || q.contains("show") || q.contains("tell") || q.contains("who are") || q.contains("available")) && 
               (q.contains("doctor") || q.contains("specialist") || q.contains("physician"))
    }

    /**
     * Helper to detect if query is a follow-up to previous conversation
     */
    private fun isFollowUpQuery(query: String): Boolean {
        val q = query.lowercase()
        return q.contains("repeat") || q.contains("that") || q.contains("him") || q.contains("her") || 
               q.contains("his") || q.contains("hers") || q.contains("they") || q.contains("them") ||
               q.contains("previous") || q.contains("last") || q.contains("again") || q.contains("who is he") ||
               q.contains("who is she")
    }

    /**
     * Detect user intent from query
     * Returns the medical department/specialty they're asking about
     */
    private fun extractDepartmentIntent(query: String): String? {
        val q = query.lowercase()

        // Medical specialty mappings (common terms → medical department)
        val specialtyMappings = mapOf(
            // Cardiology
            "cardio" to "cardio",
            "heart" to "cardio",
            "cardiovascular" to "cardio",

            // Neurology
            "neuro" to "neuro",
            "brain" to "neuro",
            "nervous" to "neuro",

            // Orthopedics
            "ortho" to "ortho",
            "bone" to "ortho",
            "joint" to "ortho",
            "fracture" to "ortho",

            // Ophthalmology
            "ophthal" to "ophthal",
            "eye" to "ophthal",
            "vision" to "ophthal",

            // Dermatology
            "dermat" to "dermat",
            "skin" to "dermat",

            // Pediatrics
            "pediatr" to "pediatr",
            "child" to "pediatr",
            "kids" to "pediatr",

            // Gynecology
            "gynec" to "gynec",
            "women" to "gynec",
            "pregnancy" to "gynec",
            "maternity" to "gynec",

            // Psychiatry
            "psychiat" to "psychiat",
            "mental" to "psychiat",

            // Physiotherapy
            "physio" to "physio",
            "therapy" to "physio",
            "rehab" to "physio",

            // General/Internal Medicine
            "general" to "general",
            "internal" to "general",
            "physician" to "general",

            // Pulmonology
            "pulmo" to "pulmo",
            "lung" to "pulmo",
            "respiratory" to "pulmo",

            // Anesthesia
            "anesth" to "anesth",
            "anesthesia" to "anesth"
        )

        // Check for matches
        for ((term, stem) in specialtyMappings) {
            if (q.contains(term)) {
                android.util.Log.d("RagContextBuilder", "🎯 Intent detected: '$term' → stem='$stem'")
                return stem
            }
        }

        return null
    }

    /**
     * Build optimized Ollama prompt with INTENT-BASED query understanding
     * PERFORMANCE OPTIMIZED: Shorter prompts = faster generation
     * For "tell me doctors" queries, includes ALL doctors
     * For department queries (e.g., "cardiology office"), finds ALL doctors in that specialty
     * For other queries, uses filtered context
     * Now includes conversation history for context-aware responses
     */
    fun buildOllamaPrompt(query: String, doctors: List<Doctor>, historyContext: String = ""): String {
        val language = detectLanguage(query)
        val lowerQuery = query.lowercase()

        // Check if this is a general doctor list query
        val isGeneralDoctorQuery = isGeneralDoctorQuery(query)

        // Check if query is about symptoms/health issues (needs general doctor guidance)
        val isHealthQuery = lowerQuery.matches(Regex(".*(pain|ache|fever|cough|cold|sick|ill|feel|hurt|problem|issue|symptom|stomach|head|eye|heart|skin|bone).*"))

        // INTENT DETECTION: Extract department/specialty intent
        val departmentIntent = extractDepartmentIntent(query)

        // Check if this is a doctor-specific query (asking about a specific doctor or department)
        val isDoctorSpecificQuery = lowerQuery.contains("doctor") || lowerQuery.contains("dr.") ||
                                     lowerQuery.contains("specialist") || lowerQuery.contains("who is") ||
                                     lowerQuery.contains("department") || lowerQuery.contains("office") ||
                                     departmentIntent != null

        // SMART RETRIEVAL: Get relevant Q&As from knowledge base
        // For doctor queries, increase limit to capture more dynamic doctor Q&As
        val kbSearchLimit = if (isDoctorSpecificQuery) 5 else 2
        val relevantQAs = HospitalKnowledgeBase.search(query, limit = kbSearchLimit)

        // Log knowledge base results for debugging
        android.util.Log.d("RagContextBuilder", "KB search for '$query' returned ${relevantQAs.size} results (limit=$kbSearchLimit)")
        relevantQAs.forEach { qa ->
            android.util.Log.d("RagContextBuilder", "  - KB Match: ${qa.question} (category=${qa.category}, id=${qa.id})")
        }

        val knowledgeBaseContext = if (relevantQAs.isNotEmpty()) {
            relevantQAs.joinToString("; ") { qa -> "${qa.question}: ${qa.answer}" }
        } else {
            ""
        }

        // SMART DOCTOR SELECTION WITH INTENT UNDERSTANDING
        val hasDynamicDocMatches = relevantQAs.any { it.id.startsWith("dynamic_doc_") }

        // Even if KB has dynamic doctor Q&As, we still fetch the doctor objects for structured context
        val relevantDoctors = when {
            // General doctor query - show sample doctors
            isGeneralDoctorQuery -> {
                android.util.Log.d("RagContextBuilder", "🎯 Intent: General doctor list")
                doctors.take(5)
            }

            // INTENT-BASED DEPARTMENT QUERY (e.g., "cardiology office", "heart doctors")
            departmentIntent != null -> {
                val matchedDoctors = doctors.filter { doctor ->
                    val department = doctor.department.lowercase()
                    val specialization = doctor.specialization?.lowercase() ?: ""

                    // Match against the intent stem
                    department.contains(departmentIntent) || specialization.contains(departmentIntent)
                }

                android.util.Log.d("RagContextBuilder", "🎯 Intent: Department query for '$departmentIntent' → found ${matchedDoctors.size} doctors")
                matchedDoctors.forEach { doctor ->
                    android.util.Log.d("RagContextBuilder", "  ✓ Matched: ${doctor.name} (${doctor.department})")
                }

                // Return ALL matching doctors (up to 10 for comprehensive list)
                matchedDoctors.take(10)
            }

            // Health symptom query - show general practitioners + relevant specialists
            isHealthQuery -> {
                android.util.Log.d("RagContextBuilder", "🎯 Intent: Health symptom query")
                val generalDocs = doctors.filter {
                    it.department.lowercase().contains("general") ||
                    it.department.lowercase().contains("internal") ||
                    it.department.lowercase().contains("physician")
                }.take(2)

                // If no general docs, take first available doctors
                if (generalDocs.isEmpty()) doctors.take(3) else generalDocs
            }

            // Specific doctor NAME query
            else -> {
                android.util.Log.d("RagContextBuilder", "🎯 Intent: Specific doctor name query")
                // Extract key medical terms from query (remove common words)
                val queryWords = lowerQuery.split(" ")
                    .filter { it.length > 3 && !listOf("doctor", "specialist", "who", "are", "the", "tell", "about", "show", "find", "office").contains(it) }

                val matchedDoctors = doctors.filter { doctor ->
                    val doctorName = doctor.name.lowercase().replace("dr.", "").replace("dr ", "").trim()
                    val department = doctor.department.lowercase()
                    val specialization = doctor.specialization?.lowercase() ?: ""
                    
                    // Name exact/substring match
                    val nameMatch = lowerQuery.contains(doctorName) || doctorName.contains(lowerQuery)

                    // Department/specialization STEM matching
                    val deptMatch = queryWords.any { word ->
                        val wordStem = word.take(5)
                        department.contains(wordStem) ||
                        specialization.contains(wordStem) ||
                        department.contains(word) ||
                        specialization.contains(word)
                    }

                    nameMatch || deptMatch
                }.sortedBy { doctor ->
                    // Prioritize name matches over department matches
                    val name = doctor.name.lowercase()
                    if (lowerQuery.contains(name) || name.contains(lowerQuery)) 0 else 1
                }

                android.util.Log.d("RagContextBuilder", "  → Found ${matchedDoctors.size} doctors by name/dept matching")
                matchedDoctors.take(5)
            }
        }

        // Build doctor context with COMPLETE details for accurate LLM responses - SPECIALIZATION-FOCUSED
        // Format: Name | PRIMARY_FIELD | Secondary_Field | Cabin | Bio
        val doctorContext = if (relevantDoctors.isNotEmpty()) {
            relevantDoctors.joinToString("\n") { doctor ->
                val name = if (doctor.name.startsWith("Dr", ignoreCase = true)) doctor.name else "Dr. ${doctor.name}"
                buildString {
                    append("$name | ")

                    // CRITICAL: Use specialization as PRIMARY field if available, otherwise use department
                    if (doctor.specialization.isNotBlank() && !doctor.specialization.equals(doctor.department, ignoreCase = true)) {
                        append("SPECIALTY: ${doctor.specialization}")
                        append(" | Department: ${doctor.department}")
                    } else {
                        append("SPECIALTY: ${doctor.department}")
                    }

                    append(" | Cabin: ${doctor.cabin}")
                    append(" | Experience: ${doctor.yearsOfExperience} years")
                    if (doctor.aboutBio.isNotBlank()) append(" | Bio: ${doctor.aboutBio.take(100)}")
                }
            }
        } else if (hasDynamicDocMatches) {
            // This case is unlikely now that we always filter, but kept for safety
            "Refer to Info section for matching doctor details."
        } else {
            "Requested doctor or specialist not found in the list."
        }

        // Hospital info for context
        val hospitalInfo = "All Is Well Hospital. OPD, Pharmacy, ICU, Pathology Lab, Billing Counter available."

        // PERFORMANCE: Simplified prompt - removed verbose instructions
        val systemPrompt = if (language == "hi") {
            """आप 'ऑल इज़ वेल हॉस्पिटल' के एक अत्यंत मददगार, खुशदिल, और सम्मानजनक सहायक हैं। हिंदी में जवाब दें। आपकी भाषा सरल, स्पष्ट और आदरपूर्ण होनी चाहिए। अपनी बात संक्षिप्त (1-2 वाक्य) रखें। हमेशा रोगियों का सम्मान करें और उन्हें 'जी' या 'आप' कहकर संबोधित करें। सवाल का सीधा जवाब दें। कोई अतिरिक्त प्रश्न या सहायता की पेशकश न करें।

बेहद महत्वपूर्ण - डॉक्टर जानकारी के नियम:
1. नीचे "Doctors:" सेक्शन में दी गई जानकारी को बिल्कुल वैसे ही इस्तेमाल करें जैसे लिखी है।
2. डॉक्टर का नाम अंग्रेजी में बोलें (उदाहरण: "Dr. Abhishek Sharma")।
3. "SPECIALTY:" के बाद जो भी लिखा है, वही उस डॉक्टर की विशेषज्ञता है। इसे हिंदी में अनुवाद करें।
4. अगर "Consultant Cosmetic & Plastic Surgeon" लिखा है, तो "प्लास्टिक सर्जन" या "कॉस्मेटिक सर्जन" कहें। "हृदय रोग विशेषज्ञ" मत कहें!
5. केबिन नंबर वैसे ही बताएं जैसे दिया गया है।
6. किसी भी जानकारी को अपने आप मत बनाएं। सिर्फ दी गई जानकारी का इस्तेमाल करें।

डिपार्टमेंट अनुवाद गाइड: Cardiology=हृदय रोग, Plastic Surgery=प्लास्टिक सर्जरी, Neurology=मस्तिष्क रोग, Orthopedics=हड्डी रोग, Dermatology=त्वचा रोग, Pediatrics=बाल रोग, Ophthalmology=नेत्र रोग।"""
        } else {
            """You are an extremely helpful, cheerful, and respectful hospital assistant for 'All Is Well Hospital'. Answer in English with clarity. Be brief (1-2 sentences). Always be polite, welcoming, and professional. Use a warm tone. Answer the question directly without offering additional help or asking follow-up questions.

CRITICAL RULES FOR DOCTOR INFORMATION:
1. Use ONLY the information provided in the "Doctors:" section below. Do NOT make up or infer any information.
2. The "SPECIALTY:" field is the PRIMARY specialization of the doctor. Use this field to describe what the doctor specializes in.
3. If a doctor's SPECIALTY says "Consultant Cosmetic & Plastic Surgeon", they are a PLASTIC SURGEON, NOT a cardiologist or any other specialty.
4. NEVER confuse the Department field with the Specialty field. Always read the SPECIALTY field first.
5. If asked about a department (like "cardiology office" or "heart doctors"), list ALL matching doctors whose SPECIALTY or Department matches the query.
6. State the doctor's name, specialty, and cabin number exactly as provided. Do not invent experiences or details not listed."""
        }

        // OPTIMIZED PROMPT: Compact but informative
        // For Hindi, include medical terminology translation guide
        val terminologyGuide = if (language == "hi") getMedicalTerminologyGuide() else ""

        return """
        $systemPrompt
        ${if (terminologyGuide.isNotEmpty()) "\n$terminologyGuide\n" else ""}
        ${if (historyContext.isNotEmpty()) "Previous: $historyContext" else ""}
        Hospital: $hospitalInfo
        ${if (knowledgeBaseContext.isNotEmpty()) "Info: $knowledgeBaseContext" else ""}
        Doctors: $doctorContext
        Q: $query
        A:""".trimIndent().replace("\n\n", "\n")
    }

    /**
     * Build streaming-optimized prompt (shorter for faster initial response)
     * Cheerful and respectful tone, NO follow-up suggestions
     * Enhanced with medical terminology guide for Hindi
     */
    fun buildStreamingPrompt(query: String, doctors: List<Doctor>): String {
        val language = detectLanguage(query)
        val context = buildContext(query, doctors)

        val langInstruction = if (language == "hi") {
            """आप 'ऑल इज़ वेल हॉस्पिटल' के एक खुशदिल, स्पष्ट और अत्यंत सम्मानजनक सहायक हैं। सवाल का सीधा जवाब दें, कोई अतिरिक्त प्रश्न न करें।
            महत्वपूर्ण: डॉक्टरों के नाम अंग्रेजी में बोलें, लेकिन विभाग (Department) को हिंदी में अनुवाद करें। उदाहरण: "Cardiology" को "हृदय रोग विशेषज्ञ", "Neurology" को "मस्तिष्क रोग विशेषज्ञ" कहें।"""
        } else {
            "You are a cheerful, clear, and highly respectful hospital assistant for 'All Is Well Hospital'. Answer directly without follow-up questions."
        }

        val terminologyGuide = if (language == "hi") getMedicalTerminologyGuide() else ""

        // Even more concise for streaming, no follow-ups
        return """
        $langInstruction
        ${if (terminologyGuide.isNotEmpty()) "\n$terminologyGuide\n" else ""}

        $context

        User: $query

        Answer clearly, respectfully, and warmly in 1-2 sentences. Do not ask follow-up questions or offer additional help.
        """.trimIndent()
    }

    /**
     * Generate fallback response when Ollama fails
     * Context-aware fallbacks based on detected intent
     */
    fun generateFallbackResponse(query: String, doctors: List<Doctor>): String {
        val language = detectLanguage(query)
        val lowerQuery = query.lowercase()

        val response = when {
            // Doctor queries
            lowerQuery.contains("doctor") || lowerQuery.contains("specialist") -> {
                if (doctors.isNotEmpty()) {
                    if (language == "hi") {
                        "मैं आपको डॉक्टर ढूंढने में मदद कर सकता हूं। हमारे पास ${doctors.size} डॉक्टर हैं। डॉक्टर सूची देखें।"
                    } else {
                        "I can help you find doctors. We have ${doctors.size} doctors available. Please check the doctors section."
                    }
                } else {
                    if (language == "hi") "डॉक्टर सूची अभी लोड हो रही है। कृपया पुनः प्रयास करें।" else "Doctor list is loading. Please try again."
                }
            }

            // Navigation queries
            lowerQuery.contains("navigate") || lowerQuery.contains("where") || lowerQuery.contains("go to") -> {
                if (language == "hi") {
                    "मैं आपको नेविगेट करने में मदद कर सकता हूं। हमारे पास फार्मेसी, ICU, पैथोलॉजी लैब, बिलिंग काउंटर और OPD हैं।"
                } else {
                    "I can help you navigate. We have pharmacy, ICU, pathology lab, billing counter, and OPD areas."
                }
            }

            // Booking queries
            lowerQuery.contains("book") || lowerQuery.contains("appointment") -> {
                if (language == "hi") "मैं आपको अपॉइंटमेंट बुक करने में मदद कर सकता हूं। अपॉइंटमेंट सेक्शन देखें।" else "I can help you book an appointment. Please visit the appointment section."
            }

            // Default fallback
            else -> {
                if (language == "hi") {
                    "नमस्ते! मैं ऑल इज़ वेल हॉस्पिटल में आपकी किस प्रकार सहायता कर सकता हूँ? कृपया मुझे बताएं या मुख्य मेनू में विकल्पों को देखें।"
                } else {
                    "Hello! I am here to help you at All Is Well Hospital. How can I assist you today? Please let me know your requirement or explore the main menu."
                }
            }
        }

        return response
    }
}
