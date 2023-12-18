package com.sayler666.gina.dayDetails.usecaase

import android.database.SQLException
import com.sayler666.gina.db.GinaDatabaseProvider
import com.sayler666.gina.db.entity.Day
import com.sayler666.gina.db.returnWithDaysDao
import timber.log.Timber
import javax.inject.Inject

interface GetNextPreviousDayUseCase {
    suspend fun getNextDayAfterDate(day: Day): Result<Int>
    suspend fun getPreviousDayBeforeDate(day: Day): Result<Int>
}

class GetNextPreviousDayUseCaseUseCaseImpl @Inject constructor(
    private val ginaDatabaseProvider: GinaDatabaseProvider
) : GetNextPreviousDayUseCase {
    override suspend fun getNextDayAfterDate(day: Day): Result<Int> = try {
        val dayId: Int? = ginaDatabaseProvider.returnWithDaysDao {
            if (day.id != null && day.date != null)
                getNextDayIdAfter(day.date, day.id) else null
        }
        dayId?.let { Result.success(it) }
            ?: Result.failure(NoSuchElementException("No next day found"))
    } catch (e: SQLException) {
        Timber.e(e, "Database error")
        Result.failure(e)
    }

    override suspend fun getPreviousDayBeforeDate(day: Day): Result<Int> = try {
        val dayId: Int? = ginaDatabaseProvider.returnWithDaysDao {
            if (day.id != null && day.date != null)
                getPreviousDayIdBefore(day.date, day.id) else null
        }
        dayId?.let { Result.success(it) }
            ?: Result.failure(NoSuchElementException("No previous day found"))

    } catch (e: SQLException) {
        Timber.e(e, "Database error")
        Result.failure(e)
    }
}
