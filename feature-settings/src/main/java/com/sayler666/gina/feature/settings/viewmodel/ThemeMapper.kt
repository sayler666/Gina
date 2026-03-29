package com.sayler666.gina.feature.settings.viewmodel

import androidx.annotation.StringRes
import com.sayler666.gina.resources.R.string
import com.sayler666.gina.ui.theme.Theme
import javax.inject.Inject

class ThemeMapper @Inject constructor() {
    fun mapToVM(activeTheme: Theme): List<ThemeItem> = Theme.entries.map { theme ->
        ThemeItem(
            theme = theme,
            name = theme.displayName(),
            selected = theme == activeTheme,
            colorsPreview = (theme as? Theme.StaticTheme)?.let {
                ColorsPreview(it.darkColors.primary, it.darkColors.secondary, it.darkColors.tertiary)
            }
        )
    }

    @StringRes
    private fun Theme.displayName(): Int = when (this) {
        is Theme.DynamicTheme -> string.theme_dynamic
        is Theme.AlterBridge -> string.theme_golden_alter_bridge
        is Theme.GoldenMeadowTwilight -> string.theme_golden_meadow_twilight
        is Theme.DeepOcean -> string.theme_deep_ocean
        is Theme.Firewatch -> string.theme_firewatch
        is Theme.IronSky -> string.theme_iron_sky
        is Theme.MountainView -> string.theme_mountain_view
        is Theme.Legacy -> string.theme_legacy
    }
}
