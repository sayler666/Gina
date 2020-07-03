package com.sayler.monia.dagger.component

import android.content.Context
import com.sayler.monia.activity.BaseActivity
import com.sayler.monia.dagger.module.ApplicationModule
import dagger.Component

@Component(modules = arrayOf(ApplicationModule::class))
interface ApplicationComponent {

    fun context(): Context

    // Injections

    fun inject(mainActivity: BaseActivity)
}