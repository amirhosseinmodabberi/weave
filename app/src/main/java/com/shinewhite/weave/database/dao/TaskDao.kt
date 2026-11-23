package com.shinewhite.weave.database.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import com.shinewhite.weave.database.entity.TaskCompletionEntity
import com.shinewhite.weave.database.entity.TaskEntity

@Dao
interface TaskDao {
    @Insert
    suspend fun insertTask(task: TaskEntity): Long

    @Update
    suspend fun updateTask(task: TaskEntity)

    @Query("DELETE FROM tasks WHERE id = :taskId")
    suspend fun deleteTask(taskId: Long)

    @Query("SELECT * FROM tasks ORDER BY id")
    suspend fun getAllTasks(): List<TaskEntity>

    @Query("SELECT * FROM tasks WHERE id = :taskId")
    suspend fun getTaskById(taskId: Long): TaskEntity?

    @Query("SELECT * FROM tasks WHERE (activeDaysMask & :dayBit) != 0 ORDER BY timeMinutes IS NULL, timeMinutes, id")
    suspend fun getTasksActiveOnDay(dayBit: Int): List<TaskEntity>

    @Insert(onConflict = OnConflictStrategy.IGNORE)
    suspend fun insertCompletion(completion: TaskCompletionEntity)

    @Query("DELETE FROM task_completions WHERE taskId = :taskId AND epochDay = :epochDay")
    suspend fun deleteCompletion(taskId: Long, epochDay: Long)

    @Query("SELECT * FROM task_completions WHERE epochDay BETWEEN :startDay AND :endDay")
    suspend fun getCompletionsBetween(startDay: Long, endDay: Long): List<TaskCompletionEntity>
}