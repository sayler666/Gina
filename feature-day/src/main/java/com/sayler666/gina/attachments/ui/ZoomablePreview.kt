package com.sayler666.gina.attachments.ui

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.graphicsLayer
import androidx.constraintlayout.compose.ConstrainedLayoutReference
import androidx.constraintlayout.compose.ConstraintLayoutScope
import androidx.constraintlayout.compose.Dimension
import androidx.navigation3.ui.LocalNavAnimatedContentScope
import coil.compose.rememberAsyncImagePainter
import com.sayler666.core.image.ScaledBitmapInfo
import com.sayler666.gina.ui.LocalSharedTransitionScope
import com.sayler666.gina.ui.ZoomableBox

@Composable
fun ConstraintLayoutScope.ZoomablePreview(
    zoomableBox: ConstrainedLayoutReference,
    attachmentId: Int?,
    scaledBitmapInfo: ScaledBitmapInfo,
    onClick: () -> Unit
) {
    val sharedScope = LocalSharedTransitionScope.current
    val sharedElementModifier: Modifier = if (sharedScope != null && attachmentId != null) {
        val state = sharedScope.rememberSharedContentState("attachment_$attachmentId")
        val animScope = LocalNavAnimatedContentScope.current
        with(sharedScope) {
            Modifier.sharedElement(sharedContentState = state, animatedVisibilityScope = animScope)
        }
    } else Modifier.Companion

    ZoomableBox(
        modifier = Modifier
            .fillMaxWidth()
            .then(sharedElementModifier)
            .constrainAs(zoomableBox) {
                top.linkTo(parent.top)
                start.linkTo(parent.start)
                bottom.linkTo(parent.bottom)
                height = Dimension.fillToConstraints
            },
        click = onClick
    ) {
        Image(
            modifier = Modifier.graphicsLayer(
                scaleX = scale,
                scaleY = scale,
                translationX = offsetX,
                translationY = offsetY
            ),
            painter = rememberAsyncImagePainter(scaledBitmapInfo.bitmap),
            contentDescription = null
        )
    }
}
