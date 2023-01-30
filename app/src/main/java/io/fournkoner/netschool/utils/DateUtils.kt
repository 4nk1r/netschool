package io.fournkoner.netschool.utils

import java.text.SimpleDateFormat
import java.time.DayOfWeek
import java.time.LocalDate
import java.time.temporal.TemporalAdjusters
import java.util.*

val currentWeekStart: String
    get() = LocalDate.now().with(TemporalAdjusters.previousOrSame(DayOfWeek.MONDAY)).toString()

val currentWeekEnd: String
    get() = LocalDate.now().with(TemporalAdjusters.nextOrSame(DayOfWeek.SUNDAY)).toString()

fun getWeekStartFromCurrent(offset: Int): String {
    if (offset == 0) return currentWeekStart

    return LocalDate.now()
        .run {
            if (offset > 0) {
                plusWeeks(offset.toLong())
            } else {
                minusWeeks(-offset.toLong())
            }
        }
        .with(TemporalAdjusters.previousOrSame(DayOfWeek.MONDAY))
        .toString()
}

fun getWeekEndFromCurrent(offset: Int): String {
    if (offset == 0) return currentWeekEnd

    return LocalDate.now()
        .run {
            if (offset > 0) {
                plusWeeks(offset.toLong())
            } else {
                minusWeeks(-offset.toLong())
            }
        }
        .with(TemporalAdjusters.nextOrSame(DayOfWeek.SUNDAY))
        .toString()
}

fun String.getFormattedTime(pattern: String): String {
    val numbers = substringBefore('T').split('-').map { it.toInt() }
    return SimpleDateFormat(pattern, Locale("ru"))
        .format(
            Calendar.getInstance().apply {
                set(numbers[0], numbers[1] - 1, numbers[2])
            }.time
        )
        .run { "${this.first().uppercaseChar()}${this.drop(1)}" }
}