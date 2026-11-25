package com.shinewhite.weave.localization

import androidx.compose.runtime.staticCompositionLocalOf
import com.shinewhite.weave.database.entity.TaskCategory
import com.shinewhite.weave.settings.AppLanguage
import java.util.Calendar

interface AppStrings {
    // Home
    fun greeting(hourOfDay: Int): String
    fun dateLabel(calendar: Calendar): String
    val emptyToday: String
    val emptyWeek: String
    val emptyChecklist: String
    val emptyNotes: String
    val myWeek: String
    val todaysChecklist: String
    val notes: String
    val dailyProgress: String
    val weeklyStatistics: String
    val writeNote: String
    val add: String
    val somethingWentWrong: String
    val retry: String
    val done: String
    val markAsDone: String
    val taskOptions: String
    val edit: String
    val delete: String
    val cancel: String
    val addTask: String
    val deleteTaskTitle: String
    fun deleteTaskMessage(title: String): String
    fun progressDescription(percent: Int): String
    fun tasksDoneToday(done: Int, total: Int): String
    fun tasksCompletedThisWeek(count: Int): String
    fun dayCompletedDescription(dayIndex: Int, count: Int): String
    val dayLetters: List<String>
    val dayNames: List<String>

    // Task form
    val newTask: String
    val editTask: String
    val back: String
    val title: String
    val titlePlaceholder: String
    val category: String
    val repeatOn: String
    val timeOptional: String
    val noTimeSet: String
    val clear: String
    val allowExactAlarms: String
    val allow: String
    val ok: String
    val saving: String
    val saveChanges: String
    val saveTask: String
    val couldNotSave: String
    val couldNotLoad: String

    // Settings
    val settings: String
    val language: String
    val appearance: String
    val darkMode: String

    // Reminders
    val reminderChannelName: String
    val reminderChannelDescription: String
    fun reminderText(time: String): String

    // Formatting
    fun categoryName(category: TaskCategory): String
    fun formatTime(minutes: Int): String
    fun number(value: Int): String
    fun percent(value: Int): String
}

val LocalStrings = staticCompositionLocalOf<AppStrings> { EnglishStrings }

fun stringsFor(language: AppLanguage): AppStrings = when (language) {
    AppLanguage.ENGLISH -> EnglishStrings
    AppLanguage.PERSIAN -> PersianStrings
}

// Calendar starts the week on Sunday, the app starts it on Monday
internal fun Calendar.mondayBasedDayIndex(): Int = (get(Calendar.DAY_OF_WEEK) + 5) % 7