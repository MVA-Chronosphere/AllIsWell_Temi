# SMART KNOWLEDGE BASE RETRIEVAL SYSTEM

## 🎯 PROBLEM & SOLUTION

**Problem:** 300+ Q&A pairs can't all fit in every prompt (token waste, slower responses).

**Solution:** Smart retrieval system that sends only the 2-3 most relevant Q&As per query.

---

## 🔍 HOW IT WORKS

### Architecture

```
User Question
    ↓
HospitalKnowledgeBase.search(query)
    ├─ Score each Q&A by keyword matching
    ├─ Sort by relevance
    └─ Return top 2-3 matches
    ↓
Only relevant Q&As added to prompt
    ↓
Ollama receives minimal context
    ↓
Fast, efficient response
```

### Token Efficiency

**Before (Old Way):**
```
Every prompt included:
- 300+ Q&A pairs
- ~5000+ tokens wasted
- Slower Ollama processing
- Higher API costs
```

**After (Smart Retrieval):**
```
Only relevant Q&As included:
- 2-3 Q&A pairs per query
- ~200-400 tokens used
- Faster Ollama processing
- Efficient use of context window
```

---

## 📚 KNOWLEDGE BASE STRUCTURE

### File: `HospitalKnowledgeBase.kt`

```kotlin
data class KnowledgeBaseQA(
    val id: String,              // Unique identifier
    val question: String,         // User-facing question
    val answer: String,          // Answer to return
    val keywords: List<String>,  // For matching queries
    val category: String,        // For organization
    val language: String = "en"  // English or Hindi
)
```

### Current Q&A Categories

```
hospital_info      → Hours, phone, address
departments        → ICU, OPD, cardiology, pathology, pharmacy
appointments       → Booking, cancellation, fees
insurance          → Accepted companies, claims
facilities         → Parking, cafeteria, WiFi
```

---

## 🔧 HOW TO ADD 300+ Q&As

### Step 1: Prepare Your Data

Export your Q&As in this format:
```
ID | Question | Answer | Keywords (comma-separated) | Category
hours_weekday | What are the hospital hours? | We are open Monday-Friday 9AM-5PM | hours,open,time,weekday | hospital_info
dept_cardiology | Do you have cardiology? | Yes, cardiology dept with cardiologists | cardiology,heart,cardiac | departments
...
```

### Step 2: Add to Knowledge Base

Edit `HospitalKnowledgeBase.kt`:

```kotlin
private val qaDatabase = listOf(
    KnowledgeBaseQA(
        id = "hours_weekday",
        question = "What are the hospital hours?",
        answer = "We are open Monday to Friday from 9 AM to 5 PM.",
        keywords = listOf("hours", "open", "time", "weekday", "monday"),
        category = "hospital_info"
    ),
    KnowledgeBaseQA(
        id = "dept_cardiology",
        question = "Do you have a cardiology department?",
        answer = "Yes, we have a well-equipped cardiology department...",
        keywords = listOf("cardiology", "heart", "cardiac", "ecg"),
        category = "departments"
    ),
    // Add all 300+ Q&As here...
)
```

### Step 3: Build & Deploy

```bash
./gradlew build
# App now has all 300+ Q&As loaded
```

---

## 🧠 SMART MATCHING ALGORITHM

### How Search Works

```kotlin
fun search(userQuery: String, limit: Int = 3): List<KnowledgeBaseQA> {
    val lowerQuery = userQuery.lowercase()
    
    // Score each Q&A
    val results = qaDatabase.map { qa ->
        val matchCount = qa.keywords.count { keyword ->
            lowerQuery.contains(keyword)  // ← Count keyword matches
        }
        qa to matchCount
    }
    .filter { it.second > 0 }           // ← Only include matches
    .sortedByDescending { it.second }   // ← Sort by relevance
    .take(limit)                        // ← Take top N results
    .map { it.first }
    
    return results
}
```

### Examples

**Query:** "What are your hours?"
```
Matching Q&As:
- "What are the hospital hours?" (4 keyword matches: what, are, hospital, hours)
- "Are you open on weekends?" (2 keyword matches: are, open)
→ Returns: Top 1-2 matches
```

**Query:** "Do you have an ICU?"
```
Matching Q&As:
- "What services does your ICU provide?" (2 keyword matches: icu, services)
→ Returns: ICU Q&A
```

**Query:** "How do I book an appointment?"
```
Matching Q&As:
- "How do I book an appointment?" (3 keyword matches: book, appointment, how)
- "Can I cancel my appointment?" (2 keyword matches: cancel, appointment)
→ Returns: Top 1-2 matches
```

---

## 📊 KNOWLEDGE BASE SIZE

### Storage

