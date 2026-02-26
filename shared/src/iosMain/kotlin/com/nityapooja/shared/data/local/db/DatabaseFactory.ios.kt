package com.nityapooja.shared.data.local.db

import androidx.room.Room
import androidx.room.RoomDatabase
import platform.Foundation.NSHomeDirectory

actual fun getDatabaseBuilder(context: Any?): RoomDatabase.Builder<NityaPoojaDatabase> {
    val dbFilePath = NSHomeDirectory() + "/Documents/nityapooja_database"
    return Room.databaseBuilder<NityaPoojaDatabase>(
        name = dbFilePath,
    )
}
