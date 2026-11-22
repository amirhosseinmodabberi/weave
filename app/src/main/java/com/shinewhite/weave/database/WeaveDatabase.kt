package com.shinewhite.weave.database

import androidx.room.Database
import androidx.room.RoomDatabase
import com.shinewhite.weave.database.dao.NoteDao
import com.shinewhite.weave.database.dao.TaskDao
import com.shinewhite.weave.database.entity.NoteEntity
import com.shinewhite.weave.database.entity.TaskCompletionEntity
import com.shinewhite.weave.database.entity.TaskEntity

@Database(
    entities = [TaskEntity::class, TaskCompletionEntity::class, NoteEntity::class],
    version = 1,
    exportSchema = false
)
abstract class WeaveDatabase : RoomDatabase() {
    abstract fun taskDao(): TaskDao
    abstract fun noteDao(): NoteDao
}