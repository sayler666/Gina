package com.sayler.gina.dagger.component

import com.sayler.gina.activity.DayActivity
import com.sayler.gina.activity.DayEditActivity
import com.sayler.gina.activity.MainActivity
import com.sayler.gina.dagger.module.ServicesModule
import com.sayler.ormliteimplementation.DataModuleOrmLite
import dagger.Component

import javax.inject.Singleton

@Singleton
@Component(modules = arrayOf(DataModuleOrmLite::class, ServicesModule::class), dependencies = arrayOf(ApplicationComponent::class))
interface DataComponent {

    /* ---- ACTIVITY ---- */
    fun inject(mainActivity: MainActivity)

    fun inject(mainActivity: DayActivity)

    fun inject(mainActivity: DayEditActivity)

    /* ---- FRAGMENT ---- */


    /* ---- PRESENTER ---- */
}