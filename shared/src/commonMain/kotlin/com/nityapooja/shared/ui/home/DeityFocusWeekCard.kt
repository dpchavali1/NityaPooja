package com.nityapooja.shared.ui.home

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.width
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import kotlinx.datetime.Clock
import kotlinx.datetime.DayOfWeek
import kotlinx.datetime.LocalDate
import kotlinx.datetime.TimeZone
import kotlinx.datetime.todayIn
import com.nityapooja.shared.ui.components.GlassmorphicCard
import com.nityapooja.shared.ui.theme.NityaPoojaTextStyles
import com.nityapooja.shared.ui.theme.TempleGold

// (Telugu name, English name, emoji)
private val FOCUS_DEITIES = listOf(
    Triple("గణేశుడు", "Ganesha", "🐘"),
    Triple("లక్ష్మీ దేవి", "Lakshmi", "🪷"),
    Triple("శివుడు", "Shiva", "🔱"),
    Triple("విష్ణువు", "Vishnu", "🪶"),
    Triple("సరస్వతి", "Saraswati", "🎵"),
    Triple("హనుమంతుడు", "Hanuman", "🌟"),
    Triple("సుబ్రహ్మణ్యేశ్వరుడు", "Subramanya", "🦚"),
    Triple("దుర్గా దేవి", "Durga", "⚔️"),
    Triple("కృష్ణుడు", "Krishna", "🎶"),
    Triple("రాముడు", "Rama", "🏹"),
)

// Devotional tip per deity (index matches FOCUS_DEITIES)
private val DEITY_TIPS = listOf(
    "బుధవారం విశేష పూజ చేయండి. వినాయక చవితి నాడు 21 మోదకాలు నైవేద్యం పెట్టండి.",
    "శుక్రవారం లక్ష్మీ పూజ మంచిది. శ్రీ సూక్తం పఠించి సంపదను ప్రార్థించండి.",
    "సోమవారం శివుని పూజకు అత్యంత శుభకరం. రుద్రాభిషేకం చేయండి.",
    "గురువారం విష్ణు పూజ విశేషమైనది. విష్ణు సహస్రనామం పఠించండి.",
    "గురువారం సరస్వతీ పూజ చేయండి. విద్యార్థులు జ్ఞాన ప్రాప్తికై ప్రార్థించండి.",
    "శనివారం హనుమద్ పూజ శుభకరం. హనుమాన్ చాలీసా పఠించి బలాన్ని పొందండి.",
    "మంగళవారం సుబ్రహ్మణ్యేశ్వర పూజ అత్యంత ఫలదాయకం. షణ్ముఖుని ధ్యానించండి.",
    "శుక్రవారం దుర్గా పూజ విశేషం. దేవీ మహాత్మ్యం పఠించి శక్తిని పొందండి.",
    "బుధవారం కృష్ణ పూజ మంచిది. భగవద్గీతలో శ్లోకాలు పఠించండి.",
    "ఆదివారం రామ పూజ శుభకరం. రామ నామ సంకీర్తన చేయండి.",
)

// Calculate week-of-year from a date using epoch days (ISO week starting Monday)
private fun weekOfYear(date: LocalDate): Int {
    // Simple approach: use day-of-year / 7
    val epochDay = date.toEpochDays().toInt()
    // Use fixed epoch as week reference: epoch days from 2000-01-03 (a Monday)
    val refMonday = 10959 // 2000-01-03 in epoch days
    return ((epochDay - refMonday) / 7).coerceAtLeast(0)
}

// Days remaining in the current week (ISO: Mon=start, Sun=end)
private fun daysLeftInWeek(date: LocalDate): Int {
    // dayOfWeek: Mon=1, Tue=2, ..., Sun=7
    // isoDayNumber: Mon=1, Tue=2, ..., Sun=7
    val dow = when (date.dayOfWeek) {
        DayOfWeek.MONDAY -> 1; DayOfWeek.TUESDAY -> 2; DayOfWeek.WEDNESDAY -> 3
        DayOfWeek.THURSDAY -> 4; DayOfWeek.FRIDAY -> 5; DayOfWeek.SATURDAY -> 6
        DayOfWeek.SUNDAY -> 7; else -> 1
    }
    return 7 - dow
}

@Composable
fun DeityFocusWeekCard(
    modifier: Modifier = Modifier,
) {
    val today = remember { Clock.System.todayIn(TimeZone.currentSystemDefault()) }
    val week = remember(today) { weekOfYear(today) }
    val deityIndex = remember(week) { week % FOCUS_DEITIES.size }
    val deity = FOCUS_DEITIES[deityIndex]
    val tip = DEITY_TIPS[deityIndex]
    val daysLeft = remember(today) { daysLeftInWeek(today) }
    val daysLeftText = when (daysLeft) {
        0 -> "నేడే చివరి రోజు · Last day today"
        1 -> "1 రోజు మిగిలింది · 1 day left"
        else -> "$daysLeft రోజులు మిగిలాయి · $daysLeft days left"
    }

    GlassmorphicCard(
        modifier = modifier,
        accentColor = TempleGold,
        cornerRadius = 20.dp,
        contentPadding = 20.dp,
    ) {
        // Header
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically,
        ) {
            Text(
                "ఈ వారం దేవత · THIS WEEK'S DEITY",
                style = NityaPoojaTextStyles.GoldLabel,
                color = TempleGold,
            )
            Text(
                daysLeftText,
                style = MaterialTheme.typography.labelSmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
            )
        }
        Spacer(Modifier.height(16.dp))

        // Deity info row
        Row(
            verticalAlignment = Alignment.CenterVertically,
        ) {
            Text(
                deity.third,
                fontSize = 48.sp,
            )
            Spacer(Modifier.width(16.dp))
            Column {
                Text(
                    deity.first,
                    style = NityaPoojaTextStyles.TeluguDisplay.copy(fontSize = 22.sp),
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.onSurface,
                )
                Text(
                    deity.second,
                    style = MaterialTheme.typography.titleMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                )
            }
        }

        Spacer(Modifier.height(12.dp))

        // Devotional tip
        Text(
            tip,
            style = MaterialTheme.typography.bodyMedium,
            color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.85f),
            lineHeight = 22.sp,
        )
    }
}
