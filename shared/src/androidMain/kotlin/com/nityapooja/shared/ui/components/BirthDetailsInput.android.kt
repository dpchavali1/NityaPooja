package com.nityapooja.shared.ui.components

import android.app.DatePickerDialog
import android.app.TimePickerDialog
import android.location.Geocoder
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.platform.LocalContext
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import java.util.Locale
import kotlin.math.abs

@Composable
actual fun PlatformDatePicker(
    year: Int,
    month: Int,
    day: Int,
    onDateSelected: (year: Int, month: Int, day: Int) -> Unit,
    onDismiss: () -> Unit,
) {
    val context = LocalContext.current
    LaunchedEffect(Unit) {
        DatePickerDialog(
            context,
            { _, y, m, d -> onDateSelected(y, m + 1, d) },
            year,
            month - 1,
            day,
        ).apply {
            setOnCancelListener { onDismiss() }
            setOnDismissListener { /* handled by cancel or selection */ }
            show()
        }
    }
}

@Composable
actual fun PlatformTimePicker(
    hour: Int,
    minute: Int,
    onTimeSelected: (hour: Int, minute: Int) -> Unit,
    onDismiss: () -> Unit,
) {
    val context = LocalContext.current
    LaunchedEffect(Unit) {
        TimePickerDialog(
            context,
            { _, h, m -> onTimeSelected(h, m) },
            hour,
            minute,
            false,
        ).apply {
            setOnCancelListener { onDismiss() }
            show()
        }
    }
}

actual suspend fun searchPlaces(query: String): List<PlaceResult> = withContext(Dispatchers.IO) {
    val results = mutableListOf<PlaceResult>()

    if (Geocoder.isPresent()) {
        try {
            // Use a temporary context-free approach - Geocoder needs context
            // This will be provided via Koin or passed differently in production
            // For now, use fallback
            // Note: Android actual needs app context. We use fallback here and
            // the androidApp module can override with Geocoder if needed.
        } catch (_: Exception) {
            // Fall through to fallback
        }
    }

    if (results.isEmpty()) {
        results.addAll(searchFallbackCities(query))
    }

    results.take(8)
}
