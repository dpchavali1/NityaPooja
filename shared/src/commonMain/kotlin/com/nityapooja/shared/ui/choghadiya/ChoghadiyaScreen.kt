package com.nityapooja.shared.ui.choghadiya

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.filled.LightMode
import androidx.compose.material.icons.filled.NightlightRound
import androidx.compose.material.icons.filled.Notifications
import androidx.compose.material.icons.filled.Warning
import com.nityapooja.shared.ui.components.InfoBottomSheet
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.nityapooja.shared.data.muhurtam.MuhurtamRules
import com.nityapooja.shared.ui.components.FontSizeControls
import com.nityapooja.shared.ui.components.FontSizeViewModel
import com.nityapooja.shared.ui.panchangam.PanchangamViewModel
import com.nityapooja.shared.ui.theme.AuspiciousGreen
import com.nityapooja.shared.ui.theme.DeepVermillion
import com.nityapooja.shared.ui.theme.NityaPoojaTextStyles
import com.nityapooja.shared.ui.theme.TempleGold
import kotlinx.datetime.Clock
import kotlinx.datetime.TimeZone
import kotlinx.datetime.toLocalDateTime
import org.koin.compose.viewmodel.koinViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ChoghadiyaScreen(
    onBack: () -> Unit,
    viewModel: PanchangamViewModel = koinViewModel(),
    fontSizeViewModel: FontSizeViewModel = koinViewModel(),
    bannerAd: (@Composable () -> Unit)? = null,
) {
    val locationInfo by viewModel.locationInfo.collectAsState()
    val panchangam = remember(locationInfo) {
        viewModel.calculatePanchangam(locationInfo.lat, locationInfo.lng, locationInfo.timezone)
    }
    val fontSize by fontSizeViewModel.fontSize.collectAsState()
    val fontScale = fontSize / 16f

    val now = Clock.System.now()
    val userTz = try { TimeZone.of(locationInfo.timezone) } catch (_: Exception) { TimeZone.currentSystemDefault() }
    val localNow = now.toLocalDateTime(userTz)
    val currentDecimal = localNow.hour + localNow.minute / 60.0

    val dayOfWeek = MuhurtamRules.dayOfWeekIndexFromTelugu(panchangam.teluguDay)
    val choghadiya = remember(panchangam.teluguDay, panchangam.sunTimes) {
        MuhurtamRules.calculateChoghadiya(
            sunriseDecimal = panchangam.sunTimes.sunriseDecimal,
            sunsetDecimal = panchangam.sunTimes.sunsetDecimal,
            dayOfWeek = dayOfWeek,
            currentDecimal = currentDecimal,
        )
    }

    var showInfoSheet by remember { mutableStateOf(false) }

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Column {
                        Text(
                            "చోఘడియా",
                            style = MaterialTheme.typography.titleLarge,
                            fontWeight = FontWeight.Bold,
                        )
                        Text(
                            "Choghadiya · ${panchangam.dateDisplay}",
                            style = MaterialTheme.typography.labelSmall,
                            color = MaterialTheme.colorScheme.onSurfaceVariant,
                        )
                    }
                },
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, "Back")
                    }
                },
                actions = {
                    IconButton(onClick = { showInfoSheet = true }) {
                        Icon(Icons.Default.Info, contentDescription = "What is Choghadiya?", tint = TempleGold)
                    }
                    FontSizeControls(
                        fontSize = fontSize,
                        onDecrease = fontSizeViewModel::decrease,
                        onIncrease = fontSizeViewModel::increase,
                    )
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.surface,
                ),
            )
        },
    ) { padding ->
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding),
            contentPadding = PaddingValues(16.dp),
            verticalArrangement = Arrangement.spacedBy(8.dp),
        ) {
            item {
                // Legend
                Surface(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(12.dp),
                    color = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f),
                ) {
                    Column(modifier = Modifier.padding(12.dp)) {
                        Row(
                            horizontalArrangement = Arrangement.spacedBy(6.dp),
                            verticalAlignment = Alignment.CenterVertically,
                        ) {
                            Surface(shape = RoundedCornerShape(4.dp), color = AuspiciousGreen.copy(alpha = 0.15f)) {
                                Text("శుభ", modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp), style = MaterialTheme.typography.labelSmall, color = AuspiciousGreen, fontWeight = FontWeight.Bold)
                            }
                            Text(
                                "అమృత · శుభ · లాభ · చర",
                                style = MaterialTheme.typography.labelSmall,
                                color = MaterialTheme.colorScheme.onSurfaceVariant,
                            )
                        }
                        Spacer(Modifier.height(4.dp))
                        Row(
                            horizontalArrangement = Arrangement.spacedBy(6.dp),
                            verticalAlignment = Alignment.CenterVertically,
                        ) {
                            Surface(shape = RoundedCornerShape(4.dp), color = DeepVermillion.copy(alpha = 0.15f)) {
                                Text("అశుభ", modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp), style = MaterialTheme.typography.labelSmall, color = DeepVermillion, fontWeight = FontWeight.Bold)
                            }
                            Text(
                                "ఉద్వేగ · కాల · రోగ",
                                style = MaterialTheme.typography.labelSmall,
                                color = MaterialTheme.colorScheme.onSurfaceVariant,
                            )
                        }
                        Spacer(Modifier.height(6.dp))
                        HorizontalDivider(color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.1f))
                        Spacer(Modifier.height(6.dp))
                        Text(
                            "Amrit = best for all • Shubh = ceremonies • Labh = business • Char = travel",
                            style = MaterialTheme.typography.labelSmall.copy(fontSize = 10.sp),
                            color = AuspiciousGreen.copy(alpha = 0.8f),
                        )
                        Spacer(Modifier.height(2.dp))
                        Text(
                            "Udveg = anxiety • Kaal = avoid • Rog = illness",
                            style = MaterialTheme.typography.labelSmall.copy(fontSize = 10.sp),
                            color = DeepVermillion.copy(alpha = 0.8f),
                        )
                    }
                }
            }

            item {
                // Day header
                Spacer(Modifier.height(4.dp))
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(8.dp),
                ) {
                    Icon(
                        Icons.Default.LightMode,
                        contentDescription = null,
                        tint = TempleGold,
                        modifier = Modifier.size(20.dp),
                    )
                    Text(
                        "పగలు · Day  (${panchangam.sunTimes.sunrise} – ${panchangam.sunTimes.sunset})",
                        style = NityaPoojaTextStyles.GoldLabel,
                        color = TempleGold,
                    )
                }
            }

            items(choghadiya.daySlots) { slot ->
                ChoghadiyaSlotCard(slot, fontScale)
            }

            item {
                // Night header
                Spacer(Modifier.height(8.dp))
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(8.dp),
                ) {
                    Icon(
                        Icons.Default.NightlightRound,
                        contentDescription = null,
                        tint = Color(0xFF7986CB),
                        modifier = Modifier.size(20.dp),
                    )
                    Text(
                        "రాత్రి · Night",
                        style = NityaPoojaTextStyles.GoldLabel,
                        color = Color(0xFF7986CB),
                    )
                }
            }

            items(choghadiya.nightSlots) { slot ->
                ChoghadiyaSlotCard(slot, fontScale)
            }

            item { Spacer(Modifier.height(24.dp)) }
            item { bannerAd?.invoke() }
        }
    }

    if (showInfoSheet) {
        InfoBottomSheet(
            titleTelugu = "చోఘడియా అంటే ఏమిటి?",
            titleEnglish = "What is Choghadiya?",
            bodyTelugu = "చోఘడియా అంటే 'నాలుగు ఘడియాలు' — పగలు మరియు రాత్రి ఒక్కొక్కటి 8 సమాన భాగాలుగా విభజించబడతాయి. ప్రతి భాగం 'అమృత, శుభ, లాభ, చర' (శుభ) లేదా 'ఉద్వేగ, కాల, రోగ' (అశుభ) అని వర్గీకరించబడుతుంది.",
            bodyEnglish = "Choghadiya splits the day and night into 8 equal time slots each. Each slot is ruled by a planet and classified as auspicious (Amrit, Shubh, Labh, Char) or inauspicious (Udveg, Kaal, Rog). Telugu families check this before travel, business, ceremonies, and important tasks.",
            whyItMatters = "ఇప్పుడు హైలైట్ అయిన స్లాట్ ప్రస్తుత సమయం. శుభ స్లాట్‌లో ముఖ్యమైన పనులు ప్రారంభించడానికి ప్రయత్నించండి. · The highlighted slot is the current time. Try to start important activities during an auspicious slot.",
            tips = listOf(
                "అమృత లేదా శుభ స్లాట్‌లో ప్రయాణం ప్రారంభించండి · Start travel during Amrit or Shubh slots",
                "వ్యాపార ఒప్పందాలకు లాభ స్లాట్ అనుకూలం · Labh slot is ideal for business agreements",
                "కాల, రోగ స్లాట్‌లలో వైద్య చికిత్స మానుకోండి · Avoid medical procedures during Kaal or Rog",
            ),
            onDismiss = { showInfoSheet = false },
        )
    }
}

