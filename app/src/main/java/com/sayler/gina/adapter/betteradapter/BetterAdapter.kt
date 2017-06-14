package com.sayler.gina.adapter.betteradapter

import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.sayler.gina.adapter.betteradapter.holder.BetterViewHolder

/**
 * Created by sayler on 14.06.2017.
 */
abstract class BetterAdapter<VM> : RecyclerView.Adapter<BetterViewHolder<VM>>() {
    protected abstract val typeFactory: TypesFactory<VM>
    protected var viewModels: MutableList<VM> = ArrayList()

    var onClickListener: ((item: VM, view: View) -> Unit)? = null

    override fun onCreateViewHolder(parent: ViewGroup?, viewType: Int): BetterViewHolder<VM> {
        if (parent != null) {
            val view = LayoutInflater.from(parent.context).inflate(viewType, parent, false)
            return typeFactory.holder(viewType, view, onClickListener) as BetterViewHolder<VM>
        }
        throw RuntimeException("Parent is null")
    }

    override fun onBindViewHolder(holder: BetterViewHolder<VM>?, position: Int) {
        holder?.bind(viewModels[position])
    }

    fun setOnClick(onClick: (item: VM, view: View) -> Unit) {
        onClickListener = onClick
    }

}