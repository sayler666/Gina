package com.sayler666.gina.dayDetails

import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.ramcosta.composedestinations.annotation.Destination
import com.ramcosta.composedestinations.annotation.RootNavGraph
import com.ramcosta.composedestinations.navigation.DestinationsNavigator
import com.sayler666.gina.attachments.ui.ImagePreviewScreenNavArgs
import com.sayler666.gina.dayDetails.ui.DayDetailsScreenNavArgs
import com.sayler666.gina.dayDetails.ui.DayDetailsTransitions
import com.sayler666.gina.destinations.DayDetailsEditScreenDestination
import com.sayler666.gina.destinations.DayDetailsScreenDestination
import com.sayler666.gina.destinations.ImagePreviewScreenDestination
import com.sayler666.gina.ginaApp.viewModel.GinaMainViewModel
import com.sayler666.gina.ui.NavigationBarColor
import com.sayler666.gina.dayDetails.ui.DayDetailsScreen as FeatureDayDetailsScreen

@RootNavGraph
@Destination(
    navArgsDelegate = DayDetailsScreenNavArgs::class,
    style = DayDetailsTransitions::class
)
@Composable
fun DayDetailsScreen(
    destinationsNavigator: DestinationsNavigator,
) {
    val ginaMainViewModel: GinaMainViewModel = hiltViewModel()
    val theme by ginaMainViewModel.theme.collectAsStateWithLifecycle()
    NavigationBarColor(theme = theme)

    FeatureDayDetailsScreen(
        onNavigateBack = { destinationsNavigator.popBackStack() },
        onNavigateToEdit = { dayId ->
            destinationsNavigator.navigate(DayDetailsEditScreenDestination(dayId))
        },
        onNavigateToDay = { dayId, way ->
            destinationsNavigator.navigate(
                DayDetailsScreenDestination(DayDetailsScreenNavArgs(dayId = dayId, way = way))
            ) {
                popUpTo(DayDetailsScreenDestination) { inclusive = true }
            }
        },
        onNavigateToAttachment = { attachmentId ->
            destinationsNavigator.navigate(
                ImagePreviewScreenDestination(
                    ImagePreviewScreenNavArgs(
                        attachmentId = attachmentId,
                        allowNavigationToDayDetails = false
                    )
                )
            )
        },
    )
}
