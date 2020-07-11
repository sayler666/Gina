package com.sayler.gina3.days.domain

import com.sayler.data.dao.DayDao
import com.sayler.data.entity.Day
import com.sayler.gina3.data.DataManager
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

interface DaysUseCase {
    fun getDays(): Flow<List<Day>>
}

class DaysUseCaseImpl @Inject constructor(
    private val dataManager: DataManager
) : DaysUseCase {

    private val daysDao: DayDao by lazy { dataManager.dao { dayDao() } }

    override fun getDays(): Flow<List<Day>> = daysDao.getAll()
}