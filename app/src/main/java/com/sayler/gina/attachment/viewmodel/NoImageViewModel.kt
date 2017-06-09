package com.sayler.gina.attachment.viewmodel

import com.sayler.gina.attachment.AttachmentViewModel
import com.sayler.gina.attachment.TypesFactory
import com.sayler.gina.domain.IAttachment

/**
 * Created by sayler on 2017-06-09.
 *
 * Copyright 2017 MiQUiDO <http://www.miquido.com/>. All rights reserved.
 */
class NoImageViewModel(val attachment: IAttachment) : AttachmentViewModel() {
    override fun type(typesFactory: TypesFactory): Int {
        return typesFactory.type(this)
    }
}