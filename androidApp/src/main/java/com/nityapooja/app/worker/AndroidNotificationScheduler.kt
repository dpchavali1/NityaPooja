package com.nityapooja.app.worker

import android.content.Context
import com.nityapooja.shared.data.grahanam.GrahanamData
import com.nityapooja.shared.platform.FestivalNotificationInfo
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

    override fun scheduleFestivalGreetings(festivals: List<FestivalNotificationInfo>, timezoneId: String, userName: String) {
        NotificationScheduler.scheduleFestivalGreetings(context, festivals, timezoneId, userName)
    }

    override fun cancelFestivalGreetings() {
        NotificationScheduler.cancelFestivalGreetings(context)
    }

    override fun scheduleVrataReminder(vrataName: String, vrataNameTelugu: String, hour: Int, minute: Int, timezoneId: String) {
        NotificationScheduler.scheduleVrataReminder(context, vrataName, vrataNameTelugu, hour, minute, timezoneId)
    }

    override fun cancelVrataReminders() {
        NotificationScheduler.cancelVrataReminders(context)
    }

    override fun scheduleSacredMonthReminder(masaNameTelugu: String, hour: Int, minute: Int, timezoneId: String) {
        NotificationScheduler.scheduleSacredMonthReminder(context, masaNameTelugu, hour, minute, timezoneId)
    }

    override fun cancelSacredMonthReminders() {
        NotificationScheduler.cancelSacredMonthReminders(context)
    }

    override fun scheduleShlokaReminder(hour: Int, minute: Int, timezoneId: String) {
        NotificationScheduler.scheduleShlokaReminder(context, hour, minute, timezoneId)
    }

    override fun cancelShlokaReminder() {
        NotificationScheduler.cancelShlokaReminder(context)
    }

    override fun scheduleRahuKalamAlerts(lat: Double, lng: Double, timezoneId: String) {
        NotificationScheduler.scheduleRahuKalamAlerts(context, timezoneId)
    }

    override fun cancelRahuKalamAlerts() {
        NotificationScheduler.cancelRahuKalamAlerts(context)
    }

    override fun schedulePlanetTransitAlert(grahaNameTelugu: String, fromRashiTelugu: String, toRashiTelugu: String, epochMillis: Long, timezoneId: String) {
        NotificationScheduler.schedulePlanetTransitAlert(context, grahaNameTelugu, fromRashiTelugu, toRashiTelugu, epochMillis, timezoneId)
    }

    override fun cancelPlanetTransitAlerts() {
        NotificationScheduler.cancelPlanetTransitAlerts(context)
    }

    override fun cancelAll() {
        cancelMorningReminder()
        cancelEveningReminder()
        cancelPanchangReminder()
        cancelQuizReminder()
        cancelGrahanamNotifications()
        cancelFestivalGreetings()
        cancelVrataReminders()
        cancelSacredMonthReminders()
        cancelShlokaReminder()
        cancelRahuKalamAlerts()
        cancelPlanetTransitAlerts()
    }
}
