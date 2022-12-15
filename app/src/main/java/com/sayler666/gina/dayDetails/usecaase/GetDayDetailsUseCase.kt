package com.sayler666.gina.dayDetails.usecaase

import android.database.SQLException
import com.sayler666.gina.db.DatabaseProvider
import com.sayler666.gina.db.Day
import com.sayler666.gina.db.DayWithAttachment
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.emitAll
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.flowOn
import timber.log.Timber
import javax.inject.Inject

interface GetDayDetailsUseCase {
    fun getDayDetails(id: Int): Flow<DayWithAttachment>
}

class GetDayDetailsUseCaseImpl @Inject constructor(
    private val databaseProvider: DatabaseProvider,
    private val dispatcher: CoroutineDispatcher = Dispatchers.IO,
) : GetDayDetailsUseCase {
    override fun getDayDetails(id: Int): Flow<DayWithAttachment> = flow {
        try {
            databaseProvider.getOpenedDb()?.let {
                emitAll(it.daysDao().getDayFlow(id))
            }
        } catch (e: SQLException) {
            Timber.e(e, "Database error")
        }
    }.flowOn(dispatcher)
}
