package com.example.alliswelltemi.data

/**
 * Hospital Knowledge Base - 294 Q&A pairs (COMPLETE VERSION)
 * Each QA pair represents common hospital questions and answers
 * These are retrieved intelligently based on user query
 */
data class KnowledgeBaseQA(
    val id: String,
    val question: String,
    val answer: String,
    val keywords: List<String>,  // For matching user queries
    val category: String,         // hospital_info, departments, appointments, etc.
    val language: String = "en"   // "en" or "hi"
)

object HospitalKnowledgeBase {

    // ==================== ALL 294 Q&A PAIRS ====================
    // Generated from Hospital temi Dataset.json
    // Updated: April 22, 2026
    private val qaDatabase = listOf(
        // ...existing code...
        KnowledgeBaseQA(
            id = "qa_1",
            question = "What is the hospital name ?",
            answer = "All Is Well Hospital is a modern multi-speciality healthcare institution located in Burhanpur, Madhya Pradesh. The founder and chairman of All is Well Hospiital is Mr. anand Prakash Chouksey sir. The hospital was established in 2019 with the vision of delivering high-quality, ethical, and patient-centered medical care to people in and around the region. The mission of All Is Well Hospital is to provide accessible, affordable, and advanced healthcare services with compassion and integrity.",
            keywords = listOf("transparent", "dignity", "hospiital", "outcomes", "dental", "specialists", "ophthalmology", "professionals", "burhanpur", "monitoring"),
            category = "general",
            language = "en"
        ),
        KnowledgeBaseQA(
            id = "qa_1_hi",
            question = "अस्पताल का नाम क्या है?",
            answer = "ऑल इज़ वेल हॉस्पिटल एक आधुनिक मल्टी-स्पेशलिटी स्वास्थ्य संस्थान है, जो बुरहानपुर, मध्य प्रदेश में स्थित है। इसके संस्थापक और चेयरमैन श्री आनंद प्रकाश चौकसे हैं। अस्पताल 2019 में स्थापित किया गया था, जिसका उद्देश्य उच्च गुणवत्ता, नैतिक और रोगी-केंद्रित चिकित्सा सेवा प्रदान करना है। ऑल इज़ वेल हॉस्पिटल का मिशन करुणा और ईमानदारी के साथ सुलभ, किफायती और उन्नत स्वास्थ्य सेवाएं प्रदान करना है।",
            keywords = listOf("अस्पताल", "नाम", "ऑल इज़ वेल", "बुरहानपुर", "चौकसे", "मिशन", "सेवा", "स्वास्थ्य", "मध्य प्रदेश"),
            category = "general",
            language = "hi"
        ),
        KnowledgeBaseQA(
            id = "qa_2_hi",
            question = "क्या आपके पास बीमा सुविधाएं हैं?",
            answer = "अस्पताल विस्तृत श्रेणी के विश्वसनीय स्वास्थ्य बीमा प्रदाताओं के माध्यम से कैशलेस और प्रतिपूर्ति सुविधाएं प्रदान करता है। स्वीकृत बीमा कंपनियों में स्टार हेल्थ इन्शुरेंस, केयर हेल्थ इन्शुरेंस, पैरामाउंट हेल्थ सर्विसेज़, IFFCO-टोकियो जनरल इंश्योरेंस, डिजिट इंश्योरेंस, आदित्य बिड़ला हेल्थ इन्शुरेंस, SBI जनरल इंश्योरेंस शामिल हैं।",
            keywords = listOf("बीमा", "सुविधाएं", "कैशलेस", "प्रतिपूर्ति", "स्वास्थ्य", "कंपनियां"),
            category = "insurance",
            language = "hi"
        ),
        KnowledgeBaseQA(
            id = "qa_3_hi",
            question = "स्वास्थ्य पैकेज कौन से हैं?",
            answer = "ऑल इज़ वेल हॉस्पिटल में विशेष रूप से डिज़ाइन किए गए स्वास्थ्य पैकेज हैं: बेसिक वेलनेस, कम्प्रिहेंसिव वेलनेस, महिला स्वास्थ्य पैकेज, डायबिटिक वेलनेस और बाल स्वास्थ्य पैकेज। प्रत्येक पैकेज में प्रयोगशाला परीक्षण और नैदानिक मूल्यांकन शामिल हैं।",
            keywords = listOf("पैकेज", "स्वास्थ्य", "वेलनेस", "परीक्षण", "महिला", "बाल", "मधुमेह"),
            category = "general",
            language = "hi"
        ),
        KnowledgeBaseQA(
            id = "qa_6_hi",
            question = "क्या वॉक-इन परामर्श की अनुमति है?",
            answer = "ऑल इज़ वेल हॉस्पिटल 24/7 OPD सेवाएं प्रदान करता है। मरीज़ रिसेप्शन पर आकर परामर्श ले सकते हैं। आपातकालीन मामलों में प्राथमिकता दी जाती है। 24 घंटे फार्मेसी, प्रयोगशाला और रेडियोलॉजी विभाग उपलब्ध हैं।",
            keywords = listOf("परामर्श", "OPD", "आपातकालीन", "24/7", "डॉक्टर", "उपलब्ध"),
            category = "departments",
            language = "hi"
        ),
        KnowledgeBaseQA(
            id = "qa_7_hi",
            question = "अस्पताल का फोन नंबर क्या है?",
            answer = "ऑल इज़ वेल हॉस्पिटल से संपर्क करने के लिए आपातकालीन नंबर +91 76977 44444 या +91 70890 66888 पर कॉल करें। ये नंबर अस्पताल के सामने के डेस्क से जुड़े हैं जहां विभाग की जानकारी, अपॉइंटमेंट बुकिंग और आपातकालीन सहायता उपलब्ध है।",
            keywords = listOf("फोन", "नंबर", "संपर्क", "अपॉइंटमेंट", "आपातकालीन"),
            category = "general",
            language = "hi"
        ),
        KnowledgeBaseQA(
            id = "qa_8_hi",
            question = "क्या आप हिंदी में बोल सकते हैं?",
            answer = "हां, मैं हिंदी में बोल सकता हूँ। आप हिंदी में अपने सवाल पूछ सकते हैं और मैं हिंदी में जवाब दूंगा। मेरा लक्ष्य आपके लिए संचार को आसान और सुविधाजनक बनाना है।",
            keywords = listOf("हिंदी", "भाषा", "बोल", "समझ", "सवाल", "जवाब"),
            category = "general",
            language = "hi"
        ),
        KnowledgeBaseQA(
            id = "qa_9_hi",
            question = "क्या आप दोहरा सकते हैं?",
            answer = "जी, मैं आपके लिए जानकारी को स्पष्ट और धीरे-धीरे दोहराऊंगा। यदि आपको कोई विस्तृत जानकारी चाहिए, तो बस पूछें। मैं आपकी सहायता के लिए यहां हूँ।",
            keywords = listOf("दोहराना", "स्पष्ट", "जानकारी", "धीरे", "समझ", "विस्तार"),
            category = "general",
            language = "hi"
        ),
        KnowledgeBaseQA(
            id = "qa_13_hi",
            question = "अस्पताल में कौन सी सुविधाएं उपलब्ध हैं?",
            answer = "ऑल इज़ वेल हॉस्पिटल में उन्नत नैदानिक प्रयोगशालाएं, 24/7 फार्मेसी, कैफेटेरिया, ATM, पेयजल स्टेशन, शौचालय, लिफ्टें, व्हीलचेयर सहायता और समर्पित प्रतीक्षा क्षेत्र हैं। ICU के लिए विशेष समय हैं (आमतौर पर 11 AM-12 PM और 6 PM-7 PM)।",
            keywords = listOf("सुविधाएं", "प्रयोगशाला", "फार्मेसी", "ATM", "शौचालय", "लिफ्ट", "ICU"),
            category = "diagnostics",
            language = "hi"
        ),
        KnowledgeBaseQA(
            id = "qa_51_hi",
            question = "अस्पताल का ईमेल क्या है?",
            answer = "आप अस्पताल को frontdesk@alliswellhospital.com पर ईमेल कर सकते हैं। यह ईमेल पता अस्पताल की प्रशासनिक टीम द्वारा समीक्षा किया जाता है और सामान्य पूछताछ, अपॉइंटमेंट अनुरोध और स्वास्थ्य पैकेज विवरण के लिए उपयोग किया जा सकता है।",
            keywords = listOf("ईमेल", "संपर्क", "frontdesk", "अपॉइंटमेंट", "पूछताछ"),
            category = "general",
            language = "hi"
        ),
        KnowledgeBaseQA(
            id = "qa_57_hi",
            question = "अस्पताल का खुलने का समय क्या है?",
            answer = "सोमवार से शुक्रवार: सुबह 8:00 से शाम 8:00 बजे | शनिवार से रविवार: सुबह 9:00 से शाम 6:00 बजे",
            keywords = listOf("खुलने", "समय", "सोमवार", "शुक्रवार", "शनिवार", "रविवार", "घंटे"),
            category = "hospital_info",
            language = "hi"
        ),
        KnowledgeBaseQA(
            id = "qa_58_hi",
            question = "ऑल इज़ वेल हॉस्पिटल कहां स्थित है?",
            answer = "ऑल इज़ वेल हॉस्पिटल मैक्रो विजन अकादमी के पास, बुरहानपुर, मध्य प्रदेश, 450331 में स्थित है।",
            keywords = listOf("स्थान", "पता", "बुरहानपुर", "मध्य प्रदेश", "मैक्रो", "विजन", "अकादमी"),
            category = "facilities",
            language = "hi"
        ),
        KnowledgeBaseQA(
            id = "qa_2",
            question = "Do you have insurance facilities?",
            answer = "The hospital provides cashless and reimbursement facilities through a wide range of trusted health insurance providers to ensure convenient and hassle-free treatment for patients. The accepted insurance companies include Star Health Insurance, Care Health Insurance, Paramount Health Services, IFFCO-Tokio General Insurance, Digit Insurance, Aditya Birla Health Insurance, SBI General Insurance, ICICI Lombard, FHPL (Family Health Plan Limited), MDIndia Healthcare Services, Niva Bupa Health Insurance.",
            keywords = listOf("provided", "limited", "plan", "according", "available", "doctor", "unless", "specific", "coverage", "eligible"),
            category = "insurance",
            language = "en"
        ),
        KnowledgeBaseQA(
            id = "qa_3",
            question = "What all are the health packages ?",
            answer = "At All Is Well Hospital, we offer a variety of specially designed health check-up packages to meet the preventive and diagnostic healthcare needs of adults, women, children, and patients managing chronic conditions such as diabetes. Currently, we provide five major health packages: Basic Wellness, Comprehensive Wellness, Comprehensive Women Wellness, Diabetic Wellness, and Healthy Child Wellness. Each package includes a carefully selected combination of laboratory tests and diagnostic evaluations.",
            keywords = listOf("metabolic", "8540", "performance", "focuses", "tsh", "ecg", "lipid", "each", "available", "consultation"),
            category = "general",
            language = "en"
        ),
        KnowledgeBaseQA(
            id = "qa_4",
            question = "What are types of yoga treatment available ?",
            answer = "At All Is Well Hospital, a range of structured yoga treatments is available to support physical health, mental well-being, and overall lifestyle balance. The hospital offers multiple therapeutic yoga approaches tailored to different health needs. These include Hatha Yoga, Vinyasa Flow, Yin Yoga, Pranayama, Meditation and Mindfulness, and Yoga Therapy, each designed with specific benefits and therapeutic goals.",
            keywords = listOf("fascia", "ligaments", "focuses", "supports", "stamina", "fluid", "each", "yin", "particularly", "available"),
            category = "diagnostics",
            language = "en"
        ),
        KnowledgeBaseQA(
            id = "qa_5",
            question = "Treatments Available in Nutrition and diet ?",
            answer = "The Treatments Available in Nutrition and Diet focus on improving overall health, preventing diseases, and supporting specific medical conditions through scientifically designed dietary strategies. Personalized Meal Planning involves creating custom nutrition plans tailored to an individual's health goals, medical history, lifestyle, and dietary preferences. This approach ensures balanced nutrient intake while addressing concerns such as weight control, nutrient deficiencies, food allergies.",
            keywords = listOf("strategies", "gain", "metabolic", "performance", "holistic", "focuses", "qualified", "loss", "available", "specific"),
            category = "diagnostics",
            language = "en"
        ),
        KnowledgeBaseQA(
            id = "qa_6",
            question = "Is walk in consultation allowed ?",
            answer = "All Is Well Hospital provides 24×7 OPD services along with emergency care, and patients can generally walk in for consultations on a first-come, first-served basis at the reception, with priority sometimes given to emergency cases or senior citizens. Specific walk-in policies will depend on doctor availability, but urgent care is always accessible any time due to the 24-hour OPD and emergency setup. The in-house Pharmacy, the labs and radiology department at All Is Well Hospital operates 24×7.",
            keywords = listOf("availability", "consultation", "available", "doctor", "urgent", "specific", "online", "firstserved", "room", "reports"),
            category = "departments",
            language = "en"
        ),
        KnowledgeBaseQA(
            id = "qa_7",
            question = "What is the hospital's phone number?",
            answer = "You can contact All Is Well Hospital directly by calling on the emergency number +91 76977 44444 or +91 70890 66888. These numbers connect you to the hospital's front desk and support team, where staff members can assist you with appointment bookings, department inquiries, emergency coordination, and general hospital information. The phone lines are active to ensure patients and visitors receive timely assistance whenever needed.",
            keywords = listOf("query", "feedback", "bookings", "quicker", "available", "specific", "package", "without", "ensure", "health", "phone number"),
            category = "general",
            language = "en"
        ),
        KnowledgeBaseQA(
            id = "qa_8",
            question = "Can you speak in Hindi?",
            answer = "Yes, I can speak in Hindi. You can ask your questions in Hindi, and I will respond in Hindi to make it easier for you. My goal is to make communication simple and comfortable for you during your visit to the hospital.",
            keywords = listOf("questions", "will", "hospital", "easier", "during", "yes", "hindi", "goal", "respond", "simple"),
            category = "general",
            language = "en"
        ),
        KnowledgeBaseQA(
            id = "qa_9",
            question = "Can you repeat?",
            answer = "Of course. I will repeat the information for you clearly and slowly. Please listen carefully, and if you need me to repeat it again or explain any specific part in more detail, feel free to ask. I am here to assist you.",
            keywords = listOf("specific", "information", "any", "detail", "slowly", "here", "please", "clearly", "repeat", "feel"),
            category = "general",
            language = "en"
        ),
        KnowledgeBaseQA(
            id = "qa_10",
            question = "Can you repeat the direction ?",
            answer = "Certainly. I will repeat the directions step by step so you can follow them easily. Please proceed straight from your current location, then turn as instructed. If you would like, I can also guide you by moving with you to the destination. Let me know how you would prefer to proceed.",
            keywords = listOf("moving", "directions", "direction", "proceed", "them", "step", "from", "current", "know", "prefer"),
            category = "facilities",
            language = "en"
        ),
        KnowledgeBaseQA(
            id = "qa_11",
            question = "Can you show the map ?",
            answer = "yes. Yes, I can display the hospital map on my screen for you. The map will show your current location and the route to your destination inside the hospital. You can follow the highlighted path, or I can guide you personally to the location. Please confirm where you would like to go.",
            keywords = listOf("personally", "confirm", "current", "please", "location", "path", "destination", "where", "highlighted", "hospital"),
            category = "general",
            language = "en"
        ),
        KnowledgeBaseQA(
            id = "qa_12",
            question = "can you explain again ?",
            answer = "Yes, let me explain you again in a simple language. I am here to ensure you receive complete and accurate information.",
            keywords = listOf("let", "language", "accurate", "receive", "yes", "explain", "here", "simple", "ensure", "complete"),
            category = "general",
            language = "en"
        ),
        KnowledgeBaseQA(
            id = "qa_13",
            question = "What facilities are available at the hospital?",
            answer = "All Is Well Hospital provides comprehensive facilities including advanced diagnostic labs, 24/7 pharmacy, cafeteria, ATM, drinking water stations, restrooms, elevators, wheelchair assistance, and dedicated waiting areas for visitors . Visiting hours for general wards are typically not defined one family member/visitor can visit the patient with a pass that is given to the patient. ICU visiting hours are more restricted (commonly 11 AM-12 PM and 6 PM-7 PM) to ensure patient rest and infection control.",
            keywords = listOf("provided", "xray", "additional", "availability", "12000day", "discharge", "each", "available", "consultation", "doctor"),
            category = "diagnostics",
            language = "en"
        ),
        KnowledgeBaseQA(
            id = "qa_14",
            question = "How much the radiology and imaging costs ?",
            answer = "At All Is Well Hospital, the Radiology and Imaging Department offers a wide range of advanced diagnostic imaging services. The exact cost of each test depends on the type of scan, body part examined, contrast usage, and clinical requirements. Patients are advised to contact the billing counter for updated pricing. The available radiology and imaging services include X-Ray for examining bones, chest, lungs, fractures, infections, and joint conditions.",
            keywords = listOf("safe", "internal", "xray", "performance", "nursing", "perday", "105", "tsh", "each", "available"),
            category = "general",
            language = "en"
        ),
        KnowledgeBaseQA(
            id = "qa_15",
            question = "Is cash accepted ?",
            answer = "Yes, the hospital accepts cash payments for consultations, diagnostic tests, room charges, and other medical services. In addition to cash, digital payment options may also be available for patient convenience. For large bills such as admissions or surgeries, patients can confirm accepted payment methods at the billing counter.",
            keywords = listOf("availability", "discharge", "station", "doctor", "visits", "ensure", "suites", "gear", "preauthorization", "instructions"),
            category = "general",
            language = "en"
        ),
        KnowledgeBaseQA(
            id = "qa_16",
            question = "Name of Hospital",
            answer = "The Name of the Hospital is All is Well Hospital. I'm here to assist you, Welcome to All Is Well Hospital. I'm your digital assistant—ready to help with directions, appointments, or information.",
            keywords = listOf("help", "hospital", "well", "appointments", "name", "here", "assistantready", "welcome", "all", "directions"),
            category = "general",
            language = "en"
        ),
        KnowledgeBaseQA(
            id = "qa_17",
            question = "Who is the Director?",
            answer = "kabir chouksey sir and Devanshi Chouksey ma'am ma'am ma'am ma'am ma'am ma'am ma'am ma'am is the Director of the hospital.",
            keywords = listOf("hospital", "director", "devanshi", "chouksey", "who", "kabir"),
            category = "general",
            language = "en"
        ),
        KnowledgeBaseQA(
            id = "qa_18",
            question = "Who is kabir chouksey sir?",
            answer = "kabir chouksey sir is the Director of the hospital.",
            keywords = listOf("hospital", "director", "chouksey", "who", "kabir"),
            category = "general",
            language = "en"
        ),
        KnowledgeBaseQA(
            id = "qa_19",
            question = "Who is Devanshi Chouksey ma'am ma'am ma'am ma'am ma'am ma'am ma'am ma'am?",
            answer = "Devanshi Chouksey ma'am ma'am ma'am ma'am ma'am ma'am ma'am ma'am is the Director of the hospital.",
            keywords = listOf("hospital", "director", "devanshi", "chouksey", "who"),
            category = "general",
            language = "en"
        ),
        // ==================== HINDI Q&A PAIRS FOR LEADERSHIP (NEW) ====================
        KnowledgeBaseQA(
            id = "qa_17_hi",
            question = "निदेशक कौन हैं?",
            answer = "कबीर चौकसे सर और देवांशी चौकसे मैडम अस्पताल के निदेशक हैं।",
            keywords = listOf("निदेशक", "कबीर", "देवांशी", "चौकसे", "कौन", "अस्पताल", "सर", "मैडम"),
            category = "general",
            language = "hi"
        ),
        KnowledgeBaseQA(
            id = "qa_18_hi",
            question = "कबीर चौकसे सर कौन हैं?",
            answer = "कबीर चौकसे सर अस्पताल के निदेशक हैं।",
            keywords = listOf("कबीर", "चौकसे", "निदेशक", "अस्पताल", "कौन", "सर"),
            category = "general",
            language = "hi"
        ),
        KnowledgeBaseQA(
            id = "qa_19_hi",
            question = "देवांशी चौकसे मैडम कौन हैं?",
            answer = "देवांशी चौकसे मैडम अस्पताल की निदेशक हैं।",
            keywords = listOf("देवांशी", "चौकसे", "निदेशक", "अस्पताल", "कौन", "मैडम"),
            category = "general",
            language = "hi"
        ),
        KnowledgeBaseQA(
            id = "qa_founder_hi",
            question = "संस्थापक कौन हैं?",
            answer = "आनंद प्रकाश चौकसे सर ऑल इज़ वेल हॉस्पिटल के संस्थापक हैं।",
            keywords = listOf("संस्थापक", "आनंद", "चौकसे", "प्रकाश", "हॉस्पिटल", "सर"),
            category = "general",
            language = "hi"
        ),
        KnowledgeBaseQA(
            id = "qa_chairman_hi",
            question = "चेयरमैन कौन हैं?",
            answer = "आनंद प्रकाश चौकसे सर ऑल इज़ वेल हॉस्पिटल के चेयरमैन हैं।",
            keywords = listOf("चेयरमैन", "आनंद", "चौकसे", "प्रकाश", "हॉस्पिटल", "सर"),
            category = "general",
            language = "hi"
        ),
        KnowledgeBaseQA(
            id = "qa_87_hi",
            question = "आनंद प्रकाश चौकसे सर कौन हैं?",
            answer = "आनंद प्रकाश चौकसे सर ऑल इज़ वेल हॉस्पिटल के संस्थापक और चेयरमैन हैं।",
            keywords = listOf("आनंद", "प्रकाश", "चौकसे", "संस्थापक", "चेयरमैन", "कौन", "सर"),
            category = "general",
            language = "hi"
        ),
        // ==================== HINGLISH Q&A PAIRS (ENGLISH WORDS WITH HINDI) ====================
        // These are the MOST PRACTICAL - people speak Hinglish in hospitals!
        KnowledgeBaseQA(
            id = "qa_directors_hinglish",
            question = "Directors कौन हैं?",
            answer = "kabir chouksey sir और Devanshi Chouksey ma'am ma'am ma'am ma'am ma'am ma'am ma'am ma'am hospital के directors हैं।",
            keywords = listOf("directors", "कौन", "हैं", "कबीर", "देवांशी", "चौकसे", "निदेशक"),
            category = "general",
            language = "hi"
        ),
        KnowledgeBaseQA(
            id = "qa_who_are_directors_hinglish",
            question = "Hospital के directors कौन हैं?",
            answer = "kabir chouksey sir और Devanshi Chouksey ma'am ma'am ma'am ma'am ma'am ma'am ma'am ma'am ऑल इज़ वेल हॉस्पिटल के directors हैं।",
            keywords = listOf("directors", "hospital", "कौन", "के", "हैं", "कबीर", "देवांशी", "चौकसे"),
            category = "general",
            language = "hi"
        ),
        KnowledgeBaseQA(
            id = "qa_founder_hinglish",
            question = "Founder कौन है?",
            answer = "anand Prakash Chouksey sir ऑल इज़ वेल हॉस्पिटल के founder हैं।",
            keywords = listOf("founder", "कौन", "है", "आनंद", "चौकसे", "प्रकाश", "संस्थापक"),
            category = "general",
            language = "hi"
        ),
        KnowledgeBaseQA(
            id = "qa_who_is_founder_hinglish",
            question = "Hospital का founder कौन है?",
            answer = "anand Prakash Chouksey sir ऑल इज़ वेल हॉस्पिटल का founder हैं।",
            keywords = listOf("founder", "hospital", "कौन", "का", "है", "आनंद", "चौकसे", "प्रकाश"),
            category = "general",
            language = "hi"
        ),
        KnowledgeBaseQA(
            id = "qa_chairman_hinglish",
            question = "Chairman कौन है?",
            answer = "anand Prakash Chouksey sir ऑल इज़ वेल हॉस्पिटल के chairman हैं।",
            keywords = listOf("chairman", "कौन", "है", "आनंद", "चौकसे", "प्रकाश", "चेयरमैन"),
            category = "general",
            language = "hi"
        ),
        KnowledgeBaseQA(
            id = "qa_who_is_chairman_hinglish",
            question = "Hospital का chairman कौन है?",
            answer = "anand Prakash Chouksey sir ऑल इज़ वेल हॉस्पिटल का chairman हैं।",
            keywords = listOf("chairman", "hospital", "कौन", "का", "है", "आनंद", "चौकसे", "प्रकाश"),
            category = "general",
            language = "hi"
        ),
        KnowledgeBaseQA(
            id = "qa_kabir_hinglish",
            question = "Kabir Chouksey सर कौन है?",
            answer = "Kabir Chouksey सर ऑल इज़ वेल हॉस्पिटल के director हैं।",
            keywords = listOf("kabir", "chouksey", "कौन", "है", "director", "निदेशक", "कबीर", "सर", "sir"),
            category = "general",
            language = "hi"
        ),
        KnowledgeBaseQA(
            id = "qa_devanshi_hinglish",
            question = "Devanshi Chouksey मैडम कौन है?",
            answer = "Devanshi Chouksey मैडम ऑल इज़ वेल हॉस्पिटल की director हैं।",
            keywords = listOf("devanshi", "chouksey", "कौन", "है", "director", "निदेशक", "देवांशी", "मैडम", "madam"),
            category = "general",
            language = "hi"
        ),
        KnowledgeBaseQA(
            id = "qa_anand_hinglish",
            question = "Anand Prakash Chouksey सर कौन है?",
            answer = "Anand Prakash Chouksey सर ऑल इज़ वेल हॉस्पिटल के founder और chairman हैं।",
            keywords = listOf("anand", "prakash", "chouksey", "कौन", "है", "founder", "chairman", "आनंद", "सर", "sir"),
            category = "general",
            language = "hi"
        ),
        KnowledgeBaseQA(
            id = "qa_director_hinglish",
            question = "Director कौन है?",
            answer = "Kabir Chouksey सर और Devanshi Chouksey मैडम ऑल इज़ वेल हॉस्पिटल के directors हैं।",
            keywords = listOf("director", "कौन", "है", "कबीर", "देवांशी", "चौकसे", "निदेशक", "सर", "मैडम"),
            category = "general",
            language = "hi"
        ),
        KnowledgeBaseQA(
            id = "qa_hospital_leadership_hinglish",
            question = "Hospital की leadership कौन है?",
            answer = "Anand Prakash Chouksey सर (founder और chairman), Kabir Chouksey सर (director), और Devanshi Chouksey मैडम (director) ऑल इज़ वेल हॉस्पिटल की leadership team हैं।",
            keywords = listOf("leadership", "hospital", "की", "कौन", "है", "management", "team", "नेतृत्व", "सर", "मैडम"),
            category = "general",
            language = "hi"
        ),
        // ==================== ENGLISH Q&A CONTINUES ====================
        KnowledgeBaseQA(
            id = "qa_founder_en",
            question = "Who is the Founder?",
            answer = "Anand Prakash Chouksey sir is the founder of All is Well Hospital.",
            keywords = listOf("founder", "anand", "chouksey", "prakash", "hospital", "who", "sir"),
            category = "general",
            language = "en"
        ),
        KnowledgeBaseQA(
            id = "qa_chairman_en",
            question = "Who is the Chairman?",
            answer = "Anand Prakash Chouksey sir is the chairman of All is Well Hospital.",
            keywords = listOf("chairman", "anand", "chouksey", "prakash", "hospital", "who", "sir"),
            category = "general",
            language = "en"
        ),
        KnowledgeBaseQA(
            id = "qa_51",
            question = "What is Hospital's Email?",
            answer = "You can reach the hospital via email at frontdesk@alliswellhospital.com. This email address is monitored by the hospital's administrative team and can be used for general inquiries, appointment requests, health package details, feedback, or non-urgent medical coordination.",
            keywords = listOf("recommended", "query", "sending", "feedback", "quicker", "receive", "number", "package", "medical", "brief"),
            category = "general",
            language = "en"
        ),
        KnowledgeBaseQA(
            id = "qa_52",
            question = "How many OTs are there?",
            answer = "There are 6+ Modular OTs in All is Well",
            keywords = listOf("well", "there", "many", "all", "how", "modular", "ots"),
            category = "general",
            language = "en"
        ),
        KnowledgeBaseQA(
            id = "qa_53",
            question = "How many rooms are there in hospital?",
            answer = "There are around 350 rooms in our hospital.",
            keywords = listOf("hospital", "there", "around", "our", "many", "350", "rooms", "how"),
            category = "general",
            language = "en"
        ),
        KnowledgeBaseQA(
            id = "qa_54",
            question = "How many beds are there in hospital?",
            answer = "There are 360+ beds in hospital.",
            keywords = listOf("hospital", "there", "many", "beds", "360", "how"),
            category = "general",
            language = "en"
        ),
        KnowledgeBaseQA(
            id = "qa_55",
            question = "What are the core Values of All is Well?",
            answer = "1)Valuing Time  , 2) Striving for Excellence, 3) Practicing Humility, 4) Fostering Inclusion , 5) Living with Integrity.",
            keywords = listOf("integrity", "inclusion", "well", "core", "fostering", "practicing", "striving", "all", "1valuing", "excellence"),
            category = "general",
            language = "en"
        ),
        KnowledgeBaseQA(
            id = "qa_56",
            question = "Which Hospital is best in All is Well Hospital?",
            answer = "All is Well Hospital is Counted in best hospitals  in Burhanpur.",
            keywords = listOf("which", "hospital", "well", "best", "all", "hospitals", "counted", "burhanpur"),
            category = "general",
            language = "en"
        ),
        KnowledgeBaseQA(
            id = "qa_57",
            question = "What is Hospital Opening timing?",
            answer = "Mon - Fri: 8:00 AM - 8:00 PM | Sat - Sun: 9:00 AM - 6:00 PM",
            keywords = listOf("900", "hospital", "mon", "sat", "800", "opening", "sun", "600", "timing", "fri"),
            category = "hospital_info",
            language = "en"
        ),
        KnowledgeBaseQA(
            id = "qa_58",
            question = "Where is All is Well Hospital located?",
            answer = "Near Macro Vision Academy, Burhanpur, Madhya Pradesh, 450331",
            keywords = listOf("madhya", "academy", "hospital", "well", "macro", "near", "pradesh", "all", "vision", "located"),
            category = "facilities",
            language = "en"
        ),
        KnowledgeBaseQA(
            id = "qa_59",
            question = "How can i contact to hospital?",
            answer = "You can contact on +91 7697744444  or +91 7089099888",
            keywords = listOf("hospital", "7089099888", "contact", "7697744444", "how"),
            category = "general",
            language = "en"
        ),
        KnowledgeBaseQA(
            id = "qa_60",
            question = "What is the email for all is well hospital?",
            answer = "digitalmarketing@mvaburhanpur.com",
            keywords = listOf("hospital", "well", "email", "all", "digitalmarketingmvaburhanpurcom"),
            category = "general",
            language = "en"
        ),
        KnowledgeBaseQA(
            id = "qa_61",
            question = "Do I need a referral to see a specialist?",
            answer = "No referral is needed for most departments; you can directly book with the specialist of your choice.",
            keywords = listOf("referral", "departments", "specialist", "most", "choice", "directly", "need", "book", "needed", "see"),
            category = "departments",
            language = "en"
        ),
        KnowledgeBaseQA(
            id = "qa_62",
            question = "How can I see which doctor treats my condition?",
            answer = "Visit \"Meet Our Specialists\" on the website to filter doctors by department and view their expertise.",
            keywords = listOf("which", "meet", "our", "website", "view", "expertise", "department", "specialists", "visit", "see"),
            category = "departments",
            language = "en"
        ),
        KnowledgeBaseQA(
            id = "qa_63",
            question = "Are your doctors experienced?",
            answer = "Yes, our team includes qualified consultants with fellowships and specialized training in their fields.",
            keywords = listOf("our", "includes", "yes", "qualified", "fields", "specialized", "experienced", "their", "fellowships", "doctors"),
            category = "departments",
            language = "en"
        ),
        KnowledgeBaseQA(
            id = "qa_64",
            question = "What health check-up packages do you offer?",
            answer = "We offer Basic Wellness (₹899), Comprehensive Wellness (₹4999), Women's, Diabetic, Pregnancy, and Child packages.",
            keywords = listOf("wellness", "899", "4999", "womens", "offer", "checkup", "diabetic", "packages", "health", "pregnancy"),
            category = "general",
            language = "en"
        ),
        KnowledgeBaseQA(
            id = "qa_65",
            question = "Are the health packages discounted?",
            answer = "Yes, all packages currently offer 25–53% off the original price—check the website for latest offers.",
            keywords = listOf("2553", "discounted", "yes", "currently", "offer", "original", "pricecheck", "all", "offers", "off"),
            category = "general",
            language = "en"
        ),
        KnowledgeBaseQA(
            id = "qa_66",
            question = "What's included in the Diabetic Wellness package?",
            answer = "It includes HbA1c, sugar tests, ECG, 2D Echo, kidney/liver tests, eye exam, and consultations with physician & dietitian.",
            keywords = listOf("wellness", "exam", "eye", "includes", "package", "sugar", "consultations", "hba1c", "kidneyliver", "dietitian"),
            category = "general",
            language = "en"
        ),
        KnowledgeBaseQA(
            id = "qa_67",
            question = "Do you offer dietitian consultations?",
            answer = "Yes, dietitian consultations are included in Comprehensive Wellness and Diabetic Wellness packages.",
            keywords = listOf("wellness", "consultations", "yes", "offer", "dietitian", "included", "diabetic", "packages", "comprehensive"),
            category = "departments",
            language = "en"
        ),
        KnowledgeBaseQA(
            id = "qa_68",
            question = "Are your services affordable?",
            answer = "We provide high-quality, cost-effective care with transparent pricing and discounted wellness packages.",
            keywords = listOf("services", "provide", "transparent", "wellness", "costeffective", "discounted", "highquality", "affordable", "care", "packages"),
            category = "general",
            language = "en"
        ),
        KnowledgeBaseQA(
            id = "qa_69",
            question = "Is parking available at the hospital?",
            answer = "Yes, we offer free parking for patients and visitors near the main entrance.",
            keywords = listOf("patients", "hospital", "free", "near", "yes", "offer", "entrance", "available", "parking", "visitors"),
            category = "facilities",
            language = "en"
        ),
        KnowledgeBaseQA(
            id = "qa_70",
            question = "Do you accept walk-in patients?",
            answer = "Yes, walk-ins are welcome for general consultation; appointments help reduce waiting time.",
            keywords = listOf("patients", "help", "waiting", "time", "appointments", "walkins", "yes", "welcome", "general", "consultation"),
            category = "general",
            language = "en"
        ),
        KnowledgeBaseQA(
            id = "qa_71",
            question = "Can I get a bill estimate before treatment?",
            answer = "Yes, our front desk can provide a tentative cost estimate after a brief consultation with the doctor.",
            keywords = listOf("get", "before", "bill", "provide", "our", "desk", "tentative", "yes", "treatment", "front"),
            category = "general",
            language = "en"
        ),
        KnowledgeBaseQA(
            id = "qa_72",
            question = "Can I access my past visit history online?",
            answer = "Hospital have all past detail of the patient every patient has a unique ID at the time of first visit from there they can access there past history.",
            keywords = listOf("online", "has", "patient", "hospital", "access", "detail", "there", "unique", "from", "history"),
            category = "general",
            language = "en"
        ),
        KnowledgeBaseQA(
            id = "qa_73",
            question = "Do you share reports with other doctors?",
            answer = "Yes, with your consent, we can share digital or printed reports with any specialist you refer to.",
            keywords = listOf("refer", "share", "yes", "other", "consent", "specialist", "digital", "doctors", "reports", "any"),
            category = "departments",
            language = "en"
        ),
        KnowledgeBaseQA(
            id = "qa_74",
            question = "What should I do in a medical emergency?",
            answer = "Call our emergency number immediately or visit the hospital—our 24/7 team is ready to help.",
            keywords = listOf("should", "call", "247", "help", "our", "number", "hospitalour", "medical", "immediately", "visit"),
            category = "hospital_info",
            language = "en"
        ),
        KnowledgeBaseQA(
            id = "qa_75",
            question = "Is the hospital wheelchair-accessible?",
            answer = "Yes, the entire facility is ramp-enabled and wheelchair-friendly for patient comfort.",
            keywords = listOf("patient", "hospital", "rampenabled", "yes", "entire", "wheelchairaccessible", "comfort", "facility", "wheelchairfriendly"),
            category = "general",
            language = "en"
        ),
        KnowledgeBaseQA(
            id = "qa_76",
            question = "How do I use the WhatsApp chat feature?",
            answer = "Click the WhatsApp icon on the website to start a chat for appointment help or quick queries.",
            keywords = listOf("use", "click", "help", "start", "queries", "website", "icon", "feature", "quick", "chat"),
            category = "general",
            language = "en"
        ),
        KnowledgeBaseQA(
            id = "qa_77",
            question = "Can I reschedule my appointment online?",
            answer = "Yes, log in to your booking or message us via WhatsApp to change your slot.",
            keywords = listOf("online", "via", "reschedule", "yes", "message", "whatsapp", "booking", "change", "slot", "appointment"),
            category = "appointments",
            language = "en"
        ),
        KnowledgeBaseQA(
            id = "qa_78",
            question = "Do you provide food for patients and attendants?",
            answer = "Yes, we offer hygienic, dietitian-approved meals; special diets can be arranged on request.",
            keywords = listOf("patients", "diets", "provide", "food", "special", "yes", "offer", "meals", "arranged", "request"),
            category = "general",
            language = "en"
        ),
        KnowledgeBaseQA(
            id = "qa_79",
            question = "Is your hospital accredited or certified?",
            answer = "Yes, we follow national healthcare standards and maintain certified protocols for safety, hygiene, and treatment.",
            keywords = listOf("protocols", "hospital", "maintain", "yes", "treatment", "standards", "safety", "follow", "healthcare", "accredited"),
            category = "general",
            language = "en"
        ),
        KnowledgeBaseQA(
            id = "qa_80",
            question = "What makes your hospital different?",
            answer = "We combine experienced specialists, advanced diagnostics, transparent pricing, and patient-first care under one roof.",
            keywords = listOf("hospital", "different", "transparent", "one", "patientfirst", "diagnostics", "specialists", "roof", "experienced", "care"),
            category = "general",
            language = "en"
        ),
        KnowledgeBaseQA(
            id = "qa_81",
            question = "Do you serve rural or remote patients?",
            answer = "Yes, we welcome patients from all areas and offer tele-consultation support for those traveling from far.",
            keywords = listOf("patients", "serve", "those", "teleconsultation", "rural", "remote", "yes", "from", "offer", "welcome"),
            category = "general",
            language = "en"
        ),
        KnowledgeBaseQA(
            id = "qa_82",
            question = "Is your hospital eco-friendly or sustainable?",
            answer = "We follow green practices like waste segregation, energy-efficient systems, and reduced plastic use where possible.",
            keywords = listOf("practices", "use", "hospital", "green", "reduced", "possible", "energyefficient", "waste", "follow", "systems"),
            category = "general",
            language = "en"
        ),
        KnowledgeBaseQA(
            id = "qa_83",
            question = "Can I tour the hospital before admission?",
            answer = "Yes, our front desk can arrange a guided visit—just call ahead or request via WhatsApp.",
            keywords = listOf("call", "before", "hospital", "our", "desk", "visitjust", "via", "yes", "ahead", "front"),
            category = "general",
            language = "en"
        ),
        KnowledgeBaseQA(
            id = "qa_84",
            question = "How do I give feedback or file a complaint?",
            answer = "Share your experience via the \"Contact Us\" , WhatsApp, or after the discharge of the patient, the patient care will contact you for the feedback—we value your input.",
            keywords = listOf("patient", "will", "feedbackwe", "via", "share", "discharge", "feedback", "complaint", "experience", "contact"),
            category = "general",
            language = "en"
        ),
        KnowledgeBaseQA(
            id = "qa_85",
            question = "Are your doctors full-time staff?",
            answer = "Yes, our core specialists are dedicated full-time consultants ensuring consistent, reliable care.",
            keywords = listOf("staff", "consistent", "our", "fulltime", "reliable", "yes", "core", "specialists", "dedicated", "care"),
            category = "departments",
            language = "en"
        ),
        KnowledgeBaseQA(
            id = "qa_86",
            question = "Is patient privacy protected?",
            answer = "All medical records and personal data are confidential and shared only with your consent.",
            keywords = listOf("patient", "records", "shared", "personal", "consent", "medical", "all", "only", "privacy", "data"),
            category = "general",
            language = "en"
        ),
        KnowledgeBaseQA(
            id = "qa_87",
            question = "Who is anand Prakash Chouksey sir?",
            answer = "anand Prakash Chouksey sir is the founder of All is Well Hospital.",
            keywords = listOf("hospital", "well", "chouksey", "who", "anand", "all", "prakash", "founder"),
            category = "general",
            language = "en"
        ),
        KnowledgeBaseQA(
            id = "qa_88",
            question = "Is Hospital NABH certified?",
            answer = "Yes we're NABH certified.You can view it on our website.",
            keywords = listOf("nabh", "hospital", "were", "view", "our", "yes", "certifiedyou", "certified", "website"),
            category = "general",
            language = "en"
        ),
        KnowledgeBaseQA(
            id = "qa_89",
            question = "Is the hospital ISO 9001 certified?",
            answer = "Yes, the hospital is ISO 9001 certified.",
            keywords = listOf("hospital", "yes", "9001", "certified", "iso"),
            category = "general",
            language = "en"
        ),
        KnowledgeBaseQA(
            id = "qa_90",
            question = "Doctors Count ?",
            answer = "There are more than 30+ doctors available.",
            keywords = listOf("there", "available", "doctors", "more", "count", "than"),
            category = "departments",
            language = "en"
        ),
        KnowledgeBaseQA(
            id = "qa_91",
            question = "Can I book appointment online ?",
            answer = "Yes, You can book appointment online directly through website or by calling on reception.",
            keywords = listOf("online", "through", "reception", "yes", "calling", "directly", "book", "appointment", "website"),
            category = "appointments",
            language = "en"
        ),
        KnowledgeBaseQA(
            id = "qa_92",
            question = "Is Emergency Services available ?",
            answer = "Yes, Emergency services are available 24/7 in our hospital.",
            keywords = listOf("247", "services", "hospital", "our", "yes", "available", "emergency"),
            category = "hospital_info",
            language = "en"
        ),
        KnowledgeBaseQA(
            id = "qa_93",
            question = "What is Ambulance number?",
            answer = "Call ambulance: +91 7697744444",
            keywords = listOf("call", "number", "7697744444", "ambulance"),
            category = "general",
            language = "en"
        ),
        KnowledgeBaseQA(
            id = "qa_94",
            question = "What time does the cafeteria stop serving breakfast/lunch/dinner?",
            answer = "Cafeterias serve meals during standard morning, midday, and evening windows. Arrive midway through any meal period to ensure you don't miss the cutoff.",
            keywords = listOf("midday", "standard", "cutoff", "arrive", "any", "breakfastlunchdinner", "through", "ensure", "stop", "period"),
            category = "hospital_info",
            language = "en"
        ),
        KnowledgeBaseQA(
            id = "qa_95",
            question = "What time do doctors do ward rounds?",
            answer = "Ward rounds commonly happen once or twice a day.",
            keywords = listOf("happen", "day", "commonly", "twice", "ward", "rounds", "once", "doctors", "time"),
            category = "departments",
            language = "en"
        ),
        KnowledgeBaseQA(
            id = "qa_96",
            question = "What are the surgery scheduling hours?",
            answer = "Surgeries are generally scheduled during regular weekday daytime hours. For specific timing, please check with reception, as schedules vary based on availability and urgency.",
            keywords = listOf("based", "availability", "urgency", "specific", "schedules", "weekday", "regular", "surgery", "please", "timing"),
            category = "hospital_info",
            language = "en"
        ),
        KnowledgeBaseQA(
            id = "qa_97",
            question = "When are health camps or screening drives held?",
            answer = "There are many health camps held by the hospital previously in panchayat & clinics, cardiology camp many more camps like this were held.",
            keywords = listOf("camps", "cardiology", "hospital", "were", "there", "previously", "more", "many", "clinics", "when"),
            category = "general",
            language = "en"
        ),
        KnowledgeBaseQA(
            id = "qa_98",
            question = "Can I bring my own food for the patient?",
            answer = "Yes, you can bring food but give it after the concerned by the specialist only.",
            keywords = listOf("but", "patient", "concerned", "bring", "food", "own", "yes", "specialist", "only", "give"),
            category = "general",
            language = "en"
        ),
        KnowledgeBaseQA(
            id = "qa_99",
            question = "Is photography or video recording allowed inside?",
            answer = "For privacy and safety reasons, photography and video recording are generally not permitted inside. Please check with reception for any specific guidelines or exceptions.",
            keywords = listOf("guidelines", "recording", "generally", "allowed", "reception", "not", "safety", "please", "check", "exceptions"),
            category = "general",
            language = "en"
        ),
        KnowledgeBaseQA(
            id = "qa_100",
            question = "Do you offer payment plans or EMI for expensive treatments?",
            answer = "We understand treatment costs can vary, and flexible payment options may be available. Kindly speak with reception to explore plans or EMI details suited to your needs.",
            keywords = listOf("offer", "may", "options", "available", "plans", "emi", "treatment", "treatments", "details", "payment"),
            category = "general",
            language = "en"
        ),
        KnowledgeBaseQA(
            id = "qa_101",
            question = "Is there a discount for government employees or senior citizens?",
            answer = "No, there is no discount like this right now but the employees who is working under Micro vision academy, All is Well can be treated by the vision card.",
            keywords = listOf("well", "treated", "senior", "right", "government", "micro", "vision", "this", "under", "working"),
            category = "general",
            language = "en"
        ),
        KnowledgeBaseQA(
            id = "qa_102",
            question = "What happens if I can't pay the full bill immediately?",
            answer = "If you can't pay the full bill immediately, please speak with reception—they can guide you through available support options or payment arrangements.",
            keywords = listOf("bill", "payment", "through", "cant", "guide", "immediately", "please", "support", "available", "full"),
            category = "general",
            language = "en"
        ),
        KnowledgeBaseQA(
            id = "qa_103",
            question = "What should I do if I suspect a heart attack or stroke?",
            answer = "Alert any staff member immediately or call the emergency no. +91 7697744444.",
            keywords = listOf("should", "heart", "staff", "call", "attack", "stroke", "alert", "immediately", "member", "7697744444"),
            category = "general",
            language = "en"
        ),
        KnowledgeBaseQA(
            id = "qa_104",
            question = "Do you have a poison control or toxicology unit?",
            answer = "For poisoning concerns, contact reception or emergency services immediately for rapid guidance and care coordination.",
            keywords = listOf("unit", "services", "concerns", "reception", "guidance", "toxicology", "contact", "rapid", "poison", "have"),
            category = "general",
            language = "en"
        ),
        KnowledgeBaseQA(
            id = "qa_105",
            question = "What is the protocol if a patient's condition suddenly worsens?",
            answer = "Alert any nearby staff or press the emergency call button—our team is trained to respond quickly to urgent changes. Reception can also assist with immediate escalation.",
            keywords = listOf("changes", "immediate", "respond", "alert", "trained", "press", "urgent", "condition", "any", "suddenly"),
            category = "general",
            language = "en"
        ),
        KnowledgeBaseQA(
            id = "qa_106",
            question = "Do you offer genetic testing or advanced molecular diagnostics?",
            answer = "While we don't conduct genetic or advanced molecular tests on-site, we can refer you to trusted partner labs. They will perform the test and share the report through us for your doctor to review.",
            keywords = listOf("conduct", "refer", "molecular", "offer", "testing", "partner", "doctor", "through", "review", "onsite"),
            category = "diagnostics",
            language = "en"
        ),
        KnowledgeBaseQA(
            id = "qa_107",
            question = "Can I get vitamin D, B12, or hormone tests without a prescription?",
            answer = "Most routine tests—including Vitamin D, B12, and hormone panels—can be done without a prescription. The only exception is pregnancy testing, which requires a doctor's prescription.",
            keywords = listOf("get", "which", "testing", "most", "done", "exception", "without", "prescription", "tests", "pregnancy"),
            category = "diagnostics",
            language = "en"
        ),
        KnowledgeBaseQA(
            id = "qa_108",
            question = "Do patient rooms have TVs or entertainment options?",
            answer = "Room amenities like TVs vary by category (General, Silver, Diamond, or Platinum). Please check with reception for what's included in your selected room.",
            keywords = listOf("diamond", "entertainment", "rooms", "options", "tvs", "amenities", "room", "please", "general", "category"),
            category = "general",
            language = "en"
        ),
        KnowledgeBaseQA(
            id = "qa_109",
            question = "Is there a refrigerator or microwave in the room?",
            answer = "These conveniences depend on your room type. Reception can confirm availability based on your booking.",
            keywords = listOf("confirm", "there", "microwave", "reception", "availability", "based", "room", "these", "booking", "type"),
            category = "general",
            language = "en"
        ),
        KnowledgeBaseQA(
            id = "qa_110",
            question = "Do you provide laundry services for patients?",
            answer = "For inquiries about laundry services, please check with reception—they can provide the most up-to-date information on availability",
            keywords = listOf("patients", "uptodate", "services", "provide", "inquiries", "availability", "most", "about", "please", "check"),
            category = "general",
            language = "en"
        ),
        KnowledgeBaseQA(
            id = "qa_111",
            question = "Is free WiFi available in patient rooms and waiting areas?",
            answer = "WiFi availability differs across room types and common areas. For access details, please ask reception at check-in.",
            keywords = listOf("availability", "rooms", "available", "room", "please", "common", "waiting", "details", "differs", "ask"),
            category = "diagnostics",
            language = "en"
        ),
        KnowledgeBaseQA(
            id = "qa_112",
            question = "Do you offer a mobile app for appointments and reports?",
            answer = "you can book appointments, generate tokens, and access reports via our website or WhatsApp. Reception is also happy to assist with bookings anytime.",
            keywords = listOf("our", "happy", "offer", "bookings", "book", "mobile", "assist", "reports", "website", "appointments"),
            category = "appointments",
            language = "en"
        ),
        KnowledgeBaseQA(
            id = "qa_113",
            question = "Do you issue birth/death certificates?",
            answer = "Yes, the hospital processes applications for birth and death certificates. For queries or assistance, please contact the MRD (Medical Records Department) or check with reception for direction.",
            keywords = listOf("queries", "certificates", "issue", "department", "birthdeath", "direction", "records", "death", "medical", "processes"),
            category = "general",
            language = "en"
        ),
        KnowledgeBaseQA(
            id = "qa_114",
            question = "Do you conduct free health camps in nearby villages?",
            answer = "Yes, we regularly conduct free health camps in nearby villages. For schedules or participation details, please inquire at reception.",
            keywords = listOf("conduct", "camps", "inquire", "schedules", "nearby", "free", "yes", "reception", "regularly", "participation"),
            category = "general",
            language = "en"
        ),
        KnowledgeBaseQA(
            id = "qa_115",
            question = "Can I volunteer or intern at the hospital?",
            answer = "Yes, we welcome volunteers and interns to work under our specialists. Please speak with reception to learn about opportunities and the application process.",
            keywords = listOf("our", "application", "welcome", "specialists", "volunteers", "please", "learn", "opportunities", "under", "hospital"),
            category = "general",
            language = "en"
        ),
        KnowledgeBaseQA(
            id = "qa_116",
            question = "Do you offer medical training or certification courses?",
            answer = "Yes, we offer medical training programs with certification upon completion.",
            keywords = listOf("upon", "certification", "completion", "yes", "offer", "programs", "medical", "courses", "training"),
            category = "general",
            language = "en"
        ),
        KnowledgeBaseQA(
            id = "qa_117",
            question = "How many visitors are allowed at a time?",
            answer = "For general wards, one visitor with a patient pass is allowed at a time. ICU visits are restricted to 1–2 close relatives during designated hours (11 AM–12 PM and 6 PM–7 PM).",
            keywords = listOf("many", "am12", "pass", "close", "visits", "visitor", "general", "visitors", "pm7", "how"),
            category = "hospital_info",
            language = "en"
        ),
        KnowledgeBaseQA(
            id = "qa_118",
            question = "Can children visit patients in ICU?",
            answer = "Children under 12 are generally not permitted in ICU to ensure patient safety and infection control. Please consult the nursing station for any exceptions.",
            keywords = listOf("nursing", "station", "any", "patients", "ensure", "safety", "please", "consult", "control", "permitted"),
            category = "general",
            language = "en"
        ),
        KnowledgeBaseQA(
            id = "qa_119",
            question = "What items can I bring for the patient?",
            answer = "You may bring essential personal items, prescribed medicines, and light snacks. Always check with nursing staff before bringing outside food or gifts, especially for ICU patients.",
            keywords = listOf("prescribed", "especially", "nursing", "personal", "items", "may", "medicines", "patients", "before", "bring"),
            category = "general",
            language = "en"
        ),
        KnowledgeBaseQA(
            id = "qa_120",
            question = "Are flowers or outside food allowed in patient rooms?",
            answer = "Flowers and outside food are not allowed in ICU/critical care areas. For general wards, please confirm with the nursing team before bringing such items.",
            keywords = listOf("nursing", "items", "care", "rooms", "flowers", "before", "confirm", "food", "icucritical", "please"),
            category = "general",
            language = "en"
        ),
        KnowledgeBaseQA(
            id = "qa_121",
            question = "Can I stay overnight with the patient?",
            answer = "Yes, one attendant may stay overnight in private rooms or designated guest rooms (102, 103, 221, 222), subject to availability and hospital policy.",
            keywords = listOf("availability", "may", "rooms", "222", "102", "overnight", "221", "103", "stay", "hospital"),
            category = "general",
            language = "en"
        ),
        KnowledgeBaseQA(
            id = "qa_122",
            question = "Can I video call the patient if I cannot visit physically?",
            answer = "Yes, video calls are permitted with the patient's consent and nursing staff coordination, especially for ICU patients or when physical visits aren't possible.",
            keywords = listOf("especially", "arent", "nursing", "consent", "patients", "visits", "coordination", "permitted", "video", "call"),
            category = "general",
            language = "en"
        ),
        KnowledgeBaseQA(
            id = "qa_123",
            question = "Can I visit multiple patients in one trip?",
            answer = "Yes, you may visit multiple patients during visiting hours. Please collect separate visitor passes for each patient at the reception desk.",
            keywords = listOf("desk", "each", "may", "separate", "patients", "passes", "trip", "please", "visitor", "hours"),
            category = "general",
            language = "en"
        ),
        KnowledgeBaseQA(
            id = "qa_124",
            question = "What items are prohibited in patient rooms or ICU?",
            answer = "Prohibited items include outside food (in ICU), flowers, sharp objects, alcohol, tobacco, and large electronics. Please check with staff for a complete, updated list.",
            keywords = listOf("items", "rooms", "flowers", "sharp", "food", "updated", "alcohol", "please", "tobacco", "electronics"),
            category = "general",
            language = "en"
        ),
        KnowledgeBaseQA(
            id = "qa_126",
            question = "How do I confirm if my appointment is booked?",
            answer = "After booking, you'll receive a token number via SMS/WhatsApp as confirmation. You can also call +91 76977 44444 or +91 70890 99888 or check with reception to verify your appointment status.",
            keywords = listOf("booked", "smswhatsapp", "booking", "token", "receive", "status", "confirm", "44444", "number", "99888"),
            category = "appointments",
            language = "en"
        ),
        KnowledgeBaseQA(
            id = "qa_127",
            question = "What happens if I miss my appointment slot?",
            answer = "If you miss your slot, please contact reception at +91 76977 44444 or +91 70890 99888 to reschedule. Walk-in consultations may be available based on doctor availability, though waiting times may apply.",
            keywords = listOf("based", "availability", "times", "may", "available", "doctor", "though", "44444", "please", "99888"),
            category = "appointments",
            language = "en"
        ),
        KnowledgeBaseQA(
            id = "qa_128",
            question = "Can I get a same-day or urgent appointment?",
            answer = "Yes, same-day walk-in consultations are available for general OPD on a first-come, first-served basis. For urgent medical needs, our 24/7 emergency services are always accessible.",
            keywords = listOf("get", "our", "available", "urgent", "247", "services", "firstserved", "accessible", "sameday", "medical"),
            category = "appointments",
            language = "en"
        ),
        KnowledgeBaseQA(
            id = "qa_129",
            question = "How do I reschedule or cancel my appointment?",
            answer = "You can reschedule or cancel by calling +91 76977 44444 or +91 70890 99888, messaging via WhatsApp, or logging into your online booking. Please provide prior notice to free the slot for other patients.",
            keywords = listOf("other", "calling", "booking", "online", "patients", "44444", "prior", "logging", "please", "99888"),
            category = "appointments",
            language = "en"
        ),
        KnowledgeBaseQA(
            id = "qa_130",
            question = "Is there a separate counter for senior citizens or pregnant women?",
            answer = "While there's no dedicated counter, senior citizens and pregnant women receive priority assistance at reception. Please inform staff upon arrival for expedited support.",
            keywords = listOf("women", "separate", "counter", "receive", "upon", "expedited", "inform", "senior", "please", "dedicated"),
            category = "general",
            language = "en"
        ),
        KnowledgeBaseQA(
            id = "qa_131",
            question = "Do I need to carry previous medical reports for consultation?",
            answer = "Yes, carrying previous medical reports, prescriptions, and test results helps your doctor make accurate decisions. Please also bring a valid ID and insurance documents if applicable.",
            keywords = listOf("previous", "consultation", "doctor", "applicable", "bring", "medical", "carry", "please", "reports", "test"),
            category = "departments",
            language = "en"
        ),
        KnowledgeBaseQA(
            id = "qa_132",
            question = "Can I consult a doctor via phone or video call?",
            answer = "Yes, tele-consultation via video/phone is available for remote patients. Contact +91 76977 44444 or use the WhatsApp feature on our website to schedule a virtual consultation.",
            keywords = listOf("our", "virtual", "available", "consultation", "doctor", "patients", "44444", "consult", "video", "videophone"),
            category = "departments",
            language = "en"
        ),
        KnowledgeBaseQA(
            id = "qa_133",
            question = "How early should I arrive before my appointment time?",
            answer = "We recommend arriving 15–20 minutes before your scheduled time for registration and token collection. For first-time visits, please allow an extra 10 minutes for documentation.",
            keywords = listOf("recommend", "extra", "token", "arrive", "documentation", "should", "before", "minutes", "visits", "1520"),
            category = "appointments",
            language = "en"
        ),
        KnowledgeBaseQA(
            id = "qa_134",
            question = "Can I pay using UPI, Google Pay, or PhonePe?",
            answer = "Yes, we accept UPI payments including Google Pay, PhonePe, and Paytm, along with cash, cards, and net banking at all billing counters.",
            keywords = listOf("using", "including", "yes", "payments", "along", "banking", "upi", "paytm", "cash", "google"),
            category = "general",
            language = "en"
        ),
        KnowledgeBaseQA(
            id = "qa_135",
            question = "Do you provide an itemized/detailed bill?",
            answer = "Yes, you can request an itemized bill showing consultation fees, tests, medicines, and room charges at the billing counter during or after payment.",
            keywords = listOf("bill", "payment", "provide", "charges", "during", "itemizeddetailed", "yes", "room", "itemized", "fees"),
            category = "general",
            language = "en"
        ),
        KnowledgeBaseQA(
            id = "qa_136",
            question = "Can I receive my payment receipt via email or WhatsApp?",
            answer = "Yes, payment receipts can be shared via your registered WhatsApp number or email upon request at the billing desk.",
            keywords = listOf("payment", "via", "shared", "receipts", "number", "yes", "upon", "desk", "email", "request"),
            category = "general",
            language = "en"
        ),
        KnowledgeBaseQA(
            id = "qa_137",
            question = "What should I do if there is an error in my bill?",
            answer = "Please inform the billing counter staff immediately or call +91 76977 44444; our team will verify and correct any discrepancies promptly.",
            keywords = listOf("our", "correct", "discrepancies", "counter", "billing", "any", "should", "44444", "inform", "please"),
            category = "general",
            language = "en"
        ),
        KnowledgeBaseQA(
            id = "qa_138",
            question = "Do you accept international health insurance?",
            answer = "Currently, we primarily accept Indian health insurance providers and government schemes like Ayushman Bharat; for international policies, please contact our billing desk for case-by-case assistance.",
            keywords = listOf("our", "desk", "providers", "billing", "indian", "bharat", "currently", "government", "please", "accept"),
            category = "insurance",
            language = "en"
        ),
        KnowledgeBaseQA(
            id = "qa_139",
            question = "Can I get a tentative cost estimate before treatment starts?",
            answer = "Yes, after a brief consultation, our front desk can provide a tentative cost estimate for procedures, tests, or admissions.",
            keywords = listOf("get", "before", "starts", "provide", "our", "desk", "tentative", "yes", "treatment", "admissions"),
            category = "general",
            language = "en"
        ),
        KnowledgeBaseQA(
            id = "qa_140",
            question = "Is there a separate billing counter for insurance patients?",
            answer = "While there's no dedicated counter, our billing team has specialized staff to assist insurance patients with cashless pre-authorization and documentation.",
            keywords = listOf("patients", "has", "staff", "there", "our", "while", "cashless", "dedicated", "specialized", "separate"),
            category = "insurance",
            language = "en"
        ),
        KnowledgeBaseQA(
            id = "qa_141",
            question = "How do I claim a refund if treatment is cancelled?",
            answer = "Refund requests are processed at the billing desk with doctor approval and original payment proof; please contact +91 76977 44444 for guidance on your specific case.",
            keywords = listOf("desk", "guidance", "claim", "proof", "doctor", "specific", "billing", "approval", "44444", "processed"),
            category = "insurance",
            language = "en"
        ),
        KnowledgeBaseQA(
            id = "qa_142",
            question = "Can I pay my bill in installments or EMI?",
            answer = "For eligible treatments and admissions, EMI options through partner banks may be available; please discuss with our billing desk at the time of admission.",
            keywords = listOf("our", "desk", "partner", "may", "options", "available", "discuss", "billing", "eligible", "emi"),
            category = "general",
            language = "en"
        ),
        KnowledgeBaseQA(
            id = "qa_143",
            question = "Is there a discount for cash payment or early payment?",
            answer = "Discounted rates are available on our health check-up packages; for other services, please inquire at the billing counter for any ongoing offers or concessions.",
            keywords = listOf("our", "other", "available", "packages", "counter", "billing", "any", "services", "please", "rates"),
            category = "general",
            language = "en"
        ),
        KnowledgeBaseQA(
            id = "qa_144",
            question = "How do I get a duplicate bill if I lose the original?",
            answer = "Visit the billing counter with a valid ID and patient details to request a duplicate bill; a nominal processing fee may apply, and the copy will be stamped for official use.",
            keywords = listOf("get", "lose", "may", "counter", "billing", "nominal", "request", "processing", "details", "how"),
            category = "general",
            language = "en"
        ),
        KnowledgeBaseQA(
            id = "qa_145",
            question = "Do you offer concessions for senior citizens or BPL card holders?",
            answer = "Concessions may be available for eligible patients. For details on applicable benefits, please contact the Insurance Department or check with reception.",
            keywords = listOf("offer", "department", "may", "available", "eligible", "patients", "applicable", "senior", "please", "details"),
            category = "general",
            language = "en"
        ),
        KnowledgeBaseQA(
            id = "qa_146",
            question = "Is Ayushman Bharat / PM-JAY card accepted here?",
            answer = "Yes, we accept Ayushman Bharat / PM-JAY cards. For eligibility, coverage, and claim assistance, please speak with the Insurance Department.",
            keywords = listOf("bharat", "yes", "here", "department", "card", "eligibility", "claim", "assistance", "please", "speak"),
            category = "general",
            language = "en"
        ),
        KnowledgeBaseQA(
            id = "qa_147",
            question = "Who can I contact for insurance-related queries at the hospital?",
            answer = "You can contact to Ashish Rawale for all the insurance queries.",
            keywords = listOf("queries", "hospital", "insurancerelated", "contact", "who", "all", "insurance", "ashish", "rawale"),
            category = "insurance",
            language = "en"
        ),
        KnowledgeBaseQA(
            id = "qa_148",
            question = "What if I need emergency surgery but have no insurance?",
            answer = "Emergency care is provided based on medical need, regardless of insurance status. For financial support options or billing guidance, please contact the Insurance Department after stabilization.",
            keywords = listOf("provided", "based", "guidance", "department", "stabilization", "care", "options", "billing", "status", "medical"),
            category = "insurance",
            language = "en"
        ),
        KnowledgeBaseQA(
            id = "qa_149",
            question = "What should I do if I lose a personal item in the hospital?",
            answer = "Please inform the reception desk or call +91 76977 44444 immediately; our staff will help you search and coordinate with security for recovery.",
            keywords = listOf("desk", "our", "personal", "lose", "item", "should", "44444", "recovery", "inform", "please"),
            category = "general",
            language = "en"
        ),
        KnowledgeBaseQA(
            id = "qa_150",
            question = "Is there a lost and found desk or contact?",
            answer = "Lost items are managed at the reception/security desk near the main entrance—please contact them directly or call +91 76977 44444 for assistance.",
            keywords = listOf("call", "receptionsecurity", "76977", "desk", "there", "44444", "near", "items", "contact", "assistance"),
            category = "general",
            language = "en"
        ),
        KnowledgeBaseQA(
            id = "qa_151",
            question = "Can the hospital help me book a taxi, auto, or cab?",
            answer = "Yes, our reception team can assist you in arranging local transport, or you can directly book an auto transport via our online auto booking app.",
            keywords = listOf("cab", "our", "booking", "book", "auto", "arranging", "online", "transport", "team", "assist"),
            category = "appointments",
            language = "en"
        ),
        KnowledgeBaseQA(
            id = "qa_152",
            question = "Can I get a visit summary letter for my employer or school?",
            answer = "Yes, you can request a visit summary or medical certificate at the reception or billing desk after consultation; please carry valid ID for verification.",
            keywords = listOf("get", "summary", "desk", "consultation", "billing", "verification", "medical", "please", "carry", "request"),
            category = "general",
            language = "en"
        ),
        KnowledgeBaseQA(
            id = "qa_153",
            question = "Where can I buy toiletries, blankets, or essentials for the patient?",
            answer = "Our in-house pharmacy and nearby cafeteria stock basic essentials; for specialized items, please ask reception—they can assist with procurement or local vendor guidance.",
            keywords = listOf("our", "stock", "guidance", "items", "procurement", "specialized", "receptionthey", "blankets", "vendor", "please"),
            category = "facilities",
            language = "en"
        ),
        KnowledgeBaseQA(
            id = "qa_154",
            question = "Is there a cloakroom or locker facility available?",
            answer = "While dedicated lockers aren't available, reception can help store small valuables temporarily; we recommend keeping important items with you for security.",
            keywords = listOf("arent", "recommend", "important", "items", "facility", "available", "keeping", "valuables", "temporarily", "dedicated"),
            category = "diagnostics",
            language = "en"
        ),
        KnowledgeBaseQA(
            id = "qa_155",
            question = "Do you have facilities for visually or hearing-impaired patients?",
            answer = "Yes, the hospital is wheelchair-accessible with ramps and elevators; please inform reception upon arrival for personalized assistance and communication support.",
            keywords = listOf("ramps", "facilities", "wheelchairaccessible", "hearingimpaired", "patients", "elevators", "upon", "inform", "visually", "please"),
            category = "general",
            language = "en"
        ),
        KnowledgeBaseQA(
            id = "qa_156",
            question = "Can elderly or differently-abled patients get priority service?",
            answer = "Yes, senior citizens and differently-abled patients receive priority assistance at reception and OPD—just inform staff upon arrival for expedited support.",
            keywords = listOf("get", "elderly", "receive", "patients", "upon", "differentlyabled", "expedited", "inform", "senior", "priority"),
            category = "general",
            language = "en"
        ),
        KnowledgeBaseQA(
            id = "qa_157",
            question = "Is there a private lactation or nursing room for mothers?",
            answer = "While a dedicated lactation room isn't specified, private rooms and guest areas can be arranged for nursing mothers upon request at reception.",
            keywords = listOf("nursing", "there", "mothers", "while", "upon", "reception", "room", "dedicated", "areas", "rooms"),
            category = "general",
            language = "en"
        ),
        KnowledgeBaseQA(
            id = "qa_158",
            question = "Where can I park my vehicle for multiple days during admission?",
            answer = "Free parking is available near the main entrance for patients and visitors; for multi-day parking during admission, please register your vehicle at reception for security coordination.",
            keywords = listOf("multiday", "vehicle", "park", "available", "patients", "near", "days", "multiple", "please", "coordination"),
            category = "facilities",
            language = "en"
        ),
        KnowledgeBaseQA(
            id = "qa_159",
            question = "Do I need a doctor's prescription for lab tests?",
            answer = "Yes, a valid doctor's prescription is required for most diagnostic tests to ensure appropriate testing and accurate interpretation of results.",
            keywords = listOf("valid", "required", "accurate", "yes", "testing", "diagnostic", "most", "ensure", "appropriate", "need"),
            category = "departments",
            language = "en"
        ),
        KnowledgeBaseQA(
            id = "qa_160",
            question = "Can I walk in for a blood test without an appointment?",
            answer = "Yes, our 24/7 laboratory accepts walk-in patients for blood tests; simply register at the lab counter with your prescription or it can be done without prescription.",
            keywords = listOf("our", "simply", "counter", "247", "patients", "done", "without", "prescription", "tests", "laboratory"),
            category = "appointments",
            language = "en"
        ),
        KnowledgeBaseQA(
            id = "qa_161",
            question = "How do I give a urine or stool sample?",
            answer = "Clean collection containers and instructions are provided at the lab counter; please follow the hygiene guidelines shared by our staff for accurate results.",
            keywords = listOf("provided", "our", "stool", "clean", "counter", "hygiene", "sample", "shared", "containers", "urine"),
            category = "general",
            language = "en"
        ),
        KnowledgeBaseQA(
            id = "qa_162",
            question = "Is home sample collection service available?",
            answer = "Home sample collection may be available for select tests and areas; please call +91 76977 44444 or WhatsApp us to check eligibility and schedule.",
            keywords = listOf("sample", "call", "select", "44444", "service", "schedule", "eligibility", "please", "check", "areas"),
            category = "diagnostics",
            language = "en"
        ),
        KnowledgeBaseQA(
            id = "qa_163",
            question = "How long are lab reports considered valid?",
            answer = "Report validity depends on the test and clinical context; generally, routine reports are valid for 3–6 months, but your doctor will advise based on your condition.",
            keywords = listOf("based", "doctor", "condition", "validity", "considered", "depends", "advise", "long", "reports", "test"),
            category = "diagnostics",
            language = "en"
        ),
        KnowledgeBaseQA(
            id = "qa_164",
            question = "Do you provide both hard copy and digital soft copy of reports?",
            answer = "Yes, lab reports are delivered digitally via your registered WhatsApp number and can also be collected as printed copies from the report counter.",
            keywords = listOf("collected", "delivered", "digitally", "registered", "digital", "counter", "soft", "number", "from", "reports"),
            category = "general",
            language = "en"
        ),
        KnowledgeBaseQA(
            id = "qa_165",
            question = "Can I repeat a test if I doubt the result?",
            answer = "Yes, you may request a repeat test after consulting your doctor; please discuss your concerns with the lab staff or treating physician for guidance.",
            keywords = listOf("staff", "lab", "concerns", "yes", "result", "guidance", "please", "physician", "may", "doubt"),
            category = "diagnostics",
            language = "en"
        ),
        KnowledgeBaseQA(
            id = "qa_166",
            question = "What if I need a specialized test not available here?",
            answer = "For specialized tests not available onsite, our team can coordinate with accredited external labs; please consult your doctor or the lab desk for assistance.",
            keywords = listOf("our", "desk", "specialized", "available", "doctor", "onsite", "here", "external", "please", "accredited"),
            category = "diagnostics",
            language = "en"
        ),
        KnowledgeBaseQA(
            id = "qa_167",
            question = "Can I access my reports through an online patient portal?",
            answer = "Digital reports are shared via WhatsApp or email  please check with reception for the latest updates.",
            keywords = listOf("online", "patient", "portal", "access", "shared", "through", "via", "reception", "latest", "email"),
            category = "general",
            language = "en"
        ),
        KnowledgeBaseQA(
            id = "qa_168",
            question = "Is the pharmacy located inside or outside the main building?",
            answer = "Our in-house pharmacy is located inside the main hospital building on the ground floor for easy access.",
            keywords = listOf("outside", "floor", "hospital", "building", "our", "access", "ground", "inhouse", "easy", "inside"),
            category = "pharmacy",
            language = "en"
        ),
        KnowledgeBaseQA(
            id = "qa_169",
            question = "Do you have all prescribed medicines in stock?",
            answer = "We maintain a comprehensive stock of essential medicines; if a specific item is unavailable, we can arrange procurement within 24 hours.",
            keywords = listOf("prescribed", "stock", "maintain", "hours", "within", "unavailable", "procurement", "all", "have", "essential"),
            category = "pharmacy",
            language = "en"
        ),
        KnowledgeBaseQA(
            id = "qa_170",
            question = "Are generic/low-cost medicine options available?",
            answer = "Yes, we offer both branded and affordable generic medicine options as per your doctor's prescription and preference.",
            keywords = listOf("per", "genericlowcost", "branded", "yes", "offer", "affordable", "medicine", "available", "options", "generic"),
            category = "diagnostics",
            language = "en"
        ),
        KnowledgeBaseQA(
            id = "qa_171",
            question = "Do you offer home delivery for medicines?",
            answer = "No, the In-house pharmacy of our hospital do not offer home deliver for medicines.",
            keywords = listOf("hospital", "deliver", "our", "offer", "not", "inhouse", "home", "medicines", "pharmacy", "delivery"),
            category = "pharmacy",
            language = "en"
        ),
        KnowledgeBaseQA(
            id = "qa_172",
            question = "Can I buy over-the-counter medicines without a prescription?",
            answer = "Yes, basic OTC medicines like pain relievers or vitamins are available without prescription; prescription medicines require a valid doctor's prescription.",
            keywords = listOf("overthecounter", "vitamins", "valid", "yes", "pain", "without", "require", "doctors", "buy", "medicines"),
            category = "pharmacy",
            language = "en"
        ),
        KnowledgeBaseQA(
            id = "qa_173",
            question = "Is the pharmacy open 24/7 for emergency prescriptions?",
            answer = "Yes, our pharmacy operates 24/7 to serve emergency and urgent prescription needs at any time.",
            keywords = listOf("247", "serve", "our", "operates", "yes", "pharmacy", "emergency", "needs", "prescription", "prescriptions"),
            category = "hospital_info",
            language = "en"
        ),
        KnowledgeBaseQA(
            id = "qa_174",
            question = "What is the policy for returning unused or unopened medicines?",
            answer = "For safety and regulatory reasons, we cannot accept returns on dispensed medicines; please confirm your prescription details before purchase.",
            keywords = listOf("regulatory", "confirm", "before", "policy", "unused", "returning", "safety", "please", "unopened", "cannot"),
            category = "insurance",
            language = "en"
        ),
        KnowledgeBaseQA(
            id = "qa_175",
            question = "Do you compound or prepare custom medications?",
            answer = "For questions about compounded or custom-prepared medicines, please speak with our pharmacy team or your treating specialist—they can advise on availability and options.",
            keywords = listOf("questions", "compounded", "our", "specialistthey", "availability", "medicines", "treating", "options", "prepare", "customprepared"),
            category = "general",
            language = "en"
        ),
        KnowledgeBaseQA(
            id = "qa_176",
            question = "Do you offer medication counseling or usage instructions?",
            answer = "Yes, we provide medication counseling and clear usage instructions. Our pharmacists are happy to walk you through dosage, timing, and precautions.",
            keywords = listOf("counseling", "provide", "our", "happy", "through", "yes", "offer", "clear", "pharmacists", "usage"),
            category = "general",
            language = "en"
        ),
        KnowledgeBaseQA(
            id = "qa_177",
            question = "Is there a formulary list I can check for available medicines?",
            answer = "Medicine availability can vary. For the most accurate and up-to-date information, please check directly with our pharmacy staff.",
            keywords = listOf("staff", "uptodate", "vary", "formulary", "list", "there", "accurate", "our", "availability", "most"),
            category = "diagnostics",
            language = "en"
        ),
        KnowledgeBaseQA(
            id = "qa_178",
            question = "Do you stock Ayurvedic or herbal medicines?",
            answer = "Yes, our pharmacy stocks a range of Ayurvedic and herbal products. For specific items or recommendations, kindly ask the pharmacy team.",
            keywords = listOf("stocks", "products", "stock", "our", "yes", "ayurvedic", "pharmacy", "items", "specific", "medicines"),
            category = "pharmacy",
            language = "en"
        ),
        KnowledgeBaseQA(
            id = "qa_179",
            question = "Does the ambulance have ventilator or life-support equipment?",
            answer = "Yes, we offer both basic and advanced life-support ambulances equipped with ventilators, oxygen, and emergency monitoring; please specify your requirement when calling +91 76977 44444.",
            keywords = listOf("offer", "calling", "ventilator", "ambulances", "monitoring", "specify", "44444", "ambulance", "ventilators", "please"),
            category = "general",
            language = "en"
        ),
        KnowledgeBaseQA(
            id = "qa_180",
            question = "Can I track the ambulance location after booking?",
            answer = "Real-time tracking is not currently available; however, our dispatch team will provide estimated arrival time and driver contact details upon confirmation.",
            keywords = listOf("our", "estimated", "booking", "available", "upon", "ambulance", "currently", "location", "however", "details"),
            category = "appointments",
            language = "en"
        ),
        KnowledgeBaseQA(
            id = "qa_181",
            question = "Is there a charge for ambulance service, and how much?",
            answer = "Ambulance charges vary based on distance, type (basic/advanced), and equipment; please contact +91 76977 44444 for a case-specific quote.",
            keywords = listOf("quote", "based", "distance", "much", "44444", "ambulance", "please", "how", "there", "service"),
            category = "general",
            language = "en"
        ),
        KnowledgeBaseQA(
            id = "qa_182",
            question = "Can ambulance pick up from my home or another location?",
            answer = "Yes, our ambulances can pick up patients from home, workplaces, or other locations within Burhanpur and nearby areas—just share the exact address when booking.",
            keywords = listOf("our", "another", "other", "booking", "areasjust", "burhanpur", "patients", "ambulances", "ambulance", "from"),
            category = "facilities",
            language = "en"
        ),
        KnowledgeBaseQA(
            id = "qa_183",
            question = "What details should I provide when calling for an ambulance?",
            answer = "Please share patient name, age, condition/symptoms, exact pickup location, contact number, and any special requirements (e.g., ventilator, wheelchair access).",
            keywords = listOf("calling", "ventilator", "any", "should", "age", "ambulance", "number", "please", "location", "details"),
            category = "general",
            language = "en"
        ),
        KnowledgeBaseQA(
            id = "qa_184",
            question = "Can a family member accompany the patient in the ambulance?",
            answer = "Yes, one attendant may accompany the patient in most cases; for advanced life-support ambulances, space may be limited—please confirm with the dispatch team.",
            keywords = listOf("most", "may", "family", "cases", "confirm", "ambulances", "ambulance", "advanced", "team", "member"),
            category = "general",
            language = "en"
        ),
        KnowledgeBaseQA(
            id = "qa_185",
            question = "Can you arrange ambulance for inter-hospital patient transfer?",
            answer = "Yes, we coordinate safe inter-hospital transfers with appropriate medical support; please call +91 76977 44444 with patient details and destination hospital for arrangement.",
            keywords = listOf("safe", "appropriate", "arrangement", "44444", "ambulance", "medical", "please", "destination", "details", "call"),
            category = "general",
            language = "en"
        ),
        KnowledgeBaseQA(
            id = "qa_186",
            question = "Can I see the room before confirming admission?",
            answer = "Yes, you can request a brief room view before confirming admission; our admission desk staff will arrange this based on availability and patient priority.",
            keywords = listOf("our", "desk", "based", "availability", "before", "view", "room", "brief", "request", "this"),
            category = "general",
            language = "en"
        ),
        KnowledgeBaseQA(
            id = "qa_187",
            question = "Is an attendant bed or recliner provided in the room?",
            answer = "Yes, private rooms and designated guest rooms (102, 103, 221, 222) include seating or recliner facilities for attendants; please confirm amenities at admission.",
            keywords = listOf("provided", "facilities", "recliner", "rooms", "222", "102", "attendants", "confirm", "221", "103"),
            category = "general",
            language = "en"
        ),
        KnowledgeBaseQA(
            id = "qa_188",
            question = "Are patient meals included in the room charge?",
            answer = "Basic hygienic meals are included in room charges; special or dietitian-approved diets can be arranged on request, with any additional costs communicated upfront.",
            keywords = listOf("diets", "patient", "charges", "special", "additional", "room", "meals", "communicated", "costs", "included"),
            category = "general",
            language = "en"
        ),
        KnowledgeBaseQA(
            id = "qa_189",
            question = "Is WiFi available in patient rooms for family?",
            answer = "Complimentary WiFi is available in common areas; for in-room connectivity, please check with reception for current availability and access instructions.",
            keywords = listOf("patient", "access", "reception", "availability", "complimentary", "inroom", "current", "please", "check", "areas"),
            category = "diagnostics",
            language = "en"
        ),
        KnowledgeBaseQA(
            id = "qa_190",
            question = "Can I request a room closer to the nursing station?",
            answer = "Yes, you can request a room near the nursing station for added convenience; we'll accommodate based on availability and medical priority.",
            keywords = listOf("nursing", "well", "priority", "near", "yes", "based", "station", "room", "closer", "added"),
            category = "hospital_info",
            language = "en"
        ),
        KnowledgeBaseQA(
            id = "qa_191",
            question = "Do private rooms have attached bathrooms?",
            answer = "Yes, all private room categories (Diamond, Platinum, Golden, Silver, Ruby  Suite) include attached bathrooms with essential amenities for patient comfort and hygiene.",
            keywords = listOf("diamond", "ruby", "suite", "comfort", "rooms", "hygiene", "amenities", "room", "bathrooms", "attached"),
            category = "general",
            language = "en"
        ),
        KnowledgeBaseQA(
            id = "qa_192",
            question = "Can I change to a different room category after admission?",
            answer = "Yes, room changes or upgrades are possible subject to availability, please discuss with the admission or billing desk for options and any additional charges.",
            keywords = listOf("changes", "desk", "additional", "availability", "options", "discuss", "billing", "any", "room", "please"),
            category = "general",
            language = "en"
        ),
        KnowledgeBaseQA(
            id = "qa_193",
            question = "Are there isolation rooms for infectious disease patients?",
            answer = "Yes, we have designated isolation rooms and strict infection control protocols for infectious disease patients to ensure safety for all, please inform staff upon admission.",
            keywords = listOf("rooms", "patients", "upon", "inform", "strict", "ensure", "disease", "safety", "please", "control"),
            category = "general",
            language = "en"
        ),
        KnowledgeBaseQA(
            id = "qa_194",
            question = "Do you provide bed linens, towels, and basic toiletries?",
            answer = "Yes, all rooms are provided with clean bed linens, towels, and basic toiletries; additional items can be requested from nursing staff as needed.",
            keywords = listOf("staff", "bed", "provided", "provide", "nursing", "toiletries", "additional", "linens", "yes", "requested"),
            category = "general",
            language = "en"
        ),
        KnowledgeBaseQA(
            id = "qa_195",
            question = "Where is the nearest restroom or washroom?",
            answer = "Restrooms are available on every floor near the elevator lobbies and waiting areas; please follow the signage or ask any staff member for the closest one.",
            keywords = listOf("restroom", "nearest", "every", "available", "lobbies", "any", "signage", "restrooms", "near", "please"),
            category = "facilities",
            language = "en"
        ),
        KnowledgeBaseQA(
            id = "qa_196",
            question = "Is there a cafeteria, canteen, or food court inside?",
            answer = "Yes, our cafeteria is located on the ground floor near the main lobby, serving hygienic vegetarian and non-vegetarian meals from 7 AM to 9 PM.",
            keywords = listOf("our", "food", "canteen", "near", "from", "ground", "vegetarian", "nonvegetarian", "floor", "there"),
            category = "general",
            language = "en"
        ),
        KnowledgeBaseQA(
            id = "qa_197",
            question = "Where can I find safe drinking water?",
            answer = "Safe, filtered drinking water stations are available on all floors near waiting areas and nursing stations.",
            keywords = listOf("filtered", "safe", "nursing", "near", "drinking", "floors", "all", "areas", "available", "waiting"),
            category = "facilities",
            language = "en"
        ),
        KnowledgeBaseQA(
            id = "qa_198",
            question = "Is there an ATM or cash withdrawal facility on campus?",
            answer = "Yes, an ATM is located for easy cash withdrawal, additional digital payment options are accepted throughout the hospital.",
            keywords = listOf("payment", "hospital", "there", "additional", "campus", "yes", "withdrawal", "facility", "easy", "options"),
            category = "general",
            language = "en"
        ),
        KnowledgeBaseQA(
            id = "qa_199",
            question = "Where can I get a wheelchair or stretcher assistance?",
            answer = "Wheelchairs and stretchers are available free of charge at the reception desk, simply request assistance and our support staff will help you promptly.",
            keywords = listOf("get", "desk", "our", "available", "simply", "request", "where", "help", "wheelchairs", "stretchers"),
            category = "facilities",
            language = "en"
        ),
        KnowledgeBaseQA(
            id = "qa_200",
            question = "Where can I buy patient care items like masks, gloves, or sanitizers?",
            answer = "Our 24/7 in-house pharmacy on the ground floor stocks masks, gloves, sanitizers, and other essential patient care items.",
            keywords = listOf("247", "floor", "patient", "gloves", "sanitizers", "our", "stocks", "other", "items", "masks"),
            category = "facilities",
            language = "en"
        ),
        KnowledgeBaseQA(
            id = "qa_201",
            question = "Is there a patient care coordinator or helpdesk I can contact?",
            answer = "Yes, our Patient Care Desk at reception and the front desk team (+91 76977 44444) are available to assist with coordination, queries, and support throughout your hospital visit.",
            keywords = listOf("queries", "helpdesk", "our", "desk", "care", "available", "throughout", "coordinator", "44444", "coordination"),
            category = "general",
            language = "en"
        ),
        KnowledgeBaseQA(
            id = "qa_202",
            question = "How do I report a concern about staff behavior or service quality?",
            answer = "You can share feedback or concerns directly with the reception desk, call +91 76977 44444, or use the \"Contact Us\" form on our website; all feedback is reviewed promptly by our patient care team.",
            keywords = listOf("desk", "our", "feedback", "care", "44444", "concern", "team", "how", "call", "use"),
            category = "general",
            language = "en"
        ),
        KnowledgeBaseQA(
            id = "qa_203",
            question = "Can I seek a second opinion from another specialist in the hospital?",
            answer = "Yes, you may request a second opinion from another in-house specialist; please discuss with your treating doctor or contact reception to coordinate the consultation.",
            keywords = listOf("another", "may", "treating", "consultation", "doctor", "discuss", "second", "from", "specialist", "please"),
            category = "departments",
            language = "en"
        ),
        KnowledgeBaseQA(
            id = "qa_204",
            question = "How can I receive regular updates on my patient's condition without being at bedside?",
            answer = "You can request periodic updates via phone or WhatsApp by providing your contact details to the nursing station; for ICU patients, updates are shared during designated visiting hours or as clinically appropriate.",
            keywords = listOf("nursing", "station", "appropriate", "condition", "receive", "patients", "shared", "clinically", "regular", "without"),
            category = "general",
            language = "en"
        ),
        KnowledgeBaseQA(
            id = "qa_205",
            question = "Is there a waiting lounge with seating, charging points, or reading material?",
            answer = "Yes, we have comfortable waiting areas near OPD and main lobby with seating, drinking water, and basic amenities; charging points and reading material availability may vary—please ask reception for current facilities.",
            keywords = listOf("facilities", "availability", "comfortable", "may", "water", "amenities", "near", "current", "material", "waiting"),
            category = "general",
            language = "en"
        ),
        KnowledgeBaseQA(
            id = "qa_206",
            question = "Where is the pharmacy?",
            answer = "The pharmacy is located on the Ground Floor, directly in front of the reception desk.",
            keywords = listOf("floor", "desk", "reception", "ground", "front", "directly", "located", "pharmacy", "where"),
            category = "facilities",
            language = "en"
        ),
        KnowledgeBaseQA(
            id = "qa_207",
            question = "Where is the pathology lab?",
            answer = "The pathology department is situated to the left of the reception desk—just follow the signs from there.",
            keywords = listOf("there", "signs", "left", "reception", "from", "department", "deskjust", "situated", "follow", "pathology"),
            category = "facilities",
            language = "en"
        ),
        KnowledgeBaseQA(
            id = "qa_208",
            question = "Where is the blood collection center?",
            answer = "The blood collection center is located in the hospital Basement.",
            keywords = listOf("basement", "center", "hospital", "located", "blood", "collection", "where"),
            category = "facilities",
            language = "en"
        ),
        KnowledgeBaseQA(
            id = "qa_209",
            question = "Where is the X-ray room?",
            answer = "The X-ray room and most scans are on the Ground Floor. For MRI and CT scans, please proceed to the Basement floor.",
            keywords = listOf("floor", "basement", "xray", "room", "ground", "most", "please", "mri", "where", "scans"),
            category = "facilities",
            language = "en"
        ),
        KnowledgeBaseQA(
            id = "qa_210",
            question = "Where is the MRI scan center?",
            answer = "The MRI scan center is located on the Basement floor.",
            keywords = listOf("basement", "floor", "center", "scan", "mri", "located", "where"),
            category = "facilities",
            language = "en"
        ),
        KnowledgeBaseQA(
            id = "qa_211",
            question = "Where is the CT scan room?",
            answer = "The CT scan room is also on the Basement floor, alongside other advanced imaging services.",
            keywords = listOf("basement", "floor", "services", "other", "room", "scan", "also", "imaging", "alongside", "advanced"),
            category = "facilities",
            language = "en"
        ),
        KnowledgeBaseQA(
            id = "qa_212",
            question = "Where is the ultrasound department?",
            answer = "Ultrasound services are typically available on the Ground Floor. For exact location, please check with reception or follow directional signs.",
            keywords = listOf("floor", "exact", "services", "directional", "signs", "reception", "typically", "department", "ground", "please"),
            category = "departments",
            language = "en"
        ),
        KnowledgeBaseQA(
            id = "qa_213",
            question = "Where is the radiology department?",
            answer = "Most radiology services, including X-ray, are on the Ground Floor; advanced imaging like MRI/CT is on the Basement. Reception can guide you to the right counter.",
            keywords = listOf("floor", "basement", "services", "xray", "mrict", "including", "reception", "department", "most", "ground"),
            category = "departments",
            language = "en"
        ),
        KnowledgeBaseQA(
            id = "qa_214",
            question = "Where is the diagnostic center?",
            answer = "Our main diagnostic services are spread across Ground and Basement floors. For a specific test or department, reception can provide quick directions.",
            keywords = listOf("basement", "services", "center", "provide", "our", "spread", "reception", "diagnostic", "ground", "floors"),
            category = "facilities",
            language = "en"
        ),
        KnowledgeBaseQA(
            id = "qa_215",
            question = "Where is the emergency room?",
            answer = "The Emergency Room is clearly signposted from the main hospital entrance. If you're unsure, please ask reception or any staff member—they'll guide you right away.",
            keywords = listOf("main", "any", "room", "from", "right", "please", "youre", "clearly", "signposted", "where"),
            category = "hospital_info",
            language = "en"
        ),
        KnowledgeBaseQA(
            id = "qa_216",
            question = "Where is the ICU?",
            answer = "The ICUs (including KICU, MICU, and NICU) are located on the upper floors. For exact location based on patient need, please check with reception or follow emergency signage.",
            keywords = listOf("icus", "based", "floors", "upper", "nicu", "micu", "signage", "please", "location", "where"),
            category = "facilities",
            language = "en"
        ),
        KnowledgeBaseQA(
            id = "qa_217",
            question = "Where is the cardiology department?",
            answer = "Cardiology services are available on the Ground Floor. Reception can guide you to the right counter or consultation room.",
            keywords = listOf("floor", "services", "cardiology", "reception", "room", "department", "ground", "right", "guide", "available"),
            category = "departments",
            language = "en"
        ),
        KnowledgeBaseQA(
            id = "qa_218",
            question = "Where is the pediatrics ward?",
            answer = "Pediatric care is provided on the Ground Floor. For ward-specific directions, kindly ask reception or look for pediatric signage.",
            keywords = listOf("floor", "provided", "look", "signage", "wardspecific", "reception", "ground", "ward", "care", "kindly"),
            category = "facilities",
            language = "en"
        ),
        KnowledgeBaseQA(
            id = "qa_219",
            question = "Where is the maternity ward?",
            answer = "The maternity ward is located on the Ground Floor. Reception can assist with visitation guidelines and room directions.",
            keywords = listOf("floor", "reception", "visitation", "directions", "room", "ground", "maternity", "ward", "guidelines", "located"),
            category = "facilities",
            language = "en"
        ),
        KnowledgeBaseQA(
            id = "qa_220",
            question = "Where is the labor room?",
            answer = "Labor and delivery services are coordinated through the Operation Theater (OT) area. For immediate assistance, please alert reception or any nursing staff.",
            keywords = listOf("staff", "services", "operation", "immediate", "nursing", "through", "reception", "room", "assistance", "alert"),
            category = "facilities",
            language = "en"
        ),
        KnowledgeBaseQA(
            id = "qa_221",
            question = "Where is the NICU?",
            answer = "The Neonatal ICU (NICU) is located on the 3rd floor. Access is coordinated through medical staff—please check with reception for visitor protocols and directions.",
            keywords = listOf("neonatal", "floor", "protocols", "icu", "access", "through", "reception", "directions", "where", "medical"),
            category = "facilities",
            language = "en"
        ),
        KnowledgeBaseQA(
            id = "qa_222",
            question = "Where is the neurology department?",
            answer = "Neurology services are located on the Ground Floor. For appointment check-in or directions, please ask reception.",
            keywords = listOf("floor", "services", "reception", "department", "ground", "neurology", "checkin", "please", "directions", "located"),
            category = "departments",
            language = "en"
        ),
        KnowledgeBaseQA(
            id = "qa_223",
            question = "Where is the dental department?",
            answer = "Dental care is provided on the Ground Floor. Reception can help you locate the dental clinic and confirm doctor availability.",
            keywords = listOf("floor", "help", "provided", "confirm", "dental", "reception", "availability", "department", "ground", "clinic"),
            category = "departments",
            language = "en"
        ),
        KnowledgeBaseQA(
            id = "qa_224",
            question = "Where is the surgery department?",
            answer = "Surgical consultations and pre-op care are coordinated on the Ground Floor. For surgeon availability or appointment check-in, please check with reception.",
            keywords = listOf("floor", "surgeon", "preop", "consultations", "availability", "reception", "department", "ground", "surgery", "surgical"),
            category = "departments",
            language = "en"
        ),
        KnowledgeBaseQA(
            id = "qa_225",
            question = "Where is the operation theater?",
            answer = "Our hospital has 6 operation theaters located across different floors. Since surgeries are scheduled based on urgency and specialty, reception can guide you to the correct OT or provide updates on your procedure location.",
            keywords = listOf("our", "procedure", "based", "correct", "floors", "specialty", "urgency", "theaters", "theater", "since"),
            category = "facilities",
            language = "en"
        ),
        KnowledgeBaseQA(
            id = "qa_226",
            question = "Where is the cafeteria?",
            answer = "All dining options are located beside the back gate of the hospital. Reception can provide quick directions if needed.",
            keywords = listOf("dining", "quick", "beside", "gate", "hospital", "provide", "cafeteria", "reception", "back", "all"),
            category = "facilities",
            language = "en"
        ),
        KnowledgeBaseQA(
            id = "qa_227",
            question = "Where is the vending machine?",
            answer = "We don't have vending machines on campus, but snacks and meals are available at the canteen beside the back gate.",
            keywords = listOf("but", "machine", "beside", "machines", "gate", "campus", "canteen", "back", "meals", "dont"),
            category = "facilities",
            language = "en"
        ),
        KnowledgeBaseQA(
            id = "qa_228",
            question = "Where is the restaurant?",
            answer = "All dining options are located beside the back gate of the hospital. Reception can provide quick directions if needed.",
            keywords = listOf("dining", "quick", "beside", "gate", "hospital", "provide", "reception", "restaurant", "back", "all"),
            category = "facilities",
            language = "en"
        ),
        KnowledgeBaseQA(
            id = "qa_229",
            question = "Where is the restroom?",
            answer = "Restrooms are available on every floor, typically near waiting areas and elevators. If you need directions to the nearest one, please ask reception or any staff member",
            keywords = listOf("restroom", "every", "nearest", "available", "directions", "any", "elevators", "restrooms", "near", "please"),
            category = "facilities",
            language = "en"
        ),
        KnowledgeBaseQA(
            id = "qa_230",
            question = "Where is the waiting area?",
            answer = "Comfortable seating areas are available on the Ground Floor, near the main entrance and department cabins.",
            keywords = listOf("floor", "near", "ground", "entrance", "department", "comfortable", "seating", "areas", "cabins", "available"),
            category = "facilities",
            language = "en"
        ),
        KnowledgeBaseQA(
            id = "qa_231",
            question = "Where is the reception?",
            answer = "The main reception desk is located on the Ground Floor, right at the hospital entrance—your first stop for any assistance.",
            keywords = listOf("floor", "entranceyour", "hospital", "desk", "reception", "ground", "right", "assistance", "stop", "first"),
            category = "facilities",
            language = "en"
        ),
        KnowledgeBaseQA(
            id = "qa_232",
            question = "Where is the help desk?",
            answer = "A dedicated help desk is available on the Ground Floor to guide visitors, answer queries, and provide support.",
            keywords = listOf("floor", "help", "queries", "provide", "desk", "answer", "ground", "guide", "dedicated", "support"),
            category = "facilities",
            language = "en"
        ),
        KnowledgeBaseQA(
            id = "qa_233",
            question = "Where is the registration counter?",
            answer = "Patient registration is handled at the counter on the Ground Floor, adjacent to the main reception area.",
            keywords = listOf("floor", "patient", "handled", "reception", "adjacent", "ground", "registration", "counter", "where", "main"),
            category = "appointments",
            language = "en"
        ),
        KnowledgeBaseQA(
            id = "qa_234",
            question = "Where is the billing counter?",
            answer = "Billing and payment services are available at the dedicated counter on the Ground Floor, near the main reception.",
            keywords = listOf("floor", "services", "payment", "main", "near", "reception", "ground", "dedicated", "available", "counter"),
            category = "facilities",
            language = "en"
        ),
        KnowledgeBaseQA(
            id = "qa_235",
            question = "Where is the insurance desk?",
            answer = "The insurance desk is located on the Ground Floor. For claim assistance, documentation, or policy queries, please visit the desk directly or check with reception for exact directions.",
            keywords = listOf("floor", "queries", "documentation", "exact", "desk", "reception", "policy", "ground", "claim", "assistance"),
            category = "insurance",
            language = "en"
        ),
        KnowledgeBaseQA(
            id = "qa_236",
            question = "Where is the appointment counter?",
            answer = "Appointments can be booked directly at the reception desk on the Ground Floor—or via our website/WhatsApp for convenience.",
            keywords = listOf("booked", "appointment", "appointments", "desk", "via", "reception", "our", "ground", "websitewhatsapp", "directly"),
            category = "appointments",
            language = "en"
        ),
        KnowledgeBaseQA(
            id = "qa_237",
            question = "Where is the parking lot?",
            answer = "General parking is available both in front of the main entrance and near the back gate. Follow signage or ask security for guidance.",
            keywords = listOf("both", "lot", "gate", "signage", "security", "near", "back", "guidance", "entrance", "front"),
            category = "facilities",
            language = "en"
        ),
        KnowledgeBaseQA(
            id = "qa_238",
            question = "Where is the visitor parking?",
            answer = "Dedicated visitor parking is provided at both the front (main entrance) and back gate areas on the Ground Level.",
            keywords = listOf("provided", "both", "gate", "back", "ground", "entrance", "front", "level", "visitor", "dedicated"),
            category = "facilities",
            language = "en"
        ),
        KnowledgeBaseQA(
            id = "qa_239",
            question = "Where is the ambulance bay?",
            answer = "The ambulance bay is located directly in front of the main entrance for quick emergency access. Staff are always ready to assist upon arrival.",
            keywords = listOf("staff", "quick", "access", "upon", "ambulance", "arrival", "bay", "entrance", "front", "directly"),
            category = "facilities",
            language = "en"
        ),
        KnowledgeBaseQA(
            id = "qa_240",
            question = "Where is the lift?",
            answer = "Lifts are located near the staircase, just beside the reception desk on the Ground Floor.",
            keywords = listOf("floor", "beside", "lifts", "desk", "near", "reception", "ground", "staircase", "just", "located"),
            category = "facilities",
            language = "en"
        ),
        KnowledgeBaseQA(
            id = "qa_241",
            question = "Where is the staircase?",
            answer = "Staircases are conveniently situated near the reception desk on the Ground Floor for easy access to all floors.",
            keywords = listOf("floor", "desk", "access", "near", "reception", "conveniently", "ground", "floors", "staircase", "all"),
            category = "facilities",
            language = "en"
        ),
        KnowledgeBaseQA(
            id = "qa_242",
            question = "Where is the wheelchair access?",
            answer = "Wheelchairs are available right at the main entrance—just inform any staff member or security for immediate assistance.",
            keywords = listOf("staff", "immediate", "entrancejust", "access", "wheelchair", "security", "inform", "wheelchairs", "right", "assistance"),
            category = "facilities",
            language = "en"
        ),
        KnowledgeBaseQA(
            id = "qa_243",
            question = "Where is the ramp?",
            answer = "A ramp is available for easy access. Please ask any staff member for guidance or support while using it.",
            keywords = listOf("staff", "using", "access", "while", "guidance", "any", "ramp", "please", "support", "easy"),
            category = "facilities",
            language = "en"
        ),
        KnowledgeBaseQA(
            id = "qa_244",
            question = "Where is the ATM?",
            answer = "An ATM is available in front of the back entrance, beside Nacl Restaurant.",
            keywords = listOf("nacl", "beside", "where", "restaurant", "entrance", "front", "available", "back", "atm"),
            category = "facilities",
            language = "en"
        ),
        KnowledgeBaseQA(
            id = "qa_245",
            question = "Where is the locker room?",
            answer = "Locker facilities are available in the Basement. Please check with reception for access instructions.",
            keywords = listOf("basement", "facilities", "access", "reception", "room", "locker", "please", "check", "available", "where"),
            category = "facilities",
            language = "en"
        ),
        KnowledgeBaseQA(
            id = "qa_246",
            question = "Where is the children's play area?",
            answer = "A small play area for children is located within the OPD area on the Ground Floor.",
            keywords = listOf("floor", "play", "within", "ground", "children", "childrens", "located", "where", "opd", "area"),
            category = "facilities",
            language = "en"
        ),
        KnowledgeBaseQA(
            id = "qa_247",
            question = "Where is the family lounge?",
            answer = "While we don't have a dedicated family lounge, we offer a VIP lounge for added comfort. Please ask reception for access details.",
            keywords = listOf("access", "while", "reception", "offer", "added", "dont", "have", "lounge", "dedicated", "comfort"),
            category = "facilities",
            language = "en"
        ),
        KnowledgeBaseQA(
            id = "qa_248",
            question = "Where is the smoking area?",
            answer = "Smoking is not permitted anywhere on hospital premises—and for good reason: smoking is harmful to your health and can affect healing, breathing, and overall wellness. We encourage a smoke-free environment to support everyone's recovery and well-being.",
            keywords = listOf("smoking", "area", "where", "premises", "permitted", "smoke-free", "not allowed", "forbidden", "prohibited"),
            category = "facilities",
            language = "en"
        ),
        KnowledgeBaseQA(
            id = "qa_249",
            question = "Where is the admin office?",
            answer = "For administrative inquiries, please check with reception—they'll direct you to the appropriate office or staff member.",
            keywords = listOf("staff", "inquiries", "receptiontheyll", "admin", "appropriate", "please", "check", "direct", "office", "administrative"),
            category = "facilities",
            language = "en"
        ),
        KnowledgeBaseQA(
            id = "qa_250",
            question = "Where is the HR department?",
            answer = "HR-related matters are handled internally. For visitor access or queries, kindly coordinate through reception.",
            keywords = listOf("queries", "hrrelated", "access", "handled", "through", "reception", "coordinate", "department", "internally", "visitor"),
            category = "departments",
            language = "en"
        ),
        KnowledgeBaseQA(
            id = "qa_251",
            question = "Where is the medical records department?",
            answer = "The MRD is located in the Basement. For records requests or assistance, please visit during working hours or check with reception first.",
            keywords = listOf("basement", "records", "hours", "during", "reception", "department", "medical", "assistance", "please", "visit"),
            category = "departments",
            language = "en"
        ),
        KnowledgeBaseQA(
            id = "qa_252",
            question = "Where is Discharge Counter ?",
            answer = "For discharge, please proceed to the Billing Counter on the Ground Floor. Your doctor will coordinate the medical clearance, and reception can guide you through the final steps.",
            keywords = listOf("floor", "will", "clearance", "through", "reception", "discharge", "coordinate", "ground", "medical", "final"),
            category = "facilities",
            language = "en"
        ),
        KnowledgeBaseQA(
            id = "qa_285",
            question = "Where is the doctor's consultation room?",
            answer = "Consultation rooms are assigned by specialty and schedule. For your doctor's exact location, please check with reception—they'll guide you right away.",
            keywords = listOf("exact", "assigned", "schedule", "room", "receptiontheyll", "specialty", "right", "guide", "please", "check"),
            category = "departments",
            language = "en"
        ),
        KnowledgeBaseQA(
            id = "qa_286",
            question = "Where is the nurse station?",
            answer = "Nurse stations are available on every floor, within each ward and department. Look for the nursing desk near your patient's room or ask any staff member.",
            keywords = listOf("nursing", "desk", "nurse", "station", "department", "every", "each", "available", "any", "patients"),
            category = "facilities",
            language = "en"
        ),
        KnowledgeBaseQA(
            id = "qa_287",
            question = "Where is the staff room?",
            answer = "The staff room is located on the 2nd Floor. This area is for hospital personnel only—visitors please check with reception for assistance.",
            keywords = listOf("staff", "floor", "hospital", "onlyvisitors", "reception", "room", "personnel", "assistance", "2nd", "please"),
            category = "facilities",
            language = "en"
        ),
        KnowledgeBaseQA(
            id = "qa_288",
            question = "Where is the ground floor pharmacy?",
            answer = "The pharmacy on the Ground Floor is located directly in front of the billing/reception desk for quick access.",
            keywords = listOf("floor", "quick", "desk", "access", "billingreception", "ground", "front", "directly", "located", "pharmacy"),
            category = "facilities",
            language = "en"
        ),
        KnowledgeBaseQA(
            id = "qa_289",
            question = "Where is the first floor OPD?",
            answer = "OPD services span multiple floors. For the exact location of your specialist's OPD, please confirm with reception at check-in.",
            keywords = listOf("floor", "exact", "services", "confirm", "span", "reception", "floors", "specialists", "first", "please"),
            category = "facilities",
            language = "en"
        ),
        KnowledgeBaseQA(
            id = "qa_290",
            question = "Where is the second floor ICU?",
            answer = "Specialized ICUs (including KICU) are located on the 2nd Floor. Access is coordinated through medical staff—please check with reception for visitor guidelines.",
            keywords = listOf("icus", "specialized", "second", "through", "medical", "2nd", "visitor", "guidelines", "where", "floor"),
            category = "facilities",
            language = "en"
        ),
        KnowledgeBaseQA(
            id = "qa_291",
            question = "Where is the blood bank?",
            answer = "The blood bank is located in the Basement. For donations, requests, or emergencies, please contact reception for immediate coordination.",
            keywords = listOf("basement", "immediate", "bank", "emergencies", "reception", "donations", "located", "contact", "please", "coordination"),
            category = "facilities",
            language = "en"
        ),
        KnowledgeBaseQA(
            id = "qa_292",
            question = "Where is the security desk?",
            answer = "Security personnel are stationed throughout the campus. If you need assistance, ask any staff member—they'll quickly connect you with security.",
            keywords = listOf("staff", "security", "desk", "campus", "membertheyll", "personnel", "quickly", "assistance", "any", "need"),
            category = "facilities",
            language = "en"
        ),
        KnowledgeBaseQA(
            id = "qa_293",
            question = "Where is the taxi stand or cab pickup point?",
            answer = "Cab pickup and drop-off is conveniently located near the main entrance for easy access.",
            keywords = listOf("cab", "access", "point", "near", "conveniently", "dropoff", "entrance", "pickup", "easy", "taxi"),
            category = "facilities",
            language = "en"
        ),
        KnowledgeBaseQA(
            id = "qa_294",
            question = "Where is the visitor locker facility?",
            answer = "Visitor lockers are available in the Basement. Please check with reception for access instructions and usage guidelines.",
            keywords = listOf("basement", "guidelines", "access", "reception", "visitor", "locker", "please", "facility", "available", "lockers"),
            category = "facilities",
            language = "en"
        ),
        // ==================== MEDICAL KNOWLEDGE BASE ====================
        // Basic medical information - patients should see a doctor
        KnowledgeBaseQA(
            id = "qa_medical_001",
            question = "What should I do if I have fever?",
            answer = "Fever can be a sign of infection or other health conditions. You should consult a doctor at All Is Well Hospital for proper diagnosis and treatment. Our OPD services are available 24/7. Call +91 76977 44444 for appointment or urgent care.",
            keywords = listOf("fever", "temperature", "high", "ill", "sick", "symptom", "consult", "doctor"),
            category = "medical_info",
            language = "en"
        ),
        KnowledgeBaseQA(
            id = "qa_medical_001_hi",
            question = "अगर मुझे बुखार है तो क्या करूँ?",
            answer = "बुखार संक्रमण या अन्य स्वास्थ्य समस्याओं का संकेत हो सकता है। आपको ऑल इज़ वेल हॉस्पिटल में एक डॉक्टर से मिलना चाहिए। हमारी OPD सेवाएं 24/7 उपलब्ध हैं। अपॉइंटमेंट या आपातकालीन देखभाल के लिए +91 76977 44444 पर कॉल करें।",
            keywords = listOf("बुखार", "तापमान", "बीमार", "संक्रमण", "डॉक्टर", "दवा", "उपचार", "परामर्श"),
            category = "medical_info",
            language = "hi"
        ),
        KnowledgeBaseQA(
            id = "qa_medical_002",
            question = "What should I do if I have stomach pain?",
            answer = "Stomach pain can have various causes - it may be due to indigestion, infection, or other conditions. You should see a doctor for proper evaluation. All Is Well Hospital has experienced gastroenterologists and general physicians. Contact us at +91 76977 44444 for consultation.",
            keywords = listOf("stomach", "pain", "abdominal", "belly", "indigestion", "gastro", "digestive", "consult"),
            category = "medical_info",
            language = "en"
        ),
        KnowledgeBaseQA(
            id = "qa_medical_002_hi",
            question = "अगर मुझे पेट में दर्द है तो क्या करूँ?",
            answer = "पेट में दर्द में कई कारण हो सकते हैं - यह अपच, संक्रमण, या अन्य समस्याओं के कारण हो सकता है। आपको सही मूल्यांकन के लिए डॉक्टर से मिलना चाहिए। ऑल इज़ वेल हॉस्पिटल में अनुभवी जठरांत्र रोग विशेषज्ञ और सामान्य चिकित्सक हैं। परामर्श के लिए +91 76977 44444 पर संपर्क करें।",
            keywords = listOf("पेट", "दर्द", "पेटदर्द", "अपच", "पाचन", "गैस्ट्रो", "डॉक्टर", "परामर्श"),
            category = "medical_info",
            language = "hi"
        ),
        KnowledgeBaseQA(
            id = "qa_medical_003",
            question = "What should I do if I have a cough or cold?",
            answer = "Cough and cold are usually viral infections but can sometimes indicate more serious conditions. Rest, stay hydrated, and see a doctor if symptoms persist beyond a few days. All Is Well Hospital's respiratory specialists can help. Contact +91 76977 44444.",
            keywords = listOf("cough", "cold", "respiratory", "throat", "congestion", "sneeze", "flu", "consult"),
            category = "medical_info",
            language = "en"
        ),
        KnowledgeBaseQA(
            id = "qa_medical_003_hi",
            question = "अगर मुझे खांसी या सर्दी है तो क्या करूँ?",
            answer = "खांसी और सर्दी आमतौर पर वायरल संक्रमण होते हैं लेकिन कभी-कभी गंभीर समस्याओं का संकेत हो सकते हैं। आराम करें, हाइड्रेटेड रहें, और अगर लक्षण कुछ दिनों से अधिक बने रहें तो डॉक्टर से मिलें। ऑल इज़ वेल हॉस्पिटल के श्वसन विशेषज्ञ मदद कर सकते हैं। +91 76977 44444 पर संपर्क करें।",
            keywords = listOf("खांसी", "सर्दी", "फ्लू", "सांस", "गले", "बुखार", "डॉक्टर", "परामर्श"),
            category = "medical_info",
            language = "hi"
        ),
        KnowledgeBaseQA(
            id = "qa_medical_004",
            question = "What should I do if I have body pain or aches?",
            answer = "Body pain or aches can result from muscle strain, stress, infection, or other conditions. For persistent pain, consult a doctor for evaluation. All Is Well Hospital has specialists in orthopedics, rheumatology, and neurology. Call +91 76977 44444 for medical advice.",
            keywords = listOf("pain", "ache", "muscle", "body", "soreness", "joint", "stiffness", "consult"),
            category = "medical_info",
            language = "en"
        ),
        KnowledgeBaseQA(
            id = "qa_medical_004_hi",
            question = "अगर मुझे शरीर में दर्द या पीड़ा है तो क्या करूँ?",
            answer = "शरीर में दर्द मांसपेशियों में खिंचाव, तनाव, संक्रमण, या अन्य स्थितियों के कारण हो सकता है। लगातार दर्द के लिए, मूल्यांकन के लिए डॉक्टर से मिलें। ऑल इज़ वेल हॉस्पिटल में ऑर्थोपेडिक्स, गठिया रोग, और न्यूरोलॉजी विशेषज्ञ हैं। चिकित्सा सलाह के लिए +91 76977 44444 पर कॉल करें।",
            keywords = listOf("दर्द", "पीड़ा", "शरीर", "मांसपेशी", "जोड़", "अकड़न", "डॉक्टर", "परामर्श"),
            category = "medical_info",
            language = "hi"
        ),
        KnowledgeBaseQA(
            id = "qa_medical_005",
            question = "What should I do if I feel dizzy or lightheaded?",
            answer = "Dizziness or lightheadedness can be caused by dehydration, low blood pressure, inner ear issues, or other conditions. Sit down, rest, and drink water. If symptoms persist or are severe, visit the hospital immediately. Call +91 76977 44444 for urgent assistance.",
            keywords = listOf("dizzy", "lightheaded", "vertigo", "spinning", "faint", "balance", "consult", "urgent"),
            category = "medical_info",
            language = "en"
        ),
        KnowledgeBaseQA(
            id = "qa_medical_005_hi",
            question = "अगर मुझे चक्कर आ रहे हैं या सिर हल्का महसूस हो रहा है तो क्या करूँ?",
            answer = "चक्कर या सिर हल्का महसूस होना निर्जलीकरण, कम रक्तचाप, आंतरिक कान की समस्याएं, या अन्य स्थितियों के कारण हो सकता है। बैठ जाएं, आराम करें, और पानी पिएं। अगर लक्षण बने रहें या गंभीर हों तो तुरंत हॉस्पिटल जाएं। तुरंत सहायता के लिए +91 76977 44444 पर कॉल करें।",
            keywords = listOf("चक्कर", "सिर", "चक्कर खाना", "बेहोश", "संतुलन", "कमजोरी", "डॉक्टर", "आपातकालीन"),
            category = "medical_info",
            language = "hi"
        ),
        KnowledgeBaseQA(
            id = "qa_medical_006",
            question = "When should I see a doctor immediately (emergency)?",
            answer = "Seek immediate medical care if you experience: chest pain, difficulty breathing, severe bleeding, unconsciousness, severe allergic reaction, poisoning, severe burns, or signs of stroke. Call emergency: +91 76977 44444. Do not delay - go to the nearest hospital ER.",
            keywords = listOf("emergency", "urgent", "immediately", "hospital", "serious", "critical", "danger", "ambulance"),
            category = "medical_info",
            language = "en"
        ),
        KnowledgeBaseQA(
            id = "qa_medical_006_hi",
            question = "मुझे तुरंत डॉक्टर से मिलना कब चाहिए (आपातकालीन)?",
            answer = "अगर आपको ये लक्षण हों तो तुरंत चिकित्सा सुविधा लें: छाती में दर्द, सांस लेने में कठिनाई, गंभीर रक्तस्राव, बेहोशी, गंभीर एलर्जी प्रतिक्रिया, जहर, गंभीर जलन, या स्ट्रोक के संकेत। आपातकालीन कॉल करें: +91 76977 44444। देरी न करें - निकटतम अस्पताल के आपातकालीन विभाग में जाएं।",
            keywords = listOf("आपातकालीन", "तुरंत", "गंभीर", "ख़तरा", "चिकित्सा", "एम्बुलेंस", "124", "संकट"),
            category = "medical_info",
            language = "hi"
        ),
        KnowledgeBaseQA(
            id = "qa_medical_007",
            question = "What is basic first aid for a minor injury?",
            answer = "For minor cuts or scrapes: Clean the wound with clean water, apply antiseptic cream, and cover with a sterile bandage. For minor burns: Cool with cold water immediately (not ice). For sprains: Rest, ice, compress, and elevate. See a doctor if pain, swelling, or infection develops. Call +91 76977 44444.",
            keywords = listOf("injury", "first", "aid", "wound", "burn", "sprain", "cut", "bleed"),
            category = "medical_info",
            language = "en"
        ),
        KnowledgeBaseQA(
            id = "qa_medical_007_hi",
            question = "मामूली चोट के लिए बुनियादी प्राथमिक चिकित्सा क्या है?",
            answer = "छोटे कट या खरोंच के लिए: घाव को साफ पानी से धोएं, एंटीसेप्टिक क्रीम लगाएं, और बाँझ पट्टी से ढकें। हल्की जलन के लिए: तुरंत ठंडे पानी से ठंडा करें (बर्फ नहीं)। मोच के लिए: आराम, बर्फ, संपीड़न, और ऊंचाई रखें। अगर दर्द, सूजन, या संक्रमण हो तो डॉक्टर से मिलें। +91 76977 44444 पर कॉल करें।",
            keywords = listOf("चोट", "घाव", "पहली सहायता", "जलन", "खून", "मोच", "कट", "खरोंच"),
            category = "medical_info",
            language = "hi"
        ),
        // Time and location information
        KnowledgeBaseQA(
            id = "qa_info_time_001",
            question = "What is the current date and time?",
            answer = "Please check your device for the current date and time. All Is Well Hospital operates: Mon-Fri 8:00 AM - 8:00 PM, Sat-Sun 9:00 AM - 6:00 PM. Emergency and OPD services are available 24/7.",
            keywords = listOf("date", "time", "current", "today", "hour", "when", "schedule", "operating"),
            category = "hospital_info",
            language = "en"
        ),
        KnowledgeBaseQA(
            id = "qa_info_time_001_hi",
            question = "वर्तमान तारीख और समय क्या है?",
            answer = "कृपया अपने डिवाइस पर वर्तमान तारीख और समय देखें। ऑल इज़ वेल हॉस्पिटल का संचालन: सोमवार-शुक्रवार 8:00 AM - 8:00 PM, शनिवार-रविवार 9:00 AM - 6:00 PM। आपातकालीन और OPD सेवाएं 24/7 उपलब्ध हैं।",
            keywords = listOf("तारीख", "समय", "वर्तमान", "आज", "घंटा", "कब", "अनुसूची", "संचालन"),
            category = "hospital_info",
            language = "hi"
        ),
        KnowledgeBaseQA(
            id = "qa_info_location_001",
            question = "What is the hospital location and address?",
            answer = "All Is Well Hospital is located near Macro Vision Academy, Burhanpur, Madhya Pradesh 450331. For directions and navigation, contact reception or use our navigation system. We can guide you to any department.",
            keywords = listOf("location", "address", "where", "directions", "burhanpur", "near", "macro", "academy"),
            category = "facilities",
            language = "en"
        ),
        KnowledgeBaseQA(
            id = "qa_info_location_001_hi",
            question = "अस्पताल का स्थान और पता क्या है?",
            answer = "ऑल इज़ वेल हॉस्पिटल मैक्रो विजन अकादमी के पास, बुरहानपुर, मध्य प्रदेश 450331 में स्थित है। दिशा और नेविगेशन के लिए रिसेप्शन से संपर्क करें या हमारी नेविगेशन सिस्टम का उपयोग करें। हम आपको किसी भी विभाग तक गाइड कर सकते हैं।",
            keywords = listOf("स्थान", "पता", "कहां", "दिशा", "बुरहानपुर", "पास", "मैक्रो", "अकादमी"),
            category = "facilities",
            language = "hi"
        ),

        KnowledgeBaseQA(
            id = "qa_4_hi",
            question = "योग उपचार के कौन से प्रकार उपलब्ध हैं?",
            answer = "ऑल इज़ वेल हॉस्पिटल में शारीरिक स्वास्थ्य, मानसिक कल्याण और समग्र जीवनशैली संतुलन का समर्थन करने के लिए विभिन्न प्रकार के संरचित योग उपचार उपलब्ध हैं। अस्पताल विभिन्न स्वास्थ्य आवश्यकताओं के अनुसार अनुकूलित बहुतेरिप्यूटिक योग दृष्टिकोण प्रदान करता है। इनमें हठ योग, विन्यासा फ्लो, यिन योग, प्राणायाम, ध्यान और माइंडफुलनेस, और योग थेरेपी शामिल हैं, प्रत्येक को विशिष्ट लाभों और चिकित्सीय लक्ष्यों के साथ डिज़ाइन किया गया है।",
            keywords = listOf("योग", "उपचार", "हठ", "विन्यासा", "प्राणायाम", "ध्यान", "चिकित्सा", "स्वास्थ्य"),
            category = "diagnostics",
            language = "hi"
        ),
        KnowledgeBaseQA(
            id = "qa_5_hi",
            question = "पोषण और आहार में कौन से उपचार उपलब्ध हैं?",
            answer = "पोषण और आहार में उपचार वैज्ञानिक रूप से डिज़ाइन की गई आहार रणनीतियों के माध्यम से समग्र स्वास्थ्य में सुधार, बीमारियों की रोकथाम और विशिष्ट चिकित्सा स्थितियों का समर्थन करने पर केंद्रित हैं। व्यक्तिगत भोजन योजना में किसी व्यक्ति के स्वास्थ्य लक्ष्यों, चिकित्सा इतिहास, जीवनशैली और आहार प्राथमिकताओं के अनुसार कस्टम पोषण योजनाएं बनाना शामिल है।",
            keywords = listOf("पोषण", "आहार", "भोजन", "योजना", "स्वास्थ्य", "वजन", "बीमारी", "रोकथाम"),
            category = "diagnostics",
            language = "hi"
        ),
        KnowledgeBaseQA(
            id = "qa_10_hi",
            question = "क्या आप दिशा दोहरा सकते हैं?",
            answer = "ज़रूर। मैं आपको आसानी से फॉलो कर सकें, इसलिए दिशाएं कदम-दर-कदम दोहराऊंगा। कृपया अपने वर्तमान स्थान से सीधे आगे बढ़ें, फिर निर्देशानुसार मुड़ें। यदि आप चाहें तो मैं आपको गंतव्य तक ले जाने के लिए भी गाइड कर सकता हूँ।",
            keywords = listOf("दिशा", "दोहराना", "कदम", "गंतव्य", "गाइड", "मार्ग", "निर्देश"),
            category = "facilities",
            language = "hi"
        ),
        KnowledgeBaseQA(
            id = "qa_11_hi",
            question = "क्या आप मैप दिखा सकते हैं?",
            answer = "हाँ। मैं आपके लिए अपनी स्क्रीन पर अस्पताल का मैप दिखा सकता हूँ। मैप आपके वर्तमान स्थान और अस्पताल के अंदर आपके गंतव्य तक मार्ग दिखाएगा। आप हाइलाइट किए गए पथ का पालन कर सकते हैं, या मैं व्यक्तिगत रूप से आपको स्थान तक गाइड कर सकता हूँ।",
            keywords = listOf("मैप", "दिखाना", "स्क्रीन", "स्थान", "मार्ग", "गंतव्य", "पथ"),
            category = "general",
            language = "hi"
        ),
        KnowledgeBaseQA(
            id = "qa_12_hi",
            question = "क्या आप फिर से समझा सकते हैं?",
            answer = "हाँ, मैं आपको सरल भाषा में फिर से समझाऊंगा। मैं यहाँ हूँ ताकि आपको पूर्ण और सटीक जानकारी मिल सके।",
            keywords = listOf("समझाना", "सरल", "भाषा", "जानकारी", "सटीक", "पूर्ण"),
            category = "general",
            language = "hi"
        ),
        KnowledgeBaseQA(
            id = "qa_14_hi",
            question = "रेडियोलॉजी और इमेजिंग का क्या खर्च है?",
            answer = "ऑल इज़ वेल हॉस्पिटल में रेडियोलॉजी और इमेजिंग विभाग उन्नत नैदानिक इमेजिंग सेवाओं की एक विस्तृत श्रृंखला प्रदान करता है। प्रत्येक टेस्ट की सटीक लागत स्कैन के प्रकार, जांचे गए शरीर के अंग, कंट्रास्ट उपयोग और चिकित्सीय आवश्यकताओं पर निर्भर करती है। अपडेटेड मूल्य निर्धारण के लिए रोगियों को बिलिंग काउंटर से संपर्क करने की सलाह दी जाती है।",
            keywords = listOf("रेडियोलॉजी", "इमेजिंग", "खर्च", "कीमत", "एक्स-रे", "स्कैन", "बिलिंग"),
            category = "general",
            language = "hi"
        ),
        KnowledgeBaseQA(
            id = "qa_15_hi",
            question = "क्या नकद भुगतान स्वीकार किया जाता है?",
            answer = "हाँ, अस्पताल परामर्श, नैदानिक परीक्षण, कमरा शुल्क और अन्य चिकित्सा सेवाओं के लिए नकद भुगतान स्वीकार करता है। नकद के अलावा, रोगी की सुविधा के लिए डिजिटल भुगतान विकल्प भी उपलब्ध हो सकते हैं। बड़े बिलों के लिए, बिलिंग काउंटर पर स्वीकृत भुगतान विधियों की पुष्टि करें।",
            keywords = listOf("नकद", "भुगतान", "बिल", "डिजिटल", "स्वीकार", "भुगतान विधि"),
            category = "general",
            language = "hi"
        ),
        KnowledgeBaseQA(
            id = "qa_16_hi",
            question = "अस्पताल का नाम क्या है?",
            answer = "अस्पताल का नाम ऑल इज़ वेल हॉस्पिटल है। मैं आपकी सहायता के लिए यहां हूँ, ऑल इज़ वेल हॉस्पिटल में आपका स्वागत है। मैं आपका डिजिटल सहायक हूँ - दिशाएं, अपॉइंटमेंट या जानकारी में मदद के लिए तैयार।",
            keywords = listOf("नाम", "अस्पताल", "ऑल इज़ वेल", "स्वागत", "सहायक"),
            category = "general",
            language = "hi"
        ),
        KnowledgeBaseQA(
            id = "qa_52_hi",
            question = "यहां कितने ओटी हैं?",
            answer = "ऑल इज़ वेल में 6+ मॉड्यूलर ओटी हैं।",
            keywords = listOf("ओटी", "ऑपरेशन थिएटर", "मॉड्यूलर", "कितने", "सर्जरी"),
            category = "general",
            language = "hi"
        ),
        KnowledgeBaseQA(
            id = "qa_53_hi",
            question = "अस्पताल में कितने कमरे हैं?",
            answer = "हमारे अस्पताल में लगभग 350 कमरे हैं।",
            keywords = listOf("कमरे", "अस्पताल", "कितने", "350", "कक्ष"),
            category = "general",
            language = "hi"
        ),
        KnowledgeBaseQA(
            id = "qa_54_hi",
            question = "अस्पताल में कितने बेड हैं?",
            answer = "अस्पताल में 360+ बेड हैं।",
            keywords = listOf("बेड", "अस्पताल", "कितने", "360", "पलंग"),
            category = "general",
            language = "hi"
        ),
        KnowledgeBaseQA(
            id = "qa_55_hi",
            question = "ऑल इज़ वेल के मूल मूल्य क्या हैं?",
            answer = "1) समय का महत्व 2) उत्कृष्टता के लिए प्रयास 3) विनम्रता का अभ्यास 4) समावेशिता को बढ़ावा 5) ईमानदारी के साथ जीवन।",
            keywords = listOf("मूल्य", "उत्कृष्टता", "विनम्रता", "समावेशिता", "ईमानदारी", "समय"),
            category = "general",
            language = "hi"
        ),
        KnowledgeBaseQA(
            id = "qa_56_hi",
            question = "ऑल इज़ वेल हॉस्पिटल में कौन सा अस्पताल सबसे अच्छा है?",
            answer = "ऑल इज़ वेल हॉस्पिटल बुरहानपुर के सबसे अच्छे अस्पतालों में गिना जाता है।",
            keywords = listOf("अच्छा", "बेस्ट", "अस्पताल", "बुरहानपुर", "श्रेष्ठ"),
            category = "general",
            language = "hi"
        ),
        KnowledgeBaseQA(
            id = "qa_59_hi",
            question = "मैं अस्पताल से कैसे संपर्क करूं?",
            answer = "आप +91 7697744444 या +91 7089099888 पर संपर्क कर सकते हैं।",
            keywords = listOf("संपर्क", "फोन", "कॉल", "नंबर", "पूछताछ"),
            category = "general",
            language = "hi"
        ),
        KnowledgeBaseQA(
            id = "qa_60_hi",
            question = "ऑल इज़ वेल हॉस्पिटल का ईमेल क्या है?",
            answer = "digitalmarketing@mvaburhanpur.com",
            keywords = listOf("ईमेल", "डिजिटल", "मार्केटिंग", "संपर्क"),
            category = "general",
            language = "hi"
        ),
        KnowledgeBaseQA(
            id = "qa_61_hi",
            question = "क्या विशेषज्ञ से मिलने के लिए रेफरल की जरूरत है?",
            answer = "नहीं, अधिकांश विभागों के लिए रेफरल की आवश्यकता नहीं है; आप सीधे अपनी पसंद के विशेषज्ञ से बुकिंग कर सकते हैं।",
            keywords = listOf("रेफरल", "विशेषज्ञ", "विभाग", "सीधे", "बुकिंग"),
            category = "departments",
            language = "hi"
        ),
        KnowledgeBaseQA(
            id = "qa_62_hi",
            question = "मैं कैसे देख सकता हूं कि कौन सा डॉक्टर मेरी स्थिति का इलाज करता है?",
            answer = "विभाग के अनुसार डॉक्टरों को फ़िल्टर करने और उनकी विशेषज्ञता देखने के लिए वेबसाइट पर 'हमारे विशेषज्ञों से मिलें' पर जाएं।",
            keywords = listOf("डॉक्टर", "विशेषज्ञ", "विभाग", "इलाज", "वेबसाइट"),
            category = "departments",
            language = "hi"
        ),
        KnowledgeBaseQA(
            id = "qa_63_hi",
            question = "क्या आपके डॉक्टर अनुभवी हैं?",
            answer = "हाँ, हमारी टीम में फेलोशिप और अपने क्षेत्रों में विशेष प्रशिक्षण वाले योग्य कंसल्टेंट शामिल हैं।",
            keywords = listOf("डॉक्टर", "अनुभवी", "योग्य", "फेलोशिप", "प्रशिक्षण"),
            category = "departments",
            language = "hi"
        ),
        KnowledgeBaseQA(
            id = "qa_64_hi",
            question = "आप कौन से स्वास्थ्य चेक-अप पैकेज ऑफर करते हैं?",
            answer = "हम बेसिक वेलनेस (₹899), कम्प्रिहेंसिव वेलनेस (₹4999), महिलाओं के, मधुमेह, गर्भावस्था और बाल पैकेज ऑफर करते हैं।",
            keywords = listOf("पैकेज", "स्वास्थ्य", "चेक-अप", "वेलनेस", "महिला", "मधुमेह"),
            category = "general",
            language = "hi"
        ),
        KnowledgeBaseQA(
            id = "qa_65_hi",
            question = "क्या स्वास्थ्य पैकेज पर छूट मिलती है?",
            answer = "हाँ, सभी पैकेज वर्तमान में मूल कीमत पर 25-53% की छूट देते हैं - नवीनतम ऑफर के लिए वेबसाइट देखें।",
            keywords = listOf("छूट", "पैकेज", "ऑफर", "कीमत", "वेबसाइट"),
            category = "general",
            language = "hi"
        ),
        KnowledgeBaseQA(
            id = "qa_66_hi",
            question = "डायबिटिक वेलनेस पैकेज में क्या शामिल है?",
            answer = "इसमें HbA1c, शुगर टेस्ट, ECG, 2D इको, किडनी/लिवर टेस्ट, आंखों की जांच और फिजिशियन और डायटीशियन से परामर्श शामिल हैं।",
            keywords = listOf("मधुमेह", "पैकेज", "टेस्ट", "ECG", "इको", "किडनी", "लिवर"),
            category = "general",
            language = "hi"
        ),
        KnowledgeBaseQA(
            id = "qa_67_hi",
            question = "क्या आप डायटीशियन परामर्श ऑफर करते हैं?",
            answer = "हाँ, कम्प्रिहेंसिव वेलनेस और डायबिटिक वेलनेस पैकेज में डायटीशियन परामर्श शामिल हैं।",
            keywords = listOf("डायटीशियन", "परामर्श", "पैकेज", "वेलनेस", "मधुमेह"),
            category = "departments",
            language = "hi"
        ),
        KnowledgeBaseQA(
            id = "qa_68_hi",
            question = "क्या आपकी सेवाएं किफायती हैं?",
            answer = "हम पारदर्शी मूल्य निर्धारण और छूट वाले वेलनेस पैकेज के साथ उच्च गुणवत्ता, लागत प्रभावी देखभाल प्रदान करते हैं।",
            keywords = listOf("किफायती", "सेवाएं", "पारदर्शी", "मूल्य", "गुणवत्ता"),
            category = "general",
            language = "hi"
        ),
        KnowledgeBaseQA(
            id = "qa_69_hi",
            question = "क्या अस्पताल में पार्किंग उपलब्ध है?",
            answer = "हाँ, हम मुख्य प्रवेश द्वार के पास रोगियों और आगंतुकों के लिए मुफ्त पार्किंग ऑफर करते हैं।",
            keywords = listOf("पार्किंग", "मुफ्त", "रोगी", "आगंतुक", "प्रवेश"),
            category = "facilities",
            language = "hi"
        ),
        KnowledgeBaseQA(
            id = "qa_70_hi",
            question = "क्या आप वॉक-इन रोगियों को स्वीकार करते हैं?",
            answer = "हाँ, सामान्य परामर्श के लिए वॉक-इन का स्वागत है; अपॉइंटमेंट से प्रतीक्षा समय कम होता है।",
            keywords = listOf("वॉक-इन", "रोगी", "परामर्श", "अपॉइंटमेंट", "प्रतीक्षा"),
            category = "general",
            language = "hi"
        ),
        KnowledgeBaseQA(
            id = "qa_71_hi",
            question = "क्या मैं इलाज से पहले बिल अनुमान प्राप्त कर सकता हूं?",
            answer = "हाँ, डॉक्टर से संक्षिप्त परामर्श के बाद हमारा फ्रंट डेस्क अनुमानित लागत अनुमान प्रदान कर सकता है।",
            keywords = listOf("बिल", "अनुमान", "इलाज", "लागत", "फ्रंट डेस्क"),
            category = "general",
            language = "hi"
        ),
        KnowledgeBaseQA(
            id = "qa_72_hi",
            question = "क्या मैं ऑनलाइन अपने पिछले विजिट हिस्ट्री एक्सेस कर सकता हूं?",
            answer = "अस्पताल के पास रोगी के सभी पिछले विवरण हैं, हर रोगी के पास पहली विजिट पर एक यूनिक आईडी होती है, जहां से वे अपना पिछला इतिहास एक्सेस कर सकते हैं।",
            keywords = listOf("ऑनलाइन", "विजिट", "इतिहास", "रोगी", "आईडी"),
            category = "general",
            language = "hi"
        ),
        KnowledgeBaseQA(
            id = "qa_73_hi",
            question = "क्या आप रिपोर्ट अन्य डॉक्टरों के साथ शेयर करते हैं?",
            answer = "हाँ, आपकी सहमति से, हम किसी भी विशेषज्ञ के साथ डिजिटल या प्रिंटेड रिपोर्ट शेयर कर सकते हैं।",
            keywords = listOf("रिपोर्ट", "शेयर", "डॉक्टर", "सहमति", "डिजिटल"),
            category = "departments",
            language = "hi"
        ),
        KnowledgeBaseQA(
            id = "qa_74_hi",
            question = "चिकित्सा आपातकालीन स्थिति में मुझे क्या करना चाहिए?",
            answer = "तुरंत हमारे आपातकालीन नंबर पर कॉल करें या अस्पताल का दौरा करें - हमारी 24/7 टीम मदद के लिए तैयार है।",
            keywords = listOf("आपातकालीन", "चिकित्सा", "कॉल", "24/7", "मदद"),
            category = "hospital_info",
            language = "hi"
        ),
        KnowledgeBaseQA(
            id = "qa_75_hi",
            question = "क्या अस्पताल व्हीलचेयर-एक्सेसिबल है?",
            answer = "हाँ, पूरी सुविधा रैम्प-इनेबल और रोगी की सुविधा के लिए व्हीलचेयर-फ्रेंडली है।",
            keywords = listOf("व्हीलचेयर", "रैम्प", "एक्सेसिबल", "रोगी", "सुविधा"),
            category = "general",
            language = "hi"
        ),
        KnowledgeBaseQA(
            id = "qa_76_hi",
            question = "मैं व्हाट्सएप चैट फीचर का उपयोग कैसे करूं?",
            answer = "अपॉइंटमेंट मदद या त्वरित क्वेरी के लिए वेबसाइट पर व्हाट्सएप आइकन पर क्लिक करें।",
            keywords = listOf("व्हाट्सएप", "चैट", "वेबसाइट", "अपॉइंटमेंट", "क्वेरी"),
            category = "general",
            language = "hi"
        ),
        KnowledgeBaseQA(
            id = "qa_77_hi",
            question = "क्या मैं ऑनलाइन अपनी अपॉइंटमेंट रीशेड्यूल कर सकता हूं?",
            answer = "हाँ, अपनी बुकिंग में लॉग इन करें या अपना स्लॉट बदलने के लिए व्हाट्सएप के माध्यम से संदेश भेजें।",
            keywords = listOf("ऑनलाइन", "रीशेड्यूल", "अपॉइंटमेंट", "व्हाट्सएप", "स्लॉट"),
            category = "appointments",
            language = "hi"
        ),
        KnowledgeBaseQA(
            id = "qa_78_hi",
            question = "क्या आप रोगियों और सेवकों के लिए भोजन प्रदान करते हैं?",
            answer = "हाँ, हम स्वच्छ, डायटीशियन-अप्रूव्ड भोजन प्रदान करते हैं; विशेष आहार अनुरोध पर व्यवस्थित किए जा सकते हैं।",
            keywords = listOf("भोजन", "रोगी", "सेवक", "आहार", "डायटीशियन"),
            category = "general",
            language = "hi"
        ),
        KnowledgeBaseQA(
            id = "qa_79_hi",
            question = "क्या आपका अस्पताल प्रमाणित या प्रमाणपत्रित है?",
            answer = "हाँ, हम सुरक्षा, स्वच्छता और उपचार के लिए प्रमाणित प्रोटोकॉल बनाए रखते हुए राष्ट्रीय स्वास्थ्य सेवा मानकों का पालन करते हैं।",
            keywords = listOf("प्रमाणित", "अस्पताल", "सुरक्षा", "स्वच्छता", "मानक"),
            category = "general",
            language = "hi"
        ),
        KnowledgeBaseQA(
            id = "qa_80_hi",
            question = "आपका अस्पताल अलग कैसे है?",
            answer = "हम अनुभवी विशेषज्ञ, उन्नत डायग्नोस्टिक्स, पारदर्शी मूल्य निर्धारण और रोगी-पहले देखभाल को एक छत के नीचे जोड़ते हैं।",
            keywords = listOf("अलग", "विशेषज्ञ", "डायग्नोस्टिक्स", "पारदर्शी", "रोगी"),
            category = "general",
            language = "hi"
        ),
        KnowledgeBaseQA(
            id = "qa_81_hi",
            question = "क्या आप ग्रामीण या दूरस्थ रोगियों की सेवा करते हैं?",
            answer = "हाँ, हम सभी क्षेत्रों के रोगियों का स्वागत करते हैं और दूर से यात्रा करने वालों के लिए टेली-कंसल्टेशन सपोर्ट ऑफर करते हैं।",
            keywords = listOf("ग्रामीण", "दूरस्थ", "रोगी", "टेली-कंसल्टेशन", "सपोर्ट"),
            category = "general",
            language = "hi"
        ),
        KnowledgeBaseQA(
            id = "qa_82_hi",
            question = "क्या आपका अस्पताल पर्यावरण अनुकूल या सतत है?",
            answer = "हम कचरा अलगाव, ऊर्जा-कुशल सिस्टम और संभव होने पर प्लास्टिक के उपयोग में कमी जैसी हरित प्रथाओं का पालन करते हैं।",
            keywords = listOf("पर्यावरण", "सतत", "हरित", "कचरा", "ऊर्जा"),
            category = "general",
            language = "hi"
        ),
        KnowledgeBaseQA(
            id = "qa_83_hi",
            question = "क्या मैं एडमिशन से पहले अस्पताल का दौरा कर सकता हूं?",
            answer = "हाँ, हमारा फ्रंट डेस्क एक गाइडेड विजिट व्यवस्थित कर सकता है - बस आगे से कॉल करें या व्हाट्सएप के माध्यम से अनुरोध करें।",
            keywords = listOf("दौरा", "एडमिशन", "गाइडेड", "फ्रंट डेस्क", "व्हाट्सएप"),
            category = "general",
            language = "hi"
        ),
        KnowledgeBaseQA(
            id = "qa_84_hi",
            question = "मैं फीडबैक कैसे दे सकता हूं या शिकायत कैसे दर्ज करूं?",
            answer = "'हमसे संपर्क करें' फॉर्म, व्हाट्सएप के माध्यम से अपना अनुभव शेयर करें, या रोगी की डिस्चार्ज के बाद, पेशेंट केयर आपसे फीडबैक के लिए संपर्क करेगा - हम आपके इनपुट को महत्व देते हैं।",
            keywords = listOf("फीडबैक", "शिकायत", "संपर्क", "व्हाट्सएप", "डिस्चार्ज"),
            category = "general",
            language = "hi"
        ),
        KnowledgeBaseQA(
            id = "qa_85_hi",
            question = "क्या आपके डॉक्टर फुल-टाइम स्टाफ हैं?",
            answer = "हाँ, हमारे मुख्य विशेषज्ञ समर्पित फुल-टाइम कंसल्टेंट हैं जो लगातार, विश्वसनीय देखभाल सुनिश्चित करते हैं।",
            keywords = listOf("डॉक्टर", "फुल-टाइम", "स्टाफ", "विशेषज्ञ", "कंसल्टेंट"),
            category = "departments",
            language = "hi"
        ),
        KnowledgeBaseQA(
            id = "qa_86_hi",
            question = "क्या रोगी की गोपनीयता संरक्षित है?",
            answer = "सभी चिकित्सा रिकॉर्ड और व्यक्तिगत डेटा गोपनीय हैं और केवल आपकी सहमति से शेयर किए जाते हैं।",
            keywords = listOf("गोपनीयता", "रोगी", "रिकॉर्ड", "डेटा", "सहमति"),
            category = "general",
            language = "hi"
        ),
        KnowledgeBaseQA(
            id = "qa_88_hi",
            question = "क्या अस्पताल NABH प्रमाणित है?",
            answer = "हाँ, हम NABH प्रमाणित हैं। आप इसे हमारी वेबसाइट पर देख सकते हैं।",
            keywords = listOf("NABH", "प्रमाणित", "अस्पताल", "वेबसाइट"),
            category = "general",
            language = "hi"
        ),
        KnowledgeBaseQA(
            id = "qa_89_hi",
            question = "क्या अस्पताल ISO 9001 प्रमाणित है?",
            answer = "हाँ, अस्पताल ISO 9001 प्रमाणित है।",
            keywords = listOf("ISO", "9001", "प्रमाणित", "अस्पताल"),
            category = "general",
            language = "hi"
        ),
        KnowledgeBaseQA(
            id = "qa_90_hi",
            question = "डॉक्टरों की संख्या?",
            answer = "30+ डॉक्टर उपलब्ध हैं।",
            keywords = listOf("डॉक्टर", "संख्या", "30", "उपलब्ध"),
            category = "departments",
            language = "hi"
        ),
        KnowledgeBaseQA(
            id = "qa_91_hi",
            question = "क्या मैं ऑनलाइन अपॉइंटमेंट बुक कर सकता हूं?",
            answer = "हाँ, आप सीधे वेबसाइट के माध्यम से या रिसेप्शन पर कॉल करके ऑनलाइन अपॉइंटमेंट बुक कर सकते हैं।",
            keywords = listOf("ऑनलाइन", "अपॉइंटमेंट", "बुक", "वेबसाइट", "रिसेप्शन"),
            category = "appointments",
            language = "hi"
        ),
        KnowledgeBaseQA(
            id = "qa_92_hi",
            question = "क्या आपातकालीन सेवाएं उपलब्ध हैं?",
            answer = "हाँ, आपातकालीन सेवाएं हमारे अस्पताल में 24/7 उपलब्ध हैं।",
            keywords = listOf("आपातकालीन", "सेवाएं", "24/7", "अस्पताल"),
            category = "hospital_info",
            language = "hi"
        ),
        KnowledgeBaseQA(
            id = "qa_93_hi",
            question = "एम्बुलेंस नंबर क्या है?",
            answer = "एम्बुलेंस कॉल करें: +91 7697744444",
            keywords = listOf("एम्बुलेंस", "नंबर", "कॉल", "आपातकालीन"),
            category = "general",
            language = "hi"
        ),
        KnowledgeBaseQA(
            id = "qa_94_hi",
            question = "कैफेटेरिया नाश्ता/दोपहर का भोजन/रात का खाना कब तक परोसता है?",
            answer = "कैफेटेरियां मानक सुबह, दोपहर और शाम के समय में भोजन परोसती हैं। किसी भी भोजन अवधि के बीच में पहुंचें ताकि आप कटऑफ मिस न करें।",
            keywords = listOf("कैफेटेरिया", "नाश्ता", "दोपहर", "रात", "भोजन", "समय"),
            category = "hospital_info",
            language = "hi"
        ),
        KnowledgeBaseQA(
            id = "qa_95_hi",
            question = "डॉक्टर वार्ड राउंड किस समय करते हैं?",
            answer = "वार्ड राउंड आमतौर पर दिन में एक या दो बार होते हैं।",
            keywords = listOf("डॉक्टर", "वार्ड", "राउंड", "समय", "दिन"),
            category = "departments",
            language = "hi"
        ),
        KnowledgeBaseQA(
            id = "qa_96_hi",
            question = "सर्जरी शेड्यूलिंग के घंटे क्या हैं?",
            answer = "सर्जरी आमतौर पर नियमित सप्ताह के दिन दिन के समय में शेड्यूल की जाती हैं। विशिष्ट समय के लिए, कृपया उपलब्धता और तत्कालता के आधार पर शेड्यूल भिन्न होते हैं, रिसेप्शन से जांचें।",
            keywords = listOf("सर्जरी", "शेड्यूलिंग", "समय", "सप्ताह", "उपलब्धता"),
            category = "hospital_info",
            language = "hi"
        ),
        KnowledgeBaseQA(
            id = "qa_97_hi",
            question = "स्वास्थ्य शिविर या स्क्रीनिंग ड्राइव कब आयोजित किए जाते हैं?",
            answer = "अस्पताल द्वारा पहले पंचायत और क्लिनिक में कई स्वास्थ्य शिविर आयोजित किए गए हैं, कार्डियोलॉजी शिविर और इस तरह के कई शिविर आयोजित किए गए हैं।",
            keywords = listOf("स्वास्थ्य शिविर", "स्क्रीनिंग", "कार्डियोलॉजी", "पंचायत", "क्लिनिक"),
            category = "general",
            language = "hi"
        ),
        KnowledgeBaseQA(
            id = "qa_98_hi",
            question = "क्या मैं रोगी के लिए अपना खाना ला सकता हूं?",
            answer = "हाँ, आप खाना ला सकते हैं लेकिन इसे केवल विशेषज्ञ की चिंता के बाद दें।",
            keywords = listOf("खाना", "रोगी", "बाहरी", "विशेषज्ञ", "अनुमति"),
            category = "general",
            language = "hi"
        ),
        KnowledgeBaseQA(
            id = "qa_99_hi",
            question = "क्या अंदर फोटोग्राफी या वीडियो रिकॉर्डिंग की अनुमति है?",
            answer = "गोपनीयता और सुरक्षा कारणों से, फोटोग्राफी और वीडियो रिकॉर्डिंग आमतौर पर अंदर की अनुमति नहीं है। कृपया किसी भी विशिष्ट दिशानिर्देशों या अपवादों के लिए रिसेप्शन से जांचें।",
            keywords = listOf("फोटोग्राफी", "वीडियो", "रिकॉर्डिंग", "गोपनीयता", "सुरक्षा", "अनुमति"),
            category = "general",
            language = "hi"
        ),
        KnowledgeBaseQA(
            id = "qa_100_hi",
            question = "क्या आप महंगे उपचारों के लिए पेमेंट प्लान या EMI ऑफर करते हैं?",
            answer = "हम समझते हैं कि उपचार लागत भिन्न हो सकती है, और लचीले भुगतान विकल्प उपलब्ध हो सकते हैं। कृपया अपनी जरूरतों के अनुसार प्लान या EMI विवरण तलाशने के लिए रिसेप्शन से बात करें।",
            keywords = listOf("EMI", "पेमेंट", "प्लान", "उपचार", "लागत", "विकल्प"),
            category = "general",
            language = "hi"
        ),
        KnowledgeBaseQA(
            id = "qa_101_hi",
            question = "क्या सरकारी कर्मचारियों या वरिष्ठ नागरिकों के लिए छूट है?",
            answer = "नहीं, अभी ऐसी कोई छूट नहीं है लेकिन माइक्रो विजन अकादमी के तहत काम करने वाले कर्मचारी जो ऑल इज़ वेल में काम करते हैं, उन्हें विजन कार्ड द्वारा इलाज किया जा सकता है।",
            keywords = listOf("छूट", "सरकारी", "कर्मचारी", "वरिष्ठ", "विजन कार्ड"),
            category = "general",
            language = "hi"
        ),
        KnowledgeBaseQA(
            id = "qa_102_hi",
            question = "अगर मैं तुरंत पूरा बिल नहीं दे पाता तो क्या होगा?",
            answer = "अगर आप तुरंत पूरा बिल नहीं दे पाते तो कृपया रिसेप्शन से बात करें - वे उपलब्ध सपोर्ट विकल्पों या भुगतान व्यवस्था के माध्यम से आपको गाइड कर सकते हैं।",
            keywords = listOf("बिल", "भुगतान", "तुरंत", "रिसेप्शन", "सपोर्ट"),
            category = "general",
            language = "hi"
        ),
        KnowledgeBaseQA(
            id = "qa_103_hi",
            question = "अगर मुझे हार्ट अटैक या स्ट्रोक का शक हो तो मुझे क्या करना चाहिए?",
            answer = "तुरंत किसी भी स्टाफ मेंबर को अलर्ट करें या इमरजेंसी नंबर +91 7697744444 पर कॉल करें।",
            keywords = listOf("हार्ट अटैक", "स्ट्रोक", "इमरजेंसी", "कॉल", "अलर्ट"),
            category = "general",
            language = "hi"
        ),
        KnowledgeBaseQA(
            id = "qa_104_hi",
            question = "क्या आपके पास पॉइजन कंट्रोल या टॉक्सिकोलॉजी यूनिट है?",
            answer = "ज़हर के मामलों के लिए, तेज़ गाइडेंस और केयर कोऑर्डिनेशन के लिए तुरंत रिसेप्शन या इमरजेंसी सेवाओं से संपर्क करें।",
            keywords = listOf("पॉइजन", "टॉक्सिकोलॉजी", "यूनिट", "इमरजेंसी", "रिसेप्शन"),
            category = "general",
            language = "hi"
        ),
        KnowledgeBaseQA(
            id = "qa_105_hi",
            question = "अगर रोगी की स्थिति अचानक बिगड़ जाए तो क्या प्रोटोकॉल है?",
            answer = "किसी भी आस-पास के स्टाफ को अलर्ट करें या इमरजेंसी कॉल बटन दबाएं - हमारी टीम तत्काल परिवर्तनों पर जल्दी प्रतिक्रिया करने के लिए प्रशिक्षित है। रिसेप्शन भी तत्काल एस्केलेशन में मदद कर सकता है।",
            keywords = listOf("रोगी", "स्थिति", "बिगड़ना", "इमरजेंसी", "स्टाफ", "एस्केलेशन"),
            category = "general",
            language = "hi"
        ),
        KnowledgeBaseQA(
            id = "qa_106_hi",
            question = "क्या आप जेनेटिक टेस्टिंग या एडवांस्ड मॉलिक्यूलर डायग्नोस्टिक्स ऑफर करते हैं?",
            answer = "जबकि हम ऑन-साइट जेनेटिक या एडवांस्ड मॉलिक्यूलर टेस्ट नहीं करते हैं, हम आपको विश्वसनीय पार्टनर लैब्स को रेफर कर सकते हैं। वे टेस्ट करेंगे और आपके डॉक्टर द्वारा रिव्यू के लिए रिपोर्ट हमारे माध्यम से शेयर करेंगे।",
            keywords = listOf("जेनेटिक", "टेस्टिंग", "मॉलिक्यूलर", "डायग्नोस्टिक्स", "लैब"),
            category = "diagnostics",
            language = "hi"
        ),
        KnowledgeBaseQA(
            id = "qa_107_hi",
            question = "क्या मैं बिना प्रिस्क्रिप्शन विटामिन D, B12, या हार्मोन टेस्ट करा सकता हूं?",
            answer = "अधिकांश रूटीन टेस्ट - विटामिन D, B12, और हार्मोन पैनल सहित - बिना प्रिस्क्रिप्शन किए जा सकते हैं। एकमात्र अपवाद गर्भावस्था टेस्टिंग है, जिसके लिए डॉक्टर की प्रिस्क्रिप्शन आवश्यक है।",
            keywords = listOf("विटामिन", "B12", "हार्मोन", "टेस्ट", "प्रिस्क्रिप्शन"),
            category = "diagnostics",
            language = "hi"
        ),
        KnowledgeBaseQA(
            id = "qa_108_hi",
            question = "क्या रोगी के कमरों में टीवी या एंटरटेनमेंट विकल्प हैं?",
            answer = "कमरा सुविधाओं जैसे टीवी कैटेगरी (जनरल, सिल्वर, डायमंड, या प्लेटिनम) के अनुसार भिन्न होते हैं। कृपया आपके चयनित कमरे में क्या शामिल है इसके लिए रिसेप्शन से जांचें।",
            keywords = listOf("टीवी", "एंटरटेनमेंट", "कमरा", "कैटेगरी", "जनरल", "डायमंड"),
            category = "general",
            language = "hi"
        ),
        KnowledgeBaseQA(
            id = "qa_109_hi",
            question = "क्या कमरे में रेफ्रिजरेटर या माइक्रोवेव है?",
            answer = "ये सुविधाएं आपके कमरे के प्रकार पर निर्भर करती हैं। रिसेप्शन आपकी बुकिंग के आधार पर उपलब्धता की पुष्टि कर सकता है।",
            keywords = listOf("रेफ्रिजरेटर", "माइक्रोवेव", "कमरा", "उपलब्धता", "बुकिंग"),
            category = "general",
            language = "hi"
        ),
        KnowledgeBaseQA(
            id = "qa_110_hi",
            question = "क्या आप रोगियों के लिए लॉन्ड्री सेवाएं प्रदान करते हैं?",
            answer = "लॉन्ड्री सेवाओं के बारे में पूछताछ के लिए, कृपया रिसेप्शन से जांचें - वे उपलब्धता पर सबसे अप-टू-डेट जानकारी प्रदान कर सकते हैं।",
            keywords = listOf("लॉन्ड्री", "सेवा", "रोगी", "उपलब्धता", "रिसेप्शन"),
            category = "general",
            language = "hi"
        ),
        KnowledgeBaseQA(
            id = "qa_111_hi",
            question = "क्या रोगी के कमरों और प्रतीक्षा क्षेत्रों में मुफ्त वाईफाई उपलब्ध है?",
            answer = "वाईफाई उपलब्धता कमरे के प्रकारों और सामान्य क्षेत्रों में भिन्न होती है। एक्सेस विवरण के लिए, कृपया चेक-इन पर रिसेप्शन से पूछें।",
            keywords = listOf("वाईफाई", "मुफ्त", "कमरा", "प्रतीक्षा", "एक्सेस"),
            category = "diagnostics",
            language = "hi"
        ),
        KnowledgeBaseQA(
            id = "qa_112_hi",
            question = "क्या आप अपॉइंटमेंट और रिपोर्ट के लिए मोबाइल ऐप ऑफर करते हैं?",
            answer = "आप हमारी वेबसाइट या व्हाट्सएप के माध्यम से अपॉइंटमेंट बुक कर सकते हैं, टोकन जनरेट कर सकते हैं और रिपोर्ट एक्सेस कर सकते हैं। रिसेप्शन भी किसी भी समय बुकिंग में मदद करने के लिए खुश है।",
            keywords = listOf("मोबाइल", "ऐप", "अपॉइंटमेंट", "रिपोर्ट", "वेबसाइट", "व्हाट्सएप"),
            category = "appointments",
            language = "hi"
        ),
        KnowledgeBaseQA(
            id = "qa_113_hi",
            question = "क्या आप जन्म/मृत्यु प्रमाणपत्र जारी करते हैं?",
            answer = "हाँ, अस्पताल जन्म और मृत्यु प्रमाणपत्रों के लिए एप्लिकेशन प्रोसेस करता है। क्वेरी या सहायता के लिए, कृपया MRD (मेडिकल रिकॉर्ड डिपार्टमेंट) से संपर्क करें या दिशा निर्देश के लिए रिसेप्शन से जांचें।",
            keywords = listOf("जन्म", "मृत्यु", "प्रमाणपत्र", "MRD", "रिकॉर्ड"),
            category = "general",
            language = "hi"
        ),
        KnowledgeBaseQA(
            id = "qa_114_hi",
            question = "क्या आप आस-पास के गांवों में मुफ्त स्वास्थ्य शिविर करते हैं?",
            answer = "हाँ, हम नियमित रूप से आस-पास के गांवों में मुफ्त स्वास्थ्य शिविर करते हैं। शेड्यूल या भागीदारी विवरण के लिए, कृपया रिसेप्शन पर पूछें।",
            keywords = listOf("मुफ्त", "स्वास्थ्य शिविर", "गांव", "नियमित", "रिसेप्शन"),
            category = "general",
            language = "hi"
        ),
        KnowledgeBaseQA(
            id = "qa_115_hi",
            question = "क्या मैं अस्पताल में वॉलंटियर या इंटर्न कर सकता हूं?",
            answer = "हाँ, हम हमारे विशेषज्ञों के तहत काम करने वाले वॉलंटियर और इंटर्न का स्वागत करते हैं। कृपया अवसरों और एप्लिकेशन प्रोसेस के बारे में जानने के लिए रिसेप्शन से बात करें।",
            keywords = listOf("वॉलंटियर", "इंटर्न", "अस्पताल", "विशेषज्ञ", "एप्लिकेशन"),
            category = "general",
            language = "hi"
        ),
        KnowledgeBaseQA(
            id = "qa_116_hi",
            question = "क्या आप चिकित्सा प्रशिक्षण या प्रमाणन कोर्सेज ऑफर करते हैं?",
            answer = "हाँ, हम पूर्ण होने पर प्रमाणन के साथ चिकित्सा प्रशिक्षण प्रोग्राम ऑफर करते हैं।",
            keywords = listOf("प्रशिक्षण", "प्रमाणन", "कोर्सेज", "चिकित्सा"),
            category = "general",
            language = "hi"
        ),
        KnowledgeBaseQA(
            id = "qa_117_hi",
            question = "एक समय में कितने आगंतुकों को अनुमति है?",
            answer = "जनरल वार्ड के लिए, एक समय में एक आगंतुक को रोगी पास के साथ अनुमति है। ICU विजिट निर्दिष्ट घंटों (सुबह 11 बजे-दोपहर 12 बजे और शाम 6 बजे-7 बजे) के दौरान 1-2 करीबी रिश्तेदारों तक सीमित हैं।",
            keywords = listOf("आगंतुक", "वार्ड", "ICU", "पास", "समय", "रिश्तेदार"),
            category = "hospital_info",
            language = "hi"
        ),
        KnowledgeBaseQA(
            id = "qa_118_hi",
            question = "क्या बच्चे ICU में रोगियों से मिल सकते हैं?",
            answer = "रोगी सुरक्षा और संक्रमण नियंत्रण सुनिश्चित करने के लिए 12 वर्ष से कम उम्र के बच्चों को आमतौर पर ICU में अनुमति नहीं है। कृपया किसी भी अपवाद के लिए नर्सिंग स्टेशन से परामर्श करें।",
            keywords = listOf("बच्चे", "ICU", "रोगी", "सुरक्षा", "संक्रमण", "नर्सिंग"),
            category = "general",
            language = "hi"
        ),
        KnowledgeBaseQA(
            id = "qa_119_hi",
            question = "मैं रोगी के लिए कौन सी चीजें ला सकता हूं?",
            answer = "आप आवश्यक व्यक्तिगत वस्तुएं, निर्धारित दवाइयां और हल्के स्नैक्स ला सकते हैं। विशेष रूप से ICU रोगियों के लिए, बाहरी भोजन या उपहार लाने से पहले हमेशा नर्सिंग स्टाफ से जांचें।",
            keywords = listOf("व्यक्तिगत", "दवाई", "स्नैक्स", "रोगी", "नर्सिंग"),
            category = "general",
            language = "hi"
        ),
        KnowledgeBaseQA(
            id = "qa_120_hi",
            question = "क्या रोगी के कमरों में फूल या बाहरी भोजन की अनुमति है?",
            answer = "ICU/क्रिटिकल केयर क्षेत्रों में फूल और बाहरी भोजन की अनुमति नहीं है। जनरल वार्ड के लिए, कृपया ऐसी वस्तुएं लाने से पहले नर्सिंग टीम से पुष्टि करें।",
            keywords = listOf("फूल", "भोजन", "ICU", "कमरा", "वार्ड", "नर्सिंग"),
            category = "general",
            language = "hi"
        ),
        KnowledgeBaseQA(
            id = "qa_121_hi",
            question = "क्या मैं रोगी के साथ रात भर रह सकता हूं?",
            answer = "हाँ, एक सेवक प्राइवेट कमरों या निर्दिष्ट गेस्ट कमरों (102, 103, 221, 222) में उपलब्धता और अस्पताल नीति के अनुसार रात भर रह सकता है।",
            keywords = listOf("रात", "कमरा", "सेवक", "प्राइवेट", "उपलब्धता"),
            category = "general",
            language = "hi"
        ),
        KnowledgeBaseQA(
            id = "qa_122_hi",
            question = "अगर मैं शारीरिक रूप से विजिट नहीं कर सकता तो क्या मैं रोगी से वीडियो कॉल कर सकता हूं?",
            answer = "हाँ, रोगी की सहमति और नर्सिंग स्टाफ कोऑर्डिनेशन के साथ वीडियो कॉल की अनुमति है, विशेष रूप से ICU रोगियों के लिए या जब शारीरिक विजिट संभव नहीं हों।",
            keywords = listOf("वीडियो", "कॉल", "रोगी", "ICU", "सहमति", "नर्सिंग"),
            category = "general",
            language = "hi"
        ),
        KnowledgeBaseQA(
            id = "qa_123_hi",
            question = "क्या मैं एक ट्रिप में कई रोगियों से मिल सकता हूं?",
            answer = "हाँ, आप विजिटिंग घंटों के दौरान कई रोगियों से मिल सकते हैं। कृपया रिसेप्शन डेस्क पर प्रत्येक रोगी के लिए अलग विजिटर पास लें।",
            keywords = listOf("रोगी", "विजिटर", "पास", "रिसेप्शन", "ट्रिप"),
            category = "general",
            language = "hi"
        ),
        KnowledgeBaseQA(
            id = "qa_124_hi",
            question = "रोगी के कमरों या ICU में कौन सी चीजें निषिद्ध हैं?",
            answer = "निषिद्ध वस्तुओं में बाहरी भोजन (ICU में), फूल, तेज वस्तुएं, शराब, तंबाकू और बड़े इलेक्ट्रॉनिक्स शामिल हैं। कृपया पूर्ण, अपडेटेड सूची के लिए स्टाफ से जांचें।",
            keywords = listOf("निषिद्ध", "भोजन", "फूल", "शराब", "तंबाकू", "ICU"),
            category = "general",
            language = "hi"
        ),
        KnowledgeBaseQA(
            id = "qa_126_hi",
            question = "मैं कैसे पुष्टि करूं कि मेरी अपॉइंटमेंट बुक है?",
            answer = "बुकिंग के बाद, आपको पुष्टि के रूप में SMS/व्हाट्सएप के माध्यम से टोकन नंबर प्राप्त होगा। आप +91 76977 44444 या +91 70890 99888 पर कॉल करके या रिसेप्शन से जांचकर अपनी अपॉइंटमेंट स्टेटस वेरिफाई भी कर सकते हैं।",
            keywords = listOf("अपॉइंटमेंट", "बुक", "टोकन", "SMS", "व्हाट्सएप", "पुष्टि"),
            category = "appointments",
            language = "hi"
        ),
        KnowledgeBaseQA(
            id = "qa_127_hi",
            question = "अगर मैं अपना अपॉइंटमेंट स्लॉट मिस करूं तो क्या होगा?",
            answer = "अगर आप अपना स्लॉट मिस करते हैं तो कृपया रीशेड्यूल के लिए +91 76977 44444 या +91 70890 99888 पर रिसेप्शन से संपर्क करें। डॉक्टर की उपलब्धता के आधार पर वॉक-इन कंसल्टेशन उपलब्ध हो सकते हैं, हालांकि प्रतीक्षा समय लागू हो सकता है।",
            keywords = listOf("स्लॉट", "मिस", "रीशेड्यूल", "वॉक-इन", "डॉक्टर", "उपलब्धता"),
            category = "appointments",
            language = "hi"
        ),
        KnowledgeBaseQA(
            id = "qa_128_hi",
            question = "क्या मैं सम-दिन या तत्काल अपॉइंटमेंट प्राप्त कर सकता हूं?",
            answer = "हाँ, जनरल OPD के लिए सम-दिन वॉक-इन कंसल्टेशन पहले-आओ, पहले-पाओ के आधार पर उपलब्ध हैं। तत्काल चिकित्सा आवश्यकताओं के लिए, हमारी 24/7 इमरजेंसी सेवाएं हमेशा एक्सेसिबल हैं।",
            keywords = listOf("सम-दिन", "तत्काल", "अपॉइंटमेंट", "वॉक-इन", "OPD", "इमरजेंसी"),
            category = "appointments",
            language = "hi"
        ),
        KnowledgeBaseQA(
            id = "qa_129_hi",
            question = "मैं अपनी अपॉइंटमेंट कैसे रीशेड्यूल या कैंसल करूं?",
            answer = "आप +91 76977 44444 या +91 70890 99888 पर कॉल करके, व्हाट्सएप के माध्यम से मैसेज करके, या अपनी ऑनलाइन बुकिंग में लॉग इन करके रीशेड्यूल या कैंसल कर सकते हैं। कृपया अन्य रोगियों के लिए स्लॉट खाली करने के लिए पहले सूचना दें।",
            keywords = listOf("रीशेड्यूल", "कैंसल", "अपॉइंटमेंट", "कॉल", "व्हाट्सएप", "ऑनलाइन"),
            category = "appointments",
            language = "hi"
        ),
        KnowledgeBaseQA(
            id = "qa_130_hi",
            question = "क्या वरिष्ठ नागरिकों या गर्भवती महिलाओं के लिए अलग काउंटर है?",
            answer = "जबकि कोई समर्पित काउंटर नहीं है, वरिष्ठ नागरिकों और गर्भवती महिलाओं को रिसेप्शन पर प्राथमिकता सहायता मिलती है। कृपया तेज़ सपोर्ट के लिए आगमन पर स्टाफ को सूचित करें।",
            keywords = listOf("वरिष्ठ", "गर्भवती", "काउंटर", "रिसेप्शन", "प्राथमिकता"),
            category = "general",
            language = "hi"
        ),
        KnowledgeBaseQA(
            id = "qa_131_hi",
            question = "क्या कंसल्टेशन के लिए मुझे पिछले मेडिकल रिपोर्ट ले जाने की जरूरत है?",
            answer = "हाँ, पिछले मेडिकल रिपोर्ट, प्रिस्क्रिप्शन और टेस्ट रिजल्ट्स ले जाने से आपके डॉक्टर को सटीक निर्णय लेने में मदद मिलती है। कृपया लागू होने पर एक वैध आईडी और बीमा दस्तावेज भी लाएं।",
            keywords = listOf("रिपोर्ट", "प्रिस्क्रिप्शन", "टेस्ट", "कंसल्टेशन", "डॉक्टर", "आईडी"),
            category = "departments",
            language = "hi"
        ),
        KnowledgeBaseQA(
            id = "qa_132_hi",
            question = "क्या मैं फोन या वीडियो कॉल पर डॉक्टर से परामर्श कर सकता हूं?",
            answer = "हाँ, दूरस्थ रोगियों के लिए वीडियो/फोन के माध्यम से टेली-कंसल्टेशन उपलब्ध है। वर्चुअल कंसल्टेशन शेड्यूल करने के लिए +91 76977 44444 पर संपर्क करें या हमारी वेबसाइट पर व्हाट्सएप फीचर का उपयोग करें।",
            keywords = listOf("फोन", "वीडियो", "कॉल", "टेली-कंसल्टेशन", "डॉक्टर", "वर्चुअल"),
            category = "departments",
            language = "hi"
        ),
        KnowledgeBaseQA(
            id = "qa_133_hi",
            question = "मुझे अपने अपॉइंटमेंट समय से कितना पहले आना चाहिए?",
            answer = "हम रजिस्ट्रेशन और टोकन कलेक्शन के लिए अपने निर्धारित समय से 15-20 मिनट पहले आने की सलाह देते हैं। पहली बार की विजिट के लिए, कृपया डॉक्यूमेंटेशन के लिए अतिरिक्त 10 मिनट की अनुमति दें।",
            keywords = listOf("पहले", "समय", "अपॉइंटमेंट", "रजिस्ट्रेशन", "टोकन"),
            category = "appointments",
            language = "hi"
        ),
        KnowledgeBaseQA(
            id = "qa_134_hi",
            question = "क्या मैं UPI, गूगल पे, या फोनपे का उपयोग करके भुगतान कर सकता हूं?",
            answer = "हाँ, हम सभी बिलिंग काउंटरों पर गूगल पे, फोनपे और पेटीएम सहित UPI पेमेंट स्वीकार करते हैं, साथ ही नकद, कार्ड और नेट बैंकिंग भी।",
            keywords = listOf("UPI", "गूगल पे", "फोनपे", "पेटीएम", "भुगतान", "बिलिंग"),
            category = "general",
            language = "hi"
        ),
        KnowledgeBaseQA(
            id = "qa_135_hi",
            question = "क्या आप आइटमाइज्ड/विस्तृत बिल प्रदान करते हैं?",
            answer = "हाँ, आप भुगतान के दौरान या बाद में बिलिंग काउंटर पर कंसल्टेशन फीस, टेस्ट, दवाइयां और कमरा चार्ज दिखाने वाला आइटमाइज्ड बिल रिक्वेस्ट कर सकते हैं।",
            keywords = listOf("बिल", "आइटमाइज्ड", "विस्तृत", "फीस", "टेस्ट", "दवाई"),
            category = "general",
            language = "hi"
        ),
        KnowledgeBaseQA(
            id = "qa_136_hi",
            question = "क्या मैं अपना भुगतान रिसीप्ट ईमेल या व्हाट्सएप पर प्राप्त कर सकता हूं?",
            answer = "हाँ, भुगतान रिसीप्ट आपके रजिस्टर्ड व्हाट्सएप नंबर या ईमेल पर बिलिंग डेस्क पर रिक्वेस्ट पर शेयर किए जा सकते हैं।",
            keywords = listOf("रिसीप्ट", "ईमेल", "व्हाट्सएप", "भुगतान", "बिलिंग"),
            category = "general",
            language = "hi"
        ),
        KnowledgeBaseQA(
            id = "qa_137_hi",
            question = "अगर मेरे बिल में त्रुटि है तो मुझे क्या करना चाहिए?",
            answer = "कृपया तुरंत बिलिंग काउंटर स्टाफ को सूचित करें या +91 76977 44444 पर कॉल करें; हमारी टीम किसी भी विसंगति को जल्दी से वेरिफाई और करेक्ट करेगी。",
            keywords = listOf("बिल", "त्रुटि", "बिलिंग", "विसंगति", "करेक्ट"),
            category = "general",
            language = "hi"
        ),
        KnowledgeBaseQA(
            id = "qa_138_hi",
            question = "क्या आप अंतर्राष्ट्रीय स्वास्थ्य बीमा स्वीकार करते हैं?",
            answer = "वर्तमान में, हम मुख्य रूप से भारतीय स्वास्थ्य बीमा प्रदाताओं और आयुष्मान भारत जैसी सरकारी योजनाओं को स्वीकार करते हैं; अंतर्राष्ट्रीय पॉलिसी के लिए, कृपया केस-बाय-केस सहायता के लिए हमारे बिलिंग डेस्क से संपर्क करें।",
            keywords = listOf("अंतर्राष्ट्रीय", "बीमा", "भारतीय", "आयुष्मान भारत", "बिलिंग"),
            category = "insurance",
            language = "hi"
        ),
        KnowledgeBaseQA(
            id = "qa_139_hi",
            question = "क्या इलाज शुरू होने से पहले मैं अनुमानित लागत अनुमान प्राप्त कर सकता हूं?",
            answer = "हाँ, संक्षिप्त परामर्श के बाद, हमारा फ्रंट डेस्क प्रक्रियाओं, टेस्ट या एडमिशन के लिए अनुमानित लागत अनुमान प्रदान कर सकता है।",
            keywords = listOf("लागत", "अनुमान", "इलाज", "फ्रंट डेस्क", "प्रक्रिया"),
            category = "general",
            language = "hi"
        ),
        KnowledgeBaseQA(
            id = "qa_140_hi",
            question = "क्या बीमा रोगियों के लिए अलग बिलिंग काउंटर है?",
            answer = "जबकि कोई समर्पित काउंटर नहीं है, हमारी बिलिंग टीम में कैशलेस प्री-अथॉराइजेशन और डॉक्यूमेंटेशन में बीमा रोगियों की सहायता के लिए विशेष स्टाफ है।",
            keywords = listOf("बीमा", "बिलिंग", "काउंटर", "कैशलेस", "डॉक्यूमेंटेशन"),
            category = "insurance",
            language = "hi"
        ),
        KnowledgeBaseQA(
            id = "qa_141_hi",
            question = "अगर इलाज कैंसल हो तो मैं रिफंड कैसे क्लेम करूं?",
            answer = "रिफंड रिक्वेस्ट डॉक्टर अप्रूवल और ओरिजिनल पेमेंट प्रूफ के साथ बिलिंग डेस्क पर प्रोसेस किए जाते हैं; कृपया अपने विशिष्ट केस पर गाइडेंस के लिए +91 76977 44444 पर संपर्क करें।",
            keywords = listOf("रिफंड", "कैंसल", "इलाज", "बिलिंग", "डॉक्टर"),
            category = "insurance",
            language = "hi"
        ),
        KnowledgeBaseQA(
            id = "qa_142_hi",
            question = "क्या मैं किस्तों या EMI में अपना बिल भुगतान कर सकता हूं?",
            answer = "पात्र उपचारों और एडमिशन के लिए, पार्टनर बैंकों के माध्यम से EMI विकल्प उपलब्ध हो सकते हैं; कृपया एडमिशन के समय हमारे बिलिंग डेस्क से चर्चा करें।",
            keywords = listOf("EMI", "किस्त", "भुगतान", "बिल", "एडमिशन", "बैंक"),
            category = "general",
            language = "hi"
        ),
        KnowledgeBaseQA(
            id = "qa_143_hi",
            question = "क्या नकद भुगतान या जल्दी भुगतान के लिए छूट है?",
            answer = "हमारे स्वास्थ्य चेक-अप पैकेज पर छूट दरें उपलब्ध हैं; अन्य सेवाओं के लिए, कृपया कोई भी चल रहे ऑफर या छूट के लिए बिलिंग काउंटर पर पूछें।",
            keywords = listOf("छूट", "नकद", "भुगतान", "पैकेज", "ऑफर", "बिलिंग"),
            category = "general",
            language = "hi"
        ),
        KnowledgeBaseQA(
            id = "qa_144_hi",
            question = "अगर मैं ओरिजिनल खो दूं तो मैं डुप्लीकेट बिल कैसे प्राप्त करूं?",
            answer = "डुप्लीकेट बिल रिक्वेस्ट करने के लिए एक वैध आईडी और रोगी विवरण के साथ बिलिंग काउंटर पर जाएं; नॉमिनल प्रोसेसिंग फीस लागू हो सकती है, और कॉपी को आधिकारिक उपयोग के लिए स्टैम्प किया जाएगा।",
            keywords = listOf("डुप्लीकेट", "बिल", "खोना", "आईडी", "रोगी", "फीस"),
            category = "general",
            language = "hi"
        ),
        KnowledgeBaseQA(
            id = "qa_145_hi",
            question = "क्या वरिष्ठ नागरिकों या BPL कार्ड धारकों के लिए छूट है?",
            answer = "पात्र रोगियों के लिए छूट उपलब्ध हो सकती है। लागू लाभों के विवरण के लिए, कृपया इंश्योरेंस डिपार्टमेंट से संपर्क करें या रिसेप्शन से जांचें।",
            keywords = listOf("छूट", "वरिष्ठ", "BPL", "कार्ड", "लाभ", "इंश्योरेंस"),
            category = "general",
            language = "hi"
        ),
        KnowledgeBaseQA(
            id = "qa_146_hi",
            question = "क्या यहां आयुष्मान भारत / PM-JAY कार्ड स्वीकार किया जाता है?",
            answer = "हाँ, हम आयुष्मान भारत / PM-JAY कार्ड स्वीकार करते हैं। पात्रता, कवरेज और क्लेम सहायता के लिए, कृपया इंश्योरेंस डिपार्टमेंट से बात करें।",
            keywords = listOf("आयुष्मान भारत", "PM-JAY", "कार्ड", "पात्रता", "कवरेज"),
            category = "general",
            language = "hi"
        ),
        KnowledgeBaseQA(
            id = "qa_147_hi",
            question = "अस्पताल में बीमा-संबंधित क्वेरी के लिए मुझे किससे संपर्क करना चाहिए?",
            answer = "आप सभी बीमा क्वेरी के लिए आशीष रावले से संपर्क कर सकते हैं।",
            keywords = listOf("बीमा", "क्वेरी", "संपर्क", "आशीष", "रावले"),
            category = "insurance",
            language = "hi"
        ),
        KnowledgeBaseQA(
            id = "qa_148_hi",
            question = "अगर मुझे इमरजेंसी सर्जरी चाहिए लेकिन बीमा नहीं है तो क्या होगा?",
            answer = "इमरजेंसी केयर बीमा स्टेटस की परवाह किए बिना चिकित्सा आवश्यकता के आधार पर प्रदान किया जाता है। फाइनेंशियल सपोर्ट विकल्पों या बिलिंग गाइडेंस के लिए, कृपया स्टेबिलाइजेशन के बाद इंश्योरेंस डिपार्टमेंट से संपर्क करें।",
            keywords = listOf("इमरजेंसी", "सर्जरी", "बीमा", "केयर", "फाइनेंशियल", "सपोर्ट"),
            category = "insurance",
            language = "hi"
        ),
        KnowledgeBaseQA(
            id = "qa_149_hi",
            question = "अगर मैं अस्पताल में कोई व्यक्तिगत वस्तु खो दूं तो मुझे क्या करना चाहिए?",
            answer = "कृपया तुरंत रिसेप्शन डेस्क को सूचित करें या +91 76977 44444 पर कॉल करें; हमारा स्टाफ आपको खोजने और रिकवरी के लिए सिक्योरिटी के साथ कोऑर्डिनेट करने में मदद करेगा।",
            keywords = listOf("खोना", "व्यक्तिगत", "वस्तु", "रिसेप्शन", "सिक्योरिटी", "रिकवरी"),
            category = "general",
            language = "hi"
        ),
        KnowledgeBaseQA(
            id = "qa_150_hi",
            question = "क्या कोई लॉस्ट एंड फाउंड डेस्क या संपर्क है?",
            answer = "लॉस्ट आइटम्स मुख्य प्रवेश द्वार के पास रिसेप्शन/सिक्योरिटी डेस्क पर मैनेज किए जाते हैं - कृपया सहायता के लिए उनसे सीधे संपर्क करें या +91 76977 44444 पर कॉल करें।",
            keywords = listOf("लॉस्ट", "फाउंड", "डेस्क", "सिक्योरिटी", "संपर्क"),
            category = "general",
            language = "hi"
        ),
        KnowledgeBaseQA(
            id = "qa_151_hi",
            question = "क्या अस्पताल टैक्सी, ऑटो, या कैब बुक करने में मदद कर सकता है?",
            answer = "हाँ, हमारी रिसेप्शन टीम लोकल ट्रांसपोर्ट व्यवस्थित करने में आपकी सहायता कर सकती है, या आप सीधे हमारे ऑनलाइन ऑटो बुकिंग ऐप के माध्यम से ऑटो ट्रांसपोर्ट बुक कर सकते हैं।",
            keywords = listOf("टैक्सी", "ऑटो", "कैब", "बुकिंग", "ट्रांसपोर्ट", "रिसेप्शन"),
            category = "appointments",
            language = "hi"
        ),
        KnowledgeBaseQA(
            id = "qa_152_hi",
            question = "क्या मैं अपने नियोक्ता या स्कूल के लिए विजिट सारांश पत्र प्राप्त कर सकता हूं?",
            answer = "हाँ, आप कंसल्टेशन के बाद रिसेप्शन या बिलिंग डेस्क पर विजिट सारांश या मेडिकल सर्टिफिकेट रिक्वेस्ट कर सकते हैं; कृपया वेरिफिकेशन के लिए वैध आईडी लाएं।",
            keywords = listOf("विजिट", "सारांश", "पत्र", "नियोक्ता", "स्कूल", "सर्टिफिकेट"),
            category = "general",
            language = "hi"
        ),
        KnowledgeBaseQA(
            id = "qa_153_hi",
            question = "मैं रोगी के लिए टॉयलेट्रीज, ब्लैंकेट, या आवश्यक वस्तुएं कहां खरीद सकता हूं?",
            answer = "हमारा इन-हाउस फार्मेसी और नजदीकी कैफेटेरिया बेसिक आवश्यक वस्तुओं का स्टॉक रखते हैं; विशेष आइटम्स के लिए, कृपया रिसेप्शन से पूछें - वे प्रोक्योरमेंट या लोकल वेंडर गाइडेंस में मदद कर सकते हैं।",
            keywords = listOf("टॉयलेट्रीज", "ब्लैंकेट", "फार्मेसी", "कैफेटेरिया", "रिसेप्शन"),
            category = "facilities",
            language = "hi"
        ),
        KnowledgeBaseQA(
            id = "qa_154_hi",
            question = "क्या कोई क्लोकरूम या लॉकर सुविधा उपलब्ध है?",
            answer = "जबकि समर्पित लॉकर उपलब्ध नहीं हैं, रिसेप्शन छोटे कीमती सामान को अस्थायी रूप से स्टोर करने में मदद कर सकता है; हम सुरक्षा के लिए महत्वपूर्ण आइटम्स अपने पास रखने की सलाह देते हैं।",
            keywords = listOf("क्लोकरूम", "लॉकर", "सुविधा", "रिसेप्शन", "सुरक्षा"),
            category = "diagnostics",
            language = "hi"
        ),
        KnowledgeBaseQA(
            id = "qa_155_hi",
            question = "क्या आपके पास दृष्टिबाधित या सुनवाईबाधित रोगियों के लिए सुविधाएं हैं?",
            answer = "हाँ, अस्पताल रैम्प और लिफ्ट के साथ व्हीलचेयर-एक्सेसिबल है; कृपया व्यक्तिगत सहायता और कम्युनिकेशन सपोर्ट के लिए आगमन पर रिसेप्शन को सूचित करें।",
            keywords = listOf("दृष्टिबाधित", "सुनवाईबाधित", "व्हीलचेयर", "रैम्प", "रिसेप्शन"),
            category = "general",
            language = "hi"
        ),
        KnowledgeBaseQA(
            id = "qa_156_hi",
            question = "क्या वृद्ध या विकलांग रोगियों को प्राथमिकता सेवा मिल सकती है?",
            answer = "हाँ, वरिष्ठ नागरिकों और विकलांग रोगियों को रिसेप्शन और OPD में प्राथमिकता सहायता मिलती है - आगमन पर तेज़ सपोर्ट के लिए बस स्टाफ को सूचित करें।",
            keywords = listOf("वृद्ध", "विकलांग", "प्राथमिकता", "रिसेप्शन", "OPD", "स्टाफ"),
            category = "general",
            language = "hi"
        ),
        KnowledgeBaseQA(
            id = "qa_157_hi",
            question = "क्या माताओं के लिए प्राइवेट लैक्टेशन या नर्सिंग रूम है?",
            answer = "जबकि समर्पित लैक्टेशन रूम निर्दिष्ट नहीं है, रिसेप्शन पर रिक्वेस्ट पर नर्सिंग माताओं के लिए प्राइवेट कमरे और गेस्ट एरिया व्यवस्थित किए जा सकते हैं।",
            keywords = listOf("लैक्टेशन", "नर्सिंग", "माताएं", "प्राइवेट", "कमरा"),
            category = "general",
            language = "hi"
        ),
        KnowledgeBaseQA(
            id = "qa_158_hi",
            question = "एडमिशन के दौरान कई दिनों के लिए मैं अपना वाहन कहां पार्क कर सकता हूं?",
            answer = "रोगियों और आगंतुकों के लिए मुख्य प्रवेश द्वार के पास मुफ्त पार्किंग उपलब्ध है; एडमिशन के दौरान मल्टी-डे पार्किंग के लिए, कृपया सिक्योरिटी कोऑर्डिनेशन के लिए रिसेप्शन पर अपना वाहन रजिस्टर करें।",
            keywords = listOf("पार्किंग", "वाहन", "एडमिशन", "मल्टी-डे", "रिसेप्शन"),
            category = "facilities",
            language = "hi"
        ),
        KnowledgeBaseQA(
            id = "qa_159_hi",
            question = "क्या लैब टेस्ट के लिए डॉक्टर की प्रिस्क्रिप्शन की जरूरत है?",
            answer = "हाँ, अधिकांश डायग्नोस्टिक टेस्ट के लिए वैध डॉक्टर की प्रिस्क्रिप्शन आवश्यक है ताकि उचित टेस्टिंग और रिजल्ट्स की सटीक इंटरप्रिटेशन सुनिश्चित हो सके।",
            keywords = listOf("लैब", "टेस्ट", "प्रिस्क्रिप्शन", "डॉक्टर", "डायग्नोस्टिक"),
            category = "departments",
            language = "hi"
        ),
        KnowledgeBaseQA(
            id = "qa_160_hi",
            question = "क्या मैं बिना अपॉइंटमेंट के ब्लड टेस्ट के लिए वॉक-इन कर सकता हूं?",
            answer = "हाँ, हमारी 24/7 लैब ब्लड टेस्ट के लिए वॉक-इन रोगियों को स्वीकार करती है; बस अपनी प्रिस्क्रिप्शन के साथ लैब काउंटर पर रजिस्टर करें या इसे बिना प्रिस्क्रिप्शन भी कराया जा सकता है।",
            keywords = listOf("ब्लड", "टेस्ट", "वॉक-इन", "लैब", "काउंटर", "प्रिस्क्रिप्शन"),
            category = "appointments",
            language = "hi"
        ),
        KnowledgeBaseQA(
            id = "qa_161_hi",
            question = "मैं मूत्र या मल का नमूना कैसे दूं?",
            answer = "क्लीन कलेक्शन कंटेनर और निर्देश लैब काउंटर पर दिए जाते हैं; कृपया सटीक रिजल्ट्स के लिए हमारे स्टाफ द्वारा शेयर किए गए हाइजीन दिशानिर्देशों का पालन करें।",
            keywords = listOf("मूत्र", "मल", "नमूना", "लैब", "कंटेनर", "हाइजीन"),
            category = "general",
            language = "hi"
        ),
        KnowledgeBaseQA(
            id = "qa_162_hi",
            question = "क्या होम सैंपल कलेक्शन सेवा उपलब्ध है?",
            answer = "चयनित टेस्ट और क्षेत्रों के लिए होम सैंपल कलेक्शन उपलब्ध हो सकता है; कृपया एलिजिबिलिटी चेक करने और शेड्यूल करने के लिए +91 76977 44444 पर कॉल करें या व्हाट्सएप करें।",
            keywords = listOf("होम", "सैंपल", "कलेक्शन", "टेस्ट", "व्हाट्सएप"),
            category = "diagnostics",
            language = "hi"
        ),
        KnowledgeBaseQA(
            id = "qa_163_hi",
            question = "लैब रिपोर्ट कितने समय तक मान्य मानी जाती हैं?",
            answer = "रिपोर्ट वैलिडिटी टेस्ट और क्लिनिकल कंटेक्स्ट पर निर्भर करती है; आमतौर पर रूटीन रिपोर्ट 3-6 महीने तक मान्य होती हैं, लेकिन आपका डॉक्टर आपकी स्थिति के आधार पर सलाह देगा।",
            keywords = listOf("रिपोर्ट", "मान्य", "वैलिडिटी", "टेस्ट", "डॉक्टर", "महीने"),
            category = "diagnostics",
            language = "hi"
        ),
        KnowledgeBaseQA(
            id = "qa_164_hi",
            question = "क्या आप रिपोर्ट का हार्ड कॉपी और डिजिटल सॉफ्ट कॉपी दोनों प्रदान करते हैं?",
            answer = "हाँ, लैब रिपोर्ट आपके रजिस्टर्ड व्हाट्सएप नंबर पर डिजिटली शेयर की जाती हैं और रिपोर्ट काउंटर से प्रिंटेड कॉपी के रूप में भी कलेक्ट की जा सकती हैं।",
            keywords = listOf("रिपोर्ट", "हार्ड कॉपी", "डिजिटल", "व्हाट्सएप", "प्रिंटेड"),
            category = "general",
            language = "hi"
        ),
        KnowledgeBaseQA(
            id = "qa_165_hi",
            question = "अगर मुझे रिजल्ट पर शक हो तो क्या मैं टेस्ट दोबारा करा सकता हूं?",
            answer = "हाँ, डॉक्टर से परामर्श के बाद आप रिपीट टेस्ट रिक्वेस्ट कर सकते हैं; कृपया गाइडेंस के लिए लैब स्टाफ या ट्रीटिंग फिजिशियन से अपनी चिंताओं पर चर्चा करें।",
            keywords = listOf("टेस्ट", "दोबारा", "रिजल्ट", "शक", "डॉक्टर", "लैब"),
            category = "diagnostics",
            language = "hi"
        ),
        KnowledgeBaseQA(
            id = "qa_166_hi",
            question = "अगर मुझे यहां उपलब्ध नहीं वाला विशेष टेस्ट चाहिए तो क्या होगा?",
            answer = "ऑन-साइट उपलब्ध नहीं वाले विशेष टेस्ट के लिए, हमारी टीम एक्रेडिटेड एक्सटर्नल लैब्स के साथ कोऑर्डिनेट कर सकती है; कृपया सहायता के लिए अपने डॉक्टर या लैब डेस्क से परामर्श करें।",
            keywords = listOf("विशेष", "टेस्ट", "लैब", "डॉक्टर", "एक्सटर्नल"),
            category = "diagnostics",
            language = "hi"
        ),
        KnowledgeBaseQA(
            id = "qa_167_hi",
            question = "क्या मैं ऑनलाइन पेशेंट पोर्टल के माध्यम से अपनी रिपोर्ट एक्सेस कर सकता हूं?",
            answer = "डिजिटल रिपोर्ट व्हाट्सएप या ईमेल के माध्यम से शेयर की जाती हैं - कृपया लेटेस्ट अपडेट के लिए रिसेप्शन से जांचें।",
            keywords = listOf("ऑनलाइन", "रिपोर्ट", "पोर्टल", "व्हाट्सएप", "ईमेल"),
            category = "general",
            language = "hi"
        ),
        KnowledgeBaseQA(
            id = "qa_168_hi",
            question = "क्या फार्मेसी मुख्य भवन के अंदर या बाहर है?",
            answer = "हमारी इन-हाउस फार्मेसी आसान पहुंच के लिए मुख्य अस्पताल भवन के ग्राउंड फ्लोर पर अंदर स्थित है।",
            keywords = listOf("फार्मेसी", "भवन", "ग्राउंड फ्लोर", "अंदर", "पहुंच"),
            category = "pharmacy",
            language = "hi"
        ),
        KnowledgeBaseQA(
            id = "qa_169_hi",
            question = "क्या आपके पास सभी प्रिस्क्राइब्ड दवाइयां स्टॉक में हैं?",
            answer = "हम आवश्यक दवाइयों का व्यापक स्टॉक बनाए रखते हैं; अगर कोई विशिष्ट आइटम अनउपलब्ध हो तो हम 24 घंटे के भीतर प्रोक्योरमेंट की व्यवस्था कर सकते हैं।",
            keywords = listOf("दवाई", "स्टॉक", "प्रिस्क्राइब्ड", "प्रोक्योरमेंट", "24 घंटे"),
            category = "pharmacy",
            language = "hi"
        ),
        KnowledgeBaseQA(
            id = "qa_170_hi",
            question = "क्या जेनेरिक/कम लागत वाली दवाई के विकल्प उपलब्ध हैं?",
            answer = "हाँ, हम आपके डॉक्टर की प्रिस्क्रिप्शन और प्राथमिकता के अनुसार ब्रांडेड और किफायती जेनेरिक दवाई विकल्प दोनों ऑफर करते हैं।",
            keywords = listOf("जेनेरिक", "कम लागत", "दवाई", "ब्रांडेड", "प्रिस्क्रिप्शन"),
            category = "diagnostics",
            language = "hi"
        ),
        KnowledgeBaseQA(
            id = "qa_171_hi",
            question = "क्या आप दवाइयों की होम डिलीवरी ऑफर करते हैं?",
            answer = "नहीं, हमारे अस्पताल की इन-हाउस फार्मेसी दवाइयों की होम डिलीवरी ऑफर नहीं करती।",
            keywords = listOf("होम", "डिलीवरी", "दवाई", "फार्मेसी", "नहीं"),
            category = "pharmacy",
            language = "hi"
        ),
        KnowledgeBaseQA(
            id = "qa_172_hi",
            question = "क्या मैं बिना प्रिस्क्रिप्शन ओवर-द-काउंटर दवाइयां खरीद सकता हूं?",
            answer = "हाँ, पेन रिलीवर्स या विटामिन जैसी बेसिक OTC दवाइयां बिना प्रिस्क्रिप्शन उपलब्ध हैं; प्रिस्क्रिप्शन दवाइयों के लिए वैध डॉक्टर की प्रिस्क्रिप्शन आवश्यक है।",
            keywords = listOf("ओवर-द-काउंटर", "दवाई", "प्रिस्क्रिप्शन", "विटामिन", "पेन"),
            category = "pharmacy",
            language = "hi"
        ),
        KnowledgeBaseQA(
            id = "qa_173_hi",
            question = "क्या फार्मेसी इमरजेंसी प्रिस्क्रिप्शन के लिए 24/7 खुली है?",
            answer = "हाँ, हमारी फार्मेसी किसी भी समय इमरजेंसी और तत्काल प्रिस्क्रिप्शन जरूरतों को पूरा करने के लिए 24/7 ऑपरेट करती है।",
            keywords = listOf("फार्मेसी", "24/7", "इमरजेंसी", "प्रिस्क्रिप्शन", "खुली"),
            category = "hospital_info",
            language = "hi"
        ),
        KnowledgeBaseQA(
            id = "qa_174_hi",
            question = "अपयोग नहीं की गई या अनओपन्ड दवाइयां लौटाने की नीति क्या है?",
            answer = "सुरक्षा और रेगुलेटरी कारणों से, हम डिस्पेंस्ड दवाइयों पर रिटर्न स्वीकार नहीं कर सकते; कृपया खरीदारी से पहले अपनी प्रिस्क्रिप्शन डिटेल्स कन्फर्म करें।",
            keywords = listOf("रिटर्न", "दवाई", "सुरक्षा", "रेगुलेटरी", "प्रिस्क्रिप्शन"),
            category = "insurance",
            language = "hi"
        ),
        KnowledgeBaseQA(
            id = "qa_175_hi",
            question = "क्या आप कस्टम दवाइयां तैयार करते हैं?",
            answer = "कंपाउंडेड या कस्टम-प्रिपेयर्ड दवाइयों के बारे में प्रश्नों के लिए, कृपया हमारी फार्मेसी टीम या आपके ट्रीटिंग स्पेशलिस्ट से बात करें - वे उपलब्धता और विकल्पों पर सलाह दे सकते हैं।",
            keywords = listOf("कस्टम", "दवाई", "कंपाउंडेड", "फार्मेसी", "स्पेशलिस्ट"),
            category = "general",
            language = "hi"
        ),
        KnowledgeBaseQA(
            id = "qa_176_hi",
            question = "क्या आप मेडिकेशन काउंसलिंग या उपयोग निर्देश प्रदान करते हैं?",
            answer = "हाँ, हम मेडिकेशन काउंसलिंग और स्पष्ट उपयोग निर्देश प्रदान करते हैं। हमारे फार्मासिस्ट डोसेज, टाइमिंग और सावधानियों के बारे में आपको गाइड करने में खुश हैं।",
            keywords = listOf("मेडिकेशन", "काउंसलिंग", "फार्मासिस्ट", "डोसेज", "निर्देश"),
            category = "general",
            language = "hi"
        ),
        KnowledgeBaseQA(
            id = "qa_177_hi",
            question = "क्या कोई फार्मेसरी लिस्ट है जिसे मैं उपलब्ध दवाइयों के लिए चेक कर सकता हूं?",
            answer = "दवाई की उपलब्धता भिन्न हो सकती है। सबसे सटीक और अप-टू-डेट जानकारी के लिए, कृपया सीधे हमारे फार्मेसी स्टाफ से जांचें।",
            keywords = listOf("फार्मेसरी", "लिस्ट", "दवाई", "उपलब्धता", "फार्मेसी"),
            category = "diagnostics",
            language = "hi"
        ),
        KnowledgeBaseQA(
            id = "qa_178_hi",
            question = "क्या आपके पास आयुर्वेदिक या हर्बल दवाइयां हैं?",
            answer = "हाँ, हमारी फार्मेसी में आयुर्वेदिक और हर्बल प्रोडक्ट्स की रेंज स्टॉक है। विशिष्ट आइटम्स या सिफारिशों के लिए, कृपया फार्मेसी टीम से पूछें।",
            keywords = listOf("आयुर्वेदिक", "हर्बल", "दवाई", "फार्मेसी", "स्टॉक"),
            category = "pharmacy",
            language = "hi"
        ),
        KnowledgeBaseQA(
            id = "qa_179_hi",
            question = "क्या एम्बुलेंस में वेंटिलेटर या लाइफ-सपोर्ट उपकरण हैं?",
            answer = "हाँ, हम वेंटिलेटर, ऑक्सीजन और इमरजेंसी मॉनिटरिंग से सुसज्जित बेसिक और एडवांस्ड लाइफ-सपोर्ट एम्बुलेंस दोनों ऑफर करते हैं; कृपया +91 76977 44444 पर कॉल करते समय अपनी जरूरत बताएं।",
            keywords = listOf("एम्बुलेंस", "वेंटिलेटर", "लाइफ-सपोर्ट", "ऑक्सीजन", "मॉनिटरिंग"),
            category = "general",
            language = "hi"
        ),
        KnowledgeBaseQA(
            id = "qa_180_hi",
            question = "क्या मैं बुकिंग के बाद एम्बुलेंस का लोकेशन ट्रैक कर सकता हूं?",
            answer = "रियल-टाइम ट्रैकिंग वर्तमान में उपलब्ध नहीं है; हालांकि, हमारी डिस्पैच टीम कन्फर्मेशन पर अनुमानित आगमन समय और ड्राइवर कॉन्टैक्ट डिटेल्स प्रदान करेगी।",
            keywords = listOf("एम्बुलेंस", "ट्रैकिंग", "लोकेशन", "आगमन", "ड्राइवर"),
            category = "appointments",
            language = "hi"
        ),
        KnowledgeBaseQA(
            id = "qa_181_hi",
            question = "क्या एम्बुलेंस सेवा का चार्ज है, और कितना है?",
            answer = "एम्बुलेंस चार्ज दूरी, प्रकार (बेसिक/एडवांस्ड) और उपकरण के आधार पर भिन्न होते हैं; केस-स्पेसिफिक कोट के लिए कृपया +91 76977 44444 पर संपर्क करें।",
            keywords = listOf("एम्बुलेंस", "चार्ज", "दूरी", "कीमत", "बेसिक", "एडवांस्ड"),
            category = "general",
            language = "hi"
        ),
        KnowledgeBaseQA(
            id = "qa_182_hi",
            question = "क्या एम्बुलेंस मेरे घर या किसी अन्य स्थान से पिकअप कर सकती है?",
            answer = "हाँ, हमारी एम्बुलेंस बुरहानपुर और आस-पास के क्षेत्रों में घर, कार्यस्थल या अन्य स्थानों से रोगियों को पिकअप कर सकती हैं - बस बुकिंग करते समय सटीक पता शेयर करें।",
            keywords = listOf("एम्बुलेंस", "पिकअप", "घर", "बुरहानपुर", "पता"),
            category = "facilities",
            language = "hi"
        ),
        KnowledgeBaseQA(
            id = "qa_183_hi",
            question = "एम्बुलेंस बुलाते समय मुझे क्या जानकारी देनी चाहिए?",
            answer = "कृपया रोगी का नाम, उम्र, स्थिति/लक्षण, सटीक पिकअप स्थान, कॉन्टैक्ट नंबर और कोई विशेष जरूरत (जैसे वेंटिलेटर, व्हीलचेयर एक्सेस) शेयर करें।",
            keywords = listOf("एम्बुलेंस", "रोगी", "नाम", "उम्र", "लक्षण", "पिकअप"),
            category = "general",
            language = "hi"
        ),
        KnowledgeBaseQA(
            id = "qa_184_hi",
            question = "क्या परिवार का कोई सदस्य एम्बुलेंस में रोगी के साथ जा सकता है?",
            answer = "हाँ, अधिकांश मामलों में एक सेवक रोगी के साथ जा सकता है; एडवांस्ड लाइफ-सपोर्ट एम्बुलेंस के लिए, जगह सीमित हो सकती है - कृपया डिस्पैच टीम से कन्फर्म करें।",
            keywords = listOf("एम्बुलेंस", "परिवार", "सेवक", "रोगी", "एडवांस्ड"),
            category = "general",
            language = "hi"
        ),
        KnowledgeBaseQA(
            id = "qa_185_hi",
            question = "क्या आप इंटर-हॉस्पिटल रोगी ट्रांसफर के लिए एम्बुलेंस व्यवस्थित कर सकते हैं?",
            answer = "हाँ, हम उचित मेडिकल सपोर्ट के साथ सुरक्षित इंटर-हॉस्पिटल ट्रांसफर कोऑर्डिनेट करते हैं; कृपया व्यवस्था के लिए रोगी डिटेल्स और डेस्टिनेशन हॉस्पिटल के साथ +91 76977 44444 पर कॉल करें।",
            keywords = listOf("एम्बुलेंस", "ट्रांसफर", "इंटर-हॉस्पिटल", "मेडिकल", "कोऑर्डिनेट"),
            category = "general",
            language = "hi"
        ),
        KnowledgeBaseQA(
            id = "qa_186_hi",
            question = "क्या मैं एडमिशन कन्फर्म करने से पहले कमरा देख सकता हूं?",
            answer = "हाँ, आप एडमिशन कन्फर्म करने से पहले संक्षिप्त कमरा व्यू रिक्वेस्ट कर सकते हैं; हमारा एडमिशन डेस्क स्टाफ उपलब्धता और रोगी प्राथमिकता के आधार पर इसकी व्यवस्था करेगा।",
            keywords = listOf("कमरा", "एडमिशन", "व्यू", "उपलब्धता", "डेस्क"),
            category = "general",
            language = "hi"
        ),
        KnowledgeBaseQA(
            id = "qa_187_hi",
            question = "क्या कमरे में सेवक बेड या रिक्लाइनर प्रदान किया जाता है?",
            answer = "हाँ, प्राइवेट कमरों और निर्दिष्ट गेस्ट कमरों (102, 103, 221, 222) में सेवकों के लिए सीटिंग या रिक्लाइनर सुविधाएं शामिल हैं; कृपया एडमिशन पर एमेनिटीज कन्फर्म करें।",
            keywords = listOf("सेवक", "बेड", "रिक्लाइनर", "कमरा", "प्राइवेट"),
            category = "general",
            language = "hi"
        ),
        KnowledgeBaseQA(
            id = "qa_188_hi",
            question = "क्या रोगी के भोजन कमरे के चार्ज में शामिल हैं?",
            answer = "बेसिक स्वच्छ भोजन कमरा चार्ज में शामिल हैं; विशेष या डायटीशियन-अप्रूव्ड डाइट रिक्वेस्ट पर व्यवस्थित की जा सकती हैं, बिना किसी अतिरिक्त लागत का अपफ्रंट कम्युनिकेट किया जाएगा।",
            keywords = listOf("भोजन", "कमरा", "चार्ज", "डाइट", "डायटीशियन"),
            category = "general",
            language = "hi"
        ),
        KnowledgeBaseQA(
            id = "qa_189_hi",
            question = "क्या परिवार के लिए रोगी के कमरों में वाईफाई उपलब्ध है?",
            answer = "कमन एरियाज में कम्प्लिमेंट्री वाईफाई उपलब्ध है; इन-रूम कनेक्टिविटी के लिए, कृपया वर्तमान उपलब्धता और एक्सेस निर्देशों के लिए रिसेप्शन से जांचें।",
            keywords = listOf("वाईफाई", "कमरा", "रोगी", "परिवार", "एक्सेस"),
            category = "diagnostics",
            language = "hi"
        ),
        KnowledgeBaseQA(
            id = "qa_190_hi",
            question = "क्या मैं नर्सिंग स्टेशन के पास का कमरा रिक्वेस्ट कर सकता हूं?",
            answer = "हाँ, आप अतिरिक्त सुविधा के लिए नर्सिंग स्टेशन के पास कमरा रिक्वेस्ट कर सकते हैं; हम उपलब्धता और मेडिकल प्राथमिकता के आधार पर एकॉमोडेट करेंगे।",
            keywords = listOf("कमरा", "नर्सिंग", "स्टेशन", "पास", "उपलब्धता"),
            category = "hospital_info",
            language = "hi"
        ),
        KnowledgeBaseQA(
            id = "qa_191_hi",
            question = "क्या प्राइवेट कमरों में अटैच्ड बाथरूम हैं?",
            answer = "हाँ, सभी प्राइवेट कमरा कैटेगरी (डायमंड, प्लेटिनम, गोल्डन, सिल्वर, रूबी सूट) में रोगी की सुविधा और हाइजीन के लिए आवश्यक एमेनिटीज के साथ अटैच्ड बाथरूम शामिल हैं।",
            keywords = listOf("प्राइवेट", "कमरा", "बाथरूम", "डायमंड", "प्लेटिनम", "सिल्वर"),
            category = "general",
            language = "hi"
        ),
        KnowledgeBaseQA(
            id = "qa_192_hi",
            question = "क्या एडमिशन के बाद मैं अलग कमरा कैटेगरी में बदल सकता हूं?",
            answer = "हाँ, उपलब्धता के अधीन कमरा परिवर्तन या अपग्रेड संभव है, कृपया विकल्पों और किसी भी अतिरिक्त चार्ज के लिए एडमिशन या बिलिंग डेस्क से चर्चा करें।",
            keywords = listOf("कमरा", "बदलना", "अपग्रेड", "एडमिशन", "बिलिंग"),
            category = "general",
            language = "hi"
        ),
        KnowledgeBaseQA(
            id = "qa_193_hi",
            question = "क्या संक्रामक रोग के रोगियों के लिए आइसोलेशन कमरे हैं?",
            answer = "हाँ, हमारे पास संक्रामक रोग के रोगियों के लिए नामित आइसोलेशन कमरे और सख्त संक्रमण नियंत्रण प्रोटोकॉल हैं ताकि सभी की सुरक्षा सुनिश्चित हो सके, कृपया एडमिशन पर स्टाफ को सूचित करें।",
            keywords = listOf("आइसोलेशन", "संक्रामक", "रोग", "संक्रमण", "सुरक्षा"),
            category = "general",
            language = "hi"
        ),
        KnowledgeBaseQA(
            id = "qa_194_hi",
            question = "क्या आप बेड लिनेन, तौलिए और बेसिक टॉयलेट्रीज प्रदान करते हैं?",
            answer = "हाँ, सभी कमरों में साफ बेड लिनेन, तौलिए और बेसिक टॉयलेट्रीज प्रदान किए जाते हैं; अतिरिक्त आइटम्स आवश्यकतानुसार नर्सिंग स्टाफ से रिक्वेस्ट किए जा सकते हैं।",
            keywords = listOf("लिनेन", "तौलिया", "टॉयलेट्रीज", "बेड", "कमरा"),
            category = "general",
            language = "hi"
        ),
        KnowledgeBaseQA(
            id = "qa_195_hi",
            question = "नजदीकी शौचालय या वॉशरूम कहां है?",
            answer = "शौचालय हर फ्लोर पर लिफ्ट लॉबी और प्रतीक्षा क्षेत्रों के पास उपलब्ध हैं; कृपया साइनेज फॉलो करें या सबसे नजदीकी वाले के लिए किसी भी स्टाफ मेंबर से पूछें।",
            keywords = listOf("शौचालय", "वॉशरूम", "फ्लोर", "लिफ्ट", "प्रतीक्षा"),
            category = "facilities",
            language = "hi"
        ),
        KnowledgeBaseQA(
            id = "qa_196_hi",
            question = "क्या अंदर कैफेटेरिया, कैंटीन या फूड कोर्ट है?",
            answer = "हाँ, हमारा कैफेटेरिया मेन लॉबी के पास ग्राउंड फ्लोर पर स्थित है, जो सुबह 7 बजे से रात 9 बजे तक स्वच्छ शाकाहारी और नॉन-वेज भोजन परोसता है।",
            keywords = listOf("कैफेटेरिया", "कैंटीन", "भोजन", "शाकाहारी", "ग्राउंड फ्लोर"),
            category = "general",
            language = "hi"
        ),
        KnowledgeBaseQA(
            id = "qa_197_hi",
            question = "मुझे सुरक्षित पीने का पानी कहां मिलेगा?",
            answer = "सुरक्षित, फ़िल्टर किया हुआ पीने का पानी सभी फ्लोर पर प्रतीक्षा क्षेत्रों और नर्सिंग स्टेशनों के पास उपलब्ध है।",
            keywords = listOf("पानी", "पीने", "फ़िल्टर", "सुरक्षित", "फ्लोर"),
            category = "facilities",
            language = "hi"
        ),
        KnowledgeBaseQA(
            id = "qa_198_hi",
            question = "क्या कैंपस पर ATM या कैश विड्रॉल सुविधा है?",
            answer = "हाँ, आसान कैश विड्रॉल के लिए ATM उपलब्ध है, अतिरिक्त डिजिटल पेमेंट विकल्प पूरे अस्पताल में स्वीकार किए जाते हैं।",
            keywords = listOf("ATM", "कैश", "विड्रॉल", "डिजिटल", "पेमेंट"),
            category = "general",
            language = "hi"
        ),
        KnowledgeBaseQA(
            id = "qa_199_hi",
            question = "मुझे व्हीलचेयर या स्ट्रेचर सहायता कहां मिलेगी?",
            answer = "व्हीलचेयर और स्ट्रेचर रिसेप्शन डेस्क पर मुफ्त में उपलब्ध हैं, बस सहायता रिक्वेस्ट करें और हमारा सपोर्ट स्टाफ तुरंत आपकी मदद करेगा。",
            keywords = listOf("व्हीलचेयर", "स्ट्रेचर", "रिसेप्शन", "मुफ्त", "सहायता"),
            category = "facilities",
            language = "hi"
        ),
        KnowledgeBaseQA(
            id = "qa_200_hi",
            question = "मुझे मास्क, ग्लव्स, या सैनिटाइज़र जैसी पेशेंट केयर चीजें कहां खरीदनी चाहिए?",
            answer = "हमारी 24/7 इन-हाउस फार्मेसी ग्राउंड फ्लोर पर मास्क, ग्लव्स, सैनिटाइज़र और अन्य आवश्यक पेशेंट केयर आइटम्स स्टॉक करती है।",
            keywords = listOf("मास्क", "ग्लव्स", "सैनिटाइज़र", "फार्मेसी", "ग्राउंड फ्लोर"),
            category = "facilities",
            language = "hi"
        ),
        KnowledgeBaseQA(
            id = "qa_201_hi",
            question = "क्या कोई पेशेंट केयर कोऑर्डिनेटर या हेल्पडेस्क है जिससे मैं संपर्क कर सकूं?",
            answer = "हाँ, रिसेप्शन पर हमारा पेशेंट केयर डेस्क और फ्रंट डेस्क टीम (+91 76977 44444) आपके अस्पताल विजिट के दौरान कोऑर्डिनेशन, क्वेरी और सपोर्ट में मदद के लिए उपलब्ध है।",
            keywords = listOf("पेशेंट केयर", "हेल्पडेस्क", "रिसेप्शन", "कोऑर्डिनेशन", "सपोर्ट"),
            category = "general",
            language = "hi"
        ),
        KnowledgeBaseQA(
            id = "qa_202_hi",
            question = "मैं स्टाफ व्यवहार या सेवा गुणवत्ता के बारे में चिंता कैसे रिपोर्ट करूं?",
            answer = "आप रिसेप्शन डेस्क के साथ सीधे, +91 76977 44444 पर कॉल करके, या हमारी वेबसाइट पर 'हमसे संपर्क करें' फॉर्म का उपयोग करके फीडबैक या चिंता शेयर कर सकते हैं; सभी फीडबैक हमारी पेशेंट केयर टीम द्वारा जल्दी से रिव्यू किया जाता है।",
            keywords = listOf("फीडबैक", "शिकायत", "स्टाफ", "सेवा", "गुणवत्ता", "रिपोर्ट"),
            category = "general",
            language = "hi"
        ),
        KnowledgeBaseQA(
            id = "qa_203_hi",
            question = "क्या मैं अस्पताल में किसी अन्य विशेषज्ञ से द्वितीय राय ले सकता हूं?",
            answer = "हाँ, आप एक अन्य इन-हाउस विशेषज्ञ से द्वितीय राय रिक्वेस्ट कर सकते हैं; कृपया अपने ट्रीटिंग डॉक्टर से चर्चा करें या कंसल्टेशन कोऑर्डिनेट करने के लिए रिसेप्शन से संपर्क करें।",
            keywords = listOf("द्वितीय राय", "विशेषज्ञ", "डॉक्टर", "कंसल्टेशन", "रिसेप्शन"),
            category = "departments",
            language = "hi"
        ),
        KnowledgeBaseQA(
            id = "qa_204_hi",
            question = "बिना बेडसाइड पर होते हुए मैं अपने रोगी की स्थिति के नियमित अपडेट कैसे प्राप्त कर सकता हूं?",
            answer = "आप नर्सिंग स्टेशन पर अपने कॉन्टैक्ट डिटेल्स देकर फोन या व्हाट्सएप के माध्यम से नियमित अपडेट रिक्वेस्ट कर सकते हैं; ICU रोगियों के लिए, अपडेट निर्दिष्ट विजिटिंग घंटों के दौरान या क्लिनिकली उचित होने पर शेयर किए जाते हैं।",
            keywords = listOf("रोगी", "अपडेट", "नर्सिंग", "फोन", "व्हाट्सएप", "ICU"),
            category = "general",
            language = "hi"
        ),
        KnowledgeBaseQA(
            id = "qa_205_hi",
            question = "क्या सीटिंग, चार्जिंग पॉइंट, या रीडिंग मटीरियल वाला प्रतीक्षा लॉन्ज है?",
            answer = "हाँ, हमारे पास OPD और मेन लॉबी के पास आरामदायक प्रतीक्षा क्षेत्र हैं जिनमें सीटिंग, पीने का पानी और बेसिक एमेनिटीज हैं; चार्जिंग पॉइंट और रीडिंग मटीरियल की उपलब्धता भिन्न हो सकती है - कृपया वर्तमान सुविधाओं के लिए रिसेप्शन से पूछें।",
            keywords = listOf("प्रतीक्षा", "लॉन्ज", "सीटिंग", "चार्जिंग", "पानी"),
            category = "general",
            language = "hi"
        ),
        // ==================== LOCATION HINDI Q&A PAIRS ====================
        KnowledgeBaseQA(
            id = "qa_206_hi",
            question = "फार्मेसी कहां है?",
            answer = "फार्मेसी ग्राउंड फ्लोर पर, रिसेप्शन डेस्क के सामने सीधे स्थित है।",
            keywords = listOf("फार्मेसी", "ग्राउंड", "रिसेप्शन", "सामने", "कहां"),
            category = "facilities",
            language = "hi"
        ),
        KnowledgeBaseQA(
            id = "qa_207_hi",
            question = "पैथोलॉजी लैब कहां है?",
            answer = "पैथोलॉजी डिपार्टमेंट रिसेप्शन डेस्क के बाईं ओर स्थित है - बस वहां से साइन फॉलो करें।",
            keywords = listOf("पैथोलॉजी", "लैब", "बाईं", "रिसेप्शन", "कहां"),
            category = "facilities",
            language = "hi"
        ),
        KnowledgeBaseQA(
            id = "qa_208_hi",
            question = "ब्लड कलेक्शन सेंटर कहां है?",
            answer = "ब्लड कलेक्शन सेंटर अस्पताल के बेसमेंट में स्थित है।",
            keywords = listOf("ब्लड", "कलेक्शन", "बेसमेंट", "कहां"),
            category = "facilities",
            language = "hi"
        ),
        KnowledgeBaseQA(
            id = "qa_209_hi",
            question = "एक्स-रे रूम कहां है?",
            answer = "एक्स-रे रूम और अधिकांश स्कैन ग्राउंड फ्लोर पर हैं। MRI और CT स्कैन के लिए, कृपया बेसमेंट फ्लोर पर जाएं।",
            keywords = listOf("एक्स-रे", "ग्राउंड", "बेसमेंट", "MRI", "CT", "कहां"),
            category = "facilities",
            language = "hi"
        ),
        KnowledgeBaseQA(
            id = "qa_210_hi",
            question = "MRI स्कैन सेंटर कहां है?",
            answer = "MRI स्कैन सेंटर बेसमेंट फ्लोर पर स्थित है।",
            keywords = listOf("MRI", "स्कैन", "बेसमेंट", "कहां"),
            category = "facilities",
            language = "hi"
        ),
        KnowledgeBaseQA(
            id = "qa_211_hi",
            question = "CT स्कैन रूम कहां है?",
            answer = "CT स्कैन रूम भी बेसमेंट फ्लोर पर है, अन्य एडवांस्ड इमेजिंग सेवाओं के साथ।",
            keywords = listOf("CT", "स्कैन", "बेसमेंट", "इमेजिंग", "कहां"),
            category = "facilities",
            language = "hi"
        ),
        KnowledgeBaseQA(
            id = "qa_212_hi",
            question = "अल्ट्रासाउंड डिपार्टमेंट कहां है?",
            answer = "अल्ट्रासाउंड सेवाएं आमतौर पर ग्राउंड फ्लोर पर उपलब्ध हैं। सटीक स्थान के लिए, कृपया रिसेप्शन से जांचें या दिशात्मक साइन फॉलो करें।",
            keywords = listOf("अल्ट्रासाउंड", "ग्राउंड", "रिसेप्शन", "कहां"),
            category = "departments",
            language = "hi"
        ),
        KnowledgeBaseQA(
            id = "qa_213_hi",
            question = "रेडियोलॉजी डिपार्टमेंट कहां है?",
            answer = "अधिकांश रेडियोलॉजी सेवाएं, जिसमें एक्स-रे शामिल है, ग्राउंड फ्लोर पर हैं; एडवांस्ड इमेजिंग जैसे MRI/CT बेसमेंट पर हैं। रिसेप्शन आपको सही काउंटर तक गाइड कर सकता है।",
            keywords = listOf("रेडियोलॉजी", "एक्स-रे", "ग्राउंड", "बेसमेंट", "कहां"),
            category = "departments",
            language = "hi"
        ),
        KnowledgeBaseQA(
            id = "qa_214_hi",
            question = "डायग्नोस्टिक सेंटर कहां है?",
            answer = "हमारी मुख्य डायग्नोस्टिक सेवाएं ग्राउंड और बेसमेंट फ्लोर पर फैली हैं। विशिष्ट टेस्ट या डिपार्टमेंट के लिए, रिसेप्शन त्वरित दिशाएं प्रदान कर सकता है।",
            keywords = listOf("डायग्नोस्टिक", "ग्राउंड", "बेसमेंट", "रिसेप्शन", "कहां"),
            category = "facilities",
            language = "hi"
        ),
        KnowledgeBaseQA(
            id = "qa_215_hi",
            question = "इमरजेंसी रूम कहां है?",
            answer = "इमरजेंसी रूम मुख्य अस्पताल प्रवेश द्वार से स्पष्ट रूप से साइनपोस्टेड है। अगर आप अनिश्चित हैं, तो कृपया रिसेप्शन या किसी भी स्टाफ से पूछें - वे तुरंत आपको गाइड करेंगे।",
            keywords = listOf("इमरजेंसी", "रूम", "प्रवेश", "रिसेप्शन", "कहां"),
            category = "hospital_info",
            language = "hi"
        ),
        KnowledgeBaseQA(
            id = "qa_216_hi",
            question = "ICU कहां है?",
            answer = "ICU (KICU, MICU और NICU सहित) ऊपरी फ्लोर पर स्थित हैं। रोगी की जरूरत के आधार पर सटीक स्थान के लिए, कृपया रिसेप्शन से जांचें या इमरजेंसी साइनेज फॉलो करें।",
            keywords = listOf("ICU", "KICU", "MICU", "NICU", "ऊपरी", "कहां"),
            category = "facilities",
            language = "hi"
        ),
        KnowledgeBaseQA(
            id = "qa_217_hi",
            question = "कार्डियोलॉजी डिपार्टमेंट कहां है?",
            answer = "कार्डियोलॉजी सेवाएं ग्राउंड फ्लोर पर उपलब्ध हैं। रिसेप्शन आपको सही काउंटर या कंसल्टेशन रूम तक गाइड कर सकता है।",
            keywords = listOf("कार्डियोलॉजी", "ग्राउंड", "रिसेप्शन", "कहां"),
            category = "departments",
            language = "hi"
        ),
        KnowledgeBaseQA(
            id = "qa_218_hi",
            question = "पीडियाट्रिक्स वार्ड कहां है?",
            answer = "पीडियाट्रिक केयर ग्राउंड फ्लोर पर प्रदान किया जाता है। वार्ड-विशिष्ट दिशाओं के लिए, कृपया रिसेप्शन से पूछें या पीडियाट्रिक साइनेज देखें।",
            keywords = listOf("पीडियाट्रिक्स", "बाल", "वार्ड", "ग्राउंड", "कहां"),
            category = "facilities",
            language = "hi"
        ),
        KnowledgeBaseQA(
            id = "qa_219_hi",
            question = "मैटरनिटी वार्ड कहां है?",
            answer = "मैटरनिटी वार्ड ग्राउंड फ्लोर पर स्थित है। रिसेप्शन विजिटेशन गाइडलाइन और कमरा दिशाओं में सहायता कर सकता है।",
            keywords = listOf("मैटरनिटी", "वार्ड", "ग्राउंड", "रिसेप्शन", "कहां"),
            category = "facilities",
            language = "hi"
        ),
        KnowledgeBaseQA(
            id = "qa_220_hi",
            question = "लेबर रूम कहां है?",
            answer = "लेबर और डिलीवरी सेवाएं ऑपरेशन थिएटर (OT) एरिया के माध्यम से कोऑर्डिनेट की जाती हैं। तत्काल सहायता के लिए, कृपया रिसेप्शन या किसी नर्सिंग स्टाफ को अलर्ट करें।",
            keywords = listOf("लेबर", "डिलीवरी", "OT", "नर्सिंग", "कहां"),
            category = "facilities",
            language = "hi"
        ),
        KnowledgeBaseQA(
            id = "qa_221_hi",
            question = "NICU कहां है?",
            answer = "नियोनेटल ICU (NICU) तीसरे फ्लोर पर स्थित है। एक्सेस मेडिकल स्टाफ के माध्यम से कोऑर्डिनेट किया जाता है - कृपया विजिटर प्रोटोकॉल और दिशाओं के लिए रिसेप्शन से जांचें।",
            keywords = listOf("NICU", "नियोनेटल", "तीसरा", "फ्लोर", "कहां"),
            category = "facilities",
            language = "hi"
        ),
        KnowledgeBaseQA(
            id = "qa_222_hi",
            question = "न्यूरोलॉजी डिपार्टमेंट कहां है?",
            answer = "न्यूरोलॉजी सेवाएं ग्राउंड फ्लोर पर स्थित हैं। अपॉइंटमेंट चेक-इन या दिशाओं के लिए, कृपया रिसेप्शन से पूछें।",
            keywords = listOf("न्यूरोलॉजी", "ग्राउंड", "रिसेप्शन", "कहां"),
            category = "departments",
            language = "hi"
        ),
        KnowledgeBaseQA(
            id = "qa_223_hi",
            question = "डेंटल डिपार्टमेंट कहां है?",
            answer = "डेंटल केयर ग्राउंड फ्लोर पर प्रदान किया जाता है। रिसेप्शन आपको डेंटल क्लिनिक लोकेट करने और डॉक्टर की उपलब्धता कन्फर्म करने में मदद कर सकता है।",
            keywords = listOf("डेंटल", "दांत", "ग्राउंड", "रिसेप्शन", "कहां"),
            category = "departments",
            language = "hi"
        ),
        KnowledgeBaseQA(
            id = "qa_224_hi",
            question = "सर्जरी डिपार्टमेंट कहां है?",
            answer = "सर्जिकल कंसल्टेशन और प्री-ऑप केयर ग्राउंड फ्लोर पर कोऑर्डिनेट किए जाते हैं। सर्जन की उपलब्धता या अपॉइंटमेंट चेक-इन के लिए, कृपया रिसेप्शन से जांचें।",
            keywords = listOf("सर्जरी", "ग्राउंड", "सर्जन", "रिसेप्शन", "कहां"),
            category = "departments",
            language = "hi"
        ),
        KnowledgeBaseQA(
            id = "qa_225_hi",
            question = "ऑपरेशन थिएटर कहां है?",
            answer = "हमारे अस्पताल में विभिन्न फ्लोर पर 6 ऑपरेशन थिएटर हैं। चूंकि सर्जरी तत्कालता और स्पेशलिटी के आधार पर शेड्यूल की जाती हैं, रिसेप्शन आपको सही OT तक गाइड कर सकता है या आपकी प्रक्रिया के स्थान पर अपडेट प्रदान कर सकता है।",
            keywords = listOf("ऑपरेशन", "थिएटर", "OT", "फ्लोर", "रिसेप्शन", "कहां"),
            category = "facilities",
            language = "hi"
        ),
        KnowledgeBaseQA(
            id = "qa_226_hi",
            question = "कैफेटेरिया कहां है?",
            answer = "सभी डाइनिंग विकल्प अस्पताल के बैक गेट के पास स्थित हैं। जरूरत हो तो रिसेप्शन त्वरित दिशाएं प्रदान कर सकता है।",
            keywords = listOf("कैफेटेरिया", "डाइनिंग", "बैक गेट", "रिसेप्शन", "कहां"),
            category = "facilities",
            language = "hi"
        ),
        KnowledgeBaseQA(
            id = "qa_227_hi",
            question = "वेंडिंग मशीन कहां है?",
            answer = "हमारे कैंपस पर वेंडिंग मशीन नहीं हैं, लेकिन बैक गेट के पास कैंटीन में स्नैक्स और भोजन उपलब्ध हैं।",
            keywords = listOf("वेंडिंग", "मशीन", "कैंटीन", "बैक गेट", "कहां"),
            category = "facilities",
            language = "hi"
        ),
        KnowledgeBaseQA(
            id = "qa_228_hi",
            question = "रेस्टोरेंट कहां है?",
            answer = "सभी डाइनिंग विकल्प अस्पताल के बैक गेट के पास स्थित हैं। जरूरत हो तो रिसेप्शन त्वरित दिशाएं प्रदान कर सकता है।",
            keywords = listOf("रेस्टोरेंट", "डाइनिंग", "बैक गेट", "रिसेप्शन", "कहां"),
            category = "facilities",
            language = "hi"
        ),
        KnowledgeBaseQA(
            id = "qa_229_hi",
            question = "शौचालय कहां है?",
            answer = "शौचालय हर फ्लोर पर, आमतौर पर प्रतीक्षा क्षेत्रों और लिफ्ट के पास उपलब्ध हैं। अगर आप नजदीकी वाले का दिशा-निर्देश चाहते हैं तो कृपया रिसेप्शन या किसी स्टाफ से पूछें।",
            keywords = listOf("शौचालय", "वॉशरूम", "फ्लोर", "लिफ्ट", "कहां"),
            category = "facilities",
            language = "hi"
        ),
        KnowledgeBaseQA(
            id = "qa_230_hi",
            question = "प्रतीक्षा क्षेत्र कहां है?",
            answer = "आरामदायक सीटिंग एरिया ग्राउंड फ्लोर पर, मुख्य प्रवेश द्वार और डिपार्टमेंट केबिन के पास उपलब्ध हैं।",
            keywords = listOf("प्रतीक्षा", "ग्राउंड", "प्रवेश", "सीटिंग", "कहां"),
            category = "facilities",
            language = "hi"
        ),
        KnowledgeBaseQA(
            id = "qa_231_hi",
            question = "रिसेप्शन कहां है?",
            answer = "मुख्य रिसेप्शन डेस्क ग्राउंड फ्लोर पर, अस्पताल के प्रवेश द्वार पर सीधे स्थित है - किसी भी सहायता के लिए आपकी पहली पड़ाव।",
            keywords = listOf("रिसेप्शन", "ग्राउंड", "प्रवेश", "पहली", "कहां"),
            category = "facilities",
            language = "hi"
        ),
        KnowledgeBaseQA(
            id = "qa_232_hi",
            question = "हेल्प डेस्क कहां है?",
            answer = "एक समर्पित हेल्प डेस्क ग्राउंड फ्लोर पर विजिटरों को गाइड करने, क्वेरी का उत्तर देने और सपोर्ट प्रदान करने के लिए उपलब्ध है।",
            keywords = listOf("हेल्प डेस्क", "ग्राउंड", "विजिटर", "सपोर्ट", "कहां"),
            category = "facilities",
            language = "hi"
        ),
        KnowledgeBaseQA(
            id = "qa_233_hi",
            question = "रजिस्ट्रेशन काउंटर कहां है?",
            answer = "रोगी रजिस्ट्रेशन ग्राउंड फ्लोर पर, मुख्य रिसेप्शन एरिया से सटे काउंटर पर हैंडल किया जाता है।",
            keywords = listOf("रजिस्ट्रेशन", "काउंटर", "ग्राउंड", "रिसेप्शन", "कहां"),
            category = "appointments",
            language = "hi"
        ),
        KnowledgeBaseQA(
            id = "qa_234_hi",
            question = "बिलिंग काउंटर कहां है?",
            answer = "बिलिंग और पेमेंट सेवाएं ग्राउंड फ्लोर पर, मुख्य रिसेप्शन के पास समर्पित काउंटर पर उपलब्ध हैं।",
            keywords = listOf("बिलिंग", "काउंटर", "ग्राउंड", "पेमेंट", "रिसेप्शन", "कहां"),
            category = "facilities",
            language = "hi"
        ),
        KnowledgeBaseQA(
            id = "qa_235_hi",
            question = "इंश्योरेंस डेस्क कहां है?",
            answer = "इंश्योरेंस डेस्क ग्राउंड फ्लोर पर स्थित है। क्लेम सहायता, डॉक्यूमेंटेशन या पॉलिसी क्वेरी के लिए, कृपया डेस्क पर सीधे जाएं या सटीक दिशाओं के लिए रिसेप्शन से जांचें।",
            keywords = listOf("इंश्योरेंस", "डेस्क", "ग्राउंड", "क्लेम", "कहां"),
            category = "insurance",
            language = "hi"
        ),
        KnowledgeBaseQA(
            id = "qa_236_hi",
            question = "अपॉइंटमेंट काउंटर कहां है?",
            answer = "अपॉइंटमेंट ग्राउंड फ्लोर पर रिसेप्शन डेस्क पर सीधे बुक किए जा सकते हैं - या सुविधा के लिए हमारी वेबसाइट/व्हाट्सएप के माध्यम से।",
            keywords = listOf("अपॉइंटमेंट", "काउंटर", "ग्राउंड", "रिसेप्शन", "वेबसाइट", "कहां"),
            category = "appointments",
            language = "hi"
        ),
        KnowledgeBaseQA(
            id = "qa_237_hi",
            question = "पार्किंग लॉट कहां है?",
            answer = "जनरल पार्किंग मुख्य प्रवेश द्वार के सामने और बैक गेट के पास दोनों जगह उपलब्ध है। साइनेज फॉलो करें या गाइडेंस के लिए सिक्योरिटी से पूछें।",
            keywords = listOf("पार्किंग", "लॉट", "प्रवेश", "बैक गेट", "सिक्योरिटी", "कहां"),
            category = "facilities",
            language = "hi"
        ),
        KnowledgeBaseQA(
            id = "qa_238_hi",
            question = "विजिटर पार्किंग कहां है?",
            answer = "समर्पित विजिटर पार्किंग ग्राउंड लेवल पर फ्रंट (मुख्य प्रवेश) और बैक गेट एरिया दोनों जगह प्रदान की जाती है।",
            keywords = listOf("विजिटर", "पार्किंग", "ग्राउंड", "प्रवेश", "बैक गेट", "कहां"),
            category = "facilities",
            language = "hi"
        ),
        KnowledgeBaseQA(
            id = "qa_239_hi",
            question = "एम्बुलेंस बे कहां है?",
            answer = "एम्बुलेंस बे तेज़ इमरजेंसी एक्सेस के लिए मुख्य प्रवेश द्वार के सामने सीधे स्थित है। आगमन पर स्टाफ हमेशा सहायता के लिए तैयार है।",
            keywords = listOf("एम्बुलेंस", "बे", "प्रवेश", "इमरजेंसी", "स्टाफ", "कहां"),
            category = "facilities",
            language = "hi"
        ),
        KnowledgeBaseQA(
            id = "qa_240_hi",
            question = "लिफ्ट कहां है?",
            answer = "लिफ्ट ग्राउंड फ्लोर पर, सीढ़ी के पास, रिसेप्शन डेस्क के बगल में स्थित हैं।",
            keywords = listOf("लिफ्ट", "सीढ़ी", "रिसेप्शन", "ग्राउंड", "कहां"),
            category = "facilities",
            language = "hi"
        ),
        KnowledgeBaseQA(
            id = "qa_241_hi",
            question = "सीढ़ी कहां है?",
            answer = "सीढ़ियां ग्राउंड फ्लोर पर रिसेप्शन डेस्क के पास सभी फ्लोर तक आसान पहुंच के लिए सुविधाजनक रूप से स्थित हैं।",
            keywords = listOf("सीढ़ी", "ग्राउंड", "रिसेप्शन", "फ्लोर", "कहां"),
            category = "facilities",
            language = "hi"
        ),
        KnowledgeBaseQA(
            id = "qa_242_hi",
            question = "व्हीलचेयर एक्सेस कहां है?",
            answer = "व्हीलचेयर मुख्य प्रवेश द्वार पर सीधे उपलब्ध हैं - बस किसी भी स्टाफ मेंबर या सिक्योरिटी को सूचित करें ताकि तत्काल सहायता मिल सके।",
            keywords = listOf("व्हीलचेयर", "एक्सेस", "प्रवेश", "सिक्योरिटी", "सहायता", "कहां"),
            category = "facilities",
            language = "hi"
        ),
        KnowledgeBaseQA(
            id = "qa_243_hi",
            question = "रैम्प कहां है?",
            answer = "आसान एक्सेस के लिए रैम्प उपलब्ध है। कृपया इस्तेमाल करते समय किसी भी स्टाफ से गाइडेंस या सपोर्ट के लिए पूछें।",
            keywords = listOf("रैम्प", "एक्सेस", "स्टाफ", "सपोर्ट", "कहां"),
            category = "facilities",
            language = "hi"
        ),
        KnowledgeBaseQA(
            id = "qa_244_hi",
            question = "ATM कहां है?",
            answer = "ATM बैक एंट्रेंस के सामने, नाकल रेस्टोरेंट के बगल में उपलब्ध है।",
            keywords = listOf("ATM", "बैक", "एंट्रेंस", "नाकल", "रेस्टोरेंट", "कहां"),
            category = "facilities",
            language = "hi"
        ),
        KnowledgeBaseQA(
            id = "qa_245_hi",
            question = "लॉकर रूम कहां है?",
            answer = "लॉकर सुविधाएं बेसमेंट में उपलब्ध हैं। एक्सेस निर्देशों के लिए कृपया रिसेप्शन से जांचें।",
            keywords = listOf("लॉकर", "रूम", "बेसमेंट", "एक्सेस", "कहां"),
            category = "facilities",
            language = "hi"
        ),
        KnowledgeBaseQA(
            id = "qa_246_hi",
            question = "बच्चों का खेल का मैदान कहां है?",
            answer = "बच्चों के लिए एक छोटा खेल का क्षेत्र ग्राउंड फ्लोर पर OPD एरिया के भीतर स्थित है।",
            keywords = listOf("बच्चे", "खेल", "ग्राउंड", "OPD", "कहां"),
            category = "facilities",
            language = "hi"
        ),
        KnowledgeBaseQA(
            id = "qa_247_hi",
            question = "फैमिली लॉन्ज कहां है?",
            answer = "जबकि हमारे पास समर्पित फैमिली लॉन्ज नहीं है, हम अतिरिक्त आराम के लिए VIP लॉन्ज ऑफर करते हैं। कृपया एक्सेस डिटेल के लिए रिसेप्शन से पूछें।",
            keywords = listOf("फैमिली", "लॉन्ज", "VIP", "एक्सेस", "रिसेप्शन", "कहां"),
            category = "facilities",
            language = "hi"
        ),
        KnowledgeBaseQA(
            id = "qa_248_hi",
            question = "स्मोकिंग एरिया कहां है?",
            answer = "अस्पताल परिसर में कहीं भी धूम्रपान की अनुमति नहीं है - और अच्छे कारण से: धूम्रपान आपके स्वास्थ्य के लिए हानिकारक है और ठीक होने, सांस लेने और समग्र कल्याण को प्रभावित कर सकता है। हम सभी की रिकवरी और भलाई का समर्थन करने के लिए धूम्रपान-मुक्त वातावरण को प्रोत्साहित करते हैं।",
            keywords = listOf("स्मोकिंग", "धूम्रपान", "एरिया", "परिसर", "निषिद्ध", "स्वास्थ्य"),
            category = "facilities",
            language = "hi"
        ),
        KnowledgeBaseQA(
            id = "qa_249_hi",
            question = "एडमिन ऑफिस कहां है?",
            answer = "एडमिनिस्ट्रेटिव पूछताछ के लिए, कृपया रिसेप्शन से जांचें - वे आपको उचित ऑफिस या स्टाफ मेंबर तक डायरेक्ट करेंगे।",
            keywords = listOf("एडमिन", "ऑफिस", "रिसेप्शन", "स्टाफ", "कहां"),
            category = "facilities",
            language = "hi"
        ),
        KnowledgeBaseQA(
            id = "qa_250_hi",
            question = "HR डिपार्टमेंट कहां है?",
            answer = "HR-संबंधित मामले इंटरनली हैंडल किए जाते हैं। विजिटर एक्सेस या क्वेरी के लिए, कृपया रिसेप्शन के माध्यम से कोऑर्डिनेट करें।",
            keywords = listOf("HR", "डिपार्टमेंट", "विजिटर", "रिसेप्शन", "कहां"),
            category = "departments",
            language = "hi"
        ),
        KnowledgeBaseQA(
            id = "qa_251_hi",
            question = "मेडिकल रिकॉर्ड डिपार्टमेंट कहां है?",
            answer = "MRD बेसमेंट में स्थित है। रिकॉर्ड रिक्वेस्ट या सहायता के लिए, कृपया वर्किंग घंटों के दौरान विजिट करें या पहले रिसेप्शन से जांचें।",
            keywords = listOf("मेडिकल", "रिकॉर्ड", "MRD", "बेसमेंट", "कहां"),
            category = "departments",
            language = "hi"
        ),
        KnowledgeBaseQA(
            id = "qa_252_hi",
            question = "डिस्चार्ज काउंटर कहां है?",
            answer = "डिस्चार्ज के लिए, कृपया ग्राउंड फ्लोर पर बिलिंग काउंटर पर जाएं। आपके डॉक्टर मेडिकल क्लियरेंस कोऑर्डिनेट करेंगे, और रिसेप्शन आपको अंतिम चरणों से गाइड कर सकता है।",
            keywords = listOf("डिस्चार्ज", "काउंटर", "बिलिंग", "ग्राउंड", "क्लियरेंस", "कहां"),
            category = "facilities",
            language = "hi"
        ),
        KnowledgeBaseQA(
            id = "qa_285_hi",
            question = "डॉक्टर का कंसल्टेशन रूम कहां है?",
            answer = "कंसल्टेशन रूम स्पेशलिटी और शेड्यूल के अनुसार असाइन किए जाते हैं। अपने डॉक्टर के सटीक स्थान के लिए, कृपया रिसेप्शन से जांचें - वे तुरंत आपको गाइड करेंगे।",
            keywords = listOf("डॉक्टर", "कंसल्टेशन", "रूम", "रिसेप्शन", "स्पेशलिटी", "कहां"),
            category = "departments",
            language = "hi"
        ),
        KnowledgeBaseQA(
            id = "qa_286_hi",
            question = "नर्स स्टेशन कहां है?",
            answer = "नर्स स्टेशन हर फ्लोर पर, प्रत्येक वार्ड और डिपार्टमेंट के भीतर उपलब्ध हैं। अपने रोगी के कमरे के पास नर्सिंग डेस्क देखें या किसी भी स्टाफ से पूछें।",
            keywords = listOf("नर्स", "स्टेशन", "फ्लोर", "वार्ड", "डिपार्टमेंट", "कहां"),
            category = "facilities",
            language = "hi"
        ),
        KnowledgeBaseQA(
            id = "qa_287_hi",
            question = "स्टाफ रूम कहां है?",
            answer = "स्टाफ रूम दूसरे फ्लोर पर स्थित है। यह एरिया केवल अस्पताल पर्सनल के लिए है - विजिटर कृपया सहायता के लिए रिसेप्शन से जांचें।",
            keywords = listOf("स्टाफ", "रूम", "दूसरा", "फ्लोर", "पर्सनल", "कहां"),
            category = "facilities",
            language = "hi"
        ),
        KnowledgeBaseQA(
            id = "qa_288_hi",
            question = "ग्राउंड फ्लोर फार्मेसी कहां है?",
            answer = "ग्राउंड फ्लोर पर फार्मेसी बिलिंग/रिसेप्शन डेस्क के सामने सीधे तेज़ एक्सेस के लिए स्थित है।",
            keywords = listOf("फार्मेसी", "ग्राउंड", "बिलिंग", "रिसेप्शन", "कहां"),
            category = "facilities",
            language = "hi"
        ),
        KnowledgeBaseQA(
            id = "qa_289_hi",
            question = "पहले फ्लोर का OPD कहां है?",
            answer = "OPD सेवाएं कई फ्लोर पर फैली हैं। अपने विशेषज्ञ के OPD के सटीक स्थान के लिए, कृपया चेक-इन पर रिसेप्शन से कन्फर्म करें।",
            keywords = listOf("OPD", "पहला", "फ्लोर", "विशेषज्ञ", "रिसेप्शन", "कहां"),
            category = "facilities",
            language = "hi"
        ),
        KnowledgeBaseQA(
            id = "qa_290_hi",
            question = "दूसरे फ्लोर का ICU कहां है?",
            answer = "स्पेशलाइज्ड ICU (KICU सहित) दूसरे फ्लोर पर स्थित हैं। एक्सेस मेडिकल स्टाफ के माध्यम से कोऑर्डिनेट किया जाता है - कृपया विजिटर गाइडलाइन के लिए रिसेप्शन से जांचें।",
            keywords = listOf("ICU", "दूसरा", "फ्लोर", "KICU", "विजिटर", "कहां"),
            category = "facilities",
            language = "hi"
        ),
        KnowledgeBaseQA(
            id = "qa_291_hi",
            question = "ब्लड बैंक कहां है?",
            answer = "ब्लड बैंक बेसमेंट में स्थित है। डोनेशन, रिक्वेस्ट या इमरजेंसी के लिए, कृपया तत्काल कोऑर्डिनेशन के लिए रिसेप्शन से संपर्क करें।",
            keywords = listOf("ब्लड", "बैंक", "बेसमेंट", "डोनेशन", "इमरजेंसी", "कहां"),
            category = "facilities",
            language = "hi"
        ),
        KnowledgeBaseQA(
            id = "qa_292_hi",
            question = "सिक्योरिटी डेस्क कहां है?",
            answer = "सिक्योरिटी पर्सनल पूरे कैंपस में तैनात हैं। अगर आपको सहायता चाहिए तो किसी भी स्टाफ से पूछें - वे जल्दी से आपको सिक्योरिटी से कनेक्ट करेंगे।",
            keywords = listOf("सिक्योरिटी", "डेस्क", "कैंपस", "स्टाफ", "सहायता", "कहां"),
            category = "facilities",
            language = "hi"
        ),
        KnowledgeBaseQA(
            id = "qa_293_hi",
            question = "टैक्सी स्टैंड या कैब पिकअप पॉइंट कहां है?",
            answer = "कैब पिकअप और ड्रॉप-ऑफ आसान एक्सेस के लिए मुख्य प्रवेश द्वार के पास सुविधाजनक रूप से स्थित है।",
            keywords = listOf("टैक्सी", "कैब", "पिकअप", "प्रवेश", "एक्सेस", "कहां"),
            category = "facilities",
            language = "hi"
        ),
        KnowledgeBaseQA(
            id = "qa_294_hi",
            question = "विजिटर लॉकर सुविधा कहां है?",
            answer = "विजिटर लॉकर बेसमेंट में उपलब्ध हैं। एक्सेस निर्देशों और उपयोग गाइडलाइन के लिए कृपया रिसेप्शन से जांचें।",
            keywords = listOf("विजिटर", "लॉकर", "बेसमेंट", "एक्सेस", "गाइडलाइन", "कहां"),
            category = "facilities",
            language = "hi"
        ),
    )

