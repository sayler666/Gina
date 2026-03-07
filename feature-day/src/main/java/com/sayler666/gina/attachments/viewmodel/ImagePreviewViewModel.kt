package com.sayler666.gina.attachments.viewmodel

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.sayler666.data.database.db.journal.GinaDatabaseProvider
import com.sayler666.gina.attachments.usecase.GetAttachmentWithDayUseCase
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

    private val id: Int = savedStateHandle.get<Int>("attachmentId") ?: error("Missing attachmentId arg")
    val allowNavigationToDayDetails: Boolean =
        savedStateHandle.get<Boolean>("allowNavigationToDayDetails") ?: true

    val attachmentWithDay = getAttachmentWithDayUseCase.getAttachmentWithDayFlow(id)
        .filterNotNull()
        .map(imagePreviewMapper::mapToVm)
        .stateIn(viewModelScope, WhileSubscribed(500), null)
}

@HiltViewModel
class ImagePreviewTmpViewModel @Inject constructor(
    private val mapper: ImagePreviewTmpMapper,
    savedStateHandle: SavedStateHandle
) : ViewModel() {

    private val image: ByteArray = savedStateHandle.get<ByteArray>("image") ?: error("Missing image arg")
    private val mimeType: String = savedStateHandle.get<String>("mimeType") ?: error("Missing mimeType arg")

    val imagePreview = flow {
        emit(mapper.mapToVm(image, mimeType))
    }.stateIn(viewModelScope, WhileSubscribed(5000), null)
}
