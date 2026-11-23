package com.shinewhite.weave.ui.screens

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.shinewhite.weave.database.entity.NoteEntity
import com.shinewhite.weave.database.entity.TaskCompletionEntity
import com.shinewhite.weave.database.entity.TaskEntity
import com.shinewhite.weave.database.repository.NoteRepository
import com.shinewhite.weave.database.repository.TaskRepository
import com.shinewhite.weave.utils.EpochDay
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

@HiltViewModel
class HomeViewModel @Inject constructor(
    private val taskRepository: TaskRepository,
    private val noteRepository: NoteRepository
) : ViewModel() {
    private val _uiState: MutableStateFlow<HomeUiState> = MutableStateFlow(HomeUiState.Loading)
    val uiState: StateFlow<HomeUiState> = _uiState.asStateFlow()

    private val _isRefreshing = MutableStateFlow(false)
    val isRefreshing: StateFlow<Boolean> = _isRefreshing.asStateFlow()

    init {
        loadData()
    }

    fun loadData() {
        _uiState.value = HomeUiState.Loading

        viewModelScope.launch {
            _isRefreshing.value = true
            fetchAll()
            _isRefreshing.value = false
        }
    }

    // Reloads without showing the Loading state, used when returning to the screen
    fun refreshData() {
        if (_isRefreshing.value) return

        viewModelScope.launch {
            _isRefreshing.value = true
            fetchAll()
            _isRefreshing.value = false
        }
    }

    fun deleteTask(taskId: Long) {
        viewModelScope.launch {
            taskRepository.deleteTask(taskId)
                .onSuccess { fetchAll() }
                .onFailure { _uiState.value = HomeUiState.Error(it) }
        }
    }

    fun toggleTask(taskId: Long, epochDay: Long, done: Boolean) {
        viewModelScope.launch {
            taskRepository.setTaskDone(taskId, epochDay, done)
                .onSuccess { refreshCompletions() }
                .onFailure { _uiState.value = HomeUiState.Error(it) }
        }
    }

    fun addNote(text: String) {
        val trimmed = text.trim()
        if (trimmed.isEmpty()) return

        viewModelScope.launch {
            noteRepository.addNote(EpochDay.today(), trimmed)
                .onSuccess { refreshNotes() }
                .onFailure { _uiState.value = HomeUiState.Error(it) }
        }
    }

    private suspend fun fetchAll() {
        taskRepository.getAllTasks()
            .onSuccess { tasks -> updateSuccess { it.copy(tasks = tasks) } }
            .onFailure { _uiState.value = HomeUiState.Error(it) }

        refreshCompletions()
        refreshNotes()
    }

    private suspend fun refreshCompletions() {
        val weekStart = EpochDay.weekStart(EpochDay.today())
        taskRepository.getCompletions(weekStart, weekStart + 6)
            .onSuccess { completions -> updateSuccess { it.copy(completions = completions) } }
            .onFailure { _uiState.value = HomeUiState.Error(it) }
    }

    private suspend fun refreshNotes() {
        noteRepository.getNotesForDay(EpochDay.today())
            .onSuccess { notes -> updateSuccess { it.copy(notes = notes) } }
            .onFailure { _uiState.value = HomeUiState.Error(it) }
    }

    // Keeps an existing Error state so a late success cannot hide a failure
    private fun updateSuccess(transform: (HomeUiState.Success) -> HomeUiState.Success) {
        _uiState.update { state ->
            when (state) {
                is HomeUiState.Success -> transform(state)
                HomeUiState.Loading -> transform(HomeUiState.Success())
                is HomeUiState.Error -> state
            }
        }
    }
}

sealed interface HomeUiState {
    data object Loading : HomeUiState

    data class Success(
        val todayEpochDay: Long = EpochDay.today(),
        val tasks: List<TaskEntity> = emptyList(),
        val completions: List<TaskCompletionEntity> = emptyList(),
        val notes: List<NoteEntity> = emptyList()
    ) : HomeUiState {
        val weekStart: Long get() = EpochDay.weekStart(todayEpochDay)
        val todayIndex: Int get() = EpochDay.dayIndex(todayEpochDay)
        val todayTasks: List<TaskEntity> get() = tasks.filter { it.isActiveOn(todayIndex) }
        val doneTodayCount: Int get() = todayTasks.count { isDone(it.id, todayEpochDay) }

        fun isDone(taskId: Long, epochDay: Long): Boolean =
            completions.any { it.taskId == taskId && it.epochDay == epochDay }

        fun completedCountOn(dayIndex: Int): Int =
            completions.count { it.epochDay == weekStart + dayIndex }
    }

    data class Error(val exception: Throwable) : HomeUiState
}