package com.sayler666.gina.ginaApp

import androidx.compose.animation.ExperimentalAnimationApi
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.imePadding
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.FabPosition
import androidx.compose.material3.MaterialTheme.colorScheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.surfaceColorAtElevation
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.ViewModelStoreOwner
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.google.accompanist.navigation.animation.rememberAnimatedNavController
import com.ramcosta.composedestinations.DestinationsNavHost
import com.ramcosta.composedestinations.navigation.dependency
import com.sayler666.gina.NavGraphs
import com.sayler666.gina.appCurrentDestinationAsState
import com.sayler666.gina.destinations.CalendarScreenDestination
import com.sayler666.gina.destinations.Destination
import com.sayler666.gina.destinations.GalleryScreenDestination
import com.sayler666.gina.destinations.InsightsScreenDestination
import com.sayler666.gina.destinations.JournalScreenDestination
import com.sayler666.gina.destinations.SelectDatabaseScreenDestination
import com.sayler666.gina.destinations.SettingsScreenDestination
import com.sayler666.gina.ginaApp.navigation.BottomNavigationBar
import com.sayler666.gina.ginaApp.navigation.DayFab
import com.sayler666.gina.ginaApp.viewModel.BottomBarState
import com.sayler666.gina.ginaApp.viewModel.BottomBarState.*
import com.sayler666.gina.ginaApp.viewModel.BottomNavigationBarViewModel
import com.sayler666.gina.ginaApp.viewModel.GinaMainViewModel
import com.sayler666.gina.startAppDestination
import com.sayler666.gina.ui.NavigationBarColor
import com.sayler666.gina.ui.StatusBarColor
import com.sayler666.gina.ui.hideNavBar.VerticalBottomBarAnimation
import com.sayler666.gina.ui.theme.GinaTheme

@OptIn(ExperimentalAnimationApi::class)
@Composable
fun GinaApp(vm: GinaMainViewModel, activity: ViewModelStoreOwner) {
    val theme by vm.theme.collectAsStateWithLifecycle()
    GinaTheme(theme) {
        val rememberedDatabase: Boolean? by vm.hasRememberedDatabase.collectAsStateWithLifecycle()

        if (rememberedDatabase != null) {
            StatusBarColor(theme = theme)
            val startRoute = if (rememberedDatabase == false) SelectDatabaseScreenDestination
            else JournalScreenDestination

            val navController = rememberAnimatedNavController()
            val destination: Destination = navController.appCurrentDestinationAsState().value
                ?: NavGraphs.root.startAppDestination

            val bottomBarVm: BottomNavigationBarViewModel = hiltViewModel(activity)
            val bottomBarState: BottomBarState by bottomBarVm.state.collectAsStateWithLifecycle()

            val bottomBarVisibilityAnimation = VerticalBottomBarAnimation(
                maxOffset = 70.dp,
                visibleColor = colorScheme.surfaceColorAtElevation(3.dp),
                hiddenColor = colorScheme.surface
            )
            val bottomBarAnimInfoState by bottomBarVisibilityAnimation.animateAsState(
                visible = bottomBarState == Shown
            )

            NavigationBarColor(theme = theme, color = bottomBarAnimInfoState.color)
            Scaffold(
                Modifier.imePadding(),
                containerColor = colorScheme.background,
                floatingActionButton = {
                    if (destination.shouldShowScaffoldElements)
                        DayFab(
                            modifier = Modifier.offset(y = bottomBarAnimInfoState.yOffset),
                            navController = navController
                        )
                },
                floatingActionButtonPosition = FabPosition.End,
                bottomBar = {
                    if (destination.shouldShowScaffoldElements)
                        BottomNavigationBar(
                            modifier = Modifier
                                .offset(y = bottomBarAnimInfoState.yOffset)
                                .height(65.dp),
                            color = bottomBarAnimInfoState.color,
                            navController = navController,
                        )
                },
                content = { scaffoldPadding ->
                    val calculatedBP =
                        scaffoldPadding.calculateBottomPadding() - bottomBarAnimInfoState.yOffset

                    Column(
                        modifier = Modifier
                            .fillMaxSize()
                            .padding(bottom = if (calculatedBP < 0.dp) 0.dp else calculatedBP),
                    ) {
                        DestinationsNavHost(
                            navGraph = NavGraphs.root,
                            startRoute = startRoute,
                            navController = navController,
                            dependenciesContainerBuilder = {
                                dependency(hiltViewModel<BottomNavigationBarViewModel>(activity))
                            }
                            // engine = rememberAnimatedNavHostEngine()
                        )
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
        is GalleryScreenDestination,
        is SettingsScreenDestination -> true

        else -> false
    }
