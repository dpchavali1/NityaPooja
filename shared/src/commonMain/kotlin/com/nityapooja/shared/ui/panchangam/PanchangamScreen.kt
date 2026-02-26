package com.nityapooja.shared.ui.panchangam

import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
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
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import org.koin.compose.viewmodel.koinViewModel
import androidx.compose.runtime.collectAsState
import com.nityapooja.shared.ui.components.FontSizeControls
import com.nityapooja.shared.ui.components.FontSizeViewModel
import com.nityapooja.shared.ui.components.GlassmorphicCard
import com.nityapooja.shared.ui.components.SectionHeader
import com.nityapooja.shared.ui.theme.*
import com.nityapooja.shared.platform.shareText

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun PanchangamScreen(
    viewModel: PanchangamViewModel = koinViewModel(),
    fontSizeViewModel: FontSizeViewModel = koinViewModel(),
) {
    val locationInfo by viewModel.locationInfo.collectAsState()
    val selectedDate by viewModel.selectedDate.collectAsState()
    val fontSize by fontSizeViewModel.fontSize.collectAsState()
    val fontScale = fontSize / 16f

    var showDatePicker by remember { mutableStateOf(false) }

    val panchangam = remember(locationInfo, selectedDate) {
        viewModel.calculatePanchangam(locationInfo.lat, locationInfo.lng, locationInfo.timezone, selectedDate)
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
                        val shareTextContent = buildPanchangamShareText(panchangam, locationInfo.city)
                        shareText(shareTextContent)
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
                    onClick = { viewModel.navigateDay(-1) },
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
                    onClick = { viewModel.navigateDay(1) },
                    modifier = Modifier.size(36.dp),
                ) {
                    Icon(Icons.Default.ChevronRight, "Next day", modifier = Modifier.size(20.dp))
                }
            }

            // "Today" button
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
            // Telugu Calendar Info
            // ═══════════════════════════════════════════
            SectionHeader(titleTelugu = "తెలుగు సంవత్సర వివరాలు", titleEnglish = "Telugu Calendar")

            GlassmorphicCard(accentColor = TempleGold, cornerRadius = 14.dp, contentPadding = 16.dp) {
                Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                    CalendarInfoRow("సంవత్సరం", "Year", panchangam.samvatsara.nameTelugu, panchangam.samvatsara.nameEnglish, fontScale)
                    HorizontalDivider(color = TempleGold.copy(alpha = 0.15f))
                    CalendarInfoRow("మాసం", "Month", panchangam.masa.nameTelugu, panchangam.masa.nameEnglish, fontScale)
                    HorizontalDivider(color = TempleGold.copy(alpha = 0.15f))
                    CalendarInfoRow("ఆయనం", "Ayana", panchangam.ayana.nameTelugu, panchangam.ayana.nameEnglish, fontScale)
                    HorizontalDivider(color = TempleGold.copy(alpha = 0.15f))
                    CalendarInfoRow("ఋతువు", "Season", panchangam.rutu.nameTelugu, panchangam.rutu.nameEnglish, fontScale)
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
                    SunTimeColumn(Icons.Default.WbSunny, "సూర్యోదయం", "Sunrise", panchangam.sunTimes.sunrise, SuryaColor, fontScale)
                    Box(
                        modifier = Modifier.width(1.dp).height(56.dp).background(TempleGold.copy(alpha = 0.3f)),
                    )
                    SunTimeColumn(Icons.Default.WbTwilight, "సూర్యాస్తమయం", "Sunset", panchangam.sunTimes.sunset, DeepVermillion, fontScale)
                }
            }

            // ═══════════════════════════════════════════
            // Panchangam Details (5 Angas)
            // ═══════════════════════════════════════════
            SectionHeader(titleTelugu = "పంచాంగ వివరాలు", titleEnglish = "Panchang Details")

            PanchangDetailCardWithTime("తిథి", "Tithi", panchangam.tithi.nameTelugu, panchangam.tithi.nameEnglish,
                "${panchangam.tithi.pakshaTelugu} (${panchangam.tithi.paksha})", panchangam.tithi.endTime,
                Icons.Default.Brightness2, TempleGold, fontScale)

            PanchangDetailCardWithTime("నక్షత్రం", "Nakshatra", panchangam.nakshatra.nameTelugu, panchangam.nakshatra.nameEnglish,
                endTime = panchangam.nakshatra.endTime, icon = Icons.Default.Star, accentColor = TempleGold, fontScale = fontScale)

            PanchangDetailCardWithTime("యోగం", "Yoga", panchangam.yoga.nameTelugu, panchangam.yoga.nameEnglish,
                endTime = panchangam.yoga.endTime, icon = Icons.Default.AllInclusive, accentColor = TempleGold, fontScale = fontScale)

            // Karana card
            GlassmorphicCard(accentColor = TempleGold, cornerRadius = 14.dp, contentPadding = 14.dp) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Box(
                        modifier = Modifier.size(36.dp).clip(CircleShape).background(TempleGold.copy(alpha = 0.15f)),
                        contentAlignment = Alignment.Center,
                    ) {
                        Icon(Icons.Default.SwapHoriz, null, tint = TempleGold, modifier = Modifier.size(20.dp))
                    }
                    Spacer(Modifier.width(12.dp))
                    Column(modifier = Modifier.weight(1f)) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Text("కరణం", style = MaterialTheme.typography.titleSmall, fontWeight = FontWeight.Bold)
                            Spacer(Modifier.width(6.dp))
                            Text("Karana", style = MaterialTheme.typography.labelSmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
                        }
                        Spacer(Modifier.height(4.dp))
                        Text("1st: ${panchangam.karana.firstNameTelugu} (${panchangam.karana.firstNameEnglish})",
                            style = MaterialTheme.typography.bodyMedium, fontWeight = FontWeight.SemiBold)
                        Text("2nd: ${panchangam.karana.secondNameTelugu} (${panchangam.karana.secondNameEnglish})",
                            style = MaterialTheme.typography.bodyMedium, color = MaterialTheme.colorScheme.onSurfaceVariant)
                    }
                }
            }

            // Vaaram (day) card
            PanchangDetailCard("వారం", "Day", teluguDay, panchangam.englishDay, icon = Icons.Default.Today, accentColor = TempleGold, fontScale = fontScale)

            // ═══════════════════════════════════════════
            // Rashi section
            // ═══════════════════════════════════════════
            SectionHeader(titleTelugu = "రాశి వివరాలు", titleEnglish = "Rashi (Zodiac)")

            GlassmorphicCard(accentColor = TempleGold, cornerRadius = 14.dp, contentPadding = 16.dp) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceEvenly,
                ) {
                    RashiColumn("సూర్య రాశి", "Sun Sign", panchangam.sunRashi.nameTelugu, panchangam.sunRashi.nameEnglish, SuryaColor, fontScale)
                    Box(modifier = Modifier.width(1.dp).height(56.dp).background(TempleGold.copy(alpha = 0.3f)))
                    RashiColumn("చంద్ర రాశి", "Moon Sign", panchangam.moonRashi.nameTelugu, panchangam.moonRashi.nameEnglish, TempleGold, fontScale)
                }
            }

            // ═══════════════════════════════════════════
            // Shubha Muhurtam / Good Times
            // ═══════════════════════════════════════════
            SectionHeader(titleTelugu = "శుభ సమయాలు", titleEnglish = "Good Times")

            ShubhaTimeCard(panchangam.brahmaMuhurta, Icons.Default.Brightness5, panchangam.isToday)

            ShubhaTimeCard(
                TimeSlotInfo(
                    nameTelugu = "అభిజిత్ ముహూర్తం", nameEnglish = "Abhijit Muhurt",
                    startTime = panchangam.abhijitMuhurt.startTime, endTime = panchangam.abhijitMuhurt.endTime,
                    isActive = panchangam.abhijitMuhurt.isActive,
                ),
                Icons.Default.Verified, panchangam.isToday,
            )

            // Shubh Hora cards
            if (panchangam.shubhHoras.isNotEmpty()) {
                GlassmorphicCard(accentColor = AuspiciousGreen, cornerRadius = 14.dp, contentPadding = 14.dp) {
                    Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Box(
                                modifier = Modifier.size(36.dp).clip(CircleShape).background(AuspiciousGreen.copy(alpha = 0.15f)),
                                contentAlignment = Alignment.Center,
                            ) {
                                Icon(Icons.Default.AccessTime, null, tint = AuspiciousGreen, modifier = Modifier.size(20.dp))
                            }
                            Spacer(Modifier.width(12.dp))
                            Column {
                                Text("శుభ హోరలు", style = MaterialTheme.typography.titleSmall, fontWeight = FontWeight.Bold)
                                Text("Shubh Hora · Auspicious Hours", style = MaterialTheme.typography.labelSmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
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
                                    Text(hora.nameTelugu, style = MaterialTheme.typography.bodyMedium, fontWeight = FontWeight.SemiBold)
                                    Text(hora.nameEnglish, style = MaterialTheme.typography.labelSmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
                                }
                                Column(horizontalAlignment = Alignment.End) {
                                    Text("${hora.startTime} - ${hora.endTime}", style = MaterialTheme.typography.bodyMedium, fontWeight = FontWeight.Bold, color = AuspiciousGreen)
                                    if (panchangam.isToday && hora.isActive) {
                                        Text("Active Now", style = MaterialTheme.typography.labelSmall, fontWeight = FontWeight.SemiBold, color = AuspiciousGreen)
                                    }
                                }
                            }
                        }
                    }
                }
            }

            // ═══════════════════════════════════════════
            // Nishiddha Samayalu / Times to Avoid
            // ═══════════════════════════════════════════
            SectionHeader(titleTelugu = "నిషిద్ధ సమయాలు", titleEnglish = "Times to Avoid")

            RahuKaalCard(rahuKaal = panchangam.rahuKaal, showActive = panchangam.isToday)

            MuhurtaWarningCard("యమగండం", "Yamagandam", panchangam.yamagandam.startTime, panchangam.yamagandam.endTime,
                panchangam.isToday && panchangam.yamagandam.isActive, "ప్రస్తుతం యమగండం · Active Now",
                Icons.Default.DoNotDisturb, InauspiciousRed, WarningAmber)

            MuhurtaWarningCard("గుళిక కాలం", "Gulika Kalam", panchangam.gulikaKalam.startTime, panchangam.gulikaKalam.endTime,
                panchangam.isToday && panchangam.gulikaKalam.isActive, "ప్రస్తుతం గుళిక కాలం · Active Now",
                Icons.Default.RemoveCircleOutline, InauspiciousRed, WarningAmber)

            Spacer(Modifier.height(60.dp))
        }
    }

    // Date Picker Dialog
    if (showDatePicker) {
        val datePickerState = rememberDatePickerState()
        DatePickerDialog(
            onDismissRequest = { showDatePicker = false },
            confirmButton = {
                TextButton(onClick = {
                    datePickerState.selectedDateMillis?.let { millis ->
                        viewModel.selectDateFromMillis(millis)
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
    labelTelugu: String, labelEnglish: String,
    valueTelugu: String, valueEnglish: String, fontScale: Float = 1f,
) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically,
    ) {
        Column {
            Text(labelTelugu, style = MaterialTheme.typography.labelMedium, color = TempleGold, fontWeight = FontWeight.Bold)
            Text(labelEnglish, style = MaterialTheme.typography.labelSmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
        }
        Column(horizontalAlignment = Alignment.End) {
            Text(valueTelugu, style = MaterialTheme.typography.bodyMedium.copy(fontSize = (14 * fontScale).sp), fontWeight = FontWeight.Bold)
            Text(valueEnglish, style = MaterialTheme.typography.labelSmall.copy(fontSize = (11 * fontScale).sp), color = MaterialTheme.colorScheme.onSurfaceVariant)
        }
    }
}

@Composable
private fun SunTimeColumn(icon: ImageVector, label: String, labelEn: String, time: String, tint: androidx.compose.ui.graphics.Color, fontScale: Float = 1f) {
    Column(horizontalAlignment = Alignment.CenterHorizontally) {
        Icon(icon, contentDescription = labelEn, tint = tint, modifier = Modifier.size(24.dp))
        Spacer(Modifier.height(4.dp))
        Text(label, style = MaterialTheme.typography.labelSmall.copy(fontSize = (11 * fontScale).sp), color = MaterialTheme.colorScheme.onSurfaceVariant)
        Text(time, style = MaterialTheme.typography.titleMedium.copy(fontSize = (16 * fontScale).sp), fontWeight = FontWeight.Bold)
    }
}

@Composable
private fun RashiColumn(label: String, labelEn: String, valueTelugu: String, valueEnglish: String, tint: androidx.compose.ui.graphics.Color, fontScale: Float = 1f) {
    Column(horizontalAlignment = Alignment.CenterHorizontally) {
        Text(label, style = MaterialTheme.typography.labelSmall.copy(fontSize = (11 * fontScale).sp), color = tint, fontWeight = FontWeight.Bold)
        Text(labelEn, style = MaterialTheme.typography.labelSmall, color = MaterialTheme.colorScheme.onSurfaceVariant, fontSize = (10 * fontScale).sp)
        Spacer(Modifier.height(4.dp))
        Text(valueTelugu, style = MaterialTheme.typography.titleMedium.copy(fontSize = (16 * fontScale).sp), fontWeight = FontWeight.Bold)
        Text(valueEnglish, style = MaterialTheme.typography.labelSmall.copy(fontSize = (11 * fontScale).sp), color = MaterialTheme.colorScheme.onSurfaceVariant)
    }
}

@Composable
private fun PanchangDetailCard(
    titleTelugu: String, titleEnglish: String, valueTelugu: String, valueEnglish: String,
    subtitle: String? = null, icon: ImageVector, accentColor: androidx.compose.ui.graphics.Color, fontScale: Float = 1f,
) {
    GlassmorphicCard(accentColor = accentColor, cornerRadius = 14.dp, contentPadding = 14.dp) {
        Row(modifier = Modifier.fillMaxWidth(), verticalAlignment = Alignment.CenterVertically) {
            Box(modifier = Modifier.size(36.dp).clip(CircleShape).background(accentColor.copy(alpha = 0.15f)), contentAlignment = Alignment.Center) {
                Icon(icon, null, tint = accentColor, modifier = Modifier.size(20.dp))
            }
            Spacer(Modifier.width(12.dp))
            Column(modifier = Modifier.weight(1f)) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Text(titleTelugu, style = MaterialTheme.typography.titleSmall.copy(fontSize = (14 * fontScale).sp), fontWeight = FontWeight.Bold)
                    Spacer(Modifier.width(6.dp))
                    Text(titleEnglish, style = MaterialTheme.typography.labelSmall.copy(fontSize = (11 * fontScale).sp), color = MaterialTheme.colorScheme.onSurfaceVariant)
                }
                if (subtitle != null) {
                    Spacer(Modifier.height(2.dp))
                    Text(subtitle, style = MaterialTheme.typography.labelSmall.copy(fontSize = (11 * fontScale).sp), color = accentColor.copy(alpha = 0.8f))
                }
            }
            Column(horizontalAlignment = Alignment.End) {
                Text(valueTelugu, style = MaterialTheme.typography.bodyMedium.copy(fontSize = (14 * fontScale).sp), fontWeight = FontWeight.Bold, textAlign = TextAlign.End)
                Text(valueEnglish, style = MaterialTheme.typography.labelSmall.copy(fontSize = (11 * fontScale).sp), color = MaterialTheme.colorScheme.onSurfaceVariant, textAlign = TextAlign.End)
            }
        }
    }
}

@Composable
private fun PanchangDetailCardWithTime(
    titleTelugu: String, titleEnglish: String, valueTelugu: String, valueEnglish: String,
    subtitle: String? = null, endTime: String, icon: ImageVector, accentColor: androidx.compose.ui.graphics.Color, fontScale: Float = 1f,
) {
    GlassmorphicCard(accentColor = accentColor, cornerRadius = 14.dp, contentPadding = 14.dp) {
        Row(modifier = Modifier.fillMaxWidth(), verticalAlignment = Alignment.CenterVertically) {
            Box(modifier = Modifier.size(36.dp).clip(CircleShape).background(accentColor.copy(alpha = 0.15f)), contentAlignment = Alignment.Center) {
                Icon(icon, null, tint = accentColor, modifier = Modifier.size(20.dp))
            }
            Spacer(Modifier.width(12.dp))
            Column(modifier = Modifier.weight(1f)) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Text(titleTelugu, style = MaterialTheme.typography.titleSmall.copy(fontSize = (14 * fontScale).sp), fontWeight = FontWeight.Bold)
                    Spacer(Modifier.width(6.dp))
                    Text(titleEnglish, style = MaterialTheme.typography.labelSmall.copy(fontSize = (11 * fontScale).sp), color = MaterialTheme.colorScheme.onSurfaceVariant)
                }
                if (subtitle != null) {
                    Spacer(Modifier.height(2.dp))
                    Text(subtitle, style = MaterialTheme.typography.labelSmall.copy(fontSize = (11 * fontScale).sp), color = accentColor.copy(alpha = 0.8f))
                }
                Spacer(Modifier.height(2.dp))
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(Icons.Default.Schedule, null, tint = MaterialTheme.colorScheme.onSurfaceVariant, modifier = Modifier.size(12.dp))
                    Spacer(Modifier.width(4.dp))
                    Text("వరకు: $endTime", style = MaterialTheme.typography.labelSmall.copy(fontSize = (11 * fontScale).sp), color = MaterialTheme.colorScheme.onSurfaceVariant)
                }
            }
            Column(horizontalAlignment = Alignment.End) {
                Text(valueTelugu, style = MaterialTheme.typography.bodyMedium.copy(fontSize = (14 * fontScale).sp), fontWeight = FontWeight.Bold, textAlign = TextAlign.End)
                Text(valueEnglish, style = MaterialTheme.typography.labelSmall.copy(fontSize = (11 * fontScale).sp), color = MaterialTheme.colorScheme.onSurfaceVariant, textAlign = TextAlign.End)
            }
        }
    }
}

