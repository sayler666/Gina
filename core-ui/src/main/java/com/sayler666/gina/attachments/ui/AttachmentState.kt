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
    ) : AttachmentState(id, content, mimeType)

    @Immutable
    data class AttachmentNonImageState(
        override val id: Int?,
        override val content: ByteArray,
        override val mimeType: String,
        val name: String,
    ) : AttachmentState(id, content, mimeType)
}
