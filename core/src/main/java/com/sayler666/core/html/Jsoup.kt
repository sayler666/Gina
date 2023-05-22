package com.sayler666.core.html

import org.jsoup.Jsoup

fun String.getTextWithoutHtml(): String =
    if (containsHtml()) Jsoup.parse(this).text() else this

fun String.containsHtml(): Boolean = contains("</")
