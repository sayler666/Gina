package com.sayler.gina.attachment

import android.view.View
import com.sayler.gina.attachment.holder.BetterViewHolder
import com.sayler.gina.attachment.viewmodel.ImageViewModel
import com.sayler.gina.attachment.viewmodel.NoImageViewModel

/**
 * Created by sayler on 2017-06-09.
 *
 * Copyright 2017 MiQUiDO <http://www.miquido.com/>. All rights reserved.
 */

interface TypesFactory {
    fun type(attachment: ImageViewModel): Int
    fun type(attachment: NoImageViewModel): Int

    fun holder(type: Int, view: View): BetterViewHolder<*>
}