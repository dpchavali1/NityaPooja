package com.nityapooja.app.ui.panchangam

import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import android.content.Intent
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.nityapooja.app.ui.components.BannerAd
import com.nityapooja.app.ui.components.FontSizeControls
import com.nityapooja.app.ui.components.FontSizeViewModel
import com.nityapooja.app.ui.components.GlassmorphicCard
import com.nityapooja.app.ui.components.SectionHeader
import com.nityapooja.app.ui.theme.*
import java.util.Calendar
import java.util.TimeZone

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun PanchangamScreen(
    viewModel: PanchangamViewModel = hiltViewModel(),
    fontSizeViewModel: FontSizeViewModel = hiltViewModel(),
) {
    val locationInfo by viewModel.locationInfo.collectAsStateWithLifecycle()
    val selectedDate by viewModel.selectedDate.collectAsStateWithLifecycle()
    val fontSize by fontSizeViewModel.fontSize.collectAsStateWithLifecycle()
    val fontScale = fontSize / 16f

    val context = LocalContext.current
    var showDatePicker by remember { mutableStateOf(false) }

    val panchangam = remember(locationInfo, selectedDate) {
        viewModel.calculatePanchangam(locationInfo.lat, locationInfo.lng, locationInfo.timezone, selectedDate)
    }

    // Get current calendar for date picker and navigation
    val currentCal = remember(selectedDate) {
        val tz = TimeZone.getTimeZone(locationInfo.timezone)
        Calendar.getInstance(tz).apply {
            if (selectedDate != null) {
                set(Calendar.YEAR, selectedDate!!.year)
                set(Calendar.MONTH, selectedDate!!.month - 1)
                set(Calendar.DAY_OF_MONTH, selectedDate!!.day)
            }
        }
    }

    val teluguDay = panchangam.teluguDay
    val today = panchangam.dateDisplay

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Column {
                        Text(
                            "పంచాంగం",
                            style = MaterialTheme.typography.titleLarge,
                            fontWeight = FontWeight.Bold,
                        )
                        Text(
                            "Panchangam",
                            style = MaterialTheme.typography.labelSmall,
                            color = MaterialTheme.colorScheme.onSurfaceVariant,
                        )
                    }
                },
                actions = {
                    IconButton(onClick = {
                        val shareText = buildPanchangamShareText(panchangam, locationInfo.city)
                        val intent = Intent(Intent.ACTION_SEND).apply {
                            type = "text/plain"
                            putExtra(Intent.EXTRA_TEXT, shareText)
                        }
                        context.startActivity(Intent.createChooser(intent, "Share Panchangam"))
                    }) {
                        Icon(Icons.Default.Share, "Share", tint = TempleGold)
                    }
                    FontSizeControls(
                        fontSize = fontSize,
                        onDecrease = fontSizeViewModel::decrease,
                        onIncrease = fontSizeViewModel::increase,
                    )
                },
            )
        },
    ) { padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .verticalScroll(rememberScrollState())
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp),
        ) {
            // ═══════════════════════════════════════════
            // Date navigation row
            // ═══════════════════════════════════════════
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(8.dp),
                verticalAlignment = Alignment.CenterVertically,
            ) {
                // Previous day button
                FilledTonalIconButton(
                    onClick = {
                        val c = currentCal.clone() as Calendar
                        c.add(Calendar.DAY_OF_MONTH, -1)
                        viewModel.selectDate(
                            c.get(Calendar.YEAR),
                            c.get(Calendar.MONTH) + 1,
                            c.get(Calendar.DAY_OF_MONTH),
                        )
                    },
                    modifier = Modifier.size(36.dp),
                ) {
                    Icon(Icons.Default.ChevronLeft, "Previous day", modifier = Modifier.size(20.dp))
                }

                // Date header card — clickable to open date picker
                GlassmorphicCard(
                    accentColor = TempleGold,
                    cornerRadius = 16.dp,
                    contentPadding = 16.dp,
                    modifier = Modifier.weight(1f),
                    onClick = { showDatePicker = true },
                ) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically,
                    ) {
                        Column(modifier = Modifier.weight(1f)) {
                            Text(
                                teluguDay,
                                style = MaterialTheme.typography.titleMedium.copy(
                                    fontSize = (16 * fontScale).sp,
                                ),
                                fontWeight = FontWeight.Bold,
                            )
                            Text(
                                today,
                                style = MaterialTheme.typography.bodySmall.copy(
                                    fontSize = (12 * fontScale).sp,
                                ),
                                color = MaterialTheme.colorScheme.onSurfaceVariant,
                            )
                            Spacer(Modifier.height(4.dp))
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Icon(
                                    Icons.Default.LocationOn,
                                    contentDescription = null,
                                    tint = TempleGold,
                                    modifier = Modifier.size(14.dp),
                                )
                                Spacer(Modifier.width(4.dp))
                                Text(
                                    locationInfo.city,
                                    style = MaterialTheme.typography.labelSmall,
                                    color = TempleGold,
                                )
                            }
                        }
                        Icon(
                            Icons.Default.CalendarMonth,
                            contentDescription = "Pick date",
                            tint = TempleGold,
                            modifier = Modifier.size(32.dp),
                        )
                    }
                }

                // Next day button
                FilledTonalIconButton(
                    onClick = {
                        val c = currentCal.clone() as Calendar
                        c.add(Calendar.DAY_OF_MONTH, 1)
                        viewModel.selectDate(
                            c.get(Calendar.YEAR),
                            c.get(Calendar.MONTH) + 1,
                            c.get(Calendar.DAY_OF_MONTH),
                        )
                    },
                    modifier = Modifier.size(36.dp),
                ) {
                    Icon(Icons.Default.ChevronRight, "Next day", modifier = Modifier.size(20.dp))
                }
            }

            // "Today" button — shown only when viewing a non-today date
            if (!panchangam.isToday) {
                AssistChip(
                    onClick = { viewModel.selectToday() },
                    label = { Text("Today") },
                    leadingIcon = {
                        Icon(
                            Icons.Default.Today,
                            contentDescription = null,
                            modifier = Modifier.size(16.dp),
                        )
                    },
                    modifier = Modifier.align(Alignment.CenterHorizontally),
                )
            }

            // ═══════════════════════════════════════════
            // Telugu Calendar Info — Samvatsara, Masa, Ayana, Rutu
            // ═══════════════════════════════════════════
            SectionHeader(titleTelugu = "తెలుగు సంవత్సర వివరాలు", titleEnglish = "Telugu Calendar")

            GlassmorphicCard(accentColor = TempleGold, cornerRadius = 14.dp, contentPadding = 16.dp) {
                Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                    CalendarInfoRow(
                        labelTelugu = "సంవత్సరం",
                        labelEnglish = "Year",
                        valueTelugu = panchangam.samvatsara.nameTelugu,
                        valueEnglish = panchangam.samvatsara.nameEnglish,
                        fontScale = fontScale,
                    )
                    HorizontalDivider(color = TempleGold.copy(alpha = 0.15f))
                    CalendarInfoRow(
                        labelTelugu = "మాసం",
                        labelEnglish = "Month",
                        valueTelugu = panchangam.masa.nameTelugu,
                        valueEnglish = panchangam.masa.nameEnglish,
                        fontScale = fontScale,
                    )
                    HorizontalDivider(color = TempleGold.copy(alpha = 0.15f))
                    CalendarInfoRow(
                        labelTelugu = "ఆయనం",
                        labelEnglish = "Ayana",
                        valueTelugu = panchangam.ayana.nameTelugu,
                        valueEnglish = panchangam.ayana.nameEnglish,
                        fontScale = fontScale,
                    )
                    HorizontalDivider(color = TempleGold.copy(alpha = 0.15f))
                    CalendarInfoRow(
                        labelTelugu = "ఋతువు",
                        labelEnglish = "Season",
                        valueTelugu = panchangam.rutu.nameTelugu,
                        valueEnglish = panchangam.rutu.nameEnglish,
                        fontScale = fontScale,
                    )
                }
            }

            // ═══════════════════════════════════════════
            // Sunrise / Sunset
            // ═══════════════════════════════════════════
            SectionHeader(titleTelugu = "సూర్యచంద్రులు", titleEnglish = "Sun Times")

            GlassmorphicCard(accentColor = SuryaColor, cornerRadius = 14.dp, contentPadding = 16.dp) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceEvenly,
                ) {
                    SunTimeColumn(
                        icon = Icons.Default.WbSunny,
                        label = "సూర్యోదయం",
                        labelEn = "Sunrise",
                        time = panchangam.sunTimes.sunrise,
                        tint = SuryaColor,
                        fontScale = fontScale,
                    )
                    Box(
                        modifier = Modifier
                            .width(1.dp)
                            .height(56.dp)
                            .background(TempleGold.copy(alpha = 0.3f)),
                    )
                    SunTimeColumn(
                        icon = Icons.Default.WbTwilight,
                        label = "సూర్యాస్తమయం",
                        labelEn = "Sunset",
                        time = panchangam.sunTimes.sunset,
                        tint = DeepVermillion,
                        fontScale = fontScale,
                    )
                }
            }

            // ═══════════════════════════════════════════
            // Panchangam Details (5 Angas)
            // ═══════════════════════════════════════════
            SectionHeader(titleTelugu = "పంచాంగ వివరాలు", titleEnglish = "Panchang Details")

            // Tithi card — with end time
            PanchangDetailCardWithTime(
                titleTelugu = "తిథి",
                titleEnglish = "Tithi",
                valueTelugu = panchangam.tithi.nameTelugu,
                valueEnglish = panchangam.tithi.nameEnglish,
                subtitle = "${panchangam.tithi.pakshaTelugu} (${panchangam.tithi.paksha})",
                endTime = panchangam.tithi.endTime,
                icon = Icons.Default.Brightness2,
                accentColor = TempleGold,
                fontScale = fontScale,
            )

            // Nakshatra card — with end time
            PanchangDetailCardWithTime(
                titleTelugu = "నక్షత్రం",
                titleEnglish = "Nakshatra",
                valueTelugu = panchangam.nakshatra.nameTelugu,
                valueEnglish = panchangam.nakshatra.nameEnglish,
                endTime = panchangam.nakshatra.endTime,
                icon = Icons.Default.Star,
                accentColor = TempleGold,
                fontScale = fontScale,
            )

            // Yoga card — with end time
            PanchangDetailCardWithTime(
                titleTelugu = "యోగం",
                titleEnglish = "Yoga",
                valueTelugu = panchangam.yoga.nameTelugu,
                valueEnglish = panchangam.yoga.nameEnglish,
                endTime = panchangam.yoga.endTime,
                icon = Icons.Default.AllInclusive,
                accentColor = TempleGold,
                fontScale = fontScale,
            )

            // Karana card
            GlassmorphicCard(accentColor = TempleGold, cornerRadius = 14.dp, contentPadding = 14.dp) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Box(
                        modifier = Modifier
                            .size(36.dp)
                            .clip(CircleShape)
                            .background(TempleGold.copy(alpha = 0.15f)),
                        contentAlignment = Alignment.Center,
                    ) {
                        Icon(
                            Icons.Default.SwapHoriz,
                            contentDescription = null,
                            tint = TempleGold,
                            modifier = Modifier.size(20.dp),
                        )
                    }
                    Spacer(Modifier.width(12.dp))
                    Column(modifier = Modifier.weight(1f)) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Text(
                                "కరణం",
                                style = MaterialTheme.typography.titleSmall,
                                fontWeight = FontWeight.Bold,
                            )
                            Spacer(Modifier.width(6.dp))
                            Text(
                                "Karana",
                                style = MaterialTheme.typography.labelSmall,
                                color = MaterialTheme.colorScheme.onSurfaceVariant,
                            )
                        }
                        Spacer(Modifier.height(4.dp))
                        Text(
                            "1st: ${panchangam.karana.firstNameTelugu} (${panchangam.karana.firstNameEnglish})",
                            style = MaterialTheme.typography.bodyMedium,
                            fontWeight = FontWeight.SemiBold,
                        )
                        Text(
                            "2nd: ${panchangam.karana.secondNameTelugu} (${panchangam.karana.secondNameEnglish})",
                            style = MaterialTheme.typography.bodyMedium,
                            color = MaterialTheme.colorScheme.onSurfaceVariant,
                        )
                    }
                }
            }

            // Vaaram (day) card
            PanchangDetailCard(
                titleTelugu = "వారం",
                titleEnglish = "Day",
                valueTelugu = teluguDay,
                valueEnglish = panchangam.englishDay,
                icon = Icons.Default.Today,
                accentColor = TempleGold,
                fontScale = fontScale,
            )

            // ═══════════════════════════════════════════
            // Rashi section — Sun & Moon
            // ═══════════════════════════════════════════
            SectionHeader(titleTelugu = "రాశి వివరాలు", titleEnglish = "Rashi (Zodiac)")

            GlassmorphicCard(accentColor = TempleGold, cornerRadius = 14.dp, contentPadding = 16.dp) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceEvenly,
                ) {
                    RashiColumn(
                        label = "సూర్య రాశి",
                        labelEn = "Sun Sign",
                        valueTelugu = panchangam.sunRashi.nameTelugu,
                        valueEnglish = panchangam.sunRashi.nameEnglish,
                        tint = SuryaColor,
                        fontScale = fontScale,
                    )
                    Box(
                        modifier = Modifier
                            .width(1.dp)
                            .height(56.dp)
                            .background(TempleGold.copy(alpha = 0.3f)),
                    )
                    RashiColumn(
                        label = "చంద్ర రాశి",
                        labelEn = "Moon Sign",
                        valueTelugu = panchangam.moonRashi.nameTelugu,
                        valueEnglish = panchangam.moonRashi.nameEnglish,
                        tint = TempleGold,
                        fontScale = fontScale,
                    )
                }
            }

            // ═══════════════════════════════════════════
            // Shubha Muhurtam / Good Times section
            // ═══════════════════════════════════════════
            SectionHeader(titleTelugu = "శుభ సమయాలు", titleEnglish = "Good Times")

            // Brahma Muhurta card
            ShubhaTimeCard(
                slot = panchangam.brahmaMuhurta,
                icon = Icons.Default.Brightness5,
                showActive = panchangam.isToday,
            )

            // Abhijit Muhurt card
            ShubhaTimeCard(
                slot = TimeSlotInfo(
                    nameTelugu = "అభిజిత్ ముహూర్తం",
                    nameEnglish = "Abhijit Muhurt",
                    startTime = panchangam.abhijitMuhurt.startTime,
                    endTime = panchangam.abhijitMuhurt.endTime,
                    isActive = panchangam.abhijitMuhurt.isActive,
                ),
                icon = Icons.Default.Verified,
                showActive = panchangam.isToday,
            )

            // Shubh Hora cards
            if (panchangam.shubhHoras.isNotEmpty()) {
                GlassmorphicCard(
                    accentColor = AuspiciousGreen,
                    cornerRadius = 14.dp,
                    contentPadding = 14.dp,
                ) {
                    Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Box(
                                modifier = Modifier
                                    .size(36.dp)
                                    .clip(CircleShape)
                                    .background(AuspiciousGreen.copy(alpha = 0.15f)),
                                contentAlignment = Alignment.Center,
                            ) {
                                Icon(
                                    Icons.Default.AccessTime,
                                    contentDescription = null,
                                    tint = AuspiciousGreen,
                                    modifier = Modifier.size(20.dp),
                                )
                            }
                            Spacer(Modifier.width(12.dp))
                            Column {
                                Text(
                                    "శుభ హోరలు",
                                    style = MaterialTheme.typography.titleSmall,
                                    fontWeight = FontWeight.Bold,
                                )
                                Text(
                                    "Shubh Hora · Auspicious Hours",
                                    style = MaterialTheme.typography.labelSmall,
                                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                                )
                            }
                        }
                        panchangam.shubhHoras.forEach { hora ->
                            HorizontalDivider(color = AuspiciousGreen.copy(alpha = 0.15f))
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically,
                            ) {
                                Column {
                                    Text(
                                        hora.nameTelugu,
                                        style = MaterialTheme.typography.bodyMedium,
                                        fontWeight = FontWeight.SemiBold,
                                    )
                                    Text(
                                        hora.nameEnglish,
                                        style = MaterialTheme.typography.labelSmall,
                                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                                    )
                                }
                                Column(horizontalAlignment = Alignment.End) {
                                    Text(
                                        "${hora.startTime} - ${hora.endTime}",
                                        style = MaterialTheme.typography.bodyMedium,
                                        fontWeight = FontWeight.Bold,
                                        color = AuspiciousGreen,
                                    )
                                    if (panchangam.isToday && hora.isActive) {
                                        Text(
                                            "Active Now",
                                            style = MaterialTheme.typography.labelSmall,
                                            fontWeight = FontWeight.SemiBold,
                                            color = AuspiciousGreen,
                                        )
                                    }
                                }
                            }
                        }
                    }
                }
            }

            // ═══════════════════════════════════════════
            // Nishiddha Samayalu / Times to Avoid section
            // ═══════════════════════════════════════════
            SectionHeader(titleTelugu = "నిషిద్ధ సమయాలు", titleEnglish = "Times to Avoid")

            // Rahu Kaal warning card
            RahuKaalCard(rahuKaal = panchangam.rahuKaal, showActive = panchangam.isToday)

            // Yamagandam card
            MuhurtaWarningCard(
                titleTelugu = "యమగండం",
                titleEnglish = "Yamagandam",
                startTime = panchangam.yamagandam.startTime,
                endTime = panchangam.yamagandam.endTime,
                isActive = panchangam.isToday && panchangam.yamagandam.isActive,
                activeMessage = "ప్రస్తుతం యమగండం · Active Now",
                icon = Icons.Default.DoNotDisturb,
                activeColor = InauspiciousRed,
                inactiveColor = WarningAmber,
            )

            // Gulika Kalam card
            MuhurtaWarningCard(
                titleTelugu = "గుళిక కాలం",
                titleEnglish = "Gulika Kalam",
                startTime = panchangam.gulikaKalam.startTime,
                endTime = panchangam.gulikaKalam.endTime,
                isActive = panchangam.isToday && panchangam.gulikaKalam.isActive,
                activeMessage = "ప్రస్తుతం గుళిక కాలం · Active Now",
                icon = Icons.Default.RemoveCircleOutline,
                activeColor = InauspiciousRed,
                inactiveColor = WarningAmber,
            )

            // ═══ Ad Banner ═══
            BannerAd()

            Spacer(Modifier.height(60.dp))
        }
    }

    // Date Picker Dialog
    if (showDatePicker) {
        val datePickerState = rememberDatePickerState(
            initialSelectedDateMillis = currentCal.timeInMillis,
        )
        DatePickerDialog(
            onDismissRequest = { showDatePicker = false },
            confirmButton = {
                TextButton(onClick = {
                    datePickerState.selectedDateMillis?.let { millis ->
                        val picked = Calendar.getInstance(TimeZone.getTimeZone("UTC"))
                        picked.timeInMillis = millis
                        viewModel.selectDate(
                            picked.get(Calendar.YEAR),
                            picked.get(Calendar.MONTH) + 1,
                            picked.get(Calendar.DAY_OF_MONTH),
                        )
                    }
                    showDatePicker = false
                }) { Text("OK") }
            },
            dismissButton = {
                TextButton(onClick = { showDatePicker = false }) { Text("Cancel") }
            },
        ) {
            DatePicker(state = datePickerState)
        }
    }
}

