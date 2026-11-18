package com.example.ui.screens

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
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
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Assignment
import androidx.compose.material.icons.filled.CheckCircleOutline
import androidx.compose.material.icons.filled.PendingActions
import androidx.compose.material.icons.filled.RateReview
import androidx.compose.material.icons.filled.Schedule
import androidx.compose.material.icons.filled.WarningAmber
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.example.R
import com.example.model.TaskItem
import com.example.model.TaskStatus
import com.example.ui.components.TaskTicketCard
import com.example.ui.theme.DevSprintTheme
import com.example.viewmodel.DevSprintUiState
import kotlin.math.roundToInt

@Composable
fun HomeScreen(
  uiState: DevSprintUiState,
  onNavigateToTasksStatus: (TaskStatus) -> Unit,
  onTaskClick: (TaskItem) -> Unit,
  onAdvanceStatus: (String) -> Unit,
  modifier: Modifier = Modifier,
) {
  val colors = DevSprintTheme.colors
  val sprint = uiState.workspace.activeSprint
  val progressPct = (uiState.sprintProgressFraction * 100).roundToInt()

  Box(modifier = modifier.fillMaxSize(), contentAlignment = Alignment.TopCenter) {
    LazyColumn(
      modifier = Modifier.widthIn(max = 760.dp).fillMaxSize(),
      contentPadding = PaddingValues(start = 16.dp, end = 16.dp, top = 12.dp, bottom = 96.dp),
      verticalArrangement = Arrangement.spacedBy(16.dp),
    ) {
      // Sprint Overview Card
      item {
        Card(
          modifier = Modifier.fillMaxWidth().testTag("sprint_overview_card"),
          shape = RoundedCornerShape(14.dp),
          colors = CardDefaults.cardColors(containerColor = colors.surfaceCard),
          border = BorderStroke(1.dp, colors.borderSubtle),
        ) {
          Column(
            modifier =
              Modifier
                .fillMaxWidth()
                .background(
                  Brush.verticalGradient(
                    listOf(
                      colors.accentCyan.copy(alpha = 0.08f),
                      Color.Transparent,
                    )
                  )
                )
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp),
          ) {
            Row(
              modifier = Modifier.fillMaxWidth(),
              horizontalArrangement = Arrangement.SpaceBetween,
              verticalAlignment = Alignment.CenterVertically,
            ) {
              Column(modifier = Modifier.weight(1f)) {
                Text(
                  text = "ACTIVE SPRINT OBJECTIVE",
                  style = MaterialTheme.typography.labelSmall,
                  color = colors.accentCyan,
                )
                Spacer(modifier = Modifier.height(2.dp))
                Text(
                  text = sprint.name,
                  style = MaterialTheme.typography.titleMedium,
                  color = colors.textPrimary,
                )
              }

              Surface(
                shape = RoundedCornerShape(10.dp),
                color = colors.surfaceElevated,
                border = BorderStroke(1.dp, colors.borderSubtle),
              ) {
                Row(
                  modifier = Modifier.padding(horizontal = 10.dp, vertical = 6.dp),
                  verticalAlignment = Alignment.CenterVertically,
                  horizontalArrangement = Arrangement.spacedBy(4.dp),
                ) {
                  Icon(
                    imageVector = Icons.Filled.Schedule,
                    contentDescription = null,
                    tint = colors.accentCyan,
                    modifier = Modifier.size(14.dp),
                  )
                  Text(
                    text =
                      if (uiState.remainingDays == 1) "1 day left"
                      else "${uiState.remainingDays} days left",
                    style = MaterialTheme.typography.labelMedium,
                    color = colors.textPrimary,
                  )
                }
              }
            }

            if (sprint.goal.isNotBlank()) {
              Text(
                text = sprint.goal,
                style = MaterialTheme.typography.bodyMedium,
                color = colors.textSecondary,
              )
            }

            Column(verticalArrangement = Arrangement.spacedBy(6.dp)) {
              Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically,
              ) {
                Text(
                  text = "${uiState.completedStoryPoints} / ${uiState.totalStoryPoints} Story Points",
                  style = MaterialTheme.typography.labelMedium.copy(fontWeight = FontWeight.SemiBold),
                  color = colors.textPrimary,
                )
                Text(
                  text = "$progressPct% Complete",
                  style = MaterialTheme.typography.labelMedium.copy(fontWeight = FontWeight.Bold),
                  color = colors.successEmerald,
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
      }

      // 2x2 KPI Metric Grid
      item {
        Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
          Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(12.dp),
          ) {
            KpiMetricCard(
              title = stringResource(R.string.kpi_total_active),
              count = uiState.totalActiveTasksCount,
              icon = Icons.Filled.Assignment,
              accentColor = colors.accentCyan,
              containerColor = colors.accentCyanContainer,
              onClick = { onNavigateToTasksStatus(TaskStatus.BACKLOG) },
              modifier = Modifier.weight(1f).testTag("kpi_total_active"),
            )
            KpiMetricCard(
              title = stringResource(R.string.kpi_in_progress),
              count = uiState.inProgressTasksCount,
              icon = Icons.Filled.PendingActions,
              accentColor = colors.warningAmber,
              containerColor = colors.warningContainer,
              onClick = { onNavigateToTasksStatus(TaskStatus.IN_PROGRESS) },
              modifier = Modifier.weight(1f).testTag("kpi_in_progress"),
            )
          }

          Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(12.dp),
          ) {
            KpiMetricCard(
              title = stringResource(R.string.kpi_pending_review),
              count = uiState.pendingReviewOrQaCount,
              icon = Icons.Filled.RateReview,
              accentColor = colors.warningAmber,
              containerColor = colors.warningContainer,
              onClick = { onNavigateToTasksStatus(TaskStatus.CODE_REVIEW) },
              modifier = Modifier.weight(1f).testTag("kpi_pending_review"),
            )
            KpiMetricCard(
              title = stringResource(R.string.kpi_closed),
              count = uiState.closedTasksCount,
              icon = Icons.Filled.CheckCircleOutline,
              accentColor = colors.successEmerald,
              containerColor = colors.successContainer,
              onClick = { onNavigateToTasksStatus(TaskStatus.DONE) },
              modifier = Modifier.weight(1f).testTag("kpi_closed"),
            )
          }
        }
      }

      // Urgent Blockers Header
      item {
        Row(
          modifier = Modifier.fillMaxWidth().padding(top = 4.dp),
          horizontalArrangement = Arrangement.SpaceBetween,
          verticalAlignment = Alignment.CenterVertically,
        ) {
          Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(8.dp),
          ) {
            Icon(
              imageVector = Icons.Filled.WarningAmber,
              contentDescription = null,
              tint = colors.criticalCrimson,
              modifier = Modifier.size(20.dp),
            )
            Text(
              text = stringResource(R.string.home_urgent_blockers),
              style = MaterialTheme.typography.titleMedium,
              color = colors.textPrimary,
            )
          }
          Surface(
            shape = RoundedCornerShape(8.dp),
            color = colors.criticalContainer,
          ) {
            Text(
              text = "${uiState.urgentBlockers.size} OPEN",
              style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.Bold),
              color = colors.criticalCrimson,
              modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp),
            )
          }
        }
      }

      if (uiState.urgentBlockers.isEmpty()) {
        item {
          Card(
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(14.dp),
            colors = CardDefaults.cardColors(containerColor = colors.surfaceCard),
            border = BorderStroke(1.dp, colors.borderSubtle),
          ) {
            Row(
              modifier = Modifier.padding(16.dp),
              verticalAlignment = Alignment.CenterVertically,
              horizontalArrangement = Arrangement.spacedBy(12.dp),
            ) {
              Icon(
                imageVector = Icons.Filled.CheckCircleOutline,
                contentDescription = null,
                tint = colors.successEmerald,
                modifier = Modifier.size(24.dp),
              )
              Text(
                text = stringResource(R.string.home_no_blockers),
                style = MaterialTheme.typography.bodyMedium,
                color = colors.textSecondary,
              )
            }
          }
        }
      } else {
        items(uiState.urgentBlockers, key = { it.id }) { task ->
          TaskTicketCard(
            task = task,
            compactView = uiState.workspace.appSettings.compactView,
            showStatusBadge = true,
            onClick = { onTaskClick(task) },
            onAdvanceStatus = { onAdvanceStatus(task.id) },
          )
        }
      }
    }
  }
}

