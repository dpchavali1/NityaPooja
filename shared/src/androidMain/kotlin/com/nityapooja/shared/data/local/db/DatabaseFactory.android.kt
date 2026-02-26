package com.nityapooja.shared.data.local.db

import android.content.Context
import androidx.room.Room
import androidx.room.RoomDatabase

actual fun getDatabaseBuilder(context: Any?): RoomDatabase.Builder<NityaPoojaDatabase> {
    val appContext = context as Context
    val dbFile = appContext.getDatabasePath("nityapooja_database")
    return Room.databaseBuilder<NityaPoojaDatabase>(
        context = appContext,
        name = dbFile.absolutePath,
    )
}
