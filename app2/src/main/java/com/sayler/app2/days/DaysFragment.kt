package com.sayler.app2.days

import android.content.Context
import android.os.Bundle
import android.util.Log
import android.view.View
import androidx.navigation.fragment.findNavController
import com.airbnb.mvrx.Success
import com.airbnb.mvrx.fragmentViewModel
import com.sayler.app2.R
import com.sayler.app2.file.FileUtils
import com.sayler.app2.mvrx.MvRxFragment
import com.sayler.app2.mvrx.viewModelController
import com.sayler.app2.ui.days.view.dayView
import dagger.android.support.AndroidSupportInjection
import kotlinx.android.synthetic.main.f_days.*
import javax.inject.Inject

class DaysFragment : MvRxFragment() {

    @Inject
    lateinit var viewModelFactory: DaysViewModel.Factory
    private val viewModel: DaysViewModel by fragmentViewModel()
    override val layoutResId = R.layout.f_days
    override val recyclerViewResId = R.id.daysRecyclerView

    override fun onAttach(context: Context) {
        AndroidSupportInjection.inject(this)
        super.onAttach(context)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        addDayButton.setOnClickListener {
            viewModel.addDay()
        }
        choosePathButton.setOnClickListener {
            FileUtils.selectFileIntent(requireActivity(), REQUEST_CODE_SELECT_DB)
        }
    }

    override fun epoxyController() = viewModelController(viewModel) { state ->
        when (state.days) {
            is Success -> state.days()?.forEach { day ->
                dayView {
                    id(day.id)
                    title(day.content)
                    clickListener { view ->
                        Log.d(TAG, "Open day ${day.id}")
                        findNavController().navigate(DaysFragmentDirections.openDay(day.id))
                    }
                }
            }
        }
    }

    companion object {
        const val REQUEST_CODE_SELECT_DB = 665
        const val TAG = "DaysFragment"
    }

}
