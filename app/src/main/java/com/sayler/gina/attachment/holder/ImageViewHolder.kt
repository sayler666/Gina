package com.sayler.gina.attachment.holder

import android.graphics.BitmapFactory
import android.view.View
import android.widget.ImageView
import android.widget.TextView
import butterknife.Bind
import butterknife.ButterKnife
import com.sayler.gina.R
import com.sayler.gina.adapter.betteradapter.holder.BetterViewHolder
import com.sayler.gina.attachment.viewmodel.ImageViewModel

/**
 * Created by sayler on 2017-06-09.
 *
 * Copyright 2017 MiQUiDO <http://www.miquido.com/>. All rights reserved.
 */
class ImageViewHolder(view: View, onClickListener: ((item: ImageViewModel, view: View) -> Unit)?) : BetterViewHolder<ImageViewModel>(view, onClickListener) {

    @Bind(R.id.fileTypeSmallLabel)
    lateinit var fileTypeSmallLabel: TextView
    @Bind(R.id.image)
    lateinit var image: ImageView

    override fun bind(item: ImageViewModel) {
        super.bind(item)
        ButterKnife.bind(this, view)
        with(item.attachment) {
            fileTypeSmallLabel.text = mimeType.substring(mimeType.indexOf("/") + 1).toUpperCase()
            val decodeByteArray = BitmapFactory.decodeByteArray(file, 0, file.size)
            image.setImageBitmap(decodeByteArray)
        }

    }

}