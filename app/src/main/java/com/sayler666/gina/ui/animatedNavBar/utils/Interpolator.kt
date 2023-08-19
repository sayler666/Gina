package com.sayler666.gina.ui.animatedNavBar.utils

fun lerp(start: Float, stop: Float, fraction: Float) =
    (start * (1 - fraction) + stop * fraction)
