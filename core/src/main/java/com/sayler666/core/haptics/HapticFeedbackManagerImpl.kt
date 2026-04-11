package com.sayler666.core.haptics

import android.content.Context
import com.swmansion.pulsar.Pulsar

class HapticFeedbackManagerImpl(context: Context) : HapticFeedbackManager {
    private val pulsar = Pulsar(context)

    override fun tap() {
        pulsar.getPresets().systemEffectClick()
    }

    override fun swipe() {
        pulsar.getPresets().snap()
    }

    override fun addDaySuccess() {
        pulsar.getPresets().guitarStrum()
    }

    override fun toggle(boolean: Boolean) {
        with(pulsar.getPresets()) {
            if (boolean) systemToggleOn() else systemToggleOff()
        }
    }
}