// ═══════════════════════════════════════════════════════════════
// Composable helpers
// ═══════════════════════════════════════════════════════════════

@Composable
private fun CalendarInfoRow(
    labelTelugu: String,
    labelEnglish: String,
    valueTelugu: String,
    valueEnglish: String,
    fontScale: Float = 1f,
) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically,
    ) {
        Column {
            Text(
                labelTelugu,
                style = MaterialTheme.typography.labelMedium,
                color = TempleGold,
                fontWeight = FontWeight.Bold,
            )
            Text(
                labelEnglish,
                style = MaterialTheme.typography.labelSmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
            )
        }
        Column(horizontalAlignment = Alignment.End) {
            Text(
                valueTelugu,
                style = MaterialTheme.typography.bodyMedium.copy(fontSize = (14 * fontScale).sp),
                fontWeight = FontWeight.Bold,
            )
            Text(
                valueEnglish,
                style = MaterialTheme.typography.labelSmall.copy(fontSize = (11 * fontScale).sp),
                color = MaterialTheme.colorScheme.onSurfaceVariant,
            )
        }
    }
}

@Composable
private fun SunTimeColumn(
    icon: ImageVector,
    label: String,
    labelEn: String,
    time: String,
    tint: androidx.compose.ui.graphics.Color,
    fontScale: Float = 1f,
) {
    Column(horizontalAlignment = Alignment.CenterHorizontally) {
        Icon(icon, contentDescription = labelEn, tint = tint, modifier = Modifier.size(24.dp))
        Spacer(Modifier.height(4.dp))
        Text(label, style = MaterialTheme.typography.labelSmall.copy(fontSize = (11 * fontScale).sp), color = MaterialTheme.colorScheme.onSurfaceVariant)
        Text(
            time,
            style = MaterialTheme.typography.titleMedium.copy(fontSize = (16 * fontScale).sp),
            fontWeight = FontWeight.Bold,
        )
    }
}

