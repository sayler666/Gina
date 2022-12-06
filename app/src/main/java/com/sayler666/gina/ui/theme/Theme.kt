package com.sayler666.gina.ui.theme

import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.runtime.Composable

private val DarkColorPalette = darkColorScheme(
    primary = Purple200,
    onPrimaryContainer = Purple700,
    secondary = Teal200,
)

private val LightColorPalette = darkColorScheme(
    primary = Purple500,
    secondary = Purple700,
    tertiary = Teal200,
)

@Composable
fun GinaTheme(darkTheme: Boolean = isSystemInDarkTheme(), content: @Composable () -> Unit) {
    val colors = if (darkTheme) {
        DarkColorPalette
    } else {
        LightColorPalette
    }

    MaterialTheme(
        colorScheme = colors,
        typography = Typography,
        shapes = Shapes,
        content = content
    )
}
