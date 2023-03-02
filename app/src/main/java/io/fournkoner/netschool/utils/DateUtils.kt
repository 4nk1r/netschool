package io.fournkoner.netschool.utils

import android.content.res.Configuration
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import io.fournkoner.netschool.R
import java.text.SimpleDateFormat
import java.time.*
import java.time.format.DateTimeFormatter
import java.time.temporal.ChronoUnit
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

fun Long.formatDate(): String {
    return SimpleDateFormat("hh:mm dd.MM.yyyy", Locale.getDefault()).format(Date(this))
}

@Composable
fun Long.getMessageFormattedDate(): String {
    val today = LocalDateTime.now()
    val messageDate = Instant.ofEpochMilli(this).atZone(ZoneId.systemDefault()).toLocalDateTime()

    return when (ChronoUnit.DAYS.between(messageDate, today).toInt()) {
        0 -> {
            val configuration = LocalConfiguration.current
            val context = LocalContext.current

            val resources = remember(configuration, context) {
                context.createConfigurationContext(
                    Configuration(configuration).apply {
                        setLocale(Locale("ru"))
                    }
                ).resources
            }

            val minutesDiff = ChronoUnit.MINUTES.between(messageDate, today).toInt()
            val hoursDiff = ChronoUnit.HOURS.between(messageDate, today).toInt()

            return when {
                minutesDiff < 1 -> stringResource(R.string.mail_just_now)
                minutesDiff < 60 -> resources.getQuantityString(
                    R.plurals.mail_minutes,
                    minutesDiff,
                    minutesDiff
                )
                hoursDiff < 24 -> resources.getQuantityString(
                    R.plurals.mail_hours,
                    hoursDiff,
                    hoursDiff
                )
                else -> messageDate.toString()
            }
        }
        1 -> stringResource(R.string.mail_yesterday)
        in 3..364 -> messageDate.format(DateTimeFormatter.ofPattern("dd.MM"))
        else -> messageDate.format(DateTimeFormatter.ofPattern("dd.MM.yyyy"))
    }
}