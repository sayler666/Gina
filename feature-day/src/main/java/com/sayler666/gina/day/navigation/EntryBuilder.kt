package com.sayler666.gina.day.navigation

import androidx.navigation3.runtime.EntryProviderScope
import com.sayler666.gina.day.addDay.ui.AddDayScreen
import com.sayler666.gina.day.attachments.ui.ImagePreviewScreen
import com.sayler666.gina.day.attachments.ui.ImagePreviewTmpScreen
import com.sayler666.gina.day.dayDetailsEdit.ui.DayDetailsEditScreen
import com.sayler666.gina.navigation.routes.AddDay
import com.sayler666.gina.navigation.routes.DayDetailsEdit
import com.sayler666.gina.navigation.routes.ImagePreview
import com.sayler666.gina.navigation.routes.ImagePreviewTmp
import com.sayler666.gina.navigation.routes.Route


fun EntryProviderScope<Route>.featureDayEntryBuilder(){
    entry<DayDetailsEdit> { DayDetailsEditScreen(dayId = it.dayId) }
    entry<AddDay> { AddDayScreen(date = it.date) }
    entry<ImagePreview> {
        ImagePreviewScreen(
            initialAttachmentId = it.initialAttachmentId,
            source = it.source,
        )
    }
    entry<ImagePreviewTmp> {
        ImagePreviewTmpScreen(
            image = it.image,
            mimeType = it.mimeType,
            attachmentId = it.attachmentId,
            hidden = it.hidden,
        )
    }
}
