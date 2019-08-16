package com.sayler.app2.mvrx

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.airbnb.epoxy.EpoxyRecyclerView
import com.airbnb.mvrx.BaseMvRxFragment

abstract class MvRxFragment : BaseMvRxFragment() {
    protected lateinit var recyclerView: EpoxyRecyclerView
    protected val epoxyController by lazy { epoxyController() }
    abstract val layoutResId: Int
    abstract val recyclerViewResId: Int

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? =
            inflater.inflate(layoutResId, container, false).apply {
                recyclerView = findViewById(recyclerViewResId)
                recyclerView.setController(epoxyController)
            }

    abstract fun epoxyController(): MvRxEpoxyController

    override fun invalidate() = recyclerView.requestModelBuild()
}
