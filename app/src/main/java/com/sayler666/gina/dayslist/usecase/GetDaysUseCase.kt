package com.sayler666.gina.dayslist.usecase

import com.sayler666.gina.db.DatabaseProvider
import com.sayler666.gina.db.Days
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.emitAll
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.flowOn
import javax.inject.Inject

interface GetDaysUseCase {
    fun getDaysFlow(): Flow<List<Days>>
}

class GetDaysUseCaseImpl @Inject constructor(
    private val databaseProvider: DatabaseProvider,
    private val dispatcher: CoroutineDispatcher = Dispatchers.IO,
) : GetDaysUseCase {
    override fun getDaysFlow(): Flow<List<Days>> = flow {
        databaseProvider.getOpenedDb()?.let {
            emitAll(it.daysDao().getDaysFlow())
        }
    }.flowOn(dispatcher)
}
