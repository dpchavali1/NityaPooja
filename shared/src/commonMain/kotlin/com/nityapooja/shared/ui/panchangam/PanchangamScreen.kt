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
import androidx.compose.material.icons.automirrored.filled.ArrowForward
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.foundation.isSystemInDarkTheme
import com.nityapooja.shared.data.grahanam.GrahanamRepository
import com.nityapooja.shared.data.grahanam.GrahanamType
import com.nityapooja.shared.data.grahanam.toLocalFormatted
import com.nityapooja.shared.ui.theme.MoonPhaseDark
import com.nityapooja.shared.ui.theme.MoonPhaseLight
import kotlinx.datetime.Clock
import kotlinx.datetime.TimeZone
import kotlinx.datetime.toLocalDateTime
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
import androidx.compose.foundation.clickable
import com.nityapooja.shared.ui.components.FontSizeControls
import com.nityapooja.shared.ui.components.FontSizeViewModel
import com.nityapooja.shared.ui.components.GlassmorphicCard
import com.nityapooja.shared.ui.components.PlaceSearchField
import com.nityapooja.shared.ui.components.SectionHeader
import com.nityapooja.shared.ui.theme.*
import com.nityapooja.shared.platform.shareText

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun PanchangamScreen(
    viewModel: PanchangamViewModel = koinViewModel(),
    fontSizeViewModel: FontSizeViewModel = koinViewModel(),
    bannerAd: (@Composable () -> Unit)? = null,
) {
    val locationInfo by viewModel.locationInfo.collectAsState()
    val selectedDate by viewModel.selectedDate.collectAsState()
    val fontSize by fontSizeViewModel.fontSize.collectAsState()
    val fontScale = fontSize / 16f

    var showDatePicker by remember { mutableStateOf(false) }
    var showLocationPicker by remember { mutableStateOf(false) }

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
                            Row(
                                verticalAlignment = Alignment.CenterVertically,
                                modifier = Modifier.clickable {
                                    showLocationPicker = true
                                },
                            ) {
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
                                Spacer(Modifier.width(4.dp))
                                Icon(
                                    Icons.Default.Edit,
                                    contentDescription = "Change location",
                                    tint = TempleGold.copy(alpha = 0.6f),
                                    modifier = Modifier.size(12.dp),
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

            bannerAd?.invoke()

            // ═══════════════════════════════════════════
            // Panchangam Details (5 Angas)
            // ═══════════════════════════════════════════
            SectionHeader(titleTelugu = "పంచాంగ వివరాలు", titleEnglish = "Panchang Details")

            PanchangDetailCardWithTime("తిథి", "Tithi", panchangam.tithi.nameTelugu, panchangam.tithi.nameEnglish,
                "${panchangam.tithi.pakshaTelugu} (${panchangam.tithi.paksha})", panchangam.tithi.endTime,
                Icons.Default.Brightness2, TempleGold, fontScale,
                nextValueTelugu = panchangam.tithi.nextNameTelugu, nextValueEnglish = panchangam.tithi.nextNameEnglish)

            PanchangDetailCardWithTime("నక్షత్రం", "Nakshatra", panchangam.nakshatra.nameTelugu, panchangam.nakshatra.nameEnglish,
                endTime = panchangam.nakshatra.endTime, icon = Icons.Default.Star, accentColor = TempleGold, fontScale = fontScale,
                nextValueTelugu = panchangam.nakshatra.nextNameTelugu, nextValueEnglish = panchangam.nakshatra.nextNameEnglish)

            PanchangDetailCardWithTime("యోగం", "Yoga", panchangam.yoga.nameTelugu, panchangam.yoga.nameEnglish,
                endTime = panchangam.yoga.endTime, icon = Icons.Default.AllInclusive, accentColor = TempleGold, fontScale = fontScale,
                nextValueTelugu = panchangam.yoga.nextNameTelugu, nextValueEnglish = panchangam.yoga.nextNameEnglish)

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

            // ═══════════════════════════════════════════
            // Moon Phases
            // ═══════════════════════════════════════════
            val moonPhases = remember(locationInfo) {
                viewModel.calculateUpcomingMoonPhases(locationInfo.timezone)
            }

            SectionHeader(titleTelugu = "చంద్ర కళలు", titleEnglish = "Moon Phases")
            MoonPhasesCard(phases = moonPhases, fontScale = fontScale)

            // ═══════════════════════════════════════════
            // Upcoming Grahanam
            // ═══════════════════════════════════════════
            val now = remember { Clock.System.now() }
            val userTz = remember(locationInfo.timezone) {
                try { TimeZone.of(locationInfo.timezone) } catch (_: Exception) { TimeZone.of("Asia/Kolkata") }
            }
            val nextGrahanam = remember(now) { GrahanamRepository.getNextGrahanam(now) }

            SectionHeader(titleTelugu = "తదుపరి గ్రహణం", titleEnglish = "Upcoming Grahanam")
            UpcomingGrahanamCard(nextGrahanam = nextGrahanam, now = now, tz = userTz, fontScale = fontScale)

            Spacer(Modifier.height(60.dp))
        }
    }

    // Location Picker Dialog
    if (showLocationPicker) {
        AlertDialog(
            onDismissRequest = { showLocationPicker = false },
            title = {
                Text("ప్రదేశం మార్చండి / Change Location", style = MaterialTheme.typography.titleMedium)
            },
            text = {
                PlaceSearchField(
                    currentCity = locationInfo.city,
                    currentLat = locationInfo.lat,
                    currentLng = locationInfo.lng,
                    onPlaceSelected = { city, lat, lng, timezoneId, _ ->
                        viewModel.setLocation(city, lat, lng, timezoneId)
                        showLocationPicker = false
                    },
                )
            },
            confirmButton = {
                TextButton(onClick = { showLocationPicker = false }) {
                    Text("Cancel")
                }
            },
        )
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
            Text(valueTelugu, style = MaterialTheme.typography.bodyMedium.copy(fontSize = (14 * fontScale).sp), fontWeight = FontWeight.Bold)
            Text(valueEnglish, style = MaterialTheme.typography.labelSmall.copy(fontSize = (11 * fontScale).sp), color = MaterialTheme.colorScheme.onSurfaceVariant)
        }
        Column(horizontalAlignment = Alignment.End) {
            Text(labelTelugu, style = MaterialTheme.typography.labelMedium, color = TempleGold, fontWeight = FontWeight.Bold)
            Text(labelEnglish, style = MaterialTheme.typography.labelSmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
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
    nextValueTelugu: String? = null, nextValueEnglish: String? = null,
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
                if (nextValueTelugu != null && nextValueEnglish != null) {
                    Spacer(Modifier.height(2.dp))
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(Icons.AutoMirrored.Filled.ArrowForward, null, tint = accentColor.copy(alpha = 0.6f), modifier = Modifier.size(12.dp))
                        Spacer(Modifier.width(4.dp))
                        Text("తర్వాత: $nextValueTelugu ($nextValueEnglish)", style = MaterialTheme.typography.labelSmall.copy(fontSize = (10 * fontScale).sp), color = accentColor.copy(alpha = 0.7f))
                    }
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

@Composable
private fun MoonPhasesCard(
    phases: List<MoonPhaseEvent>,
    fontScale: Float = 1f,
) {
    val accent = if (isSystemInDarkTheme()) MoonPhaseLight else MoonPhaseDark

    GlassmorphicCard(accentColor = accent, cornerRadius = 14.dp, contentPadding = 16.dp) {
        if (phases.isEmpty()) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Text("🌕", style = MaterialTheme.typography.titleLarge)
                Spacer(Modifier.width(12.dp))
                Text(
                    "అందుబాటులో లేదు / Unavailable",
                    style = MaterialTheme.typography.bodyMedium.copy(fontSize = (14 * fontScale).sp),
                    color = accent,
                )
            }
        } else {
            Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
                phases.forEachIndexed { index, phase ->
                    if (index > 0) HorizontalDivider(color = accent.copy(alpha = 0.2f))
                    MoonPhaseRow(phase = phase, accent = accent, fontScale = fontScale)
                }
            }
        }
    }
}

