package com.shinewhite.weave.ui.screens

import android.content.res.Configuration
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.systemBarsPadding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material3.Button
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalLayoutDirection
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.LayoutDirection
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import com.shinewhite.weave.database.entity.NoteEntity
import com.shinewhite.weave.database.entity.TaskCategory
import com.shinewhite.weave.database.entity.TaskCompletionEntity
import com.shinewhite.weave.database.entity.TaskEntity
import com.shinewhite.weave.localization.LocalStrings
import com.shinewhite.weave.localization.PersianStrings
import com.shinewhite.weave.ui.theme.AppTheme
import com.shinewhite.weave.ui.theme.Dimens
import com.shinewhite.weave.utils.EpochDay
import java.util.Calendar

// viewModel() is used instead of hiltViewModel() because the hilt-navigation-compose dependency is not added
@Composable
fun HomeScreen(
    onAddTaskClick: () -> Unit,
    onEditTask: (Long) -> Unit,
    onSettingsClick: () -> Unit,
    viewModel: HomeViewModel = viewModel()
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()

    LaunchedEffect(Unit) { viewModel.refreshData() }

    HomeScreenContent(
        uiState = uiState,
        onAddTaskClick = onAddTaskClick,
        onEditTask = onEditTask,
        onSettingsClick = onSettingsClick,
        onDeleteTask = viewModel::deleteTask,
        onToggleTask = viewModel::toggleTask,
        onAddNote = viewModel::addNote,
        onRetry = viewModel::loadData
    )
}

@Composable
fun HomeScreenContent(
    uiState: HomeUiState,
    onAddTaskClick: () -> Unit,
    onEditTask: (Long) -> Unit,
    onSettingsClick: () -> Unit,
    onDeleteTask: (Long) -> Unit,
    onToggleTask: (taskId: Long, epochDay: Long, done: Boolean) -> Unit,
    onAddNote: (String) -> Unit,
    onRetry: () -> Unit
) {
    val strings = LocalStrings.current

    Surface(modifier = Modifier.fillMaxSize(), color = MaterialTheme.colorScheme.background) {
        when (uiState) {
            HomeUiState.Loading -> Box(
                modifier = Modifier.fillMaxSize(),
                contentAlignment = Alignment.Center
            ) {
                CircularProgressIndicator()
            }

            is HomeUiState.Error -> Column(
                modifier = Modifier.fillMaxSize(),
                verticalArrangement = Arrangement.Center,
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Text(
                    text = strings.somethingWentWrong,
                    style = MaterialTheme.typography.titleMedium,
                    color = MaterialTheme.colorScheme.onBackground
                )
                Button(onClick = onRetry, modifier = Modifier.padding(top = Dimens.paddingMedium)) {
                    Text(strings.retry)
                }
            }

            is HomeUiState.Success -> {
                val sidePadding = Modifier.padding(horizontal = Dimens.paddingLarge)
                var taskToDelete by remember { mutableStateOf<TaskEntity?>(null) }

                taskToDelete?.let { pending ->
                    DeleteTaskDialog(
                        task = pending,
                        onConfirm = {
                            onDeleteTask(pending.id)
                            taskToDelete = null
                        },
                        onDismiss = { taskToDelete = null }
                    )
                }

                Box(modifier = Modifier.fillMaxSize()) {
                    LazyColumn(
                        modifier = Modifier
                            .fillMaxSize()
                            .systemBarsPadding(),
                        contentPadding = PaddingValues(top = Dimens.paddingLarge, bottom = 96.dp),
                        verticalArrangement = Arrangement.spacedBy(Dimens.paddingLarge)
                    ) {
                        item {
                            Row(modifier = sidePadding, verticalAlignment = Alignment.CenterVertically) {
                                Column(modifier = Modifier.weight(1f)) {
                                    Text(
                                        text = strings.dateLabel(Calendar.getInstance()),
                                        style = MaterialTheme.typography.bodyMedium,
                                        color = MaterialTheme.colorScheme.onSurfaceVariant
                                    )
                                    Text(
                                        text = strings.greeting(Calendar.getInstance().get(Calendar.HOUR_OF_DAY)),
                                        style = MaterialTheme.typography.headlineMedium,
                                        fontWeight = FontWeight.Bold,
                                        color = MaterialTheme.colorScheme.onBackground
                                    )
                                }
                                IconButton(
                                    onClick = onSettingsClick,
                                    modifier = Modifier.size(Dimens.minTouchTarget)
                                ) {
                                    Icon(
                                        imageVector = WeaveIcons.Settings,
                                        contentDescription = strings.settings,
                                        tint = MaterialTheme.colorScheme.onBackground
                                    )
                                }
                            }
                        }
                        item {
                            TodayCarousel(
                                tasks = uiState.todayTasks,
                                isDone = { uiState.isDone(it.id, uiState.todayEpochDay) },
                                onToggle = { task, done -> onToggleTask(task.id, uiState.todayEpochDay, done) },
                                onEdit = { onEditTask(it.id) },
                                onDelete = { taskToDelete = it }
                            )
                        }
                        item { SectionTitle(strings.myWeek, sidePadding) }
                        item {
                            WeekTable(
                                state = uiState,
                                onToggle = onToggleTask,
                                onEdit = { onEditTask(it.id) },
                                onDelete = { taskToDelete = it },
                                modifier = sidePadding
                            )
                        }
                        item { SectionTitle(strings.todaysChecklist, sidePadding) }
                        item {
                            TodayChecklist(
                                state = uiState,
                                onToggle = onToggleTask,
                                onEdit = { onEditTask(it.id) },
                                onDelete = { taskToDelete = it },
                                modifier = sidePadding
                            )
                        }
                        item { SectionTitle(strings.notes, sidePadding) }
                        item { NotesSection(notes = uiState.notes, onAddNote = onAddNote, modifier = sidePadding) }
                        item { SectionTitle(strings.dailyProgress, sidePadding) }
                        item {
                            DailyProgressCard(
                                done = uiState.doneTodayCount,
                                total = uiState.todayTasks.size,
                                modifier = sidePadding
                            )
                        }
                        item { SectionTitle(strings.weeklyStatistics, sidePadding) }
                        item {
                            WeeklyStatsCard(
                                counts = (0..6).map { uiState.completedCountOn(it) },
                                todayIndex = uiState.todayIndex,
                                modifier = sidePadding
                            )
                        }
                    }
                    FloatingActionButton(
                        onClick = onAddTaskClick,
                        modifier = Modifier
                            .align(Alignment.BottomEnd)
                            .systemBarsPadding()
                            .padding(Dimens.paddingLarge),
                        containerColor = MaterialTheme.colorScheme.primary,
                        contentColor = MaterialTheme.colorScheme.onPrimary
                    ) {
                        Icon(imageVector = WeaveIcons.Plus, contentDescription = strings.addTask)
                    }
                }
            }
        }
    }
}

