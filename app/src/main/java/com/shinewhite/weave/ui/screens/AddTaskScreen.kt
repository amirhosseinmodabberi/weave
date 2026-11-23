package com.shinewhite.weave.ui.screens

import android.content.res.Configuration
import androidx.activity.compose.BackHandler
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.imePadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.systemBarsPadding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.selection.toggleable
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TimePicker
import androidx.compose.material3.rememberTimePickerState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import com.shinewhite.weave.database.entity.TaskCategory
import com.shinewhite.weave.ui.theme.AppTheme
import com.shinewhite.weave.ui.theme.Dimens

private val DAY_SHORT_LABELS = listOf("Mo", "Tu", "We", "Th", "Fr", "Sa", "Su")

@Composable
fun AddTaskScreen(taskId: Long?, onBack: () -> Unit, viewModel: AddTaskViewModel = viewModel()) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()
    val goBack = {
        viewModel.resetForm()
        onBack()
    }

    LaunchedEffect(taskId) { viewModel.startForm(taskId) }
    BackHandler(onBack = goBack)
    LaunchedEffect(uiState.isSaved) {
        if (uiState.isSaved) goBack()
    }

    AddTaskScreenContent(
        uiState = uiState,
        onBack = goBack,
        onTitleChange = viewModel::onTitleChange,
        onCategoryChange = viewModel::onCategoryChange,
        onDayToggle = viewModel::onDayToggle,
        onTimeChange = viewModel::onTimeChange,
        onSave = viewModel::saveTask
    )
}

@Composable
fun AddTaskScreenContent(
    uiState: AddTaskUiState,
    onBack: () -> Unit,
    onTitleChange: (String) -> Unit,
    onCategoryChange: (TaskCategory) -> Unit,
    onDayToggle: (Int) -> Unit,
    onTimeChange: (Int?) -> Unit,
    onSave: () -> Unit
) {
    var showTimePicker by remember { mutableStateOf(false) }

    if (showTimePicker) {
        TimePickerDialog(
            initialMinutes = uiState.timeMinutes,
            onConfirm = {
                onTimeChange(it)
                showTimePicker = false
            },
            onDismiss = { showTimePicker = false }
        )
    }

    Surface(modifier = Modifier.fillMaxSize(), color = MaterialTheme.colorScheme.background) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .systemBarsPadding()
                .imePadding()
                .padding(horizontal = Dimens.paddingLarge)
        ) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                IconButton(onClick = onBack, modifier = Modifier.size(Dimens.minTouchTarget)) {
                    Icon(
                        imageVector = WeaveIcons.Back,
                        contentDescription = "Back",
                        tint = MaterialTheme.colorScheme.onBackground
                    )
                }
                Text(
                    text = if (uiState.isEditing) "Edit task" else "New task",
                    modifier = Modifier.padding(start = Dimens.paddingSmall),
                    style = MaterialTheme.typography.titleLarge,
                    color = MaterialTheme.colorScheme.onBackground
                )
            }

            Column(
                modifier = Modifier
                    .weight(1f)
                    .verticalScroll(rememberScrollState())
                    .padding(vertical = Dimens.paddingMedium),
                verticalArrangement = Arrangement.spacedBy(Dimens.paddingLarge)
            ) {
                OutlinedTextField(
                    value = uiState.title,
                    onValueChange = onTitleChange,
                    modifier = Modifier.fillMaxWidth(),
                    label = { Text("Title") },
                    placeholder = { Text("What do you want to do?") },
                    singleLine = true
                )

                CategoryDropdown(
                    selected = uiState.category,
                    onSelected = onCategoryChange
                )

                Column(verticalArrangement = Arrangement.spacedBy(Dimens.paddingSmall)) {
                    Text(
                        text = "Repeat on",
                        style = MaterialTheme.typography.labelLarge,
                        color = MaterialTheme.colorScheme.onBackground
                    )
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        DAY_SHORT_LABELS.forEachIndexed { index, label ->
                            DayChip(
                                label = label,
                                selected = (uiState.activeDaysMask and (1 shl index)) != 0,
                                onToggle = { onDayToggle(index) }
                            )
                        }
                    }
                }

                TimeField(
                    timeMinutes = uiState.timeMinutes,
                    onClick = { showTimePicker = true },
                    onClear = { onTimeChange(null) }
                )

                uiState.errorMessage?.let {
                    Text(
                        text = it,
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.error
                    )
                }
            }

            Button(
                onClick = onSave,
                enabled = uiState.canSave,
                modifier = Modifier
                    .fillMaxWidth()
                    .height(Dimens.buttonHeight),
                shape = RoundedCornerShape(Dimens.cornerRadius)
            ) {
                Text(
                    when {
                        uiState.isSaving -> "Saving..."
                        uiState.isEditing -> "Save changes"
                        else -> "Save task"
                    }
                )
            }
            Spacer(Modifier.height(Dimens.paddingMedium))
        }
    }
}

