package com.nityapooja.shared.ui.home

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import org.koin.compose.viewmodel.koinViewModel
import com.nityapooja.shared.data.grahanam.GrahanamRepository
import com.nityapooja.shared.data.grahanam.GrahanamType
import com.nityapooja.shared.ui.components.*
import com.nityapooja.shared.ui.panchangam.PanchangamViewModel
import com.nityapooja.shared.ui.panchangam.MoonPhaseEvent
import com.nityapooja.shared.ui.panchangam.MoonPhaseType
import com.nityapooja.shared.ui.theme.*
import com.nityapooja.shared.platform.shareText
import androidx.compose.foundation.isSystemInDarkTheme
import kotlinx.datetime.Clock
import kotlinx.datetime.TimeZone
import kotlinx.datetime.todayIn
import kotlinx.datetime.toLocalDateTime

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HomeScreen(
    viewModel: HomeViewModel = koinViewModel(),
    onNavigateToAartiDetail: (Int) -> Unit = {},
    onNavigateToStotrams: () -> Unit = {},
    onNavigateToKeertanalu: () -> Unit = {},
    onNavigateToSearch: () -> Unit = {},
    onNavigateToSettings: () -> Unit = {},
    onNavigateToProfile: () -> Unit = {},
    onNavigateToTemples: () -> Unit = {},
    onNavigateToFestivals: () -> Unit = {},
    onNavigateToJapa: () -> Unit = {},
    onNavigateToDeityDetail: (Int) -> Unit = {},
    onNavigateToAartis: () -> Unit = {},
    onNavigateToPanchangam: () -> Unit = {},
    onNavigateToMuhurtam: () -> Unit = {},
    onNavigateToRashifal: () -> Unit = {},
    onNavigateToBookmark: (String, Int) -> Unit = { _, _ -> },
    fontSizeViewModel: FontSizeViewModel = koinViewModel(),
    bannerAd: (@Composable () -> Unit)? = null,
) {
    androidx.compose.runtime.LaunchedEffect(Unit) { viewModel.refreshToday() }

    val deities by viewModel.deities.collectAsState()
    val todayShloka by viewModel.todayShloka.collectAsState()
    val deityOfDay by viewModel.deityOfTheDay.collectAsState()
    val bookmarks by viewModel.bookmarks.collectAsState()
    val userName by viewModel.userName.collectAsState()
    val userGotra by viewModel.userGotra.collectAsState()
    val userNakshatra by viewModel.userNakshatra.collectAsState()
    val todayFestival by viewModel.todayFestival.collectAsState()
    val recentHistory by viewModel.recentHistory.collectAsState()
    val upcomingFestivals by viewModel.upcomingFestivals.collectAsState()

    val fontSize by fontSizeViewModel.fontSize.collectAsState()
    val fontScale = fontSize / 16f

    val panchangamViewModel: PanchangamViewModel = koinViewModel()
    val locationInfo by panchangamViewModel.locationInfo.collectAsState()
    val todayKey = Clock.System.todayIn(TimeZone.currentSystemDefault()).toString()
    val panchangamData = remember(locationInfo, todayKey) {
        panchangamViewModel.calculatePanchangam(locationInfo.lat, locationInfo.lng, locationInfo.timezone)
    }

    val now = remember { Clock.System.now() }
    val userTz = remember(locationInfo.timezone) {
        try { TimeZone.of(locationInfo.timezone) } catch (_: Exception) { TimeZone.of("Asia/Kolkata") }
    }
    val nextGrahanam = remember(now) { GrahanamRepository.getNextGrahanam(now) }
    val daysUntilGrahanam = remember(nextGrahanam, now, userTz) {
        nextGrahanam?.let { g ->
            val nowDate = now.toLocalDateTime(userTz).date
            val sparthaDate = g.sparthaUtc.toLocalDateTime(userTz).date
            val diff = (sparthaDate.toEpochDays() - nowDate.toEpochDays()).toInt()
            if (diff in 0..7) diff else null
        }
    }
    var grahanamBannerDismissed by remember { mutableStateOf(false) }

    val salutation = userName.ifBlank { "భక్తా" }
    val greetingTelugu = viewModel.getGreetingTelugu()
    val greetingEnglish = viewModel.getGreetingEnglish()

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Column {
                        Text(
                            "$greetingTelugu, $salutation",
                            style = MaterialTheme.typography.titleLarge,
                            fontWeight = FontWeight.Bold,
                        )
                        Text(
                            "$greetingEnglish · Your Spiritual Companion",
                            style = MaterialTheme.typography.labelSmall,
                            color = MaterialTheme.colorScheme.onSurfaceVariant,
                        )
                    }
                },
                actions = {
                    IconButton(onClick = onNavigateToSearch) {
                        Icon(Icons.Default.Search, "Search")
                    }
                    IconButton(onClick = onNavigateToSettings) {
                        Icon(Icons.Default.Settings, "Settings")
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = Color.Transparent,
                ),
            )
        },
    ) { padding ->
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding),
            contentPadding = PaddingValues(bottom = 24.dp),
            verticalArrangement = Arrangement.spacedBy(20.dp),
        ) {
            // Festival Greeting Card (shown only on festival day)
            if (todayFestival != null) {
                item {
                    FestivalGreetingCard(
                        festival = todayFestival!!,
                        userName = userName,
                        gotra = userGotra,
                        nakshatra = userNakshatra,
                        fontScale = fontScale,
                        samvatsaraNameTelugu = panchangamData.samvatsara.nameTelugu,
                        samvatsaraNameEnglish = panchangamData.samvatsara.nameEnglish,
                        modifier = Modifier.padding(horizontal = 16.dp),
                    )
                }
            }

            // Telugu Calendar Card
            item {
                TeluguCalendarCard(
                    panchangamData = panchangamData,
                    fontScale = fontScale,
                    onClick = onNavigateToPanchangam,
                    modifier = Modifier.padding(horizontal = 16.dp),
                )
            }

            // Today's Muhurtam Quick Info
            item {
                Column(modifier = Modifier.padding(horizontal = 16.dp)) {
                    SectionHeader(
                        titleTelugu = "నేటి ముహూర్తం",
                        titleEnglish = "Today's Auspicious Times",
                    )
                    Spacer(Modifier.height(8.dp))
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(8.dp),
                    ) {
                        // Rahu Kalam warning
                        Surface(
                            modifier = Modifier.weight(1f),
                            shape = MaterialTheme.shapes.medium,
                            color = if (panchangamData.rahuKaal.isActive)
                                DeepVermillion.copy(alpha = 0.12f)
                            else MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f),
                        ) {
                            Column(modifier = Modifier.padding(10.dp)) {
                                Text(
                                    if (panchangamData.rahuKaal.isActive) "!! రాహు కాలం" else "రాహు కాలం",
                                    style = MaterialTheme.typography.labelSmall.copy(
                                        fontSize = (11 * fontScale).sp,
                                        fontWeight = FontWeight.Bold,
                                    ),
                                    color = if (panchangamData.rahuKaal.isActive) DeepVermillion else TempleGold,
                                )
                                Text(
                                    "${panchangamData.rahuKaal.startTime} - ${panchangamData.rahuKaal.endTime}",
                                    style = MaterialTheme.typography.bodySmall.copy(fontSize = (12 * fontScale).sp),
                                    color = MaterialTheme.colorScheme.onSurface,
                                )
                            }
                        }
                        // Abhijit Muhurt
                        Surface(
                            modifier = Modifier.weight(1f),
                            shape = MaterialTheme.shapes.medium,
                            color = if (panchangamData.abhijitMuhurt.isActive)
                                AuspiciousGreen.copy(alpha = 0.12f)
                            else MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f),
                        ) {
                            Column(modifier = Modifier.padding(10.dp)) {
                                Text(
                                    "అభిజిత్ ముహూర్తం",
                                    style = MaterialTheme.typography.labelSmall.copy(
                                        fontSize = (11 * fontScale).sp,
                                        fontWeight = FontWeight.Bold,
                                    ),
                                    color = if (panchangamData.abhijitMuhurt.isActive) AuspiciousGreen else TempleGold,
                                )
                                Text(
                                    "${panchangamData.abhijitMuhurt.startTime} - ${panchangamData.abhijitMuhurt.endTime}",
                                    style = MaterialTheme.typography.bodySmall.copy(fontSize = (12 * fontScale).sp),
                                    color = MaterialTheme.colorScheme.onSurface,
                                )
                            }
                        }
                        // Sunrise/Sunset
                        Surface(
                            modifier = Modifier.weight(1f),
                            shape = MaterialTheme.shapes.medium,
                            color = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f),
                        ) {
                            Column(modifier = Modifier.padding(10.dp)) {
                                Text(
                                    "సూర్యోదయం",
                                    style = MaterialTheme.typography.labelSmall.copy(
                                        fontSize = (11 * fontScale).sp,
                                        fontWeight = FontWeight.Bold,
                                    ),
                                    color = TempleGold,
                                )
                                Text(
                                    "${panchangamData.sunTimes.sunrise}",
                                    style = MaterialTheme.typography.bodySmall.copy(fontSize = (12 * fontScale).sp),
                                    color = MaterialTheme.colorScheme.onSurface,
                                )
                            }
                        }
                    }
                }
            }

            // Hero: Daily Shloka Card
            item {
                todayShloka?.let { shloka ->
                    GlassmorphicCard(
                        modifier = Modifier.padding(horizontal = 16.dp),
                        accentColor = TempleGold,
                    ) {
                        Text(
                            "నేటి శ్లోకం · TODAY'S BLESSING",
                            style = NityaPoojaTextStyles.GoldLabel,
                            color = TempleGold,
                        )
                        Spacer(Modifier.height(12.dp))
                        Text(
                            shloka.textSanskrit,
                            style = NityaPoojaTextStyles.SanskritVerse.copy(
                                fontSize = (16 * fontScale).sp,
                                lineHeight = (24 * fontScale).sp,
                            ),
                            color = MaterialTheme.colorScheme.onSurface,
                        )
                        shloka.meaningTelugu?.let {
                            Spacer(Modifier.height(8.dp))
                            Text(
                                it,
                                style = MaterialTheme.typography.bodyMedium.copy(
                                    fontSize = (14 * fontScale).sp,
                                ),
                                color = MaterialTheme.colorScheme.onSurfaceVariant,
                            )
                        }
                        shloka.meaningEnglish?.let {
                            Spacer(Modifier.height(4.dp))
                            Text(
                                it,
                                style = MaterialTheme.typography.bodySmall.copy(
                                    fontSize = (12 * fontScale).sp,
                                ),
                                color = MaterialTheme.colorScheme.onSurfaceVariant,
                            )
                        }
                        Spacer(Modifier.height(12.dp))
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.End,
                        ) {
                            TextButton(onClick = {
                                val text = buildString {
                                    append("నేటి శ్లోకం · Today's Blessing\n\n")
                                    append(shloka.textSanskrit)
                                    shloka.meaningTelugu?.let { append("\n\n$it") }
                                    shloka.meaningEnglish?.let { append("\n\n$it") }
                                    shloka.source?.let { append("\n\n— $it") }
                                    append("\n\nShared via NityaPooja")
                                }
                                shareText(text)
                            }) {
                                Icon(Icons.Default.Share, "Share", Modifier.size(16.dp))
                                Spacer(Modifier.width(4.dp))
                                Text("Share", style = MaterialTheme.typography.labelSmall)
                            }
                        }
                    }
                }
            }

            // Deity of the Day
            item {
                deityOfDay.firstOrNull()?.let { deity ->
                    val deityColor = resolveDeityColor(deity.colorTheme)
                    GlassmorphicCard(
                        modifier = Modifier.padding(horizontal = 16.dp),
                        accentColor = deityColor,
                        onClick = { onNavigateToDeityDetail(deity.id) },
                    ) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.Top,
                        ) {
                            Column(modifier = Modifier.weight(1f)) {
                                Text(
                                    "${viewModel.getTodayTeluguDay()} · ${viewModel.getTodayDayName()}",
                                    style = NityaPoojaTextStyles.GoldLabel,
                                    color = TempleGold,
                                )
                                Spacer(Modifier.height(8.dp))
                                Text(
                                    deity.nameTelugu,
                                    style = NityaPoojaTextStyles.TeluguDisplay.copy(
                                        fontSize = (22 * fontScale).sp,
                                    ),
                                    color = MaterialTheme.colorScheme.onSurface,
                                )
                                Text(
                                    deity.name,
                                    style = MaterialTheme.typography.titleMedium.copy(
                                        fontSize = (16 * fontScale).sp,
                                    ),
                                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                                )
                                Spacer(Modifier.height(8.dp))
                                deity.descriptionTelugu?.let {
                                    Text(
                                        it,
                                        style = MaterialTheme.typography.bodySmall.copy(
                                            fontSize = (12 * fontScale).sp,
                                        ),
                                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                                        maxLines = 2,
                                    )
                                }
                            }
                            Spacer(Modifier.width(16.dp))
                            DeityAvatar(
                                nameTelugu = deity.nameTelugu,
                                nameEnglish = deity.name,
                                deityColor = deityColor,
                                size = 72.dp,
                                showLabel = false,
                                imageResName = deity.imageResName,
                            )
                        }
                    }
                }
            }

            // Quick Access Row
            item {
                Column(modifier = Modifier.padding(horizontal = 16.dp)) {
                    SectionHeader(
                        titleTelugu = "త్వరిత ప్రాప్యత",
                        titleEnglish = "Quick Access",
                    )
                    Spacer(Modifier.height(16.dp))
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceEvenly,
                    ) {
                        QuickAccessCircle(
                            icon = Icons.Default.MusicNote,
                            labelTelugu = "హారతి",
                            labelEnglish = "Aarti",
                            onClick = onNavigateToAartis,
                        )
                        QuickAccessCircle(
                            icon = Icons.Default.SelfImprovement,
                            labelTelugu = "జపం",
                            labelEnglish = "Japa",
                            onClick = onNavigateToJapa,
                        )
                        QuickAccessCircle(
                            icon = Icons.Default.TempleHindu,
                            labelTelugu = "దేవాలయాలు",
                            labelEnglish = "Temples",
                            onClick = onNavigateToTemples,
                        )
                        QuickAccessCircle(
                            icon = Icons.Default.Celebration,
                            labelTelugu = "పండుగలు",
                            labelEnglish = "Festivals",
                            onClick = onNavigateToFestivals,
                        )
                        QuickAccessCircle(
                            icon = Icons.Default.Stars,
                            labelTelugu = "రాశిఫలం",
                            labelEnglish = "Rashifal",
                            onClick = onNavigateToRashifal,
                        )
                    }
                }
            }

            item { bannerAd?.invoke() }

            // Upcoming Festivals Countdown
            if (upcomingFestivals.isNotEmpty()) {
                item {
                    Column(modifier = Modifier.padding(start = 16.dp)) {
                        SectionHeader(
                            titleTelugu = "రాబోయే పండుగలు",
                            titleEnglish = "Upcoming Festivals",
                            modifier = Modifier.padding(end = 16.dp),
                        )
                        Spacer(Modifier.height(12.dp))
                        LazyRow(
                            horizontalArrangement = Arrangement.spacedBy(12.dp),
                            contentPadding = PaddingValues(end = 16.dp),
                        ) {
                            items(upcomingFestivals, key = { it.festival.id }) { upcoming ->
                                GlassmorphicCard(
                                    accentColor = TempleGold,
                                    cornerRadius = 16.dp,
                                    contentPadding = 16.dp,
                                    onClick = onNavigateToFestivals,
                                    modifier = Modifier.width(180.dp),
                                ) {
                                    Text(
                                        upcoming.festival.nameTelugu,
                                        style = MaterialTheme.typography.titleSmall.copy(
                                            fontSize = (14 * fontScale).sp,
                                        ),
                                        fontWeight = FontWeight.Bold,
                                        maxLines = 1,
                                    )
                                    Text(
                                        upcoming.festival.name,
                                        style = MaterialTheme.typography.labelSmall,
                                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                                        maxLines = 1,
                                    )
                                    Spacer(Modifier.height(8.dp))
                                    Text(
                                        if (upcoming.daysUntil == 0) "Today!"
                                        else if (upcoming.daysUntil == 1) "Tomorrow"
                                        else "${upcoming.daysUntil} days",
                                        style = MaterialTheme.typography.headlineSmall,
                                        fontWeight = FontWeight.Bold,
                                        color = TempleGold,
                                    )
                                    Text(
                                        upcoming.displayDate,
                                        style = MaterialTheme.typography.labelSmall,
                                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                                    )
                                }
                            }
                        }
                    }
                }
            }

            // Grahanam Banner (within 7 days)
            if (nextGrahanam != null && daysUntilGrahanam != null && !grahanamBannerDismissed) {
                item {
                    val grahanamAccent = if (isSystemInDarkTheme()) GrahanamLight else GrahanamDark
                    val typeNameTelugu = if (nextGrahanam.type == GrahanamType.SURYA) "సూర్య గ్రహణం" else "చంద్ర గ్రహణం"
                    val typeNameEnglish = if (nextGrahanam.type == GrahanamType.SURYA) "Surya Grahanam" else "Chandra Grahanam"
                    GlassmorphicCard(
                        modifier = Modifier.padding(horizontal = 16.dp),
                        accentColor = grahanamAccent,
                        onClick = onNavigateToPanchangam,
                    ) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.Top,
                        ) {
                            Column(modifier = Modifier.weight(1f)) {
                                Text(
                                    "గ్రహణం · GRAHANAM",
                                    style = NityaPoojaTextStyles.GoldLabel,
                                    color = grahanamAccent,
                                )
                                Spacer(Modifier.height(4.dp))
                                Text(
                                    typeNameTelugu,
                                    style = MaterialTheme.typography.titleMedium.copy(fontSize = (18 * fontScale).sp),
                                    fontWeight = FontWeight.Bold,
                                    color = grahanamAccent,
                                )
                                Text(
                                    typeNameEnglish,
                                    style = MaterialTheme.typography.bodyMedium.copy(fontSize = (14 * fontScale).sp),
                                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                                )
                                Spacer(Modifier.height(8.dp))
                                Text(
                                    when (daysUntilGrahanam) {
                                        0 -> "నేడు! / Today!"
                                        1 -> "రేపు / Tomorrow"
                                        else -> "$daysUntilGrahanam రోజులలో / In $daysUntilGrahanam days"
                                    },
                                    style = MaterialTheme.typography.headlineSmall.copy(fontSize = (20 * fontScale).sp),
                                    fontWeight = FontWeight.Bold,
                                    color = grahanamAccent,
                                )
                                Spacer(Modifier.height(4.dp))
                                Text(
                                    "పంచాంగం తెరవడానికి నొక్కండి · Tap for Sparsha/Moksham times",
                                    style = MaterialTheme.typography.labelSmall.copy(fontSize = (11 * fontScale).sp),
                                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                                )
                            }
                            Column(horizontalAlignment = Alignment.End) {
                                IconButton(
                                    onClick = { grahanamBannerDismissed = true },
                                    modifier = Modifier.size(28.dp),
                                ) {
                                    Icon(Icons.Default.Close, "Dismiss", tint = MaterialTheme.colorScheme.onSurfaceVariant, modifier = Modifier.size(16.dp))
                                }
                                Spacer(Modifier.height(8.dp))
                                Icon(Icons.Default.NightsStay, null, tint = grahanamAccent, modifier = Modifier.size(36.dp))
                            }
                        }
                    }
                }
            }

            // Moon Phase mini card
            item {
                val moonPhases = remember(locationInfo) {
                    panchangamViewModel.calculateUpcomingMoonPhases(locationInfo.timezone)
                }
                if (moonPhases.isNotEmpty()) {
                    MoonPhaseHomeCard(
                        phases = moonPhases,
                        fontScale = fontScale,
                        onTap = onNavigateToPanchangam,
                        modifier = Modifier.padding(horizontal = 16.dp),
                    )
                }
            }

            // All Deities — Horizontal Scroll
            item {
                Column(modifier = Modifier.padding(start = 16.dp)) {
                    SectionHeader(
                        titleTelugu = "అన్ని దేవతలు",
                        titleEnglish = "All Deities",
                        modifier = Modifier.padding(end = 16.dp),
                    )
                    Spacer(Modifier.height(12.dp))
                    LazyRow(
                        horizontalArrangement = Arrangement.spacedBy(16.dp),
                        contentPadding = PaddingValues(end = 16.dp),
                    ) {
                        items(deities, key = { it.id }) { deity ->
                            DeityAvatar(
                                nameTelugu = deity.nameTelugu,
                                nameEnglish = deity.name,
                                deityColor = resolveDeityColor(deity.colorTheme),
                                imageResName = deity.imageResName,
                                onClick = { onNavigateToDeityDetail(deity.id) },
                            )
                        }
                    }
                }
            }

            // Devotional Sections
            item {
                Column(
                    modifier = Modifier.padding(horizontal = 16.dp),
                    verticalArrangement = Arrangement.spacedBy(12.dp),
                ) {
                    SectionHeader(
                        titleTelugu = "భక్తి విభాగాలు",
                        titleEnglish = "Devotional Sections",
                    )
                    Spacer(Modifier.height(4.dp))
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(12.dp),
                    ) {
                        DevotionalSectionCard(
                            titleTelugu = "స్తోత్రాలు",
                            titleEnglish = "Stotrams",
                            icon = Icons.Default.MenuBook,
                            modifier = Modifier.weight(1f),
                            onClick = onNavigateToStotrams,
                            fontScale = fontScale,
                        )
                        DevotionalSectionCard(
                            titleTelugu = "కీర్తనలు",
                            titleEnglish = "Keertanalu",
                            icon = Icons.Default.LibraryMusic,
                            modifier = Modifier.weight(1f),
                            onClick = onNavigateToKeertanalu,
                            fontScale = fontScale,
                        )
                    }
                }
            }

            // Panchangam Snapshot
            // Sankalpam Card (collapsible)
            item {
                var sankalpamExpanded by remember { mutableStateOf(false) }
                GlassmorphicCard(
                    modifier = Modifier.padding(horizontal = 16.dp),
                    accentColor = TempleGold,
                    onClick = { sankalpamExpanded = !sankalpamExpanded },
                ) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically,
                    ) {
                        Text(
                            "సంకల్పం · SANKALPAM",
                            style = NityaPoojaTextStyles.GoldLabel,
                            color = TempleGold,
                        )
                        Icon(
                            if (sankalpamExpanded) Icons.Default.ExpandLess else Icons.Default.ExpandMore,
                            contentDescription = null,
                            tint = TempleGold,
                        )
                    }
                    if (sankalpamExpanded) {
                        Spacer(Modifier.height(8.dp))
                        SankalpamCard(
                            panchangamData = panchangamData,
                            userName = userName,
                            gotra = userGotra,
                            userNakshatra = userNakshatra,
                            city = locationInfo.city,
                            fontScale = fontScale,
                            timezone = locationInfo.timezone,
                            onNavigateToSettings = onNavigateToSettings,
                        )
                    }
                }
            }

            // Recently Viewed Section
            if (recentHistory.isNotEmpty()) {
                item {
                    Column(modifier = Modifier.padding(horizontal = 16.dp)) {
                        SectionHeader(
                            titleTelugu = "ఇటీవల చదివినవి",
                            titleEnglish = "Recently Viewed",
                        )
                        Spacer(Modifier.height(8.dp))
                    }
                }
                items(recentHistory.take(5), key = { "history_${it.id}" }) { entry ->
                    GlassmorphicCard(
                        modifier = Modifier.padding(horizontal = 16.dp),
                        onClick = { onNavigateToBookmark(entry.contentType, entry.contentId) },
                    ) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            verticalAlignment = Alignment.CenterVertically,
                        ) {
                            Icon(
                                Icons.Default.History,
                                contentDescription = null,
                                tint = TempleGold,
                                modifier = Modifier.size(18.dp),
                            )
                            Spacer(Modifier.width(12.dp))
                            Column(modifier = Modifier.weight(1f)) {
                                Text(
                                    entry.titleTelugu,
                                    style = MaterialTheme.typography.titleSmall.copy(fontSize = (14 * fontScale).sp),
                                    fontWeight = FontWeight.Medium,
                                    maxLines = 1,
                                )
                                Text(
                                    entry.title,
                                    style = MaterialTheme.typography.labelSmall,
                                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                                    maxLines = 1,
                                )
                            }
                        }
                    }
                }
            }

            // Bookmarks Section
            if (bookmarks.isNotEmpty()) {
                item {
                    Column(modifier = Modifier.padding(start = 16.dp)) {
                        SectionHeader(
                            titleTelugu = "మీ ఇష్టాలు",
                            titleEnglish = "Your Favorites",
                            modifier = Modifier.padding(end = 16.dp),
                        )
                        Spacer(Modifier.height(8.dp))
                        LazyRow(
                            horizontalArrangement = Arrangement.spacedBy(12.dp),
                            contentPadding = PaddingValues(end = 16.dp),
                        ) {
                            items(bookmarks.take(5), key = { "bm_${it.id}" }) { bookmark ->
                                GlassmorphicCard(
                                    cornerRadius = 12.dp,
                                    contentPadding = 12.dp,
                                    onClick = { onNavigateToBookmark(bookmark.contentType, bookmark.contentId) },
                                    modifier = Modifier.width(140.dp),
                                ) {
                                    Icon(
                                        Icons.Default.Bookmark,
                                        contentDescription = null,
                                        tint = TempleGold,
                                        modifier = Modifier.size(18.dp),
                                    )
                                    Spacer(Modifier.height(4.dp))
                                    Text(
                                        bookmark.contentType.replaceFirstChar { it.uppercase() },
                                        style = MaterialTheme.typography.titleSmall.copy(fontSize = (13 * fontScale).sp),
                                        fontWeight = FontWeight.Medium,
                                        maxLines = 1,
                                    )
                                    Text(
                                        "#${bookmark.contentId}",
                                        style = MaterialTheme.typography.labelSmall,
                                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                                    )
                                }
                            }
                        }
                    }
                }
            }
        }
    }
}

