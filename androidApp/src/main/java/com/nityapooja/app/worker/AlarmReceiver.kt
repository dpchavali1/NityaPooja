package com.nityapooja.app.worker

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import com.nityapooja.shared.data.local.db.NityaPoojaDatabase
import com.nityapooja.shared.data.preferences.UserPreferencesManager
import com.nityapooja.shared.utils.AstronomicalCalculator
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch
import kotlinx.coroutines.withTimeoutOrNull
import org.koin.core.component.KoinComponent
import org.koin.core.component.inject
import java.util.Calendar
import java.util.TimeZone

/**
 * Receives exact AlarmManager alarms and shows notifications.
 * Uses goAsync() so the 10-second BroadcastReceiver window is not breached.
 */
class AlarmReceiver : BroadcastReceiver(), KoinComponent {

    private val db: NityaPoojaDatabase by inject()
    private val prefs: UserPreferencesManager by inject()

    // Rahu Kaal slot indices per day-of-week (1-based, Sun=1..Sat=7, 0 = placeholder)
    private val RAHU_KAAL_SLOTS = intArrayOf(0, 8, 2, 7, 5, 6, 4, 3)
    private val YAMAGANDAM_SLOTS = intArrayOf(0, 5, 4, 3, 7, 2, 1, 6)
    private val GULIKA_KALAM_SLOTS = intArrayOf(0, 7, 6, 5, 4, 3, 2, 1)

    override fun onReceive(context: Context, intent: Intent) {
        val type = intent.getStringExtra(NotificationWorker.KEY_NOTIFICATION_TYPE) ?: return
        val fallbackBody = intent.getStringExtra(NotificationWorker.KEY_NOTIFICATION_BODY)
            ?: "Time for your daily puja"
        val notifId = intent.getIntExtra(NotificationWorker.KEY_NOTIFICATION_ID, 1001)
        val hour = intent.getIntExtra(NotificationWorker.KEY_HOUR, -1)
        val minute = intent.getIntExtra(NotificationWorker.KEY_MINUTE, -1)
        val timezoneId = intent.getStringExtra(NotificationWorker.KEY_TIMEZONE) ?: ""
        val workName = intent.getStringExtra(NotificationWorker.KEY_WORK_NAME) ?: ""

        val pendingResult = goAsync()
        CoroutineScope(Dispatchers.IO).launch {
            try {
                if (type == NotificationWorker.TYPE_RAHU_KALAM_SCHEDULER) {
                    // Compute intraday rahu/yama/gulika times and schedule one-shot alarms
                    scheduleRahuKalamIntradayAlerts(context)
                    // Reschedule this daily scheduler for tomorrow
                    if (hour >= 0 && workName.isNotEmpty()) {
                        NotificationScheduler.rescheduleRecurring(context, workName, hour, minute, timezoneId, intent)
                    }
                    return@launch
                }

                val body = withTimeoutOrNull(5_000) { enrichBody(type, fallbackBody) } ?: fallbackBody
                NotificationHelper.showNotification(context, body, notifId, type)

                // Reschedule for tomorrow (one-shot types do not reschedule)
                val isOneShot = type == NotificationWorker.TYPE_GRAHANAM_BEFORE ||
                    type == NotificationWorker.TYPE_GRAHANAM_ON ||
                    type == NotificationWorker.TYPE_FESTIVAL ||
                    type == NotificationWorker.TYPE_RAHU_KALAM ||
                    type == NotificationWorker.TYPE_YAMAGANDAM ||
                    type == NotificationWorker.TYPE_GULIKA ||
                    type == NotificationWorker.TYPE_PLANET_TRANSIT
                if (!isOneShot && hour >= 0 && workName.isNotEmpty()) {
                    NotificationScheduler.rescheduleRecurring(context, workName, hour, minute, timezoneId, intent)
                }
            } finally {
                pendingResult.finish()
            }
        }
    }

