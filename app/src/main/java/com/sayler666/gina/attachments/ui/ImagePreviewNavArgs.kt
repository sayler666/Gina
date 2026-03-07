package com.sayler666.gina.attachments.ui

data class ImagePreviewScreenNavArgs(
    val attachmentId: Int,
    val allowNavigationToDayDetails: Boolean = true
)

data class ImagePreviewTmpScreenNavArgs(
    val image: ByteArray,
    val mimeType: String
)
