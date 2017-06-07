package com.sayler.gina;

import android.app.Application
import android.content.Context
import com.nostra13.universalimageloader.core.DisplayImageOptions
import com.nostra13.universalimageloader.core.ImageLoaderConfiguration
import com.nostra13.universalimageloader.core.assist.QueueProcessingType
import com.sayler.gina.activity.BaseActivity
import com.sayler.gina.dagger.ComponentBuilder
import com.sayler.gina.dagger.component.ApplicationComponent
import com.sayler.gina.dagger.component.DataComponent
import com.sayler.gina.image.ImageLoaderHelper
import io.realm.Realm

class GinaApplication : Application() {

    private lateinit var applicationComponent: ApplicationComponent
    private lateinit var dataComponent: DataComponent


    override fun onCreate() {
        super.onCreate()

        Realm.init(this)

        createComponents()

        initImageLoader()
    }

    private fun initImageLoader() {
        val options = DisplayImageOptions.Builder()
                .cacheOnDisk(true)
                .build()

        val configBuilder = ImageLoaderConfiguration.Builder(this)
                .tasksProcessingOrder(QueueProcessingType.LIFO)
                .defaultDisplayImageOptions(options)

        ImageLoaderHelper.getImageLoader().init(configBuilder.build())
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