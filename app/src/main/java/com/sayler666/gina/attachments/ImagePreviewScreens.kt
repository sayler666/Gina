package com.sayler666.gina.attachments

import androidx.compose.runtime.Composable
import com.sayler666.gina.navigation.Route
import com.sayler666.gina.attachments.ui.ImagePreviewScreen as FeatureImagePreviewScreen
import com.sayler666.gina.attachments.ui.ImagePreviewTmpScreen as FeatureImagePreviewTmpScreen

@Composable
fun ImagePreviewScreen(route: Route.ImagePreview) {
    FeatureImagePreviewScreen(
        initialAttachmentId = route.initialAttachmentId,
        source = route.source,
    )
}

@Composable
fun ImagePreviewTmpScreen(route: Route.ImagePreviewTmp) {
    FeatureImagePreviewTmpScreen(
        image = route.image,
        mimeType = route.mimeType,
    )
}
