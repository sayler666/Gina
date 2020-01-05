package com.sayler.app2.day

import android.content.Context
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.navigation.fragment.navArgs
import com.airbnb.mvrx.*
import com.sayler.app2.R
import dagger.android.support.AndroidSupportInjection
import kotlinx.android.synthetic.main.f_day.*
import kotlinx.coroutines.InternalCoroutinesApi
import javax.inject.Inject

@UseExperimental(InternalCoroutinesApi::class)
class DayFragment : BaseMvRxFragment() {
    val arguments: DayFragmentArgs by navArgs()

    @Inject
    lateinit var viewModelFactory: DayViewModel.Factory
    private val viewModel: DayViewModel by fragmentViewModel()

    override fun onAttach(context: Context) {
        AndroidSupportInjection.inject(this)
        Log.d(TAG, "Showing day ${arguments.dayId}")
        super.onAttach(context)
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?) =
            inflater.inflate(R.layout.f_day, container, false)

    override fun invalidate() {
        withState(viewModel) { state ->
            when (val day = state.day) {
                Uninitialized -> Log.d(TAG, "Uninitialized")
                is Loading -> Log.d(TAG, "Loading")
                is Success -> {
                    date.text = day().date.toString()
                    content.text = day().content
                }
                is Fail -> Log.d(TAG, "Fail: ${day.error}")
            }
        }
    }

    companion object {
        const val TAG = "DayFragment"
    }

}
