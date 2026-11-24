package com.shinewhite.weave.reminder

import android.annotation.SuppressLint
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.os.Build
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat
import com.shinewhite.weave.R

object ReminderNotifier {
    private const val CHANNEL_ID = "task_reminders"

    @SuppressLint("MissingPermission")
    fun show(context: Context, taskId: Long, title: String, timeMinutes: Int) {
        if (!ReminderPermissions.hasNotificationPermission(context)) return

        createChannel(context)

        val launchIntent = context.packageManager
            .getLaunchIntentForPackage(context.packageName)
            ?.apply { flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TOP }
        val contentIntent = launchIntent?.let {
            PendingIntent.getActivity(
                context,
                taskId.toInt(),
                it,
                PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
            )
        }

        val notification = NotificationCompat.Builder(context, CHANNEL_ID)
            .setSmallIcon(R.drawable.ic_notification)
            .setContentTitle(title)
            .setContentText("Scheduled for ${formatTime(timeMinutes)}")
            .setCategory(NotificationCompat.CATEGORY_REMINDER)
            .setPriority(NotificationCompat.PRIORITY_HIGH)
            .setAutoCancel(true)
            .setContentIntent(contentIntent)
            .build()

        NotificationManagerCompat.from(context).notify(taskId.toInt(), notification)
    }

    private fun createChannel(context: Context) {
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.O) return

        val channel = NotificationChannel(
            CHANNEL_ID,
            "Task reminders",
            NotificationManager.IMPORTANCE_HIGH
        ).apply { description = "Reminders at the time of your planned tasks" }
        context.getSystemService(NotificationManager::class.java).createNotificationChannel(channel)
    }

    private fun formatTime(minutes: Int): String {
        val hour = minutes / 60
        val displayHour = if (hour % 12 == 0) 12 else hour % 12
        val suffix = if (hour < 12) "AM" else "PM"
        return "%d:%02d %s".format(displayHour, minutes % 60, suffix)
    }
}