package com.example.viewmodel

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import com.example.data.LocalDataManager
import com.example.model.AppSettings
import com.example.model.DepartmentDomain
import com.example.model.DevSprintWorkspace
import com.example.model.Priority
import com.example.model.Screen
import com.example.model.SprintInfo
import com.example.model.TaskCategoryFilter
import com.example.model.TaskItem
import com.example.model.TaskStatus
import com.example.model.TaskType
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update

data class DepartmentProgress(
  val domain: DepartmentDomain,
  val totalTasks: Int,
  val completedTasks: Int,
  val totalPoints: Int,
  val completedPoints: Int,
) {
  val progressFraction: Float
    get() =
      if (totalPoints > 0) completedPoints.toFloat() / totalPoints.toFloat()
      else if (totalTasks > 0) completedTasks.toFloat() / totalTasks.toFloat()
      else 0f
}

data class DevSprintUiState(
  val currentScreen: Screen = Screen.Splash,
  val workspace: DevSprintWorkspace,
  val remainingDays: Int = 0,
  val selectedCategoryFilter: TaskCategoryFilter = TaskCategoryFilter.ALL,
  val selectedStatusTab: TaskStatus = TaskStatus.IN_PROGRESS,
  val isSearchVisible: Boolean = false,
  val searchQuery: String = "",
  val isTaskSheetOpen: Boolean = false,
  val editingTask: TaskItem? = null,
  val statusBannerMessage: String? = null,
) {
  val tasks: List<TaskItem>
    get() = workspace.tasks

  val totalStoryPoints: Int
    get() = tasks.sumOf { it.storyPoints }

  val completedStoryPoints: Int
    get() = tasks.filter { it.status == TaskStatus.DONE }.sumOf { it.storyPoints }

  val sprintProgressFraction: Float
    get() =
      if (totalStoryPoints > 0) completedStoryPoints.toFloat() / totalStoryPoints.toFloat()
      else 0f

  val totalActiveTasksCount: Int
    get() = tasks.count { it.status != TaskStatus.DONE }

  val inProgressTasksCount: Int
    get() = tasks.count { it.status == TaskStatus.IN_PROGRESS }

  val pendingReviewOrQaCount: Int
    get() = tasks.count { it.status == TaskStatus.CODE_REVIEW || it.status == TaskStatus.QA_TESTING }

  val closedTasksCount: Int
    get() = tasks.count { it.status == TaskStatus.DONE }

  val urgentBlockers: List<TaskItem>
    get() =
      tasks
        .filter {
          it.status != TaskStatus.DONE &&
            (it.priority == Priority.P0 || it.priority == Priority.P1) &&
            matchesSearch(it)
        }
        .sortedWith(compareBy<TaskItem> { it.priority.rank }.thenBy { it.status.ordinal })

  val filteredTasksForSelectedTab: List<TaskItem>
    get() =
      tasks
        .filter {
          it.status == selectedStatusTab &&
            it.matchesCategory(selectedCategoryFilter) &&
            matchesSearch(it)
        }
        .sortedBy { it.priority.rank }

  fun countForStatus(status: TaskStatus): Int =
    tasks.count {
      it.status == status && it.matchesCategory(selectedCategoryFilter) && matchesSearch(it)
    }

  val departmentBreakdown: List<DepartmentProgress>
    get() =
      DepartmentDomain.entries.map { domain ->
        val domainTasks = tasks.filter { it.departmentDomain() == domain }
        DepartmentProgress(
          domain = domain,
          totalTasks = domainTasks.size,
          completedTasks = domainTasks.count { it.status == TaskStatus.DONE },
          totalPoints = domainTasks.sumOf { it.storyPoints },
          completedPoints = domainTasks.filter { it.status == TaskStatus.DONE }.sumOf { it.storyPoints },
        )
      }

  private fun matchesSearch(task: TaskItem): Boolean {
    val q = searchQuery.trim()
    if (q.isEmpty()) return true
    return task.id.contains(q, ignoreCase = true) ||
      task.title.contains(q, ignoreCase = true) ||
      task.description.contains(q, ignoreCase = true) ||
      task.module.contains(q, ignoreCase = true)
  }
}

class DevSprintViewModel(application: Application) : AndroidViewModel(application) {

  private val dataManager = LocalDataManager(application)

  private val initialWorkspace = dataManager.loadWorkspace()

