package com.nityapooja.app.ui.gunamilan

import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
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
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import com.nityapooja.app.ui.components.BirthDetails
import com.nityapooja.app.ui.components.BirthDetailsInput
import com.nityapooja.app.ui.theme.*
import com.nityapooja.app.utils.AshtaKootaCalculator

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun GunaMilanScreen(
    onBack: () -> Unit,
    viewModel: GunaMilanViewModel = hiltViewModel(),
) {
    val uiState by viewModel.uiState.collectAsState()
    var brideDetails by remember { mutableStateOf(BirthDetails(name = "")) }
    var groomDetails by remember { mutableStateOf(BirthDetails(name = "")) }

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Column {
                        Text("గుణ మిలనం", style = MaterialTheme.typography.titleLarge, fontWeight = FontWeight.Bold)
                        Text("Compatibility Match", style = MaterialTheme.typography.labelSmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
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
            // Bride input
            item {
                BirthDetailsInput(
                    label = "వధువు వివరాలు / Bride Details",
                    details = brideDetails,
                    onDetailsChange = { brideDetails = it },
                    showNameField = true,
                )
            }

            // Groom input
            item {
                BirthDetailsInput(
                    label = "వరుడి వివరాలు / Groom Details",
                    details = groomDetails,
                    onDetailsChange = { groomDetails = it },
                    showNameField = true,
                )
            }

            // Calculate button
            item {
                Button(
                    onClick = {
                        viewModel.calculateCompatibility(
                            brideName = brideDetails.name,
                            brideYear = brideDetails.year, brideMonth = brideDetails.month, brideDay = brideDetails.day,
                            brideHour = brideDetails.hour, brideMinute = brideDetails.minute,
                            brideLat = brideDetails.latitude, brideLng = brideDetails.longitude, brideTzOffset = brideDetails.timezoneOffsetHours,
                            groomName = groomDetails.name,
                            groomYear = groomDetails.year, groomMonth = groomDetails.month, groomDay = groomDetails.day,
                            groomHour = groomDetails.hour, groomMinute = groomDetails.minute,
                            groomLat = groomDetails.latitude, groomLng = groomDetails.longitude, groomTzOffset = groomDetails.timezoneOffsetHours,
                        )
                    },
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(12.dp),
                    colors = ButtonDefaults.buttonColors(containerColor = TempleGold),
                ) {
                    Text(
                        "గుణ మిలనం చూడండి / Check Compatibility",
                        fontWeight = FontWeight.Bold,
                        color = DarkTeak,
                        modifier = Modifier.padding(vertical = 4.dp),
                    )
                }
            }

            // Results
            uiState.milanResult?.let { result ->
                // Person summaries side by side
                item {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(12.dp),
                    ) {
                        uiState.brideResult?.let { bride ->
                            PersonSummaryCard(
                                person = bride,
                                labelTelugu = "వధువు",
                                modifier = Modifier.weight(1f),
                            )
                        }
                        uiState.groomResult?.let { groom ->
                            PersonSummaryCard(
                                person = groom,
                                labelTelugu = "వరుడు",
                                modifier = Modifier.weight(1f),
                            )
                        }
                    }
                }

                // Total score card
                item {
                    TotalScoreCard(result)
                }

                // Ashta Koota breakdown
                item {
                    Text(
                        "అష్ట కూట వివరాలు / Ashta Koota Details",
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold,
                        color = TempleGold,
                    )
                }

                items(result.kootaScores) { score ->
                    KootaScoreCard(score)
                }

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
private fun PersonSummaryCard(
    person: PersonResult,
    labelTelugu: String,
    modifier: Modifier = Modifier,
) {
    Card(
        modifier = modifier,
        shape = RoundedCornerShape(12.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant),
    ) {
        Column(
            modifier = Modifier.padding(12.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(4.dp),
        ) {
            Text(
                person.name.ifBlank { labelTelugu },
                style = MaterialTheme.typography.titleSmall,
                fontWeight = FontWeight.Bold,
                color = TempleGold,
                textAlign = TextAlign.Center,
            )
            Text(
                person.nakshatraTelugu,
                style = MaterialTheme.typography.bodyMedium,
                fontWeight = FontWeight.Medium,
                textAlign = TextAlign.Center,
            )
            Text(
                "పాద ${person.pada}",
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
            )
            Text(
                person.rashiTelugu,
                style = MaterialTheme.typography.bodyMedium,
                textAlign = TextAlign.Center,
            )
        }
    }
}

@Composable
private fun TotalScoreCard(result: AshtaKootaCalculator.GunaMilanResult) {
    val scoreColor = when (result.recommendation.level) {
        4 -> AuspiciousGreen
        3 -> AuspiciousGreen.copy(alpha = 0.8f)
        2 -> WarningAmber
        else -> InauspiciousRed
    }

    Card(
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = scoreColor.copy(alpha = 0.1f)),
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(20.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(8.dp),
        ) {
            Text(
                "మొత్తం స్కోరు",
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.onSurface,
            )

            Text(
                "%.0f / %.0f".format(result.totalScore, result.maxScore),
                style = MaterialTheme.typography.displaySmall,
                fontWeight = FontWeight.Bold,
                color = scoreColor,
            )

            // Progress bar
            val animatedProgress by animateFloatAsState(
                targetValue = (result.totalScore / result.maxScore).toFloat(),
                animationSpec = tween(durationMillis = 1000),
                label = "score_progress",
            )
            LinearProgressIndicator(
                progress = { animatedProgress },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(8.dp)
                    .clip(RoundedCornerShape(4.dp)),
                color = scoreColor,
                trackColor = scoreColor.copy(alpha = 0.2f),
            )

            // Recommendation
            Text(
                result.recommendation.telugu,
                style = MaterialTheme.typography.titleLarge,
                fontWeight = FontWeight.Bold,
                color = scoreColor,
            )
            Text(
                result.recommendation.english,
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
            )
        }
    }
}

@Composable
private fun KootaScoreCard(score: AshtaKootaCalculator.KootaScore) {
    val fraction = if (score.maxPoints > 0) (score.obtainedPoints / score.maxPoints).toFloat() else 0f
    val scoreColor = when {
        fraction >= 0.7f -> AuspiciousGreen
        fraction >= 0.4f -> WarningAmber
        else -> InauspiciousRed
    }

    Card(
        shape = RoundedCornerShape(12.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f)),
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(12.dp),
            verticalArrangement = Arrangement.spacedBy(6.dp),
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically,
            ) {
                Column {
                    Text(
                        score.nameTelugu,
                        style = MaterialTheme.typography.bodyLarge,
                        fontWeight = FontWeight.Bold,
                    )
                    Text(
                        score.nameEnglish,
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                    )
                }
                Text(
                    "%.1f / %.0f".format(score.obtainedPoints, score.maxPoints),
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold,
                    color = scoreColor,
                )
            }

            LinearProgressIndicator(
                progress = { fraction },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(6.dp)
                    .clip(RoundedCornerShape(3.dp)),
                color = scoreColor,
                trackColor = scoreColor.copy(alpha = 0.2f),
            )

            Text(
                score.description,
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
            )
        }
    }
}
