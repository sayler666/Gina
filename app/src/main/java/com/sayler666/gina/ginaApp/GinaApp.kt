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
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.lifecycle.compose.ExperimentalLifecycleComposeApi
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.google.accompanist.navigation.animation.rememberAnimatedNavController
import com.ramcosta.composedestinations.DestinationsNavHost
import com.sayler666.gina.NavGraphs
import com.sayler666.gina.appCurrentDestinationAsState
import com.sayler666.gina.destinations.AddDayScreenDestination
import com.sayler666.gina.destinations.DayDetailsEditScreenDestination
import com.sayler666.gina.destinations.DayDetailsScreenDestination
import com.sayler666.gina.destinations.Destination
import com.sayler666.gina.destinations.FullImageDestination
import com.sayler666.gina.destinations.JournalScreenDestination
import com.sayler666.gina.destinations.SelectDatabaseScreenDestination
import com.sayler666.gina.ginaApp.navigation.AddDayFab
import com.sayler666.gina.ginaApp.navigation.BottomNavigationBar
import com.sayler666.gina.ginaApp.viewModel.GinaMainViewModel
import com.sayler666.gina.startAppDestination
import com.sayler666.gina.ui.NavigationBarColor
import com.sayler666.gina.ui.StatusBarColor
import com.sayler666.gina.ui.theme.GinaTheme

@OptIn(
    ExperimentalAnimationApi::class,
    ExperimentalLifecycleComposeApi::class
)
@Composable
fun GinaApp(vm: GinaMainViewModel) {
    GinaTheme {
        val rememberedDatabase: Boolean? by vm.hasRememberedDatabase.collectAsStateWithLifecycle()
        if (rememberedDatabase != null) {
            Surface(modifier = Modifier.fillMaxSize()) {
                StatusBarColor()
                NavigationBarColor()
                val startRoute = if (rememberedDatabase == false) SelectDatabaseScreenDestination
                else JournalScreenDestination

                val navController = rememberAnimatedNavController()
                val destination: Destination = navController.appCurrentDestinationAsState().value
                    ?: NavGraphs.root.startAppDestination

                Scaffold(
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
                    content = { padding ->
                        Column(modifier = Modifier.padding(padding)) {
                            DestinationsNavHost(
                                navGraph = NavGraphs.root,
                                startRoute = startRoute,
                                navController = navController,
                                //engine = rememberAnimatedNavHostEngine()
                            )
                        }
                    })
            }
        }
    }
}

private val Destination.shouldShowScaffoldElements
    get() = when (this) {
        is SelectDatabaseScreenDestination, is DayDetailsScreenDestination,
        is DayDetailsEditScreenDestination, is FullImageDestination,
        is AddDayScreenDestination -> false
        else -> true
    }
