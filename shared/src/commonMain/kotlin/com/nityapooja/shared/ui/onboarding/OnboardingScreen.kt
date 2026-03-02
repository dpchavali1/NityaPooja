package com.nityapooja.shared.ui.onboarding

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import org.koin.compose.viewmodel.koinViewModel
import com.nityapooja.shared.ui.components.GlassmorphicCard
import com.nityapooja.shared.ui.components.PlaceResult
import com.nityapooja.shared.ui.components.searchPlaces
import kotlinx.coroutines.Job
import com.nityapooja.shared.ui.components.GoldGradientButton
import com.nityapooja.shared.ui.theme.NityaPoojaTextStyles
import com.nityapooja.shared.ui.theme.TempleGold
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

@OptIn(ExperimentalFoundationApi::class)
@Composable
fun OnboardingScreen(
    onComplete: () -> Unit,
    viewModel: OnboardingViewModel = koinViewModel(),
) {
    val pagerState = rememberPagerState(pageCount = { 3 })
    val scope = rememberCoroutineScope()

    var userName by remember { mutableStateOf("") }
    var selectedCityName by remember { mutableStateOf("Hyderabad") }
    var selectedCityLat by remember { mutableStateOf(17.385) }
    var selectedCityLng by remember { mutableStateOf(78.4867) }
    var selectedCityTimezone by remember { mutableStateOf("Asia/Kolkata") }
    var morningNotification by remember { mutableStateOf(true) }
    var eveningNotification by remember { mutableStateOf(true) }

    Scaffold { padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding),
        ) {
            // Page indicator
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp),
                horizontalArrangement = Arrangement.Center,
            ) {
                repeat(3) { index ->
                    val isActive = index == pagerState.currentPage
                    Box(
                        modifier = Modifier
                            .padding(horizontal = 4.dp)
                            .size(if (isActive) 10.dp else 8.dp)
                            .then(
                                Modifier
                                    .statusBarsPadding()
                            ),
                    ) {
                        Surface(
                            shape = MaterialTheme.shapes.small,
                            color = if (isActive) TempleGold else MaterialTheme.colorScheme.outlineVariant,
                            modifier = Modifier.fillMaxSize(),
                        ) {}
                    }
                }
            }

            HorizontalPager(
                state = pagerState,
                modifier = Modifier.weight(1f),
            ) { page ->
                when (page) {
                    0 -> WelcomePage(
                        userName = userName,
                        onUserNameChange = { userName = it },
                    )
                    1 -> CitySelectionPage(
                        selectedCityName = selectedCityName,
                        onCitySelected = { name, lat, lng, timezone ->
                            selectedCityName = name
                            selectedCityLat = lat
                            selectedCityLng = lng
                            selectedCityTimezone = timezone
                        },
                    )
                    2 -> RemindersPage(
                        morningEnabled = morningNotification,
                        eveningEnabled = eveningNotification,
                        onMorningChange = { morningNotification = it },
                        onEveningChange = { eveningNotification = it },
                    )
                }
            }

            // Bottom navigation buttons
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 24.dp, vertical = 16.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically,
            ) {
                if (pagerState.currentPage > 0) {
                    TextButton(onClick = {
                        scope.launch { pagerState.animateScrollToPage(pagerState.currentPage - 1) }
                    }) {
                        Text("Back", color = MaterialTheme.colorScheme.onSurfaceVariant)
                    }
                } else {
                    Spacer(Modifier.width(64.dp))
                }

                if (pagerState.currentPage < 2) {
                    GoldGradientButton(
                        text = "Next",
                        onClick = {
                            scope.launch { pagerState.animateScrollToPage(pagerState.currentPage + 1) }
                        },
                        modifier = Modifier.width(120.dp),
                    )
                } else {
                    GoldGradientButton(
                        text = "Get Started",
                        onClick = {
                            scope.launch {
                                viewModel.completeOnboarding(
                                    userName = userName,
                                    city = selectedCityName,
                                    lat = selectedCityLat,
                                    lng = selectedCityLng,
                                    timezone = selectedCityTimezone,
                                    morningNotification = morningNotification,
                                    eveningNotification = eveningNotification,
                                )
                                onComplete()
                            }
                        },
                        modifier = Modifier.width(160.dp),
                    )
                }
            }

            Spacer(Modifier.height(16.dp))
        }
    }
}

@Composable
private fun WelcomePage(
    userName: String,
    onUserNameChange: (String) -> Unit,
) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(32.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center,
    ) {
        Text(
            "\uD83D\uDE4F",
            fontSize = 64.sp,
        )
        Spacer(Modifier.height(24.dp))
        Text(
            "\u0C28\u0C3F\u0C24\u0C4D\u0C2F \u0C2A\u0C42\u0C1C",
            style = NityaPoojaTextStyles.TeluguDisplay,
            color = TempleGold,
            textAlign = TextAlign.Center,
        )
        Text(
            "NityaPooja",
            style = MaterialTheme.typography.headlineMedium,
            fontWeight = FontWeight.Bold,
            color = MaterialTheme.colorScheme.onSurface,
        )
        Spacer(Modifier.height(8.dp))
        Text(
            "\u0C2E\u0C40 \u0C06\u0C27\u0C4D\u0C2F\u0C3E\u0C24\u0C4D\u0C2E\u0C3F\u0C15 \u0C38\u0C39\u0C1A\u0C30\u0C41\u0C21\u0C41",
            style = MaterialTheme.typography.bodyMedium,
            color = TempleGold,
            textAlign = TextAlign.Center,
        )
        Text(
            "Your Spiritual Companion",
            style = MaterialTheme.typography.bodySmall,
            color = MaterialTheme.colorScheme.onSurfaceVariant,
            textAlign = TextAlign.Center,
        )
        Spacer(Modifier.height(40.dp))
        OutlinedTextField(
            value = userName,
            onValueChange = onUserNameChange,
            modifier = Modifier.fillMaxWidth(),
            placeholder = { Text("\u0C2E\u0C40 \u0C2A\u0C47\u0C30\u0C41 / Your Name") },
            singleLine = true,
            leadingIcon = { Icon(Icons.Default.Person, null, tint = TempleGold) },
            colors = OutlinedTextFieldDefaults.colors(
                focusedBorderColor = TempleGold,
                cursorColor = TempleGold,
            ),
        )
    }
}

