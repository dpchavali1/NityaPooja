package com.nityapooja.shared.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Share
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.drawWithContent
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.layer.drawLayer
import androidx.compose.ui.graphics.rememberGraphicsLayer
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.nityapooja.shared.data.muhurtam.MuhurtamRules.MuhurtamScore
import com.nityapooja.shared.platform.shareImage
import com.nityapooja.shared.ui.muhurtam.ScoredDate
import com.nityapooja.shared.ui.panchangam.LocationInfo
import com.nityapooja.shared.ui.panchangam.PanchangamData
import com.nityapooja.shared.ui.theme.TempleGold
import kotlinx.coroutines.launch

// ─────────────────────────────────────────────────────────────────────────────
// Panchangam Share Card + Preview Sheet
// ─────────────────────────────────────────────────────────────────────────────

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun PanchangamSharePreviewSheet(
    panchangam: PanchangamData,
    locationInfo: LocationInfo,
    onDismiss: () -> Unit,
) {
    val graphicsLayer = rememberGraphicsLayer()
    val coroutineScope = rememberCoroutineScope()

    ModalBottomSheet(
        onDismissRequest = onDismiss,
        containerColor = MaterialTheme.colorScheme.surface,
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp)
                .padding(bottom = 32.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
        ) {
            Text(
                "Share Preview",
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Bold,
                modifier = Modifier.padding(bottom = 16.dp),
            )

            // Card — rendered here so graphicsLayer can capture it
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .drawWithContent {
                        graphicsLayer.record { this@drawWithContent.drawContent() }
                        drawLayer(graphicsLayer)
                    }
            ) {
                PanchangamShareCard(panchangam = panchangam, locationInfo = locationInfo)
            }

            Spacer(Modifier.height(20.dp))

            Button(
                onClick = {
                    coroutineScope.launch {
                        val bitmap = graphicsLayer.toImageBitmap()
                        shareImage(bitmap, "నేటి పంచాంగం · Today's Panchangam — ${locationInfo.city}")
                        onDismiss()
                    }
                },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(52.dp),
                colors = ButtonDefaults.buttonColors(containerColor = TempleGold),
                shape = RoundedCornerShape(14.dp),
            ) {
                Icon(Icons.Default.Share, null, tint = Color.Black)
                Spacer(Modifier.width(8.dp))
                Text("Share · షేర్ చేయండి", color = Color.Black, fontWeight = FontWeight.Bold)
            }
        }
    }
}

