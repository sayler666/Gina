package com.sayler.gina.dagger;


import com.sayler.gina.GinaApplication
import com.sayler.gina.dagger.component.ApplicationComponent
import com.sayler.gina.dagger.component.DaggerApplicationComponent
import com.sayler.gina.dagger.component.DaggerDataComponent
import com.sayler.gina.dagger.component.DataComponent
import com.sayler.gina.dagger.module.ApplicationModule

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
