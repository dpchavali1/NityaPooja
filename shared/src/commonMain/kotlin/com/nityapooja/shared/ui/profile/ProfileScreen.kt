package com.nityapooja.shared.ui.profile

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import org.koin.compose.viewmodel.koinViewModel
import com.nityapooja.shared.data.local.entity.BookmarkEntity
import com.nityapooja.shared.data.local.entity.ReadingHistoryEntity
import com.nityapooja.shared.ui.components.EmptyState
import com.nityapooja.shared.ui.components.GlassmorphicCard
import com.nityapooja.shared.ui.components.SectionHeader
import com.nityapooja.shared.ui.theme.TempleGold
import kotlinx.datetime.Instant
import kotlinx.datetime.TimeZone
import kotlinx.datetime.toLocalDateTime

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ProfileScreen(
    viewModel: ProfileViewModel = koinViewModel(),
    onBackClick: () -> Unit = {},
    onBookmarkClick: (String, Int) -> Unit = { _, _ -> },
) {
    val bookmarksByType by viewModel.bookmarksByType.collectAsState()
    val recentHistory by viewModel.recentHistory.collectAsState()
    var selectedTab by remember { mutableIntStateOf(0) }

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Column {
                        Text(
                            "ప్రొఫైల్",
                            style = MaterialTheme.typography.titleLarge,
                            fontWeight = FontWeight.Bold,
                        )
                        Text(
                            "Profile",
                            style = MaterialTheme.typography.labelSmall,
                            color = MaterialTheme.colorScheme.onSurfaceVariant,
                        )
                    }
                },
                navigationIcon = {
                    IconButton(onClick = onBackClick) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Back")
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(containerColor = MaterialTheme.colorScheme.surface),
            )
        },
    ) { padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding),
        ) {
            // Tabs
            TabRow(
                selectedTabIndex = selectedTab,
                containerColor = MaterialTheme.colorScheme.surface,
                contentColor = TempleGold,
            ) {
                Tab(
                    selected = selectedTab == 0,
                    onClick = { selectedTab = 0 },
                    text = {
                        Column(horizontalAlignment = Alignment.CenterHorizontally) {
                            Text("బుక్‌మార్క్‌లు", style = MaterialTheme.typography.labelSmall)
                            Text("Bookmarks", style = MaterialTheme.typography.labelSmall)
                        }
                    },
                    icon = { Icon(Icons.Default.Bookmark, null, Modifier.size(18.dp)) },
                )
                Tab(
                    selected = selectedTab == 1,
                    onClick = { selectedTab = 1 },
                    text = {
                        Column(horizontalAlignment = Alignment.CenterHorizontally) {
                            Text("ఇటీవల", style = MaterialTheme.typography.labelSmall)
                            Text("Recent", style = MaterialTheme.typography.labelSmall)
                        }
                    },
                    icon = { Icon(Icons.Default.History, null, Modifier.size(18.dp)) },
                )
            }

            when (selectedTab) {
                0 -> BookmarksTab(bookmarksByType, onBookmarkClick)
                1 -> RecentHistoryTab(recentHistory, onBookmarkClick, viewModel::clearHistory)
            }
        }
    }
}

@Composable
private fun BookmarksTab(
    bookmarksByType: Map<String, List<BookmarkEntity>>,
    onBookmarkClick: (String, Int) -> Unit,
) {
    if (bookmarksByType.isEmpty()) {
        Box(modifier = Modifier.fillMaxSize().padding(16.dp)) {
            EmptyState(
                icon = Icons.Default.BookmarkBorder,
                titleTelugu = "బుక్‌మార్క్‌లు లేవు",
                titleEnglish = "No bookmarks yet",
            )
        }
    } else {
        LazyColumn(
            contentPadding = PaddingValues(16.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp),
        ) {
            bookmarksByType.forEach { (type, bookmarks) ->
                item {
                    SectionHeader(
                        titleTelugu = getTypeLabelTelugu(type),
                        titleEnglish = getTypeLabelEnglish(type),
                    )
                    Spacer(Modifier.height(4.dp))
                }
                items(bookmarks, key = { it.id }) { bookmark ->
                    BookmarkCard(
                        bookmark = bookmark,
                        onClick = { onBookmarkClick(bookmark.contentType, bookmark.contentId) },
                    )
                }
                item { Spacer(Modifier.height(8.dp)) }
            }
        }
    }
}

