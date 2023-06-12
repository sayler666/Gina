package com.sayler666.gina.settings.viewmodel

import androidx.annotation.StringRes
import com.sayler666.gina.settings.Theme

data class ThemeItem(val theme: Theme, @StringRes val name: Int, val selected: Boolean)
