package com.shinewhite.weave.ui.screens

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.shinewhite.weave.database.entity.TaskCategory
import com.shinewhite.weave.database.entity.TaskEntity
import com.shinewhite.weave.database.repository.TaskRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

private const val EVERY_DAY_MASK = 0b1111111

@HiltViewModel
class AddTaskViewModel @Inject constructor(
    private val taskRepository: TaskRepository
) : ViewModel() {
    private val _uiState = MutableStateFlow(AddTaskUiState())
    val uiState: StateFlow<AddTaskUiState> = _uiState.asStateFlow()

    fun onTitleChange(title: String) {
        _uiState.update { it.copy(title = title) }
    }

    fun onCategoryChange(category: TaskCategory) {
        _uiState.update { it.copy(category = category) }
    }

    fun onDayToggle(dayIndex: Int) {
        _uiState.update { it.copy(activeDaysMask = it.activeDaysMask xor (1 shl dayIndex)) }
    }

    fun saveTask() {
        val current = _uiState.value
        if (!current.canSave) return

        _uiState.update { it.copy(isSaving = true, errorMessage = null) }

        viewModelScope.launch {
            val task = TaskEntity(
                title = current.title.trim(),
                category = current.category,
                activeDaysMask = current.activeDaysMask
            )
            taskRepository.addTask(task)
                .onSuccess { _uiState.update { state -> state.copy(isSaving = false, isSaved = true) } }
                .onFailure {
                    _uiState.update { state ->
                        state.copy(isSaving = false, errorMessage = "Could not save the task")
                    }
                }
        }
    }

    // The ViewModel outlives the screen, so the form is cleared when the user leaves it
    fun resetForm() {
        _uiState.value = AddTaskUiState()
    }
}

data class AddTaskUiState(
    val title: String = "",
    val category: TaskCategory = TaskCategory.OTHER,
    val activeDaysMask: Int = EVERY_DAY_MASK,
    val isSaving: Boolean = false,
    val isSaved: Boolean = false,
    val errorMessage: String? = null
) {
    val canSave: Boolean get() = title.isNotBlank() && activeDaysMask != 0 && !isSaving
}