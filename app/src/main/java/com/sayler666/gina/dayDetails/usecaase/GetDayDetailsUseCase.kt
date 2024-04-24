package com.sayler666.gina.dayDetails.usecaase

import android.database.SQLException
import com.sayler666.gina.db.GinaDatabaseProvider
import com.sayler666.gina.db.entity.DayDetails
import com.sayler666.gina.db.returnWithDaysDao
import com.sayler666.gina.db.withDaysDao
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.emitAll
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.flowOn
import kotlinx.coroutines.withContext
import timber.log.Timber
import javax.inject.Inject

interface GetDayDetailsUseCase {
    fun getDayDetailsFlow(id: Int): Flow<DayDetails?>
    suspend fun getDayDetails(id: Int): Result<DayDetails>
}

class GetDayDetailsUseCaseImpl @Inject constructor(
    private val ginaDatabaseProvider: GinaDatabaseProvider,
    private val dispatcher: CoroutineDispatcher = Dispatchers.IO,
) : GetDayDetailsUseCase {
    override fun getDayDetailsFlow(id: Int): Flow<DayDetails?> = flow {
        try {
            ginaDatabaseProvider.withDaysDao {
                emitAll(getDayFlow(id))
            }
        } catch (e: SQLException) {
            Timber.e(e, "Database error")
        }
    }.flowOn(dispatcher)

    override suspend fun getDayDetails(id: Int): Result<DayDetails> = withContext(dispatcher) {
        runCatching {
            ginaDatabaseProvider.returnWithDaysDao {
                getDay(id)
            } ?: throw IllegalStateException("No day with id: $id")
        }
    }
}
