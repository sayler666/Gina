package com.sayler666.gina.navigation.routes

sealed interface Route {
    val showScaffoldElements: Boolean get() = false
}
