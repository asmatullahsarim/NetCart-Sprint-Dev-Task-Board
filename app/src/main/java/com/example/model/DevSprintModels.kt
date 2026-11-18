package com.example.model

/**
 * Navigation states for single-activity Compose state routing.
 */
sealed class Screen(val route: String) {
  data object Splash : Screen("splash")
  data object Home : Screen("home")
  data object Tasks : Screen("tasks")
  data object Sprints : Screen("sprints")
  data object Settings : Screen("settings")
}

enum class TaskType(val label: String, val jsonValue: String) {
  BUG("Bug", "BUG"),
  FEATURE("Feature", "FEATURE"),
  BACKEND("Backend/API", "BACKEND"),
  FRONTEND("Frontend", "FRONTEND"),
  DEVOPS("DevOps", "DEVOPS");

  companion object {
    fun fromJson(value: String): TaskType =
      entries.firstOrNull {
        it.jsonValue.equals(value, ignoreCase = true) ||
          it.label.equals(value, ignoreCase = true)
      } ?: FEATURE
  }
}

enum class TaskCategoryFilter(val label: String) {
  ALL("All"),
  BUG("Bug"),
  FEATURE("Feature"),
  BACKEND_API("Backend/API"),
  FRONTEND("Frontend"),
  DEVOPS("DevOps"),
}

enum class Priority(val code: String, val label: String, val rank: Int) {
  P0("P0", "P0 Critical", 0),
  P1("P1", "P1 High", 1),
  P2("P2", "P2 Normal", 2),
  P3("P3", "P3 Low", 3);

  companion object {
    fun fromJson(value: String): Priority =
      entries.firstOrNull { it.code.equals(value, ignoreCase = true) } ?: P2
  }
}

enum class TaskStatus(val label: String, val shortLabel: String, val jsonValue: String) {
  BACKLOG("Backlog", "Backlog", "BACKLOG"),
  IN_PROGRESS("In Progress", "In Progress", "IN_PROGRESS"),
  CODE_REVIEW("Code Review", "Review", "CODE_REVIEW"),
  QA_TESTING("QA Testing", "QA", "QA_TESTING"),
  DONE("Done", "Done", "DONE");

  fun nextOrNull(): TaskStatus? =
    when (this) {
      BACKLOG -> IN_PROGRESS
      IN_PROGRESS -> CODE_REVIEW
      CODE_REVIEW -> QA_TESTING
      QA_TESTING -> DONE
      DONE -> null
    }

  companion object {
    fun fromJson(value: String): TaskStatus =
      entries.firstOrNull { it.jsonValue.equals(value, ignoreCase = true) } ?: BACKLOG
  }
}

enum class DepartmentDomain(val title: String) {
  FRONTEND_MOBILE("Frontend / Mobile"),
  BACKEND_API("Backend & API Infrastructure"),
  QA_AUTOMATION("QA & Automation"),
}

data class AppSettings(
  val darkMode: Boolean = true,
  val compactView: Boolean = false,
  val defaultPriority: Priority = Priority.P2,
  val sprintDurationDays: Int = 14,
)

data class SprintInfo(
  val id: String,
  val name: String,
  val goal: String,
  val startDate: String,
  val endDate: String,
) {
  /** Short badge label e.g. "Sprint 24" extracted from sprint name if present. */
  val badgeLabel: String
    get() {
      val regex = Regex("Sprint\\s+\\d+", RegexOption.IGNORE_CASE)
      return regex.find(name)?.value ?: id
    }
}

data class TaskItem(
  val id: String,
  val title: String,
  val description: String,
  val type: TaskType,
  val priority: Priority,
  val module: String,
  val storyPoints: Int,
  val status: TaskStatus,
  val dueDate: String,
) {
  fun matchesCategory(filter: TaskCategoryFilter): Boolean =
    when (filter) {
      TaskCategoryFilter.ALL -> true
      TaskCategoryFilter.BUG -> type == TaskType.BUG
      TaskCategoryFilter.FEATURE -> type == TaskType.FEATURE
      TaskCategoryFilter.BACKEND_API ->
        type == TaskType.BACKEND ||
          module.contains("auth", ignoreCase = true) ||
          module.contains("api", ignoreCase = true) ||
          module.contains("backend", ignoreCase = true) ||
          module.contains("core", ignoreCase = true) ||
          module.contains("data", ignoreCase = true)
      TaskCategoryFilter.FRONTEND ->
        type == TaskType.FRONTEND ||
          module.contains("ui", ignoreCase = true) ||
          module.contains("front", ignoreCase = true) ||
          module.contains("mobile", ignoreCase = true) ||
          module.contains("design", ignoreCase = true)
      TaskCategoryFilter.DEVOPS ->
        type == TaskType.DEVOPS ||
          module.contains("devops", ignoreCase = true) ||
          module.contains("ci", ignoreCase = true) ||
          module.contains("infra", ignoreCase = true) ||
          module.contains("qa", ignoreCase = true)
    }

  fun departmentDomain(): DepartmentDomain =
    when {
      type == TaskType.FRONTEND ||
        module.contains("ui", ignoreCase = true) ||
        module.contains("front", ignoreCase = true) ||
        module.contains("mobile", ignoreCase = true) ||
        module.contains("design", ignoreCase = true) -> DepartmentDomain.FRONTEND_MOBILE
      module.contains("qa", ignoreCase = true) ||
        module.contains("test", ignoreCase = true) ||
        module.contains("auto", ignoreCase = true) -> DepartmentDomain.QA_AUTOMATION
      else -> DepartmentDomain.BACKEND_API
    }
}

data class DevSprintWorkspace(
  val appSettings: AppSettings,
  val activeSprint: SprintInfo,
  val tasks: List<TaskItem>,
)
