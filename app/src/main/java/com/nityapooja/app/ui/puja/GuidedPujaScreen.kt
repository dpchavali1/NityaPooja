package com.nityapooja.app.ui.puja

import androidx.compose.animation.*
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.automirrored.filled.ArrowForward
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.nityapooja.app.ui.components.*
import com.nityapooja.app.ui.panchangam.PanchangamViewModel
import com.nityapooja.app.ui.theme.*

private data class PujaTypeInfo(
    val key: String,
    val nameTelugu: String,
    val nameEnglish: String,
    val icon: ImageVector,
)

private val pujaTypeInfoMap = mapOf(
    "ganesh" to PujaTypeInfo("ganesh", "గణేశ పూజ", "Ganesh Puja", Icons.Default.Spa),
    "shiva" to PujaTypeInfo("shiva", "శివ పూజ", "Shiva Puja", Icons.Default.Brightness7),
    "lakshmi" to PujaTypeInfo("lakshmi", "లక్ష్మీ పూజ", "Lakshmi Puja", Icons.Default.Star),
    "vishnu" to PujaTypeInfo("vishnu", "విష్ణు పూజ", "Vishnu Puja", Icons.Default.FilterVintage),
    "saraswathi" to PujaTypeInfo("saraswathi", "సరస్వతి పూజ", "Saraswathi Puja", Icons.Default.MenuBook),
    "durga" to PujaTypeInfo("durga", "దుర్గా పూజ", "Durga Puja", Icons.Default.Shield),
    "hanuman" to PujaTypeInfo("hanuman", "హనుమాన్ పూజ", "Hanuman Puja", Icons.Default.FitnessCenter),
    "satyanarayan" to PujaTypeInfo("satyanarayan", "సత్యనారాయణ పూజ", "Satyanarayan Puja", Icons.Default.WbSunny),
    "general" to PujaTypeInfo("general", "సాధారణ పూజ", "General Puja", Icons.Default.VolunteerActivism),
)

private fun getPujaInfo(key: String): PujaTypeInfo {
    return pujaTypeInfoMap[key] ?: PujaTypeInfo(
        key = key,
        nameTelugu = key,
        nameEnglish = key.replaceFirstChar { it.uppercase() } + " Puja",
        icon = Icons.Default.VolunteerActivism,
    )
}

private data class TierOption(
    val key: String,
    val labelTelugu: String,
    val labelEnglish: String,
)

private val tierOptions = listOf(
    TierOption("quick", "శీఘ్ర", "Quick"),
    TierOption("standard", "ప్రామాణిక", "Standard"),
    TierOption("full", "పూర్తి", "Full"),
)

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun GuidedPujaScreen(
    onBack: () -> Unit = {},
    viewModel: GuidedPujaViewModel = hiltViewModel(),
) {
    val steps by viewModel.steps.collectAsStateWithLifecycle()
    val currentStepIndex by viewModel.currentStepIndex.collectAsStateWithLifecycle()
    val selectedTier by viewModel.selectedTier.collectAsStateWithLifecycle()

    val panchangamViewModel: PanchangamViewModel = hiltViewModel()
    val locationInfo by panchangamViewModel.locationInfo.collectAsStateWithLifecycle()
    val panchangamData = remember(locationInfo) {
        panchangamViewModel.calculatePanchangam(locationInfo.lat, locationInfo.lng, locationInfo.timezone)
    }

    val fontSizeViewModel: FontSizeViewModel = hiltViewModel()
    val fontSize by fontSizeViewModel.fontSize.collectAsStateWithLifecycle()
    val fontScale = fontSize / 16f

    val userPrefsViewModel: com.nityapooja.app.ui.home.HomeViewModel = hiltViewModel()
    val userName by userPrefsViewModel.userName.collectAsStateWithLifecycle()
    val userGotra by userPrefsViewModel.userGotra.collectAsStateWithLifecycle()
    val userNakshatra by userPrefsViewModel.userNakshatra.collectAsStateWithLifecycle()

    var selectedPujaType by remember { mutableStateOf<String?>(null) }
    val isStepMode = selectedPujaType != null && steps.isNotEmpty()

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Column {
                        Text(
                            "పూజా విధానం",
                            style = MaterialTheme.typography.titleLarge,
                            fontWeight = FontWeight.Bold,
                        )
                        Text(
                            "Guided Puja",
                            style = MaterialTheme.typography.labelSmall,
                            color = MaterialTheme.colorScheme.onSurfaceVariant,
                        )
                    }
                },
                navigationIcon = {
                    IconButton(onClick = {
                        if (isStepMode) {
                            selectedPujaType = null
                        } else {
                            onBack()
                        }
                    }) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, "Back")
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.surface,
                ),
            )
        }
    ) { padding ->
        AnimatedContent(
            targetState = isStepMode,
            transitionSpec = {
                slideInHorizontally { it } + fadeIn() togetherWith
                    slideOutHorizontally { -it } + fadeOut()
            },
            label = "pujaMode",
        ) { showSteps ->
            if (showSteps) {
                PujaStepContent(
                    steps = steps,
                    currentStepIndex = currentStepIndex,
                    onNext = { viewModel.nextStep() },
                    onPrevious = { viewModel.previousStep() },
                    panchangamData = panchangamData,
                    userName = userName,
                    gotra = userGotra,
                    userNakshatra = userNakshatra,
                    city = locationInfo.city,
                    fontScale = fontScale,
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(padding),
                )
            } else {
                PujaSelectionContent(
                    pujaTypes = pujaTypeInfoMap.keys.toList(),
                    selectedTier = selectedTier,
                    onTierSelected = { viewModel.setTier(it) },
                    onPujaSelected = { pujaType ->
                        selectedPujaType = pujaType
                        viewModel.loadSteps(pujaType, selectedTier)
                    },
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(padding),
                )
            }
        }
    }
}

