package com.sayler666.gina.dayDetails.usecaase

import android.database.SQLException
import com.sayler666.gina.db.GinaDatabaseProvider
import com.sayler666.gina.db.returnWithDaysDao
import timber.log.Timber
import java.time.LocalDate
import javax.inject.Inject

interface GetNextPreviousDayUseCase {
    suspend fun getNextDayAfterDate(localDate: LocalDate): Result<Int>
    suspend fun getPreviousDayBeforeDate(localDate: LocalDate): Result<Int>
}

class GetNextPreviousDayUseCaseUseCaseImpl @Inject constructor(
    private val ginaDatabaseProvider: GinaDatabaseProvider
) : GetNextPreviousDayUseCase {
    override suspend fun getNextDayAfterDate(localDate: LocalDate): Result<Int> = try {
        val dayId: Int? = ginaDatabaseProvider.returnWithDaysDao {
            getNextDayIdAfter(localDate)
        }
        dayId?.let { Result.success(it) }
            ?: Result.failure(NoSuchElementException("No next day found"))
    } catch (e: SQLException) {
        Timber.e(e, "Database error")
        Result.failure(e)
    }

    override suspend fun getPreviousDayBeforeDate(localDate: LocalDate): Result<Int> = try {
        val dayId: Int? = ginaDatabaseProvider.returnWithDaysDao {
            getPreviousDayIdBefore(localDate)
        }
        dayId?.let { Result.success(it) }
            ?: Result.failure(NoSuchElementException("No previous day found"))

    } catch (e: SQLException) {
        Timber.e(e, "Database error")
        Result.failure(e)
    }
}
