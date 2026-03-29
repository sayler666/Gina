package com.sayler666.gina.ui.theme

import androidx.compose.material3.ColorScheme
import com.sayler666.gina.ui.theme.colors.AlterBridgeColors
import com.sayler666.gina.ui.theme.colors.DeepOceanColors
import com.sayler666.gina.ui.theme.colors.FirewatchColors
import com.sayler666.gina.ui.theme.colors.GoldenMeadowTwilightColors
import com.sayler666.gina.ui.theme.colors.IronSkyColors
import com.sayler666.gina.ui.theme.colors.LegacyColors
import com.sayler666.gina.ui.theme.colors.MountainViewColors

sealed interface Theme {
    val key: String get() = this::class.simpleName ?: error("Anonymous Theme objects are not supported")

    data object DynamicTheme : Theme

    sealed interface StaticTheme : Theme {
        val darkColors: ColorScheme
        val lightColors: ColorScheme
    }

    data object AlterBridge : StaticTheme {
        override val darkColors = AlterBridgeColors.DarkColors
        override val lightColors = AlterBridgeColors.LightColors
    }

    data object GoldenMeadowTwilight : StaticTheme {
        override val darkColors = GoldenMeadowTwilightColors.DarkColors
        override val lightColors = GoldenMeadowTwilightColors.LightColors
    }

    data object DeepOcean : StaticTheme {
        override val darkColors = DeepOceanColors.DarkColors
        override val lightColors = DeepOceanColors.LightColors
    }

    data object Firewatch : StaticTheme {
        override val darkColors = FirewatchColors.DarkColors
        override val lightColors = FirewatchColors.LightColors
    }

    data object IronSky : StaticTheme {
        override val darkColors = IronSkyColors.DarkColors
        override val lightColors = IronSkyColors.LightColors
    }

    data object MountainView : StaticTheme {
        override val darkColors = MountainViewColors.DarkColors
        override val lightColors = MountainViewColors.LightColors
    }

    data object Legacy : StaticTheme {
        override val darkColors = LegacyColors.DarkColors
        override val lightColors = LegacyColors.LightColors
    }

    companion object {
        val entries: List<Theme> = listOf(
            DynamicTheme, AlterBridge, GoldenMeadowTwilight, DeepOcean, Firewatch, IronSky, MountainView, Legacy
        )

        fun default(): Theme = DynamicTheme

        fun fromKey(key: String): Theme = entries.firstOrNull { it.key == key } ?: default()
    }
}
