package com.nityapooja.shared.platform

import kotlinx.cinterop.ExperimentalForeignApi
import kotlinx.cinterop.allocArrayOf
import kotlinx.cinterop.memScoped
import platform.AVFAudio.AVAudioPlayer
import platform.Foundation.NSData
import platform.Foundation.NSTemporaryDirectory
import platform.Foundation.NSURL
import platform.Foundation.dataWithBytes
import platform.Foundation.writeToFile

@OptIn(ExperimentalForeignApi::class)
actual class PlatformSoundEffect {

    private var bellWavData: ByteArray? = null
    private var player: AVAudioPlayer? = null
    private var tempFilePath: String? = null

    actual fun playBellSound() {
        val wav = bellWavData ?: BellSoundGenerator.generateBellWav().also { bellWavData = it }

        // Stop any currently playing bell
        player?.stop()

        // Write WAV to temp file and play from there
        val filePath = tempFilePath ?: "${NSTemporaryDirectory()}temple_bell.wav".also { tempFilePath = it }

        memScoped {
            val nsData = NSData.dataWithBytes(allocArrayOf(wav), wav.size.toULong())
            nsData?.writeToFile(filePath, atomically = true)
        }

        val fileUrl = NSURL.fileURLWithPath(filePath)
        val audioPlayer = AVAudioPlayer(contentsOfURL = fileUrl, error = null)
        audioPlayer?.prepareToPlay()
        audioPlayer?.play()
        player = audioPlayer
    }

    actual fun release() {
        player?.stop()
        player = null
    }
}