@Composable
fun PanchangamShareCard(
    panchangam: PanchangamData,
    locationInfo: LocationInfo,
    modifier: Modifier = Modifier,
) {
    val cardBg = Brush.verticalGradient(
        colors = listOf(Color(0xFF1A1200), Color(0xFF0D0900)),
    )

    Box(
        modifier = modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(20.dp))
            .background(cardBg)
    ) {
        // Subtle radial glow in top-right
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(200.dp)
                .background(
                    Brush.radialGradient(
                        colors = listOf(TempleGold.copy(alpha = 0.08f), Color.Transparent),
                        center = Offset(Float.POSITIVE_INFINITY, 0f),
                        radius = 400f,
                    )
                )
        )

        Column(modifier = Modifier.padding(20.dp)) {
            // ── Header ──────────────────────────────────────────
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically,
            ) {
                Text("🪔", fontSize = 22.sp)
                Spacer(Modifier.width(8.dp))
                Text(
                    "NityaPooja",
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold,
                    color = TempleGold,
                )
                Spacer(Modifier.weight(1f))
                Text(
                    "పంచాంగం",
                    style = MaterialTheme.typography.labelMedium,
                    color = TempleGold.copy(alpha = 0.7f),
                )
            }

            Spacer(Modifier.height(2.dp))
            HorizontalDivider(color = TempleGold.copy(alpha = 0.3f), thickness = 0.5.dp)
            Spacer(Modifier.height(14.dp))

            // ── Date block ──────────────────────────────────────
            Text(
                panchangam.dateDisplay,
                style = MaterialTheme.typography.headlineSmall,
                fontWeight = FontWeight.Bold,
                color = Color.White,
            )
            Text(
                "${panchangam.masa.nameTelugu} ${panchangam.tithi.pakshaTelugu} ${panchangam.tithi.nameTelugu}",
                style = MaterialTheme.typography.bodyMedium,
                color = TempleGold.copy(alpha = 0.9f),
                fontWeight = FontWeight.Medium,
            )
            Text(
                "${panchangam.masa.nameEnglish} ${panchangam.tithi.paksha} ${panchangam.tithi.nameEnglish}",
                style = MaterialTheme.typography.labelMedium,
                color = Color.White.copy(alpha = 0.6f),
            )

            Spacer(Modifier.height(14.dp))

            // ── Data grid ───────────────────────────────────────
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(12.dp))
                    .background(Color.White.copy(alpha = 0.05f))
                    .padding(12.dp)
            ) {
                Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                    ShareCardRow("తిథి", panchangam.tithi.nameTelugu, panchangam.tithi.nameEnglish, "ends ${panchangam.tithi.endTime}")
                    ShareCardRow("నక్షత్రం", panchangam.nakshatra.nameTelugu, panchangam.nakshatra.nameEnglish, "ends ${panchangam.nakshatra.endTime}")
                    ShareCardRow("యోగం", panchangam.yoga.nameTelugu, panchangam.yoga.nameEnglish, "")
                    ShareCardRow("వారం", panchangam.teluguDay, panchangam.englishDay, "")
                }
            }

            Spacer(Modifier.height(12.dp))

            // ── Sun times + Rahu ────────────────────────────────
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(8.dp),
            ) {
                SharePill("🌅 ${panchangam.sunTimes.sunrise}", Modifier.weight(1f))
                SharePill("🌇 ${panchangam.sunTimes.sunset}", Modifier.weight(1f))
            }
            Spacer(Modifier.height(6.dp))
            SharePill("⚠️ రాహుకాలం  ${panchangam.rahuKaal.startTime} – ${panchangam.rahuKaal.endTime}", Modifier.fillMaxWidth())

            Spacer(Modifier.height(14.dp))
            HorizontalDivider(color = TempleGold.copy(alpha = 0.25f), thickness = 0.5.dp)
            Spacer(Modifier.height(8.dp))

            // ── Footer ──────────────────────────────────────────
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically,
            ) {
                Text(
                    "📍 ${locationInfo.city}",
                    style = MaterialTheme.typography.labelSmall,
                    color = Color.White.copy(alpha = 0.5f),
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis,
                    modifier = Modifier.weight(1f),
                )
                Text(
                    "nityapooja.app",
                    style = MaterialTheme.typography.labelSmall,
                    color = TempleGold.copy(alpha = 0.6f),
                )
            }
        }
    }
}

// ─────────────────────────────────────────────────────────────────────────────
// Muhurtam Share Card + Preview Sheet
// ─────────────────────────────────────────────────────────────────────────────

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MuhurtamSharePreviewSheet(
    scoredDate: ScoredDate,
    eventName: String,
    forNakshatra: String,
    onDismiss: () -> Unit,
) {
    val graphicsLayer = rememberGraphicsLayer()
    val coroutineScope = rememberCoroutineScope()

    ModalBottomSheet(
        onDismissRequest = onDismiss,
        containerColor = MaterialTheme.colorScheme.surface,
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp)
                .padding(bottom = 32.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
        ) {
            Text(
                "Share Preview",
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Bold,
                modifier = Modifier.padding(bottom = 16.dp),
            )

            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .drawWithContent {
                        graphicsLayer.record { this@drawWithContent.drawContent() }
                        drawLayer(graphicsLayer)
                    }
            ) {
                MuhurtamShareCard(scoredDate = scoredDate, eventName = eventName, forNakshatra = forNakshatra)
            }

            Spacer(Modifier.height(20.dp))

            Button(
                onClick = {
                    coroutineScope.launch {
                        val bitmap = graphicsLayer.toImageBitmap()
                        shareImage(bitmap, "శుభ ముహూర్తం · Auspicious Muhurtam — $eventName")
                        onDismiss()
                    }
                },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(52.dp),
                colors = ButtonDefaults.buttonColors(containerColor = TempleGold),
                shape = RoundedCornerShape(14.dp),
            ) {
                Icon(Icons.Default.Share, null, tint = Color.Black)
                Spacer(Modifier.width(8.dp))
                Text("Share · షేర్ చేయండి", color = Color.Black, fontWeight = FontWeight.Bold)
            }
        }
    }
}

