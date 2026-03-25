package com.nityapooja.shared.platform

import com.nityapooja.shared.data.grahanam.GrahanamData
import com.nityapooja.shared.data.grahanam.GrahanamType
import com.nityapooja.shared.platform.FestivalNotificationInfo
import kotlinx.datetime.TimeZone
import kotlinx.datetime.toLocalDateTime
import platform.Foundation.NSDateComponents
import platform.UserNotifications.UNCalendarNotificationTrigger
import platform.UserNotifications.UNMutableNotificationContent
import platform.UserNotifications.UNNotificationRequest
import platform.UserNotifications.UNNotificationSound
import platform.UserNotifications.UNUserNotificationCenter

class IosNotificationScheduler : NotificationScheduler {

    private val center get() = UNUserNotificationCenter.currentNotificationCenter()

    override fun scheduleMorningReminder(hour: Int, minute: Int, timezoneId: String) {
        scheduleDaily(
            id = "morning_reminder",
            title = "NityaPooja - Morning Blessing",
            body = "సుప్రభాతం సమయం / Time for your morning prayers!",
            hour = hour,
            minute = minute,
            route = "home",
        )
    }

    override fun scheduleEveningReminder(hour: Int, minute: Int, timezoneId: String) {
        scheduleDaily(
            id = "evening_reminder",
            title = "NityaPooja - Evening Aarti",
            body = "సాయంకాలం హారతి సమయం / Time for evening aarti!",
            hour = hour,
            minute = minute,
            route = "home",
        )
    }

    override fun schedulePanchangReminder(timezoneId: String) {
        scheduleDaily(
            id = "panchang_reminder",
            title = "NityaPooja - Panchangam",
            body = "నేటి పంచాంగం చూడండి / Check today's Panchangam!",
            hour = 6,
            minute = 0,
            route = "panchangam",
        )
    }

    override fun scheduleQuizReminder(hour: Int, minute: Int, timezoneId: String) {
        scheduleDaily(
            id = "quiz_reminder",
            title = "NityaPooja - Puranas Quiz",
            body = "పురాణాల క్విజ్ సమయం / Time for your Puranas Quiz!",
            hour = hour,
            minute = minute,
            route = "purana_quiz",
        )
    }

    override fun scheduleGrahanamNotifications(grahanamList: List<GrahanamData>, timezoneId: String) {
        cancelGrahanamNotifications()
        val tz = try { TimeZone.of(timezoneId) } catch (_: Exception) { TimeZone.of("Asia/Kolkata") }

        grahanamList.forEach { g ->
            val sparthaLocal = g.sparthaUtc.toLocalDateTime(tz)
            val typeName = if (g.type == GrahanamType.SURYA) "సూర్య గ్రహణం / Solar Eclipse" else "చంద్ర గ్రహణం / Lunar Eclipse"

            // Day-before notification at 8 AM
            val dayBeforeComponents = NSDateComponents().apply {
                this.year = sparthaLocal.year.toLong()
                this.month = sparthaLocal.monthNumber.toLong()
                this.day = (sparthaLocal.dayOfMonth - 1).toLong()
                this.hour = 8
                this.minute = 0
            }
            scheduleOnce(
                id = "grahanam_before_${g.sparthaUtc.toEpochMilliseconds()}",
                title = "రేపు గ్రహణం / Eclipse Tomorrow",
                body = "$typeName - Prepare for tomorrow's eclipse",
                dateComponents = dayBeforeComponents,
                route = "panchangam",
            )

            // 1 hour before sparsha
            val hourBeforeComponents = NSDateComponents().apply {
                this.year = sparthaLocal.year.toLong()
                this.month = sparthaLocal.monthNumber.toLong()
                this.day = sparthaLocal.dayOfMonth.toLong()
                this.hour = (sparthaLocal.hour - 1).toLong()
                this.minute = sparthaLocal.minute.toLong()
            }
            scheduleOnce(
                id = "grahanam_on_${g.sparthaUtc.toEpochMilliseconds()}",
                title = "గ్రహణ కాలం / Grahanam Today",
                body = "$typeName starts in 1 hour",
                dateComponents = hourBeforeComponents,
                route = "panchangam",
            )
        }
    }

    override fun cancelMorningReminder() {
        center.removePendingNotificationRequestsWithIdentifiers(listOf("morning_reminder"))
    }

    override fun cancelEveningReminder() {
        center.removePendingNotificationRequestsWithIdentifiers(listOf("evening_reminder"))
    }

    override fun cancelPanchangReminder() {
        center.removePendingNotificationRequestsWithIdentifiers(listOf("panchang_reminder"))
    }

    override fun cancelQuizReminder() {
        center.removePendingNotificationRequestsWithIdentifiers(listOf("quiz_reminder"))
    }