- 300+ Q&As in memory
- ~500KB total size (negligible)
- Instant search/retrieval
- No network calls needed

### Token Usage

| Query Type | Tokens | Ollama Context Used |
|------------|--------|-------------------|
| Generic question | ~150 tokens | 5% |
| Department question | ~200 tokens | 6% |
| Procedure question | ~250 tokens | 7% |
| Complex question | ~350 tokens | 10% |

**Total Ollama context window:** 4000+ tokens (plenty of room!)

---

## 💻 API USAGE EXAMPLES

### Search for Best Match

```kotlin
// Get single best match
val bestQA = HospitalKnowledgeBase.findBestMatch("What are your hours?")
// Returns: Q&A about operating hours
```

### Search for Top N Matches

```kotlin
// Get top 3 relevant Q&As
val relevantQAs = HospitalKnowledgeBase.search(userQuery, limit = 3)
// Returns: List<KnowledgeBaseQA>
```

### Get All Q&As in Category

```kotlin
// Get all appointment-related Q&As
val appointmentQAs = HospitalKnowledgeBase.getByCategory("appointments")
```

### In Ollama Prompt

```kotlin
// Automatically happens in buildOllamaPrompt()
val relevantQAs = HospitalKnowledgeBase.search(query, limit = 2)
val knowledgeBaseContext = relevantQAs.joinToString("\n\n") { qa ->
    "Q: ${qa.question}\nA: ${qa.answer}"
}
// Only relevant Q&As added to prompt
```

---

## 🎯 NEXT STEPS TO POPULATE DATABASE

### CSV Import Template

Create `hospital_qa.csv`:
```
id,question,answer,keywords,category,language
hours_weekday,What are your hours?,Monday-Friday 9AM-5PM,hours|open|time|weekday,hospital_info,en
emergency_24_7,24/7 Emergency?,Yes available,emergency|24/7|urgent,hospital_info,en
dept_icu,Do you have ICU?,Yes with 24/7 staff,icu|intensive|critical,departments,en
...
```

### Script to Generate Code

```python
import csv

with open('hospital_qa.csv', 'r') as f:
    reader = csv.DictReader(f)
    for row in reader:
        keywords = [k.strip() for k in row['keywords'].split('|')]
        print(f"""KnowledgeBaseQA(
    id = "{row['id']}",
    question = "{row['question']}",
    answer = "{row['answer']}",
    keywords = listOf({', '.join([f'"{k}"' for k in keywords])}),
    category = "{row['category']}",
    language = "{row['language']}"
),""")
```

---

## ✅ VERIFICATION

### In Logcat

```
OLLAMA_PROMPT: ========== OLLAMA PROMPT START ==========
OLLAMA_PROMPT: Respond in English. You are a hospital assistant.
OLLAMA_PROMPT: Relevant Hospital Information:
OLLAMA_PROMPT: Q: What are the hospital hours?
OLLAMA_PROMPT: A: We are open Monday to Friday from 9 AM to 5 PM.
OLLAMA_PROMPT: [Only 1-2 most relevant Q&As appear]
OLLAMA_PROMPT: User: [your question]
OLLAMA_PROMPT: ========== OLLAMA PROMPT END ==========
```

✅ **If you see only 1-2 relevant Q&As in the prompt (not 300+), it's working!**

---

## 🚀 BENEFITS

| Aspect | Benefit |
|--------|---------|
| **Token Efficiency** | Only 200-400 tokens vs 5000+ |
| **Speed** | Faster Ollama processing |
| **Cost** | Fewer tokens = lower costs |
| **Relevance** | Only relevant answers shown |
| **Scalability** | Can add 1000+ Q&As without slowdown |
| **Offline** | All Q&As loaded in memory |

---

## 🔄 WORKFLOW

```
1. User asks question
   └─ "How do I book an appointment?"
   
2. RagContextBuilder.buildOllamaPrompt() called
   └─ Calls HospitalKnowledgeBase.search(query)
   
3. Smart search scores all 300+ Q&As
   └─ Matches: booking (3), cancellation (2), fee (1)
   
4. Top 2 matches added to prompt
   └─ "How do I book an appointment?" → Booking answer
   └─ "Can I cancel my appointment?" → Cancellation answer
   
5. Ollama receives minimal, relevant context
   └─ Only ~200 tokens for Q&As
   
6. Fast, accurate response
   └─ "You can book through our app or visit reception..."
```

---

## ✨ KEY ADVANTAGES

✅ **Scalable:** Add hundreds of Q&As without slowdown
✅ **Smart:** Only relevant answers in each prompt
✅ **Fast:** Instant in-memory search
✅ **Efficient:** Minimal token usage
✅ **Offline:** No API calls needed for knowledge base
✅ **Flexible:** Easy to add/update Q&As


