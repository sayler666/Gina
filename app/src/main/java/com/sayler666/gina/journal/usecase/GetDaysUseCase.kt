package com.sayler666.gina.journal.usecase

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
    fun getDaysFlow(): Flow<List<Day>>
    fun getDaysFlow(searchQuery: String? = null): Flow<List<Day>>
}

class GetDaysUseCaseImpl @Inject constructor(
    private val databaseProvider: DatabaseProvider,
    private val dispatcher: CoroutineDispatcher = Dispatchers.IO,
) : GetDaysUseCase {
    override fun getDaysFlow(): Flow<List<Day>> = flow {
        try {
            databaseProvider.getOpenedDb()?.let {
                emitAll(it.daysDao().getDaysFlow())
            }
        } catch (e: SQLException) {
            Timber.e(e, "Database error")
        }
    }.flowOn(dispatcher)

    override fun getDaysFlow(searchQuery: String?): Flow<List<Day>> = flow {
        try {
            databaseProvider.getOpenedDb()?.let {
                emitAll(it.daysDao().getDaysFlow(searchQuery = searchQuery))
            }
        } catch (e: SQLException) {
            Timber.e(e, "Database error")
        }
    }.flowOn(dispatcher)
}
