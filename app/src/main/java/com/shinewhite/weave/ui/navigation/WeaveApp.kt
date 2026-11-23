package com.shinewhite.weave.ui.navigation

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import com.shinewhite.weave.ui.screens.AddTaskScreen
import com.shinewhite.weave.ui.screens.HomeScreen
import com.shinewhite.weave.ui.splash.SplashScreen

enum class Destination {
    HOME,
    TASK_FORM
}

@Composable
fun WeaveApp() {
    var destination by rememberSaveable { mutableStateOf(Destination.HOME) }
    // null means a new task is being created, otherwise this task is being edited
    var editingTaskId by rememberSaveable { mutableStateOf<Long?>(null) }
    var splashFinished by rememberSaveable { mutableStateOf(false) }
    // Home is composed only when the splash starts fading, so it appears underneath the fade-out
    var homeReady by remember { mutableStateOf(false) }

    Box(modifier = Modifier.fillMaxSize()) {
        if (splashFinished || homeReady) {
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

        if (!splashFinished) {
            SplashScreen(
                onFadeOutStart = { homeReady = true },
                onFinished = { splashFinished = true }
            )
        }
    }
}