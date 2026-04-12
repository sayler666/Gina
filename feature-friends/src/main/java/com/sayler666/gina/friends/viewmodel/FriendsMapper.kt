package com.sayler666.gina.friends.viewmodel

import com.sayler666.domain.model.journal.Friend
import com.sayler666.domain.model.journal.FriendWithCount
import com.sayler666.gina.friends.ui.FriendState
import javax.inject.Inject

class FriendsMapper @Inject constructor() {
    fun mapToDayFriends(
        selectedFriendIds: Set<Int>,
        allFriends: List<FriendWithCount>,
        searchQuery: String? = null
    ): List<FriendState> = when (allFriends.isNotEmpty()) {
        true -> allFriends.map { f ->
            mapToFriend(f).copy(selected = selectedFriendIds.contains(f.friendId))
        }
        false -> emptyList()
    }.filterBySearchQuery(searchQuery)

    fun mapToFriends(
        allFriends: List<FriendWithCount> = emptyList(),
        searchQuery: String? = null
    ): List<FriendState> = allFriends.map(::mapToFriend)
        .sort()
        .filterBySearchQuery(searchQuery)
        .toList()

    private fun mapToFriend(
        friend: FriendWithCount
    ): FriendState = FriendState(
        id = friend.friendId,
        name = friend.friendName,
        avatar = friend.friendAvatar,
        selected = true,
        initials = createInitials(friend.friendName),
        daysCount = friend.daysCount
    )

    fun mapToFriend(
        friend: Friend
    ): FriendState = FriendState(
        id = friend.id,
        name = friend.name,
        avatar = friend.avatar,
        selected = true,
        initials = createInitials(friend.name)
    )

    private fun List<FriendState>.filterBySearchQuery(query: String?) = filter { friend ->
        query?.let { friend.name.contains(it, ignoreCase = true) } ?: run { true }
    }

    private fun List<FriendState>.sort() = sortedBy { it.name }
        .sortedByDescending { it.daysCount }

}

fun createInitials(name: String): String {
    val nameParts = name.split(" ").filter { it.isNotEmpty() }
    val initials = when {
        nameParts.size >= 2 -> nameParts[0][0].toString() + nameParts[1][0].toString()
        else -> name[0].toString()
    }
    return initials
}
