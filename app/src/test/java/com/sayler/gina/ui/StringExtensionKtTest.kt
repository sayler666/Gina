package com.sayler.gina.ui

import org.assertj.core.api.Assertions.assertThat
import org.junit.Test

/**
 * Created by sayler on 2017-12-09.
 *
 */
class StringExtensionKtTest {
    @Test
    fun test_truncateTo_3_long_enough() {
        val stringToTruncate = "Truncate this text"
        val truncated = stringToTruncate.truncateTo(3, "...")
        assertThat(truncated).isEqualTo("Tru...")
    }

    @Test
    fun test_truncateTo_18_equal_length() {
        val stringToTruncate = "Truncate this text"
        val truncated = stringToTruncate.truncateTo(18, "...")
        assertThat(truncated).isEqualTo("Truncate this text")
    }

    @Test
    fun test_truncateTo_20_longer() {
        val stringToTruncate = "Truncate this text"
        val truncated = stringToTruncate.truncateTo(18, "...")
        assertThat(truncated).isEqualTo("Truncate this text")
    }

    @Test
    fun test_truncateTo_17_shorter_but_ellipsis() {
        val stringToTruncate = "Truncate this text"
        val truncated = stringToTruncate.truncateTo(17, "...")
        assertThat(truncated).isEqualTo("Truncate this text")
    }

    @Test
    fun test_truncateTo_15_shorter_but_ellipsis() {
        val stringToTruncate = "Truncate this text"
        val truncated = stringToTruncate.truncateTo(15, "...")
        assertThat(truncated).isEqualTo("Truncate this text")
    }

    @Test
    fun test_truncateTo_15_shorter() {
        val stringToTruncate = "Truncate this text"
        val truncated = stringToTruncate.truncateTo(15, ".")
        assertThat(truncated).isEqualTo("Truncate this t.")
    }

}