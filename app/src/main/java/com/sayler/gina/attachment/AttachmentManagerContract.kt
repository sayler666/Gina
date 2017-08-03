package com.sayler.gina.attachment

import com.sayler.gina.domain.IAttachment

/**
 * Created by sayler on 03.08.2017.
 */

class AttachmentManagerContract {
    interface View {
        fun onUpdate(attachments: List<IAttachment>)
    }

    interface Presenter {
        fun add(bytes: ByteArray, mimeType: String)
        fun remove(id: Int)
    }
}
