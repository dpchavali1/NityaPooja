package com.nityapooja.shared.platform

import android.content.Context
import android.media.AudioAttributes
import android.media.AudioFormat
import android.media.AudioTrack

actual class PlatformSoundEffect(private val context: Context) {

    private var bellWavData: ByteArray? = null
    private var audioTrack: AudioTrack? = null

    actual fun playBellSound() {
        // Generate WAV on first play, cache for subsequent plays
        val wav = bellWavData ?: BellSoundGenerator.generateBellWav().also { bellWavData = it }

        // Stop any currently playing bell
        audioTrack?.release()

        val sampleRate = 44100
        val pcmData = wav.copyOfRange(44, wav.size) // skip WAV header

        val track = AudioTrack.Builder()
            .setAudioAttributes(
                AudioAttributes.Builder()
                    .setUsage(AudioAttributes.USAGE_MEDIA)
                    .setContentType(AudioAttributes.CONTENT_TYPE_SONIFICATION)
                    .build()
            )
            .setAudioFormat(
                AudioFormat.Builder()
                    .setEncoding(AudioFormat.ENCODING_PCM_16BIT)
                    .setSampleRate(sampleRate)
                    .setChannelMask(AudioFormat.CHANNEL_OUT_MONO)
                    .build()
            )
            .setBufferSizeInBytes(pcmData.size)
            .setTransferMode(AudioTrack.MODE_STATIC)
            .build()

        track.write(pcmData, 0, pcmData.size)
        track.play()
        audioTrack = track
    }

    actual fun release() {
        audioTrack?.release()
        audioTrack = null
    }
}
