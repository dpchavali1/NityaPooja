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
const val WHATS_NEW_VERSION = 2

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
                    icon = Icons.Default.EventAvailable,
                    titleTelugu = "శుభ ముహూర్తాలు",
                    titleEnglish = "Muhurtam Finder",
                    descriptionTelugu = "గృహ ప్రవేశం, వివాహం, ఉపనయనం వంటి శుభ కార్యాలకు ఉత్తమ ముహూర్తాలు చూడండి. తార బలం, చంద్ర బలం ఆధారంగా వ్యక్తిగత ఫలితాలు.",
                    descriptionEnglish = "Find best dates for housewarming, wedding, and more. Personalized with Tara Balam & Chandrabalam.",
                    howToUse = "More → తెలుగు సంస్కృతి → శుభ ముహూర్తాలు",
                )
                WhatsNewItem(
                    icon = Icons.Default.Brightness5,
                    titleTelugu = "వ్రతాలు",
                    titleEnglish = "Vratas & Observances",
                    descriptionTelugu = "ఏకాదశి, ప్రదోషం, చతుర్థి వంటి 14 వ్రతాలు. రాబోయే వ్రతాల తేదీలు, ఉపవాస నియమాలు, ప్రత్యేక ఆహారం.",
                    descriptionEnglish = "14 vratas with upcoming dates, fasting rules, and special foods. Mark favorites for quick access.",
                    howToUse = "More → తెలుగు సంస్కృతి → వ్రతాలు",
                )
                WhatsNewItem(
                    icon = Icons.Default.CalendarMonth,
                    titleTelugu = "పవిత్ర మాసాలు",
                    titleEnglish = "Sacred Months",
                    descriptionTelugu = "కార్తీక, శ్రావణ, మార్గశిర, చైత్ర మాసాల దైనందిన ఆచరణలు, ప్రత్యేక దినాలు, తేదీలు.",
                    descriptionEnglish = "Daily practices and special days for Karthika, Shravana, Margashira, and Chaitra months with dates.",
                    howToUse = "More → తెలుగు సంస్కృతి → పవిత్ర మాసాలు",
                )
                WhatsNewItem(
                    icon = Icons.Default.Celebration,
                    titleTelugu = "27 పండుగలు + శుభాకాంక్షలు",
                    titleEnglish = "27 Festivals + Wishes",
                    descriptionTelugu = "బతుకమ్మ, బోనాలు, కార్తీక పౌర్ణమి వంటి 17 కొత్త పండుగలు. ప్రతి పండుగకు వ్యక్తిగత శుభాకాంక్షలు + నోటిఫికేషన్.",
                    descriptionEnglish = "17 new Telugu festivals with personalized greetings and morning push notifications.",
                    howToUse = "Home → Festivals section, or More → పండుగలు",
                )
                WhatsNewItem(
                    icon = Icons.Default.TempleHindu,
                    titleTelugu = "25 దేవాలయాలు",
                    titleEnglish = "25 AP/TS Temples",
                    descriptionTelugu = "పంచారామాలు, శక్తి పీఠాలు, చిల్కూర్ బాలాజీ, మంత్రాలయం వంటి 17 కొత్త దేవాలయాలు.",
                    descriptionEnglish = "Pancha Aramas, Shakti Peethalu, Chilkur Balaji, Mantralayam and more.",
                    howToUse = "Quick Access → దేవాలయాలు",
                )
                WhatsNewItem(
                    icon = Icons.Default.People,
                    titleTelugu = "కుటుంబ ప్రొఫైల్స్",
                    titleEnglish = "Family Profiles for Muhurtam",
                    descriptionTelugu = "కుటుంబ సభ్యుల నక్షత్రం, రాశి సేవ్ చేసి ముహూర్తం త్వరగా చెక్ చేయండి.",
                    descriptionEnglish = "Save family members' nakshatra & rashi for quick muhurtam switching.",
                    howToUse = "Muhurtam Finder → '+ కుటుంబం' button",
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
