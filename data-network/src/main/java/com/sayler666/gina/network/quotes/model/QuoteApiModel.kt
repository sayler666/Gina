package com.sayler666.gina.network.quotes.model

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class QuoteApiModel(
    @SerialName("q")
    val quote: String,
    @SerialName("a")
    val author: String
)
