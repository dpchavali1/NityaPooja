package com.nityapooja.app.worker

import android.content.Context
import android.content.Intent
import com.nityapooja.shared.data.grahanam.GrahanamData
import com.nityapooja.shared.data.grahanam.GrahanamRepository
import com.nityapooja.shared.data.grahanam.GrahanamType
import com.nityapooja.shared.platform.FestivalNotificationInfo
import java.util.Calendar
import java.util.TimeZone

object NotificationScheduler {

    private const val MORNING_WORK_NAME = "morning_reminder"
    private const val EVENING_WORK_NAME = "evening_reminder"
    private const val PANCHANG_WORK_NAME = "panchang_reminder"
    private const val QUIZ_WORK_NAME = "quiz_reminder"
    private const val VRATA_WORK_NAME = "vrata_reminder"
    private const val SACRED_MONTH_WORK_NAME = "sacred_month_reminder"
    private const val SHLOKA_WORK_NAME = "shloka_reminder"
    private const val RAHU_KALAM_SCHEDULER_WORK_NAME = "rahu_kalam_scheduler"

    private const val MORNING_NOTIFICATION_ID = 1001
    private const val EVENING_NOTIFICATION_ID = 1002
    private const val PANCHANG_NOTIFICATION_ID = 1003
    private const val QUIZ_NOTIFICATION_ID = 1004
    private const val SHLOKA_NOTIFICATION_ID = 1005
    private const val GRAHANAM_NOTIFICATION_ID_BEFORE = 2001
    private const val GRAHANAM_NOTIFICATION_ID_ON = 2002

    // SharedPreferences key for tracking scheduled festival/transit work names
    private const val PREFS_NAME = "notification_alarms"
    private const val KEY_FESTIVAL_WORK_NAMES = "festival_work_names"
    private const val KEY_TRANSIT_WORK_NAMES = "transit_work_names"

    // ── Core recurring alarms — use setAlarmClock for maximum reliability ────────────────────────

    fun scheduleMorningReminder(context: Context, hour: Int, minute: Int, timezoneId: String = "") {
        scheduleAlarmClockRecurring(
            context, MORNING_WORK_NAME, hour, minute, timezoneId,
            "Time for morning prayers", MORNING_NOTIFICATION_ID, NotificationWorker.TYPE_MORNING,
        )
    }

    fun scheduleEveningReminder(context: Context, hour: Int, minute: Int, timezoneId: String = "") {
        scheduleAlarmClockRecurring(
            context, EVENING_WORK_NAME, hour, minute, timezoneId,
            "Time for evening aarti", EVENING_NOTIFICATION_ID, NotificationWorker.TYPE_EVENING,
        )
    }

    fun schedulePanchangReminder(context: Context, timezoneId: String = "") {
        scheduleAlarmClockRecurring(
            context, PANCHANG_WORK_NAME, 6, 0, timezoneId,
            "Check today's Panchangam for auspicious timings",
            PANCHANG_NOTIFICATION_ID, "panchang",
        )
    }

    // ── Lower-priority recurring alarms — setExactAndAllowWhileIdle is sufficient ──────────────

    fun scheduleQuizReminder(context: Context, hour: Int, minute: Int, timezoneId: String = "") {
        scheduleExactRecurring(
            context, QUIZ_WORK_NAME, hour, minute, timezoneId,
            "పురాణాల క్విజ్ సమయం / Time for your Puranas Quiz!",
            QUIZ_NOTIFICATION_ID, NotificationWorker.TYPE_QUIZ,
        )
    }

    fun scheduleVrataReminder(
        context: Context, vrataName: String, vrataNameTelugu: String,
        hour: Int, minute: Int, timezoneId: String,
    ) {
        scheduleExactRecurring(
            context, VRATA_WORK_NAME, hour, minute, timezoneId,
            "$vrataNameTelugu / $vrataName - Upcoming vratam", 3001, "vrata",
        )
    }

    fun scheduleSacredMonthReminder(
        context: Context, masaNameTelugu: String, hour: Int, minute: Int, timezoneId: String,
    ) {
        scheduleExactRecurring(
            context, SACRED_MONTH_WORK_NAME, hour, minute, timezoneId,
            "$masaNameTelugu - దైనందిన ఆచరణ సమయం / Daily sacred practice time",
            3002, "sacred_month",
        )
    }

