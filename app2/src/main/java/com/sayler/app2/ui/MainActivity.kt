package com.sayler.app2.ui


import android.os.Bundle
import androidx.fragment.app.Fragment
import com.airbnb.mvrx.BaseMvRxActivity
import com.sayler.app2.R
import dagger.android.AndroidInjection
import dagger.android.AndroidInjector
import dagger.android.DispatchingAndroidInjector
import dagger.android.support.HasSupportFragmentInjector
import javax.inject.Inject

class MainActivity : BaseMvRxActivity(), HasSupportFragmentInjector {
    override fun supportFragmentInjector(): AndroidInjector<Fragment> = supportFragmentInjector

    @Inject
    lateinit var supportFragmentInjector: DispatchingAndroidInjector<Fragment>

    override fun onCreate(savedInstanceState: Bundle?) {
        AndroidInjection.inject(this)
        super.onCreate(savedInstanceState)
        setContentView(R.layout.a_main)

    }

}
