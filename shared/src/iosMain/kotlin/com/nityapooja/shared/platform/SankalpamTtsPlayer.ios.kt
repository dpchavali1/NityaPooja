package com.nityapooja.shared.platform

import com.nityapooja.shared.data.tts.GoogleTtsApi
import kotlinx.cinterop.ExperimentalForeignApi
import kotlinx.cinterop.addressOf
import kotlinx.cinterop.usePinned
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.cancel
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import platform.AVFAudio.AVAudioPlayer
import platform.Foundation.NSData
import platform.Foundation.NSDocumentDirectory
import platform.Foundation.NSFileManager
import platform.Foundation.NSURL
import platform.Foundation.NSUserDomainMask
import platform.Foundation.NSSearchPathForDirectoriesInDomains
import platform.Foundation.create

actual class SankalpamTtsPlayer(
    private val ttsApi: GoogleTtsApi,
) {
    private val _isSpeaking = MutableStateFlow(false)
    actual val isSpeaking: StateFlow<Boolean> = _isSpeaking.asStateFlow()

    private val _isLoading = MutableStateFlow(false)
    actual val isLoading: StateFlow<Boolean> = _isLoading.asStateFlow()

    private val scope = CoroutineScope(Dispatchers.Main + SupervisorJob())
    private var audioPlayer: AVAudioPlayer? = null

    @OptIn(ExperimentalForeignApi::class)
    actual fun speak(text: String) {
        scope.launch {
            val docDir = NSSearchPathForDirectoriesInDomains(
                NSDocumentDirectory, NSUserDomainMask, true
            ).firstOrNull() as? String ?: return@launch

            val ttsCacheDir = "$docDir/sankalpam_tts"
            val filePath = "$ttsCacheDir/v4_${text.hashCode()}.mp3"

            NSFileManager.defaultManager.createDirectoryAtPath(
                ttsCacheDir, withIntermediateDirectories = true, attributes = null, error = null
            )

            if (!NSFileManager.defaultManager.fileExistsAtPath(filePath)) {
                _isLoading.value = true
                try {
                    val bytes = withContext(Dispatchers.Default) { ttsApi.synthesize(text) }
                    NSFileManager.defaultManager.createFileAtPath(
                        filePath, contents = bytes.toNSData(), attributes = null
                    )
                } catch (e: Exception) {
                    _isLoading.value = false
                    return@launch
                }
                _isLoading.value = false
            }

            val url = NSURL.fileURLWithPath(filePath)
            audioPlayer?.stop()
            audioPlayer = AVAudioPlayer(contentsOfURL = url, error = null)
            audioPlayer?.play()
            _isSpeaking.value = true

            while (audioPlayer?.playing == true) {
                delay(300)
            }
            _isSpeaking.value = false
        }
    }

    actual fun stop() {
        audioPlayer?.stop()
        audioPlayer = null
        _isSpeaking.value = false
    }

    actual fun release() {
        stop()
        scope.cancel()
    }

    @OptIn(ExperimentalForeignApi::class, kotlinx.cinterop.BetaInteropApi::class)
    private fun ByteArray.toNSData(): NSData = usePinned { pinned ->
        NSData.create(bytes = pinned.addressOf(0), length = size.toULong())
    }
}
