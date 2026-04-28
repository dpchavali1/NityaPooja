package com.nityapooja.shared.ui.settings

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import org.koin.compose.viewmodel.koinViewModel
import com.nityapooja.shared.data.preferences.ThemeMode
import com.nityapooja.shared.ui.panchangam.NAKSHATRA_NAMES_ENGLISH
import com.nityapooja.shared.ui.panchangam.NAKSHATRA_NAMES_TELUGU
import com.nityapooja.shared.ui.components.GlassmorphicCard
import com.nityapooja.shared.ui.components.PlaceResult
import com.nityapooja.shared.ui.components.SectionHeader
import com.nityapooja.shared.ui.components.searchPlaces
import com.nityapooja.shared.ui.theme.SpotifyGreen
import com.nityapooja.shared.ui.theme.TempleGold
import com.nityapooja.shared.ui.theme.NityaPoojaTextStyles
import com.nityapooja.shared.platform.shareText
import com.nityapooja.shared.platform.openUrl  // from PlatformUrlOpener

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SettingsScreen(
    onBack: () -> Unit = {},
    onNavigateToPrivacyPolicy: () -> Unit = {},
    onNavigateToFeatures: () -> Unit = {},
    onLinkSpotify: (() -> Unit)? = null,
    onUnlinkSpotify: (() -> Unit)? = null,
    onRequestExactAlarmPermission: (() -> Unit)? = null,
    onRequestReview: (() -> Unit)? = null,
    spotifyLinked: Boolean = false,
    spotifyConnecting: Boolean = false,
    spotifyInstalled: Boolean = false,
    viewModel: SettingsViewModel = koinViewModel(),
    bannerAd: (@Composable () -> Unit)? = null,
) {
    val themeMode by viewModel.themeMode.collectAsState()
    val userName by viewModel.userName.collectAsState()
    val gotra by viewModel.gotra.collectAsState()
    val userNakshatra by viewModel.nakshatra.collectAsState()
    val locationCity by viewModel.locationCity.collectAsState()
    val morningNotification by viewModel.morningNotification.collectAsState()
    val eveningNotification by viewModel.eveningNotification.collectAsState()
    val fontSize by viewModel.fontSize.collectAsState()
    val autoDarkMode by viewModel.autoDarkMode.collectAsState()
    val panchangNotifications by viewModel.panchangNotifications.collectAsState()
    val quizNotification by viewModel.quizNotification.collectAsState()
    val quizNotificationHour by viewModel.quizNotificationHour.collectAsState()
    val quizNotificationMinute by viewModel.quizNotificationMinute.collectAsState()
    val grahanamNotification by viewModel.grahanamNotification.collectAsState()
    val vrataNotification by viewModel.vrataNotification.collectAsState()
    val sacredMonthNotification by viewModel.sacredMonthNotification.collectAsState()
    val shlokaNotification by viewModel.shlokaNotification.collectAsState()
    val rahuKalamAlerts by viewModel.rahuKalamAlerts.collectAsState()
    val showEnglish by viewModel.showEnglish.collectAsState()

    val dataCleared by viewModel.dataCleared.collectAsState()

    // Request exact alarm permission (Android 12+) when user enables any notification.
    // On iOS/Desktop this is null and nothing happens.
    val requestPermissionOnEnable: (Boolean) -> Unit = { enabled ->
        if (enabled) onRequestExactAlarmPermission?.invoke()
    }

    var showCityPicker by remember { mutableStateOf(false) }
    var showClearDataDialog by remember { mutableStateOf(false) }
    var nameInput by remember(userName) { mutableStateOf(userName) }
    var gotraInput by remember(gotra) { mutableStateOf(gotra) }
    var nakshatraDropdownExpanded by remember { mutableStateOf(false) }
    var rashiDropdownExpanded by remember { mutableStateOf(false) }
    val selectedRashiId by viewModel.selectedRashiId.collectAsState()

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Column {
                        Text("\u0C38\u0C46\u0C1F\u0C4D\u0C1F\u0C3F\u0C02\u0C17\u0C4D\u200C\u0C32\u0C41", style = MaterialTheme.typography.titleLarge, fontWeight = FontWeight.Bold)
                        Text("Settings", style = MaterialTheme.typography.labelSmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
                    }
                },
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, "Back")
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.surface,
                ),
            )
        }
    ) { padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .verticalScroll(rememberScrollState())
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp),
        ) {
            // Profile Section
            SectionHeader(titleTelugu = "\u0C2A\u0C4D\u0C30\u0C4A\u0C2B\u0C48\u0C32\u0C4D", titleEnglish = "Profile")
            GlassmorphicCard(cornerRadius = 16.dp, contentPadding = 16.dp) {
                Text("\u0C2E\u0C40 \u0C2A\u0C47\u0C30\u0C41", style = MaterialTheme.typography.labelMedium, color = TempleGold)
                Text("Your Name", style = MaterialTheme.typography.labelSmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
                Spacer(Modifier.height(8.dp))
                // Auto-save name after user stops typing
                LaunchedEffect(nameInput) {
                    if (nameInput != userName && nameInput.isNotBlank()) {
                        delay(800) // debounce
                        viewModel.setUserName(nameInput)
                    }
                }
                OutlinedTextField(
                    value = nameInput,
                    onValueChange = { nameInput = it },
                    modifier = Modifier.fillMaxWidth(),
                    placeholder = { Text("\u0C2D\u0C15\u0C4D\u0C24\u0C3E / Enter your name") },
                    singleLine = true,
                    leadingIcon = { Icon(Icons.Default.Person, null, tint = TempleGold) },
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedBorderColor = TempleGold,
                        cursorColor = TempleGold,
                    ),
                )
                if (nameInput != userName && nameInput.isNotBlank()) {
                    Spacer(Modifier.height(8.dp))
                    Button(
                        onClick = { viewModel.setUserName(nameInput) },
                        colors = ButtonDefaults.buttonColors(containerColor = TempleGold),
                    ) {
                        Icon(Icons.Default.Save, null, Modifier.size(16.dp))
                        Spacer(Modifier.width(4.dp))
                        Text("Save Name")
                    }
                }
            }

            // Sankalpam Details
            SectionHeader(titleTelugu = "\u0C38\u0C02\u0C15\u0C32\u0C4D\u0C2A\u0C02 \u0C35\u0C3F\u0C35\u0C30\u0C3E\u0C32\u0C41", titleEnglish = "Sankalpam Details")
            GlassmorphicCard(cornerRadius = 16.dp, contentPadding = 16.dp) {
                // Gotra field
                Text("\u0C17\u0C4B\u0C24\u0C4D\u0C30\u0C02", style = MaterialTheme.typography.labelMedium, color = TempleGold)
                Text("Gotra", style = MaterialTheme.typography.labelSmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
                Spacer(Modifier.height(8.dp))
                LaunchedEffect(gotraInput) {
                    if (gotraInput != gotra && gotraInput.isNotBlank()) {
                        delay(800)
                        viewModel.setGotra(gotraInput)
                    }
                }
                OutlinedTextField(
                    value = gotraInput,
                    onValueChange = { gotraInput = it },
                    modifier = Modifier.fillMaxWidth(),
                    placeholder = { Text("\u0C17\u0C4B\u0C24\u0C4D\u0C30\u0C02 / Enter your gotra") },
                    singleLine = true,
                    leadingIcon = { Icon(Icons.Default.AccountTree, null, tint = TempleGold) },
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedBorderColor = TempleGold,
                        cursorColor = TempleGold,
                    ),
                )

                Spacer(Modifier.height(16.dp))
                HorizontalDivider(color = MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.3f))
                Spacer(Modifier.height(16.dp))

                // Nakshatra dropdown
                Text("\u0C1C\u0C28\u0C4D\u0C2E \u0C28\u0C15\u0C4D\u0C37\u0C24\u0C4D\u0C30\u0C02", style = MaterialTheme.typography.labelMedium, color = TempleGold)
                Text("Birth Star", style = MaterialTheme.typography.labelSmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
                Spacer(Modifier.height(8.dp))
                ExposedDropdownMenuBox(
                    expanded = nakshatraDropdownExpanded,
                    onExpandedChange = { nakshatraDropdownExpanded = it },
                ) {
                    OutlinedTextField(
                        value = if (userNakshatra.isNotBlank()) {
                            val idx = NAKSHATRA_NAMES_ENGLISH.indexOf(userNakshatra)
                            if (idx >= 0) "${NAKSHATRA_NAMES_TELUGU[idx]} / ${NAKSHATRA_NAMES_ENGLISH[idx]}"
                            else userNakshatra
                        } else "",
                        onValueChange = {},
                        readOnly = true,
                        modifier = Modifier.fillMaxWidth().menuAnchor(),
                        placeholder = { Text("\u0C28\u0C15\u0C4D\u0C37\u0C24\u0C4D\u0C30\u0C02 \u0C0E\u0C02\u0C1A\u0C41\u0C15\u0C4B\u0C02\u0C21\u0C3F / Select nakshatra") },
                        singleLine = true,
                        leadingIcon = { Icon(Icons.Default.Stars, null, tint = TempleGold) },
                        trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = nakshatraDropdownExpanded) },
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedBorderColor = TempleGold,
                            cursorColor = TempleGold,
                        ),
                    )
                    ExposedDropdownMenu(
                        expanded = nakshatraDropdownExpanded,
                        onDismissRequest = { nakshatraDropdownExpanded = false },
                    ) {
                        NAKSHATRA_NAMES_ENGLISH.forEachIndexed { index, englishName ->
                            DropdownMenuItem(
                                text = {
                                    Text("${NAKSHATRA_NAMES_TELUGU[index]} / $englishName")
                                },
                                onClick = {
                                    viewModel.setNakshatra(englishName)
                                    nakshatraDropdownExpanded = false
                                },
                                contentPadding = ExposedDropdownMenuDefaults.ItemContentPadding,
                            )
                        }
                    }
                }
            }

            GlassmorphicCard(cornerRadius = 16.dp, contentPadding = 16.dp) {
                Spacer(Modifier.height(16.dp))
                HorizontalDivider(color = MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.3f))
                Spacer(Modifier.height(16.dp))
                Text("\u0C30ాశి చక్రం", style = MaterialTheme.typography.labelMedium, color = TempleGold)
                Text("Rashifal Widget — Your Rashi", style = MaterialTheme.typography.labelSmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
                Spacer(Modifier.height(8.dp))
                val rashiNamesT = listOf("మేషం", "వృషభం", "మిథునం", "కర్కాటకం", "సింహం", "కన్య", "తుల", "వృశ్చికం", "ధనస్సు", "మకరం", "కుంభం", "మీనం")
                val rashiNamesE = listOf("Mesha", "Vrishabha", "Mithuna", "Kataka", "Simha", "Kanya", "Tula", "Vrischika", "Dhanus", "Makara", "Kumbha", "Meena")
                ExposedDropdownMenuBox(
                    expanded = rashiDropdownExpanded,
                    onExpandedChange = { rashiDropdownExpanded = it },
                ) {
                    OutlinedTextField(
                        value = if (selectedRashiId in 1..12) "${rashiNamesT[selectedRashiId - 1]} / ${rashiNamesE[selectedRashiId - 1]}" else "",
                        onValueChange = {},
                        readOnly = true,
                        modifier = Modifier.fillMaxWidth().menuAnchor(),
                        placeholder = { Text("\u0C30ాశి ఎంచుకోండి / Select your rashi") },
                        singleLine = true,
                        leadingIcon = { Icon(Icons.Default.Stars, null, tint = TempleGold) },
                        trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = rashiDropdownExpanded) },
                        colors = OutlinedTextFieldDefaults.colors(focusedBorderColor = TempleGold, cursorColor = TempleGold),
                    )
                    ExposedDropdownMenu(
                        expanded = rashiDropdownExpanded,
                        onDismissRequest = { rashiDropdownExpanded = false },
                    ) {
                        DropdownMenuItem(
                            text = { Text("మేషం / Mesha") },
                            onClick = {
                                viewModel.setSelectedRashiId(1)
                                rashiDropdownExpanded = false
                            },
                            contentPadding = ExposedDropdownMenuDefaults.ItemContentPadding,
                        )
                        DropdownMenuItem(
                            text = { Text("వృషభం / Vrishabha") },
                            onClick = {
                                viewModel.setSelectedRashiId(2)
                                rashiDropdownExpanded = false
                            },
                            contentPadding = ExposedDropdownMenuDefaults.ItemContentPadding,
                        )
                        DropdownMenuItem(
                            text = { Text("మిథునం / Mithuna") },
                            onClick = {
                                viewModel.setSelectedRashiId(3)
                                rashiDropdownExpanded = false
                            },
                            contentPadding = ExposedDropdownMenuDefaults.ItemContentPadding,
                        )
                        DropdownMenuItem(
                            text = { Text("కర్కాటకం / Kataka") },
                            onClick = {
                                viewModel.setSelectedRashiId(4)
                                rashiDropdownExpanded = false
                            },
                            contentPadding = ExposedDropdownMenuDefaults.ItemContentPadding,
                        )
                        DropdownMenuItem(
                            text = { Text("సింహం / Simha") },
                            onClick = {
                                viewModel.setSelectedRashiId(5)
                                rashiDropdownExpanded = false
                            },
                            contentPadding = ExposedDropdownMenuDefaults.ItemContentPadding,
                        )
                        DropdownMenuItem(
                            text = { Text("కన్య / Kanya") },
                            onClick = {
                                viewModel.setSelectedRashiId(6)
                                rashiDropdownExpanded = false
                            },
                            contentPadding = ExposedDropdownMenuDefaults.ItemContentPadding,
                        )
                        DropdownMenuItem(
                            text = { Text("తుల / Tula") },
                            onClick = {
                                viewModel.setSelectedRashiId(7)
                                rashiDropdownExpanded = false
                            },
                            contentPadding = ExposedDropdownMenuDefaults.ItemContentPadding,
                        )
                        DropdownMenuItem(
                            text = { Text("వృశ్చికం / Vrischika") },
                            onClick = {
                                viewModel.setSelectedRashiId(8)
                                rashiDropdownExpanded = false
                            },
                            contentPadding = ExposedDropdownMenuDefaults.ItemContentPadding,
                        )
                        DropdownMenuItem(
                            text = { Text("ధనస్సు / Dhanus") },
                            onClick = {
                                viewModel.setSelectedRashiId(9)
                                rashiDropdownExpanded = false
                            },
                            contentPadding = ExposedDropdownMenuDefaults.ItemContentPadding,
                        )
                        DropdownMenuItem(
                            text = { Text("మకరం / Makara") },
                            onClick = {
                                viewModel.setSelectedRashiId(10)
                                rashiDropdownExpanded = false
                            },
                            contentPadding = ExposedDropdownMenuDefaults.ItemContentPadding,
                        )
                        DropdownMenuItem(
                            text = { Text("కుంభం / Kumbha") },
                            onClick = {
                                viewModel.setSelectedRashiId(11)
                                rashiDropdownExpanded = false
                            },
                            contentPadding = ExposedDropdownMenuDefaults.ItemContentPadding,
                        )
                        DropdownMenuItem(
                            text = { Text("మీనం / Meena") },
                            onClick = {
                                viewModel.setSelectedRashiId(12)
                                rashiDropdownExpanded = false
                            },
                            contentPadding = ExposedDropdownMenuDefaults.ItemContentPadding,
                        )
                    }
                }
            }

            // Appearance
            SectionHeader(titleTelugu = "\u0C30\u0C42\u0C2A\u0C02", titleEnglish = "Appearance")
            GlassmorphicCard(cornerRadius = 16.dp, contentPadding = 16.dp) {
                Text("\u0C25\u0C40\u0C2E\u0C4D", style = MaterialTheme.typography.labelMedium, color = TempleGold)
                Text("Theme", style = MaterialTheme.typography.labelSmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
                Spacer(Modifier.height(12.dp))
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(8.dp),
                ) {
                    ThemeOption.entries.forEach { option ->
                        val isSelected = themeMode == option.mode
                        FilterChip(
                            selected = isSelected,
                            onClick = { viewModel.setThemeMode(option.mode) },
                            label = {
                                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                                    Text(option.labelTelugu, style = MaterialTheme.typography.labelSmall)
                                    Text(option.labelEnglish, style = MaterialTheme.typography.labelSmall)
                                }
                            },
                            leadingIcon = {
                                Icon(option.icon, null, Modifier.size(18.dp))
                            },
                            colors = FilterChipDefaults.filterChipColors(
                                selectedContainerColor = TempleGold.copy(alpha = 0.15f),
                                selectedLabelColor = TempleGold,
                                selectedLeadingIconColor = TempleGold,
                            ),
                            modifier = Modifier.weight(1f),
                        )
                    }
                }
            }

            // Auto Dark Mode
            GlassmorphicCard(cornerRadius = 16.dp, contentPadding = 16.dp) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically,
                ) {
                    Column {
                        Text("\u0C38\u0C4D\u0C35\u0C2F\u0C02\u0C1A\u0C3E\u0C32\u0C15 \u0C21\u0C3E\u0C30\u0C4D\u0C15\u0C4D \u0C2E\u0C4B\u0C21\u0C4D", style = MaterialTheme.typography.titleSmall, fontWeight = FontWeight.Medium)
                        Text("Auto Dark Mode \u00B7 Sunrise/Sunset based", style = MaterialTheme.typography.labelSmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
                    }
                    Switch(
                        checked = autoDarkMode,
                        onCheckedChange = { viewModel.setAutoDarkMode(it) },
                        colors = SwitchDefaults.colors(checkedThumbColor = TempleGold),
                    )
                }
            }

            // Font Size
            GlassmorphicCard(cornerRadius = 16.dp, contentPadding = 16.dp) {
                Text("\u0C05\u0C15\u0C4D\u0C37\u0C30\u0C3E\u0C32 \u0C2A\u0C30\u0C3F\u0C2E\u0C3E\u0C23\u0C02", style = MaterialTheme.typography.labelMedium, color = TempleGold)
                Text("Font Size", style = MaterialTheme.typography.labelSmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
                Spacer(Modifier.height(8.dp))
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically,
                ) {
                    Text("A", style = MaterialTheme.typography.bodySmall)
                    Slider(
                        value = fontSize.toFloat(),
                        onValueChange = { viewModel.setFontSize(it.toInt()) },
                        valueRange = 14f..32f,
                        steps = 8,
                        modifier = Modifier.weight(1f).padding(horizontal = 8.dp),
                        colors = SliderDefaults.colors(
                            thumbColor = TempleGold,
                            activeTrackColor = TempleGold,
                        ),
                    )
                    Text("A", style = MaterialTheme.typography.titleLarge, fontWeight = FontWeight.Bold)
                }
                Text(
                    "${fontSize}sp",
                    style = MaterialTheme.typography.labelSmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    modifier = Modifier.fillMaxWidth(),
                    textAlign = TextAlign.Center,
                )
            }

            // Show English Translations toggle
            GlassmorphicCard(cornerRadius = 16.dp, contentPadding = 16.dp) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically,
                ) {
                    Column(modifier = Modifier.weight(1f)) {
                        Text("\u0C07\u0C02\u0C17\u0C4D\u0C32\u0C40\u0C37\u0C4D \u0C05\u0C28\u0C41\u0C35\u0C3E\u0C26\u0C3E\u0C32\u0C41", style = MaterialTheme.typography.titleSmall, fontWeight = FontWeight.Medium)
                        Text("Show English translations", style = MaterialTheme.typography.labelSmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
                    }
                    Switch(
                        checked = showEnglish,
                        onCheckedChange = { viewModel.setShowEnglish(it) },
                        colors = SwitchDefaults.colors(checkedThumbColor = TempleGold),
                    )
                }
            }

            // Location
            SectionHeader(titleTelugu = "\u0C38\u0C4D\u0C25\u0C3E\u0C28\u0C02", titleEnglish = "Location")
            GlassmorphicCard(
                cornerRadius = 16.dp,
                contentPadding = 16.dp,
                onClick = { showCityPicker = true },
            ) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically,
                ) {
                    Column {
                        Text("\u0C2A\u0C02\u0C1A\u0C3E\u0C02\u0C17\u0C02 \u0C38\u0C4D\u0C25\u0C3E\u0C28\u0C02", style = MaterialTheme.typography.labelMedium, color = TempleGold)
                        Text("Panchangam Location", style = MaterialTheme.typography.labelSmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
                        Spacer(Modifier.height(4.dp))
                        Text(
                            locationCity,
                            style = MaterialTheme.typography.titleMedium,
                            fontWeight = FontWeight.Medium,
                        )
                    }
                    Icon(
                        Icons.Default.LocationOn,
                        contentDescription = null,
                        tint = TempleGold,
                    )
                }
            }

            NotificationsSection(
                morningNotification = morningNotification,
                onMorningNotificationChange = { requestPermissionOnEnable(it); viewModel.setMorningNotification(it) },
                eveningNotification = eveningNotification,
                onEveningNotificationChange = { requestPermissionOnEnable(it); viewModel.setEveningNotification(it) },
                shlokaNotification = shlokaNotification,
                onShlokaNotificationChange = { requestPermissionOnEnable(it); viewModel.setShlokaNotification(it) },
                panchangNotifications = panchangNotifications,
                onPanchangNotificationsChange = { requestPermissionOnEnable(it); viewModel.setPanchangNotifications(it) },
                quizNotification = quizNotification,
                onQuizNotificationChange = { requestPermissionOnEnable(it); viewModel.setQuizNotification(it) },
                quizNotificationHour = quizNotificationHour,
                quizNotificationMinute = quizNotificationMinute,
                onQuizTimeChange = { h, m -> viewModel.setQuizNotificationTime(h, m) },
                vrataNotification = vrataNotification,
                onVrataNotificationChange = { requestPermissionOnEnable(it); viewModel.setVrataNotification(it) },
                sacredMonthNotification = sacredMonthNotification,
                onSacredMonthNotificationChange = { requestPermissionOnEnable(it); viewModel.setSacredMonthNotification(it) },
                grahanamNotification = grahanamNotification,
                onGrahanamNotificationChange = { requestPermissionOnEnable(it); viewModel.setGrahanamNotification(it) },
                rahuKalamAlerts = rahuKalamAlerts,
                onRahuKalamAlertsChange = { requestPermissionOnEnable(it); viewModel.setRahuKalamAlerts(it) },
            )

            // Music / Spotify (Android only)
            if (onLinkSpotify != null) {
                SectionHeader(titleTelugu = "సంగీతం", titleEnglish = "Music")
                GlassmorphicCard(cornerRadius = 16.dp, contentPadding = 16.dp) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically,
                    ) {
                        Column(modifier = Modifier.weight(1f)) {
                            Text("Spotify", style = MaterialTheme.typography.titleSmall, fontWeight = FontWeight.Medium)
                            when {
                                !spotifyInstalled -> {
                                    Text(
                                        "Spotify app not installed",
                                        style = MaterialTheme.typography.labelSmall,
                                        color = MaterialTheme.colorScheme.error,
                                    )
                                }
                                spotifyLinked -> {
                                    Text(
                                        "Connected",
                                        style = MaterialTheme.typography.labelSmall,
                                        color = SpotifyGreen,
                                    )
                                }
                                spotifyConnecting -> {
                                    Text(
                                        "Connecting...",
                                        style = MaterialTheme.typography.labelSmall,
                                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                                    )
                                }
                                else -> {
                                    Text(
                                        "Not linked",
                                        style = MaterialTheme.typography.labelSmall,
                                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                                    )
                                }
                            }
                        }
                        when {
                            !spotifyInstalled -> {
                                FilledTonalButton(
                                    onClick = onLinkSpotify,
                                    colors = ButtonDefaults.filledTonalButtonColors(
                                        containerColor = SpotifyGreen.copy(alpha = 0.15f),
                                        contentColor = SpotifyGreen,
                                    ),
                                ) {
                                    Icon(Icons.Default.GetApp, null, Modifier.size(16.dp))
                                    Spacer(Modifier.width(4.dp))
                                    Text("Install")
                                }
                            }
                            spotifyLinked -> {
                                OutlinedButton(
                                    onClick = { onUnlinkSpotify?.invoke() },
                                ) {
                                    Text("Unlink")
                                }
                            }
                            else -> {
                                FilledTonalButton(
                                    onClick = onLinkSpotify,
                                    colors = ButtonDefaults.filledTonalButtonColors(
                                        containerColor = SpotifyGreen.copy(alpha = 0.15f),
                                        contentColor = SpotifyGreen,
                                    ),
                                ) {
                                    Icon(Icons.Default.Link, null, Modifier.size(16.dp))
                                    Spacer(Modifier.width(4.dp))
                                    Text("Connect")
                                }
                            }
                        }
                    }
                    Spacer(Modifier.height(8.dp))
                    Text(
                        "Link Spotify to play devotional audio while reading along",
                        style = MaterialTheme.typography.labelSmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                    )
                }
            }

            // Data & Privacy
            SectionHeader(titleTelugu = "డేటా & గోప్యత", titleEnglish = "Data & Privacy")
            GlassmorphicCard(cornerRadius = 16.dp, contentPadding = 16.dp) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clickable { onNavigateToPrivacyPolicy() },
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically,
                ) {
                    Column {
                        Text("\u0C17\u0C4B\u0C2A\u0C4D\u0C2F\u0C24\u0C3E \u0C35\u0C3F\u0C27\u0C3E\u0C28\u0C02", style = MaterialTheme.typography.titleSmall, fontWeight = FontWeight.Medium)
                        Text("Privacy Policy", style = MaterialTheme.typography.labelSmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
                    }
                    Icon(Icons.Default.Policy, null, tint = TempleGold)
                }
                Spacer(Modifier.height(12.dp))
                HorizontalDivider(color = MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.3f))
                Spacer(Modifier.height(12.dp))
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clickable { showClearDataDialog = true },
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically,
                ) {
                    Column {
                        Text("\u0C28\u0C3E \u0C21\u0C47\u0C1F\u0C3E\u0C28\u0C41 \u0C24\u0C4A\u0C32\u0C17\u0C3F\u0C02\u0C1A\u0C41", style = MaterialTheme.typography.titleSmall, fontWeight = FontWeight.Medium)
                        Text("Clear My Data", style = MaterialTheme.typography.labelSmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
                    }
                    Icon(Icons.Default.DeleteForever, null, tint = MaterialTheme.colorScheme.error)
                }
            }

            // About
            SectionHeader(titleTelugu = "\u0C17\u0C41\u0C30\u0C3F\u0C02\u0C1A\u0C3F", titleEnglish = "About")
            GlassmorphicCard(cornerRadius = 16.dp, contentPadding = 20.dp) {
                Column(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalAlignment = Alignment.CenterHorizontally,
                ) {
                    Text(
                        "\u0C28\u0C3F\u0C24\u0C4D\u0C2F \u0C2A\u0C42\u0C1C",
                        style = NityaPoojaTextStyles.TeluguDisplay,
                        color = TempleGold,
                        textAlign = TextAlign.Center,
                    )
                    Text(
                        "NityaPooja",
                        style = MaterialTheme.typography.titleMedium,
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                    )
                    Spacer(Modifier.height(8.dp))
                    Text(
                        "Version 2.2",
                        style = MaterialTheme.typography.labelSmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                    )
                    Spacer(Modifier.height(4.dp))
                    Text(
                        "\u0C2E\u0C40 \u0C06\u0C27\u0C4D\u0C2F\u0C3E\u0C24\u0C4D\u0C2E\u0C3F\u0C15 \u0C38\u0C39\u0C1A\u0C30\u0C41\u0C21\u0C41",
                        style = MaterialTheme.typography.bodySmall,
                        color = TempleGold,
                    )
                    Text(
                        "Your Spiritual Companion",
                        style = MaterialTheme.typography.labelSmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                    )
                    Spacer(Modifier.height(16.dp))
                    // Explore Features
                    Button(
                        onClick = onNavigateToFeatures,
                        modifier = Modifier.fillMaxWidth(),
                        colors = ButtonDefaults.buttonColors(containerColor = TempleGold.copy(alpha = 0.15f), contentColor = TempleGold),
                    ) {
                        Icon(Icons.Default.AutoAwesome, null, modifier = Modifier.size(16.dp))
                        Spacer(Modifier.width(6.dp))
                        Text("యాప్ విశేషాలు · Explore Features", style = MaterialTheme.typography.labelMedium)
                    }
                    Spacer(Modifier.height(8.dp))
                    // Website
                    OutlinedButton(
                        onClick = { openUrl("https://nityapooja.app") },
                        modifier = Modifier.fillMaxWidth(),
                        border = androidx.compose.foundation.BorderStroke(1.dp, TempleGold.copy(alpha = 0.5f)),
                    ) {
                        Icon(Icons.Default.Language, null, modifier = Modifier.size(16.dp), tint = TempleGold)
                        Spacer(Modifier.width(6.dp))
                        Text("Visit nityapooja.app", color = TempleGold, style = MaterialTheme.typography.labelMedium)
                    }
                    Spacer(Modifier.height(8.dp))
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(12.dp),
                    ) {
                        // Share App
                        OutlinedButton(
                            onClick = {
                                shareText(
                                    "🙏 I use NityaPooja for daily Panchangam, Japa, Sankalpam & more!\n\nDownload free: https://nityapooja.app",
                                    "Check out NityaPooja",
                                )
                            },
                            modifier = Modifier.weight(1f),
                            border = androidx.compose.foundation.BorderStroke(1.dp, TempleGold.copy(alpha = 0.5f)),
                        ) {
                            Icon(Icons.Default.Share, null, modifier = Modifier.size(16.dp), tint = TempleGold)
                            Spacer(Modifier.width(6.dp))
                            Text("Share App", color = TempleGold, style = MaterialTheme.typography.labelMedium)
                        }
                        // Rate App — open store directly (reliable on all builds)
                        OutlinedButton(
                            onClick = {
                                openUrl("https://play.google.com/store/apps/details?id=com.nityapooja.app")
                            },
                            modifier = Modifier.weight(1f),
                            border = androidx.compose.foundation.BorderStroke(1.dp, TempleGold.copy(alpha = 0.5f)),
                        ) {
                            Icon(Icons.Default.Star, null, modifier = Modifier.size(16.dp), tint = TempleGold)
                            Spacer(Modifier.width(6.dp))
                            Text("Rate App", color = TempleGold, style = MaterialTheme.typography.labelMedium)
                        }
                    }
                    Spacer(Modifier.height(8.dp))
                    Text(
                        "nityapooja.contact@gmail.com",
                        style = MaterialTheme.typography.labelSmall,
                        color = TempleGold,
                    )
                }
            }

            bannerAd?.invoke()

            Spacer(Modifier.height(32.dp))
        }
    }

    // Clear Data Confirmation Dialog
    if (showClearDataDialog) {
        AlertDialog(
            onDismissRequest = { showClearDataDialog = false },
            icon = { Icon(Icons.Default.DeleteForever, null, tint = MaterialTheme.colorScheme.error) },
            title = {
                Column {
                    Text("\u0C21\u0C47\u0C1F\u0C3E\u0C28\u0C41 \u0C24\u0C4A\u0C32\u0C17\u0C3F\u0C02\u0C1A\u0C3E\u0C32\u0C3E?", style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold)
                    Text("Clear all data?", style = MaterialTheme.typography.labelSmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
                }
            },
            text = {
                Text("This will clear your bookmarks, reading history, japa sessions, and preferences. This action cannot be undone.")
            },
            confirmButton = {
                TextButton(
                    onClick = {
                        viewModel.clearAllUserData()
                        showClearDataDialog = false
                    },
                ) {
                    Text("Clear", color = MaterialTheme.colorScheme.error)
                }
            },
            dismissButton = {
                TextButton(onClick = { showClearDataDialog = false }) {
                    Text("Cancel", color = TempleGold)
                }
            },
        )
    }

    // Data Cleared Snackbar
    if (dataCleared) {
        LaunchedEffect(Unit) {
            delay(2000)
            viewModel.resetDataClearedFlag()
        }
    }

    // City Picker Dialog
    if (showCityPicker) {
        CityPickerDialog(
            currentCity = locationCity,
            onDismiss = { showCityPicker = false },
            onCitySelected = { name, lat, lng, timezoneId ->
                viewModel.setLocation(name, lat, lng, timezoneId)
                showCityPicker = false
            },
        )
    }
}