@Composable
private fun KpiMetricCard(
  title: String,
  count: Int,
  icon: ImageVector,
  accentColor: Color,
  containerColor: Color,
  onClick: () -> Unit,
  modifier: Modifier = Modifier,
) {
  val colors = DevSprintTheme.colors

  Card(
    modifier =
      modifier
        .clip(RoundedCornerShape(14.dp))
        .clickable(role = Role.Button, onClick = onClick),
    shape = RoundedCornerShape(14.dp),
    colors = CardDefaults.cardColors(containerColor = colors.surfaceCard),
    border = BorderStroke(1.dp, colors.borderSubtle),
  ) {
    Column(
      modifier = Modifier.fillMaxWidth().padding(14.dp),
      verticalArrangement = Arrangement.spacedBy(10.dp),
    ) {
      Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically,
      ) {
        Box(
          modifier = Modifier.size(34.dp).clip(CircleShape).background(containerColor),
          contentAlignment = Alignment.Center,
        ) {
          Icon(
            imageVector = icon,
            contentDescription = null,
            tint = accentColor,
            modifier = Modifier.size(18.dp),
          )
        }
        Text(
          text = count.toString(),
          style = MaterialTheme.typography.headlineLarge.copy(fontWeight = FontWeight.Bold),
          color = colors.textPrimary,
        )
      }
      Text(
        text = title,
        style = MaterialTheme.typography.labelMedium,
        color = colors.textSecondary,
        maxLines = 2,
      )
    }
  }
}
