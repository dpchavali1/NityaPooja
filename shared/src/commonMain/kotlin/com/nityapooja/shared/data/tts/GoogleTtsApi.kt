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
        val input: SsmlInput,
        val voice: VoiceSelectionParams,
        val audioConfig: AudioConfig,
    )

    @Serializable
    private data class SsmlInput(val ssml: String)

    @Serializable
    private data class VoiceSelectionParams(
        val languageCode: String,
        val ssmlGender: String,
        val name: String? = null,
    )

    @Serializable
    private data class AudioConfig(val audioEncoding: String)

    @Serializable
    private data class TtsResponse(val audioContent: String)

    // Wrap plain text in SSML: slower rate, lower pitch, and a short pause after each line
    // to mimic the measured cadence of a Sanskrit/Telugu priest recitation.
    private fun toSsml(text: String): String {
        val escaped = text
            .replace("&", "&amp;")
            .replace("<", "&lt;")
            .replace(">", "&gt;")
        // Replace newlines with a 400 ms pause so each verse line gets a breath
        val withPauses = escaped.replace("\n", """<break time="400ms"/>""" + "\n")
        return """<speak><prosody rate="85%" pitch="-2st">$withPauses</prosody></speak>"""
    }

    @OptIn(ExperimentalEncodingApi::class)
    suspend fun synthesize(text: String): ByteArray {
        val response = httpClient.post {
            url("https://texttospeech.googleapis.com/v1/text:synthesize?key=${credentials.apiKey}")
            contentType(ContentType.Application.Json)
            setBody(
                TtsRequest(
                    input = SsmlInput(toSsml(text)),
                    voice = VoiceSelectionParams(
                        languageCode = "te-IN",
                        ssmlGender = "MALE",
                        name = "te-IN-Standard-B",
                    ),
                    audioConfig = AudioConfig(audioEncoding = "MP3"),
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
