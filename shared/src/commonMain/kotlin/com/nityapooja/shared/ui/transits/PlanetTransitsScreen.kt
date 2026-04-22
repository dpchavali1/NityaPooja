package com.nityapooja.shared.ui.transits

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.filled.Star
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.nityapooja.shared.ui.components.FontSizeControls
import com.nityapooja.shared.ui.components.FontSizeViewModel
import com.nityapooja.shared.ui.components.GlassmorphicCard
import com.nityapooja.shared.ui.components.InfoBottomSheet
import com.nityapooja.shared.ui.components.SectionHeader
import com.nityapooja.shared.ui.theme.TempleGold
import com.nityapooja.shared.utils.JyotishConstants
import com.nityapooja.shared.utils.PlanetTransitCalculator
import kotlinx.datetime.Clock
import kotlinx.datetime.Instant
import kotlinx.datetime.TimeZone
import kotlinx.datetime.toLocalDateTime
import org.koin.compose.viewmodel.koinViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun PlanetTransitsScreen(
    onBack: () -> Unit = {},
    fontSizeViewModel: FontSizeViewModel = koinViewModel(),
    bannerAd: (@Composable () -> Unit)? = null,
) {
    val transits = remember {
        PlanetTransitCalculator.getUpcomingTransits(lookaheadDays = 730)
    }
    val fontSize by fontSizeViewModel.fontSize.collectAsState()
    val fontScale = fontSize / 16f
    var showInfoSheet by remember { mutableStateOf(false) }

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Column {
                        Text("గ్రహ పరివర్తనలు", style = MaterialTheme.typography.titleLarge, fontWeight = FontWeight.Bold)
                        Text("Planet Transits", style = MaterialTheme.typography.labelSmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
                    }
                },
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, "Back")
                    }
                },
                actions = {
                    IconButton(onClick = { showInfoSheet = true }) {
                        Icon(Icons.Default.Info, contentDescription = "Info", tint = TempleGold)
                    }
                    FontSizeControls(
                        fontSize = fontSize,
                        onDecrease = fontSizeViewModel::decrease,
                        onIncrease = fontSizeViewModel::increase,
                    )
                },
                colors = TopAppBarDefaults.topAppBarColors(containerColor = MaterialTheme.colorScheme.surface),
            )
        }
    ) { padding ->
        if (transits.isEmpty()) {
            Box(
                modifier = Modifier.fillMaxSize().padding(padding),
                contentAlignment = Alignment.Center,
            ) {
                Text("No upcoming transits found", color = MaterialTheme.colorScheme.onSurfaceVariant)
            }
        } else {
            LazyColumn(
                modifier = Modifier.fillMaxSize().padding(padding),
                contentPadding = PaddingValues(16.dp),
                verticalArrangement = Arrangement.spacedBy(12.dp),
            ) {
                item {
                    SectionHeader(
                        titleTelugu = "రాబోయే 2 సంవత్సరాలు",
                        titleEnglish = "Next 2 Years",
                    )
                }

                // Group by graha for display
                val guruTransits = transits.filter { it.grahaIndex == 4 }
                val saniTransits = transits.filter { it.grahaIndex == 6 }
                val rahuKetuTransits = transits.filter { it.grahaIndex == 7 || it.grahaIndex == 8 }

                if (guruTransits.isNotEmpty()) {
                    item {
                        Text(
                            "${JyotishConstants.GRAHA_NAMES_TELUGU[4]} పరివర్తన / Jupiter Transits",
                            style = MaterialTheme.typography.titleMedium.copy(fontSize = (16 * fontScale).sp),
                            fontWeight = FontWeight.SemiBold,
                            color = TempleGold,
                            modifier = Modifier.padding(vertical = 4.dp),
                        )
                    }
                    items(guruTransits) { transit -> TransitCard(transit, fontScale) }
                }

                if (saniTransits.isNotEmpty()) {
                    item {
                        Spacer(Modifier.height(4.dp))
                        Text(
                            "${JyotishConstants.GRAHA_NAMES_TELUGU[6]} పరివర్తన / Saturn Transits",
                            style = MaterialTheme.typography.titleMedium.copy(fontSize = (16 * fontScale).sp),
                            fontWeight = FontWeight.SemiBold,
                            color = TempleGold,
                            modifier = Modifier.padding(vertical = 4.dp),
                        )
                    }
                    items(saniTransits) { transit -> TransitCard(transit, fontScale) }
                }

                if (rahuKetuTransits.isNotEmpty()) {
                    item {
                        Spacer(Modifier.height(4.dp))
                        Text(
                            "రాహు-కేతు పరివర్తన / Rahu-Ketu Transits",
                            style = MaterialTheme.typography.titleMedium.copy(fontSize = (16 * fontScale).sp),
                            fontWeight = FontWeight.SemiBold,
                            color = TempleGold,
                            modifier = Modifier.padding(vertical = 4.dp),
                        )
                    }
                    items(rahuKetuTransits) { transit -> TransitCard(transit, fontScale) }
                }

                item {
                    Spacer(Modifier.height(8.dp))
                    GlassmorphicCard(cornerRadius = 12.dp, contentPadding = 12.dp) {
                        Text(
                            "గమనిక: తేదీలు లహిరి అయనాంశం ఆధారంగా గణించబడ్డాయి. " +
                                "ఈ తేదీలు సూచన మాత్రమే.\n" +
                                "Note: Dates computed using Lahiri ayanamsa. These are approximate ingress dates for reference.",
                            style = MaterialTheme.typography.bodySmall.copy(fontSize = (12 * fontScale).sp),
                            color = MaterialTheme.colorScheme.onSurfaceVariant,
                        )
                    }
                }

                item { bannerAd?.invoke() }
            }
        }
    }

    if (showInfoSheet) {
        InfoBottomSheet(
            titleTelugu = "గ్రహ పరివర్తన అంటే ఏమిటి?",
            titleEnglish = "What are Planet Transits?",
            bodyTelugu = "గ్రహ పరివర్తన అంటే గురు, శని, రాహు-కేతు వంటి నెమ్మది గ్రహాలు ఒక రాశి నుండి మరొక రాశికి మారడం. ఈ మార్పులు జ్యోతిష్యంలో ముఖ్యమైన ఫలితాలు ఇస్తాయి.",
            bodyEnglish = "Planet transits mark when slow-moving planets — Jupiter, Saturn, Rahu, and Ketu — move from one Rashi (zodiac sign) to the next. These ingress events are significant in Vedic astrology as they influence collective and personal themes for months or years.",
            whyItMatters = "గురు పరివర్తన 12-13 నెలలు, శని పరివర్తన 2.5 సంవత్సరాలు, రాహు-కేతు పరివర్తన 18 నెలలు ఉంటుంది. · Jupiter transits last ~13 months, Saturn ~2.5 years, and Rahu-Ketu ~18 months per sign.",
            tips = listOf(
                "మీ జన్మ రాశి నుండి గురు స్థానం మీకు అత్యంత ముఖ్యం · Jupiter's position relative to your Janma Rashi is most significant",
                "శని సాడేసాతి (7.5 సంవత్సరాలు) మరియు అష్టమ శని గమనించండి · Watch for Sade Sati (7.5 yrs) and Ashtama Shani from your Moon sign",
                "రాహు-కేతు ఎల్లప్పుడూ అభిముఖంగా ఉంటాయి — 6 రాశులు దూరంలో · Rahu and Ketu are always exactly opposite — 6 signs apart",
            ),
            onDismiss = { showInfoSheet = false },
        )
    }
}

