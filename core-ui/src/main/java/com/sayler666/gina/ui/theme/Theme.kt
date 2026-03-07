package com.sayler666.gina.ui.theme

enum class Theme {
    Dynamic,
    AlterBridge,
    GoldenMeadowTwilight,
    DeepOcean,
    Firewatch,
    MountainView,
    Legacy;

    companion object {
        fun default(): Theme = Dynamic
    }
}
