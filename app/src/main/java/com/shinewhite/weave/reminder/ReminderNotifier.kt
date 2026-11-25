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
import com.shinewhite.weave.localization.AppStrings
import com.shinewhite.weave.localization.stringsFor
import com.shinewhite.weave.settings.SettingsRepository

object ReminderNotifier {
    private const val CHANNEL_ID = "task_reminders"

    @SuppressLint("MissingPermission")
    fun show(context: Context, taskId: Long, title: String, timeMinutes: Int) {
        if (!ReminderPermissions.hasNotificationPermission(context)) return

        val strings = stringsFor(SettingsRepository.readLanguage(context))
        createChannel(context, strings)

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
            .setContentText(strings.reminderText(strings.formatTime(timeMinutes)))
            .setCategory(NotificationCompat.CATEGORY_REMINDER)
            .setPriority(NotificationCompat.PRIORITY_HIGH)
            .setAutoCancel(true)
            .setContentIntent(contentIntent)
            .build()

        NotificationManagerCompat.from(context).notify(taskId.toInt(), notification)
    }

    private fun createChannel(context: Context, strings: AppStrings) {
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.O) return

        val channel = NotificationChannel(
            CHANNEL_ID,
            strings.reminderChannelName,
            NotificationManager.IMPORTANCE_HIGH
        ).apply { description = strings.reminderChannelDescription }
        context.getSystemService(NotificationManager::class.java).createNotificationChannel(channel)
    }
}