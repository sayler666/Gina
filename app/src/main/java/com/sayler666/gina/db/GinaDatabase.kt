package com.sayler666.gina.db

import androidx.room.*
import kotlinx.coroutines.flow.Flow


@Database(entities = [Day::class, Attachment::class], version = 1, exportSchema = false)
abstract class GinaDatabase : RoomDatabase() {
    abstract fun daysDao(): DaysDao
}

@Entity(tableName = "days")
data class Day(
    @ColumnInfo(name = "date", typeAffinity = ColumnInfo.INTEGER)
    val date: Long?,

    @ColumnInfo(name = "content", typeAffinity = ColumnInfo.TEXT)
    val content: String?,

    @PrimaryKey(autoGenerate = true)
    @ColumnInfo(name = "id", typeAffinity = ColumnInfo.INTEGER)
    val id: Int?,
)

@Entity(tableName = "attachments", foreignKeys = [ForeignKey(
    entity = Day::class,
    parentColumns = arrayOf("id"),
    childColumns = arrayOf("days_id"),
    onDelete = ForeignKey.CASCADE
)])
data class Attachment(
    @PrimaryKey(autoGenerate = true)
    @ColumnInfo(name = "id", typeAffinity = ColumnInfo.INTEGER)
    val id: Int?,

    @ColumnInfo(name = "days_id", typeAffinity = ColumnInfo.INTEGER)
    val dayId: Int?,

    @ColumnInfo(name = "file", typeAffinity = ColumnInfo.BLOB)
    val content: ByteArray?,

    @ColumnInfo(name = "mime_type", typeAffinity = ColumnInfo.TEXT)
    val mimeType: String?,
)

data class DayWithAttachment(
    @Embedded val day: Day,
    @Relation(
        parentColumn = "id",
        entityColumn = "days_id"
    )
    val attachments: List<Attachment>
)

@Dao
interface DaysDao {
    @Query("SELECT * FROM days ORDER by date DESC")
    fun getDaysFlow(): Flow<List<Day>>

    @Query("SELECT * FROM days WHERE id = :id")
    fun getDayFlow(id: Int): Flow<DayWithAttachment>

}
