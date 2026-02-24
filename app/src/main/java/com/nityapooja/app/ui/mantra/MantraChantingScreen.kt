package com.nityapooja.app.ui.mantra

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalView
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.ViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewModelScope
import com.nityapooja.app.data.repository.DevotionalRepository
import com.nityapooja.app.ui.theme.TempleGold
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

data class ChantingContent(
    val title: String = "",
    val text: String = "",
)

@HiltViewModel
class MantraChantingViewModel @Inject constructor(
    private val repository: DevotionalRepository,
) : ViewModel() {

    private val _content = MutableStateFlow(ChantingContent())
    val content: StateFlow<ChantingContent> = _content.asStateFlow()

    fun loadContent(id: Int, type: String) {
        viewModelScope.launch {
            when (type) {
                "mantra" -> repository.getMantraById(id).collect { mantra ->
                    mantra?.let {
                        _content.value = ChantingContent(
                            title = it.titleTelugu,
                            text = it.sanskrit ?: "",
                        )
                    }
                }
                "stotram" -> repository.getStotramById(id).collect { stotram ->
                    stotram?.let {
                        _content.value = ChantingContent(
                            title = it.titleTelugu,
                            text = it.textTelugu ?: it.textSanskrit ?: "",
                        )
                    }
                }
                "chalisa" -> repository.getChalisaById(id).collect { chalisa ->
                    chalisa?.let {
                        _content.value = ChantingContent(
                            title = it.titleTelugu,
                            text = buildString {
                                it.dohaTelugu?.let { d -> append(d); append("\n\n") }
                                it.chaupaiTelugu?.let { c -> append(c) }
                            },
                        )
                    }
                }
            }
        }
    }
}

@Composable
fun MantraChantingScreen(
    contentId: Int,
    contentType: String,
    onBack: () -> Unit,
    viewModel: MantraChantingViewModel = hiltViewModel(),
) {
    val view = LocalView.current
    val content by viewModel.content.collectAsStateWithLifecycle()

    // Load content
    LaunchedEffect(contentId, contentType) {
        viewModel.loadContent(contentId, contentType)
    }

    // Keep screen on
    DisposableEffect(Unit) {
        view.keepScreenOn = true
        onDispose { view.keepScreenOn = false }
    }

    // Auto-scroll
    var scrollSpeed by remember { mutableFloatStateOf(1f) }
    val scrollState = rememberScrollState()

    LaunchedEffect(scrollSpeed) {
        if (scrollSpeed > 0f) {
            while (true) {
                delay(50)
                val newValue = scrollState.value + (scrollSpeed * 1.5f).toInt()
                if (newValue <= scrollState.maxValue) {
                    scrollState.scrollTo(newValue)
                } else {
                    break
                }
            }
        }
    }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(Color(0xFF1A0A00)),
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(top = 48.dp, bottom = 100.dp)
                .verticalScroll(scrollState)
                .padding(horizontal = 24.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
        ) {
            Spacer(Modifier.height(32.dp))

            Text(
                content.title,
                style = MaterialTheme.typography.headlineSmall,
                fontWeight = FontWeight.Bold,
                color = TempleGold,
                textAlign = TextAlign.Center,
            )
            Spacer(Modifier.height(32.dp))

            Text(
                content.text,
                style = MaterialTheme.typography.bodyLarge.copy(
                    fontSize = 24.sp,
                    lineHeight = 44.sp,
                ),
                fontWeight = FontWeight.Medium,
                color = Color(0xFFF5E6CC),
                textAlign = TextAlign.Center,
            )

            Spacer(Modifier.height(120.dp))
        }

        // Close button
        IconButton(
            onClick = onBack,
            modifier = Modifier
                .align(Alignment.TopEnd)
                .padding(16.dp)
                .statusBarsPadding(),
        ) {
            Icon(
                Icons.Default.Close,
                contentDescription = "Close",
                tint = Color.White.copy(alpha = 0.7f),
                modifier = Modifier.size(28.dp),
            )
        }

        // Speed controls
        Surface(
            modifier = Modifier
                .align(Alignment.BottomCenter)
                .fillMaxWidth(),
            color = Color(0xFF1A0A00).copy(alpha = 0.95f),
        ) {
            Column(
                modifier = Modifier
                    .padding(horizontal = 24.dp, vertical = 16.dp)
                    .navigationBarsPadding(),
                horizontalAlignment = Alignment.CenterHorizontally,
            ) {
                Text(
                    "Auto-Scroll Speed",
                    style = MaterialTheme.typography.labelSmall,
                    color = TempleGold.copy(alpha = 0.7f),
                )
                Spacer(Modifier.height(8.dp))
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceEvenly,
                ) {
                    SpeedChip("Off", scrollSpeed == 0f) { scrollSpeed = 0f }
                    SpeedChip("Slow", scrollSpeed == 0.5f) { scrollSpeed = 0.5f }
                    SpeedChip("Medium", scrollSpeed == 1f) { scrollSpeed = 1f }
                    SpeedChip("Fast", scrollSpeed == 2f) { scrollSpeed = 2f }
                }
            }
        }
    }
}

@Composable
private fun SpeedChip(
    label: String,
    isSelected: Boolean,
    onClick: () -> Unit,
) {
    FilterChip(
        selected = isSelected,
        onClick = onClick,
        label = { Text(label, style = MaterialTheme.typography.labelSmall) },
        colors = FilterChipDefaults.filterChipColors(
            selectedContainerColor = TempleGold.copy(alpha = 0.2f),
            selectedLabelColor = TempleGold,
            containerColor = Color.Transparent,
            labelColor = Color.White.copy(alpha = 0.5f),
        ),
    )
}
