package com.nityapooja.shared.ui.more

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.GridItemSpan
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.MenuBook
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import com.nityapooja.shared.ui.components.FontSizeControls
import com.nityapooja.shared.ui.components.FontSizeViewModel
import com.nityapooja.shared.ui.components.GlassmorphicCard
import com.nityapooja.shared.ui.components.QuickAccessCircle
import com.nityapooja.shared.ui.components.ScaledContent
import com.nityapooja.shared.ui.components.SectionHeader
import com.nityapooja.shared.ui.theme.TempleGold
import org.koin.compose.viewmodel.koinViewModel

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
    onNavigateToSavedProfiles: () -> Unit = {},
    onNavigateToVirtualPoojaRoom: () -> Unit = {},
    onNavigateToPuranaQuiz: () -> Unit = {},
    onNavigateToMuhurtam: () -> Unit = {},
    onNavigateToVratas: () -> Unit = {},
    onNavigateToSacredMonth: () -> Unit = {},
    onNavigateToChoghadiya: () -> Unit = {},
    onNavigateToPlanetTransits: () -> Unit = {},
    onNavigateToFamilyDays: () -> Unit = {},
    onNavigateToBadges: () -> Unit = {},
    onNavigateToFeatures: () -> Unit = {},
    bannerAd: (@Composable () -> Unit)? = null,
    fontSizeViewModel: FontSizeViewModel = koinViewModel(),
) {
    val fontSize by fontSizeViewModel.fontSize.collectAsState()
    val fontScale = fontSize / 16f

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Column {
                        Text("మరిన్ని", style = MaterialTheme.typography.titleLarge, fontWeight = FontWeight.Bold)
                        Text("More", style = MaterialTheme.typography.labelSmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
                    }
                },
                actions = {
                    FontSizeControls(
                        fontSize = fontSize,
                        onDecrease = fontSizeViewModel::decrease,
                        onIncrease = fontSizeViewModel::increase,
                    )
                },
            )
        }
    ) { padding ->
        ScaledContent(fontScale) {
        LazyVerticalGrid(
            columns = GridCells.Fixed(2),
            modifier = Modifier.fillMaxSize().padding(padding),
            contentPadding = PaddingValues(horizontal = 16.dp, vertical = 12.dp),
            verticalArrangement = Arrangement.spacedBy(10.dp),
            horizontalArrangement = Arrangement.spacedBy(10.dp),
        ) {

            // ── Quick Access Row ─────────────────────────────────────────────
            item(span = { GridItemSpan(maxLineSpan) }) {
                Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
                    SectionHeader(titleTelugu = "త్వరిత యాక్సెస్", titleEnglish = "Quick Access")
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceEvenly,
                    ) {
                        QuickAccessCircle(
                            icon = Icons.Default.SelfImprovement,
                            labelTelugu = "జపం",
                            labelEnglish = "Japa",
                            onClick = onNavigateToJapa,
                            modifier = Modifier.weight(1f),
                        )
                        QuickAccessCircle(
                            icon = Icons.AutoMirrored.Filled.MenuBook,
                            labelTelugu = "స్తోత్రాలు",
                            labelEnglish = "Stotrams",
                            onClick = onNavigateToStotrams,
                            modifier = Modifier.weight(1f),
                        )
                        QuickAccessCircle(
                            icon = Icons.Default.Stars,
                            labelTelugu = "రాశిఫలం",
                            labelEnglish = "Rashifal",
                            onClick = onNavigateToRashifal,
                            modifier = Modifier.weight(1f),
                        )
                        QuickAccessCircle(
                            icon = Icons.Default.EventAvailable,
                            labelTelugu = "ముహూర్తం",
                            labelEnglish = "Muhurtam",
                            onClick = onNavigateToMuhurtam,
                            modifier = Modifier.weight(1f),
                        )
                        QuickAccessCircle(
                            icon = Icons.Default.Spa,
                            labelTelugu = "పూజ విధి",
                            labelEnglish = "Guided",
                            onClick = onNavigateToGuidedPuja,
                            modifier = Modifier.weight(1f),
                        )
                    }
                }
            }

            // ── Store Hero ───────────────────────────────────────────────────
            item(span = { GridItemSpan(maxLineSpan) }) {
                GlassmorphicCard(
                    accentColor = TempleGold,
                    cornerRadius = 20.dp,
                    contentPadding = 18.dp,
                    onClick = onNavigateToStore,
                ) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.SpaceBetween,
                        modifier = Modifier.fillMaxWidth(),
                    ) {
                        Column(modifier = Modifier.weight(1f)) {
                            Text("పూజా సామగ్రి", style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold, color = TempleGold)
                            Text("శుభ కానుకలు · Devotional Items", style = MaterialTheme.typography.bodySmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
                            Spacer(Modifier.height(8.dp))
                            Surface(
                                shape = RoundedCornerShape(20.dp),
                                color = TempleGold.copy(alpha = 0.15f),
                            ) {
                                Text(
                                    "Browse Store →",
                                    modifier = Modifier.padding(horizontal = 12.dp, vertical = 5.dp),
                                    style = MaterialTheme.typography.labelMedium,
                                    fontWeight = FontWeight.Bold,
                                    color = TempleGold,
                                )
                            }
                        }
                        Icon(Icons.Default.ShoppingCart, null, tint = TempleGold, modifier = Modifier.size(48.dp))
                    }
                }
            }

            // ── Section: Worship & Texts ─────────────────────────────────────
            item(span = { GridItemSpan(maxLineSpan) }) {
                Spacer(Modifier.height(4.dp))
                SectionHeader(titleTelugu = "భక్తి గ్రంథాలు & జపం", titleEnglish = "Worship & Texts")
            }
            item { CompactGridTile("స్తోత్రాలు", "Stotrams", Icons.AutoMirrored.Filled.MenuBook, onNavigateToStotrams) }
            item { CompactGridTile("కీర్తనలు", "Keertanalu", Icons.Default.LibraryMusic, onNavigateToKeertanalu) }
            item { CompactGridTile("మంత్రాలు", "Mantras", Icons.Default.SelfImprovement, onNavigateToMantras) }
            item { CompactGridTile("భజనలు", "Bhajans", Icons.Default.MusicNote, onNavigateToBhajans) }
            item { CompactGridTile("సుప్రభాతం", "Suprabhatam", Icons.Default.WbSunny, onNavigateToSuprabhatam) }
            item { CompactGridTile("అష్టోత్రాలు", "Ashtottara", Icons.Default.AutoAwesome, onNavigateToAshtotra) }
            item { CompactGridTile("జపం", "Japa Counter", Icons.Default.SelfImprovement, onNavigateToJapa) }
            item { CompactGridTile("చాలీసా", "Chalisa", Icons.AutoMirrored.Filled.MenuBook, onNavigateToChalisas) }

            // Banner ad
            item(span = { GridItemSpan(maxLineSpan) }) {
                bannerAd?.invoke()
            }

            // ── Section: Time & Auspicion ────────────────────────────────────
            item(span = { GridItemSpan(maxLineSpan) }) {
                Spacer(Modifier.height(4.dp))
                SectionHeader(titleTelugu = "కాలం & ముహూర్తం", titleEnglish = "Time & Auspicion")
            }
            item { CompactGridTile("శుభ ముహూర్తాలు", "Muhurtam", Icons.Default.EventAvailable, onNavigateToMuhurtam) }
            item { CompactGridTile("చోఘడియా", "Choghadiya", Icons.Default.WatchLater, onNavigateToChoghadiya) }
            item { CompactGridTile("గ్రహ పరివర్తనలు", "Planet Transits", Icons.Default.Star, onNavigateToPlanetTransits) }
            item { CompactGridTile("వ్రతాలు", "Vratas", Icons.Default.Brightness5, onNavigateToVratas) }
            item { CompactGridTile("పవిత్ర మాసాలు", "Sacred Months", Icons.Default.CalendarMonth, onNavigateToSacredMonth) }

            // ── Section: Spiritual Tools ─────────────────────────────────────
            item(span = { GridItemSpan(maxLineSpan) }) {
                Spacer(Modifier.height(4.dp))
                SectionHeader(titleTelugu = "ఆధ్యాత్మిక సాధనాలు", titleEnglish = "Spiritual Tools")
            }
            item { CompactGridTile("రాశిఫలం", "Rashifal", Icons.Default.Stars, onNavigateToRashifal) }
            item { CompactGridTile("పూజా విధానం", "Guided Puja", Icons.Default.Spa, onNavigateToGuidedPuja) }
            item { CompactGridTile("పూజా టైమర్", "Pooja Timer", Icons.Default.Timer, onNavigateToPoojaTimer) }
            item { CompactGridTile("పూజా గది", "Pooja Room", Icons.Default.VolunteerActivism, onNavigateToVirtualPoojaRoom) }
            item { CompactGridTile("పురాణాల క్విజ్", "Puranas Quiz", Icons.Default.Quiz, onNavigateToPuranaQuiz) }

            // ── Section: Jyotish & Family ────────────────────────────────────
            item(span = { GridItemSpan(maxLineSpan) }) {
                Spacer(Modifier.height(4.dp))
                SectionHeader(titleTelugu = "జ్యోతిష్యం & కుటుంబం", titleEnglish = "Jyotish & Family")
            }
            item { CompactGridTile("జాతక చక్రం", "Birth Chart", Icons.Default.AutoAwesome, onNavigateToJatakaChakram) }
            item { CompactGridTile("గుణ మిలనం", "Compatibility", Icons.Default.Favorite, onNavigateToGunaMilan) }
            item { CompactGridTile("కుటుంబ ప్రొఫైల్స్", "Family Profiles", Icons.Default.People, onNavigateToSavedProfiles) }
            item { CompactGridTile("పర్వదినాలు", "Family Days", Icons.Default.Celebration, onNavigateToFamilyDays) }
            item { CompactGridTile("పురస్కారాలు", "Badges", Icons.Default.EmojiEvents, onNavigateToBadges) }

            // ── Section: Places ──────────────────────────────────────────────
            item(span = { GridItemSpan(maxLineSpan) }) {
                Spacer(Modifier.height(4.dp))
                SectionHeader(titleTelugu = "ప్రదేశాలు & పండుగలు", titleEnglish = "Places & Festivals")
            }
            item(span = { GridItemSpan(maxLineSpan) }) {
                MoreMenuRow("దేవాలయాలు", "Temples", Icons.Default.TempleHindu, onNavigateToTemples)
            }
            item(span = { GridItemSpan(maxLineSpan) }) {
                MoreMenuRow("పండుగలు", "Festivals", Icons.Default.Celebration, onNavigateToFestivals)
            }

            // ── App ──────────────────────────────────────────────────────────
            item(span = { GridItemSpan(maxLineSpan) }) {
                Spacer(Modifier.height(4.dp))
                HorizontalDivider(thickness = 0.5.dp)
                Spacer(Modifier.height(4.dp))
            }
            item(span = { GridItemSpan(maxLineSpan) }) {
                MoreMenuRow("యాప్ విశేషాలు", "App Features", Icons.Default.AutoAwesome, onNavigateToFeatures, accentColor = TempleGold)
            }
            item(span = { GridItemSpan(maxLineSpan) }) {
                MoreMenuRow("సెట్టింగ్‌లు", "Settings", Icons.Default.Settings, onNavigateToSettings)
            }

            item(span = { GridItemSpan(maxLineSpan) }) {
                Spacer(Modifier.height(80.dp))
            }
        }
        } // ScaledContent
    }
}

