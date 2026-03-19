package com.sayler666.gina.reminders.usecase

import com.sayler666.data.database.db.journal.dao.DaysDao
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import java.time.LocalDate
import javax.inject.Inject

interface TodayEntryExistUseCase {
    suspend operator fun invoke(): Boolean
}

class TodayEntryExistUseCaseImpl @Inject constructor(
    private val daysDao: DaysDao,
    private val dispatcher: CoroutineDispatcher = Dispatchers.IO,
) : TodayEntryExistUseCase {

    override suspend fun invoke(): Boolean = withContext(dispatcher) {
        try {
            daysDao.getLastDay().day.date == LocalDate.now()
        } catch (e: Exception) { false }
    }
}
