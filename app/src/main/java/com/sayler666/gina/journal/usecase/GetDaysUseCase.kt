package com.sayler666.gina.journal.usecase

import android.database.SQLException
import com.sayler666.gina.db.Day
import com.sayler666.gina.db.GinaDatabaseProvider
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.emitAll
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.flowOn
import com.sayler666.gina.mood.Mood
import timber.log.Timber
import javax.inject.Inject

interface GetDaysUseCase {
    fun getAllDaysFlow(): Flow<List<Day>>
    fun getFilteredDaysFlow(searchQuery: String = "", moods: List<Mood>): Flow<List<Day>>
}

class GetDaysUseCaseImpl @Inject constructor(
    private val ginaDatabaseProvider: GinaDatabaseProvider,
    private val dispatcher: CoroutineDispatcher = Dispatchers.IO,
) : GetDaysUseCase {
    override fun getAllDaysFlow(): Flow<List<Day>> = flow {
        try {
            ginaDatabaseProvider.getOpenedDb()?.let {
                emitAll(it.daysDao().getDaysFlow())
            }
        } catch (e: SQLException) {
            Timber.e(e, "Database error")
        }
    }.flowOn(dispatcher)

    override fun getFilteredDaysFlow(searchQuery: String, moods: List<Mood>): Flow<List<Day>> =
        flow {
            try {
                ginaDatabaseProvider.getOpenedDb()?.let {
                    emitAll(
                        it.daysDao().getDaysWithFiltersFlow(searchQuery, *moods.toTypedArray())
                    )
                }
            } catch (e: SQLException) {
                Timber.e(e, "Database error")
            }
        }.flowOn(dispatcher)
}
