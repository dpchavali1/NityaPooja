package com.nityapooja.app.ui.festival

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.nityapooja.app.data.local.entity.FestivalEntity
import com.nityapooja.app.ui.components.EmptyState
import com.nityapooja.app.ui.components.GlassmorphicCard
import com.nityapooja.app.ui.theme.AuspiciousGreen
import com.nityapooja.app.ui.theme.TempleGold
import com.nityapooja.app.ui.theme.DeepVermillion
import java.time.LocalDate
import java.time.YearMonth
import java.time.format.DateTimeFormatter
import java.time.format.TextStyle
import java.util.Locale

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun FestivalListScreen(
    viewModel: FestivalViewModel = hiltViewModel(),
    onBack: () -> Unit,
) {
    val festivals by viewModel.allFestivals.collectAsStateWithLifecycle()
    var showCalendar by remember { mutableStateOf(false) }

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Column {
                        Text(
                            "పండుగలు",
                            style = MaterialTheme.typography.titleLarge,
                            fontWeight = FontWeight.Bold,
                        )
                        Text(
                            "Festivals",
                            style = MaterialTheme.typography.labelSmall,
                            color = MaterialTheme.colorScheme.onSurfaceVariant,
                        )
                    }
                },
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Back")
                    }
                },
                actions = {
                    IconButton(onClick = { showCalendar = !showCalendar }) {
                        Icon(
                            if (showCalendar) Icons.Default.ViewList else Icons.Default.CalendarMonth,
                            contentDescription = "Toggle view",
                            tint = TempleGold,
                        )
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.surface,
                ),
            )
        },
    ) { padding ->
        when {
            festivals.isEmpty() -> {
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(padding)
                        .padding(16.dp),
                ) {
                    EmptyState(
                        icon = Icons.Default.Celebration,
                        titleTelugu = "పండుగలు లేవు",
                        titleEnglish = "No festivals found",
                    )
                }
            }
            showCalendar -> {
                FestivalCalendarView(
                    festivals = festivals,
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(padding),
                )
            }
            else -> {
                LazyColumn(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(padding),
                    contentPadding = PaddingValues(16.dp),
                    verticalArrangement = Arrangement.spacedBy(12.dp),
                ) {
                    items(festivals, key = { it.id }) { festival ->
                        FestivalListItem(festival = festival)
                    }
                }
            }
        }
    }
}

@Composable
private fun FestivalCalendarView(
    festivals: List<FestivalEntity>,
    modifier: Modifier = Modifier,
) {
    var currentMonth by remember { mutableStateOf(YearMonth.now()) }
    val formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd")
    var selectedFestival by remember { mutableStateOf<FestivalEntity?>(null) }

    // Map dates to festivals
    val festivalDates = remember(festivals, currentMonth) {
        festivals.mapNotNull { festival ->
            val dateStr = festival.dateThisYear ?: return@mapNotNull null
            try {
                val date = LocalDate.parse(dateStr, formatter)
                if (date.year == currentMonth.year && date.month == currentMonth.month) {
                    date.dayOfMonth to festival
                } else null
            } catch (_: Exception) { null }
        }.toMap()
    }

    Column(modifier = modifier.padding(16.dp)) {
        // Month navigation
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically,
        ) {
            IconButton(onClick = { currentMonth = currentMonth.minusMonths(1) }) {
                Icon(Icons.Default.ChevronLeft, "Previous month")
            }
            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                Text(
                    currentMonth.month.getDisplayName(TextStyle.FULL, Locale.ENGLISH),
                    style = MaterialTheme.typography.titleLarge,
                    fontWeight = FontWeight.Bold,
                )
                Text(
                    "${currentMonth.year}",
                    style = MaterialTheme.typography.labelMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                )
            }
            IconButton(onClick = { currentMonth = currentMonth.plusMonths(1) }) {
                Icon(Icons.Default.ChevronRight, "Next month")
            }
        }

        Spacer(Modifier.height(16.dp))

        // Day headers
        Row(modifier = Modifier.fillMaxWidth()) {
            listOf("Sun", "Mon", "Tue", "Wed", "Thu", "Fri", "Sat").forEach { day ->
                Text(
                    day,
                    modifier = Modifier.weight(1f),
                    textAlign = TextAlign.Center,
                    style = MaterialTheme.typography.labelSmall,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                )
            }
        }

        Spacer(Modifier.height(8.dp))

        // Calendar grid
        val firstDayOfMonth = currentMonth.atDay(1)
        val startDayOfWeek = (firstDayOfMonth.dayOfWeek.value % 7) // Sunday = 0
        val daysInMonth = currentMonth.lengthOfMonth()
        val today = LocalDate.now()

        val cells = buildList {
            repeat(startDayOfWeek) { add(0) }
            for (day in 1..daysInMonth) add(day)
            while (size % 7 != 0) add(0)
        }

        LazyVerticalGrid(
            columns = GridCells.Fixed(7),
            modifier = Modifier.weight(1f),
            verticalArrangement = Arrangement.spacedBy(4.dp),
            horizontalArrangement = Arrangement.spacedBy(4.dp),
        ) {
            items(cells) { day ->
                if (day == 0) {
                    Box(modifier = Modifier.aspectRatio(1f))
                } else {
                    val hasFestival = festivalDates.containsKey(day)
                    val isToday = today.dayOfMonth == day &&
                        today.month == currentMonth.month &&
                        today.year == currentMonth.year

                    Box(
                        modifier = Modifier
                            .aspectRatio(1f)
                            .clip(RoundedCornerShape(8.dp))
                            .background(
                                when {
                                    hasFestival -> TempleGold.copy(alpha = 0.15f)
                                    isToday -> MaterialTheme.colorScheme.primaryContainer.copy(alpha = 0.3f)
                                    else -> MaterialTheme.colorScheme.surface
                                }
                            )
                            .clickable(enabled = hasFestival) {
                                selectedFestival = festivalDates[day]
                            },
                        contentAlignment = Alignment.Center,
                    ) {
                        Column(horizontalAlignment = Alignment.CenterHorizontally) {
                            Text(
                                "$day",
                                style = MaterialTheme.typography.bodyMedium,
                                fontWeight = if (isToday || hasFestival) FontWeight.Bold else FontWeight.Normal,
                                color = when {
                                    hasFestival -> TempleGold
                                    isToday -> MaterialTheme.colorScheme.primary
                                    else -> MaterialTheme.colorScheme.onSurface
                                },
                            )
                            if (hasFestival) {
                                Box(
                                    modifier = Modifier
                                        .size(6.dp)
                                        .clip(CircleShape)
                                        .background(TempleGold),
                                )
                            }
                        }
                    }
                }
            }
        }

        // Selected festival detail
        selectedFestival?.let { festival ->
            Spacer(Modifier.height(12.dp))
            GlassmorphicCard(
                accentColor = TempleGold,
                cornerRadius = 16.dp,
                contentPadding = 16.dp,
            ) {
                Text(
                    festival.nameTelugu,
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold,
                )
                Text(
                    festival.name,
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                )
                festival.descriptionTelugu?.let {
                    Spacer(Modifier.height(8.dp))
                    Text(it, style = MaterialTheme.typography.bodySmall, maxLines = 3, overflow = TextOverflow.Ellipsis)
                }
            }
        }
    }
}

