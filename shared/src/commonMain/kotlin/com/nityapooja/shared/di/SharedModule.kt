package com.nityapooja.shared.di

import androidx.sqlite.driver.bundled.BundledSQLiteDriver
import androidx.room.RoomDatabase
import com.nityapooja.shared.data.local.db.DatabaseSeeder
import com.nityapooja.shared.data.local.db.NityaPoojaDatabase
import com.nityapooja.shared.data.repository.DevotionalRepository
import com.nityapooja.shared.data.spotify.SpotifyApi
import com.nityapooja.shared.data.spotify.SpotifyCredentials
import io.ktor.client.*
import io.ktor.client.plugins.contentnegotiation.*
import io.ktor.client.plugins.logging.*
import io.ktor.serialization.kotlinx.json.*
import kotlinx.serialization.json.Json
import com.nityapooja.shared.ui.aarti.AartiViewModel
import com.nityapooja.shared.ui.ashtotra.AshtotraViewModel
import com.nityapooja.shared.ui.audio.AudioPlayerViewModel
import com.nityapooja.shared.ui.bhajan.BhajanViewModel
import com.nityapooja.shared.ui.chalisa.ChalisaViewModel
import com.nityapooja.shared.ui.components.FontSizeViewModel
import com.nityapooja.shared.ui.deity.DeityViewModel
import com.nityapooja.shared.ui.festival.FestivalViewModel
import com.nityapooja.shared.ui.gunamilan.GunaMilanViewModel
import com.nityapooja.shared.ui.home.HomeViewModel
import com.nityapooja.shared.ui.japa.JapaViewModel
import com.nityapooja.shared.ui.jataka.JatakaChakramViewModel
import com.nityapooja.shared.ui.jataka.SavedProfilesViewModel
import com.nityapooja.shared.ui.keertana.KeertanaViewModel
import com.nityapooja.shared.ui.mantra.MantraChantingViewModel
import com.nityapooja.shared.ui.mantra.MantraViewModel
import com.nityapooja.shared.ui.onboarding.OnboardingViewModel
import com.nityapooja.shared.ui.panchangam.PanchangamViewModel
import com.nityapooja.shared.ui.profile.ProfileViewModel
import com.nityapooja.shared.ui.puja.GuidedPujaViewModel
import com.nityapooja.shared.ui.rashifal.RashifalViewModel
import com.nityapooja.shared.ui.search.SearchViewModel
import com.nityapooja.shared.ui.settings.SettingsViewModel
import com.nityapooja.shared.ui.stotram.StotramViewModel
import com.nityapooja.shared.ui.suprabhatam.SuprabhatamViewModel
import com.nityapooja.shared.ui.temple.TempleViewModel
import com.nityapooja.shared.ui.virtualpooja.VirtualPoojaRoomViewModel
import org.koin.core.module.dsl.singleOf
import org.koin.core.module.dsl.viewModelOf
import org.koin.dsl.module

val sharedModule = module {
    // Database - builder is provided by platform modules
    single<NityaPoojaDatabase> {
        get<RoomDatabase.Builder<NityaPoojaDatabase>>()
            .setDriver(BundledSQLiteDriver())
            .fallbackToDestructiveMigration(dropAllTables = true)
            .build()
    }

    // DAOs
    single { get<NityaPoojaDatabase>().deityDao() }
    single { get<NityaPoojaDatabase>().aartiDao() }
    single { get<NityaPoojaDatabase>().stotramDao() }
    single { get<NityaPoojaDatabase>().keertanaDao() }
    single { get<NityaPoojaDatabase>().mantraDao() }
    single { get<NityaPoojaDatabase>().bhajanDao() }
    single { get<NityaPoojaDatabase>().suprabhatamDao() }
    single { get<NityaPoojaDatabase>().ashtotraDao() }
    single { get<NityaPoojaDatabase>().templeDao() }
    single { get<NityaPoojaDatabase>().festivalDao() }
    single { get<NityaPoojaDatabase>().bookmarkDao() }
    single { get<NityaPoojaDatabase>().shlokaDao() }
    single { get<NityaPoojaDatabase>().japaSessionDao() }
    single { get<NityaPoojaDatabase>().chalisaDao() }
    single { get<NityaPoojaDatabase>().rashiDao() }
    single { get<NityaPoojaDatabase>().pujaStepDao() }
    single { get<NityaPoojaDatabase>().readingHistoryDao() }
    single { get<NityaPoojaDatabase>().savedProfileDao() }

    // Database Seeder
    single { DatabaseSeeder(get(), get(), get(), get(), get(), get(), get(), get(), get(), get(), get(), get(), get(), get(), get()) }

    // Repository
    singleOf(::DevotionalRepository)

    // Ktor HttpClient
    single {
        HttpClient {
            install(ContentNegotiation) {
                json(Json {
                    ignoreUnknownKeys = true
                    isLenient = true
                })
            }
            install(Logging) {
                level = LogLevel.NONE
            }
        }
    }

    // Spotify API (Ktor-based) — credentials provided by platform modules
    single { SpotifyApi(get(), get()) }

    // ViewModels
    viewModelOf(::AartiViewModel)
    viewModelOf(::AshtotraViewModel)
    viewModelOf(::AudioPlayerViewModel)
    viewModelOf(::BhajanViewModel)
    viewModelOf(::ChalisaViewModel)
    viewModelOf(::FontSizeViewModel)
    viewModelOf(::DeityViewModel)
    viewModelOf(::FestivalViewModel)
    viewModelOf(::GunaMilanViewModel)
    viewModelOf(::HomeViewModel)
    viewModelOf(::JapaViewModel)
    viewModelOf(::JatakaChakramViewModel)
    viewModelOf(::SavedProfilesViewModel)
    viewModelOf(::KeertanaViewModel)
    viewModelOf(::MantraChantingViewModel)
    viewModelOf(::MantraViewModel)
    viewModelOf(::OnboardingViewModel)
    viewModelOf(::PanchangamViewModel)
    viewModelOf(::ProfileViewModel)
    viewModelOf(::GuidedPujaViewModel)
    viewModelOf(::RashifalViewModel)
    viewModelOf(::SearchViewModel)
    viewModelOf(::SettingsViewModel)
    viewModelOf(::StotramViewModel)
    viewModelOf(::SuprabhatamViewModel)
    viewModelOf(::TempleViewModel)
    viewModelOf(::VirtualPoojaRoomViewModel)
}
