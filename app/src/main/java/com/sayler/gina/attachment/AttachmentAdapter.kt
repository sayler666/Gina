package com.sayler.gina.attachment;

import com.sayler.gina.adapter.betteradapter.BetterAdapter
import com.sayler.gina.attachment.viewmodel.AttachmentViewModelFactory
import com.sayler.gina.attachment.viewmodel.AttachmentTypesFactory
import com.sayler.gina.domain.IAttachment

class AttachmentAdapter(val items: Collection<IAttachment>) : BetterAdapter<AttachmentViewModel>() {
    override val typeFactory = AttachmentTypesFactory()

    init {
        items.forEach { viewModels.add(AttachmentViewModelFactory.type(it)) }
    }

    override fun getItemCount(): Int {
        return items.count()
    }

    override fun getItemViewType(position: Int): Int {
        return viewModels[position].type(typeFactory)
    }

}