package com.nityapooja.shared.ui.settings

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.expandVertically
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.shrinkVertically
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.KeyboardArrowDown
import androidx.compose.material3.FilterChip
import androidx.compose.material3.FilterChipDefaults
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Switch
import androidx.compose.material3.SwitchDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.rotate
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.nityapooja.shared.ui.components.GlassmorphicCard
import com.nityapooja.shared.ui.theme.AuspiciousGreen
import com.nityapooja.shared.ui.theme.DeepVermillion
import com.nityapooja.shared.ui.theme.TempleGold
import com.nityapooja.shared.ui.theme.WarningAmber

// ---------------------------------------------------------------------------
// NotificationsSection
//
// Drop-in replacement for the fragmented per-card notification rows in
// SettingsScreen.kt.  Accepts all state as plain parameters so the caller
// (SettingsScreen) owns the ViewModel and can thread its callbacks through
// the existing requestPermissionOnEnable wrapper before forwarding here.
// ---------------------------------------------------------------------------

/**
 * Redesigned notifications section for SettingsScreen.
 *
 * Groups the 9 notification types into three visually distinct cards:
 *   1. Daily Devotion  — Morning Blessing, Evening Aarti, Daily Shloka
 *   2. Calendar & Learning — Panchang Alerts, Puranas Quiz, Vrata Reminders, Sacred Month
 *   3. Alerts          — Eclipse (Grahanam), Rahu Kalam  (warning-tinted card border)
 *
 * Quiz time picker expands inline inside the Calendar card when the quiz
 * toggle is ON, keeping the full section coherent rather than spilling
 * outside the card.
 *
 * @param morningNotification         Morning Blessing toggle state (5:30 AM)
 * @param onMorningNotificationChange Callback forwarded from SettingsScreen (already wraps exactAlarm request)
 * @param eveningNotification         Evening Aarti toggle state (6:30 PM)
 * @param onEveningNotificationChange Callback forwarded from SettingsScreen
 * @param shlokaNotification          Daily Shloka toggle state (7:00 AM)
 * @param onShlokaNotificationChange  Callback forwarded from SettingsScreen
 * @param panchangNotifications       Panchang Alerts toggle state
 * @param onPanchangNotificationsChange Callback forwarded from SettingsScreen
 * @param quizNotification            Puranas Quiz toggle state
 * @param onQuizNotificationChange    Callback forwarded from SettingsScreen
 * @param quizNotificationHour        Currently selected quiz hour (18–21)
 * @param quizNotificationMinute      Currently selected quiz minute (0 or 30)
 * @param onQuizTimeChange            Callback(hour, minute) to update quiz time
 * @param vrataNotification           Vrata Reminders toggle state
 * @param onVrataNotificationChange   Callback forwarded from SettingsScreen
 * @param sacredMonthNotification     Sacred Month toggle state
 * @param onSacredMonthNotificationChange Callback forwarded from SettingsScreen
 * @param grahanamNotification        Eclipse Alerts toggle state
 * @param onGrahanamNotificationChange Callback forwarded from SettingsScreen
 * @param rahuKalamAlerts             Rahu Kalam Alerts toggle state
 * @param onRahuKalamAlertsChange     Callback forwarded from SettingsScreen
 */
