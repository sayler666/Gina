package com.sayler666.gina.friends.usecase

import android.database.SQLException
import com.sayler666.gina.db.GinaDatabaseProvider
import com.sayler666.gina.db.entity.Friend
import com.sayler666.gina.db.withDaysDao
import timber.log.Timber
import javax.inject.Inject

interface DeleteFriendUseCase {
    suspend fun deleteFriend(friend: Friend)
}

class DeleteFriendUseCaseImpl @Inject constructor(
    private val ginaDatabaseProvider: GinaDatabaseProvider
) : DeleteFriendUseCase {
    override suspend fun deleteFriend(friend: Friend) {
        try {
            ginaDatabaseProvider.withDaysDao {
                deleteFriend(friend)
            }
        } catch (e: SQLException) {
            Timber.e(e, "Database error")
        }
    }
}
