package com.sayler.monia.dagger;


import com.sayler.monia.GinaApplication
import com.sayler.monia.dagger.component.ApplicationComponent
import com.sayler.monia.dagger.component.DaggerApplicationComponent
import com.sayler.monia.dagger.component.DaggerDataComponent
import com.sayler.monia.dagger.component.DataComponent
import com.sayler.monia.dagger.module.ApplicationModule

class ComponentBuilder {

    companion object Factory {
        fun createApplicationComponent(application: GinaApplication): ApplicationComponent {
            return DaggerApplicationComponent.builder()
                    .applicationModule(ApplicationModule(application))
                    .build()
        }

        fun createDataComponent(applicationComponent: ApplicationComponent): DataComponent {
            return DaggerDataComponent.builder()
                    .applicationComponent(applicationComponent)
                    .build()
        }
    }

}
