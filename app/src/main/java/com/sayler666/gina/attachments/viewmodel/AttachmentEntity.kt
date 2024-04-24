package com.sayler666.gina.attachments.viewmodel

@Deprecated("Use AttachmentState")
sealed class AttachmentEntity(
    open val id: Int?,
    open val bytes: ByteArray,
    open val mimeType: String,
    open val dayId: Int? = null
) {
    data class Image(
        override val id: Int? = null,
        override val bytes: ByteArray,
        override val mimeType: String,
        override val dayId: Int? = null
    ) : AttachmentEntity(id, bytes, mimeType, dayId) {
        override fun equals(other: Any?): Boolean {
            if (this === other) return true
            if (javaClass != other?.javaClass) return false

            other as Image

            if (id != other.id) return false
            if (dayId != other.dayId) return false
            if (!bytes.contentEquals(other.bytes)) return false
            if (mimeType != other.mimeType) return false

            return true
        }

        override fun hashCode(): Int {
            var result = id ?: 0
            result = 31 * result + bytes.contentHashCode()
            result = 31 * result + mimeType.hashCode()
            return result
        }
    }

    data class NonImage(
        override val id: Int? = null,
        override val bytes: ByteArray,
        override val mimeType: String,
        override val dayId: Int? = null,
        val displayName: String
    ) : AttachmentEntity(id, bytes, mimeType, dayId) {
        override fun equals(other: Any?): Boolean {
            if (this === other) return true
            if (javaClass != other?.javaClass) return false

            other as NonImage

            if (id != other.id) return false
            if (dayId != other.dayId) return false
            if (!bytes.contentEquals(other.bytes)) return false
            if (mimeType != other.mimeType) return false
            if (displayName != other.displayName) return false

            return true
        }

        override fun hashCode(): Int {
            var result = id ?: 0
            result = 31 * result + bytes.contentHashCode()
            result = 31 * result + mimeType.hashCode()
            result = 31 * result + displayName.hashCode()
            return result
        }
    }
}
