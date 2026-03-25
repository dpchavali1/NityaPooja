package com.nityapooja.shared.platform

import com.nityapooja.shared.data.grahanam.GrahanamData

data class FestivalNotificationInfo(
    val id: String,
    val name: String,
    val nameTelugu: String,
    val dateString: String, // "yyyy-MM-dd"
)

interface NotificationScheduler {
    fun scheduleMorningReminder(hour: Int, minute: Int, timezoneId: String = "")
    fun scheduleEveningReminder(hour: Int, minute: Int, timezoneId: String = "")
    fun schedulePanchangReminder(timezoneId: String = "")
    fun scheduleQuizReminder(hour: Int, minute: Int, timezoneId: String = "")
    fun scheduleGrahanamNotifications(grahanamList: List<GrahanamData>, timezoneId: String)
    fun scheduleFestivalGreetings(festivals: List<FestivalNotificationInfo>, timezoneId: String, userName: String)
    fun cancelMorningReminder()
    fun cancelEveningReminder()
    fun cancelPanchangReminder()
    fun cancelQuizReminder()
    fun cancelGrahanamNotifications()
    fun cancelFestivalGreetings()
    fun scheduleVrataReminder(vrataName: String, vrataNameTelugu: String, hour: Int, minute: Int, timezoneId: String)
    fun cancelVrataReminders()
    fun scheduleSacredMonthReminder(masaNameTelugu: String, hour: Int, minute: Int, timezoneId: String)
    fun cancelSacredMonthReminders()
    fun cancelAll()
}
