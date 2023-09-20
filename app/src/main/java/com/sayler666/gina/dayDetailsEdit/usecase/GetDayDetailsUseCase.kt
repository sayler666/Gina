package com.sayler666.gina.dayDetailsEdit.usecase

import android.database.SQLException
import com.sayler666.gina.db.Friend
import com.sayler666.gina.db.GinaDatabaseProvider
import com.sayler666.gina.db.withDaysDao
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.emitAll
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.flowOn
import timber.log.Timber
import javax.inject.Inject

interface GetFriendUseCase {
    fun getFriend(id: Int): Flow<Friend?>
}

class GetFriendUseCaseImpl @Inject constructor(
    private val ginaDatabaseProvider: GinaDatabaseProvider,
    private val dispatcher: CoroutineDispatcher = Dispatchers.IO,
) : GetFriendUseCase {
    override fun getFriend(id: Int): Flow<Friend?> = flow {
        try {
            ginaDatabaseProvider.withDaysDao {
                emitAll(getFriendFlow(id))
            }
        } catch (e: SQLException) {
            Timber.e(e, "Database error")
        }
    }.flowOn(dispatcher)
}
