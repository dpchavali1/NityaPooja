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
const val WHATS_NEW_VERSION = 4

@Composable
fun WhatsNewDialog(onDismiss: () -> Unit) {
    AlertDialog(
        onDismissRequest = onDismiss,
        title = {
            Column(horizontalAlignment = Alignment.CenterHorizontally, modifier = Modifier.fillMaxWidth()) {
                Text("NityaPooja", style = MaterialTheme.typography.headlineSmall, fontWeight = FontWeight.Bold, color = TempleGold)
                Text("కొత్త ఫీచర్లు · What's New in v2.1.1", style = MaterialTheme.typography.labelMedium, color = MaterialTheme.colorScheme.onSurfaceVariant)
            }
        },
        text = {
            Column(
                modifier = Modifier.verticalScroll(rememberScrollState()),
                verticalArrangement = Arrangement.spacedBy(16.dp),
            ) {
                WhatsNewItem(
                    icon = Icons.Default.Celebration,
                    titleTelugu = "కుటుంబ పర్వదినాలు",
                    titleEnglish = "Family Important Days",
                    descriptionTelugu = "పుట్టినరోజులు, వివాహ వార్షికోత్సవాలు, తిథి రోజులు నమోదు చేయండి. తిథి రోజు తేదీ ప్రతి సంవత్సరం మారుతుంది — అది స్వయంగా లెక్కిస్తుంది. ముందు రోజు & ఆ రోజు రిమైండర్ వస్తుంది.",
                    descriptionEnglish = "Add birthdays, anniversaries, and tithi (shraddha) days. Tithi dates shift each year — the app calculates the next 3 years automatically. Get reminded the day before and on the day.",
                    howToUse = "More → కుటుంబ పర్వదినాలు → + Add",
                )
                WhatsNewItem(
                    icon = Icons.Default.EmojiEvents,
                    titleTelugu = "ఆధ్యాత్మిక పురస్కారాలు",
                    titleEnglish = "Spiritual Badges",
                    descriptionTelugu = "జప సాధనకు హిందూ చిహ్నాలతో పురస్కారాలు: త్రిపతాక (3 రోజులు), సప్తర్షి (7 రోజులు), సహస్ర దీపం (1000 మాలలు), వైకుంఠ ద్వారం (30 రోజులు వరుసగా) మరియు మరిన్ని.",
                    descriptionEnglish = "Earn badges for your japa practice — Tripataka (3 days), Saptarishi (7 days), Sahasra Deepam (1000 malas), Vaikunta Dwaram (30 consecutive days) and more.",
                    howToUse = "More → పురస్కారాలు",
                )
                WhatsNewItem(
                    icon = Icons.Default.Lock,
                    titleTelugu = "జప లాక్ మోడ్",
                    titleEnglish = "Japa Lock Mode",
                    descriptionTelugu = "జపం చేస్తున్నప్పుడు స్క్రీన్ లాక్ చేయండి — పూర్తి స్క్రీన్, కౌంటర్ మాత్రమే కనిపిస్తుంది. పైకి స్వైప్ చేసి బయటకు వెళ్ళండి.",
                    descriptionEnglish = "Lock the screen during japa for full focus — full screen counter only, no distractions. Swipe up to exit.",
                    howToUse = "Japa Counter → 🔒 Lock button",
                )
                WhatsNewItem(
                    icon = Icons.Default.AddPhotoAlternate,
                    titleTelugu = "దేవత ఫోటో మార్చండి",
                    titleEnglish = "Custom Deity Photos",
                    descriptionTelugu = "మీ స్వంత దేవత ఫోటో పెట్టుకోండి — మీ ఇంటి దేవత, మీకు ఇష్టమైన ఆలయం ఫోటో. మీ పరికరంలో మాత్రమే సేవ్ అవుతుంది.",
                    descriptionEnglish = "Set your own deity photos — your kula devata, your favourite temple idol. Stored only on your device, never uploaded.",
                    howToUse = "Any Deity screen → 📷 నా ఫోటో సెట్ చేయండి",
                )
                WhatsNewItem(
                    icon = Icons.Default.Person,
                    titleTelugu = "వ్యక్తిగత మార్గదర్శనం",
                    titleEnglish = "Personalized Daily Briefing",
                    descriptionTelugu = "మీ నక్షత్రం ఆధారంగా నేటి ఆధ్యాత్మిక మార్గదర్శనం హోమ్ స్క్రీన్‌లో వస్తుంది. Profile లో నక్షత్రం నమోదు చేయండి.",
                    descriptionEnglish = "Daily spiritual guidance based on your nakshatra appears on your home screen. Set your nakshatra in Profile to activate.",
                    howToUse = "Profile → Nakshatra → then check Home screen",
                )
                WhatsNewItem(
                    icon = Icons.Default.Star,
                    titleTelugu = "ఈ వారం దేవత",
                    titleEnglish = "Deity of the Week",
                    descriptionTelugu = "ప్రతి వారం ఒక దేవతపై దృష్టి పెట్టండి — ఆ దేవతకు సంబంధించిన మంత్రాలు, భక్తి చిట్కాలు హోమ్ స్క్రీన్‌లో కనిపిస్తాయి.",
                    descriptionEnglish = "Each week focuses on a different deity — mantras, tips, and auspicious days shown on your home screen.",
                    howToUse = "Home screen → ఈ వారం దేవత card",
                )
                WhatsNewItem(
                    icon = Icons.Default.Language,
                    titleTelugu = "వెబ్‌సైట్ అందుబాటులో",
                    titleEnglish = "Website: nityapooja.app",
                    descriptionTelugu = "నిత్య పూజ వెబ్‌సైట్ ఇప్పుడు అందుబాటులో ఉంది — పంచాంగం, ఫీచర్లు, సహాయం.",
                    descriptionEnglish = "NityaPooja now has a full website with live Panchangam, features, and support.",
                    howToUse = "Settings → About → Visit nityapooja.app",
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
