package com.sayler.gina.activity

import android.os.Bundle
import com.sayler.gina.GinaApplication
import com.sayler.gina.R
import com.sayler.gina.store.settings.SettingsStoreManager
import javax.inject.Inject

class KotlinActivity : BaseActivity() {
    @Inject
    lateinit var settingsStoreManager: SettingsStoreManager

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_kotlin)

        GinaApplication.getDataComponentForActivity(this).inject(this)
        val get = settingsStoreManager.get()

    }
}
