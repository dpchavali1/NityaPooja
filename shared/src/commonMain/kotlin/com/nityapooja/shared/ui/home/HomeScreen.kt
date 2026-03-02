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
import com.nityapooja.shared.ui.theme.*
import com.nityapooja.shared.platform.shareText
import androidx.compose.foundation.isSystemInDarkTheme
import kotlinx.datetime.Clock
import kotlinx.datetime.TimeZone
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
    onNavigateToRashifal: () -> Unit = {},
    onNavigateToBookmark: (String, Int) -> Unit = { _, _ -> },
    fontSizeViewModel: FontSizeViewModel = koinViewModel(),
    bannerAd: (@Composable () -> Unit)? = null,
) {
    val deities by viewModel.deities.collectAsState()
    val todayShloka by viewModel.todayShloka.collectAsState()
    val deityOfDay by viewModel.deityOfTheDay.collectAsState()
    val bookmarks by viewModel.bookmarks.collectAsState()
    val userName by viewModel.userName.collectAsState()
    val userGotra by viewModel.userGotra.collectAsState()
    val userNakshatra by viewModel.userNakshatra.collectAsState()
    val recentHistory by viewModel.recentHistory.collectAsState()
    val upcomingFestivals by viewModel.upcomingFestivals.collectAsState()

    val fontSize by fontSizeViewModel.fontSize.collectAsState()
    val fontScale = fontSize / 16f

    val panchangamViewModel: PanchangamViewModel = koinViewModel()
    val locationInfo by panchangamViewModel.locationInfo.collectAsState()
    val panchangamData = remember(locationInfo) {
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
            item {
                GlassmorphicCard(
                    modifier = Modifier.padding(horizontal = 16.dp),
                    cornerRadius = 16.dp,
                    contentPadding = 16.dp,
                    accentColor = TempleGold,
                    onClick = onNavigateToPanchangam,
                ) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.Top,
                    ) {
                        Column(modifier = Modifier.weight(1f)) {
                            Text(
                                "పంచాంగం · PANCHANGAM",
                                style = NityaPoojaTextStyles.GoldLabel,
                                color = TempleGold,
                            )
                            Spacer(Modifier.height(4.dp))
                            Text(
                                "${panchangamData.samvatsara.nameTelugu} · ${panchangamData.masa.nameTelugu}",
                                style = MaterialTheme.typography.bodySmall.copy(
                                    fontSize = (12 * fontScale).sp,
                                ),
                                fontWeight = FontWeight.Medium,
                                color = TempleGold.copy(alpha = 0.8f),
                            )
                            Spacer(Modifier.height(6.dp))
                            Text(
                                panchangamData.teluguDay,
                                style = MaterialTheme.typography.titleMedium.copy(
                                    fontSize = (18 * fontScale).sp,
                                ),
                                fontWeight = FontWeight.Bold,
                                color = TempleGold,
                            )
                            Text(
                                panchangamData.englishDay,
                                style = MaterialTheme.typography.bodyMedium.copy(
                                    fontSize = (14 * fontScale).sp,
                                ),
                                fontWeight = FontWeight.Medium,
                                color = MaterialTheme.colorScheme.onSurface,
                            )
                            Spacer(Modifier.height(8.dp))
                            Row {
                                Text("తిథి: ", style = MaterialTheme.typography.labelMedium.copy(fontSize = (12 * fontScale).sp), color = TempleGold)
                                Text("${panchangamData.tithi.nameTelugu} (${panchangamData.tithi.endTime} వరకు)", style = MaterialTheme.typography.bodySmall.copy(fontSize = (12 * fontScale).sp), color = MaterialTheme.colorScheme.onSurface)
                            }
                            Row {
                                Text("నక్షత్రం: ", style = MaterialTheme.typography.labelMedium.copy(fontSize = (12 * fontScale).sp), color = TempleGold)
                                Text("${panchangamData.nakshatra.nameTelugu} (${panchangamData.nakshatra.endTime} వరకు)", style = MaterialTheme.typography.bodySmall.copy(fontSize = (12 * fontScale).sp), color = MaterialTheme.colorScheme.onSurface)
                            }
                            Row {
                                Text("యోగం: ", style = MaterialTheme.typography.labelMedium.copy(fontSize = (12 * fontScale).sp), color = TempleGold)
                                Text(panchangamData.yoga.nameTelugu, style = MaterialTheme.typography.bodySmall.copy(fontSize = (12 * fontScale).sp), color = MaterialTheme.colorScheme.onSurface)
                            }
                            Spacer(Modifier.height(4.dp))
                            Text(
                                "☀ ${panchangamData.sunTimes.sunrise}  🌙 ${panchangamData.sunTimes.sunset}",
                                style = MaterialTheme.typography.labelSmall.copy(fontSize = (11 * fontScale).sp),
                                color = MaterialTheme.colorScheme.onSurfaceVariant,
                            )
                            if (panchangamData.rahuKaal.isActive) {
                                Spacer(Modifier.height(4.dp))
                                Text(
                                    "⚠ రాహు కాలం: ${panchangamData.rahuKaal.startTime} - ${panchangamData.rahuKaal.endTime}",
                                    style = MaterialTheme.typography.labelSmall.copy(fontSize = (11 * fontScale).sp),
                                    color = DeepVermillion,
                                    fontWeight = FontWeight.Bold,
                                )
                            }
                        }
                        Icon(
                            Icons.Default.CalendarMonth,
                            contentDescription = null,
                            tint = TempleGold,
                            modifier = Modifier.size(32.dp),
                        )
                    }
                }
            }

            // Sankalpam Card
            item {
                SankalpamCard(
                    panchangamData = panchangamData,
                    userName = userName,
                    gotra = userGotra,
                    userNakshatra = userNakshatra,
                    city = locationInfo.city,
                    fontScale = fontScale,
                    timezone = locationInfo.timezone,
                    onNavigateToSettings = onNavigateToSettings,
                    modifier = Modifier.padding(horizontal = 16.dp),
                )
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
                        onClick = {
                            when (entry.contentType) {
                                "aarti" -> onNavigateToAartiDetail(entry.contentId)
                                "stotram" -> onNavigateToStotrams()
                                "keertana" -> onNavigateToKeertanalu()
                                "temple" -> onNavigateToTemples()
                                else -> {}
                            }
                        },
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
