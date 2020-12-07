package com.awesomeworktimetracker2000.awesomeworktimetrackermobile.utils

import java.time.LocalDateTime
import java.time.OffsetDateTime
import java.time.ZoneId
import java.time.ZoneOffset
import java.time.format.DateTimeFormatter
import java.util.*

object DateUtils {
    val localOffset: ZoneOffset = OffsetDateTime.now().offset
    val ZoneId: ZoneId = java.time.ZoneId.systemDefault()
    val isoDateFormatter: DateTimeFormatter = DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm:ssxxx")

    /**
     * Convert OffsetDateTime to time string, e.g. 11:11
     */
    fun convertOffsetDateTimeToLocalTimeString(dateTime: OffsetDateTime): String {
        val localDateTime = dateTime.withOffsetSameInstant(localOffset)

        return DateTimeFormatter.ofPattern("HH:mm").format(localDateTime)
    }

    /**
     * Get date object + or - days from today.
     *
     * e.g.
     ```
        val yesterday = todayAddDays(-1)
     ```
     */
    fun todayAddDays(days: Int): Date {
        val calendar = Calendar.getInstance()
        calendar.add(Calendar.DAY_OF_YEAR, days)

        return calendar.time
    }
    fun convertLocalDateTimeToString(localDateTime: LocalDateTime): String {
        return DateTimeFormatter.ofPattern("dd.MM.yyyy HH.mm").format(localDateTime)
    }

    fun convertOffsetDateTimeToString(dateTime: OffsetDateTime): String {
        return DateTimeFormatter.ofPattern("dd.MM.yyyy HH.mm").format(dateTime)
    }
}