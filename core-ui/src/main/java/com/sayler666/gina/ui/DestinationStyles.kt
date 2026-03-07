package com.sayler666.gina.ui

import androidx.compose.ui.window.DialogProperties
import com.ramcosta.composedestinations.spec.DestinationStyle

object FullScreenDialog : DestinationStyle.Dialog {
    override val properties = DialogProperties(
        usePlatformDefaultWidth = false
    )
}
