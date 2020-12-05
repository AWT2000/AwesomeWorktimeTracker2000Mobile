package com.awesomeworktimetracker2000.awesomeworktimetrackermobile.utils

import java.time.OffsetDateTime
import java.time.ZoneId
import java.time.ZoneOffset
import java.time.format.DateTimeFormatter

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
}