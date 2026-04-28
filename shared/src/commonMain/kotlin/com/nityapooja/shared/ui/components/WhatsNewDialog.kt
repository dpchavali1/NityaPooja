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
const val WHATS_NEW_VERSION = 6

@Composable
fun WhatsNewDialog(onDismiss: () -> Unit) {
    AlertDialog(
        onDismissRequest = onDismiss,
        title = {
            Column(horizontalAlignment = Alignment.CenterHorizontally, modifier = Modifier.fillMaxWidth()) {
                Text("NityaPooja", style = MaterialTheme.typography.headlineSmall, fontWeight = FontWeight.Bold, color = TempleGold)
                Text("కొత్త ఫీచర్లు · What's New in v2.3", style = MaterialTheme.typography.labelMedium, color = MaterialTheme.colorScheme.onSurfaceVariant)
            }
        },
        text = {
            Column(
                modifier = Modifier.verticalScroll(rememberScrollState()),
                verticalArrangement = Arrangement.spacedBy(16.dp),
            ) {
                WhatsNewItem(
                    icon = Icons.Default.PhoneAndroid,
                    titleTelugu = "రాశిఫలం హోమ్ విజెట్",
                    titleEnglish = "Rashifal Home Screen Widget",
                    descriptionTelugu = "ఇప్పుడు మీ రాశిఫలం నేరుగా హోమ్ స్క్రీన్‌పై చూడవచ్చు — అప్లికేషన్ తెరవకుండా. చంద్రాష్టమ హెచ్చరిక ఎర్ర రంగులో. తెలుగు లేదా ఆంగ్లంలో చూపిస్తుంది.",
                    descriptionEnglish = "See your daily rashifal right on your home screen without opening the app. Chandrashtama shown in red. Switches language with your app setting.",
                    howToUse = "Settings → Sankalpam Details → మీ రాశి ఎంచుకోండి → Home screen long-press → Widgets → Nitya Pooja → Rashifal",
                )
                WhatsNewItem(
                    icon = Icons.Default.Widgets,
                    titleTelugu = "పంచాంగం విజెట్ — పెద్ద అక్షరాలు",
                    titleEnglish = "Panchangam Widget — Larger Text",
                    descriptionTelugu = "పంచాంగం విజెట్ ఇప్పుడు 4×3 పరిమాణంలో పెద్ద అక్షరాలతో వస్తుంది — వయసైన వారికి సులభంగా చదవడానికి. తిథి, నక్షత్రం, యోగం, కరణం, సూర్యోదయ/సూర్యాస్తమయ స్పష్టంగా.",
                    descriptionEnglish = "Panchangam widget redesigned as a 4×3 tile with large, easy-to-read text — perfect for elderly users. Shows Tithi, Nakshatra, Yoga, Karana, and Sunrise/Sunset clearly.",
                    howToUse = "Home screen long-press → Widgets → Nitya Pooja → Panchangam",
                )
                WhatsNewItem(
                    icon = Icons.Default.Translate,
                    titleTelugu = "ఆంగ్లంలో చూపించు",
                    titleEnglish = "Show English Translations",
                    descriptionTelugu = "మంత్రాల అర్థాలు, రాశిఫలం — అన్నీ ఇప్పుడు ఆంగ్లంలో చదవవచ్చు. ఒక్క టాగుల్‌తో మారండి.",
                    descriptionEnglish = "Mantra meanings, benefits, and Rashifal predictions can now be read in English. One toggle switches the whole app.",
                    howToUse = "Settings → Appearance → Show English Translations",
                )
                WhatsNewItem(
                    icon = Icons.Default.Stars,
                    titleTelugu = "రాశిఫలం — నిజమైన వైదిక గోచారం",
                    titleEnglish = "Rashifal — Real Vedic Moon Transit",
                    descriptionTelugu = "చంద్రుని నిజమైన సిద్ధాంత స్థానం ఆధారంగా రాశిఫలం లెక్కిస్తాం. మీ జన్మ రాశికి చంద్రుడు ఏ స్థానంలో ఉన్నాడో చూపిస్తాం. చంద్రాష్టమం (8వ స్థానం) ప్రత్యేక హెచ్చరికతో.",
                    descriptionEnglish = "Rashifal now uses actual Vedic Moon gochara — your janma rashi's house position calculated from real Moon transit. Chandrashtama (8th house) shown with a clear warning.",
                    howToUse = "More → రాశిఫలం → మీ రాశి నొక్కండి",
                )
                WhatsNewItem(
                    icon = Icons.Default.Share,
                    titleTelugu = "అందమైన షేర్ కార్డ్",
                    titleEnglish = "Graphic Share Cards",
                    descriptionTelugu = "పంచాంగం మరియు ముహూర్తాన్ని అందమైన చిత్ర కార్డ్‌గా WhatsApp, Instagram కి నేరుగా షేర్ చేయండి.",
                    descriptionEnglish = "Share Panchangam and Muhurtam as beautiful image cards directly to WhatsApp, Instagram, and more.",
                    howToUse = "Panchangam → Share → Preview → షేర్ చేయండి",
                )
                WhatsNewItem(
                    icon = Icons.Default.Quiz,
                    titleTelugu = "పురాణాల క్విజ్",
                    titleEnglish = "Puranas Quiz",
                    descriptionTelugu = "12 పురాణాలపై 300+ ద్విభాష ప్రశ్నలు — వేద పురాణ జ్ఞానం పరీక్షించుకోండి. 70%+ స్కోర్ చేస్తే పురాణ పండిత్ పురస్కారం.",
                    descriptionEnglish = "300+ bilingual questions across 12 Puranas. Score 70%+ to earn the Purana Scholar badge.",
                    howToUse = "More → పురాణాల క్విజ్",
                )
                WhatsNewItem(
                    icon = Icons.Default.TextFields,
                    titleTelugu = "ఫాంట్ సైజ్ నియంత్రణ",
                    titleEnglish = "Font Size Controls",
                    descriptionTelugu = "మరిన్ని స్క్రీన్‌లో A+/A− బటన్‌లతో అన్ని టెక్స్ట్ సైజ్ మార్చవచ్చు — చదవడానికి అనుకూలంగా సెట్ చేసుకోండి.",
                    descriptionEnglish = "Adjust text size across key screens with A+/A− buttons — set your preferred reading size once and it applies everywhere.",
                    howToUse = "More → A+ / A− buttons in top bar",
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
