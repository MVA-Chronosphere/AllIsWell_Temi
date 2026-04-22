package com.example.alliswelltemi.network

import retrofit2.http.Body
import retrofit2.http.POST

/**
 * Ollama API Service
 * Interfaces with local Ollama LLM server for text generation
 */
interface OllamaApiService {

    /**
     * Generate text using Ollama model
     * @param request Contains model name and prompt
     * @return Response with generated text
     */
    @POST("api/generate")
    suspend fun generate(@Body request: OllamaRequest): OllamaResponse

    /**
     * Generate streaming text using Ollama model
     * @param request Contains model name and prompt with stream=true
     * @return Flow of streaming response chunks
     */
    @POST("api/generate")
    suspend fun generateStream(@Body request: OllamaRequest): retrofit2.Response<okhttp3.ResponseBody>
}