// ── Compact 2-column tile ────────────────────────────────────────────────────

@Composable
private fun CompactGridTile(
    titleTelugu: String,
    titleEnglish: String,
    icon: ImageVector,
    onClick: () -> Unit,
) {
    Card(
        modifier = Modifier.fillMaxWidth().clickable(onClick = onClick),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant),
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(vertical = 16.dp, horizontal = 10.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(6.dp),
        ) {
            Icon(icon, null, tint = TempleGold, modifier = Modifier.size(28.dp))
            Text(
                titleTelugu,
                style = MaterialTheme.typography.bodyMedium,
                fontWeight = FontWeight.Medium,
                textAlign = TextAlign.Center,
                maxLines = 2,
            )
            Text(
                titleEnglish,
                style = MaterialTheme.typography.labelSmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                textAlign = TextAlign.Center,
                maxLines = 1,
            )
        }
    }
}

// ── Full-width row (Places, App section) ─────────────────────────────────────

@Composable
private fun MoreMenuRow(
    titleTelugu: String,
    titleEnglish: String,
    icon: ImageVector,
    onClick: () -> Unit,
    accentColor: androidx.compose.ui.graphics.Color = MaterialTheme.colorScheme.onSurfaceVariant,
) {
    Card(
        modifier = Modifier.fillMaxWidth().clickable(onClick = onClick),
        shape = RoundedCornerShape(12.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant),
    ) {
        Row(
            modifier = Modifier.padding(horizontal = 16.dp, vertical = 14.dp),
            verticalAlignment = Alignment.CenterVertically,
        ) {
            Icon(icon, titleEnglish, tint = TempleGold)
            Spacer(Modifier.width(14.dp))
            Column(modifier = Modifier.weight(1f)) {
                Text(titleTelugu, style = MaterialTheme.typography.titleSmall, fontWeight = FontWeight.Medium)
                Text(titleEnglish, style = MaterialTheme.typography.bodySmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
            }
            Icon(Icons.Default.ChevronRight, "Go", tint = accentColor)
        }
    }
}