@Composable
private fun RashiColumn(
    label: String,
    labelEn: String,
    valueTelugu: String,
    valueEnglish: String,
    tint: androidx.compose.ui.graphics.Color,
    fontScale: Float = 1f,
) {
    Column(horizontalAlignment = Alignment.CenterHorizontally) {
        Text(
            label,
            style = MaterialTheme.typography.labelSmall.copy(fontSize = (11 * fontScale).sp),
            color = tint,
            fontWeight = FontWeight.Bold,
        )
        Text(
            labelEn,
            style = MaterialTheme.typography.labelSmall,
            color = MaterialTheme.colorScheme.onSurfaceVariant,
            fontSize = (10 * fontScale).sp,
        )
        Spacer(Modifier.height(4.dp))
        Text(
            valueTelugu,
            style = MaterialTheme.typography.titleMedium.copy(fontSize = (16 * fontScale).sp),
            fontWeight = FontWeight.Bold,
        )
        Text(
            valueEnglish,
            style = MaterialTheme.typography.labelSmall.copy(fontSize = (11 * fontScale).sp),
            color = MaterialTheme.colorScheme.onSurfaceVariant,
        )
    }
}

@Composable
private fun PanchangDetailCard(
    titleTelugu: String,
    titleEnglish: String,
    valueTelugu: String,
    valueEnglish: String,
    subtitle: String? = null,
    icon: ImageVector,
    accentColor: androidx.compose.ui.graphics.Color,
    fontScale: Float = 1f,
) {
    GlassmorphicCard(accentColor = accentColor, cornerRadius = 14.dp, contentPadding = 14.dp) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically,
        ) {
            Box(
                modifier = Modifier
                    .size(36.dp)
                    .clip(CircleShape)
                    .background(accentColor.copy(alpha = 0.15f)),
                contentAlignment = Alignment.Center,
            ) {
                Icon(
                    icon,
                    contentDescription = null,
                    tint = accentColor,
                    modifier = Modifier.size(20.dp),
                )
            }
            Spacer(Modifier.width(12.dp))
            Column(modifier = Modifier.weight(1f)) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Text(
                        titleTelugu,
                        style = MaterialTheme.typography.titleSmall.copy(fontSize = (14 * fontScale).sp),
                        fontWeight = FontWeight.Bold,
                    )
                    Spacer(Modifier.width(6.dp))
                    Text(
                        titleEnglish,
                        style = MaterialTheme.typography.labelSmall.copy(fontSize = (11 * fontScale).sp),
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                    )
                }
                Spacer(Modifier.height(2.dp))
                if (subtitle != null) {
                    Text(
                        subtitle,
                        style = MaterialTheme.typography.labelSmall.copy(fontSize = (11 * fontScale).sp),
                        color = accentColor.copy(alpha = 0.8f),
                    )
                }
            }
            Column(horizontalAlignment = Alignment.End) {
                Text(
                    valueTelugu,
                    style = MaterialTheme.typography.bodyMedium.copy(fontSize = (14 * fontScale).sp),
                    fontWeight = FontWeight.Bold,
                    textAlign = TextAlign.End,
                )
                Text(
                    valueEnglish,
                    style = MaterialTheme.typography.labelSmall.copy(fontSize = (11 * fontScale).sp),
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    textAlign = TextAlign.End,
                )
            }
        }
    }
}