@Composable
private fun TransitCard(transit: PlanetTransitCalculator.TransitEvent, fontScale: Float = 1f) {
    val now = Clock.System.now()
    val isPast = transit.epochMillis < now.toEpochMilliseconds()
    val daysUntil = ((transit.epochMillis - now.toEpochMilliseconds()) / (24 * 60 * 60 * 1000L)).toInt()

    val transitDate = Instant.fromEpochMilliseconds(transit.epochMillis)
        .toLocalDateTime(TimeZone.currentSystemDefault())
    val dateStr = "${transitDate.dayOfMonth} ${monthName(transitDate.monthNumber)} ${transitDate.year}"

    GlassmorphicCard(cornerRadius = 12.dp, contentPadding = 12.dp) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(12.dp),
        ) {
            Icon(
                Icons.Default.Star,
                contentDescription = null,
                tint = if (isPast) MaterialTheme.colorScheme.onSurfaceVariant else TempleGold,
                modifier = Modifier.size(28.dp),
            )
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    "${transit.grahaNameTelugu} — ${transit.fromRashiTelugu} → ${transit.toRashiTelugu}",
                    style = MaterialTheme.typography.titleSmall.copy(fontSize = (14 * fontScale).sp),
                    fontWeight = FontWeight.SemiBold,
                )
                Text(
                    "${transit.grahaNameEnglish} enters ${transit.toRashiTelugu}",
                    style = MaterialTheme.typography.bodySmall.copy(fontSize = (12 * fontScale).sp),
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                )
                val impact = transitImpact(transit.grahaIndex, transit.toRashiIndex)
                if (impact.isNotBlank()) {
                    Text(
                        impact,
                        style = MaterialTheme.typography.bodySmall.copy(fontSize = (11 * fontScale).sp),
                        color = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.85f),
                        fontStyle = androidx.compose.ui.text.font.FontStyle.Italic,
                        lineHeight = (16 * fontScale).sp,
                    )
                }
                Spacer(Modifier.height(4.dp))
                Text(
                    dateStr,
                    style = MaterialTheme.typography.labelMedium.copy(fontSize = (12 * fontScale).sp),
                    color = if (isPast) MaterialTheme.colorScheme.onSurfaceVariant else TempleGold,
                    fontWeight = FontWeight.Medium,
                )
            }
            if (!isPast) {
                Surface(
                    color = TempleGold.copy(alpha = 0.15f),
                    shape = MaterialTheme.shapes.small,
                ) {
                    Text(
                        if (daysUntil <= 0) "Today" else "${daysUntil}d",
                        style = MaterialTheme.typography.labelSmall.copy(fontSize = (11 * fontScale).sp),
                        color = TempleGold,
                        modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp),
                    )
                }
            }
        }
    }
}