@Composable
private fun PujaSelectionContent(
    pujaTypes: List<String>,
    selectedTier: String,
    onTierSelected: (String) -> Unit,
    onPujaSelected: (String) -> Unit,
    modifier: Modifier = Modifier,
) {
    Column(
        modifier = modifier.padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp),
    ) {
        // Tier Selector
        SectionHeader(titleTelugu = "పూజా స్థాయి", titleEnglish = "Puja Tier")
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(8.dp),
        ) {
            tierOptions.forEach { option ->
                val isSelected = selectedTier == option.key
                FilterChip(
                    selected = isSelected,
                    onClick = { onTierSelected(option.key) },
                    label = {
                        Column(horizontalAlignment = Alignment.CenterHorizontally) {
                            Text(option.labelTelugu, style = MaterialTheme.typography.labelSmall)
                            Text(option.labelEnglish, style = MaterialTheme.typography.labelSmall)
                        }
                    },
                    colors = FilterChipDefaults.filterChipColors(
                        selectedContainerColor = TempleGold.copy(alpha = 0.15f),
                        selectedLabelColor = TempleGold,
                    ),
                    modifier = Modifier.weight(1f),
                )
            }
        }

        // Puja Types Grid
        SectionHeader(titleTelugu = "పూజ ఎంచుకోండి", titleEnglish = "Select Puja")
        LazyVerticalGrid(
            columns = GridCells.Fixed(2),
            verticalArrangement = Arrangement.spacedBy(12.dp),
            horizontalArrangement = Arrangement.spacedBy(12.dp),
            modifier = Modifier.fillMaxSize(),
        ) {
            items(pujaTypes) { pujaType ->
                val info = getPujaInfo(pujaType)
                GlassmorphicCard(
                    cornerRadius = 16.dp,
                    contentPadding = 16.dp,
                    onClick = { onPujaSelected(pujaType) },
                ) {
                    Column(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalAlignment = Alignment.CenterHorizontally,
                    ) {
                        Icon(
                            info.icon,
                            contentDescription = null,
                            tint = TempleGold,
                            modifier = Modifier.size(36.dp),
                        )
                        Spacer(Modifier.height(8.dp))
                        Text(
                            info.nameTelugu,
                            style = MaterialTheme.typography.titleSmall,
                            fontWeight = FontWeight.Bold,
                            textAlign = TextAlign.Center,
                            color = TempleGold,
                        )
                        Text(
                            info.nameEnglish,
                            style = MaterialTheme.typography.labelSmall,
                            textAlign = TextAlign.Center,
                            color = MaterialTheme.colorScheme.onSurfaceVariant,
                        )
                    }
                }
            }
        }
    }
}

