package com.sayler.monia.dagger.component

import com.sayler.monia.GinaApplication
import com.sayler.monia.activity.DayActivity
import com.sayler.monia.activity.EditDayActivity
import com.sayler.monia.activity.MainActivity
import com.sayler.monia.dagger.module.ManagersModule
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