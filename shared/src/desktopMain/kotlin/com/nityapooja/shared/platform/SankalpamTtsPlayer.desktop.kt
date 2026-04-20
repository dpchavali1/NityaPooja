package com.nityapooja.shared.platform

import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow

actual class SankalpamTtsPlayer {
    private val _isSpeaking = MutableStateFlow(false)
    actual val isSpeaking: StateFlow<Boolean> = _isSpeaking.asStateFlow()
    private val _isLoading = MutableStateFlow(false)
    actual val isLoading: StateFlow<Boolean> = _isLoading.asStateFlow()
    actual fun speak(text: String, cacheKey: String) {}
    actual fun stop() { _isSpeaking.value = false }
    actual fun release() { _isSpeaking.value = false }
}