@Composable
fun NotificationsSection(
    // Daily Devotion
    morningNotification: Boolean,
    onMorningNotificationChange: (Boolean) -> Unit,
    eveningNotification: Boolean,
    onEveningNotificationChange: (Boolean) -> Unit,
    shlokaNotification: Boolean,
    onShlokaNotificationChange: (Boolean) -> Unit,
    // Calendar & Learning
    panchangNotifications: Boolean,
    onPanchangNotificationsChange: (Boolean) -> Unit,
    quizNotification: Boolean,
    onQuizNotificationChange: (Boolean) -> Unit,
    quizNotificationHour: Int,
    quizNotificationMinute: Int,
    onQuizTimeChange: (hour: Int, minute: Int) -> Unit,
    vrataNotification: Boolean,
    onVrataNotificationChange: (Boolean) -> Unit,
    sacredMonthNotification: Boolean,
    onSacredMonthNotificationChange: (Boolean) -> Unit,
    // Alerts
    grahanamNotification: Boolean,
    onGrahanamNotificationChange: (Boolean) -> Unit,
    rahuKalamAlerts: Boolean,
    onRahuKalamAlertsChange: (Boolean) -> Unit,
) {
    var expanded by rememberSaveable { mutableStateOf(true) }
    val chevronRotation by animateFloatAsState(
        targetValue = if (expanded) 180f else 0f,
        label = "chevron",
    )

    // Collapsible section header
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clickable { expanded = !expanded }
            .padding(vertical = 4.dp),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        Column(modifier = Modifier.weight(1f)) {
            Text(
                "\u0C28\u0C4B\u0C1F\u0C3F\u0C2B\u0C3F\u0C15\u0C47\u0C37\u0C28\u0C4D\u0C32\u0C41",
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.onBackground,
            )
            Text(
                "Notifications",
                style = MaterialTheme.typography.labelSmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
            )
        }
        Icon(
            imageVector = Icons.Default.KeyboardArrowDown,
            contentDescription = if (expanded) "Collapse" else "Expand",
            tint = TempleGold.copy(alpha = 0.7f),
            modifier = Modifier.rotate(chevronRotation),
        )
    }
    HorizontalDivider(
        modifier = Modifier.fillMaxWidth(0.3f),
        thickness = 2.dp,
        color = TempleGold.copy(alpha = 0.5f),
    )
    Spacer(Modifier.height(6.dp))

    AnimatedVisibility(
        visible = expanded,
        enter = expandVertically() + fadeIn(),
        exit = shrinkVertically() + fadeOut(),
    ) {
        Column {

    // -----------------------------------------------------------------------
    // Group 1 — Daily Devotion
    // -----------------------------------------------------------------------
    NotificationGroupLabel(
        teluguLabel = "\u0C30\u0C4B\u0C1C\u0C41 \u0C2D\u0C15\u0C4D\u0C24\u0C3F",
        englishLabel = "Daily Devotion",
    )
    GlassmorphicCard(cornerRadius = 16.dp, contentPadding = 0.dp) {
        NotificationRow(
            emoji = "\uD83C\uDF05",
            titleTelugu = "\u0C09\u0C26\u0C2F\u0C02 \u0C06\u0C36\u0C40\u0C30\u0C4D\u0C35\u0C1A\u0C28\u0C02",
            titleEnglish = "Morning Blessing",
            subtitle = "Daily \u00B7 5:30 AM",
            checked = morningNotification,
            onCheckedChange = onMorningNotificationChange,
            iconTint = Color(0xFFFFA726),   // warm amber-orange — sunrise palette
        )
        NotificationRowDivider()
        NotificationRow(
            emoji = "\uD83E\uDE94",
            titleTelugu = "\u0C38\u0C3E\u0C2F\u0C02\u0C15\u0C3E\u0C32\u0C02 \u0C06\u0C30\u0C24\u0C3F",
            titleEnglish = "Evening Aarti",
            subtitle = "Daily \u00B7 6:30 PM",
            checked = eveningNotification,
            onCheckedChange = onEveningNotificationChange,
            iconTint = Color(0xFFEF5350),   // diya flame red
        )
        NotificationRowDivider()
        NotificationRow(
            emoji = "\uD83D\uDCFF",
            titleTelugu = "\u0C30\u0C4B\u0C1C\u0C41 \u0C36\u0C4D\u0C32\u0C4B\u0C15\u0C02",
            titleEnglish = "Daily Shloka",
            subtitle = "Daily \u00B7 7:00 AM",
            checked = shlokaNotification,
            onCheckedChange = onShlokaNotificationChange,
            iconTint = Color(0xFF66BB6A),   // sacred tulasi green
            isLast = true,
        )
    }

    Spacer(Modifier.height(12.dp))

    // -----------------------------------------------------------------------
    // Group 2 — Calendar & Learning
    // -----------------------------------------------------------------------
    NotificationGroupLabel(
        teluguLabel = "\u0C2A\u0C02\u0C1A\u0C3E\u0C02\u0C17\u0C02 & \u0C05\u0C27\u0C4D\u0C2F\u0C2F\u0C28\u0C02",
        englishLabel = "Calendar & Learning",
    )
    GlassmorphicCard(cornerRadius = 16.dp, contentPadding = 0.dp) {
        NotificationRow(
            emoji = "\uD83D\uDCC5",
            titleTelugu = "\u0C2A\u0C02\u0C1A\u0C3E\u0C02\u0C17 \u0C17\u0C41\u0C30\u0C4D\u0C24\u0C3F\u0C02\u0C2A\u0C41\u0C32\u0C41",
            titleEnglish = "Panchang Alerts",
            subtitle = "Ekadashi, Purnima, Amavasya",
            checked = panchangNotifications,
            onCheckedChange = onPanchangNotificationsChange,
            iconTint = TempleGold,
        )
        NotificationRowDivider()

        // Quiz row + inline time picker
        NotificationRow(
            emoji = "\uD83D\uDCD6",
            titleTelugu = "\u0C2A\u0C41\u0C30\u0C3E\u0C23\u0C3E\u0C32 \u0C15\u0C4D\u0C35\u0C3F\u0C1C\u0C4D",
            titleEnglish = "Puranas Quiz",
            subtitle = buildQuizSubtitle(quizNotificationHour, quizNotificationMinute),
            checked = quizNotification,
            onCheckedChange = onQuizNotificationChange,
            iconTint = Color(0xFF5C6BC0),   // deep indigo — scholarship/learning
        )
        AnimatedVisibility(
            visible = quizNotification,
            enter = expandVertically() + fadeIn(),
            exit = shrinkVertically() + fadeOut(),
        ) {
            QuizTimePicker(
                selectedHour = quizNotificationHour,
                selectedMinute = quizNotificationMinute,
                onTimeChange = onQuizTimeChange,
            )
        }

        NotificationRowDivider()
        NotificationRow(
            emoji = "\uD83D\uDE4F",
            titleTelugu = "\u0C35\u0C4D\u0C30\u0C24 \u0C30\u0C3F\u0C2E\u0C48\u0C02\u0C21\u0C30\u0C4D\u0C32\u0C41",
            titleEnglish = "Vrata Reminders",
            subtitle = "6:00 AM on vrata days",
            checked = vrataNotification,
            onCheckedChange = onVrataNotificationChange,
            iconTint = Color(0xFFEC407A),   // lotus pink — vrata devotion
        )
        NotificationRowDivider()
        NotificationRow(
            emoji = "\uD83D\uDED5",
            titleTelugu = "\u0C2A\u0C35\u0C3F\u0C24\u0C4D\u0C30 \u0C2E\u0C3E\u0C38\u0C02",
            titleEnglish = "Sacred Month",
            subtitle = "5:30 AM during sacred months",
            checked = sacredMonthNotification,
            onCheckedChange = onSacredMonthNotificationChange,
            iconTint = Color(0xFFFF7043),   // saffron — temple spire
            isLast = true,
        )
    }

    Spacer(Modifier.height(12.dp))

    // -----------------------------------------------------------------------
    // Group 3 — Alerts  (warning-tinted border to signal urgency)
    // -----------------------------------------------------------------------
    NotificationGroupLabel(
        teluguLabel = "\u0C39\u0C46\u0C1A\u0C4D\u0C1A\u0C30\u0C3F\u0C15\u0C32\u0C41",
        englishLabel = "Alerts",
        labelColor = DeepVermillion,
    )
    GlassmorphicCard(
        cornerRadius = 16.dp,
        contentPadding = 0.dp,
        accentColor = DeepVermillion,
    ) {
        NotificationRow(
            emoji = "\uD83C\uDF11",
            titleTelugu = "\u0C17\u0C4D\u0C30\u0C39\u0C23\u0C02 \u0C28\u0C4B\u0C1F\u0C3F\u0C2B\u0C3F\u0C15\u0C47\u0C37\u0C28\u0C4D",
            titleEnglish = "Eclipse Alerts",
            subtitle = "Day before + day of eclipse",
            checked = grahanamNotification,
            onCheckedChange = onGrahanamNotificationChange,
            iconTint = Color(0xFF7E57C2),   // eclipse purple
            isAlert = true,
        )
        NotificationRowDivider(tint = DeepVermillion.copy(alpha = 0.15f))
        NotificationRow(
            emoji = "\u26A0\uFE0F",
            titleTelugu = "\u0C30\u0C3E\u0C39\u0C41 \u0C15\u0C3E\u0C32 \u0C39\u0C46\u0C1A\u0C4D\u0C1A\u0C30\u0C3F\u0C15\u0C32\u0C41",
            titleEnglish = "Rahu Kalam Alerts",
            subtitle = "10 min before Rahu Kalam / Yamagandam / Gulika",
            checked = rahuKalamAlerts,
            onCheckedChange = onRahuKalamAlertsChange,
            iconTint = WarningAmber,
            isAlert = true,
            isLast = true,
        )
    }

        } // end Column
    } // end AnimatedVisibility
}

