package com.nityapooja.app

import android.app.Application
import android.app.NotificationChannel
import android.app.NotificationManager
import com.google.android.gms.ads.MobileAds
import com.google.android.gms.ads.RequestConfiguration
import com.nityapooja.app.di.androidAppModule
import com.nityapooja.shared.data.grahanam.GrahanamRepository
import com.nityapooja.shared.data.preferences.UserPreferencesManager
import com.nityapooja.shared.di.androidPlatformModule
import com.nityapooja.shared.di.sharedModule
import com.nityapooja.app.widget.PanchangamWidgetUpdateWorker
import com.nityapooja.shared.platform.NotificationScheduler
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch
import kotlinx.datetime.Clock
import org.koin.android.ext.koin.androidContext
import org.koin.core.context.startKoin
import org.koin.java.KoinJavaComponent.getKoin

class NityaPoojaApp : Application() {

    private val appScope = CoroutineScope(SupervisorJob() + Dispatchers.IO)

    override fun onCreate() {
        super.onCreate()
        startKoin {
            androidContext(this@NityaPoojaApp)
            modules(sharedModule, androidPlatformModule, androidAppModule)
        }
        createNotificationChannels()
        scheduleNotificationsFromPreferences()
        PanchangamWidgetUpdateWorker.schedule(this)
        MobileAds.setRequestConfiguration(
            RequestConfiguration.Builder()
                .setMaxAdContentRating(RequestConfiguration.MAX_AD_CONTENT_RATING_G)
                .setTagForChildDirectedTreatment(RequestConfiguration.TAG_FOR_CHILD_DIRECTED_TREATMENT_TRUE)
                .build()
        )
        MobileAds.initialize(this)
    }

    private fun scheduleNotificationsFromPreferences() {
        appScope.launch {
            val prefs = getKoin().get<UserPreferencesManager>()
            val scheduler = getKoin().get<NotificationScheduler>()
            val timezone = prefs.locationTimezone.first()

            if (prefs.morningNotification.first()) {
                scheduler.scheduleMorningReminder(5, 30, timezone)
            }
            if (prefs.eveningNotification.first()) {
                scheduler.scheduleEveningReminder(18, 30, timezone)
            }
            if (prefs.panchangNotifications.first()) {
                scheduler.schedulePanchangReminder(timezone)
            }
            if (prefs.quizNotification.first()) {
                val hour = prefs.quizNotificationHour.first()
                val minute = prefs.quizNotificationMinute.first()
                scheduler.scheduleQuizReminder(hour, minute, timezone)
            }
            if (prefs.grahanamNotification.first()) {
                val grahanamList = GrahanamRepository.getUpcomingGrahanam(Clock.System.now())
                scheduler.scheduleGrahanamNotifications(grahanamList, timezone)
            }
        }
    }

    private fun createNotificationChannels() {
        val manager = getSystemService(NotificationManager::class.java)

        val dailyReminder = NotificationChannel(
            CHANNEL_DAILY_REMINDER,
            "Daily Reminders",
            NotificationManager.IMPORTANCE_DEFAULT,
        ).apply {
            description = "Morning Suprabhatam and evening aarti reminders"
        }

        val festivalAlerts = NotificationChannel(
            CHANNEL_FESTIVAL_ALERTS,
            "Festival Alerts",
            NotificationManager.IMPORTANCE_HIGH,
        ).apply {
            description = "Notifications for upcoming festivals"
        }

        val audioPlayback = NotificationChannel(
            CHANNEL_AUDIO_PLAYBACK,
            "Audio Playback",
            NotificationManager.IMPORTANCE_LOW,
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