  private val _uiState =
    MutableStateFlow(
      DevSprintUiState(
        currentScreen = Screen.Splash,
        workspace = initialWorkspace,
        remainingDays = dataManager.computeRemainingDays(initialWorkspace.activeSprint.endDate),
      )
    )
  val uiState: StateFlow<DevSprintUiState> = _uiState.asStateFlow()

  fun onSplashCompleted() {
    _uiState.update { state ->
      if (state.currentScreen is Screen.Splash) {
        state.copy(currentScreen = Screen.Home)
      } else {
        state
      }
    }
  }

  fun navigateTo(screen: Screen) {
    if (screen is Screen.Splash) return
    _uiState.update { it.copy(currentScreen = screen) }
  }

  fun navigateToTasksWithStatus(status: TaskStatus) {
    _uiState.update {
      it.copy(
        currentScreen = Screen.Tasks,
        selectedStatusTab = status,
        selectedCategoryFilter = TaskCategoryFilter.ALL,
      )
    }
  }

  fun toggleSearch() {
    _uiState.update { state ->
      val nextVisible = !state.isSearchVisible
      state.copy(
        isSearchVisible = nextVisible,
        searchQuery = if (nextVisible) state.searchQuery else "",
      )
    }
  }

  fun updateSearchQuery(query: String) {
    _uiState.update { it.copy(searchQuery = query) }
  }

  fun selectCategoryFilter(filter: TaskCategoryFilter) {
    _uiState.update { it.copy(selectedCategoryFilter = filter) }
  }

  fun selectStatusTab(status: TaskStatus) {
    _uiState.update { it.copy(selectedStatusTab = status) }
  }

  fun openCreateTaskSheet() {
    _uiState.update { it.copy(isTaskSheetOpen = true, editingTask = null) }
  }

  fun openEditTaskSheet(task: TaskItem) {
    _uiState.update { it.copy(isTaskSheetOpen = true, editingTask = task) }
  }

  fun dismissTaskSheet() {
    _uiState.update { it.copy(isTaskSheetOpen = false, editingTask = null) }
  }

  fun saveTask(
    editingId: String?,
    title: String,
    description: String,
    type: TaskType,
    priority: Priority,
    module: String,
    storyPoints: Int,
    status: TaskStatus,
    dueDate: String,
  ) {
    val cleanTitle = title.trim()
    if (cleanTitle.isEmpty()) return
    val cleanModule = module.trim().ifEmpty { "CoreModule" }
    val cleanDueDate = dueDate.trim().ifEmpty { _uiState.value.workspace.activeSprint.endDate }
    val clampedPoints = storyPoints.coerceIn(1, 21)

    updateWorkspace { workspace ->
      val updatedTasks =
        if (editingId != null) {
          workspace.tasks.map { existing ->
            if (existing.id == editingId) {
              existing.copy(
                title = cleanTitle,
                description = description.trim(),
                type = type,
                priority = priority,
                module = cleanModule,
                storyPoints = clampedPoints,
                status = status,
                dueDate = cleanDueDate,
              )
            } else {
              existing
            }
          }
        } else {
          val nextNumber =
            (workspace.tasks.mapNotNull { it.id.substringAfter("-", "").toIntOrNull() }.maxOrNull()
              ?: 100) + 1
          val newTask =
            TaskItem(
              id = "DEV-$nextNumber",
              title = cleanTitle,
              description = description.trim(),
              type = type,
              priority = priority,
              module = cleanModule,
              storyPoints = clampedPoints,
              status = status,
              dueDate = cleanDueDate,
            )
          listOf(newTask) + workspace.tasks
        }
      workspace.copy(tasks = updatedTasks)
    }

    _uiState.update {
      it.copy(
        isTaskSheetOpen = false,
        editingTask = null,
        statusBannerMessage =
          if (editingId != null) "Updated $editingId" else "Created new engineering ticket",
      )
    }
  }

  fun deleteTask(taskId: String) {
    updateWorkspace { workspace ->
      workspace.copy(tasks = workspace.tasks.filterNot { it.id == taskId })
    }
    _uiState.update {
      it.copy(
        isTaskSheetOpen = false,
        editingTask = null,
        statusBannerMessage = "Deleted $taskId",
      )
    }
  }

  fun advanceTaskStatus(taskId: String) {
    var movedTo: TaskStatus? = null
    updateWorkspace { workspace ->
      val updated =
        workspace.tasks.map { task ->
          if (task.id == taskId) {
            val next = task.status.nextOrNull()
            if (next != null) {
              movedTo = next
              task.copy(status = next)
            } else {
              task
            }
          } else {
            task
          }
        }
      workspace.copy(tasks = updated)
    }
    movedTo?.let { status ->
      _uiState.update { it.copy(statusBannerMessage = "$taskId moved to ${status.label}") }
    }
  }

