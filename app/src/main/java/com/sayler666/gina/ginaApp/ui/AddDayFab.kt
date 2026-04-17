package com.sayler666.gina.ginaApp.ui

import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material3.Icon
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.sayler666.gina.resources.R
import com.sayler666.gina.ui.button.FloatingButton
import com.sayler666.gina.ui.theme.GinaTheme
import com.sayler666.gina.ui.theme.Theme

@Composable
fun AddDayFab(
    modifier: Modifier = Modifier,
    onClick: () -> Unit
) {
    FloatingButton(
        modifier = modifier,
        onClick = onClick
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

@Preview
@Composable
private fun AddDayFabPreview() {
    GinaTheme(theme = Theme.Firewatch, darkTheme = true) {
        AddDayFab(
            onClick = {})
    }
}
