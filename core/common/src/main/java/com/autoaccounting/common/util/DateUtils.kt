package com.autoaccounting.common.util

import java.time.LocalDate
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter

object DateUtils {
    private val fullFormatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss")
    private val dateFormatter = DateTimeFormatter.ofPattern("yyyy-MM-dd")
    private val displayFormatter = DateTimeFormatter.ofPattern("MM月dd日")

    fun formatFull(dateTime: LocalDateTime): String = dateTime.format(fullFormatter)
    fun formatDate(dateTime: LocalDateTime): String = dateTime.format(dateFormatter)
    fun formatDisplay(dateTime: LocalDateTime): String = dateTime.format(displayFormatter)
    fun formatDisplay(date: LocalDate): String = date.format(displayFormatter)
}
