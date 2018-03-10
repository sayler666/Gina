package com.sayler.gina.dagger.component

import com.sayler.gina.GinaApplication
import com.sayler.gina.activity.DayActivity
import com.sayler.gina.activity.EditDayActivity
import com.sayler.gina.activity.MainActivity
import com.sayler.gina.dagger.module.ManagersModule
import com.sayler.ormliteimplementation.DataModuleOrmLite
import dagger.Component

import javax.inject.Singleton

@Singleton
@Component(modules = arrayOf(DataModuleOrmLite::class, ManagersModule::class), dependencies = arrayOf(ApplicationComponent::class))
interface DataComponent {

    /* ---- ACTIVITY ---- */
    fun inject(mainActivity: MainActivity)

    fun inject(mainActivity: DayActivity)

    fun inject(mainDayActivity: EditDayActivity)

    fun inject(ginaApplication: GinaApplication)

    /* ---- FRAGMENT ---- */


    /* ---- PRESENTER ---- */
}