package com.nityapooja.shared.ui.family

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.nityapooja.shared.data.local.entity.FamilyDayEntity
import com.nityapooja.shared.data.local.entity.FamilyDayType
import com.nityapooja.shared.ui.components.FontSizeControls
import com.nityapooja.shared.ui.components.FontSizeViewModel
import com.nityapooja.shared.ui.components.ScaledContent
import com.nityapooja.shared.ui.components.GlassmorphicCard
import com.nityapooja.shared.ui.components.SectionHeader
import com.nityapooja.shared.ui.theme.AuspiciousGreen
import com.nityapooja.shared.ui.theme.TempleGold
import org.koin.compose.viewmodel.koinViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun FamilyDaysScreen(
    onBack: () -> Unit = {},
    viewModel: FamilyDayViewModel = koinViewModel(),
    fontSizeViewModel: FontSizeViewModel = koinViewModel(),
    bannerAd: (@Composable () -> Unit)? = null,
) {
    val allDays by viewModel.allDays.collectAsState()
    val upcomingDays by viewModel.upcomingDays.collectAsState()
    val tithiDetails by viewModel.tithiDetails.collectAsState()
    val fontSize by fontSizeViewModel.fontSize.collectAsState()
    val fontScale = fontSize / 16f

    var showAddSheet by remember { mutableStateOf(false) }
    var deleteCandidateId by remember { mutableStateOf<Long?>(null) }

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Column {
                        Text(
                            "కుటుంబ పర్వదినాలు",
                            style = MaterialTheme.typography.titleLarge,
                            fontWeight = FontWeight.Bold,
                        )
                        Text(
                            "Family Days",
                            style = MaterialTheme.typography.labelSmall,
                            color = MaterialTheme.colorScheme.onSurfaceVariant,
                        )
                    }
                },
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Back")
                    }
                },
                actions = {
                    FontSizeControls(
                        fontSize = fontSize,
                        onDecrease = fontSizeViewModel::decrease,
                        onIncrease = fontSizeViewModel::increase,
                    )
                    IconButton(onClick = { showAddSheet = true }) {
                        Icon(Icons.Default.Add, contentDescription = "Add Family Day", tint = TempleGold)
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(containerColor = Color.Transparent),
            )
        },
        floatingActionButton = {
            if (allDays.isNotEmpty()) {
                FloatingActionButton(
                    onClick = { showAddSheet = true },
                    containerColor = TempleGold,
                    contentColor = Color.White,
                ) {
                    Icon(Icons.Default.Add, contentDescription = "Add")
                }
            }
        },
    ) { padding ->
        ScaledContent(fontScale) {
        if (allDays.isEmpty()) {
            EmptyState(
                modifier = Modifier.fillMaxSize().padding(padding),
                onAdd = { showAddSheet = true },
            )
        } else {
            LazyColumn(
                modifier = Modifier.fillMaxSize().padding(padding),
                contentPadding = PaddingValues(16.dp),
                verticalArrangement = Arrangement.spacedBy(12.dp),
            ) {
                item { bannerAd?.invoke() }

                if (upcomingDays.isNotEmpty()) {
                    item {
                        SectionHeader(
                            titleTelugu = "రాబోయే పర్వదినాలు",
                            titleEnglish = "Upcoming (next 90 days)",
                        )
                    }
                    items(upcomingDays, key = { "upcoming_${it.entity.id}" }) { upcoming ->
                        FamilyDayCard(
                            entity = upcoming.entity,
                            daysUntil = upcoming.daysUntil,
                            nextDateDisplay = upcoming.nextDateDisplay,
                            tithiInfo = tithiDetails[upcoming.entity.id],
                            onDeleteRequest = { deleteCandidateId = upcoming.entity.id },
                        )
                    }
                }

                if (allDays.size > upcomingDays.size) {
                    item {
                        Spacer(Modifier.height(4.dp))
                        SectionHeader(
                            titleTelugu = "అన్ని పర్వదినాలు",
                            titleEnglish = "All Family Days",
                        )
                    }
                    val upcomingIds = upcomingDays.map { it.entity.id }.toSet()
                    items(allDays.filter { it.id !in upcomingIds }, key = { "all_${it.id}" }) { entity ->
                        FamilyDayCard(
                            entity = entity,
                            daysUntil = null,
                            nextDateDisplay = null,
                            tithiInfo = tithiDetails[entity.id],
                            onDeleteRequest = { deleteCandidateId = entity.id },
                        )
                    }
                }

                item { Spacer(Modifier.height(80.dp)) }
            }
        }
        } // ScaledContent
    }

    if (showAddSheet) {
        AddFamilyDaySheet(
            onDismiss = { showAddSheet = false },
            onSave = { entity ->
                viewModel.addDay(entity)
                showAddSheet = false
            },
        )
    }

    deleteCandidateId?.let { id ->
        val entity = allDays.find { it.id == id }
        if (entity != null) {
            DeleteConfirmDialog(
                personName = entity.personName,
                onConfirm = {
                    viewModel.deleteDay(id)
                    deleteCandidateId = null
                },
                onDismiss = { deleteCandidateId = null },
            )
        }
    }
}

