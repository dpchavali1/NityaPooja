package com.nityapooja.shared.platform

import com.nityapooja.shared.data.grahanam.GrahanamData

interface NotificationScheduler {
    fun scheduleMorningReminder(hour: Int, minute: Int, timezoneId: String = "")
    fun scheduleEveningReminder(hour: Int, minute: Int, timezoneId: String = "")
    fun schedulePanchangReminder(timezoneId: String = "")
    fun scheduleQuizReminder(hour: Int, minute: Int, timezoneId: String = "")
    fun scheduleGrahanamNotifications(grahanamList: List<GrahanamData>, timezoneId: String)
    fun cancelMorningReminder()
    fun cancelEveningReminder()
    fun cancelPanchangReminder()
    fun cancelQuizReminder()
    fun cancelGrahanamNotifications()
    fun cancelAll()
}
