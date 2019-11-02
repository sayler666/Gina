package com.sayler.app2.di

import com.sayler.app2.day.DayFragment
import com.sayler.app2.days.DaysFragment
import com.sayler.app2.ui.MainActivity
import dagger.Module
import dagger.android.ContributesAndroidInjector

@Module
abstract class ActivityFragmentBuilder {
    @ContributesAndroidInjector
    abstract fun bindMainActivity(): MainActivity

    @ContributesAndroidInjector
    abstract fun bindDaysFragment(): DaysFragment

    @ContributesAndroidInjector
    abstract fun bindDayFragment(): DayFragment
}
