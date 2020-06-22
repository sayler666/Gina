package com.sayler.gina.entry

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.Observer
import com.sayler.gina.R
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class EntryFragment : Fragment() {

    private val entryViewModel by activityViewModels<EntryViewModel>()

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? = inflater.inflate(R.layout.entry_fragment, container, false)

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        setupObservers()
        entryViewModel.updateTest()
    }

    private fun setupObservers() {
        with(entryViewModel) {
            test.observe(viewLifecycleOwner, Observer {
                Log.d("EntryFragment", "value $it")
            })
        }
    }
}