package com.sayler666.gina.ui.theme.colors

import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.ui.graphics.Color

object DeepOceanColors {
    // Light Theme - Tropical Paradise Lagoon
    private val light_primary = Color(0xff006874) // Deep teal-cyan
    private val light_onPrimary = Color(0xffffffff) // White
    private val light_primaryContainer = Color(0xff97f0ff) // Bright aqua
    private val light_onPrimaryContainer = Color(0xff001f24) // Very dark teal
    private val light_secondary = Color(0xff8b5000) // Burnt orange (sunset)
    private val light_onSecondary = Color(0xffffffff) // White
    private val light_secondaryContainer = Color(0xffffdcc2) // Light peach
    private val light_onSecondaryContainer = Color(0xff2d1600) // Very dark orange
    private val light_tertiary = Color(0xff36618e) // Ocean blue
    private val light_onTertiary = Color(0xffffffff) // White
    private val light_tertiaryContainer = Color(0xffd1e4ff) // Light sky blue
    private val light_onTertiaryContainer = Color(0xff001d36) // Very dark blue
    private val light_error = Color(0xffba1a1a) // Red
    private val light_errorContainer = Color(0xffffdad6) // Light pink
    private val light_onError = Color(0xffffffff) // White
    private val light_onErrorContainer = Color(0xff410002) // Very dark red
    private val light_background = Color(0xfff5feff) // Very light cyan-white
    private val light_onBackground = Color(0xff171c1f) // Almost black
    private val light_surface = Color(0xfff5feff) // Very light cyan-white
    private val light_onSurface = Color(0xff171c1f) // Almost black
    private val light_surfaceVariant = Color(0xffdbe4e6) // Light blue-gray
    private val light_onSurfaceVariant = Color(0xff3f484a) // Dark gray
    private val light_outline = Color(0xff6f797a) // Medium gray
    private val light_inverseOnSurface = Color(0xffecf1f2) // Very light gray
    private val light_inverseSurface = Color(0xff2c3133) // Dark gray
    private val light_inversePrimary = Color(0xff4fd8eb) // Bright cyan
    private val light_surfaceTint = Color(0xff006874) // Deep teal-cyan
    private val light_outlineVariant = Color(0xffbfc8ca) // Light gray-blue
    private val light_scrim = Color(0xff000000) // Pure black

    // Dark Theme - Tropical Paradise Lagoon
    private val dark_primary = Color(0xff4fd8eb) // Bright cyan (lagoon water)
    private val dark_onPrimary = Color(0xff00363d) // Deep teal
    private val dark_primaryContainer = Color(0xff004f58) // Dark teal
    private val dark_onPrimaryContainer = Color(0xff97f0ff) // Bright aqua
    private val dark_secondary = Color(0xffffb77c) // Golden sand
    private val dark_onSecondary = Color(0xff4a2800) // Deep brown
    private val dark_secondaryContainer = Color(0xff6a3c00) // Dark amber
    private val dark_onSecondaryContainer = Color(0xffffdcc2) // Light peach
    private val dark_tertiary = Color(0xff9ecaff) // Sky blue
    private val dark_onTertiary = Color(0xff003258) // Deep navy
    private val dark_tertiaryContainer = Color(0xff1a4975) // Dark ocean blue
    private val dark_onTertiaryContainer = Color(0xffd1e4ff) // Light sky blue
    private val dark_error = Color(0xffffb4ab) // Soft salmon
    private val dark_errorContainer = Color(0xff93000a) // Deep red
    private val dark_onError = Color(0xff690005) // Dark red
    private val dark_onErrorContainer = Color(0xffffdad6) // Light pink
    private val dark_background = Color(0xff0e1415) // Almost black (deep ocean)
    private val dark_onBackground = Color(0xffdfe3e4) // Light gray
    private val dark_surface = Color(0xff0e1415) // Matches background
    private val dark_onSurface = Color(0xffdfe3e4) // Light gray
    private val dark_surfaceVariant = Color(0xff3f484a) // Dark gray-teal
    private val dark_onSurfaceVariant = Color(0xffbfc8ca) // Light gray-blue
    private val dark_outline = Color(0xff899294) // Medium gray
    private val dark_inverseOnSurface = Color(0xff0e1415) // Almost black
    private val dark_inverseSurface = Color(0xffdfe3e4) // Light gray
    private val dark_inversePrimary = Color(0xff006874) // Deep teal-cyan
    private val dark_surfaceTint = Color(0xff4fd8eb) // Matches primary
    private val dark_outlineVariant = Color(0xff3f484a) // Dark gray-teal
    private val dark_scrim = Color(0xff000000) // Pure black

    val LightColors = lightColorScheme(
        primary = light_primary,
        onPrimary = light_onPrimary,
        primaryContainer = light_primaryContainer,
        onPrimaryContainer = light_onPrimaryContainer,
        secondary = light_secondary,
        onSecondary = light_onSecondary,
        secondaryContainer = light_secondaryContainer,
        onSecondaryContainer = light_onSecondaryContainer,
        tertiary = light_tertiary,
        onTertiary = light_onTertiary,
        tertiaryContainer = light_tertiaryContainer,
        onTertiaryContainer = light_onTertiaryContainer,
        error = light_error,
        errorContainer = light_errorContainer,
        onError = light_onError,
        onErrorContainer = light_onErrorContainer,
        background = light_background,
        onBackground = light_onBackground,
        surface = light_surface,
        onSurface = light_onSurface,
        surfaceVariant = light_surfaceVariant,
        onSurfaceVariant = light_onSurfaceVariant,
        outline = light_outline,
        inverseOnSurface = light_inverseOnSurface,
        inverseSurface = light_inverseSurface,
        inversePrimary = light_inversePrimary,
        surfaceTint = light_surfaceTint,
        outlineVariant = light_outlineVariant,
        scrim = light_scrim,
    )
    val DarkColors = darkColorScheme(
        primary = dark_primary,
        onPrimary = dark_onPrimary,
        primaryContainer = dark_primaryContainer,
        onPrimaryContainer = dark_onPrimaryContainer,
        secondary = dark_secondary,
        onSecondary = dark_onSecondary,
        secondaryContainer = dark_secondaryContainer,
        onSecondaryContainer = dark_onSecondaryContainer,
        tertiary = dark_tertiary,
        onTertiary = dark_onTertiary,
        tertiaryContainer = dark_tertiaryContainer,
        onTertiaryContainer = dark_onTertiaryContainer,
        error = dark_error,
        errorContainer = dark_errorContainer,
        onError = dark_onError,
        onErrorContainer = dark_onErrorContainer,
        background = dark_background,
        onBackground = dark_onBackground,
        surface = dark_surface,
        onSurface = dark_onSurface,
        surfaceVariant = dark_surfaceVariant,
        onSurfaceVariant = dark_onSurfaceVariant,
        outline = dark_outline,
        inverseOnSurface = dark_inverseOnSurface,
        inverseSurface = dark_inverseSurface,
        inversePrimary = dark_inversePrimary,
        surfaceTint = dark_surfaceTint,
        outlineVariant = dark_outlineVariant,
        scrim = dark_scrim,
    )
}
