package com.nityapooja.shared.platform

import android.net.Uri
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.PickVisualMediaRequest
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.runtime.Composable
import androidx.compose.ui.platform.LocalContext
import java.io.File

@Composable
actual fun rememberDeityImagePicker(deityId: Int, onResult: (String?) -> Unit): () -> Unit {
    val context = LocalContext.current
    val launcher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.PickVisualMedia(),
    ) { uri: Uri? ->
        if (uri == null) {
            onResult(null)
            return@rememberLauncherForActivityResult
        }
        val dir = File(context.filesDir, "deity_images").also { it.mkdirs() }
        val dest = File(dir, "deity_$deityId.jpg")
        try {
            context.contentResolver.openInputStream(uri)?.use { input ->
                dest.outputStream().use { output -> input.copyTo(output) }
            }
            onResult(dest.absolutePath)
        } catch (e: Exception) {
            onResult(null)
        }
    }
    return {
        launcher.launch(PickVisualMediaRequest(ActivityResultContracts.PickVisualMedia.ImageOnly))
    }
}
