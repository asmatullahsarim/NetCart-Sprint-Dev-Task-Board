package com.example.ui.screens

import androidx.activity.compose.BackHandler
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.horizontalScroll
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
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Inbox
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.FilterChip
import androidx.compose.material3.FilterChipDefaults
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ScrollableTabRow
import androidx.compose.material3.Surface
import androidx.compose.material3.Tab
import androidx.compose.material3.TabRowDefaults
import androidx.compose.material3.TabRowDefaults.tabIndicatorOffset
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import com.example.R
import com.example.model.TaskCategoryFilter
import com.example.model.TaskItem
import com.example.model.TaskStatus
import com.example.ui.components.TaskTicketCard
import com.example.ui.theme.DevSprintTheme
import com.example.viewmodel.DevSprintUiState

@Composable
fun TasksScreen(
  uiState: DevSprintUiState,
  onSelectCategory: (TaskCategoryFilter) -> Unit,
  onSelectStatusTab: (TaskStatus) -> Unit,
  onTaskClick: (TaskItem) -> Unit,
  onAdvanceStatus: (String) -> Unit,
  onNavigateBackHome: () -> Unit,
  modifier: Modifier = Modifier,
) {
  BackHandler(onBack = onNavigateBackHome)

  val colors = DevSprintTheme.colors
  val statuses = TaskStatus.entries
  val selectedTabIndex = statuses.indexOf(uiState.selectedStatusTab).coerceAtLeast(0)
  val tasks = uiState.filteredTasksForSelectedTab

  Box(modifier = modifier.fillMaxSize(), contentAlignment = Alignment.TopCenter) {
    Column(modifier = Modifier.widthIn(max = 820.dp).fillMaxSize()) {
      // Horizontal scrolling category filter chips
      Row(
        modifier =
          Modifier
            .fillMaxWidth()
            .horizontalScroll(rememberScrollState())
            .padding(horizontal = 16.dp, vertical = 8.dp),
        horizontalArrangement = Arrangement.spacedBy(8.dp),
        verticalAlignment = Alignment.CenterVertically,
      ) {
        TaskCategoryFilter.entries.forEach { category ->
          val selected = uiState.selectedCategoryFilter == category
          FilterChip(
            selected = selected,
            onClick = { onSelectCategory(category) },
            modifier = Modifier.testTag("filter_chip_${category.name.lowercase()}"),
            label = {
              Text(
                text = category.label,
                style = MaterialTheme.typography.labelMedium.copy(
                  fontWeight = if (selected) FontWeight.Bold else FontWeight.Medium
                ),
              )
            },
            colors =
              FilterChipDefaults.filterChipColors(
                containerColor = colors.surfaceCard,
                labelColor = colors.textSecondary,
                selectedContainerColor = colors.accentCyanContainer,
                selectedLabelColor = colors.accentCyan,
              ),
            border =
              FilterChipDefaults.filterChipBorder(
                enabled = true,
                selected = selected,
                borderColor = colors.borderSubtle,
                selectedBorderColor = colors.accentCyan.copy(alpha = 0.6f),
              ),
          )
        }
      }

      // Status Pipeline Tabs: Backlog | In Progress | Code Review | QA Testing | Done
      ScrollableTabRow(
        selectedTabIndex = selectedTabIndex,
        containerColor = colors.background,
        contentColor = colors.accentCyan,
        edgePadding = 16.dp,
        divider = { HorizontalDivider(color = colors.borderSubtle) },
        indicator = { tabPositions ->
          if (selectedTabIndex < tabPositions.size) {
            TabRowDefaults.SecondaryIndicator(
              modifier = Modifier.tabIndicatorOffset(tabPositions[selectedTabIndex]),
              height = 3.dp,
              color = colors.accentCyan,
            )
          }
        },
      ) {
        statuses.forEach { status ->
          val isSelected = uiState.selectedStatusTab == status
          val count = uiState.countForStatus(status)
          Tab(
            selected = isSelected,
            onClick = { onSelectStatusTab(status) },
            modifier = Modifier.testTag("status_tab_${status.name.lowercase()}"),
            text = {
              Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(6.dp),
              ) {
                Text(
                  text = status.label,
                  style = MaterialTheme.typography.labelLarge.copy(
                    fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Medium
                  ),
                  color = if (isSelected) colors.accentCyan else colors.textSecondary,
                )
                Surface(
                  shape = RoundedCornerShape(6.dp),
                  color =
                    if (isSelected) colors.accentCyanContainer else colors.surfaceCard,
                ) {
                  Text(
                    text = count.toString(),
                    style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.Bold),
                    color = if (isSelected) colors.accentCyan else colors.textSecondary,
                    modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp),
                  )
                }
              }
            },
          )
        }
      }

      if (tasks.isEmpty()) {
        Box(
          modifier = Modifier.fillMaxSize().padding(24.dp),
          contentAlignment = Alignment.Center,
        ) {
          Card(
            shape = RoundedCornerShape(14.dp),
            colors = CardDefaults.cardColors(containerColor = colors.surfaceCard),
            border = BorderStroke(1.dp, colors.borderSubtle),
          ) {
            Column(
              modifier = Modifier.padding(horizontal = 24.dp, vertical = 28.dp),
              horizontalAlignment = Alignment.CenterHorizontally,
              verticalArrangement = Arrangement.spacedBy(8.dp),
            ) {
              Icon(
                imageVector = Icons.Filled.Inbox,
                contentDescription = null,
                tint = colors.accentCyan,
                modifier = Modifier.size(36.dp),
              )
              Spacer(modifier = Modifier.height(2.dp))
              Text(
                text = stringResource(R.string.empty_tasks_title),
                style = MaterialTheme.typography.titleMedium,
                color = colors.textPrimary,
                textAlign = TextAlign.Center,
              )
              Text(
                text = stringResource(R.string.empty_tasks_subtitle),
                style = MaterialTheme.typography.bodyMedium,
                color = colors.textSecondary,
                textAlign = TextAlign.Center,
              )
            }
          }
        }
      } else {
        LazyColumn(
          modifier = Modifier.fillMaxSize(),
          contentPadding = PaddingValues(start = 16.dp, end = 16.dp, top = 14.dp, bottom = 96.dp),
          verticalArrangement = Arrangement.spacedBy(12.dp),
        ) {
          items(tasks, key = { it.id }) { task ->
            TaskTicketCard(
              task = task,
              compactView = uiState.workspace.appSettings.compactView,
              showStatusBadge = false,
              onClick = { onTaskClick(task) },
              onAdvanceStatus = { onAdvanceStatus(task.id) },
            )
          }
        }
      }
    }
  }
}
