package com.sayler666.gina.network.quotes

import com.sayler666.gina.network.quotes.model.QuoteApiModel
import retrofit2.http.GET

interface ZenQuotesService {
    companion object {
        const val BASE_URL = "https://zenquotes.io/api/"
    }

    @GET("today")
    suspend fun fetchToday(): List<QuoteApiModel>
}
