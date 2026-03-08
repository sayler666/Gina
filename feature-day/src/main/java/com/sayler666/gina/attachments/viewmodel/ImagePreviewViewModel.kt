package com.sayler666.gina.attachments.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.sayler666.data.database.db.journal.GinaDatabaseProvider
import com.sayler666.gina.attachments.usecase.GetAttachmentWithDayUseCase
import dagger.assisted.Assisted
import dagger.assisted.AssistedFactory
import dagger.assisted.AssistedInject
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.SharingStarted.Companion.WhileSubscribed
import kotlinx.coroutines.flow.filterNotNull
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

@HiltViewModel(assistedFactory = ImagePreviewViewModel.Factory::class)
class ImagePreviewViewModel @AssistedInject constructor(
    @Assisted val attachmentId: Int,
    @Assisted val allowNavigationToDayDetails: Boolean,
    private val ginaDatabaseProvider: GinaDatabaseProvider,
    getAttachmentWithDayUseCase: GetAttachmentWithDayUseCase,
    private val imagePreviewMapper: ImagePreviewMapper,
) : ViewModel() {

    @AssistedFactory
    interface Factory {
        fun create(attachmentId: Int, allowNavigationToDayDetails: Boolean): ImagePreviewViewModel
    }

    init {
        viewModelScope.launch { ginaDatabaseProvider.openSavedDB() }
    }

    val attachmentWithDay = getAttachmentWithDayUseCase.getAttachmentWithDayFlow(attachmentId)
        .filterNotNull()
        .map(imagePreviewMapper::mapToVm)
        .stateIn(viewModelScope, WhileSubscribed(500), null)
}

@HiltViewModel(assistedFactory = ImagePreviewTmpViewModel.Factory::class)
class ImagePreviewTmpViewModel @AssistedInject constructor(
    @Assisted val image: ByteArray,
    @Assisted val mimeType: String,
    private val mapper: ImagePreviewTmpMapper,
) : ViewModel() {

    @AssistedFactory
    interface Factory {
        fun create(image: ByteArray, mimeType: String): ImagePreviewTmpViewModel
    }

    val imagePreview = flow {
        emit(mapper.mapToVm(image, mimeType))
    }.stateIn(viewModelScope, WhileSubscribed(5000), null)
}
