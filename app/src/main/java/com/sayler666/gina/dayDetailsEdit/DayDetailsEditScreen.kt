package com.sayler666.gina.dayDetailsEdit

import androidx.compose.runtime.Composable
import com.ramcosta.composedestinations.annotation.Destination
import com.ramcosta.composedestinations.annotation.RootNavGraph
import com.ramcosta.composedestinations.navigation.DestinationsNavigator
import com.sayler666.gina.attachments.ui.ImagePreviewTmpScreenNavArgs
import com.sayler666.gina.dayDetailsEdit.ui.DayDetailsEditScreenNavArgs
import com.sayler666.gina.destinations.DayDetailsScreenDestination
import com.sayler666.gina.destinations.ImagePreviewTmpScreenDestination
import com.sayler666.gina.dayDetailsEdit.ui.DayDetailsEditScreen as FeatureDayDetailsEditScreen

@RootNavGraph
@Destination(navArgsDelegate = DayDetailsEditScreenNavArgs::class)
@Composable
fun DayDetailsEditScreen(
    destinationsNavigator: DestinationsNavigator,
) {
    FeatureDayDetailsEditScreen(
        onNavigateBack = { destinationsNavigator.popBackStack() },
        onNavigateToList = {
            destinationsNavigator.popBackStack(
                route = DayDetailsScreenDestination,
                inclusive = true
            )
        },
        onNavigateToImagePreview = { image, mimeType ->
            destinationsNavigator.navigate(
                ImagePreviewTmpScreenDestination(
                    ImagePreviewTmpScreenNavArgs(image = image, mimeType = mimeType)
                )
            )
        },
    )
}
