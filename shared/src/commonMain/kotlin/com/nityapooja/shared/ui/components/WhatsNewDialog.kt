package com.nityapooja.shared.ui.components

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.nityapooja.shared.ui.theme.AuspiciousGreen
import com.nityapooja.shared.ui.theme.TempleGold

// Bump this when you want to show the dialog again after a new release
const val WHATS_NEW_VERSION = 3

@Composable
fun WhatsNewDialog(onDismiss: () -> Unit) {
    AlertDialog(
        onDismissRequest = onDismiss,
        title = {
            Column(horizontalAlignment = Alignment.CenterHorizontally, modifier = Modifier.fillMaxWidth()) {
                Text("NityaPooja", style = MaterialTheme.typography.headlineSmall, fontWeight = FontWeight.Bold, color = TempleGold)
                Text("కొత్త ఫీచర్లు · What's New", style = MaterialTheme.typography.labelMedium, color = MaterialTheme.colorScheme.onSurfaceVariant)
            }
        },
        text = {
            Column(
                modifier = Modifier.verticalScroll(rememberScrollState()),
                verticalArrangement = Arrangement.spacedBy(16.dp),
            ) {
                WhatsNewItem(
                    icon = Icons.Default.RecordVoiceOver,
                    titleTelugu = "సంకల్పం వాయిస్",
                    titleEnglish = "Sankalpam Priest Voice",
                    descriptionTelugu = "గూగుల్ AI వాయిస్ ద్వారా సంకల్పం పఠనం. మొదటిసారి తయారు చేసిన తర్వాత పరికరంలో సేవ్ అవుతుంది — ఇంటర్నెట్ అవసరం లేదు.",
                    descriptionEnglish = "Hear your personalized Sankalpam read aloud in a natural Telugu priest voice powered by Google AI. Generated once and saved offline.",
                    howToUse = "Home → Sankalpam card → వినండి button",
                )
                WhatsNewItem(
                    icon = Icons.Default.NightsStay,
                    titleTelugu = "గ్రహణం నోటిఫికేషన్లు",
                    titleEnglish = "Eclipse Alerts",
                    descriptionTelugu = "2025–2028 సూర్య, చంద్ర గ్రహణాల ముందు రోజు ఉదయం 8 గంటలకు మరియు స్పర్శ 1 గంట ముందు అలర్ట్ వస్తుంది.",
                    descriptionEnglish = "Get notified the morning before and 1 hour before every solar & lunar eclipse through 2028. Includes Sparsha, Madhyamam, and Moksham times.",
                    howToUse = "Settings → గ్రహణం నోటిఫికేషన్ toggle",
                )
                WhatsNewItem(
                    icon = Icons.Default.AutoStories,
                    titleTelugu = "పురాణాల క్విజ్",
                    titleEnglish = "Purana Quiz",
                    descriptionTelugu = "12 పురాణాలపై 300+ ప్రశ్నలు తెలుగు మరియు ఆంగ్లంలో. జ్ఞానాన్ని పరీక్షించుకోండి.",
                    descriptionEnglish = "300+ bilingual questions across 12 Puranas — Vishnu, Shiva, Bhagavata, Garuda and more. Test your knowledge!",
                    howToUse = "More → పురాణాల క్విజ్",
                )
                WhatsNewItem(
                    icon = Icons.Default.Widgets,
                    titleTelugu = "పంచాంగం విడ్జెట్",
                    titleEnglish = "Panchangam Home Widget",
                    descriptionTelugu = "నేటి తిథి, నక్షత్రం, వారం, యోగం, కరణం నేరుగా హోమ్ స్క్రీన్‌లో చూడండి. అర్ధరాత్రి స్వయంగా అప్‌డేట్ అవుతుంది.",
                    descriptionEnglish = "Today's Tithi, Nakshatra, Vara, Yoga, and Karana at a glance on your home screen. Auto-updates at midnight.",
                    howToUse = "Long-press home screen → Widgets → NityaPooja",
                )
            }
        },
        confirmButton = {
            TextButton(onClick = onDismiss) {
                Text("అర్థమైంది · Got it!", fontWeight = FontWeight.Bold, color = TempleGold)
            }
        },
    )
}

@Composable
private fun WhatsNewItem(
    icon: ImageVector,
    titleTelugu: String,
    titleEnglish: String,
    descriptionTelugu: String,
    descriptionEnglish: String,
    howToUse: String,
) {
    Row {
        Icon(icon, null, tint = TempleGold, modifier = Modifier.size(24.dp).padding(top = 2.dp))
        Spacer(Modifier.width(12.dp))
        Column {
            Text(titleTelugu, style = MaterialTheme.typography.titleSmall, fontWeight = FontWeight.Bold)
            Text(titleEnglish, style = MaterialTheme.typography.labelSmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
            Spacer(Modifier.height(4.dp))
            Text(descriptionTelugu, style = MaterialTheme.typography.bodySmall)
            Spacer(Modifier.height(2.dp))
            Text(descriptionEnglish, style = MaterialTheme.typography.bodySmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
            Spacer(Modifier.height(4.dp))
            Surface(shape = MaterialTheme.shapes.small, color = AuspiciousGreen.copy(alpha = 0.1f)) {
                Text(
                    howToUse,
                    modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp),
                    style = MaterialTheme.typography.labelSmall,
                    color = AuspiciousGreen,
                    fontWeight = FontWeight.Medium,
                )
            }
        }
    }
}
