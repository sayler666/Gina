package com.sayler666.gina.attachments.ui

import android.content.res.Configuration
import android.graphics.BitmapFactory
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.OpenInNew
import androidx.compose.material.icons.filled.Share
import androidx.compose.material3.BottomAppBar
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.surfaceColorAtElevation
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.constraintlayout.compose.ConstraintLayout
import androidx.constraintlayout.compose.Dimension
import coil.compose.rememberAsyncImagePainter
import com.ramcosta.composedestinations.annotation.Destination
import com.ramcosta.composedestinations.navigation.DestinationsNavigator
import com.sayler666.core.file.Files
import com.sayler666.gina.dayDetails.ui.DayDetailsScreenNavArgs
import com.sayler666.gina.destinations.DayDetailsScreenDestination
import com.sayler666.gina.ui.FullScreenDialog
import com.sayler666.gina.ui.ZoomableBox

@Destination(style = FullScreenDialog::class)
@Composable
fun FullImageDialog(
    destinationsNavigator: DestinationsNavigator,
    image: ByteArray,
    mimeType: String,
    dayId: Int? = null
) {
    val configuration = LocalConfiguration.current
    val context = LocalContext.current
    val (bitmapWidth, bitmapHeight) = BitmapFactory.decodeByteArray(image, 0, image.size).let {
        val (w, h) = it.width to it.height
        it.recycle()
        w to h
    }
    ConstraintLayout(
        modifier = Modifier
            .fillMaxSize()
            .padding(0.dp)
    ) {
        val (zoomableBox, bottomBar) = createRefs()
        ZoomableBox(
            modifier = Modifier
                .fillMaxWidth()
                .constrainAs(zoomableBox) {
                    top.linkTo(parent.top)
                    start.linkTo(parent.start)
                    bottom.linkTo(bottomBar.top)
                    height = Dimension.fillToConstraints
                },
            originalImageHeight = bitmapHeight,
            originalImageWidth = bitmapWidth,
            outsideImageClick = { destinationsNavigator.popBackStack() }) {
            Image(
                modifier = Modifier
                    .graphicsLayer(
                        scaleX = scale,
                        scaleY = scale,
                        translationX = offsetX,
                        translationY = offsetY
                    ),
                painter = rememberAsyncImagePainter(image),
                contentDescription = null
            )
        }
        if (configuration.orientation == Configuration.ORIENTATION_PORTRAIT) BottomAppBar(
            modifier = Modifier
                .fillMaxWidth()
                .constrainAs(bottomBar) {
                    bottom.linkTo(parent.bottom)
                    start.linkTo(parent.start)
                },
            containerColor = MaterialTheme.colorScheme.surfaceColorAtElevation(3.dp),
            actions = {
                IconButton(onClick = {
                    destinationsNavigator.popBackStack()
                    Files.openFileIntent(context, bytes = image, mimeType = mimeType)
                }) {
                    Icon(Icons.Filled.Share, null)
                }

                dayId?.let { dayId ->
                    IconButton(onClick = {
                        destinationsNavigator.navigate(
                            DayDetailsScreenDestination(
                                DayDetailsScreenNavArgs(dayId)
                            )
                        )
                    }) {
                        Icon(Icons.Filled.OpenInNew, null)
                    }
                }

                Spacer(modifier = Modifier.weight(1f))
                Text(
                    text = "$mimeType : ${image.size / 1024}KB",
                    modifier = Modifier.padding(end = 8.dp)
                )
            })
    }
}
