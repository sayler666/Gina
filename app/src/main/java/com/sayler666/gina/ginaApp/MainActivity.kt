package com.sayler666.gina.ginaApp

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.viewModels
import androidx.core.splashscreen.SplashScreen.Companion.installSplashScreen
import androidx.core.view.WindowCompat
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.flowWithLifecycle
import androidx.lifecycle.lifecycleScope
import com.sayler666.gina.ginaApp.viewModel.GinaMainViewModel
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

@AndroidEntryPoint
class MainActivity : ComponentActivity() {

    private val vm: GinaMainViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        var showSplash = savedInstanceState?.getBoolean(SPLASH_KEY) ?: true
        installSplashScreen().setKeepOnScreenCondition { showSplash }

        lifecycleScope.launch {
            vm.hasRememberedDatabase
                .flowWithLifecycle(lifecycle, Lifecycle.State.STARTED)
                .collect {
                    delay(300)
                    showSplash = it != null
                }
        }
        WindowCompat.setDecorFitsSystemWindows(window, false)
        enableEdgeToEdge()
        setContent {
            GinaApp(vm)
        }

    }

    override fun onSaveInstanceState(outState: Bundle) {
        outState.putBoolean(SPLASH_KEY, false)
        super.onSaveInstanceState(outState)
    }

    companion object {
        const val SPLASH_KEY = "SPLASH_KEY"
    }
}
