package com.sayler666.gina.daysList.usecase

import android.database.SQLException
import com.sayler666.gina.db.DatabaseProvider
import com.sayler666.gina.db.Day
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.emitAll
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.flowOn
import timber.log.Timber
import javax.inject.Inject

interface GetDaysUseCase {
    fun getDaysFlow(searchQuery: String? = null): Flow<List<Day>>
}

class GetDaysUseCaseImpl @Inject constructor(
    private val databaseProvider: DatabaseProvider,
    private val dispatcher: CoroutineDispatcher = Dispatchers.IO,
) : GetDaysUseCase {
    override fun getDaysFlow(searchQuery: String?): Flow<List<Day>> = flow {
        try {
            databaseProvider.getOpenedDb()?.let {
                if (searchQuery == null) {
                    emitAll(it.daysDao().getDaysFlow())
                } else {
                    emitAll(it.daysDao().getDaysFlow(searchQuery = searchQuery))
                }
            }
        } catch (e: SQLException) {
            Timber.e(e, "Database error")
        }
    }.flowOn(dispatcher)
}
