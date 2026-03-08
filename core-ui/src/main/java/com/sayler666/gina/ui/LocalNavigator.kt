package com.sayler666.gina.ui

import androidx.compose.runtime.compositionLocalOf
import com.sayler666.gina.navigation.Navigator

val LocalNavigator = compositionLocalOf<Navigator> { error("No Navigator provided") }
