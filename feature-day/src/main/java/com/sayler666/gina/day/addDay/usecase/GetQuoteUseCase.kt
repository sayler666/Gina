package com.sayler666.gina.day.addDay.usecase

import com.sayler666.data.database.db.quotes.QuoteEntity
import com.sayler666.data.database.db.quotes.QuotesDatabaseProvider
import com.sayler666.data.database.db.quotes.returnWithQuotesDao
import com.sayler666.domain.model.quotes.Quote
import com.sayler666.gina.network.quotes.ZenQuotesService
import com.sayler666.gina.network.quotes.model.QuoteApiModel
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.withContext
import timber.log.Timber
import java.time.LocalDate
import javax.inject.Inject
import javax.inject.Singleton

interface GetQuoteUseCase {
    suspend fun getQuote(): Quote?
}

@Singleton
class GetQuoteUseCaseImpl @Inject constructor(
    private val quotesDatabaseProvider: QuotesDatabaseProvider,
    private val quotesService: ZenQuotesService,
    private val coroutineDispatcher: CoroutineDispatcher = Dispatchers.IO
) : GetQuoteUseCase {

    override suspend fun getQuote(): Quote? = withContext(coroutineDispatcher) {
        val todayDate = LocalDate.now()
        val latestQuote = quotesDatabaseProvider.returnWithQuotesDao { getLatestQuoteFlow().first() }
        when (latestQuote?.date) {
            todayDate -> latestQuote.toDomain()
            else -> {
                val newQuoteId = fetchAndSaveNewQuote(todayDate)
                if (newQuoteId == null) {
                    quotesDatabaseProvider.returnWithQuotesDao { getRandomQuote()?.toDomain() }
                } else {
                    quotesDatabaseProvider.returnWithQuotesDao { getLatestQuoteFlow().first()?.toDomain() }
                }
            }
        }
    }

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

    private fun QuoteEntity.toDomain() = Quote(quote = quote, author = author)
}
