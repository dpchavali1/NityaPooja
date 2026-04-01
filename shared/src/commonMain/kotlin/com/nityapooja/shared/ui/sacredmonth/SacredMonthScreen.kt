package com.nityapooja.shared.ui.sacredmonth

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
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.nityapooja.shared.data.sacredmonth.SacredMonthDateRange
import com.nityapooja.shared.data.sacredmonth.SacredMonthInfo
import com.nityapooja.shared.ui.components.FontSizeControls
import com.nityapooja.shared.ui.components.FontSizeViewModel
import com.nityapooja.shared.ui.components.GlassmorphicCard
import com.nityapooja.shared.ui.components.SectionHeader
import com.nityapooja.shared.ui.panchangam.PanchangamViewModel
import com.nityapooja.shared.ui.theme.AuspiciousGreen
import com.nityapooja.shared.ui.theme.TempleGold
import org.koin.compose.viewmodel.koinViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SacredMonthScreen(
    viewModel: SacredMonthViewModel = koinViewModel(),
    panchangamViewModel: PanchangamViewModel = koinViewModel(),
    fontSizeViewModel: FontSizeViewModel = koinViewModel(),
    onBack: () -> Unit = {},
    bannerAd: (@Composable () -> Unit)? = null,
) {
    val currentSacredMonth by viewModel.currentSacredMonth.collectAsState()
    val allSacredMonths by viewModel.allSacredMonths.collectAsState()
    val sacredMonthRanges by viewModel.sacredMonthRanges.collectAsState()
    val isLoading by viewModel.isLoading.collectAsState()
    val locationInfo by panchangamViewModel.locationInfo.collectAsState()
    val fontSize by fontSizeViewModel.fontSize.collectAsState()
    val fontScale = fontSize / 16f

    LaunchedEffect(locationInfo) {
        viewModel.detectCurrentMonth(locationInfo.lat, locationInfo.lng, locationInfo.timezone)
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Column {
                        Text("పవిత్ర మాసాలు", style = MaterialTheme.typography.titleLarge, fontWeight = FontWeight.Bold)
                        Text("Sacred Months", style = MaterialTheme.typography.labelSmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
                    }
                },
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, "Back")
                    }
                },
                actions = {
                    FontSizeControls(fontSize = fontSize, onDecrease = fontSizeViewModel::decrease, onIncrease = fontSizeViewModel::increase)
                },
                colors = TopAppBarDefaults.topAppBarColors(containerColor = Color.Transparent),
            )
        },
    ) { padding ->
        LazyColumn(
            modifier = Modifier.fillMaxSize().padding(padding),
            contentPadding = PaddingValues(16.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp),
        ) {
            // Current sacred month (if active)
            currentSacredMonth?.let { month ->
                item {
                    GlassmorphicCard(accentColor = AuspiciousGreen) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(Icons.Default.Brightness7, contentDescription = null, tint = AuspiciousGreen, modifier = Modifier.size(20.dp))
                            Spacer(Modifier.width(8.dp))
                            Text("ప్రస్తుతం ${month.masaNameTelugu} నడుస్తోంది", style = MaterialTheme.typography.labelMedium.copy(fontSize = (12 * fontScale).sp), fontWeight = FontWeight.Bold, color = AuspiciousGreen)
                        }
                    }
                }
                item { SacredMonthDetailCard(month, fontScale) }
            }

            // If no sacred month is active, show all
            if (currentSacredMonth == null) {
                item {
                    Text(
                        "ప్రస్తుతం పవిత్ర మాసం కాదు. అన్ని పవిత్ర మాసాల వివరాలు క్రింద చూడండి.",
                        style = MaterialTheme.typography.bodySmall.copy(fontSize = (14 * fontScale).sp),
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                    )
                }
            }

            item { bannerAd?.invoke() }

            // All sacred months with date ranges
            item { SectionHeader(titleTelugu = "పవిత్ర మాసాలు · తేదీలు", titleEnglish = "Sacred Months · Dates") }
            if (isLoading) {
                item {
                    Box(Modifier.fillMaxWidth().padding(32.dp), contentAlignment = Alignment.Center) {
                        Column(horizontalAlignment = Alignment.CenterHorizontally) {
                            CircularProgressIndicator(color = TempleGold, modifier = Modifier.size(32.dp))
                            Spacer(Modifier.height(8.dp))
                            Text("తేదీలు లెక్కిస్తోంది...", style = MaterialTheme.typography.bodySmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
                        }
                    }
                }
            } else if (sacredMonthRanges.isNotEmpty()) {
                items(sacredMonthRanges) { range ->
                    SacredMonthDateCard(range, fontScale)
                }
            } else {
                items(allSacredMonths) { month ->
                    val isActive = month == currentSacredMonth
                    SacredMonthSummaryCard(month, isActive, fontScale)
                }
            }
        }
    }
}

