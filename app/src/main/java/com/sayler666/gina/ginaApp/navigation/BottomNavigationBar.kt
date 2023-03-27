package com.sayler666.gina.ginaApp.navigation

import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.width
import androidx.compose.material.BottomNavigation
import androidx.compose.material.BottomNavigationItem
import androidx.compose.material.Icon
import androidx.compose.material.Text
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AutoStories
import androidx.compose.material.icons.filled.CalendarMonth
import androidx.compose.material.icons.filled.Insights
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.ramcosta.composedestinations.navigation.navigate
import com.sayler666.gina.NavGraphs
import com.sayler666.gina.R
import com.sayler666.gina.appCurrentDestinationAsState
import com.sayler666.gina.destinations.CalendarScreenDestination
import com.sayler666.gina.destinations.Destination
import com.sayler666.gina.destinations.DirectionDestination
import com.sayler666.gina.destinations.InsightsScreenDestination
import com.sayler666.gina.destinations.JournalScreenDestination
import com.sayler666.gina.destinations.SettingsScreenDestination
import com.sayler666.gina.startAppDestination

enum class BottomDestinations(
    val destination: DirectionDestination,
    val icon: ImageVector,
    val label: Int
) {
    Journal(
        JournalScreenDestination,
        Icons.Filled.AutoStories,
        R.string.days_label
    ),
    Calendar(
        CalendarScreenDestination,
        Icons.Filled.CalendarMonth,
        R.string.calendar_label
    ),
    Insights(
        InsightsScreenDestination,
        Icons.Filled.Insights,
        R.string.insights_label
    ),
    Settings(
        SettingsScreenDestination,
        Icons.Filled.Settings,
        R.string.settings_label
    )
}

@Composable
fun BottomNavigationBar(navController: NavController) {
    val currentDestination: Destination = navController.appCurrentDestinationAsState().value
        ?: NavGraphs.root.startAppDestination

    val selectedIconColor = MaterialTheme.colorScheme.primary
    val unselectedIconColor = MaterialTheme.colorScheme.onSurface

    BottomNavigation(
        modifier = Modifier.height(56.dp),
        backgroundColor = MaterialTheme.colorScheme.surfaceVariant
    ) {
        BottomDestinations.values().forEachIndexed { index, dest ->
            BottomNavigationItem(
                selectedContentColor = selectedIconColor,
                unselectedContentColor = unselectedIconColor,
                icon = {
                    Icon(
                        dest.icon,
                        contentDescription = null,
                    )
                },
                label = {
                    Text(
                        stringResource(id = dest.label),
                        style = MaterialTheme.typography.labelSmall,
                    )
                },
                onClick = {
                    navController.navigate(dest.destination) {
                        launchSingleTop = true
                        restoreState = true
                        popUpTo(JournalScreenDestination.route)
                    }
                },
                selected = currentDestination == dest.destination,
            )
            if (index == 1) Spacer(modifier = Modifier.width(65.dp))
        }
    }
}
