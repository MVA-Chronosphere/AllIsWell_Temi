# Hindi Language Response - Visual Flow

## System Architecture

```
┌─────────────────────────────────────────────────────────────────┐
│                         USER INPUT                               │
│              (Voice via Temi Robot or Text)                      │
└───────────────────────┬─────────────────────────────────────────┘
                        │
                        ▼
┌─────────────────────────────────────────────────────────────────┐
│                    MainActivity.kt                               │
│              onAsrResult(asrResult, language)                    │
│                  processSpeech(text)                             │
└───────────────────────┬─────────────────────────────────────────┘
                        │
                        ▼
┌─────────────────────────────────────────────────────────────────┐
│                  SpeechOrchestrator.kt                           │
│                   analyze(text)                                  │
│  ┌──────────────────────────────────────────────────────────┐  │
│  │ Intent Detection (Bilingual)                             │  │
│  │ • English: "navigate", "book", "doctor"                  │  │
│  │ • Hindi: "ले चलो", "बुक", "डॉक्टर"                        │  │
│  └──────────────────────────────────────────────────────────┘  │
└───────────────────────┬─────────────────────────────────────────┘
                        │
                        ▼
┌─────────────────────────────────────────────────────────────────┐
│                  RagContextBuilder.kt                            │
│               detectLanguage(query)                              │
│  ┌──────────────────────────────────────────────────────────┐  │
│  │ Language Detection                                        │  │
│  │ ✓ Check Devanagari chars: [\u0900-\u097F]               │  │
│  │ ✓ Check Hindi keywords: "डॉक्टर", "कहां", "क्या"         │  │
│  │ ✓ Log detection results                                  │  │
│  │ → Returns: "hi" or "en"                                  │  │
│  └──────────────────────────────────────────────────────────┘  │
└───────────────────────┬─────────────────────────────────────────┘
                        │
                        ▼
┌─────────────────────────────────────────────────────────────────┐
│                  RagContextBuilder.kt                            │
│              buildOllamaPrompt(query, doctors)                   │
│  ┌──────────────────────────────────────────────────────────┐  │
│  │ IF language == "hi":                                      │  │
│  │   आप एक अस्पताल सहायक हैं                                │  │
│  │   CRITICAL: आपको ONLY हिंदी में ही जवाब देना है          │  │
│  │   NEVER respond in English                                │  │
│  │                                                            │  │
│  │ IF language == "en":                                      │  │
│  │   You are a hospital assistant                            │  │
│  │   CRITICAL: You MUST respond ONLY in English             │  │
│  │   NEVER respond in Hindi                                  │  │
│  └──────────────────────────────────────────────────────────┘  │
└───────────────────────┬─────────────────────────────────────────┘
                        │
                        ▼
┌─────────────────────────────────────────────────────────────────┐
│                      MainActivity.kt                             │
│                   callOllama(prompt)                             │
└───────────────────────┬─────────────────────────────────────────┘
                        │
                        ▼
┌─────────────────────────────────────────────────────────────────┐
│                     Ollama LLM (llama3:8b)                       │
│              Streaming Response Generation                       │
│  ┌──────────────────────────────────────────────────────────┐  │
│  │ • Processes prompt with language instructions            │  │
│  │ • Generates response in detected language                │  │
│  │ • Streams response chunks                                │  │
│  └──────────────────────────────────────────────────────────┘  │
└───────────────────────┬─────────────────────────────────────────┘
                        │
                        ▼
┌─────────────────────────────────────────────────────────────────┐
│                    MainActivity.kt                               │
│                   safeSpeak(response)                            │
└───────────────────────┬─────────────────────────────────────────┘
                        │
                        ▼
┌─────────────────────────────────────────────────────────────────┐
│                      Temi Robot TTS                              │
│               Speaks Response in Hindi/English                   │
└─────────────────────────────────────────────────────────────────┘
```

---

## Language Detection Logic Flow

```
┌─────────────────────────┐
│   Input: "डॉक्टर कहां हैं?" │
└────────────┬────────────┘
             │
             ▼
    ┌────────────────────┐
    │ Check Devanagari?   │
    │ Regex: [\u0900-\u097F] │
    └────────┬───────────┘
             │ YES ✓
             ▼
    ┌────────────────────┐
    │  Language = "hi"    │
    └────────┬───────────┘
             │
             ▼
    ┌────────────────────┐
    │   Build Prompt      │
    │   with Hindi        │
    │   Instructions      │
    └────────┬───────────┘
             │
             ▼
    ┌────────────────────┐
    │  Ollama Responds   │
    │    in Hindi        │
    └────────────────────┘


┌─────────────────────────┐
│ Input: "Where are doctors?"│
└────────────┬────────────┘
             │
             ▼
    ┌────────────────────┐
    │ Check Devanagari?   │
    │ Regex: [\u0900-\u097F] │
    └────────┬───────────┘
             │ NO ✗
             ▼
    ┌────────────────────┐
    │ Check Hindi Keywords?│
    │ "डॉक्टर", "कहां"...    │
    └────────┬───────────┘
             │ NO ✗
             ▼
    ┌────────────────────┐
    │  Language = "en"    │
    └────────┬───────────┘
             │
             ▼
    ┌────────────────────┐
    │   Build Prompt      │
    │   with English      │
    │   Instructions      │
    └────────┬───────────┘
             │
             ▼
    ┌────────────────────┐
    │  Ollama Responds   │
    │   in English       │
    └────────────────────┘
```

