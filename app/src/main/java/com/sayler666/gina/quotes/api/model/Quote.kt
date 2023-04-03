package com.sayler666.gina.quotes.api.model

import com.squareup.moshi.Json
import com.squareup.moshi.JsonClass

@JsonClass(generateAdapter = true)
data class Quote(
    @Json(name = "q")
    val quote: String,
    @Json(name = "a")
    val author: String
)
