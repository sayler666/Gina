package com.sayler666.gina.friends.usecase

import android.database.SQLException
import com.sayler666.gina.db.GinaDatabaseProvider
import com.sayler666.gina.db.entity.FriendWithCount
import com.sayler666.gina.db.returnWithDaysDao
import com.sayler666.gina.db.withDaysDao
import com.sayler666.gina.mood.Mood
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.emitAll
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.flowOn
import timber.log.Timber
import java.time.LocalDate
import javax.inject.Inject

interface GetAllFriendsUseCase {
    fun getAllFriendsWithCount(): Flow<List<FriendWithCount>>
    suspend fun getAllFriendsWithCount(
        searchQuery: String,
        moods: List<Mood>,
        dateFrom: LocalDate,
        dateTo: LocalDate
    ): List<FriendWithCount>
}

class GetAllFriendsUseCaseImpl @Inject constructor(
    private val ginaDatabaseProvider: GinaDatabaseProvider,
    private val dispatcher: CoroutineDispatcher = Dispatchers.IO,
) : GetAllFriendsUseCase {
    override fun getAllFriendsWithCount(): Flow<List<FriendWithCount>> = flow {
        try {
            ginaDatabaseProvider.withDaysDao {
                emitAll(getFriendsWithCountFlow())
            }
        } catch (e: SQLException) {
            Timber.e(e, "Database error")
        }
    }.flowOn(dispatcher)

    override suspend fun getAllFriendsWithCount(
        searchQuery: String,
        moods: List<Mood>,
        dateFrom: LocalDate,
        dateTo: LocalDate
    ): List<FriendWithCount> = try {
        ginaDatabaseProvider.returnWithDaysDao {
            getFriendsWithCount(searchQuery, dateFrom, dateTo, *moods.toTypedArray())
        } ?: emptyList()
    } catch (e: SQLException) {
        Timber.e(e, "Database error")
        emptyList()
    }
}
