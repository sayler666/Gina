package com.sayler.gina.attachment.holder

import android.support.v7.widget.RecyclerView
import android.view.View

/**
 * Created by sayler on 2017-06-09.
 *
 * Copyright 2017 MiQUiDO <http://www.miquido.com/>. All rights reserved.
 */
abstract class BetterViewHolder<in T>(view: View): RecyclerView.ViewHolder(view) {
    abstract fun bind(item: T)
}