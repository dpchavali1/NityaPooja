package com.nityapooja.shared.data.local.db

import androidx.room.RoomDatabase

expect fun getDatabaseBuilder(context: Any? = null): RoomDatabase.Builder<NityaPoojaDatabase>
