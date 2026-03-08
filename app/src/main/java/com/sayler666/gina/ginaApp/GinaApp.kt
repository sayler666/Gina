package com.sayler666.gina.ginaApp

import android.annotation.SuppressLint
import androidx.compose.animation.SharedTransitionLayout
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.togetherWith
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
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation3.runtime.entryProvider
import androidx.navigation3.ui.NavDisplay
import com.sayler666.core.compose.ANIMATION_DURATION
import com.sayler666.core.navigation.BottomBarState
import com.sayler666.core.navigation.BottomBarState.Shown
import com.sayler666.gina.di.EntryProviderInstaller
import com.sayler666.gina.di.NavEntryFallback
import com.sayler666.gina.ginaApp.navigation.BottomNavigationBar
import com.sayler666.gina.ginaApp.navigation.DayFab
import com.sayler666.gina.ginaApp.viewModel.GinaMainViewModel
import com.sayler666.gina.navigation.Navigator
import com.sayler666.gina.navigation.Route
import com.sayler666.gina.ui.LocalNavigator
import com.sayler666.gina.ui.LocalSharedTransitionScope
import com.sayler666.gina.ui.LocalTheme
import com.sayler666.gina.ui.NavigationBarColor
import com.sayler666.gina.ui.StatusBarColor
import com.sayler666.gina.ui.hideNavBar.BOTTOM_NAV_HEIGHT
import com.sayler666.gina.ui.hideNavBar.VerticalBottomBarAnimation
import com.sayler666.gina.ui.theme.GinaTheme

@SuppressLint("UnusedMaterial3ScaffoldPaddingParameter")
@Composable
fun GinaApp(
    vm: GinaMainViewModel,
    installers: Set<@JvmSuppressWildcards EntryProviderInstaller>,
    fallback: NavEntryFallback
) {
    val theme by vm.theme.collectAsStateWithLifecycle()
    GinaTheme(theme) {
        // ViewModel-owned backStack survives configuration changes.
        val backStack = vm.backStack
        val navigator = remember { Navigator(backStack) }

        if (backStack.isNotEmpty()) {
            val currentRoute = backStack.lastOrNull()

            // Handle deep links arriving via onNewIntent (app already running).
            // Fresh-start deep links are already baked into backStack by the ViewModel.
            val pendingDeepLink: Route? by vm.pendingDeepLink.collectAsStateWithLifecycle()
            LaunchedEffect(pendingDeepLink) {
                val route = pendingDeepLink ?: return@LaunchedEffect
                if (backStack.none { it is Route.AddDay }) {
                    navigator.navigate(route)
                }
                vm.consumeDeepLink()
            }

            val bottomBarState: BottomBarState by vm.bottomBarState.collectAsStateWithLifecycle()
            val bottomBarVisibilityAnimation = VerticalBottomBarAnimation(
                maxOffset = BOTTOM_NAV_HEIGHT,
                visibleColor = colorScheme.surfaceColorAtElevation(3.dp),
                hiddenColor = Color.Transparent
            )
            val bottomBarAnimInfoState by bottomBarVisibilityAnimation.animateAsState(
                visible = bottomBarState == Shown
            )

            val entryProviderFn = remember(installers, fallback) {
                entryProvider(fallback = fallback) {
                    installers.forEach { installer -> installer(this) }
                }
            }

            SharedTransitionLayout {
                CompositionLocalProvider(
                    LocalNavigator provides navigator,
                    LocalTheme provides theme,
                    LocalSharedTransitionScope provides this,
                ) {
                    StatusBarColor(color = colorScheme.surface)
                    NavigationBarColor(color = bottomBarAnimInfoState.color)
                    Scaffold(
                        Modifier.fillMaxSize(),
                        containerColor = colorScheme.background,
                        floatingActionButton = {
                            if (currentRoute?.showScaffoldElements == true)
                                DayFab(
                                    modifier = Modifier.offset(y = bottomBarAnimInfoState.yOffset),
                                    onNavigateToAddDay = { backStack.add(Route.AddDay()) }
                                )
                        },
                        floatingActionButtonPosition = FabPosition.End,
                        bottomBar = {
                            if (currentRoute?.showScaffoldElements == true)
                                BottomNavigationBar(
                                    modifier = Modifier
                                        .windowInsetsPadding(WindowInsets.navigationBars)
                                        .offset(y = bottomBarAnimInfoState.yOffset)
                                        .height(BOTTOM_NAV_HEIGHT)
                                        .alpha(bottomBarAnimInfoState.alpha),
                                    color = bottomBarAnimInfoState.color,
                                    currentRoute = currentRoute,
                                    backStack = backStack
                                )
                        },
                        content = {
                            Column(modifier = Modifier.fillMaxSize()) {
                                NavDisplay(
                                    backStack = backStack,
                                    onBack = { backStack.removeLastOrNull() },
                                    transitionSpec = {
                                        fadeIn(tween(ANIMATION_DURATION)) togetherWith fadeOut(
                                            tween(
                                                ANIMATION_DURATION
                                            )
                                        )
                                    },
                                    popTransitionSpec = {
                                        fadeIn(tween(ANIMATION_DURATION)) togetherWith fadeOut(
                                            tween(
                                                ANIMATION_DURATION
                                            )
                                        )
                                    },
                                    entryProvider = entryProviderFn
                                )
                            }
                        }
                    )
                }
            } // SharedTransitionLayout
        }
    }
}