@Composable
private fun ChoghadiyaSlotCard(slot: MuhurtamRules.ChoghadiyaSlot, fontScale: Float = 1f) {
    val accentColor = when {
        slot.isActive -> TempleGold
        slot.type.isAuspicious -> AuspiciousGreen
        else -> DeepVermillion
    }
    val bgColor = when {
        slot.isActive -> TempleGold.copy(alpha = 0.15f)
        slot.type.isAuspicious -> AuspiciousGreen.copy(alpha = 0.06f)
        else -> DeepVermillion.copy(alpha = 0.06f)
    }

    Surface(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(12.dp),
        color = bgColor,
    ) {
        Row(
            modifier = Modifier
                .padding(horizontal = 14.dp, vertical = 12.dp)
                .heightIn(min = 56.dp),
            verticalAlignment = Alignment.CenterVertically,
        ) {
            // Color indicator bar
            Box(
                modifier = Modifier
                    .width(4.dp)
                    .height(40.dp)
                    .background(accentColor, RoundedCornerShape(2.dp)),
            )
            Spacer(Modifier.width(12.dp))

            Column(modifier = Modifier.weight(1f)) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Text(
                        slot.type.nameTelugu,
                        style = MaterialTheme.typography.titleSmall.copy(fontSize = (14 * fontScale).sp),
                        fontWeight = FontWeight.Bold,
                        color = accentColor,
                    )
                    Text(
                        "  ·  ${slot.type.nameEnglish}",
                        style = MaterialTheme.typography.labelSmall.copy(fontSize = (11 * fontScale).sp),
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                    )
                }
                Spacer(Modifier.height(2.dp))
                Text(
                    slot.type.descriptionTelugu,
                    style = MaterialTheme.typography.labelSmall.copy(fontSize = (10 * fontScale).sp),
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                )
            }

            Icon(
                imageVector = if (slot.isActive) Icons.Default.Notifications
                    else if (slot.type.isAuspicious) Icons.Default.CheckCircle
                    else Icons.Default.Warning,
                contentDescription = if (slot.type.isAuspicious) "Auspicious" else "Inauspicious",
                tint = accentColor.copy(alpha = 0.7f),
                modifier = Modifier.size(16.dp).padding(end = 4.dp),
            )

            Column(horizontalAlignment = Alignment.End) {
                Text(
                    slot.startTime,
                    style = MaterialTheme.typography.bodySmall.copy(fontSize = (12 * fontScale).sp),
                    fontWeight = FontWeight.Medium,
                    color = MaterialTheme.colorScheme.onSurface,
                )
                Text(
                    slot.endTime,
                    style = MaterialTheme.typography.bodySmall.copy(fontSize = (12 * fontScale).sp),
                    fontWeight = FontWeight.Medium,
                    color = MaterialTheme.colorScheme.onSurface,
                )
            }

            if (slot.isActive) {
                Spacer(Modifier.width(8.dp))
                Surface(
                    shape = RoundedCornerShape(6.dp),
                    color = TempleGold.copy(alpha = 0.2f),
                ) {
                    Text(
                        "ఇప్పుడు",
                        modifier = Modifier.padding(horizontal = 6.dp, vertical = 3.dp),
                        style = MaterialTheme.typography.labelSmall.copy(fontSize = (11 * fontScale).sp),
                        fontWeight = FontWeight.Bold,
                        color = TempleGold,
                    )
                }
            }
        }
    }
}
