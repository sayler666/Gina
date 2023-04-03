package com.sayler666.gina.quotes.db

import androidx.room.ColumnInfo
import androidx.room.Dao
import androidx.room.Database
import androidx.room.Delete
import androidx.room.Entity
import androidx.room.Insert
import androidx.room.PrimaryKey
import androidx.room.Query
import androidx.room.RoomDatabase
import kotlinx.coroutines.flow.Flow

@Database(
    entities = [Quote::class],
    version = 1,
    exportSchema = false
)
abstract class QuotesDatabase : RoomDatabase() {
    abstract fun quotesDao(): QuotesDao
}

@Entity(tableName = "quotes")
data class Quote(
    @PrimaryKey(autoGenerate = true)
    @ColumnInfo(name = "id", typeAffinity = ColumnInfo.INTEGER)
    val id: Int?,

    @ColumnInfo(name = "quote", typeAffinity = ColumnInfo.TEXT)
    val quote: String,

    @ColumnInfo(name = "author", typeAffinity = ColumnInfo.TEXT)
    val author: String,

    @ColumnInfo(name = "date", typeAffinity = ColumnInfo.INTEGER)
    val date: Long,
)

@Dao
interface QuotesDao {
    @Query("SELECT * FROM quotes ORDER by id DESC LIMIT 1")
    fun getLatestQuoteFlow(): Flow<Quote?>

    @Query("SELECT * FROM quotes  ORDER BY RANDOM() LIMIT 1")
    suspend fun getRandomQuote(): Quote?

    @Insert
    suspend fun addQuote(quote: Quote): Long

    @Delete
    suspend fun deleteQuote(quote: Quote): Int
}
