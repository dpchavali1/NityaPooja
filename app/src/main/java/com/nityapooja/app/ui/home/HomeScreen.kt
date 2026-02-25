package com.nityapooja.app.ui.home

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import android.content.Intent
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.nityapooja.app.ui.components.*
import com.nityapooja.app.ui.panchangam.PanchangamViewModel
import com.nityapooja.app.ui.theme.*

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HomeScreen(
    viewModel: HomeViewModel = hiltViewModel(),
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
    fontSizeViewModel: FontSizeViewModel = hiltViewModel(),
) {
    val deities by viewModel.deities.collectAsStateWithLifecycle()
    val todayShloka by viewModel.todayShloka.collectAsStateWithLifecycle()
    val deityOfDay by viewModel.deityOfTheDay.collectAsStateWithLifecycle()
    val bookmarks by viewModel.bookmarks.collectAsStateWithLifecycle()
    val userName by viewModel.userName.collectAsStateWithLifecycle()
    val userGotra by viewModel.userGotra.collectAsStateWithLifecycle()
    val userNakshatra by viewModel.userNakshatra.collectAsStateWithLifecycle()
    val recentHistory by viewModel.recentHistory.collectAsStateWithLifecycle()
    val upcomingFestivals by viewModel.upcomingFestivals.collectAsStateWithLifecycle()

    val fontSize by fontSizeViewModel.fontSize.collectAsStateWithLifecycle()
    val fontScale = fontSize / 16f

    val panchangamViewModel: PanchangamViewModel = hiltViewModel()
    val locationInfo by panchangamViewModel.locationInfo.collectAsStateWithLifecycle()
    val panchangamData = remember(locationInfo) {
        panchangamViewModel.calculatePanchangam(locationInfo.lat, locationInfo.lng, locationInfo.timezone)
    }

    val context = LocalContext.current
    val salutation = userName.ifBlank { "భక్తా" }
    val greetingTelugu = viewModel.getGreetingTelugu()
    val greetingEnglish = viewModel.getGreetingEnglish()

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Column {
                        Text(
                            "$greetingTelugu, $salutation \uD83D\uDE4F",
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
            // ═══ Hero: Daily Shloka Card ═══
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
                                val shareText = buildString {
                                    append("✨ నేటి శ్లోకం · Today's Blessing ✨\n\n")
                                    append(shloka.textSanskrit)
                                    shloka.meaningTelugu?.let { append("\n\n$it") }
                                    shloka.meaningEnglish?.let { append("\n\n$it") }
                                    shloka.source?.let { append("\n\n— $it") }
                                    append("\n\n🙏 Shared via NityaPooja")
                                }
                                val intent = Intent(Intent.ACTION_SEND).apply {
                                    type = "text/plain"
                                    putExtra(Intent.EXTRA_TEXT, shareText)
                                }
                                context.startActivity(Intent.createChooser(intent, "Share Shloka"))
                            }) {
                                Icon(Icons.Default.Share, "Share", Modifier.size(16.dp))
                                Spacer(Modifier.width(4.dp))
                                Text("Share", style = MaterialTheme.typography.labelSmall)
                            }
                        }
                    }
                }
            }

            // ═══ Deity of the Day ═══
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

            // ═══ Quick Access Row ═══
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

            // ═══ Upcoming Festivals Countdown ═══
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

            // ═══ All Deities — Horizontal Scroll ═══
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

            // ═══ Devotional Sections ═══
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

            // ═══ Panchangam Snapshot ═══
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
                            // Samvatsara & Masa
                            Text(
                                "${panchangamData.samvatsara.nameTelugu} · ${panchangamData.masa.nameTelugu}",
                                style = MaterialTheme.typography.bodySmall.copy(
                                    fontSize = (12 * fontScale).sp,
                                ),
                                fontWeight = FontWeight.Medium,
                                color = TempleGold.copy(alpha = 0.8f),
                            )
                            Spacer(Modifier.height(6.dp))
                            // Day of the week — prominent, clear in both themes
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
                            // Tithi with end time
                            Row {
                                Text(
                                    "తిథి: ",
                                    style = MaterialTheme.typography.labelMedium.copy(
                                        fontSize = (12 * fontScale).sp,
                                    ),
                                    color = TempleGold,
                                )
                                Text(
                                    "${panchangamData.tithi.nameTelugu} (${panchangamData.tithi.endTime} వరకు)",
                                    style = MaterialTheme.typography.bodySmall.copy(
                                        fontSize = (12 * fontScale).sp,
                                    ),
                                    color = MaterialTheme.colorScheme.onSurface,
                                )
                            }
                            // Nakshatra with end time
                            Row {
                                Text(
                                    "నక్షత్రం: ",
                                    style = MaterialTheme.typography.labelMedium.copy(
                                        fontSize = (12 * fontScale).sp,
                                    ),
                                    color = TempleGold,
                                )
                                Text(
                                    "${panchangamData.nakshatra.nameTelugu} (${panchangamData.nakshatra.endTime} వరకు)",
                                    style = MaterialTheme.typography.bodySmall.copy(
                                        fontSize = (12 * fontScale).sp,
                                    ),
                                    color = MaterialTheme.colorScheme.onSurface,
                                )
                            }
                            // Yoga
                            Row {
                                Text(
                                    "యోగం: ",
                                    style = MaterialTheme.typography.labelMedium.copy(
                                        fontSize = (12 * fontScale).sp,
                                    ),
                                    color = TempleGold,
                                )
                                Text(
                                    panchangamData.yoga.nameTelugu,
                                    style = MaterialTheme.typography.bodySmall.copy(
                                        fontSize = (12 * fontScale).sp,
                                    ),
                                    color = MaterialTheme.colorScheme.onSurface,
                                )
                            }
                            Spacer(Modifier.height(4.dp))
                            // Sunrise/Sunset
                            Text(
                                "☀ ${panchangamData.sunTimes.sunrise}  🌙 ${panchangamData.sunTimes.sunset}",
                                style = MaterialTheme.typography.labelSmall.copy(
                                    fontSize = (11 * fontScale).sp,
                                ),
                                color = MaterialTheme.colorScheme.onSurfaceVariant,
                            )
                            // Rahu Kaal
                            if (panchangamData.rahuKaal.isActive) {
                                Spacer(Modifier.height(4.dp))
                                Text(
                                    "⚠ రాహు కాలం: ${panchangamData.rahuKaal.startTime} - ${panchangamData.rahuKaal.endTime}",
                                    style = MaterialTheme.typography.labelSmall.copy(
                                        fontSize = (11 * fontScale).sp,
                                    ),
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

            // ═══ Ad Banner ═══
            item {
                BannerAd(modifier = Modifier.padding(horizontal = 16.dp))
            }

            // ═══ Sankalpam Card ═══
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

            // ═══ Recently Viewed Section ═══
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
                            // Navigate based on content type
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
                                    style = MaterialTheme.typography.titleSmall.copy(
                                        fontSize = (14 * fontScale).sp,
                                    ),
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

            // ═══ Bookmarks Section — Quick Favorites ═══
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
                                        style = MaterialTheme.typography.titleSmall.copy(
                                            fontSize = (13 * fontScale).sp,
                                        ),
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
            style = MaterialTheme.typography.titleMedium.copy(
                fontSize = (16 * fontScale).sp,
            ),
            fontWeight = FontWeight.Bold,
        )
        Text(
            titleEnglish,
            style = MaterialTheme.typography.bodySmall,
            color = MaterialTheme.colorScheme.onSurfaceVariant,
        )
    }
}
