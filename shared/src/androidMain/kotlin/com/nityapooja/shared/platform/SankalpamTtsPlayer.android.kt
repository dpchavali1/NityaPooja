package com.nityapooja.shared.platform

import android.content.Context
import android.media.MediaPlayer
import android.util.Log
import com.nityapooja.shared.data.tts.GoogleTtsApi
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.cancel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.io.File

actual class SankalpamTtsPlayer(
    private val context: Context,
    private val ttsApi: GoogleTtsApi,
) {
    private val _isSpeaking = MutableStateFlow(false)
    actual val isSpeaking: StateFlow<Boolean> = _isSpeaking.asStateFlow()

    private val _isLoading = MutableStateFlow(false)
    actual val isLoading: StateFlow<Boolean> = _isLoading.asStateFlow()
    actual val isSupported: Boolean = true

    private val scope = CoroutineScope(Dispatchers.Main + SupervisorJob())
    private var mediaPlayer: MediaPlayer? = null

    private val cacheDir = File(context.filesDir, "sankalpam_tts").also { it.mkdirs() }

    actual fun speak(text: String, cacheKey: String) {
        Log.d("SankalpamTTS", "speak() called, cacheKey=$cacheKey")
        scope.launch {
            val safeKey = cacheKey.replace(Regex("[^A-Za-z0-9_\\-]"), "_")
            val cacheFile = File(cacheDir, "v5_$safeKey.mp3")
            Log.d("SankalpamTTS", "cacheFile=${cacheFile.absolutePath}, exists=${cacheFile.exists()}")

            if (!cacheFile.exists()) {
                _isLoading.value = true
                Log.d("SankalpamTTS", "Calling Google TTS API...")
                try {
                    val bytes = withContext(Dispatchers.IO) { ttsApi.synthesize(text) }
                    Log.d("SankalpamTTS", "Got ${bytes.size} bytes, saving to cache")
                    withContext(Dispatchers.IO) { cacheFile.writeBytes(bytes) }
                } catch (e: Exception) {
                    Log.e("SankalpamTTS", "API/save failed: ${e.message}", e)
                    _isLoading.value = false
                    return@launch
                }
                _isLoading.value = false
            }

            Log.d("SankalpamTTS", "Playing file")
            playFile(cacheFile)
        }
    }

    private fun playFile(file: File) {
        mediaPlayer?.release()
        mediaPlayer = MediaPlayer().apply {
            setDataSource(file.absolutePath)
            setOnCompletionListener { _isSpeaking.value = false }
            setOnErrorListener { _, _, _ -> _isSpeaking.value = false; true }
            prepare()
            start()
        }
        _isSpeaking.value = true
    }

    actual fun stop() {
        mediaPlayer?.stop()
        mediaPlayer?.release()
        mediaPlayer = null
        _isSpeaking.value = false
    }

    actual fun release() {
        stop()
        scope.cancel()
    }
}
