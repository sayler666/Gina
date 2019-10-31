package com.sayler.app2.day

import android.content.Context
import android.os.Bundle
import android.view.LayoutInflater
import android.view.ViewGroup
import com.airbnb.mvrx.BaseMvRxFragment
import com.sayler.app2.R
import dagger.android.support.AndroidSupportInjection

class DayFragment : BaseMvRxFragment() {
    override fun invalidate() {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    override fun onAttach(context: Context) {
        AndroidSupportInjection.inject(this)
        super.onAttach(context)
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?) =
            inflater.inflate(R.layout.f_day, container, false)

}
