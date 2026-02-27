package com.nityapooja.shared.platform

interface NotificationScheduler {
    fun scheduleMorningReminder(hour: Int, minute: Int, timezoneId: String = "")
    fun scheduleEveningReminder(hour: Int, minute: Int, timezoneId: String = "")
    fun schedulePanchangReminder(timezoneId: String = "")
    fun cancelMorningReminder()
    fun cancelEveningReminder()
    fun cancelPanchangReminder()
    fun cancelAll()
}
