package com.sayler666.gina.ginaApp.navigation

import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import androidx.navigation.NavHostController
import com.ramcosta.composedestinations.navigation.navigate
import com.sayler666.gina.R
import com.sayler666.gina.destinations.AddDayScreenDestination

@Composable
fun DayFab(modifier: Modifier = Modifier, navController: NavHostController) {
    FloatingActionButton(
        modifier = modifier,
        containerColor = MaterialTheme.colorScheme.primary,
        shape = CircleShape,
        onClick = {
            navController.navigate(AddDayScreenDestination()) {
                launchSingleTop = true
                restoreState = false
            }
        }) {
        Icon(
            painter = painterResource(R.drawable.feather_icon),
            contentDescription = "Add new entry",
            modifier = Modifier
                .size(65.dp)
                .padding(12.dp)
        )
    }
}
