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

class PanchangamWidget : GlanceAppWidget() {

    override suspend fun provideGlance(context: Context, id: GlanceId) {
        val data = PanchangamWidgetDataProvider.getData(context)
        val openAppIntent = Intent(context, MainActivity::class.java).apply {
            flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TOP
        }
        provideContent {
            WidgetContent(data, openAppIntent)
        }
    }

    @Composable
    private fun WidgetContent(data: WidgetPanchangamData, openAppIntent: Intent) {
        val goldColor = ColorProvider(Color(0xFFD4A017))
        val whiteColor = ColorProvider(Color.White)
        val bgColor = ColorProvider(Color(0xFF1A0A00))
        val dimColor = ColorProvider(Color(0xFFCCCCCC))

        Box(
            modifier = GlanceModifier
                .fillMaxSize()
                .background(bgColor)
                .clickable(actionStartActivity(openAppIntent))
                .padding(horizontal = 14.dp, vertical = 10.dp),
        ) {
            Column(
                modifier = GlanceModifier.fillMaxSize(),
                verticalAlignment = Alignment.CenterVertically,
            ) {
                // Date — large, centred, gold
                Row(
                    modifier = GlanceModifier.fillMaxWidth(),
                    horizontalAlignment = Alignment.CenterHorizontally,
                ) {
                    Text(
                        text = data.dateDisplay,
                        style = TextStyle(
                            color = goldColor,
                            fontSize = 20.sp,
                            fontWeight = FontWeight.Bold,
                        ),
                    )
                }

                Spacer(GlanceModifier.height(10.dp))

                // Row 1: Tithi | Karana
                Row(modifier = GlanceModifier.fillMaxWidth()) {
                    BigField(
                        label = "తిథి",
                        value = data.tithi,
                        labelColor = goldColor,
                        valueColor = whiteColor,
                        modifier = GlanceModifier.defaultWeight(),
                    )
                    BigField(
                        label = "కరణం",
                        value = data.karana,
                        labelColor = goldColor,
                        valueColor = whiteColor,
                        modifier = GlanceModifier.defaultWeight(),
                    )
                }

                Spacer(GlanceModifier.height(10.dp))

                // Row 2: Nakshatra | Yoga
                Row(modifier = GlanceModifier.fillMaxWidth()) {
                    BigField(
                        label = "నక్షత్రం",
                        value = data.nakshatra,
                        labelColor = goldColor,
                        valueColor = whiteColor,
                        modifier = GlanceModifier.defaultWeight(),
                    )
                    BigField(
                        label = "యోగం",
                        value = data.yoga,
                        labelColor = goldColor,
                        valueColor = whiteColor,
                        modifier = GlanceModifier.defaultWeight(),
                    )
                }

                Spacer(GlanceModifier.height(10.dp))

                // Row 3: Sunrise | Sunset — prominent
                Row(modifier = GlanceModifier.fillMaxWidth()) {
                    BigField(
                        label = "సూర్యోదయం",
                        value = data.sunrise,
                        labelColor = goldColor,
                        valueColor = whiteColor,
                        modifier = GlanceModifier.defaultWeight(),
                    )
                    BigField(
                        label = "సూర్యాస్తం",
                        value = data.sunset,
                        labelColor = goldColor,
                        valueColor = whiteColor,
                        modifier = GlanceModifier.defaultWeight(),
                    )
                }

                Spacer(GlanceModifier.height(8.dp))

                Row(
                    modifier = GlanceModifier.fillMaxWidth(),
                    horizontalAlignment = Alignment.CenterHorizontally,
                ) {
                    Text(
                        "నిత్య పూజ",
                        style = TextStyle(color = dimColor, fontSize = 12.sp),
                    )
                }
            }
        }
    }

    @Composable
    private fun BigField(
        label: String,
        value: String,
        labelColor: ColorProvider,
        valueColor: ColorProvider,
        modifier: GlanceModifier = GlanceModifier,
    ) {
        Column(modifier = modifier) {
            Text(label, style = TextStyle(color = labelColor, fontSize = 14.sp))
            Text(
                value,
                style = TextStyle(
                    color = valueColor,
                    fontSize = 18.sp,
                    fontWeight = FontWeight.Bold,
                ),
            )
        }
    }
}
