package com.sayler.monia.adapter.betteradapter

import android.support.v7.widget.RecyclerView
import android.view.View
import android.view.ViewGroup
import com.sayler.monia.adapter.betteradapter.holder.BetterViewHolder
import com.sayler.monia.domain.IAttachment

/**
 * Created by sayler on 14.06.2017.
 */
abstract class BetterAdapter<VM> : RecyclerView.Adapter<BetterViewHolder<VM>>() {
    protected var viewModels: MutableList<VM> = ArrayList()

    abstract fun updateItems(items: Collection<IAttachment>)

    abstract override fun onCreateViewHolder(parent: ViewGroup?, viewType: Int): BetterViewHolder<VM>

    var onClickListener: ((item: VM, view: View) -> Unit)? = null

    override fun onBindViewHolder(holder: BetterViewHolder<VM>?, position: Int) {
        holder?.bind(viewModels[position])
    }

    fun setOnClick(onClick: (item: VM, view: View) -> Unit) {
        onClickListener = onClick
    }
}