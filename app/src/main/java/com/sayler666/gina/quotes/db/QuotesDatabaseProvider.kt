package com.sayler666.gina.quotes.db

import android.app.Application
import androidx.room.Room
import timber.log.Timber

class QuotesDatabaseProvider(
    private val application: Application
) {
    private var INSTANCE: QuotesDatabase? = null

    fun getDB(): QuotesDatabase? {
        try {
            if (INSTANCE == null) {
                synchronized(this) {
                    INSTANCE =
                        Room.databaseBuilder(application, QuotesDatabase::class.java, "Quotes")
                            .build()
                }
            }
        } catch (e: Exception) {
            Timber.e(e, "Error opening DB")
            return null
        }
        return INSTANCE
    }
}

suspend fun QuotesDatabaseProvider.withQuotesDao(action: suspend QuotesDao.() -> Unit) {
    getDB()?.quotesDao()?.action()
}

suspend fun <T> QuotesDatabaseProvider.returnWithQuotesDao(action: suspend QuotesDao.() -> T): T? {
    return getDB()?.quotesDao()?.action()
}
