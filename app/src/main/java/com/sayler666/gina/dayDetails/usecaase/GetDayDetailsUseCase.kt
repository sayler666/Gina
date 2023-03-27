package com.sayler666.gina.dayDetails.usecaase

import android.database.SQLException
import com.sayler666.gina.db.DatabaseProvider
import com.sayler666.gina.db.DayDetails
import com.sayler666.gina.db.returnWithDaysDao
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
    suspend fun getNextDayAfterDate(date: Long): Int?
    suspend fun getPreviousDayBeforeDate(date: Long): Int?
}

class GetDayDetailsUseCaseImpl @Inject constructor(
    private val databaseProvider: DatabaseProvider,
    private val dispatcher: CoroutineDispatcher = Dispatchers.IO,
) : GetDayDetailsUseCase {
    override fun getDayDetails(id: Int): Flow<DayDetails?> = flow {
        try {
            databaseProvider.withDaysDao {
                emitAll(getDayFlow(id))
            }
        } catch (e: SQLException) {
            Timber.e(e, "Database error")
        }
    }.flowOn(dispatcher)

    override suspend fun getNextDayAfterDate(date: Long): Int? {
        return try {
            databaseProvider.returnWithDaysDao {
                getNextDayIdAfter(date)
            }
        } catch (e: SQLException) {
            Timber.e(e, "Database error")
            null
        }
    }

    override suspend fun getPreviousDayBeforeDate(date: Long): Int? {
        return try {
            databaseProvider.returnWithDaysDao {
                getPreviousDayIdBefore(date)
            }
        } catch (e: SQLException) {
            Timber.e(e, "Database error")
            null
        }
    }
}
