package com.example.ui.components

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.imePadding
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowForward
import androidx.compose.material.icons.filled.BugReport
import androidx.compose.material.icons.filled.Build
import androidx.compose.material.icons.filled.CalendarToday
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Code
import androidx.compose.material.icons.filled.DeleteOutline
import androidx.compose.material.icons.filled.Layers
import androidx.compose.material.icons.filled.NewReleases
import androidx.compose.material.icons.filled.Storage
import androidx.compose.material.icons.filled.Web
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExposedDropdownMenuBox
import androidx.compose.material3.ExposedDropdownMenuDefaults
import androidx.compose.material3.FilterChip
import androidx.compose.material3.FilterChipDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.MenuAnchorType
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.StrokeJoin
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import com.example.R
import com.example.model.Priority
import com.example.model.TaskItem
import com.example.model.TaskStatus
import com.example.model.TaskType
import com.example.ui.theme.DevSprintTheme

/**
 * Geometric code-bracket + checkmark emblem rendered natively via Compose Canvas.
 */
@Composable
fun DevSprintEmblem(
  modifier: Modifier = Modifier,
  size: Dp = 110.dp,
  showOuterGlow: Boolean = true,
) {
  val colors = DevSprintTheme.colors
  val cyan = colors.accentCyan
  val emerald = colors.successEmerald
  val cardBg = colors.surfaceCard

  Canvas(modifier = modifier.size(size)) {
    val w = this.size.width
    val h = this.size.height
    val cx = w / 2f
    val cy = h / 2f
    val r = minOf(w, h) * 0.46f

    if (showOuterGlow) {
      drawCircle(
        brush =
          Brush.radialGradient(
            colors = listOf(cyan.copy(alpha = 0.24f), Color.Transparent),
            center = Offset(cx, cy),
            radius = minOf(w, h) * 0.55f,
          ),
        radius = minOf(w, h) * 0.55f,
        center = Offset(cx, cy),
      )
    }

    // Hexagonal tech frame
    val hexPath =
      Path().apply {
        moveTo(cx, cy - r)
        lineTo(cx + r * 0.866f, cy - r * 0.5f)
        lineTo(cx + r * 0.866f, cy + r * 0.5f)
        lineTo(cx, cy + r)
        lineTo(cx - r * 0.866f, cy + r * 0.5f)
        lineTo(cx - r * 0.866f, cy - r * 0.5f)
        close()
      }
    drawPath(path = hexPath, color = cardBg)
    drawPath(
      path = hexPath,
      brush = Brush.linearGradient(listOf(cyan, emerald)),
      style = Stroke(width = w * 0.045f, join = StrokeJoin.Round),
    )

    // Left code bracket '<'
    val strokeW = w * 0.07f
    val leftBracket =
      Path().apply {
        moveTo(cx - r * 0.34f, cy - r * 0.24f)
        lineTo(cx - r * 0.58f, cy)
        lineTo(cx - r * 0.34f, cy + r * 0.24f)
      }
    drawPath(
      path = leftBracket,
      color = cyan,
      style = Stroke(width = strokeW, cap = StrokeCap.Round, join = StrokeJoin.Round),
    )

    // Right code bracket '>'
    val rightBracket =
      Path().apply {
        moveTo(cx + r * 0.34f, cy - r * 0.24f)
        lineTo(cx + r * 0.58f, cy)
        lineTo(cx + r * 0.34f, cy + r * 0.24f)
      }
    drawPath(
      path = rightBracket,
      color = emerald,
      style = Stroke(width = strokeW, cap = StrokeCap.Round, join = StrokeJoin.Round),
    )

    // Center checkmark
    val check =
      Path().apply {
        moveTo(cx - r * 0.19f, cy + r * 0.02f)
        lineTo(cx - r * 0.03f, cy + r * 0.18f)
        lineTo(cx + r * 0.22f, cy - r * 0.16f)
      }
    drawPath(
      path = check,
      color = Color.White,
      style = Stroke(width = strokeW, cap = StrokeCap.Round, join = StrokeJoin.Round),
    )
  }
}

