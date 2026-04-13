package com.sayler666.core.haptics

import android.content.Context
import android.media.AudioAttributes
import android.media.SoundPool
import com.sayler666.gina.resources.R
import com.swmansion.pulsar.Pulsar

class HapticFeedbackManagerImpl(context: Context) : HapticFeedbackManager {
    private val pulsar = Pulsar(context)

    private val soundPool = SoundPool.Builder()
        .setMaxStreams(1)
        .setAudioAttributes(
            AudioAttributes.Builder()
                .setUsage(AudioAttributes.USAGE_ASSISTANCE_SONIFICATION)
                .setContentType(AudioAttributes.CONTENT_TYPE_SONIFICATION)
                .build()
        )
        .build()

    private var scribbleSoundId: Int = 0
    private var paperCrumbleSoundId: Int = 0

    init {
        scribbleSoundId = soundPool.load(context, R.raw.scribble, 1)
        paperCrumbleSoundId = soundPool.load(context, R.raw.paper_crumble, 1)
    }

    override fun tap() {
        pulsar.getPresets().systemEffectClick()
    }

    override fun swipe() {
        pulsar.getPresets().snap()
    }

    override fun newDayAdded() {
        pulsar.getPresets().typewriter()
        if (scribbleSoundId != 0) {
            soundPool.play(scribbleSoundId, 0.2f, 0.2f, 1, 0, 1.0f)
        }
    }

    override fun dayRemoved() {
        pulsar.getPresets().typewriter()
        if (paperCrumbleSoundId != 0) {
            soundPool.play(paperCrumbleSoundId, 0.2f, 0.2f, 1, 0, 1.0f)
        }
    }

    override fun toggle(boolean: Boolean) {
        with(pulsar.getPresets()) {
            if (boolean) systemToggleOn() else systemToggleOff()
        }
    }
}
