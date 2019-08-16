package com.sayler.app2.view

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.annotation.LayoutRes


fun ViewGroup.inflate(@LayoutRes layout: Int, attachToRoot: Boolean = true): View =
        LayoutInflater.from(context).inflate(layout, this, attachToRoot)
