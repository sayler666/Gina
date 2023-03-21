package com.sayler666.gina.dayDetailsEdit.usecase

import android.database.SQLException
import com.sayler666.gina.db.DatabaseProvider
import com.sayler666.gina.db.Friend
import com.sayler666.gina.db.withDaysDao
import timber.log.Timber
import javax.inject.Inject

interface EditFriendUseCase {
    suspend fun editFriend(friend: Friend)
}

class EditFriendUseCaseImpl @Inject constructor(
    private val databaseProvider: DatabaseProvider
) : EditFriendUseCase {
    override suspend fun editFriend(friend: Friend) {
        try {
            databaseProvider.withDaysDao {
                updateFriend(friend)
            }
        } catch (e: SQLException) {
            Timber.e(e, "Database error")
        }
    }
}