@Composable
private fun MoonPhaseHomeCard(
    phases: List<MoonPhaseEvent>,
    fontScale: Float,
    onTap: () -> Unit,
    modifier: Modifier = Modifier,
) {
    val isDark = androidx.compose.foundation.isSystemInDarkTheme()
    val accent = if (isDark) MoonPhaseLight else MoonPhaseDark
    val next = phases.first()
    val isPournami = next.type == MoonPhaseType.POURNAMI

    GlassmorphicCard(
        modifier = modifier,
        accentColor = accent,
        onClick = onTap,
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically,
        ) {
            Text(
                if (isPournami) "🌕" else "🌑",
                style = MaterialTheme.typography.headlineMedium,
            )
            Spacer(Modifier.width(12.dp))
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    "చంద్ర కళలు · MOON PHASES",
                    style = NityaPoojaTextStyles.GoldLabel,
                    color = accent,
                )
                Spacer(Modifier.height(2.dp))
                Text(
                    if (isPournami) "పౌర్ణమి · Pournami" else "అమావాస్య · Amavasya",
                    style = MaterialTheme.typography.titleMedium.copy(fontSize = (16 * fontScale).sp),
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.onSurface,
                )
                Text(
                    "${next.dateDisplay} · ${next.masaNameTelugu}",
                    style = MaterialTheme.typography.bodySmall.copy(fontSize = (12 * fontScale).sp),
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                )
            }
            Column(horizontalAlignment = Alignment.End) {
                Text(
                    when (next.daysUntil) {
                        0 -> "నేడు"
                        1 -> "రేపు"
                        else -> "${next.daysUntil}d"
                    },
                    style = MaterialTheme.typography.headlineSmall.copy(fontSize = (22 * fontScale).sp),
                    fontWeight = FontWeight.Bold,
                    color = accent,
                )
                Text(
                    next.timeFormatted,
                    style = MaterialTheme.typography.labelSmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                )
            }
        }
        // Second phase preview
        if (phases.size > 1) {
            val other = phases[1]
            val otherIsPournami = other.type == MoonPhaseType.POURNAMI
            Spacer(Modifier.height(8.dp))
            HorizontalDivider(color = accent.copy(alpha = 0.2f))
            Spacer(Modifier.height(8.dp))
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically,
            ) {
                Text(if (otherIsPournami) "🌕" else "🌑", style = MaterialTheme.typography.bodyLarge)
                Spacer(Modifier.width(8.dp))
                Text(
                    if (otherIsPournami) "పౌర్ణమి · ${other.dateDisplay}" else "అమావాస్య · ${other.dateDisplay}",
                    style = MaterialTheme.typography.labelSmall.copy(fontSize = (11 * fontScale).sp),
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    modifier = Modifier.weight(1f),
                )
                Text(
                    "${other.daysUntil}d",
                    style = MaterialTheme.typography.labelMedium,
                    color = accent.copy(alpha = 0.7f),
                )
            }
        }
    }
}