@Composable
private fun PanchangDetailCardWithTime(
    titleTelugu: String,
    titleEnglish: String,
    valueTelugu: String,
    valueEnglish: String,
    subtitle: String? = null,
    endTime: String,
    icon: ImageVector,
    accentColor: androidx.compose.ui.graphics.Color,
    fontScale: Float = 1f,
) {
    GlassmorphicCard(accentColor = accentColor, cornerRadius = 14.dp, contentPadding = 14.dp) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically,
        ) {
            Box(
                modifier = Modifier
                    .size(36.dp)
                    .clip(CircleShape)
                    .background(accentColor.copy(alpha = 0.15f)),
                contentAlignment = Alignment.Center,
            ) {
                Icon(
                    icon,
                    contentDescription = null,
                    tint = accentColor,
                    modifier = Modifier.size(20.dp),
                )
            }
            Spacer(Modifier.width(12.dp))
            Column(modifier = Modifier.weight(1f)) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Text(
                        titleTelugu,
                        style = MaterialTheme.typography.titleSmall.copy(fontSize = (14 * fontScale).sp),
                        fontWeight = FontWeight.Bold,
                    )
                    Spacer(Modifier.width(6.dp))
                    Text(
                        titleEnglish,
                        style = MaterialTheme.typography.labelSmall.copy(fontSize = (11 * fontScale).sp),
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                    )
                }
                if (subtitle != null) {
                    Spacer(Modifier.height(2.dp))
                    Text(
                        subtitle,
                        style = MaterialTheme.typography.labelSmall.copy(fontSize = (11 * fontScale).sp),
                        color = accentColor.copy(alpha = 0.8f),
                    )
                }
                Spacer(Modifier.height(2.dp))
                // End time row
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(
                        Icons.Default.Schedule,
                        contentDescription = null,
                        tint = MaterialTheme.colorScheme.onSurfaceVariant,
                        modifier = Modifier.size(12.dp),
                    )
                    Spacer(Modifier.width(4.dp))
                    Text(
                        "వరకు: $endTime",
                        style = MaterialTheme.typography.labelSmall.copy(fontSize = (11 * fontScale).sp),
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                    )
                }
            }
            Column(horizontalAlignment = Alignment.End) {
                Text(
                    valueTelugu,
                    style = MaterialTheme.typography.bodyMedium.copy(fontSize = (14 * fontScale).sp),
                    fontWeight = FontWeight.Bold,
                    textAlign = TextAlign.End,
                )
                Text(
                    valueEnglish,
                    style = MaterialTheme.typography.labelSmall.copy(fontSize = (11 * fontScale).sp),
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    textAlign = TextAlign.End,
                )
            }
        }
    }
}

