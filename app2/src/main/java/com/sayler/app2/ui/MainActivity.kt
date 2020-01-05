package com.sayler.app2.ui


import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import com.sayler.app2.R
import com.sayler.app2.file.ActivityResultFlow
import dagger.android.AndroidInjection
import dagger.android.AndroidInjector
import dagger.android.DispatchingAndroidInjector
import dagger.android.HasAndroidInjector
import kotlinx.coroutines.launch
import javax.inject.Inject

class MainActivity : AppCompatActivity(), HasAndroidInjector {
    override fun androidInjector(): AndroidInjector<Any> = supportFragmentInjector

    @Inject
    lateinit var supportFragmentInjector: DispatchingAndroidInjector<Any>

    @Inject
    lateinit var resultFlow: ActivityResultFlow

    override fun onCreate(savedInstanceState: Bundle?) {
        AndroidInjection.inject(this)
        super.onCreate(savedInstanceState)
        setContentView(R.layout.a_main)
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        lifecycleScope.launch {
            resultFlow.send(requestCode, resultCode, data)
        }
    }
}
