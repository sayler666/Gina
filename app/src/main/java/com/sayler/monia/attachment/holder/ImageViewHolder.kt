package com.sayler.monia.attachment.holder

import android.graphics.BitmapFactory
import android.view.View
import android.widget.ImageView
import android.widget.TextView
import butterknife.BindView
import butterknife.ButterKnife
import com.sayler.monia.R
import com.sayler.monia.adapter.betteradapter.holder.BetterViewHolder
import com.sayler.monia.attachment.viewmodel.ImageViewModel

/**
 * Created by sayler on 2017-06-09.
 *

 */
class ImageViewHolder(view: View, onClickListener: ((item: ImageViewModel, view: View) -> Unit)?, var editable: Boolean, var onRemoveClickListener: ((item: ImageViewModel) -> Unit)?) : BetterViewHolder<ImageViewModel>(view, onClickListener) {

    @BindView(R.id.fileTypeSmallLabel)
    lateinit var fileTypeSmallLabel: TextView
    @BindView(R.id.image)
    lateinit var image: ImageView
    @BindView(R.id.removeButton)
    lateinit var removeButton: View

    override fun bind(item: ImageViewModel) {
        super.bind(item)
        ButterKnife.bind(this, view)
        with(item.attachment) {
            fileTypeSmallLabel.text = mimeType.substring(mimeType.indexOf("/") + 1).toUpperCase()
            val decodeByteArray = BitmapFactory.decodeByteArray(file, 0, file.size)
            image.setImageBitmap(decodeByteArray)
        }

        removeButton.visibility = if (editable) {
            View.VISIBLE
        } else {
            View.GONE
        }
        removeButton.setOnClickListener { onRemoveClick() }
    }

    fun onRemoveClick() {
        onRemoveClickListener?.invoke(viewModel as ImageViewModel)
    }
}