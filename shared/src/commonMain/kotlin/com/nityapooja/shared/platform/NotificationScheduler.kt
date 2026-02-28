package com.nityapooja.shared.platform

interface NotificationScheduler {
    fun scheduleMorningReminder(hour: Int, minute: Int, timezoneId: String = "")
    fun scheduleEveningReminder(hour: Int, minute: Int, timezoneId: String = "")
    fun schedulePanchangReminder(timezoneId: String = "")
    fun scheduleQuizReminder(hour: Int, minute: Int, timezoneId: String = "")
    fun cancelMorningReminder()
    fun cancelEveningReminder()
    fun cancelPanchangReminder()
    fun cancelQuizReminder()
    fun cancelAll()
}
