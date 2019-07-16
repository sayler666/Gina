package com.sayler.app2.di

import com.sayler.app2.ui.MainActivity
import com.sayler.app2.ui.days.DaysFragment
import dagger.Module
import dagger.android.ContributesAndroidInjector

@Module
abstract class ActivityFragmentBuilder {
    @ContributesAndroidInjector
    abstract fun bindMainActivity(): MainActivity

    @ContributesAndroidInjector
    abstract fun bindDaysFragment(): DaysFragment
}
