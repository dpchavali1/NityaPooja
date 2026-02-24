package com.nityapooja.app.ui.temple

import android.content.Intent
import android.net.Uri
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.BookOnline
import androidx.compose.material.icons.filled.LocationOn
import androidx.compose.material.icons.filled.Schedule
import androidx.compose.material.icons.filled.Videocam
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.nityapooja.app.ui.components.FontSizeControls
import com.nityapooja.app.ui.components.FontSizeViewModel
import com.nityapooja.app.ui.components.VerseBlock
import com.nityapooja.app.ui.theme.AuspiciousGreen

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun TempleDetailScreen(
    templeId: Int,
    viewModel: TempleViewModel = hiltViewModel(),
    onBack: () -> Unit,
    fontSizeViewModel: FontSizeViewModel = hiltViewModel(),
) {
    val templeFlow = remember(templeId) { viewModel.getTempleById(templeId) }
    val temple by templeFlow.collectAsStateWithLifecycle()
    val context = LocalContext.current

    val fontSize by fontSizeViewModel.fontSize.collectAsStateWithLifecycle()
    val fontScale = fontSize / 16f

    LaunchedEffect(temple) {
        temple?.let { viewModel.trackHistory("temple", it.id, it.name, it.nameTelugu) }
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    temple?.let {
                        Column {
                            Text(
                                it.nameTelugu,
                                style = MaterialTheme.typography.titleMedium,
                                fontWeight = FontWeight.Bold,
                                maxLines = 1,
                            )
                            Text(
                                it.name,
                                style = MaterialTheme.typography.labelSmall,
                                color = MaterialTheme.colorScheme.onSurfaceVariant,
                                maxLines = 1,
                            )
                        }
                    } ?: Text("దేవాలయం · Temple")
                },
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Icon(
                            Icons.AutoMirrored.Filled.ArrowBack,
                            contentDescription = "Back",
                        )
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
        },
    ) { padding ->
        temple?.let { t ->
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(padding)
                    .verticalScroll(rememberScrollState())
                    .padding(16.dp),
                verticalArrangement = Arrangement.spacedBy(16.dp),
            ) {
                // Location info
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(4.dp),
                ) {
                    Icon(
                        Icons.Default.LocationOn,
                        contentDescription = null,
                        modifier = Modifier.size(18.dp),
                        tint = MaterialTheme.colorScheme.primary,
                    )
                    Column {
                        t.locationTelugu?.let { locTelugu ->
                            Text(
                                locTelugu,
                                style = MaterialTheme.typography.bodyLarge,
                                fontWeight = FontWeight.Medium,
                            )
                        }
                        val locationText = listOfNotNull(
                            t.location,
                            t.state,
                        ).joinToString(", ")
                        if (locationText.isNotEmpty()) {
                            Text(
                                locationText,
                                style = MaterialTheme.typography.bodyMedium,
                                color = MaterialTheme.colorScheme.onSurfaceVariant,
                            )
                        }
                    }
                }

                // Timings
                t.timings?.let { timings ->
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(4.dp),
                    ) {
                        Icon(
                            Icons.Default.Schedule,
                            contentDescription = null,
                            modifier = Modifier.size(18.dp),
                            tint = MaterialTheme.colorScheme.secondary,
                        )
                        Text(
                            timings,
                            style = MaterialTheme.typography.bodyMedium,
                            color = MaterialTheme.colorScheme.onSurfaceVariant,
                        )
                    }
                }

                // Live Darshan badge and button
                if (t.hasLiveDarshan) {
                    Surface(
                        shape = RoundedCornerShape(12.dp),
                        color = AuspiciousGreen.copy(alpha = 0.1f),
                    ) {
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(12.dp),
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(8.dp),
                        ) {
                            Icon(
                                Icons.Default.Videocam,
                                contentDescription = null,
                                tint = AuspiciousGreen,
                                modifier = Modifier.size(20.dp),
                            )
                            Column(modifier = Modifier.weight(1f)) {
                                Text(
                                    "లైవ్ దర్శనం అందుబాటులో ఉంది",
                                    style = MaterialTheme.typography.labelMedium,
                                    fontWeight = FontWeight.Bold,
                                    color = AuspiciousGreen,
                                )
                                Text(
                                    "Live Darshan Available",
                                    style = MaterialTheme.typography.labelSmall,
                                    color = AuspiciousGreen.copy(alpha = 0.8f),
                                )
                            }
                            t.youtubeUrl?.let { url ->
                                FilledTonalButton(
                                    onClick = {
                                        val intent = Intent(Intent.ACTION_VIEW, Uri.parse(url))
                                        context.startActivity(intent)
                                    },
                                    colors = ButtonDefaults.filledTonalButtonColors(
                                        containerColor = AuspiciousGreen.copy(alpha = 0.2f),
                                        contentColor = AuspiciousGreen,
                                    ),
                                ) {
                                    Text("Watch")
                                }
                            }
                        }
                    }
                }

                // Booking URL button
                t.bookingUrl?.let { url ->
                    OutlinedButton(
                        onClick = {
                            val intent = Intent(Intent.ACTION_VIEW, Uri.parse(url))
                            context.startActivity(intent)
                        },
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(12.dp),
                    ) {
                        Icon(
                            Icons.Default.BookOnline,
                            contentDescription = null,
                            modifier = Modifier.size(18.dp),
                        )
                        Spacer(Modifier.width(8.dp))
                        Text("దర్శనం బుకింగ్ · Book Darshan")
                    }
                }

                HorizontalDivider(color = MaterialTheme.colorScheme.outlineVariant)

                // Telugu Description
                t.descriptionTelugu?.let { descTelugu ->
                    Column(verticalArrangement = Arrangement.spacedBy(4.dp)) {
                        Text(
                            "వివరణ · Description (Telugu)",
                            style = MaterialTheme.typography.labelMedium,
                            color = MaterialTheme.colorScheme.primary,
                            fontWeight = FontWeight.Bold,
                        )
                        Spacer(Modifier.height(4.dp))
                        VerseBlock(
                            teluguText = descTelugu,
                            fontScale = fontScale,
                        )
                    }
                }

                // English Description
                t.description?.let { desc ->
                    Column(verticalArrangement = Arrangement.spacedBy(4.dp)) {
                        Text(
                            "Description (English)",
                            style = MaterialTheme.typography.labelMedium,
                            color = MaterialTheme.colorScheme.secondary,
                            fontWeight = FontWeight.Bold,
                        )
                        Spacer(Modifier.height(4.dp))
                        Text(
                            desc,
                            style = MaterialTheme.typography.bodyMedium.copy(
                                lineHeight = (26 * fontScale).sp,
                                fontSize = (14 * fontScale).sp,
                            ),
                            color = MaterialTheme.colorScheme.onSurfaceVariant,
                        )
                    }
                }

                // Bottom spacing
                Spacer(Modifier.height(24.dp))
            }
        } ?: run {
            // Loading state
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(padding),
                contentAlignment = Alignment.Center,
            ) {
                CircularProgressIndicator(
                    color = MaterialTheme.colorScheme.primary,
                )
            }
        }
    }
}
