package com.sayler666.gina.friends.usecase

import com.sayler666.data.database.db.journal.GinaDatabaseProvider
import com.sayler666.data.database.db.journal.entity.FriendEntity
import com.sayler666.data.database.db.journal.withDaysDao
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
            addFriend(FriendEntity(name = name, avatar = null))
        }
    }
}
