package com.sayler.monia.ui

/**
 * Created by sayler on 07.06.2017.
 */
fun String.truncateTo(maxLength: Int, ellipsis: String): String {
    return if (length > maxLength + ellipsis.length) {
        substring(0, maxLength) + ellipsis
    } else {
        this
    }
}