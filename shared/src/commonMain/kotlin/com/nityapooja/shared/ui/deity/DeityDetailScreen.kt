package com.nityapooja.shared.ui.deity

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import org.koin.compose.viewmodel.koinViewModel
import com.nityapooja.shared.ui.components.*
import com.nityapooja.shared.ui.theme.*

private val ALL_TABS = listOf(
    "సుప్రభాతం\nSuprabhatam",
    "అష్టోత్తరం\nAshtotharam",
    "స్తోత్రం\nStotram",
    "మంత్రం\nMantra",
    "చాలీసా\nChalisa",
    "కీర్తన\nKeertana",
    "హారతి\nAarti",
)

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DeityDetailScreen(
    deityId: Int,
    onBack: () -> Unit,
    onAartiClick: (Int) -> Unit = {},
    onStotramClick: (Int) -> Unit = {},
    onMantraClick: (Int) -> Unit = {},
    onKeertanaClick: (Int) -> Unit = {},
    onChalisaClick: (Int) -> Unit = {},
    onSuprabhatamClick: (Int) -> Unit = {},
    onAshtotraClick: (Int) -> Unit = {},
    viewModel: DeityViewModel = koinViewModel(),
    fontSizeViewModel: FontSizeViewModel = koinViewModel(),
) {
    val deity by remember(deityId) { viewModel.getDeityById(deityId) }
        .collectAsState(initial = null)

    val aartis by remember(deityId) { viewModel.getAartisByDeity(deityId) }
        .collectAsState(initial = emptyList())
    val stotrams by remember(deityId) { viewModel.getStotramsByDeity(deityId) }
        .collectAsState(initial = emptyList())
    val mantras by remember(deityId) { viewModel.getMantrasByDeity(deityId) }
        .collectAsState(initial = emptyList())
    val keertanalu by remember(deityId) { viewModel.getKeertanaluByDeity(deityId) }
        .collectAsState(initial = emptyList())
    val chalisas by remember(deityId) { viewModel.getChalisasByDeity(deityId) }
        .collectAsState(initial = emptyList())
    val suprabhatams by remember(deityId) { viewModel.getSuprabhatamByDeity(deityId) }
        .collectAsState(initial = emptyList())
    val ashtotras by remember(deityId) { viewModel.getAshtotraByDeity(deityId) }
        .collectAsState(initial = emptyList())

    val fontSize by fontSizeViewModel.fontSize.collectAsState()
    val fontScale = fontSize / 16f

    var selectedTab by rememberSaveable { mutableIntStateOf(0) }

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Column {
                        Text(
                            deity?.nameTelugu ?: "",
                            style = MaterialTheme.typography.titleMedium,
                            fontWeight = FontWeight.Bold,
                        )
                        Text(
                            deity?.name ?: "",
                            style = MaterialTheme.typography.labelSmall,
                            color = MaterialTheme.colorScheme.onSurfaceVariant,
                        )
                    }
                },
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, "Back")
                    }
                },
                actions = {
                    FontSizeControls(
                        fontSize = fontSize,
                        onDecrease = fontSizeViewModel::decrease,
                        onIncrease = fontSizeViewModel::increase,
                    )
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.surface,
                ),
            )
        }
    ) { padding ->
        deity?.let { currentDeity ->
            val deityColor = resolveDeityColor(currentDeity.colorTheme)

            LazyColumn(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(padding),
                contentPadding = PaddingValues(16.dp),
                verticalArrangement = Arrangement.spacedBy(16.dp),
            ) {
                // Deity Header Card
                item {
                    GlassmorphicCard(accentColor = deityColor) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            verticalAlignment = Alignment.CenterVertically,
                        ) {
                            DeityAvatar(
                                nameTelugu = currentDeity.nameTelugu,
                                nameEnglish = currentDeity.name,
                                deityColor = deityColor,
                                size = 80.dp,
                                showLabel = false,
                                imageResName = currentDeity.imageResName,
                            )
                            Spacer(Modifier.width(16.dp))
                            Column {
                                Text(
                                    currentDeity.nameTelugu,
                                    style = NityaPoojaTextStyles.TeluguDisplay,
                                    color = MaterialTheme.colorScheme.onSurface,
                                )
                                Text(
                                    currentDeity.name,
                                    style = MaterialTheme.typography.titleMedium,
                                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                                )
                                currentDeity.dayOfWeek?.let { day ->
                                    Spacer(Modifier.height(4.dp))
                                    AssistChip(
                                        onClick = {},
                                        label = { Text(day, style = MaterialTheme.typography.labelSmall) },
                                        leadingIcon = { Icon(Icons.Default.CalendarMonth, null, Modifier.size(14.dp)) },
                                    )
                                }
                            }
                        }
                        currentDeity.descriptionTelugu?.let {
                            Spacer(Modifier.height(12.dp))
                            Text(
                                it,
                                style = MaterialTheme.typography.bodyMedium.copy(
                                    fontSize = (14 * fontScale).sp,
                                    lineHeight = (22 * fontScale).sp,
                                ),
                                color = MaterialTheme.colorScheme.onSurfaceVariant,
                            )
                        }
                    }
                }

                // Tab Row
                item {
                    ScrollableTabRow(
                        selectedTabIndex = selectedTab,
                        containerColor = MaterialTheme.colorScheme.surface,
                        contentColor = TempleGold,
                        edgePadding = 0.dp,
                    ) {
                        ALL_TABS.forEachIndexed { index, title ->
                            Tab(
                                selected = selectedTab == index,
                                onClick = { selectedTab = index },
                                text = {
                                    Text(
                                        title,
                                        style = MaterialTheme.typography.labelSmall,
                                        fontWeight = if (selectedTab == index) FontWeight.Bold else FontWeight.Normal,
                                    )
                                },
                            )
                        }
                    }
                }

                // Tab Content
                when (selectedTab) {
                    0 -> {
                        if (suprabhatams.isEmpty()) {
                            item { EmptyContentMessage("సుప్రభాతం లేదు", "No suprabhatam available for this deity") }
                        }
                        items(suprabhatams) { suprabhatam ->
                            ContentListCard(
                                titleTelugu = suprabhatam.titleTelugu ?: suprabhatam.title,
                                titleEnglish = suprabhatam.title,
                                onClick = { onSuprabhatamClick(suprabhatam.id) },
                            )
                        }
                    }
                    1 -> {
                        if (ashtotras.isEmpty()) {
                            item { EmptyContentMessage("అష్టోత్తరం లేదు", "No ashtotharam available for this deity") }
                        }
                        items(ashtotras) { ashtotra ->
                            ContentListCard(
                                titleTelugu = ashtotra.titleTelugu ?: ashtotra.title,
                                titleEnglish = ashtotra.title,
                                onClick = { onAshtotraClick(ashtotra.id) },
                            )
                        }
                    }
                    2 -> {
                        if (stotrams.isEmpty()) {
                            item { EmptyContentMessage("స్తోత్రాలు లేవు", "No stotrams available") }
                        }
                        items(stotrams) { stotram ->
                            ContentListCard(
                                titleTelugu = stotram.titleTelugu ?: stotram.title,
                                titleEnglish = stotram.title,
                                onClick = { onStotramClick(stotram.id) },
                            )
                        }
                    }
                    3 -> {
                        if (mantras.isEmpty()) {
                            item { EmptyContentMessage("మంత్రాలు లేవు", "No mantras available") }
                        }
                        items(mantras) { mantra ->
                            ContentListCard(
                                titleTelugu = mantra.titleTelugu ?: mantra.title,
                                titleEnglish = mantra.title,
                                onClick = { onMantraClick(mantra.id) },
                            )
                        }
                    }
                    4 -> {
                        if (chalisas.isEmpty()) {
                            item { EmptyContentMessage("చాలీసా లేదు", "No chalisa available for this deity") }
                        }
                        items(chalisas) { chalisa ->
                            ContentListCard(
                                titleTelugu = chalisa.titleTelugu ?: chalisa.title,
                                titleEnglish = chalisa.title,
                                onClick = { onChalisaClick(chalisa.id) },
                            )
                        }
                    }
                    5 -> {
                        if (keertanalu.isEmpty()) {
                            item { EmptyContentMessage("కీర్తనలు లేవు", "No keertanalu available") }
                        }
                        items(keertanalu) { keertana ->
                            ContentListCard(
                                titleTelugu = keertana.titleTelugu ?: keertana.title,
                                titleEnglish = keertana.title,
                                subtitle = keertana.composerTelugu ?: keertana.composer,
                                onClick = { onKeertanaClick(keertana.id) },
                            )
                        }
                    }
                    6 -> {
                        if (aartis.isEmpty()) {
                            item { EmptyContentMessage("హారతులు లేవు", "No aartis available") }
                        }
                        items(aartis) { aarti ->
                            ContentListCard(
                                titleTelugu = aarti.titleTelugu ?: aarti.title,
                                titleEnglish = aarti.title,
                                onClick = { onAartiClick(aarti.id) },
                            )
                        }
                    }
                }

                item { Spacer(Modifier.height(60.dp)) }
            }
        } ?: Box(
            modifier = Modifier.fillMaxSize().padding(padding),
            contentAlignment = Alignment.Center,
        ) {
            CircularProgressIndicator(color = TempleGold)
        }
    }
}

@Composable
private fun ContentListCard(
    titleTelugu: String,
    titleEnglish: String,
    subtitle: String? = null,
    onClick: () -> Unit,
) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        onClick = onClick,
        shape = RoundedCornerShape(12.dp),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surfaceVariant,
        ),
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Text(
                titleTelugu,
                style = MaterialTheme.typography.titleSmall,
                fontWeight = FontWeight.Medium,
            )
            Text(
                titleEnglish,
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
            )
            subtitle?.let {
                Text(
                    it,
                    style = MaterialTheme.typography.labelSmall,
                    color = TempleGold,
                )
            }
        }
    }
}

@Composable
private fun EmptyContentMessage(titleTelugu: String, titleEnglish: String) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(32.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
    ) {
        Text(titleTelugu, style = MaterialTheme.typography.bodyMedium, color = MaterialTheme.colorScheme.onSurfaceVariant)
        Text(titleEnglish, style = MaterialTheme.typography.bodySmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
    }
}
