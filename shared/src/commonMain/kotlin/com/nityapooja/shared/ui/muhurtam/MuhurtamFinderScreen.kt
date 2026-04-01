package com.nityapooja.shared.ui.muhurtam

import androidx.compose.foundation.clickable
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
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
import androidx.compose.ui.unit.sp
import com.nityapooja.shared.data.muhurtam.MuhurtamRules.EventType
import com.nityapooja.shared.data.muhurtam.MuhurtamRules.MuhurtamScore
import com.nityapooja.shared.ui.components.FontSizeControls
import com.nityapooja.shared.ui.components.FontSizeViewModel
import com.nityapooja.shared.ui.components.GlassmorphicCard
import com.nityapooja.shared.ui.panchangam.PanchangamViewModel
import com.nityapooja.shared.ui.theme.AuspiciousGreen
import com.nityapooja.shared.ui.theme.DeepVermillion
import com.nityapooja.shared.ui.theme.SacredTurmeric
import com.nityapooja.shared.ui.theme.TempleGold
import com.nityapooja.shared.platform.shareText
import org.koin.compose.viewmodel.koinViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MuhurtamFinderScreen(
    viewModel: MuhurtamFinderViewModel = koinViewModel(),
    panchangamViewModel: PanchangamViewModel = koinViewModel(),
    fontSizeViewModel: FontSizeViewModel = koinViewModel(),
    onBack: () -> Unit = {},
    bannerAd: (@Composable () -> Unit)? = null,
) {
    val selectedEvent by viewModel.selectedEvent.collectAsState()
    val scoredDates by viewModel.scoredDates.collectAsState()
    val isLoading by viewModel.isLoading.collectAsState()
    val locationInfo by panchangamViewModel.locationInfo.collectAsState()
    val userNakshatra by viewModel.userNakshatra.collectAsState()
    val selectedNakshatra by viewModel.selectedNakshatra.collectAsState()
    val selectedPersonName by viewModel.selectedPersonName.collectAsState()
    val familyMembers by viewModel.familyMembers.collectAsState()
    val isPersonalized = selectedNakshatra.isNotBlank()
    val fontSize by fontSizeViewModel.fontSize.collectAsState()
    val fontScale = fontSize / 16f
    var showNakshatraDropdown by remember { mutableStateOf(false) }
    var showAddFamilyDialog by remember { mutableStateOf(false) }

    val allNakshatras = listOf(
        "అశ్విని", "భరణి", "కృత్తిక", "రోహిణి", "మృగశిర",
        "ఆర్ద్ర", "పునర్వసు", "పుష్యమి", "ఆశ్లేష", "మఘ",
        "పూర్వ ఫల్గుణి", "ఉత్తర ఫల్గుణి", "హస్త", "చిత్ర", "స్వాతి",
        "విశాఖ", "అనురాధ", "జ్యేష్ఠ", "మూల", "పూర్వాషాఢ",
        "ఉత్తరాషాఢ", "శ్రవణం", "ధనిష్ఠ", "శతభిషం",
        "పూర్వాభాద్ర", "ఉత్తరాభాద్ర", "రేవతి",
    )

    LaunchedEffect(locationInfo) {
        viewModel.calculate(locationInfo.lat, locationInfo.lng, locationInfo.timezone)
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Column {
                        Text("శుభ ముహూర్తాలు", style = MaterialTheme.typography.titleLarge, fontWeight = FontWeight.Bold)
                        Text("Muhurtam Finder", style = MaterialTheme.typography.labelSmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
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
                colors = TopAppBarDefaults.topAppBarColors(containerColor = Color.Transparent),
            )
        },
    ) { padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding),
        ) {
            // Event type chips
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .horizontalScroll(rememberScrollState())
                    .padding(horizontal = 16.dp, vertical = 8.dp),
                horizontalArrangement = Arrangement.spacedBy(8.dp),
            ) {
                EventType.entries.forEach { event ->
                    FilterChip(
                        selected = event == selectedEvent,
                        onClick = { viewModel.selectEvent(event) },
                        label = {
                            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                                Text(event.nameTelugu, style = MaterialTheme.typography.labelSmall.copy(fontSize = (11 * fontScale).sp), fontWeight = FontWeight.Bold)
                                Text(event.nameEnglish, style = MaterialTheme.typography.labelSmall.copy(fontSize = (10 * fontScale).sp))
                            }
                        },
                        colors = FilterChipDefaults.filterChipColors(
                            selectedContainerColor = TempleGold.copy(alpha = 0.2f),
                            selectedLabelColor = TempleGold,
                        ),
                    )
                }
            }

            // Family member quick-switch chips + nakshatra selector
            Column(modifier = Modifier.padding(horizontal = 16.dp, vertical = 4.dp)) {
                // Quick-switch row: Self + saved family members + Add button
                Row(
                    modifier = Modifier.fillMaxWidth().horizontalScroll(rememberScrollState()),
                    horizontalArrangement = Arrangement.spacedBy(8.dp),
                    verticalAlignment = Alignment.CenterVertically,
                ) {
                    // Self chip
                    if (userNakshatra.isNotBlank()) {
                        FilterChip(
                            selected = selectedPersonName.isBlank() && selectedNakshatra == userNakshatra,
                            onClick = { viewModel.selectSelf() },
                            label = { Text("నేను (${userNakshatra})", style = MaterialTheme.typography.labelSmall) },
                            leadingIcon = { Icon(Icons.Default.Person, null, modifier = Modifier.size(16.dp)) },
                            colors = FilterChipDefaults.filterChipColors(selectedContainerColor = AuspiciousGreen.copy(alpha = 0.2f), selectedLabelColor = AuspiciousGreen),
                        )
                    }
                    // Family member chips
                    familyMembers.forEachIndexed { index, member ->
                        FilterChip(
                            selected = selectedPersonName == member.name && selectedNakshatra == member.nakshatra,
                            onClick = { viewModel.selectFamilyMember(member) },
                            label = { Text("${member.name} (${member.nakshatra})", style = MaterialTheme.typography.labelSmall) },
                            leadingIcon = { Icon(Icons.Default.People, null, modifier = Modifier.size(16.dp)) },
                            trailingIcon = {
                                Icon(Icons.Default.Close, "Remove", modifier = Modifier.size(14.dp).clickable { viewModel.removeFamilyMember(index) })
                            },
                            colors = FilterChipDefaults.filterChipColors(selectedContainerColor = TempleGold.copy(alpha = 0.2f), selectedLabelColor = TempleGold),
                        )
                    }
                    // Add family member button
                    AssistChip(
                        onClick = { showAddFamilyDialog = true },
                        label = { Text("+ కుటుంబం", style = MaterialTheme.typography.labelSmall) },
                        leadingIcon = { Icon(Icons.Default.PersonAdd, null, modifier = Modifier.size(16.dp)) },
                    )
                }

                // Current selection info
                if (isPersonalized) {
                    Spacer(Modifier.height(4.dp))
                    val displayName = if (selectedPersonName.isNotBlank()) "$selectedPersonName · " else ""
                    Text(
                        "${displayName}జన్మ నక్షత్రం: $selectedNakshatra · తార బలం",
                        style = MaterialTheme.typography.labelSmall.copy(fontSize = (11 * fontScale).sp),
                        color = AuspiciousGreen,
                    )
                }
            }

            // Nakshatra dropdown (for manual selection)
            Box(modifier = Modifier.padding(horizontal = 16.dp)) {
                TextButton(onClick = { showNakshatraDropdown = true }) {
                    Text("నక్షత్రం మార్చండి · Change nakshatra", style = MaterialTheme.typography.labelSmall, color = TempleGold)
                    Icon(Icons.Default.ArrowDropDown, null, tint = TempleGold, modifier = Modifier.size(16.dp))
                }
                DropdownMenu(
                    expanded = showNakshatraDropdown,
                    onDismissRequest = { showNakshatraDropdown = false },
                    modifier = Modifier.heightIn(max = 400.dp),
                ) {
                    DropdownMenuItem(
                        text = { Text("నక్షత్రం లేకుండా · Without birth star", style = MaterialTheme.typography.bodySmall) },
                        onClick = { viewModel.selectNakshatra(""); showNakshatraDropdown = false },
                    )
                    HorizontalDivider()
                    allNakshatras.forEach { nakshatra ->
                        DropdownMenuItem(
                            text = {
                                Row {
                                    Text(nakshatra, style = MaterialTheme.typography.bodyMedium, fontWeight = if (nakshatra == selectedNakshatra) FontWeight.Bold else FontWeight.Normal)
                                    if (nakshatra == userNakshatra) { Spacer(Modifier.width(8.dp)); Text("(మీది)", style = MaterialTheme.typography.labelSmall, color = AuspiciousGreen) }
                                }
                            },
                            onClick = { viewModel.selectNakshatra(nakshatra); showNakshatraDropdown = false },
                        )
                    }
                }
            }

            if (isLoading) {
                Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                    CircularProgressIndicator(color = TempleGold)
                }
            } else {
                Text(
                    "రాబోయే 30 రోజులలో ${selectedEvent.nameTelugu}కు శుభ ముహూర్తాలు",
                    style = MaterialTheme.typography.bodySmall.copy(fontSize = (12 * fontScale).sp),
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    modifier = Modifier.padding(horizontal = 16.dp, vertical = 4.dp),
                )

                bannerAd?.invoke()

                // Scored dates list
                LazyColumn(
                    contentPadding = PaddingValues(16.dp),
                    verticalArrangement = Arrangement.spacedBy(12.dp),
                ) {
                    items(scoredDates) { scoredDate ->
                        MuhurtamDateCard(scoredDate, fontScale, "${selectedEvent.nameTelugu} · ${selectedEvent.nameEnglish}", selectedNakshatra)
                    }
                }
            }
        }
    }

    // Add Family Member Dialog
    if (showAddFamilyDialog) {
        var nameInput by remember { mutableStateOf("") }
        var nakshatraInput by remember { mutableStateOf("") }
        var showNakshatraPicker by remember { mutableStateOf(false) }

        AlertDialog(
            onDismissRequest = { showAddFamilyDialog = false },
            title = { Text("కుటుంబ సభ్యుని చేర్చండి", fontWeight = FontWeight.Bold) },
            text = {
                Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
                    Text("Add Family Member", style = MaterialTheme.typography.labelSmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
                    OutlinedTextField(
                        value = nameInput,
                        onValueChange = { nameInput = it },
                        label = { Text("పేరు / Name") },
                        singleLine = true,
                        modifier = Modifier.fillMaxWidth(),
                    )
                    ExposedDropdownMenuBox(
                        expanded = showNakshatraPicker,
                        onExpandedChange = { showNakshatraPicker = it },
                    ) {
                        OutlinedTextField(
                            value = nakshatraInput.ifBlank { "" },
                            onValueChange = {},
                            label = { Text("నక్షత్రం / Nakshatra") },
                            readOnly = true,
                            singleLine = true,
                            modifier = Modifier.fillMaxWidth().menuAnchor(),
                            trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = showNakshatraPicker) },
                        )
                        ExposedDropdownMenu(
                            expanded = showNakshatraPicker,
                            onDismissRequest = { showNakshatraPicker = false },
                        ) {
                            allNakshatras.forEach { nak ->
                                DropdownMenuItem(
                                    text = { Text(nak) },
                                    onClick = { nakshatraInput = nak; showNakshatraPicker = false },
                                )
                            }
                        }
                    }
                }
            },
            confirmButton = {
                TextButton(
                    onClick = {
                        if (nameInput.isNotBlank() && nakshatraInput.isNotBlank()) {
                            viewModel.addFamilyMember(nameInput, nakshatraInput)
                            showAddFamilyDialog = false
                        }
                    },
                    enabled = nameInput.isNotBlank() && nakshatraInput.isNotBlank(),
                ) { Text("చేర్చండి / Add", color = TempleGold) }
            },
            dismissButton = {
                TextButton(onClick = { showAddFamilyDialog = false }) { Text("రద్దు / Cancel") }
            },
        )
    }
}

