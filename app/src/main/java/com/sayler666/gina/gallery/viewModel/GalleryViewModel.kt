package com.sayler666.gina.gallery.viewModel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.sayler666.gina.attachments.viewmodel.AttachmentEntity
import com.sayler666.gina.attachments.viewmodel.AttachmentMapper
import com.sayler666.gina.db.GinaDatabaseProvider
import com.sayler666.gina.gallery.usecase.ImageAttachmentsRepository
import com.sayler666.gina.gallery.viewModel.GalleryViewModel.ViewEvent.OnHideBottomBar
import com.sayler666.gina.gallery.viewModel.GalleryViewModel.ViewEvent.OnShowBottomBar
import com.sayler666.gina.ginaApp.navigation.BottomNavigationVisibilityManager
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.drop
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class GalleryViewModel @Inject constructor(
    private val ginaDatabaseProvider: GinaDatabaseProvider,
    private val imageAttachmentsRepository: ImageAttachmentsRepository,
    private val galleryMapper: GalleryMapper,
    private val attachmentMapper: AttachmentMapper,
    private val bottomNavigationVisibilityManager: BottomNavigationVisibilityManager,
) : ViewModel() {

    private val _state = MutableStateFlow<GalleryState>(GalleryState.LoadingState)
    val state = _state

    private val _openImage = MutableSharedFlow<AttachmentEntity>()
    val openImage = _openImage.asSharedFlow()

    init {
        initDb()
        observerImages()
    }

    private fun initDb() {
        viewModelScope.launch { ginaDatabaseProvider.openSavedDB() }
    }

    private fun observerImages() {
        viewModelScope.launch {
            imageAttachmentsRepository.fetchNextPage()
            imageAttachmentsRepository
                .attachment
                .drop(1)
                .map(galleryMapper::toGalleryState)
                .collect(_state::tryEmit)
        }
    }

    fun fetchNextPage() {
        imageAttachmentsRepository.fetchNextPage()
    }

    fun fetchFullImage(id: Int) {
        viewModelScope.launch {
            imageAttachmentsRepository.fetchFullImage(id)
                .map(attachmentMapper::mapToAttachmentEntity)
                .onSuccess { _openImage.emit(it) }
        }
    }

    fun onViewEvent(event: ViewEvent) {
        when (event) {
            OnHideBottomBar -> bottomNavigationVisibilityManager.hide()
            OnShowBottomBar -> bottomNavigationVisibilityManager.show()
        }
    }

    sealed interface ViewEvent {
        // TODO add rest of the events
        data object OnHideBottomBar : ViewEvent
        data object OnShowBottomBar : ViewEvent
    }
}
