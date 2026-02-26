package com.nityapooja.shared.data.local.db

import androidx.room.ConstructedBy
import androidx.room.Database
import androidx.room.RoomDatabase
import androidx.room.RoomDatabaseConstructor
import com.nityapooja.shared.data.local.dao.*
import com.nityapooja.shared.data.local.entity.*

// Room KMP requires this for non-Android targets
@Suppress("NO_ACTUAL_FOR_EXPECT")
expect object NityaPoojaDatabaseConstructor : RoomDatabaseConstructor<NityaPoojaDatabase> {
    override fun initialize(): NityaPoojaDatabase
}

@Database(
    entities = [
        DeityEntity::class,
        AartiEntity::class,
        StotramEntity::class,
        KeertanaEntity::class,
        MantraEntity::class,
        BhajanEntity::class,
        SuprabhatamEntity::class,
        AshtotraEntity::class,
        TempleEntity::class,
        FestivalEntity::class,
        BookmarkEntity::class,
        ShlokaEntity::class,
        JapaSessionEntity::class,
        ChalisaEntity::class,
        RashiEntity::class,
        PujaStepEntity::class,
        ReadingHistoryEntity::class,
    ],
    version = 8,
    exportSchema = true,
)
@ConstructedBy(NityaPoojaDatabaseConstructor::class)
abstract class NityaPoojaDatabase : RoomDatabase() {
    abstract fun deityDao(): DeityDao
    abstract fun aartiDao(): AartiDao
    abstract fun stotramDao(): StotramDao
    abstract fun keertanaDao(): KeertanaDao
    abstract fun mantraDao(): MantraDao
    abstract fun bhajanDao(): BhajanDao
    abstract fun suprabhatamDao(): SuprabhatamDao
    abstract fun ashtotraDao(): AshtotraDao
    abstract fun templeDao(): TempleDao
    abstract fun festivalDao(): FestivalDao
    abstract fun bookmarkDao(): BookmarkDao
    abstract fun shlokaDao(): ShlokaDao
    abstract fun japaSessionDao(): JapaSessionDao
    abstract fun chalisaDao(): ChalisaDao
    abstract fun rashiDao(): RashiDao
    abstract fun pujaStepDao(): PujaStepDao
    abstract fun readingHistoryDao(): ReadingHistoryDao
}
