package com.nityapooja.shared.platform

import java.io.ByteArrayInputStream
import javax.sound.sampled.AudioSystem
import javax.sound.sampled.Clip

actual class PlatformSoundEffect {

    private var bellWavData: ByteArray? = null
    private var clip: Clip? = null

    actual fun playBellSound() {
        val wav = bellWavData ?: BellSoundGenerator.generateBellWav().also { bellWavData = it }

        // Stop any currently playing bell
        clip?.stop()
        clip?.close()

        try {
            val audioInputStream = AudioSystem.getAudioInputStream(ByteArrayInputStream(wav))
            val newClip = AudioSystem.getClip()
            newClip.open(audioInputStream)
            newClip.start()
            clip = newClip
        } catch (e: Exception) {
            // Fallback: system beep
            java.awt.Toolkit.getDefaultToolkit().beep()
        }
    }

    actual fun release() {
        clip?.stop()
        clip?.close()
        clip = null
    }
}
