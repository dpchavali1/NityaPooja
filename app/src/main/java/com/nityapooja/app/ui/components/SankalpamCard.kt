package com.nityapooja.app.ui.components

import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.nityapooja.app.ui.panchangam.NAKSHATRA_NAMES_ENGLISH
import com.nityapooja.app.ui.panchangam.NAKSHATRA_NAMES_TELUGU
import com.nityapooja.app.ui.panchangam.PanchangamData
import com.nityapooja.app.ui.theme.NityaPoojaTextStyles
import com.nityapooja.app.ui.theme.TempleGold

@Composable
fun SankalpamCard(
    panchangamData: PanchangamData,
    userName: String,
    gotra: String,
    userNakshatra: String,
    city: String,
    fontScale: Float,
    onNavigateToSettings: (() -> Unit)? = null,
    modifier: Modifier = Modifier,
) {
    val missingInfo = gotra.isBlank() || userNakshatra.isBlank()

    GlassmorphicCard(
        modifier = modifier,
        cornerRadius = 16.dp,
        contentPadding = 16.dp,
        accentColor = TempleGold,
    ) {
        Text(
            "సంకల్పం · SANKALPAM",
            style = NityaPoojaTextStyles.GoldLabel,
            color = TempleGold,
        )
        Spacer(Modifier.height(12.dp))

        // Telugu Sankalpam text
        Text(
            buildSankalpamTelugu(panchangamData, userName, gotra, userNakshatra, city),
            style = MaterialTheme.typography.bodyMedium.copy(
                fontSize = (14 * fontScale).sp,
                lineHeight = (22 * fontScale).sp,
            ),
            color = MaterialTheme.colorScheme.onSurface,
        )

        Spacer(Modifier.height(12.dp))
        HorizontalDivider(color = MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.3f))
        Spacer(Modifier.height(12.dp))

        // English transliteration
        Text(
            buildSankalpamEnglish(panchangamData, userName, gotra, userNakshatra, city),
            style = MaterialTheme.typography.bodySmall.copy(
                fontSize = (12 * fontScale).sp,
                lineHeight = (18 * fontScale).sp,
            ),
            color = MaterialTheme.colorScheme.onSurfaceVariant,
        )

        // Prompt to set missing info
        if (missingInfo && onNavigateToSettings != null) {
            Spacer(Modifier.height(12.dp))
            HorizontalDivider(color = MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.3f))
            Spacer(Modifier.height(8.dp))
            TextButton(
                onClick = onNavigateToSettings,
                modifier = Modifier.align(Alignment.End),
            ) {
                Icon(
                    Icons.Default.Settings,
                    contentDescription = null,
                    modifier = Modifier.size(16.dp),
                    tint = TempleGold,
                )
                Spacer(Modifier.width(4.dp))
                Text(
                    "గోత్ర/నక్షత్రం సెట్ చేయండి · Set in Settings",
                    style = MaterialTheme.typography.labelSmall,
                    color = TempleGold,
                    fontWeight = FontWeight.Medium,
                )
            }
        }
    }
}

private fun buildSankalpamTelugu(
    data: PanchangamData,
    userName: String,
    gotra: String,
    userNakshatra: String,
    city: String,
): String {
    val name = userName.ifBlank { "___" }
    val gotraText = gotra.ifBlank { "___" }
    val userNakshatraTelugu = if (userNakshatra.isNotBlank()) {
        val idx = NAKSHATRA_NAMES_ENGLISH.indexOf(userNakshatra)
        if (idx >= 0) NAKSHATRA_NAMES_TELUGU[idx] else userNakshatra
    } else "___"

    return buildString {
        append("శ్రీమాన్ ${data.samvatsara.nameTelugu} నామ సంవత్సరే, ")
        append("${data.masa.nameTelugu} మాసే,\n")
        append("${data.tithi.pakshaTelugu}, ${data.tithi.nameTelugu} తిథౌ, ")
        append("${data.teluguDay} వాసరే,\n")
        append("${data.nakshatra.nameTelugu} నక్షత్రే, ")
        append("${data.yoga.nameTelugu} యోగే, ")
        append("${data.karana.firstNameTelugu} కరణే,\n")
        append("${city} క్షేత్రే, ${gotraText} గోత్రస్య,\n")
        append("${userNakshatraTelugu} నక్షత్రే జాతస్య,\n")
        append("${name} నామధేయస్య,\n")
        append("శ్రీమన్నారాయణ ప్రీత్యర్థం పూజాం కరిష్యే ॥")
    }
}

private fun buildSankalpamEnglish(
    data: PanchangamData,
    userName: String,
    gotra: String,
    userNakshatra: String,
    city: String,
): String {
    val name = userName.ifBlank { "___" }
    val gotraText = gotra.ifBlank { "___" }
    val nakshatraText = userNakshatra.ifBlank { "___" }

    return buildString {
        append("Shriman ${data.samvatsara.nameEnglish} nama samvatsare, ")
        append("${data.masa.nameEnglish} mase,\n")
        append("${data.tithi.paksha}, ${data.tithi.nameEnglish} tithau, ")
        append("${data.englishDay} vasare,\n")
        append("${data.nakshatra.nameEnglish} nakshtre, ")
        append("${data.yoga.nameEnglish} yoge, ")
        append("${data.karana.firstNameEnglish} karane,\n")
        append("${city} kshetre, ${gotraText} gotrasya,\n")
        append("${nakshatraText} nakshtre jatasya,\n")
        append("${name} namadheyasya,\n")
        append("Shrimannarayana prityartham pujam karishye.")
    }
}
