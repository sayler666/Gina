package com.sayler666.gina.day.dayDetailsEdit.usecase

import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.flow.asSharedFlow
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class TmpAttachmentHiddenStore @Inject constructor() {
    private val _updates = MutableSharedFlow<Pair<Int, Boolean>>(extraBufferCapacity = 8)
    val updates: SharedFlow<Pair<Int, Boolean>> = _updates.asSharedFlow()

    fun update(contentHashCode: Int, hidden: Boolean) {
        _updates.tryEmit(contentHashCode to hidden)
    }
}
