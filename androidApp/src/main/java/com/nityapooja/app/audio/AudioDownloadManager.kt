package com.nityapooja.app.audio

import android.content.Context
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.withContext
import java.io.File
import java.net.URL

data class DownloadProgress(
    val url: String = "",
    val progress: Float = 0f,
    val isDownloading: Boolean = false,
)

class AudioDownloadManager(
    private val context: Context,
) {
    private val audioDir: File
        get() = File(context.filesDir, "audio_downloads").also { it.mkdirs() }

    private val _downloadProgress = MutableStateFlow(DownloadProgress())
    val downloadProgress: StateFlow<DownloadProgress> = _downloadProgress.asStateFlow()

    fun isDownloaded(url: String): Boolean {
        return getLocalFile(url).exists()
    }

    fun getLocalPath(url: String): String? {
        val file = getLocalFile(url)
        return if (file.exists()) file.absolutePath else null
    }

    private fun getLocalFile(url: String): File {
        val fileName = url.hashCode().toUInt().toString() + ".mp3"
        return File(audioDir, fileName)
    }

    suspend fun download(url: String): Boolean = withContext(Dispatchers.IO) {
        if (url.isBlank()) return@withContext false
        if (isDownloaded(url)) return@withContext true

        _downloadProgress.value = DownloadProgress(url = url, progress = 0f, isDownloading = true)

        try {
            val connection = URL(url).openConnection().apply {
                connectTimeout = 15_000
                readTimeout = 30_000
            }
            connection.connect()
            val totalSize = connection.contentLength.toLong()
            val file = getLocalFile(url)

            connection.getInputStream().use { inputStream ->
                file.outputStream().use { outputStream ->
                    var bytesRead = 0L
                    val buffer = ByteArray(8192)
                    var len: Int

                    while (inputStream.read(buffer).also { len = it } != -1) {
                        outputStream.write(buffer, 0, len)
                        bytesRead += len
                        if (totalSize > 0) {
                            _downloadProgress.value = DownloadProgress(
                                url = url,
                                progress = bytesRead.toFloat() / totalSize,
                                isDownloading = true,
                            )
                        }
                    }
                    outputStream.flush()
                }
            }

            _downloadProgress.value = DownloadProgress(url = url, progress = 1f, isDownloading = false)
            true
        } catch (e: Exception) {
            _downloadProgress.value = DownloadProgress(url = url, progress = 0f, isDownloading = false)
            // Clean up partial download
            getLocalFile(url).delete()
            false
        }
    }

    fun deleteDownload(url: String): Boolean {
        return getLocalFile(url).delete()
    }

    fun getDownloadedSize(): Long {
        return audioDir.listFiles()?.sumOf { it.length() } ?: 0L
    }

    fun clearAllDownloads() {
        audioDir.listFiles()?.forEach { it.delete() }
    }
}
