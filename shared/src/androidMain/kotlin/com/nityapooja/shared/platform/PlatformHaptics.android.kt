package com.nityapooja.shared.platform

import android.content.Context
import android.media.AudioManager
import android.media.ToneGenerator
import android.os.Build
import android.os.Handler
import android.os.Looper
import android.os.VibrationEffect
import android.os.Vibrator
import android.os.VibratorManager

actual class PlatformHaptics(private val context: Context) {

    private val vibrator: Vibrator? by lazy {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
            val vm = context.getSystemService(Context.VIBRATOR_MANAGER_SERVICE) as? VibratorManager
            vm?.defaultVibrator
        } else {
            @Suppress("DEPRECATION")
            context.getSystemService(Context.VIBRATOR_SERVICE) as? Vibrator
        }
    }

    private val handler = Handler(Looper.getMainLooper())

    actual fun lightTap() {
        vibrate(30, 80)
    }

    actual fun mediumTap() {
        vibrate(100, VibrationEffect.DEFAULT_AMPLITUDE)
    }

    actual fun strongTap() {
        playTone(ToneGenerator.TONE_PROP_BEEP, 500, 80)
        vibratePattern(longArrayOf(0, 120, 60, 200))
    }

    actual fun malaComplete() {
        playTone(ToneGenerator.TONE_PROP_BEEP2, 800, ToneGenerator.MAX_VOLUME)
        vibratePattern(longArrayOf(0, 150, 80, 250, 80, 400))
    }

    actual fun uiTap() {
        vibrate(20, 60)
    }

    private fun vibrate(durationMs: Long, amplitude: Int) {
        vibrator?.let {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                it.vibrate(VibrationEffect.createOneShot(durationMs, amplitude))
            } else {
                @Suppress("DEPRECATION")
                it.vibrate(durationMs)
            }
        }
    }

    private fun vibratePattern(pattern: LongArray) {
        vibrator?.let {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                it.vibrate(VibrationEffect.createWaveform(pattern, -1))
            } else {
                @Suppress("DEPRECATION")
                it.vibrate(pattern, -1)
            }
        }
    }

    private fun playTone(tone: Int, durationMs: Int, volume: Int) {
        try {
            val toneGen = ToneGenerator(AudioManager.STREAM_MUSIC, volume)
            toneGen.startTone(tone, durationMs)
            handler.postDelayed({ toneGen.release() }, durationMs.toLong() + 200)
        } catch (_: Exception) {}
    }
}
