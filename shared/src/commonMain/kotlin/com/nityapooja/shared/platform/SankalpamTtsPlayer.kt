package com.nityapooja.shared.platform

import kotlinx.coroutines.flow.StateFlow

expect class SankalpamTtsPlayer {
    val isSpeaking: StateFlow<Boolean>
    val isLoading: StateFlow<Boolean>
    val isSupported: Boolean
    /** [cacheKey] must change whenever the spoken text changes (includes tithi/nakshatra index) */
    fun speak(text: String, cacheKey: String)
    fun stop()
    fun release()
}
