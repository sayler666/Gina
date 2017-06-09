package com.sayler.gina.attachment;

import android.view.View
import com.sayler.gina.R
import com.sayler.gina.attachment.holder.BetterViewHolder
import com.sayler.gina.attachment.holder.ImageViewHolder
import com.sayler.gina.attachment.holder.NoImageViewHolder
import com.sayler.gina.attachment.viewmodel.ImageViewModel
import com.sayler.gina.attachment.viewmodel.NoImageViewModel

class TypesFactoryImpl : TypesFactory {
    override fun type(attachment: ImageViewModel) = R.layout.i_attachment_image
    override fun type(attachment: NoImageViewModel) = R.layout.i_attachment_no_image

    override fun holder(type: Int, view: View): BetterViewHolder<*> {
        return when (type) {
            R.layout.i_attachment_image -> ImageViewHolder(view)
            R.layout.i_attachment_no_image -> NoImageViewHolder(view)
            else -> throw RuntimeException("Illegal view type")
        }
    }

}