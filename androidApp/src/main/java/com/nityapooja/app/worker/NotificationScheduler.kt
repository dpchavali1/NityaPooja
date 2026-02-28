package com.nityapooja.app.worker

import android.content.Context
import androidx.work.*
import java.util.Calendar
import java.util.TimeZone
import java.util.concurrent.TimeUnit

object NotificationScheduler {

    private const val MORNING_WORK_NAME = "morning_reminder"
    private const val EVENING_WORK_NAME = "evening_reminder"
    private const val PANCHANG_WORK_NAME = "panchang_reminder"
    private const val MORNING_NOTIFICATION_ID = 1001
    private const val EVENING_NOTIFICATION_ID = 1002
    private const val PANCHANG_NOTIFICATION_ID = 1003

    fun scheduleMorningReminder(context: Context, hour: Int, minute: Int, timezoneId: String = "") {
        val inputData = workDataOf(
            NotificationWorker.KEY_NOTIFICATION_BODY to "Time for morning prayers",
            NotificationWorker.KEY_NOTIFICATION_ID to MORNING_NOTIFICATION_ID,
            NotificationWorker.KEY_NOTIFICATION_TYPE to NotificationWorker.TYPE_MORNING,
            NotificationWorker.KEY_HOUR to hour,
            NotificationWorker.KEY_MINUTE to minute,
            NotificationWorker.KEY_TIMEZONE to timezoneId,
            NotificationWorker.KEY_WORK_NAME to MORNING_WORK_NAME,
        )
        scheduleReminder(
            context = context,
            workName = MORNING_WORK_NAME,
            hour = hour,
            minute = minute,
            timezoneId = timezoneId,
            inputData = inputData,
        )
    }

    fun scheduleEveningReminder(context: Context, hour: Int, minute: Int, timezoneId: String = "") {
        val inputData = workDataOf(
            NotificationWorker.KEY_NOTIFICATION_BODY to "Time for evening aarti",
            NotificationWorker.KEY_NOTIFICATION_ID to EVENING_NOTIFICATION_ID,
            NotificationWorker.KEY_NOTIFICATION_TYPE to NotificationWorker.TYPE_EVENING,
            NotificationWorker.KEY_HOUR to hour,
            NotificationWorker.KEY_MINUTE to minute,
            NotificationWorker.KEY_TIMEZONE to timezoneId,
            NotificationWorker.KEY_WORK_NAME to EVENING_WORK_NAME,
        )
        scheduleReminder(
            context = context,
            workName = EVENING_WORK_NAME,
            hour = hour,
            minute = minute,
            timezoneId = timezoneId,
            inputData = inputData,
        )
    }

    fun schedulePanchangReminder(context: Context, timezoneId: String = "") {
        val inputData = workDataOf(
            NotificationWorker.KEY_NOTIFICATION_BODY to "Check today's Panchangam for auspicious timings",
            NotificationWorker.KEY_NOTIFICATION_ID to PANCHANG_NOTIFICATION_ID,
            NotificationWorker.KEY_NOTIFICATION_TYPE to "panchang",
            NotificationWorker.KEY_HOUR to 6,
            NotificationWorker.KEY_MINUTE to 0,
            NotificationWorker.KEY_TIMEZONE to timezoneId,
            NotificationWorker.KEY_WORK_NAME to PANCHANG_WORK_NAME,
        )
        scheduleReminder(
            context = context,
            workName = PANCHANG_WORK_NAME,
            hour = 6,
            minute = 0,
            timezoneId = timezoneId,
            inputData = inputData,
        )
    }

    fun cancelMorningReminder(context: Context) {
        WorkManager.getInstance(context).cancelUniqueWork(MORNING_WORK_NAME)
    }

    fun cancelEveningReminder(context: Context) {
        WorkManager.getInstance(context).cancelUniqueWork(EVENING_WORK_NAME)
    }

    fun cancelPanchangReminder(context: Context) {
        WorkManager.getInstance(context).cancelUniqueWork(PANCHANG_WORK_NAME)
    }

    private fun scheduleReminder(
        context: Context,
        workName: String,
        hour: Int,
        minute: Int,
        timezoneId: String,
        inputData: Data,
    ) {
        val delay = calculateDelayMillis(hour, minute, timezoneId)

        val workRequest = OneTimeWorkRequestBuilder<NotificationWorker>()
            .setInitialDelay(delay, TimeUnit.MILLISECONDS)
            .setInputData(inputData)
            .setConstraints(
                Constraints.Builder()
                    .setRequiresBatteryNotLow(false)
                    .build()
            )
            .build()

        WorkManager.getInstance(context).enqueueUniqueWork(
            workName,
            ExistingWorkPolicy.REPLACE,
            workRequest,
        )
    }

    /**
     * Calculates milliseconds until the next occurrence of [hour]:[minute] in [timezoneId].
     * Falls back to the device timezone if [timezoneId] is blank.
     */
    internal fun calculateDelayMillis(hour: Int, minute: Int, timezoneId: String): Long {
        val tz = if (timezoneId.isNotBlank()) TimeZone.getTimeZone(timezoneId) else TimeZone.getDefault()
        val now = Calendar.getInstance(tz)
        val target = Calendar.getInstance(tz).apply {
            set(Calendar.HOUR_OF_DAY, hour)
            set(Calendar.MINUTE, minute)
            set(Calendar.SECOND, 0)
            set(Calendar.MILLISECOND, 0)
        }
        if (!target.after(now)) target.add(Calendar.DAY_OF_YEAR, 1)
        return target.timeInMillis - now.timeInMillis
    }
}