@Composable
private fun MoonPhaseRow(
    phase: MoonPhaseEvent,
    accent: androidx.compose.ui.graphics.Color,
    fontScale: Float,
) {
    val isPournami = phase.type == MoonPhaseType.POURNAMI
    val emoji = if (isPournami) "🌕" else "🌑"
    val nameTelugu = if (isPournami) "పౌర్ణమి" else "అమావాస్య"
    val nameEnglish = if (isPournami) "Pournami" else "Amavasya"
    val significance = if (isPournami)
        "సత్యనారాయణ వ్రతం, విష్ణు పూజ, వ్రత నిర్వహణ"
    else
        "పితృ తర్పణం, పూర్వుల స్మరణ, పిండ ప్రదానం"

    Row(
        modifier = Modifier.fillMaxWidth(),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        Text(emoji, style = MaterialTheme.typography.headlineMedium)
        Spacer(Modifier.width(12.dp))
        Column(modifier = Modifier.weight(1f)) {
            Text(
                nameTelugu,
                style = MaterialTheme.typography.titleMedium.copy(fontSize = (15 * fontScale).sp),
                fontWeight = FontWeight.Bold,
                color = accent,
            )
            Text(
                "$nameEnglish · ${phase.dateDisplay}",
                style = MaterialTheme.typography.bodySmall.copy(fontSize = (12 * fontScale).sp),
                color = MaterialTheme.colorScheme.onSurfaceVariant,
            )
            Text(
                "${phase.masaNameTelugu} · ${phase.masaNameEnglish}",
                style = MaterialTheme.typography.labelSmall.copy(fontSize = (11 * fontScale).sp),
                color = accent.copy(alpha = 0.7f),
            )
            Text(
                significance,
                style = MaterialTheme.typography.labelSmall.copy(fontSize = (10 * fontScale).sp),
                color = MaterialTheme.colorScheme.onSurfaceVariant,
            )
        }
        Column(horizontalAlignment = Alignment.End) {
            Text(
                when (phase.daysUntil) {
                    0 -> "నేడు"
                    1 -> "రేపు"
                    else -> "${phase.daysUntil}d"
                },
                style = MaterialTheme.typography.headlineSmall.copy(fontSize = (20 * fontScale).sp),
                fontWeight = FontWeight.Bold,
                color = accent,
            )
            Text(
                phase.timeFormatted,
                style = MaterialTheme.typography.labelSmall.copy(fontSize = (11 * fontScale).sp),
                color = MaterialTheme.colorScheme.onSurfaceVariant,
            )
        }
    }
}

