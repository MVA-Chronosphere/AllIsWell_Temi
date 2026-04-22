# OLLAMA Wait Fix - Complete Response Before Speaking

## Problem
Temi was answering Q&A questions **directly instead of waiting for the Ollama response**. Looking at the logs:
- 16:46:14.259 - ASR Result received: "what is the email of this Hospital"
- 16:46:14.376 - Ollama streaming starts
- 16:46:25.062 - **Ollama response completes (10+ seconds later)**
- 16:46:28.852 - Response finally processed

The issue was that the code was calling `speakStreamingChunk()` on **every chunk** as it arrived from Ollama, instead of waiting for the complete response.

## Root Cause
In `MainActivity.kt`, the `callOllama()` function was immediately speaking each streaming chunk:

```kotlin
// OLD - BROKEN CODE
OllamaClient.generateStreaming(ollamaRequest).collect { chunk ->
    fullResponse.append(chunk)
    
    // Stream chunks to TTS for real-time speech - TOO EARLY!
    withContext(Dispatchers.Main) {
        speakStreamingChunk(chunk)  // Speaking individual words before complete response
    }
}
```

This caused Temi to:
1. Receive chunks like "The", "email", "for", "All", "Is", "Well"
2. Speak each word individually as it arrived
3. Create a garbled, incomplete response

## Solution
Modified `callOllama()` to **buffer all chunks and speak the complete response only after streaming finishes**:

```kotlin
// NEW - FIXED CODE
OllamaClient.generateStreaming(ollamaRequest).collect { chunk ->
    fullResponse.append(chunk)
    // IMPORTANT: Do NOT speak streaming chunks immediately
    // Buffer them and speak the complete response when done
}

// ... later, after streaming completes:
withContext(Dispatchers.Main) {
    // Speak the COMPLETE response now that streaming is done
    android.util.Log.d("OLLAMA_FIX", "Speaking complete response: ${finalResponse.length} chars")
    safeSpeak(finalResponse)
    
    // Release locks and restart timer
    isGptProcessing = false
    isConversationActive = false
    conversationActiveState.value = false
}
```

## Changes Made

### File: `app/src/main/java/com/example/alliswelltemi/MainActivity.kt`

**Changed in `callOllama()` function (lines 346-352):**
- **Removed:** Real-time chunk speaking with `speakStreamingChunk(chunk)`
- **Added:** Complete buffering of all chunks into `fullResponse`
- **Added:** Speaking of complete response only after streaming finishes (line 368)

**Result:**
1. Ollama completes generating full response
2. Complete response is buffered in memory
3. Temi speaks the **entire, coherent response** once
4. Conversation lock is released after speaking completes

## Behavior Change

### Before
- User asks: "What is the email?"
- Temi starts speaking immediately (partial response)
- Temi speaks individual words as they arrive
- Result: Garbled, incomplete answer

### After
- User asks: "What is the email?"
- Temi waits for Ollama to complete (~10 seconds)
- Temi speaks the complete response in one coherent sentence
- Result: Clear, complete answer

## Testing
Deploy the updated APK and verify:
1. Ask any Q&A question (e.g., "What is the email of this hospital?")
2. Temi should wait silently while Ollama processes
3. Once Ollama responds, Temi speaks the complete, coherent answer
4. No partial/garbled responses should occur

## Logs to Monitor
- `OLLAMA_FIX`: Shows conversation lock state
- `OLLAMA_RESPONSE`: Shows the complete response Temi will speak
- Verify sequence: Response received → Speaking complete response → Conversation lock RELEASED

## Notes
- The `speakStreamingChunk()` function is now unused but left in place for future optimization
- Conversation lock (`isConversationActive`) prevents overlapping Ollama calls
- 30-second inactivity timer is properly blocked during Ollama processing

