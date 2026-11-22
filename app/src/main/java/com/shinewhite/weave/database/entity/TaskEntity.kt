package com.shinewhite.weave.database.entity

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "tasks")
data class TaskEntity(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val title: String,
    val category: TaskCategory,
    // Bit 0 = Monday ... bit 6 = Sunday
    val activeDaysMask: Int,
    // Minutes since midnight, null when the task has no fixed time
    val timeMinutes: Int? = null
) {
    fun isActiveOn(dayIndex: Int): Boolean = (activeDaysMask and (1 shl dayIndex)) != 0
}