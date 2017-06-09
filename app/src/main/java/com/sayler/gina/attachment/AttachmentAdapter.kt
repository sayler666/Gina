package com.sayler.gina.attachment;

import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.ViewGroup
import com.sayler.gina.attachment.holder.BetterViewHolder
import com.sayler.gina.attachment.viewmodel.AttachmentViewModelFactory
import com.sayler.gina.domain.IAttachment

class AttachmentAdapter(val items: Collection<IAttachment>) : RecyclerView.Adapter<BetterViewHolder<AttachmentViewModel>>() {
    private val typeFactory = TypesFactoryImpl()

    private  var viewModels: MutableList<AttachmentViewModel> = ArrayList()

    init {
        items.forEach { viewModels.add(AttachmentViewModelFactory.type(it)) }
    }

    override fun getItemCount(): Int {
        return items.count()
    }

    override fun onBindViewHolder(holder: BetterViewHolder<AttachmentViewModel>?, position: Int) {
        holder?.bind(viewModels[position])
    }

    override fun onCreateViewHolder(parent: ViewGroup?, viewType: Int): BetterViewHolder<AttachmentViewModel> {
        if (parent != null) {
            val view = LayoutInflater.from(parent.context).inflate(viewType, parent, false)
            return typeFactory.holder(viewType, view) as BetterViewHolder<AttachmentViewModel>
        }
        throw RuntimeException("Parent is null")
    }

    override fun getItemViewType(position: Int): Int {
        return viewModels[position].type(typeFactory)
    }
}