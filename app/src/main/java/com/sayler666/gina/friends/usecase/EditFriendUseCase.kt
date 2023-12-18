package com.sayler666.gina.friends.usecase

import android.database.SQLException
import com.sayler666.gina.db.GinaDatabaseProvider
import com.sayler666.gina.db.entity.Friend
import com.sayler666.gina.db.withDaysDao
import timber.log.Timber
import javax.inject.Inject

interface EditFriendUseCase {
    suspend fun editFriend(friend: Friend)
}

class EditFriendUseCaseImpl @Inject constructor(
    private val ginaDatabaseProvider: GinaDatabaseProvider
) : EditFriendUseCase {
    override suspend fun editFriend(friend: Friend) {
        try {
            ginaDatabaseProvider.withDaysDao {
                updateFriend(friend)
            }
        } catch (e: SQLException) {
            Timber.e(e, "Database error")
        }
    }
}
