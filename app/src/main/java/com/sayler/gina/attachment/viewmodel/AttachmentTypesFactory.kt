package com.sayler.gina.attachment.viewmodel

import android.view.View
import com.sayler.gina.R
import com.sayler.gina.adapter.betteradapter.holder.BetterViewHolder
import com.sayler.gina.attachment.AttachmentViewModel
import com.sayler.gina.attachment.holder.ImageViewHolder
import com.sayler.gina.attachment.holder.NoImageViewHolder

class AttachmentTypesFactory {
    fun holder(type: Int, view: View, onClick: ((item: AttachmentViewModel, view: View) -> Unit)?, editable: Boolean, onRemoveClick: ((item: AttachmentViewModel) -> Unit)?): BetterViewHolder<out AttachmentViewModel> {
        return when (type) {
            R.layout.i_attachment_image -> ImageViewHolder(view, onClick, editable, onRemoveClick)
            R.layout.i_attachment_no_image -> NoImageViewHolder(view, onClick, editable, onRemoveClick)
            else -> throw RuntimeException("Illegal view type")
        }
    }

    fun type(viewModel: ImageViewModel) = R.layout.i_attachment_image
    fun type(viewModel: NoImageViewModel) = R.layout.i_attachment_no_image
}