package com.sayler666.gina.navigation

sealed interface Route {
    val showScaffoldElements: Boolean get() = false
}