@Composable
private fun ShubhaTimeCard(
    slot: TimeSlotInfo,
    icon: ImageVector,
    showActive: Boolean,
) {
    val accentColor = AuspiciousGreen

    GlassmorphicCard(
        accentColor = accentColor,
        cornerRadius = 14.dp,
        contentPadding = 14.dp,
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically,
        ) {
            Box(
                modifier = Modifier
                    .size(36.dp)
                    .clip(CircleShape)
                    .background(accentColor.copy(alpha = 0.15f)),
                contentAlignment = Alignment.Center,
            ) {
                Icon(
                    icon,
                    contentDescription = null,
                    tint = accentColor,
                    modifier = Modifier.size(20.dp),
                )
            }
            Spacer(Modifier.width(12.dp))
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    slot.nameTelugu,
                    style = MaterialTheme.typography.titleSmall,
                    fontWeight = FontWeight.Bold,
                )
                Text(
                    slot.nameEnglish,
                    style = MaterialTheme.typography.labelSmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                )
                if (showActive && slot.isActive) {
                    Spacer(Modifier.height(2.dp))
                    Text(
                        "ప్రస్తుతం శుభ సమయం · Active Now",
                        style = MaterialTheme.typography.labelSmall,
                        fontWeight = FontWeight.SemiBold,
                        color = accentColor,
                    )
                }
            }
            Text(
                "${slot.startTime} - ${slot.endTime}",
                style = MaterialTheme.typography.bodyMedium,
                fontWeight = FontWeight.Bold,
                color = accentColor,
            )
        }
    }
}

