package com.shinewhite.weave.ui.screens

import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.collectIsDraggedAsState
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.foundation.selection.toggleable
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.key
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.scale
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import com.shinewhite.weave.database.entity.NoteEntity
import com.shinewhite.weave.database.entity.TaskEntity
import com.shinewhite.weave.localization.LocalStrings
import com.shinewhite.weave.ui.screens.HomeUiState
import com.shinewhite.weave.ui.screens.WeaveIcons
import com.shinewhite.weave.ui.theme.Dimens
import kotlinx.coroutines.delay
import kotlin.math.roundToInt
import kotlin.time.Duration.Companion.seconds

private val AUTO_SCROLL_DELAY = 4.seconds
private val MAX_BAR_HEIGHT = 100.dp

@Composable
internal fun SectionTitle(text: String, modifier: Modifier = Modifier) {
    Text(
        text = text,
        modifier = modifier,
        style = MaterialTheme.typography.titleLarge,
        color = MaterialTheme.colorScheme.onBackground
    )
}

@Composable
internal fun SectionCard(modifier: Modifier = Modifier, content: @Composable () -> Unit) {
    Card(
        modifier = modifier.fillMaxWidth(),
        shape = RoundedCornerShape(Dimens.cardCornerRadius),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        content()
    }
}

@Composable
internal fun EmptyHint(text: String, modifier: Modifier = Modifier) {
    SectionCard(modifier) {
        Text(
            text = text,
            modifier = Modifier.padding(Dimens.paddingMedium),
            style = MaterialTheme.typography.bodyLarge,
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )
    }
}

@Composable
internal fun TaskCheckbox(done: Boolean, modifier: Modifier = Modifier, size: Dp = 24.dp) {
    val shape = RoundedCornerShape(8.dp)
    val fillModifier = if (done) {
        Modifier.background(MaterialTheme.colorScheme.tertiary)
    } else {
        Modifier.border(2.dp, MaterialTheme.colorScheme.onSurfaceVariant, shape)
    }
    Box(
        modifier = modifier
            .size(size)
            .clip(shape)
            .then(fillModifier),
        contentAlignment = Alignment.Center
    ) {
        if (done) {
            Icon(
                imageVector = WeaveIcons.Check,
                contentDescription = null,
                tint = MaterialTheme.colorScheme.onTertiary,
                modifier = Modifier.size(size * 0.7f)
            )
        }
    }
}

@Composable
internal fun TaskActionsMenu(
    expanded: Boolean,
    onDismiss: () -> Unit,
    onEdit: () -> Unit,
    onDelete: () -> Unit
) {
    val strings = LocalStrings.current
    DropdownMenu(expanded = expanded, onDismissRequest = onDismiss) {
        DropdownMenuItem(
            text = { Text(strings.edit) },
            leadingIcon = { Icon(imageVector = WeaveIcons.Edit, contentDescription = null) },
            onClick = onEdit
        )
        DropdownMenuItem(
            text = { Text(strings.delete, color = MaterialTheme.colorScheme.error) },
            leadingIcon = {
                Icon(
                    imageVector = WeaveIcons.Delete,
                    contentDescription = null,
                    tint = MaterialTheme.colorScheme.error
                )
            },
            onClick = onDelete
        )
    }
}

@Composable
internal fun DeleteTaskDialog(task: TaskEntity, onConfirm: () -> Unit, onDismiss: () -> Unit) {
    val strings = LocalStrings.current
    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text(strings.deleteTaskTitle) },
        text = { Text(strings.deleteTaskMessage(task.title)) },
        confirmButton = {
            TextButton(onClick = onConfirm) {
                Text(strings.delete, color = MaterialTheme.colorScheme.error)
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) { Text(strings.cancel) }
        }
    )
}