@Composable
fun PriorityPill(priority: Priority, modifier: Modifier = Modifier) {
  val colors = DevSprintTheme.colors
  val (fg, bg) =
    when (priority) {
      Priority.P0 -> colors.criticalCrimson to colors.criticalContainer
      Priority.P1 -> colors.warningAmber to colors.warningContainer
      Priority.P2 -> colors.accentCyan to colors.accentCyanContainer
      Priority.P3 -> colors.textSecondary to colors.surfaceElevated
    }

  Surface(
    modifier = modifier,
    shape = RoundedCornerShape(8.dp),
    color = bg,
    border = BorderStroke(1.dp, fg.copy(alpha = 0.4f)),
  ) {
    Text(
      text = priority.code,
      style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.Bold),
      color = fg,
      modifier = Modifier.padding(horizontal = 8.dp, vertical = 3.dp),
    )
  }
}

@Composable
fun TaskTypeTag(type: TaskType, modifier: Modifier = Modifier) {
  val colors = DevSprintTheme.colors
  val (fg, icon) =
    when (type) {
      TaskType.BUG -> colors.criticalCrimson to Icons.Filled.BugReport
      TaskType.FEATURE -> colors.accentCyan to Icons.Filled.NewReleases
      TaskType.BACKEND -> colors.warningAmber to Icons.Filled.Storage
      TaskType.FRONTEND -> colors.successEmerald to Icons.Filled.Web
      TaskType.DEVOPS -> colors.accentCyan to Icons.Filled.Build
    }

  Surface(
    modifier = modifier,
    shape = RoundedCornerShape(8.dp),
    color = fg.copy(alpha = 0.14f),
  ) {
    Row(
      modifier = Modifier.padding(horizontal = 8.dp, vertical = 3.dp),
      verticalAlignment = Alignment.CenterVertically,
      horizontalArrangement = Arrangement.spacedBy(4.dp),
    ) {
      Icon(
        imageVector = icon,
        contentDescription = null,
        tint = fg,
        modifier = Modifier.size(12.dp),
      )
      Text(
        text = type.label.uppercase(),
        style = MaterialTheme.typography.labelSmall,
        color = fg,
      )
    }
  }
}

@Composable
fun StatusPill(status: TaskStatus, modifier: Modifier = Modifier) {
  val colors = DevSprintTheme.colors
  val (fg, bg) =
    when (status) {
      TaskStatus.BACKLOG -> colors.textSecondary to colors.surfaceElevated
      TaskStatus.IN_PROGRESS -> colors.warningAmber to colors.warningContainer
      TaskStatus.CODE_REVIEW -> colors.accentCyan to colors.accentCyanContainer
      TaskStatus.QA_TESTING -> colors.warningAmber to colors.warningContainer
      TaskStatus.DONE -> colors.successEmerald to colors.successContainer
    }

  Surface(
    modifier = modifier,
    shape = RoundedCornerShape(8.dp),
    color = bg,
  ) {
    Text(
      text = status.label.uppercase(),
      style = MaterialTheme.typography.labelSmall,
      color = fg,
      modifier = Modifier.padding(horizontal = 8.dp, vertical = 3.dp),
    )
  }
}

/**
 * Ticket card following Screen 3 anatomy:
 * Header: Ticket ID, Type tag, Priority Pill (+ Status Pill when shown on Home)
 * Body: Title (16sp SemiBold) + 2-line clamped description (hidden in compact view)
 * Footer: Module badge, Story Points tag, Due Date, and quick-action "Advance Status" button.
 */
