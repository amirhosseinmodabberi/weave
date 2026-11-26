package com.shinewhite.weave.localization

import java.util.Calendar

data class JalaliDate(val year: Int, val month: Int, val day: Int) {
    companion object {
        private val gregorianMonthDays = intArrayOf(31, 28, 31, 30, 31, 30, 31, 31, 30, 31, 30, 31)
        private val jalaliMonthDays = intArrayOf(31, 31, 31, 31, 31, 31, 30, 30, 30, 30, 30, 29)

        fun fromCalendar(calendar: Calendar): JalaliDate = fromGregorian(
            year = calendar.get(Calendar.YEAR),
            month = calendar.get(Calendar.MONTH) + 1,
            day = calendar.get(Calendar.DAY_OF_MONTH)
        )

        // Works without java.time, which needs desugaring below API 26
        fun fromGregorian(year: Int, month: Int, day: Int): JalaliDate {
            val gy = year - 1600
            val gm = month - 1
            val gd = day - 1

            var gregorianDayNumber = 365 * gy + (gy + 3) / 4 - (gy + 99) / 100 + (gy + 399) / 400
            for (i in 0 until gm) gregorianDayNumber += gregorianMonthDays[i]
            val isLeapYear = (year % 4 == 0 && year % 100 != 0) || year % 400 == 0
            if (gm > 1 && isLeapYear) gregorianDayNumber += 1
            gregorianDayNumber += gd

            var jalaliDayNumber = gregorianDayNumber - 79
            val cycle = jalaliDayNumber / 12053
            jalaliDayNumber %= 12053

            var jalaliYear = 979 + 33 * cycle + 4 * (jalaliDayNumber / 1461)
            jalaliDayNumber %= 1461
            if (jalaliDayNumber >= 366) {
                jalaliYear += (jalaliDayNumber - 1) / 365
                jalaliDayNumber = (jalaliDayNumber - 1) % 365
            }

            var monthIndex = 0
            while (monthIndex < 11 && jalaliDayNumber >= jalaliMonthDays[monthIndex]) {
                jalaliDayNumber -= jalaliMonthDays[monthIndex]
                monthIndex++
            }
            return JalaliDate(jalaliYear, monthIndex + 1, jalaliDayNumber + 1)
        }
    }
}