@Composable
internal fun TodayCarousel(
    tasks: List<TaskEntity>,
    isDone: (TaskEntity) -> Boolean,
    onToggle: (TaskEntity, Boolean) -> Unit,
    onEdit: (TaskEntity) -> Unit,
    onDelete: (TaskEntity) -> Unit
) {
    val strings = LocalStrings.current
    if (tasks.isEmpty()) {
        EmptyHint(
            text = strings.emptyToday,
            modifier = Modifier.padding(horizontal = Dimens.paddingLarge)
        )
        return
    }

    val pagerState = rememberPagerState(pageCount = { tasks.size })
    val isDragged by pagerState.interactionSource.collectIsDraggedAsState()

    // Auto-advance is paused while the user is dragging the carousel
    // settledPage changes only after the animation ends, so it is not cancelled halfway
    LaunchedEffect(pagerState.settledPage, isDragged, tasks.size) {
        if (!isDragged && tasks.size > 1) {
            delay(AUTO_SCROLL_DELAY)
            pagerState.animateScrollToPage((pagerState.settledPage + 1) % tasks.size)
        }
    }

    Column {
        HorizontalPager(
            state = pagerState,
            contentPadding = PaddingValues(horizontal = Dimens.paddingLarge),
            pageSpacing = Dimens.paddingMedium
        ) { page ->
            val task = tasks[page]
            val done = isDone(task)
            var menuOpen by remember { mutableStateOf(false) }
            Column {
                Box {
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(160.dp)
                            .clip(RoundedCornerShape(Dimens.cardCornerRadius))
                            .background(MaterialTheme.colorScheme.primary)
                            .clickable(onClickLabel = strings.taskOptions) { menuOpen = true }
                            .padding(Dimens.paddingMedium),
                        verticalArrangement = Arrangement.SpaceBetween
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Box(
                                modifier = Modifier
                                    .size(40.dp)
                                    .clip(RoundedCornerShape(Dimens.cornerRadius))
                                    .background(MaterialTheme.colorScheme.onPrimary.copy(alpha = 0.2f)),
                                contentAlignment = Alignment.Center
                            ) {
                                Icon(
                                    imageVector = WeaveIcons.forCategory(task.category),
                                    contentDescription = null,
                                    tint = MaterialTheme.colorScheme.onPrimary
                                )
                            }
                            Spacer(Modifier.width(Dimens.paddingSmall))
                            Text(
                                text = strings.categoryName(task.category),
                                style = MaterialTheme.typography.labelLarge,
                                color = MaterialTheme.colorScheme.onPrimary
                            )
                        }
                        Column {
                            Text(
                                text = task.title,
                                style = MaterialTheme.typography.headlineSmall,
                                fontWeight = FontWeight.Bold,
                                color = MaterialTheme.colorScheme.onPrimary,
                                maxLines = 2,
                                overflow = TextOverflow.Ellipsis
                            )
                            task.timeMinutes?.let {
                                Text(
                                    text = strings.formatTime(it),
                                    style = MaterialTheme.typography.bodyMedium,
                                    color = MaterialTheme.colorScheme.onPrimary
                                )
                            }
                        }
                    }
                    TaskActionsMenu(
                        expanded = menuOpen,
                        onDismiss = { menuOpen = false },
                        onEdit = {
                            menuOpen = false
                            onEdit(task)
                        },
                        onDelete = {
                            menuOpen = false
                            onDelete(task)
                        }
                    )
                }
                Row(
                    modifier = Modifier
                        .height(Dimens.minTouchTarget)
                        .toggleable(
                            value = done,
                            onValueChange = { onToggle(task, it) },
                            role = Role.Checkbox
                        ),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    TaskCheckbox(done = done)
                    Spacer(Modifier.width(Dimens.paddingSmall))
                    Text(
                        text = if (done) strings.done else strings.markAsDone,
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.onBackground
                    )
                }
            }
        }

        if (tasks.size > 1) {
            PagerDots(
                pageCount = tasks.size,
                currentPage = pagerState.currentPage,
                modifier = Modifier
                    .align(Alignment.CenterHorizontally)
                    .padding(top = Dimens.paddingSmall)
            )
        }
    }
}

