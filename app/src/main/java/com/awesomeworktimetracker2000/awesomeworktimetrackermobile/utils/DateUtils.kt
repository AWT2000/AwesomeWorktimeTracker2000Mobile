package com.awesomeworktimetracker2000.awesomeworktimetrackermobile.utils

import java.time.OffsetDateTime
import java.time.ZoneOffset
import java.time.format.DateTimeFormatter

object DateUtils {
    val localOffset: ZoneOffset = OffsetDateTime.now().offset
    val isoDateFormatter: DateTimeFormatter = DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm:ssxxx")
}