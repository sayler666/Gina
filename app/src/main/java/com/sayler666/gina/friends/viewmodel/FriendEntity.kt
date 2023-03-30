package com.sayler666.gina.friends.viewmodel

data class FriendEntity(
    val id: Int,
    val name: String,
    val selected: Boolean = false,
    val avatar: ByteArray? = null,
    val initials: String,
    val daysCount: Int = 0
)
