package com.sayler666.gina.attachments.ui

sealed class AttachmentState(
    open val id: Int?,
    open val content: ByteArray?,
    open val mimeType: String,
) {
    data class AttachmentImageState(
        override val id: Int?,
        override val content: ByteArray?,
        override val mimeType: String,
    ) : AttachmentState(id, content, mimeType)

    data class AttachmentNonImageState(
        override val id: Int?,
        override val content: ByteArray?,
        override val mimeType: String,
        val name: String,
    ) : AttachmentState(id, content, mimeType)
}
