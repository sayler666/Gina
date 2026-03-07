package com.sayler666.gina.addDay.usecase

import com.sayler666.data.database.db.quotes.QuoteEntity
import kotlinx.coroutines.flow.Flow

interface DayQuoteProvider {
    fun latestTodayQuoteFlow(): Flow<QuoteEntity?>
}