  fun closeCurrentSprintAndRollOver(customNextGoal: String?) {
    var rolledCount = 0
    var newSprintLabel = ""

    updateWorkspace { workspace ->
      val currentSprint = workspace.activeSprint
      val currentNum = Regex("\\d+").findAll(currentSprint.badgeLabel).lastOrNull()?.value?.toIntOrNull()
        ?: 24
      val nextNum = currentNum + 1
      newSprintLabel = "Sprint $nextNum"

      val duration = workspace.appSettings.sprintDurationDays.coerceIn(7, 30)
      val newStart = dataManager.formatRelativeDate(0)
      val newEnd = dataManager.formatRelativeDate(duration)

      val rolledOverTasks =
        workspace.tasks
          .filter { it.status != TaskStatus.DONE }
          .map { it.copy(status = TaskStatus.BACKLOG) }
      rolledCount = rolledOverTasks.size

      val goalText =
        customNextGoal?.trim()?.takeIf { it.isNotEmpty() }
          ?: "Execute rolled-over backlog commitments, stabilize API latency, and advance QA automation."

      val nextSprint =
        SprintInfo(
          id = "SPRINT-2026-$nextNum",
          name = "Sprint $nextNum — Delivery & Hardening",
          goal = goalText,
          startDate = newStart,
          endDate = newEnd,
        )

      workspace.copy(activeSprint = nextSprint, tasks = rolledOverTasks)
    }

    _uiState.update {
      it.copy(
        selectedStatusTab = TaskStatus.BACKLOG,
        statusBannerMessage =
          "$newSprintLabel initialized • $rolledCount incomplete item(s) rolled over to Backlog",
      )
    }
  }

  fun updateDarkMode(enabled: Boolean) {
    updateSettings { it.copy(darkMode = enabled) }
  }

  fun updateCompactView(enabled: Boolean) {
    updateSettings { it.copy(compactView = enabled) }
  }

  fun updateDefaultPriority(priority: Priority) {
    updateSettings { it.copy(defaultPriority = priority) }
  }

  fun updateSprintDurationDays(days: Int) {
    updateSettings { it.copy(sprintDurationDays = days.coerceIn(7, 30)) }
  }

  fun exportWorkspaceJson(): String = dataManager.exportFormattedJson(_uiState.value.workspace)

  fun importWorkspaceJson(rawJson: String): Boolean {
    val result = dataManager.importFromJson(rawJson)
    return result.fold(
      onSuccess = { imported ->
        _uiState.update {
          it.copy(
            workspace = imported,
            remainingDays = dataManager.computeRemainingDays(imported.activeSprint.endDate),
            statusBannerMessage = "Imported ${imported.tasks.size} tickets from local JSON",
          )
        }
        true
      },
      onFailure = {
        _uiState.update {
          it.copy(statusBannerMessage = "Import failed: Invalid DevSprint JSON schema")
        }
        false
      },
    )
  }

  fun resetToDemoData() {
    val seeded = dataManager.resetToDemoData()
    _uiState.update {
      it.copy(
        workspace = seeded,
        remainingDays = dataManager.computeRemainingDays(seeded.activeSprint.endDate),
        selectedCategoryFilter = TaskCategoryFilter.ALL,
        selectedStatusTab = TaskStatus.IN_PROGRESS,
        statusBannerMessage = "Workspace reset to offline demo sprint data",
      )
    }
  }

  fun defaultDueDateForNewTask(): String = _uiState.value.workspace.activeSprint.endDate

  fun clearStatusBanner() {
    _uiState.update { it.copy(statusBannerMessage = null) }
  }

  private fun updateSettings(transform: (AppSettings) -> AppSettings) {
    updateWorkspace { workspace -> workspace.copy(appSettings = transform(workspace.appSettings)) }
  }

  private fun updateWorkspace(transform: (DevSprintWorkspace) -> DevSprintWorkspace) {
    _uiState.update { state ->
      val nextWorkspace = transform(state.workspace)
      dataManager.saveWorkspace(nextWorkspace)
      state.copy(
        workspace = nextWorkspace,
        remainingDays = dataManager.computeRemainingDays(nextWorkspace.activeSprint.endDate),
      )
    }
  }
}
