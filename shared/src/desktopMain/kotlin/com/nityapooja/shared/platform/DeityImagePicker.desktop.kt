package com.nityapooja.shared.platform

import androidx.compose.runtime.Composable

// Desktop: no-op stub — file picker not yet wired up.
@Composable
actual fun rememberDeityImagePicker(deityId: Int, onResult: (String?) -> Unit): () -> Unit {
    return { onResult(null) }
}