    override fun cancelGrahanamNotifications() {
        center.getPendingNotificationRequestsWithCompletionHandler { requests ->
            val ids = (requests as? List<UNNotificationRequest>)
                ?.filter { it.identifier.startsWith("grahanam_") }
                ?.map { it.identifier }
                ?: emptyList()
            if (ids.isNotEmpty()) {
                center.removePendingNotificationRequestsWithIdentifiers(ids)
            }
        }
    }

    override fun scheduleFestivalGreetings(festivals: List<FestivalNotificationInfo>, timezoneId: String, userName: String) {
        cancelFestivalGreetings()
        for (festival in festivals) {
            try {
                val parts = festival.dateString.split("-")
                val year = parts[0].toInt(); val month = parts[1].toInt(); val day = parts[2].toInt()

                val nameDisplay = if (userName.isNotBlank()) "$userName గారు" else ""
                val greeting = if (nameDisplay.isNotBlank())
                    "శుభ ${festival.nameTelugu}, $nameDisplay! / Happy ${festival.name}!"
                else
                    "శుభ ${festival.nameTelugu}! / Happy ${festival.name}!"

                val dateComponents = NSDateComponents().apply {
                    this.year = year.toLong()
                    this.month = month.toLong()
                    this.day = day.toLong()
                    this.hour = 7
                    this.minute = 0
                }

                scheduleOnce(
                    id = "festival_${festival.id}",
                    title = "NityaPooja - పండుగ శుభాకాంక్షలు",
                    body = greeting,
                    dateComponents = dateComponents,
                    route = "home",
                )
            } catch (_: Exception) { /* skip malformed dates */ }
        }
    }

    override fun cancelFestivalGreetings() {
        center.getPendingNotificationRequestsWithCompletionHandler { requests ->
            val ids = (requests as? List<UNNotificationRequest>)
                ?.filter { it.identifier.startsWith("festival_") }
                ?.map { it.identifier }
                ?: emptyList()
            if (ids.isNotEmpty()) {
                center.removePendingNotificationRequestsWithIdentifiers(ids)
            }
        }
    }

    override fun scheduleVrataReminder(vrataName: String, vrataNameTelugu: String, hour: Int, minute: Int, timezoneId: String) {
        scheduleDaily(
            id = "vrata_reminder",
            title = "NityaPooja - వ్రత రిమైండర్",
            body = "$vrataNameTelugu / $vrataName - Upcoming vratam tomorrow",
            hour = hour,
            minute = minute,
            route = "vrata_list",
        )
    }

    override fun cancelVrataReminders() {
        center.removePendingNotificationRequestsWithIdentifiers(listOf("vrata_reminder"))
    }

    override fun scheduleSacredMonthReminder(masaNameTelugu: String, hour: Int, minute: Int, timezoneId: String) {
        scheduleDaily(
            id = "sacred_month_reminder",
            title = "NityaPooja - పవిత్ర మాసం",
            body = "$masaNameTelugu - దైనందిన ఆచరణ సమయం / Time for daily sacred month practice",
            hour = hour,
            minute = minute,
            route = "sacred_month",
        )
    }

    override fun cancelSacredMonthReminders() {
        center.removePendingNotificationRequestsWithIdentifiers(listOf("sacred_month_reminder"))
    }

    override fun cancelAll() {
        center.removeAllPendingNotificationRequests()
    }

    private fun scheduleDaily(
        id: String,
        title: String,
        body: String,
        hour: Int,
        minute: Int,
        route: String,
    ) {
        val content = UNMutableNotificationContent().apply {
            setTitle(title)
            setBody(body)
            setSound(UNNotificationSound.defaultSound())
            setUserInfo(mapOf("nav_route" to route))
        }

        val dateComponents = NSDateComponents().apply {
            this.hour = hour.toLong()
            this.minute = minute.toLong()
        }

        val trigger = UNCalendarNotificationTrigger.triggerWithDateMatchingComponents(
            dateComponents,
            repeats = true,
        )
        val request = UNNotificationRequest.requestWithIdentifier(id, content, trigger)
        center.addNotificationRequest(request, withCompletionHandler = null)
    }

    private fun scheduleOnce(
        id: String,
        title: String,
        body: String,
        dateComponents: NSDateComponents,
        route: String,
    ) {
        val content = UNMutableNotificationContent().apply {
            setTitle(title)
            setBody(body)
            setSound(UNNotificationSound.defaultSound())
            setUserInfo(mapOf("nav_route" to route))
        }

        val trigger = UNCalendarNotificationTrigger.triggerWithDateMatchingComponents(
            dateComponents,
            repeats = false,
        )
        val request = UNNotificationRequest.requestWithIdentifier(id, content, trigger)
        center.addNotificationRequest(request, withCompletionHandler = null)
    }
}
