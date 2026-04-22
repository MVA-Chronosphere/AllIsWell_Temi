# GPT Conversation Flow - Quick Reference

## ✅ SUCCESSFUL FLOW

```
1. ASR Input
   TemiSpeech: ASR Result: 'who is Dr Sharma'

2. Prompt Generated
   GPT_PROMPT: ========== GPT PROMPT START ==========
   GPT_PROMPT: <full prompt here>
   GPT_PROMPT: ========== GPT PROMPT END (XXX chars) ==========

3. GPT Started
   GPT_DEBUG: ========== STARTING GPT CONVERSATION ==========
   GPT_DEBUG: robot.askQuestion() called successfully
   
4. Conversation Active
   TemiSpeech: Conversation attached: true  ✅

5. GPT Processing
   GPT_DEBUG: Status: LISTENING (1)
   GPT_DEBUG: Status: THINKING (2)
   
6. Response Received
   GPT_DEBUG: ========== GPT RESPONSE RECEIVED ==========
   GPT_RESPONSE: <GPT's actual response>
   GPT_DEBUG: Response time: 2500ms
   
7. TTS Speaking
   GPT_DEBUG: Speaking response via TTS...
   GPT_DEBUG: Conversation lock RELEASED
```

---

## ❌ FAILURE: Conversation Detached

```
GPT_DEBUG: robot.askQuestion() called successfully
TemiSpeech: Conversation attached: false  ❌  ← PROBLEM!

<12 seconds later>
GPT_DEBUG: ========== GPT TIMEOUT ==========
```

**Root Cause**: Prompt rejected by Temi SDK
**Check**: `GPT_PROMPT` logs for prompt content and length

---

## ❌ FAILURE: GPT Timeout

```
GPT_DEBUG: robot.askQuestion() called successfully
TemiSpeech: Conversation attached: true  ✅
GPT_DEBUG: Status: LISTENING (1)
GPT_DEBUG: Status: THINKING (2)

<no response for 12 seconds>

GPT_DEBUG: ========== GPT TIMEOUT ==========
GPT_DEBUG: Conversation still active, generating fallback...
```

**Root Cause**: GPT service not responding
**Check**: Network connectivity, Temi GPT configuration

---

## 🔍 Key Logcat Commands

### Monitor GPT Flow Only
```bash
adb logcat -s GPT_DEBUG:D GPT_PROMPT:D GPT_RESPONSE:D
```

### Full Debug (Includes Temi SDK)
```bash
adb logcat | grep -E "(GPT_|TemiSpeech|PERF)"
```

### Save to File
```bash
adb logcat | grep "GPT" > gpt_debug.log
```

---

## 🎯 Critical Checkpoints

1. ✅ **Prompt logged**: `GPT_PROMPT: ========== GPT PROMPT START`
2. ✅ **Conversation started**: `STARTING GPT CONVERSATION`
3. ✅ **Attached successfully**: `Conversation attached: true`
4. ✅ **Response received**: `GPT RESPONSE RECEIVED`
5. ✅ **Lock released**: `Conversation lock RELEASED`

---

## 📊 Expected Timings

- Prompt generation: **<100ms**
- GPT response: **2-5 seconds**
- Timeout threshold: **12 seconds**

---

## 🚨 Red Flags

- ❌ `Conversation attached: false`
- ❌ `GPT TIMEOUT` 
- ❌ `⚠️ Conversation went IDLE without response`
- ❌ Response time > 8 seconds (warning, will timeout soon)

