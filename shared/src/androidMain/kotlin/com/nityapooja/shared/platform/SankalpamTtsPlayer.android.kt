package com.nityapooja.shared.platform

import android.content.Context
import android.speech.tts.TextToSpeech
import android.speech.tts.UtteranceProgressListener
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import java.util.Locale

actual class SankalpamTtsPlayer(private val context: Context) {

    private val _isSpeaking = MutableStateFlow(false)
    actual val isSpeaking: StateFlow<Boolean> = _isSpeaking.asStateFlow()

    private var tts: TextToSpeech? = null
    private var initialized = false
    private var pendingText: String? = null

    init {
        tts = TextToSpeech(context) { status ->
            if (status == TextToSpeech.SUCCESS) {
                // Prefer an offline male Telugu voice; fall back through progressively
                val teluguVoices = tts?.voices
                    ?.filter { it.locale.language == "te" }
                    ?: emptyList()
                val selectedVoice = teluguVoices.firstOrNull {
                    it.name.contains("male", ignoreCase = true) && !it.isNetworkConnectionRequired
                } ?: teluguVoices.firstOrNull {
                    it.name.contains("male", ignoreCase = true)
                } ?: teluguVoices.firstOrNull {
                    !it.isNetworkConnectionRequired
                } ?: teluguVoices.firstOrNull()

                if (selectedVoice != null) {
                    tts?.voice = selectedVoice
                } else {
                    val result = tts?.setLanguage(Locale("te", "IN"))
                        ?: TextToSpeech.LANG_NOT_SUPPORTED
                    if (result == TextToSpeech.LANG_MISSING_DATA || result == TextToSpeech.LANG_NOT_SUPPORTED) {
                        tts?.setLanguage(Locale("te"))
                    }
                }
                // Male Indian priest: slow deliberate pace, deep pitch
                tts?.setSpeechRate(0.70f)
                tts?.setPitch(0.75f)
                tts?.setOnUtteranceProgressListener(object : UtteranceProgressListener() {
                    override fun onStart(utteranceId: String?) { _isSpeaking.value = true }
                    override fun onDone(utteranceId: String?) { _isSpeaking.value = false }
                    @Deprecated("Deprecated in Java")
                    override fun onError(utteranceId: String?) { _isSpeaking.value = false }
                    override fun onError(utteranceId: String?, errorCode: Int) { _isSpeaking.value = false }
                })
                initialized = true
                pendingText?.let { text ->
                    pendingText = null
                    speakInternal(text)
                }
            }
        }
    }

    actual fun speak(text: String) {
        if (!initialized) {
            pendingText = text
            return
        }
        speakInternal(text)
    }

    private fun speakInternal(text: String) {
        tts?.stop()
        _isSpeaking.value = true
        tts?.speak(
            text,
            TextToSpeech.QUEUE_FLUSH,
            null,
            "sankalpam_${System.currentTimeMillis()}",
        )
    }

    actual fun stop() {
        tts?.stop()
        _isSpeaking.value = false
    }

    actual fun release() {
        tts?.stop()
        tts?.shutdown()
        tts = null
        _isSpeaking.value = false
    }
}
