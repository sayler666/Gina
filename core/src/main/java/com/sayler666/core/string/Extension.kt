package com.sayler666.core.string

import org.jsoup.Jsoup

fun String.getTextWithoutHtml(): String =
    if (containsHtml()) Jsoup.parse(this).text() else this

fun String.containsHtml(): Boolean = contains("<")

fun String.scrambleText(shift: Int = 13): String =
    map { char ->
        when (char) {
            in 'a'..'z' -> 'a' + (char - 'a' + shift) % 26
            in 'A'..'Z' -> 'A' + (char - 'A' + shift) % 26
            else -> char
        }
    }.joinToString("")

fun String.countWordsAndChars(): Pair<Int, Int> {
    val wordRegex = Regex("\\b\\w+\\b")
    val charRegex = Regex("\\S")

    val wordCount = wordRegex.findAll(this).count()
    val charCount = charRegex.findAll(this).count()

    return Pair(wordCount, charCount)
}
