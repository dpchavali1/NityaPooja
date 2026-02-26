package com.nityapooja.shared.ui.components

import android.app.DatePickerDialog
import android.app.TimePickerDialog
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.platform.LocalContext

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

