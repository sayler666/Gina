package com.sayler666.gina.ginaApp.navigation

import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AutoStories
import androidx.compose.material.icons.filled.CalendarMonth
import androidx.compose.material.icons.filled.Insights
import androidx.compose.material.icons.filled.PhotoLibrary
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material.icons.twotone.AutoStories
import androidx.compose.material.icons.twotone.CalendarMonth
import androidx.compose.material.icons.twotone.Insights
import androidx.compose.material.icons.twotone.PhotoLibrary
import androidx.compose.material.icons.twotone.Settings
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.zIndex
import com.sayler666.gina.navigation.Route
import com.sayler666.gina.resources.R
import com.sayler666.gina.ui.animatedNavBar.AnimatedNavigationBar
import com.sayler666.gina.ui.animatedNavBar.item.DropletButton
import dev.chrisbanes.haze.HazeState
import com.sayler666.gina.navigation.Calendar as RouteCalendar
import com.sayler666.gina.navigation.Gallery as RouteGallery
import com.sayler666.gina.navigation.Insights as RouteInsights
import com.sayler666.gina.navigation.Journal as RouteJournal
import com.sayler666.gina.navigation.Settings as RouteSettings

enum class BottomDestinations(
    val route: Route,
    val icon: ImageVector,
    val iconSelected: ImageVector,
    val label: Int
) {
    Journal(
        RouteJournal,
        Icons.TwoTone.AutoStories,
        Icons.Filled.AutoStories,
        R.string.days_label
    ),
    Calendar(
        RouteCalendar,
        Icons.TwoTone.CalendarMonth,
        Icons.Filled.CalendarMonth,
        R.string.calendar_label
    ),
    Gallery(
        RouteGallery,
        Icons.TwoTone.PhotoLibrary,
        Icons.Filled.PhotoLibrary,
        R.string.gallery_label
    ),
    Insights(
        RouteInsights,
        Icons.TwoTone.Insights,
        Icons.Filled.Insights,
        R.string.insights_label
    ),
    Settings(
        RouteSettings,
        Icons.TwoTone.Settings,
        Icons.Filled.Settings,
        R.string.settings_label
    )
}

@Composable
fun BottomNavigationBar(
    modifier: Modifier = Modifier,
    color: Color,
    currentRoute: Any?,
    backStack: MutableList<Route>,
    hazeState: HazeState
) {
    val selectedIndex = remember(currentRoute) {
        val index = BottomDestinations.entries.indexOfFirst { it.route == currentRoute }
        if (index >= 0) index else 0
    }

    AnimatedNavigationBar(
        modifier = modifier,
        selectedIndex = selectedIndex,
        barColor = color,
        ballColor = MaterialTheme.colorScheme.secondary.copy(alpha = 0.2f),
        menuItemsSize = BottomDestinations.entries.size,
        hazeState = hazeState,
    ) {
        BottomDestinations.entries.forEach { dest ->
            DropletButton(
                modifier = Modifier
                    .fillMaxSize()
                    .zIndex(1f),
                isSelected = currentRoute == dest.route,
                icon = if (currentRoute == dest.route) dest.iconSelected else dest.icon,
                onClick = {
                    val existingIndex = backStack.indexOfFirst { it == dest.route }
                    if (existingIndex >= 0) {
                        // pop to existing entry
                        while (backStack.size > existingIndex + 1) backStack.removeLastOrNull()
                    } else {
                        // pop to Journal, then push destination
                        while (backStack.size > 1) backStack.removeLastOrNull()
                        if (dest.route != RouteJournal) backStack.add(dest.route)
                    }
                }
            )
        }
    }
}