     // Dynamic doctor Q&As injected from Strapi
     private val dynamicDoctorQAs = mutableListOf<KnowledgeBaseQA>()

     /**
      * Get English specialist noun form for a department
      * E.g., "Cardiology" -> "cardiologist", "Gynecology" -> "gynecologist"
      */
     private fun getSpecialistNoun(department: String): String {
         return when (department.lowercase()) {
             "cardiology" -> "cardiologist"
             "gynecology" -> "gynecologist"
             "orthopedics" -> "orthopedist"
             "dermatology" -> "dermatologist"
             "neurology" -> "neurologist"
             "pediatrics" -> "pediatrician"
             "ophthalmology" -> "ophthalmologist"
             "pathology" -> "pathologist"
             "radiology" -> "radiologist"
             "psychiatry" -> "psychiatrist"
             "anesthesiology" -> "anesthesiologist"
             "urology" -> "urologist"
             "internal medicine" -> "internist"
             "oncology" -> "oncologist"
             "nephrology" -> "nephrologist"
             "emergency medicine" -> "emergency expert"
             "pulmonology" -> "pulmonologist"
             "nutrition & dietetics" -> "nutritionist"
             "medical director" -> "director"
             else -> ""
         }
     }

     /**
      * Get Hindi specialist noun form for a department
      * E.g., "Gynecology" -> "गायनेकोलॉजिस्ट", "Cardiology" -> "कार्डियोलॉजिस्ट"
      */
     private fun getHindiSpecialistNoun(department: String): String {
         return when (department.lowercase()) {
             "cardiology" -> "कार्डियोलॉजिस्ट"
             "gynecology" -> "गायनेकोलॉजिस्ट"
             "orthopedics" -> "ऑर्थोपेडिक्स विशेषज्ञ"
             "dermatology" -> "त्वचा विशेषज्ञ"
             "neurology" -> "न्यूरोलॉजिस्ट"
             "pediatrics" -> "बाल चिकित्सक"
             "ophthalmology" -> "नेत्र विशेषज्ञ"
             "pathology" -> "पैथोलॉजिस्ट"
             "radiology" -> "रेडियोलॉजिस्ट"
             "psychiatry" -> "मनोचिकित्सक"
             "anesthesiology" -> "एनेस्थेसियोलॉजिस्ट"
             "urology" -> "यूरोलॉजिस्ट"
             "oncology" -> "ऑन्कोलॉजिस्ट"
             "nephrology" -> "नेफ्रोलॉजिस्ट"
             "internal medicine" -> "आंतरिक चिकित्सा विशेषज्ञ"
             "emergency medicine" -> "आपातकालीन चिकित्सा विशेषज्ञ"
             "pulmonology" -> "पल्मोनोलॉजिस्ट"
             "nutrition & dietetics" -> "पोषण विशेषज्ञ"
             "medical director" -> "चिकित्सा निदेशक"
             else -> ""
         }
     }

