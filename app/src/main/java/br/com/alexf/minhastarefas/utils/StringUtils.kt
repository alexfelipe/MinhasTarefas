package br.com.alexf.minhastarefas.utils

import kotlinx.datetime.Instant
import kotlinx.datetime.LocalDate
import kotlinx.datetime.TimeZone
import kotlinx.datetime.format
import kotlinx.datetime.format.FormatStringsInDatetimeFormats
import kotlinx.datetime.format.byUnicodePattern
import kotlinx.datetime.toLocalDateTime

@OptIn(FormatStringsInDatetimeFormats::class)
fun Long?.toBrazilianDateFormat(): String? = this?.let { date ->
    Instant.fromEpochMilliseconds(date)
        .toLocalDateTime(TimeZone.UTC)
        .date
        .format(LocalDate.Format { byUnicodePattern("dd/MM/yyyy") })
}
