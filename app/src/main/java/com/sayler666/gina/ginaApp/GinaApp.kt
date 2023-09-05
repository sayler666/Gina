package com.sayler666.gina.ginaApp

import androidx.compose.animation.ExperimentalAnimationApi
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
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
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.ViewModelStoreOwner
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation.compose.rememberNavController
import com.google.accompanist.navigation.material.ExperimentalMaterialNavigationApi
import com.ramcosta.composedestinations.DestinationsNavHost
import com.ramcosta.composedestinations.animations.defaults.NestedNavGraphDefaultAnimations
import com.ramcosta.composedestinations.animations.defaults.RootNavGraphDefaultAnimations
import com.ramcosta.composedestinations.animations.rememberAnimatedNavHostEngine
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
import com.sayler666.gina.ginaApp.viewModel.BottomBarState.Shown
import com.sayler666.gina.ginaApp.viewModel.BottomNavigationBarViewModel
import com.sayler666.gina.ginaApp.viewModel.GinaMainViewModel
import com.sayler666.gina.startAppDestination
import com.sayler666.gina.ui.NavigationBarColor
import com.sayler666.gina.ui.StatusBarColor
import com.sayler666.gina.ui.hideNavBar.VerticalBottomBarAnimation
import com.sayler666.gina.ui.theme.GinaTheme

@OptIn(ExperimentalMaterialNavigationApi::class, ExperimentalAnimationApi::class)
@Composable
fun GinaApp(vm: GinaMainViewModel, activity: ViewModelStoreOwner) {
    val theme by vm.theme.collectAsStateWithLifecycle()
    GinaTheme(theme) {
        val rememberedDatabase: Boolean? by vm.hasRememberedDatabase.collectAsStateWithLifecycle()

        if (rememberedDatabase != null) {
            StatusBarColor(theme = theme)
            val startRoute = if (rememberedDatabase == false) SelectDatabaseScreenDestination
            else JournalScreenDestination

            val navController = rememberNavController()
            val destination: Destination = navController.appCurrentDestinationAsState().value
                ?: NavGraphs.root.startAppDestination

            val navHostEngine = rememberAnimatedNavHostEngine(
                navHostContentAlignment = Alignment.BottomCenter,
                rootDefaultAnimations = RootNavGraphDefaultAnimations(
                    enterTransition = { fadeIn(animationSpec = tween(500)) },
                    exitTransition = { fadeOut(animationSpec = tween(500)) }
                )
            )

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
                                .height(70.dp),
                            color = bottomBarAnimInfoState.color,
                            navController = navController
                        )
                },
                content = { scaffoldPadding ->
                    val calculatedBP =
                        scaffoldPadding.calculateBottomPadding() - bottomBarAnimInfoState.yOffset

                    Column(
                        modifier = Modifier
                            .fillMaxSize()
                            .padding(bottom = if (calculatedBP < 0.dp) 0.dp else calculatedBP)
                    ) {
                        DestinationsNavHost(
                            navGraph = NavGraphs.root,
                            startRoute = startRoute,
                            navController = navController,
                            dependenciesContainerBuilder = {
                                dependency(hiltViewModel<BottomNavigationBarViewModel>(activity))
                            },
                            engine = navHostEngine
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
