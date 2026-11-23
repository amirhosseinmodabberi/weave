package com.shinewhite.weave.ui.screens

import android.content.res.Configuration
import kotlin.time.Duration.Companion.seconds
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.systemBarsPadding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material3.Button
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import com.shinewhite.weave.database.entity.NoteEntity
import com.shinewhite.weave.database.entity.TaskCategory
import com.shinewhite.weave.database.entity.TaskCompletionEntity
import com.shinewhite.weave.database.entity.TaskEntity
import com.shinewhite.weave.ui.theme.AppTheme
import com.shinewhite.weave.ui.theme.Dimens
import com.shinewhite.weave.utils.EpochDay
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Date
import java.util.Locale

// viewModel() is used instead of hiltViewModel() because the hilt-navigation-compose dependency is not added
@Composable
fun HomeScreen(onAddTaskClick: () -> Unit, viewModel: HomeViewModel = viewModel()) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()

    LaunchedEffect(Unit) { viewModel.refreshData() }

    HomeScreenContent(
        uiState = uiState,
        onAddTaskClick = onAddTaskClick,
        onToggleTask = viewModel::toggleTask,
        onAddNote = viewModel::addNote,
        onRetry = viewModel::loadData
    )
}

@Composable
fun HomeScreenContent(
    uiState: HomeUiState,
    onAddTaskClick: () -> Unit,
    onToggleTask: (taskId: Long, epochDay: Long, done: Boolean) -> Unit,
    onAddNote: (String) -> Unit,
    onRetry: () -> Unit
) {
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
                    text = "Something went wrong",
                    style = MaterialTheme.typography.titleMedium,
                    color = MaterialTheme.colorScheme.onBackground
                )
                Button(onClick = onRetry, modifier = Modifier.padding(top = Dimens.paddingMedium)) {
                    Text("Retry")
                }
            }

            is HomeUiState.Success -> {
                val sidePadding = Modifier.padding(horizontal = Dimens.paddingLarge)
                Box(modifier = Modifier.fillMaxSize()) {
                    LazyColumn(
                        modifier = Modifier
                            .fillMaxSize()
                            .systemBarsPadding(),
                        contentPadding = PaddingValues(top = Dimens.paddingLarge, bottom = 96.dp),
                        verticalArrangement = Arrangement.spacedBy(Dimens.paddingLarge)
                    ) {
                        item {
                            Column(modifier = sidePadding) {
                                Text(
                                    text = todayLabel(),
                                    style = MaterialTheme.typography.bodyMedium,
                                    color = MaterialTheme.colorScheme.onSurfaceVariant
                                )
                                Text(
                                    text = greetingForNow(),
                                    style = MaterialTheme.typography.headlineMedium,
                                    fontWeight = FontWeight.Bold,
                                    color = MaterialTheme.colorScheme.onBackground
                                )
                            }
                        }
                        item {
                            TodayCarousel(
                                tasks = uiState.todayTasks,
                                isDone = { uiState.isDone(it.id, uiState.todayEpochDay) },
                                onToggle = { task, done ->
                                    onToggleTask(
                                        task.id,
                                        uiState.todayEpochDay,
                                        done
                                    )
                                }
                            )
                        }
                        item { SectionTitle("My week", sidePadding) }
                        item {
                            WeekTable(
                                state = uiState,
                                onToggle = onToggleTask,
                                modifier = sidePadding
                            )
                        }
                        item { SectionTitle("Today's checklist", sidePadding) }
                        item {
                            TodayChecklist(
                                state = uiState,
                                onToggle = onToggleTask,
                                modifier = sidePadding
                            )
                        }
                        item { SectionTitle("Notes", sidePadding) }
                        item {
                            NotesSection(
                                notes = uiState.notes,
                                onAddNote = onAddNote,
                                modifier = sidePadding
                            )
                        }
                        item { SectionTitle("Daily progress", sidePadding) }
                        item {
                            DailyProgressCard(
                                done = uiState.doneTodayCount,
                                total = uiState.todayTasks.size,
                                modifier = sidePadding
                            )
                        }
                        item { SectionTitle("Weekly statistics", sidePadding) }
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
                        Icon(imageVector = WeaveIcons.Plus, contentDescription = "Add task")
                    }
                }
            }
        }
    }
}

private fun greetingForNow(): String {
    val hour = Calendar.getInstance().get(Calendar.HOUR_OF_DAY)
    return when {
        hour < 12 -> "Good morning"
        hour < 18 -> "Good afternoon"
        else -> "Good evening"
    }
}

private fun todayLabel(): String =
    SimpleDateFormat("EEEE, d MMM", Locale.getDefault()).format(Date())

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
            onToggleTask = { _, _, _ -> },
            onAddNote = {},
            onRetry = {}
        )
    }
}