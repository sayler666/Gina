package com.sayler666.data.database.db.quotes

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.Query
import kotlinx.coroutines.flow.Flow

@Dao
interface QuotesDao {
    @Query("SELECT * FROM quotes ORDER by id DESC LIMIT 1")
    fun getLatestQuoteFlow(): Flow<QuoteEntity?>

    @Query("SELECT * FROM quotes  ORDER BY RANDOM() LIMIT 1")
    suspend fun getRandomQuote(): QuoteEntity?

    @Insert
    suspend fun addQuote(quote: QuoteEntity): Long

    @Delete
    suspend fun deleteQuote(quote: QuoteEntity): Int
}
