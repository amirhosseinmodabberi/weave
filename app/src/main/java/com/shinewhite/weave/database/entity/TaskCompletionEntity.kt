package com.shinewhite.weave.database.entity

import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.Index

@Entity(
    tableName = "task_completions",
    primaryKeys = ["taskId", "epochDay"],
    foreignKeys = [
        ForeignKey(
            entity = TaskEntity::class,
            parentColumns = ["id"],
            childColumns = ["taskId"],
            onDelete = ForeignKey.CASCADE
        )
    ],
    indices = [Index("epochDay")]
)
data class TaskCompletionEntity(
    val taskId: Long,
    val epochDay: Long
)