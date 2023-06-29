package com.sayler666.gina.settings.viewmodel

import com.sayler666.gina.R.string
import com.sayler666.gina.settings.Theme
import com.sayler666.gina.settings.Theme.Dynamic
import com.sayler666.gina.settings.Theme.Firewatch
import com.sayler666.gina.settings.Theme.Legacy
import javax.inject.Inject

class ThemeMapper @Inject constructor() {
    fun mapToVM(activeTheme: Theme): List<ThemeItem> {
        return Theme.values().map { theme ->
            when (theme) {
                Firewatch -> ThemeItem(
                    theme,
                    string.theme_firewatch,
                    theme == activeTheme
                )

                Dynamic -> ThemeItem(
                    theme,
                    string.theme_dynamic,
                    theme == activeTheme
                )

                Legacy -> ThemeItem(
                    theme,
                    string.theme_legacy,
                    theme == activeTheme
                )
            }
        }
    }
}
