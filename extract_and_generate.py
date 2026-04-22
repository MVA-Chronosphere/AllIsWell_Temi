import json
import os

os.chdir('/Users/mva357/AndroidStudioProjects/AlliswellTemi')

with open('Hospital temi Dataset.json', 'r', encoding='utf-8') as f:
    data = json.load(f)

# Extract ALL items from Sheet1
sheet1 = data.get('Sheet1', [])
all_qa = []

for item in sheet1:
    if not item:
        continue
    question = (item.get('HOSPITAL TEMI') or '').strip()
    answer = (item.get('Column2') or '').strip()

    if question and len(question) > 3:
        if not answer:
            answer = "This information is available at our reception desk. Please contact us at +91 76977 44444 for more details."
        all_qa.append({'q': question, 'a': answer})

# Extract from Sheet2 (Hindi)
sheet2 = data.get('Sheet2', [])
hindi_qa = []

for item in sheet2:
    if not item:
        continue
    question = (item.get('Hindi Questions') or '').strip()
    answer = (item.get('Column2') or '').strip()

    if question and len(question) > 2:
        if not answer:
            answer = "कृपया रिसेप्शन डेस्क पर संपर्क करें। +91 76977 44444"
        hindi_qa.append({'q': question, 'a': answer})

total = len(all_qa) + len(hindi_qa)
print(f"English: {len(all_qa)}, Hindi: {len(hindi_qa)}, Total: {total}")

# Write Kotlin file
with open('HospitalKnowledgeBase_ALL.kt', 'w') as f:
    f.write(f'''package com.example.alliswelltemi.data

data class KnowledgeBaseQA(
    val id: String,
    val question: String,
    val answer: String,
    val keywords: List<String>,
    val category: String = "general",
    val language: String = "en"
)

object HospitalKnowledgeBase {{

    private val qaDatabase = listOf(
''')

    # Write English QAs
    for i, qa in enumerate(all_qa, 1):
        words = qa['q'].lower().split()
        kw = [w for w in words if len(w) > 3][:5] or words[:3]
        a = qa['a'][:250].replace('"', "'")
        f.write(f'        KnowledgeBaseQA(id="en{i}", question={repr(qa["q"])}, answer={repr(a)}, keywords={repr(kw)}),\n')

    # Write Hindi QAs
    for i, qa in enumerate(hindi_qa, 1):
        words = qa['q'].lower().split()
        kw = [w for w in words if len(w) > 2][:5] or words[:3]
        a = qa['a'][:250].replace('"', "'")
        f.write(f'        KnowledgeBaseQA(id="hi{i}", question={repr(qa["q"])}, answer={repr(a)}, keywords={repr(kw)}, language="hi"),\n')

    f.write(f''')

    fun search(userQuery: String, limit: Int = 3): List<KnowledgeBaseQA> {{
        val lowerQuery = userQuery.lowercase()
        return qaDatabase.map {{ qa ->
            val matchCount = qa.keywords.count {{ lowerQuery.contains(it) }}
            qa to matchCount
        }}.filter {{ it.second > 0 }}.sortedByDescending {{ it.second }}.take(limit).map {{ it.first }}
    }}

    fun getAll(): List<KnowledgeBaseQA> = qaDatabase
    fun getTotalCount(): Int = qaDatabase.size
}}
''')

print(f"✅ Generated HospitalKnowledgeBase_ALL.kt with {total} Q&As")

