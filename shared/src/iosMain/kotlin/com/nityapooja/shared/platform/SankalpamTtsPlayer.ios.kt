package com.nityapooja.shared.platform

import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.cancel
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import platform.AVFAudio.AVSpeechSynthesisVoice
import platform.AVFAudio.AVSpeechSynthesisVoiceGenderMale
import platform.AVFAudio.AVSpeechSynthesizer
import platform.AVFAudio.AVSpeechUtterance

actual class SankalpamTtsPlayer {

    private val _isSpeaking = MutableStateFlow(false)
    actual val isSpeaking: StateFlow<Boolean> = _isSpeaking.asStateFlow()

    private val synthesizer = AVSpeechSynthesizer()
    private val scope = CoroutineScope(Dispatchers.Main)
    private var speakingJob: Job? = null

    actual fun speak(text: String) {
        if (synthesizer.speaking) {
            synthesizer.stopSpeakingAtBoundary(platform.AVFAudio.AVSpeechBoundaryImmediate)
        }
        val utterance = AVSpeechUtterance(string = text)
        // Prefer an offline male Telugu voice; fall back to any te-IN voice
        @Suppress("UNCHECKED_CAST")
        val allVoices = AVSpeechSynthesisVoice.speechVoices() as List<AVSpeechSynthesisVoice>
        val selectedVoice = allVoices.firstOrNull {
            it.language == "te-IN" && it.gender == AVSpeechSynthesisVoiceGenderMale
        } ?: AVSpeechSynthesisVoice.voiceWithLanguage("te-IN")
        selectedVoice?.let { utterance.voice = it }
        // Male Indian priest: slow deliberate pace, deep pitch (iOS rate 0..1, default ~0.5)
        utterance.rate = 0.35f
        utterance.pitchMultiplier = 0.75f
        utterance.volume = 1.0f

        _isSpeaking.value = true
        synthesizer.speakUtterance(utterance)

        // Poll synthesizer.speaking to automatically reset isSpeaking when done
        speakingJob?.cancel()
        speakingJob = scope.launch {
            while (synthesizer.speaking) {
                delay(300)
            }
            _isSpeaking.value = false
        }
    }

    actual fun stop() {
        speakingJob?.cancel()
        synthesizer.stopSpeakingAtBoundary(platform.AVFAudio.AVSpeechBoundaryImmediate)
        _isSpeaking.value = false
    }

    actual fun release() {
        speakingJob?.cancel()
        scope.cancel()
        synthesizer.stopSpeakingAtBoundary(platform.AVFAudio.AVSpeechBoundaryImmediate)
        _isSpeaking.value = false
    }
}
