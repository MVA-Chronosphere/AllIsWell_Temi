# Production AI Pipeline - Enhanced Safeguards

## Overview
This document outlines additional safeguards and best practices for the Ollama integration beyond the cache fix.

---

## 1. Request Validation & Sanitization

### Current State
`RagContextBuilder.kt` includes input validation, but we should ensure it's enforced everywhere.

### Recommended Enhancement: Request Validator

```kotlin
/**
 * Validates Ollama requests before sending
 * Prevents malformed/dangerous requests from reaching the LLM
 */
object RequestValidator {
    private const val TAG = "RequestValidator"
    private const val MAX_PROMPT_LENGTH = 5000
    private const val MIN_PROMPT_LENGTH = 5
    
    data class ValidationResult(
        val isValid: Boolean,
        val errors: List<String> = emptyList()
    )
    
    fun validateOllamaRequest(request: OllamaRequest): ValidationResult {
        val errors = mutableListOf<String>()
        
        // Check prompt length
        if (request.prompt.length < MIN_PROMPT_LENGTH) {
            errors.add("Prompt too short (min: $MIN_PROMPT_LENGTH chars)")
        }
        if (request.prompt.length > MAX_PROMPT_LENGTH) {
            errors.add("Prompt too long (max: $MAX_PROMPT_LENGTH chars)")
        }
        
        // Check for suspicious patterns (basic prompt injection detection)
        val suspiciousPatterns = listOf(
            "SELECT",      // SQL injection
            "DROP TABLE",
            "DELETE FROM",
            "INSERT INTO",
            "UPDATE SET",
            "UNION",       
            "OR 1=1",      // SQL bypass
            "admin",       // Common bypass words
            "root",
            "execute",
            "script",
            "eval",
            "exec"
        )
        
        val promptUpper = request.prompt.uppercase()
        val found = suspiciousPatterns.filter { promptUpper.contains(it) }
        if (found.isNotEmpty()) {
            errors.add("Suspicious patterns detected: ${found.joinToString(", ")}")
            Log.w(TAG, "⚠ Prompt injection attempt detected: $found")
        }
        
        // Check model exists
        if (request.model.isBlank()) {
            errors.add("Model name required")
        }
        
        // Validate options
        if (request.options?.num_predict != null && request.options.num_predict < 0) {
            errors.add("num_predict must be positive")
        }
        if (request.options?.temperature != null && 
            (request.options.temperature < 0f || request.options.temperature > 2f)) {
            errors.add("temperature must be between 0 and 2")
        }
        
        return ValidationResult(
            isValid = errors.isEmpty(),
            errors = errors
        )
    }
}
```

### Integration Point
```kotlin
// In OllamaClient.generateStreaming():
val validation = RequestValidator.validateOllamaRequest(request)
if (!validation.isValid) {
    Log.e("OllamaClient", "Invalid request: ${validation.errors}")
    throw IllegalArgumentException("Request validation failed: ${validation.errors}")
}
```

---

## 2. Response Validation & Sanitization

### Current State
Responses are passed directly to TTS without validation.

### Recommended Enhancement: Response Validator

