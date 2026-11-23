package com.shinewhite.weave.ui.navigation

import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import com.shinewhite.weave.ui.screens.AddTaskScreen
import com.shinewhite.weave.ui.screens.HomeScreen

enum class Destination {
    HOME,
    TASK_FORM
}

@Composable
fun WeaveApp() {
    var destination by rememberSaveable { mutableStateOf(Destination.HOME) }
    // null means a new task is being created, otherwise this task is being edited
    var editingTaskId by rememberSaveable { mutableStateOf<Long?>(null) }

    when (destination) {
        Destination.HOME -> HomeScreen(
            onAddTaskClick = {
                editingTaskId = null
                destination = Destination.TASK_FORM
            },
            onEditTask = { taskId ->
                editingTaskId = taskId
                destination = Destination.TASK_FORM
            }
        )

        Destination.TASK_FORM -> AddTaskScreen(
            taskId = editingTaskId,
            onBack = { destination = Destination.HOME }
        )
    }
}