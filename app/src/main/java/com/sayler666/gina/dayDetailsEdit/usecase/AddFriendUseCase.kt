package com.sayler666.gina.dayDetailsEdit.usecase

import com.sayler666.gina.db.DatabaseProvider
import com.sayler666.gina.db.Friend
import com.sayler666.gina.db.withDaysDao
import javax.inject.Inject

interface AddFriendUseCase {
    suspend fun addFriend(name: String)
}

class AddFriendUseCaseImpl @Inject constructor(
    private val databaseProvider: DatabaseProvider
) : AddFriendUseCase {
    override suspend fun addFriend(name: String) {
        databaseProvider.withDaysDao {
            addFriend(Friend(name = name, avatar = null))
        }
    }
}
