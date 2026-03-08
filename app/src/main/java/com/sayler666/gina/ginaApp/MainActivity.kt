package com.sayler666.gina.ginaApp

import android.content.Intent
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.viewModels
import androidx.core.content.pm.ShortcutManagerCompat
import androidx.core.splashscreen.SplashScreen.Companion.installSplashScreen
import androidx.core.view.WindowCompat
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.flowWithLifecycle
import androidx.lifecycle.lifecycleScope
import com.sayler666.gina.di.EntryProviderInstaller
import com.sayler666.gina.di.NavEntryFallback
import com.sayler666.gina.ginaApp.navigation.addDayShortcut
import com.sayler666.gina.ginaApp.viewModel.GinaMainViewModel
import com.sayler666.gina.navigation.Route
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import javax.inject.Inject

@AndroidEntryPoint
class MainActivity : ComponentActivity() {

    private val vm: GinaMainViewModel by viewModels()

    @Inject
    lateinit var installers: Set<@JvmSuppressWildcards EntryProviderInstaller>

    @Inject
    lateinit var fallback: @JvmSuppressWildcards NavEntryFallback

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

        ShortcutManagerCompat.removeAllDynamicShortcuts(baseContext)
        ShortcutManagerCompat.pushDynamicShortcut(baseContext, addDayShortcut(baseContext))

        if (savedInstanceState == null) handleDeepLinkIntent(intent)

        WindowCompat.setDecorFitsSystemWindows(window, false)
        enableEdgeToEdge()
        setContent {
            GinaApp(vm, installers, fallback)
        }
    }

    override fun onNewIntent(intent: Intent) {
        super.onNewIntent(intent)
        handleDeepLinkIntent(intent)
    }

    private fun handleDeepLinkIntent(intent: Intent) {
        if (intent.action == Intent.ACTION_VIEW &&
            intent.data?.scheme == "gina" &&
            intent.data?.host == "add_day"
        ) {
            vm.setDeepLink(Route.AddDay())
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
