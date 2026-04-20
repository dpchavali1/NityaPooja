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
import platform.AVFAudio.AVAudioSession
import platform.AVFAudio.AVAudioSessionCategoryPlayback
import platform.AVFAudio.setActive
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
    actual fun speak(text: String, cacheKey: String) {
        scope.launch {
            val docDir = NSSearchPathForDirectoriesInDomains(
                NSDocumentDirectory, NSUserDomainMask, true
            ).firstOrNull() as? String ?: return@launch

            val ttsCacheDir = "$docDir/sankalpam_tts"
            val safeKey = cacheKey.replace(Regex("[^A-Za-z0-9_\\-]"), "_")
            val filePath = "$ttsCacheDir/v5_$safeKey.mp3"

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

            // Activate audio session so playback works even in silent mode
            val session = AVAudioSession.sharedInstance()
            session.setCategory(AVAudioSessionCategoryPlayback, null)
            session.setActive(true, null)

            val url = NSURL.fileURLWithPath(filePath)
            audioPlayer?.stop()
            val player = AVAudioPlayer(contentsOfURL = url, error = null) ?: run {
                _isSpeaking.value = false
                return@launch
            }
            audioPlayer = player
            player.prepareToPlay()
            player.play()
            _isSpeaking.value = true

            // Give AVAudioPlayer a moment to start before polling
            delay(200)
            while (player.playing) {
                delay(300)
            }
            _isSpeaking.value = false
            session.setActive(false, null)
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
