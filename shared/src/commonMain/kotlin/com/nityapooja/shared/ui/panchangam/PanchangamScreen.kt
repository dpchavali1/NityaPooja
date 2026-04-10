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
import kotlinx.datetime.LocalDate
import kotlinx.datetime.TimeZone
import kotlinx.datetime.todayIn
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
    var showPanchangamInfo by remember { mutableStateOf(false) }
    var showTithiInfo by remember { mutableStateOf(false) }
    var showNakshatraInfo by remember { mutableStateOf(false) }
    var showYogaInfo by remember { mutableStateOf(false) }

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
                    IconButton(onClick = { showPanchangamInfo = true }) {
                        Icon(Icons.Default.Info, "What is Panchangam?", tint = TempleGold)
                    }
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
                    modifier = Modifier.size(48.dp),
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
                    modifier = Modifier.size(48.dp),
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
            // Today at a Glance
            // ═══════════════════════════════════════════
            run {
                val tithiIdx = panchangam.tithi.index  // 0-based (0=Prathama..29=Amavasya)
                val nakshatraIdx = panchangam.nakshatra.index  // 0-based
                val yogaIdx = panchangam.yoga.index  // 0-based

                // Rikta tithis by index (Chaturthi=3, Ashtami=7, Trayodashi=12 in Shukla;
                // Krishna Chaturthi=18, Krishna Ashtami=22, Krishna Trayodashi=27)
                val rikta = tithiIdx in setOf(3, 7, 12, 18, 22, 27)
                // Avoid nakshatras by index: Ardra=5, Ashlesha=8, Magha=9, Jyeshtha=17, Moola=18
                val avoidNakshatra = nakshatraIdx in setOf(5, 8, 9, 17, 18)
                // Avoid yogas by index: Vyatipata=16, Vaidhriti=26
                val avoidYoga = yogaIdx in setOf(16, 26)

                val (chipText, chipColor) = when {
                    rikta && avoidNakshatra -> "జాగ్రత్త · Caution — Tithi + Nakshatra unfavorable" to DeepVermillion
                    rikta -> "తిథి జాగ్రత్త · Rikta Tithi — avoid important starts" to SacredTurmeric
                    avoidNakshatra -> "నక్షత్ర జాగ్రత్త · Nakshatra needs care today" to SacredTurmeric
                    avoidYoga -> "యోగ జాగ్రత్త · ${panchangam.yoga.nameEnglish} — avoid major activities" to SacredTurmeric
                    else -> "శుభ దినం · Auspicious day" to AuspiciousGreen
                }

                Surface(
                    shape = RoundedCornerShape(20.dp),
                    color = chipColor.copy(alpha = 0.12f),
                    modifier = Modifier.fillMaxWidth(),
                ) {
                    Text(
                        chipText,
                        modifier = Modifier.padding(horizontal = 16.dp, vertical = 6.dp),
                        style = MaterialTheme.typography.labelMedium.copy(fontSize = (12 * fontScale).sp),
                        color = chipColor,
                        fontWeight = FontWeight.SemiBold,
                        textAlign = TextAlign.Center,
                    )
                }
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
            TextButton(
                onClick = { showTithiInfo = true },
                modifier = Modifier.align(Alignment.End).padding(end = 4.dp),
                contentPadding = PaddingValues(horizontal = 8.dp, vertical = 2.dp),
            ) {
                Icon(Icons.Default.Info, null, modifier = Modifier.size(12.dp), tint = TempleGold.copy(alpha = 0.7f))
                Spacer(Modifier.width(4.dp))
                Text("తిథి గురించి · About Tithi", style = MaterialTheme.typography.labelSmall, color = TempleGold.copy(alpha = 0.7f))
            }

            PanchangDetailCardWithTime("నక్షత్రం", "Nakshatra", panchangam.nakshatra.nameTelugu, panchangam.nakshatra.nameEnglish,
                endTime = panchangam.nakshatra.endTime, icon = Icons.Default.Star, accentColor = TempleGold, fontScale = fontScale,
                nextValueTelugu = panchangam.nakshatra.nextNameTelugu, nextValueEnglish = panchangam.nakshatra.nextNameEnglish)
            TextButton(
                onClick = { showNakshatraInfo = true },
                modifier = Modifier.align(Alignment.End).padding(end = 4.dp),
                contentPadding = PaddingValues(horizontal = 8.dp, vertical = 2.dp),
            ) {
                Icon(Icons.Default.Info, null, modifier = Modifier.size(12.dp), tint = TempleGold.copy(alpha = 0.7f))
                Spacer(Modifier.width(4.dp))
                Text("నక్షత్రం గురించి · About Nakshatra", style = MaterialTheme.typography.labelSmall, color = TempleGold.copy(alpha = 0.7f))
            }

            PanchangDetailCardWithTime("యోగం", "Yoga", panchangam.yoga.nameTelugu, panchangam.yoga.nameEnglish,
                endTime = panchangam.yoga.endTime, icon = Icons.Default.AllInclusive, accentColor = TempleGold, fontScale = fontScale,
                nextValueTelugu = panchangam.yoga.nextNameTelugu, nextValueEnglish = panchangam.yoga.nextNameEnglish)
            TextButton(
                onClick = { showYogaInfo = true },
                modifier = Modifier.align(Alignment.End).padding(end = 4.dp),
                contentPadding = PaddingValues(horizontal = 8.dp, vertical = 2.dp),
            ) {
                Icon(Icons.Default.Info, null, modifier = Modifier.size(12.dp), tint = TempleGold.copy(alpha = 0.7f))
                Spacer(Modifier.width(4.dp))
                Text("యోగం గురించి · About Yoga", style = MaterialTheme.typography.labelSmall, color = TempleGold.copy(alpha = 0.7f))
            }

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

            // Rahu Kalam always-visible explanation for young users
            Text(
                "ప్రతిరోజు 90 నిమిషాలు అశుభ సమయం — కొత్త పనులు మొదలుపెట్టవద్దు · 90-min inauspicious window daily — avoid starting new activities",
                style = MaterialTheme.typography.labelSmall.copy(fontSize = (10 * fontScale).sp),
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                modifier = Modifier.padding(horizontal = 2.dp),
            )
            RahuKaalCard(rahuKaal = panchangam.rahuKaal, showActive = panchangam.isToday)
            // Rahu Kalam countdown — shown only for today
            if (panchangam.isToday) {
                val rahuKaal = panchangam.rahuKaal
                run {
                    // Parse "H:MM AM/PM" strings to decimal hours for countdown
                    fun parseToDecimalHour(timeStr: String): Double? {
                        return try {
                            val parts = timeStr.trim().split(" ")
                            val isPm = parts.getOrNull(1)?.uppercase() == "PM"
                            val isAm = parts.getOrNull(1)?.uppercase() == "AM"
                            val hmParts = parts[0].split(":")
                            var hour = hmParts[0].toInt()
                            val minute = hmParts.getOrNull(1)?.toInt() ?: 0
                            if (isPm && hour != 12) hour += 12
                            if (isAm && hour == 12) hour = 0
                            hour + minute / 60.0
                        } catch (_: Exception) { null }
                    }

                    val nowLocal = Clock.System.now().toLocalDateTime(
                        try { TimeZone.of(locationInfo.timezone) } catch (_: Exception) { TimeZone.of("Asia/Kolkata") }
                    )
                    val currentDecimal = nowLocal.hour + nowLocal.minute / 60.0

                    val startDec = parseToDecimalHour(rahuKaal.startTime)
                    val endDec = parseToDecimalHour(rahuKaal.endTime)

                    if (startDec != null && endDec != null) {
                        when {
                            currentDecimal in startDec..endDec -> {
                                val minsLeft = ((endDec - currentDecimal) * 60).toInt()
                                Text(
                                    "రాహు కాలంలో ఉన్నారు — $minsLeft నిమిషాలు మిగిలాయి · Currently in Rahu Kalam — $minsLeft min remaining",
                                    style = MaterialTheme.typography.labelSmall.copy(fontSize = (10 * fontScale).sp),
                                    color = InauspiciousRed,
                                    fontWeight = FontWeight.SemiBold,
                                    modifier = Modifier.padding(horizontal = 2.dp),
                                )
                            }
                            currentDecimal < startDec -> {
                                val totalMins = ((startDec - currentDecimal) * 60).toInt()
                                val hrs = totalMins / 60
                                val mins = totalMins % 60
                                val timeLabel = if (hrs > 0) "${hrs}గ గ. ${mins}ని లో · in ${hrs}h ${mins}m" else "${mins}ని లో · in ${mins}m"
                                Text(
                                    "రాహు కాలం $timeLabel",
                                    style = MaterialTheme.typography.labelSmall.copy(fontSize = (10 * fontScale).sp),
                                    color = WarningAmber,
                                    modifier = Modifier.padding(horizontal = 2.dp),
                                )
                            }
                            // Past for today — show nothing
                        }
                    }
                }
            }

            MuhurtaWarningCard("యమగండం", "Yamagandam", panchangam.yamagandam.startTime, panchangam.yamagandam.endTime,
                panchangam.isToday && panchangam.yamagandam.isActive, "ప్రస్తుతం యమగండం · Active Now",
                Icons.Default.DoNotDisturb, InauspiciousRed, WarningAmber)

            MuhurtaWarningCard("గుళిక కాలం", "Gulika Kalam", panchangam.gulikaKalam.startTime, panchangam.gulikaKalam.endTime,
                panchangam.isToday && panchangam.gulikaKalam.isActive, "ప్రస్తుతం గుళిక కాలం · Active Now",
                Icons.Default.RemoveCircleOutline, InauspiciousRed, WarningAmber)

            // ═══════════════════════════════════════════
            // Month Calendar
            // ═══════════════════════════════════════════
            SectionHeader(titleTelugu = "నెల క్యాలెండర్", titleEnglish = "Month Calendar")
            MonthCalendarCard(
                locationInfo = locationInfo,
                selectedDate = selectedDate,
                viewModel = viewModel,
            )

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

    // Panchangam info bottom sheet
    if (showPanchangamInfo) {
        com.nityapooja.shared.ui.components.InfoBottomSheet(
            titleTelugu = "పంచాంగం అంటే ఏమిటి?",
            titleEnglish = "What is Panchangam?",
            bodyTelugu = "పంచాంగం అనేది హిందూ పంచాంగ విధానం. ఐదు అంగాలు — తిథి, నక్షత్రం, యోగం, కరణం, వారం — రోజు యొక్క స్వభావాన్ని వివరిస్తాయి. తెలుగు కుటుంబాలు ముఖ్యమైన కార్యాలకు ముందు పంచాంగం చూస్తాయి.",
            bodyEnglish = "Panchangam is the Hindu almanac describing each day through five elements: Tithi (lunar date), Nakshatra (star), Yoga (planetary combination), Karana (half-day), and Vara (weekday). Telugu families consult it before weddings, travel, business, and ceremonies.",
            whyItMatters = "రాహు కాలం, అభిజిత్ ముహూర్తం, సూర్యోదయ సమయాలు ఈ పేజీలో ఉన్నాయి — రోజు ప్రారంభంలో ఒకసారి చూడటం మంచి అలవాటు. · Rahu Kalam, Abhijit Muhurtam, and sunrise times are all here — checking once each morning is a good daily habit.",
            tips = listOf(
                "పూర్ణిమ, ఏకాదశి తిథులు ఉపవాసానికి శ్రేష్ఠమైనవి · Purnima and Ekadashi are ideal for fasting",
                "రాహు కాలంలో కొత్త పనులు ప్రారంభించవద్దు · Avoid starting new activities during Rahu Kalam",
                "అభిజిత్ ముహూర్తం సాధారణంగా మధ్యాహ్నం — అన్నింటికీ అనుకూలం · Abhijit Muhurtam (midday) is auspicious for all activities",
            ),
            onDismiss = { showPanchangamInfo = false },
        )
    }

    // Tithi info bottom sheet
    if (showTithiInfo) {
        com.nityapooja.shared.ui.components.InfoBottomSheet(
            titleTelugu = "తిథి అంటే ఏమిటి?",
            titleEnglish = "What is Tithi?",
            bodyTelugu = "తిథి అనేది చంద్ర తేదీ. సూర్యుడు మరియు చంద్రుడు మధ్య కోణీయ దూరం 12 డిగ్రీలు అయినప్పుడు ఒక తిథి అవుతుంది. నెలలో 30 తిథులు ఉంటాయి.",
            bodyEnglish = "Tithi is the lunar date. The Moon moves ~12° per day relative to the Sun, and each 12° is one Tithi. There are 30 Tithis per lunar month — 15 in the waxing phase (Shukla) and 15 in the waning phase (Krishna).",
            whyItMatters = "ఏకాదశి, పూర్ణిమ, అమావాస్య తిథులు ఉపవాసానికి, పూజలకు ప్రత్యేకమైనవి. · Ekadashi, Purnima, and Amavasya are sacred for fasting and special worship.",
            onDismiss = { showTithiInfo = false },
        )
    }

    // Nakshatra info bottom sheet
    if (showNakshatraInfo) {
        com.nityapooja.shared.ui.components.InfoBottomSheet(
            titleTelugu = "నక్షత్రం అంటే ఏమిటి?",
            titleEnglish = "What is Nakshatra?",
            bodyTelugu = "నక్షత్రం అంటే చంద్రుని స్థానం ఆధారంగా రోజు యొక్క నక్షత్రం. ఆకాశంలో 27 నక్షత్ర గుచ్ఛాలు ఉన్నాయి. మీరు పుట్టినప్పుడు చంద్రుడు ఉన్న నక్షత్రం మీ జన్మ నక్షత్రం.",
            bodyEnglish = "Nakshatra is the lunar mansion — the star cluster the Moon occupies today. There are 27 Nakshatras. The one the Moon was in at your birth is your Janma Nakshatra (birth star), which plays a key role in Vedic astrology.",
            whyItMatters = "మీ జన్మ నక్షత్రం నేటి నక్షత్రంతో అనుకూలంగా ఉంటే — శుభ కార్యాలు ప్రారంభించడానికి మంచి రోజు. · When today's Nakshatra is favorable to your birth star, it's a good day for new endeavors.",
            onDismiss = { showNakshatraInfo = false },
        )
    }

    // Yoga info bottom sheet
    if (showYogaInfo) {
        com.nityapooja.shared.ui.components.InfoBottomSheet(
            titleTelugu = "యోగం అంటే ఏమిటి?",
            titleEnglish = "What is Yoga (in Panchangam)?",
            bodyTelugu = "ఇక్కడ 'యోగం' అంటే వ్యాయామం కాదు — సూర్యుడు మరియు చంద్రుని రాశి స్థానాల మొత్తం ఆధారంగా లెక్కించే జ్యోతిష్య సూచిక. 27 యోగాలు ఉంటాయి.",
            bodyEnglish = "In Panchangam, Yoga is not exercise — it's an astrological quality index calculated by adding the Sun and Moon's positions. 27 Yogas each carry a different quality: Siddhi and Amrit are highly auspicious; Vyatipata and Vaidhriti are inauspicious.",
            whyItMatters = "సిద్ధి, అమృత యోగాలు — అన్ని కార్యాలకు అనుకూలం. వ్యతీపాత, వైధృతి యోగాలలో ముఖ్యమైన పనులు నివారించాలి. · Siddhi and Amrit Yogas are excellent; avoid important activities on Vyatipata and Vaidhriti.",
            onDismiss = { showYogaInfo = false },
        )
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

// ════════════════════════════════════════════════════════════
// Month Calendar Card
// ════════════════════════════════════════════════════════════

@Composable
private fun MonthCalendarCard(
    locationInfo: LocationInfo,
    selectedDate: SelectedDate?,
    viewModel: PanchangamViewModel,
) {
    val tz = remember(locationInfo.timezone) {
        try { TimeZone.of(locationInfo.timezone) } catch (_: Exception) { TimeZone.of("Asia/Kolkata") }
    }
    val today = remember(tz) { Clock.System.todayIn(tz) }
    // Show the month of the selected date, or today's month
    val displayMonth = remember(selectedDate, today) {
        if (selectedDate != null) LocalDate(selectedDate.year, selectedDate.month, 1)
        else LocalDate(today.year, today.monthNumber, 1)
    }

    // Compute first day of month + days in month
    val firstDayOfMonth = displayMonth
    val daysInMonth = remember(displayMonth) { daysInMonth(displayMonth.year, displayMonth.monthNumber) }
    // dayOfWeek for firstDayOfMonth: Mon=1..Sun=7 → we want Sun=0..Sat=6
    val startOffset = remember(firstDayOfMonth) {
        val dow = firstDayOfMonth.dayOfWeek.ordinal // Mon=0..Sun=6
        (dow + 1) % 7 // Sun=0..Sat=6
    }

    // Compute tithis for all days in the month (pure calc, fast)
    val tithis = remember(locationInfo, displayMonth) {
        (1..daysInMonth).map { day ->
            val sd = SelectedDate(displayMonth.year, displayMonth.monthNumber, day)
            val p = viewModel.calculatePanchangam(locationInfo.lat, locationInfo.lng, locationInfo.timezone, sd)
            p.tithi.index // 0-29
        }
    }

    val monthNames = arrayOf("January","February","March","April","May","June",
        "July","August","September","October","November","December")
    val teluguMonthNames = arrayOf("జనవరి","ఫిబ్రవరి","మార్చి","ఏప్రిల్","మే","జూన్",
        "జూలై","ఆగస్టు","సెప్టెంబర్","అక్టోబర్","నవంబర్","డిసెంబర్")
    val dayHeaders = arrayOf("ఆ", "సో", "మం", "బు", "గు", "శు", "శ")

    GlassmorphicCard(cornerRadius = 16.dp, contentPadding = 12.dp) {
        // Month title + navigation row
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.Center,
            verticalAlignment = Alignment.CenterVertically,
        ) {
            Text(
                "${teluguMonthNames[displayMonth.monthNumber - 1]} ${displayMonth.year}  ·  ${monthNames[displayMonth.monthNumber - 1]}",
                style = MaterialTheme.typography.titleSmall,
                fontWeight = FontWeight.SemiBold,
                color = TempleGold,
            )
        }

        Spacer(Modifier.height(8.dp))

        // Day-of-week header
        Row(modifier = Modifier.fillMaxWidth()) {
            dayHeaders.forEach { h ->
                Box(modifier = Modifier.weight(1f), contentAlignment = Alignment.Center) {
                    Text(h, style = MaterialTheme.typography.labelSmall,
                        fontWeight = FontWeight.Bold,
                        color = if (h == "ఆ") InauspiciousRed else MaterialTheme.colorScheme.onSurfaceVariant)
                }
            }
        }

        Spacer(Modifier.height(4.dp))

        // Calendar cells
        val totalCells = startOffset + daysInMonth
        val rows = (totalCells + 6) / 7

        repeat(rows) { row ->
            Row(modifier = Modifier.fillMaxWidth()) {
                repeat(7) { col ->
                    val cellIndex = row * 7 + col
                    val day = cellIndex - startOffset + 1
                    val isValidDay = day in 1..daysInMonth

                    Box(
                        modifier = Modifier
                            .weight(1f)
                            .aspectRatio(1f)
                            .padding(1.dp)
                            .then(
                                if (isValidDay) Modifier.clickable {
                                    viewModel.selectDate(displayMonth.year, displayMonth.monthNumber, day)
                                } else Modifier
                            )
                            .clip(CircleShape)
                            .background(
                                when {
                                    !isValidDay -> androidx.compose.ui.graphics.Color.Transparent
                                    selectedDate != null && day == selectedDate.day &&
                                        displayMonth.monthNumber == selectedDate.month &&
                                        displayMonth.year == selectedDate.year ->
                                        TempleGold.copy(alpha = 0.8f)
                                    isValidDay && day == today.dayOfMonth &&
                                        displayMonth.monthNumber == today.monthNumber &&
                                        displayMonth.year == today.year ->
                                        MaterialTheme.colorScheme.primaryContainer
                                    else -> androidx.compose.ui.graphics.Color.Transparent
                                }
                            ),
                        contentAlignment = Alignment.Center,
                    ) {
                        if (isValidDay) {
                            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                                Text(
                                    day.toString(),
                                    style = MaterialTheme.typography.labelMedium,
                                    fontWeight = if (day == today.dayOfMonth && displayMonth.monthNumber == today.monthNumber && displayMonth.year == today.year)
                                        FontWeight.Bold else FontWeight.Normal,
                                    color = when {
                                        selectedDate != null && day == selectedDate.day &&
                                            displayMonth.monthNumber == selectedDate.month ->
                                            MaterialTheme.colorScheme.onPrimary
                                        col == 0 -> InauspiciousRed // Sunday
                                        else -> MaterialTheme.colorScheme.onSurface
                                    },
                                )
                                // Tithi abbreviation (paksha dot: ● Shukla, ○ Krishna)
                                val tithiIdx = tithis[day - 1]
                                val isShukla = tithiIdx < 15
                                val tithiNum = if (isShukla) tithiIdx + 1 else tithiIdx - 14
                                Text(
                                    tithiNum.toString(),
                                    style = MaterialTheme.typography.labelSmall.copy(fontSize = 7.sp),
                                    color = MaterialTheme.colorScheme.onSurfaceVariant.copy(
                                        alpha = if (isShukla) 0.7f else 0.5f
                                    ),
                                )
                            }
                        }
                    }
                }
            }
        }

        Spacer(Modifier.height(4.dp))
        // Legend
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.End,
            verticalAlignment = Alignment.CenterVertically,
        ) {
            Box(Modifier.size(8.dp).clip(CircleShape).background(MaterialTheme.colorScheme.primaryContainer))
            Spacer(Modifier.width(4.dp))
            Text("నేడు", style = MaterialTheme.typography.labelSmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
            Spacer(Modifier.width(8.dp))
            Box(Modifier.size(8.dp).clip(CircleShape).background(TempleGold.copy(alpha = 0.8f)))
            Spacer(Modifier.width(4.dp))
            Text("ఎంచుకున్న రోజు", style = MaterialTheme.typography.labelSmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
            Spacer(Modifier.width(8.dp))
            Text("సంఖ్య = తిథి", style = MaterialTheme.typography.labelSmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
        }
    }
}

private fun daysInMonth(year: Int, month: Int): Int {
    val nextMonth = if (month == 12) LocalDate(year + 1, 1, 1) else LocalDate(year, month + 1, 1)
    val thisMonth = LocalDate(year, month, 1)
    return (nextMonth.toEpochDays() - thisMonth.toEpochDays()).toInt()
}