@Composable
private fun ShubhaTimeCard(slot: TimeSlotInfo, icon: ImageVector, showActive: Boolean) {
    val accentColor = AuspiciousGreen

    GlassmorphicCard(accentColor = accentColor, cornerRadius = 14.dp, contentPadding = 14.dp) {
        Row(modifier = Modifier.fillMaxWidth(), verticalAlignment = Alignment.CenterVertically) {
            Box(modifier = Modifier.size(36.dp).clip(CircleShape).background(accentColor.copy(alpha = 0.15f)), contentAlignment = Alignment.Center) {
                Icon(icon, null, tint = accentColor, modifier = Modifier.size(20.dp))
            }
            Spacer(Modifier.width(12.dp))
            Column(modifier = Modifier.weight(1f)) {
                Text(slot.nameTelugu, style = MaterialTheme.typography.titleSmall, fontWeight = FontWeight.Bold)
                Text(slot.nameEnglish, style = MaterialTheme.typography.labelSmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
                if (showActive && slot.isActive) {
                    Spacer(Modifier.height(2.dp))
                    Text("ప్రస్తుతం శుభ సమయం · Active Now", style = MaterialTheme.typography.labelSmall, fontWeight = FontWeight.SemiBold, color = accentColor)
                }
            }
            Text("${slot.startTime} - ${slot.endTime}", style = MaterialTheme.typography.bodyMedium, fontWeight = FontWeight.Bold, color = accentColor)
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
        animationSpec = infiniteRepeatable(animation = tween(800), repeatMode = RepeatMode.Reverse),
        label = "rahu_alpha",
    )

    GlassmorphicCard(accentColor = accentColor, cornerRadius = 14.dp, contentPadding = 14.dp) {
        Row(modifier = Modifier.fillMaxWidth(), verticalAlignment = Alignment.CenterVertically) {
            Box(modifier = Modifier.size(36.dp).clip(CircleShape).background(accentColor.copy(alpha = 0.15f * pulseAlpha)), contentAlignment = Alignment.Center) {
                Icon(Icons.Default.Warning, null, tint = accentColor.copy(alpha = pulseAlpha), modifier = Modifier.size(20.dp))
            }
            Spacer(Modifier.width(12.dp))
            Column(modifier = Modifier.weight(1f)) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Text("రాహు కాలం", style = MaterialTheme.typography.titleSmall, fontWeight = FontWeight.Bold, color = accentColor)
                    Spacer(Modifier.width(6.dp))
                    Text("Rahu Kaal", style = MaterialTheme.typography.labelSmall, color = accentColor.copy(alpha = 0.7f))
                }
                if (isActiveNow) {
                    Spacer(Modifier.height(2.dp))
                    Text("ప్రస్తుతం రాహు కాలం · Active Now", style = MaterialTheme.typography.labelSmall, fontWeight = FontWeight.SemiBold, color = InauspiciousRed)
                }
            }
            Text("${rahuKaal.startTime} - ${rahuKaal.endTime}", style = MaterialTheme.typography.bodyMedium, fontWeight = FontWeight.Bold, color = accentColor)
        }
    }
}

