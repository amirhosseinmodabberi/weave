package com.shinewhite.weave.reminder

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent

class ReminderReceiver : BroadcastReceiver() {
    override fun onReceive(context: Context, intent: Intent) {
        if (intent.action != ACTION_TASK_REMINDER) return

        val taskId = intent.getLongExtra(EXTRA_TASK_ID, -1L)
        val timeMinutes = intent.getIntExtra(EXTRA_TIME_MINUTES, -1)
        if (taskId == -1L || timeMinutes < 0) return

        val title = intent.getStringExtra(EXTRA_TITLE).orEmpty()
        val daysMask = intent.getIntExtra(EXTRA_DAYS_MASK, 0)

        ReminderNotifier.show(context, taskId, title, timeMinutes)
        // Exact alarms fire once, so the next occurrence is planned right away
        ReminderScheduler(context.applicationContext).schedule(taskId, title, timeMinutes, daysMask)
    }
}