@Composable
private fun DevotionalSectionCard(
    titleTelugu: String,
    titleEnglish: String,
    icon: androidx.compose.ui.graphics.vector.ImageVector,
    modifier: Modifier = Modifier,
    fontScale: Float = 1f,
    onClick: () -> Unit = {},
) {
    GlassmorphicCard(
        modifier = modifier,
        cornerRadius = 16.dp,
        contentPadding = 16.dp,
        onClick = onClick,
    ) {
        Icon(
            icon,
            contentDescription = titleEnglish,
            tint = TempleGold,
            modifier = Modifier.size(28.dp),
        )
        Spacer(Modifier.height(8.dp))
        Text(
            titleTelugu,
            style = MaterialTheme.typography.titleMedium.copy(fontSize = (16 * fontScale).sp),
            fontWeight = FontWeight.Bold,
        )
        Text(
            titleEnglish,
            style = MaterialTheme.typography.bodySmall,
            color = MaterialTheme.colorScheme.onSurfaceVariant,
        )
    }
}

@Composable
private fun FestivalGreetingCard(
    festival: com.nityapooja.shared.data.local.entity.FestivalEntity,
    userName: String,
    gotra: String,
    nakshatra: String,
    fontScale: Float,
    samvatsaraNameTelugu: String,
    samvatsaraNameEnglish: String,
    modifier: Modifier = Modifier,
) {
    val greeting = getFestivalGreeting(festival.name, samvatsaraNameTelugu, samvatsaraNameEnglish)
    val isDark = isSystemInDarkTheme()
    val accentColor = greeting.accentColor

    GlassmorphicCard(
        modifier = modifier,
        accentColor = accentColor,
    ) {
        // Festival emoji + title
        Text(
            greeting.emoji,
            style = MaterialTheme.typography.displaySmall,
        )
        Spacer(Modifier.height(8.dp))

        // Personalized Telugu greeting
        val nameDisplay = if (userName.isNotBlank()) "$userName గారు" else ""
        Text(
            if (nameDisplay.isNotBlank()) "${greeting.greetingTelugu}, $nameDisplay!" else "${greeting.greetingTelugu}!",
            style = MaterialTheme.typography.titleLarge.copy(fontSize = (22 * fontScale).sp),
            fontWeight = FontWeight.Bold,
            color = accentColor,
        )

        // Gotra + Nakshatra line (only if entered)
        val profileParts = buildList {
            if (gotra.isNotBlank()) add("$gotra గోత్రం")
            if (nakshatra.isNotBlank()) add("$nakshatra నక్షత్రం")
        }
        if (profileParts.isNotEmpty()) {
            Spacer(Modifier.height(4.dp))
            Text(
                profileParts.joinToString(" · "),
                style = MaterialTheme.typography.labelMedium.copy(fontSize = (12 * fontScale).sp),
                color = accentColor.copy(alpha = 0.8f),
            )
        }

        Spacer(Modifier.height(12.dp))

        // Telugu blessing
        Text(
            greeting.blessingTelugu,
            style = MaterialTheme.typography.bodyMedium.copy(
                fontSize = (14 * fontScale).sp,
                lineHeight = (22 * fontScale).sp,
            ),
            color = MaterialTheme.colorScheme.onSurface,
        )

        Spacer(Modifier.height(8.dp))

        // English blessing
        Text(
            greeting.blessingEnglish,
            style = MaterialTheme.typography.bodySmall.copy(
                fontSize = (12 * fontScale).sp,
                lineHeight = (18 * fontScale).sp,
            ),
            color = MaterialTheme.colorScheme.onSurfaceVariant,
        )

        Spacer(Modifier.height(12.dp))

        // Share button
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.End,
        ) {
            TextButton(onClick = {
                val shareText = buildString {
                    append("${greeting.greetingTelugu}!")
                    if (nameDisplay.isNotBlank()) append(" $nameDisplay")
                    append("\n\n${greeting.blessingTelugu}")
                    append("\n\n${greeting.blessingEnglish}")
                    append("\n\n— NityaPooja")
                }
                shareText(shareText)
            }) {
                Icon(Icons.Default.Share, "Share", Modifier.size(16.dp))
                Spacer(Modifier.width(4.dp))
                Text("Share", style = MaterialTheme.typography.labelSmall)
            }
        }
    }
}

