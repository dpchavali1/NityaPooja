package com.nityapooja.app.ui.aarti

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.MusicNote
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.nityapooja.app.data.local.entity.AartiEntity
import com.nityapooja.app.data.local.entity.DeityEntity
import com.nityapooja.app.ui.components.DeityAvatar
import com.nityapooja.app.ui.components.EmptyState
import com.nityapooja.app.ui.components.GlassmorphicCard
import com.nityapooja.app.ui.components.resolveDeityColor

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AartiListScreen(
    viewModel: AartiViewModel = hiltViewModel(),
    onAartiClick: (Int) -> Unit = {},
    onBack: () -> Unit = {},
) {
    val aartis by viewModel.allAartis.collectAsStateWithLifecycle()
    val deityMap by viewModel.deityMap.collectAsStateWithLifecycle()

    Scaffold(
        topBar = {
            TopAppBar(
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, "Back")
                    }
                },
                title = {
                    Column {
                        Text(
                            "హారతులు",
                            style = MaterialTheme.typography.titleLarge,
                            fontWeight = FontWeight.Bold,
                        )
                        Text(
                            "Aartulu",
                            style = MaterialTheme.typography.labelSmall,
                            color = MaterialTheme.colorScheme.onSurfaceVariant,
                        )
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.surface,
                ),
            )
        }
    ) { padding ->
        if (aartis.isEmpty()) {
            EmptyState(
                icon = Icons.Filled.MusicNote,
                titleTelugu = "హారతులు లేవు",
                titleEnglish = "No aartis available",
                modifier = Modifier.padding(padding),
            )
        } else {
            LazyVerticalGrid(
                columns = GridCells.Fixed(2),
                modifier = Modifier
                    .fillMaxSize()
                    .padding(padding),
                contentPadding = PaddingValues(16.dp),
                horizontalArrangement = Arrangement.spacedBy(12.dp),
                verticalArrangement = Arrangement.spacedBy(12.dp),
            ) {
                items(aartis, key = { it.id }) { aarti ->
                    val deity = deityMap[aarti.deityId]
                    AartiTile(
                        aarti = aarti,
                        deity = deity,
                        onClick = { onAartiClick(aarti.id) },
                    )
                }
            }
        }
    }
}

@Composable
private fun AartiTile(
    aarti: AartiEntity,
    deity: DeityEntity?,
    onClick: () -> Unit,
) {
    val deityColor = resolveDeityColor(deity?.colorTheme)

    GlassmorphicCard(
        modifier = Modifier.fillMaxWidth(),
        accentColor = deityColor,
        onClick = onClick,
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(12.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
        ) {
            DeityAvatar(
                nameTelugu = deity?.nameTelugu ?: "",
                nameEnglish = deity?.name ?: "",
                deityColor = deityColor,
                size = 72.dp,
                showLabel = false,
                imageResName = deity?.imageResName,
            )
            Spacer(Modifier.height(12.dp))
            Text(
                aarti.titleTelugu,
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Bold,
                textAlign = TextAlign.Center,
                maxLines = 2,
                overflow = TextOverflow.Ellipsis,
            )
            Spacer(Modifier.height(4.dp))
            Text(
                aarti.title,
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                textAlign = TextAlign.Center,
                maxLines = 2,
                overflow = TextOverflow.Ellipsis,
            )
            deity?.let {
                Spacer(Modifier.height(8.dp))
                Text(
                    it.nameTelugu,
                    style = MaterialTheme.typography.labelSmall,
                    color = deityColor,
                    fontWeight = FontWeight.Medium,
                    textAlign = TextAlign.Center,
                )
            }
        }
    }
}
