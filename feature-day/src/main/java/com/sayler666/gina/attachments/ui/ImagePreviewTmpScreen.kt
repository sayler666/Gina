package com.sayler666.gina.attachments.ui

import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.constraintlayout.compose.ConstraintLayout
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.sayler666.core.image.scaleToMinSize
import com.sayler666.gina.attachments.viewmodel.ImagePreviewTmpEntity
import com.sayler666.gina.attachments.viewmodel.ImagePreviewTmpViewModel

@Composable
fun ImagePreviewTmpScreen(
    image: ByteArray,
    mimeType: String,
) {
    val viewModel: ImagePreviewTmpViewModel =
        hiltViewModel<ImagePreviewTmpViewModel, ImagePreviewTmpViewModel.Factory>(key = image.hashCode().toString()) {
            it.create(image, mimeType)
        }
    val context = LocalContext.current

    val imagePreview: ImagePreviewTmpEntity? by viewModel.imagePreview.collectAsStateWithLifecycle(
        null
    )
    var barsVisible by rememberSaveable { mutableStateOf(false) }

    ConstraintLayout(modifier = Modifier.fillMaxSize()) {
        imagePreview?.let {
            val scaledBitmapInfo =
                remember(it.attachment.id) { it.attachment.content.scaleToMinSize() }
            val (zoomableBox, bottomBar) = createRefs()
            ZoomablePreview(
                zoomableBox,
                null,
                scaledBitmapInfo,
                onClick = { barsVisible = !barsVisible })
            BottomBar(barsVisible, bottomBar, context, it)
        }
    }
}
