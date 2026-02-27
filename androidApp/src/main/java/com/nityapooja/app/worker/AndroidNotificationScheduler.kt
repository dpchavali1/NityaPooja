package com.nityapooja.app.worker

import android.content.Context
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

    override fun cancelAll() {
        cancelMorningReminder()
        cancelEveningReminder()
        cancelPanchangReminder()
    }
}
