package com.sayler.gina.adapter.betteradapter

import android.view.View
import com.sayler.gina.adapter.betteradapter.holder.BetterViewHolder
import com.sayler.gina.attachment.viewmodel.ImageViewModel
import com.sayler.gina.attachment.viewmodel.NoImageViewModel

/**
 * Created by sayler on 2017-06-09.
 *
 * Copyright 2017 MiQUiDO <http://www.miquido.com/>. All rights reserved.
 */

interface TypesFactory<out VM> {
    fun holder(type: Int, view: View, onClick: ((viewModel: VM, view: View) -> Unit)?): BetterViewHolder<out VM>
}