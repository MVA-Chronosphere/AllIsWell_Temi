package com.example.alliswelltemi.utils

import java.io.ByteArrayOutputStream

/**
 * WAV Audio Utilities - Convert PCM audio to WAV format
 *
 * The WebView's HeadAudio system expects valid WAV files for analysis.
 * Temi TTS may provide raw PCM frames — this utility wraps them in WAV headers.
 */
object WavAudioFormat {

    /**
     * Create a minimal WAV header for PCM audio
     *
     * @param sampleRate Sample rate in Hz (e.g., 16000, 44100)
     * @param numChannels Number of channels (1=mono, 2=stereo)
     * @param bitsPerSample Bits per sample (16, 24, 32)
     * @param audioData Raw PCM audio bytes
     * @return Complete WAV file as ByteArray
     */
    fun createWavFile(
        sampleRate: Int = 16000,
        numChannels: Int = 1,
        bitsPerSample: Int = 16,
        audioData: ByteArray
    ): ByteArray {
        val byteRate = sampleRate * numChannels * bitsPerSample / 8
        val blockAlign = numChannels * bitsPerSample / 8
        val subChunk2Size = audioData.size
        val chunkSize = 36 + subChunk2Size

        val output = ByteArrayOutputStream()

        // RIFF header
        output.write("RIFF".toByteArray())
        output.write(intToBytes(chunkSize))
        output.write("WAVE".toByteArray())

        // fmt sub-chunk
        output.write("fmt ".toByteArray())
        output.write(intToBytes(16))  // SubChunk1Size
        output.write(shortToBytes(1)) // Audio format (1 = PCM)
        output.write(shortToBytes(numChannels))
        output.write(intToBytes(sampleRate))
        output.write(intToBytes(byteRate))
        output.write(shortToBytes(blockAlign))
        output.write(shortToBytes(bitsPerSample))

        // data sub-chunk
        output.write("data".toByteArray())
        output.write(intToBytes(subChunk2Size))
        output.write(audioData)

        return output.toByteArray()
    }

    /**
     * Convert int to 4 bytes (little-endian)
     */
    private fun intToBytes(value: Int): ByteArray {
        return byteArrayOf(
            (value and 0xFF).toByte(),
            ((value shr 8) and 0xFF).toByte(),
            ((value shr 16) and 0xFF).toByte(),
            ((value shr 24) and 0xFF).toByte()
        )
    }

    /**
     * Convert short to 2 bytes (little-endian)
     */
    private fun shortToBytes(value: Int): ByteArray {
        return byteArrayOf(
            (value and 0xFF).toByte(),
            ((value shr 8) and 0xFF).toByte()
        )
    }
}

