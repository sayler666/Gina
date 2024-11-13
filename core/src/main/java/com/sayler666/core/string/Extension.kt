package com.sayler666.core.string

import org.jsoup.Jsoup

fun String.getTextWithoutHtml(): String =
    if (containsHtml()) Jsoup.parse(this).text() else this

fun String.containsHtml(): Boolean = contains("<")

fun String.countWordsAndChars(): Pair<Int, Int> {
    val wordRegex = Regex("\\b\\w+\\b")
    val charRegex = Regex("\\S")

    val wordCount = wordRegex.findAll(this).count()
    val charCount = charRegex.findAll(this).count()

    return Pair(wordCount, charCount)
}