@Composable
fun MuhurtamShareCard(
    scoredDate: ScoredDate,
    eventName: String,
    forNakshatra: String,
    modifier: Modifier = Modifier,
) {
    val p = scoredDate.panchangamData
    val r = scoredDate.result
    val scoreColor = when (r.score) {
        MuhurtamScore.EXCELLENT -> Color(0xFF2E7D32)
        MuhurtamScore.GOOD      -> Color(0xFFF9A825)
        MuhurtamScore.AVERAGE   -> Color(0xFFE65100)
        MuhurtamScore.AVOID     -> Color(0xFFC62828)
    }
    val scoreEmoji = when (r.score) {
        MuhurtamScore.EXCELLENT -> "🟢"
        MuhurtamScore.GOOD      -> "🟡"
        MuhurtamScore.AVERAGE   -> "🟠"
        MuhurtamScore.AVOID     -> "🔴"
    }
    val scoreLabel = when (r.score) {
        MuhurtamScore.EXCELLENT -> "Excellent · అత్యుత్తమం"
        MuhurtamScore.GOOD      -> "Good · శుభం"
        MuhurtamScore.AVERAGE   -> "Average · సాధారణం"
        MuhurtamScore.AVOID     -> "Avoid · వర్జ్యం"
    }
    val primaryPositiveReason = r.reasons.firstOrNull { it.isPositive }?.textTelugu ?: ""

    val cardBg = Brush.verticalGradient(
        colors = listOf(Color(0xFF0D1A0D), Color(0xFF080D08)),
    )

    Box(
        modifier = modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(20.dp))
            .background(cardBg)
    ) {
        Column(modifier = Modifier.padding(20.dp)) {
            // ── Header ──────────────────────────────────────────
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically,
            ) {
                Text("🪔", fontSize = 22.sp)
                Spacer(Modifier.width(8.dp))
                Text(
                    "NityaPooja",
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold,
                    color = TempleGold,
                )
                Spacer(Modifier.weight(1f))
                Text(
                    "శుభ ముహూర్తం",
                    style = MaterialTheme.typography.labelMedium,
                    color = TempleGold.copy(alpha = 0.7f),
                )
            }

            Spacer(Modifier.height(2.dp))
            HorizontalDivider(color = TempleGold.copy(alpha = 0.3f), thickness = 0.5.dp)
            Spacer(Modifier.height(14.dp))

            // ── Event & score ────────────────────────────────────
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically,
            ) {
                Column(modifier = Modifier.weight(1f)) {
                    Text(
                        eventName,
                        style = MaterialTheme.typography.titleLarge,
                        fontWeight = FontWeight.Bold,
                        color = Color.White,
                    )
                    if (forNakshatra.isNotBlank()) {
                        Text(
                            "Nakshatra: $forNakshatra",
                            style = MaterialTheme.typography.labelMedium,
                            color = Color.White.copy(alpha = 0.6f),
                        )
                    }
                }
                Spacer(Modifier.width(8.dp))
                Box(
                    modifier = Modifier
                        .clip(RoundedCornerShape(10.dp))
                        .background(scoreColor.copy(alpha = 0.15f))
                        .padding(horizontal = 10.dp, vertical = 6.dp)
                ) {
                    Text(
                        "$scoreEmoji $scoreLabel",
                        style = MaterialTheme.typography.labelMedium,
                        fontWeight = FontWeight.Bold,
                        color = scoreColor,
                    )
                }
            }

            Spacer(Modifier.height(14.dp))

            // ── Date block ───────────────────────────────────────
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(12.dp))
                    .background(TempleGold.copy(alpha = 0.08f))
                    .padding(14.dp)
            ) {
                Column {
                    Text(
                        p.dateDisplay,
                        style = MaterialTheme.typography.headlineSmall,
                        fontWeight = FontWeight.Bold,
                        color = TempleGold,
                        textAlign = TextAlign.Center,
                        modifier = Modifier.fillMaxWidth(),
                    )
                    Text(
                        "${p.teluguDay}  ·  ${p.masa.nameTelugu} ${p.tithi.pakshaTelugu} ${p.tithi.nameTelugu}",
                        style = MaterialTheme.typography.bodyMedium,
                        color = Color.White.copy(alpha = 0.8f),
                        textAlign = TextAlign.Center,
                        modifier = Modifier.fillMaxWidth(),
                    )
                }
            }

            Spacer(Modifier.height(12.dp))

            // ── Key muhurtam details ─────────────────────────────
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(12.dp))
                    .background(Color.White.copy(alpha = 0.04f))
                    .padding(12.dp)
            ) {
                Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                    ShareCardRow("నక్షత్రం", p.nakshatra.nameTelugu, p.nakshatra.nameEnglish, "")
                    ShareCardRow("తిథి", p.tithi.nameTelugu, p.tithi.nameEnglish, "")
                    ShareCardRow("యోగం", p.yoga.nameTelugu, p.yoga.nameEnglish, "")
                    if (primaryPositiveReason.isNotBlank()) {
                        Spacer(Modifier.height(4.dp))
                        Text(
                            "✓ $primaryPositiveReason",
                            style = MaterialTheme.typography.labelSmall,
                            color = TempleGold.copy(alpha = 0.8f),
                        )
                    }
                }
            }

            Spacer(Modifier.height(12.dp))
            HorizontalDivider(color = TempleGold.copy(alpha = 0.25f), thickness = 0.5.dp)
            Spacer(Modifier.height(8.dp))

            // ── Footer ──────────────────────────────────────────
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically,
            ) {
                Text(
                    "Shared via NityaPooja",
                    style = MaterialTheme.typography.labelSmall,
                    color = Color.White.copy(alpha = 0.4f),
                )
                Text(
                    "nityapooja.app",
                    style = MaterialTheme.typography.labelSmall,
                    color = TempleGold.copy(alpha = 0.6f),
                )
            }
        }
    }
}

