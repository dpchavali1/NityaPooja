package com.nityapooja.shared.ui.jataka

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Share
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import org.koin.compose.viewmodel.koinViewModel
import com.nityapooja.shared.ui.components.BirthDetails
import com.nityapooja.shared.ui.components.BirthDetailsInput
import com.nityapooja.shared.ui.theme.*
import com.nityapooja.shared.platform.shareText

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun JatakaChakramScreen(
    onBack: () -> Unit,
    viewModel: JatakaChakramViewModel = koinViewModel(),
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
                actions = {
                    uiState.result?.let { result ->
                        IconButton(onClick = {
                            shareText(buildJatakaShareText(result))
                        }) {
                            Icon(Icons.Default.Share, "Share", tint = TempleGold)
                        }
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
                horizontalArrangement = Arrangement.spacedBy(8.dp),
            ) {
                SummaryItem("లగ్నం", result.lagnaRashiTelugu, "${((result.lagnaDegreesInRashi * 10).toInt() / 10.0)}°", modifier = Modifier.weight(1f))
                SummaryItem("జన్మ రాశి", result.janmaRashiTelugu, result.janmaRashiEnglish, modifier = Modifier.weight(1f))
                SummaryItem("నక్షత్రం", result.janmaNakshatraTelugu, "పాద ${result.janmaNakshatraPada}", modifier = Modifier.weight(1f))
            }
        }
    }
}

@Composable
private fun SummaryItem(label: String, value: String, subValue: String, modifier: Modifier = Modifier) {
    Column(
        modifier = modifier,
        horizontalAlignment = Alignment.CenterHorizontally,
    ) {
        Text(
            label,
            style = MaterialTheme.typography.labelSmall,
            color = MaterialTheme.colorScheme.onSurfaceVariant,
            maxLines = 1,
        )
        Text(
            value,
            style = MaterialTheme.typography.titleSmall,
            fontWeight = FontWeight.Bold,
            color = MaterialTheme.colorScheme.onSurface,
            maxLines = 1,
            textAlign = TextAlign.Center,
        )
        Text(
            subValue,
            style = MaterialTheme.typography.bodySmall,
            color = TempleGold,
            maxLines = 1,
        )
    }
}

private fun buildJatakaShareText(result: JatakaResult): String = buildString {
    appendLine("🪷 జాతక చక్రం / Birth Chart")
    appendLine()
    appendLine("లగ్నం: ${result.lagnaRashiTelugu} (${result.lagnaRashiEnglish}) ${((result.lagnaDegreesInRashi * 10).toInt() / 10.0)}°")
    appendLine("జన్మ రాశి: ${result.janmaRashiTelugu} (${result.janmaRashiEnglish})")
    appendLine("జన్మ నక్షత్రం: ${result.janmaNakshatraTelugu} (${result.janmaNakshatraEnglish}) పాద ${result.janmaNakshatraPada}")
    appendLine()
    append(buildSouthIndianChartText(result))
    appendLine()
    appendLine("గ్రహ స్థానాలు:")
    result.positions.forEach { g ->
        appendLine("  ${g.nameTelugu} (${g.nameEnglish}): ${g.rashiTelugu} ${((g.degreesInRashi * 10).toInt() / 10.0)}° — ${g.nakshatraTelugu} పాద ${g.pada}")
    }
    appendLine()
    append("Shared via NityaPooja")
}

/**
 * Builds a text-based South Indian chart (4x4 grid with center label).
 * Fixed rashi positions — Mesha at (row=0, col=1).
 * Each cell shows rashi abbreviation + graha abbreviations.
 * Lagna cell is marked with "ల".
 */
private fun buildSouthIndianChartText(result: JatakaResult): String {
    val grahasByRashi = result.positions.groupBy { it.rashiIndex }

    // South Indian grid: row, col for each rashi (0=Mesha..11=Meena)
    // Same order as JyotishConstants.SOUTH_INDIAN_POSITIONS
    val rashiGrid = arrayOf(
        intArrayOf(0, 1), intArrayOf(0, 2), intArrayOf(0, 3),
        intArrayOf(1, 3), intArrayOf(2, 3), intArrayOf(3, 3),
        intArrayOf(3, 2), intArrayOf(3, 1), intArrayOf(3, 0),
        intArrayOf(2, 0), intArrayOf(1, 0), intArrayOf(0, 0),
    )

    // Telugu abbreviations for text chart
    val rashiShort = arrayOf("మేష", "వృషభ", "మిథున", "కర్కా", "సింహ", "కన్య", "తుల", "వృశ్చి", "ధను", "మకర", "కుంభ", "మీన")
    val grahaShort = arrayOf("సూ", "చం", "కు", "బు", "గు", "శు", "శ", "రా", "కే")

    // Build cell content for each grid position
    val cells = Array(4) { Array(4) { "" } }

    for (rashiIdx in 0..11) {
        val (row, col) = rashiGrid[rashiIdx]
        val parts = mutableListOf<String>()
        parts.add(rashiShort[rashiIdx])
        if (rashiIdx == result.lagnaRashiIndex) parts.add("ల")
        val grahas = grahasByRashi[rashiIdx]
        if (grahas != null) {
            parts.add(grahas.joinToString(" ") { grahaShort[it.grahaIndex] })
        }
        cells[row][col] = parts.joinToString(" ")
    }

    // Center cells
    cells[1][1] = "జాతక"
    cells[1][2] = ""
    cells[2][1] = "చక్రం"
    cells[2][2] = ""

    // Render as fixed-width text grid
    val colWidth = 10
    val hLine = "+" + ("-".repeat(colWidth) + "+").repeat(4)

    return buildString {
        appendLine(hLine)
        for (row in 0..3) {
            append("|")
            for (col in 0..3) {
                val content = cells[row][col]
                val padded = if (content.length > colWidth) content.take(colWidth) else content.padEnd(colWidth)
                append(padded)
                append("|")
            }
            appendLine()
            appendLine(hLine)
        }
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
                    "${((graha.degreesInRashi * 10).toInt() / 10.0)}°",
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
