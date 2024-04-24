package com.sayler666.gina.friends.viewmodel

import com.sayler666.gina.friends.ui.FriendState

@Deprecated("Use FriendState")
data class FriendEntity(
    val id: Int,
    val name: String,
    val selected: Boolean = false,
    val avatar: ByteArray? = null,
    val initials: String,
    val daysCount: Int = 0
)

fun FriendEntity.toState() = FriendState(
    name = name,
    avatar = avatar,
    initials = initials
)
