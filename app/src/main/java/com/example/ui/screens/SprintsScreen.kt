package com.example.ui.screens

import androidx.activity.compose.BackHandler
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.BugReport
import androidx.compose.material.icons.filled.CalendarMonth
import androidx.compose.material.icons.filled.FastForward
import androidx.compose.material.icons.filled.Layers
import androidx.compose.material.icons.filled.PhoneAndroid
import androidx.compose.material.icons.filled.RocketLaunch
import androidx.compose.material.icons.filled.Storage
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.example.R
import com.example.model.DepartmentDomain
import com.example.model.TaskStatus
import com.example.ui.theme.DevSprintTheme
import com.example.viewmodel.DepartmentProgress
import com.example.viewmodel.DevSprintUiState
import kotlin.math.roundToInt

@Composable
fun SprintsScreen(
  uiState: DevSprintUiState,
  onCloseSprintAndRollOver: (String?) -> Unit,
  onNavigateBackHome: () -> Unit,
  modifier: Modifier = Modifier,
) {
  BackHandler(onBack = onNavigateBackHome)

  val colors = DevSprintTheme.colors
  val sprint = uiState.workspace.activeSprint
  val completionPct = (uiState.sprintProgressFraction * 100).roundToInt()

  var showRolloverDialog by remember { mutableStateOf(false) }
  var nextSprintGoal by remember { mutableStateOf("") }

  Box(modifier = modifier.fillMaxSize(), contentAlignment = Alignment.TopCenter) {
    LazyColumn(
      modifier = Modifier.widthIn(max = 760.dp).fillMaxSize(),
      contentPadding = PaddingValues(start = 16.dp, end = 16.dp, top = 12.dp, bottom = 32.dp),
      verticalArrangement = Arrangement.spacedBy(16.dp),
    ) {
      // Sprint Header Card
      item {
        Card(
          modifier = Modifier.fillMaxWidth(),
          shape = RoundedCornerShape(14.dp),
          colors = CardDefaults.cardColors(containerColor = colors.surfaceCard),
          border = BorderStroke(1.dp, colors.borderSubtle),
        ) {
          Column(
            modifier = Modifier.padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp),
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
                      .size(36.dp)
                      .clip(CircleShape)
                      .background(colors.accentCyanContainer),
                  contentAlignment = Alignment.Center,
                ) {
                  Icon(
                    imageVector = Icons.Filled.RocketLaunch,
                    contentDescription = null,
                    tint = colors.accentCyan,
                    modifier = Modifier.size(18.dp),
                  )
                }
                Column {
                  Text(
                    text = sprint.id,
                    style = MaterialTheme.typography.labelSmall,
                    color = colors.accentCyan,
                  )
                  Text(
                    text = sprint.name,
                    style = MaterialTheme.typography.titleMedium,
                    color = colors.textPrimary,
                  )
                }
              }

              Surface(
                shape = RoundedCornerShape(10.dp),
                color = colors.successContainer,
              ) {
                Text(
                  text = "$completionPct%",
                  style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold),
                  color = colors.successEmerald,
                  modifier = Modifier.padding(horizontal = 12.dp, vertical = 6.dp),
                )
              }
            }

            if (sprint.goal.isNotBlank()) {
              Text(
                text = sprint.goal,
                style = MaterialTheme.typography.bodyMedium,
                color = colors.textSecondary,
              )
            }

            Row(
              modifier = Modifier.fillMaxWidth(),
              horizontalArrangement = Arrangement.SpaceBetween,
              verticalAlignment = Alignment.CenterVertically,
            ) {
              Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(6.dp),
              ) {
                Icon(
                  imageVector = Icons.Filled.CalendarMonth,
                  contentDescription = null,
                  tint = colors.textSecondary,
                  modifier = Modifier.size(15.dp),
                )
                Text(
                  text = "${sprint.startDate}  →  ${sprint.endDate}",
                  style = MaterialTheme.typography.labelMedium,
                  color = colors.textSecondary,
                )
              }

              Text(
                text = "${uiState.completedStoryPoints}/${uiState.totalStoryPoints} pts",
                style = MaterialTheme.typography.labelMedium.copy(fontWeight = FontWeight.Bold),
                color = colors.accentCyan,
              )
            }

            LinearProgressIndicator(
              progress = { uiState.sprintProgressFraction.coerceIn(0f, 1f) },
              modifier = Modifier.fillMaxWidth().height(9.dp).clip(RoundedCornerShape(6.dp)),
              color = colors.successEmerald,
              trackColor = colors.surfaceElevated,
              strokeCap = StrokeCap.Round,
            )
          }
        }
      }

      // Pipeline Stage Snapshot
      item {
        Card(
          modifier = Modifier.fillMaxWidth(),
          shape = RoundedCornerShape(14.dp),
          colors = CardDefaults.cardColors(containerColor = colors.surfaceCard),
          border = BorderStroke(1.dp, colors.borderSubtle),
        ) {
          Column(
            modifier = Modifier.padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(10.dp),
          ) {
            Text(
              text = "PIPELINE VELOCITY SNAPSHOT",
              style = MaterialTheme.typography.labelSmall,
              color = colors.textSecondary,
            )
            Row(
              modifier = Modifier.fillMaxWidth(),
              horizontalArrangement = Arrangement.spacedBy(8.dp),
            ) {
              TaskStatus.entries.forEach { stage ->
                val count = uiState.tasks.count { it.status == stage }
                val stageColor =
                  when (stage) {
                    TaskStatus.BACKLOG -> colors.textSecondary
                    TaskStatus.IN_PROGRESS -> colors.warningAmber
                    TaskStatus.CODE_REVIEW -> colors.accentCyan
                    TaskStatus.QA_TESTING -> colors.warningAmber
                    TaskStatus.DONE -> colors.successEmerald
                  }
                Surface(
                  modifier = Modifier.weight(1f),
                  shape = RoundedCornerShape(10.dp),
                  color = colors.surfaceElevated,
                ) {
                  Column(
                    modifier = Modifier.padding(vertical = 10.dp, horizontal = 4.dp),
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.spacedBy(2.dp),
                  ) {
                    Text(
                      text = count.toString(),
                      style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold),
                      color = stageColor,
                    )
                    Text(
                      text = stage.shortLabel,
                      style = MaterialTheme.typography.labelSmall,
                      color = colors.textSecondary,
                      maxLines = 1,
                    )
                  }
                }
              }
            }
          }
        }
      }

      // Department & Module Progress Breakdown Header
      item {
        Row(
          verticalAlignment = Alignment.CenterVertically,
          horizontalArrangement = Arrangement.spacedBy(8.dp),
          modifier = Modifier.padding(top = 4.dp),
        ) {
          Icon(
            imageVector = Icons.Filled.Layers,
            contentDescription = null,
            tint = colors.accentCyan,
            modifier = Modifier.size(18.dp),
          )
          Text(
            text = stringResource(R.string.sprint_dept_breakdown),
            style = MaterialTheme.typography.titleMedium,
            color = colors.textPrimary,
          )
        }
      }

      items(uiState.departmentBreakdown, key = { it.domain.name }) { dept ->
        DepartmentProgressCard(department = dept)
      }

      // Lifecycle Action: Close Current Sprint & Roll Over
      item {
        Spacer(modifier = Modifier.height(4.dp))
        Button(
          onClick = {
            nextSprintGoal = ""
            showRolloverDialog = true
          },
          modifier = Modifier.fillMaxWidth().height(52.dp).testTag("close_sprint_button"),
          shape = RoundedCornerShape(10.dp),
          colors =
            ButtonDefaults.buttonColors(
              containerColor = colors.accentCyan,
              contentColor = if (colors.isDark) colors.background else Color.White,
            ),
        ) {
          Icon(
            imageVector = Icons.Filled.FastForward,
            contentDescription = null,
            modifier = Modifier.size(20.dp),
          )
          Spacer(modifier = Modifier.width(8.dp))
          Text(
            text = stringResource(R.string.sprint_close_rollover),
            style = MaterialTheme.typography.labelLarge.copy(fontWeight = FontWeight.Bold),
          )
        }
      }
    }
  }

  if (showRolloverDialog) {
    val incompleteCount = uiState.tasks.count { it.status != TaskStatus.DONE }
    AlertDialog(
      onDismissRequest = { showRolloverDialog = false },
      containerColor = colors.surfaceCard,
      titleContentColor = colors.textPrimary,
      textContentColor = colors.textSecondary,
      title = { Text(stringResource(R.string.sprint_close_confirm_title)) },
      text = {
        Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
          Text(
            text =
              "${stringResource(R.string.sprint_close_confirm_body)}\n\n• $incompleteCount incomplete item(s) will move to Backlog\n• ${uiState.closedTasksCount} completed item(s) will be cleared from the active board",
            style = MaterialTheme.typography.bodyMedium,
          )
          OutlinedTextField(
            value = nextSprintGoal,
            onValueChange = { nextSprintGoal = it },
            label = { Text("Next Sprint Goal (optional)") },
            placeholder = { Text("e.g., Harden API caching & UI polish") },
            singleLine = false,
            maxLines = 3,
            colors =
              OutlinedTextFieldDefaults.colors(
                focusedBorderColor = colors.accentCyan,
                unfocusedBorderColor = colors.borderSubtle,
                focusedLabelColor = colors.accentCyan,
                unfocusedLabelColor = colors.textSecondary,
                focusedTextColor = colors.textPrimary,
                unfocusedTextColor = colors.textPrimary,
              ),
            modifier = Modifier.fillMaxWidth(),
          )
        }
      },
      confirmButton = {
        Button(
          onClick = {
            showRolloverDialog = false
            onCloseSprintAndRollOver(nextSprintGoal)
          },
          shape = RoundedCornerShape(10.dp),
          colors =
            ButtonDefaults.buttonColors(
              containerColor = colors.accentCyan,
              contentColor = if (colors.isDark) colors.background else Color.White,
            ),
        ) {
          Text("Roll Over & Start Sprint")
        }
      },
      dismissButton = {
        TextButton(onClick = { showRolloverDialog = false }) {
          Text(stringResource(R.string.action_cancel), color = colors.textSecondary)
        }
      },
    )
  }
}

