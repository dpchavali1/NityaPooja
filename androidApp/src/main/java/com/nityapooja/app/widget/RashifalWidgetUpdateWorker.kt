package com.nityapooja.app.widget

import android.content.Context
import androidx.glance.appwidget.updateAll
import androidx.work.CoroutineWorker
import androidx.work.ExistingWorkPolicy
import androidx.work.OneTimeWorkRequestBuilder
import androidx.work.WorkManager
import androidx.work.WorkerParameters
import java.util.Calendar
import java.util.concurrent.TimeUnit

class RashifalWidgetUpdateWorker(
    context: Context,
    params: WorkerParameters,
) : CoroutineWorker(context, params) {

    override suspend fun doWork(): Result {
        try {
            RashifalWidget().updateAll(applicationContext)
        } catch (_: Exception) { /* widget may not be placed yet */ }
        rescheduleForTomorrow()
        return Result.success()
    }

    private fun rescheduleForTomorrow() {
        val cal = Calendar.getInstance()
        val secondsPastMidnight = cal.get(Calendar.HOUR_OF_DAY) * 3600L +
            cal.get(Calendar.MINUTE) * 60L +
            cal.get(Calendar.SECOND)
        val millisUntilMidnight = (86_400L - secondsPastMidnight) * 1000L
        val delay = millisUntilMidnight.coerceAtLeast(60_000L)

        val next = OneTimeWorkRequestBuilder<RashifalWidgetUpdateWorker>()
            .setInitialDelay(delay, TimeUnit.MILLISECONDS)
            .build()

        WorkManager.getInstance(applicationContext)
            .enqueueUniqueWork(WORK_NAME, ExistingWorkPolicy.REPLACE, next)
    }

    companion object {
        const val WORK_NAME = "rashifal_widget_update"

        fun schedule(context: Context) {
            val request = OneTimeWorkRequestBuilder<RashifalWidgetUpdateWorker>().build()
            WorkManager.getInstance(context)
                .enqueueUniqueWork(WORK_NAME, ExistingWorkPolicy.KEEP, request)
        }
    }
}
