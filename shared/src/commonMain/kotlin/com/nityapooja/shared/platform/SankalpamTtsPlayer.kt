package com.nityapooja.shared.platform

import kotlinx.coroutines.flow.StateFlow

expect class SankalpamTtsPlayer {
    val isSpeaking: StateFlow<Boolean>
    fun speak(text: String)
    fun stop()
    fun release()
}