    fun scheduleShlokaReminder(context: Context, hour: Int, minute: Int, timezoneId: String = "") {
        scheduleExactRecurring(
            context, SHLOKA_WORK_NAME, hour, minute, timezoneId,
            "రోజువారీ శ్లోకం / Daily Shloka",
            SHLOKA_NOTIFICATION_ID, NotificationWorker.TYPE_SHLOKA,
        )
    }

    /** Schedules a daily 5:30 AM alarm that computes Rahu Kalam/Yamagandam/Gulika times
     *  and fires one-shot 10-minute-before alerts for each inauspicious period. */
    fun scheduleRahuKalamAlerts(context: Context, timezoneId: String = "") {
        scheduleExactRecurring(
            context, RAHU_KALAM_SCHEDULER_WORK_NAME, 5, 30, timezoneId,
            "రాహు కాలం హెచ్చరికలు / Rahu Kalam Alerts",
            4000, NotificationWorker.TYPE_RAHU_KALAM_SCHEDULER,
        )
    }

    fun schedulePlanetTransitAlert(
        context: Context,
        grahaNameTelugu: String,
        fromRashiTelugu: String,
        toRashiTelugu: String,
        epochMillis: Long,
        timezoneId: String,
    ) {
        val tz = tzFor(timezoneId)
        val now = System.currentTimeMillis()
        // Alert 3 days before the transit
        val alertMs = epochMillis - 3L * 24 * 60 * 60 * 1000
        if (alertMs <= now) return

        val transitDateFmt = fmtLocalDate(epochMillis, tz)
        val body = "$grahaNameTelugu $fromRashiTelugu నుండి $toRashiTelugu లోకి $transitDateFmt న / " +
            "$grahaNameTelugu transits from $fromRashiTelugu to $toRashiTelugu on $transitDateFmt"
        val workName = "transit_${grahaNameTelugu}_${epochMillis}"
        val notifId = 5000 + (workName.hashCode() and 0x7FFFFFFF) % 1000
        val intent = buildAlarmIntent(context).apply {
            putExtra(NotificationWorker.KEY_NOTIFICATION_BODY, body)
            putExtra(NotificationWorker.KEY_NOTIFICATION_ID, notifId)
            putExtra(NotificationWorker.KEY_NOTIFICATION_TYPE, NotificationWorker.TYPE_PLANET_TRANSIT)
            putExtra(NotificationWorker.KEY_GRAHA_NAME_TELUGU, grahaNameTelugu)
            putExtra(NotificationWorker.KEY_FROM_RASHI_TELUGU, fromRashiTelugu)
            putExtra(NotificationWorker.KEY_TO_RASHI_TELUGU, toRashiTelugu)
            putExtra(NotificationWorker.KEY_WORK_NAME, workName)
        }
        NotificationAlarmScheduler.scheduleAlarmClock(context, workName, alertMs, intent)

        // Track work name so cancelPlanetTransitAlerts() can cancel it
        val names = loadTransitWorkNames(context).toMutableList()
        names += workName
        saveTransitWorkNames(context, names)
    }

    fun cancelPlanetTransitAlerts(context: Context) {
        loadTransitWorkNames(context).forEach { NotificationAlarmScheduler.cancel(context, it) }
        saveTransitWorkNames(context, emptyList())
    }

    // ── Cancellations ────────────────────────────────────────────────────────────────────────────

    fun cancelMorningReminder(context: Context) = NotificationAlarmScheduler.cancel(context, MORNING_WORK_NAME)
    fun cancelEveningReminder(context: Context) = NotificationAlarmScheduler.cancel(context, EVENING_WORK_NAME)
    fun cancelPanchangReminder(context: Context) = NotificationAlarmScheduler.cancel(context, PANCHANG_WORK_NAME)
    fun cancelQuizReminder(context: Context) = NotificationAlarmScheduler.cancel(context, QUIZ_WORK_NAME)
    fun cancelVrataReminders(context: Context) = NotificationAlarmScheduler.cancel(context, VRATA_WORK_NAME)
    fun cancelSacredMonthReminders(context: Context) = NotificationAlarmScheduler.cancel(context, SACRED_MONTH_WORK_NAME)
    fun cancelShlokaReminder(context: Context) = NotificationAlarmScheduler.cancel(context, SHLOKA_WORK_NAME)
    fun cancelRahuKalamAlerts(context: Context) {
        NotificationAlarmScheduler.cancel(context, RAHU_KALAM_SCHEDULER_WORK_NAME)
        // Also cancel any already-scheduled intraday alarms
        NotificationAlarmScheduler.cancel(context, "rahu_kalam_today")
        NotificationAlarmScheduler.cancel(context, "yamagandam_today")
        NotificationAlarmScheduler.cancel(context, "gulika_today")
    }