// ---------------------------------------------------------------------------
// Private helpers
// ---------------------------------------------------------------------------

/** Thin label row above each group card — mirrors the style of SectionHeader
 *  but at a smaller scale to create visual hierarchy within the section. */
@Composable
private fun NotificationGroupLabel(
    teluguLabel: String,
    englishLabel: String,
    labelColor: Color = TempleGold,
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(start = 4.dp, bottom = 6.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(8.dp),
    ) {
        // Accent pip
        Box(
            modifier = Modifier
                .size(width = 3.dp, height = 14.dp)
                .clip(RoundedCornerShape(2.dp))
                .background(labelColor.copy(alpha = 0.7f)),
        )
        Column {
            Text(
                teluguLabel,
                style = MaterialTheme.typography.labelMedium,
                fontWeight = FontWeight.SemiBold,
                color = labelColor,
            )
            Text(
                englishLabel,
                style = MaterialTheme.typography.labelSmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
            )
        }
    }
}

/**
 * A single notification row: icon container | titles + subtitle | switch.
 *
 * @param emoji        The emoji character used inside the icon container.
 * @param titleTelugu  Primary label in Telugu script.
 * @param titleEnglish Secondary label in English.
 * @param subtitle     Small descriptive text (time / trigger description).
 * @param checked      Current switch state.
 * @param onCheckedChange Switch toggle callback.
 * @param iconTint     Background tint for the icon container.
 *                     Active rows are shown at full opacity; inactive rows at
 *                     reduced opacity so the icon recedes when disabled.
 * @param isAlert      When true, the title uses a slightly warmer/brighter
 *                     color to reinforce urgency.
 * @param isLast       When true, no bottom padding is added — the card's own
 *                     contentPadding provides the correct outer spacing.
 */
