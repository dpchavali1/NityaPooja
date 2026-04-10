package com.nityapooja.app.worker

import android.app.AlarmManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.os.Build
import com.nityapooja.app.MainActivity

/**
 * Wraps AlarmManager for notification scheduling.
 *
 * On Android 12+ (API 31+), ALL exact alarm APIs (setAlarmClock, setExact,
 * setExactAndAllowWhileIdle) require SCHEDULE_EXACT_ALARM permission, which defaults to
 * DENIED on new installs targeting API 33+. Every scheduling path checks
 * canScheduleExactAlarms() and gracefully falls back to setAndAllowWhileIdle() (inexact,
 * but still wakes the device through Doze — typically within ~15 minutes).
 *
 * When the permission IS granted:
 *  - [scheduleAlarmClock]: setAlarmClock() — device EXITS Doze before firing, shows clock
 *    icon in status bar, gets OEM kill-list exemption (Samsung/Xiaomi). Best reliability.
 *    Use for morning, evening, panchang, grahanam, festival alarms.
 *  - [scheduleAt]: setExactAndAllowWhileIdle() — fires during a Doze maintenance window.
 *    Use for lower-priority alarms (quiz, vrata, sacred month).
 */
object NotificationAlarmScheduler {

    /**
     * Schedule using setAlarmClock when SCHEDULE_EXACT_ALARM is granted (highest reliability),
     * otherwise falls back to setAndAllowWhileIdle (inexact but Doze-safe).
     */
    fun scheduleAlarmClock(context: Context, workName: String, triggerAtMs: Long, intent: Intent) {
        val pi = buildPendingIntent(context, workName, intent)
        val am = context.getSystemService(Context.ALARM_SERVICE) as AlarmManager
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S && !am.canScheduleExactAlarms()) {
            // Permission not yet granted — use inexact fallback; AlarmPermissionReceiver will
            // reschedule with exact precision once the user grants it in Settings.
            am.setAndAllowWhileIdle(AlarmManager.RTC_WAKEUP, triggerAtMs, pi)
            return
        }
        val showIntent = PendingIntent.getActivity(
            context,
            0,
            Intent(context, MainActivity::class.java).apply {
                flags = Intent.FLAG_ACTIVITY_NEW_TASK
            },
            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE,
        )
        am.setAlarmClock(AlarmManager.AlarmClockInfo(triggerAtMs, showIntent), pi)
    }

    /**
     * Schedule using setExactAndAllowWhileIdle when permission is granted,
     * otherwise setAndAllowWhileIdle (inexact fallback).
     */
    fun scheduleAt(context: Context, workName: String, triggerAtMs: Long, intent: Intent) {
        val pi = buildPendingIntent(context, workName, intent)
        val am = context.getSystemService(Context.ALARM_SERVICE) as AlarmManager
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S && am.canScheduleExactAlarms()) {
            am.setExactAndAllowWhileIdle(AlarmManager.RTC_WAKEUP, triggerAtMs, pi)
        } else {
            am.setAndAllowWhileIdle(AlarmManager.RTC_WAKEUP, triggerAtMs, pi)
        }
    }

    fun cancel(context: Context, workName: String) {
        val intent = Intent(context, AlarmReceiver::class.java)
        val pi = PendingIntent.getBroadcast(
            context,
            requestCodeFor(workName),
            intent,
            PendingIntent.FLAG_NO_CREATE or PendingIntent.FLAG_IMMUTABLE,
        ) ?: return
        val am = context.getSystemService(Context.ALARM_SERVICE) as AlarmManager
        am.cancel(pi)
    }

    private fun buildPendingIntent(context: Context, workName: String, intent: Intent): PendingIntent =
        PendingIntent.getBroadcast(
            context,
            requestCodeFor(workName),
            intent,
            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE,
        )

    internal fun requestCodeFor(workName: String) = when (workName) {
        "morning_reminder" -> 1001
        "evening_reminder" -> 1002
        "panchang_reminder" -> 1003
        "quiz_reminder" -> 1004
        "vrata_reminder" -> 1005
        "sacred_month_reminder" -> 1006
        else -> (workName.hashCode() and 0x7FFFFFFF) + 10_000
    }
}
