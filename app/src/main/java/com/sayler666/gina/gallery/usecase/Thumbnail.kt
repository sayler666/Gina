package com.sayler666.gina.gallery.usecase

data class Thumbnail(val bytes: ByteArray, val id: Int) {
    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (javaClass != other?.javaClass) return false

        other as Thumbnail

        if (!bytes.contentEquals(other.bytes)) return false
        if (id != other.id) return false

        return true
    }

    override fun hashCode(): Int {
        var result = bytes.contentHashCode()
        result = 31 * result + id
        return result
    }
}
