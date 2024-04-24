package com.sayler666.gina.dayDetails.usecaase

import android.database.SQLException
import com.sayler666.gina.db.GinaDatabaseProvider
import com.sayler666.gina.db.returnWithDaysDao
import timber.log.Timber
import javax.inject.Inject

interface GetNextPreviousDayUseCase {
    suspend fun getNextDay(dayId: Int): Result<Int>
    suspend fun getPreviousDay(dayId: Int): Result<Int>
}

class GetNextPreviousDayUseCaseUseCaseImpl @Inject constructor(
    private val ginaDatabaseProvider: GinaDatabaseProvider
) : GetNextPreviousDayUseCase {
    override suspend fun getNextDay(dayId: Int): Result<Int> = try {
        val currentDay = ginaDatabaseProvider.returnWithDaysDao {
            getDay(dayId)
        }
        val nextDayId: Int? = ginaDatabaseProvider.returnWithDaysDao {
            if (currentDay?.day?.id != null && currentDay.day.date != null)
                getNextDayIdAfter(currentDay.day.date, currentDay.day.id) else null
        }
        nextDayId?.let { Result.success(it) }
            ?: Result.failure(NoSuchElementException("No next day found"))
    } catch (e: SQLException) {
        Timber.e(e, "Database error")
        Result.failure(e)
    }

    override suspend fun getPreviousDay(dayId: Int): Result<Int> = try {
        val currentDay = ginaDatabaseProvider.returnWithDaysDao {
            getDay(dayId)
        }
        val previousDayId: Int? = ginaDatabaseProvider.returnWithDaysDao {
            if (currentDay?.day?.id != null && currentDay.day.date != null)
                getPreviousDayIdBefore(currentDay.day.date, currentDay.day.id) else null
        }
        previousDayId?.let { Result.success(it) }
            ?: Result.failure(NoSuchElementException("No previous day found"))

    } catch (e: SQLException) {
        Timber.e(e, "Database error")
        Result.failure(e)
    }
}