@Composable
private fun PagerDots(pageCount: Int, currentPage: Int, modifier: Modifier = Modifier) {
    Row(
        modifier = modifier,
        horizontalArrangement = Arrangement.spacedBy(8.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        repeat(pageCount) { index ->
            val selected = index == currentPage
            val color by animateColorAsState(
                targetValue = if (selected) {
                    MaterialTheme.colorScheme.secondary
                } else {
                    MaterialTheme.colorScheme.outline
                },
                animationSpec = tween(durationMillis = 300),
                label = "dotColor"
            )
            val dotScale by animateFloatAsState(
                targetValue = if (selected) 1.3f else 1f,
                animationSpec = tween(durationMillis = 300),
                label = "dotScale"
            )
            Box(
                modifier = Modifier
                    .size(7.dp)
                    .scale(dotScale)
                    .clip(CircleShape)
                    .background(color)
            )
        }
    }
}

@Composable
internal fun WeekTable(
    state: HomeUiState.Success,
    onToggle: (taskId: Long, epochDay: Long, done: Boolean) -> Unit,
    onEdit: (TaskEntity) -> Unit,
    onDelete: (TaskEntity) -> Unit,
    modifier: Modifier = Modifier
) {
    val strings = LocalStrings.current
    if (state.tasks.isEmpty()) {
        EmptyHint(strings.emptyWeek, modifier)
        return
    }

    SectionCard(modifier) {
        Column(modifier = Modifier.padding(Dimens.paddingSmall)) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Spacer(Modifier.weight(3f))
                strings.dayLetters.forEachIndexed { index, label ->
                    val isToday = index == state.todayIndex
                    Text(
                        text = label,
                        modifier = Modifier
                            .weight(1f)
                            .padding(vertical = Dimens.paddingSmall),
                        style = MaterialTheme.typography.labelLarge,
                        fontWeight = FontWeight.Bold,
                        color = if (isToday) {
                            MaterialTheme.colorScheme.secondary
                        } else {
                            MaterialTheme.colorScheme.onSurfaceVariant
                        },
                        textAlign = androidx.compose.ui.text.style.TextAlign.Center
                    )
                }
            }
            state.tasks.forEach { task ->
                key(task.id) {
                    var menuOpen by remember { mutableStateOf(false) }
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Box(modifier = Modifier.weight(3f)) {
                            Row(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .height(44.dp)
                                    .clickable(onClickLabel = strings.taskOptions) { menuOpen = true },
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Icon(
                                    imageVector = WeaveIcons.forCategory(task.category),
                                    contentDescription = null,
                                    tint = MaterialTheme.colorScheme.secondary,
                                    modifier = Modifier.size(18.dp)
                                )
                                Spacer(Modifier.width(Dimens.paddingSmall))
                                Text(
                                    text = task.title,
                                    style = MaterialTheme.typography.bodyMedium,
                                    color = MaterialTheme.colorScheme.onSurface,
                                    maxLines = 1,
                                    overflow = TextOverflow.Ellipsis
                                )
                            }
                            TaskActionsMenu(
                                expanded = menuOpen,
                                onDismiss = { menuOpen = false },
                                onEdit = {
                                    menuOpen = false
                                    onEdit(task)
                                },
                                onDelete = {
                                    menuOpen = false
                                    onDelete(task)
                                }
                            )
                        }
                        strings.dayLetters.indices.forEach { index ->
                            val epochDay = state.weekStart + index
                            val active = task.isActiveOn(index)
                            val editable = active && epochDay <= state.todayEpochDay
                            val done = state.isDone(task.id, epochDay)
                            val cellBackground = if (index == state.todayIndex) {
                                MaterialTheme.colorScheme.surfaceVariant
                            } else {
                                Color.Transparent
                            }
                            val cellModifier = if (editable) {
                                Modifier.toggleable(
                                    value = done,
                                    onValueChange = { onToggle(task.id, epochDay, it) },
                                    role = Role.Checkbox
                                )
                            } else {
                                Modifier
                            }
                            Box(
                                modifier = Modifier
                                    .weight(1f)
                                    .height(44.dp)
                                    .background(cellBackground)
                                    .then(cellModifier),
                                contentAlignment = Alignment.Center
                            ) {
                                if (active) {
                                    TaskCheckbox(done = done, size = 22.dp)
                                } else {
                                    Box(
                                        modifier = Modifier
                                            .size(6.dp)
                                            .clip(CircleShape)
                                            .background(MaterialTheme.colorScheme.outlineVariant)
                                    )
                                }
                            }
                        }
                    }
                }
            }
        }
    }
}

