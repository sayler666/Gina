package com.sayler666.gina.dayDetails.usecaase

import android.database.SQLException
import com.sayler666.gina.db.GinaDatabaseProvider
import com.sayler666.gina.db.entity.DayDetails
import com.sayler666.gina.db.withDaysDao
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.emitAll
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.flowOn
import timber.log.Timber
import javax.inject.Inject

interface GetDayDetailsUseCase {
    fun getDayDetails(id: Int): Flow<DayDetails?>
}

class GetDayDetailsUseCaseImpl @Inject constructor(
    private val ginaDatabaseProvider: GinaDatabaseProvider,
    private val dispatcher: CoroutineDispatcher = Dispatchers.IO,
) : GetDayDetailsUseCase {
    override fun getDayDetails(id: Int): Flow<DayDetails?> = flow {
        try {
            ginaDatabaseProvider.withDaysDao {
                emitAll(getDayFlow(id))
            }
        } catch (e: SQLException) {
            Timber.e(e, "Database error")
        }
    }.flowOn(dispatcher)
}
