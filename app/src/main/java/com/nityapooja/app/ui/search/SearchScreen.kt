package com.nityapooja.app.ui.search

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.icons.filled.SearchOff
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.focus.focusRequester
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.nityapooja.app.ui.components.EmptyState
import com.nityapooja.app.ui.components.GlassmorphicCard
import com.nityapooja.app.ui.theme.TempleGold

private data class FilterOption(
    val key: String,
    val labelTelugu: String,
    val labelEnglish: String,
)

private val filterOptions = listOf(
    FilterOption("all", "అన్నీ", "All"),
    FilterOption("aarti", "హారతి", "Aarti"),
    FilterOption("stotram", "స్తోత్రం", "Stotram"),
    FilterOption("keertana", "కీర్తన", "Keertana"),
    FilterOption("mantra", "మంత్రం", "Mantra"),
    FilterOption("bhajan", "భజన", "Bhajan"),
    FilterOption("chalisa", "చాలీసా", "Chalisa"),
    FilterOption("temple", "దేవాలయం", "Temple"),
)

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SearchScreen(
    viewModel: SearchViewModel = hiltViewModel(),
    onBackClick: () -> Unit = {},
    onAartiClick: (Int) -> Unit = {},
    onStotramClick: (Int) -> Unit = {},
    onKeertanaClick: (Int) -> Unit = {},
    onTempleClick: (Int) -> Unit = {},
    onMantraClick: (Int) -> Unit = {},
    onBhajanClick: (Int) -> Unit = {},
    onChalisaClick: (Int) -> Unit = {},
) {
    val query by viewModel.query.collectAsStateWithLifecycle()
    val selectedFilter by viewModel.selectedFilter.collectAsStateWithLifecycle()
    val results by viewModel.searchResults.collectAsStateWithLifecycle()
    val focusRequester = remember { FocusRequester() }

    LaunchedEffect(Unit) {
        focusRequester.requestFocus()
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Column {
                        Text(
                            "శోధన",
                            style = MaterialTheme.typography.titleLarge,
                            fontWeight = FontWeight.Bold,
                        )
                        Text(
                            "Search",
                            style = MaterialTheme.typography.labelSmall,
                            color = MaterialTheme.colorScheme.onSurfaceVariant,
                        )
                    }
                },
                navigationIcon = {
                    IconButton(onClick = onBackClick) {
                        Icon(
                            imageVector = Icons.AutoMirrored.Filled.ArrowBack,
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
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding),
        ) {
            // Search TextField
            OutlinedTextField(
                value = query,
                onValueChange = viewModel::updateQuery,
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp, vertical = 8.dp)
                    .focusRequester(focusRequester),
                placeholder = {
                    Text("హారతి, స్తోత్రం, మంత్రం, భజన వెతకండి...")
                },
                leadingIcon = {
                    Icon(
                        imageVector = Icons.Default.Search,
                        contentDescription = null,
                        tint = TempleGold,
                    )
                },
                singleLine = true,
                shape = RoundedCornerShape(16.dp),
                colors = OutlinedTextFieldDefaults.colors(
                    focusedBorderColor = TempleGold,
                    cursorColor = TempleGold,
                ),
            )

            // Filter chips
            LazyRow(
                modifier = Modifier.fillMaxWidth(),
                contentPadding = PaddingValues(horizontal = 16.dp),
                horizontalArrangement = Arrangement.spacedBy(8.dp),
            ) {
                items(filterOptions) { option ->
                    FilterChip(
                        selected = selectedFilter == option.key,
                        onClick = { viewModel.updateFilter(option.key) },
                        label = {
                            Text(
                                text = "${option.labelTelugu} ${option.labelEnglish}",
                                style = MaterialTheme.typography.labelMedium,
                            )
                        },
                        colors = FilterChipDefaults.filterChipColors(
                            selectedContainerColor = TempleGold.copy(alpha = 0.15f),
                            selectedLabelColor = TempleGold,
                        ),
                        border = FilterChipDefaults.filterChipBorder(
                            borderColor = MaterialTheme.colorScheme.outlineVariant,
                            selectedBorderColor = TempleGold,
                            enabled = true,
                            selected = selectedFilter == option.key,
                        ),
                    )
                }
            }

            Spacer(Modifier.height(8.dp))

            // Results or Empty State
            if (query.length >= 2 && results.isEmpty()) {
                EmptyState(
                    icon = Icons.Default.SearchOff,
                    titleTelugu = "ఫలితాలు లేవు",
                    titleEnglish = "No results found",
                    modifier = Modifier.fillMaxSize(),
                )
            } else {
                LazyColumn(
                    modifier = Modifier.fillMaxSize(),
                    contentPadding = PaddingValues(horizontal = 16.dp, vertical = 8.dp),
                    verticalArrangement = Arrangement.spacedBy(12.dp),
                ) {
                    items(results, key = { "${it.type}_${it.id}" }) { result ->
                        SearchResultCard(
                            result = result,
                            onClick = {
                                when (result.type) {
                                    "aarti" -> onAartiClick(result.id)
                                    "stotram" -> onStotramClick(result.id)
                                    "keertana" -> onKeertanaClick(result.id)
                                    "mantra" -> onMantraClick(result.id)
                                    "bhajan" -> onBhajanClick(result.id)
                                    "chalisa" -> onChalisaClick(result.id)
                                    "temple" -> onTempleClick(result.id)
                                }
                            },
                        )
                    }
                }
            }
        }
    }
}

@Composable
private fun SearchResultCard(
    result: SearchResult,
    onClick: () -> Unit,
) {
    GlassmorphicCard(
        onClick = onClick,
        contentPadding = 16.dp,
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
        ) {
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = result.titleTelugu,
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.onSurface,
                )
                Spacer(Modifier.height(4.dp))
                Text(
                    text = result.titleEnglish,
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                )
            }
            Spacer(Modifier.width(8.dp))
            TypeBadge(type = result.type)
        }
    }
}

@Composable
private fun TypeBadge(type: String) {
    val label = when (type) {
        "aarti" -> "హారతి"
        "stotram" -> "స్తోత్రం"
        "keertana" -> "కీర్తన"
        "mantra" -> "మంత్రం"
        "bhajan" -> "భజన"
        "chalisa" -> "చాలీసా"
        "temple" -> "దేవాలయం"
        else -> type
    }
    Surface(
        shape = RoundedCornerShape(8.dp),
        color = TempleGold.copy(alpha = 0.12f),
    ) {
        Text(
            text = label,
            modifier = Modifier.padding(horizontal = 10.dp, vertical = 4.dp),
            style = MaterialTheme.typography.labelSmall,
            fontWeight = FontWeight.Medium,
            color = TempleGold,
        )
    }
}
