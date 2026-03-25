package com.nityapooja.shared.ui.muhurtam

import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
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
import com.nityapooja.shared.data.muhurtam.MuhurtamRules.EventType
import com.nityapooja.shared.data.muhurtam.MuhurtamRules.MuhurtamScore
import com.nityapooja.shared.ui.components.GlassmorphicCard
import com.nityapooja.shared.ui.panchangam.PanchangamViewModel
import com.nityapooja.shared.ui.theme.AuspiciousGreen
import com.nityapooja.shared.ui.theme.DeepVermillion
import com.nityapooja.shared.ui.theme.SacredTurmeric
import com.nityapooja.shared.ui.theme.TempleGold
import org.koin.compose.viewmodel.koinViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MuhurtamFinderScreen(
    viewModel: MuhurtamFinderViewModel = koinViewModel(),
    panchangamViewModel: PanchangamViewModel = koinViewModel(),
    onBack: () -> Unit = {},
) {
    val selectedEvent by viewModel.selectedEvent.collectAsState()
    val scoredDates by viewModel.scoredDates.collectAsState()
    val isLoading by viewModel.isLoading.collectAsState()
    val locationInfo by panchangamViewModel.locationInfo.collectAsState()

    LaunchedEffect(locationInfo) {
        viewModel.calculate(locationInfo.lat, locationInfo.lng, locationInfo.timezone)
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Column {
                        Text("శుభ ముహూర్తాలు", style = MaterialTheme.typography.titleLarge, fontWeight = FontWeight.Bold)
                        Text("Muhurtam Finder", style = MaterialTheme.typography.labelSmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
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
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding),
        ) {
            // Event type chips
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .horizontalScroll(rememberScrollState())
                    .padding(horizontal = 16.dp, vertical = 8.dp),
                horizontalArrangement = Arrangement.spacedBy(8.dp),
            ) {
                EventType.entries.forEach { event ->
                    FilterChip(
                        selected = event == selectedEvent,
                        onClick = { viewModel.selectEvent(event) },
                        label = {
                            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                                Text(event.nameTelugu, style = MaterialTheme.typography.labelSmall, fontWeight = FontWeight.Bold)
                                Text(event.nameEnglish, style = MaterialTheme.typography.labelSmall.copy(fontSize = 10.sp))
                            }
                        },
                        colors = FilterChipDefaults.filterChipColors(
                            selectedContainerColor = TempleGold.copy(alpha = 0.2f),
                            selectedLabelColor = TempleGold,
                        ),
                    )
                }
            }

            if (isLoading) {
                Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                    CircularProgressIndicator(color = TempleGold)
                }
            } else {
                // Info text
                Text(
                    "రాబోయే 30 రోజులలో ${selectedEvent.nameTelugu}కు శుభ ముహూర్తాలు",
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    modifier = Modifier.padding(horizontal = 16.dp, vertical = 4.dp),
                )

                // Scored dates list
                LazyColumn(
                    contentPadding = PaddingValues(16.dp),
                    verticalArrangement = Arrangement.spacedBy(12.dp),
                ) {
                    items(scoredDates) { scoredDate ->
                        MuhurtamDateCard(scoredDate)
                    }
                }
            }
        }
    }
}

@Composable
private fun MuhurtamDateCard(scoredDate: ScoredDate) {
    val scoreColor = when (scoredDate.result.score) {
        MuhurtamScore.EXCELLENT -> AuspiciousGreen
        MuhurtamScore.GOOD -> TempleGold
        MuhurtamScore.AVERAGE -> SacredTurmeric
        MuhurtamScore.AVOID -> DeepVermillion
    }

    GlassmorphicCard(
        accentColor = scoreColor,
        cornerRadius = 16.dp,
        contentPadding = 16.dp,
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.Top,
        ) {
            Column(modifier = Modifier.weight(1f)) {
                // Date display
                Text(
                    scoredDate.panchangamData.dateDisplay,
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.onSurface,
                )
                Text(
                    scoredDate.panchangamData.teluguDay,
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                )

                Spacer(Modifier.height(8.dp))

                // Panchangam summary
                Text(
                    "${scoredDate.panchangamData.tithi.nameTelugu} · ${scoredDate.panchangamData.tithi.pakshaTelugu}",
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurface,
                )
                Text(
                    scoredDate.panchangamData.nakshatra.nameTelugu,
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurface,
                )
                Text(
                    scoredDate.panchangamData.yoga.nameTelugu,
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                )
            }

            // Score badge
            Surface(
                shape = MaterialTheme.shapes.medium,
                color = scoreColor.copy(alpha = 0.15f),
            ) {
                Column(
                    modifier = Modifier.padding(horizontal = 12.dp, vertical = 8.dp),
                    horizontalAlignment = Alignment.CenterHorizontally,
                ) {
                    Text(
                        "${scoredDate.result.points}",
                        style = MaterialTheme.typography.headlineMedium,
                        fontWeight = FontWeight.Bold,
                        color = scoreColor,
                    )
                    Text(
                        scoredDate.result.score.labelTelugu,
                        style = MaterialTheme.typography.labelSmall,
                        fontWeight = FontWeight.Bold,
                        color = scoreColor,
                    )
                }
            }
        }

        // Reasons
        if (scoredDate.result.reasons.isNotEmpty()) {
            Spacer(Modifier.height(8.dp))
            HorizontalDivider(color = MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.3f))
            Spacer(Modifier.height(8.dp))

            scoredDate.result.reasons.forEach { reason ->
                Row(
                    modifier = Modifier.padding(vertical = 2.dp),
                    verticalAlignment = Alignment.CenterVertically,
                ) {
                    Icon(
                        if (reason.isPositive) Icons.Default.CheckCircle else Icons.Default.Cancel,
                        contentDescription = null,
                        modifier = Modifier.size(14.dp),
                        tint = if (reason.isPositive) AuspiciousGreen else DeepVermillion,
                    )
                    Spacer(Modifier.width(6.dp))
                    Text(
                        reason.textTelugu,
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurface,
                    )
                }
            }
        }
    }
}
