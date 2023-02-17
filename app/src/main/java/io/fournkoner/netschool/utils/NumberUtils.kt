package io.fournkoner.netschool.utils

import kotlin.math.roundToInt

val Int.formattedShortString: String
    get() {
        check(this >= 0)
        val str = toString()
        return when (this) {
            in 0..999 -> str
            in 1_000..9_999 -> "${str[0]}${if (str[1] != '0') str[1] else ""}K"
            in 10_000..99_999 -> "${str.take(2)}${if (str[2] != '0') str[2] else ""}K"
            in 100_000..999_999 -> "0,${(str.take(2).toInt() / 10f).roundToInt().coerceAtMost(9)}M"
            in 1_000_000..9_999_000 -> "${str[0]}${if (str[1] != '0') str[1] else ""}M"
            in 10_000_000..99_999_000 -> "${str.take(2)}${if (str[2] != '0') str[2] else ""}M"
            in 100_000_000..999_999_000 -> "0,${(str.take(2).toInt() / 10f).roundToInt().coerceAtMost(9)}B"
            in 1_000_000_000..9_999_000_000 -> "${str[0]}${if (str[1] != '0') str[1] else ""}B"
            in 10_000_000_000..99_999_000_000 -> "${str.take(2)}${if (str[2] != '0') str[2] else ""}B"
            in 100_000_000_000..999_999_000_000 -> "${str.take(3)}B"
            else -> "WTF"
        }
    }