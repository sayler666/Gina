package com.sayler.app2

import com.sayler.app2.di.DaggerApplicationComponent
import dagger.android.AndroidInjector
import dagger.android.DaggerApplication
import kotlinx.coroutines.InternalCoroutinesApi

@UseExperimental(InternalCoroutinesApi::class)
class GinaApplication : DaggerApplication() {

    override fun applicationInjector(): AndroidInjector<out DaggerApplication> =
            DaggerApplicationComponent
                    .builder()
                    .applicationContext(this)
                    .build()
                    .also {
                        it.inject(this)
                    }
}
