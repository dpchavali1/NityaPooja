package com.nityapooja.app.widget

import android.content.Context
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.glance.*
import androidx.glance.action.actionStartActivity
import androidx.glance.action.clickable
import androidx.glance.appwidget.GlanceAppWidget
import androidx.glance.appwidget.provideContent
import androidx.glance.layout.*
import androidx.glance.text.*
import androidx.glance.unit.ColorProvider
import com.nityapooja.app.MainActivity

class PanchangamWidget : GlanceAppWidget() {

    override suspend fun provideGlance(context: Context, id: GlanceId) {
        val data = PanchangamWidgetDataProvider.getData(context)
        provideContent {
            WidgetContent(data)
        }
    }

    @Composable
    private fun WidgetContent(data: WidgetPanchangamData) {
        val goldColor = ColorProvider(Color(0xFFD4A017))
        val whiteColor = ColorProvider(Color.White)
        val bgColor = ColorProvider(Color(0xFF1A0A00))
        val subtleColor = ColorProvider(Color(0xFFBBBBBB))

        Box(
            modifier = GlanceModifier
                .fillMaxSize()
                .background(bgColor)
                .clickable(actionStartActivity<MainActivity>())
                .padding(10.dp),
        ) {
            Column(
                modifier = GlanceModifier.fillMaxSize(),
                verticalAlignment = Alignment.CenterVertically,
            ) {
                // Header row: date + location
                Row(
                    modifier = GlanceModifier.fillMaxWidth(),
                    horizontalAlignment = Alignment.CenterHorizontally,
                ) {
                    Text(
                        text = data.dateDisplay,
                        style = TextStyle(color = goldColor, fontSize = 13.sp, fontWeight = FontWeight.Bold),
                    )
                }

                Spacer(GlanceModifier.height(4.dp))

                // 2-column layout for panchangam fields
                Row(
                    modifier = GlanceModifier.fillMaxWidth(),
                    horizontalAlignment = Alignment.CenterHorizontally,
                ) {
                    Column(modifier = GlanceModifier.defaultWeight()) {
                        PanchangamRow("తిథి", data.tithi, goldColor, whiteColor)
                        Spacer(GlanceModifier.height(3.dp))
                        PanchangamRow("నక్షత్రం", data.nakshatra, goldColor, whiteColor)
                        Spacer(GlanceModifier.height(3.dp))
                        PanchangamRow("యోగం", data.yoga, goldColor, whiteColor)
                    }
                    Spacer(GlanceModifier.width(8.dp))
                    Column(modifier = GlanceModifier.defaultWeight()) {
                        PanchangamRow("కరణం", data.karana, goldColor, whiteColor)
                        Spacer(GlanceModifier.height(3.dp))
                        PanchangamRow("సూర్యోదయం", data.sunrise, goldColor, whiteColor)
                        Spacer(GlanceModifier.height(3.dp))
                        PanchangamRow("సూర్యాస్తం", data.sunset, goldColor, whiteColor)
                    }
                }

                Spacer(GlanceModifier.height(4.dp))

                // Footer
                Row(
                    modifier = GlanceModifier.fillMaxWidth(),
                    horizontalAlignment = Alignment.CenterHorizontally,
                ) {
                    Text(
                        text = "నిత్య పూజ",
                        style = TextStyle(color = subtleColor, fontSize = 10.sp),
                    )
                }
            }
        }
    }

    @Composable
    private fun PanchangamRow(
        label: String,
        value: String,
        labelColor: ColorProvider,
        valueColor: ColorProvider,
    ) {
        Column {
            Text(label, style = TextStyle(color = labelColor, fontSize = 11.sp))
            Text(value, style = TextStyle(color = valueColor, fontSize = 13.sp, fontWeight = FontWeight.Medium))
        }
    }
}
