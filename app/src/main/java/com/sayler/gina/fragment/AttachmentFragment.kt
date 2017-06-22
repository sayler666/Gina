package com.sayler.gina.fragment

import android.os.Bundle
import android.support.v4.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.sayler.gina.R
import com.sayler.gina.util.ViewSliderCoordinator
import kotlinx.android.synthetic.main.f_attachment.*

/**
 * Created by sayler on 2017-06-23.
 *
 * Copyright 2017 MiQUiDO <http://www.miquido.com/>. All rights reserved.
 */
class AttachmentFragment : Fragment(), ViewSliderCoordinator.Slideable {

    override fun onCreateView(inflater: LayoutInflater?, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        val rootView = inflater?.inflate(R.layout.f_attachment, container, false)
        return rootView
    }

    override fun onSlide(progress: Float) {
        minimizedContainer.alpha = progress
        maximizedContainer.alpha = 1.0f - progress

        if (progress < 1) {
            maximizedContainer.visibility = View.VISIBLE
        } else if (progress == 1.0f) {
            maximizedContainer.visibility = View.GONE
        }
    }

}