@Composable
private fun MuhurtamDateCard(scoredDate: ScoredDate, fontScale: Float, eventName: String, forNakshatra: String) {
    val scoreColor = when (scoredDate.result.score) {
        MuhurtamScore.EXCELLENT -> AuspiciousGreen
        MuhurtamScore.GOOD -> TempleGold
        MuhurtamScore.AVERAGE -> SacredTurmeric
        MuhurtamScore.AVOID -> DeepVermillion
    }

    GlassmorphicCard(
        accentColor = scoreColor,
        cornerRadius = 16.dp,
        contentPadding = 16.dp,
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.Top,
        ) {
            Column(modifier = Modifier.weight(1f)) {
                // Date display
                Text(
                    scoredDate.panchangamData.dateDisplay,
                    style = MaterialTheme.typography.titleMedium.copy(fontSize = (16 * fontScale).sp),
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.onSurface,
                )
                Text(
                    scoredDate.panchangamData.teluguDay,
                    style = MaterialTheme.typography.bodySmall.copy(fontSize = (14 * fontScale).sp),
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                )

                Spacer(Modifier.height(8.dp))

                // Panchangam summary
                Text(
                    "${scoredDate.panchangamData.tithi.nameTelugu} · ${scoredDate.panchangamData.tithi.pakshaTelugu}",
                    style = MaterialTheme.typography.bodyMedium.copy(fontSize = (14 * fontScale).sp),
                    color = MaterialTheme.colorScheme.onSurface,
                )
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Text(
                        scoredDate.panchangamData.nakshatra.nameTelugu,
                        style = MaterialTheme.typography.bodyMedium.copy(fontSize = (14 * fontScale).sp),
                        color = MaterialTheme.colorScheme.onSurface,
                    )
                    scoredDate.taraBalam?.let { tara ->
                        Spacer(Modifier.width(8.dp))
                        Surface(
                            shape = MaterialTheme.shapes.small,
                            color = (if (tara.isGood) AuspiciousGreen else DeepVermillion).copy(alpha = 0.12f),
                        ) {
                            Text(
                                "తా: ${tara.nameTelugu}",
                                modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp),
                                style = MaterialTheme.typography.labelSmall.copy(fontSize = 10.sp),
                                fontWeight = FontWeight.Bold,
                                color = if (tara.isGood) AuspiciousGreen else DeepVermillion,
                            )
                        }
                    }
                }
                Text(
                    scoredDate.panchangamData.yoga.nameTelugu,
                    style = MaterialTheme.typography.bodySmall.copy(fontSize = (14 * fontScale).sp),
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                )
            }

            // Score badge
            Surface(
                shape = MaterialTheme.shapes.medium,
                color = scoreColor.copy(alpha = 0.15f),
            ) {
                Column(
                    modifier = Modifier.padding(horizontal = 12.dp, vertical = 8.dp),
                    horizontalAlignment = Alignment.CenterHorizontally,
                ) {
                    Text(
                        "${scoredDate.result.points}",
                        style = MaterialTheme.typography.headlineMedium.copy(fontSize = (28 * fontScale).sp),
                        fontWeight = FontWeight.Bold,
                        color = scoreColor,
                    )
                    Text(
                        scoredDate.result.score.labelTelugu,
                        style = MaterialTheme.typography.labelSmall.copy(fontSize = (11 * fontScale).sp),
                        fontWeight = FontWeight.Bold,
                        color = scoreColor,
                    )
                }
            }
        }

        // Reasons + Share
        if (scoredDate.result.reasons.isNotEmpty()) {
            Spacer(Modifier.height(8.dp))
            HorizontalDivider(color = MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.3f))
            Spacer(Modifier.height(8.dp))

            scoredDate.result.reasons.forEach { reason ->
                Row(
                    modifier = Modifier.padding(vertical = 2.dp),
                    verticalAlignment = Alignment.CenterVertically,
                ) {
                    Icon(
                        if (reason.isPositive) Icons.Default.CheckCircle else Icons.Default.Cancel,
                        contentDescription = null,
                        modifier = Modifier.size(14.dp),
                        tint = if (reason.isPositive) AuspiciousGreen else DeepVermillion,
                    )
                    Spacer(Modifier.width(6.dp))
                    Text(
                        reason.textTelugu,
                        style = MaterialTheme.typography.bodySmall.copy(fontSize = (14 * fontScale).sp),
                        color = MaterialTheme.colorScheme.onSurface,
                    )
                }
            }

            Spacer(Modifier.height(4.dp))
            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.End) {
                TextButton(onClick = { shareText(buildMuhurtamShareText(scoredDate, eventName, forNakshatra)) }) {
                    Icon(Icons.Default.Share, "Share", Modifier.size(16.dp), tint = TempleGold)
                    Spacer(Modifier.width(4.dp))
                    Text("Share", style = MaterialTheme.typography.labelSmall, color = TempleGold)
                }
            }
        }
    }
}

