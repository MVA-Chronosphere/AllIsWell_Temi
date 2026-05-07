// Quick test to verify knowledge base search with new mappings
// Run this in Android Studio Logcat to see search results

import com.example.alliswelltemi.data.HospitalKnowledgeBase

fun testKnowledgeBaseSearch() {
    // Test queries in different languages
    val testQueries = listOf(
        "Directors कौन हैं?",           // Hinglish plural
        "directors kaun hain",           // Romanized Hinglish
        "निदेशक कौन हैं?",               // Pure Hindi plural
        "Founder कौन है?",               // Hinglish singular
        "founder kaun hai",              // Romanized
        "संस्थापक कौन हैं?",              // Pure Hindi
        "Chairman कौन है?",              // Hinglish
        "chairman kaun hai",             // Romanized
        "चेयरमैन कौन हैं?",               // Pure Hindi
        "Hospital की leadership कौन है?", // Hindi mixed
        "Hospital leadership kaun hai",   // Romanized
        "Kabir Chouksey कौन है?",        // Person query Hinglish
        "Devanshi Chouksey कौन है?",     // Person query Hinglish
        "Anand Prakash Chouksey कौन है?" // Full name Hinglish
    )
    
    println("\n========== KNOWLEDGE BASE SEARCH TEST ==========")
    testQueries.forEach { query ->
        val results = HospitalKnowledgeBase.search(query, limit = 3)
        println("\n📝 Query: '$query'")
        println("   Results: ${results.size} matches")
        results.forEachIndexed { idx, qa ->
            println("   [$idx] ID: ${qa.id}, Q: ${qa.question.take(50)}..., Score: ?")
        }
    }
    println("\n==========================================\n")
}

