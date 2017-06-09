package com.sayler.gina.attachment.holder

import android.view.View
import android.widget.TextView
import butterknife.Bind
import butterknife.ButterKnife
import com.sayler.gina.R
import com.sayler.gina.attachment.viewmodel.ImageViewModel

/**
 * Created by sayler on 2017-06-09.
 *
 * Copyright 2017 MiQUiDO <http://www.miquido.com/>. All rights reserved.
 */
class ImageViewHolder(val view: View) : BetterViewHolder<ImageViewModel>(view) {

    @Bind(R.id.file_type)
    lateinit var fileType: TextView

    override fun bind(item: ImageViewModel) {
        ButterKnife.bind(this, view)
        fileType.text = item.attachment.mimeType.toString()

    }

}