#!/usr/bin/env python3
import json

# Load the JSON file
with open('Hospital temi Dataset.json', 'r', encoding='utf-8') as f:
    data = json.load(f)

# Extract ALL items from Sheet1 (including questions without answers)
sheet1 = data.get('Sheet1', [])
all_qa = []

for item in sheet1:
    if not item:
        continue

    question = (item.get('HOSPITAL TEMI') or '').strip()
    answer = (item.get('Column2') or '').strip()

    # Include everything - questions with and without answers
    if question and len(question) > 3:  # Skip very short entries
        # If no answer, use a default response
        if not answer:
            answer = "This information is available at our reception desk. Please contact us at +91 76977 44444 for more details."

        all_qa.append({
            'question': question,
            'answer': answer
        })

# Extract ALL items from Sheet2 (Hindi)
sheet2 = data.get('Sheet2', [])
hindi_qa = []

for item in sheet2:
    if not item:
        continue

    question = (item.get('Hindi Questions') or '').strip()
    answer = (item.get('Column2') or '').strip()

    if question and len(question) > 2:
        if not answer:
            answer = "कृपया हमारे रिसेप्शन डेस्क पर संपर्क करें। +91 76977 44444 पर कॉल करें।"

        hindi_qa.append({
            'question': question,
            'answer': answer
        })

total = len(all_qa) + len(hindi_qa)

print(f"English Q&As: {len(all_qa)}")
print(f"Hindi Q&As: {len(hindi_qa)}")
print(f"TOTAL Q&As (including all questions): {total}")
print(f"\nFirst 5 English questions:")
for i, qa in enumerate(all_qa[:5], 1):
    print(f"{i}. {qa['question']}")

print(f"\nGenerating Kotlin file with {total} Q&As...")

# Generate Kotlin code with ALL Q&As
kotlin_code = '''package com.example.alliswelltemi.data

/**
 * Hospital Knowledge Base - ALL Q&As from Hospital temi Dataset.json
 * Includes all questions and answers from the hospital database
 * Questions without answers get default response
 * Auto-generated: April 22, 2026
 * Total Q&As: ''' + str(total) + '''
 */
data class KnowledgeBaseQA(
    val id: String,
    val question: String,
    val answer: String,
    val keywords: List<String>,
    val category: String = "general",
    val language: String = "en"
)

object HospitalKnowledgeBase {

    private val qaDatabase = listOf(
'''

# Add ALL English Q&As
for i, qa in enumerate(all_qa, 1):
    # Extract keywords from question
    words = qa['question'].lower().split()
    keywords = [w for w in words if len(w) > 3][:5]  # Words > 3 chars, first 5
    if not keywords:
        keywords = words[:3]  # Fallback to first 3 words

    # Escape quotes in answer
    answer_escaped = qa['answer'].replace('"', '\\"').replace('\n', ' ')[:300]  # Truncate long answers

    kotlin_code += f'''
        KnowledgeBaseQA(
            id = "en_{i}",
            question = {repr(qa['question'])},
            answer = {repr(answer_escaped)},
            keywords = {repr(keywords)},
            category = "general",
            language = "en"
        ),'''

# Add ALL Hindi Q&As
for i, qa in enumerate(hindi_qa, 1):
    words = qa['question'].lower().split()
    keywords = [w for w in words if len(w) > 2][:5]
    if not keywords:
        keywords = words[:3]

    answer_escaped = qa['answer'].replace('"', '\\"').replace('\n', ' ')[:300]

    kotlin_code += f'''
        KnowledgeBaseQA(
            id = "hi_{i}",
            question = {repr(qa['question'])},
            answer = {repr(answer_escaped)},
            keywords = {repr(keywords)},
            category = "general",
            language = "hi"
        ),'''

kotlin_code += '''
    )

    /**
     * Search knowledge base for relevant Q&As
     * Returns top N results sorted by relevance
     */
    fun search(userQuery: String, limit: Int = 3): List<KnowledgeBaseQA> {
        val lowerQuery = userQuery.lowercase()

        // Score each QA pair based on keyword matches
        val results = qaDatabase.map { qa ->
            val matchCount = qa.keywords.count { keyword ->
                lowerQuery.contains(keyword)
            }
            qa to matchCount
        }
        .filter { it.second > 0 }  // Only include matches
        .sortedByDescending { it.second }  // Sort by relevance
        .take(limit)
        .map { it.first }

        return results
    }

    /**
     * Get QA by category
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

    /**
     * Get all Q&As (for admin/debugging)
     */
    fun getAll(): List<KnowledgeBaseQA> {
        return qaDatabase
    }

    /**
     * Total count of Q&As loaded
     */
    fun getTotalCount(): Int {
        return qaDatabase.size
    }
}
'''

# Write to file
with open('HospitalKnowledgeBase_FULL.kt', 'w', encoding='utf-8') as f:
    f.write(kotlin_code)

print(f"\n✅ Generated: HospitalKnowledgeBase_FULL.kt")
print(f"   Total lines: {len(kotlin_code.split(chr(10)))}")
print(f"   Total Q&As: {total}")

