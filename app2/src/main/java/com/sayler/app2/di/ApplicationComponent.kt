package com.sayler.app2.di


import android.content.Context
import com.sayler.app2.GinaApplication
import com.sayler.app2.data.DatabaseModule
import com.sayler.app2.data.SettingsModule
import dagger.BindsInstance
import dagger.Component
import dagger.android.AndroidInjector
import dagger.android.DaggerApplication
import dagger.android.support.AndroidSupportInjectionModule
import javax.inject.Singleton

@Singleton
@Component(
        modules = [
            AndroidSupportInjectionModule::class,
            AppAssistedModule::class,
            ActivityFragmentBuilder::class,
            SettingsModule::class,
            DatabaseModule::class
        ]
)
interface ApplicationComponent : AndroidInjector<DaggerApplication> {

    @Component.Builder
    interface Builder {
        @BindsInstance
        fun applicationContext(applicationContext: Context): Builder

        fun build(): ApplicationComponent
    }

    fun inject(app: GinaApplication)

    override fun inject(instance: DaggerApplication?)
}
