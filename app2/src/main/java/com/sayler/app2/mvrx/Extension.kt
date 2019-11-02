package com.sayler.app2.mvrx

import androidx.fragment.app.Fragment
import com.airbnb.mvrx.FragmentViewModelContext
import com.airbnb.mvrx.ViewModelContext


inline fun <reified T : Fragment> ViewModelContext.fragment(): T =
        (this as FragmentViewModelContext).fragment()

