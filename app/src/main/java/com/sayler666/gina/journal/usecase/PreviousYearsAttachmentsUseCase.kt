package com.sayler666.gina.journal.usecase

import android.database.SQLException
import com.sayler666.gina.db.GinaDatabaseProvider
import com.sayler666.gina.db.entity.AttachmentWithDay
import com.sayler666.gina.db.withDaysDao
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.emitAll
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.flowOn
import timber.log.Timber
import javax.inject.Inject

interface PreviousYearsAttachmentsUseCase {
    operator fun invoke(): Flow<List<AttachmentWithDay>>
}

class PreviousYearsAttachmentsUseCaseImpl @Inject constructor(
    private val ginaDatabaseProvider: GinaDatabaseProvider,
    private val dispatcher: CoroutineDispatcher = Dispatchers.IO,
) : PreviousYearsAttachmentsUseCase {
    override operator fun invoke(): Flow<List<AttachmentWithDay>> = flow {
        try {
            ginaDatabaseProvider.withDaysDao {
                emitAll(getPreviousYearsAttachments())
            }
        } catch (e: SQLException) {
            Timber.e(e, "Database error")
        }
    }.flowOn(dispatcher)

}