@Composable
private fun PujaStepContent(
    steps: List<com.nityapooja.app.data.local.entity.PujaStepEntity>,
    currentStepIndex: Int,
    onNext: () -> Unit,
    onPrevious: () -> Unit,
    panchangamData: com.nityapooja.app.ui.panchangam.PanchangamData,
    userName: String,
    gotra: String,
    userNakshatra: String,
    city: String,
    fontScale: Float,
    modifier: Modifier = Modifier,
) {
    val currentStep = steps.getOrNull(currentStepIndex) ?: return
    val totalSteps = steps.size
    val progress = if (totalSteps > 0) (currentStepIndex + 1).toFloat() / totalSteps else 0f

    Column(
        modifier = modifier
            .verticalScroll(rememberScrollState())
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp),
    ) {
        // Sankalpam — show only before step 1
        if (currentStepIndex == 0) {
            SankalpamCard(
                panchangamData = panchangamData,
                userName = userName,
                gotra = gotra,
                userNakshatra = userNakshatra,
                city = city,
                fontScale = fontScale,
            )
        }

        // Step Counter
        GlassmorphicCard(cornerRadius = 16.dp, contentPadding = 16.dp) {
            Column(modifier = Modifier.fillMaxWidth()) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically,
                ) {
                    Text(
                        "Step ${currentStepIndex + 1} of $totalSteps",
                        style = MaterialTheme.typography.titleSmall,
                        fontWeight = FontWeight.Medium,
                        color = TempleGold,
                    )
                    Text(
                        "${((progress) * 100).toInt()}%",
                        style = MaterialTheme.typography.labelSmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                    )
                }
                Spacer(Modifier.height(8.dp))
                LinearProgressIndicator(
                    progress = { progress },
                    modifier = Modifier.fillMaxWidth(),
                    color = TempleGold,
                    trackColor = MaterialTheme.colorScheme.surfaceVariant,
                )
            }
        }

        // Step Title
        SectionHeader(
            titleTelugu = currentStep.titleTelugu,
            titleEnglish = currentStep.title,
        )

        // Instruction
        GlassmorphicCard(cornerRadius = 16.dp, contentPadding = 16.dp) {
            Text(
                "సూచనలు · INSTRUCTIONS",
                style = NityaPoojaTextStyles.GoldLabel,
                color = TempleGold,
            )
            Spacer(Modifier.height(8.dp))
            Text(
                currentStep.instructionTelugu,
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurface,
            )
            Spacer(Modifier.height(4.dp))
            Text(
                currentStep.instruction,
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
            )
        }

        // Items Needed
        if (!currentStep.itemsNeeded.isNullOrBlank()) {
            GlassmorphicCard(cornerRadius = 16.dp, contentPadding = 16.dp) {
                Text(
                    "సామగ్రి · ITEMS NEEDED",
                    style = NityaPoojaTextStyles.GoldLabel,
                    color = TempleGold,
                )
                Spacer(Modifier.height(8.dp))
                if (!currentStep.itemsNeededTelugu.isNullOrBlank()) {
                    Text(
                        currentStep.itemsNeededTelugu,
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.onSurface,
                    )
                    Spacer(Modifier.height(4.dp))
                }
                Text(
                    currentStep.itemsNeeded,
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                )
            }
        }

        // Mantra
        if (!currentStep.mantra.isNullOrBlank()) {
            GlassmorphicCard(
                cornerRadius = 16.dp,
                contentPadding = 16.dp,
                accentColor = TempleGold,
            ) {
                Text(
                    "మంత్రం · MANTRA",
                    style = NityaPoojaTextStyles.GoldLabel,
                    color = TempleGold,
                )
                Spacer(Modifier.height(8.dp))
                if (!currentStep.mantraTelugu.isNullOrBlank()) {
                    Text(
                        currentStep.mantraTelugu,
                        style = MaterialTheme.typography.bodyLarge,
                        fontWeight = FontWeight.Medium,
                        color = TempleGold,
                    )
                    Spacer(Modifier.height(4.dp))
                }
                Text(
                    currentStep.mantra,
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                )
            }
        }

        // Navigation Buttons
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(12.dp),
        ) {
            OutlinedButton(
                onClick = onPrevious,
                enabled = currentStepIndex > 0,
                modifier = Modifier.weight(1f),
                colors = ButtonDefaults.outlinedButtonColors(
                    contentColor = TempleGold,
                ),
            ) {
                Icon(
                    Icons.AutoMirrored.Filled.ArrowBack,
                    contentDescription = null,
                    modifier = Modifier.size(18.dp),
                )
                Spacer(Modifier.width(4.dp))
                Text("Previous")
            }
            Button(
                onClick = onNext,
                enabled = currentStepIndex < steps.size - 1,
                modifier = Modifier.weight(1f),
                colors = ButtonDefaults.buttonColors(
                    containerColor = TempleGold,
                ),
            ) {
                Text("Next")
                Spacer(Modifier.width(4.dp))
                Icon(
                    Icons.AutoMirrored.Filled.ArrowForward,
                    contentDescription = null,
                    modifier = Modifier.size(18.dp),
                )
            }
        }

        Spacer(Modifier.height(60.dp))
    }
}