private data class FestivalGreeting(
    val emoji: String,
    val greetingTelugu: String,
    val blessingTelugu: String,
    val blessingEnglish: String,
    val accentColor: androidx.compose.ui.graphics.Color,
)

private fun getFestivalGreeting(festivalName: String, samvatsaraNameTelugu: String, samvatsaraNameEnglish: String): FestivalGreeting {
    return when {
        festivalName.contains("Ugadi", ignoreCase = true) -> FestivalGreeting(
            emoji = "🌸",
            greetingTelugu = "శుభ ఉగాది",
            blessingTelugu = "శ్రీ $samvatsaraNameTelugu నామ సంవత్సరం మీకు, మీ కుటుంబానికి సుఖ సంతోషాలు, ఆరోగ్యం, ఐశ్వర్యం కలిగించాలని ప్రార్థిస్తున్నాము. ఉగాది పచ్చడిలా జీవితంలోని అన్ని రుచులను ఆనందించండి.",
            blessingEnglish = "Happy Ugadi! May the $samvatsaraNameEnglish year bring joy, health and prosperity to you and your family. Embrace all flavors of life like the Ugadi Pachadi.",
            accentColor = androidx.compose.ui.graphics.Color(0xFFE91E63),
        )
        festivalName.contains("Rama Navami", ignoreCase = true) -> FestivalGreeting(
            emoji = "🏹",
            greetingTelugu = "శుభ శ్రీరామ నవమి",
            blessingTelugu = "శ్రీరాముడి ఆశీర్వాదం మీ కుటుంబంపై సదా ఉండాలి. ధర్మం, సత్యం మీ మార్గదర్శకాలు కావాలి. సీతారాముల కళ్యాణం మీ ఇంట సంతోషాన్ని నింపాలి.",
            blessingEnglish = "Happy Sri Rama Navami! May Lord Rama's blessings guide your family on the path of dharma and truth.",
            accentColor = androidx.compose.ui.graphics.Color(0xFFFF9800),
        )
        festivalName.contains("Vinayaka", ignoreCase = true) -> FestivalGreeting(
            emoji = "🐘",
            greetingTelugu = "శుభ వినాయక చవితి",
            blessingTelugu = "విఘ్నేశ్వరుడు మీ అన్ని అడ్డంకులను తొలగించి, విజయాలు ప్రసాదించాలి. గణపతి బప్పా మోరియా!",
            blessingEnglish = "Happy Vinayaka Chavithi! May Lord Ganesha remove all obstacles and bless your family with success and wisdom.",
            accentColor = androidx.compose.ui.graphics.Color(0xFFFF5722),
        )
        festivalName.contains("Dasara", ignoreCase = true) || festivalName.contains("Vijayadashami", ignoreCase = true) -> FestivalGreeting(
            emoji = "🪷",
            greetingTelugu = "శుభ విజయదశమి",
            blessingTelugu = "దుర్గామాత మీకు విజయాన్ని, ధైర్యాన్ని ప్రసాదించాలి. ఈ శుభ సమయంలో మీ కొత్త ప్రయత్నాలన్నీ విజయవంతం కావాలి.",
            blessingEnglish = "Happy Dasara! May Goddess Durga bless you with victory and courage. May all your new endeavors succeed.",
            accentColor = androidx.compose.ui.graphics.Color(0xFF9C27B0),
        )
        festivalName.contains("Deepavali", ignoreCase = true) || festivalName.contains("Diwali", ignoreCase = true) -> FestivalGreeting(
            emoji = "🪔",
            greetingTelugu = "శుభ దీపావళి",
            blessingTelugu = "దీపాల వెలుగులో మీ ఇల్లు, మీ జీవితం ఎల్లప్పుడూ ప్రకాశించాలి. లక్ష్మీదేవి మీ కుటుంబాన్ని ఆశీర్వదించాలి.",
            blessingEnglish = "Happy Deepavali! May the light of Diwali fill your home with happiness and prosperity forever.",
            accentColor = androidx.compose.ui.graphics.Color(0xFFFFC107),
        )
        festivalName.contains("Sankranti", ignoreCase = true) -> FestivalGreeting(
            emoji = "🌾",
            greetingTelugu = "శుభ మకర సంక్రాంతి",
            blessingTelugu = "సూర్యభగవానుడి కృపతో మీ జీవితంలో కొత్త శుభారంభం కలగాలి. సంక్రాంతి లక్ష్ములు మీ ఇంట సిరిసంపదలు నింపాలి.",
            blessingEnglish = "Happy Sankranti! May the harvest season bring new beginnings, abundance and prosperity to your family.",
            accentColor = androidx.compose.ui.graphics.Color(0xFF4CAF50),
        )
        festivalName.contains("Shivaratri", ignoreCase = true) -> FestivalGreeting(
            emoji = "🔱",
            greetingTelugu = "శుభ మహా శివరాత్రి",
            blessingTelugu = "ఓం నమః శివాయ! మహాదేవుడి ఆశీర్వాదం మీ కుటుంబంపై ఎల్లప్పుడూ ఉండాలి. శివుని కృపతో మీ జీవితం శాంతిమయం కావాలి.",
            blessingEnglish = "Happy Maha Shivaratri! Om Namah Shivaya! May Lord Shiva's blessings bring peace and divine grace to your family.",
            accentColor = androidx.compose.ui.graphics.Color(0xFF3F51B5),
        )
        festivalName.contains("Hanuman", ignoreCase = true) -> FestivalGreeting(
            emoji = "🙏",
            greetingTelugu = "శుభ హనుమాన్ జయంతి",
            blessingTelugu = "ఆంజనేయస్వామి మీకు బలం, ధైర్యం, భక్తి ప్రసాదించాలి. సుందరకాండ పారాయణంతో మీ ఇంట శాంతి నెలకొనాలి.",
            blessingEnglish = "Happy Hanuman Jayanti! May Lord Hanuman bless you with strength, courage and unwavering devotion.",
            accentColor = androidx.compose.ui.graphics.Color(0xFFFF5722),
        )
        festivalName.contains("Janmashtami", ignoreCase = true) || festivalName.contains("Krishna", ignoreCase = true) -> FestivalGreeting(
            emoji = "🦚",
            greetingTelugu = "శుభ కృష్ణ జన్మాష్టమి",
            blessingTelugu = "శ్రీకృష్ణ భగవానుడు మీ జీవితంలో ఆనందం, ప్రేమ నింపాలి. గీతాచార్యుడి బోధనలు మీకు మార్గదర్శకం కావాలి.",
            blessingEnglish = "Happy Krishna Janmashtami! May Lord Krishna fill your life with joy, love and divine wisdom.",
            accentColor = androidx.compose.ui.graphics.Color(0xFF1565C0),
        )
        festivalName.contains("Varalakshmi", ignoreCase = true) -> FestivalGreeting(
            emoji = "🪷",
            greetingTelugu = "శుభ వరలక్ష్మీ వ్రతం",
            blessingTelugu = "వరలక్ష్మీ అమ్మవారు మీ కుటుంబానికి సంపద, ఆరోగ్యం, సౌభాగ్యం ప్రసాదించాలి. అమ్మవారి కృపతో మీ ఇల్లు సిరిసంపదలతో నిండాలి.",
            blessingEnglish = "Happy Varalakshmi Vratam! May Goddess Lakshmi bless your family with wealth, health and happiness.",
            accentColor = androidx.compose.ui.graphics.Color(0xFFE91E63),
        )
        festivalName.contains("Bathukamma", ignoreCase = true) -> FestivalGreeting(
            emoji = "🌺",
            greetingTelugu = "శుభ బతుకమ్మ",
            blessingTelugu = "బతుకమ్మ పండుగ మీ జీవితంలో పూలలా అందం, సుగంధం నింపాలి. గౌరమ్మ తల్లి మిమ్మల్ని ఆశీర్వదించాలి.",
            blessingEnglish = "Happy Bathukamma! May Goddess Gauri bless your life with beauty and fragrance like the Bathukamma flowers.",
            accentColor = androidx.compose.ui.graphics.Color(0xFFE91E63),
        )
        festivalName.contains("Bonalu", ignoreCase = true) -> FestivalGreeting(
            emoji = "🏺",
            greetingTelugu = "శుభ బోనాలు",
            blessingTelugu = "మహంకాళి అమ్మవారు మీ కుటుంబాన్ని రక్షించి, ఆశీర్వదించాలి. బోనాల పండుగ మీకు శుభం కలిగించాలి.",
            blessingEnglish = "Happy Bonalu! May Goddess Mahankali protect and bless your family with prosperity.",
            accentColor = androidx.compose.ui.graphics.Color(0xFFC62828),
        )
        festivalName.contains("Nagula", ignoreCase = true) -> FestivalGreeting(
            emoji = "🐍",
            greetingTelugu = "శుభ నాగుల చవితి",
            blessingTelugu = "నాగ దేవతల ఆశీర్వాదం మీ కుటుంబంపై సదా ఉండాలి. మీ ఇంట సుఖ సంతోషాలు నెలకొనాలి.",
            blessingEnglish = "Happy Nagula Chavithi! May the serpent gods bless your family with health and happiness.",
            accentColor = androidx.compose.ui.graphics.Color(0xFF4CAF50),
        )
        festivalName.contains("Kartika Pournami", ignoreCase = true) || festivalName.contains("Karthika", ignoreCase = true) -> FestivalGreeting(
            emoji = "🪔",
            greetingTelugu = "శుభ కార్తీక పౌర్ణమి",
            blessingTelugu = "కార్తీక దీపాల వెలుగులో మీ జీవితం ప్రకాశించాలి. శివకేశవుల కృపతో మీ కుటుంబం సుఖంగా ఉండాలి.",
            blessingEnglish = "Happy Karthika Pournami! May the sacred lamps of Karthika illuminate your life with divine grace.",
            accentColor = androidx.compose.ui.graphics.Color(0xFFFF9800),
        )
        festivalName.contains("Arudra", ignoreCase = true) -> FestivalGreeting(
            emoji = "🔱",
            greetingTelugu = "శుభ ఆరుద్ర దర్శనం",
            blessingTelugu = "నటరాజ స్వామి తాండవ నృత్యంతో మీ జీవితంలో కొత్త శక్తి నింపాలి. శివుని కృపతో మీకు శాంతి లభించాలి.",
            blessingEnglish = "Happy Arudra Darshanam! May Lord Nataraja's cosmic dance fill your life with new energy and peace.",
            accentColor = androidx.compose.ui.graphics.Color(0xFF3F51B5),
        )
        festivalName.contains("Vaikunta", ignoreCase = true) -> FestivalGreeting(
            emoji = "🙏",
            greetingTelugu = "శుభ వైకుంఠ ఏకాదశి",
            blessingTelugu = "వైకుంఠ ద్వారాలు తెరుచుకునే ఈ పవిత్ర దినం మీకు మోక్ష మార్గం చూపాలి. శ్రీమన్నారాయణుని కృపతో మీ జీవితం ధన్యం కావాలి.",
            blessingEnglish = "Happy Vaikunta Ekadashi! May the gates of Vaikuntam open divine blessings upon you and your family.",
            accentColor = androidx.compose.ui.graphics.Color(0xFF1565C0),
        )
        festivalName.contains("Ratha Saptami", ignoreCase = true) -> FestivalGreeting(
            emoji = "☀",
            greetingTelugu = "శుభ రథ సప్తమి",
            blessingTelugu = "సూర్య భగవానుడి తేజస్సు మీ జీవితంలో వెలుగు నింపాలి. ఆరోగ్యం, శక్తి మీకు సదా తోడుండాలి.",
            blessingEnglish = "Happy Ratha Saptami! May the Sun God bless you with radiant health and energy.",
            accentColor = androidx.compose.ui.graphics.Color(0xFFFF8F00),
        )
        festivalName.contains("Atla Taddi", ignoreCase = true) -> FestivalGreeting(
            emoji = "🌙",
            greetingTelugu = "శుభ అట్ల తద్దె",
            blessingTelugu = "గౌరీదేవి మీ దాంపత్య జీవితాన్ని ఆశీర్వదించాలి. మీ కుటుంబంలో ప్రేమ, అనురాగం ఎల్లప్పుడూ నిండి ఉండాలి.",
            blessingEnglish = "Happy Atla Taddi! May Goddess Gauri bless your married life with love, health and togetherness.",
            accentColor = androidx.compose.ui.graphics.Color(0xFFAD1457),
        )
        festivalName.contains("Sammakka", ignoreCase = true) -> FestivalGreeting(
            emoji = "🪘",
            greetingTelugu = "శుభ సమ్మక్క సారలమ్మ జాతర",
            blessingTelugu = "సమ్మక్క సారలమ్మ తల్లుల ఆశీర్వాదం మీపై సదా ఉండాలి. ధైర్యం, శక్తి మీకు తోడుండాలి.",
            blessingEnglish = "Happy Sammakka Saralamma Jatara! May the brave mother goddesses bless you with courage and strength.",
            accentColor = androidx.compose.ui.graphics.Color(0xFF2E7D32),
        )
        festivalName.contains("Skanda", ignoreCase = true) -> FestivalGreeting(
            emoji = "🔱",
            greetingTelugu = "శుభ స్కంద షష్ఠి",
            blessingTelugu = "సుబ్రమణ్య స్వామి మీ జీవితంలోని అన్ని అరిష్టాలను నాశనం చేసి, విజయాన్ని ప్రసాదించాలి.",
            blessingEnglish = "Happy Skanda Shashti! May Lord Subramanya destroy all evils and bless you with victory.",
            accentColor = androidx.compose.ui.graphics.Color(0xFFF57F17),
        )
        festivalName.contains("Bhogi", ignoreCase = true) -> FestivalGreeting(
            emoji = "🔥",
            greetingTelugu = "శుభ భోగి",
            blessingTelugu = "భోగి మంటలతో పాతవి పోయి, కొత్త శుభారంభం కలగాలి. మీ జీవితంలో సంతోషం, సమృద్ధి నిండాలి.",
            blessingEnglish = "Happy Bhogi! May the bonfire of Bhogi burn away the old and bring new beginnings and prosperity.",
            accentColor = androidx.compose.ui.graphics.Color(0xFFFF5722),
        )
        festivalName.contains("Kanuma", ignoreCase = true) -> FestivalGreeting(
            emoji = "🐄",
            greetingTelugu = "శుభ కానుమ",
            blessingTelugu = "కానుమ పండుగ మీ కుటుంబంలో ఆనందం, సమృద్ధి నింపాలి. ప్రకృతి మాత మిమ్మల్ని ఆశీర్వదించాలి.",
            blessingEnglish = "Happy Kanuma! May this day of gratitude to nature and cattle bring joy and abundance to your family.",
            accentColor = androidx.compose.ui.graphics.Color(0xFF4CAF50),
        )
        festivalName.contains("Navaratri", ignoreCase = true) -> FestivalGreeting(
            emoji = "🪷",
            greetingTelugu = "శుభ నవరాత్రులు",
            blessingTelugu = "తొమ్మిది రాత్రులు దేవి అమ్మవారి ఆశీర్వాదం మీపై ఉండాలి. శక్తి, సంపద, విద్య మీకు ప్రసాదించాలి.",
            blessingEnglish = "Happy Navaratri! May the nine nights of Goddess Durga bless you with power, prosperity and wisdom.",
            accentColor = androidx.compose.ui.graphics.Color(0xFF9C27B0),
        )
        else -> FestivalGreeting(
            emoji = "🙏",
            greetingTelugu = "శుభ పండుగ",
            blessingTelugu = "ఈ పవిత్ర దినం మీకు, మీ కుటుంబానికి శుభాలు కలిగించాలి.",
            blessingEnglish = "Wishing you and your family a blessed festival day.",
            accentColor = TempleGold,
        )
    }
}
