package com.sayler666.domain.model.journal

data class FriendWithCount(
    val friendId: Int,
    val friendName: String,
    val friendAvatar: ByteArray?,
    val daysCount: Int
)
