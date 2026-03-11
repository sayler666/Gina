package com.sayler666.gina.day.attachments.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.sayler666.core.image.ScaledBitmapInfo
import com.sayler666.core.image.scaleToMinSize
import com.sayler666.data.database.db.journal.GinaDatabaseProvider
import com.sayler666.gina.day.attachments.usecase.GetAttachmentIdsBySourceUseCase
import com.sayler666.gina.day.attachments.usecase.GetAttachmentWithDayUseCase
import com.sayler666.gina.navigation.routes.ImagePreviewSource
import dagger.assisted.Assisted
import dagger.assisted.AssistedFactory
import dagger.assisted.AssistedInject
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted.Companion.WhileSubscribed
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

@HiltViewModel(assistedFactory = ImagePreviewViewModel.Factory::class)
class ImagePreviewViewModel @AssistedInject constructor(
    @Assisted val initialAttachmentId: Int,
    @Assisted val source: ImagePreviewSource,
    private val ginaDatabaseProvider: GinaDatabaseProvider,
    private val getAttachmentWithDayUseCase: GetAttachmentWithDayUseCase,
    private val getAttachmentIdsBySourceUseCase: GetAttachmentIdsBySourceUseCase,
    private val imagePreviewMapper: ImagePreviewMapper,
) : ViewModel() {

    @AssistedFactory
    interface Factory {
        fun create(initialAttachmentId: Int, source: ImagePreviewSource): ImagePreviewViewModel
    }

    private val mutableViewState = MutableStateFlow(State())
    val viewState: StateFlow<State> = mutableViewState.asStateFlow()

    private val mutableViewActions = Channel<ViewAction>(Channel.BUFFERED)
    val viewActions = mutableViewActions.receiveAsFlow()

    private val loadingPages = mutableSetOf<Int>()

    init {
        viewModelScope.launch { ginaDatabaseProvider.openSavedDB() }
        viewModelScope.launch { loadIds() }
    }

    private suspend fun loadIds() {
        val ids = getAttachmentIdsBySourceUseCase.getIds(source)
        val initialPage = ids.indexOf(initialAttachmentId).coerceAtLeast(0)
        mutableViewState.update { it.copy(attachmentIds = ids, initialPage = initialPage) }
    }

    fun onViewEvent(event: ViewEvent) {
        when (event) {
            is ViewEvent.OnLoadPage -> loadPage(event.attachmentId)
            ViewEvent.OnBackPressed -> mutableViewActions.trySend(ViewAction.Back)
            is ViewEvent.OnNavigateToDayDetails -> mutableViewActions.trySend(ViewAction.NavToDayDetails(event.dayId))
        }
    }

    private fun loadPage(attachmentId: Int) {
        synchronized(loadingPages) {
            if (mutableViewState.value.pages.containsKey(attachmentId)) return
            if (!loadingPages.add(attachmentId)) return
        }
        viewModelScope.launch {
            try {
                val attachmentWithDay = getAttachmentWithDayUseCase
                    .getAttachmentWithDay(attachmentId) ?: return@launch
                val entity = imagePreviewMapper.mapToVm(attachmentWithDay)
                val bitmap = withContext(Dispatchers.Default) {
                    entity.attachment.content.scaleToMinSize()
                }
                mutableViewState.update { state ->
                    state.copy(pages = state.pages + (attachmentId to PageData(entity, bitmap)))
                }
            } finally {
                synchronized(loadingPages) { loadingPages.remove(attachmentId) }
            }
        }
    }

    data class PageData(
        val entity: ImagePreviewWithDayEntity,
        val bitmap: ScaledBitmapInfo,
    )

    data class State(
        val attachmentIds: List<Int> = emptyList(),
        val initialPage: Int = 0,
        val pages: Map<Int, PageData> = emptyMap(),
    )

    sealed interface ViewEvent {
        data class OnLoadPage(val attachmentId: Int) : ViewEvent
        data object OnBackPressed : ViewEvent
        data class OnNavigateToDayDetails(val dayId: Int) : ViewEvent
    }

    sealed interface ViewAction {
        data object Back : ViewAction
        data class NavToDayDetails(val dayId: Int) : ViewAction
    }
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
