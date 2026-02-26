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
        const val TYPE_MORNING = "morning"
        const val TYPE_EVENING = "evening"
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

        return Result.success()
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
        val intent = Intent(applicationContext, MainActivity::class.java).apply {
            flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
        }
        val pendingIntent = PendingIntent.getActivity(
            applicationContext,
            0,
            intent,
            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE,
        )

        val title = when (type) {
            TYPE_MORNING -> "NityaPooja - Morning Blessing"
            TYPE_EVENING -> "NityaPooja - Evening Aarti"
            else -> "NityaPooja"
        }

        val notification = NotificationCompat.Builder(applicationContext, CHANNEL_ID)
            .setSmallIcon(R.drawable.ic_launcher_foreground)
            .setContentTitle(title)
            .setContentText(body)
            .setStyle(NotificationCompat.BigTextStyle().bigText(body))
            .setPriority(NotificationCompat.PRIORITY_DEFAULT)
            .setContentIntent(pendingIntent)
            .setAutoCancel(true)
            .build()

        val notificationManager =
            applicationContext.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
        notificationManager.notify(notificationId, notification)
    }
}
