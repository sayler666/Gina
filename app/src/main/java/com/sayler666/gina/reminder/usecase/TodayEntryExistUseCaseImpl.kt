package com.sayler666.gina.reminder.usecase

import com.sayler666.core.date.toLocalDate
import com.sayler666.gina.db.GinaDatabaseProvider
import com.sayler666.gina.db.returnWithDaysDao
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import timber.log.Timber
import java.sql.SQLException
import java.time.LocalDate
import javax.inject.Inject

interface TodayEntryExistUseCase {
    suspend operator fun invoke(): Boolean
}

class TodayEntryExistUseCaseImpl @Inject constructor(
    private val ginaDatabaseProvider: GinaDatabaseProvider,
    private val dispatcher: CoroutineDispatcher = Dispatchers.IO,
) : TodayEntryExistUseCase {

    override suspend fun invoke(): Boolean = withContext(dispatcher) {
        try {
            return@withContext ginaDatabaseProvider.returnWithDaysDao {
                val lastDay = getLastDay()
                val lastDate = lastDay.day.date?.toLocalDate()?.atStartOfDay()
                val todayDate = LocalDate.now().atStartOfDay()
                Timber.d("TodayEntryExistUseCaseImpl: ${lastDate == todayDate}")

                lastDate == todayDate
            } ?: false
        } catch (e: SQLException) {
            Timber.e(e, "Database error")
        }
        return@withContext false
    }
}
