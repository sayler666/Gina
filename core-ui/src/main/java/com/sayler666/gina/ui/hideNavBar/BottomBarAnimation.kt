package com.sayler666.gina.ui.hideNavBar

import androidx.compose.runtime.Composable
import androidx.compose.runtime.State

interface BottomBarAnimation {
    @Composable
    fun animateAsState(visible: Boolean): State<BottomBarAnimInfo>
}
