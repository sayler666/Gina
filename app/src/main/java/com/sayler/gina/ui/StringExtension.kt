package com.sayler.gina.ui

/**
 * Created by sayler on 07.06.2017.
 */
fun String.truncateTo(maxLength: Int, ellipsis: String): String {
    val contentShort: String
    if (this.length > maxLength) {
        contentShort = this.substring(0, maxLength) + ellipsis
    } else {
        contentShort = this
    }
    return contentShort
}