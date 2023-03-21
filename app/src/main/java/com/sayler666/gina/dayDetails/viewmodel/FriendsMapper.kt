package com.sayler666.gina.dayDetails.viewmodel

import com.sayler666.gina.db.Friend
import javax.inject.Inject

class FriendsMapper @Inject constructor() {
    fun mapToFriends(
        friends: List<Friend> = emptyList(),
        friendsSearchQuery: String? = null
    ): List<FriendEntity> = friends.map(::mapToFriend)
        .sortedBy { it.name }
        .sortedBy { it.avatar == null }
        .sortedBy { !it.selected }
        .filter { friend ->
            friendsSearchQuery?.let { friend.name.contains(it, ignoreCase = true) } ?: run { true }
        }.toList()

    fun mapToFriend(
        friend: Friend
    ): FriendEntity = FriendEntity(
        id = friend.id,
        name = friend.name,
        avatar = friend.avatar,
        selected = true,
        initials = createInitials(friend)
    )

    companion object {
        fun createInitials(f: Friend): String {
            val nameParts = f.name.split(" ").filter { it.isNotEmpty() }
            val initials = when {
                nameParts.size >= 2 -> nameParts[0][0].toString() + nameParts[1][0].toString()
                else -> f.name[0].toString()
            }
            return initials
        }
    }
}
