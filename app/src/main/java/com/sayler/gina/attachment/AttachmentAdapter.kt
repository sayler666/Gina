package com.sayler.gina.attachment;

import android.graphics.drawable.BitmapDrawable
import android.support.v7.widget.RecyclerView
import android.widget.ImageView
import com.sayler.gina.R
import com.sayler.gina.adapter.betteradapter.BetterAdapter
import com.sayler.gina.attachment.viewmodel.AttachmentTypesFactory
import com.sayler.gina.attachment.viewmodel.AttachmentViewModelFactory
import com.sayler.gina.domain.IAttachment

class AttachmentAdapter(val items: Collection<IAttachment>, var attachmentsRecyclerView: RecyclerView) : BetterAdapter<AttachmentViewModel>() {
    override val typeFactory = AttachmentTypesFactory()

    init {
        items.forEach { viewModels.add(AttachmentViewModelFactory.type(it)) }
    }

    override fun updateItems(items: Collection<IAttachment>){
        viewModels.clear()
        items.forEach { viewModels.add(AttachmentViewModelFactory.type(it)) }
    }

    override fun getItemCount(): Int {
        return viewModels.count()
    }

    override fun getItemViewType(position: Int): Int {
        return viewModels[position].type(typeFactory)
    }

    fun releaseMemory() {
        val count = itemCount
        (0..count - 1)
                .asSequence()
                .map { attachmentsRecyclerView.getChildAt(it) }
                .filter { it.findViewById(R.id.image) != null }
                .map { it.findViewById(R.id.image) as ImageView }
                .map { it.drawable as BitmapDrawable }
                .filter { it.bitmap != null }
                .forEach { it.bitmap.recycle() }
        attachmentsRecyclerView.removeViews(0, count)
    }

}