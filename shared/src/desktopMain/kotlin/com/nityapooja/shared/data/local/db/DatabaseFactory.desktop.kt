package com.nityapooja.shared.data.local.db

import androidx.room.Room
import androidx.room.RoomDatabase
import java.io.File

actual fun getDatabaseBuilder(context: Any?): RoomDatabase.Builder<NityaPoojaDatabase> {
    val dbDir = File(System.getProperty("user.home"), ".nityapooja")
    dbDir.mkdirs()
    val dbFile = File(dbDir, "nityapooja_database")
    return Room.databaseBuilder<NityaPoojaDatabase>(
        name = dbFile.absolutePath,
    )
}
