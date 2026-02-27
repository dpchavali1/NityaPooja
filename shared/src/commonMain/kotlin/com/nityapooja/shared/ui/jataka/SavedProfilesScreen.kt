package com.nityapooja.shared.ui.jataka

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.expandVertically
import androidx.compose.animation.shrinkVertically
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import com.nityapooja.shared.data.local.entity.SavedProfileEntity
import com.nityapooja.shared.ui.components.BirthDetails
import com.nityapooja.shared.ui.components.BirthDetailsInput
import com.nityapooja.shared.ui.theme.TempleGold
import org.koin.compose.viewmodel.koinViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SavedProfilesScreen(
    onBack: () -> Unit,
    onProfileSelected: ((SavedProfileEntity) -> Unit)? = null,
    viewModel: SavedProfilesViewModel = koinViewModel(),
) {
    val profiles by viewModel.profiles.collectAsState()
    val editingProfile by viewModel.editingProfile.collectAsState()
    var showAddForm by remember { mutableStateOf(false) }
    var deleteConfirmProfile by remember { mutableStateOf<SavedProfileEntity?>(null) }

    // Form state for add/edit
    var formDetails by remember { mutableStateOf(BirthDetails()) }
    var formName by remember { mutableStateOf("") }

    // When editing, populate form
    LaunchedEffect(editingProfile) {
        editingProfile?.let { p ->
            formName = p.name
            formDetails = BirthDetails(
                name = p.name,
                year = p.year,
                month = p.month,
                day = p.day,
                hour = p.hour,
                minute = p.minute,
                city = p.city,
                latitude = p.latitude,
                longitude = p.longitude,
                timezoneId = p.timezoneId,
                timezoneOffsetHours = p.timezoneOffsetHours,
            )
            showAddForm = true
        }
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Column {
                        Text("కుటుంబ ప్రొఫైల్స్", style = MaterialTheme.typography.titleLarge, fontWeight = FontWeight.Bold)
                        Text("Family Profiles", style = MaterialTheme.typography.labelSmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
                    }
                },
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, "Back")
                    }
                },
                actions = {
                    if (!showAddForm) {
                        IconButton(onClick = {
                            viewModel.clearEditing()
                            formName = ""
                            formDetails = BirthDetails()
                            showAddForm = true
                        }) {
                            Icon(Icons.Default.Add, "Add Profile", tint = TempleGold)
                        }
                    }
                },
            )
        }
    ) { padding ->
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding),
            contentPadding = PaddingValues(16.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp),
        ) {
            // Add/Edit form
            item {
                AnimatedVisibility(
                    visible = showAddForm,
                    enter = expandVertically(),
                    exit = shrinkVertically(),
                ) {
                    Card(
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(16.dp),
                        colors = CardDefaults.cardColors(
                            containerColor = MaterialTheme.colorScheme.surfaceContainerLow,
                        ),
                    ) {
                        Column(
                            modifier = Modifier.padding(16.dp),
                            verticalArrangement = Arrangement.spacedBy(12.dp),
                        ) {
                            Text(
                                if (editingProfile != null) "ప్రొఫైల్ మార్చండి / Edit Profile"
                                else "కొత్త ప్రొఫైల్ / New Profile",
                                style = MaterialTheme.typography.titleMedium,
                                fontWeight = FontWeight.Bold,
                                color = TempleGold,
                            )

                            // Name field
                            OutlinedTextField(
                                value = formName,
                                onValueChange = { formName = it },
                                label = { Text("పేరు / Name") },
                                singleLine = true,
                                modifier = Modifier.fillMaxWidth(),
                                shape = RoundedCornerShape(12.dp),
                            )

                            // Reuse BirthDetailsInput
                            BirthDetailsInput(
                                label = "జన్మ వివరాలు / Birth Details",
                                details = formDetails,
                                onDetailsChange = { formDetails = it },
                            )

                            // Action buttons
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.spacedBy(12.dp),
                            ) {
                                OutlinedButton(
                                    onClick = {
                                        showAddForm = false
                                        viewModel.clearEditing()
                                    },
                                    modifier = Modifier.weight(1f),
                                    shape = RoundedCornerShape(12.dp),
                                ) {
                                    Text("రద్దు / Cancel")
                                }
                                Button(
                                    onClick = {
                                        if (formName.isNotBlank()) {
                                            viewModel.saveProfile(
                                                existingId = editingProfile?.id,
                                                name = formName,
                                                year = formDetails.year,
                                                month = formDetails.month,
                                                day = formDetails.day,
                                                hour = formDetails.hour,
                                                minute = formDetails.minute,
                                                city = formDetails.city,
                                                latitude = formDetails.latitude,
                                                longitude = formDetails.longitude,
                                                timezoneId = formDetails.timezoneId,
                                                timezoneOffsetHours = formDetails.timezoneOffsetHours,
                                            )
                                            showAddForm = false
                                            formName = ""
                                            formDetails = BirthDetails()
                                        }
                                    },
                                    modifier = Modifier.weight(1f),
                                    shape = RoundedCornerShape(12.dp),
                                    colors = ButtonDefaults.buttonColors(containerColor = TempleGold),
                                    enabled = formName.isNotBlank(),
                                ) {
                                    Text(
                                        "సేవ్ / Save",
                                        fontWeight = FontWeight.Bold,
                                        color = MaterialTheme.colorScheme.onPrimary,
                                    )
                                }
                            }
                        }
                    }
                }
            }

            if (profiles.isEmpty() && !showAddForm) {
                item {
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(vertical = 48.dp),
                        horizontalAlignment = Alignment.CenterHorizontally,
                    ) {
                        Icon(
                            Icons.Default.People,
                            contentDescription = null,
                            modifier = Modifier.size(64.dp),
                            tint = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.4f),
                        )
                        Spacer(Modifier.height(16.dp))
                        Text(
                            "ఇంకా ప్రొఫైల్స్ లేవు",
                            style = MaterialTheme.typography.titleMedium,
                            color = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.6f),
                        )
                        Text(
                            "No saved profiles yet",
                            style = MaterialTheme.typography.bodySmall,
                            color = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.4f),
                        )
                        Spacer(Modifier.height(16.dp))
                        Text(
                            "కుటుంబ సభ్యుల జన్మ వివరాలను సేవ్ చేసి\nజాతక చక్రం, గుణ మిలనంలో వాడండి",
                            style = MaterialTheme.typography.bodySmall,
                            color = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.5f),
                            textAlign = androidx.compose.ui.text.style.TextAlign.Center,
                        )
                    }
                }
            }

            items(profiles, key = { it.id }) { profile ->
                ProfileCard(
                    profile = profile,
                    onSelect = onProfileSelected?.let { callback -> { callback(profile) } },
                    onEdit = {
                        viewModel.startEditing(profile)
                    },
                    onDelete = {
                        deleteConfirmProfile = profile
                    },
                )
            }
        }
    }

    // Delete confirmation dialog
    deleteConfirmProfile?.let { profile ->
        AlertDialog(
            onDismissRequest = { deleteConfirmProfile = null },
            title = { Text("ప్రొఫైల్ తొలగించు / Delete Profile") },
            text = { Text("\"${profile.name}\" ప్రొఫైల్‌ను తొలగించాలా?\nDelete \"${profile.name}\" profile?") },
            confirmButton = {
                TextButton(
                    onClick = {
                        viewModel.deleteProfile(profile)
                        deleteConfirmProfile = null
                    },
                    colors = ButtonDefaults.textButtonColors(contentColor = MaterialTheme.colorScheme.error),
                ) {
                    Text("తొలగించు / Delete")
                }
            },
            dismissButton = {
                TextButton(onClick = { deleteConfirmProfile = null }) {
                    Text("రద్దు / Cancel")
                }
            },
        )
    }
}

