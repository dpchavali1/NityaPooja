package com.nityapooja.app.worker

import android.content.Context
import com.nityapooja.shared.platform.NotificationScheduler as SharedNotificationScheduler

class AndroidNotificationScheduler(
    private val context: Context,
) : SharedNotificationScheduler {

    override fun scheduleMorningReminder(hour: Int, minute: Int) {
        NotificationScheduler.scheduleMorningReminder(context, hour, minute)
    }

    override fun scheduleEveningReminder(hour: Int, minute: Int) {
        NotificationScheduler.scheduleEveningReminder(context, hour, minute)
    }

    override fun schedulePanchangReminder() {
        NotificationScheduler.schedulePanchangReminder(context)
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