@Composable
private fun RahuKaalCard(rahuKaal: RahuKaalInfo, showActive: Boolean = true) {
    val isActiveNow = showActive && rahuKaal.isActive
    val accentColor = if (isActiveNow) InauspiciousRed else WarningAmber

    val infiniteTransition = rememberInfiniteTransition(label = "rahu_pulse")
    val pulseAlpha by infiniteTransition.animateFloat(
        initialValue = 1f,
        targetValue = if (isActiveNow) 0.5f else 1f,
        animationSpec = infiniteRepeatable(
            animation = tween(800),
            repeatMode = RepeatMode.Reverse,
        ),
        label = "rahu_alpha",
    )

    GlassmorphicCard(accentColor = accentColor, cornerRadius = 14.dp, contentPadding = 14.dp) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically,
        ) {
            Box(
                modifier = Modifier
                    .size(36.dp)
                    .clip(CircleShape)
                    .background(accentColor.copy(alpha = 0.15f * pulseAlpha)),
                contentAlignment = Alignment.Center,
            ) {
                Icon(
                    Icons.Default.Warning,
                    contentDescription = null,
                    tint = accentColor.copy(alpha = pulseAlpha),
                    modifier = Modifier.size(20.dp),
                )
            }
            Spacer(Modifier.width(12.dp))
            Column(modifier = Modifier.weight(1f)) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Text(
                        "రాహు కాలం",
                        style = MaterialTheme.typography.titleSmall,
                        fontWeight = FontWeight.Bold,
                        color = accentColor,
                    )
                    Spacer(Modifier.width(6.dp))
                    Text(
                        "Rahu Kaal",
                        style = MaterialTheme.typography.labelSmall,
                        color = accentColor.copy(alpha = 0.7f),
                    )
                }
                if (isActiveNow) {
                    Spacer(Modifier.height(2.dp))
                    Text(
                        "ప్రస్తుతం రాహు కాలం · Active Now",
                        style = MaterialTheme.typography.labelSmall,
                        fontWeight = FontWeight.SemiBold,
                        color = InauspiciousRed,
                    )
                }
            }
            Text(
                "${rahuKaal.startTime} - ${rahuKaal.endTime}",
                style = MaterialTheme.typography.bodyMedium,
                fontWeight = FontWeight.Bold,
                color = accentColor,
            )
        }
    }
}

