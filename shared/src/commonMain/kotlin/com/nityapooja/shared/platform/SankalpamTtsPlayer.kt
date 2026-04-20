package com.nityapooja.shared.platform

import kotlinx.coroutines.flow.StateFlow

expect class SankalpamTtsPlayer {
    val isSpeaking: StateFlow<Boolean>
    val isLoading: StateFlow<Boolean>
    /** [cacheKey] is a stable daily key — does NOT include intraday-volatile fields like karana/yoga */
    fun speak(text: String, cacheKey: String)
    fun stop()
    fun release()
}
