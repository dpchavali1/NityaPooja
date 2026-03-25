package com.nityapooja.shared.ui.vrata

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.nityapooja.shared.data.local.entity.VrataEntity
import com.nityapooja.shared.ui.components.GlassmorphicCard
import com.nityapooja.shared.ui.components.SectionHeader
import com.nityapooja.shared.ui.panchangam.PanchangamViewModel
import com.nityapooja.shared.ui.theme.AuspiciousGreen
import com.nityapooja.shared.ui.theme.TempleGold
import org.koin.compose.viewmodel.koinViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun VrataListScreen(
    viewModel: VrataViewModel = koinViewModel(),
    panchangamViewModel: PanchangamViewModel = koinViewModel(),
    onNavigateToDetail: (Int) -> Unit = {},
    onBack: () -> Unit = {},
    bannerAd: (@Composable () -> Unit)? = null,
) {
    val allVratas by viewModel.allVratas.collectAsState()
    val upcomingVratas by viewModel.upcomingVratas.collectAsState()
    val isLoading by viewModel.isLoading.collectAsState()
    val locationInfo by panchangamViewModel.locationInfo.collectAsState()
    var selectedTab by remember { mutableIntStateOf(0) }

    LaunchedEffect(locationInfo, allVratas) {
        if (allVratas.isNotEmpty()) {
            viewModel.calculateUpcoming(locationInfo.lat, locationInfo.lng, locationInfo.timezone)
        }
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Column {
                        Text("వ్రతాలు", style = MaterialTheme.typography.titleLarge, fontWeight = FontWeight.Bold)
                        Text("Vratas & Observances", style = MaterialTheme.typography.labelSmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
                    }
                },
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, "Back")
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(containerColor = Color.Transparent),
            )
        },
    ) { padding ->
        Column(modifier = Modifier.fillMaxSize().padding(padding)) {
            TabRow(
                selectedTabIndex = selectedTab,
                containerColor = Color.Transparent,
                contentColor = TempleGold,
            ) {
                Tab(selected = selectedTab == 0, onClick = { selectedTab = 0 }) {
                    Text("రాబోయే వ్రతాలు", modifier = Modifier.padding(12.dp), style = MaterialTheme.typography.labelMedium, fontWeight = FontWeight.Bold)
                }
                Tab(selected = selectedTab == 1, onClick = { selectedTab = 1 }) {
                    Text("అన్ని వ్రతాలు", modifier = Modifier.padding(12.dp), style = MaterialTheme.typography.labelMedium, fontWeight = FontWeight.Bold)
                }
            }

            bannerAd?.invoke()

            if (isLoading) {
                Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                    CircularProgressIndicator(color = TempleGold)
                }
            } else {
                when (selectedTab) {
                    0 -> UpcomingVratasList(upcomingVratas, onNavigateToDetail)
                    1 -> AllVratasList(allVratas, onNavigateToDetail)
                }
            }
        }
    }
}

@Composable
private fun UpcomingVratasList(vratas: List<UpcomingVrata>, onNavigateToDetail: (Int) -> Unit) {
    LazyColumn(
        contentPadding = PaddingValues(16.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp),
    ) {
        if (vratas.isEmpty()) {
            item {
                Box(Modifier.fillMaxWidth().padding(32.dp), contentAlignment = Alignment.Center) {
                    Text("వ్రతాలు లోడ్ అవుతున్నాయి...", color = MaterialTheme.colorScheme.onSurfaceVariant)
                }
            }
        }
        items(vratas) { upcoming ->
            GlassmorphicCard(
                accentColor = TempleGold,
                cornerRadius = 16.dp,
                contentPadding = 14.dp,
                onClick = { onNavigateToDetail(upcoming.vrata.id) },
            ) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically,
                ) {
                    Column(modifier = Modifier.weight(1f)) {
                        Text(
                            upcoming.vrata.nameTelugu,
                            style = MaterialTheme.typography.titleMedium,
                            fontWeight = FontWeight.Bold,
                            color = MaterialTheme.colorScheme.onSurface,
                        )
                        Text(
                            upcoming.vrata.name,
                            style = MaterialTheme.typography.bodySmall,
                            color = MaterialTheme.colorScheme.onSurfaceVariant,
                        )
                        Spacer(Modifier.height(4.dp))
                        Text(
                            "${upcoming.dateDisplay} · ${upcoming.teluguDay}",
                            style = MaterialTheme.typography.bodySmall,
                            color = MaterialTheme.colorScheme.onSurface,
                        )
                        upcoming.vrata.associatedDeityTelugu?.let {
                            Text(it, style = MaterialTheme.typography.labelSmall, color = TempleGold)
                        }
                    }
                    Surface(
                        shape = MaterialTheme.shapes.small,
                        color = if (upcoming.daysUntil == 0) AuspiciousGreen.copy(alpha = 0.15f)
                               else TempleGold.copy(alpha = 0.12f),
                    ) {
                        Text(
                            if (upcoming.daysUntil == 0) "నేడు" else "${upcoming.daysUntil} రోజులు",
                            modifier = Modifier.padding(horizontal = 10.dp, vertical = 6.dp),
                            style = MaterialTheme.typography.labelMedium,
                            fontWeight = FontWeight.Bold,
                            color = if (upcoming.daysUntil == 0) AuspiciousGreen else TempleGold,
                        )
                    }
                }
            }
        }
    }
}

@Composable
private fun AllVratasList(vratas: List<VrataEntity>, onNavigateToDetail: (Int) -> Unit) {
    val tithiBased = vratas.filter { it.category == "tithi_based" }
    val vaaramBased = vratas.filter { it.category == "vaaram_based" }

    LazyColumn(
        contentPadding = PaddingValues(16.dp),
        verticalArrangement = Arrangement.spacedBy(8.dp),
    ) {
        if (tithiBased.isNotEmpty()) {
            item { SectionHeader(titleTelugu = "తిథి ఆధారిత వ్రతాలు", titleEnglish = "Tithi-based Vratas") }
            items(tithiBased) { vrata -> VrataListItem(vrata, onNavigateToDetail) }
        }
        if (vaaramBased.isNotEmpty()) {
            item { Spacer(Modifier.height(8.dp)); SectionHeader(titleTelugu = "వార ఆధారిత వ్రతాలు", titleEnglish = "Weekly Vratas") }
            items(vaaramBased) { vrata -> VrataListItem(vrata, onNavigateToDetail) }
        }
    }
}

@Composable
private fun VrataListItem(vrata: VrataEntity, onNavigateToDetail: (Int) -> Unit) {
    Card(
        onClick = { onNavigateToDetail(vrata.id) },
        modifier = Modifier.fillMaxWidth(),
        shape = MaterialTheme.shapes.medium,
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f)),
    ) {
        Row(
            modifier = Modifier.padding(14.dp),
            verticalAlignment = Alignment.CenterVertically,
        ) {
            Column(modifier = Modifier.weight(1f)) {
                Text(vrata.nameTelugu, style = MaterialTheme.typography.titleSmall, fontWeight = FontWeight.Bold)
                Text(vrata.name, style = MaterialTheme.typography.bodySmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
                vrata.associatedDeityTelugu?.let {
                    Text(it, style = MaterialTheme.typography.labelSmall, color = TempleGold)
                }
            }
            Icon(Icons.Default.ChevronRight, contentDescription = null, tint = MaterialTheme.colorScheme.onSurfaceVariant)
        }
    }
}
