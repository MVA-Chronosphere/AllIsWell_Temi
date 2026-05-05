package com.example.alliswelltemi

import com.example.alliswelltemi.data.Doctor
import com.example.alliswelltemi.data.HospitalKnowledgeBase
import org.junit.Test
import org.junit.Assert.*

class HindiRAGTest {

    @Test
    fun testHindiKeywordTranslation() {
        // "kahan" should be translated to "where"
        val query = "pharmacy kahan hai"
        val results = HospitalKnowledgeBase.search(query)
        
        // Should find pharmacy related QA
        assertTrue("Should find results for pharmacy", results.any { it.answer.lowercase().contains("pharmacy") })
    }

    @Test
    fun testHinglishDoctorQuery() {
        val testDoctors = listOf(
            Doctor(
                id = "1",
                name = "Dr. Rajesh Sharma",
                department = "Cardiology",
                yearsOfExperience = 15,
                aboutBio = "Expert heart surgeon",
                cabin = "3A",
                specialization = "Heart Surgery"
            )
        )
        HospitalKnowledgeBase.injectDoctorQAs(testDoctors)

        val query = "rajesh sharma kaun hai" // "kaun" translates to "who"
        val results = HospitalKnowledgeBase.search(query)

        assertNotNull(results)
        assertTrue("Should find Dr. Rajesh Sharma", results.any { it.answer.contains("Rajesh Sharma") })
    }

    @Test
    fun testHindiDepartmentQuery() {
        val testDoctors = listOf(
            Doctor(
                id = "2",
                name = "Dr. Priya Verma",
                department = "Neurology",
                yearsOfExperience = 10,
                aboutBio = "Expert in brain and nerves",
                cabin = "4B",
                specialization = "Neurology"
            )
        )
        HospitalKnowledgeBase.injectDoctorQAs(testDoctors)

        val query = "neurology doctor kahan milenge" // "kahan" -> "where"
        val results = HospitalKnowledgeBase.search(query)

        assertTrue("Should find Neurology related results", results.any { it.answer.contains("Priya Verma") || it.category == "departments" })
    }
}
