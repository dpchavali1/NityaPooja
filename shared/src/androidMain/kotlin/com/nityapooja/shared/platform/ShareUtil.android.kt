package com.nityapooja.shared.platform

import android.content.Intent
import androidx.compose.ui.graphics.ImageBitmap
import androidx.compose.ui.graphics.asAndroidBitmap
import org.koin.core.context.GlobalContext

internal val appContext get() = GlobalContext.get().get<android.content.Context>()

actual fun shareText(text: String, title: String) {
    val intent = Intent(Intent.ACTION_SEND).apply {
        type = "text/plain"
        putExtra(Intent.EXTRA_TEXT, text)
        if (title.isNotBlank()) putExtra(Intent.EXTRA_SUBJECT, title)
        flags = Intent.FLAG_ACTIVITY_NEW_TASK
    }
    appContext.startActivity(Intent.createChooser(intent, "Share via").apply {
        flags = Intent.FLAG_ACTIVITY_NEW_TASK
    })
}

actual fun shareImage(bitmap: ImageBitmap, title: String) {
    val context = appContext
    val androidBitmap = bitmap.asAndroidBitmap()
    val cacheDir = java.io.File(context.cacheDir, "share_images").also { it.mkdirs() }
    val file = java.io.File(cacheDir, "nityapooja_share.png")
    file.outputStream().use { out ->
        androidBitmap.compress(android.graphics.Bitmap.CompressFormat.PNG, 95, out)
    }
    val uri = androidx.core.content.FileProvider.getUriForFile(
        context,
        "${context.packageName}.fileprovider",
        file,
    )
    val intent = Intent(Intent.ACTION_SEND).apply {
        type = "image/png"
        putExtra(Intent.EXTRA_STREAM, uri)
        putExtra(Intent.EXTRA_TEXT, title)
        addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION)
        addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
    }
    context.startActivity(Intent.createChooser(intent, "Share").apply {
        addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
    })
}
