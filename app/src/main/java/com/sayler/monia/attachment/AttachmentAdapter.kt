package com.sayler.monia.attachment;

import android.graphics.drawable.BitmapDrawable
import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import com.sayler.monia.R
import com.sayler.monia.adapter.betteradapter.BetterAdapter
import com.sayler.monia.adapter.betteradapter.holder.BetterViewHolder
import com.sayler.monia.attachment.viewmodel.AttachmentTypesFactory
import com.sayler.monia.attachment.viewmodel.AttachmentViewModelFactory
import com.sayler.monia.domain.IAttachment

class AttachmentAdapter(val items: Collection<IAttachment>, var attachmentsRecyclerView: RecyclerView, var editable: Boolean = false) : BetterAdapter<AttachmentViewModel>() {

    val typeFactory = AttachmentTypesFactory()
    var onRemoveClickListener: ((item: AttachmentViewModel) -> Unit)? = null

    init {
        items.forEach { viewModels.add(AttachmentViewModelFactory.type(it, editable)) }
    }

    override fun updateItems(items: Collection<IAttachment>) {
        viewModels.clear()
        items.forEach { viewModels.add(AttachmentViewModelFactory.type(it, editable)) }
    }

    override fun getItemCount(): Int {
        return viewModels.count()
    }

    override fun onCreateViewHolder(parent: ViewGroup?, viewType: Int): BetterViewHolder<AttachmentViewModel> {
        if (parent != null) {
            val view = LayoutInflater.from(parent.context).inflate(viewType, parent, false)
            return typeFactory.holder(viewType, view, onClickListener, editable, onRemoveClickListener) as BetterViewHolder<AttachmentViewModel>
        }
        throw RuntimeException("Parent is null")
    }

    override fun getItemViewType(position: Int): Int {
        return viewModels[position].type(typeFactory)
    }

    fun releaseMemory() {
        val count = itemCount
        (0..count - 1)
                .asSequence()
                .map { attachmentsRecyclerView.getChildAt(it) }
                .filter {
                    val b: View? = it.findViewById(R.id.image)
                    b != null
                }
                .map {
                    val imageView: ImageView? = it.findViewById(R.id.image)
                    imageView
                }
                .map { it?.drawable as BitmapDrawable }
                .filter { it.bitmap != null }
                .forEach { it.bitmap.recycle() }
        attachmentsRecyclerView.removeViews(0, count)
    }

    fun setOnRemoveClick(onClick: (item: AttachmentViewModel) -> Unit) {
        onRemoveClickListener = onClick
    }
}