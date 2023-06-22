package mood

enum class Mood(val numberValue: Int) {
    EMPTY(Int.MIN_VALUE),
    BAD(-2),
    LOW(-1),
    NEUTRAL(0),
    GOOD(1),
    SUPERB(2),
    AWESOME(3);

    companion object {
        fun Int?.mapToMood() = when (this) {
            -2 -> BAD
            -1 -> LOW
            0 -> NEUTRAL
            1 -> GOOD
            2 -> SUPERB
            3 -> AWESOME
            else -> EMPTY
        }

        fun valuesWithoutEmpty() = Mood.values().toMutableList()
            .also { it.remove(EMPTY) }
    }
}
