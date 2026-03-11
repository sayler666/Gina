package com.sayler666.gina.quotes

import com.sayler666.data.database.db.quotes.QuoteEntity
import com.sayler666.data.database.db.quotes.QuotesDatabaseProvider
import com.sayler666.data.database.db.quotes.returnWithQuotesDao
import com.sayler666.data.database.db.quotes.withQuotesDao
import com.sayler666.gina.quotes.api.ZenQuotesService
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.flowOn
import timber.log.Timber
import java.time.LocalDate
import javax.inject.Inject
import javax.inject.Singleton
import com.sayler666.gina.quotes.api.model.Quote as QuoteApiModel

@Singleton
class QuotesRepository @Inject constructor(
    private val quotesDatabaseProvider: QuotesDatabaseProvider,
    private val quotesService: ZenQuotesService,
    private val coroutineDispatcher: CoroutineDispatcher = Dispatchers.IO
) : com.sayler666.gina.day.addDay.usecase.DayQuoteProvider {

    override fun latestTodayQuoteFlow(): Flow<QuoteEntity?> = flow {
        val todayDate = LocalDate.now()
        quotesDatabaseProvider.withQuotesDao {
            getLatestQuoteFlow()
                .collect { quote ->
                    when (quote?.date) {
                        todayDate -> emit(quote)
                        else -> with(fetchAndSaveNewQuote(todayDate)) {
                            if (this == null) emit(getRandomQuote())
                        }
                    }
                }
        }
    }
        .distinctUntilChanged()
        .flowOn(coroutineDispatcher)

    private suspend fun fetchAndSaveNewQuote(todayDate: LocalDate): Long? = try {
        val dbQuote = mapToDbModel(quotesService.fetchToday().first(), todayDate)
        val newQuoteId = quotesDatabaseProvider.returnWithQuotesDao { addQuote(dbQuote) }
        newQuoteId
    } catch (e: Exception) {
        Timber.e(e)
        null
    }

    private fun mapToDbModel(quoteApiModel: QuoteApiModel, date: LocalDate) =
        QuoteEntity(null, quoteApiModel.quote, quoteApiModel.author, date)
}
