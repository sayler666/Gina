package com.sayler.app2.ui.days

import android.content.Context
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.airbnb.mvrx.BaseMvRxFragment
import com.airbnb.mvrx.fragmentViewModel
import com.airbnb.mvrx.withState
import com.sayler.app2.R
import dagger.android.support.AndroidSupportInjection
import kotlinx.android.synthetic.main.f_days.*
import javax.inject.Inject

class DaysFragment : BaseMvRxFragment() {

    @Inject
    lateinit var viewModelFactory: DaysViewModel.Factory
    private val viewModel: DaysViewModel by fragmentViewModel()

    override fun onAttach(context: Context) {
        AndroidSupportInjection.inject(this)
        super.onAttach(context)
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?) =
            inflater.inflate(R.layout.f_days, container, false)

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        saveSettingsButton.setOnClickListener {
            viewModel.saveSettings("path")
        }
        readSettingsButton.setOnClickListener {
            viewModel.readSettings()
        }

        addDayButton.setOnClickListener {
            viewModel.addDay()
        }
        readDayButton.setOnClickListener {
            viewModel.readDays()
        }
    }

    override fun invalidate() {
        withState(viewModel) {
            Log.d("DaysFragment", it.toString())
        }
    }

}
