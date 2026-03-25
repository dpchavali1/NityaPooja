package com.nityapooja.app.worker

import android.content.Context
import androidx.work.*
import com.nityapooja.shared.data.grahanam.GrahanamData
import com.nityapooja.shared.data.grahanam.GrahanamType
import com.nityapooja.shared.platform.FestivalNotificationInfo
import java.util.Calendar
import java.util.TimeZone
import java.util.concurrent.TimeUnit

object NotificationScheduler {

    private const val MORNING_WORK_NAME = "morning_reminder"
    private const val EVENING_WORK_NAME = "evening_reminder"
    private const val PANCHANG_WORK_NAME = "panchang_reminder"
    private const val QUIZ_WORK_NAME = "quiz_reminder"
    private const val MORNING_NOTIFICATION_ID = 1001
    private const val EVENING_NOTIFICATION_ID = 1002
    private const val PANCHANG_NOTIFICATION_ID = 1003
    private const val QUIZ_NOTIFICATION_ID = 1004
    const val GRAHANAM_TAG = "grahanam"
    private const val GRAHANAM_NOTIFICATION_ID_BEFORE = 2001
    private const val GRAHANAM_NOTIFICATION_ID_ON = 2002

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

    fun scheduleQuizReminder(context: Context, hour: Int, minute: Int, timezoneId: String = "") {
        val inputData = workDataOf(
            NotificationWorker.KEY_NOTIFICATION_BODY to "పురాణాల క్విజ్ సమయం 📖 / Time for your Puranas Quiz!",
            NotificationWorker.KEY_NOTIFICATION_ID to QUIZ_NOTIFICATION_ID,
            NotificationWorker.KEY_NOTIFICATION_TYPE to NotificationWorker.TYPE_QUIZ,
            NotificationWorker.KEY_HOUR to hour,
            NotificationWorker.KEY_MINUTE to minute,
            NotificationWorker.KEY_TIMEZONE to timezoneId,
            NotificationWorker.KEY_WORK_NAME to QUIZ_WORK_NAME,
        )
        scheduleReminder(
            context = context,
            workName = QUIZ_WORK_NAME,
            hour = hour,
            minute = minute,
            timezoneId = timezoneId,
            inputData = inputData,
        )
    }

    fun cancelQuizReminder(context: Context) {
        WorkManager.getInstance(context).cancelUniqueWork(QUIZ_WORK_NAME)
    }

    fun scheduleGrahanamNotifications(context: Context, grahanamList: List<GrahanamData>, timezoneId: String) {
        val wm = WorkManager.getInstance(context)
        val tz = if (timezoneId.isNotBlank()) TimeZone.getTimeZone(timezoneId) else TimeZone.getDefault()
        val now = System.currentTimeMillis()

        for (grahanam in grahanamList) {
            val sparthaMs = grahanam.sparthaUtc.toEpochMilliseconds()
            val mokshamMs = grahanam.mokshamUtc.toEpochMilliseconds()
            if (mokshamMs <= now) continue

            val sparthaFormatted = formatInstantLocal(sparthaMs, tz)
            val madhyamFormatted = formatInstantLocal(grahanam.madhyamUtc.toEpochMilliseconds(), tz)
            val mokshamFormatted = formatInstantLocal(mokshamMs, tz)
            val typeLabel = if (grahanam.type == GrahanamType.SURYA) "Surya Grahanam" else "Chandra Grahanam"

            // Day-before: 8 AM local time the day before Sparsha
            val dayBeforeAt8 = Calendar.getInstance(tz).apply {
                timeInMillis = sparthaMs
                add(Calendar.DAY_OF_YEAR, -1)
                set(Calendar.HOUR_OF_DAY, 8)
                set(Calendar.MINUTE, 0)
                set(Calendar.SECOND, 0)
                set(Calendar.MILLISECOND, 0)
            }
            if (dayBeforeAt8.timeInMillis > now) {
                val body = "రేపు ${if (grahanam.type == GrahanamType.SURYA) "సూర్య" else "చంద్ర"} గ్రహణం · $typeLabel · స్పర్శ: $sparthaFormatted"
                val inputData = workDataOf(
                    NotificationWorker.KEY_NOTIFICATION_BODY to body,
                    NotificationWorker.KEY_NOTIFICATION_ID to GRAHANAM_NOTIFICATION_ID_BEFORE,
                    NotificationWorker.KEY_NOTIFICATION_TYPE to NotificationWorker.TYPE_GRAHANAM_BEFORE,
                    NotificationWorker.KEY_SPARSHA to sparthaFormatted,
                    NotificationWorker.KEY_MADHYAM to madhyamFormatted,
                    NotificationWorker.KEY_MOKSHAM to mokshamFormatted,
                )
                val request = OneTimeWorkRequestBuilder<NotificationWorker>()
                    .setInitialDelay(dayBeforeAt8.timeInMillis - now, TimeUnit.MILLISECONDS)
                    .setInputData(inputData)
                    .addTag(GRAHANAM_TAG)
                    .build()
                wm.enqueueUniqueWork("grahanam_before_${grahanam.id}", ExistingWorkPolicy.REPLACE, request)
            }

            // Day-of: 60 minutes before Sparsha
            val oneHourBefore = sparthaMs - 60 * 60 * 1000L
            if (oneHourBefore > now) {
                val body = "స్పర్శ: $sparthaFormatted · మధ్యం: $madhyamFormatted · మోక్షం: $mokshamFormatted"
                val inputData = workDataOf(
                    NotificationWorker.KEY_NOTIFICATION_BODY to body,
                    NotificationWorker.KEY_NOTIFICATION_ID to GRAHANAM_NOTIFICATION_ID_ON,
                    NotificationWorker.KEY_NOTIFICATION_TYPE to NotificationWorker.TYPE_GRAHANAM_ON,
                    NotificationWorker.KEY_SPARSHA to sparthaFormatted,
                    NotificationWorker.KEY_MADHYAM to madhyamFormatted,
                    NotificationWorker.KEY_MOKSHAM to mokshamFormatted,
                )
                val request = OneTimeWorkRequestBuilder<NotificationWorker>()
                    .setInitialDelay(oneHourBefore - now, TimeUnit.MILLISECONDS)
                    .setInputData(inputData)
                    .addTag(GRAHANAM_TAG)
                    .build()
                wm.enqueueUniqueWork("grahanam_on_${grahanam.id}", ExistingWorkPolicy.REPLACE, request)
            }
        }
    }

