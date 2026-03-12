package com.sayler666.gina.day.attachments.ui

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
import com.sayler666.gina.day.attachments.viewmodel.ImagePreviewTmpEntity
import com.sayler666.gina.day.attachments.viewmodel.ImagePreviewTmpViewModel
import com.sayler666.gina.day.attachments.viewmodel.ImagePreviewTmpViewModel.ViewEvent.OnToggleHidden

@Composable
fun ImagePreviewTmpScreen(
    image: ByteArray,
    mimeType: String,
    attachmentId: Int? = null,
    hidden: Boolean = false,
) {
    val viewModel: ImagePreviewTmpViewModel =
        hiltViewModel<ImagePreviewTmpViewModel, ImagePreviewTmpViewModel.Factory>(key = image.hashCode().toString()) {
            it.create(image, mimeType, attachmentId, hidden)
        }
    val context = LocalContext.current

    val imagePreview: ImagePreviewTmpEntity? by viewModel.imagePreview.collectAsStateWithLifecycle(
        null
    )
    val currentHidden by viewModel.hidden.collectAsStateWithLifecycle()
    var barsVisible by rememberSaveable { mutableStateOf(true) }

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
            BottomBar(
                barsVisible = barsVisible,
                bottomBarRef = bottomBar,
                context = context,
                imagePreviewEntity = it,
                hidden = currentHidden,
                onToggleHidden = { newHidden -> viewModel.onViewEvent(OnToggleHidden(newHidden)) }
            )
        }
    }
}
