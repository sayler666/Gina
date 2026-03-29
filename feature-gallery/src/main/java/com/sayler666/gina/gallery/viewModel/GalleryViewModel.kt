package com.sayler666.gina.gallery.viewModel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.sayler666.core.navigation.BottomNavigationVisibilityManager
import com.sayler666.gina.gallery.usecase.ImageAttachmentsRepository
import com.sayler666.gina.gallery.usecase.Thumbnail
import com.sayler666.gina.gallery.viewModel.GalleryViewModel.ViewAction.NavigateToImage
import com.sayler666.gina.gallery.viewModel.GalleryViewModel.ViewEvent.OnFetchNextPage
import com.sayler666.gina.gallery.viewModel.GalleryViewModel.ViewEvent.OnFiltersChanged
import com.sayler666.gina.gallery.viewModel.GalleryViewModel.ViewEvent.OnHideBottomBar
import com.sayler666.gina.gallery.viewModel.GalleryViewModel.ViewEvent.OnImageClick
import com.sayler666.gina.gallery.viewModel.GalleryViewModel.ViewEvent.OnResetFilters
import com.sayler666.gina.gallery.viewModel.GalleryViewModel.ViewEvent.OnShowBottomBar
import com.sayler666.gina.ui.filters.FiltersState
import com.sayler666.gina.ui.filters.toDateBounds
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.drop
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.flow.update
import javax.inject.Inject

@HiltViewModel
class GalleryViewModel @Inject constructor(
    private val imageAttachmentsRepository: ImageAttachmentsRepository,
    private val galleryMapper: GalleryMapper,
    private val bottomNavigationVisibilityManager: BottomNavigationVisibilityManager,
) : ViewModel() {

    private val mutableViewState = MutableStateFlow<GalleryState>(GalleryState.LoadingState)
    val viewState: StateFlow<GalleryState> = mutableViewState.asStateFlow()

    private val mutableFiltersState = MutableStateFlow(FiltersState())
    val filtersState: StateFlow<FiltersState> = mutableFiltersState.asStateFlow()

    private val mutableViewActions = Channel<ViewAction>(Channel.BUFFERED)
    val viewActions = mutableViewActions.receiveAsFlow()

    init {
        observeImages()
    }

    fun onViewEvent(event: ViewEvent) {
        when (event) {
            OnHideBottomBar -> bottomNavigationVisibilityManager.hide()
            OnShowBottomBar -> bottomNavigationVisibilityManager.show()
            OnFetchNextPage -> imageAttachmentsRepository.fetchNextPage()
            is OnImageClick -> mutableViewActions.trySend(NavigateToImage(event.id))
            is OnFiltersChanged -> updateFilters(event.filters)
            OnResetFilters -> updateFilters(FiltersState())
        }
    }

    private fun observeImages() {
        imageAttachmentsRepository.fetchNextPage()
        imageAttachmentsRepository
            .attachment
            .drop(1)
            .combine(mutableFiltersState) { thumbnails, filters ->
                val filtered = applyFilters(thumbnails, filters)
                galleryMapper.toGalleryState(filtered, filters.anyFilterActive)
            }
            .onEach { mutableViewState.value = it }
            .launchIn(viewModelScope)
    }

    private fun applyFilters(thumbnails: List<Thumbnail>, filters: FiltersState): List<Thumbnail> {
        return thumbnails.filter { thumbnail ->
            val inDateRange = filters.dateRange?.let {
                val (from, to) = it.toDateBounds()
                !thumbnail.date.isBefore(from) && !thumbnail.date.isAfter(to)
            } ?: true
            val matchesSearch = filters.searchQuery.isEmpty() ||
                    thumbnail.content.contains(filters.searchQuery, ignoreCase = true)
            val moodMatches = thumbnail.mood in filters.moods
            inDateRange && matchesSearch && moodMatches
        }
    }

    private fun updateFilters(new: FiltersState) {
        val old = mutableFiltersState.value
        if (old.searchVisible != new.searchVisible) {
            if (new.searchVisible) bottomNavigationVisibilityManager.lockHide()
            else bottomNavigationVisibilityManager.unlockAndShow()
        }
        mutableFiltersState.update { new }
    }

    sealed interface ViewEvent {
        data object OnHideBottomBar : ViewEvent
        data object OnShowBottomBar : ViewEvent
        data object OnFetchNextPage : ViewEvent
        data class OnImageClick(val id: Int) : ViewEvent
        data class OnFiltersChanged(val filters: FiltersState) : ViewEvent
        data object OnResetFilters : ViewEvent
    }

    sealed interface ViewAction {
        data class NavigateToImage(val id: Int) : ViewAction
    }
}