     /**
      * Inject doctor Q&As from Strapi into the knowledge base
      * Called whenever doctors are fetched or updated
      */
     fun injectDoctorQAs(doctors: List<Doctor>) {
         android.util.Log.d("HospitalKnowledgeBase", "Starting injection of ${doctors.size} doctors")

        // Clear previous dynamic doctor QAs
        dynamicDoctorQAs.clear()
        
        // Create Q&A pairs for each doctor
        doctors.forEach { doctor ->
            val doctorName = if (doctor.name.startsWith("Dr", ignoreCase = true)) 
                doctor.name else "Dr. ${doctor.name}"
            
            android.util.Log.d("HospitalKnowledgeBase", "🏥 Injecting: $doctorName | dept='${doctor.department}' | dept_hi='${doctor.departmentHi}' | specialization='${doctor.specialization}'")

            // Extract keywords from name (first name, last name, full name)
            val nameKeywords = mutableListOf<String>()
            val nameParts = doctor.name.replace("Dr.", "").replace("Dr", "").trim().split(" ")
            nameKeywords.addAll(nameParts.map { it.lowercase() })
            nameKeywords.add(doctor.name.lowercase())

            // Extract keywords from bio
            val bioKeywords = doctor.aboutBio.lowercase()
                .split(" ")
                .filter { it.length > 4 && !it.matches(Regex(".*[0-9].*")) } // Words longer than 4 chars, no numbers
                .distinct()
                .take(10) // Top 10 bio keywords

            // Q&A 1: Direct doctor name query with COMPLETE details - SPECIALTY FIRST
            val primarySpecialty = if (doctor.specialization.isNotBlank() &&
                                      !doctor.specialization.equals(doctor.department, ignoreCase = true)) {
                doctor.specialization
            } else {
                doctor.department
            }

             dynamicDoctorQAs.add(
                 KnowledgeBaseQA(
                     id = "dynamic_doc_${doctor.id}_name",
                     question = "Who is $doctorName?",
                     answer = "$doctorName is a $primarySpecialty. " +
                             "PRIMARY SPECIALTY: $primarySpecialty. " +
                             "Department: ${doctor.department}. " +
                             "Cabin: ${doctor.cabin}. " +
                             "Details: ${doctor.aboutBio}",
                     keywords = listOf(
                         doctor.name.lowercase(),
                         doctor.specialization?.lowercase() ?: "",
                         doctor.department.lowercase(),
                         "doctor",
                         "specialist",
                         "cabin",
                         doctor.cabin.lowercase(),
                         doctor.department.lowercase()
                     ).plus(nameKeywords).plus(bioKeywords).filter { it.isNotEmpty() }.distinct(),
                     category = "departments",
                     language = "en"
                 )
             )

             // Q&A 1 (Hindi): Direct doctor name query - HINDI VERSION
             dynamicDoctorQAs.add(
                 KnowledgeBaseQA(
                     id = "dynamic_doc_${doctor.id}_name_hi",
                     question = "$doctorName कौन हैं?",
                     answer = "$doctorName एक $primarySpecialty हैं। " +
                             "प्राथमिक विशेषता: $primarySpecialty। " +
                             "विभाग: ${doctor.department}। " +
                             "केबिन: ${doctor.cabin}। " +
                             "विवरण: ${doctor.aboutBio}",
                     keywords = listOf(
                         doctor.name.lowercase(),
                         doctor.specialization?.lowercase() ?: "",
                         doctor.department.lowercase(),
                         "डॉक्टर",
                         "विशेषज्ञ",
                         "केबिन",
                         doctor.cabin.lowercase()
                     ).plus(nameKeywords).plus(bioKeywords).filter { it.isNotEmpty() }.distinct(),
                     category = "departments",
                     language = "hi"
                 )
             )

              // Q&A 2: Department query with COMPLETE details - SPECIALTY FIRST
              dynamicDoctorQAs.add(
                  KnowledgeBaseQA(
                      id = "dynamic_doc_${doctor.id}_dept",
                      question = "Is there a ${doctor.department} specialist?",
                      answer = "$doctorName - PRIMARY SPECIALTY: $primarySpecialty. " +
                              "Department: ${doctor.department}. " +
                              "Cabin: ${doctor.cabin}. " +
                              "About: ${doctor.aboutBio}",
                      keywords = listOf(
                          doctor.department.lowercase(),
                          "specialist",
                          "doctor",
                          "department",
                          doctor.specialization?.lowercase() ?: "",
                          doctor.cabin.lowercase()
                      ).plus(nameKeywords).filter { it.isNotEmpty() }.distinct(),
                      category = "departments",
                      language = "en"
                  )
              )

              // Q&A 2 (Hindi): Department query - HINDI VERSION
              dynamicDoctorQAs.add(
                  KnowledgeBaseQA(
                      id = "dynamic_doc_${doctor.id}_dept_hi",
                      question = "क्या कोई ${doctor.department} विशेषज्ञ है?",
                      answer = "$doctorName - प्राथमिक विशेषता: $primarySpecialty। " +
                              "विभाग: ${doctor.department}। " +
                              "केबिन: ${doctor.cabin}। " +
                              "विवरण: ${doctor.aboutBio}",
                      keywords = listOf(
                          doctor.department.lowercase(),
                          "विशेषज्ञ",
                          "डॉक्टर",
                          "विभाग",
                          doctor.specialization?.lowercase() ?: "",
                          doctor.cabin.lowercase()
                      ).plus(nameKeywords).filter { it.isNotEmpty() }.distinct(),
                      category = "departments",
                      language = "hi"
                  )
              )

            // Q&A 3: Specialization query (if different from department) - SPECIALTY FIRST
             if (doctor.specialization.isNotBlank() && !doctor.specialization.equals(doctor.department, ignoreCase = true)) {
                 dynamicDoctorQAs.add(
                     KnowledgeBaseQA(
                         id = "dynamic_doc_${doctor.id}_spec",
                         question = "Who specializes in ${doctor.specialization}?",
                         answer = "$doctorName - PRIMARY SPECIALTY: ${doctor.specialization}. " +
                                 "Department: ${doctor.department}. " +
                                 "Experience: ${doctor.yearsOfExperience} years. " +
                                 "Cabin: ${doctor.cabin}. " +
                                 "Details: ${doctor.aboutBio}",
                         keywords = listOf(
                             doctor.specialization.lowercase(),
                             doctor.department.lowercase(),
                             "specialist",
                             "specialization",
                             doctor.name.lowercase()
                         ).plus(nameKeywords).filter { it.isNotEmpty() }.distinct(),
                         category = "departments",
                         language = "en"
                     )
                 )

                 // Q&A 3 (Hindi): Specialization query - HINDI VERSION
                 dynamicDoctorQAs.add(
                     KnowledgeBaseQA(
                         id = "dynamic_doc_${doctor.id}_spec_hi",
                         question = "कौन ${doctor.specialization} में विशेषज्ञ है?",
                         answer = "$doctorName - प्राथमिक विशेषता: ${doctor.specialization}। " +
                                 "विभाग: ${doctor.department}। " +
                                 "अनुभव: ${doctor.yearsOfExperience} वर्ष। " +
                                 "केबिन: ${doctor.cabin}। " +
                                 "विवरण: ${doctor.aboutBio}",
                         keywords = listOf(
                             doctor.specialization.lowercase(),
                             doctor.department.lowercase(),
                             "विशेषज्ञ",
                             "विशेषता",
                             doctor.name.lowercase()
                         ).plus(nameKeywords).filter { it.isNotEmpty() }.distinct(),
                         category = "departments",
                         language = "hi"
                     )
                 )
             }

             // Q&A 4: "Who is the [Department] specialist?" pattern - BRAND NEW
             dynamicDoctorQAs.add(
                 KnowledgeBaseQA(
                     id = "dynamic_doc_${doctor.id}_who_is_dept",
                     question = "Who is the ${doctor.department} specialist?",
                     answer = "$doctorName is a ${doctor.department} specialist. " +
                             "Department: ${doctor.department}. " +
                             "Cabin: ${doctor.cabin}. " +
                             "Details: ${doctor.aboutBio}",
                     keywords = listOf(
                         doctor.department.lowercase(),
                         "specialist",
                         "who is",
                         "doctor",
                         doctor.name.lowercase(),
                         doctor.specialization?.lowercase() ?: "",
                         // Add specialist noun forms (e.g., "cardiologist", "gynecologist")
                         getSpecialistNoun(doctor.department)
                     ).plus(nameKeywords).filter { it.isNotEmpty() }.distinct(),
                     category = "departments",
                     language = "en"
                 )
             )

            // Q&A 4 (Hindi): "Who is the [Department] specialist?" - HINDI VERSION
             dynamicDoctorQAs.add(
                 KnowledgeBaseQA(
                     id = "dynamic_doc_${doctor.id}_who_is_dept_hi",
                     question = "कौन ${doctor.department} विशेषज्ञ है?",
                     answer = "$doctorName एक ${doctor.department} विशेषज्ञ हैं। " +
                             "विभाग: ${doctor.department}। " +
                             "केबिन: ${doctor.cabin}। " +
                             "विवरण: ${doctor.aboutBio}",
                     keywords = listOf(
                         doctor.department.lowercase(),
                         "विशेषज्ञ",
                         "कौन है",
                         "कौन हैं",
                         "डॉक्टर",
                         doctor.name.lowercase(),
                         doctor.specialization?.lowercase() ?: "",
                         // Add specialist noun forms
                         getHindiSpecialistNoun(doctor.department)
                     ).plus(nameKeywords).filter { it.isNotEmpty() }.distinct(),
                     category = "departments",
                     language = "hi"
                 )
             )

             // Q&A 5: Combined doctor name + department query (e.g., "Dr. Sharma Cardiology" or "Cardiology Dr. Sharma")
              dynamicDoctorQAs.add(
                  KnowledgeBaseQA(
                      id = "dynamic_doc_${doctor.id}_name_dept",
                      question = "$doctorName ${doctor.department}",
                      answer = "$doctorName is a ${doctor.department} specialist. " +
                              "Department: ${doctor.department}. " +
                              "Cabin: ${doctor.cabin}. " +
                              "Details: ${doctor.aboutBio}",
                      keywords = listOf(
                          doctor.name.lowercase(),
                          doctor.department.lowercase(),
                          "specialist",
                          "doctor",
                          doctor.specialization?.lowercase() ?: "",
                          doctor.cabin.lowercase(),
                          "${doctor.name.replace("Dr.", "").lowercase().trim()} ${doctor.department.lowercase()}",
                          "${doctor.department.lowercase()} ${doctor.name.replace("Dr.", "").lowercase().trim()}"
                      ).plus(nameKeywords).filter { it.isNotEmpty() }.distinct(),
                      category = "departments",
                      language = "en"
                  )
              )

             // Q&A 5 (Hindi): Combined doctor name + department query - HINDI VERSION
              dynamicDoctorQAs.add(
                  KnowledgeBaseQA(
                      id = "dynamic_doc_${doctor.id}_name_dept_hi",
                      question = "$doctorName ${doctor.department}",
                      answer = "$doctorName एक ${doctor.department} विशेषज्ञ हैं। " +
                              "विभाग: ${doctor.department}। " +
                              "केबिन: ${doctor.cabin}। " +
                              "विवरण: ${doctor.aboutBio}",
                      keywords = listOf(
                          doctor.name.lowercase(),
                          doctor.department.lowercase(),
                          "विशेषज्ञ",
                          "डॉक्टर",
                          doctor.specialization?.lowercase() ?: "",
                          doctor.cabin.lowercase(),
                          "${doctor.name.replace("डॉ.", "").replace("Dr.", "").lowercase().trim()} ${doctor.department.lowercase()}",
                          "${doctor.department.lowercase()} ${doctor.name.replace("डॉ.", "").replace("Dr.", "").lowercase().trim()}"
                      ).plus(nameKeywords).filter { it.isNotEmpty() }.distinct(),
                      category = "departments",
                      language = "hi"
                  )
              )
        }

        android.util.Log.d("HospitalKnowledgeBase", "✅ Successfully injected ${dynamicDoctorQAs.size} dynamic doctor Q&As from ${doctors.size} doctors (avg ${dynamicDoctorQAs.size / maxOf(doctors.size, 1)} Q&As per doctor)")

        // Log sample of departments injected
        val uniqueDepts = doctors.map { it.department }.distinct()
        android.util.Log.d("HospitalKnowledgeBase", "📋 Departments found: $uniqueDepts")

        // Log doctors by department for debugging
        uniqueDepts.forEach { dept ->
            val doctorsInDept = doctors.filter { it.department.equals(dept, ignoreCase = true) }
            android.util.Log.d("HospitalKnowledgeBase", "  📍 $dept: ${doctorsInDept.map { it.name }.joinToString(", ")}")
        }
    }

