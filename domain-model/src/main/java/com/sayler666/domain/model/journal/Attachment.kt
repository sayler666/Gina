package com.sayler666.domain.model.journal

data class Attachment(
    val id: Int?,
    val dayId: Int?,
    val content: ByteArray,
    val mimeType: String,
    val hidden: Boolean = false, // new flag
) {
    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (javaClass != other?.javaClass) return false

        other as Attachment

        if (id != other.id) return false
        if (dayId != other.dayId) return false
        if (!content.contentEquals(other.content)) return false
        if (mimeType != other.mimeType) return false
        return hidden == other.hidden
    }

    override fun hashCode(): Int {
        var result = id ?: 0
        result = 31 * result + (dayId ?: 0)
        result = 31 * result + content.contentHashCode()
        result = 31 * result + mimeType.hashCode()
        result = 31 * result + hidden.hashCode()
        return result
    }
}
