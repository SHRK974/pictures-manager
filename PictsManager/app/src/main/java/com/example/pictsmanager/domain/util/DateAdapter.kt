package com.example.pictsmanager.domain.util

import com.squareup.moshi.FromJson
import com.squareup.moshi.ToJson
import java.text.SimpleDateFormat
import java.time.LocalDate
import java.time.format.DateTimeFormatter
import java.util.*

class DateAdapter {
    private val format = "yyyy-MM-dd'T'HH:mm:ss.SSS'Z'"
    private val utcTimeZone = TimeZone.getTimeZone("UTC")

    @FromJson
    fun fromJson(json: String): Date {
        val calendar = Calendar.getInstance(utcTimeZone)
        val formatter = SimpleDateFormat(format, Locale.US)
        formatter.calendar = calendar
        val date = formatter.parse(json)
        calendar.timeInMillis = date!!.time
        return calendar.time
    }

    @ToJson
    fun toJson(value: Date): String {
        val calendar = Calendar.getInstance(utcTimeZone)
        calendar.time = value
        val formatter = SimpleDateFormat(format, Locale.US)
        formatter.calendar = calendar
        return formatter.format(value)
    }

    companion object {
        fun formatDate(date: LocalDate): String {
            val today = LocalDate.now()
            val yesterday = today.minusDays(1)

            return when {
                date == today -> "Today"
                date == yesterday -> "Yesterday"
                date.isAfter(yesterday) && date.isBefore(today) -> date.format(DateTimeFormatter.ofPattern("EEEE"))
                else -> date.format(DateTimeFormatter.ofPattern("dd MMM yyyy"))
            }
        }
    }
}