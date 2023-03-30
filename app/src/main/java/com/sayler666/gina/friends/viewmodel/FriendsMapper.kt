package com.sayler666.gina.friends.viewmodel

import com.sayler666.gina.db.Friend
import com.sayler666.gina.db.FriendWithCount
import javax.inject.Inject

class FriendsMapper @Inject constructor() {
    fun mapToDayFriends(
        dayFriends: List<Friend>,
        allFriends: List<FriendWithCount>,
        searchQuery: String? = null
    ): List<FriendEntity> {
        val friendsIds = dayFriends.map { it.id }

        return when (allFriends.isNotEmpty()) {
            true -> allFriends.map { f ->
                mapToFriend(f)
                    .copy(selected = friendsIds.contains(f.friendId))
            }
            false -> dayFriends.map { f ->
                mapToFriend(f)
                    .copy(selected = true)
            }
        }
            .map { it.copy(selected = friendsIds.contains(it.id)) }
            .sort()
            .filterBySearchQuery(searchQuery)
    }

    fun mapToFriends(
        allFriends: List<FriendWithCount> = emptyList(),
        searchQuery: String? = null
    ): List<FriendEntity> = allFriends.map(::mapToFriend)
        .sort()
        .filterBySearchQuery(searchQuery)
        .toList()

    private fun mapToFriend(
        friend: FriendWithCount
    ): FriendEntity = FriendEntity(
        id = friend.friendId,
        name = friend.friendName,
        avatar = friend.friendAvatar,
        selected = true,
        initials = createInitials(friend.friendName),
        daysCount = friend.daysCount
    )

    fun mapToFriend(
        friend: Friend
    ): FriendEntity = FriendEntity(
        id = friend.id,
        name = friend.name,
        avatar = friend.avatar,
        selected = true,
        initials = createInitials(friend.name)
    )

    private fun List<FriendEntity>.filterBySearchQuery(query: String?) = filter { friend ->
        query?.let { friend.name.contains(it, ignoreCase = true) } ?: run { true }
    }

    private fun List<FriendEntity>.sort() = sortedBy { it.name }
        .sortedByDescending { it.daysCount }

    private fun createInitials(name: String): String {
        val nameParts = name.split(" ").filter { it.isNotEmpty() }
        val initials = when {
            nameParts.size >= 2 -> nameParts[0][0].toString() + nameParts[1][0].toString()
            else -> name[0].toString()
        }
        return initials
    }
}
