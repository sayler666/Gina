package com.sayler666.gina.ui.theme

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.border
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.composed
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun secondaryTextColors() = TextFieldDefaults.textFieldColors(
    focusedIndicatorColor = Color.Transparent,
    unfocusedIndicatorColor = Color.Transparent,
    containerColor = MaterialTheme.colorScheme.secondaryContainer.copy(alpha = 0.2f),
    cursorColor = MaterialTheme.colorScheme.secondary,
    focusedTextColor = MaterialTheme.colorScheme.secondary
)


fun Modifier.defaultTextFieldBorder() = composed {
    this
        .border(
            BorderStroke(
                1.dp,
                color = MaterialTheme.colorScheme.secondary.copy(alpha = 0.4f)
            ), shape = MaterialTheme.shapes.large
        )
        .clip(RoundedCornerShape(4.dp))
}
