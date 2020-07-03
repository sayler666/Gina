package com.sayler.monia.ui

import android.content.Context
import android.support.design.widget.CoordinatorLayout
import android.support.design.widget.FloatingActionButton
import android.support.design.widget.Snackbar
import android.util.AttributeSet
import android.view.View


class FloatingActionButtonBehavior : CoordinatorLayout.Behavior<FloatingActionButton> {
    constructor() : super()
    constructor(context: Context?, attrs: AttributeSet?) : super(context, attrs)

    override fun layoutDependsOn(parent: CoordinatorLayout?, child: FloatingActionButton?, dependency: View?): Boolean {
        return dependency is Snackbar.SnackbarLayout
    }

    override fun onDependentViewChanged(parent: CoordinatorLayout?, child: FloatingActionButton?, dependency: View?): Boolean {
        val translationY = Math.min(0.0, (dependency!!.translationY - dependency.height).toDouble())
        child?.translationY = translationY.toFloat()
        return true
    }
}