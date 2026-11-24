package com.shinewhite.weave.reminder

import android.app.AlarmManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import com.shinewhite.weave.database.entity.TaskEntity
import dagger.hilt.android.qualifiers.ApplicationContext
import java.util.Calendar
import javax.inject.Inject
import javax.inject.Singleton

internal const val ACTION_TASK_REMINDER = "com.shinewhite.weave.action.TASK_REMINDER"
internal const val EXTRA_TASK_ID = "extra_task_id"
internal const val EXTRA_TITLE = "extra_title"
internal const val EXTRA_TIME_MINUTES = "extra_time_minutes"
internal const val EXTRA_DAYS_MASK = "extra_days_mask"

@Singleton
class ReminderScheduler @Inject constructor(
    @ApplicationContext private val context: Context
) {
    private val alarmManager = context.getSystemService(Context.ALARM_SERVICE) as AlarmManager

    fun schedule(task: TaskEntity) {
        schedule(task.id, task.title, task.timeMinutes, task.activeDaysMask)
    }

    // The alarm carries the task data, so the receiver can show it and plan the next one without the database
    fun schedule(taskId: Long, title: String, timeMinutes: Int?, activeDaysMask: Int) {
        if (timeMinutes == null || activeDaysMask == 0) {
            cancel(taskId)
            return
        }

        val triggerAt = nextTriggerMillis(timeMinutes, activeDaysMask, System.currentTimeMillis())
        val pendingIntent = pendingIntent(taskId) {
            putExtra(EXTRA_TITLE, title)
            putExtra(EXTRA_TIME_MINUTES, timeMinutes)
            putExtra(EXTRA_DAYS_MASK, activeDaysMask)
        }

        runCatching {
            if (ReminderPermissions.canScheduleExactAlarms(context)) {
                alarmManager.setExactAndAllowWhileIdle(AlarmManager.RTC_WAKEUP, triggerAt, pendingIntent)
            } else {
                alarmManager.setAndAllowWhileIdle(AlarmManager.RTC_WAKEUP, triggerAt, pendingIntent)
            }
        }
    }

    fun cancel(taskId: Long) {
        val pendingIntent = pendingIntent(taskId)
        alarmManager.cancel(pendingIntent)
        pendingIntent.cancel()
    }

    private fun pendingIntent(taskId: Long, extras: Intent.() -> Unit = {}): PendingIntent {
        val intent = Intent(context, ReminderReceiver::class.java).apply {
            action = ACTION_TASK_REMINDER
            putExtra(EXTRA_TASK_ID, taskId)
            extras()
        }
        return PendingIntent.getBroadcast(
            context,
            taskId.toInt(),
            intent,
            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
        )
    }

    private fun nextTriggerMillis(timeMinutes: Int, activeDaysMask: Int, now: Long): Long {
        val calendar = Calendar.getInstance()
        for (offset in 0..7) {
            calendar.timeInMillis = now
            calendar.add(Calendar.DAY_OF_YEAR, offset)
            calendar.set(Calendar.HOUR_OF_DAY, timeMinutes / 60)
            calendar.set(Calendar.MINUTE, timeMinutes % 60)
            calendar.set(Calendar.SECOND, 0)
            calendar.set(Calendar.MILLISECOND, 0)
            // Calendar starts the week on Sunday, the task mask starts on Monday
            val dayIndex = (calendar.get(Calendar.DAY_OF_WEEK) + 5) % 7
            if ((activeDaysMask and (1 shl dayIndex)) != 0 && calendar.timeInMillis > now) {
                return calendar.timeInMillis
            }
        }
        return calendar.timeInMillis
    }
}