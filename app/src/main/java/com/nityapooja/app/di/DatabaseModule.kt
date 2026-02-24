package com.nityapooja.app.di

import android.content.Context
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.sqlite.db.SupportSQLiteDatabase
import com.nityapooja.app.data.local.dao.*
import com.nityapooja.app.data.local.db.NityaPoojaDatabase
import com.nityapooja.app.data.local.db.DatabaseSeeder
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.launch
import javax.inject.Provider
import javax.inject.Singleton
import com.nityapooja.app.BuildConfig

@Module
@InstallIn(SingletonComponent::class)
object DatabaseModule {

    @Provides
    @Singleton
    fun provideDatabase(
        @ApplicationContext context: Context,
        seederProvider: Provider<DatabaseSeeder>,
    ): NityaPoojaDatabase {
        return Room.databaseBuilder(
            context,
            NityaPoojaDatabase::class.java,
            "nityapooja_database"
        )
            .addCallback(object : RoomDatabase.Callback() {
                override fun onCreate(db: SupportSQLiteDatabase) {
                    super.onCreate(db)
                    // Seed on first install
                    CoroutineScope(SupervisorJob() + Dispatchers.IO).launch {
                        seederProvider.get().seed()
                    }
                }

                override fun onOpen(db: SupportSQLiteDatabase) {
                    super.onOpen(db)
                    // Re-seed when app version or content version changes
                    val prefs = context.getSharedPreferences("db_seed", Context.MODE_PRIVATE)
                    val lastSeededVersion = prefs.getInt("seeded_version_code", 0)
                    val lastContentVersion = prefs.getInt("seeded_content_version", 0)
                    val currentVersion = BuildConfig.VERSION_CODE
                    val currentContentVersion = DatabaseSeeder.CONTENT_VERSION
                    if (lastSeededVersion < currentVersion || lastContentVersion < currentContentVersion) {
                        CoroutineScope(SupervisorJob() + Dispatchers.IO).launch {
                            seederProvider.get().seed()
                            prefs.edit()
                                .putInt("seeded_version_code", currentVersion)
                                .putInt("seeded_content_version", currentContentVersion)
                                .apply()
                        }
                    }
                }
            })
            .build()
    }

    @Provides fun provideDeityDao(db: NityaPoojaDatabase): DeityDao = db.deityDao()
    @Provides fun provideAartiDao(db: NityaPoojaDatabase): AartiDao = db.aartiDao()
    @Provides fun provideStotramDao(db: NityaPoojaDatabase): StotramDao = db.stotramDao()
    @Provides fun provideKeertanaDao(db: NityaPoojaDatabase): KeertanaDao = db.keertanaDao()
    @Provides fun provideMantraDao(db: NityaPoojaDatabase): MantraDao = db.mantraDao()
    @Provides fun provideBhajanDao(db: NityaPoojaDatabase): BhajanDao = db.bhajanDao()
    @Provides fun provideSuprabhatamDao(db: NityaPoojaDatabase): SuprabhatamDao = db.suprabhatamDao()
    @Provides fun provideAshtotraDao(db: NityaPoojaDatabase): AshtotraDao = db.ashtotraDao()
    @Provides fun provideTempleDao(db: NityaPoojaDatabase): TempleDao = db.templeDao()
    @Provides fun provideFestivalDao(db: NityaPoojaDatabase): FestivalDao = db.festivalDao()
    @Provides fun provideBookmarkDao(db: NityaPoojaDatabase): BookmarkDao = db.bookmarkDao()
    @Provides fun provideShlokaDao(db: NityaPoojaDatabase): ShlokaDao = db.shlokaDao()
    @Provides fun provideJapaSessionDao(db: NityaPoojaDatabase): JapaSessionDao = db.japaSessionDao()
    @Provides fun provideChalisaDao(db: NityaPoojaDatabase): ChalisaDao = db.chalisaDao()
    @Provides fun provideRashiDao(db: NityaPoojaDatabase): RashiDao = db.rashiDao()
    @Provides fun providePujaStepDao(db: NityaPoojaDatabase): PujaStepDao = db.pujaStepDao()
    @Provides fun provideReadingHistoryDao(db: NityaPoojaDatabase): ReadingHistoryDao = db.readingHistoryDao()
}
