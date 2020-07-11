package com.sayler.gina3

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.navigation.findNavController
import com.sayler.gina3.data.DataManager
import dagger.hilt.android.AndroidEntryPoint
import javax.inject.Inject

@AndroidEntryPoint
class MainActivity : AppCompatActivity() {

    private val navController by lazy { findNavController(R.id.nav_host_fragment) }

    @Inject
    lateinit var dataManager: DataManager

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        setupNavigation()
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

}