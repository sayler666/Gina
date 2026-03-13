package com.sayler666.gina.day.attachments.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.sayler666.data.database.db.journal.usecase.UpdateAttachmentHiddenUseCase
import com.sayler666.gina.day.dayDetailsEdit.usecase.TmpAttachmentHiddenStore
import dagger.assisted.Assisted
import dagger.assisted.AssistedFactory
import dagger.assisted.AssistedInject
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

@HiltViewModel(assistedFactory = ImagePreviewTmpViewModel.Factory::class)
class ImagePreviewTmpViewModel @AssistedInject constructor(
    @Assisted val image: ByteArray,
    @Assisted val mimeType: String,
    @Assisted val attachmentId: Int?,
    @Assisted val initialHidden: Boolean,
    private val mapper: ImagePreviewTmpMapper,
    private val updateAttachmentHiddenUseCase: UpdateAttachmentHiddenUseCase,
    private val tmpAttachmentHiddenStore: TmpAttachmentHiddenStore,
) : ViewModel() {

    @AssistedFactory
    interface Factory {
        fun create(
            image: ByteArray,
            mimeType: String,
            attachmentId: Int?,
            initialHidden: Boolean,
        ): ImagePreviewTmpViewModel
    }

    private val mutableHidden = MutableStateFlow(initialHidden)
    val hidden: StateFlow<Boolean> = mutableHidden.asStateFlow()

    val imagePreview = flow {
        emit(mapper.mapToVm(image, mimeType))
    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), null)

    fun onViewEvent(event: ViewEvent) {
        when (event) {
            is ViewEvent.OnToggleHidden -> toggleHidden(event.hidden)
        }
    }

    private fun toggleHidden(hidden: Boolean) {
        mutableHidden.value = hidden
        if (attachmentId != null) {
            viewModelScope.launch { updateAttachmentHiddenUseCase(attachmentId, hidden) }
        } else {
            tmpAttachmentHiddenStore.update(image.contentHashCode(), hidden)
        }
    }

    sealed interface ViewEvent {
        data class OnToggleHidden(val hidden: Boolean) : ViewEvent
    }
}
