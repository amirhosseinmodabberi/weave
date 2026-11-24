package com.shinewhite.weave.reminder

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import com.shinewhite.weave.database.repository.TaskRepository
import dagger.hilt.EntryPoint
import dagger.hilt.InstallIn
import dagger.hilt.android.EntryPointAccessors
import dagger.hilt.components.SingletonComponent
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

@EntryPoint
@InstallIn(SingletonComponent::class)
interface ReminderEntryPoint {
    fun taskRepository(): TaskRepository
    fun reminderScheduler(): ReminderScheduler
}

// Alarms are lost after a reboot or an app update, so every task is scheduled again
class RescheduleReceiver : BroadcastReceiver() {
    override fun onReceive(context: Context, intent: Intent) {
        val supportedActions = setOf(
            Intent.ACTION_BOOT_COMPLETED,
            Intent.ACTION_MY_PACKAGE_REPLACED,
            "android.app.action.SCHEDULE_EXACT_ALARM_PERMISSION_STATE_CHANGED"
        )
        if (intent.action !in supportedActions) return

        val pendingResult = goAsync()
        val entryPoint = EntryPointAccessors.fromApplication(
            context.applicationContext,
            ReminderEntryPoint::class.java
        )

        CoroutineScope(Dispatchers.IO).launch {
            try {
                entryPoint.taskRepository().getAllTasks().onSuccess { tasks ->
                    tasks.forEach { entryPoint.reminderScheduler().schedule(it) }
                }
            } finally {
                pendingResult.finish()
            }
        }
    }
}