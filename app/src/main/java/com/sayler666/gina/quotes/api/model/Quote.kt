package com.sayler666.gina.quotes.api.model

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class Quote(
    @SerialName("q")
    val quote: String,
    @SerialName("a")
    val author: String
)