---

## Intent Detection Flow (Bilingual)

```
User Input: "डॉक्टर कहां हैं?" or "Where is the doctor?"
                        │
                        ▼
            ┌───────────────────────┐
            │ SpeechOrchestrator    │
            │    analyze(text)      │
            └───────────┬───────────┘
                        │
        ┌───────────────┴───────────────┐
        │                               │
        ▼                               ▼
┌──────────────┐              ┌──────────────┐
│ English KWs  │              │  Hindi KWs   │
│ "doctor"     │      OR      │  "डॉक्टर"    │
│ "navigate"   │              │  "ले चलो"    │
│ "book"       │              │  "बुक"       │
└──────┬───────┘              └──────┬───────┘
       │                             │
       └──────────────┬──────────────┘
                      │
                      ▼
            ┌───────────────────────┐
            │ Intent Detected       │
            │ • FIND_DOCTOR         │
            │ • NAVIGATE            │
            │ • BOOK                │
            │ • GENERAL             │
            └───────────┬───────────┘
                        │
                        ▼
            ┌───────────────────────┐
            │ Navigate to Screen    │
            │ or Build Context      │
            └───────────────────────┘
```

---

## Prompt Structure

```
┌─────────────────────────────────────────────────────────────┐
│                    OLLAMA PROMPT                             │
├─────────────────────────────────────────────────────────────┤
│                                                              │
│  [SYSTEM ROLE - Language Specific]                          │
│  आप एक अस्पताल सहायक हैं (Hindi)                            │
│  OR                                                          │
│  You are a hospital assistant (English)                     │
│                                                              │
│  CRITICAL: ONLY respond in [Hindi/English]                  │
│  NEVER respond in [English/Hindi]                           │
│                                                              │
├─────────────────────────────────────────────────────────────┤
│                                                              │
│  [KNOWLEDGE BASE CONTEXT]                                   │
│  Relevant Hospital Information (Q&As)                       │
│                                                              │
├─────────────────────────────────────────────────────────────┤
│                                                              │
│  [CONVERSATION HISTORY]                                     │
│  Previous Context (if exists)                               │
│                                                              │
├─────────────────────────────────────────────────────────────┤
│                                                              │
│  [HOSPITAL DATA]                                            │
│  • Hospital info                                            │
│  • Doctor list (filtered or all)                           │
│  • Popular locations                                        │
│                                                              │
├─────────────────────────────────────────────────────────────┤
│                                                              │
│  User: [User's question in detected language]              │
│                                                              │
├─────────────────────────────────────────────────────────────┤
│                                                              │
│  [12 CRITICAL INSTRUCTIONS]                                 │
│  1. LANGUAGE REQUIREMENT: ONLY [Hindi/English]              │
│  2. LANGUAGE DETECTION: User asked in [lang]                │
│  3. Use ONLY provided information                           │
│  4. Follow-up context handling                              │
│  5-11. [Other instructions...]                              │
│  12. REMEMBER: [Answer in detected language]                │
│                                                              │
└─────────────────────────────────────────────────────────────┘
```

---

## Error Handling Flow

```
User Input → Language Detection
                │
                ├─ Detection SUCCESS → Build Prompt → Ollama
                │                                        │
                │                           ┌────────────┴────────────┐
                │                           │                         │
                │                      SUCCESS ✓                   FAIL ✗
                │                           │                         │
                │                      Response in                   │
                │                   Detected Language                │
                │                           │                         │
                │                           ▼                         │
                │                    Robot Speaks                    │
                │                                                     │
                └─────────────────────────────────────────────────────┘
                                                                      │
                                                                      ▼
                                                         ┌────────────────────┐
                                                         │ Fallback Response  │
                                                         │  (In Detected      │
                                                         │   Language)        │
                                                         └────────────────────┘
```

---

## Key Features Summary

| Feature | Status | Details |
|---------|--------|---------|
| **Language Detection** | ✅ | Devanagari + keywords detection |
| **Hindi Keywords** | ✅ | Navigation, booking, doctor queries |
| **English Keywords** | ✅ | All existing keywords maintained |
| **Bilingual Prompts** | ✅ | Language-specific instructions |
| **Fallback Responses** | ✅ | Context-aware in both languages |
| **Logging** | ✅ | Comprehensive detection logging |
| **Intent Detection** | ✅ | Works with both languages |
| **TTS Support** | ✅ | Temi speaks Hindi/English |

---

## Testing Matrix

| Test Case | Input Language | Expected Output |
|-----------|----------------|-----------------|
| Doctor query | Hindi | Hindi response with doctor info |
| Doctor query | English | English response with doctor info |
| Navigation | Hindi | Hindi response with directions |
| Navigation | English | English response with directions |
| Booking | Hindi | Hindi response, navigate to booking |
| Booking | English | English response, navigate to booking |
| Follow-up | Hindi | Hindi response using context |
| Follow-up | English | English response using context |
| Fallback | Hindi | Hindi fallback message |
| Fallback | English | English fallback message |

---

**Visual Flow Version**: 1.0
**Status**: ✅ Complete
**Date**: April 23, 2026