    /**
     * Comprehensive Hindi/Romanized keyword mappings for cross-language matching
     * Maps Hindi words and romanized Hinglish to English equivalents
     * Covers: Facilities, Locations, Medical Terms, Departments, Common Words
     */
    private val hindiToEnglishKeywords = mapOf(
        // === FACILITIES & LOCATIONS ===
        "फार्मेसी" to "pharmacy",
        "फार्मसी" to "pharmacy",
        "pharmasy" to "pharmacy",
        "farmacie" to "pharmacy",
        "दवाखाना" to "pharmacy",
        "दवा" to "medicine",
        "दवाई" to "medicine",
        "पैथोलॉजी" to "pathology",
        "लैब" to "lab",
        "laboratory" to "lab",
        "प्रयोगशाला" to "lab",
        "आइसीयू" to "icu",
        "आईसीयू" to "icu",
        "icu" to "icu",
        "बिलिंग" to "billing",
        "बिल" to "billing",
        "काउंटर" to "counter",
        "रिसेप्शन" to "reception",
        "reception" to "reception",
        "स्वागत" to "reception",
        "ओपीडी" to "opd",
        "opd" to "opd",
        "बाह्य" to "opd",
        "आपातकालीन" to "emergency",
        "emergency" to "emergency",
        "इमरजेंसी" to "emergency",
        "कमरा" to "room",
        "room" to "room",
        "वार्ड" to "ward",
        "ward" to "ward",
        "बेड" to "bed",
        "bed" to "bed",
        "फ्लोर" to "floor",
        "floor" to "floor",
        "तल" to "floor",
        "basement" to "basement",
        "बेसमेंट" to "basement",
        "ground" to "ground",
        "ग्राउंड" to "ground",
        "भूतल" to "ground",
        
        // === DIRECTIONAL WORDS - EXPANDED ===
        "कहाँ" to "where",
        "कहां" to "where",
        "किधर" to "where",
        "कहा" to "where",
        "kedhar" to "where",
        "kidhar" to "where",
        "kahan" to "where",
        "kahaan" to "where",
        "कैसे" to "how",
        "kaise" to "how",
        "kyse" to "how",
        "जाना" to "go",
        "जाओ" to "go",
        "जाएं" to "go",
        "jana" to "go",
         "jao" to "go",
         "hai" to "is",
         "है" to "is",
         "हैं" to "are",  // Plural form - CRITICAL for "Directors कौन हैं?"
         "hei" to "is",
         "hey" to "is",
         "हो" to "be",
         "हो" to "are",
         "पास" to "near",
        "near" to "near",
        "नज़दीक" to "near",
        "दूर" to "far",
        "far" to "far",
        "आगे" to "front",
        "front" to "front",
        "पीछे" to "back",
        "back" to "back",
        "बाएं" to "left",
        "left" to "left",
        "दाएं" to "right",
        "right" to "right",
        "ऊपर" to "upper",
        "upper" to "upper",
        "नीचे" to "lower",
        "lower" to "lower",
        
        // === MEDICAL PROFESSIONALS ===
        "डॉक्टर" to "doctor",
        "डाक्टर" to "doctor",
        "doctor" to "doctor",
        "docter" to "doctor",
        "daktar" to "doctor",
        "चिकित्सक" to "doctor",
        "विशेषज्ञ" to "specialist",
        "specialist" to "specialist",
        "सलाहकार" to "consultant",
        "consultant" to "consultant",
        "सर्जन" to "surgeon",
        "surgeon" to "surgeon",
         "नर्स" to "nurse",
         "nurse" to "nurse",
         
         // === HOSPITAL LEADERSHIP (CRITICAL FOR TEMI) ===
         "निदेशक" to "director",
         "निदेशकों" to "directors",
         "डायरेक्टर्स" to "directors",  // Alternative Devanagari spelling
         "डायरेक्टर" to "director",     // Singular alternative spelling
         "directors" to "directors",
         "director" to "director",
         "संस्थापक" to "founder",
         "फाउंडर" to "founder",  // Alternative Devanagari spelling
         "founder" to "founder",
         "चेयरमैन" to "chairman",
         "chairman" to "chairman",
         "नेतृत्व" to "leadership",
         "leadership" to "leadership",
         "management" to "management",
         "प्रबंधन" to "management",
         "टीम" to "team",
         "team" to "team",
         
         // === COMMON NAMES (Hospital Leadership) ===
         "कबीर" to "kabir",
         "kabir" to "kabir",
         "कबीर सर" to "kabir",        // With honorific
         "देवांशी" to "devanshi",
         "devanshi" to "devanshi",
         "देवांशी मैडम" to "devanshi",  // With honorific
         "आनंद" to "anand",
         "anand" to "anand",
         "आनंद सर" to "anand",         // With honorific
         "प्रकाश" to "prakash",
         "prakash" to "prakash",
         "चौकसे" to "chouksey",
         "chouksey" to "chouksey",
         "सर" to "sir",                // Honorific for male
         "sir" to "sir",
         "मैडम" to "madam",            // Honorific for female
         "madam" to "madam",
         
         // === DEPARTMENTS & SPECIALIZATIONS ===
        "हृदय" to "heart",
        "heart" to "heart",
        "cardiology" to "cardiology",
        "कार्डियोलॉजी" to "cardiology",
        "हृदयरोग" to "cardiology",
        "मस्तिष्क" to "brain",
        "brain" to "brain",
        "neurology" to "neurology",
        "न्यूरोलॉजी" to "neurology",
        "मस्तिष्करोग" to "neurology",
        "हड्डी" to "bone",
        "bone" to "bone",
        "orthopedics" to "orthopedics",
        "ऑर्थोपेडिक्स" to "orthopedics",
        "हड्डीरोग" to "orthopedics",
        "त्वचा" to "skin",
        "skin" to "skin",
        "dermatology" to "dermatology",
        "डर्मेटोलॉजी" to "dermatology",
        "त्वचारोग" to "dermatology",
        "बच्चे" to "children",
        "children" to "children",
        "pediatrics" to "pediatrics",
        "पीडियाट्रिक्स" to "pediatrics",
        "बालरोग" to "pediatrics",
        "आंख" to "eye",
        "eye" to "eye",
        "eyes" to "eye",
        "नेत्र" to "eye",
        "ophthalmology" to "ophthalmology",
        "ऑप्थेल्मोलॉजी" to "ophthalmology",
        "नेत्ररोग" to "ophthalmology",
        "दांत" to "dental",
        "dental" to "dental",
        "dentist" to "dental",
        "दंत" to "dental",
        "surgery" to "surgery",
        "सर्जरी" to "surgery",
        "शल्यचिकित्सा" to "surgery",
         "स्त्री" to "gynecology",
         "gynecology" to "gynecology",
         "गायनेकोलॉजी" to "gynecology",
         "gynecologist" to "gynecology",
         "गायनेकोलॉजिस्ट" to "gynecology",
         "गाइनेकोलॉजिस्ट" to "gynecology",  // Alternative Hindi spelling (Devnagari variant)
         "गाइनेकोलॉजी" to "gynecology",      // Alternative Hindi spelling
         "मनोरोग" to "psychiatry",
         "psychiatry" to "psychiatry",
         "साइकियाट्री" to "psychiatry",
         "psychiatrist" to "psychiatry",
         "साइकियाट्रिस्ट" to "psychiatry",

         // === SPECIALIST NOUNS (map to department names) ===
         "cardiologist" to "cardiology",
         "नेत्र चिकित्सक" to "ophthalmology",
         "ophthalmologist" to "ophthalmology",
         "आंखों का डॉक्टर" to "ophthalmology",
         "pediatrician" to "pediatrics",
         "बाल चिकित्सक" to "pediatrics",
         "orthopedist" to "orthopedics",
         "हड्डी विशेषज्ञ" to "orthopedics",
         "dermatologist" to "dermatology",
         "त्वचा विशेषज्ञ" to "dermatology",
         "neurosurgeon" to "neurology",
         "न्यूरो सर्जन" to "neurology",
         "neurologist" to "neurology",
         "नेत्र विशेषज्ञ" to "neurology",

        // === COMMON MEDICAL TERMS ===
        "अस्पताल" to "hospital",
        "hospital" to "hospital",
        "अपॉइंटमेंट" to "appointment",
        "appointment" to "appointment",
        "समय" to "time",
        "time" to "time",
        "तारीख" to "date",
        "date" to "date",
        "मरीज" to "patient",
        "patient" to "patient",
        "रोगी" to "patient",
        "इलाज" to "treatment",
        "treatment" to "treatment",
        "उपचार" to "treatment",
        "जांच" to "test",
        "test" to "test",
        "परीक्षण" to "test",
        "रिपोर्ट" to "report",
        "report" to "report",
        "शुल्क" to "fee",
        "fee" to "fee",
        "फीस" to "fee",
        "खर्च" to "cost",
        "cost" to "cost",
        "कीमत" to "price",
        "price" to "price",
        
        // === COMMON QUESTIONS/WORDS ===
        "क्या" to "what",
        "what" to "what",
        "kya" to "what",
        "कौन" to "who",
        "who" to "who",
        "kaun" to "who",
        "कब" to "when",
        "when" to "when",
        "kab" to "when",
        "क्यों" to "why",
        "why" to "why",
        "kyon" to "why",
        "कितना" to "how much",
        "howmuch" to "how much",
        "kitna" to "how much",
        "मुझे" to "me",
        "me" to "me",
        "mujhe" to "me",
        "चाहिए" to "need",
        "need" to "need",
        "chahiye" to "need",
        "बताओ" to "tell",
        "tell" to "tell",
        "batao" to "tell",
        "बताइए" to "tell",
        "बुक" to "book",
        "book" to "book",
        "booking" to "book",
        "उपलब्ध" to "available",
        "available" to "available",
        "खुला" to "open",
        "open" to "open",
        "बंद" to "closed",
        "closed" to "closed",
        "हाँ" to "yes",
        "yes" to "yes",
        "हां" to "yes",
        "नहीं" to "no",
        "no" to "no",
        "nahin" to "no",
        
        // === FACILITIES SERVICES ===
        "x-ray" to "xray",
        "xray" to "xray",
        "एक्स-रे" to "xray",
        "एक्सरे" to "xray",
        "mri" to "mri",
        "एमआरआई" to "mri",
        "ct" to "ct",
        "सीटी" to "ct",
        "scan" to "scan",
        "स्कैन" to "scan",
        "ultrasound" to "ultrasound",
        "अल्ट्रासाउंड" to "ultrasound",
        "blood" to "blood",
        "खून" to "blood",
        "रक्त" to "blood",
        "collection" to "collection",
        "संग्रह" to "collection",
        
        // === DEPARTMENTS ===
        "विभाग" to "department",
        "department" to "department",
        "vibhag" to "department",
        "केबिन" to "cabin",
        "cabin" to "cabin",
        "kabin" to "cabin"
    )
    
