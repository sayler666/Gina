package com.sayler666.gina.settings.viewmodel

import androidx.annotation.StringRes
import androidx.compose.ui.graphics.Color
import com.sayler666.gina.settings.Theme

data class ThemeItem(
    val theme: Theme,
    @StringRes val name: Int,
    val selected: Boolean,
    val colorsPreview: ColorsPreview? = null
)

data class ColorsPreview(
    val primary: Color,
    val secondary: Color,
    val tertiary: Color
)
