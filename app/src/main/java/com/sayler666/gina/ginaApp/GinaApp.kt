package com.sayler666.gina.ginaApp

import androidx.compose.animation.ExperimentalAnimationApi
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.imePadding
import androidx.compose.foundation.layout.padding
import androidx.compose.material.FabPosition
import androidx.compose.material.Scaffold
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.google.accompanist.navigation.animation.rememberAnimatedNavController
import com.ramcosta.composedestinations.DestinationsNavHost
import com.sayler666.gina.NavGraphs
import com.sayler666.gina.appCurrentDestinationAsState
import com.sayler666.gina.destinations.CalendarScreenDestination
import com.sayler666.gina.destinations.Destination
import com.sayler666.gina.destinations.InsightsScreenDestination
import com.sayler666.gina.destinations.JournalScreenDestination
import com.sayler666.gina.destinations.SelectDatabaseScreenDestination
import com.sayler666.gina.destinations.SettingsScreenDestination
import com.sayler666.gina.ginaApp.navigation.AddDayFab
import com.sayler666.gina.ginaApp.navigation.BottomNavigationBar
import com.sayler666.gina.ginaApp.viewModel.GinaMainViewModel
import com.sayler666.gina.startAppDestination
import com.sayler666.gina.ui.NavigationBarColor
import com.sayler666.gina.ui.StatusBarColor
import com.sayler666.gina.ui.theme.GinaTheme
import timber.log.Timber

@OptIn(ExperimentalAnimationApi::class)
@Composable
fun GinaApp(vm: GinaMainViewModel) {
    GinaTheme {
        val rememberedDatabase: Boolean? by vm.hasRememberedDatabase.collectAsStateWithLifecycle()
        if (rememberedDatabase != null) {
            StatusBarColor()
            NavigationBarColor()
            val startRoute = if (rememberedDatabase == false) SelectDatabaseScreenDestination
            else JournalScreenDestination

            val navController = rememberAnimatedNavController()
            val destination: Destination = navController.appCurrentDestinationAsState().value
                ?: NavGraphs.root.startAppDestination

            Scaffold(
                Modifier.imePadding(),
                backgroundColor = MaterialTheme.colorScheme.background,
                floatingActionButton = {
                    if (destination.shouldShowScaffoldElements) AddDayFab(navController)
                },
                floatingActionButtonPosition = FabPosition.Center,
                isFloatingActionButtonDocked = true,
                bottomBar = {
                    if (destination.shouldShowScaffoldElements) BottomNavigationBar(
                        navController
                    )
                },
                content = { scaffoldPadding ->
                    var bottomP = scaffoldPadding.calculateBottomPadding()
                    Timber.d("Padding: ${scaffoldPadding.calculateBottomPadding()}")
                    bottomP = if (bottomP >= 80.dp) bottomP - 48.dp else bottomP
                    Box(
                        modifier = Modifier
                            .padding(bottom = bottomP)
                    ) {
                        Column(
                            modifier = Modifier
                                .fillMaxSize()
                        ) {
                            DestinationsNavHost(
                                navGraph = NavGraphs.root,
                                startRoute = startRoute,
                                navController = navController,
                                //engine = rememberAnimatedNavHostEngine()
                            )
                        }
                    }
                })
        }
    }
}

private val Destination.shouldShowScaffoldElements
    get() = when (this) {
        is JournalScreenDestination,
        is CalendarScreenDestination,
        is InsightsScreenDestination,
        is SettingsScreenDestination -> true
        else -> false
    }
