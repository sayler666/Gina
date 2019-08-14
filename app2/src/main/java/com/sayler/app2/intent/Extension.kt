package com.sayler.app2.intent

import android.content.Intent
import com.sayler.app2.intent.Path.NotSet
import com.sayler.app2.intent.Path.Set


sealed class Path {
    object NotSet : Path()
    data class Set(val path: String) : Path()
}

fun Intent?.getPath(): Path = this?.data?.path?.let { Set(it) } ?: NotSet
