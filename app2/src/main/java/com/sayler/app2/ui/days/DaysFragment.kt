package com.sayler.app2.ui.days

import android.content.Context
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.airbnb.mvrx.BaseMvRxFragment
import com.airbnb.mvrx.Success
import com.airbnb.mvrx.fragmentViewModel
import com.airbnb.mvrx.withState
import com.sayler.app2.R
import com.sayler.app2.file.FileUtils
import com.sayler.data.settings.SettingsState
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
            viewModel.saveSettings(pathEditText.text.toString())
        }
        addDayButton.setOnClickListener {
            viewModel.addDay()
        }
        clearDaysButton.setOnClickListener {
            viewModel.clearDays()
        }
        choosePathButton.setOnClickListener {
            FileUtils.selectFileIntent(requireActivity(), REQUEST_CODE_SELECT_DB)
        }
    }

    override fun invalidate() {
        withState(viewModel) {
            //Log.d("DaysFragment: [Days]", it.days.toString())
            when (it.attachment) {
                is Success -> Log.d("DaysFragment: Attachments count:", it.attachment()?.size.toString())
            }
           // Log.d("DaysFragment: [Settings]", it.settingsState.toString())
            when (it.days) {
                is Success -> Log.d("DaysFragment: Days count:", it.days()?.size.toString())
            }
            when (it.settingsState) {
                is SettingsState.Set -> pathEditText.setText(it.settingsState.settingsData.databasePath)
            }
        }
    }


    companion object {
        const val REQUEST_CODE_SELECT_DB = 665
    }

}
