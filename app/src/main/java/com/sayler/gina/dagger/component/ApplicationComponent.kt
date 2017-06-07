package com.sayler.gina.dagger.component

import android.content.Context
import com.sayler.gina.activity.BaseActivity
import com.sayler.gina.dagger.module.ApplicationModule
import dagger.Component

@Component(modules = arrayOf(ApplicationModule::class))
interface ApplicationComponent {

    fun context(): Context

    // Injections

    fun inject(mainActivity: BaseActivity)
}