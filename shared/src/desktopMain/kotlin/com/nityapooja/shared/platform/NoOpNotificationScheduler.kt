package com.nityapooja.shared.platform

/** No-op — Desktop notifications are not supported yet. */
class NoOpNotificationScheduler : NotificationScheduler {
    override fun scheduleMorningReminder(hour: Int, minute: Int, timezoneId: String) {}
    override fun scheduleEveningReminder(hour: Int, minute: Int, timezoneId: String) {}
    override fun schedulePanchangReminder(timezoneId: String) {}
    override fun cancelMorningReminder() {}
    override fun cancelEveningReminder() {}
    override fun cancelPanchangReminder() {}
    override fun cancelAll() {}
}
