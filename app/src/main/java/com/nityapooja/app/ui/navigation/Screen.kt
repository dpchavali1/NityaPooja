package com.nityapooja.app.ui.navigation

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.CalendarMonth
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.MoreHoriz
import androidx.compose.material.icons.filled.MusicNote
import androidx.compose.material.icons.filled.VolunteerActivism
import androidx.compose.material.icons.outlined.CalendarMonth
import androidx.compose.material.icons.outlined.Home
import androidx.compose.material.icons.outlined.MoreHoriz
import androidx.compose.material.icons.outlined.MusicNote
import androidx.compose.material.icons.outlined.VolunteerActivism
import androidx.compose.ui.graphics.vector.ImageVector

sealed class Screen(val route: String) {
    // Bottom Nav
    data object Home : Screen("home")
    data object AartiList : Screen("aarti_list")
    data object MantraList : Screen("mantra_list")
    data object Panchangam : Screen("panchangam")
    data object More : Screen("more")

    // Detail Screens
    data object AartiDetail : Screen("aarti_detail/{id}") {
        fun createRoute(id: Int) = "aarti_detail/$id"
    }
    data object StotramList : Screen("stotram_list")
    data object StotramDetail : Screen("stotram_detail/{id}") {
        fun createRoute(id: Int) = "stotram_detail/$id"
    }
    data object KeertanaList : Screen("keertana_list")
    data object KeertanaDetail : Screen("keertana_detail/{id}") {
        fun createRoute(id: Int) = "keertana_detail/$id"
    }
    data object MantraDetail : Screen("mantra_detail/{id}") {
        fun createRoute(id: Int) = "mantra_detail/$id"
    }
    data object BhajanList : Screen("bhajan_list")
    data object BhajanDetail : Screen("bhajan_detail/{id}") {
        fun createRoute(id: Int) = "bhajan_detail/$id"
    }
    data object SuprabhatamList : Screen("suprabhatam_list")
    data object SuprabhatamDetail : Screen("suprabhatam_detail/{id}") {
        fun createRoute(id: Int) = "suprabhatam_detail/$id"
    }
    data object AshtotraList : Screen("ashtotra_list")
    data object AshtotraDetail : Screen("ashtotra_detail/{id}") {
        fun createRoute(id: Int) = "ashtotra_detail/$id"
    }
    data object TempleList : Screen("temple_list")
    data object TempleDetail : Screen("temple_detail/{id}") {
        fun createRoute(id: Int) = "temple_detail/$id"
    }
    data object FestivalList : Screen("festival_list")
    data object DeityDetail : Screen("deity_detail/{id}") {
        fun createRoute(id: Int) = "deity_detail/$id"
    }
    data object JapaCounter : Screen("japa_counter")
    data object Search : Screen("search")
    data object Settings : Screen("settings")
    data object Profile : Screen("profile")

    // New feature screens
    data object ChalisaList : Screen("chalisa_list")
    data object ChalisaDetail : Screen("chalisa_detail/{id}") {
        fun createRoute(id: Int) = "chalisa_detail/$id"
    }
    data object Rashifal : Screen("rashifal")
    data object GuidedPuja : Screen("guided_puja")
    data object PoojaTimer : Screen("pooja_timer")

    // Jyotish feature screens
    data object JatakaChakram : Screen("jataka_chakram")
    data object GunaMilan : Screen("guna_milan")

    // Virtual Pooja Room
    data object VirtualPoojaRoom : Screen("virtual_pooja_room")

    // Privacy Policy
    data object PrivacyPolicy : Screen("privacy_policy")

    // Onboarding
    data object Onboarding : Screen("onboarding")

    // Mantra Chanting Mode
    data object MantraChanting : Screen("mantra_chanting/{id}/{type}") {
        fun createRoute(id: Int, type: String) = "mantra_chanting/$id/$type"
    }
}

data class BottomNavItem(
    val screen: Screen,
    val label: String,
    val labelTelugu: String,
    val selectedIcon: ImageVector,
    val unselectedIcon: ImageVector,
)

// 5 items: Home, Aarti, [center Japa FAB], Panchangam, Pooja Room, More
val bottomNavItems = listOf(
    BottomNavItem(Screen.Home, "Home", "హోమ్", Icons.Filled.Home, Icons.Outlined.Home),
    BottomNavItem(Screen.AartiList, "Aarti", "హారతి", Icons.Filled.MusicNote, Icons.Outlined.MusicNote),
    BottomNavItem(Screen.Panchangam, "Panchangam", "పంచాంగం", Icons.Filled.CalendarMonth, Icons.Outlined.CalendarMonth),
    BottomNavItem(Screen.VirtualPoojaRoom, "Pooja", "పూజ", Icons.Filled.VolunteerActivism, Icons.Outlined.VolunteerActivism),
    BottomNavItem(Screen.More, "More", "మరిన్ని", Icons.Filled.MoreHoriz, Icons.Outlined.MoreHoriz),
)
