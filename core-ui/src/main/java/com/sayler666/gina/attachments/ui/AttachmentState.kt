package com.sayler666.gina.attachments.ui

import androidx.compose.runtime.Immutable

sealed class AttachmentState(
    open val id: Int?,
    open val content: ByteArray,
    open val mimeType: String,
    open val hidden: Boolean = false,
) {
    @Immutable
    data class AttachmentImageState(
        override val id: Int?,
        override val content: ByteArray,
        override val mimeType: String,
        override val hidden: Boolean = false,
    ) : AttachmentState(id, content, mimeType) {
        override fun equals(other: Any?): Boolean {
            if (this === other) return true
            if (javaClass != other?.javaClass) return false

            other as AttachmentImageState

            if (id != other.id) return false
            if (hidden != other.hidden) return false
            if (!content.contentEquals(other.content)) return false
            if (mimeType != other.mimeType) return false

            return true
        }

        override fun hashCode(): Int {
            var result = id ?: 0
            result = 31 * result + hidden.hashCode()
            result = 31 * result + content.contentHashCode()
            result = 31 * result + mimeType.hashCode()
            return result
        }
    }

    @Immutable
    data class AttachmentNonImageState(
        override val id: Int?,
        override val content: ByteArray,
        override val mimeType: String,
        val name: String,
    ) : AttachmentState(id, content, mimeType) {
        override fun equals(other: Any?): Boolean {
            if (this === other) return true
            if (javaClass != other?.javaClass) return false

            other as AttachmentNonImageState

            if (id != other.id) return false
            if (!content.contentEquals(other.content)) return false
            if (mimeType != other.mimeType) return false
            if (name != other.name) return false

            return true
        }

        override fun hashCode(): Int {
            var result = id ?: 0
            result = 31 * result + content.contentHashCode()
            result = 31 * result + mimeType.hashCode()
            result = 31 * result + name.hashCode()
            return result
        }
    }
}
