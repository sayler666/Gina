package com.sayler666.domain.model.journal

data class Friend(
    val id: Int = 0,
    val name: String,
    val avatar: ByteArray?
) {
    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (javaClass != other?.javaClass) return false
        other as Friend
        if (id != other.id) return false
        if (name != other.name) return false
        return avatar.contentEquals(other.avatar)
    }

    override fun hashCode(): Int {
        var result = id
        result = 31 * result + name.hashCode()
        result = 31 * result + (avatar?.contentHashCode() ?: 0)
        return result
    }
}