@Composable
private fun NotificationRow(
    emoji: String,
    titleTelugu: String,
    titleEnglish: String,
    subtitle: String,
    checked: Boolean,
    onCheckedChange: (Boolean) -> Unit,
    iconTint: Color,
    isAlert: Boolean = false,
    isLast: Boolean = false,
) {
    val iconAlpha = if (checked) 1f else 0.38f
    val iconBg = iconTint.copy(alpha = if (checked) 0.22f else 0.10f)
    val titleColor = when {
        isAlert && checked -> DeepVermillion.copy(alpha = 0.9f)
        else -> MaterialTheme.colorScheme.onSurface
    }

    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(
                start = 16.dp,
                end = 12.dp,
                top = 14.dp,
                bottom = if (isLast) 14.dp else 2.dp,
            ),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        // Icon container
        Box(
            modifier = Modifier
                .size(40.dp)
                .clip(RoundedCornerShape(12.dp))
                .background(iconBg),
            contentAlignment = Alignment.Center,
        ) {
            Text(
                text = emoji,
                style = MaterialTheme.typography.titleSmall,
                color = iconTint.copy(alpha = iconAlpha),
            )
        }

        Spacer(Modifier.width(12.dp))

        // Title + subtitle
        Column(modifier = Modifier.weight(1f)) {
            Text(
                titleTelugu,
                style = MaterialTheme.typography.titleSmall,
                fontWeight = FontWeight.Medium,
                color = titleColor,
            )
            Text(
                titleEnglish,
                style = MaterialTheme.typography.labelSmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
            )
            Text(
                subtitle,
                style = MaterialTheme.typography.labelSmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.7f),
            )
        }

        Spacer(Modifier.width(8.dp))

        // Switch
        Switch(
            checked = checked,
            onCheckedChange = onCheckedChange,
            colors = SwitchDefaults.colors(
                checkedThumbColor = TempleGold,
                checkedTrackColor = TempleGold.copy(alpha = 0.35f),
                checkedBorderColor = TempleGold.copy(alpha = 0.6f),
            ),
        )
    }
}

