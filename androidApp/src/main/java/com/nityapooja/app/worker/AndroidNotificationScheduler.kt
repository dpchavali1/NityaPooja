package com.nityapooja.app.worker

import android.content.Context
import com.nityapooja.shared.data.grahanam.GrahanamData
import com.nityapooja.shared.platform.NotificationScheduler as SharedNotificationScheduler

class AndroidNotificationScheduler(
    private val context: Context,
) : SharedNotificationScheduler {

    override fun scheduleMorningReminder(hour: Int, minute: Int, timezoneId: String) {
        NotificationScheduler.scheduleMorningReminder(context, hour, minute, timezoneId)
    }

    override fun scheduleEveningReminder(hour: Int, minute: Int, timezoneId: String) {
        NotificationScheduler.scheduleEveningReminder(context, hour, minute, timezoneId)
    }

    override fun schedulePanchangReminder(timezoneId: String) {
        NotificationScheduler.schedulePanchangReminder(context, timezoneId)
    }

    override fun cancelMorningReminder() {
        NotificationScheduler.cancelMorningReminder(context)
    }

    override fun cancelEveningReminder() {
        NotificationScheduler.cancelEveningReminder(context)
    }

    override fun cancelPanchangReminder() {
        NotificationScheduler.cancelPanchangReminder(context)
    }

    override fun scheduleQuizReminder(hour: Int, minute: Int, timezoneId: String) {
        NotificationScheduler.scheduleQuizReminder(context, hour, minute, timezoneId)
    }

    override fun cancelQuizReminder() {
        NotificationScheduler.cancelQuizReminder(context)
    }

    override fun scheduleGrahanamNotifications(grahanamList: List<GrahanamData>, timezoneId: String) {
        NotificationScheduler.scheduleGrahanamNotifications(context, grahanamList, timezoneId)
    }

    override fun cancelGrahanamNotifications() {
        NotificationScheduler.cancelGrahanamNotifications(context)
    }

    override fun cancelAll() {
        cancelMorningReminder()
        cancelEveningReminder()
        cancelPanchangReminder()
        cancelQuizReminder()
        cancelGrahanamNotifications()
    }
}
