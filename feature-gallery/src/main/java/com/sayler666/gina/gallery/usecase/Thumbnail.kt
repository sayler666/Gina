package com.sayler666.gina.gallery.usecase

import com.sayler666.domain.model.journal.Mood
import java.time.LocalDate

data class Thumbnail(val bytes: ByteArray, val id: Int, val aspectRatio: Float, val date: LocalDate, val mood: Mood, val content: String) {
    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (javaClass != other?.javaClass) return false

        other as Thumbnail

        if (!bytes.contentEquals(other.bytes)) return false
        if (id != other.id) return false
        if (aspectRatio != other.aspectRatio) return false
        if (date != other.date) return false

        return true
    }

    override fun hashCode(): Int {
        var result = bytes.contentHashCode()
        result = 31 * result + id
        result = 31 * result + aspectRatio.hashCode()
        result = 31 * result + date.hashCode()
        return result
    }
}