/** Returns a brief English impact summary for a given planet transiting into a given Rashi. */
private fun transitImpact(grahaIndex: Int, toRashiIndex: Int): String {
    return when (grahaIndex) {
        4 -> when (toRashiIndex) { // Jupiter
            0 -> "Favorable for new beginnings, health, and personal growth"
            1 -> "Wealth, family harmony, and speech gain prominence"
            2 -> "Excellent for education, short travel, and communication"
            3 -> "Home, property, and emotional matters come into focus"
            4 -> "Children, creativity, and investment thrive"
            5 -> "Health attention needed; service and daily routines highlighted"
            6 -> "Marriage, partnerships, and business dealings favored"
            7 -> "Spiritual depth, research, and transformation emphasized"
            8 -> "Long-distance travel, higher education, and dharma favored"
            9 -> "Career advancement and public recognition increase"
            10 -> "Gains from networks, elder siblings, and social causes"
            11 -> "Spiritual reflection; expenses on good causes are favorable"
            else -> "Major life shifts in areas ruled by Jupiter"
        }
        6 -> when (toRashiIndex) { // Saturn
            0 -> "Discipline required in personal efforts; health needs care"
            1 -> "Financial discipline and steady family responsibility"
            2 -> "Hard work in communication and short-term ventures"
            3 -> "Home renovations and family duties increase"
            4 -> "Creative work requires patience; investments need caution"
            5 -> "Health and daily routines demand structured attention"
            6 -> "Partnerships tested; legal matters need care"
            7 -> "Transformation through hard work and karmic lessons"
            8 -> "Long journeys and religious pursuits with discipline"
            9 -> "Career demands steady effort; public duties increase"
            10 -> "Social and community responsibilities grow"
            11 -> "Spiritual retreat; foreign connections may be restricted"
            else -> "Karmic restructuring in Saturn's domain"
        }
        7 -> when (toRashiIndex) { // Rahu
            0 -> "Bold ambition and unconventional health approaches"
            1 -> "Material desires and family stability tested"
            2 -> "Strong curiosity and communication breakthrough"
            3 -> "Psychological depth and home-based changes"
            4 -> "Creative risk-taking and speculative ventures"
            5 -> "Health regimens and service orientation emphasized"
            6 -> "Relationship boundaries and foreign partnerships"
            7 -> "Hidden matters, research, and spiritual illusions"
            8 -> "Philosophical exploration and foreign travel"
            9 -> "Career ambition and public image transformation"
            10 -> "Social networks and technology ventures"
            11 -> "Spiritual detachment from material desires"
            else -> "Rahu intensifies desires in this life area"
        }
        8 -> when (toRashiIndex) { // Ketu — always opposite Rahu
            else -> "Spiritual detachment, past-life karma surfacing; focus shifts inward"
        }
        else -> ""
    }
}

private fun monthName(month: Int): String = arrayOf(
    "Jan","Feb","Mar","Apr","May","Jun","Jul","Aug","Sep","Oct","Nov","Dec"
)[month - 1]
