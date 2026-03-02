package com.nityapooja.shared.platform

import com.nityapooja.shared.data.grahanam.GrahanamData

/** No-op — Desktop notifications are not supported yet. */
class NoOpNotificationScheduler : NotificationScheduler {
    override fun scheduleMorningReminder(hour: Int, minute: Int, timezoneId: String) {}
    override fun scheduleEveningReminder(hour: Int, minute: Int, timezoneId: String) {}
    override fun schedulePanchangReminder(timezoneId: String) {}
    override fun scheduleQuizReminder(hour: Int, minute: Int, timezoneId: String) {}
    override fun scheduleGrahanamNotifications(grahanamList: List<GrahanamData>, timezoneId: String) {}
    override fun cancelMorningReminder() {}
    override fun cancelEveningReminder() {}
    override fun cancelPanchangReminder() {}
    override fun cancelQuizReminder() {}
    override fun cancelGrahanamNotifications() {}
    override fun cancelAll() {}
}
