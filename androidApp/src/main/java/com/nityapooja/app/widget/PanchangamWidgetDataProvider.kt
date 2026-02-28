package com.nityapooja.app.widget

import android.content.Context
import com.nityapooja.shared.data.preferences.UserPreferencesManager
import com.nityapooja.shared.ui.panchangam.PanchangamViewModel
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.runBlocking
import org.koin.java.KoinJavaComponent.getKoin

data class WidgetPanchangamData(
    val dateDisplay: String = "",
    val tithi: String = "—",
    val nakshatra: String = "—",
    val yoga: String = "—",
    val karana: String = "—",
    val sunrise: String = "—",
    val sunset: String = "—",
)

object PanchangamWidgetDataProvider {

    fun getData(context: Context): WidgetPanchangamData {
        return try {
            val prefs = getKoin().get<UserPreferencesManager>()
            val lat: Double
            val lng: Double
            val timezone: String

            runBlocking {
                lat = prefs.locationLat.first()
                lng = prefs.locationLng.first()
                timezone = prefs.locationTimezone.first()
            }

            // PanchangamViewModel.calculatePanchangam is a pure computation — safe to call directly
            val vm = PanchangamViewModel(prefs)
            val data = vm.calculatePanchangam(lat, lng, timezone)

            WidgetPanchangamData(
                dateDisplay = data.dateDisplay,
                tithi = "${data.tithi.nameTelugu} • ${data.tithi.pakshaTelugu}",
                nakshatra = data.nakshatra.nameTelugu,
                yoga = data.yoga.nameTelugu,
                karana = data.karana.firstNameTelugu,
                sunrise = data.sunTimes.sunrise,
                sunset = data.sunTimes.sunset,
            )
        } catch (e: Exception) {
            WidgetPanchangamData()
        }
    }
}
