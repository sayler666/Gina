package com.sayler666.gina.ginaApp.navigation

import android.R.attr.onClick
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.FloatingActionButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import com.sayler666.gina.R
import dev.chrisbanes.haze.HazeState
import dev.chrisbanes.haze.HazeStyle
import dev.chrisbanes.haze.HazeTint
import dev.chrisbanes.haze.hazeEffect

@Composable
fun AddDayFab(
    modifier: Modifier = Modifier,
    onNavigateToAddDay: () -> Unit
) {
    FloatingActionButton(
        modifier = modifier
            .border(
                width = 0.5.dp,
                brush = Brush.verticalGradient(
                    colors = listOf(
                        Color.Transparent,
                        Color.White.copy(alpha = 0.2f)
                    )
                ),
                shape = CircleShape
            ),
        containerColor = MaterialTheme.colorScheme.secondaryContainer,
        shape = CircleShape,
        elevation = FloatingActionButtonDefaults.elevation(
            defaultElevation = 6.dp,
            pressedElevation = 2.dp
        ),
        onClick = onNavigateToAddDay
    ) {
        Icon(
            painter = painterResource(R.drawable.feather_icon),
            contentDescription = "Add new entry",
            modifier = Modifier
                .size(65.dp)
                .padding(12.dp)
        )
    }
}