@Composable
internal fun TodayChecklist(
    state: HomeUiState.Success,
    onToggle: (taskId: Long, epochDay: Long, done: Boolean) -> Unit,
    onEdit: (TaskEntity) -> Unit,
    onDelete: (TaskEntity) -> Unit,
    modifier: Modifier = Modifier
) {
    val strings = LocalStrings.current
    if (state.todayTasks.isEmpty()) {
        EmptyHint(strings.emptyChecklist, modifier)
        return
    }

    Column(modifier = modifier, verticalArrangement = Arrangement.spacedBy(Dimens.paddingSmall)) {
        state.todayTasks.forEach { task ->
            key(task.id) {
                val done = state.isDone(task.id, state.todayEpochDay)
                var menuOpen by remember { mutableStateOf(false) }
                SectionCard {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .heightIn(min = Dimens.buttonHeight)
                            .padding(start = Dimens.paddingSmall),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Box(
                            modifier = Modifier
                                .size(Dimens.minTouchTarget)
                                .toggleable(
                                    value = done,
                                    onValueChange = { onToggle(task.id, state.todayEpochDay, it) },
                                    role = Role.Checkbox
                                ),
                            contentAlignment = Alignment.Center
                        ) {
                            TaskCheckbox(done = done)
                        }
                        Box(modifier = Modifier.weight(1f)) {
                            Row(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .heightIn(min = Dimens.buttonHeight)
                                    .clickable(onClickLabel = strings.taskOptions) { menuOpen = true }
                                    .padding(horizontal = Dimens.paddingSmall, vertical = Dimens.paddingSmall),
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Column {
                                    Text(
                                        text = task.title,
                                        style = MaterialTheme.typography.bodyLarge,
                                        textDecoration = if (done) TextDecoration.LineThrough else TextDecoration.None,
                                        color = if (done) {
                                            MaterialTheme.colorScheme.onSurfaceVariant
                                        } else {
                                            MaterialTheme.colorScheme.onSurface
                                        }
                                    )
                                    task.timeMinutes?.let {
                                        Text(
                                            text = strings.formatTime(it),
                                            style = MaterialTheme.typography.bodySmall,
                                            color = MaterialTheme.colorScheme.onSurfaceVariant
                                        )
                                    }
                                }
                            }
                            TaskActionsMenu(
                                expanded = menuOpen,
                                onDismiss = { menuOpen = false },
                                onEdit = {
                                    menuOpen = false
                                    onEdit(task)
                                },
                                onDelete = {
                                    menuOpen = false
                                    onDelete(task)
                                }
                            )
                        }
                    }
                }
            }
        }
    }
}

