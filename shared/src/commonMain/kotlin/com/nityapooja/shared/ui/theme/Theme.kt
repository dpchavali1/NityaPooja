package com.nityapooja.shared.ui.theme

import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.staticCompositionLocalOf
import androidx.compose.ui.graphics.Color

data class ExtendedColors(
    val goldGradientStart: Color = GoldShimmerStart,
    val goldGradientEnd: Color = GoldShimmerEnd,
    val goldGradientHighlight: Color = GoldShimmerHighlight,
    val goldGlow: Color = GoldGlow,
    val glassSurface: Color = GlassSurface,
    val glassBorder: Color = GlassBorder,
    val auspiciousGreen: Color = AuspiciousGreen,
    val warningAmber: Color = WarningAmber,
    val cardSurface: Color = WarmWhite,
)

val LocalExtendedColors = staticCompositionLocalOf { ExtendedColors() }

private val LightColorScheme = lightColorScheme(
    primary = TempleGold,
    onPrimary = DarkTeak,
    primaryContainer = TempleGold50,
    onPrimaryContainer = DarkTeak,
    secondary = DeepVermillion,
    onSecondary = IvoryWhite,
    secondaryContainer = DeepVermillionLight,
    onSecondaryContainer = DeepVermillionDark,
    tertiary = SacredTurmeric,
    onTertiary = DarkTeak,
    tertiaryContainer = SacredTurmericLight,
    onTertiaryContainer = SacredTurmericDark,
    background = IvoryWhite,
    onBackground = DarkTeak,
    surface = WarmWhite,
    onSurface = DarkTeak,
    surfaceVariant = IvoryCream,
    onSurfaceVariant = DarkTeakLight,
    surfaceContainerLowest = IvoryWhite,
    surfaceContainerLow = WarmWhite,
    surfaceContainer = IvoryCream,
    surfaceContainerHigh = IvoryDark,
    error = InauspiciousRed,
    outline = TempleGoldDark,
    outlineVariant = TempleGoldLight,
)

private val DarkColorScheme = darkColorScheme(
    primary = DarkPrimary,
    onPrimary = DarkOnPrimary,
    primaryContainer = TempleGoldDark,
    onPrimaryContainer = NightOnSurface,
    secondary = DarkSecondary,
    onSecondary = DarkOnPrimary,
    secondaryContainer = DeepVermillionDark,
    onSecondaryContainer = NightOnSurface,
    tertiary = SacredTurmericLight,
    onTertiary = DarkOnPrimary,
    background = NightBackground,
    onBackground = NightOnSurface,
    surface = NightSurface,
    onSurface = NightOnSurface,
    surfaceVariant = NightSurfaceVariant,
    onSurfaceVariant = NightOnSurface,
    surfaceContainerLowest = NightBackground,
    surfaceContainerLow = NightSurface,
    surfaceContainer = NightCard,
    surfaceContainerHigh = NightSurfaceVariant,
    error = InauspiciousRed,
    outline = DarkPrimary,
    outlineVariant = TempleGoldDark,
)

private val SaffronColorScheme = lightColorScheme(
    primary = SaffronPrimary,
    onPrimary = SaffronOnPrimary,
    primaryContainer = SaffronPrimaryLight,
    onPrimaryContainer = SaffronOnPrimary,
    secondary = DeepVermillion,
    onSecondary = IvoryWhite,
    secondaryContainer = DeepVermillionLight,
    onSecondaryContainer = DeepVermillionDark,
    tertiary = TempleGold,
    onTertiary = DarkTeak,
    tertiaryContainer = TempleGoldLight,
    onTertiaryContainer = TempleGoldDark,
    background = SaffronBackground,
    onBackground = SaffronOnSurface,
    surface = SaffronSurface,
    onSurface = SaffronOnSurface,
    surfaceVariant = SaffronSurfaceVariant,
    onSurfaceVariant = SaffronOnSurfaceVariant,
    surfaceContainerLowest = SaffronBackground,
    surfaceContainerLow = SaffronSurface,
    surfaceContainer = SaffronSurfaceVariant,
    surfaceContainerHigh = SaffronSurfaceHigh,
    error = InauspiciousRed,
    outline = SaffronPrimaryDark,
    outlineVariant = SaffronPrimaryLight,
)

@Composable
fun NityaPoojaTheme(
    darkTheme: Boolean = isSystemInDarkTheme(),
    forceDark: Boolean? = null,
    saffronTheme: Boolean = false,
    content: @Composable () -> Unit
) {
    val isDark = if (saffronTheme) false else (forceDark ?: darkTheme)
    val colorScheme = when {
        saffronTheme -> SaffronColorScheme
        isDark -> DarkColorScheme
        else -> LightColorScheme
    }

    val extendedColors = if (isDark) {
        ExtendedColors(
            glassSurface = GlassSurfaceDark,
            glassBorder = GlassBorderDark,
            cardSurface = NightCard,
        )
    } else {
        ExtendedColors()
    }

    CompositionLocalProvider(
        LocalExtendedColors provides extendedColors,
    ) {
        MaterialTheme(
            colorScheme = colorScheme,
            typography = Typography,
            shapes = Shapes,
            content = content,
        )
    }
}
