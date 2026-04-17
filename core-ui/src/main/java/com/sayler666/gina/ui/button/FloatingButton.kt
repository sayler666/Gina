package com.sayler666.gina.ui.button

import androidx.compose.foundation.border
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.FloatingActionButtonDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp


@Composable
fun FloatingButton(
    modifier: Modifier = Modifier,
    onClick: () -> Unit,
    iconComposable: @Composable () -> Unit,
) {
    FloatingActionButton(
        modifier = modifier
            .border(
                width = 0.5.dp,
                brush = Brush.verticalGradient(
                    colors = listOf(Color.White.copy(alpha = 0.29f), Color.Transparent)
                ),
                shape = CircleShape
            ),
        containerColor = MaterialTheme.colorScheme.secondaryContainer,
        shape = CircleShape,
        elevation = FloatingActionButtonDefaults.elevation(
            defaultElevation = 6.dp,
            pressedElevation = 2.dp
        ),
        onClick = onClick
    ) {
        iconComposable()
    }
}
