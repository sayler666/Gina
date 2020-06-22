package com.sayler.gina

import android.os.Bundle
import android.util.Log
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.Observer
import androidx.navigation.findNavController
import com.sayler.gina.data.DataManager
import com.sayler.gina.entry.EntryViewModel
import dagger.hilt.android.AndroidEntryPoint
import javax.inject.Inject

@AndroidEntryPoint
class MainActivity : AppCompatActivity() {

    private val entryViewModel by viewModels<EntryViewModel>()
    private val navController by lazy { findNavController(R.id.nav_host_fragment) }

    @Inject
    lateinit var dataManager: DataManager

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        setupNavigation()
        setupObservers()
    }

    private fun setupNavigation() {
        navController.graph = navController.navInflater.inflate(R.navigation.main_navigation_graph)
            .apply {
                startDestination = when (dataManager.isDbOpen()) {
                    false -> R.id.entryFragment
                    true -> R.id.daysFragment
                }
            }
    }

    private fun setupObservers() {
        with(entryViewModel) {
            test.observe(this@MainActivity, Observer {
                Log.d("MainActivity", "value $it")
            })
        }
    }
}