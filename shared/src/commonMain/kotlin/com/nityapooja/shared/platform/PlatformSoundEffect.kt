package com.nityapooja.shared.platform

/**
 * Lightweight sound effect player for short UI sounds (bell, chime, etc.).
 * Separate from PlatformAudioPlayer which handles streaming music.
 */
expect class PlatformSoundEffect {
    fun playBellSound()
    fun release()
}