@Composable
private fun CityPickerDialog(
    currentCity: String,
    onDismiss: () -> Unit,
    onCitySelected: (name: String, lat: Double, lng: Double, timezoneId: String) -> Unit,
) {
    var searchQuery by remember { mutableStateOf("") }
    var suggestions by remember { mutableStateOf<List<PlaceResult>>(emptyList()) }
    var isLoading by remember { mutableStateOf(false) }
    var searchJob by remember { mutableStateOf<Job?>(null) }
    val scope = rememberCoroutineScope()

    AlertDialog(
        onDismissRequest = onDismiss,
        title = {
            Column {
                Text("\u0C28\u0C17\u0C30\u0C02 \u0C0E\u0C02\u0C1A\u0C41\u0C15\u0C4B\u0C02\u0C21\u0C3F", style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold)
                Text("Select City", style = MaterialTheme.typography.labelSmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
            }
        },
        text = {
            Column(modifier = Modifier.heightIn(max = 400.dp)) {
                OutlinedTextField(
                    value = searchQuery,
                    onValueChange = { newQuery ->
                        searchQuery = newQuery
                        searchJob?.cancel()
                        if (newQuery.length >= 2) {
                            isLoading = true
                            searchJob = scope.launch {
                                delay(300)
                                suggestions = searchPlaces(newQuery)
                                isLoading = false
                            }
                        } else {
                            suggestions = emptyList()
                            isLoading = false
                        }
                    },
                    modifier = Modifier.fillMaxWidth(),
                    placeholder = { Text("Type city name to search worldwide...") },
                    singleLine = true,
                    leadingIcon = { Icon(Icons.Default.Search, null, tint = TempleGold) },
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedBorderColor = TempleGold,
                        cursorColor = TempleGold,
                    ),
                )
                if (isLoading) {
                    LinearProgressIndicator(
                        modifier = Modifier.fillMaxWidth().padding(top = 2.dp),
                        color = TempleGold,
                    )
                }
                Spacer(Modifier.height(8.dp))
                LazyColumn(
                    verticalArrangement = Arrangement.spacedBy(2.dp),
                ) {
                    if (suggestions.isEmpty() && !isLoading) {
                        item {
                            Text(
                                if (searchQuery.length < 2)
                                    "\u0C35\u0C47\u0C21\u0C41\u0C15\u0C41\u0C28\u0C47 \u0C28\u0C17\u0C30\u0C02 \u0C1F\u0C48\u0C2A\u0C4D \u0C1A\u0C47\u0C2F\u0C02\u0C21\u0C3F · Type a city name to search"
                                else
                                    "No results found · Try a different name",
                                style = MaterialTheme.typography.bodySmall,
                                color = MaterialTheme.colorScheme.onSurfaceVariant,
                                modifier = Modifier.padding(vertical = 8.dp),
                            )
                        }
                    }
                    items(suggestions) { place ->
                        val isSelected = place.displayName == currentCity
                        ListItem(
                            headlineContent = {
                                Text(
                                    place.displayName,
                                    fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal,
                                    color = if (isSelected) TempleGold else MaterialTheme.colorScheme.onSurface,
                                )
                            },
                            supportingContent = {
                                Text(
                                    place.subtitle,
                                    style = MaterialTheme.typography.labelSmall,
                                )
                            },
                            leadingContent = {
                                Icon(
                                    Icons.Default.LocationOn,
                                    null,
                                    tint = if (isSelected) TempleGold else MaterialTheme.colorScheme.onSurfaceVariant,
                                )
                            },
                            trailingContent = {
                                if (isSelected) Icon(Icons.Default.Check, null, tint = TempleGold)
                            },
                            modifier = Modifier.clickable {
                                onCitySelected(place.displayName, place.lat, place.lng, place.timezoneId)
                            },
                        )
                    }
                }
            }
        },
        confirmButton = {
            TextButton(onClick = onDismiss) {
                Text("Cancel", color = TempleGold)
            }
        },
    )
}

private enum class ThemeOption(
    val mode: ThemeMode,
    val labelTelugu: String,
    val labelEnglish: String,
    val icon: androidx.compose.ui.graphics.vector.ImageVector,
) {
    SYSTEM(ThemeMode.SYSTEM, "\u0C38\u0C3F\u0C38\u0C4D\u0C1F\u0C2E\u0C4D", "System", Icons.Default.PhoneAndroid),
    LIGHT(ThemeMode.LIGHT, "\u0C32\u0C48\u0C1F\u0C4D", "Light", Icons.Default.LightMode),
    DARK(ThemeMode.DARK, "\u0C21\u0C3E\u0C30\u0C4D\u0C15\u0C4D", "Dark", Icons.Default.DarkMode),
    SAFFRON(ThemeMode.SAFFRON, "\u0C2D\u0C17\u0C35\u0C3E", "Saffron", Icons.Default.Whatshot),
}
