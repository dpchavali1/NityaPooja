package com.nityapooja.shared.ui.gunamilan

import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
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
import androidx.compose.ui.draw.clip
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import org.koin.compose.viewmodel.koinViewModel
import com.nityapooja.shared.ui.components.BirthDetails
import com.nityapooja.shared.ui.components.BirthDetailsInput
import com.nityapooja.shared.ui.theme.*
import com.nityapooja.shared.utils.AshtaKootaCalculator
import com.nityapooja.shared.platform.shareText

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun GunaMilanScreen(
    onBack: () -> Unit,
    viewModel: GunaMilanViewModel = koinViewModel(),
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
                actions = {
                    val result = uiState.milanResult
                    val bride = uiState.brideResult
                    val groom = uiState.groomResult
                    if (result != null && bride != null && groom != null) {
                        IconButton(onClick = {
                            shareText(
                                buildGunaMilanShareText(
                                    bride = bride,
                                    groom = groom,
                                    result = result,
                                    checks = uiState.teluguChecks,
                                    hasCriticalIssues = uiState.hasCriticalIssues,
                                )
                            )
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
                            brideLat = brideDetails.latitude,
                            brideLng = brideDetails.longitude,
                            brideTimezoneId = brideDetails.timezoneId,
                            brideTzOffset = brideDetails.timezoneOffsetHours,
                            groomName = groomDetails.name,
                            groomYear = groomDetails.year, groomMonth = groomDetails.month, groomDay = groomDetails.day,
                            groomHour = groomDetails.hour, groomMinute = groomDetails.minute,
                            groomLat = groomDetails.latitude,
                            groomLng = groomDetails.longitude,
                            groomTimezoneId = groomDetails.timezoneId,
                            groomTzOffset = groomDetails.timezoneOffsetHours,
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

                if (uiState.teluguChecks.isNotEmpty()) {
                    item {
                        Text(
                            "తెలుగు జ్యోతిష్య పరిశీలనలు / Telugu Rule Checks",
                            style = MaterialTheme.typography.titleMedium,
                            fontWeight = FontWeight.Bold,
                            color = TempleGold,
                        )
                    }
                    items(uiState.teluguChecks) { check ->
                        TeluguCheckCard(check)
                    }
                    if (uiState.hasCriticalIssues) {
                        item {
                            CriticalWarningCard()
                        }
                    }
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
                "${result.totalScore.toInt()} / ${result.maxScore.toInt()}",
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

private fun buildGunaMilanShareText(
    bride: PersonResult,
    groom: PersonResult,
    result: AshtaKootaCalculator.GunaMilanResult,
    checks: List<TeluguCompatibilityCheck>,
    hasCriticalIssues: Boolean,
): String = buildString {
    appendLine("🪷 గుణ మిలనం / Compatibility Match")
    appendLine()
    appendLine("వధువు / Bride: ${bride.name}")
    appendLine("  నక్షత్రం: ${bride.nakshatraTelugu} (${bride.nakshatraEnglish}) పాద ${bride.pada}")
    appendLine("  రాశి: ${bride.rashiTelugu} (${bride.rashiEnglish})")
    appendLine()
    appendLine("వరుడు / Groom: ${groom.name}")
    appendLine("  నక్షత్రం: ${groom.nakshatraTelugu} (${groom.nakshatraEnglish}) పాద ${groom.pada}")
    appendLine("  రాశి: ${groom.rashiTelugu} (${groom.rashiEnglish})")
    appendLine()
    appendLine("మొత్తం స్కోరు: ${result.totalScore.toInt()} / ${result.maxScore.toInt()}")
    appendLine("${result.recommendation.telugu} — ${result.recommendation.english}")
    appendLine()
    appendLine("అష్ట కూట వివరాలు:")
    result.kootaScores.forEach { s ->
        appendLine("  ${s.nameTelugu} (${s.nameEnglish}): ${((s.obtainedPoints * 10).toInt() / 10.0)} / ${s.maxPoints.toInt()}")
    }
    if (checks.isNotEmpty()) {
        appendLine()
        appendLine("తెలుగు జ్యోతిష్య పరిశీలనలు:")
        checks.forEach { c ->
            val status = if (c.passed) "✔" else "✖"
            appendLine("  $status ${c.titleTelugu}: ${c.detailsTelugu}")
        }
    }
    if (hasCriticalIssues) {
        appendLine()
        appendLine("గమనిక: ముఖ్య దోష సూచనలు ఉన్నందున నిపుణుడితో సంప్రదించడం మంచిది.")
    }
    appendLine()
    append("Shared via NityaPooja")
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
                    "${((score.obtainedPoints * 10).toInt() / 10.0)} / ${score.maxPoints.toInt()}",
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

@Composable
private fun TeluguCheckCard(check: TeluguCompatibilityCheck) {
    val color = when {
        check.passed -> AuspiciousGreen
        check.critical -> InauspiciousRed
        else -> WarningAmber
    }
    Card(
        shape = RoundedCornerShape(12.dp),
        colors = CardDefaults.cardColors(containerColor = color.copy(alpha = 0.12f)),
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(12.dp),
            verticalArrangement = Arrangement.spacedBy(4.dp),
        ) {
            Text(
                check.titleTelugu,
                style = MaterialTheme.typography.bodyLarge,
                fontWeight = FontWeight.Bold,
                color = color,
            )
            Text(
                check.titleEnglish,
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
            )
            Text(
                check.detailsTelugu,
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurface,
            )
        }
    }
}

@Composable
private fun CriticalWarningCard() {
    Card(
        shape = RoundedCornerShape(12.dp),
        colors = CardDefaults.cardColors(containerColor = InauspiciousRed.copy(alpha = 0.12f)),
    ) {
        Text(
            "గమనిక: ముఖ్య దోష సూచనలు ఉన్నాయి. తుది నిర్ణయానికి అనుభవజ్ఞుడైన తెలుగు జ్యోతిష్య నిపుణుడిని సంప్రదించండి.",
            modifier = Modifier.padding(12.dp),
            style = MaterialTheme.typography.bodyMedium,
            color = InauspiciousRed,
            fontWeight = FontWeight.Medium,
        )
    }
}
