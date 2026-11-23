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
    ADD_TASK
}

@Composable
fun WeaveApp() {
    var destination by rememberSaveable { mutableStateOf(Destination.HOME) }

    when (destination) {
        Destination.HOME -> HomeScreen(onAddTaskClick = { destination = Destination.ADD_TASK })
        Destination.ADD_TASK -> AddTaskScreen(onBack = { destination = Destination.HOME })
    }
}