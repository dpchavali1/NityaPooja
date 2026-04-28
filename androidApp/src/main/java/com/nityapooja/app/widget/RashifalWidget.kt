package com.nityapooja.app.widget

import android.content.Context
import android.content.Intent
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.glance.*
import androidx.glance.action.clickable
import androidx.glance.appwidget.GlanceAppWidget
import androidx.glance.appwidget.action.actionStartActivity
import androidx.glance.appwidget.provideContent
import androidx.glance.layout.*
import androidx.glance.text.*
import androidx.glance.unit.ColorProvider
import com.nityapooja.app.MainActivity
import com.nityapooja.shared.data.preferences.UserPreferencesManager
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.runBlocking
import org.koin.java.KoinJavaComponent.getKoin

class RashifalWidget : GlanceAppWidget() {

    override suspend fun provideGlance(context: Context, id: GlanceId) {
        val data = RashifalWidgetDataProvider.getData(context)
        val showEnglish = runBlocking {
            getKoin().get<UserPreferencesManager>().showEnglish.first()
        }
        val openAppIntent = Intent(context, MainActivity::class.java).apply {
            flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TOP
            putExtra("nav_route", "rashifal")
        }
        provideContent {
            WidgetContent(data, showEnglish, openAppIntent)
        }
    }

    @Composable
    private fun WidgetContent(
        data: WidgetRashifalData,
        showEnglish: Boolean,
        openAppIntent: Intent,
    ) {
        val goldColor = ColorProvider(Color(0xFFD4A017))
        val whiteColor = ColorProvider(Color.White)
        val bgColor = ColorProvider(Color(0xFF0A0014))
        val dimColor = ColorProvider(Color(0xFFCCCCCC))
        val warnColor = ColorProvider(Color(0xFFFF6B6B))

        Box(
            modifier = GlanceModifier
                .fillMaxSize()
                .background(bgColor)
                .clickable(actionStartActivity(openAppIntent))
                .padding(horizontal = 14.dp, vertical = 10.dp),
        ) {
            if (data.notConfigured) {
                // Prompt user to configure
                Column(
                    modifier = GlanceModifier.fillMaxSize(),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalAlignment = Alignment.CenterHorizontally,
                ) {
                    Text(
                        "రాశిఫలం",
                        style = TextStyle(color = goldColor, fontSize = 18.sp, fontWeight = FontWeight.Bold),
                    )
                    Spacer(GlanceModifier.height(8.dp))
                    Text(
                        "Settings లో మీ రాశి ఎంచుకోండి",
                        style = TextStyle(color = dimColor, fontSize = 14.sp),
                    )
                    Spacer(GlanceModifier.height(4.dp))
                    Text(
                        "Select your rashi in Settings",
                        style = TextStyle(color = dimColor, fontSize = 12.sp),
                    )
                }
            } else {
                Column(
                    modifier = GlanceModifier.fillMaxSize(),
                    verticalAlignment = Alignment.Top,
                ) {
                    // Header: symbol + rashi name
                    Row(
                        modifier = GlanceModifier.fillMaxWidth(),
                        horizontalAlignment = Alignment.CenterHorizontally,
                        verticalAlignment = Alignment.CenterVertically,
                    ) {
                        Text(
                            "${data.symbol} ${if (showEnglish) data.rashiName else data.rashiNameTelugu}",
                            style = TextStyle(
                                color = goldColor,
                                fontSize = 20.sp,
                                fontWeight = FontWeight.Bold,
                            ),
                        )
                    }

                    Spacer(GlanceModifier.height(2.dp))

                    // Subtitle in the other language
                    Row(
                        modifier = GlanceModifier.fillMaxWidth(),
                        horizontalAlignment = Alignment.CenterHorizontally,
                    ) {
                        Text(
                            if (showEnglish) data.rashiNameTelugu else data.rashiName,
                            style = TextStyle(color = dimColor, fontSize = 13.sp),
                        )
                    }

                    Spacer(GlanceModifier.height(10.dp))

                    // Prediction text
                    val prediction = if (showEnglish) data.predictionEnglish else data.predictionTelugu
                    // Truncate to ~150 chars for widget display
                    val truncated = if (prediction.length > 150) prediction.take(147) + "…" else prediction

                    Text(
                        truncated,
                        style = TextStyle(
                            color = if (data.isChandrashtama) warnColor else whiteColor,
                            fontSize = 15.sp,
                            fontWeight = if (data.isChandrashtama) FontWeight.Medium else FontWeight.Normal,
                        ),
                    )

                    Spacer(GlanceModifier.height(8.dp))

                    // Footer
                    Row(
                        modifier = GlanceModifier.fillMaxWidth(),
                        horizontalAlignment = Alignment.CenterHorizontally,
                    ) {
                        Text(
                            "నిత్య పూజ · Tap for full rashifal",
                            style = TextStyle(color = dimColor, fontSize = 11.sp),
                        )
                    }
                }
            }
        }
    }
}
