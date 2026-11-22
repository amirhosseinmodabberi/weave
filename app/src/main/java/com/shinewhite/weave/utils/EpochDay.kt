package com.shinewhite.weave.utils

import java.util.TimeZone

object EpochDay {
    private const val MILLIS_PER_DAY = 86_400_000L

    fun today(): Long {
        val now = System.currentTimeMillis()
        return (now + TimeZone.getDefault().getOffset(now)) / MILLIS_PER_DAY
    }

    // 1970-01-01 was a Thursday, so shifting by 3 makes Monday index 0
    fun dayIndex(epochDay: Long): Int = Math.floorMod(epochDay + 3, 7L).toInt()

    fun weekStart(epochDay: Long): Long = epochDay - dayIndex(epochDay)
}