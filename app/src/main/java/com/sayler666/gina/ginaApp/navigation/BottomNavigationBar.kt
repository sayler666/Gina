package com.sayler666.gina.ginaApp.navigation

import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.width
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AutoStories
import androidx.compose.material.icons.filled.CalendarMonth
import androidx.compose.material.icons.filled.Insights
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material.icons.twotone.AutoStories
import androidx.compose.material.icons.twotone.CalendarMonth
import androidx.compose.material.icons.twotone.Insights
import androidx.compose.material.icons.twotone.Settings
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.Text
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
fun BottomNavigationBar(navController: NavController) {
    val currentDestination: Destination = navController.appCurrentDestinationAsState().value
        ?: NavGraphs.root.startAppDestination

    NavigationBar(
        containerColor = MaterialTheme.colorScheme.surfaceVariant,
        modifier = Modifier
            .navigationBarsPadding()
            .height(80.dp)
    ) {
        BottomDestinations.values().forEachIndexed { index, dest ->
            if (index == 0) Spacer(modifier = Modifier.width(8.dp))
            NavigationBarItem(
                icon = {
                    Icon(
                        if (currentDestination == dest.destination) dest.iconSelected else dest.icon,
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
            if (index == 1) Spacer(modifier = Modifier.width(55.dp))
            if (index == 3) Spacer(modifier = Modifier.width(8.dp))
        }
    }
}
