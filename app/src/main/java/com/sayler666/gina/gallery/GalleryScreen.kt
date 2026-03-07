package com.sayler666.gina.gallery

import androidx.compose.runtime.Composable
import com.ramcosta.composedestinations.annotation.Destination
import com.ramcosta.composedestinations.annotation.RootNavGraph
import com.ramcosta.composedestinations.navigation.DestinationsNavigator
import com.sayler666.gina.attachments.ui.ImagePreviewScreenNavArgs
import com.sayler666.gina.destinations.ImagePreviewScreenDestination
import com.sayler666.gina.gallery.ui.GalleryScreen as FeatureGalleryScreen

@RootNavGraph
@Destination
@Composable
fun GalleryScreen(
    destinationsNavigator: DestinationsNavigator,
) {
    FeatureGalleryScreen(
        onOpenImage = { imageId ->
            destinationsNavigator.navigate(
                ImagePreviewScreenDestination(ImagePreviewScreenNavArgs(imageId))
            )
        }
    )
}
