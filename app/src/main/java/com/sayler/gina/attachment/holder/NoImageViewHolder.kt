package com.sayler.gina.attachment.holder

import android.view.View
import android.widget.TextView
import butterknife.Bind
import butterknife.ButterKnife
import com.sayler.gina.R
import com.sayler.gina.adapter.betteradapter.holder.BetterViewHolder
import com.sayler.gina.attachment.viewmodel.NoImageViewModel

/**
 * Created by sayler on 2017-06-09.
 *
 * Copyright 2017 MiQUiDO <http://www.miquido.com/>. All rights reserved.
 */
class NoImageViewHolder( view: View, onClickListener: ((item: NoImageViewModel, view: View) -> Unit)?) : BetterViewHolder<NoImageViewModel>(view, onClickListener) {


    @Bind(R.id.fileTypeSmallLabel)
    lateinit var fileTypeSmallLabel: TextView
    @Bind(R.id.fileTypeBigLabel)
    lateinit var fileTypeBig: TextView

    override fun bind(item: NoImageViewModel) {
        super.bind(item)
        ButterKnife.bind(this, view)
        with(item.attachment) {
            val fileTypeString = mimeType.substring(mimeType.indexOf("/") + 1).toUpperCase()
            fileTypeSmallLabel.text = fileTypeString
            fileTypeBig.text = fileTypeString
        }
    }
}