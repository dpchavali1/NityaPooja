package com.nityapooja.app.ui.settings

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
import kotlinx.coroutines.delay
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.nityapooja.app.data.model.indianCities
import com.nityapooja.app.data.preferences.ThemeMode
import com.nityapooja.app.ui.panchangam.NAKSHATRA_NAMES_ENGLISH
import com.nityapooja.app.ui.panchangam.NAKSHATRA_NAMES_TELUGU
import com.nityapooja.app.data.spotify.SpotifyConnectionStatus
import com.nityapooja.app.ui.components.GlassmorphicCard
import com.nityapooja.app.ui.components.SectionHeader
import com.nityapooja.app.ui.theme.SpotifyGreen
import com.nityapooja.app.ui.theme.TempleGold
import com.nityapooja.app.ui.theme.NityaPoojaTextStyles

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SettingsScreen(
    onBack: () -> Unit = {},
    onNavigateToPrivacyPolicy: () -> Unit = {},
    onLinkSpotify: () -> Unit = {},
    viewModel: SettingsViewModel = hiltViewModel(),
) {
    val themeMode by viewModel.themeMode.collectAsStateWithLifecycle()
    val userName by viewModel.userName.collectAsStateWithLifecycle()
    val gotra by viewModel.gotra.collectAsStateWithLifecycle()
    val userNakshatra by viewModel.nakshatra.collectAsStateWithLifecycle()
    val locationCity by viewModel.locationCity.collectAsStateWithLifecycle()
    val morningNotification by viewModel.morningNotification.collectAsStateWithLifecycle()
    val eveningNotification by viewModel.eveningNotification.collectAsStateWithLifecycle()
    val fontSize by viewModel.fontSize.collectAsStateWithLifecycle()
    val autoDarkMode by viewModel.autoDarkMode.collectAsStateWithLifecycle()
    val panchangNotifications by viewModel.panchangNotifications.collectAsStateWithLifecycle()

    val dataCleared by viewModel.dataCleared.collectAsStateWithLifecycle()
    val spotifyLinked by viewModel.spotifyLinked.collectAsStateWithLifecycle()
    val spotifyConnectionStatus by viewModel.spotifyConnectionStatus.collectAsStateWithLifecycle()
    val spotifyInstalled by viewModel.spotifyInstalled.collectAsStateWithLifecycle()

    var showCityPicker by remember { mutableStateOf(false) }
    var showClearDataDialog by remember { mutableStateOf(false) }
    var nameInput by remember(userName) { mutableStateOf(userName) }
    var gotraInput by remember(gotra) { mutableStateOf(gotra) }
    var nakshatraDropdownExpanded by remember { mutableStateOf(false) }

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Column {
                        Text("సెట్టింగ్‌లు", style = MaterialTheme.typography.titleLarge, fontWeight = FontWeight.Bold)
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
            // ═══ Profile Section ═══
            SectionHeader(titleTelugu = "ప్రొఫైల్", titleEnglish = "Profile")
            GlassmorphicCard(cornerRadius = 16.dp, contentPadding = 16.dp) {
                Text("మీ పేరు", style = MaterialTheme.typography.labelMedium, color = TempleGold)
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
                    placeholder = { Text("భక్తా / Enter your name") },
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

            // ═══ Sankalpam Details ═══
            SectionHeader(titleTelugu = "సంకల్పం వివరాలు", titleEnglish = "Sankalpam Details")
            GlassmorphicCard(cornerRadius = 16.dp, contentPadding = 16.dp) {
                // Gotra field
                Text("గోత్రం", style = MaterialTheme.typography.labelMedium, color = TempleGold)
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
                    placeholder = { Text("గోత్రం / Enter your gotra") },
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
                Text("జన్మ నక్షత్రం", style = MaterialTheme.typography.labelMedium, color = TempleGold)
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
                        placeholder = { Text("నక్షత్రం ఎంచుకోండి / Select nakshatra") },
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

            // ═══ Appearance ═══
            SectionHeader(titleTelugu = "రూపం", titleEnglish = "Appearance")
            GlassmorphicCard(cornerRadius = 16.dp, contentPadding = 16.dp) {
                Text("థీమ్", style = MaterialTheme.typography.labelMedium, color = TempleGold)
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

            // ═══ Auto Dark Mode ═══
            GlassmorphicCard(cornerRadius = 16.dp, contentPadding = 16.dp) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically,
                ) {
                    Column {
                        Text("స్వయంచాలక డార్క్ మోడ్", style = MaterialTheme.typography.titleSmall, fontWeight = FontWeight.Medium)
                        Text("Auto Dark Mode · Sunrise/Sunset based", style = MaterialTheme.typography.labelSmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
                    }
                    Switch(
                        checked = autoDarkMode,
                        onCheckedChange = { viewModel.setAutoDarkMode(it) },
                        colors = SwitchDefaults.colors(checkedThumbColor = TempleGold),
                    )
                }
            }

            // ═══ Font Size ═══
            GlassmorphicCard(cornerRadius = 16.dp, contentPadding = 16.dp) {
                Text("అక్షరాల పరిమాణం", style = MaterialTheme.typography.labelMedium, color = TempleGold)
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
                        valueRange = 12f..28f,
                        steps = 7,
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

            // ═══ Location ═══
            SectionHeader(titleTelugu = "స్థానం", titleEnglish = "Location")
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
                        Text("పంచాంగం స్థానం", style = MaterialTheme.typography.labelMedium, color = TempleGold)
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

            // ═══ Notifications ═══
            SectionHeader(titleTelugu = "నోటిఫికేషన్లు", titleEnglish = "Notifications")
            GlassmorphicCard(cornerRadius = 16.dp, contentPadding = 16.dp) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically,
                ) {
                    Column {
                        Text("ఉదయం సుప్రభాతం", style = MaterialTheme.typography.titleSmall, fontWeight = FontWeight.Medium)
                        Text("Morning · 5:30 AM", style = MaterialTheme.typography.labelSmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
                    }
                    Switch(
                        checked = morningNotification,
                        onCheckedChange = { viewModel.setMorningNotification(it) },
                        colors = SwitchDefaults.colors(checkedThumbColor = TempleGold),
                    )
                }
                Spacer(Modifier.height(12.dp))
                HorizontalDivider(color = MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.3f))
                Spacer(Modifier.height(12.dp))
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically,
                ) {
                    Column {
                        Text("సాయంకాలం హారతి", style = MaterialTheme.typography.titleSmall, fontWeight = FontWeight.Medium)
                        Text("Evening · 6:30 PM", style = MaterialTheme.typography.labelSmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
                    }
                    Switch(
                        checked = eveningNotification,
                        onCheckedChange = { viewModel.setEveningNotification(it) },
                        colors = SwitchDefaults.colors(checkedThumbColor = TempleGold),
                    )
                }
                Spacer(Modifier.height(12.dp))
                HorizontalDivider(color = MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.3f))
                Spacer(Modifier.height(12.dp))
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically,
                ) {
                    Column {
                        Text("పంచాంగ గుర్తింపులు", style = MaterialTheme.typography.titleSmall, fontWeight = FontWeight.Medium)
                        Text("Panchang · Ekadashi, Purnima, Amavasya", style = MaterialTheme.typography.labelSmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
                    }
                    Switch(
                        checked = panchangNotifications,
                        onCheckedChange = { viewModel.setPanchangNotifications(it) },
                        colors = SwitchDefaults.colors(checkedThumbColor = TempleGold),
                    )
                }
            }

            // ═══ Music / Spotify ═══
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
                            spotifyConnectionStatus == SpotifyConnectionStatus.CONNECTING -> {
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
                                onClick = { viewModel.openSpotifyPlayStore() },
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
                                onClick = { viewModel.unlinkSpotify() },
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

            // ═══ Data & Privacy ═══
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
                        Text("గోప్యతా విధానం", style = MaterialTheme.typography.titleSmall, fontWeight = FontWeight.Medium)
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
                        Text("నా డేటాను తొలగించు", style = MaterialTheme.typography.titleSmall, fontWeight = FontWeight.Medium)
                        Text("Clear My Data", style = MaterialTheme.typography.labelSmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
                    }
                    Icon(Icons.Default.DeleteForever, null, tint = MaterialTheme.colorScheme.error)
                }
            }

            // ═══ About ═══
            SectionHeader(titleTelugu = "గురించి", titleEnglish = "About")
            GlassmorphicCard(cornerRadius = 16.dp, contentPadding = 20.dp) {
                Column(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalAlignment = Alignment.CenterHorizontally,
                ) {
                    Text(
                        "నిత్య పూజ",
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
                        "Version 1.1.0",
                        style = MaterialTheme.typography.labelSmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                    )
                    Spacer(Modifier.height(4.dp))
                    Text(
                        "మీ ఆధ్యాత్మిక సహచరుడు",
                        style = MaterialTheme.typography.bodySmall,
                        color = TempleGold,
                    )
                    Text(
                        "Your Spiritual Companion",
                        style = MaterialTheme.typography.labelSmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                    )
                    Spacer(Modifier.height(8.dp))
                    HorizontalDivider(color = MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.3f))
                    Spacer(Modifier.height(8.dp))
                    Text(
                        "nityapooja.contact@yahoo.com",
                        style = MaterialTheme.typography.labelSmall,
                        color = TempleGold,
                    )
                }
            }

            Spacer(Modifier.height(32.dp))
        }
    }

    // ═══ Clear Data Confirmation Dialog ═══
    if (showClearDataDialog) {
        AlertDialog(
            onDismissRequest = { showClearDataDialog = false },
            icon = { Icon(Icons.Default.DeleteForever, null, tint = MaterialTheme.colorScheme.error) },
            title = {
                Column {
                    Text("డేటాను తొలగించాలా?", style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold)
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

    // ═══ Data Cleared Snackbar ═══
    if (dataCleared) {
        LaunchedEffect(Unit) {
            delay(2000)
            viewModel.resetDataClearedFlag()
        }
    }

    // ═══ City Picker Dialog ═══
    if (showCityPicker) {
        CityPickerDialog(
            currentCity = locationCity,
            onDismiss = { showCityPicker = false },
            onCitySelected = { city ->
                viewModel.setLocation(city.name, city.lat, city.lng, city.timezone)
                showCityPicker = false
            },
        )
    }
}

@Composable
private fun CityPickerDialog(
    currentCity: String,
    onDismiss: () -> Unit,
    onCitySelected: (com.nityapooja.app.data.model.IndianCity) -> Unit,
) {
    var searchQuery by remember { mutableStateOf("") }
    val filteredCities = remember(searchQuery) {
        if (searchQuery.isBlank()) indianCities
        else indianCities.filter {
            it.name.contains(searchQuery, ignoreCase = true) ||
            it.nameTelugu.contains(searchQuery) ||
            it.state.contains(searchQuery, ignoreCase = true)
        }
    }

    AlertDialog(
        onDismissRequest = onDismiss,
        title = {
            Column {
                Text("నగరం ఎంచుకోండి", style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold)
                Text("Select City", style = MaterialTheme.typography.labelSmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
            }
        },
        text = {
            Column(modifier = Modifier.heightIn(max = 400.dp)) {
                OutlinedTextField(
                    value = searchQuery,
                    onValueChange = { searchQuery = it },
                    modifier = Modifier.fillMaxWidth(),
                    placeholder = { Text("Search city...") },
                    singleLine = true,
                    leadingIcon = { Icon(Icons.Default.Search, null) },
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedBorderColor = TempleGold,
                        cursorColor = TempleGold,
                    ),
                )
                Spacer(Modifier.height(8.dp))
                LazyColumn(
                    verticalArrangement = Arrangement.spacedBy(2.dp),
                ) {
                    items(filteredCities) { city ->
                        val isSelected = city.name == currentCity
                        ListItem(
                            headlineContent = {
                                Text(
                                    city.name,
                                    fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal,
                                    color = if (isSelected) TempleGold else MaterialTheme.colorScheme.onSurface,
                                )
                            },
                            supportingContent = {
                                Text(
                                    if (city.nameTelugu.isNotBlank()) "${city.nameTelugu} · ${city.state}" else city.state,
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
                                if (isSelected) {
                                    Icon(Icons.Default.Check, null, tint = TempleGold)
                                }
                            },
                            modifier = Modifier.clickable { onCitySelected(city) },
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
    SYSTEM(ThemeMode.SYSTEM, "సిస్టమ్", "System", Icons.Default.PhoneAndroid),
    LIGHT(ThemeMode.LIGHT, "లైట్", "Light", Icons.Default.LightMode),
    DARK(ThemeMode.DARK, "డార్క్", "Dark", Icons.Default.DarkMode),
    SAFFRON(ThemeMode.SAFFRON, "భగవా", "Saffron", Icons.Default.Whatshot),
}
