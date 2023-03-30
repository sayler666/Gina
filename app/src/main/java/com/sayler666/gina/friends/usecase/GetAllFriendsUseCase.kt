package com.sayler666.gina.friends.usecase

import android.database.SQLException
import com.sayler666.gina.db.DatabaseProvider
import com.sayler666.gina.db.FriendWithCount
import com.sayler666.gina.db.withDaysDao
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.emitAll
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.flowOn
import timber.log.Timber
import javax.inject.Inject

interface GetAllFriendsUseCase {
    fun getAllFriendsWithCount(): Flow<List<FriendWithCount>>
}

class GetAllFriendsUseCaseImpl @Inject constructor(
    private val databaseProvider: DatabaseProvider,
    private val dispatcher: CoroutineDispatcher = Dispatchers.IO,
) : GetAllFriendsUseCase {
    override fun getAllFriendsWithCount(): Flow<List<FriendWithCount>> = flow {
        try {
            databaseProvider.withDaysDao {
                emitAll(getFriendsWithCountFlow())
            }
        } catch (e: SQLException) {
            Timber.e(e, "Database error")
        }
    }.flowOn(dispatcher)
}
