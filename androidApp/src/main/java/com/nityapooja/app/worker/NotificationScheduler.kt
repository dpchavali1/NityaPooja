package com.nityapooja.app.worker

import android.content.Context
import androidx.work.*
import java.util.Calendar
import java.util.concurrent.TimeUnit

object NotificationScheduler {

    private const val MORNING_WORK_NAME = "morning_reminder"
    private const val EVENING_WORK_NAME = "evening_reminder"
    private const val PANCHANG_WORK_NAME = "panchang_reminder"
    private const val MORNING_NOTIFICATION_ID = 1001
    private const val EVENING_NOTIFICATION_ID = 1002
    private const val PANCHANG_NOTIFICATION_ID = 1003

    fun scheduleMorningReminder(context: Context, hour: Int, minute: Int) {
        val inputData = workDataOf(
            NotificationWorker.KEY_NOTIFICATION_BODY to "Time for morning prayers",
            NotificationWorker.KEY_NOTIFICATION_ID to MORNING_NOTIFICATION_ID,
            NotificationWorker.KEY_NOTIFICATION_TYPE to NotificationWorker.TYPE_MORNING,
        )
        scheduleReminder(
            context = context,
            workName = MORNING_WORK_NAME,
            hour = hour,
            minute = minute,
            inputData = inputData,
        )
    }

    fun scheduleEveningReminder(context: Context, hour: Int, minute: Int) {
        val inputData = workDataOf(
            NotificationWorker.KEY_NOTIFICATION_BODY to "Time for evening aarti",
            NotificationWorker.KEY_NOTIFICATION_ID to EVENING_NOTIFICATION_ID,
            NotificationWorker.KEY_NOTIFICATION_TYPE to NotificationWorker.TYPE_EVENING,
        )
        scheduleReminder(
            context = context,
            workName = EVENING_WORK_NAME,
            hour = hour,
            minute = minute,
            inputData = inputData,
        )
    }

    fun schedulePanchangReminder(context: Context) {
        val inputData = workDataOf(
            NotificationWorker.KEY_NOTIFICATION_BODY to "Check today's Panchangam for auspicious timings",
            NotificationWorker.KEY_NOTIFICATION_ID to PANCHANG_NOTIFICATION_ID,
            NotificationWorker.KEY_NOTIFICATION_TYPE to "panchang",
        )
        scheduleReminder(
            context = context,
            workName = PANCHANG_WORK_NAME,
            hour = 6,
            minute = 0,
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
        inputData: Data,
    ) {
        val initialDelay = calculateInitialDelay(hour, minute)

        val workRequest = PeriodicWorkRequestBuilder<NotificationWorker>(
            24, TimeUnit.HOURS,
        )
            .setInitialDelay(initialDelay, TimeUnit.MILLISECONDS)
            .setInputData(inputData)
            .setConstraints(
                Constraints.Builder()
                    .setRequiresBatteryNotLow(false)
                    .build()
            )
            .build()

        WorkManager.getInstance(context).enqueueUniquePeriodicWork(
            workName,
            ExistingPeriodicWorkPolicy.UPDATE,
            workRequest,
        )
    }

    private fun calculateInitialDelay(hour: Int, minute: Int): Long {
        val now = Calendar.getInstance()
        val target = Calendar.getInstance().apply {
            set(Calendar.HOUR_OF_DAY, hour)
            set(Calendar.MINUTE, minute)
            set(Calendar.SECOND, 0)
            set(Calendar.MILLISECOND, 0)
        }

        if (target.before(now)) {
            target.add(Calendar.DAY_OF_YEAR, 1)
        }

        return target.timeInMillis - now.timeInMillis
    }
}
