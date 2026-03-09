package com.sayler666.gina.di

import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.slideInVertically
import androidx.compose.animation.slideOutVertically
import androidx.compose.animation.togetherWith
import androidx.navigation3.runtime.EntryProviderScope
import androidx.navigation3.runtime.NavEntry
import androidx.navigation3.ui.NavDisplay
import com.sayler666.core.compose.ANIMATION_DURATION
import com.sayler666.domain.model.Way
import com.sayler666.gina.addDay.AddDayScreen
import com.sayler666.gina.attachments.ImagePreviewScreen
import com.sayler666.gina.attachments.ImagePreviewTmpScreen
import com.sayler666.gina.calendar.CalendarScreen
import com.sayler666.gina.dayDetails.DayDetailsScreen
import com.sayler666.gina.dayDetailsEdit.DayDetailsEditScreen
import com.sayler666.gina.feature.journal.ui.JournalScreen
import com.sayler666.gina.friends.ManageFriendsScreen
import com.sayler666.gina.gallery.GalleryScreen
import com.sayler666.gina.gameoflife.ui.GameOfLifeScreen
import com.sayler666.gina.insights.InsightsScreen
import com.sayler666.gina.navigation.Route
import com.sayler666.gina.selectdatabase.ui.SelectDatabaseScreen
import com.sayler666.gina.settings.ui.SettingsScreen
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import dagger.multibindings.IntoSet

typealias EntryProviderInstaller = EntryProviderScope<Route>.() -> Unit
typealias NavEntryFallback = (Route) -> NavEntry<Route>

@Module
@InstallIn(SingletonComponent::class)
object NavModule {

    @Provides
    @IntoSet
    fun provideCoreInstaller(): @JvmSuppressWildcards EntryProviderInstaller = {
        entry<Route.SelectDatabase> { SelectDatabaseScreen() }
        entry<Route.Journal> { JournalScreen() }
        entry<Route.Calendar> { CalendarScreen() }
        entry<Route.Gallery> { GalleryScreen() }
        entry<Route.Insights> { InsightsScreen() }
        entry<Route.Settings> { SettingsScreen() }
        entry<Route.ManageFriends> { ManageFriendsScreen() }
        entry<Route.GameOfLife> { GameOfLifeScreen() }
    }

    @Provides
    @IntoSet
    fun provideDayInstaller(): @JvmSuppressWildcards EntryProviderInstaller = {
        entry<Route.DayDetailsEdit> { DayDetailsEditScreen(route = it) }
        entry<Route.AddDay> { AddDayScreen(route = it) }
        entry<Route.ImagePreview> { ImagePreviewScreen(route = it) }
        entry<Route.ImagePreviewTmp> { ImagePreviewTmpScreen(route = it) }
    }

    @Provides
    fun provideDayDetailsFallback(): @JvmSuppressWildcards NavEntryFallback = { key ->
        when (key) {
            is Route.DayDetails -> NavEntry(
                key = key,
                metadata = when (key.way) {
                    Way.NEXT -> NavDisplay.transitionSpec {
                        slideInVertically(tween(ANIMATION_DURATION)) { it } + fadeIn(
                            tween(
                                ANIMATION_DURATION
                            )
                        ) togetherWith
                                slideOutVertically(tween(ANIMATION_DURATION)) { -it } + fadeOut(
                            tween(ANIMATION_DURATION)
                        )
                    }

                    Way.PREVIOUS -> NavDisplay.transitionSpec {
                        slideInVertically(tween(ANIMATION_DURATION)) { -it } + fadeIn(
                            tween(
                                ANIMATION_DURATION
                            )
                        ) togetherWith
                                slideOutVertically(tween(ANIMATION_DURATION)) { it } + fadeOut(
                            tween(
                                ANIMATION_DURATION
                            )
                        )
                    }

                    else -> emptyMap()
                }
            ) {
                DayDetailsScreen(route = key)
            }

            else -> error("Unknown route: $key")
        }
    }
}
