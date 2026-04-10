package com.nityapooja.app.worker

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import com.nityapooja.shared.data.grahanam.GrahanamRepository
import com.nityapooja.shared.data.preferences.UserPreferencesManager
import com.nityapooja.shared.data.repository.DevotionalRepository
import com.nityapooja.shared.platform.FestivalNotificationInfo
import com.nityapooja.shared.platform.NotificationScheduler as SharedScheduler
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch
import kotlinx.datetime.Clock
import org.koin.core.component.KoinComponent
import org.koin.core.component.inject

/**
 * Reschedules all AlarmManager alarms after device reboot.
 *
 * Uses goAsync() so the process is not killed before the DataStore read + alarm setup completes.
 * Only responds to BOOT_COMPLETED (post-unlock) — not LOCKED_BOOT_COMPLETED, because
 * DataStore (credential-encrypted storage) is unavailable at locked-boot time.
 */
class BootReceiver : BroadcastReceiver(), KoinComponent {

    private val prefs: UserPreferencesManager by inject()
    private val scheduler: SharedScheduler by inject()
    private val devotionalRepository: DevotionalRepository by inject()

    override fun onReceive(context: Context, intent: Intent) {
        if (intent.action != Intent.ACTION_BOOT_COMPLETED) return

        val pendingResult = goAsync()
        CoroutineScope(Dispatchers.IO).launch {
            try {
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
                if (prefs.vrataNotification.first()) {
                    scheduler.scheduleVrataReminder("Vratam", "వ్రతం", 6, 0, timezone)
                }
                if (prefs.sacredMonthNotification.first()) {
                    scheduler.scheduleSacredMonthReminder("పవిత్ర మాసం", 5, 30, timezone)
                }
                if (prefs.shlokaNotification.first()) {
                    scheduler.scheduleShlokaReminder(7, 0, timezone)
                }
                if (prefs.rahuKalamAlerts.first()) {
                    val lat = prefs.locationLat.first()
                    val lng = prefs.locationLng.first()
                    scheduler.scheduleRahuKalamAlerts(lat, lng, timezone)
                }

                // Reschedule festival greeting notifications
                val userName = prefs.userName.first()
                val festivals = devotionalRepository.getAllFestivalsOnce()
                val festivalInfos = festivals.mapNotNull { f ->
                    val date = f.dateThisYear ?: f.dateNextYear ?: return@mapNotNull null
                    FestivalNotificationInfo(id = f.id.toString(), name = f.name, nameTelugu = f.nameTelugu, dateString = date)
                }
                val nextYearInfos = festivals.mapNotNull { f ->
                    val date = f.dateNextYear ?: return@mapNotNull null
                    if (date == f.dateThisYear) return@mapNotNull null
                    FestivalNotificationInfo(id = "${f.id}_next", name = f.name, nameTelugu = f.nameTelugu, dateString = date)
                }
                scheduler.scheduleFestivalGreetings(festivalInfos + nextYearInfos, timezone, userName)
            } finally {
                pendingResult.finish()
            }
        }
    }
}
