package com.nityapooja.app.ui.onboarding

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
import androidx.hilt.navigation.compose.hiltViewModel
import com.nityapooja.app.data.model.IndianCity
import com.nityapooja.app.data.model.indianCities
import com.nityapooja.app.ui.components.GlassmorphicCard
import com.nityapooja.app.ui.components.GoldGradientButton
import com.nityapooja.app.ui.theme.NityaPoojaTextStyles
import com.nityapooja.app.ui.theme.TempleGold
import kotlinx.coroutines.launch

@OptIn(ExperimentalFoundationApi::class)
@Composable
fun OnboardingScreen(
    onComplete: () -> Unit,
    viewModel: OnboardingViewModel = hiltViewModel(),
) {
    val pagerState = rememberPagerState(pageCount = { 3 })
    val scope = rememberCoroutineScope()

    var userName by remember { mutableStateOf("") }
    var selectedCity by remember { mutableStateOf(indianCities.first()) }
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
                        selectedCity = selectedCity,
                        onCitySelected = { selectedCity = it },
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
                                    city = selectedCity.name,
                                    lat = selectedCity.lat,
                                    lng = selectedCity.lng,
                                    timezone = selectedCity.timezone,
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
            "🙏",
            fontSize = 64.sp,
        )
        Spacer(Modifier.height(24.dp))
        Text(
            "నిత్య పూజ",
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
            "మీ ఆధ్యాత్మిక సహచరుడు",
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
            placeholder = { Text("మీ పేరు / Your Name") },
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
    selectedCity: IndianCity,
    onCitySelected: (IndianCity) -> Unit,
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

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(horizontal = 24.dp),
    ) {
        Spacer(Modifier.height(16.dp))
        Text(
            "నగరం ఎంచుకోండి",
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
        Spacer(Modifier.height(16.dp))
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
        Spacer(Modifier.height(12.dp))
        LazyColumn(
            verticalArrangement = Arrangement.spacedBy(2.dp),
        ) {
            items(filteredCities) { city ->
                val isSelected = city.name == selectedCity.name
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
            "రిమైండర్లు",
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
                        "ఉదయం సుప్రభాతం",
                        style = MaterialTheme.typography.titleSmall,
                        fontWeight = FontWeight.Medium,
                    )
                    Text(
                        "Morning Suprabhatam · 5:30 AM",
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
                        "సాయంకాలం హారతి",
                        style = MaterialTheme.typography.titleSmall,
                        fontWeight = FontWeight.Medium,
                    )
                    Text(
                        "Evening Aarti · 6:30 PM",
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
