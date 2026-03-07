package com.sayler666.gina.addDay

import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.ramcosta.composedestinations.annotation.DeepLink
import com.ramcosta.composedestinations.annotation.Destination
import com.ramcosta.composedestinations.annotation.RootNavGraph
import com.ramcosta.composedestinations.navigation.DestinationsNavigator
import com.sayler666.gina.addDay.ui.ADD_DAY_URL
import com.sayler666.gina.addDay.ui.AddDayScreenNavArgs
import com.sayler666.gina.attachments.ui.ImagePreviewTmpScreenNavArgs
import com.sayler666.gina.destinations.ImagePreviewTmpScreenDestination
import com.sayler666.gina.ginaApp.viewModel.GinaMainViewModel
import com.sayler666.gina.ui.NavigationBarColor
import com.sayler666.gina.addDay.ui.AddDayScreen as FeatureAddDayScreen

@RootNavGraph
@Destination(
    navArgsDelegate = AddDayScreenNavArgs::class,
    deepLinks = [DeepLink(uriPattern = ADD_DAY_URL)]
)
@Composable
fun AddDayScreen(
    destinationsNavigator: DestinationsNavigator,
) {
    val vm: GinaMainViewModel = hiltViewModel()
    val theme by vm.theme.collectAsStateWithLifecycle()
    NavigationBarColor(theme = theme)

    FeatureAddDayScreen(
        onNavigateBack = { destinationsNavigator.popBackStack() },
        onNavigateToImagePreview = { image, mimeType ->
            destinationsNavigator.navigate(
                ImagePreviewTmpScreenDestination(
                    ImagePreviewTmpScreenNavArgs(image = image, mimeType = mimeType)
                )
            )
        },
    )
}
