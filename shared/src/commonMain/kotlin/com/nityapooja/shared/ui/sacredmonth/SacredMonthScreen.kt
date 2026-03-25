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
import com.nityapooja.shared.data.sacredmonth.SacredMonthInfo
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
    onBack: () -> Unit = {},
) {
    val currentSacredMonth by viewModel.currentSacredMonth.collectAsState()
    val allSacredMonths by viewModel.allSacredMonths.collectAsState()
    val locationInfo by panchangamViewModel.locationInfo.collectAsState()

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
                            Text("ప్రస్తుతం ${month.masaNameTelugu} నడుస్తోంది", style = MaterialTheme.typography.labelMedium, fontWeight = FontWeight.Bold, color = AuspiciousGreen)
                        }
                    }
                }
                item { SacredMonthDetailCard(month) }
            }

            // If no sacred month is active, show all
            if (currentSacredMonth == null) {
                item {
                    Text(
                        "ప్రస్తుతం పవిత్ర మాసం కాదు. అన్ని పవిత్ర మాసాల వివరాలు క్రింద చూడండి.",
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                    )
                }
            }

            // All sacred months
            item { SectionHeader(titleTelugu = "పవిత్ర మాసాలు", titleEnglish = "All Sacred Months") }
            items(allSacredMonths) { month ->
                val isActive = month == currentSacredMonth
                SacredMonthSummaryCard(month, isActive)
            }
        }
    }
}

@Composable
private fun SacredMonthDetailCard(month: SacredMonthInfo) {
    // Daily Practices
    GlassmorphicCard(accentColor = TempleGold) {
        SectionHeader(titleTelugu = "దైనందిన ఆచరణలు", titleEnglish = "Daily Practices")
        Spacer(Modifier.height(8.dp))
        month.dailyPractices.forEach { practice ->
            Row(modifier = Modifier.padding(vertical = 4.dp)) {
                Icon(Icons.Default.CheckCircle, contentDescription = null, tint = TempleGold, modifier = Modifier.size(16.dp))
                Spacer(Modifier.width(8.dp))
                Column {
                    Text(practice.nameTelugu, style = MaterialTheme.typography.bodyMedium, fontWeight = FontWeight.Bold, color = MaterialTheme.colorScheme.onSurface)
                    Text(practice.descriptionTelugu, style = MaterialTheme.typography.bodySmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
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
                    Text(day.nameTelugu, style = MaterialTheme.typography.bodyMedium, fontWeight = FontWeight.Bold, color = MaterialTheme.colorScheme.onSurface)
                    Text(day.nameEnglish, style = MaterialTheme.typography.labelSmall, color = TempleGold)
                    Text(day.descriptionTelugu, style = MaterialTheme.typography.bodySmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
                }
            }
        }
    }
}

@Composable
private fun SacredMonthSummaryCard(month: SacredMonthInfo, isActive: Boolean) {
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
                Text(month.masaNameTelugu, style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold, color = if (isActive) AuspiciousGreen else MaterialTheme.colorScheme.onSurface)
                Text(month.masaNameEnglish, style = MaterialTheme.typography.bodySmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
                Spacer(Modifier.height(4.dp))
                Text(month.significanceTelugu, style = MaterialTheme.typography.bodySmall, color = MaterialTheme.colorScheme.onSurface, maxLines = 3)
            }
            if (isActive) {
                Surface(shape = MaterialTheme.shapes.small, color = AuspiciousGreen.copy(alpha = 0.15f)) {
                    Text("నేడు", modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp), style = MaterialTheme.typography.labelSmall, fontWeight = FontWeight.Bold, color = AuspiciousGreen)
                }
            }
        }
    }
}
