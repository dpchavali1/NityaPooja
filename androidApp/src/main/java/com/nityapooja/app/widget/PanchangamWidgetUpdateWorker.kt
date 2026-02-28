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

class PanchangamWidgetUpdateWorker(
    context: Context,
    params: WorkerParameters,
) : CoroutineWorker(context, params) {

    override suspend fun doWork(): Result {
        try {
            PanchangamWidget().updateAll(applicationContext)
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
        val delay = millisUntilMidnight.coerceAtLeast(60_000L) // at least 1 minute

        val next = OneTimeWorkRequestBuilder<PanchangamWidgetUpdateWorker>()
            .setInitialDelay(delay, TimeUnit.MILLISECONDS)
            .build()

        WorkManager.getInstance(applicationContext)
            .enqueueUniqueWork(WORK_NAME, ExistingWorkPolicy.REPLACE, next)
    }

    companion object {
        const val WORK_NAME = "panchangam_widget_update"

        fun schedule(context: Context) {
            // Run immediately on first call, then self-rescheduling takes over
            val request = OneTimeWorkRequestBuilder<PanchangamWidgetUpdateWorker>().build()
            WorkManager.getInstance(context)
                .enqueueUniqueWork(WORK_NAME, ExistingWorkPolicy.KEEP, request)
        }
    }
}