@OptIn(ExperimentalLayoutApi::class)
@Composable
fun TaskTicketCard(
  task: TaskItem,
  compactView: Boolean,
  showStatusBadge: Boolean = false,
  onClick: () -> Unit,
  onAdvanceStatus: () -> Unit,
  modifier: Modifier = Modifier,
) {
  val colors = DevSprintTheme.colors
  val nextStatus = task.status.nextOrNull()

  Card(
    modifier =
      modifier
        .fillMaxWidth()
        .testTag("task_card_${task.id}")
        .clip(RoundedCornerShape(14.dp))
        .clickable(role = Role.Button, onClick = onClick),
    shape = RoundedCornerShape(14.dp),
    colors = CardDefaults.cardColors(containerColor = colors.surfaceCard),
    border = BorderStroke(1.dp, colors.borderSubtle),
  ) {
    Column(
      modifier = Modifier.padding(if (compactView) 12.dp else 16.dp),
      verticalArrangement = Arrangement.spacedBy(if (compactView) 8.dp else 10.dp),
    ) {
      // Header row
      Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically,
      ) {
        Row(
          verticalAlignment = Alignment.CenterVertically,
          horizontalArrangement = Arrangement.spacedBy(8.dp),
        ) {
          Text(
            text = task.id,
            style =
              MaterialTheme.typography.labelMedium.copy(
                fontWeight = FontWeight.Bold,
                color = colors.textSecondary,
              ),
          )
          TaskTypeTag(type = task.type)
          if (showStatusBadge) {
            StatusPill(status = task.status)
          }
        }
        PriorityPill(priority = task.priority)
      }

      // Body: Title & description
      Text(
        text = task.title,
        style = MaterialTheme.typography.titleMedium,
        color = colors.textPrimary,
        maxLines = if (compactView) 1 else 2,
        overflow = TextOverflow.Ellipsis,
      )

      if (!compactView && task.description.isNotBlank()) {
        Text(
          text = task.description,
          style = MaterialTheme.typography.bodyMedium,
          color = colors.textSecondary,
          maxLines = 2,
          overflow = TextOverflow.Ellipsis,
        )
      }

      // Footer metadata & Advance button
      Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically,
      ) {
        FlowRow(
          modifier = Modifier.weight(1f),
          horizontalArrangement = Arrangement.spacedBy(6.dp),
          verticalArrangement = Arrangement.spacedBy(6.dp),
        ) {
          // Module badge
          Surface(
            shape = RoundedCornerShape(6.dp),
            color = colors.surfaceElevated,
          ) {
            Row(
              modifier = Modifier.padding(horizontal = 7.dp, vertical = 4.dp),
              verticalAlignment = Alignment.CenterVertically,
              horizontalArrangement = Arrangement.spacedBy(4.dp),
            ) {
              Icon(
                imageVector = Icons.Filled.Layers,
                contentDescription = null,
                tint = colors.accentCyan,
                modifier = Modifier.size(12.dp),
              )
              Text(
                text = task.module,
                style = MaterialTheme.typography.labelSmall,
                color = colors.textPrimary,
              )
            }
          }

          // Story points tag
          Surface(
            shape = RoundedCornerShape(6.dp),
            color = colors.accentCyanContainer,
          ) {
            Text(
              text = "${task.storyPoints} PTS",
              style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.Bold),
              color = colors.accentCyan,
              modifier = Modifier.padding(horizontal = 7.dp, vertical = 4.dp),
            )
          }

          if (!compactView) {
            Row(
              verticalAlignment = Alignment.CenterVertically,
              horizontalArrangement = Arrangement.spacedBy(3.dp),
              modifier = Modifier.padding(vertical = 4.dp),
            ) {
              Icon(
                imageVector = Icons.Filled.CalendarToday,
                contentDescription = null,
                tint = colors.textSecondary,
                modifier = Modifier.size(11.dp),
              )
              Text(
                text = task.dueDate,
                style = MaterialTheme.typography.labelSmall,
                color = colors.textSecondary,
              )
            }
          }
        }

        Spacer(modifier = Modifier.width(8.dp))

        if (nextStatus != null) {
          Button(
            onClick = onAdvanceStatus,
            modifier = Modifier.testTag("advance_status_${task.id}"),
            shape = RoundedCornerShape(10.dp),
            contentPadding = PaddingValues(horizontal = 12.dp, vertical = 8.dp),
            colors =
              ButtonDefaults.buttonColors(
                containerColor = colors.accentCyan,
                contentColor = if (colors.isDark) colors.background else Color.White,
              ),
          ) {
            Text(
              text = stringResource(R.string.advance_status),
              style = MaterialTheme.typography.labelMedium.copy(fontWeight = FontWeight.Bold),
            )
            Spacer(modifier = Modifier.width(4.dp))
            Icon(
              imageVector = Icons.AutoMirrored.Filled.ArrowForward,
              contentDescription = stringResource(R.string.cd_advance_status),
              modifier = Modifier.size(14.dp),
            )
          }
        } else {
          Surface(
            shape = RoundedCornerShape(10.dp),
            color = colors.successContainer,
            border = BorderStroke(1.dp, colors.successEmerald.copy(alpha = 0.45f)),
          ) {
            Row(
              modifier = Modifier.padding(horizontal = 10.dp, vertical = 6.dp),
              verticalAlignment = Alignment.CenterVertically,
              horizontalArrangement = Arrangement.spacedBy(4.dp),
            ) {
              Icon(
                imageVector = Icons.Filled.CheckCircle,
                contentDescription = null,
                tint = colors.successEmerald,
                modifier = Modifier.size(14.dp),
              )
              Text(
                text = stringResource(R.string.completed_label).uppercase(),
                style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.Bold),
                color = colors.successEmerald,
              )
            }
          }
        }
      }
    }
  }
}