@Composable
private fun ProfileCard(
    profile: SavedProfileEntity,
    onSelect: (() -> Unit)?,
    onEdit: () -> Unit,
    onDelete: () -> Unit,
) {
    val monthNames = arrayOf(
        "Jan", "Feb", "Mar", "Apr", "May", "Jun",
        "Jul", "Aug", "Sep", "Oct", "Nov", "Dec",
    )
    val dateStr = "${profile.day} ${monthNames[(profile.month - 1).coerceIn(0, 11)]} ${profile.year}"
    val timeStr = buildString {
        val h = if (profile.hour > 12) profile.hour - 12 else if (profile.hour == 0) 12 else profile.hour
        val amPm = if (profile.hour >= 12) "PM" else "AM"
        append("$h:${profile.minute.toString().padStart(2, '0')} $amPm")
    }

    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surfaceContainerLow,
        ),
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically,
            ) {
                Icon(
                    Icons.Default.Person,
                    contentDescription = null,
                    tint = TempleGold,
                    modifier = Modifier.size(24.dp),
                )
                Spacer(Modifier.width(8.dp))
                Text(
                    profile.name,
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis,
                    modifier = Modifier.weight(1f),
                )
            }
            Spacer(Modifier.height(8.dp))
            Row(verticalAlignment = Alignment.CenterVertically) {
                Icon(Icons.Default.CalendarMonth, null, modifier = Modifier.size(14.dp), tint = MaterialTheme.colorScheme.onSurfaceVariant)
                Spacer(Modifier.width(4.dp))
                Text("$dateStr, $timeStr", style = MaterialTheme.typography.bodySmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
            }
            Spacer(Modifier.height(4.dp))
            Row(verticalAlignment = Alignment.CenterVertically) {
                Icon(Icons.Default.LocationOn, null, modifier = Modifier.size(14.dp), tint = MaterialTheme.colorScheme.onSurfaceVariant)
                Spacer(Modifier.width(4.dp))
                Text(profile.city, style = MaterialTheme.typography.bodySmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
            }
            Spacer(Modifier.height(12.dp))
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.End,
                verticalAlignment = Alignment.CenterVertically,
            ) {
                if (onSelect != null) {
                    FilledTonalButton(
                        onClick = onSelect,
                        shape = RoundedCornerShape(8.dp),
                        contentPadding = PaddingValues(horizontal = 16.dp, vertical = 4.dp),
                    ) {
                        Icon(Icons.Default.Check, null, modifier = Modifier.size(16.dp))
                        Spacer(Modifier.width(4.dp))
                        Text("ఎంచుకో / Select", style = MaterialTheme.typography.labelSmall)
                    }
                    Spacer(Modifier.width(8.dp))
                }
                IconButton(onClick = onEdit) {
                    Icon(Icons.Default.Edit, "Edit", tint = TempleGold, modifier = Modifier.size(20.dp))
                }
                IconButton(onClick = onDelete) {
                    Icon(Icons.Default.Delete, "Delete", tint = MaterialTheme.colorScheme.error, modifier = Modifier.size(20.dp))
                }
            }
        }
    }
}
