package com.nityapooja.shared.ui.temple

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.LocationOn
import androidx.compose.material.icons.filled.Videocam
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
import com.nityapooja.shared.data.local.entity.TempleEntity
import com.nityapooja.shared.ui.components.EmptyState
import com.nityapooja.shared.ui.components.GlassmorphicCard
import com.nityapooja.shared.ui.components.ShimmerLoading
import com.nityapooja.shared.ui.theme.AuspiciousGreen

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun TempleListScreen(
    viewModel: TempleViewModel = koinViewModel(),
    onTempleClick: (Int) -> Unit,
    onBack: () -> Unit,
    bannerAd: (@Composable () -> Unit)? = null,
) {
    val temples by viewModel.allTemples.collectAsState()

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Column {
                        Text(
                            "దేవాలయాలు",
                            style = MaterialTheme.typography.titleLarge,
                            fontWeight = FontWeight.Bold,
                        )
                        Text(
                            "Temples",
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
            temples.isEmpty() -> {
                // Show shimmer briefly then empty state
                // Since Flow starts with emptyList(), show shimmer initially
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(padding)
                        .padding(16.dp),
                ) {
                    EmptyState(
                        icon = Icons.Default.LocationOn,
                        titleTelugu = "దేవాలయాలు లేవు",
                        titleEnglish = "No temples found",
                    )
                }
            }
            else -> {
                LazyColumn(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(padding),
                    contentPadding = PaddingValues(16.dp),
                    verticalArrangement = Arrangement.spacedBy(12.dp),
                ) {
                    itemsIndexed(temples, key = { _, it -> it.id }) { index, temple ->
                        if (index == 3) {
                            bannerAd?.invoke()
                        }
                        TempleListItem(
                            temple = temple,
                            onClick = { onTempleClick(temple.id) },
                        )
                    }
                }
            }
        }
    }
}

@Composable
private fun TempleListItem(
    temple: TempleEntity,
    onClick: () -> Unit,
) {
    GlassmorphicCard(
        onClick = onClick,
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.Top,
        ) {
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    temple.nameTelugu,
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis,
                )
                Text(
                    temple.name,
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis,
                )

                Spacer(Modifier.height(8.dp))

                // Location and State
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(4.dp),
                ) {
                    Icon(
                        Icons.Default.LocationOn,
                        contentDescription = null,
                        modifier = Modifier.size(14.dp),
                        tint = MaterialTheme.colorScheme.onSurfaceVariant,
                    )
                    val locationText = listOfNotNull(
                        temple.location,
                        temple.state,
                    ).joinToString(", ")
                    Text(
                        locationText,
                        style = MaterialTheme.typography.labelSmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis,
                    )
                }
            }

            // Live Darshan badge
            if (temple.hasLiveDarshan) {
                Spacer(Modifier.width(8.dp))
                Surface(
                    shape = RoundedCornerShape(8.dp),
                    color = AuspiciousGreen.copy(alpha = 0.15f),
                ) {
                    Row(
                        modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(4.dp),
                    ) {
                        Icon(
                            Icons.Default.Videocam,
                            contentDescription = null,
                            modifier = Modifier.size(12.dp),
                            tint = AuspiciousGreen,
                        )
                        Text(
                            "Live Darshan",
                            style = MaterialTheme.typography.labelSmall,
                            color = AuspiciousGreen,
                            fontWeight = FontWeight.Medium,
                        )
                    }
                }
            }
        }
    }
}
