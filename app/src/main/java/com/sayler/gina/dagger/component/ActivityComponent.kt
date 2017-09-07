package com.sayler.gina.dagger.component

import android.app.Activity
import com.sayler.gina.dagger.module.ActivityModule
import dagger.Component

@Component(modules = arrayOf(ActivityModule::class), dependencies = arrayOf(ApplicationComponent::class))
interface ActivityComponent {

    // Provide
    fun activity(): Activity
}