@Composable
private fun MuhurtaWarningCard(
    titleTelugu: String, titleEnglish: String, startTime: String, endTime: String,
    isActive: Boolean, activeMessage: String, icon: ImageVector,
    activeColor: androidx.compose.ui.graphics.Color, inactiveColor: androidx.compose.ui.graphics.Color,
) {
    val accentColor = if (isActive) activeColor else inactiveColor

    GlassmorphicCard(accentColor = accentColor, cornerRadius = 14.dp, contentPadding = 14.dp) {
        Row(modifier = Modifier.fillMaxWidth(), verticalAlignment = Alignment.CenterVertically) {
            Box(modifier = Modifier.size(36.dp).clip(CircleShape).background(accentColor.copy(alpha = 0.15f)), contentAlignment = Alignment.Center) {
                Icon(icon, null, tint = accentColor, modifier = Modifier.size(20.dp))
            }
            Spacer(Modifier.width(12.dp))
            Column(modifier = Modifier.weight(1f)) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Text(titleTelugu, style = MaterialTheme.typography.titleSmall, fontWeight = FontWeight.Bold, color = accentColor)
                    Spacer(Modifier.width(6.dp))
                    Text(titleEnglish, style = MaterialTheme.typography.labelSmall, color = accentColor.copy(alpha = 0.7f))
                }
                if (isActive) {
                    Spacer(Modifier.height(2.dp))
                    Text(activeMessage, style = MaterialTheme.typography.labelSmall, fontWeight = FontWeight.SemiBold, color = activeColor)
                }
            }
            Text("$startTime - $endTime", style = MaterialTheme.typography.bodyMedium, fontWeight = FontWeight.Bold, color = accentColor)
        }
    }
}