@Composable
private fun MuhurtaWarningCard(
    titleTelugu: String,
    titleEnglish: String,
    startTime: String,
    endTime: String,
    isActive: Boolean,
    activeMessage: String,
    icon: ImageVector,
    activeColor: androidx.compose.ui.graphics.Color,
    inactiveColor: androidx.compose.ui.graphics.Color,
) {
    val accentColor = if (isActive) activeColor else inactiveColor

    GlassmorphicCard(accentColor = accentColor, cornerRadius = 14.dp, contentPadding = 14.dp) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically,
        ) {
            Box(
                modifier = Modifier
                    .size(36.dp)
                    .clip(CircleShape)
                    .background(accentColor.copy(alpha = 0.15f)),
                contentAlignment = Alignment.Center,
            ) {
                Icon(
                    icon,
                    contentDescription = null,
                    tint = accentColor,
                    modifier = Modifier.size(20.dp),
                )
            }
            Spacer(Modifier.width(12.dp))
            Column(modifier = Modifier.weight(1f)) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Text(
                        titleTelugu,
                        style = MaterialTheme.typography.titleSmall,
                        fontWeight = FontWeight.Bold,
                        color = accentColor,
                    )
                    Spacer(Modifier.width(6.dp))
                    Text(
                        titleEnglish,
                        style = MaterialTheme.typography.labelSmall,
                        color = accentColor.copy(alpha = 0.7f),
                    )
                }
                if (isActive) {
                    Spacer(Modifier.height(2.dp))
                    Text(
                        activeMessage,
                        style = MaterialTheme.typography.labelSmall,
                        fontWeight = FontWeight.SemiBold,
                        color = activeColor,
                    )
                }
            }
            Text(
                "$startTime - $endTime",
                style = MaterialTheme.typography.bodyMedium,
                fontWeight = FontWeight.Bold,
                color = accentColor,
            )
        }
    }
}