@Composable
private fun UpcomingGrahanamCard(
    nextGrahanam: com.nityapooja.shared.data.grahanam.GrahanamData?,
    now: kotlinx.datetime.Instant,
    tz: TimeZone,
    fontScale: Float = 1f,
) {
    val grahanamAccent = if (isSystemInDarkTheme()) GrahanamLight else GrahanamDark

    GlassmorphicCard(accentColor = grahanamAccent, cornerRadius = 14.dp, contentPadding = 14.dp) {
        if (nextGrahanam == null) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Icon(Icons.Default.NightsStay, null, tint = grahanamAccent, modifier = Modifier.size(24.dp))
                Spacer(Modifier.width(12.dp))
                Text(
                    "సమీప భవిష్యత్తులో గ్రహణం లేదు",
                    style = MaterialTheme.typography.bodyMedium.copy(fontSize = (13 * fontScale).sp),
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                )
            }
        } else {
            val localSparsha = nextGrahanam.sparthaUtc.toLocalDateTime(tz)
            val daysUntil = (nextGrahanam.sparthaUtc.toLocalDateTime(tz).date.toEpochDays() -
                    now.toLocalDateTime(tz).date.toEpochDays()).toInt()
            val typeNameTelugu = if (nextGrahanam.type == GrahanamType.SURYA) "సూర్య గ్రహణం" else "చంద్ర గ్రహణం"
            val typeNameEnglish = if (nextGrahanam.type == GrahanamType.SURYA) "Surya Grahanam" else "Chandra Grahanam"
            val dateStr = "${localSparsha.dayOfMonth}/${localSparsha.monthNumber}/${localSparsha.year}"

            Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Box(
                        modifier = Modifier.size(36.dp).clip(CircleShape).background(grahanamAccent.copy(alpha = 0.15f)),
                        contentAlignment = Alignment.Center,
                    ) {
                        Icon(Icons.Default.NightsStay, null, tint = grahanamAccent, modifier = Modifier.size(20.dp))
                    }
                    Spacer(Modifier.width(12.dp))
                    Column(modifier = Modifier.weight(1f)) {
                        Text(typeNameTelugu, style = MaterialTheme.typography.titleSmall.copy(fontSize = (14 * fontScale).sp), fontWeight = FontWeight.Bold)
                        Text(typeNameEnglish, style = MaterialTheme.typography.labelSmall.copy(fontSize = (11 * fontScale).sp), color = MaterialTheme.colorScheme.onSurfaceVariant)
                    }
                    Column(horizontalAlignment = Alignment.End) {
                        Text(
                            when {
                                daysUntil == 0 -> "నేడు!"
                                daysUntil == 1 -> "రేపు"
                                else -> "$daysUntil రోజులు"
                            },
                            style = MaterialTheme.typography.titleMedium.copy(fontSize = (16 * fontScale).sp),
                            fontWeight = FontWeight.Bold,
                            color = grahanamAccent,
                        )
                        Text(dateStr, style = MaterialTheme.typography.labelSmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
                    }
                }

                HorizontalDivider(color = grahanamAccent.copy(alpha = 0.2f))

                Column(verticalArrangement = Arrangement.spacedBy(4.dp)) {
                    GrahanamTimeRow("స్పర్శ", "Sparsha", nextGrahanam.sparthaUtc.toLocalFormatted(tz), grahanamAccent, fontScale)
                    GrahanamTimeRow("మధ్యం", "Madhyam", nextGrahanam.madhyamUtc.toLocalFormatted(tz), grahanamAccent, fontScale)
                    GrahanamTimeRow("మోక్షం", "Moksham", nextGrahanam.mokshamUtc.toLocalFormatted(tz), grahanamAccent, fontScale)
                    Text(
                        "పుణ్యకాలం: ${nextGrahanam.punyakalaMinutes} నిమిషాలు / Punyakalam: ${nextGrahanam.punyakalaMinutes} min",
                        style = MaterialTheme.typography.labelSmall.copy(fontSize = (11 * fontScale).sp),
                        color = grahanamAccent.copy(alpha = 0.8f),
                    )
                }

                if (nextGrahanam.type == GrahanamType.SURYA && !nextGrahanam.visibleFromIndia) {
                    Text(
                        "మీ ప్రాంతంలో దృశ్యమానం కాదు / May not be visible from your region",
                        style = MaterialTheme.typography.labelSmall.copy(fontSize = (11 * fontScale).sp),
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                    )
                }
            }
        }
    }
}

