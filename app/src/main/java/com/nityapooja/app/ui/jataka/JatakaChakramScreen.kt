package com.nityapooja.app.ui.jataka

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import com.nityapooja.app.ui.components.BirthDetails
import com.nityapooja.app.ui.components.BirthDetailsInput
import com.nityapooja.app.ui.theme.*

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun JatakaChakramScreen(
    onBack: () -> Unit,
    viewModel: JatakaChakramViewModel = hiltViewModel(),
) {
    val uiState by viewModel.uiState.collectAsState()
    var birthDetails by remember { mutableStateOf(BirthDetails()) }

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Column {
                        Text("జాతక చక్రం", style = MaterialTheme.typography.titleLarge, fontWeight = FontWeight.Bold)
                        Text("Birth Chart", style = MaterialTheme.typography.labelSmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
                    }
                },
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, "Back")
                    }
                },
            )
        }
    ) { padding ->
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding),
            contentPadding = PaddingValues(16.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp),
        ) {
            // Birth details input
            item {
                BirthDetailsInput(
                    label = "జన్మ వివరాలు / Birth Details",
                    details = birthDetails,
                    onDetailsChange = { birthDetails = it },
                )
            }

            // Calculate button
            item {
                Button(
                    onClick = {
                        viewModel.calculateChart(
                            year = birthDetails.year,
                            month = birthDetails.month,
                            day = birthDetails.day,
                            hour = birthDetails.hour,
                            minute = birthDetails.minute,
                            latitude = birthDetails.latitude,
                            longitude = birthDetails.longitude,
                            utcOffsetHours = birthDetails.timezoneOffsetHours,
                        )
                    },
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(12.dp),
                    colors = ButtonDefaults.buttonColors(containerColor = TempleGold),
                ) {
                    Text(
                        "చక్రం చూడండి / View Chart",
                        fontWeight = FontWeight.Bold,
                        color = DarkTeak,
                        modifier = Modifier.padding(vertical = 4.dp),
                    )
                }
            }

            // Results
            uiState.result?.let { result ->
                // Summary card
                item {
                    JanmaSummaryCard(result)
                }

                // South Indian Chart
                item {
                    Card(
                        shape = RoundedCornerShape(16.dp),
                        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
                    ) {
                        SouthIndianChart(
                            positions = result.positions,
                            lagnaRashiIndex = result.lagnaRashiIndex,
                            modifier = Modifier.padding(8.dp),
                        )
                    }
                }

                // Graha position details
                item {
                    Text(
                        "గ్రహ స్థానాలు / Planetary Positions",
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold,
                        color = TempleGold,
                        modifier = Modifier.padding(top = 8.dp),
                    )
                }

                items(result.positions) { graha ->
                    GrahaPositionCard(graha)
                }

                // Bottom spacing
                item { Spacer(Modifier.height(16.dp)) }
            }

            if (uiState.isCalculating) {
                item {
                    Box(
                        modifier = Modifier.fillMaxWidth().padding(32.dp),
                        contentAlignment = Alignment.Center,
                    ) {
                        CircularProgressIndicator(color = TempleGold)
                    }
                }
            }
        }
    }
}

@Composable
private fun JanmaSummaryCard(result: JatakaResult) {
    Card(
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surfaceVariant,
        ),
    ) {
        Column(
            modifier = Modifier.padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(8.dp),
        ) {
            Text(
                "జన్మ సారాంశం",
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Bold,
                color = TempleGold,
            )

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceEvenly,
            ) {
                SummaryItem("లగ్నం", result.lagnaRashiTelugu, "%.1f°".format(result.lagnaDegreesInRashi))
                SummaryItem("జన్మ రాశి", result.janmaRashiTelugu, result.janmaRashiEnglish)
                SummaryItem("జన్మ నక్షత్రం", result.janmaNakshatraTelugu, "పాద ${result.janmaNakshatraPada}")
            }
        }
    }
}

@Composable
private fun SummaryItem(label: String, value: String, subValue: String) {
    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
    ) {
        Text(
            label,
            style = MaterialTheme.typography.labelSmall,
            color = MaterialTheme.colorScheme.onSurfaceVariant,
        )
        Text(
            value,
            style = MaterialTheme.typography.titleMedium,
            fontWeight = FontWeight.Bold,
            color = MaterialTheme.colorScheme.onSurface,
        )
        Text(
            subValue,
            style = MaterialTheme.typography.bodySmall,
            color = TempleGold,
        )
    }
}

@Composable
private fun GrahaPositionCard(graha: GrahaPosition) {
    Card(
        shape = RoundedCornerShape(12.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f)),
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(12.dp),
            verticalAlignment = Alignment.CenterVertically,
        ) {
            // Graha name
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    graha.nameTelugu,
                    style = MaterialTheme.typography.bodyLarge,
                    fontWeight = FontWeight.Bold,
                )
                Text(
                    graha.nameEnglish,
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                )
            }

            // Rashi
            Column(
                modifier = Modifier.weight(1f),
                horizontalAlignment = Alignment.CenterHorizontally,
            ) {
                Text(
                    graha.rashiTelugu,
                    style = MaterialTheme.typography.bodyMedium,
                    fontWeight = FontWeight.Medium,
                    textAlign = TextAlign.Center,
                )
                Text(
                    "%.1f°".format(graha.degreesInRashi),
                    style = MaterialTheme.typography.bodySmall,
                    color = TempleGold,
                    textAlign = TextAlign.Center,
                )
            }

            // Nakshatra
            Column(
                modifier = Modifier.weight(1.2f),
                horizontalAlignment = Alignment.End,
            ) {
                Text(
                    graha.nakshatraTelugu,
                    style = MaterialTheme.typography.bodyMedium,
                    fontWeight = FontWeight.Medium,
                    textAlign = TextAlign.End,
                )
                Text(
                    "పాద ${graha.pada}",
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    textAlign = TextAlign.End,
                )
            }
        }
    }
}
