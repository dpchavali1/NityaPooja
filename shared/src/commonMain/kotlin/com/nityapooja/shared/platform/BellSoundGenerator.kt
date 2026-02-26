package com.nityapooja.shared.platform

import kotlin.math.PI
import kotlin.math.exp
import kotlin.math.sin

/**
 * Generates a realistic temple bell (ghanta) sound as a PCM WAV byte array.
 *
 * A brass temple bell produces a complex tone with:
 * - A fundamental frequency around 520-600 Hz
 * - Strong 2nd and 3rd harmonics
 * - A metallic "strike" transient at the beginning
 * - Exponential decay (long sustain like a real bell)
 */
object BellSoundGenerator {

    private const val SAMPLE_RATE = 44100
    private const val DURATION_SECONDS = 2.5
    private const val NUM_SAMPLES = (SAMPLE_RATE * DURATION_SECONDS).toInt()

    /**
     * Returns a 16-bit mono PCM WAV file as a byte array.
     */
    fun generateBellWav(): ByteArray {
        val samples = ShortArray(NUM_SAMPLES)
        val fundamental = 548.0 // Hz — brass temple bell fundamental

        // Bell harmonics: frequency multiplier, amplitude, decay rate
        // Based on acoustic analysis of brass temple bells
        val harmonics = listOf(
            Triple(1.0, 1.0, 1.8),      // fundamental — strongest, slow decay
            Triple(2.0, 0.6, 2.5),      // 2nd harmonic
            Triple(3.0, 0.35, 3.2),     // 3rd harmonic
            Triple(4.17, 0.25, 4.0),    // inharmonic partial (gives metallic character)
            Triple(5.43, 0.15, 5.0),    // higher inharmonic partial
            Triple(6.8, 0.08, 6.5),     // shimmer
            Triple(8.2, 0.04, 8.0),     // very high shimmer
        )

        for (i in 0 until NUM_SAMPLES) {
            val t = i.toDouble() / SAMPLE_RATE
            var sample = 0.0

            // Sum all harmonics with exponential decay
            for ((freqMul, amplitude, decay) in harmonics) {
                val freq = fundamental * freqMul
                sample += amplitude * exp(-decay * t) * sin(2.0 * PI * freq * t)
            }

            // Strike transient: sharp attack in first 5ms (like mallet hitting brass)
            if (t < 0.005) {
                val strikeEnv = (0.005 - t) / 0.005
                sample += strikeEnv * 0.5 * sin(2.0 * PI * 1800.0 * t) // high-freq click
                sample += strikeEnv * 0.3 * sin(2.0 * PI * 3200.0 * t) // metallic ping
            }

            // Soft overall envelope: instant attack, long ring
            val envelope = if (t < 0.002) t / 0.002 else 1.0 // 2ms attack ramp to avoid click

            val normalizedSample = (sample * envelope * 0.4 * Short.MAX_VALUE)
                .toInt()
                .coerceIn(Short.MIN_VALUE.toInt(), Short.MAX_VALUE.toInt())
                .toShort()

            samples[i] = normalizedSample
        }

        return createWav(samples)
    }

    /**
     * Wraps raw 16-bit PCM samples in a WAV container.
     */
    private fun createWav(samples: ShortArray): ByteArray {
        val dataSize = samples.size * 2 // 16-bit = 2 bytes per sample
        val fileSize = 44 + dataSize // WAV header is 44 bytes
        val wav = ByteArray(fileSize)

        // RIFF header
        wav[0] = 'R'.code.toByte(); wav[1] = 'I'.code.toByte()
        wav[2] = 'F'.code.toByte(); wav[3] = 'F'.code.toByte()
        writeInt32LE(wav, 4, fileSize - 8)
        wav[8] = 'W'.code.toByte(); wav[9] = 'A'.code.toByte()
        wav[10] = 'V'.code.toByte(); wav[11] = 'E'.code.toByte()

        // fmt chunk
        wav[12] = 'f'.code.toByte(); wav[13] = 'm'.code.toByte()
        wav[14] = 't'.code.toByte(); wav[15] = ' '.code.toByte()
        writeInt32LE(wav, 16, 16)             // chunk size
        writeInt16LE(wav, 20, 1)              // PCM format
        writeInt16LE(wav, 22, 1)              // mono
        writeInt32LE(wav, 24, SAMPLE_RATE)    // sample rate
        writeInt32LE(wav, 28, SAMPLE_RATE * 2) // byte rate (sampleRate * channels * bitsPerSample/8)
        writeInt16LE(wav, 32, 2)              // block align (channels * bitsPerSample/8)
        writeInt16LE(wav, 34, 16)             // bits per sample

        // data chunk
        wav[36] = 'd'.code.toByte(); wav[37] = 'a'.code.toByte()
        wav[38] = 't'.code.toByte(); wav[39] = 'a'.code.toByte()
        writeInt32LE(wav, 40, dataSize)

        // PCM data
        for (i in samples.indices) {
            val s = samples[i].toInt()
            wav[44 + i * 2] = (s and 0xFF).toByte()
            wav[44 + i * 2 + 1] = ((s shr 8) and 0xFF).toByte()
        }

        return wav
    }

    private fun writeInt32LE(buf: ByteArray, offset: Int, value: Int) {
        buf[offset] = (value and 0xFF).toByte()
        buf[offset + 1] = ((value shr 8) and 0xFF).toByte()
        buf[offset + 2] = ((value shr 16) and 0xFF).toByte()
        buf[offset + 3] = ((value shr 24) and 0xFF).toByte()
    }

    private fun writeInt16LE(buf: ByteArray, offset: Int, value: Int) {
        buf[offset] = (value and 0xFF).toByte()
        buf[offset + 1] = ((value shr 8) and 0xFF).toByte()
    }
}
