package com.sayler666.gina.day.di

import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.slideInVertically
import androidx.compose.animation.slideOutVertically
import androidx.compose.animation.togetherWith
import androidx.navigation3.runtime.NavEntry
import androidx.navigation3.ui.NavDisplay
import com.sayler666.core.compose.ANIMATION_DURATION
import com.sayler666.domain.model.Way
import com.sayler666.gina.day.addDay.ui.AddDayScreen
import com.sayler666.gina.day.attachments.ui.ImagePreviewScreen
import com.sayler666.gina.day.attachments.ui.ImagePreviewTmpScreen
import com.sayler666.gina.day.dayDetails.ui.DayDetailsScreen
import com.sayler666.gina.day.dayDetailsEdit.ui.DayDetailsEditScreen
import com.sayler666.gina.navigation.AddDay
import com.sayler666.gina.navigation.DayDetails
import com.sayler666.gina.navigation.DayDetailsEdit
import com.sayler666.gina.navigation.EntryProviderInstaller
import com.sayler666.gina.navigation.ImagePreview
import com.sayler666.gina.navigation.ImagePreviewTmp
import com.sayler666.gina.navigation.NavEntryFallback
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import dagger.multibindings.IntoSet

@Module
@InstallIn(SingletonComponent::class)
object DayNavModule {

    @Provides
    @IntoSet
    fun provideInstaller(): @JvmSuppressWildcards EntryProviderInstaller = {
        entry<DayDetailsEdit> { DayDetailsEditScreen(dayId = it.dayId) }
        entry<AddDay> { AddDayScreen(date = it.date) }
        entry<ImagePreview> {
            ImagePreviewScreen(
                initialAttachmentId = it.initialAttachmentId,
                source = it.source,
            )
        }
        entry<ImagePreviewTmp> {
            ImagePreviewTmpScreen(
                image = it.image,
                mimeType = it.mimeType,
            )
        }
    }

    @Provides
    @IntoSet
    fun provideFallback(): @JvmSuppressWildcards NavEntryFallback = { key ->
        when (key) {
            is DayDetails -> NavEntry(
                key = key,
                metadata = when (key.way) {
                    Way.NEXT -> NavDisplay.transitionSpec {
                        slideInVertically(tween(ANIMATION_DURATION)) { it } +
                                fadeIn(tween(ANIMATION_DURATION)) togetherWith
                                slideOutVertically(tween(ANIMATION_DURATION)) { -it } +
                                fadeOut(tween(ANIMATION_DURATION))
                    }

                    Way.PREVIOUS -> NavDisplay.transitionSpec {
                        slideInVertically(tween(ANIMATION_DURATION)) { -it } +
                                fadeIn(tween(ANIMATION_DURATION)) togetherWith
                                slideOutVertically(tween(ANIMATION_DURATION)) { it } +
                                fadeOut(tween(ANIMATION_DURATION))
                    }

                    else -> emptyMap()
                }
            ) {
                DayDetailsScreen(dayId = key.dayId)
            }

            else -> null
        }
    }
}
