package com.sayler.monia.ui

import com.futuremind.recyclerviewfastscroll.viewprovider.ViewBehavior
import com.futuremind.recyclerviewfastscroll.viewprovider.VisibilityAnimationManager

/**
 * Created by Michal on 11/08/16.
 */
class DefaultBubbleBehavior(private val animationManager: VisibilityAnimationManager) : ViewBehavior {
    var onHandleVisibilityChangeListener: ((visible: Boolean) -> Unit)? = null

    override fun onHandleGrabbed() {
        animationManager.show()
        onHandleVisibilityChangeListener?.invoke(true)
    }

    override fun onHandleReleased() {
        animationManager.hide()
        onHandleVisibilityChangeListener?.invoke(false)
    }

    override fun onScrollStarted() {

    }

    override fun onScrollFinished() {

    }

}
