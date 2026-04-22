package com.nityapooja.shared.platform

import androidx.compose.runtime.Composable

/**
 * Returns a lambda that, when invoked, launches the platform photo picker.
 * On Android, this uses ActivityResultContracts.PickVisualMedia.
 * On iOS and Desktop, returns a no-op.
 *
 * [deityId]  — used to form the saved file name (deity_{id}.jpg)
 * [onResult] — called with the saved absolute file path, or null if cancelled/unsupported
 */
@Composable
expect fun rememberDeityImagePicker(deityId: Int, onResult: (String?) -> Unit): () -> Unit
