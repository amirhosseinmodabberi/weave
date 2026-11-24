package com.shinewhite.weave.database.repository

import com.shinewhite.weave.database.dao.TaskDao
import com.shinewhite.weave.database.entity.TaskCompletionEntity
import com.shinewhite.weave.database.entity.TaskEntity
import com.shinewhite.weave.reminder.ReminderScheduler
import com.shinewhite.weave.utils.EpochDay
import javax.inject.Inject

class TaskRepository @Inject constructor(
    private val taskDao: TaskDao,
    private val reminderScheduler: ReminderScheduler
) {
    suspend fun getAllTasks(): Result<List<TaskEntity>> =
        runCatching { taskDao.getAllTasks() }

    suspend fun getTask(taskId: Long): Result<TaskEntity?> =
        runCatching { taskDao.getTaskById(taskId) }

    suspend fun getTasksForDay(epochDay: Long): Result<List<TaskEntity>> =
        runCatching { taskDao.getTasksActiveOnDay(1 shl EpochDay.dayIndex(epochDay)) }

    suspend fun addTask(task: TaskEntity): Result<Long> =
        runCatching {
            val id = taskDao.insertTask(task)
            reminderScheduler.schedule(task.copy(id = id))
            id
        }

    suspend fun updateTask(task: TaskEntity): Result<Unit> =
        runCatching {
            taskDao.updateTask(task)
            reminderScheduler.schedule(task)
        }

    suspend fun deleteTask(taskId: Long): Result<Unit> =
        runCatching {
            taskDao.deleteTask(taskId)
            reminderScheduler.cancel(taskId)
        }

    suspend fun setTaskDone(taskId: Long, epochDay: Long, done: Boolean): Result<Unit> =
        runCatching {
            if (done) {
                taskDao.insertCompletion(TaskCompletionEntity(taskId, epochDay))
            } else {
                taskDao.deleteCompletion(taskId, epochDay)
            }
        }

    suspend fun getCompletions(startDay: Long, endDay: Long): Result<List<TaskCompletionEntity>> =
        runCatching { taskDao.getCompletionsBetween(startDay, endDay) }
}