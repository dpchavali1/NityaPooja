package com.nityapooja.app

import android.app.Application
import android.app.NotificationChannel
import android.app.NotificationManager
import com.google.android.gms.ads.MobileAds
import dagger.hilt.android.HiltAndroidApp

@HiltAndroidApp
class NityaPoojaApp : Application() {

    override fun onCreate() {
        super.onCreate()
        createNotificationChannels()
        MobileAds.initialize(this)
    }

    private fun createNotificationChannels() {
        val manager = getSystemService(NotificationManager::class.java)

        val dailyReminder = NotificationChannel(
            CHANNEL_DAILY_REMINDER,
            "Daily Reminders",
            NotificationManager.IMPORTANCE_DEFAULT
        ).apply {
            description = "Morning Suprabhatam and evening aarti reminders"
        }

        val festivalAlerts = NotificationChannel(
            CHANNEL_FESTIVAL_ALERTS,
            "Festival Alerts",
            NotificationManager.IMPORTANCE_HIGH
        ).apply {
            description = "Notifications for upcoming festivals"
        }

        val audioPlayback = NotificationChannel(
            CHANNEL_AUDIO_PLAYBACK,
            "Audio Playback",
            NotificationManager.IMPORTANCE_LOW
        ).apply {
            description = "Audio playback controls"
        }

        manager.createNotificationChannels(listOf(dailyReminder, festivalAlerts, audioPlayback))
    }

    companion object {
        const val CHANNEL_DAILY_REMINDER = "daily_reminder"
        const val CHANNEL_FESTIVAL_ALERTS = "festival_alerts"
        const val CHANNEL_AUDIO_PLAYBACK = "audio_playback"
    }
}
