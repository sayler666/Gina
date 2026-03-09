package com.sayler666.gina.gallery.viewModel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.sayler666.core.navigation.BottomNavigationVisibilityManager
import com.sayler666.data.database.db.journal.GinaDatabaseProvider
import com.sayler666.gina.gallery.usecase.ImageAttachmentsRepository
import com.sayler666.gina.gallery.viewModel.GalleryViewModel.ViewAction
import com.sayler666.gina.gallery.viewModel.GalleryViewModel.ViewEvent.OnFetchNextPage
import com.sayler666.gina.gallery.viewModel.GalleryViewModel.ViewEvent.OnHideBottomBar
import com.sayler666.gina.gallery.viewModel.GalleryViewModel.ViewEvent.OnImageClick
import com.sayler666.gina.gallery.viewModel.GalleryViewModel.ViewEvent.OnShowBottomBar
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.drop
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class GalleryViewModel @Inject constructor(
    private val ginaDatabaseProvider: GinaDatabaseProvider,
    private val imageAttachmentsRepository: ImageAttachmentsRepository,
    private val galleryMapper: GalleryMapper,
    private val bottomNavigationVisibilityManager: BottomNavigationVisibilityManager,
) : ViewModel() {

    private val mutableViewState = MutableStateFlow<GalleryState>(GalleryState.LoadingState)
    val viewState: StateFlow<GalleryState> = mutableViewState.asStateFlow()

    private val mutableViewActions = Channel<ViewAction>(Channel.BUFFERED)
    val viewActions = mutableViewActions.receiveAsFlow()

    init {
        initDb()
        observeImages()
    }

    private fun initDb() {
        viewModelScope.launch { ginaDatabaseProvider.openSavedDB() }
    }

    private fun observeImages() {
        imageAttachmentsRepository.fetchNextPage()
        imageAttachmentsRepository
            .attachment
            .drop(1)
            .map(galleryMapper::toGalleryState)
            .onEach { mutableViewState.value = it }
            .launchIn(viewModelScope)
    }

    fun onViewEvent(event: ViewEvent) {
        when (event) {
            OnHideBottomBar -> bottomNavigationVisibilityManager.hide()
            OnShowBottomBar -> bottomNavigationVisibilityManager.show()
            OnFetchNextPage -> imageAttachmentsRepository.fetchNextPage()
            is OnImageClick -> mutableViewActions.trySend(ViewAction.NavigateToImage(event.id))
        }
    }

    sealed interface ViewEvent {
        data object OnHideBottomBar : ViewEvent
        data object OnShowBottomBar : ViewEvent
        data object OnFetchNextPage : ViewEvent
        data class OnImageClick(val id: Int) : ViewEvent
    }

    sealed interface ViewAction {
        data class NavigateToImage(val id: Int) : ViewAction
    }
}