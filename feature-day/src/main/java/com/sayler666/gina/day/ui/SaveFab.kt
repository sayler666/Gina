package com.sayler666.gina.day.ui

import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material.icons.Icons.Filled
import androidx.compose.material.icons.filled.Save
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.sayler666.gina.ui.button.FloatingButton
import com.sayler666.gina.ui.theme.GinaTheme
import com.sayler666.gina.ui.theme.Theme

@Composable
fun SaveFab(
    modifier: Modifier = Modifier,
    onClick: () -> Unit
) {
    FloatingButton(
        modifier = modifier,
        onClick = onClick
    ) {
        Icon(
            imageVector = Filled.Save,
            contentDescription = null,
            tint = MaterialTheme.colorScheme.onSurface,
            modifier = Modifier
                .size(65.dp)
                .padding(12.dp)
        )
    }
}

@Preview
@Composable
private fun AddDayFabPreview() {
    GinaTheme(theme = Theme.Firewatch, darkTheme = true) {
        SaveFab(
            onClick = {})
    }
}