/**
 * Create / Edit Ticket Modal Bottom Sheet.
 */
@OptIn(ExperimentalMaterial3Api::class, ExperimentalLayoutApi::class)
@Composable
fun CreateEditTaskBottomSheet(
  editingTask: TaskItem?,
  defaultPriority: Priority,
  defaultDueDate: String,
  onDismiss: () -> Unit,
  onSave: (
    editingId: String?,
    title: String,
    description: String,
    type: TaskType,
    priority: Priority,
    module: String,
    storyPoints: Int,
    status: TaskStatus,
    dueDate: String,
  ) -> Unit,
  onDelete: ((String) -> Unit)?,
) {
  val colors = DevSprintTheme.colors
  val sheetState = rememberModalBottomSheetState(skipPartiallyExpanded = true)

  var title by remember(editingTask) { mutableStateOf(editingTask?.title ?: "") }
  var description by remember(editingTask) { mutableStateOf(editingTask?.description ?: "") }
  var selectedType by remember(editingTask) { mutableStateOf(editingTask?.type ?: TaskType.FEATURE) }
  var selectedPriority by remember(editingTask) {
    mutableStateOf(editingTask?.priority ?: defaultPriority)
  }
  var storyPointsText by remember(editingTask) {
    mutableStateOf((editingTask?.storyPoints ?: 3).toString())
  }
  var module by remember(editingTask) {
    mutableStateOf(editingTask?.module ?: "AuthModule")
  }
  var selectedStatus by remember(editingTask) {
    mutableStateOf(editingTask?.status ?: TaskStatus.BACKLOG)
  }
  var dueDate by remember(editingTask) {
    mutableStateOf(editingTask?.dueDate ?: defaultDueDate)
  }
  var typeDropdownExpanded by remember { mutableStateOf(false) }
  var showTitleError by remember { mutableStateOf(false) }

  val suggestedModules =
    listOf("AuthModule", "UI Components", "APIGateway", "Frontend / Mobile", "QA & Automation", "CI/CD Pipeline")

  val textFieldColors =
    OutlinedTextFieldDefaults.colors(
      focusedBorderColor = colors.accentCyan,
      unfocusedBorderColor = colors.borderSubtle,
      focusedLabelColor = colors.accentCyan,
      unfocusedLabelColor = colors.textSecondary,
      focusedTextColor = colors.textPrimary,
      unfocusedTextColor = colors.textPrimary,
      cursorColor = colors.accentCyan,
    )

  ModalBottomSheet(
    onDismissRequest = onDismiss,
    sheetState = sheetState,
    containerColor = colors.surfaceCard,
    contentColor = colors.textPrimary,
  ) {
    Column(
      modifier =
        Modifier
          .fillMaxWidth()
          .verticalScroll(rememberScrollState())
          .navigationBarsPadding()
          .imePadding()
          .padding(horizontal = 16.dp, vertical = 8.dp),
      verticalArrangement = Arrangement.spacedBy(14.dp),
    ) {
      Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically,
      ) {
        Row(
          verticalAlignment = Alignment.CenterVertically,
          horizontalArrangement = Arrangement.spacedBy(8.dp),
        ) {
          Box(
            modifier =
              Modifier
                .size(32.dp)
                .clip(CircleShape)
                .background(colors.accentCyanContainer),
            contentAlignment = Alignment.Center,
          ) {
            Icon(
              imageVector = Icons.Filled.Code,
              contentDescription = null,
              tint = colors.accentCyan,
              modifier = Modifier.size(18.dp),
            )
          }
          Text(
            text =
              if (editingTask == null) stringResource(R.string.sheet_create_title)
              else "${stringResource(R.string.sheet_edit_title)} (${editingTask.id})",
            style = MaterialTheme.typography.titleLarge,
            color = colors.textPrimary,
          )
        }
        IconButton(onClick = onDismiss) {
          Icon(
            imageVector = Icons.Filled.Close,
            contentDescription = stringResource(R.string.action_cancel),
            tint = colors.textSecondary,
          )
        }
      }

      OutlinedTextField(
        value = title,
        onValueChange = {
          title = it
          if (showTitleError && it.isNotBlank()) showTitleError = false
        },
        label = { Text(stringResource(R.string.field_title)) },
        placeholder = { Text("e.g., Fix OAuth PKCE refresh timeout") },
        isError = showTitleError,
        supportingText =
          if (showTitleError) {
            { Text("Ticket title is required", color = colors.criticalCrimson) }
          } else null,
        singleLine = true,
        colors = textFieldColors,
        modifier = Modifier.fillMaxWidth().testTag("task_title_input"),
      )

      OutlinedTextField(
        value = description,
        onValueChange = { description = it },
        label = { Text(stringResource(R.string.field_description)) },
        placeholder = { Text("Technical scope, acceptance criteria, or stack trace…") },
        minLines = 3,
        maxLines = 4,
        colors = textFieldColors,
        modifier = Modifier.fillMaxWidth().testTag("task_desc_input"),
      )

      // Task Type Dropdown & Story Points row
      Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.spacedBy(12.dp),
      ) {
        ExposedDropdownMenuBox(
          expanded = typeDropdownExpanded,
          onExpandedChange = { typeDropdownExpanded = it },
          modifier = Modifier.weight(1.4f),
        ) {
          OutlinedTextField(
            value = selectedType.label,
            onValueChange = {},
            readOnly = true,
            label = { Text(stringResource(R.string.field_type)) },
            trailingIcon = {
              ExposedDropdownMenuDefaults.TrailingIcon(expanded = typeDropdownExpanded)
            },
            colors = textFieldColors,
            modifier =
              Modifier
                .fillMaxWidth()
                .menuAnchor(MenuAnchorType.PrimaryNotEditable),
          )
          ExposedDropdownMenu(
            expanded = typeDropdownExpanded,
            onDismissRequest = { typeDropdownExpanded = false },
            modifier = Modifier.background(colors.surfaceElevated),
          ) {
            TaskType.entries.forEach { taskType ->
              DropdownMenuItem(
                text = { Text(taskType.label, color = colors.textPrimary) },
                onClick = {
                  selectedType = taskType
                  typeDropdownExpanded = false
                },
              )
            }
          }
        }

        OutlinedTextField(
          value = storyPointsText,
          onValueChange = { input ->
            storyPointsText = input.filter { it.isDigit() }.take(2)
          },
          label = { Text(stringResource(R.string.field_story_points)) },
          keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
          singleLine = true,
          colors = textFieldColors,
          modifier = Modifier.weight(1f).testTag("task_story_points_input"),
        )
      }

      // Priority Selector Chips
      Column(verticalArrangement = Arrangement.spacedBy(6.dp)) {
        Text(
          text = stringResource(R.string.field_priority),
          style = MaterialTheme.typography.labelMedium,
          color = colors.textSecondary,
        )
        Row(
          modifier = Modifier.fillMaxWidth(),
          horizontalArrangement = Arrangement.spacedBy(8.dp),
        ) {
          Priority.entries.forEach { priority ->
            val selected = selectedPriority == priority
            FilterChip(
              selected = selected,
              onClick = { selectedPriority = priority },
              label = { Text(priority.code, style = MaterialTheme.typography.labelMedium) },
              colors =
                FilterChipDefaults.filterChipColors(
                  selectedContainerColor = colors.accentCyanContainer,
                  selectedLabelColor = colors.accentCyan,
                  labelColor = colors.textSecondary,
                ),
              modifier = Modifier.weight(1f),
            )
          }
        }
      }

      // Target Module + quick presets
      Column(verticalArrangement = Arrangement.spacedBy(6.dp)) {
        OutlinedTextField(
          value = module,
          onValueChange = { module = it },
          label = { Text(stringResource(R.string.field_module)) },
          singleLine = true,
          colors = textFieldColors,
          modifier = Modifier.fillMaxWidth().testTag("task_module_input"),
        )
        Row(
          modifier = Modifier.fillMaxWidth().horizontalScroll(rememberScrollState()),
          horizontalArrangement = Arrangement.spacedBy(6.dp),
        ) {
          suggestedModules.forEach { preset ->
            FilterChip(
              selected = module.equals(preset, ignoreCase = true),
              onClick = { module = preset },
              label = { Text(preset, style = MaterialTheme.typography.labelSmall) },
              colors =
                FilterChipDefaults.filterChipColors(
                  selectedContainerColor = colors.accentCyanContainer,
                  selectedLabelColor = colors.accentCyan,
                  labelColor = colors.textSecondary,
                ),
            )
          }
        }
      }

      // Pipeline status & Due date when editing (or creating)
      if (editingTask != null) {
        Column(verticalArrangement = Arrangement.spacedBy(6.dp)) {
          Text(
            text = stringResource(R.string.field_status),
            style = MaterialTheme.typography.labelMedium,
            color = colors.textSecondary,
          )
          FlowRow(
            horizontalArrangement = Arrangement.spacedBy(6.dp),
            verticalArrangement = Arrangement.spacedBy(4.dp),
          ) {
            TaskStatus.entries.forEach { status ->
              FilterChip(
                selected = selectedStatus == status,
                onClick = { selectedStatus = status },
                label = { Text(status.label, style = MaterialTheme.typography.labelSmall) },
                colors =
                  FilterChipDefaults.filterChipColors(
                    selectedContainerColor = colors.accentCyanContainer,
                    selectedLabelColor = colors.accentCyan,
                    labelColor = colors.textSecondary,
                  ),
              )
            }
          }
        }
      }

      OutlinedTextField(
        value = dueDate,
        onValueChange = { dueDate = it },
        label = { Text(stringResource(R.string.field_due_date)) },
        singleLine = true,
        colors = textFieldColors,
        modifier = Modifier.fillMaxWidth(),
      )

      // Actions
      Row(
        modifier = Modifier.fillMaxWidth().padding(top = 4.dp, bottom = 12.dp),
        horizontalArrangement = Arrangement.spacedBy(10.dp),
        verticalAlignment = Alignment.CenterVertically,
      ) {
        if (editingTask != null && onDelete != null) {
          OutlinedButton(
            onClick = { onDelete(editingTask.id) },
            shape = RoundedCornerShape(10.dp),
            border = BorderStroke(1.dp, colors.criticalCrimson.copy(alpha = 0.6f)),
            colors = ButtonDefaults.outlinedButtonColors(contentColor = colors.criticalCrimson),
          ) {
            Icon(
              imageVector = Icons.Filled.DeleteOutline,
              contentDescription = null,
              modifier = Modifier.size(18.dp),
            )
            Spacer(modifier = Modifier.width(4.dp))
            Text(stringResource(R.string.action_delete))
          }
        } else {
          TextButton(onClick = onDismiss) {
            Text(stringResource(R.string.action_cancel), color = colors.textSecondary)
          }
        }

        Spacer(modifier = Modifier.weight(1f))

        Button(
          onClick = {
            if (title.isBlank()) {
              showTitleError = true
            } else {
              val pts = (storyPointsText.toIntOrNull() ?: 3).coerceIn(1, 21)
              onSave(
                editingTask?.id,
                title,
                description,
                selectedType,
                selectedPriority,
                module,
                pts,
                selectedStatus,
                dueDate,
              )
            }
          },
          modifier = Modifier.testTag("save_task_button"),
          shape = RoundedCornerShape(10.dp),
          colors =
            ButtonDefaults.buttonColors(
              containerColor = colors.accentCyan,
              contentColor = if (colors.isDark) colors.background else Color.White,
            ),
        ) {
          Text(
            text = stringResource(R.string.action_save),
            style = MaterialTheme.typography.labelLarge,
          )
        }
      }
    }
  }
}