```kotlin
/**
 * Validates and sanitizes LLM responses
 * Removes dangerous content before speaking
 */
object ResponseValidator {
    private const val TAG = "ResponseValidator"
    private const val MAX_RESPONSE_LENGTH = 1000
    private const val SAFE_PATTERN = "^[a-zA-Z0-9\\s.,:!?'\"()-\\u0900-\\u097F]*$"
    
    data class ValidationResult(
        val isValid: Boolean,
        val sanitized: String = "",
        val warnings: List<String> = emptyList()
    )
    
    fun validateResponse(response: String): ValidationResult {
        val warnings = mutableListOf<String>()
        var sanitized = response
        
        // Check length
        if (response.length > MAX_RESPONSE_LENGTH) {
            sanitized = response.take(MAX_RESPONSE_LENGTH)
            warnings.add("Response truncated (was ${response.length} chars)")
        }
        
        // Remove control characters
        sanitized = sanitized.replace(Regex("[\\x00-\\x1F\\x7F]"), "")
        
        // Check for HTML/script injection
        val injectionPatterns = listOf(
            "<script", 
            "javascript:",
            "onerror=",
            "onclick=",
            "<iframe",
            "<style"
        )
        
        val found = injectionPatterns.filter { sanitized.lowercase().contains(it) }
        if (found.isNotEmpty()) {
            warnings.add("Potential injection patterns removed: ${found.joinToString(", ")}")
            // Remove the patterns
            sanitized = injectionPatterns.fold(sanitized) { acc, pattern ->
                acc.replace(Regex(Regex.escape(pattern), RegexOption.IGNORE_CASE), "")
            }
        }
        
        // Log excessive exclamations (sign of weird LLM output)
        if (sanitized.count { it == '!' } > 3) {
            warnings.add("Excessive exclamation marks (${sanitized.count { it == '!' }})")
        }
        
        // Check if response looks "normal"
        if (!sanitized.matches(Regex(SAFE_PATTERN))) {
            warnings.add("Response contains unusual characters")
        }
        
        return ValidationResult(
            isValid = sanitized.isNotBlank(),
            sanitized = sanitized.trim(),
            warnings = warnings
        )
    }
}
```

### Integration Point
```kotlin
// In MainActivity.callOllama():
val validation = ResponseValidator.validateResponse(finalResponse)
if (!validation.isValid) {
    Log.w("OllamaClient", "Response validation failed: ${validation.warnings}")
    val fallback = RagContextBuilder.generateFallbackResponse(cleanedPrompt, doctors)
    safeSpeak(fallback)
    return
}

// Use sanitized response
safeSpeak(validation.sanitized)
validation.warnings.forEach { warning ->
    Log.w("ResponseValidator", warning)
}
```

---

## 3. Timeout & Circuit Breaker Enhancements

### Current State
`ResponseCache.kt` includes `OllamaCircuitBreaker`, but it should be more aggressive.

### Recommended Enhancement: Adaptive Timeouts

```kotlin
/**
 * Adaptive timeout management based on circuit breaker state
 * Prevents hanging requests accumulating
 */
object AdaptiveTimeout {
    private const val TAG = "AdaptiveTimeout"
    
    // Timeout configuration
    private const val NORMAL_TIMEOUT_MS = 10000L      // 10 seconds
    private const val HALF_OPEN_TIMEOUT_MS = 5000L    // Faster timeout during recovery
    private const val BACKOFF_TIMEOUT_MS = 3000L      // More aggressive
    
    fun getTimeoutMs(): Long {
        return when (OllamaCircuitBreaker.getState()) {
            OllamaCircuitBreaker.State.CLOSED -> NORMAL_TIMEOUT_MS
            OllamaCircuitBreaker.State.HALF_OPEN -> HALF_OPEN_TIMEOUT_MS
            OllamaCircuitBreaker.State.OPEN -> BACKOFF_TIMEOUT_MS
        }
    }
}

// Usage in OllamaClient:
private fun buildRetrofit(baseUrl: String): Retrofit {
    val timeout = AdaptiveTimeout.getTimeoutMs()
    val httpClient = OkHttpClient.Builder()
        .readTimeout(timeout, TimeUnit.MILLISECONDS)
        .build()
    // ...
}
```

---

## 4. Conversation Memory Cleanup

### Current State
`ConversationContext` maintains history but may grow unbounded.

### Verification
Check if `ConversationContext` includes cleanup:

```kotlin
// Recommended in ConversationContext.kt:
fun pruneOldEntries() {
    val maxAge = 30 * 60 * 1000  // 30 minutes
    val now = System.currentTimeMillis()
    
    history.removeIf { turn ->
        (now - turn.timestamp) > maxAge
    }
    
    Log.d("ConversationContext", "Pruned old entries. Remaining: ${history.size}")
}

// Call periodically:
// MainActivity.inactivityRunnable -> conversationContext.pruneOldEntries()
```

