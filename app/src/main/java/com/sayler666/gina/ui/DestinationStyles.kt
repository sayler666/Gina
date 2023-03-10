package com.sayler666.gina.ui

import androidx.compose.ui.ExperimentalComposeUiApi
import androidx.compose.ui.window.DialogProperties
import com.ramcosta.composedestinations.spec.DestinationStyle

object FullScreenDialog : DestinationStyle.Dialog {
    @OptIn(ExperimentalComposeUiApi::class)
    override val properties = DialogProperties(
        usePlatformDefaultWidth = false
    )
}
