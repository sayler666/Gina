package com.sayler666.gina.gallery.viewModel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.sayler666.gina.attachments.viewmodel.AttachmentEntity
import com.sayler666.gina.attachments.viewmodel.AttachmentMapper
import com.sayler666.gina.db.DatabaseProvider
import com.sayler666.gina.gallery.usecase.ImageAttachmentsRepository
import com.sayler666.gina.gallery.viewModel.GalleryState.EmptyState
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.drop
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.flow.skip
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class GalleryViewModel @Inject constructor(
    private val databaseProvider: DatabaseProvider,
    private val imageAttachmentsRepository: ImageAttachmentsRepository,
    private val galleryMapper: GalleryMapper,
    private val attachmentMapper: AttachmentMapper
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
        viewModelScope.launch { databaseProvider.openSavedDB() }
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
}
