package com.sayler666.core.compose

import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.add

operator fun WindowInsets.plus(insets: WindowInsets): WindowInsets = add(insets)
