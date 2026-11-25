package com.shinewhite.weave.ui.screens

import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.StrokeJoin
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.graphics.vector.PathParser
import androidx.compose.ui.unit.dp
import com.shinewhite.weave.database.entity.TaskCategory

private fun strokeIcon(name: String, vararg paths: String, autoMirror: Boolean = false): ImageVector =
    ImageVector.Builder(
        name = name,
        defaultWidth = 24.dp,
        defaultHeight = 24.dp,
        viewportWidth = 24f,
        viewportHeight = 24f,
        autoMirror = autoMirror
    ).apply {
        paths.forEach { data ->
            addPath(
                pathData = PathParser().parsePathString(data).toNodes(),
                stroke = SolidColor(Color.Black),
                strokeLineWidth = 2f,
                strokeLineCap = StrokeCap.Round,
                strokeLineJoin = StrokeJoin.Round
            )
        }
    }.build()

object WeaveIcons {
    val Check = strokeIcon("Check", "M5 12l5 5 9-10")
    val Plus = strokeIcon("Plus", "M12 5v14M5 12h14")
    val Settings = strokeIcon(
        "Settings",
        "M4 7h8",
        "M16 7h4",
        "M12 7a2 2 0 1 0 4 0a2 2 0 1 0-4 0",
        "M4 17h4",
        "M12 17h8",
        "M8 17a2 2 0 1 0 4 0a2 2 0 1 0-4 0"
    )
    val Back = strokeIcon("Back", "M15 5l-7 7 7 7", autoMirror = true)
    val ChevronDown = strokeIcon("ChevronDown", "M6 9l6 6 6-6")
    val Clock = strokeIcon("Clock", "M3 12a9 9 0 1 0 18 0a9 9 0 1 0-18 0", "M12 7v5l3 2")
    val Edit = strokeIcon("Edit", "M4 20h4L19 9a2.8 2.8 0 0 0-4-4L4 16z", "M13.5 6.5l4 4")
    val Delete = strokeIcon(
        "Delete",
        "M4 7h16",
        "M10 11v6",
        "M14 11v6",
        "M6 7l1 13h10l1-13",
        "M9 7V4h6v3"
    )
    val Painting = strokeIcon("Painting", "M14 4l6 6-8 8-6-6z", "M6 12c-2 1-3 3-3 6 3 0 5-1 6-3")
    val Gym = strokeIcon("Gym", "M6 7v10M18 7v10M3 9.5v5M21 9.5v5M6 12h12")
    val School = strokeIcon("School", "M2 9l10-5 10 5-10 5z", "M6 11.5V16c3 2.5 9 2.5 12 0v-4.5")
    val Reading = strokeIcon("Reading", "M4 4h10a4 4 0 0 1 4 4v12H8a4 4 0 0 1-4-4z", "M8 20V4")
    val Work = strokeIcon(
        "Work",
        "M5 7h14a2 2 0 0 1 2 2v9a2 2 0 0 1-2 2H5a2 2 0 0 1-2-2V9a2 2 0 0 1 2-2z",
        "M9 7V5h6v2",
        "M3 13h18"
    )
    val Cooking = strokeIcon(
        "Cooking",
        "M5 11h14v7a2 2 0 0 1-2 2H7a2 2 0 0 1-2-2z",
        "M3 11h18",
        "M9 7V5",
        "M15 7V5"
    )
    val Shopping = strokeIcon(
        "Shopping",
        "M5 8h14l-1 12H6z",
        "M9 8V6a3 3 0 0 1 6 0v2"
    )
    val Music = strokeIcon(
        "Music",
        "M9 18V6l10-2v12",
        "M9 18a3 3 0 1 1-6 0a3 3 0 1 1 6 0z",
        "M19 16a3 3 0 1 1-6 0a3 3 0 1 1 6 0z"
    )
    val Sleep = strokeIcon("Sleep", "M20 14A8 8 0 0 1 9 3a8 8 0 1 0 11 11z")
    val Health = strokeIcon("Health", "M12 20s-7-4.5-7-10a4 4 0 0 1 7-2.5A4 4 0 0 1 19 10c0 5.5-7 10-7 10z")
    val Friends = strokeIcon(
        "Friends",
        "M6 8a3 3 0 1 0 6 0a3 3 0 1 0-6 0",
        "M3 20c0-3 2.5-5 6-5s6 2 6 5",
        "M16 5.5a3 3 0 0 1 0 5",
        "M18 15c2 .5 3 2.5 3 5"
    )
    val Gaming = strokeIcon(
        "Gaming",
        "M7 8h10a4 4 0 0 1 4 4v1a3 3 0 0 1-5.5 1.5L15 14H9l-.5.5A3 3 0 0 1 3 13v-1a4 4 0 0 1 4-4z",
        "M8 11v2",
        "M7 12h2"
    )
    val Outdoors = strokeIcon("Outdoors", "M3 20l6-11 4 7 3-4 5 8z")
    val Cleaning = strokeIcon(
        "Cleaning",
        "M12 3l1.8 5.2L19 10l-5.2 1.8L12 17l-1.8-5.2L5 10l5.2-1.8z",
        "M19 16v4",
        "M17 18h4"
    )
    val Other = strokeIcon("Other", "M6 12h0.01", "M12 12h0.01", "M18 12h0.01")

    fun forCategory(category: TaskCategory): ImageVector = when (category) {
        TaskCategory.PAINTING -> Painting
        TaskCategory.GYM -> Gym
        TaskCategory.SCHOOL -> School
        TaskCategory.READING -> Reading
        TaskCategory.WORK -> Work
        TaskCategory.COOKING -> Cooking
        TaskCategory.SHOPPING -> Shopping
        TaskCategory.MUSIC -> Music
        TaskCategory.SLEEP -> Sleep
        TaskCategory.HEALTH -> Health
        TaskCategory.FRIENDS -> Friends
        TaskCategory.GAMING -> Gaming
        TaskCategory.OUTDOORS -> Outdoors
        TaskCategory.CLEANING -> Cleaning
        TaskCategory.OTHER -> Other
    }
}

fun TaskCategory.displayName(): String = name.lowercase().replaceFirstChar { it.uppercase() }