// ---------------------------------------------------------------------------
// Empty state
// ---------------------------------------------------------------------------

@Composable
private fun EmptyState(modifier: Modifier = Modifier, onAdd: () -> Unit) {
    Box(modifier = modifier, contentAlignment = Alignment.Center) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(12.dp),
            modifier = Modifier.padding(32.dp),
        ) {
            Text("🏠", fontSize = 56.sp)
            Text(
                "కుటుంబ పర్వదినాలు లేవు",
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.onSurface,
            )
            Text(
                "Add your first family day",
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
            )
            Spacer(Modifier.height(8.dp))
            Button(
                onClick = onAdd,
                colors = ButtonDefaults.buttonColors(containerColor = TempleGold),
                shape = RoundedCornerShape(12.dp),
            ) {
                Icon(Icons.Default.Add, contentDescription = null, modifier = Modifier.size(18.dp))
                Spacer(Modifier.width(8.dp))
                Text("పర్వదినం జోడించు")
            }
        }
    }
}

// ---------------------------------------------------------------------------
// Family Day Card
// ---------------------------------------------------------------------------

@Composable
private fun FamilyDayCard(
    entity: FamilyDayEntity,
    daysUntil: Int?,
    nextDateDisplay: String?,
    tithiInfo: TithiInfo?,
    onDeleteRequest: () -> Unit,
) {
    val type = entity.getFamilyDayType()
    GlassmorphicCard(
        accentColor = TempleGold,
        cornerRadius = 16.dp,
        contentPadding = 14.dp,
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.Top,
        ) {
            // Left: emoji + info
            Row(
                horizontalArrangement = Arrangement.spacedBy(12.dp),
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier.weight(1f),
            ) {
                Text(type.emoji, fontSize = 32.sp)
                Column(verticalArrangement = Arrangement.spacedBy(2.dp)) {
                    Text(
                        entity.personName,
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.onSurface,
                    )
                    if (entity.relation.isNotBlank()) {
                        Text(
                            entity.relation,
                            style = MaterialTheme.typography.bodySmall,
                            color = TempleGold,
                        )
                    }
                    Text(
                        type.displayNameTel,
                        style = MaterialTheme.typography.labelSmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                    )
                    if (nextDateDisplay != null) {
                        Spacer(Modifier.height(2.dp))
                        Text(
                            nextDateDisplay,
                            style = MaterialTheme.typography.bodySmall,
                            color = MaterialTheme.colorScheme.onSurface,
                            fontWeight = FontWeight.Medium,
                        )
                    }
                    if (!entity.notes.isNullOrBlank()) {
                        Text(
                            entity.notes,
                            style = MaterialTheme.typography.labelSmall,
                            color = MaterialTheme.colorScheme.onSurfaceVariant,
                        )
                    }
                }
            }

            // Right: days badge + delete
            Column(
                horizontalAlignment = Alignment.End,
                verticalArrangement = Arrangement.spacedBy(6.dp),
            ) {
                if (daysUntil != null) {
                    Surface(
                        shape = RoundedCornerShape(8.dp),
                        color = when {
                            daysUntil == 0 -> AuspiciousGreen.copy(alpha = 0.15f)
                            daysUntil <= 7 -> TempleGold.copy(alpha = 0.20f)
                            else -> TempleGold.copy(alpha = 0.10f)
                        },
                    ) {
                        Text(
                            when (daysUntil) {
                                0 -> "నేడు"
                                1 -> "రేపు"
                                else -> "$daysUntil రోజులు"
                            },
                            modifier = Modifier.padding(horizontal = 10.dp, vertical = 6.dp),
                            style = MaterialTheme.typography.labelMedium,
                            fontWeight = FontWeight.Bold,
                            color = if (daysUntil == 0) AuspiciousGreen else TempleGold,
                        )
                    }
                }
                IconButton(
                    onClick = onDeleteRequest,
                    modifier = Modifier.size(32.dp),
                ) {
                    Icon(
                        Icons.Default.Delete,
                        contentDescription = "Delete",
                        tint = MaterialTheme.colorScheme.onSurfaceVariant,
                        modifier = Modifier.size(18.dp),
                    )
                }
            }
        }

        // Tithi detail block
        if (type == FamilyDayType.TITHI && tithiInfo != null) {
            Spacer(Modifier.height(10.dp))
            HorizontalDivider(thickness = 0.5.dp, color = TempleGold.copy(alpha = 0.25f))
            Spacer(Modifier.height(10.dp))

            // Tithi name + original reference date
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(8.dp),
            ) {
                Text("🪔", fontSize = 16.sp)
                Column {
                    Text(
                        tithiInfo.tithiNameTel,
                        style = MaterialTheme.typography.bodySmall,
                        fontWeight = FontWeight.Bold,
                        color = TempleGold,
                    )
                    Text(
                        "మూల తేదీ: ${tithiInfo.refDateDisplay}",
                        style = MaterialTheme.typography.labelSmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                    )
                }
            }

            Spacer(Modifier.height(8.dp))

            // Explanation why dates shift
            Surface(
                shape = RoundedCornerShape(8.dp),
                color = MaterialTheme.colorScheme.secondaryContainer.copy(alpha = 0.4f),
            ) {
                Text(
                    "🌙 తిథి రోజు చంద్రమాన పంచాంగం ప్రకారం ప్రతి సంవత్సరం మారుతుంది · " +
                    "The tithi day shifts each year per the lunar calendar. " +
                    "Dates below are the Gregorian equivalent for each year.",
                    modifier = Modifier.padding(horizontal = 10.dp, vertical = 7.dp),
                    style = MaterialTheme.typography.labelSmall,
                    color = MaterialTheme.colorScheme.onSecondaryContainer,
                )
            }

            if (tithiInfo.futureDates.isNotEmpty()) {
                Spacer(Modifier.height(8.dp))
                Text(
                    "తదుపరి శ్రాద్ధ తేదీలు · Upcoming Dates",
                    style = MaterialTheme.typography.labelSmall,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                )
                Spacer(Modifier.height(6.dp))
                Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    tithiInfo.futureDates.forEach { fd ->
                        Surface(
                            shape = RoundedCornerShape(10.dp),
                            color = TempleGold.copy(alpha = 0.10f),
                        ) {
                            Column(
                                modifier = Modifier.padding(horizontal = 12.dp, vertical = 8.dp),
                                horizontalAlignment = Alignment.CenterHorizontally,
                            ) {
                                Text(
                                    fd.year.toString(),
                                    style = MaterialTheme.typography.labelSmall,
                                    fontWeight = FontWeight.Bold,
                                    color = TempleGold,
                                )
                                Text(
                                    fd.shortDate,
                                    style = MaterialTheme.typography.bodySmall,
                                    fontWeight = FontWeight.Medium,
                                    color = MaterialTheme.colorScheme.onSurface,
                                )
                            }
                        }
                    }
                }
            }
        }
    }
}

