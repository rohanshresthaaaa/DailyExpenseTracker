package com.rohan.dailyexpensetracker.utils

import java.util.Calendar

object DateRanges {

    private const val DAY_MS = 24L * 60L * 60L * 1000L

    fun todayRange(): Pair<Long, Long> {
        val cal = Calendar.getInstance()
        setStart(cal)
        val start = cal.timeInMillis
        return start to (start + DAY_MS - 1)
    }

    fun weekRange(): Pair<Long, Long> {
        val cal = Calendar.getInstance()
        cal.firstDayOfWeek = Calendar.SUNDAY
        cal.set(Calendar.DAY_OF_WEEK, cal.firstDayOfWeek)
        setStart(cal)
        val start = cal.timeInMillis
        return start to (start + 7 * DAY_MS - 1)
    }

    private fun setStart(cal: Calendar) {
        cal.set(Calendar.HOUR_OF_DAY, 0)
        cal.set(Calendar.MINUTE, 0)
        cal.set(Calendar.SECOND, 0)
        cal.set(Calendar.MILLISECOND, 0)
    }
}