@Composable
private fun CategoryDropdown(selected: TaskCategory, onSelected: (TaskCategory) -> Unit) {
    var expanded by remember { mutableStateOf(false) }

    Column(verticalArrangement = Arrangement.spacedBy(Dimens.paddingSmall)) {
        Text(
            text = "Category",
            style = MaterialTheme.typography.labelLarge,
            color = MaterialTheme.colorScheme.onBackground
        )
        Box {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(Dimens.buttonHeight)
                    .clip(RoundedCornerShape(Dimens.cornerRadius))
                    .border(
                        width = if (expanded) 2.dp else 1.dp,
                        color = if (expanded) {
                            MaterialTheme.colorScheme.secondary
                        } else {
                            MaterialTheme.colorScheme.outline
                        },
                        shape = RoundedCornerShape(Dimens.cornerRadius)
                    )
                    .clickable(role = Role.DropdownList) { expanded = true }
                    .padding(horizontal = Dimens.paddingMedium),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Icon(
                    imageVector = WeaveIcons.forCategory(selected),
                    contentDescription = null,
                    tint = MaterialTheme.colorScheme.secondary
                )
                Text(
                    text = selected.displayName(),
                    modifier = Modifier
                        .weight(1f)
                        .padding(start = Dimens.paddingMedium),
                    style = MaterialTheme.typography.bodyLarge,
                    color = MaterialTheme.colorScheme.onBackground
                )
                Icon(
                    imageVector = WeaveIcons.ChevronDown,
                    contentDescription = null,
                    tint = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
            DropdownMenu(expanded = expanded, onDismissRequest = { expanded = false }) {
                TaskCategory.entries.forEach { category ->
                    DropdownMenuItem(
                        text = { Text(category.displayName()) },
                        leadingIcon = {
                            Icon(
                                imageVector = WeaveIcons.forCategory(category),
                                contentDescription = null
                            )
                        },
                        onClick = {
                            onSelected(category)
                            expanded = false
                        }
                    )
                }
            }
        }
    }
}

@Composable
private fun TimeField(timeMinutes: Int?, onClick: () -> Unit, onClear: () -> Unit) {
    val shape = RoundedCornerShape(Dimens.cornerRadius)

    Column(verticalArrangement = Arrangement.spacedBy(Dimens.paddingSmall)) {
        Text(
            text = "Time (optional)",
            style = MaterialTheme.typography.labelLarge,
            color = MaterialTheme.colorScheme.onBackground
        )
        Row(verticalAlignment = Alignment.CenterVertically) {
            Row(
                modifier = Modifier
                    .weight(1f)
                    .height(Dimens.buttonHeight)
                    .clip(shape)
                    .border(1.dp, MaterialTheme.colorScheme.outline, shape)
                    .clickable(role = Role.Button, onClick = onClick)
                    .padding(horizontal = Dimens.paddingMedium),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Icon(
                    imageVector = WeaveIcons.Clock,
                    contentDescription = null,
                    tint = MaterialTheme.colorScheme.secondary
                )
                Text(
                    text = timeMinutes?.let { formatTime(it) } ?: "No time set",
                    modifier = Modifier.padding(start = Dimens.paddingMedium),
                    style = MaterialTheme.typography.bodyLarge,
                    color = if (timeMinutes == null) {
                        MaterialTheme.colorScheme.onSurfaceVariant
                    } else {
                        MaterialTheme.colorScheme.onBackground
                    }
                )
            }
            if (timeMinutes != null) {
                TextButton(onClick = onClear) { Text("Clear") }
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun TimePickerDialog(initialMinutes: Int?, onConfirm: (Int) -> Unit, onDismiss: () -> Unit) {
    val initial = initialMinutes ?: (9 * 60)
    val pickerState = rememberTimePickerState(
        initialHour = initial / 60,
        initialMinute = initial % 60,
        is24Hour = false
    )

    AlertDialog(
        onDismissRequest = onDismiss,
        confirmButton = {
            TextButton(onClick = { onConfirm(pickerState.hour * 60 + pickerState.minute) }) {
                Text("OK")
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) { Text("Cancel") }
        },
        text = { TimePicker(state = pickerState) }
    )
}

@Composable
private fun DayChip(label: String, selected: Boolean, onToggle: () -> Unit) {
    val shape = RoundedCornerShape(Dimens.cornerRadius)
    Box(
        modifier = Modifier
            .size(Dimens.minTouchTarget)
            .clip(shape)
            .then(
                if (selected) {
                    Modifier.background(MaterialTheme.colorScheme.primary)
                } else {
                    Modifier.border(1.dp, MaterialTheme.colorScheme.outline, shape)
                }
            )
            .toggleable(value = selected, onValueChange = { onToggle() }, role = Role.Checkbox),
        contentAlignment = Alignment.Center
    ) {
        Text(
            text = label,
            style = MaterialTheme.typography.labelLarge,
            fontWeight = FontWeight.SemiBold,
            textAlign = TextAlign.Center,
            color = if (selected) {
                MaterialTheme.colorScheme.onPrimary
            } else {
                MaterialTheme.colorScheme.onBackground
            }
        )
    }
}

@Preview(name = "Light", showBackground = true, heightDp = 800)
@Preview(
    name = "Dark",
    showBackground = true,
    heightDp = 800,
    uiMode = Configuration.UI_MODE_NIGHT_YES
)
@Composable
private fun AddTaskScreenPreview() {
    AppTheme {
        AddTaskScreenContent(
            uiState = AddTaskUiState(
                title = "Morning workout",
                category = TaskCategory.GYM,
                activeDaysMask = 0b0010101,
                timeMinutes = 7 * 60
            ),
            onBack = {},
            onTitleChange = {},
            onCategoryChange = {},
            onDayToggle = {},
            onTimeChange = {},
            onSave = {}
        )
    }
}