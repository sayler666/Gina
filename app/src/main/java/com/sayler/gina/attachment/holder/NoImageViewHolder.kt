package com.sayler.gina.attachment.holder

import android.view.View
import android.widget.TextView
import butterknife.BindView
import butterknife.ButterKnife
import com.sayler.gina.R
import com.sayler.gina.adapter.betteradapter.holder.BetterViewHolder
import com.sayler.gina.attachment.viewmodel.NoImageViewModel

/**
 * Created by sayler on 2017-06-09.
 *

 */
class NoImageViewHolder(view: View, onClickListener: ((item: NoImageViewModel, view: View) -> Unit)?, var editable: Boolean, var onRemoveClickListener: ((item: NoImageViewModel) -> Unit)?) : BetterViewHolder<NoImageViewModel>(view, onClickListener) {


    @BindView(R.id.fileTypeSmallLabel)
    lateinit var fileTypeSmallLabel: TextView
    @BindView(R.id.fileTypeBigLabel)
    lateinit var fileTypeBig: TextView
    @BindView(R.id.removeButton)
    lateinit var removeButton: View

    override fun bind(item: NoImageViewModel) {
        super.bind(item)
        ButterKnife.bind(this, view)
        with(item.attachment) {
            val fileTypeString = mimeType.substring(mimeType.indexOf("/") + 1).toUpperCase()
            fileTypeSmallLabel.text = fileTypeString
            fileTypeBig.text = fileTypeString
        }
        removeButton.visibility = if (editable) {
            View.VISIBLE
        } else {
            View.GONE
        }
        removeButton.setOnClickListener { onRemoveClick() }
    }

    fun onRemoveClick() {
        onRemoveClickListener?.invoke(viewModel as NoImageViewModel)
    }
}