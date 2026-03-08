package com.sayler666.gina.dayDetailsEdit.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.sayler666.core.image.ImageOptimization
import com.sayler666.data.database.db.journal.GinaDatabaseProvider
import com.sayler666.domain.model.journal.Attachment
import com.sayler666.gina.dayDetails.usecase.GetDayDetailsUseCase
import com.sayler666.gina.dayDetails.viewmodel.DayDetailsEntity
import com.sayler666.gina.dayDetails.viewmodel.toEditState
import com.sayler666.gina.dayDetailsEdit.usecase.DeleteDayUseCase
import com.sayler666.gina.dayDetailsEdit.usecase.EditDayUseCase
import com.sayler666.gina.feature.settings.viewmodel.ImageOptimizationViewModel
import com.sayler666.gina.friends.viewmodel.FriendsMapper
import com.sayler666.gina.workinCopy.WorkingCopyStorage
import dagger.assisted.Assisted
import dagger.assisted.AssistedFactory
import dagger.assisted.AssistedInject
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted.Companion.WhileSubscribed
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.filterNotNull
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

@HiltViewModel(assistedFactory = DayDetailsEditViewModel.Factory::class)
class DayDetailsEditViewModel @AssistedInject constructor(
    @Assisted val dayId: Int,
    getDayDetailsUseCase: GetDayDetailsUseCase,
    private val ginaDatabaseProvider: GinaDatabaseProvider,
    private val friendsMapper: FriendsMapper,
    private val editDayUseCase: EditDayUseCase,
    private val deleteDayUseCase: DeleteDayUseCase,
    private val imageOptimization: ImageOptimization,
    private val imageOptimizationViewModel: ImageOptimizationViewModel,
    private val workingCopyStorage: WorkingCopyStorage,
    private val dayEditingSlice: DayEditingViewModelSlice,
) : ViewModel(), ImageOptimizationViewModel by imageOptimizationViewModel,
    DayEditingViewModelSlice by dayEditingSlice {

    init {
        with(imageOptimizationViewModel) { initialize() }
        dayEditingSlice.initializeSlice(viewModelScope)
        viewModelScope.launch { ginaDatabaseProvider.openSavedDB() }
    }

    @AssistedFactory
    interface Factory {
        fun create(dayId: Int): DayDetailsEditViewModel
    }

    private val _attachmentsToDelete: MutableStateFlow<MutableList<Attachment>> =
        MutableStateFlow(mutableListOf())

    val tempDay: StateFlow<DayDetailsEntity?> = combine(
        mutableDay,
        allFriends,
        friendsSearchQuery
    ) { day, allFriends, friendsSearchQuery ->
        day?.toEditState(friendsMapper, allFriends, friendsSearchQuery)
    }
        .filterNotNull()
        .stateIn(viewModelScope, WhileSubscribed(500), null)

    val day = combine(
        getDayDetailsUseCase.getDayDetailsFlow(dayId),
        allFriends,
        friendsSearchQuery
    ) { day, allFriends, friendsSearchQuery ->
        if (mutableDay.value == null) mutableDay.value = day
        day?.toEditState(friendsMapper, allFriends, friendsSearchQuery)
    }
        .filterNotNull()
        .stateIn(viewModelScope, WhileSubscribed(500), null)

    val changesExist: StateFlow<Boolean> = tempDay.map {
        if (it != null && day.value != null) it != day.value else false
    }.stateIn(viewModelScope, WhileSubscribed(500), false)

    private val _workingCopy: MutableStateFlow<String> = MutableStateFlow("")
    val hasWorkingCopy = workingCopyStorage.getTextContent().map {
        it?.let { _workingCopy.emit(it) }
        !it.isNullOrEmpty()
    }

    private val _reinitializeText = MutableSharedFlow<Unit>()
    val reinitializeText = _reinitializeText.asSharedFlow()

    private val _navigateBack = MutableSharedFlow<Unit>()
    val navigateBack = _navigateBack.asSharedFlow()

    private val _navigateToList = MutableSharedFlow<Unit>()
    val navigateToList = _navigateToList.asSharedFlow()

    fun setNewContent(newContent: String) {
        val temp = mutableDay.value ?: return
        mutableDay.value = temp.copy(day = temp.day.copy(content = newContent))

        // create "Working Copy" with WorkingCopyStorage
        if (newContent.isNotBlank() && changesExist.value) {
            viewModelScope.launch {
                workingCopyStorage.store(newContent)
            }
        }
    }

    // override to also track attachments that need DB deletion
    override fun removeAttachment(byteHashCode: Int) {
        mutableDay.value?.attachments
            ?.firstOrNull { it.content.hashCode() == byteHashCode && it.dayId != null }
            ?.let { _attachmentsToDelete.value.add(it) }
        dayEditingSlice.removeAttachment(byteHashCode)
    }

    fun saveChanges() {
        mutableDay.value?.let {
            viewModelScope.launch {
                editDayUseCase.updateDay(it, attachmentsToDelete = _attachmentsToDelete.value)
                workingCopyStorage.clear()
                _navigateBack.emit(Unit)
            }
        }
    }

    fun removeDay() {
        mutableDay.value?.let {
            viewModelScope.launch {
                deleteDayUseCase.deleteDay(it)
                _navigateToList.emit(Unit)
            }
        }
    }

    fun optimizeAttachment(attachmentHash: Int) {
        val currentDay = mutableDay.value ?: return
        val toOptimize = currentDay.attachments.first { it.content.hashCode() == attachmentHash }
        val newAttachments = currentDay.attachments
            .toMutableList()
            .also { attachments ->
                attachments.removeIf {
                    val same = it.content.hashCode() == attachmentHash
                    // mark for deletion only if attachment already stored in DB (has nonnull dayId)
                    if (it.dayId != null && same) _attachmentsToDelete.value.add(it)
                    return@removeIf same
                }
            }
        viewModelScope.launch {
            val bytes = imageOptimization.optimizeImage(toOptimize.content)
            val newAttachment = Attachment(
                dayId = null,
                content = bytes,
                mimeType = toOptimize.mimeType,
                id = null
            )
            mutableDay.update {
                it?.copy(attachments = newAttachments + newAttachment)
            }
        }
    }

    fun restoreWorkingCopy() {
        val temp = mutableDay.value ?: return
        if (_workingCopy.value.isNotEmpty()) {
            mutableDay.value = temp.copy(day = temp.day.copy(content = _workingCopy.value))
            viewModelScope.launch {
                _reinitializeText.emit(Unit)
            }
        }
    }
}