@Composable
private fun RecentHistoryTab(
    history: List<ReadingHistoryEntity>,
    onItemClick: (String, Int) -> Unit,
    onClearHistory: () -> Unit,
) {
    if (history.isEmpty()) {
        Box(modifier = Modifier.fillMaxSize().padding(16.dp)) {
            EmptyState(
                icon = Icons.Default.History,
                titleTelugu = "చరిత్ర లేదు",
                titleEnglish = "No reading history yet",
            )
        }
    } else {
        LazyColumn(
            contentPadding = PaddingValues(16.dp),
            verticalArrangement = Arrangement.spacedBy(8.dp),
        ) {
            item {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically,
                ) {
                    SectionHeader(
                        titleTelugu = "ఇటీవల చదివినవి",
                        titleEnglish = "Recently Viewed",
                    )
                    TextButton(onClick = onClearHistory) {
                        Text("Clear", style = MaterialTheme.typography.labelSmall, color = TempleGold)
                    }
                }
            }
            items(history, key = { it.id }) { entry ->
                HistoryCard(
                    entry = entry,
                    onClick = { onItemClick(entry.contentType, entry.contentId) },
                )
            }
        }
    }
}

@Composable
private fun BookmarkCard(
    bookmark: BookmarkEntity,
    onClick: () -> Unit,
) {
    GlassmorphicCard(
        onClick = onClick,
        contentPadding = 14.dp,
        accentColor = TempleGold,
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically,
        ) {
            Icon(
                getTypeIcon(bookmark.contentType),
                null,
                tint = TempleGold,
                modifier = Modifier.size(20.dp),
            )
            Spacer(Modifier.width(12.dp))
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    "${getTypeLabelTelugu(bookmark.contentType)} / ${getTypeLabelEnglish(bookmark.contentType)}",
                    style = MaterialTheme.typography.titleSmall,
                    fontWeight = FontWeight.Bold,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis,
                )
                Text(
                    "ID: ${bookmark.contentId}",
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                )
            }
            Icon(Icons.Default.ChevronRight, null, tint = MaterialTheme.colorScheme.onSurfaceVariant, modifier = Modifier.size(18.dp))
        }
    }
}

@Composable
private fun HistoryCard(
    entry: ReadingHistoryEntity,
    onClick: () -> Unit,
) {
    val timeStr = remember(entry.timestamp) {
        try {
            val instant = Instant.fromEpochMilliseconds(entry.timestamp)
            val localDateTime = instant.toLocalDateTime(TimeZone.currentSystemDefault())
            val month = localDateTime.month.name.take(3).lowercase()
                .replaceFirstChar { it.uppercase() }
            val day = localDateTime.dayOfMonth.toString().padStart(2, '0')
            val hour = if (localDateTime.hour % 12 == 0) 12 else localDateTime.hour % 12
            val minute = localDateTime.minute.toString().padStart(2, '0')
            val amPm = if (localDateTime.hour < 12) "AM" else "PM"
            "$month $day, $hour:$minute $amPm"
        } catch (_: Exception) {
            ""
        }
    }

    GlassmorphicCard(
        onClick = onClick,
        contentPadding = 14.dp,
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically,
        ) {
            Icon(
                getTypeIcon(entry.contentType),
                null,
                tint = TempleGold,
                modifier = Modifier.size(20.dp),
            )
            Spacer(Modifier.width(12.dp))
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    entry.titleTelugu,
                    style = MaterialTheme.typography.titleSmall,
                    fontWeight = FontWeight.Medium,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis,
                )
                Text(
                    entry.title,
                    style = MaterialTheme.typography.labelSmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis,
                )
            }
            Text(
                timeStr,
                style = MaterialTheme.typography.labelSmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
            )
        }
    }
}

private fun getTypeLabelTelugu(type: String): String = when (type) {
    "aarti" -> "హారతి"
    "stotram" -> "స్తోత్రం"
    "keertana" -> "కీర్తన"
    "mantra" -> "మంత్రం"
    "temple" -> "దేవాలయం"
    "bhajan" -> "భజన"
    "suprabhatam" -> "సుప్రభాతం"
    "ashtotra" -> "అష్టోత్తరం"
    "chalisa" -> "చాలీసా"
    else -> type
}

private fun getTypeLabelEnglish(type: String): String = when (type) {
    "aarti" -> "Aarti"
    "stotram" -> "Stotram"
    "keertana" -> "Keertana"
    "mantra" -> "Mantra"
    "temple" -> "Temple"
    "bhajan" -> "Bhajan"
    "suprabhatam" -> "Suprabhatam"
    "ashtotra" -> "Ashtotra"
    "chalisa" -> "Chalisa"
    else -> type
}

private fun getTypeIcon(type: String) = when (type) {
    "aarti" -> Icons.Default.MusicNote
    "stotram" -> Icons.Default.MenuBook
    "keertana" -> Icons.Default.LibraryMusic
    "mantra" -> Icons.Default.SelfImprovement
    "temple" -> Icons.Default.TempleHindu
    "bhajan" -> Icons.Default.MusicNote
    "suprabhatam" -> Icons.Default.WbSunny
    "ashtotra" -> Icons.Default.AutoAwesome
    "chalisa" -> Icons.Default.MenuBook
    else -> Icons.Default.Article
}
