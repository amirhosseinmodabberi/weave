package com.shinewhite.weave.ui.screens

import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.StrokeJoin
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.graphics.vector.PathParser
import androidx.compose.ui.unit.dp
import com.shinewhite.weave.database.entity.TaskCategory

private fun strokeIcon(name: String, vararg paths: String): ImageVector =
    ImageVector.Builder(
        name = name,
        defaultWidth = 24.dp,
        defaultHeight = 24.dp,
        viewportWidth = 24f,
        viewportHeight = 24f
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
    val Other = strokeIcon("Other", "M6 12h0.01", "M12 12h0.01", "M18 12h0.01")

    fun forCategory(category: TaskCategory): ImageVector = when (category) {
        TaskCategory.PAINTING -> Painting
        TaskCategory.GYM -> Gym
        TaskCategory.SCHOOL -> School
        TaskCategory.READING -> Reading
        TaskCategory.WORK -> Work
        TaskCategory.OTHER -> Other
    }
}

fun TaskCategory.displayName(): String = name.lowercase().replaceFirstChar { it.uppercase() }