private fun previewState(): HomeUiState.Success {
    val today = EpochDay.today()
    val weekStart = EpochDay.weekStart(today)
    val everyDay = 0b1111111
    val tasks = listOf(
        TaskEntity(1, "Morning workout", TaskCategory.GYM, everyDay, 7 * 60),
        TaskEntity(2, "Read 20 pages", TaskCategory.READING, everyDay, 13 * 60),
        TaskEntity(3, "Painting class", TaskCategory.PAINTING, everyDay, 18 * 60),
        TaskEntity(4, "Study for exam", TaskCategory.SCHOOL, everyDay)
    )
    val completions = listOf(
        TaskCompletionEntity(1, today),
        TaskCompletionEntity(1, weekStart),
        TaskCompletionEntity(2, weekStart),
        TaskCompletionEntity(3, weekStart),
        TaskCompletionEntity(4, weekStart)
    ).distinct()
    val notes = listOf(
        NoteEntity(1, today, "Buy canvas and brushes before Sunday."),
        NoteEntity(2, today, "Try the new gym routine, 3 sets each.")
    )
    return HomeUiState.Success(
        todayEpochDay = today,
        tasks = tasks,
        completions = completions,
        notes = notes
    )
}

@Preview(name = "Light", showBackground = true, heightDp = 1900)
@Preview(
    name = "Dark",
    showBackground = true,
    heightDp = 1900,
    uiMode = Configuration.UI_MODE_NIGHT_YES
)
@Composable
private fun HomeScreenPreview() {
    AppTheme {
        HomeScreenContent(
            uiState = previewState(),
            onAddTaskClick = {},
            onSettingsClick = {},
            onEditTask = {},
            onDeleteTask = {},
            onToggleTask = { _, _, _ -> },
            onAddNote = {},
            onRetry = {}
        )
    }
}

@Preview(name = "Empty", showBackground = true)
@Composable
private fun HomeScreenEmptyPreview() {
    AppTheme {
        HomeScreenContent(
            uiState = HomeUiState.Success(),
            onAddTaskClick = {},
            onSettingsClick = {},
            onEditTask = {},
            onDeleteTask = {},
            onToggleTask = { _, _, _ -> },
            onAddNote = {},
            onRetry = {}
        )
    }
}

@Preview(name = "Persian RTL", showBackground = true, heightDp = 1900)
@Composable
private fun HomeScreenPersianPreview() {
    AppTheme {
        CompositionLocalProvider(
            LocalStrings provides PersianStrings,
            LocalLayoutDirection provides LayoutDirection.Rtl
        ) {
            HomeScreenContent(
                uiState = previewState(),
                onAddTaskClick = {},
                onEditTask = {},
                onSettingsClick = {},
                onDeleteTask = {},
                onToggleTask = { _, _, _ -> },
                onAddNote = {},
                onRetry = {}
            )
        }
    }
}