@Composable
private fun GrahanamTimeRow(
    labelTelugu: String,
    labelEnglish: String,
    time: String,
    accentColor: androidx.compose.ui.graphics.Color,
    fontScale: Float = 1f,
) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically,
    ) {
        Row {
            Text(labelTelugu, style = MaterialTheme.typography.labelMedium.copy(fontSize = (12 * fontScale).sp), color = accentColor, fontWeight = FontWeight.Bold)
            Spacer(Modifier.width(4.dp))
            Text("($labelEnglish)", style = MaterialTheme.typography.labelSmall.copy(fontSize = (11 * fontScale).sp), color = MaterialTheme.colorScheme.onSurfaceVariant)
        }
        Text(time, style = MaterialTheme.typography.bodyMedium.copy(fontSize = (13 * fontScale).sp), fontWeight = FontWeight.SemiBold)
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
    appendLine("    → తర్వాత: ${panchangam.tithi.nextNameTelugu} (${panchangam.tithi.nextNameEnglish})")
    appendLine("  నక్షత్రం: ${panchangam.nakshatra.nameTelugu} (${panchangam.nakshatra.nameEnglish})")
    appendLine("    వరకు ${panchangam.nakshatra.endTime}")
    appendLine("    → తర్వాత: ${panchangam.nakshatra.nextNameTelugu} (${panchangam.nakshatra.nextNameEnglish})")
    appendLine("  యోగం: ${panchangam.yoga.nameTelugu} (${panchangam.yoga.nameEnglish})")
    appendLine("    వరకు ${panchangam.yoga.endTime}")
    appendLine("    → తర్వాత: ${panchangam.yoga.nextNameTelugu} (${panchangam.yoga.nextNameEnglish})")
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
