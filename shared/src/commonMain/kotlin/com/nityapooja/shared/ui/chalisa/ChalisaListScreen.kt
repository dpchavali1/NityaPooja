package com.nityapooja.shared.ui.chalisa

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.MenuBook
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import org.koin.compose.viewmodel.koinViewModel
import com.nityapooja.shared.data.local.entity.ChalisaEntity
import com.nityapooja.shared.ui.components.EmptyState
import com.nityapooja.shared.ui.components.GlassmorphicCard
import com.nityapooja.shared.ui.theme.TempleGold

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ChalisaListScreen(
    viewModel: ChalisaViewModel = koinViewModel(),
    onChalisaClick: (Int) -> Unit,
    onBack: () -> Unit,
    bannerAd: (@Composable () -> Unit)? = null,
) {
    val chalisas by viewModel.chalisas.collectAsState()

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Column {
                        Text(
                            "చాలీసాలు",
                            style = MaterialTheme.typography.titleLarge,
                            fontWeight = FontWeight.Bold,
                        )
                        Text(
                            "Chalisas",
                            style = MaterialTheme.typography.labelSmall,
                            color = MaterialTheme.colorScheme.onSurfaceVariant,
                        )
                    }
                },
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Icon(
                            Icons.AutoMirrored.Filled.ArrowBack,
                            contentDescription = "Back",
                        )
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.surface,
                ),
            )
        },
    ) { padding ->
        when {
            chalisas.isEmpty() -> {
                EmptyState(
                    icon = Icons.Default.MenuBook,
                    titleTelugu = "చాలీసాలు లేవు",
                    titleEnglish = "No chalisas available yet",
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(padding),
                )
            }
            else -> {
                LazyColumn(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(padding),
                    contentPadding = PaddingValues(horizontal = 16.dp, vertical = 8.dp),
                    verticalArrangement = Arrangement.spacedBy(12.dp),
                ) {
                    itemsIndexed(chalisas, key = { _, it -> it.id }) { index, chalisa ->
                        if (index == 3) {
                            bannerAd?.invoke()
                        }
                        ChalisaListItem(
                            chalisa = chalisa,
                            onClick = { onChalisaClick(chalisa.id) },
                        )
                    }
                }
            }
        }
    }
}

@Composable
private fun ChalisaListItem(
    chalisa: ChalisaEntity,
    onClick: () -> Unit,
) {
    GlassmorphicCard(
        onClick = onClick,
        accentColor = TempleGold,
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
        ) {
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    chalisa.titleTelugu,
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis,
                )
                Spacer(Modifier.height(2.dp))
                Text(
                    chalisa.title,
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis,
                )
            }

            Spacer(Modifier.width(12.dp))

            if (chalisa.verseCount > 0) {
                Surface(
                    shape = RoundedCornerShape(8.dp),
                    color = MaterialTheme.colorScheme.primaryContainer,
                ) {
                    Text(
                        "${chalisa.verseCount} verses",
                        modifier = Modifier.padding(horizontal = 10.dp, vertical = 4.dp),
                        style = MaterialTheme.typography.labelSmall,
                        color = MaterialTheme.colorScheme.primary,
                        fontWeight = FontWeight.Medium,
                    )
                }
            }
        }

        if (chalisa.duration > 0) {
            Spacer(Modifier.height(8.dp))
            Text(
                formatChalisaDuration(chalisa.duration),
                style = MaterialTheme.typography.labelSmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
            )
        }
    }
}

private fun formatChalisaDuration(seconds: Int): String {
    val minutes = seconds / 60
    val secs = seconds % 60
    return if (minutes > 0) "${minutes}m ${secs}s" else "${secs}s"
}
