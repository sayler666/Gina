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
import androidx.navigation.NavController
import com.ramcosta.composedestinations.navigation.navigate
import com.sayler666.gina.NavGraphs
import com.sayler666.gina.R
import com.sayler666.gina.appCurrentDestinationAsState
import com.sayler666.gina.destinations.CalendarScreenDestination
import com.sayler666.gina.destinations.Destination
import com.sayler666.gina.destinations.DirectionDestination
import com.sayler666.gina.destinations.GalleryScreenDestination
import com.sayler666.gina.destinations.InsightsScreenDestination
import com.sayler666.gina.destinations.JournalScreenDestination
import com.sayler666.gina.destinations.SettingsScreenDestination
import com.sayler666.gina.startAppDestination
import com.sayler666.gina.ui.animatedNavBar.AnimatedNavigationBar
import com.sayler666.gina.ui.animatedNavBar.item.DropletButton

enum class BottomDestinations(
    val destination: DirectionDestination,
    val icon: ImageVector,
    val iconSelected: ImageVector,
    val label: Int
) {
    Journal(
        JournalScreenDestination,
        Icons.TwoTone.AutoStories,
        Icons.Filled.AutoStories,
        R.string.days_label
    ),
    Calendar(
        CalendarScreenDestination,
        Icons.TwoTone.CalendarMonth,
        Icons.Filled.CalendarMonth,
        R.string.calendar_label
    ),
    Gallery(
        GalleryScreenDestination,
        Icons.TwoTone.PhotoLibrary,
        Icons.Filled.PhotoLibrary,
        R.string.gallery_label
    ),
    Insights(
        InsightsScreenDestination,
        Icons.TwoTone.Insights,
        Icons.Filled.Insights,
        R.string.insights_label
    ),
    Settings(
        SettingsScreenDestination,
        Icons.TwoTone.Settings,
        Icons.Filled.Settings,
        R.string.settings_label
    )
}

@Composable
fun BottomNavigationBar(
    modifier: Modifier = Modifier,
    color: Color,
    navController: NavController
) {
    val currentDestination: Destination = navController.appCurrentDestinationAsState().value
        ?: NavGraphs.root.startAppDestination

    val selectedIndex = remember(currentDestination) {
        val index = BottomDestinations.values().indexOfFirst {
            currentDestination == it.destination
        }
        if (index in 0 until BottomDestinations.values().size) {
            index
        } else {
            0
        }
    }

    AnimatedNavigationBar(
        modifier = modifier,
        selectedIndex = selectedIndex,
        barColor = color,
        ballColor = MaterialTheme.colorScheme.secondaryContainer,
        menuItemsSize = BottomDestinations.values().size
    ) {
        BottomDestinations.values().forEach {
            DropletButton(
                modifier = Modifier
                    .fillMaxSize()
                    .zIndex(1f),
                isSelected = currentDestination == it.destination,
                icon = if (currentDestination == it.destination) it.iconSelected else it.icon,
                onClick = {
                    navController.navigate(it.destination) {
                        launchSingleTop = true
                        restoreState = true
                        popUpTo(JournalScreenDestination.route)
                    }
                }
            )
        }
    }
}