    const val FESTIVAL_TAG = "festival_greeting"

    fun scheduleFestivalGreetings(context: Context, festivals: List<FestivalNotificationInfo>, timezoneId: String, userName: String) {
        cancelFestivalGreetings(context)
        val wm = WorkManager.getInstance(context)
        val tz = if (timezoneId.isNotBlank()) TimeZone.getTimeZone(timezoneId) else TimeZone.getDefault()
        val now = System.currentTimeMillis()

        for (festival in festivals) {
            try {
                val parts = festival.dateString.split("-")
                val year = parts[0].toInt(); val month = parts[1].toInt(); val day = parts[2].toInt()

                val festivalAt7am = Calendar.getInstance(tz).apply {
                    set(Calendar.YEAR, year)
                    set(Calendar.MONTH, month - 1)
                    set(Calendar.DAY_OF_MONTH, day)
                    set(Calendar.HOUR_OF_DAY, 7)
                    set(Calendar.MINUTE, 0)
                    set(Calendar.SECOND, 0)
                    set(Calendar.MILLISECOND, 0)
                }
                if (festivalAt7am.timeInMillis <= now) continue

                val nameDisplay = if (userName.isNotBlank()) "$userName గారు" else ""
                val greeting = if (nameDisplay.isNotBlank())
                    "శుభ ${festival.nameTelugu}, $nameDisplay! / Happy ${festival.name}!"
                else
                    "శుభ ${festival.nameTelugu}! / Happy ${festival.name}!"

                val inputData = workDataOf(
                    NotificationWorker.KEY_NOTIFICATION_BODY to greeting,
                    NotificationWorker.KEY_NOTIFICATION_ID to (3000 + festival.id.hashCode() % 1000),
                    NotificationWorker.KEY_NOTIFICATION_TYPE to NotificationWorker.TYPE_FESTIVAL,
                )
                val request = OneTimeWorkRequestBuilder<NotificationWorker>()
                    .setInitialDelay(festivalAt7am.timeInMillis - now, TimeUnit.MILLISECONDS)
                    .setInputData(inputData)
                    .addTag(FESTIVAL_TAG)
                    .build()
                wm.enqueueUniqueWork("festival_${festival.id}", ExistingWorkPolicy.REPLACE, request)
            } catch (_: Exception) { /* skip malformed dates */ }
        }
    }

    fun cancelFestivalGreetings(context: Context) {
        WorkManager.getInstance(context).cancelAllWorkByTag(FESTIVAL_TAG)
    }

    fun cancelGrahanamNotifications(context: Context) {
        WorkManager.getInstance(context).cancelAllWorkByTag(GRAHANAM_TAG)
    }

    private fun formatInstantLocal(epochMs: Long, tz: TimeZone): String {
        val cal = Calendar.getInstance(tz)
        cal.timeInMillis = epochMs
        val hour = cal.get(Calendar.HOUR_OF_DAY)
        val minute = cal.get(Calendar.MINUTE)
        val period = if (hour >= 12) "PM" else "AM"
        val displayHour = when {
            hour == 0 -> 12
            hour > 12 -> hour - 12
            else -> hour
        }
        return "$displayHour:${minute.toString().padStart(2, '0')} $period"
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
