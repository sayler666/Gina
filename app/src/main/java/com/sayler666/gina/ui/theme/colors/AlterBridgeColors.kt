package com.sayler666.gina.ui.theme.colors

import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.ui.graphics.Color

object AlterBridgeColors {
    private val dark_primary = Color(0xFFD05730)
    private val dark_onPrimary = Color(0xFF2B0D00)
    private val dark_primaryContainer = Color(0xFF5D2410)
    private val dark_onPrimaryContainer = Color(0xFFFFDAD1)

    private val dark_secondary = Color(0xFF8BCEFF)
    private val dark_onSecondary = Color(0xFF00344F)
    private val dark_secondaryContainer = Color(0xFF004B71)
    private val dark_onSecondaryContainer = Color(0xFFCDE5FF)

    private val dark_background = Color(0xFF0D1116)
    private val dark_surface = Color(0xFF11171A)
    private val dark_onSurface = Color(0xFFE2E2E6)

    private val dark_surfaceVariant = Color(0xFF1C2226)
    private val dark_onSurfaceVariant = Color(0xFFC2C7CE)
    private val dark_outline = Color(0xFF3F4850)

    private val dark_tertiary = Color(0xFFB8C5D0)
    private val dark_onTertiary = Color(0xFF23323E)
    private val dark_tertiaryContainer = Color(0xFF394956)
    private val dark_onTertiaryContainer = Color(0xFFD4E1EC)

    private val light_primary = Color(0xFFB12E00)
    private val light_onPrimary = Color(0xFFFFFFFF)
    private val light_primaryContainer = Color(0xFFFFDBD1)
    private val light_onPrimaryContainer = Color(0xFF3B0900)

    private val light_secondary = Color(0xFF2C638B)
    private val light_onSecondary = Color(0xFFFFFFFF)
    private val light_secondaryContainer = Color(0xFFCDE5FF)
    private val light_onSecondaryContainer = Color(0xFF001D32)

    private val light_background = Color(0xFFFFFBFF)
    private val light_onBackground = Color(0xFF1B1B1F)
    private val light_surface = Color(0xFFFFFBFF)
    private val light_onSurface = Color(0xFF1B1B1F)
    private val light_surfaceVariant = Color(0xFFF5DED8)
    private val light_onSurfaceVariant = Color(0xFF49454E)
    private val light_outline = Color(0xFF7A757F)

    private val light_tertiary = Color(0xFF4F5B66)
    private val light_onTertiary = Color(0xFFFFFFFF)
    private val light_tertiaryContainer = Color(0xFFD4E1EC)
    private val light_onTertiaryContainer = Color(0xFF0B1E29)

    val DarkColors = darkColorScheme(
        primary = dark_primary,
        onPrimary = dark_onPrimary,
        primaryContainer = dark_primaryContainer,
        onPrimaryContainer = dark_onPrimaryContainer,
        secondary = dark_secondary,
        onSecondary = dark_onSecondary,
        secondaryContainer = dark_secondaryContainer,
        onSecondaryContainer = dark_onSecondaryContainer,
        background = dark_background,
        onBackground = dark_onSurface,
        surface = dark_surface,
        onSurface = dark_onSurface,
        surfaceVariant = dark_surfaceVariant,
        onSurfaceVariant = dark_onSurfaceVariant,
        outline = dark_outline,
        surfaceTint = Color.Transparent,
        tertiary = dark_tertiary,
        onTertiary = dark_onTertiary,
        tertiaryContainer = dark_tertiaryContainer,
        onTertiaryContainer = dark_onTertiaryContainer
    )

    val LightColors = lightColorScheme(
        primary = light_primary,
        onPrimary = light_onPrimary,
        primaryContainer = light_primaryContainer,
        onPrimaryContainer = light_onPrimaryContainer,
        secondary = light_secondary,
        onSecondary = light_onSecondary,
        secondaryContainer = light_secondaryContainer,
        onSecondaryContainer = light_onSecondaryContainer,
        background = light_background,
        onBackground = light_onBackground,
        surface = light_surface,
        onSurface = light_onSurface,
        surfaceVariant = light_surfaceVariant,
        onSurfaceVariant = light_onSurfaceVariant,
        outline = light_outline,
        surfaceTint = Color.Transparent,
        tertiary = light_tertiary,
        onTertiary = light_onTertiary,
        tertiaryContainer = light_tertiaryContainer,
        onTertiaryContainer = light_onTertiaryContainer
    )
}