    // ── Grahanam — setAlarmClock (once-per-eclipse, highest reliability) ─────────────────────────

    fun scheduleGrahanamNotifications(
        context: Context, grahanamList: List<GrahanamData>, timezoneId: String,
    ) {
        val tz = tzFor(timezoneId)
        val now = System.currentTimeMillis()

        for (grahanam in grahanamList) {
            val sparthaMs = grahanam.sparthaUtc.toEpochMilliseconds()
            val mokshamMs = grahanam.mokshamUtc.toEpochMilliseconds()
            if (mokshamMs <= now) continue

            val sparthaFmt = fmtLocal(sparthaMs, tz)
            val madhyamFmt = fmtLocal(grahanam.madhyamUtc.toEpochMilliseconds(), tz)
            val mokshamFmt = fmtLocal(mokshamMs, tz)
            val typeLabel = if (grahanam.type == GrahanamType.SURYA) "Surya Grahanam" else "Chandra Grahanam"

            // Day-before: 8 AM local time the day before Sparsha
            val dayBefore = Calendar.getInstance(tz).apply {
                timeInMillis = sparthaMs
                add(Calendar.DAY_OF_YEAR, -1)
                set(Calendar.HOUR_OF_DAY, 8); set(Calendar.MINUTE, 0)
                set(Calendar.SECOND, 0); set(Calendar.MILLISECOND, 0)
            }
            if (dayBefore.timeInMillis > now) {
                val body = "రేపు ${if (grahanam.type == GrahanamType.SURYA) "సూర్య" else "చంద్ర"} గ్రహణం · $typeLabel · స్పర్శ: $sparthaFmt"
                val workName = "grahanam_before_${grahanam.id}"
                val intent = buildAlarmIntent(context).applyGrahanamExtras(
                    body, GRAHANAM_NOTIFICATION_ID_BEFORE, NotificationWorker.TYPE_GRAHANAM_BEFORE,
                    sparthaFmt, madhyamFmt, mokshamFmt, workName,
                )
                NotificationAlarmScheduler.scheduleAlarmClock(context, workName, dayBefore.timeInMillis, intent)
            }

            // Day-of: 60 minutes before Sparsha
            val oneHourBefore = sparthaMs - 60 * 60 * 1000L
            if (oneHourBefore > now) {
                val body = "స్పర్శ: $sparthaFmt · మధ్యం: $madhyamFmt · మోక్షం: $mokshamFmt"
                val workName = "grahanam_on_${grahanam.id}"
                val intent = buildAlarmIntent(context).applyGrahanamExtras(
                    body, GRAHANAM_NOTIFICATION_ID_ON, NotificationWorker.TYPE_GRAHANAM_ON,
                    sparthaFmt, madhyamFmt, mokshamFmt, workName,
                )
                NotificationAlarmScheduler.scheduleAlarmClock(context, workName, oneHourBefore, intent)
            }
        }
    }

    /** Cancel all grahanam alarms — uses actual IDs from GrahanamRepository. */
    fun cancelGrahanamNotifications(context: Context) {
        for (id in GrahanamRepository.getAllIds()) {
            NotificationAlarmScheduler.cancel(context, "grahanam_before_$id")
            NotificationAlarmScheduler.cancel(context, "grahanam_on_$id")
        }
    }

    // ── Festival greetings — setAlarmClock, work names tracked in SharedPreferences ─────────────