@Composable
private fun CitySelectionPage(
    selectedCityName: String,
    onCitySelected: (name: String, lat: Double, lng: Double, timezone: String) -> Unit,
) {
    var searchQuery by remember { mutableStateOf("") }
    var suggestions by remember { mutableStateOf<List<PlaceResult>>(emptyList()) }
    var isLoading by remember { mutableStateOf(false) }
    var searchJob by remember { mutableStateOf<Job?>(null) }
    val scope = rememberCoroutineScope()

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(horizontal = 24.dp),
    ) {
        Spacer(Modifier.height(16.dp))
        Text(
            "\u0C28\u0C17\u0C30\u0C02 \u0C0E\u0C02\u0C1A\u0C41\u0C15\u0C4B\u0C02\u0C21\u0C3F",
            style = MaterialTheme.typography.titleLarge,
            fontWeight = FontWeight.Bold,
            color = TempleGold,
        )
        Text(
            "Select Your City",
            style = MaterialTheme.typography.bodyMedium,
            color = MaterialTheme.colorScheme.onSurfaceVariant,
        )
        Text(
            "For accurate Panchangam calculations",
            style = MaterialTheme.typography.labelSmall,
            color = MaterialTheme.colorScheme.onSurfaceVariant,
        )
        Spacer(Modifier.height(8.dp))
        Text(
            "\u0C2E\u0C40 \u0C28\u0C17\u0C30\u0C02: $selectedCityName",
            style = MaterialTheme.typography.bodySmall,
            color = TempleGold,
        )
        Spacer(Modifier.height(12.dp))
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
                            "Search any city worldwide — type to begin"
                        else
                            "No results found · Try a different spelling",
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                        modifier = Modifier.padding(vertical = 8.dp),
                    )
                }
            }
            items(suggestions) { place ->
                val isSelected = place.displayName == selectedCityName
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
}

@Composable
private fun RemindersPage(
    morningEnabled: Boolean,
    eveningEnabled: Boolean,
    onMorningChange: (Boolean) -> Unit,
    onEveningChange: (Boolean) -> Unit,
) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(32.dp),
        verticalArrangement = Arrangement.Center,
    ) {
        Text(
            "\u0C30\u0C3F\u0C2E\u0C48\u0C02\u0C21\u0C30\u0C4D\u0C32\u0C41",
            style = MaterialTheme.typography.titleLarge,
            fontWeight = FontWeight.Bold,
            color = TempleGold,
        )
        Text(
            "Daily Reminders",
            style = MaterialTheme.typography.bodyMedium,
            color = MaterialTheme.colorScheme.onSurfaceVariant,
        )
        Text(
            "Stay connected with your daily prayers",
            style = MaterialTheme.typography.labelSmall,
            color = MaterialTheme.colorScheme.onSurfaceVariant,
        )
        Spacer(Modifier.height(32.dp))

        GlassmorphicCard(cornerRadius = 16.dp, contentPadding = 16.dp) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically,
            ) {
                Column {
                    Text(
                        "\u0C09\u0C26\u0C2F\u0C02 \u0C38\u0C41\u0C2A\u0C4D\u0C30\u0C2D\u0C3E\u0C24\u0C02",
                        style = MaterialTheme.typography.titleSmall,
                        fontWeight = FontWeight.Medium,
                    )
                    Text(
                        "Morning Suprabhatam \u00B7 5:30 AM",
                        style = MaterialTheme.typography.labelSmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                    )
                }
                Switch(
                    checked = morningEnabled,
                    onCheckedChange = onMorningChange,
                    colors = SwitchDefaults.colors(checkedThumbColor = TempleGold),
                )
            }
            Spacer(Modifier.height(16.dp))
            HorizontalDivider(color = MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.3f))
            Spacer(Modifier.height(16.dp))
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically,
            ) {
                Column {
                    Text(
                        "\u0C38\u0C3E\u0C2F\u0C02\u0C15\u0C3E\u0C32\u0C02 \u0C39\u0C3E\u0C30\u0C24\u0C3F",
                        style = MaterialTheme.typography.titleSmall,
                        fontWeight = FontWeight.Medium,
                    )
                    Text(
                        "Evening Aarti \u00B7 6:30 PM",
                        style = MaterialTheme.typography.labelSmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                    )
                }
                Switch(
                    checked = eveningEnabled,
                    onCheckedChange = onEveningChange,
                    colors = SwitchDefaults.colors(checkedThumbColor = TempleGold),
                )
            }
        }
    }
}