private fun buildPanchangamShareText(panchangam: PanchangamData, city: String): String = buildString {
    val dateLabel = if (panchangam.isToday) "నేటి పంచాంగం · Today's Panchangam" else "పంచాంగం · Panchangam"
    append("🙏 $dateLabel 🙏\n\n")
    append("📅 ${panchangam.dateDisplay}\n")
    append("📅 ${panchangam.teluguDay} · ${panchangam.englishDay}\n")
    append("📍 ${city}\n\n")
    append("📆 ${panchangam.samvatsara.nameTelugu} · ${panchangam.masa.nameTelugu}\n\n")
    append("☀ సూర్యోదయం: ${panchangam.sunTimes.sunrise}\n")
    append("🌙 సూర్యాస్తమయం: ${panchangam.sunTimes.sunset}\n\n")
    append("తిథి: ${panchangam.tithi.nameTelugu} (${panchangam.tithi.nameEnglish})\n")
    append("నక్షత్రం: ${panchangam.nakshatra.nameTelugu} (${panchangam.nakshatra.nameEnglish})\n")
    append("యోగం: ${panchangam.yoga.nameTelugu} (${panchangam.yoga.nameEnglish})\n\n")
    append("🟢 శుభ సమయాలు / Good Times:\n")
    append("బ్రహ్మ ముహూర్తం: ${panchangam.brahmaMuhurta.startTime} - ${panchangam.brahmaMuhurta.endTime}\n")
    append("అభిజిత్ ముహూర్తం: ${panchangam.abhijitMuhurt.startTime} - ${panchangam.abhijitMuhurt.endTime}\n")
    panchangam.shubhHoras.forEach { hora ->
        append("${hora.nameTelugu} హోర: ${hora.startTime} - ${hora.endTime}\n")
    }
    append("\n🔴 నిషిద్ధ సమయాలు / Times to Avoid:\n")
    append("రాహు కాలం: ${panchangam.rahuKaal.startTime} - ${panchangam.rahuKaal.endTime}\n")
    append("యమగండం: ${panchangam.yamagandam.startTime} - ${panchangam.yamagandam.endTime}\n")
    append("గుళిక కాలం: ${panchangam.gulikaKalam.startTime} - ${panchangam.gulikaKalam.endTime}\n\n")
    append("🙏 Shared via NityaPooja")
}
