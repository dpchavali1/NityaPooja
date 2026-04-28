package com.nityapooja.app.widget

import android.content.Context
import com.nityapooja.shared.data.local.dao.RashiDao
import com.nityapooja.shared.data.preferences.UserPreferencesManager
import com.nityapooja.shared.ui.rashifal.RashifalViewModel
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.runBlocking
import org.koin.java.KoinJavaComponent.getKoin

data class WidgetRashifalData(
    val rashiName: String = "",
    val rashiNameTelugu: String = "",
    val symbol: String = "☿",
    val predictionTelugu: String = "",
    val predictionEnglish: String = "",
    val isChandrashtama: Boolean = false,
    val notConfigured: Boolean = false,
)

object RashifalWidgetDataProvider {

    fun getData(context: Context): WidgetRashifalData {
        return try {
            val prefs = getKoin().get<UserPreferencesManager>()
            val rashiDao = getKoin().get<RashiDao>()

            val selectedRashiId: Int
            val lat: Double
            val lng: Double
            val timezone: String
            val showEnglish: Boolean

            runBlocking {
                selectedRashiId = prefs.selectedRashiId.first()
                lat = prefs.locationLat.first()
                lng = prefs.locationLng.first()
                timezone = prefs.locationTimezone.first()
                showEnglish = prefs.showEnglish.first()
            }

            if (selectedRashiId == 0) {
                return WidgetRashifalData(notConfigured = true)
            }

            val rashi = runBlocking { rashiDao.getById(selectedRashiId).first() }
                ?: return WidgetRashifalData(notConfigured = true)

            val prediction = RashifalViewModel.computeGochaRa(rashi, lat, lng, timezone)

            WidgetRashifalData(
                rashiName = rashi.name,
                rashiNameTelugu = rashi.nameTelugu,
                symbol = rashi.symbol,
                predictionTelugu = prediction.textTelugu,
                predictionEnglish = prediction.textEnglish,
                isChandrashtama = prediction.isChandrashtama,
                notConfigured = false,
            )
        } catch (e: Exception) {
            WidgetRashifalData()
        }
    }
}
