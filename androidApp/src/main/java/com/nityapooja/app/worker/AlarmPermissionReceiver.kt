package com.nityapooja.app.worker

import android.app.AlarmManager
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.os.Build
import com.nityapooja.shared.data.grahanam.GrahanamRepository
import com.nityapooja.shared.data.preferences.UserPreferencesManager
import com.nityapooja.shared.platform.NotificationScheduler as SharedScheduler
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch
import kotlinx.datetime.Clock
import org.koin.core.component.KoinComponent
import org.koin.core.component.inject

/**
 * Handles ACTION_SCHEDULE_EXACT_ALARM_PERMISSION_STATE_CHANGED (Android 12+).
 *
 * When the user grants SCHEDULE_EXACT_ALARM in Settings > Special app access > Alarms & reminders,
 * all previously set alarms (which were using the inexact fallback) must be rescheduled so they
 * now use setAlarmClock / setExactAndAllowWhileIdle.
 *
 * Also handles revocation: when the permission is revoked, Android cancels all pending alarms
 * and kills the process. On next app start, NityaPoojaApp.scheduleNotificationsFromPreferences()
 * will re-schedule with the inexact fallback automatically — so no action needed here on revoke.
 */
class AlarmPermissionReceiver : BroadcastReceiver(), KoinComponent {

    private val prefs: UserPreferencesManager by inject()
    private val scheduler: SharedScheduler by inject()

    override fun onReceive(context: Context, intent: Intent) {
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.S) return
        val action = intent.action ?: return
        if (action != AlarmManager.ACTION_SCHEDULE_EXACT_ALARM_PERMISSION_STATE_CHANGED) return

        val am = context.getSystemService(Context.ALARM_SERVICE) as AlarmManager
        if (!am.canScheduleExactAlarms()) return // permission was revoked — next app launch handles it

        // Permission was granted — reschedule everything with exact precision
        val pendingResult = goAsync()
        CoroutineScope(Dispatchers.IO).launch {
            try {
                val timezone = prefs.locationTimezone.first()

                if (prefs.morningNotification.first()) scheduler.scheduleMorningReminder(5, 30, timezone)
                if (prefs.eveningNotification.first()) scheduler.scheduleEveningReminder(18, 30, timezone)
                if (prefs.panchangNotifications.first()) scheduler.schedulePanchangReminder(timezone)
                if (prefs.quizNotification.first()) {
                    scheduler.scheduleQuizReminder(
                        prefs.quizNotificationHour.first(),
                        prefs.quizNotificationMinute.first(),
                        timezone,
                    )
                }
                if (prefs.grahanamNotification.first()) {
                    scheduler.scheduleGrahanamNotifications(
                        GrahanamRepository.getUpcomingGrahanam(Clock.System.now()), timezone,
                    )
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
            } finally {
                pendingResult.finish()
            }
        }
    }
}
