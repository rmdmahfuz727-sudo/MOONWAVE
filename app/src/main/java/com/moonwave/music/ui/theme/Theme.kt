package com.moonwave.music.ui.theme

import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color

enum class ThemeMode {
    MIDNIGHT,
    OLED_BLACK,
    COSMIC_VIOLET,
    CYBER_NEON
}

@Composable
fun MoonwaveTheme(
    themeMode: ThemeMode = ThemeMode.MIDNIGHT,
    accentColor: Color = ElectricBlue,
    content: @Composable () -> Unit
) {
    val backgroundColor = when (themeMode) {
        ThemeMode.MIDNIGHT -> MidnightBlack
        ThemeMode.OLED_BLACK -> PitchBlack
        ThemeMode.COSMIC_VIOLET -> Color(0xFF0D0B18)
        ThemeMode.CYBER_NEON -> Color(0xFF050D1A)
    }

    val surfaceColor = when (themeMode) {
        ThemeMode.MIDNIGHT -> DeepNavy
        ThemeMode.OLED_BLACK -> Color(0xFF101010)
        ThemeMode.COSMIC_VIOLET -> Color(0xFF19132B)
        ThemeMode.CYBER_NEON -> Color(0xFF0B192C)
    }

    val colorScheme = darkColorScheme(
        primary = accentColor,
        onPrimary = MoonlightWhite,
        primaryContainer = accentColor.copy(alpha = 0.2f),
        onPrimaryContainer = MoonlightWhite,
        secondary = SubtlePurple,
        onSecondary = MoonlightWhite,
        tertiary = NeonCyan,
        background = backgroundColor,
        onBackground = MoonlightSilver,
        surface = surfaceColor,
        onSurface = MoonlightSilver,
        surfaceVariant = SurfaceNavyLight,
        onSurfaceVariant = MoonlightSubtext,
        outline = CardBorder
    )

    MaterialTheme(
        colorScheme = colorScheme,
        typography = MoonwaveTypography,
        content = content
    )
}