---

## 5. Monitoring & Observability

### Key Metrics to Track

```kotlin
/**
 * Ollama Pipeline Metrics
 * Tracks performance and reliability
 */
object OllamaPipelineMetrics {
    private val TAG = "OllamaPipelineMetrics"
    
    data class RequestMetrics(
        val requestId: String = UUID.randomUUID().toString(),
        val queryLength: Int = 0,
        val startTimeMs: Long = System.currentTimeMillis(),
        var firstChunkTimeMs: Long? = null,
        var totalTimeMs: Long = 0,
        var responseLength: Int = 0,
        var cacheHit: Boolean = false,
        var success: Boolean = false,
        var errorMessage: String? = null
    )
    
    private val metrics = mutableListOf<RequestMetrics>()
    
    fun recordRequest(
        query: String,
        cacheHit: Boolean = false
    ) = RequestMetrics(
        queryLength = query.length,
        cacheHit = cacheHit
    ).also {
        metrics.add(it)
    }
    
    fun recordSuccess(metric: RequestMetrics, response: String) {
        metric.success = true
        metric.responseLength = response.length
        metric.totalTimeMs = System.currentTimeMillis() - metric.startTimeMs
        logMetric(metric)
    }
    
    fun recordFailure(metric: RequestMetrics, error: Exception) {
        metric.success = false
        metric.errorMessage = error.message
        metric.totalTimeMs = System.currentTimeMillis() - metric.startTimeMs
        logMetric(metric)
    }
    
    private fun logMetric(metric: RequestMetrics) {
        Log.d(TAG, buildString {
            append("REQUEST #${metrics.indexOf(metric)}")
            append(" | Success=${metric.success}")
            append(" | Cache=${metric.cacheHit}")
            append(" | Query=${metric.queryLength}c")
            append(" | Time=${metric.totalTimeMs}ms")
            append(" | Response=${metric.responseLength}c")
            if (metric.errorMessage != null) {
                append(" | Error=${metric.errorMessage}")
            }
        })
    }
    
    fun getStats(): String {
        val successful = metrics.count { it.success }
        val cached = metrics.count { it.cacheHit }
        val avgTime = metrics.filter { it.success }
            .takeIf { it.isNotEmpty() }
            ?.map { it.totalTimeMs }
            ?.average()
            ?.toInt()
            ?: 0
        
        return "Total=${metrics.size} | Success=$successful | Cached=$cached | AvgTime=${avgTime}ms"
    }
    
    fun clearOldMetrics(maxAge: Long = 3600000) {  // 1 hour
        val cutoff = System.currentTimeMillis() - maxAge
        metrics.removeIf { it.startTimeMs < cutoff }
    }
}
```

### Use in MainActivity

```kotlin
// Track each request
private var currentMetric: OllamaPipelineMetrics.RequestMetrics? = null

private fun callOllama(prompt: String) {
    // ...
    currentMetric = OllamaPipelineMetrics.recordRequest(
        query = prompt.substringAfter("Q: "),
        cacheHit = false  // Update if cache hit detected
    )
    
    try {
        // ... streaming collection ...
        OllamaPipelineMetrics.recordSuccess(currentMetric!!, finalResponse)
    } catch (e: Exception) {
        OllamaPipelineMetrics.recordFailure(currentMetric!!, e)
    }
}

// Log stats periodically
handler.postDelayed({
    Log.d("OllamaPipelineMetrics", OllamaPipelineMetrics.getStats())
}, 300000)  // Every 5 minutes
```

---

## 6. Fallback Strategy Enhancement

### Current State
`RagContextBuilder.generateFallbackResponse()` exists but could be context-aware.

