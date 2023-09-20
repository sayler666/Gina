package com.sayler666.gina.attachments.viewmodel

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.sayler666.gina.attachments.ui.ImagePreviewScreenNavArgs
import com.sayler666.gina.attachments.ui.ImagePreviewTmpScreenNavArgs
import com.sayler666.gina.attachments.usecase.GetAttachmentWithDayUseCase
import com.sayler666.gina.db.GinaDatabaseProvider
import com.sayler666.gina.destinations.ImagePreviewScreenDestination
import com.sayler666.gina.destinations.ImagePreviewTmpScreenDestination
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.SharingStarted.Companion.WhileSubscribed
import kotlinx.coroutines.flow.filterNotNull
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class ImagePreviewViewModel @Inject constructor(
    private val ginaDatabaseProvider: GinaDatabaseProvider,
    getAttachmentWithDayUseCase: GetAttachmentWithDayUseCase,
    private val imagePreviewMapper: ImagePreviewMapper,
    savedStateHandle: SavedStateHandle
) : ViewModel() {

    init {
        viewModelScope.launch { ginaDatabaseProvider.openSavedDB() }
    }

    private val navArgs: ImagePreviewScreenNavArgs =
        ImagePreviewScreenDestination.argsFrom(savedStateHandle)
    private val id: Int
        get() = navArgs.attachmentId
    val allowNavigationToDayDetails: Boolean
        get() = navArgs.allowNavigationToDayDetails

    val attachmentWithDay = getAttachmentWithDayUseCase.getAttachmentWithDay(id)
        .filterNotNull()
        .map(imagePreviewMapper::mapToVm)
        .stateIn(viewModelScope, WhileSubscribed(500), null)
}

@HiltViewModel
class ImagePreviewTmpViewModel @Inject constructor(
    private val mapper: ImagePreviewTmpMapper,
    savedStateHandle: SavedStateHandle
) : ViewModel() {

    private val navArgs: ImagePreviewTmpScreenNavArgs =
        ImagePreviewTmpScreenDestination.argsFrom(savedStateHandle)

    private val image: ByteArray
        get() = navArgs.image
    private val mimeType: String
        get() = navArgs.mimeType

    val imagePreview = flow {
        emit(mapper.mapToVm(image, mimeType))
    }.stateIn(viewModelScope, WhileSubscribed(5000), null)
}


