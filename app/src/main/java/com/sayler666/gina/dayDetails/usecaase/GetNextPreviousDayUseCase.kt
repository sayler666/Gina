package com.sayler666.gina.dayDetails.usecaase

import android.database.SQLException
import com.sayler666.gina.db.GinaDatabaseProvider
import com.sayler666.gina.db.returnWithDaysDao
import timber.log.Timber
import javax.inject.Inject

interface GetNextPreviousDayUseCase {
    suspend fun getNextDayAfterDate(date: Long): Result<Int>
    suspend fun getPreviousDayBeforeDate(date: Long): Result<Int>
}

class GetNextPreviousDayUseCaseUseCaseImpl @Inject constructor(
    private val ginaDatabaseProvider: GinaDatabaseProvider
) : GetNextPreviousDayUseCase {
    override suspend fun getNextDayAfterDate(date: Long): Result<Int> = try {
        val dayId: Int? = ginaDatabaseProvider.returnWithDaysDao {
            getNextDayIdAfter(date)
        }
        dayId?.let { Result.success(it) }
            ?: Result.failure(NoSuchElementException("No next day found"))
    } catch (e: SQLException) {
        Timber.e(e, "Database error")
        Result.failure(e)
    }

    override suspend fun getPreviousDayBeforeDate(date: Long): Result<Int> = try {
        val dayId: Int? = ginaDatabaseProvider.returnWithDaysDao {
            getPreviousDayIdBefore(date)
        }
        dayId?.let { Result.success(it) }
            ?: Result.failure(NoSuchElementException("No previous day found"))

    } catch (e: SQLException) {
        Timber.e(e, "Database error")
        Result.failure(e)
    }
}
