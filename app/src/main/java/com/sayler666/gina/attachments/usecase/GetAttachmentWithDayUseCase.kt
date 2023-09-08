package com.sayler666.gina.attachments.usecase

import android.database.SQLException
import com.sayler666.gina.db.AttachmentWithDay
import com.sayler666.gina.db.DatabaseProvider
import com.sayler666.gina.db.withDaysDao
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.flowOn
import timber.log.Timber
import javax.inject.Inject

interface GetAttachmentWithDayUseCase {
    fun getAttachmentWithDay(attachmentId: Int): Flow<AttachmentWithDay?>
}

class GetAttachmentWithDayUseCaseImpl @Inject constructor(
    private val databaseProvider: DatabaseProvider,
    private val dispatcher: CoroutineDispatcher = Dispatchers.IO,
) : GetAttachmentWithDayUseCase {
    override fun getAttachmentWithDay(attachmentId: Int): Flow<AttachmentWithDay?> = flow {
        try {
            databaseProvider.withDaysDao {
                emit(getAttachmentDay(attachmentId))
            }
        } catch (e: SQLException) {
            Timber.e(e, "Database error")
        }
    }.flowOn(dispatcher)
}