/** Hairline divider used between rows inside a group card. */
@Composable
private fun NotificationRowDivider(
    tint: Color = Color.Unspecified,
) {
    HorizontalDivider(
        modifier = Modifier.padding(start = 68.dp, end = 16.dp),
        thickness = 0.5.dp,
        color = if (tint != Color.Unspecified) tint
                else MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.25f),
    )
}

/**
 * Inline quiz time picker shown immediately below the quiz toggle row when
 * the quiz notification is enabled.  Laid out as two rows of compact chips:
 *   Row 1 — hour selection : 6 PM | 7 PM | 8 PM | 9 PM
 *   Row 2 — minute selection: :00  | :30
 *
 * The whole picker sits inside the Calendar card so it never appears
 * orphaned outside its group context.
 */
@Composable
private fun QuizTimePicker(
    selectedHour: Int,
    selectedMinute: Int,
    onTimeChange: (hour: Int, minute: Int) -> Unit,
) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(start = 68.dp, end = 16.dp, top = 4.dp, bottom = 12.dp),
        verticalArrangement = Arrangement.spacedBy(6.dp),
    ) {
        Text(
            "\u0C38\u0C2E\u0C2F\u0C02 \u0C0E\u0C02\u0C1A\u0C41\u0C15\u0C4B\u0C02\u0C21\u0C3F / Pick a time",
            style = MaterialTheme.typography.labelSmall,
            color = MaterialTheme.colorScheme.onSurfaceVariant,
        )
        // Hour chips
        Row(horizontalArrangement = Arrangement.spacedBy(6.dp)) {
            listOf(
                18 to "6 PM",
                19 to "7 PM",
                20 to "8 PM",
                21 to "9 PM",
            ).forEach { (hour, label) ->
                FilterChip(
                    selected = selectedHour == hour,
                    onClick = { onTimeChange(hour, selectedMinute) },
                    label = {
                        Text(
                            label,
                            style = MaterialTheme.typography.labelSmall,
                        )
                    },
                    colors = FilterChipDefaults.filterChipColors(
                        selectedContainerColor = TempleGold.copy(alpha = 0.18f),
                        selectedLabelColor = TempleGold,
                    ),
                    border = FilterChipDefaults.filterChipBorder(
                        enabled = true,
                        selected = selectedHour == hour,
                        selectedBorderColor = TempleGold.copy(alpha = 0.5f),
                        borderColor = MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.4f),
                    ),
                )
            }
        }
        // Minute chips
        Row(horizontalArrangement = Arrangement.spacedBy(6.dp)) {
            listOf(
                0 to ":00",
                30 to ":30",
            ).forEach { (minute, label) ->
                FilterChip(
                    selected = selectedMinute == minute,
                    onClick = { onTimeChange(selectedHour, minute) },
                    label = {
                        Text(
                            label,
                            style = MaterialTheme.typography.labelSmall,
                        )
                    },
                    colors = FilterChipDefaults.filterChipColors(
                        selectedContainerColor = TempleGold.copy(alpha = 0.18f),
                        selectedLabelColor = TempleGold,
                    ),
                    border = FilterChipDefaults.filterChipBorder(
                        enabled = true,
                        selected = selectedMinute == minute,
                        selectedBorderColor = TempleGold.copy(alpha = 0.5f),
                        borderColor = MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.4f),
                    ),
                )
            }
        }
    }
}

/** Formats the quiz time state into a readable subtitle string. */
private fun buildQuizSubtitle(hour: Int, minute: Int): String {
    val displayHour = if (hour > 12) hour - 12 else if (hour == 0) 12 else hour
    val amPm = if (hour >= 12) "PM" else "AM"
    val minuteStr = minute.toString().padStart(2, '0')
    return "Daily \u00B7 $displayHour:$minuteStr $amPm"
}
