package com.shinewhite.weave.ui.screens

import android.Manifest
import android.content.Context
import android.content.Intent
import android.content.res.Configuration
import android.net.Uri
import android.os.Build
import android.provider.Settings
import androidx.activity.compose.BackHandler
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
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
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalLayoutDirection
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.LayoutDirection
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.LifecycleResumeEffect
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import com.shinewhite.weave.database.entity.TaskCategory
import com.shinewhite.weave.localization.LocalStrings
import com.shinewhite.weave.localization.PersianStrings
import com.shinewhite.weave.reminder.ReminderPermissions
import com.shinewhite.weave.ui.theme.AppTheme
import com.shinewhite.weave.ui.theme.Dimens

@Composable
fun AddTaskScreen(taskId: Long?, onBack: () -> Unit, viewModel: AddTaskViewModel = viewModel()) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()
    val context = LocalContext.current
    val notificationPermissionLauncher = rememberLauncherForActivityResult(
        ActivityResultContracts.RequestPermission()
    ) { }
    var canScheduleExact by remember { mutableStateOf(ReminderPermissions.canScheduleExactAlarms(context)) }

    // The exact alarm permission is granted in system settings, so it is checked again on return
    LifecycleResumeEffect(Unit) {
        canScheduleExact = ReminderPermissions.canScheduleExactAlarms(context)
        onPauseOrDispose { }
    }

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
        onTimeChange = { minutes ->
            viewModel.onTimeChange(minutes)
            if (minutes != null && !ReminderPermissions.hasNotificationPermission(context)) {
                notificationPermissionLauncher.launch(Manifest.permission.POST_NOTIFICATIONS)
            }
        },
        showExactAlarmNotice = uiState.timeMinutes != null && !canScheduleExact,
        onAllowExactAlarms = { openExactAlarmSettings(context) },
        onSave = viewModel::saveTask
    )
}

private fun openExactAlarmSettings(context: Context) {
    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
        context.startActivity(
            Intent(Settings.ACTION_REQUEST_SCHEDULE_EXACT_ALARM, Uri.parse("package:${context.packageName}"))
        )
    }
}

@Composable
fun AddTaskScreenContent(
    uiState: AddTaskUiState,
    onBack: () -> Unit,
    onTitleChange: (String) -> Unit,
    onCategoryChange: (TaskCategory) -> Unit,
    onDayToggle: (Int) -> Unit,
    onTimeChange: (Int?) -> Unit,
    showExactAlarmNotice: Boolean,
    onAllowExactAlarms: () -> Unit,
    onSave: () -> Unit
) {
    val strings = LocalStrings.current
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
                        contentDescription = strings.back,
                        tint = MaterialTheme.colorScheme.onBackground
                    )
                }
                Text(
                    text = if (uiState.isEditing) strings.editTask else strings.newTask,
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
                    label = { Text(strings.title) },
                    placeholder = { Text(strings.titlePlaceholder) },
                    singleLine = true
                )

                CategoryDropdown(
                    selected = uiState.category,
                    onSelected = onCategoryChange
                )

                Column(verticalArrangement = Arrangement.spacedBy(Dimens.paddingSmall)) {
                    Text(
                        text = strings.repeatOn,
                        style = MaterialTheme.typography.labelLarge,
                        color = MaterialTheme.colorScheme.onBackground
                    )
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        strings.dayLetters.forEachIndexed { index, label ->
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

                if (showExactAlarmNotice) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Text(
                            text = strings.allowExactAlarms,
                            modifier = Modifier.weight(1f),
                            style = MaterialTheme.typography.bodyMedium,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                        TextButton(onClick = onAllowExactAlarms) { Text(strings.allow) }
                    }
                }

                uiState.error?.let { error ->
                    Text(
                        text = when (error) {
                            AddTaskError.SAVE_FAILED -> strings.couldNotSave
                            AddTaskError.LOAD_FAILED -> strings.couldNotLoad
                        },
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
                        uiState.isSaving -> strings.saving
                        uiState.isEditing -> strings.saveChanges
                        else -> strings.saveTask
                    }
                )
            }
            Spacer(Modifier.height(Dimens.paddingMedium))
        }
    }
}

@Composable
private fun CategoryDropdown(selected: TaskCategory, onSelected: (TaskCategory) -> Unit) {
    val strings = LocalStrings.current
    var expanded by remember { mutableStateOf(false) }

    Column(verticalArrangement = Arrangement.spacedBy(Dimens.paddingSmall)) {
        Text(
            text = strings.category,
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
                    text = strings.categoryName(selected),
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
                        text = { Text(strings.categoryName(category)) },
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
    val strings = LocalStrings.current
    val shape = RoundedCornerShape(Dimens.cornerRadius)

    Column(verticalArrangement = Arrangement.spacedBy(Dimens.paddingSmall)) {
        Text(
            text = strings.timeOptional,
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
                    text = timeMinutes?.let { strings.formatTime(it) } ?: strings.noTimeSet,
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
                TextButton(onClick = onClear) { Text(strings.clear) }
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun TimePickerDialog(initialMinutes: Int?, onConfirm: (Int) -> Unit, onDismiss: () -> Unit) {
    val strings = LocalStrings.current
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
                Text(strings.ok)
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) { Text(strings.cancel) }
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
            showExactAlarmNotice = false,
            onAllowExactAlarms = {},
            onSave = {}
        )
    }
}

@Preview(name = "Persian RTL", showBackground = true, heightDp = 800)
@Composable
private fun AddTaskScreenPersianPreview() {
    AppTheme {
        CompositionLocalProvider(
            LocalStrings provides PersianStrings,
            LocalLayoutDirection provides LayoutDirection.Rtl
        ) {
            AddTaskScreenContent(
                uiState = AddTaskUiState(
                    title = "تمرین باشگاه",
                    category = TaskCategory.GYM,
                    activeDaysMask = 0b0010101,
                    timeMinutes = 15 * 60 + 15
                ),
                onBack = {},
                onTitleChange = {},
                onCategoryChange = {},
                onDayToggle = {},
                onTimeChange = {},
                showExactAlarmNotice = false,
                onAllowExactAlarms = {},
                onSave = {}
            )
        }
    }
}