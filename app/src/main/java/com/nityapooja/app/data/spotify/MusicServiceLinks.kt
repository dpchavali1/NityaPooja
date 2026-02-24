package com.nityapooja.app.data.spotify

import android.content.Context
import android.content.Intent
import android.net.Uri

object MusicServiceLinks {

    fun openJioSaavnSearch(context: Context, query: String) {
        val encodedQuery = Uri.encode(query)
        val intent = Intent(Intent.ACTION_VIEW, Uri.parse("https://www.jiosaavn.com/search/$encodedQuery"))
        intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK
        context.startActivity(intent)
    }

    fun openWynkSearch(context: Context, query: String) {
        val encodedQuery = Uri.encode(query)
        val intent = Intent(Intent.ACTION_VIEW, Uri.parse("https://wynk.in/music/search/$encodedQuery"))
        intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK
        context.startActivity(intent)
    }

    fun openYouTubeMusicSearch(context: Context, query: String) {
        val encodedQuery = Uri.encode(query)
        val intent = Intent(Intent.ACTION_VIEW, Uri.parse("https://music.youtube.com/search?q=$encodedQuery"))
        intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK
        context.startActivity(intent)
    }
}