@Composable
private fun SacredMonthDetailCard(month: SacredMonthInfo, fontScale: Float) {
    // Daily Practices
    GlassmorphicCard(accentColor = TempleGold) {
        SectionHeader(titleTelugu = "దైనందిన ఆచరణలు", titleEnglish = "Daily Practices")
        Spacer(Modifier.height(8.dp))
        month.dailyPractices.forEach { practice ->
            Row(modifier = Modifier.padding(vertical = 4.dp)) {
                Icon(Icons.Default.CheckCircle, contentDescription = null, tint = TempleGold, modifier = Modifier.size(16.dp))
                Spacer(Modifier.width(8.dp))
                Column {
                    Text(practice.nameTelugu, style = MaterialTheme.typography.bodyMedium.copy(fontSize = (14 * fontScale).sp), fontWeight = FontWeight.Bold, color = MaterialTheme.colorScheme.onSurface)
                    Text(practice.descriptionTelugu, style = MaterialTheme.typography.bodySmall.copy(fontSize = (14 * fontScale).sp), color = MaterialTheme.colorScheme.onSurfaceVariant)
                }
            }
        }
    }

    Spacer(Modifier.height(12.dp))

    // Special Days
    GlassmorphicCard {
        SectionHeader(titleTelugu = "ప్రత్యేక దినాలు", titleEnglish = "Special Days")
        Spacer(Modifier.height(8.dp))
        month.specialDays.forEach { day ->
            Row(modifier = Modifier.padding(vertical = 4.dp)) {
                Icon(Icons.Default.Star, contentDescription = null, tint = TempleGold, modifier = Modifier.size(16.dp))
                Spacer(Modifier.width(8.dp))
                Column {
                    Text(day.nameTelugu, style = MaterialTheme.typography.bodyMedium.copy(fontSize = (14 * fontScale).sp), fontWeight = FontWeight.Bold, color = MaterialTheme.colorScheme.onSurface)
                    Text(day.nameEnglish, style = MaterialTheme.typography.labelSmall.copy(fontSize = (11 * fontScale).sp), color = TempleGold)
                    Text(day.descriptionTelugu, style = MaterialTheme.typography.bodySmall.copy(fontSize = (14 * fontScale).sp), color = MaterialTheme.colorScheme.onSurfaceVariant)
                }
            }
        }
    }
}

@Composable
private fun SacredMonthDateCard(range: SacredMonthDateRange, fontScale: Float) {
    GlassmorphicCard(
        accentColor = if (range.isActive) AuspiciousGreen else null,
        cornerRadius = 16.dp,
        contentPadding = 16.dp,
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.Top,
        ) {
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    range.info.masaNameTelugu,
                    style = MaterialTheme.typography.titleMedium.copy(fontSize = (16 * fontScale).sp),
                    fontWeight = FontWeight.Bold,
                    color = if (range.isActive) AuspiciousGreen else MaterialTheme.colorScheme.onSurface,
                )
                Text(
                    range.info.masaNameEnglish,
                    style = MaterialTheme.typography.bodySmall.copy(fontSize = (12 * fontScale).sp),
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                )
                Spacer(Modifier.height(6.dp))
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(Icons.Default.CalendarMonth, contentDescription = null, modifier = Modifier.size(14.dp), tint = TempleGold)
                    Spacer(Modifier.width(4.dp))
                    Text(
                        "${range.startDate} — ${range.endDate}",
                        style = MaterialTheme.typography.labelMedium.copy(fontSize = (13 * fontScale).sp),
                        fontWeight = FontWeight.Medium,
                        color = TempleGold,
                    )
                }
                Spacer(Modifier.height(4.dp))
                Text(
                    range.info.significanceTelugu,
                    style = MaterialTheme.typography.bodySmall.copy(fontSize = (12 * fontScale).sp),
                    color = MaterialTheme.colorScheme.onSurface,
                    maxLines = 2,
                )
            }
            // Status badge
            Surface(
                shape = MaterialTheme.shapes.small,
                color = when {
                    range.isActive -> AuspiciousGreen.copy(alpha = 0.15f)
                    range.daysUntilStart in 1..30 -> TempleGold.copy(alpha = 0.12f)
                    else -> MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f)
                },
            ) {
                Text(
                    when {
                        range.isActive -> "${range.daysRemaining} రోజులు"
                        range.daysUntilStart in 1..30 -> "${range.daysUntilStart} రోజుల్లో"
                        range.daysUntilStart > 30 -> "${range.daysUntilStart} రోజుల్లో"
                        else -> ""
                    },
                    modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp),
                    style = MaterialTheme.typography.labelSmall,
                    fontWeight = FontWeight.Bold,
                    color = when {
                        range.isActive -> AuspiciousGreen
                        range.daysUntilStart in 1..30 -> TempleGold
                        else -> MaterialTheme.colorScheme.onSurfaceVariant
                    },
                )
            }
        }
    }
}

@Composable
private fun SacredMonthSummaryCard(month: SacredMonthInfo, isActive: Boolean, fontScale: Float) {
    GlassmorphicCard(
        accentColor = if (isActive) AuspiciousGreen else null,
        cornerRadius = 16.dp,
        contentPadding = 16.dp,
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically,
        ) {
            Column(modifier = Modifier.weight(1f)) {
                Text(month.masaNameTelugu, style = MaterialTheme.typography.titleMedium.copy(fontSize = (16 * fontScale).sp), fontWeight = FontWeight.Bold, color = if (isActive) AuspiciousGreen else MaterialTheme.colorScheme.onSurface)
                Text(month.masaNameEnglish, style = MaterialTheme.typography.bodySmall.copy(fontSize = (14 * fontScale).sp), color = MaterialTheme.colorScheme.onSurfaceVariant)
                Spacer(Modifier.height(4.dp))
                Text(month.significanceTelugu, style = MaterialTheme.typography.bodySmall.copy(fontSize = (14 * fontScale).sp), color = MaterialTheme.colorScheme.onSurface, maxLines = 3)
            }
            if (isActive) {
                Surface(shape = MaterialTheme.shapes.small, color = AuspiciousGreen.copy(alpha = 0.15f)) {
                    Text("నేడు", modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp), style = MaterialTheme.typography.labelSmall, fontWeight = FontWeight.Bold, color = AuspiciousGreen)
                }
            }
        }
    }
}
