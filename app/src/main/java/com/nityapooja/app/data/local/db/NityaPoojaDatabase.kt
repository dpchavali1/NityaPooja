package com.nityapooja.app.data.local.db

import androidx.room.AutoMigration
import androidx.room.Database
import androidx.room.RoomDatabase
import com.nityapooja.app.data.local.dao.*
import com.nityapooja.app.data.local.entity.*

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
    autoMigrations = [
        AutoMigration(from = 1, to = 2),
        AutoMigration(from = 2, to = 3),
        AutoMigration(from = 3, to = 4),
        AutoMigration(from = 4, to = 5),
        AutoMigration(from = 5, to = 6),
        AutoMigration(from = 6, to = 7),
        AutoMigration(from = 7, to = 8),
    ],
)
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