    fun scheduleFestivalGreetings(
        context: Context,
        festivals: List<FestivalNotificationInfo>,
        timezoneId: String,
        userName: String,
    ) {
        // Cancel previously scheduled festivals before rescheduling
        cancelFestivalGreetings(context)

        val tz = tzFor(timezoneId)
        val now = System.currentTimeMillis()
        val scheduledWorkNames = mutableListOf<String>()

        for (festival in festivals) {
            try {
                val parts = festival.dateString.split("-")
                val year = parts[0].toInt(); val month = parts[1].toInt(); val day = parts[2].toInt()

                val at7am = Calendar.getInstance(tz).apply {
                    set(Calendar.YEAR, year); set(Calendar.MONTH, month - 1)
                    set(Calendar.DAY_OF_MONTH, day)
                    set(Calendar.HOUR_OF_DAY, 7); set(Calendar.MINUTE, 0)
                    set(Calendar.SECOND, 0); set(Calendar.MILLISECOND, 0)
                }
                if (at7am.timeInMillis <= now) continue

                val greeting = if (userName.isNotBlank()) {
                    "మీకు మీ కుటుంబానికి ${festival.nameTelugu} శుభాకాంక్షలు $userName గారు / Happy ${festival.name} to you and your family $userName"
                } else {
                    "మీకు మీ కుటుంబానికి ${festival.nameTelugu} శుభాకాంక్షలు / Happy ${festival.name} to you and your family"
                }

                val workName = "festival_${festival.id}"
                val notifId = 3000 + (festival.id.hashCode() and 0x7FFFFFFF) % 1000
                val intent = buildAlarmIntent(context).apply {
                    putExtra(NotificationWorker.KEY_NOTIFICATION_BODY, greeting)
                    putExtra(NotificationWorker.KEY_NOTIFICATION_ID, notifId)
                    putExtra(NotificationWorker.KEY_NOTIFICATION_TYPE, NotificationWorker.TYPE_FESTIVAL)
                    putExtra(NotificationWorker.KEY_WORK_NAME, workName)
                }
                NotificationAlarmScheduler.scheduleAlarmClock(context, workName, at7am.timeInMillis, intent)
                scheduledWorkNames += workName
            } catch (_: Exception) { /* skip malformed dates */ }
        }

        // Persist work names so cancelFestivalGreetings() can cancel them
        saveFestivalWorkNames(context, scheduledWorkNames)
    }

    fun cancelFestivalGreetings(context: Context) {
        loadFestivalWorkNames(context).forEach { NotificationAlarmScheduler.cancel(context, it) }
        saveFestivalWorkNames(context, emptyList())
    }

    // ── AlarmReceiver callback: reschedule recurring alarm for tomorrow ───────────────────────────

    /**
     * Called from [AlarmReceiver] after a recurring alarm fires.
     * Reschedules for the next occurrence using the same method (alarmClock vs exact)
     * that was used when originally scheduling.
     */
    fun rescheduleRecurring(context: Context, workName: String, hour: Int, minute: Int, timezoneId: String, intent: Intent) {
        val triggerAtMs = System.currentTimeMillis() + calculateDelayMillis(hour, minute, timezoneId)
        val isAlarmClockType = workName in setOf(MORNING_WORK_NAME, EVENING_WORK_NAME, PANCHANG_WORK_NAME)
        // Note: SHLOKA_WORK_NAME and RAHU_KALAM_SCHEDULER_WORK_NAME use scheduleAt (not alarmClock)
        if (isAlarmClockType) {
            NotificationAlarmScheduler.scheduleAlarmClock(context, workName, triggerAtMs, intent)
        } else {
            NotificationAlarmScheduler.scheduleAt(context, workName, triggerAtMs, intent)
        }
    }

    // ── Helpers ──────────────────────────────────────────────────────────────────────────────────

    private fun scheduleAlarmClockRecurring(
        context: Context, workName: String, hour: Int, minute: Int, timezoneId: String,
        body: String, notifId: Int, type: String,
    ) {
        val triggerAtMs = System.currentTimeMillis() + calculateDelayMillis(hour, minute, timezoneId)
        val intent = buildAlarmIntent(context).applyRecurringExtras(body, notifId, type, hour, minute, timezoneId, workName)
        NotificationAlarmScheduler.scheduleAlarmClock(context, workName, triggerAtMs, intent)
    }

    private fun scheduleExactRecurring(
        context: Context, workName: String, hour: Int, minute: Int, timezoneId: String,
        body: String, notifId: Int, type: String,
    ) {
        val triggerAtMs = System.currentTimeMillis() + calculateDelayMillis(hour, minute, timezoneId)
        val intent = buildAlarmIntent(context).applyRecurringExtras(body, notifId, type, hour, minute, timezoneId, workName)
        NotificationAlarmScheduler.scheduleAt(context, workName, triggerAtMs, intent)
    }

