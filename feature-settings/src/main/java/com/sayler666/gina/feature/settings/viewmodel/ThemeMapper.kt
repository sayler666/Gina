package com.sayler666.gina.feature.settings.viewmodel

import com.sayler666.gina.feature.settings.R.string
import com.sayler666.gina.ui.theme.Theme
import com.sayler666.gina.ui.theme.Theme.AlterBridge
import com.sayler666.gina.ui.theme.Theme.DeepOcean
import com.sayler666.gina.ui.theme.Theme.Dynamic
import com.sayler666.gina.ui.theme.Theme.Firewatch
import com.sayler666.gina.ui.theme.Theme.GoldenMeadowTwilight
import com.sayler666.gina.ui.theme.Theme.Legacy
import com.sayler666.gina.ui.theme.Theme.MountainView
import com.sayler666.gina.ui.theme.colors.AlterBridgeColors
import com.sayler666.gina.ui.theme.colors.DeepOceanColors
import com.sayler666.gina.ui.theme.colors.FirewatchColors
import com.sayler666.gina.ui.theme.colors.GoldenMeadowTwilightColors
import com.sayler666.gina.ui.theme.colors.LegacyColors
import com.sayler666.gina.ui.theme.colors.MountainViewColors
import javax.inject.Inject

class ThemeMapper @Inject constructor() {
    fun mapToVM(activeTheme: Theme): List<ThemeItem> = Theme.entries.map { theme ->
        when (theme) {
            Dynamic -> ThemeItem(
                theme,
                string.theme_dynamic,
                theme == activeTheme
            )

            Firewatch -> ThemeItem(
                theme,
                string.theme_firewatch,
                theme == activeTheme,
                ColorsPreview(
                    FirewatchColors.DarkColors.primary,
                    FirewatchColors.DarkColors.secondary,
                    FirewatchColors.DarkColors.tertiary
                )
            )

            MountainView -> ThemeItem(
                theme,
                string.theme_mountain_view,
                theme == activeTheme,
                ColorsPreview(
                    MountainViewColors.DarkColors.primary,
                    MountainViewColors.DarkColors.secondary,
                    MountainViewColors.DarkColors.tertiary
                )
            )

            Legacy -> ThemeItem(
                theme,
                string.theme_legacy,
                theme == activeTheme,
                ColorsPreview(
                    LegacyColors.DarkColors.primary,
                    LegacyColors.DarkColors.secondary,
                    LegacyColors.DarkColors.tertiary
                )
            )

            DeepOcean -> ThemeItem(
                theme,
                string.theme_deep_ocean,
                theme == activeTheme,
                ColorsPreview(
                    DeepOceanColors.DarkColors.primary,
                    DeepOceanColors.DarkColors.secondary,
                    DeepOceanColors.DarkColors.tertiary
                )
            )

            GoldenMeadowTwilight -> ThemeItem(
                theme,
                string.theme_golden_meadow_twilight,
                theme == activeTheme,
                ColorsPreview(
                    GoldenMeadowTwilightColors.DarkColors.primary,
                    GoldenMeadowTwilightColors.DarkColors.secondary,
                    GoldenMeadowTwilightColors.DarkColors.tertiary
                )
            )

            AlterBridge -> ThemeItem(
                theme,
                string.theme_golden_alter_bridge,
                theme == activeTheme,
                ColorsPreview(
                    AlterBridgeColors.DarkColors.primary,
                    AlterBridgeColors.DarkColors.secondary,
                    AlterBridgeColors.DarkColors.tertiary
                )
            )
        }
    }
}