    private suspend fun scheduleRahuKalamIntradayAlerts(context: Context) {
        try {
            val lat = prefs.locationLat.first()
            val lng = prefs.locationLng.first()
            val timezoneId = prefs.locationTimezone.first()
            val tz = if (timezoneId.isNotBlank()) TimeZone.getTimeZone(timezoneId) else TimeZone.getDefault()

            val cal = Calendar.getInstance(tz)
            val year = cal.get(Calendar.YEAR)
            val month = cal.get(Calendar.MONTH) + 1
            val day = cal.get(Calendar.DAY_OF_MONTH)
            // 1-based day of week: Sun=1..Sat=7
            val dayOfWeek = cal.get(Calendar.DAY_OF_WEEK)
            val utcOffsetHours = tz.getOffset(cal.timeInMillis) / 3_600_000.0

            val sunTimes = AstronomicalCalculator.calculateSunTimesDecimal(lat, lng, year, month, day, utcOffsetHours)
            val now = System.currentTimeMillis()

            scheduleIntradayAlarm(context, now, cal, sunTimes, dayOfWeek, RAHU_KAAL_SLOTS,
                NotificationWorker.TYPE_RAHU_KALAM, 4001, "రాహు కాలం వస్తోంది / Rahu Kalam starting soon")
            scheduleIntradayAlarm(context, now, cal, sunTimes, dayOfWeek, YAMAGANDAM_SLOTS,
                NotificationWorker.TYPE_YAMAGANDAM, 4002, "యమగండం వస్తోంది / Yamagandam starting soon")
            scheduleIntradayAlarm(context, now, cal, sunTimes, dayOfWeek, GULIKA_KALAM_SLOTS,
                NotificationWorker.TYPE_GULIKA, 4003, "గులిక కాలం వస్తోంది / Gulika Kalam starting soon")
        } catch (_: Exception) {
            // Silent fail — if location is unavailable, skip for today
        }
    }

    private fun scheduleIntradayAlarm(
        context: Context,
        now: Long,
        dayCal: Calendar,
        sunTimes: AstronomicalCalculator.SunTimesDecimal,
        dayOfWeek: Int,
        slotTable: IntArray,
        type: String,
        notifId: Int,
        body: String,
    ) {
        val slot = slotTable[dayOfWeek]
        if (slot <= 0) return
        val dayLength = sunTimes.sunsetDecimal - sunTimes.sunriseDecimal
        val slotDuration = dayLength / 8.0
        val startDecimal = sunTimes.sunriseDecimal + (slot - 1) * slotDuration

        val startCal = dayCal.clone() as Calendar
        val startHour = startDecimal.toInt()
        val startMin = ((startDecimal - startHour) * 60).toInt()
        startCal.set(Calendar.HOUR_OF_DAY, startHour)
        startCal.set(Calendar.MINUTE, startMin)
        startCal.set(Calendar.SECOND, 0)
        startCal.set(Calendar.MILLISECOND, 0)

        // Alert 10 minutes before
        val alertMs = startCal.timeInMillis - 10 * 60 * 1000L
        if (alertMs <= now) return // already past

        val timeStr = AstronomicalCalculator.formatTime(startDecimal)
        val fullBody = "$body · $timeStr"
        val workName = when (type) {
            NotificationWorker.TYPE_RAHU_KALAM -> "rahu_kalam_today"
            NotificationWorker.TYPE_YAMAGANDAM -> "yamagandam_today"
            else -> "gulika_today"
        }

        val alertIntent = Intent(context, AlarmReceiver::class.java).apply {
            putExtra(NotificationWorker.KEY_NOTIFICATION_BODY, fullBody)
            putExtra(NotificationWorker.KEY_NOTIFICATION_ID, notifId)
            putExtra(NotificationWorker.KEY_NOTIFICATION_TYPE, type)
            putExtra(NotificationWorker.KEY_WORK_NAME, workName)
        }
        NotificationAlarmScheduler.scheduleAt(context, workName, alertMs, alertIntent)
    }

    private suspend fun enrichBody(type: String, fallback: String): String {
        return try {
            val dayOfYear = Calendar.getInstance().get(Calendar.DAY_OF_YEAR)
            val shloka = db.shlokaDao().getShlokaForDaySync(dayOfYear) ?: return fallback
            when (type) {
                NotificationWorker.TYPE_MORNING -> {
                    val text = shloka.textSanskrit.take(120)
                    val meaning = shloka.meaningTelugu?.take(80) ?: ""
                    "$text\n$meaning"
                }
                NotificationWorker.TYPE_EVENING ->
                    shloka.meaningEnglish?.take(120) ?: shloka.textSanskrit.take(120)
                NotificationWorker.TYPE_SHLOKA -> {
                    val text = shloka.textSanskrit.take(100)
                    val meaning = shloka.meaningTelugu?.take(100) ?: shloka.meaningEnglish?.take(100) ?: ""
                    if (meaning.isNotBlank()) "$text\n$meaning" else text
                }
                else -> fallback
            }
        } catch (_: Exception) {
            fallback
        }
    }
}