// ---------------------------------------------------------------------------
// Delete confirmation dialog
// ---------------------------------------------------------------------------

@Composable
private fun DeleteConfirmDialog(
    personName: String,
    onConfirm: () -> Unit,
    onDismiss: () -> Unit,
) {
    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text("తొలగించాలా?") },
        text = { Text("\"$personName\" పర్వదినాన్ని తొలగించాలా?\nDelete this family day?") },
        confirmButton = {
            TextButton(onClick = onConfirm) {
                Text("తొలగించు", color = MaterialTheme.colorScheme.error)
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) {
                Text("రద్దు")
            }
        },
    )
}

// ---------------------------------------------------------------------------
// Add Family Day bottom sheet
// ---------------------------------------------------------------------------

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun AddFamilyDaySheet(
    onDismiss: () -> Unit,
    onSave: (FamilyDayEntity) -> Unit,
) {
    var personName by remember { mutableStateOf("") }
    var relation by remember { mutableStateOf("") }
    var selectedType by remember { mutableStateOf(FamilyDayType.BIRTHDAY) }
    var notes by remember { mutableStateOf("") }

    // For BIRTHDAY / ANNIVERSARY / CUSTOM
    var month by remember { mutableStateOf("") }
    var day by remember { mutableStateOf("") }

    // For TITHI
    var tithiRefYear by remember { mutableStateOf("") }
    var tithiRefMonth by remember { mutableStateOf("") }
    var tithiRefDay by remember { mutableStateOf("") }

    var notifyDayBefore by remember { mutableStateOf(true) }
    var notifyOnDay by remember { mutableStateOf(true) }

    var nameError by remember { mutableStateOf(false) }

    val sheetState = rememberModalBottomSheetState(skipPartiallyExpanded = true)

    ModalBottomSheet(
        onDismissRequest = onDismiss,
        sheetState = sheetState,
        dragHandle = { BottomSheetDefaults.DragHandle() },
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .verticalScroll(rememberScrollState())
                .padding(horizontal = 20.dp)
                .padding(bottom = 32.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp),
        ) {
            // Title
            Text(
                "పర్వదినం జోడించు",
                style = MaterialTheme.typography.titleLarge,
                fontWeight = FontWeight.Bold,
                color = TempleGold,
            )
            Text(
                "Add Family Day",
                style = MaterialTheme.typography.labelSmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
            )

            // Person Name
            OutlinedTextField(
                value = personName,
                onValueChange = { personName = it; nameError = false },
                label = { Text("పేరు / Person Name *") },
                isError = nameError,
                supportingText = if (nameError) ({ Text("పేరు అవసరం") }) else null,
                modifier = Modifier.fillMaxWidth(),
                singleLine = true,
            )

            // Relation
            OutlinedTextField(
                value = relation,
                onValueChange = { relation = it },
                label = { Text("సంబంధం / Relation (optional)") },
                placeholder = { Text("అమ్మ, నాన్న, పెళ్ళాం…") },
                modifier = Modifier.fillMaxWidth(),
                singleLine = true,
            )

            // Type selector
            Text(
                "రకం / Type",
                style = MaterialTheme.typography.labelMedium,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.onSurface,
            )
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(8.dp),
            ) {
                FamilyDayType.entries.forEach { type ->
                    FilterChip(
                        selected = selectedType == type,
                        onClick = { selectedType = type },
                        label = {
                            Text(
                                "${type.emoji} ${type.displayNameTel}",
                                style = MaterialTheme.typography.labelSmall,
                            )
                        },
                        colors = FilterChipDefaults.filterChipColors(
                            selectedContainerColor = TempleGold.copy(alpha = 0.20f),
                            selectedLabelColor = TempleGold,
                        ),
                    )
                }
            }

            // Date fields
            if (selectedType != FamilyDayType.TITHI) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(12.dp),
                ) {
                    OutlinedTextField(
                        value = month,
                        onValueChange = { if (it.length <= 2) month = it },
                        label = { Text("Month (1–12)") },
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                        modifier = Modifier.weight(1f),
                        singleLine = true,
                    )
                    OutlinedTextField(
                        value = day,
                        onValueChange = { if (it.length <= 2) day = it },
                        label = { Text("Day (1–31)") },
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                        modifier = Modifier.weight(1f),
                        singleLine = true,
                    )
                }
            } else {
                // TITHI: reference Gregorian date
                Text(
                    "మూల తేదీ నమోదు చేయండి",
                    style = MaterialTheme.typography.labelMedium,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.onSurface,
                )
                Surface(
                    shape = RoundedCornerShape(8.dp),
                    color = MaterialTheme.colorScheme.secondaryContainer.copy(alpha = 0.4f),
                ) {
                    Text(
                        "🗓 మీరు జ్ఞాపకం చేసుకోవాలనుకుంటున్న వ్యక్తి మరణించిన (లేదా శ్రాద్ధం చేసిన) అసలు తేదీ ఇవ్వండి. " +
                        "అప్పటి తిథి లెక్కించి, ముందు సంవత్సరాలలో ఆ తిథి ఏ తేదీకి వస్తుందో చూపిస్తాం.\n" +
                        "Enter the original Gregorian date (year/month/day) of the event. " +
                        "We compute the tithi for that date and show you the equivalent Gregorian date each future year.",
                        modifier = Modifier.padding(horizontal = 10.dp, vertical = 8.dp),
                        style = MaterialTheme.typography.labelSmall,
                        color = MaterialTheme.colorScheme.onSecondaryContainer,
                    )
                }
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(8.dp),
                ) {
                    OutlinedTextField(
                        value = tithiRefYear,
                        onValueChange = { if (it.length <= 4) tithiRefYear = it },
                        label = { Text("Year") },
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                        modifier = Modifier.weight(1.2f),
                        singleLine = true,
                    )
                    OutlinedTextField(
                        value = tithiRefMonth,
                        onValueChange = { if (it.length <= 2) tithiRefMonth = it },
                        label = { Text("Month") },
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                        modifier = Modifier.weight(1f),
                        singleLine = true,
                    )
                    OutlinedTextField(
                        value = tithiRefDay,
                        onValueChange = { if (it.length <= 2) tithiRefDay = it },
                        label = { Text("Day") },
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                        modifier = Modifier.weight(1f),
                        singleLine = true,
                    )
                }

                // Show computed tithi if inputs are valid
                val computedTithiIndex = remember(tithiRefYear, tithiRefMonth, tithiRefDay) {
                    val y = tithiRefYear.toIntOrNull()
                    val m = tithiRefMonth.toIntOrNull()
                    val d = tithiRefDay.toIntOrNull()
                    if (y != null && m != null && d != null && m in 1..12 && d in 1..31)
                        TithiUtils.getTithiIndex(y, m, d)
                    else null
                }
                if (computedTithiIndex != null) {
                    Surface(
                        shape = RoundedCornerShape(8.dp),
                        color = TempleGold.copy(alpha = 0.12f),
                    ) {
                        Column(
                            modifier = Modifier.padding(horizontal = 12.dp, vertical = 10.dp),
                            verticalArrangement = Arrangement.spacedBy(4.dp),
                        ) {
                            Text(
                                "✓ తిథి గుర్తించారు · Tithi Identified",
                                style = MaterialTheme.typography.labelSmall,
                                color = TempleGold,
                            )
                            Text(
                                TithiUtils.fullTithiName(computedTithiIndex),
                                style = MaterialTheme.typography.titleSmall,
                                fontWeight = FontWeight.Bold,
                                color = TempleGold,
                            )
                            Text(
                                "సేవ్ చేసిన తర్వాత, ఈ తిథి ముందు సంవత్సరాలలో ఏ తేదీకి వస్తుందో కార్డ్‌లో చూపిస్తాం. · " +
                                "After saving, the card will show which Gregorian date this tithi falls on each year.",
                                style = MaterialTheme.typography.labelSmall,
                                color = MaterialTheme.colorScheme.onSurfaceVariant,
                            )
                        }
                    }
                }
            }

            // Notes
            OutlinedTextField(
                value = notes,
                onValueChange = { notes = it },
                label = { Text("గమనికలు / Notes (optional)") },
                modifier = Modifier.fillMaxWidth(),
                minLines = 2,
                maxLines = 3,
            )

            // Notification toggles
            HorizontalDivider(thickness = 0.5.dp)

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically,
            ) {
                Column {
                    Text("ముందు రోజు గుర్తుచేయి", style = MaterialTheme.typography.bodyMedium, fontWeight = FontWeight.Medium)
                    Text("Notify day before", style = MaterialTheme.typography.labelSmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
                }
                Switch(
                    checked = notifyDayBefore,
                    onCheckedChange = { notifyDayBefore = it },
                    colors = SwitchDefaults.colors(checkedThumbColor = TempleGold, checkedTrackColor = TempleGold.copy(alpha = 0.4f)),
                )
            }

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically,
            ) {
                Column {
                    Text("ఆ రోజు గుర్తుచేయి", style = MaterialTheme.typography.bodyMedium, fontWeight = FontWeight.Medium)
                    Text("Notify on the day", style = MaterialTheme.typography.labelSmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
                }
                Switch(
                    checked = notifyOnDay,
                    onCheckedChange = { notifyOnDay = it },
                    colors = SwitchDefaults.colors(checkedThumbColor = TempleGold, checkedTrackColor = TempleGold.copy(alpha = 0.4f)),
                )
            }

            HorizontalDivider(thickness = 0.5.dp)

            // Action buttons
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(12.dp),
            ) {
                OutlinedButton(
                    onClick = onDismiss,
                    modifier = Modifier.weight(1f),
                    shape = RoundedCornerShape(12.dp),
                ) {
                    Text("రద్దు / Cancel")
                }

                Button(
                    onClick = {
                        if (personName.isBlank()) {
                            nameError = true
                            return@Button
                        }
                        val entity = when (selectedType) {
                            FamilyDayType.TITHI -> FamilyDayEntity(
                                personName = personName.trim(),
                                relation = relation.trim(),
                                type = selectedType.name,
                                notes = notes.trim(),
                                tithiRefYear = tithiRefYear.toIntOrNull() ?: 0,
                                tithiRefMonth = tithiRefMonth.toIntOrNull() ?: 0,
                                tithiRefDay = tithiRefDay.toIntOrNull() ?: 0,
                                notifyDayBefore = notifyDayBefore,
                                notifyOnDay = notifyOnDay,
                            )
                            else -> FamilyDayEntity(
                                personName = personName.trim(),
                                relation = relation.trim(),
                                type = selectedType.name,
                                notes = notes.trim(),
                                gregorianMonth = month.toIntOrNull() ?: 0,
                                gregorianDay = day.toIntOrNull() ?: 0,
                                notifyDayBefore = notifyDayBefore,
                                notifyOnDay = notifyOnDay,
                            )
                        }
                        onSave(entity)
                    },
                    modifier = Modifier.weight(1f),
                    shape = RoundedCornerShape(12.dp),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = TempleGold,
                        contentColor = Color.White,
                    ),
                ) {
                    Text("సేవ్ చేయి / Save", fontWeight = FontWeight.Bold)
                }
            }
        }
    }
}