### Recommended
No changes needed - current implementation is solid. Just ensure it's always called on error.

```kotlin
// Verified in MainActivity.callOllama():
catch (e: Exception) {
    val fallbackResponse = RagContextBuilder.generateFallbackResponse(
        cleanedPrompt,
        doctorsViewModel.doctors.value
    )
    safeSpeak(fallbackResponse)  // ✓ Correct
}
```

---

## 7. Security Checklist

### Input Security
- ✅ `RagContextBuilder` validates query length (500 chars)
- ✅ `RagContextBuilder` removes dangerous characters
- ⚠️ Consider: Add RequestValidator for additional safety

### Output Security
- ⚠️ Current: No response sanitization
- Recommended: Implement ResponseValidator

### API Security
- ✅ Ollama runs on localhost only (not accessible remotely)
- ✅ No authentication exposure in logs
- ✅ Circuit breaker prevents DDoS-like behavior

### Data Security
- ✅ Conversation context stored locally
- ✅ No PII persisted in cache
- ✅ 30-second inactivity clear prevents data leakage

---

## 8. Testing Strategy

### Unit Tests
```kotlin
// Test query extraction
@Test
fun testQueryExtraction() {
    val prompt = "You are helpful...Q: Find doctors\nA:"
    val extracted = OllamaClient.extractQueryFromPrompt(prompt)
    assert(extracted == "Find doctors")
}

// Test request validation
@Test
fun testRequestValidation() {
    val valid = OllamaRequest("llama3:8b", "Find doctors")
    assert(RequestValidator.validateOllamaRequest(valid).isValid)
    
    val injection = OllamaRequest("llama3:8b", "Find doctors'; DROP TABLE users;--")
    assert(!RequestValidator.validateOllamaRequest(injection).isValid)
}
```

### Integration Tests
```kotlin
// Test cache behavior
@Test
fun testCacheDisabledByDefault() {
    // Two identical queries should produce different responses
    val response1 = OllamaClient.generateStreaming(request1, cacheEnabled = false)
    val response2 = OllamaClient.generateStreaming(request1, cacheEnabled = false)
    assert(response1 != response2)  // Fresh Ollama call each time
}
```

---

## 9. Deployment Checklist

- [ ] Rebuild with all changes
- [ ] Test cache fix: Different queries return different responses
- [ ] Test request validation: Malformed requests rejected
- [ ] Test response validation: Dangerous responses sanitized
- [ ] Monitor metrics: Log pipeline statistics
- [ ] Circuit breaker test: Verify recovery after 30s
- [ ] Load test: Multiple rapid queries handled correctly
- [ ] Memory test: Conversation context doesn't grow unbounded
- [ ] Security test: Prompt injection attempts logged as warnings

---

## 10. Monitoring Dashboard (Optional)

### What to Log (Logcat)
```
[OllamaClient] ✓ Cache HIT for query: 'Find doctors'
[OllamaClient] Cache enabled but query key is empty
[OllamaClient] Streaming error: Connection timeout
[ResponseValidator] ⚠ Response truncated (was 2500 chars)
[CircuitBreaker] CLOSED → OPEN (repeated failures)
[CircuitBreaker] OPEN → HALF_OPEN (timeout exceeded)
[OllamaPipelineMetrics] REQUEST #12 | Success=true | Cache=false | Time=4523ms
```

---

## Conclusion

The response cache bug is **FIXED** with:
1. ✅ Cache disabled by default
2. ✅ Query-only cache keys
3. ✅ Safe extraction function
4. ✅ Circuit breaker protection

Additional enhancements recommended:
1. ⚠️ Request validation
2. ⚠️ Response sanitization
3. ⚠️ Metrics collection
4. ⚠️ Adaptive timeouts

**Priority: HIGH** - Deploy cache fix immediately.
**Priority: MEDIUM** - Add validators in next release.
**Priority: LOW** - Implement metrics dashboard later.

