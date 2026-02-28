package com.nityapooja.shared.ui.puja

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
import androidx.compose.ui.unit.sp
import org.koin.compose.viewmodel.koinViewModel
import com.nityapooja.shared.ui.components.*
import com.nityapooja.shared.ui.panchangam.PanchangamViewModel
import com.nityapooja.shared.ui.theme.*

private data class PujaTypeInfo(
    val key: String,
    val nameTelugu: String,
    val nameEnglish: String,
    val icon: ImageVector,
)

private val pujaTypeInfoMap = mapOf(
    "ganesh" to PujaTypeInfo("ganesh", "\u0C17\u0C23\u0C47\u0C36 \u0C2A\u0C42\u0C1C", "Ganesh Puja", Icons.Default.Spa),
    "shiva" to PujaTypeInfo("shiva", "\u0C36\u0C3F\u0C35 \u0C2A\u0C42\u0C1C", "Shiva Puja", Icons.Default.Brightness7),
    "lakshmi" to PujaTypeInfo("lakshmi", "\u0C32\u0C15\u0C4D\u0C37\u0C4D\u0C2E\u0C40 \u0C2A\u0C42\u0C1C", "Lakshmi Puja", Icons.Default.Star),
    "vishnu" to PujaTypeInfo("vishnu", "\u0C35\u0C3F\u0C37\u0C4D\u0C23\u0C41 \u0C2A\u0C42\u0C1C", "Vishnu Puja", Icons.Default.FilterVintage),
    "saraswathi" to PujaTypeInfo("saraswathi", "\u0C38\u0C30\u0C38\u0C4D\u0C35\u0C24\u0C3F \u0C2A\u0C42\u0C1C", "Saraswathi Puja", Icons.Default.MenuBook),
    "durga" to PujaTypeInfo("durga", "\u0C26\u0C41\u0C30\u0C4D\u0C17\u0C3E \u0C2A\u0C42\u0C1C", "Durga Puja", Icons.Default.Shield),
    "hanuman" to PujaTypeInfo("hanuman", "\u0C39\u0C28\u0C41\u0C2E\u0C3E\u0C28\u0C4D \u0C2A\u0C42\u0C1C", "Hanuman Puja", Icons.Default.FitnessCenter),
    "satyanarayan" to PujaTypeInfo("satyanarayan", "\u0C38\u0C24\u0C4D\u0C2F\u0C28\u0C3E\u0C30\u0C3E\u0C2F\u0C23 \u0C2A\u0C42\u0C1C", "Satyanarayan Puja", Icons.Default.WbSunny),
    "general" to PujaTypeInfo("general", "\u0C38\u0C3E\u0C27\u0C3E\u0C30\u0C23 \u0C2A\u0C42\u0C1C", "General Puja", Icons.Default.VolunteerActivism),
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
    TierOption("quick", "\u0C36\u0C40\u0C18\u0C4D\u0C30", "Quick"),
    TierOption("standard", "\u0C2A\u0C4D\u0C30\u0C3E\u0C2E\u0C3E\u0C23\u0C3F\u0C15", "Standard"),
    TierOption("full", "\u0C2A\u0C42\u0C30\u0C4D\u0C24\u0C3F", "Full"),
)

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun GuidedPujaScreen(
    onBack: () -> Unit = {},
    viewModel: GuidedPujaViewModel = koinViewModel(),
) {
    val steps by viewModel.steps.collectAsState()
    val currentStepIndex by viewModel.currentStepIndex.collectAsState()
    val selectedTier by viewModel.selectedTier.collectAsState()

    val panchangamViewModel: PanchangamViewModel = koinViewModel()
    val locationInfo by panchangamViewModel.locationInfo.collectAsState()
    val panchangamData = remember(locationInfo) {
        panchangamViewModel.calculatePanchangam(locationInfo.lat, locationInfo.lng, locationInfo.timezone)
    }

    val fontSizeViewModel: FontSizeViewModel = koinViewModel()
    val fontSize by fontSizeViewModel.fontSize.collectAsState()
    val fontScale = fontSize / 16f

    val userPrefsViewModel: com.nityapooja.shared.ui.home.HomeViewModel = koinViewModel()
    val userName by userPrefsViewModel.userName.collectAsState()
    val userGotra by userPrefsViewModel.userGotra.collectAsState()
    val userNakshatra by userPrefsViewModel.userNakshatra.collectAsState()

    var selectedPujaType by remember { mutableStateOf<String?>(null) }
    var showTierWarning by remember { mutableStateOf(false) }
    val isStepMode = selectedPujaType != null && steps.isNotEmpty()

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Column {
                        Text(
                            "\u0C2A\u0C42\u0C1C\u0C3E \u0C35\u0C3F\u0C27\u0C3E\u0C28\u0C02",
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
                    timezone = locationInfo.timezone,
                    pujaType = selectedPujaType,
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(padding),
                )
            } else {
                PujaSelectionContent(
                    pujaTypes = pujaTypeInfoMap.keys.toList(),
                    selectedTier = selectedTier,
                    onTierSelected = {
                        showTierWarning = false
                        viewModel.setTier(it)
                    },
                    onPujaSelected = { pujaType ->
                        if (selectedTier !in tierOptions.map { it.key }) {
                            showTierWarning = true
                        } else {
                            selectedPujaType = pujaType
                            viewModel.loadSteps(pujaType, selectedTier)
                        }
                    },
                    showTierWarning = showTierWarning,
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
    showTierWarning: Boolean = false,
    modifier: Modifier = Modifier,
) {
    Column(
        modifier = modifier.padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp),
    ) {
        // Tier Selector
        SectionHeader(titleTelugu = "\u0C2A\u0C42\u0C1C\u0C3E \u0C38\u0C4D\u0C25\u0C3E\u0C2F\u0C3F", titleEnglish = "Puja Tier")
        if (showTierWarning) {
            Card(
                shape = androidx.compose.foundation.shape.RoundedCornerShape(8.dp),
                colors = CardDefaults.cardColors(containerColor = WarningAmber.copy(alpha = 0.15f)),
            ) {
                Row(
                    modifier = Modifier.padding(12.dp),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(8.dp),
                ) {
                    Icon(
                        Icons.Default.Info,
                        contentDescription = null,
                        tint = WarningAmber,
                        modifier = Modifier.size(20.dp),
                    )
                    Text(
                        "దయచేసి పూజా స్థాయిని ఎంచుకోండి / Please select a puja tier first",
                        style = MaterialTheme.typography.bodySmall,
                        color = WarningAmber,
                    )
                }
            }
        }
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
        SectionHeader(titleTelugu = "\u0C2A\u0C42\u0C1C \u0C0E\u0C02\u0C1A\u0C41\u0C15\u0C4B\u0C02\u0C21\u0C3F", titleEnglish = "Select Puja")
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
    steps: List<com.nityapooja.shared.data.local.entity.PujaStepEntity>,
    currentStepIndex: Int,
    onNext: () -> Unit,
    onPrevious: () -> Unit,
    panchangamData: com.nityapooja.shared.ui.panchangam.PanchangamData,
    userName: String,
    gotra: String,
    userNakshatra: String,
    city: String,
    fontScale: Float,
    timezone: String = "Asia/Kolkata",
    pujaType: String? = null,
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
                timezone = timezone,
                pujaType = pujaType,
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
                "\u0C38\u0C42\u0C1A\u0C28\u0C32\u0C41 \u00B7 INSTRUCTIONS",
                style = NityaPoojaTextStyles.GoldLabel,
                color = TempleGold,
            )
            Spacer(Modifier.height(8.dp))
            Text(
                currentStep.instructionTelugu,
                style = MaterialTheme.typography.bodyMedium.copy(
                    fontSize = (16 * fontScale).sp,
                    lineHeight = (26 * fontScale).sp,
                ),
                color = MaterialTheme.colorScheme.onSurface,
            )
            Spacer(Modifier.height(4.dp))
            Text(
                currentStep.instruction,
                style = MaterialTheme.typography.bodySmall.copy(
                    fontSize = (14 * fontScale).sp,
                    lineHeight = (22 * fontScale).sp,
                ),
                color = MaterialTheme.colorScheme.onSurfaceVariant,
            )
        }

        // Items Needed
        if (!currentStep.itemsNeeded.isNullOrBlank()) {
            GlassmorphicCard(cornerRadius = 16.dp, contentPadding = 16.dp) {
                Text(
                    "\u0C38\u0C3E\u0C2E\u0C17\u0C4D\u0C30\u0C3F \u00B7 ITEMS NEEDED",
                    style = NityaPoojaTextStyles.GoldLabel,
                    color = TempleGold,
                )
                Spacer(Modifier.height(8.dp))
                if (!currentStep.itemsNeededTelugu.isNullOrBlank()) {
                    Text(
                        currentStep.itemsNeededTelugu,
                        style = MaterialTheme.typography.bodyMedium.copy(
                            fontSize = (16 * fontScale).sp,
                            lineHeight = (26 * fontScale).sp,
                        ),
                        color = MaterialTheme.colorScheme.onSurface,
                    )
                    Spacer(Modifier.height(4.dp))
                }
                Text(
                    currentStep.itemsNeeded,
                    style = MaterialTheme.typography.bodySmall.copy(
                        fontSize = (14 * fontScale).sp,
                        lineHeight = (22 * fontScale).sp,
                    ),
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
                    "\u0C2E\u0C02\u0C24\u0C4D\u0C30\u0C02 \u00B7 MANTRA",
                    style = NityaPoojaTextStyles.GoldLabel,
                    color = TempleGold,
                )
                Spacer(Modifier.height(8.dp))
                if (!currentStep.mantraTelugu.isNullOrBlank()) {
                    Text(
                        currentStep.mantraTelugu,
                        style = MaterialTheme.typography.bodyLarge.copy(
                            fontSize = (18 * fontScale).sp,
                            lineHeight = (30 * fontScale).sp,
                        ),
                        fontWeight = FontWeight.Medium,
                        color = TempleGold,
                    )
                    Spacer(Modifier.height(4.dp))
                }
                Text(
                    currentStep.mantra,
                    style = MaterialTheme.typography.bodyMedium.copy(
                        fontSize = (16 * fontScale).sp,
                        lineHeight = (26 * fontScale).sp,
                    ),
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
