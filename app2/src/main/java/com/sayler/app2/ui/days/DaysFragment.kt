package com.sayler.app2.ui.days

import android.content.Context
import android.os.Bundle
import android.view.LayoutInflater
import android.view.ViewGroup
import com.airbnb.mvrx.BaseMvRxFragment
import com.airbnb.mvrx.fragmentViewModel
import com.sayler.app2.R
import dagger.android.support.AndroidSupportInjection
import javax.inject.Inject

class DaysFragment : BaseMvRxFragment() {

    @Inject
    lateinit var viewModelFactory: DaysViewModel.Factory
    private val viewModel: DaysViewModel by fragmentViewModel()

    override fun onAttach(context: Context?) {
        AndroidSupportInjection.inject(this)
        super.onAttach(context)
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?) =
            inflater.inflate(R.layout.f_days, container, false)

    override fun invalidate() {
    }

}
