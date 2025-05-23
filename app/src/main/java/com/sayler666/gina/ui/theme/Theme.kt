package com.sayler666.gina.ui.theme

import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.ColorScheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.dynamicDarkColorScheme
import androidx.compose.material3.dynamicLightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import com.sayler666.gina.BuildConfig
import com.sayler666.gina.settings.Theme
import com.sayler666.gina.settings.Theme.*
import com.sayler666.gina.ui.theme.colors.DeepOceanColors
import com.sayler666.gina.ui.theme.colors.FirewatchColors
import com.sayler666.gina.ui.theme.colors.GoldenMeadowTwilightColors
import com.sayler666.gina.ui.theme.colors.LegacyColors
import com.sayler666.gina.ui.theme.colors.MountainViewColors
import timber.log.Timber
import android.graphics.Color as AndroidColor


@Composable
fun GinaTheme(
    theme: Theme,
    darkTheme: Boolean = isSystemInDarkTheme(),
    content: @Composable () -> Unit
) {
    val colors: ColorScheme = when (theme) {
        Firewatch -> if (darkTheme) FirewatchColors.DarkColors else FirewatchColors.LightColors
        MountainView -> if (darkTheme) MountainViewColors.DarkColors else MountainViewColors.LightColors
        Dynamic -> if (darkTheme) {
            dynamicDarkColorScheme(LocalContext.current)
        } else {
            dynamicLightColorScheme(LocalContext.current)
        }

        Legacy -> if (darkTheme) LegacyColors.DarkColors else LegacyColors.LightColors
        DeepOcean -> if (darkTheme) DeepOceanColors.DarkColors else DeepOceanColors.LightColors
        GoldenMeadowTwilight -> if (darkTheme) GoldenMeadowTwilightColors.DarkColors else GoldenMeadowTwilightColors.LightColors
    }

    // Log current colors values
    if (BuildConfig.DEBUG) {
        LogColors(colors)
    }

    MaterialTheme(
        colorScheme = colors,
        typography = Typography,
        shapes = Shapes,
        content = content
    )
}

private fun LogColors(colors: ColorScheme) {
    fun Color.toHex() = Integer.toHexString(AndroidColor.rgb(red, green, blue))

    Timber.d("Theme: $colors")
    Timber.d("Color primary: ${colors.primary.toHex()}")
    Timber.d("Color onPrimary: ${colors.onPrimary.toHex()}")
    Timber.d("Color primaryContainer: ${colors.primaryContainer.toHex()}")
    Timber.d("Color onPrimaryContainer: ${colors.onPrimaryContainer.toHex()}")
    Timber.d("Color secondary: ${colors.secondary.toHex()}")
    Timber.d("Color onSecondary: ${colors.onSecondary.toHex()}")
    Timber.d("Color secondaryContainer: ${colors.secondaryContainer.toHex()}")
    Timber.d("Color onSecondaryContainer: ${colors.onSecondaryContainer.toHex()}")
    Timber.d("Color tertiary: ${colors.tertiary.toHex()}")
    Timber.d("Color onTertiary: ${colors.onTertiary.toHex()}")
    Timber.d("Color tertiaryContainer: ${colors.tertiaryContainer.toHex()}")
    Timber.d("Color onTertiaryContainer: ${colors.onTertiaryContainer.toHex()}")
    Timber.d("Color error: ${colors.error.toHex()}")
    Timber.d("Color errorContainer: ${colors.errorContainer.toHex()}")
    Timber.d("Color onError: ${colors.onError.toHex()}")
    Timber.d("Color onErrorContainer: ${colors.onErrorContainer.toHex()}")
    Timber.d("Color background: ${colors.background.toHex()}")
    Timber.d("Color onBackground: ${colors.onBackground.toHex()}")
    Timber.d("Color surface: ${colors.surface.toHex()}")
    Timber.d("Color onSurface: ${colors.onSurface.toHex()}")
    Timber.d("Color surfaceVariant: ${colors.surfaceVariant.toHex()}")
    Timber.d("Color onSurfaceVariant: ${colors.onSurfaceVariant.toHex()}")
    Timber.d("Color outline: ${colors.outline.toHex()}")
    Timber.d("Color inverseOnSurface: ${colors.inverseOnSurface.toHex()}")
    Timber.d("Color inverseSurface: ${colors.inverseSurface.toHex()}")
    Timber.d("Color inversePrimary: ${colors.inversePrimary.toHex()}")
    Timber.d("Color surfaceTint: ${colors.surfaceTint.toHex()}")
    Timber.d("Color outlineVariant: ${colors.outlineVariant.toHex()}")
    Timber.d("Color scrim: ${colors.scrim.toHex()}")
}
