package com.nityapooja.app.worker

import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import androidx.core.app.NotificationCompat
import com.nityapooja.app.MainActivity
import com.nityapooja.app.NityaPoojaApp
import com.nityapooja.app.R

object NotificationHelper {

    fun showNotification(
        context: Context,
        body: String,
        notificationId: Int,
        type: String,
    ) {
        val navRoute = when (type) {
            NotificationWorker.TYPE_QUIZ -> "purana_quiz"
            NotificationWorker.TYPE_GRAHANAM_BEFORE, NotificationWorker.TYPE_GRAHANAM_ON -> "panchangam"
            NotificationWorker.TYPE_FESTIVAL -> "home"
            NotificationWorker.TYPE_RAHU_KALAM, NotificationWorker.TYPE_YAMAGANDAM, NotificationWorker.TYPE_GULIKA -> "panchangam"
            NotificationWorker.TYPE_PLANET_TRANSIT -> "planet_transits"
            else -> null
        }
        val intent = Intent(context, MainActivity::class.java).apply {
            flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
            navRoute?.let { putExtra(NotificationWorker.EXTRA_NAV_ROUTE, it) }
        }
        val pendingIntent = PendingIntent.getActivity(
            context, notificationId, intent,
            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE,
        )

        val title = when (type) {
            NotificationWorker.TYPE_MORNING -> "NityaPooja - Morning Blessing"
            NotificationWorker.TYPE_EVENING -> "NityaPooja - Evening Aarti"
            NotificationWorker.TYPE_QUIZ -> "NityaPooja - Puranas Quiz"
            NotificationWorker.TYPE_FESTIVAL -> "NityaPooja - పండుగ శుభాకాంక్షలు"
            NotificationWorker.TYPE_GRAHANAM_BEFORE -> "రేపు గ్రహణం / Eclipse Tomorrow"
            NotificationWorker.TYPE_GRAHANAM_ON -> "గ్రహణ కాలం / Grahanam Today"
            NotificationWorker.TYPE_SHLOKA -> "NityaPooja - రోజువారీ శ్లోకం"
            NotificationWorker.TYPE_RAHU_KALAM -> "రాహు కాల హెచ్చరిక / Rahu Kalam Alert"
            NotificationWorker.TYPE_YAMAGANDAM -> "యమగండం హెచ్చరిక / Yamagandam Alert"
            NotificationWorker.TYPE_GULIKA -> "గులిక కాలం హెచ్చరిక / Gulika Kalam Alert"
            NotificationWorker.TYPE_PLANET_TRANSIT -> "గ్రహ పరివర్తన / Planet Transit"
            else -> "NityaPooja"
        }

        val channelId = when (type) {
            NotificationWorker.TYPE_GRAHANAM_BEFORE, NotificationWorker.TYPE_GRAHANAM_ON,
            NotificationWorker.TYPE_FESTIVAL,
            NotificationWorker.TYPE_RAHU_KALAM, NotificationWorker.TYPE_YAMAGANDAM,
            NotificationWorker.TYPE_GULIKA, NotificationWorker.TYPE_PLANET_TRANSIT -> NityaPoojaApp.CHANNEL_FESTIVAL_ALERTS
            else -> NityaPoojaApp.CHANNEL_DAILY_REMINDER
        }

        val priority = if (type == NotificationWorker.TYPE_GRAHANAM_BEFORE ||
            type == NotificationWorker.TYPE_GRAHANAM_ON ||
            type == NotificationWorker.TYPE_FESTIVAL ||
            type == NotificationWorker.TYPE_RAHU_KALAM ||
            type == NotificationWorker.TYPE_YAMAGANDAM ||
            type == NotificationWorker.TYPE_GULIKA ||
            type == NotificationWorker.TYPE_PLANET_TRANSIT
        ) {
            NotificationCompat.PRIORITY_HIGH
        } else {
            NotificationCompat.PRIORITY_DEFAULT
        }

        val notification = NotificationCompat.Builder(context, channelId)
            .setSmallIcon(R.drawable.ic_launcher_foreground)
            .setContentTitle(title)
            .setContentText(body)
            .setStyle(NotificationCompat.BigTextStyle().bigText(body))
            .setPriority(priority)
            .setContentIntent(pendingIntent)
            .setAutoCancel(true)
            .build()

        val nm = context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
        nm.notify(notificationId, notification)
    }
}
