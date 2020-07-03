package com.sayler.monia;

import android.app.Application
import android.content.Context
import com.nostra13.universalimageloader.core.DisplayImageOptions
import com.nostra13.universalimageloader.core.ImageLoaderConfiguration
import com.nostra13.universalimageloader.core.assist.QueueProcessingType
import com.sayler.monia.activity.BaseActivity
import com.sayler.monia.dagger.ComponentBuilder
import com.sayler.monia.dagger.component.ApplicationComponent
import com.sayler.monia.dagger.component.DataComponent
import com.sayler.monia.image.ImageLoaderHelper

class GinaApplication : Application() {

    private lateinit var applicationComponent: ApplicationComponent
    private lateinit var dataComponent: DataComponent

    override fun onCreate() {
        super.onCreate()


        createComponents()
        dataComponent(this).inject(this)

        initImageLoader()
    }

    private fun initImageLoader() {
        val options = DisplayImageOptions.Builder()
                .cacheOnDisk(true)
                .build()

        val configBuilder = ImageLoaderConfiguration.Builder(this)
                .tasksProcessingOrder(QueueProcessingType.LIFO)
                .defaultDisplayImageOptions(options)

        ImageLoaderHelper.imageLoader.init(configBuilder.build())
    }


    private fun createComponents() {
        applicationComponent = ComponentBuilder.Factory.createApplicationComponent(this)
        dataComponent = ComponentBuilder.Factory.createDataComponent(applicationComponent)
    }

    /** companion **/

    companion object {
        fun dataComponentForActivity(baseActivity: BaseActivity): DataComponent {
            (baseActivity.applicationContext as GinaApplication).applicationComponent.inject(baseActivity)
            return (baseActivity.applicationContext as GinaApplication).dataComponent
        }

        fun dataComponent(context: Context): DataComponent {
            return (context.applicationContext as GinaApplication).dataComponent
        }
    }


}