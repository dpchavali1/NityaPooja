package com.nityapooja.shared.ui.mantra

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.SelfImprovement
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import org.koin.compose.viewmodel.koinViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MantraListScreen(
    viewModel: MantraViewModel = koinViewModel(),
    onMantraClick: (Int) -> Unit = {},
    onJapaClick: () -> Unit = {},
) {
    val mantras by viewModel.allMantras.collectAsState()

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Column {
                        Text("మంత్రాలు", style = MaterialTheme.typography.titleLarge, fontWeight = FontWeight.Bold)
                        Text("Mantras", style = MaterialTheme.typography.labelSmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
                    }
                },
                actions = {
                    TextButton(onClick = onJapaClick) {
                        Icon(Icons.Default.SelfImprovement, "Japa", Modifier.size(20.dp))
                        Spacer(Modifier.width(4.dp))
                        Text("జపం")
                    }
                }
            )
        }
    ) { padding ->
        LazyColumn(
            modifier = Modifier.fillMaxSize().padding(padding),
            contentPadding = PaddingValues(16.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp),
        ) {
            items(mantras, key = { it.id }) { mantra ->
                Card(
                    modifier = Modifier.fillMaxWidth().clickable { onMantraClick(mantra.id) },
                    shape = RoundedCornerShape(12.dp),
                    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant),
                ) {
                    Column(modifier = Modifier.padding(16.dp)) {
                        Text(mantra.titleTelugu, style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold)
                        Text(mantra.title, style = MaterialTheme.typography.bodySmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
                        mantra.sanskrit?.let {
                            Spacer(Modifier.height(8.dp))
                            Text(it, style = MaterialTheme.typography.bodyMedium, color = MaterialTheme.colorScheme.primary, maxLines = 2)
                        }
                        Spacer(Modifier.height(4.dp))
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Text("${mantra.recommendedCount}x", style = MaterialTheme.typography.labelSmall, color = MaterialTheme.colorScheme.secondary)
                            mantra.category?.let {
                                Spacer(Modifier.width(8.dp))
                                Text(it, style = MaterialTheme.typography.labelSmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
                            }
                        }
                    }
                }
            }
        }
    }
}
