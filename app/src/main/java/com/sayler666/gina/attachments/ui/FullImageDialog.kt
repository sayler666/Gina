package com.sayler666.gina.attachments.ui

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.FastOutLinearInEasing
import androidx.compose.animation.core.tween
import androidx.compose.animation.slideInVertically
import androidx.compose.animation.slideOutVertically
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.OpenInNew
import androidx.compose.material.icons.filled.Share
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.surfaceColorAtElevation
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.constraintlayout.compose.ConstraintLayout
import androidx.constraintlayout.compose.Dimension
import coil.compose.rememberAsyncImagePainter
import com.ramcosta.composedestinations.annotation.Destination
import com.ramcosta.composedestinations.navigation.DestinationsNavigator
import com.sayler666.core.file.Files
import com.sayler666.core.image.scaleToMinSize
import com.sayler666.gina.dayDetails.ui.DayDetailsScreenNavArgs
import com.sayler666.gina.destinations.DayDetailsScreenDestination
import com.sayler666.gina.ui.NavigationBarColor
import com.sayler666.gina.ui.StatusBarColor
import com.sayler666.gina.ui.ZoomableBox

@Destination
@Composable
fun FullImageDialog(
    destinationsNavigator: DestinationsNavigator,
    image: ByteArray,
    mimeType: String,
    dayId: Int? = null
) {
    val context = LocalContext.current
    val scaledBitmapInfo = image.scaleToMinSize()

    StatusBarColor(color = Color.Transparent, theme = null)
    NavigationBarColor(color = Color.Transparent, theme = null)

    var bottomBarVisible by remember { mutableStateOf(true) }

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
                    bottom.linkTo(parent.bottom)
                    height = Dimension.fillToConstraints
                },
            originalImageHeight = scaledBitmapInfo.height,
            originalImageWidth = scaledBitmapInfo.width,
            click = {
                bottomBarVisible = !bottomBarVisible
            }) {
            Image(
                modifier = Modifier
                    .graphicsLayer(
                        scaleX = scale,
                        scaleY = scale,
                        translationX = offsetX,
                        translationY = offsetY
                    ),
                painter = rememberAsyncImagePainter(scaledBitmapInfo.bitmap),
                contentDescription = null
            )
        }
        AnimatedVisibility(
            visible = bottomBarVisible,
            enter = slideInVertically(
                initialOffsetY = { it },
                animationSpec = tween(durationMillis = 80, easing = FastOutLinearInEasing)
            ),
            exit = slideOutVertically(
                targetOffsetY = { it },
                animationSpec = tween(durationMillis = 80, easing = FastOutLinearInEasing)
            ),
            modifier = Modifier
                .fillMaxWidth()
                .navigationBarsPadding()
                .constrainAs(bottomBar) {
                    bottom.linkTo(parent.bottom)
                    start.linkTo(parent.start)
                }
        ) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .navigationBarsPadding()
                    .height(60.dp)
                    .background(
                        MaterialTheme.colorScheme
                            .surfaceColorAtElevation(3.dp)
                            .copy(alpha = 0.2f)
                    ),
                verticalAlignment = Alignment.CenterVertically
            ) {
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

                val mimeTypeRegexp = Regex(".+/(\\w+)")
                val type = mimeType.replace(mimeTypeRegexp) { it.groupValues[1] }

                Text(
                    text = "$type: ${image.size / 1024}KB",
                    modifier = Modifier.padding(end = 8.dp)
                )
            }
        }
    }
}
