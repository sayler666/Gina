package com.sayler666.gina.ginaApp

import android.annotation.SuppressLint
import androidx.compose.animation.ExperimentalAnimationApi
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.navigationBars
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.windowInsetsPadding
import androidx.compose.material3.FabPosition
import androidx.compose.material3.MaterialTheme.colorScheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.surfaceColorAtElevation
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation.compose.rememberNavController
import com.google.accompanist.navigation.material.ExperimentalMaterialNavigationApi
import com.ramcosta.composedestinations.DestinationsNavHost
import com.ramcosta.composedestinations.animations.defaults.RootNavGraphDefaultAnimations
import com.ramcosta.composedestinations.animations.rememberAnimatedNavHostEngine
import com.sayler666.core.compose.ANIMATION_DURATION
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
import com.sayler666.gina.ginaApp.navigation.BottomBarState
import com.sayler666.gina.ginaApp.navigation.BottomBarState.Shown
import com.sayler666.gina.ginaApp.viewModel.GinaMainViewModel
import com.sayler666.gina.startAppDestination
import com.sayler666.gina.ui.NavigationBarColor
import com.sayler666.gina.ui.StatusBarColor
import com.sayler666.gina.ui.hideNavBar.VerticalBottomBarAnimation
import com.sayler666.gina.ui.theme.GinaTheme

@SuppressLint("UnusedMaterial3ScaffoldPaddingParameter")
@OptIn(
    ExperimentalMaterialNavigationApi::class, ExperimentalAnimationApi::class,
)
@Composable
fun GinaApp(
    vm: GinaMainViewModel
) {
    val theme by vm.theme.collectAsStateWithLifecycle()
    GinaTheme(theme) {
        val rememberedDatabase: Boolean? by vm.hasRememberedDatabase.collectAsStateWithLifecycle()

        if (rememberedDatabase != null) {
            StatusBarColor(color = colorScheme.surface, theme = theme)
            val startRoute = if (rememberedDatabase == false) SelectDatabaseScreenDestination
            else JournalScreenDestination

            val navController = rememberNavController()
            val destination: Destination = navController.appCurrentDestinationAsState().value
                ?: NavGraphs.root.startAppDestination

            val navHostEngine = rememberAnimatedNavHostEngine(
                navHostContentAlignment = Alignment.BottomCenter,
                rootDefaultAnimations = RootNavGraphDefaultAnimations(
                    enterTransition = { fadeIn(animationSpec = tween(ANIMATION_DURATION)) },
                    exitTransition = { fadeOut(animationSpec = tween(ANIMATION_DURATION)) }
                )
            )

            val bottomBarState: BottomBarState by vm.bottomBarState.collectAsStateWithLifecycle()
            val bottomBarVisibilityAnimation = VerticalBottomBarAnimation(
                maxOffset = BOTTOM_NAV_HEIGHT,
                visibleColor = colorScheme.surfaceColorAtElevation(3.dp),
                hiddenColor = Color.Transparent
            )
            val bottomBarAnimInfoState by bottomBarVisibilityAnimation.animateAsState(
                visible = bottomBarState == Shown
            )

            NavigationBarColor(theme = theme, color = bottomBarAnimInfoState.color)
            Scaffold(
                Modifier.fillMaxSize(),
                containerColor = colorScheme.background,
                floatingActionButton = {
                    if (destination.shouldShowScaffoldElements)
                        DayFab(
                            modifier = Modifier
                                .offset(y = bottomBarAnimInfoState.yOffset),
                            navController = navController
                        )
                },
                floatingActionButtonPosition = FabPosition.End,
                bottomBar = {
                    if (destination.shouldShowScaffoldElements)
                        BottomNavigationBar(
                            modifier = Modifier
                                .windowInsetsPadding(WindowInsets.navigationBars)
                                .offset(y = bottomBarAnimInfoState.yOffset)
                                .height(BOTTOM_NAV_HEIGHT)
                                .alpha(bottomBarAnimInfoState.alpha),
                            color = bottomBarAnimInfoState.color,
                            navController = navController
                        )
                },
                content = {
                    Column(
                        modifier = Modifier.fillMaxSize()
                    ) {
                        DestinationsNavHost(
                            navGraph = NavGraphs.root,
                            startRoute = startRoute,
                            navController = navController,
                            engine = navHostEngine
                        )
                    }
                })
        }
    }
}

val BOTTOM_NAV_HEIGHT = 70.dp

private val Destination.shouldShowScaffoldElements
    get() = when (this) {
        is JournalScreenDestination,
        is CalendarScreenDestination,
        is InsightsScreenDestination,
        is GalleryScreenDestination,
        is SettingsScreenDestination -> true

        else -> false
    }
