package com.nityapooja.shared.platform

import android.content.Intent
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
