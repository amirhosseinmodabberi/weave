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
import androidx.compose.material3.Button
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
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
fun AddTaskScreen(onBack: () -> Unit, viewModel: AddTaskViewModel = viewModel()) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()
    val goBack = {
        viewModel.resetForm()
        onBack()
    }

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
    onSave: () -> Unit
) {
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
                    text = "New task",
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
                Text(if (uiState.isSaving) "Saving..." else "Save task")
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
                activeDaysMask = 0b0010101
            ),
            onBack = {},
            onTitleChange = {},
            onCategoryChange = {},
            onDayToggle = {},
            onSave = {}
        )
    }
}