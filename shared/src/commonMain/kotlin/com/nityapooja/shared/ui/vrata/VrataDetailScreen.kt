package com.nityapooja.shared.ui.vrata

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.nityapooja.shared.ui.components.GlassmorphicCard
import com.nityapooja.shared.ui.components.SectionHeader
import com.nityapooja.shared.ui.theme.TempleGold
import org.koin.compose.viewmodel.koinViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun VrataDetailScreen(
    vrataId: Int,
    viewModel: VrataViewModel = koinViewModel(),
    onBack: () -> Unit = {},
    bannerAd: (@Composable () -> Unit)? = null,
) {
    val allVratas by viewModel.allVratas.collectAsState()
    val vrata = allVratas.firstOrNull { it.id == vrataId }

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Column {
                        Text(vrata?.nameTelugu ?: "వ్రతం", style = MaterialTheme.typography.titleLarge, fontWeight = FontWeight.Bold)
                        Text(vrata?.name ?: "Vratam", style = MaterialTheme.typography.labelSmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
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
        if (vrata == null) {
            Box(Modifier.fillMaxSize().padding(padding))
            return@Scaffold
        }

        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .verticalScroll(rememberScrollState())
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp),
        ) {
            // Description
            vrata.descriptionTelugu?.let {
                GlassmorphicCard(accentColor = TempleGold) {
                    Text("వ్రత వివరాలు", style = MaterialTheme.typography.labelMedium, fontWeight = FontWeight.Bold, color = TempleGold)
                    Spacer(Modifier.height(8.dp))
                    Text(it, style = MaterialTheme.typography.bodyMedium, color = MaterialTheme.colorScheme.onSurface)
                    vrata.description?.let { eng ->
                        Spacer(Modifier.height(8.dp))
                        Text(eng, style = MaterialTheme.typography.bodySmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
                    }
                }
            }

            // Associated Deity
            vrata.associatedDeityTelugu?.let {
                GlassmorphicCard {
                    SectionHeader(titleTelugu = "దేవత", titleEnglish = "Deity")
                    Spacer(Modifier.height(8.dp))
                    Text(it, style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold, color = TempleGold)
                    vrata.associatedDeity?.let { eng ->
                        Text(eng, style = MaterialTheme.typography.bodySmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
                    }
                }
            }

            // Fasting Rules
            vrata.fastingRulesTelugu?.let {
                GlassmorphicCard {
                    SectionHeader(titleTelugu = "ఉపవాస నియమాలు", titleEnglish = "Fasting Rules")
                    Spacer(Modifier.height(8.dp))
                    Text(it, style = MaterialTheme.typography.bodyMedium, color = MaterialTheme.colorScheme.onSurface)
                    vrata.fastingRules?.let { eng ->
                        Spacer(Modifier.height(8.dp))
                        Text(eng, style = MaterialTheme.typography.bodySmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
                    }
                }
            }

            // Special Foods
            vrata.specialFoodsTelugu?.let {
                GlassmorphicCard {
                    SectionHeader(titleTelugu = "ప్రత్యేక ఆహారం", titleEnglish = "Special Foods")
                    Spacer(Modifier.height(8.dp))
                    Text(it, style = MaterialTheme.typography.bodyMedium, color = MaterialTheme.colorScheme.onSurface)
                }
            }

            // Mantras
            vrata.mantrasTelugu?.let {
                GlassmorphicCard(accentColor = TempleGold) {
                    SectionHeader(titleTelugu = "మంత్రం", titleEnglish = "Mantra")
                    Spacer(Modifier.height(8.dp))
                    Text(it, style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold, color = TempleGold)
                }
            }

            bannerAd?.invoke()

            Spacer(Modifier.height(24.dp))
        }
    }
}
