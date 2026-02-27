package com.nityapooja.shared.ui.navigation

import androidx.compose.animation.AnimatedContentTransitionScope
import androidx.compose.animation.core.tween
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import org.koin.compose.viewmodel.koinViewModel
import androidx.navigation.NavDestination.Companion.hierarchy
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import com.nityapooja.shared.ui.home.HomeScreen
import com.nityapooja.shared.ui.aarti.AartiListScreen
import com.nityapooja.shared.ui.aarti.AartiDetailScreen
import com.nityapooja.shared.ui.mantra.MantraListScreen
import com.nityapooja.shared.ui.mantra.MantraDetailScreen
import com.nityapooja.shared.ui.stotram.StotramListScreen
import com.nityapooja.shared.ui.stotram.StotramDetailScreen
import com.nityapooja.shared.ui.keertana.KeertanaListScreen
import com.nityapooja.shared.ui.keertana.KeertanaDetailScreen
import com.nityapooja.shared.ui.bhajan.BhajanListScreen
import com.nityapooja.shared.ui.bhajan.BhajanDetailScreen
import com.nityapooja.shared.ui.suprabhatam.SuprabhatamListScreen
import com.nityapooja.shared.ui.suprabhatam.SuprabhatamDetailScreen
import com.nityapooja.shared.ui.ashtotra.AshtotraListScreen
import com.nityapooja.shared.ui.ashtotra.AshtotraDetailScreen
import com.nityapooja.shared.ui.temple.TempleListScreen
import com.nityapooja.shared.ui.temple.TempleDetailScreen
import com.nityapooja.shared.ui.festival.FestivalListScreen
import com.nityapooja.shared.ui.deity.DeityDetailScreen
import com.nityapooja.shared.ui.more.MoreScreen
import com.nityapooja.shared.ui.panchangam.PanchangamScreen
import com.nityapooja.shared.ui.japa.JapaCounterScreen
import com.nityapooja.shared.ui.search.SearchScreen
import com.nityapooja.shared.ui.profile.ProfileScreen
import com.nityapooja.shared.ui.settings.PrivacyPolicyScreen
import com.nityapooja.shared.ui.settings.SettingsScreen
import com.nityapooja.shared.ui.chalisa.ChalisaListScreen
import com.nityapooja.shared.ui.chalisa.ChalisaDetailScreen
import com.nityapooja.shared.ui.rashifal.RashifalScreen
import com.nityapooja.shared.ui.puja.GuidedPujaScreen
import com.nityapooja.shared.ui.timer.PoojaTimerScreen
import com.nityapooja.shared.ui.jataka.JatakaChakramScreen
import com.nityapooja.shared.ui.jataka.SavedProfilesScreen
import com.nityapooja.shared.ui.gunamilan.GunaMilanScreen
import com.nityapooja.shared.ui.virtualpooja.VirtualPoojaRoomScreen
import com.nityapooja.shared.ui.audio.AudioMiniPlayer
import com.nityapooja.shared.ui.audio.AudioPlayerViewModel
import com.nityapooja.shared.ui.components.PremiumBottomBar
import com.nityapooja.shared.ui.mantra.MantraChantingScreen
import com.nityapooja.shared.ui.onboarding.OnboardingScreen
import com.nityapooja.shared.ui.store.DevotionalStoreScreen

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun NityaPoojaNavHost(
    onboardingCompleted: Boolean = false,
    onLinkSpotify: (() -> Unit)? = null,
    onUnlinkSpotify: (() -> Unit)? = null,
    spotifyLinked: Boolean = false,
    spotifyConnecting: Boolean = false,
    spotifyInstalled: Boolean = false,
    bannerAd: (@Composable () -> Unit)? = null,
) {
    val navController = rememberNavController()
    val navBackStackEntry by navController.currentBackStackEntryAsState()
    val currentDestination = navBackStackEntry?.destination
    val audioViewModel: AudioPlayerViewModel = koinViewModel()

    // Seed database on first launch
    val seeder = org.koin.compose.koinInject<com.nityapooja.shared.data.local.db.DatabaseSeeder>()
    LaunchedEffect(Unit) {
        seeder.seed()
    }

    val startDestination = if (onboardingCompleted) Screen.Home.route else Screen.Onboarding.route

    val bottomBarScreens = bottomNavItems.map { it.screen.route }
    val showBottomBar = currentDestination?.route in bottomBarScreens

    Scaffold(
        bottomBar = {
            if (showBottomBar) {
                Column {
                    AudioMiniPlayer(viewModel = audioViewModel)
                    PremiumBottomBar(
                        items = bottomNavItems,
                        currentRoute = currentDestination?.route,
                        onItemClick = { item ->
                            navController.navigate(item.screen.route) {
                                popUpTo(Screen.Home.route) {
                                    saveState = true
                                }
                                launchSingleTop = true
                                restoreState = true
                            }
                        },
                        onJapaClick = {
                            navController.navigate(Screen.JapaCounter.route)
                        },
                    )
                }
            }
        }
    ) { innerPadding ->
        NavHost(
            navController = navController,
            startDestination = startDestination,
            modifier = Modifier.padding(innerPadding),
            enterTransition = { slideIntoContainer(AnimatedContentTransitionScope.SlideDirection.Left, tween(300)) },
            exitTransition = { slideOutOfContainer(AnimatedContentTransitionScope.SlideDirection.Left, tween(300)) },
            popEnterTransition = { slideIntoContainer(AnimatedContentTransitionScope.SlideDirection.Right, tween(300)) },
            popExitTransition = { slideOutOfContainer(AnimatedContentTransitionScope.SlideDirection.Right, tween(300)) },
        ) {
            // Onboarding
            composable(Screen.Onboarding.route) {
                OnboardingScreen(
                    onComplete = {
                        navController.navigate(Screen.Home.route) {
                            popUpTo(Screen.Onboarding.route) { inclusive = true }
                        }
                    },
                )
            }

            // Bottom Nav Screens
            composable(Screen.Home.route) {
                HomeScreen(
                    onNavigateToAartiDetail = { id -> navController.navigate(Screen.AartiDetail.createRoute(id)) },
                    onNavigateToStotrams = { navController.navigate(Screen.StotramList.route) },
                    onNavigateToKeertanalu = { navController.navigate(Screen.KeertanaList.route) },
                    onNavigateToSearch = { navController.navigate(Screen.Search.route) },
                    onNavigateToSettings = { navController.navigate(Screen.Settings.route) },
                    onNavigateToProfile = { navController.navigate(Screen.Profile.route) },
                    onNavigateToTemples = { navController.navigate(Screen.TempleList.route) },
                    onNavigateToFestivals = { navController.navigate(Screen.FestivalList.route) },
                    onNavigateToJapa = { navController.navigate(Screen.JapaCounter.route) },
                    onNavigateToDeityDetail = { id -> navController.navigate(Screen.DeityDetail.createRoute(id)) },
                    onNavigateToAartis = { navController.navigate(Screen.AartiList.route) },
                    onNavigateToPanchangam = {
                        navController.navigate(Screen.Panchangam.route) {
                            popUpTo(Screen.Home.route) {
                                saveState = true
                            }
                            launchSingleTop = true
                            restoreState = true
                        }
                    },
                    onNavigateToRashifal = { navController.navigate(Screen.Rashifal.route) },
                    onNavigateToBookmark = { type, id ->
                        when (type) {
                            "aarti" -> navController.navigate(Screen.AartiDetail.createRoute(id))
                            "stotram" -> navController.navigate(Screen.StotramDetail.createRoute(id))
                            "keertana" -> navController.navigate(Screen.KeertanaDetail.createRoute(id))
                            "mantra" -> navController.navigate(Screen.MantraDetail.createRoute(id))
                            "bhajan" -> navController.navigate(Screen.BhajanDetail.createRoute(id))
                            "suprabhatam" -> navController.navigate(Screen.SuprabhatamDetail.createRoute(id))
                            "ashtotra" -> navController.navigate(Screen.AshtotraDetail.createRoute(id))
                            "temple" -> navController.navigate(Screen.TempleDetail.createRoute(id))
                            "chalisa" -> navController.navigate(Screen.ChalisaDetail.createRoute(id))
                        }
                    },
                    bannerAd = bannerAd,
                )
            }
            composable(Screen.AartiList.route) {
                AartiListScreen(
                    onAartiClick = { id -> navController.navigate(Screen.AartiDetail.createRoute(id)) },
                    onBack = { navController.popBackStack() },
                    bannerAd = bannerAd,
                )
            }
            composable(Screen.Panchangam.route) { PanchangamScreen(bannerAd = bannerAd) }
            composable(Screen.VirtualPoojaRoom.route) {
                VirtualPoojaRoomScreen(
                    onBack = { navController.popBackStack() },
                    audioViewModel = audioViewModel,
                )
            }
            composable(Screen.More.route) {
                MoreScreen(
                    onNavigateToStore = { navController.navigate(Screen.DevotionalStore.route) },
                    onNavigateToStotrams = { navController.navigate(Screen.StotramList.route) },
                    onNavigateToKeertanalu = { navController.navigate(Screen.KeertanaList.route) },
                    onNavigateToTemples = { navController.navigate(Screen.TempleList.route) },
                    onNavigateToFestivals = { navController.navigate(Screen.FestivalList.route) },
                    onNavigateToSettings = { navController.navigate(Screen.Settings.route) },
                    onNavigateToJapa = { navController.navigate(Screen.JapaCounter.route) },
                    onNavigateToBhajans = { navController.navigate(Screen.BhajanList.route) },
                    onNavigateToSuprabhatam = { navController.navigate(Screen.SuprabhatamList.route) },
                    onNavigateToAshtotra = { navController.navigate(Screen.AshtotraList.route) },
                    onNavigateToMantras = { navController.navigate(Screen.MantraList.route) },
                    onNavigateToChalisas = { navController.navigate(Screen.ChalisaList.route) },
                    onNavigateToRashifal = { navController.navigate(Screen.Rashifal.route) },
                    onNavigateToGuidedPuja = { navController.navigate(Screen.GuidedPuja.route) },
                    onNavigateToPoojaTimer = { navController.navigate(Screen.PoojaTimer.route) },
                    onNavigateToJatakaChakram = { navController.navigate(Screen.JatakaChakram.route) },
                    onNavigateToGunaMilan = { navController.navigate(Screen.GunaMilan.route) },
                    onNavigateToSavedProfiles = { navController.navigate(Screen.SavedProfiles.route) },
                    onNavigateToVirtualPoojaRoom = { navController.navigate(Screen.VirtualPoojaRoom.route) },
                    bannerAd = bannerAd,
                )
            }

            // Detail Screens
            composable(
                Screen.AartiDetail.route,
                arguments = listOf(navArgument("id") { type = NavType.IntType })
            ) { backStackEntry ->
                AartiDetailScreen(
                    aartiId = backStackEntry.arguments?.getInt("id") ?: 0,
                    onBack = { navController.popBackStack() },
                    audioViewModel = audioViewModel,
                    bannerAd = bannerAd,
                )
            }

            composable(Screen.StotramList.route) {
                StotramListScreen(
                    onStotramClick = { id -> navController.navigate(Screen.StotramDetail.createRoute(id)) },
                    onBack = { navController.popBackStack() },
                    bannerAd = bannerAd,
                )
            }
            composable(
                Screen.StotramDetail.route,
                arguments = listOf(navArgument("id") { type = NavType.IntType })
            ) { backStackEntry ->
                StotramDetailScreen(
                    stotramId = backStackEntry.arguments?.getInt("id") ?: 0,
                    onBack = { navController.popBackStack() },
                    onStartChanting = { id, type -> navController.navigate(Screen.MantraChanting.createRoute(id, type)) },
                    audioViewModel = audioViewModel,
                    bannerAd = bannerAd,
                )
            }

            composable(Screen.KeertanaList.route) {
                KeertanaListScreen(
                    onKeertanaClick = { id -> navController.navigate(Screen.KeertanaDetail.createRoute(id)) },
                    onBack = { navController.popBackStack() },
                    bannerAd = bannerAd,
                )
            }
            composable(
                Screen.KeertanaDetail.route,
                arguments = listOf(navArgument("id") { type = NavType.IntType })
            ) { backStackEntry ->
                KeertanaDetailScreen(
                    keertanaId = backStackEntry.arguments?.getInt("id") ?: 0,
                    onBack = { navController.popBackStack() },
                    audioViewModel = audioViewModel,
                    bannerAd = bannerAd,
                )
            }

            composable(Screen.MantraList.route) {
                MantraListScreen(
                    onMantraClick = { id -> navController.navigate(Screen.MantraDetail.createRoute(id)) },
                    onJapaClick = { navController.navigate(Screen.JapaCounter.route) },
                    bannerAd = bannerAd,
                )
            }
            composable(
                Screen.MantraDetail.route,
                arguments = listOf(navArgument("id") { type = NavType.IntType })
            ) { backStackEntry ->
                MantraDetailScreen(
                    mantraId = backStackEntry.arguments?.getInt("id") ?: 0,
                    onBack = { navController.popBackStack() },
                    onStartChanting = { id, type -> navController.navigate(Screen.MantraChanting.createRoute(id, type)) },
                    audioViewModel = audioViewModel,
                    bannerAd = bannerAd,
                )
            }

            composable(Screen.BhajanList.route) {
                BhajanListScreen(
                    onBhajanClick = { id -> navController.navigate(Screen.BhajanDetail.createRoute(id)) },
                    onBack = { navController.popBackStack() },
                    bannerAd = bannerAd,
                )
            }
            composable(
                Screen.BhajanDetail.route,
                arguments = listOf(navArgument("id") { type = NavType.IntType })
            ) { backStackEntry ->
                BhajanDetailScreen(
                    bhajanId = backStackEntry.arguments?.getInt("id") ?: 0,
                    onBack = { navController.popBackStack() },
                    audioViewModel = audioViewModel,
                    bannerAd = bannerAd,
                )
            }

            composable(Screen.SuprabhatamList.route) {
                SuprabhatamListScreen(
                    onSuprabhatamClick = { id -> navController.navigate(Screen.SuprabhatamDetail.createRoute(id)) },
                    onBack = { navController.popBackStack() },
                    bannerAd = bannerAd,
                )
            }
            composable(
                Screen.SuprabhatamDetail.route,
                arguments = listOf(navArgument("id") { type = NavType.IntType })
            ) { backStackEntry ->
                SuprabhatamDetailScreen(
                    suprabhatamId = backStackEntry.arguments?.getInt("id") ?: 0,
                    onBack = { navController.popBackStack() },
                    audioViewModel = audioViewModel,
                    bannerAd = bannerAd,
                )
            }

            composable(Screen.AshtotraList.route) {
                AshtotraListScreen(
                    onAshtotraClick = { id -> navController.navigate(Screen.AshtotraDetail.createRoute(id)) },
                    onBack = { navController.popBackStack() },
                    bannerAd = bannerAd,
                )
            }
            composable(
                Screen.AshtotraDetail.route,
                arguments = listOf(navArgument("id") { type = NavType.IntType })
            ) { backStackEntry ->
                AshtotraDetailScreen(
                    ashtotraId = backStackEntry.arguments?.getInt("id") ?: 0,
                    onBack = { navController.popBackStack() },
                    audioViewModel = audioViewModel,
                    bannerAd = bannerAd,
                )
            }

            composable(Screen.TempleList.route) {
                TempleListScreen(
                    onTempleClick = { id -> navController.navigate(Screen.TempleDetail.createRoute(id)) },
                    onBack = { navController.popBackStack() },
                    bannerAd = bannerAd,
                )
            }
            composable(
                Screen.TempleDetail.route,
                arguments = listOf(navArgument("id") { type = NavType.IntType })
            ) { backStackEntry ->
                TempleDetailScreen(
                    templeId = backStackEntry.arguments?.getInt("id") ?: 0,
                    onBack = { navController.popBackStack() }
                )
            }

            composable(Screen.FestivalList.route) {
                FestivalListScreen(
                    onBack = { navController.popBackStack() },
                    bannerAd = bannerAd,
                )
            }

            composable(
                Screen.DeityDetail.route,
                arguments = listOf(navArgument("id") { type = NavType.IntType })
            ) { backStackEntry ->
                DeityDetailScreen(
                    deityId = backStackEntry.arguments?.getInt("id") ?: 0,
                    onBack = { navController.popBackStack() },
                    onAartiClick = { id -> navController.navigate(Screen.AartiDetail.createRoute(id)) },
                    onStotramClick = { id -> navController.navigate(Screen.StotramDetail.createRoute(id)) },
                    onMantraClick = { id -> navController.navigate(Screen.MantraDetail.createRoute(id)) },
                    onKeertanaClick = { id -> navController.navigate(Screen.KeertanaDetail.createRoute(id)) },
                    onChalisaClick = { id -> navController.navigate(Screen.ChalisaDetail.createRoute(id)) },
                    onSuprabhatamClick = { id -> navController.navigate(Screen.SuprabhatamDetail.createRoute(id)) },
                    onAshtotraClick = { id -> navController.navigate(Screen.AshtotraDetail.createRoute(id)) },
                    bannerAd = bannerAd,
                )
            }

            composable(Screen.JapaCounter.route) {
                JapaCounterScreen(onBack = { navController.popBackStack() })
            }
            composable(Screen.Settings.route) {
                SettingsScreen(
                    onBack = { navController.popBackStack() },
                    onNavigateToPrivacyPolicy = { navController.navigate(Screen.PrivacyPolicy.route) },
                    onLinkSpotify = onLinkSpotify,
                    onUnlinkSpotify = onUnlinkSpotify,
                    spotifyLinked = spotifyLinked,
                    spotifyConnecting = spotifyConnecting,
                    spotifyInstalled = spotifyInstalled,
                )
            }
            composable(Screen.PrivacyPolicy.route) {
                PrivacyPolicyScreen(onBack = { navController.popBackStack() })
            }
            composable(Screen.Search.route) {
                SearchScreen(
                    onBackClick = { navController.popBackStack() },
                    onAartiClick = { id -> navController.navigate(Screen.AartiDetail.createRoute(id)) },
                    onStotramClick = { id -> navController.navigate(Screen.StotramDetail.createRoute(id)) },
                    onKeertanaClick = { id -> navController.navigate(Screen.KeertanaDetail.createRoute(id)) },
                    onTempleClick = { id -> navController.navigate(Screen.TempleDetail.createRoute(id)) },
                    onMantraClick = { id -> navController.navigate(Screen.MantraDetail.createRoute(id)) },
                    onBhajanClick = { id -> navController.navigate(Screen.BhajanDetail.createRoute(id)) },
                    onChalisaClick = { id -> navController.navigate(Screen.ChalisaDetail.createRoute(id)) },
                )
            }
            composable(Screen.Profile.route) {
                ProfileScreen(
                    onBackClick = { navController.popBackStack() },
                    onBookmarkClick = { type, id ->
                        when (type) {
                            "aarti" -> navController.navigate(Screen.AartiDetail.createRoute(id))
                            "stotram" -> navController.navigate(Screen.StotramDetail.createRoute(id))
                            "keertana" -> navController.navigate(Screen.KeertanaDetail.createRoute(id))
                            "mantra" -> navController.navigate(Screen.MantraDetail.createRoute(id))
                            "bhajan" -> navController.navigate(Screen.BhajanDetail.createRoute(id))
                            "suprabhatam" -> navController.navigate(Screen.SuprabhatamDetail.createRoute(id))
                            "ashtotra" -> navController.navigate(Screen.AshtotraDetail.createRoute(id))
                            "temple" -> navController.navigate(Screen.TempleDetail.createRoute(id))
                            "chalisa" -> navController.navigate(Screen.ChalisaDetail.createRoute(id))
                        }
                    },
                )
            }

            // New Feature Screens
            composable(Screen.ChalisaList.route) {
                ChalisaListScreen(
                    onChalisaClick = { id -> navController.navigate(Screen.ChalisaDetail.createRoute(id)) },
                    onBack = { navController.popBackStack() },
                    bannerAd = bannerAd,
                )
            }
            composable(
                Screen.ChalisaDetail.route,
                arguments = listOf(navArgument("id") { type = NavType.IntType })
            ) { backStackEntry ->
                ChalisaDetailScreen(
                    chalisaId = backStackEntry.arguments?.getInt("id") ?: 0,
                    onBack = { navController.popBackStack() },
                    onStartChanting = { id, type -> navController.navigate(Screen.MantraChanting.createRoute(id, type)) },
                    audioViewModel = audioViewModel,
                    bannerAd = bannerAd,
                )
            }
            composable(Screen.Rashifal.route) {
                RashifalScreen(
                    onBack = { navController.popBackStack() },
                    bannerAd = bannerAd,
                )
            }
            composable(Screen.GuidedPuja.route) {
                GuidedPujaScreen(
                    onBack = { navController.popBackStack() },
                )
            }
            composable(Screen.PoojaTimer.route) {
                PoojaTimerScreen(
                    onBack = { navController.popBackStack() },
                )
            }

            // Jyotish Feature Screens
            composable(Screen.JatakaChakram.route) {
                JatakaChakramScreen(
                    onBack = { navController.popBackStack() },
                    onNavigateToSavedProfiles = { navController.navigate(Screen.SavedProfiles.route) },
                )
            }
            composable(Screen.GunaMilan.route) {
                GunaMilanScreen(
                    onBack = { navController.popBackStack() },
                )
            }
            composable(Screen.SavedProfiles.route) {
                SavedProfilesScreen(
                    onBack = { navController.popBackStack() },
                )
            }

            // Devotional Store
            composable(Screen.DevotionalStore.route) {
                DevotionalStoreScreen(
                    onBack = { navController.popBackStack() },
                )
            }

            // Mantra Chanting Mode
            composable(
                Screen.MantraChanting.route,
                arguments = listOf(
                    navArgument("id") { type = NavType.IntType },
                    navArgument("type") { type = NavType.StringType },
                ),
            ) { backStackEntry ->
                MantraChantingScreen(
                    contentId = backStackEntry.arguments?.getInt("id") ?: 0,
                    contentType = backStackEntry.arguments?.getString("type") ?: "mantra",
                    onBack = { navController.popBackStack() },
                )
            }

        }
    }
}