// ─────────────────────────────────────────────────────────────────────────────
// Shared helpers
// ─────────────────────────────────────────────────────────────────────────────

@Composable
private fun ShareCardRow(
    labelTelugu: String,
    valueTelugu: String,
    valueEnglish: String,
    extra: String,
) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically,
    ) {
        Text(
            labelTelugu,
            style = MaterialTheme.typography.labelSmall,
            color = TempleGold.copy(alpha = 0.7f),
            modifier = Modifier.width(72.dp),
        )
        Text(
            "$valueTelugu  $valueEnglish",
            style = MaterialTheme.typography.labelMedium,
            fontWeight = FontWeight.Medium,
            color = Color.White.copy(alpha = 0.9f),
            modifier = Modifier.weight(1f),
            maxLines = 1,
            overflow = TextOverflow.Ellipsis,
        )
        if (extra.isNotBlank()) {
            Text(
                extra,
                style = MaterialTheme.typography.labelSmall,
                color = Color.White.copy(alpha = 0.4f),
            )
        }
    }
}

@Composable
private fun SharePill(text: String, modifier: Modifier = Modifier) {
    Box(
        modifier = modifier
            .clip(RoundedCornerShape(8.dp))
            .background(Color.White.copy(alpha = 0.06f))
            .padding(horizontal = 10.dp, vertical = 5.dp),
        contentAlignment = Alignment.Center,
    ) {
        Text(
            text,
            style = MaterialTheme.typography.labelSmall,
            color = Color.White.copy(alpha = 0.7f),
            textAlign = TextAlign.Center,
        )
    }
}
