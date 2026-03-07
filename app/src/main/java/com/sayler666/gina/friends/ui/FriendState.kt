package com.sayler666.gina.friends.ui

data class FriendState(
    val id: Int = -1,
    val name: String,
    val avatar: ByteArray?,
    val initials: String,
    val selected: Boolean = false,
    val daysCount: Int = 0
)
