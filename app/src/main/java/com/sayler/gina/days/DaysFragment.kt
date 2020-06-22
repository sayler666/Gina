package com.sayler.gina.entry

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.Observer
import com.sayler.gina.R
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class DaysFragment : Fragment() {

    private val daysViewModel by viewModels<DaysViewModel>()

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? = inflater.inflate(R.layout.days_fragment, container, false)

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        setupObservers()
        daysViewModel.updateTest()
    }

    private fun setupObservers() {
        with(daysViewModel) {
            test.observe(viewLifecycleOwner, Observer {
                Log.d("EntryFragment", "value $it")
            })
        }
    }
}