@Composable
private fun FestivalListItem(
    festival: FestivalEntity,
) {
    val dateInfo = remember(festival) { festival.getUpcomingDateInfo() }

    GlassmorphicCard {
        Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.Top,
        ) {
            dateInfo?.let { info ->
                Surface(
                    shape = RoundedCornerShape(12.dp),
                    color = if (info.daysUntil <= 30) TempleGold.copy(alpha = 0.15f)
                            else MaterialTheme.colorScheme.surfaceVariant,
                    modifier = Modifier.padding(end = 12.dp),
                ) {
                    Column(
                        modifier = Modifier.padding(horizontal = 12.dp, vertical = 8.dp),
                        horizontalAlignment = Alignment.CenterHorizontally,
                    ) {
                        val parts = info.displayDate.split(" ")
                        Text(
                            parts.getOrElse(0) { "" }.uppercase(),
                            style = MaterialTheme.typography.labelSmall,
                            fontWeight = FontWeight.Bold,
                            color = if (info.daysUntil <= 30) TempleGold else MaterialTheme.colorScheme.onSurfaceVariant,
                        )
                        Text(
                            parts.getOrElse(1) { "" }.replace(",", ""),
                            style = MaterialTheme.typography.headlineSmall,
                            fontWeight = FontWeight.Bold,
                            color = if (info.daysUntil <= 30) TempleGold else MaterialTheme.colorScheme.onSurface,
                        )
                    }
                }
            }

            Column(modifier = Modifier.weight(1f)) {
                Text(
                    festival.nameTelugu,
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis,
                )
                Text(
                    festival.name,
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis,
                )

                dateInfo?.let { info ->
                    Spacer(Modifier.height(6.dp))
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(4.dp),
                    ) {
                        Icon(Icons.Default.CalendarMonth, null, modifier = Modifier.size(14.dp), tint = TempleGold)
                        Text(
                            "${info.displayDate} · ${info.dayOfWeek}",
                            style = MaterialTheme.typography.labelMedium,
                            color = TempleGold,
                            fontWeight = FontWeight.Medium,
                        )
                    }

                    Spacer(Modifier.height(4.dp))
                    val countdownText = when {
                        info.daysUntil == 0 -> "Today!"
                        info.daysUntil == 1 -> "Tomorrow!"
                        info.daysUntil <= 30 -> "${info.daysUntil} days away"
                        else -> if (info.isPastThisYear) "Next year" else "${info.daysUntil} days away"
                    }
                    val countdownColor = when {
                        info.daysUntil <= 1 -> AuspiciousGreen
                        info.daysUntil <= 30 -> TempleGold
                        else -> MaterialTheme.colorScheme.onSurfaceVariant
                    }
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(4.dp),
                    ) {
                        Icon(Icons.Default.Schedule, null, modifier = Modifier.size(12.dp), tint = countdownColor)
                        Text(countdownText, style = MaterialTheme.typography.labelSmall, color = countdownColor, fontWeight = FontWeight.Medium)
                    }
                }

                val descText = festival.descriptionTelugu ?: festival.description
                descText?.let { text ->
                    Spacer(Modifier.height(4.dp))
                    Text(
                        text,
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                        maxLines = 2,
                        overflow = TextOverflow.Ellipsis,
                    )
                }
            }
        }
    }
}
