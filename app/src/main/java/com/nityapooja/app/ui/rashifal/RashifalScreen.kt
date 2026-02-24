package com.nityapooja.app.ui.rashifal

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Stars
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.nityapooja.app.data.local.entity.RashiEntity
import com.nityapooja.app.ui.components.EmptyState
import com.nityapooja.app.ui.components.GlassmorphicCard
import com.nityapooja.app.ui.theme.TempleGold

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun RashifalScreen(
    viewModel: RashifalViewModel = hiltViewModel(),
    onBack: () -> Unit,
) {
    val rashis by viewModel.rashis.collectAsStateWithLifecycle()
    val selectedRashi by viewModel.selectedRashi.collectAsStateWithLifecycle()

    // Show prediction dialog when a rashi is selected
    selectedRashi?.let { rashi ->
        val (predictionTelugu, predictionEnglish) = viewModel.getTodayPrediction(rashi)
        RashiPredictionDialog(
            rashi = rashi,
            predictionTelugu = predictionTelugu,
            predictionEnglish = predictionEnglish,
            onDismiss = { viewModel.clearSelection() },
        )
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Column {
                        Text(
                            "రాశిఫలం",
                            style = MaterialTheme.typography.titleLarge,
                            fontWeight = FontWeight.Bold,
                        )
                        Text(
                            "Daily Horoscope",
                            style = MaterialTheme.typography.labelSmall,
                            color = MaterialTheme.colorScheme.onSurfaceVariant,
                        )
                    }
                },
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Icon(
                            Icons.AutoMirrored.Filled.ArrowBack,
                            contentDescription = "Back",
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
            rashis.isEmpty() -> {
                EmptyState(
                    icon = Icons.Default.Stars,
                    titleTelugu = "రాశులు లేవు",
                    titleEnglish = "No horoscope data available",
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(padding),
                )
            }
            else -> {
                LazyVerticalGrid(
                    columns = GridCells.Fixed(2),
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(padding),
                    contentPadding = PaddingValues(16.dp),
                    horizontalArrangement = Arrangement.spacedBy(12.dp),
                    verticalArrangement = Arrangement.spacedBy(12.dp),
                ) {
                    items(rashis, key = { it.id }) { rashi ->
                        RashiGridItem(
                            rashi = rashi,
                            onClick = { viewModel.selectRashi(rashi.id) },
                        )
                    }
                }
            }
        }
    }
}

@Composable
private fun RashiGridItem(
    rashi: RashiEntity,
    onClick: () -> Unit,
) {
    GlassmorphicCard(
        onClick = onClick,
        accentColor = TempleGold,
        contentPadding = 16.dp,
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            modifier = Modifier.fillMaxWidth(),
        ) {
            Text(
                rashi.symbol,
                fontSize = 36.sp,
            )
            Spacer(Modifier.height(8.dp))
            Text(
                rashi.nameTelugu,
                style = MaterialTheme.typography.titleSmall,
                fontWeight = FontWeight.Bold,
                textAlign = TextAlign.Center,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis,
            )
            Text(
                rashi.name,
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                textAlign = TextAlign.Center,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis,
            )
            Spacer(Modifier.height(4.dp))
            Text(
                rashi.dateRange,
                style = MaterialTheme.typography.labelSmall,
                color = MaterialTheme.colorScheme.primary,
                textAlign = TextAlign.Center,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis,
            )
        }
    }
}

@Composable
private fun RashiPredictionDialog(
    rashi: RashiEntity,
    predictionTelugu: String?,
    predictionEnglish: String?,
    onDismiss: () -> Unit,
) {
    AlertDialog(
        onDismissRequest = onDismiss,
        icon = {
            Text(
                rashi.symbol,
                fontSize = 40.sp,
            )
        },
        title = {
            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                Text(
                    rashi.nameTelugu,
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold,
                    textAlign = TextAlign.Center,
                )
                Text(
                    rashi.name,
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    textAlign = TextAlign.Center,
                )
            }
        },
        text = {
            Column {
                if (!predictionTelugu.isNullOrBlank()) {
                    Text(
                        predictionTelugu,
                        style = MaterialTheme.typography.bodyLarge.copy(
                            fontSize = 18.sp,
                            lineHeight = 28.sp,
                        ),
                        fontWeight = FontWeight.Medium,
                        color = MaterialTheme.colorScheme.primary,
                    )
                    Spacer(Modifier.height(12.dp))
                }
                if (!predictionEnglish.isNullOrBlank()) {
                    Text(
                        predictionEnglish,
                        style = MaterialTheme.typography.bodyMedium.copy(
                            fontSize = 14.sp,
                            lineHeight = 22.sp,
                        ),
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                    )
                }
                if (predictionTelugu.isNullOrBlank() && predictionEnglish.isNullOrBlank()) {
                    Text(
                        "Today's prediction is not available.",
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                    )
                }

                Spacer(Modifier.height(8.dp))
                HorizontalDivider(color = MaterialTheme.colorScheme.outlineVariant)
                Spacer(Modifier.height(8.dp))

                Row(
                    horizontalArrangement = Arrangement.spacedBy(16.dp),
                ) {
                    Text(
                        rashi.dateRange,
                        style = MaterialTheme.typography.labelSmall,
                        color = MaterialTheme.colorScheme.primary,
                    )
                    Text(
                        rashi.rulingPlanetTelugu,
                        style = MaterialTheme.typography.labelSmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                    )
                }
            }
        },
        confirmButton = {
            TextButton(onClick = onDismiss) {
                Text("Close")
            }
        },
    )
}
