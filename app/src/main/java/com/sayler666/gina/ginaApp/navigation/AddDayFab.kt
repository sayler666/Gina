package com.sayler666.gina.ginaApp.navigation

import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.navigation.NavHostController
import com.ramcosta.composedestinations.navigation.navigate
import com.sayler666.gina.destinations.AddDayScreenDestination

@Composable
fun AddDayFab(navController: NavHostController) {
    FloatingActionButton(
        containerColor = MaterialTheme.colorScheme.primary,
        shape = CircleShape,
        onClick = {
            navController.navigate(AddDayScreenDestination()) {
                launchSingleTop = true
                restoreState = false
            }
        }) {
        Icon(
            Icons.Filled.Add,
            contentDescription = "Add new entry",
            tint = MaterialTheme.colorScheme.surfaceVariant
        )
    }
}
