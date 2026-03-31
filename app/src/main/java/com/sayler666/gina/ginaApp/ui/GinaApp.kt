package com.sayler666.gina.ginaApp.ui

import android.annotation.SuppressLint
import androidx.compose.animation.EnterTransition
import androidx.compose.animation.SharedTransitionLayout
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.slideOutHorizontally
import androidx.compose.animation.togetherWith
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.navigationBars
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.windowInsetsBottomHeight
import androidx.compose.material3.FabPosition
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.surfaceColorAtElevation
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.runtime.snapshots.SnapshotStateList
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.scale
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation3.runtime.entryProvider
import androidx.navigation3.ui.NavDisplay
import com.sayler666.core.compose.ANIMATION_DURATION
import com.sayler666.core.navigation.BottomBarState.Shown
import com.sayler666.gina.ginaApp.viewModel.GinaMainViewModel
import com.sayler666.gina.ginaApp.viewModel.GinaMainViewModel.ViewEvent.ConsumeDeepLink
import com.sayler666.gina.ginaApp.viewModel.GinaMainViewModel.ViewState
import com.sayler666.gina.navigation.CombinedNavEntryFallback
import com.sayler666.gina.navigation.EntryProviderInstaller
import com.sayler666.gina.navigation.Navigator
import com.sayler666.gina.navigation.routes.AddDay
import com.sayler666.gina.navigation.routes.Route
import com.sayler666.gina.ui.LocalNavigator
import com.sayler666.gina.ui.LocalSharedTransitionScope
import com.sayler666.gina.ui.LocalTheme
import com.sayler666.gina.ui.hideNavBar.BOTTOM_NAV_HEIGHT
import com.sayler666.gina.ui.hideNavBar.VerticalBottomBarAnimation
import com.sayler666.gina.ui.theme.GinaTheme
import dev.chrisbanes.haze.HazeState
import dev.chrisbanes.haze.hazeSource
import dev.chrisbanes.haze.rememberHazeState

@SuppressLint("UnusedMaterial3ScaffoldPaddingParameter")
@Composable
fun GinaApp(
    vm: GinaMainViewModel,
    installers: Set<@JvmSuppressWildcards EntryProviderInstaller>,
    fallback: CombinedNavEntryFallback
) {
    val viewState by vm.viewState.collectAsStateWithLifecycle()

    GinaTheme(viewState.theme) {
        val backStack = vm.backStack
        val navigator = remember { Navigator(backStack) }

        if (backStack.isNotEmpty()) {
            // Handle deep links arriving via onNewIntent (app already running).
            LaunchedEffect(viewState.pendingDeepLink) {
                val route = viewState.pendingDeepLink ?: return@LaunchedEffect
                if (backStack.none { it is AddDay }) navigator.navigate(route)
                vm.onViewEvent(ConsumeDeepLink)
            }

            SharedTransitionLayout {
                CompositionLocalProvider(
                    LocalNavigator provides navigator,
                    LocalTheme provides viewState.theme,
                    LocalSharedTransitionScope provides this,
                ) {
                    val hazeState = rememberHazeState()
                    Scaffold(
                        Modifier.fillMaxSize(),
                        containerColor = MaterialTheme.colorScheme.background,
                        floatingActionButton = {
                            AddDayButton(viewState = viewState)
                        },
                        floatingActionButtonPosition = FabPosition.End,
                        bottomBar = {
                            BottomNavigation(
                                backStack = backStack,
                                hazeState = hazeState
                            )
                        },
                        content = {
                            NavigationContainer(
                                hazeState = hazeState,
                                backStack = backStack,
                                installers = installers,
                                fallback = fallback
                            )
                        }
                    )
                }
            } // SharedTransitionLayout
        }
    }
}

@Composable
private fun AddDayButton(
    viewState: ViewState
) {
    val navigator = LocalNavigator.current

    val visibilityAnimation = VerticalBottomBarAnimation(
        maxOffset = BOTTOM_NAV_HEIGHT,
        visibleColor = MaterialTheme.colorScheme.surfaceColorAtElevation(3.dp),
        hiddenColor = Color.Transparent
    )
    val animInfoState by visibilityAnimation.animateAsState(
        visible = viewState.bottomBarState == Shown
    )
    if (navigator.currentRoute()?.showScaffoldElements == true) {
        AddDayFab(
            modifier = Modifier
                .offset(y = animInfoState.yOffset)
                .scale(animInfoState.alpha)
                .alpha(animInfoState.alpha),
            onNavigateToAddDay = { navigator.navigate(AddDay()) }
        )
    }
}

@Composable
private fun BottomNavigation(
    backStack: SnapshotStateList<Route>,
    hazeState: HazeState
) {
    val navigator = LocalNavigator.current
    val currentRoute = navigator.currentRoute()

    if (currentRoute?.showScaffoldElements == true)
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            modifier = Modifier
                .background(
                    brush = Brush.verticalGradient(
                        colors = listOf(
                            Color.Transparent,
                            MaterialTheme.colorScheme.background,
                        )
                    )
                )
        ) {
            BottomNavigationBar(
                modifier = Modifier
                    .height(BOTTOM_NAV_HEIGHT)
                    .fillMaxWidth(0.95f),
                color = MaterialTheme.colorScheme.surfaceColorAtElevation(9.dp).copy(alpha = 0.1f),
                currentRoute = currentRoute,
                backStack = backStack,
                hazeState = hazeState,
            )
            Spacer(
                modifier = Modifier
                    .fillMaxWidth()
                    .windowInsetsBottomHeight(WindowInsets.navigationBars)
            )
        }
}

@Composable
private fun NavigationContainer(
    hazeState: HazeState,
    backStack: SnapshotStateList<Route>,
    installers: Set<@JvmSuppressWildcards EntryProviderInstaller>,
    fallback: CombinedNavEntryFallback,
) {
    val entryProviders = remember(installers, fallback) {
        entryProvider(fallback = fallback) {
            installers.forEach { installer -> installer(this) }
        }
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .hazeSource(hazeState)
    ) {
        NavDisplay(
            backStack = backStack,
            onBack = { backStack.removeLastOrNull() },
            transitionSpec = {
                fadeIn(tween(ANIMATION_DURATION)) togetherWith
                        fadeOut(tween(ANIMATION_DURATION))
            },
            popTransitionSpec = {
                fadeIn(tween(ANIMATION_DURATION)) togetherWith
                        fadeOut(tween(ANIMATION_DURATION))
            },
            predictivePopTransitionSpec = {
                EnterTransition.None togetherWith
                        slideOutHorizontally (
                            targetOffsetX = { it },
                            animationSpec = tween(400)
                        )
            },
            entryProvider = entryProviders
        )
    }
}