@Composable
internal fun NotesSection(
    notes: List<NoteEntity>,
    onAddNote: (String) -> Unit,
    modifier: Modifier = Modifier
) {
    val strings = LocalStrings.current
    var draft by remember { mutableStateOf("") }

    SectionCard(modifier) {
        Column(
            modifier = Modifier.padding(Dimens.paddingMedium),
            verticalArrangement = Arrangement.spacedBy(Dimens.paddingSmall)
        ) {
            if (notes.isEmpty()) {
                Text(
                    text = strings.emptyNotes,
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
            notes.forEach { note ->
                Text(
                    text = note.text,
                    modifier = Modifier
                        .fillMaxWidth()
                        .clip(RoundedCornerShape(Dimens.cornerRadius))
                        .background(MaterialTheme.colorScheme.surfaceVariant)
                        .padding(Dimens.paddingMedium),
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurface
                )
            }
            Row(verticalAlignment = Alignment.CenterVertically) {
                OutlinedTextField(
                    value = draft,
                    onValueChange = { draft = it },
                    modifier = Modifier.weight(1f),
                    placeholder = { Text(strings.writeNote) },
                    singleLine = true
                )
                Spacer(Modifier.width(Dimens.paddingSmall))
                Button(
                    onClick = {
                        onAddNote(draft)
                        draft = ""
                    },
                    enabled = draft.isNotBlank()
                ) {
                    Text(strings.add)
                }
            }
        }
    }
}

@Composable
internal fun DailyProgressCard(done: Int, total: Int, modifier: Modifier = Modifier) {
    val strings = LocalStrings.current
    val fraction = if (total == 0) 0f else done / total.toFloat()
    val percent = (fraction * 100).roundToInt()
    val doneColor = MaterialTheme.colorScheme.tertiary
    val remainderColor = MaterialTheme.colorScheme.surfaceVariant

    SectionCard(modifier) {
        Row(
            modifier = Modifier.padding(Dimens.paddingMedium),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Canvas(
                modifier = Modifier
                    .size(104.dp)
                    .semantics { contentDescription = strings.progressDescription(percent) }
            ) {
                drawArc(color = remainderColor, startAngle = 0f, sweepAngle = 360f, useCenter = true)
                if (fraction > 0f) {
                    drawArc(
                        color = doneColor,
                        startAngle = -90f,
                        sweepAngle = 360f * fraction,
                        useCenter = true
                    )
                }
            }
            Spacer(Modifier.width(Dimens.paddingLarge))
            Column {
                Text(
                    text = strings.percent(percent),
                    style = MaterialTheme.typography.headlineLarge,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.onSurface
                )
                Text(
                    text = strings.tasksDoneToday(done, total),
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
        }
    }
}

@Composable
internal fun WeeklyStatsCard(counts: List<Int>, todayIndex: Int, modifier: Modifier = Modifier) {
    val strings = LocalStrings.current
    val maxCount = counts.maxOrNull()?.coerceAtLeast(1) ?: 1

    SectionCard(modifier) {
        Column(
            modifier = Modifier.padding(Dimens.paddingMedium),
            verticalArrangement = Arrangement.spacedBy(Dimens.paddingSmall)
        ) {
            Text(
                text = strings.tasksCompletedThisWeek(counts.sum()),
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(MAX_BAR_HEIGHT + 28.dp),
                horizontalArrangement = Arrangement.SpaceEvenly
            ) {
                counts.forEachIndexed { index, count ->
                    val barHeight = (MAX_BAR_HEIGHT * (count / maxCount.toFloat())).coerceAtLeast(4.dp)
                    Column(
                        modifier = Modifier
                            .weight(1f)
                            .fillMaxHeight()
                            .semantics { contentDescription = strings.dayCompletedDescription(index, count) },
                        verticalArrangement = Arrangement.Bottom,
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Box(
                            modifier = Modifier
                                .width(24.dp)
                                .height(barHeight)
                                .clip(RoundedCornerShape(topStart = 8.dp, topEnd = 8.dp))
                                .background(
                                    if (index == todayIndex) {
                                        MaterialTheme.colorScheme.secondary
                                    } else {
                                        MaterialTheme.colorScheme.secondaryContainer
                                    }
                                )
                        )
                        Text(
                            text = strings.dayLetters[index],
                            modifier = Modifier.padding(top = Dimens.paddingExtraSmall),
                            style = MaterialTheme.typography.labelMedium,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                }
            }
        }
    }
}