private fun buildMuhurtamShareText(scoredDate: ScoredDate, eventName: String, forNakshatra: String): String {
    val p = scoredDate.panchangamData
    val r = scoredDate.result
    val scoreEmoji = when (r.score) {
        MuhurtamScore.EXCELLENT -> "🟢"
        MuhurtamScore.GOOD -> "🟡"
        MuhurtamScore.AVERAGE -> "🟠"
        MuhurtamScore.AVOID -> "🔴"
    }

    return buildString {
        append("╔══════════════════════╗\n")
        append("   శుభ ముహూర్తం · MUHURTAM\n")
        append("╚══════════════════════╝\n\n")
        append("📅 ${p.dateDisplay}\n")
        append("   ${p.teluguDay}\n\n")
        append("🕉 $eventName\n")
        if (forNakshatra.isNotBlank()) {
            append("👤 జన్మ నక్షత్రం: $forNakshatra\n")
        }
        append("\n")
        append("━━ పంచాంగం ━━\n")
        append("🔸 తిథి: ${p.tithi.nameTelugu} · ${p.tithi.pakshaTelugu}\n")
        append("🔸 నక్షత్రం: ${p.nakshatra.nameTelugu}\n")
        append("🔸 యోగం: ${p.yoga.nameTelugu}\n")
        append("🔸 సూర్యోదయం: ${p.sunTimes.sunrise}\n")
        append("🔸 సూర్యాస్తమయం: ${p.sunTimes.sunset}\n\n")
        scoredDate.taraBalam?.let { tara ->
            val taraEmoji = if (tara.isGood) "✅" else "❌"
            append("🌟 తార బలం: ${tara.nameTelugu} ($forNakshatra) $taraEmoji\n\n")
        }
        append("━━ శుభ సమయాలు ━━\n")
        append("🕐 అభిజిత్ ముహూర్తం: ${p.abhijitMuhurt.startTime} - ${p.abhijitMuhurt.endTime}\n")
        append("🕐 బ్రహ్మ ముహూర్తం: ${p.brahmaMuhurta.startTime} - ${p.brahmaMuhurta.endTime}\n")
        append("⚠ రాహు కాలం: ${p.rahuKaal.startTime} - ${p.rahuKaal.endTime} (నివారించండి)\n")
        append("⚠ యమగండం: ${p.yamagandam.startTime} - ${p.yamagandam.endTime} (నివారించండి)\n\n")
        append("━━ ఫలితం ━━\n")
        append("$scoreEmoji ${r.score.labelTelugu} · ${r.score.labelEnglish} (${r.points}/100)\n\n")
        r.reasons.forEach { reason ->
            val icon = if (reason.isPositive) "✅" else "❌"
            append("$icon ${reason.textTelugu}\n")
        }
        append("\n— NityaPooja App 🙏")
    }
}