    /**
     * Normalize query by translating Hindi/Hinglish terms to English for better matching
     */
    private fun normalizeQueryForSearch(query: String): String {
        var normalized = query.lowercase()
        
        // Replace Hindi/Hinglish keywords with English equivalents
        hindiToEnglishKeywords.forEach { (hindi, english) ->
            normalized = normalized.replace(hindi, " $english ")
        }
        
        return normalized.trim()
    }

     /**
      * Search knowledge base for relevant Q&As
      * ENHANCED: Better matching for doctor queries with partial matches and answer text search
      * NOW SUPPORTS HINDI QUERIES via keyword translation
      * Returns top N results sorted by relevance (includes dynamic doctor Q&As)
      * FIX: Keywords are also normalized for proper matching
      * FIX 2: Also checks raw query for Hindi keyword matches (backward compatibility)
      */
     fun search(userQuery: String, limit: Int = 3): List<KnowledgeBaseQA> {
         val lowerQuery = userQuery.lowercase()
         
         // Normalize query to translate Hindi/Hinglish to English for better matching
         val normalizedQuery = normalizeQueryForSearch(lowerQuery)
         
         android.util.Log.d("HospitalKnowledgeBase", "KB Search - Original: '$lowerQuery'")
         android.util.Log.d("HospitalKnowledgeBase", "KB Search - Normalized: '$normalizedQuery'")
         
         val queryWords = normalizedQuery.split(" ").filter { it.length > 2 } // Words longer than 2 chars
         val rawQueryWords = lowerQuery.split(" ").filter { it.length > 2 } // Raw query words (for Hindi matching)

         // Combine static and dynamic Q&As
         val allQAs = qaDatabase + dynamicDoctorQAs

         // Score each QA pair based on multiple matching criteria
         val results = allQAs.map { qa ->
             var score = 0
             
             // CRITICAL FIX: Normalize keywords for matching against normalized query
             // This ensures Hindi keywords like "कौन" get matched properly
             val normalizedKeywords = qa.keywords.map { kw -> normalizeQueryForSearch(kw) }
             
             // 1. Exact keyword matches (highest score)
             score += normalizedKeywords.count { keyword ->
                 normalizedQuery.contains(keyword)
             } * 3
             
             // 1B. Also check raw keywords against raw query (for pure Hindi queries)
             // This catches "Directors" in pure Hindi like "निदेशक"
             score += qa.keywords.count { keyword ->
                 lowerQuery.contains(keyword) && !normalizedQuery.contains(keyword)
             } * 3
             
             // 2. Partial word matches in keywords (medium score)
             score += normalizedKeywords.count { keyword ->
                 queryWords.any { word -> keyword.contains(word) || word.contains(keyword) }
             }
             
             // 2B. Partial match on raw keywords
             score += qa.keywords.count { keyword ->
                 rawQueryWords.any { word -> keyword.contains(word) || word.contains(keyword) }
             }
             
             // 3. Question text contains query words (good score for doctor name matching)
             val questionLower = normalizeQueryForSearch(qa.question.lowercase())
             score += queryWords.count { word -> questionLower.contains(word) } * 2
             
             // 3B. Question text contains raw query words
             val questionRaw = qa.question.lowercase()
             score += rawQueryWords.count { word -> questionRaw.contains(word) } * 2
             
             // 4. Answer text contains query words (helps with department/specialization matching)
             val answerLower = normalizeQueryForSearch(qa.answer.lowercase())
             score += queryWords.count { word -> answerLower.contains(word) }
             
             // 4B. Answer text contains raw query words
             val answerRaw = qa.answer.lowercase()
             score += rawQueryWords.count { word -> answerRaw.contains(word) }
             
             // Priority Boost: If it's a dynamic doctor entry, add a significant boost
             // to ensure it outranks static hospital info for same-keyword matches
             if (score > 0 && qa.id.startsWith("dynamic_doc_")) {
                 score += 10 // Significant boost to prioritize Strapi doctor data
             }
             
             qa to score
         }
             .filter { it.second > 0 }  // Only include matches
             .sortedByDescending { it.second }  // Sort by relevance
             .take(limit)
             .map { it.first }

         return results
     }

    /**
     * Get QA by category (e.g., all departments Q&As)
     */
    fun getByCategory(category: String): List<KnowledgeBaseQA> {
        return qaDatabase.filter { it.category == category }
    }

    /**
     * Get single best match for a query
     */
    fun findBestMatch(userQuery: String): KnowledgeBaseQA? {
        return search(userQuery, limit = 1).firstOrNull()
    }
}

