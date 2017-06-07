package com.sayler.gina.dagger.module;

import android.content.Context;
import com.sayler.gina.GinaApplication;
import dagger.Module;
import dagger.Provides;

@Module
class ApplicationModule(private val application: GinaApplication) {

    @Provides
    fun provideContext(): Context {
        return application
    }

}