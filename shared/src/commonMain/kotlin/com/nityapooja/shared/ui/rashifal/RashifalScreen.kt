package com.nityapooja.shared.ui.rashifal

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.GridItemSpan
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Stars
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import org.koin.compose.viewmodel.koinViewModel
import androidx.compose.foundation.background
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.ui.graphics.Color
import com.nityapooja.shared.data.local.entity.RashiEntity
import com.nityapooja.shared.ui.components.EmptyState
import com.nityapooja.shared.ui.components.FontSizeViewModel
import com.nityapooja.shared.ui.components.GlassmorphicCard
import com.nityapooja.shared.ui.theme.TempleGold

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun RashifalScreen(
    viewModel: RashifalViewModel = koinViewModel(),
    onBack: () -> Unit,
    bannerAd: (@Composable () -> Unit)? = null,
) {
    val rashis by viewModel.rashis.collectAsState()
    val selectedRashi by viewModel.selectedRashi.collectAsState()

    val fontSizeViewModel: FontSizeViewModel = koinViewModel()
    val fontSize by fontSizeViewModel.fontSize.collectAsState()
    val fontScale = fontSize / 16f

    // Show prediction dialog when a rashi is selected
    selectedRashi?.let { rashi ->
        val prediction = viewModel.computeGochaRa(rashi)
        RashiPredictionDialog(
            rashi = rashi,
            prediction = prediction,
            fontScale = fontScale,
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
                    item(span = { GridItemSpan(maxLineSpan) }) { bannerAd?.invoke() }
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
                "జన్మ రాశి ఫలితం",
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
    prediction: RashifalPrediction,
    fontScale: Float = 1f,
    onDismiss: () -> Unit,
) {
    AlertDialog(
        onDismissRequest = onDismiss,
        icon = { Text(rashi.symbol, fontSize = 40.sp) },
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
                // Transit context pill — shows where Moon + Sun are today
                val pillColor = if (prediction.isChandrashtama)
                    Color(0xFFB71C1C).copy(alpha = 0.12f)
                else
                    TempleGold.copy(alpha = 0.10f)
                val pillTextColor = if (prediction.isChandrashtama) Color(0xFFB71C1C) else TempleGold

                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .background(pillColor, RoundedCornerShape(8.dp))
                        .padding(horizontal = 10.dp, vertical = 6.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                ) {
                    Column {
                        Text(
                            "🌙 ${prediction.moonRashiNameTelugu} (${prediction.moonHouse}వ స్థానం)",
                            style = MaterialTheme.typography.labelMedium,
                            fontWeight = FontWeight.Bold,
                            color = pillTextColor,
                        )
                        Text(
                            "Moon in ${prediction.moonRashiNameEnglish} · house ${prediction.moonHouse}",
                            style = MaterialTheme.typography.labelSmall,
                            color = pillTextColor.copy(alpha = 0.8f),
                        )
                    }
                    Column(horizontalAlignment = Alignment.End) {
                        Text(
                            "☀️ ${prediction.sunRashiNameTelugu}",
                            style = MaterialTheme.typography.labelMedium,
                            color = MaterialTheme.colorScheme.onSurfaceVariant,
                        )
                        Text(
                            "Sun · ${prediction.sunRashiNameEnglish}",
                            style = MaterialTheme.typography.labelSmall,
                            color = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.7f),
                        )
                    }
                }

                Spacer(Modifier.height(12.dp))

                // Telugu prediction
                Text(
                    prediction.textTelugu,
                    style = MaterialTheme.typography.bodyLarge.copy(
                        fontSize = (16 * fontScale).sp,
                        lineHeight = (26 * fontScale).sp,
                    ),
                    fontWeight = FontWeight.Medium,
                    color = if (prediction.isChandrashtama) Color(0xFFB71C1C)
                            else MaterialTheme.colorScheme.primary,
                )

                Spacer(Modifier.height(10.dp))

                // English prediction
                Text(
                    prediction.textEnglish,
                    style = MaterialTheme.typography.bodyMedium.copy(
                        fontSize = (13 * fontScale).sp,
                        lineHeight = (20 * fontScale).sp,
                    ),
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                )

                Spacer(Modifier.height(10.dp))
                HorizontalDivider(color = MaterialTheme.colorScheme.outlineVariant)
                Spacer(Modifier.height(6.dp))

                Text(
                    "గోచార ఫలం · చంద్ర గోచారం ఆధారంగా",
                    style = MaterialTheme.typography.labelSmall,
                    color = TempleGold.copy(alpha = 0.7f),
                )
            }
        },
        confirmButton = {
            TextButton(onClick = onDismiss) { Text("సరే · Close") }
        },
    )
}
