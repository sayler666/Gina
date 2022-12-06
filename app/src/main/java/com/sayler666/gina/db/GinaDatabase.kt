package com.sayler666.gina.db

import androidx.room.*
import kotlinx.coroutines.flow.Flow


@Database(entities = [Days::class], version = 1, exportSchema = false)
abstract class GinaDatabase : RoomDatabase() {
    abstract fun daysDao(): DaysDao
}

@Entity(tableName = "days")
data class Days(
    @ColumnInfo(name = "date", typeAffinity = ColumnInfo.INTEGER)
    val date: Long?,

    @ColumnInfo(name = "content", typeAffinity = ColumnInfo.TEXT)
    val content: String?,

    @PrimaryKey(autoGenerate = true)
    @ColumnInfo(name = "id", typeAffinity = ColumnInfo.INTEGER)
    val id: Int?,
)

@Dao
interface DaysDao {
    @Query("SELECT * FROM days")
    fun getDaysFlow(): Flow<List<Days>>

    @Query("SELECT * FROM days")
    fun getDays(): List<Days>
}
