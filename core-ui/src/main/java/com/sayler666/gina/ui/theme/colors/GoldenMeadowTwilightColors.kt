package com.sayler666.gina.ui.theme.colors

import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.ui.graphics.Color

object GoldenMeadowTwilightColors {
    // Light Theme - Golden Meadow Twilight
    private val light_primary = Color(0xff7a5900)           // Dark amber
    private val light_onPrimary = Color(0xffffffff)         // White
    private val light_primaryContainer = Color(0xffffdf9a)  // Pale golden cream
    private val light_onPrimaryContainer = Color(0xff271900) // Very dark brown
    private val light_secondary = Color(0xff904364)         // Deep rose
    private val light_onSecondary = Color(0xffffffff)       // White
    private val light_secondaryContainer = Color(0xffffd9e4) // Pale pink
    private val light_onSecondaryContainer = Color(0xff3a0621) // Very dark burgundy
    private val light_tertiary = Color(0xff475e91)          // Muted indigo
    private val light_onTertiary = Color(0xffffffff)        // White
    private val light_tertiaryContainer = Color(0xffd9e0ff) // Pale lavender
    private val light_onTertiaryContainer = Color(0xff001847) // Very dark blue
    private val light_error = Color(0xffba1a1a)             // Red
    private val light_errorContainer = Color(0xffffdad6)    // Light pink
    private val light_onError = Color(0xffffffff)           // White
    private val light_onErrorContainer = Color(0xff410002)  // Very dark red
    private val light_background = Color(0xfffffbf8)        // Warm off-white
    private val light_onBackground = Color(0xff1d1b18)      // Almost black
    private val light_surface = Color(0xfffffbf8)           // Warm off-white
    private val light_onSurface = Color(0xff1d1b18)         // Almost black
    private val light_surfaceVariant = Color(0xffeae2d8)    // Light warm gray
    private val light_onSurfaceVariant = Color(0xff4a453f)  // Dark warm gray
    private val light_outline = Color(0xff7b7569)           // Medium warm gray
    private val light_inverseOnSurface = Color(0xfff5f0eb)  // Very light warm gray
    private val light_inverseSurface = Color(0xff32302c)    // Dark warm gray
    private val light_inversePrimary = Color(0xffffc947)    // Golden yellow
    private val light_surfaceTint = Color(0xff7a5900)       // Dark amber
    private val light_outlineVariant = Color(0xffcdc5bb)    // Light warm gray
    private val light_scrim = Color(0xff000000)             // Pure black

    // Dark Theme - Golden Meadow Twilight
    private val dark_primary = Color(0xffffc947)            // Golden sunset yellow
    private val dark_onPrimary = Color(0xff402d00)          // Deep amber brown
    private val dark_primaryContainer = Color(0xff5c4200)   // Dark golden brown
    private val dark_onPrimaryContainer = Color(0xffffdf9a) // Pale golden cream
    private val dark_secondary = Color(0xffffa6c9)          // Soft cosmos pink
    private val dark_onSecondary = Color(0xff5e1133)        // Deep burgundy
    private val dark_secondaryContainer = Color(0xff7b2949) // Dark rose
    private val dark_onSecondaryContainer = Color(0xffffd9e4) // Pale pink
    private val dark_tertiary = Color(0xffa6baff)           // Periwinkle (butterfly blue)
    private val dark_onTertiary = Color(0xff192e60)         // Deep indigo
    private val dark_tertiaryContainer = Color(0xff304578)  // Dark slate blue
    private val dark_onTertiaryContainer = Color(0xffd9e0ff) // Pale lavender
    private val dark_error = Color(0xffffb4ab)              // Soft salmon
    private val dark_errorContainer = Color(0xff93000a)     // Deep red
    private val dark_onError = Color(0xff690005)            // Dark red
    private val dark_onErrorContainer = Color(0xffffdad6)   // Light pink
    private val dark_background = Color(0xff1a1614)         // Dark brown-gray (earth tone)
    private val dark_onBackground = Color(0xfff0e6dc)       // Warm cream
    private val dark_surface = Color(0xff1a1614)            // Matches background
    private val dark_onSurface = Color(0xfff0e6dc)          // Warm cream
    private val dark_surfaceVariant = Color(0xff4a453f)     // Warm medium gray
    private val dark_onSurfaceVariant = Color(0xffcdc5bb)   // Light warm gray
    private val dark_outline = Color(0xff968f86)            // Warm gray
    private val dark_inverseOnSurface = Color(0xff2f2b28)   // Dark warm gray
    private val dark_inverseSurface = Color(0xfff0e6dc)     // Warm cream
    private val dark_inversePrimary = Color(0xff7a5900)     // Dark amber
    private val dark_surfaceTint = Color(0xffffc947)        // Matches primary
    private val dark_outlineVariant = Color(0xff4a453f)     // Warm medium gray
    private val dark_scrim = Color(0xff000000)              // Pure black

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
