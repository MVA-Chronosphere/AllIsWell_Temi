#!/usr/bin/env python3
import json

with open('Hospital temi Dataset.json', 'r', encoding='utf-8') as f:
    data = json.load(f)

# Extract Sheet1 (English)
sheet1 = data.get('Sheet1', [])
valid_qa = []

for item in sheet1:
    if not item:
        continue
    question = (item.get('HOSPITAL TEMI') or '').strip()
    answer = (item.get('Column2') or '').strip()

    # Only include if BOTH question and answer exist
    if question and answer and len(question) > 5:  # Skip short headers
        valid_qa.append({
            'question': question,
            'answer': answer
        })

# Extract Sheet2 (Hindi)
sheet2 = data.get('Sheet2', [])
hindi_qa = []

for item in sheet2:
    if not item:
        continue
    question = (item.get('Hindi Questions') or '').strip()
    answer = (item.get('Column2') or '').strip()

    if question and answer and len(question) > 3:
        hindi_qa.append({
            'question': question,
            'answer': answer
        })

total = len(valid_qa) + len(hindi_qa)

print(f"English Q&As: {len(valid_qa)}")
print(f"Hindi Q&As: {len(hindi_qa)}")
print(f"TOTAL VALID Q&A PAIRS: {total}")

# Generate Kotlin code
kotlin_code = '''package com.example.alliswelltemi.data

/**
 * Hospital Knowledge Base - ACTUAL Q&A pairs from Hospital temi Dataset.json
 * Auto-generated from JSON file
 * Updated: April 22, 2026
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

# Add English Q&As
for i, qa in enumerate(valid_qa, 1):
    # Extract keywords from question
    keywords = qa['question'].lower().split()[:5]  # First 5 words as keywords

    kotlin_code += f'''
        KnowledgeBaseQA(
            id = "en_{i}",
            question = {repr(qa['question'])},
            answer = {repr(qa['answer'][:200])},  // Truncated for size
            keywords = {repr(keywords)},
            category = "general",
            language = "en"
        ),'''

# Add Hindi Q&As
for i, qa in enumerate(hindi_qa, 1):
    keywords = qa['question'].lower().split()[:3]

    kotlin_code += f'''
        KnowledgeBaseQA(
            id = "hi_{i}",
            question = {repr(qa['question'])},
            answer = {repr(qa['answer'][:200])},  // Truncated for size
            keywords = {repr(keywords)},
            category = "general",
            language = "hi"
        ),'''

kotlin_code += '''
    )

    fun search(userQuery: String, limit: Int = 3): List<KnowledgeBaseQA> {
        val lowerQuery = userQuery.lowercase()
        return qaDatabase
            .map { qa ->
                val matchCount = qa.keywords.count { keyword ->
                    lowerQuery.contains(keyword)
                }
                qa to matchCount
            }
            .filter { it.second > 0 }
            .sortedByDescending { it.second }
            .take(limit)
            .map { it.first }
    }

    fun getByCategory(category: String): List<KnowledgeBaseQA> {
        return qaDatabase.filter { it.category == category }
    }

    fun findBestMatch(userQuery: String): KnowledgeBaseQA? {
        return search(userQuery, limit = 1).firstOrNull()
    }
}
'''

# Write to file
with open('HospitalKnowledgeBase_FULL.kt', 'w', encoding='utf-8') as f:
    f.write(kotlin_code)

print(f"\nGenerated: HospitalKnowledgeBase_FULL.kt with {total} Q&As")