    private fun buildAlarmIntent(context: Context) = Intent(context, AlarmReceiver::class.java)

    private fun Intent.applyRecurringExtras(
        body: String, notifId: Int, type: String,
        hour: Int, minute: Int, timezoneId: String, workName: String,
    ) = apply {
        putExtra(NotificationWorker.KEY_NOTIFICATION_BODY, body)
        putExtra(NotificationWorker.KEY_NOTIFICATION_ID, notifId)
        putExtra(NotificationWorker.KEY_NOTIFICATION_TYPE, type)
        putExtra(NotificationWorker.KEY_HOUR, hour)
        putExtra(NotificationWorker.KEY_MINUTE, minute)
        putExtra(NotificationWorker.KEY_TIMEZONE, timezoneId)
        putExtra(NotificationWorker.KEY_WORK_NAME, workName)
    }

    private fun Intent.applyGrahanamExtras(
        body: String, notifId: Int, type: String,
        sparsha: String, madhyam: String, moksham: String, workName: String,
    ) = apply {
        putExtra(NotificationWorker.KEY_NOTIFICATION_BODY, body)
        putExtra(NotificationWorker.KEY_NOTIFICATION_ID, notifId)
        putExtra(NotificationWorker.KEY_NOTIFICATION_TYPE, type)
        putExtra(NotificationWorker.KEY_SPARSHA, sparsha)
        putExtra(NotificationWorker.KEY_MADHYAM, madhyam)
        putExtra(NotificationWorker.KEY_MOKSHAM, moksham)
        putExtra(NotificationWorker.KEY_WORK_NAME, workName)
    }

    private fun saveFestivalWorkNames(context: Context, workNames: List<String>) {
        context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)
            .edit()
            .putString(KEY_FESTIVAL_WORK_NAMES, workNames.joinToString(","))
            .apply()
    }

    private fun loadFestivalWorkNames(context: Context): List<String> =
        context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)
            .getString(KEY_FESTIVAL_WORK_NAMES, "")
            ?.split(",")
            ?.filter { it.isNotBlank() }
            ?: emptyList()

    private fun saveTransitWorkNames(context: Context, workNames: List<String>) {
        context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)
            .edit()
            .putString(KEY_TRANSIT_WORK_NAMES, workNames.joinToString(","))
            .apply()
    }

    private fun loadTransitWorkNames(context: Context): List<String> =
        context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)
            .getString(KEY_TRANSIT_WORK_NAMES, "")
            ?.split(",")
            ?.filter { it.isNotBlank() }
            ?: emptyList()

    private fun tzFor(timezoneId: String) =
        if (timezoneId.isNotBlank()) TimeZone.getTimeZone(timezoneId) else TimeZone.getDefault()

    private fun fmtLocal(epochMs: Long, tz: TimeZone): String {
        val cal = Calendar.getInstance(tz).apply { timeInMillis = epochMs }
        val h = cal.get(Calendar.HOUR_OF_DAY); val m = cal.get(Calendar.MINUTE)
        val period = if (h >= 12) "PM" else "AM"
        val dh = when { h == 0 -> 12; h > 12 -> h - 12; else -> h }
        return "$dh:${m.toString().padStart(2, '0')} $period"
    }

    private fun fmtLocalDate(epochMs: Long, tz: TimeZone): String {
        val cal = Calendar.getInstance(tz).apply { timeInMillis = epochMs }
        val months = arrayOf("Jan","Feb","Mar","Apr","May","Jun","Jul","Aug","Sep","Oct","Nov","Dec")
        return "${cal.get(Calendar.DAY_OF_MONTH)} ${months[cal.get(Calendar.MONTH)]} ${cal.get(Calendar.YEAR)}"
    }

    /**
     * Calculates milliseconds until the next occurrence of [hour]:[minute] in [timezoneId].
     */
    internal fun calculateDelayMillis(hour: Int, minute: Int, timezoneId: String): Long {
        val tz = tzFor(timezoneId)
        val now = Calendar.getInstance(tz)
        val target = Calendar.getInstance(tz).apply {
            set(Calendar.HOUR_OF_DAY, hour); set(Calendar.MINUTE, minute)
            set(Calendar.SECOND, 0); set(Calendar.MILLISECOND, 0)
        }
        if (!target.after(now)) target.add(Calendar.DAY_OF_YEAR, 1)
        return target.timeInMillis - now.timeInMillis
    }
}
