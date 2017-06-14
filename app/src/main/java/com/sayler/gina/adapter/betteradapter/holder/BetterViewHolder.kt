package com.sayler.gina.adapter.betteradapter.holder

import android.support.annotation.CallSuper
import android.support.v7.widget.RecyclerView
import android.view.View

/**
 * Created by sayler on 2017-06-09.
 *
 * Copyright 2017 MiQUiDO <http://www.miquido.com/>. All rights reserved.
 */
abstract class BetterViewHolder<VM>(var view: View, var onClickListener: ((item: VM, view: View) -> Unit)?) : RecyclerView.ViewHolder(view) {
    var viewModel: VM? = null

    init {
        view.setOnClickListener { onClick() }
    }

    @CallSuper
    open fun bind(item: VM) {
        this.viewModel = item
    }

    fun onClick() {
        if (viewModel != null && viewModel is VM)
            onClickListener?.invoke(viewModel as VM, view)
    }
}