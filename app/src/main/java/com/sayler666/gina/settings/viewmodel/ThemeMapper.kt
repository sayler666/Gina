package com.sayler666.gina.settings.viewmodel

import com.sayler666.gina.R.string
import com.sayler666.gina.settings.Theme
import com.sayler666.gina.settings.Theme.AutoDarkLight
import com.sayler666.gina.settings.Theme.Dark
import com.sayler666.gina.settings.Theme.Dynamic
import com.sayler666.gina.settings.Theme.Light
import javax.inject.Inject

class ThemeMapper @Inject constructor() {
    fun mapToVM(activeTheme: Theme): List<ThemeItem> {
        return Theme.values().map { theme ->
            when (theme) {
                AutoDarkLight -> ThemeItem(
                    theme,
                    string.theme_auto,
                    theme == activeTheme
                )

                Dynamic -> ThemeItem(
                    theme,
                    string.theme_dynamic,
                    theme == activeTheme
                )

                Dark -> ThemeItem(
                    theme,
                    string.theme_dark,
                    theme == activeTheme
                )

                Light -> ThemeItem(
                    theme,
                    string.theme_light,
                    theme == activeTheme
                )
            }
        }
    }
}
