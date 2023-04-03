package com.sayler666.gina.quotes.api

import com.sayler666.gina.quotes.api.model.Quote
import retrofit2.http.GET

interface ZenQuotesService {
    companion object {
        const val BASE_URL = "https://zenquotes.io/api/"
    }

    @GET("today")
    suspend fun fetchToday(): List<Quote>
}
