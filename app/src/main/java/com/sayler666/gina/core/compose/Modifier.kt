package com.sayler666.gina.core.compose

import androidx.compose.ui.Modifier

fun Modifier.conditional(condition: Boolean, modifier: Modifier.() -> Modifier): Modifier {
    return if (condition) {
        modifier(Modifier)
    } else {
        this
    }
}
