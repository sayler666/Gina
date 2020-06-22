package com.sayler.gina3.entry

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.Observer
import androidx.navigation.fragment.findNavController
import com.sayler.gina3.R
import com.sayler.gina3.data.OpenDatabase
import com.sayler.gina3.data.OpenDatabase.Companion.ANY_FILE
import com.sayler.gina3.entry.EntryFragmentDirections.Companion.toDaysFragment
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.android.synthetic.main.entry_fragment.*

@AndroidEntryPoint
class EntryFragment : Fragment() {

    private val entryViewModel by viewModels<EntryViewModel>()

    private val opedDatabaseLauncher = registerForActivityResult(
        OpenDatabase(), { dbPath -> dbPath?.let { entryViewModel.openDb(dbPath) } }
    )
    private val databaseOpenedObserver = Observer<Boolean> { open ->
        if (open) findNavController().navigate(toDaysFragment())
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? = inflater.inflate(R.layout.entry_fragment, container, false)

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        setupViews()
        setupObservers()
    }

    private fun setupViews() {
        select_file_button.setOnClickListener { opedDatabaseLauncher.launch(ANY_FILE) }
    }

    private fun setupObservers() = with(entryViewModel) {
        databaseOpened.observe(viewLifecycleOwner, databaseOpenedObserver)
    }
}