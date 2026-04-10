package com.nityapooja.app.worker

import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import androidx.core.app.NotificationCompat
import androidx.work.CoroutineWorker
import androidx.work.WorkerParameters
import com.nityapooja.app.MainActivity
import com.nityapooja.app.R
import com.nityapooja.shared.data.local.db.NityaPoojaDatabase
import java.util.Calendar
import org.koin.core.component.KoinComponent
import org.koin.core.component.inject

class NotificationWorker(
    context: Context,
    params: WorkerParameters,
) : CoroutineWorker(context, params), KoinComponent {

    private val db: NityaPoojaDatabase by inject()

    companion object {
        const val CHANNEL_ID = "daily_reminder"
        const val CHANNEL_NAME = "Daily Reminders"
        const val KEY_NOTIFICATION_BODY = "notification_body"
        const val KEY_NOTIFICATION_ID = "notification_id"
        const val KEY_NOTIFICATION_TYPE = "notification_type"
        const val KEY_HOUR = "hour"
        const val KEY_MINUTE = "minute"
        const val KEY_TIMEZONE = "timezone"
        const val KEY_WORK_NAME = "work_name"
        const val TYPE_MORNING = "morning"
        const val TYPE_EVENING = "evening"
        const val TYPE_QUIZ = "quiz"
        const val TYPE_GRAHANAM_BEFORE = "grahanam_before"
        const val TYPE_GRAHANAM_ON = "grahanam_on"
        const val TYPE_FESTIVAL = "festival"
        const val TYPE_SHLOKA = "shloka"
        const val TYPE_RAHU_KALAM_SCHEDULER = "rahu_kalam_scheduler"
        const val TYPE_RAHU_KALAM = "rahu_kalam"
        const val TYPE_YAMAGANDAM = "yamagandam"
        const val TYPE_GULIKA = "gulika"
        const val TYPE_PLANET_TRANSIT = "planet_transit"
        const val KEY_SPARSHA = "sparsha_time"
        const val KEY_MADHYAM = "madhyam_time"
        const val KEY_MOKSHAM = "moksham_time"
        const val KEY_GRAHA_NAME_TELUGU = "graha_name_telugu"
        const val KEY_FROM_RASHI_TELUGU = "from_rashi_telugu"
        const val KEY_TO_RASHI_TELUGU = "to_rashi_telugu"
        const val EXTRA_NAV_ROUTE = "nav_route"
    }

    override suspend fun doWork(): Result {
        val type = inputData.getString(KEY_NOTIFICATION_TYPE) ?: TYPE_MORNING
        val fallbackBody = inputData.getString(KEY_NOTIFICATION_BODY)
            ?: "It's time for your daily puja"
        val notificationId = inputData.getInt(KEY_NOTIFICATION_ID, 1001)

        val body = try {
            enrichNotificationBody(type, fallbackBody)
        } catch (_: Exception) {
            fallbackBody
        }

        createNotificationChannel()
        showNotification(body, notificationId, type)

        val isGrahanam = type == TYPE_GRAHANAM_BEFORE || type == TYPE_GRAHANAM_ON
        if (!isGrahanam) rescheduleForTomorrow()

        return Result.success()
    }

    private fun rescheduleForTomorrow() {
        val workName = inputData.getString(KEY_WORK_NAME) ?: return
        val hour = inputData.getInt(KEY_HOUR, -1).takeIf { it >= 0 } ?: return
        val minute = inputData.getInt(KEY_MINUTE, -1).takeIf { it >= 0 } ?: return
        val timezoneId = inputData.getString(KEY_TIMEZONE) ?: ""

        val intent = android.content.Intent(applicationContext, AlarmReceiver::class.java).apply {
            putExtra(KEY_NOTIFICATION_BODY, inputData.getString(KEY_NOTIFICATION_BODY) ?: "")
            putExtra(KEY_NOTIFICATION_ID, inputData.getInt(KEY_NOTIFICATION_ID, 1001))
            putExtra(KEY_NOTIFICATION_TYPE, inputData.getString(KEY_NOTIFICATION_TYPE) ?: "")
            putExtra(KEY_HOUR, hour)
            putExtra(KEY_MINUTE, minute)
            putExtra(KEY_TIMEZONE, timezoneId)
            putExtra(KEY_WORK_NAME, workName)
        }
        NotificationScheduler.rescheduleRecurring(applicationContext, workName, hour, minute, timezoneId, intent)
    }

    private suspend fun enrichNotificationBody(type: String, fallback: String): String {
        return try {
            val dayOfYear = Calendar.getInstance().get(Calendar.DAY_OF_YEAR)
            val shloka = db.shlokaDao().getShlokaForDaySync(dayOfYear)

            if (shloka != null) {
                when (type) {
                    TYPE_MORNING -> {
                        val text = shloka.textSanskrit.take(120)
                        val meaning = shloka.meaningTelugu?.take(80) ?: ""
                        "$text\n$meaning"
                    }
                    TYPE_EVENING -> {
                        shloka.meaningEnglish?.take(120) ?: shloka.textSanskrit.take(120)
                    }
                    else -> fallback
                }
            } else {
                fallback
            }
        } catch (_: Exception) {
            fallback
        }
    }

    private fun createNotificationChannel() {
        val channel = NotificationChannel(
            CHANNEL_ID,
            CHANNEL_NAME,
            NotificationManager.IMPORTANCE_DEFAULT,
        ).apply {
            description = "Daily puja and prayer reminders"
        }
        val notificationManager =
            applicationContext.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
        notificationManager.createNotificationChannel(channel)
    }

    private fun showNotification(body: String, notificationId: Int, type: String) {
        val navRoute = when (type) {
            TYPE_QUIZ -> "purana_quiz"
            TYPE_GRAHANAM_BEFORE, TYPE_GRAHANAM_ON -> "panchangam"
            TYPE_FESTIVAL -> "home"
            else -> null
        }
        val intent = Intent(applicationContext, MainActivity::class.java).apply {
            flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
            navRoute?.let { putExtra(EXTRA_NAV_ROUTE, it) }
        }
        val pendingIntent = PendingIntent.getActivity(
            applicationContext,
            notificationId,
            intent,
            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE,
        )

        val title = when (type) {
            TYPE_MORNING -> "NityaPooja - Morning Blessing"
            TYPE_EVENING -> "NityaPooja - Evening Aarti"
            TYPE_QUIZ -> "NityaPooja - Puranas Quiz 📖"
            TYPE_FESTIVAL -> "NityaPooja - పండుగ శుభాకాంక్షలు 🙏"
            TYPE_GRAHANAM_BEFORE -> "రేపు గ్రహణం / Eclipse Tomorrow"
            TYPE_GRAHANAM_ON -> "గ్రహణ కాలం / Grahanam Today"
            else -> "NityaPooja"
        }

        val channelId = when (type) {
            TYPE_GRAHANAM_BEFORE, TYPE_GRAHANAM_ON -> com.nityapooja.app.NityaPoojaApp.CHANNEL_FESTIVAL_ALERTS
            else -> CHANNEL_ID
        }

        val notification = NotificationCompat.Builder(applicationContext, channelId)
            .setSmallIcon(R.drawable.ic_launcher_foreground)
            .setContentTitle(title)
            .setContentText(body)
            .setStyle(NotificationCompat.BigTextStyle().bigText(body))
            .setPriority(if (type == TYPE_GRAHANAM_BEFORE || type == TYPE_GRAHANAM_ON) NotificationCompat.PRIORITY_HIGH else NotificationCompat.PRIORITY_DEFAULT)
            .setContentIntent(pendingIntent)
            .setAutoCancel(true)
            .build()

        val notificationManager =
            applicationContext.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
        notificationManager.notify(notificationId, notification)
    }
}
