package com.nityapooja.shared.platform

interface NotificationScheduler {
    fun scheduleMorningReminder(hour: Int, minute: Int)
    fun scheduleEveningReminder(hour: Int, minute: Int)
    fun schedulePanchangReminder()
    fun cancelMorningReminder()
    fun cancelEveningReminder()
    fun cancelPanchangReminder()
    fun cancelAll()
}
