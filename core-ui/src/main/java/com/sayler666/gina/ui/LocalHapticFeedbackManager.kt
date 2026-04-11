package com.sayler666.gina.ui

import androidx.compose.runtime.compositionLocalOf
import com.sayler666.core.haptics.HapticFeedbackManager

val LocalHapticFeedbackManager = compositionLocalOf<HapticFeedbackManager> {
    error("No HapticFeedbackManager provided")
}
