package com.sayler666.gina.ui.theme

import androidx.compose.foundation.border
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.TextFieldColors
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.material3.surfaceColorAtElevation
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.composed
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp

@Composable
fun secondaryTextColors(): TextFieldColors {
    val containerColor = MaterialTheme.colorScheme.surfaceColorAtElevation(9.dp).copy(alpha = 0.1f)
    return TextFieldDefaults.colors(
        focusedTextColor = MaterialTheme.colorScheme.secondary,
        focusedContainerColor = containerColor,
        unfocusedContainerColor = containerColor,
        disabledContainerColor = containerColor,
        cursorColor = MaterialTheme.colorScheme.secondary,
        focusedIndicatorColor = Color.Transparent,
        unfocusedIndicatorColor = Color.Transparent,
    )
}


fun Modifier.defaultTextFieldBorder() = composed {
    val shape = RoundedCornerShape(32.dp)
    shadow(2.dp, shape = shape)
        .border(
            width = 0.5.dp,
            brush = Brush.verticalGradient(
                colors = listOf(
                    Color.Transparent,
                    MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.2f)
                )
            ),
            shape = shape
        )
        .clip(shape)
}
