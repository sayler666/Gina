package com.sayler666.gina.ginaApp

import androidx.compose.animation.ExperimentalAnimationApi
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material.FabPosition
import androidx.compose.material.Scaffold
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import com.google.accompanist.navigation.animation.rememberAnimatedNavController
import com.google.accompanist.navigation.material.ExperimentalMaterialNavigationApi
import com.ramcosta.composedestinations.DestinationsNavHost
import com.ramcosta.composedestinations.animations.rememberAnimatedNavHostEngine
import com.sayler666.gina.NavGraphs
import com.sayler666.gina.appCurrentDestinationAsState
import com.sayler666.gina.destinations.DaysListScreenDestination
import com.sayler666.gina.destinations.Destination
import com.sayler666.gina.destinations.SelectDatabaseScreenDestination
import com.sayler666.gina.ginaApp.navigation.AddDayFab
import com.sayler666.gina.ginaApp.navigation.BottomNavigationBar
import com.sayler666.gina.ginaApp.viewModel.GinaMainViewModel
import com.sayler666.gina.startAppDestination
import com.sayler666.gina.ui.StatusBarColor
import com.sayler666.gina.ui.theme.GinaTheme

@OptIn(ExperimentalAnimationApi::class, ExperimentalMaterialNavigationApi::class)
@Composable
fun GinaApp(vm: GinaMainViewModel) {
    GinaTheme {
        Surface(modifier = Modifier.fillMaxSize()) {
            StatusBarColor()

            val startRoute = if (!vm.hasRememberedDatabase) SelectDatabaseScreenDestination else DaysListScreenDestination
            val navController = rememberAnimatedNavController()
            val destination: Destination = navController.appCurrentDestinationAsState().value
                ?: NavGraphs.root.startAppDestination

            Scaffold(
                backgroundColor = MaterialTheme.colorScheme.background,
                floatingActionButton = {
                    if (destination.shouldShowScaffoldElements) AddDayFab()
                },
                floatingActionButtonPosition = FabPosition.Center,
                isFloatingActionButtonDocked = true,
                bottomBar = {
                    if (destination.shouldShowScaffoldElements) BottomNavigationBar(navController)
                },
                content = { padding ->
                    Column(modifier = Modifier.padding(padding)) {
                        DestinationsNavHost(
                            navGraph = NavGraphs.root,
                            startRoute = startRoute,
                            navController = navController,
                            engine = rememberAnimatedNavHostEngine()
                        )
                    }
                })
        }
    }
}

private val Destination.shouldShowScaffoldElements get() = this !is SelectDatabaseScreenDestination
