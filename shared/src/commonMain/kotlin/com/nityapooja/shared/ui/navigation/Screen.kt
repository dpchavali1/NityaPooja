package com.nityapooja.shared.ui.navigation

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

    data object ChalisaList : Screen("chalisa_list")
    data object ChalisaDetail : Screen("chalisa_detail/{id}") {
        fun createRoute(id: Int) = "chalisa_detail/$id"
    }
    data object Rashifal : Screen("rashifal")
    data object GuidedPuja : Screen("guided_puja")
    data object PoojaTimer : Screen("pooja_timer")

    data object JatakaChakram : Screen("jataka_chakram")
    data object GunaMilan : Screen("guna_milan")
    data object SavedProfiles : Screen("saved_profiles")

    data object VirtualPoojaRoom : Screen("virtual_pooja_room")
    data object PrivacyPolicy : Screen("privacy_policy")
    data object Onboarding : Screen("onboarding")
    data object DevotionalStore : Screen("devotional_store")

    data object MantraChanting : Screen("mantra_chanting/{id}/{type}") {
        fun createRoute(id: Int, type: String) = "mantra_chanting/$id/$type"
    }

    data object PuranaQuiz : Screen("purana_quiz")
}

data class BottomNavItem(
    val screen: Screen,
    val label: String,
    val labelTelugu: String,
    val selectedIcon: ImageVector,
    val unselectedIcon: ImageVector,
)

val bottomNavItems = listOf(
    BottomNavItem(Screen.Home, "Home", "\u0C39\u0C4B\u0C2E\u0C4D", Icons.Filled.Home, Icons.Outlined.Home),
    BottomNavItem(Screen.AartiList, "Aarti", "\u0C39\u0C3E\u0C30\u0C24\u0C3F", Icons.Filled.MusicNote, Icons.Outlined.MusicNote),
    BottomNavItem(Screen.Panchangam, "Panchangam", "\u0C2A\u0C02\u0C1A\u0C3E\u0C02\u0C17\u0C02", Icons.Filled.CalendarMonth, Icons.Outlined.CalendarMonth),
    BottomNavItem(Screen.VirtualPoojaRoom, "Pooja", "\u0C2A\u0C42\u0C1C", Icons.Filled.VolunteerActivism, Icons.Outlined.VolunteerActivism),
    BottomNavItem(Screen.More, "More", "\u0C2E\u0C30\u0C3F\u0C28\u0C4D\u0C28\u0C3F", Icons.Filled.MoreHoriz, Icons.Outlined.MoreHoriz),
)