private fun buildPanchangamShareText(panchangam: PanchangamData, city: String): String = buildString {
    val dateLabel = if (panchangam.isToday) "🪷 నేటి పంచాంగం" else "🪷 పంచాంగం"
    appendLine(dateLabel)
    appendLine("━━━━━━━━━━━━━━━━━━━━")
    appendLine()

    // Date & Day
    appendLine("📅 ${panchangam.dateDisplay}")
    appendLine("${panchangam.teluguDay} · ${panchangam.englishDay}")
    appendLine("📍 $city")
    appendLine()

    // Telugu Calendar — natural Telugu format
    appendLine("🗓️ ${panchangam.samvatsara.nameTelugu} నామ సంవత్సరం")
    val masaShort = panchangam.masa.nameTelugu.removeSuffix("ము")
    val rutuShort = panchangam.rutu.nameTelugu.removeSuffix("ము")
    appendLine("${masaShort} మాసం · ${panchangam.ayana.nameTelugu} · ${rutuShort} ఋతువు")
    appendLine()

    // Sun Times
    appendLine("☀️ సూర్యోదయం: ${panchangam.sunTimes.sunrise}")
    appendLine("🌅 సూర్యాస్తమయం: ${panchangam.sunTimes.sunset}")
    appendLine()

    // Panchanga — the 5 angas
    appendLine("📿 పంచాంగ వివరాలు:")
    appendLine("  తిథి: ${panchangam.tithi.nameTelugu} (${panchangam.tithi.nameEnglish})")
    appendLine("    ${panchangam.tithi.pakshaTelugu} (${panchangam.tithi.paksha}) · వరకు ${panchangam.tithi.endTime}")
    appendLine("  నక్షత్రం: ${panchangam.nakshatra.nameTelugu} (${panchangam.nakshatra.nameEnglish})")
    appendLine("    వరకు ${panchangam.nakshatra.endTime}")
    appendLine("  యోగం: ${panchangam.yoga.nameTelugu} (${panchangam.yoga.nameEnglish})")
    appendLine("    వరకు ${panchangam.yoga.endTime}")
    appendLine("  కరణం: ${panchangam.karana.firstNameTelugu} (${panchangam.karana.firstNameEnglish})")
    appendLine("    2వ: ${panchangam.karana.secondNameTelugu} (${panchangam.karana.secondNameEnglish})")
    appendLine()

    // Rashi
    appendLine("🌞 సూర్య రాశి: ${panchangam.sunRashi.nameTelugu} (${panchangam.sunRashi.nameEnglish})")
    appendLine("🌙 చంద్ర రాశి: ${panchangam.moonRashi.nameTelugu} (${panchangam.moonRashi.nameEnglish})")
    appendLine()

    // Shubha times
    appendLine("✅ శుభ సమయాలు:")
    appendLine("  బ్రహ్మ ముహూర్తం: ${panchangam.brahmaMuhurta.startTime} - ${panchangam.brahmaMuhurta.endTime}")
    appendLine("  అభిజిత్ ముహూర్తం: ${panchangam.abhijitMuhurt.startTime} - ${panchangam.abhijitMuhurt.endTime}")
    panchangam.shubhHoras.forEach { hora ->
        appendLine("  ${hora.nameTelugu} హోర: ${hora.startTime} - ${hora.endTime}")
    }
    appendLine()

    // Nishiddha times
    appendLine("⛔ నిషిద్ధ సమయాలు:")
    appendLine("  రాహు కాలం: ${panchangam.rahuKaal.startTime} - ${panchangam.rahuKaal.endTime}")
    appendLine("  యమగండం: ${panchangam.yamagandam.startTime} - ${panchangam.yamagandam.endTime}")
    appendLine("  గుళిక కాలం: ${panchangam.gulikaKalam.startTime} - ${panchangam.gulikaKalam.endTime}")
    appendLine()
    append("Shared via NityaPooja")
}
