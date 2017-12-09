package com.sayler.gina.attachment.holder

import android.graphics.BitmapFactory
import android.view.View
import android.widget.Button
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

 */
class ImageViewHolder(view: View, onClickListener: ((item: ImageViewModel, view: View) -> Unit)?, var editable: Boolean, var onRemoveClickListener: ((item: ImageViewModel) -> Unit)?) : BetterViewHolder<ImageViewModel>(view, onClickListener) {

    @Bind(R.id.fileTypeSmallLabel)
    lateinit var fileTypeSmallLabel: TextView
    @Bind(R.id.image)
    lateinit var image: ImageView
    @Bind(R.id.removeButton)
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