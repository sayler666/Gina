package com.sayler666.gina.friends.viewmodel

import com.sayler666.gina.dayDetails.viewmodel.FriendEntity
import com.sayler666.gina.db.Friend
import javax.inject.Inject

class FriendsMapper @Inject constructor() {
    fun mapToDayFriends(
        dayFriends: List<Friend>,
        allFriends: List<Friend>,
        searchQuery: String? = null
    ): List<FriendEntity> {
        val friendsIds = dayFriends.map { it.id }

        return when (allFriends.isNotEmpty()) {
            true -> allFriends.map { f ->
                mapToFriend(f)
                    .copy(selected = friendsIds.contains(f.id))
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
        allFriends: List<Friend> = emptyList(),
        searchQuery: String? = null
    ): List<FriendEntity> = allFriends.map(::mapToFriend)
        .sort()
        .filterBySearchQuery(searchQuery)
        .toList()

    fun mapToFriend(
        friend: Friend
    ): FriendEntity = FriendEntity(
        id = friend.id,
        name = friend.name,
        avatar = friend.avatar,
        selected = true,
        initials = createInitials(friend)
    )

    private fun List<FriendEntity>.filterBySearchQuery(query: String?) = filter { friend ->
        query?.let { friend.name.contains(it, ignoreCase = true) } ?: run { true }
    }

    private fun List<FriendEntity>.sort() = sortedBy { it.name }
        .sortedBy { it.avatar == null }
        .sortedBy { !it.selected }

    private fun createInitials(f: Friend): String {
        val nameParts = f.name.split(" ").filter { it.isNotEmpty() }
        val initials = when {
            nameParts.size >= 2 -> nameParts[0][0].toString() + nameParts[1][0].toString()
            else -> f.name[0].toString()
        }
        return initials
    }
}
