package com.nityapooja.shared.platform

import androidx.compose.runtime.Composable

// iOS: photo picker requires UIKit integration not yet wired up.
// Returns a no-op lambda; the UI hides the button when the result is null.
@Composable
actual fun rememberDeityImagePicker(deityId: Int, onResult: (String?) -> Unit): () -> Unit {
    return { onResult(null) }
}
