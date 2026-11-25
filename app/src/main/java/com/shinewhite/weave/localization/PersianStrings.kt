package com.shinewhite.weave.localization

import com.shinewhite.weave.database.entity.TaskCategory
import java.util.Calendar

object PersianStrings : AppStrings {
    private val months = listOf(
        "ژانویه", "فوریه", "مارس", "آوریل", "مه", "ژوئن",
        "ژوئیه", "اوت", "سپتامبر", "اکتبر", "نوامبر", "دسامبر"
    )

    private fun digits(text: String): String = text.map { char ->
        if (char in '0'..'9') '۰' + (char - '0') else char
    }.joinToString("")

    override fun greeting(hourOfDay: Int): String = when {
        hourOfDay < 12 -> "صبح بخیر"
        hourOfDay < 18 -> "عصر بخیر"
        else -> "شب بخیر"
    }

    override fun dateLabel(calendar: Calendar): String =
        "${dayNames[calendar.mondayBasedDayIndex()]}، ${number(calendar.get(Calendar.DAY_OF_MONTH))} ${months[calendar.get(Calendar.MONTH)]}"

    override val emptyToday = "برای امروز کاری برنامه‌ریزی نشده است."
    override val emptyWeek = "برنامه هفتگی شما خالی است."
    override val emptyChecklist = "امروز کاری برای تیک‌زدن نیست."
    override val emptyNotes = "برای امروز یادداشتی نیست."
    override val myWeek = "هفته من"
    override val todaysChecklist = "چک‌لیست امروز"
    override val notes = "یادداشت‌ها"
    override val dailyProgress = "پیشرفت روزانه"
    override val weeklyStatistics = "آمار هفتگی"
    override val writeNote = "یادداشت بنویسید"
    override val add = "افزودن"
    override val somethingWentWrong = "مشکلی پیش آمد"
    override val retry = "تلاش دوباره"
    override val done = "انجام شد"
    override val markAsDone = "علامت‌زدن به‌عنوان انجام‌شده"
    override val taskOptions = "گزینه‌های تسک"
    override val edit = "ویرایش"
    override val delete = "حذف"
    override val cancel = "انصراف"
    override val addTask = "افزودن تسک"
    override val deleteTaskTitle = "حذف تسک؟"
    override fun deleteTaskMessage(title: String) = "«$title» و سابقه پیشرفت آن حذف خواهد شد."
    override fun progressDescription(percent: Int) = "${number(percent)} درصد از تسک‌های امروز انجام شده است"
    override fun tasksDoneToday(done: Int, total: Int) = "${number(done)} از ${number(total)} تسک امروز انجام شده"
    override fun tasksCompletedThisWeek(count: Int) = "${number(count)} تسک این هفته انجام شد"
    override fun dayCompletedDescription(dayIndex: Int, count: Int) = "${dayNames[dayIndex]}: ${number(count)} انجام شد"
    override val dayLetters = listOf("د", "س", "چ", "پ", "ج", "ش", "ی")
    override val dayNames = listOf("دوشنبه", "سه‌شنبه", "چهارشنبه", "پنج‌شنبه", "جمعه", "شنبه", "یکشنبه")

    override val newTask = "تسک جدید"
    override val editTask = "ویرایش تسک"
    override val back = "بازگشت"
    override val title = "عنوان"
    override val titlePlaceholder = "می‌خواهید چه کاری انجام دهید؟"
    override val category = "دسته‌بندی"
    override val repeatOn = "تکرار در"
    override val timeOptional = "ساعت (اختیاری)"
    override val noTimeSet = "ساعتی تعیین نشده"
    override val clear = "پاک کردن"
    override val allowExactAlarms = "برای رسیدن یادآور سر وقت، آلارم دقیق را مجاز کنید."
    override val allow = "مجاز کردن"
    override val ok = "تأیید"
    override val saving = "در حال ذخیره..."
    override val saveChanges = "ذخیره تغییرات"
    override val saveTask = "ذخیره تسک"
    override val couldNotSave = "ذخیره تسک ممکن نشد"
    override val couldNotLoad = "بارگذاری تسک ممکن نشد"

    override val settings = "تنظیمات"
    override val language = "زبان"
    override val appearance = "ظاهر"
    override val darkMode = "حالت تاریک"

    override val reminderChannelName = "یادآور تسک‌ها"
    override val reminderChannelDescription = "یادآور در ساعت تسک‌های برنامه‌ریزی‌شده"
    override fun reminderText(time: String) = "زمان‌بندی‌شده برای $time"

    override fun categoryName(category: TaskCategory): String = when (category) {
        TaskCategory.PAINTING -> "نقاشی"
        TaskCategory.GYM -> "باشگاه"
        TaskCategory.SCHOOL -> "مدرسه"
        TaskCategory.READING -> "مطالعه"
        TaskCategory.WORK -> "کار"
        TaskCategory.COOKING -> "آشپزی"
        TaskCategory.SHOPPING -> "خرید"
        TaskCategory.MUSIC -> "موسیقی"
        TaskCategory.SLEEP -> "خواب"
        TaskCategory.HEALTH -> "سلامتی"
        TaskCategory.FRIENDS -> "دوستان"
        TaskCategory.GAMING -> "بازی"
        TaskCategory.OUTDOORS -> "طبیعت‌گردی"
        TaskCategory.CLEANING -> "نظافت"
        TaskCategory.OTHER -> "سایر"
    }

    override fun formatTime(minutes: Int): String {
        val hour = minutes / 60
        val displayHour = if (hour % 12 == 0) 12 else hour % 12
        val suffix = if (hour < 12) "ق.ظ" else "ب.ظ"
        return digits("%d:%02d".format(displayHour, minutes % 60)) + " " + suffix
    }

    override fun number(value: Int): String = digits(value.toString())
    override fun percent(value: Int): String = "${number(value)}٪"
}