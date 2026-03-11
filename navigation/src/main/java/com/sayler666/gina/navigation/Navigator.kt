package com.sayler666.gina.navigation

import com.sayler666.gina.navigation.routes.Route

class Navigator(private val backStack: MutableList<Route>) {

    fun currentRoute() : Route? = backStack.lastOrNull()

    fun navigate(route: Route) {
        backStack.add(route)
    }

    fun back() {
        backStack.removeLastOrNull()
    }

    fun replace(route: Route) {
        backStack.removeLastOrNull()
        backStack.add(route)
    }

    fun navigateToRoot(route: Route) {
        backStack.clear()
        backStack.add(route)
    }

    fun popUntil(predicate: (Route) -> Boolean) {
        while (backStack.isNotEmpty() && !predicate(backStack.last())) {
            backStack.removeLastOrNull()
        }
    }
}
