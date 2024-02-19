package com.sayler666.core.compose.scroll

import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.input.nestedscroll.NestedScrollConnection
import androidx.compose.ui.input.nestedscroll.NestedScrollSource

@Composable
fun rememberScrollConnection(
    onScrollUp: () -> Unit,
    onScrollDown: () -> Unit
) = remember {
    object : NestedScrollConnection {
        override fun onPreScroll(
            available: Offset,
            source: NestedScrollSource
        ): Offset {
            val delta = available.y
            if (delta > 30) onScrollUp()
            if (delta < -20) onScrollDown()
            return Offset.Zero
        }
    }
}
