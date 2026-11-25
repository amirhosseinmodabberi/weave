package com.shinewhite.weave.localization

import com.shinewhite.weave.database.entity.TaskCategory
import java.util.Calendar

object EnglishStrings : AppStrings {
    private val months = listOf("Jan", "Feb", "Mar", "Apr", "May", "Jun", "Jul", "Aug", "Sep", "Oct", "Nov", "Dec")

    override fun greeting(hourOfDay: Int): String = when {
        hourOfDay < 12 -> "Good morning"
        hourOfDay < 18 -> "Good afternoon"
        else -> "Good evening"
    }

    override fun dateLabel(calendar: Calendar): String =
        "${dayNames[calendar.mondayBasedDayIndex()]}, ${calendar.get(Calendar.DAY_OF_MONTH)} ${months[calendar.get(Calendar.MONTH)]}"

    override val emptyToday = "No tasks planned for today."
    override val emptyWeek = "Your weekly plan is empty."
    override val emptyChecklist = "Nothing to check off today."
    override val emptyNotes = "No notes for today."
    override val myWeek = "My week"
    override val todaysChecklist = "Today's checklist"
    override val notes = "Notes"
    override val dailyProgress = "Daily progress"
    override val weeklyStatistics = "Weekly statistics"
    override val writeNote = "Write a note"
    override val add = "Add"
    override val somethingWentWrong = "Something went wrong"
    override val retry = "Retry"
    override val done = "Done"
    override val markAsDone = "Mark as done"
    override val taskOptions = "Task options"
    override val edit = "Edit"
    override val delete = "Delete"
    override val cancel = "Cancel"
    override val addTask = "Add task"
    override val deleteTaskTitle = "Delete task?"
    override fun deleteTaskMessage(title: String) = "\"$title\" and its progress history will be removed."
    override fun progressDescription(percent: Int) = "$percent percent of today's tasks completed"
    override fun tasksDoneToday(done: Int, total: Int) = "$done of $total tasks done today"
    override fun tasksCompletedThisWeek(count: Int) = "$count tasks completed this week"
    override fun dayCompletedDescription(dayIndex: Int, count: Int) = "${dayNames[dayIndex]}: $count completed"
    override val dayLetters = listOf("M", "T", "W", "T", "F", "S", "S")
    override val dayNames = listOf("Monday", "Tuesday", "Wednesday", "Thursday", "Friday", "Saturday", "Sunday")

    override val newTask = "New task"
    override val editTask = "Edit task"
    override val back = "Back"
    override val title = "Title"
    override val titlePlaceholder = "What do you want to do?"
    override val category = "Category"
    override val repeatOn = "Repeat on"
    override val timeOptional = "Time (optional)"
    override val noTimeSet = "No time set"
    override val clear = "Clear"
    override val allowExactAlarms = "Allow exact alarms so the reminder arrives on time."
    override val allow = "Allow"
    override val ok = "OK"
    override val saving = "Saving..."
    override val saveChanges = "Save changes"
    override val saveTask = "Save task"
    override val couldNotSave = "Could not save the task"
    override val couldNotLoad = "Could not load the task"

    override val settings = "Settings"
    override val language = "Language"
    override val appearance = "Appearance"
    override val darkMode = "Dark mode"

    override val reminderChannelName = "Task reminders"
    override val reminderChannelDescription = "Reminders at the time of your planned tasks"
    override fun reminderText(time: String) = "Scheduled for $time"

    override fun categoryName(category: TaskCategory): String = when (category) {
        TaskCategory.PAINTING -> "Painting"
        TaskCategory.GYM -> "Gym"
        TaskCategory.SCHOOL -> "School"
        TaskCategory.READING -> "Reading"
        TaskCategory.WORK -> "Work"
        TaskCategory.COOKING -> "Cooking"
        TaskCategory.SHOPPING -> "Shopping"
        TaskCategory.MUSIC -> "Music"
        TaskCategory.SLEEP -> "Sleep"
        TaskCategory.HEALTH -> "Health"
        TaskCategory.FRIENDS -> "Friends"
        TaskCategory.GAMING -> "Gaming"
        TaskCategory.OUTDOORS -> "Outdoors"
        TaskCategory.CLEANING -> "Cleaning"
        TaskCategory.OTHER -> "Other"
    }

    override fun formatTime(minutes: Int): String {
        val hour = minutes / 60
        val displayHour = if (hour % 12 == 0) 12 else hour % 12
        val suffix = if (hour < 12) "AM" else "PM"
        return "%d:%02d %s".format(displayHour, minutes % 60, suffix)
    }

    override fun number(value: Int): String = value.toString()
    override fun percent(value: Int): String = "$value%"
}