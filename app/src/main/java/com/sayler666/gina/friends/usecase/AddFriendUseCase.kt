package com.sayler666.gina.friends.usecase

import com.sayler666.gina.db.GinaDatabaseProvider
import com.sayler666.gina.db.entity.Friend
import com.sayler666.gina.db.withDaysDao
import javax.inject.Inject

interface AddFriendUseCase {
    suspend fun addFriend(name: String)
}

class AddFriendUseCaseImpl @Inject constructor(
    private val ginaDatabaseProvider: GinaDatabaseProvider
) : AddFriendUseCase {
    override suspend fun addFriend(name: String) {
        if (name.isBlank()) return
        ginaDatabaseProvider.withDaysDao {
            addFriend(Friend(name = name, avatar = null))
        }
    }
}
