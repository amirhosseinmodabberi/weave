package com.shinewhite.weave.ui.navigation

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.semantics.clearAndSetSemantics
import com.shinewhite.weave.ui.screens.AddTaskScreen
import com.shinewhite.weave.ui.screens.HomeScreen
import com.shinewhite.weave.ui.screens.SettingsScreen
import com.shinewhite.weave.ui.splash.SplashAnimationState
import com.shinewhite.weave.ui.splash.SplashScreen
import com.shinewhite.weave.ui.splash.blurredFade

enum class Destination {
    HOME,
    TASK_FORM,
    SETTINGS
}

@Composable
fun WeaveApp() {
    var destination by rememberSaveable { mutableStateOf(Destination.HOME) }
    // null means a new task is being created, otherwise this task is being edited
    var editingTaskId by rememberSaveable { mutableStateOf<Long?>(null) }
    var splashFinished by rememberSaveable { mutableStateOf(false) }
    val splashState = remember { SplashAnimationState() }

    // Home is composed from the first frame and stays invisible behind the splash,
    // so its heavy first composition never stalls the transition
    val homeModifier = if (splashFinished) {
        Modifier.fillMaxSize()
    } else {
        Modifier
            .fillMaxSize()
            .blurredFade(
                alpha = { splashState.transition.value },
                blurFraction = { 1f - splashState.transition.value }
            )
            .clearAndSetSemantics { }
    }

    // The solid background keeps the fading Home from blending with the window color
    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
    ) {
        Box(modifier = homeModifier) {
            when (destination) {
                Destination.HOME -> HomeScreen(
                    onAddTaskClick = {
                        editingTaskId = null
                        destination = Destination.TASK_FORM
                    },
                    onEditTask = { taskId ->
                        editingTaskId = taskId
                        destination = Destination.TASK_FORM
                    },
                    onSettingsClick = { destination = Destination.SETTINGS }
                )

                Destination.TASK_FORM -> AddTaskScreen(
                    taskId = editingTaskId,
                    onBack = { destination = Destination.HOME }
                )

                Destination.SETTINGS -> SettingsScreen(
                    onBack = { destination = Destination.HOME }
                )
            }
        }

        if (!splashFinished) {
            SplashScreen(
                animationState = splashState,
                onFinished = { splashFinished = true }
            )
        }
    }
}