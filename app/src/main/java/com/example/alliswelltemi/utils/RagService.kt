package com.example.alliswelltemi.utils

import android.util.Log
import kotlin.math.sqrt

/**
 * Vector Embedding Service for RAG
 * Implements semantic similarity search using keyword-based approximate embeddings
 * This is suitable for local deployment without external embedding models
 *
 * Core functionality:
 * 1. Generate document embeddings from keywords and text
 * 2. Compute cosine similarity between query and documents
 * 3. Rank and retrieve top-K semantically similar documents
 */
object VectorEmbeddingService {

    private const val TAG = "VectorEmbedding"
    private const val EMBEDDING_DIM = 256

    /**
     * Represents a semantic vector for a document
     */
    data class DocumentVector(
        val docId: String,
        val embedding: FloatArray,
        val norm: Float
    ) {
        override fun equals(other: Any?): Boolean {
            if (this === other) return true
            if (other !is DocumentVector) return false
            if (docId != other.docId) return false
            if (!embedding.contentEquals(other.embedding)) return false
            if (norm != other.norm) return false
            return true
        }

        override fun hashCode(): Int {
            var result = docId.hashCode()
            result = 31 * result + embedding.contentHashCode()
            result = 31 * result + norm.hashCode()
            return result
        }
    }

    /**
     * Generate embedding from text using keyword-based hashing
     * Approximates semantic meaning through keyword weighting
     */
    fun generateEmbedding(text: String, keywords: List<String> = emptyList()): FloatArray {
        val embedding = FloatArray(EMBEDDING_DIM)

        // Normalize and tokenize text
        val tokens = text.lowercase()
            .split(Regex("[^a-z0-9]+"))
            .filter { it.isNotEmpty() }

        // Hash-based embedding: assign weights to positions based on text tokens
        for ((i, token) in tokens.withIndex()) {
            val hashCode = token.hashCode()
            val posIndex = (hashCode % EMBEDDING_DIM).let {
                if (it < 0) it + EMBEDDING_DIM else it
            }
            embedding[posIndex] += 1.0f
        }

        // Boost weights for important keywords
        for (keyword in keywords) {
            val hashCode = keyword.lowercase().hashCode()
            val posIndex = (hashCode % EMBEDDING_DIM).let {
                if (it < 0) it + EMBEDDING_DIM else it
            }
            embedding[posIndex] += 3.0f  // 3x weight for keywords
        }

        // L2 normalization
        val sumSquares = embedding.sumOf { (it * it).toDouble() }
        val norm = sqrt(sumSquares).toFloat()
        if (norm > 0) {
            for (i in embedding.indices) {
                embedding[i] = embedding[i] / norm
            }
        }

        return embedding
    }

    /**
     * Compute cosine similarity between two embeddings (already L2-normalized)
     */
    fun cosineSimilarity(embedding1: FloatArray, embedding2: FloatArray): Float {
        if (embedding1.size != embedding2.size) {
            Log.w(TAG, "Embedding size mismatch: ${embedding1.size} vs ${embedding2.size}")
            return 0f
        }

        var dotProduct = 0f
        for (i in embedding1.indices) {
            dotProduct += embedding1[i] * embedding2[i]
        }

        // Already normalized, so no need to divide by norms
        return dotProduct.coerceIn(0f, 1f)
    }

    /**
     * Fast approximate similarity using keyword overlap
     * Useful for filtering before full embedding comparison
     */
    fun keywordSimilarity(queryKeywords: List<String>, docKeywords: List<String>): Float {
        if (queryKeywords.isEmpty() || docKeywords.isEmpty()) {
            return 0f
        }

        val matchCount = queryKeywords.count { qkw ->
            docKeywords.any { dkw ->
                dkw.startsWith(qkw, ignoreCase = true) ||
                qkw.startsWith(dkw, ignoreCase = true)
            }
        }

        val maxCount = maxOf(queryKeywords.size, docKeywords.size)
        return matchCount.toFloat() / maxCount
    }

    /**
     * Rank documents by semantic similarity to query
     * Uses both keyword matching and embedding-based cosine similarity
     */
    fun rankDocuments(
        queryText: String,
        queryKeywords: List<String>,
        documents: List<Pair<String, String>>,  // Pair of (docId, docText)
        docKeywordsList: List<List<String>>,     // Keywords for each document
        topK: Int = 5
    ): List<Pair<String, Float>> {
        if (documents.isEmpty()) {
            Log.d(TAG, "No documents to rank")
            return emptyList()
        }

        val queryEmbedding = generateEmbedding(queryText, queryKeywords)

        val scores = documents.indices.map { idx ->
            val docId = documents[idx].first
            val docText = documents[idx].second
            val docKeywords = if (idx < docKeywordsList.size) docKeywordsList[idx] else emptyList()

            // Combined score: 70% semantic + 30% keyword
            val docEmbedding = generateEmbedding(docText, docKeywords)
            val semanticScore = cosineSimilarity(queryEmbedding, docEmbedding)
            val keywordScore = keywordSimilarity(queryKeywords, docKeywords)

            val combinedScore = (semanticScore * 0.7f) + (keywordScore * 0.3f)

            Log.d(TAG, "Doc=$docId: semantic=${"%.3f".format(semanticScore)}, keyword=${"%.3f".format(keywordScore)}, combined=${"%.3f".format(combinedScore)}")

            Pair(docId, combinedScore)
        }

        return scores
            .sortedByDescending { it.second }
            .take(topK)
    }
}

/**
 * RAG Service - Retrieval Augmented Generation
 * Manages semantic document retrieval for context injection into LLM prompts
 */
object RagService {
    private const val TAG = "RagService"

    // In-memory embedding cache
    private val embeddingCache = mutableMapOf<String, FloatArray>()

    /**
     * Retrieve topK most relevant documents from knowledge base
     * using semantic similarity
     */
    fun retrieveRelevantDocuments(
        query: String,
        documents: List<Pair<String, String>>,  // (docId, docText)
        docKeywordsList: List<List<String>>,    // Keywords per document
        topK: Int = 5
    ): List<Pair<String, Float>> {
        val queryKeywords = query.split(Regex("[^a-z0-9]+"))
            .filter { it.isNotEmpty() }
            .take(10)  // Limit to top 10 keywords

        Log.d(TAG, "Retrieving $topK documents for query: '$query'")
        Log.d(TAG, "Query keywords: $queryKeywords")

        val results = VectorEmbeddingService.rankDocuments(
            queryText = query,
            queryKeywords = queryKeywords,
            documents = documents,
            docKeywordsList = docKeywordsList,
            topK = topK
        )

        Log.d(TAG, "Retrieved ${results.size} relevant documents")
        results.forEach { (docId, score) ->
            Log.d(TAG, "  - $docId (score=${"%.3f".format(score)})")
        }

        return results
    }

    /**
     * Retrieve semantic neighbors for a document
     * Useful for finding related questions/answers
     */
    fun findSimilarDocuments(
        queryText: String,
        database: List<Pair<String, String>>,
        topK: Int = 3
    ): List<String> {
        val queryEmbedding = VectorEmbeddingService.generateEmbedding(queryText)

        val scores = database.map { (docId, docText) ->
            val docEmbedding = VectorEmbeddingService.generateEmbedding(docText)
            val similarity = VectorEmbeddingService.cosineSimilarity(queryEmbedding, docEmbedding)
            Pair(docId, similarity)
        }

        return scores
            .sortedByDescending { it.second }
            .filter { it.second > 0.3f }  // Only include reasonably similar documents
            .take(topK)
            .map { it.first }
    }
}