@Composable
private fun DepartmentProgressCard(department: DepartmentProgress) {
  val colors = DevSprintTheme.colors
  val pct = (department.progressFraction * 100).roundToInt()

  val (icon, tint, container) =
    when (department.domain) {
      DepartmentDomain.FRONTEND_MOBILE ->
        Triple(Icons.Filled.PhoneAndroid, colors.accentCyan, colors.accentCyanContainer)
      DepartmentDomain.BACKEND_API ->
        Triple(Icons.Filled.Storage, colors.warningAmber, colors.warningContainer)
      DepartmentDomain.QA_AUTOMATION ->
        Triple(Icons.Filled.BugReport, colors.successEmerald, colors.successContainer)
    }

  Card(
    modifier = Modifier.fillMaxWidth(),
    shape = RoundedCornerShape(14.dp),
    colors = CardDefaults.cardColors(containerColor = colors.surfaceCard),
    border = BorderStroke(1.dp, colors.borderSubtle),
  ) {
    Column(
      modifier = Modifier.padding(16.dp),
      verticalArrangement = Arrangement.spacedBy(10.dp),
    ) {
      Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically,
      ) {
        Row(
          verticalAlignment = Alignment.CenterVertically,
          horizontalArrangement = Arrangement.spacedBy(10.dp),
          modifier = Modifier.weight(1f),
        ) {
          Box(
            modifier = Modifier.size(34.dp).clip(CircleShape).background(container),
            contentAlignment = Alignment.Center,
          ) {
            Icon(
              imageVector = icon,
              contentDescription = null,
              tint = tint,
              modifier = Modifier.size(18.dp),
            )
          }
          Column {
            Text(
              text = department.domain.title,
              style = MaterialTheme.typography.titleSmall,
              color = colors.textPrimary,
            )
            Text(
              text =
                "${department.completedTasks}/${department.totalTasks} tasks • ${department.completedPoints}/${department.totalPoints} pts",
              style = MaterialTheme.typography.bodySmall,
              color = colors.textSecondary,
            )
          }
        }

        Text(
          text = "$pct%",
          style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold),
          color = tint,
        )
      }

      LinearProgressIndicator(
        progress = { department.progressFraction.coerceIn(0f, 1f) },
        modifier = Modifier.fillMaxWidth().height(8.dp).clip(RoundedCornerShape(6.dp)),
        color = tint,
        trackColor = colors.surfaceElevated,
        strokeCap = StrokeCap.Round,
      )
    }
  }
}
