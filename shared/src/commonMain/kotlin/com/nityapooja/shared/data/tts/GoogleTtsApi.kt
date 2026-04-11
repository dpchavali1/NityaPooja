package com.nityapooja.shared.data.tts

import io.ktor.client.HttpClient
import io.ktor.client.call.body
import io.ktor.client.request.post
import io.ktor.client.request.setBody
import io.ktor.client.request.url
import io.ktor.client.statement.bodyAsText
import io.ktor.http.ContentType
import io.ktor.http.contentType
import io.ktor.http.isSuccess
import kotlinx.serialization.Serializable
import kotlin.io.encoding.Base64
import kotlin.io.encoding.ExperimentalEncodingApi

data class GoogleTtsCredentials(val apiKey: String)

class GoogleTtsApi(
    private val credentials: GoogleTtsCredentials,
    private val httpClient: HttpClient,
) {
    @Serializable
    private data class TtsRequest(
        val input: TextInput,
        val voice: VoiceSelectionParams,
        val audioConfig: AudioConfig,
    )

    @Serializable
    private data class TextInput(val text: String)

    @Serializable
    private data class VoiceSelectionParams(
        val languageCode: String,
        val ssmlGender: String,
        val name: String? = null,
    )

    @Serializable
    private data class AudioConfig(
        val audioEncoding: String,
        val speakingRate: Double,
        val pitch: Double,
    )

    @Serializable
    private data class TtsResponse(val audioContent: String)

    @OptIn(ExperimentalEncodingApi::class)
    suspend fun synthesize(text: String): ByteArray {
        val response = httpClient.post {
            url("https://texttospeech.googleapis.com/v1/text:synthesize?key=${credentials.apiKey}")
            contentType(ContentType.Application.Json)
            setBody(
                TtsRequest(
                    input = TextInput(text),
                    voice = VoiceSelectionParams(
                        languageCode = "te-IN",
                        ssmlGender = "MALE",
                        name = "te-IN-Standard-B",
                    ),
                    audioConfig = AudioConfig(
                        audioEncoding = "MP3",
                        speakingRate = 1.0,
                        pitch = -1.0,
                    ),
                )
            )
        }
        if (!response.status.isSuccess()) {
            val errorBody = response.bodyAsText()
            throw Exception("Google TTS API error ${response.status.value}: $errorBody")
        }
        val body = response.body<TtsResponse>()
        return Base64.decode(body.audioContent)
    }
}
