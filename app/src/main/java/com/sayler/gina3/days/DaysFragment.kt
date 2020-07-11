package com.sayler.gina3.entry

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.Observer
import com.sayler.data.entity.Day
import com.sayler.gina3.R
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.android.synthetic.main.toolbar.*

@AndroidEntryPoint
class DaysFragment : Fragment() {

    private val daysViewModel by viewModels<DaysViewModel>()

    private val daysObserver = Observer<List<Day>> {
        Log.d("DaysFragment", "value $it")
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? = inflater.inflate(R.layout.days_fragment, container, false)

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        setupObservers()
        setupToolbars()
    }

    private fun setupToolbars() {
        toolbar.setTitle(R.string.app_name)
    }

    private fun setupObservers() = with(daysViewModel) {
        days.observe(viewLifecycleOwner, daysObserver)
    }
}