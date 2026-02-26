package com.nityapooja.shared.ui.more

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.MenuBook
import androidx.compose.material.icons.filled.ShoppingCart
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.nityapooja.shared.ui.components.SectionHeader
import com.nityapooja.shared.ui.theme.TempleGold

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MoreScreen(
    onNavigateToStore: () -> Unit = {},
    onNavigateToStotrams: () -> Unit = {},
    onNavigateToKeertanalu: () -> Unit = {},
    onNavigateToTemples: () -> Unit = {},
    onNavigateToFestivals: () -> Unit = {},
    onNavigateToSettings: () -> Unit = {},
    onNavigateToJapa: () -> Unit = {},
    onNavigateToBhajans: () -> Unit = {},
    onNavigateToSuprabhatam: () -> Unit = {},
    onNavigateToAshtotra: () -> Unit = {},
    onNavigateToMantras: () -> Unit = {},
    onNavigateToChalisas: () -> Unit = {},
    onNavigateToRashifal: () -> Unit = {},
    onNavigateToGuidedPuja: () -> Unit = {},
    onNavigateToPoojaTimer: () -> Unit = {},
    onNavigateToJatakaChakram: () -> Unit = {},
    onNavigateToGunaMilan: () -> Unit = {},
    onNavigateToVirtualPoojaRoom: () -> Unit = {},
) {
    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Column {
                        Text("మరిన్ని", style = MaterialTheme.typography.titleLarge, fontWeight = FontWeight.Bold)
                        Text("More", style = MaterialTheme.typography.labelSmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
                    }
                }
            )
        }
    ) { padding ->
        LazyColumn(
            modifier = Modifier.fillMaxSize().padding(padding),
            contentPadding = PaddingValues(16.dp),
            verticalArrangement = Arrangement.spacedBy(8.dp),
        ) {
            item {
                SectionHeader(titleTelugu = "భక్తి స్టోర్", titleEnglish = "Devotional Store")
                Spacer(Modifier.height(8.dp))
            }
            item { MoreMenuItem("పూజా సామగ్రి", "Shop Puja Items", Icons.Default.ShoppingCart, onNavigateToStore) }

            item {
                Spacer(Modifier.height(16.dp))
                SectionHeader(titleTelugu = "భక్తి విభాగాలు", titleEnglish = "Devotional Sections")
                Spacer(Modifier.height(8.dp))
            }
            item { MoreMenuItem("స్తోత్రాలు", "Stotrams", Icons.AutoMirrored.Filled.MenuBook, onNavigateToStotrams) }
            item { MoreMenuItem("కీర్తనలు", "Keertanalu", Icons.Default.LibraryMusic, onNavigateToKeertanalu) }
            item { MoreMenuItem("మంత్రాలు", "Mantras", Icons.Default.SelfImprovement, onNavigateToMantras) }
            item { MoreMenuItem("భజనలు", "Bhajans", Icons.Default.MusicNote, onNavigateToBhajans) }
            item { MoreMenuItem("సుప్రభాతం", "Suprabhatam", Icons.Default.WbSunny, onNavigateToSuprabhatam) }
            item { MoreMenuItem("అష్టోత్రాలు", "Ashtottara", Icons.Default.AutoAwesome, onNavigateToAshtotra) }
            item { MoreMenuItem("జపం", "Japa Counter", Icons.Default.SelfImprovement, onNavigateToJapa) }
            item { MoreMenuItem("చాలీసా", "Chalisa", Icons.AutoMirrored.Filled.MenuBook, onNavigateToChalisas) }

            item {
                Spacer(Modifier.height(16.dp))
                SectionHeader(titleTelugu = "ఆధ్యాత్మిక సాధనాలు", titleEnglish = "Spiritual Tools")
                Spacer(Modifier.height(8.dp))
            }
            item { MoreMenuItem("రాశిఫలం", "Rashifal", Icons.Default.Stars, onNavigateToRashifal) }
            item { MoreMenuItem("పూజా విధానం", "Guided Puja", Icons.Default.Spa, onNavigateToGuidedPuja) }
            item { MoreMenuItem("పూజా టైమర్", "Pooja Timer", Icons.Default.Timer, onNavigateToPoojaTimer) }
            item { MoreMenuItem("పూజా గది", "Virtual Pooja Room", Icons.Default.VolunteerActivism, onNavigateToVirtualPoojaRoom) }

            item {
                Spacer(Modifier.height(16.dp))
                SectionHeader(titleTelugu = "జ్యోతిష్యం", titleEnglish = "Jyotish / Astrology")
                Spacer(Modifier.height(8.dp))
            }
            item { MoreMenuItem("జాతక చక్రం", "Birth Chart", Icons.Default.AutoAwesome, onNavigateToJatakaChakram) }
            item { MoreMenuItem("గుణ మిలనం", "Compatibility Match", Icons.Default.Favorite, onNavigateToGunaMilan) }

            item {
                Spacer(Modifier.height(16.dp))
                SectionHeader(titleTelugu = "ప్రదేశాలు", titleEnglish = "Places & Events")
                Spacer(Modifier.height(8.dp))
            }
            item { MoreMenuItem("దేవాలయాలు", "Temples", Icons.Default.TempleHindu, onNavigateToTemples) }
            item { MoreMenuItem("పండుగలు", "Festivals", Icons.Default.Celebration, onNavigateToFestivals) }

            item {
                Spacer(Modifier.height(16.dp))
                SectionHeader(titleTelugu = "సెట్టింగ్‌లు", titleEnglish = "App")
                Spacer(Modifier.height(8.dp))
            }
            item { MoreMenuItem("సెట్టింగ్‌లు", "Settings", Icons.Default.Settings, onNavigateToSettings) }
        }
    }
}

@Composable
private fun MoreMenuItem(
    titleTelugu: String,
    titleEnglish: String,
    icon: ImageVector,
    onClick: () -> Unit,
) {
    Card(
        modifier = Modifier.fillMaxWidth().clickable(onClick = onClick),
        shape = RoundedCornerShape(12.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant),
    ) {
        Row(
            modifier = Modifier.padding(16.dp),
            verticalAlignment = Alignment.CenterVertically,
        ) {
            Icon(icon, titleEnglish, tint = TempleGold)
            Spacer(Modifier.width(16.dp))
            Column(modifier = Modifier.weight(1f)) {
                Text(titleTelugu, style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Medium)
                Text(titleEnglish, style = MaterialTheme.typography.bodySmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
            }
            Icon(Icons.Default.ChevronRight, "Go", tint = MaterialTheme.colorScheme.onSurfaceVariant)
        }
    }
}
