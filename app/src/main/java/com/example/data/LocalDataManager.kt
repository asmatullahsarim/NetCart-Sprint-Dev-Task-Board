package com.example.data

import android.content.Context
import android.content.SharedPreferences
import com.example.model.AppSettings
import com.example.model.DevSprintWorkspace
import com.example.model.Priority
import com.example.model.SprintInfo
import com.example.model.TaskItem
import com.example.model.TaskStatus
import com.example.model.TaskType
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Locale
import org.json.JSONArray
import org.json.JSONObject

/**
 * 100% offline local persistence layer wrapping SharedPreferences and native org.json.
 * Zero network calls, zero third-party serialization dependencies.
 */
class LocalDataManager(context: Context) {

  private val prefs: SharedPreferences =
    context.applicationContext.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)

  private val dateFormatter = SimpleDateFormat("yyyy-MM-dd", Locale.US)

  fun loadWorkspace(): DevSprintWorkspace {
    val raw = prefs.getString(KEY_WORKSPACE_JSON, null)
    if (!raw.isNullOrBlank()) {
      parseWorkspaceJson(raw)?.let { return it }
    }
    val seeded = createSeedWorkspace()
    saveWorkspace(seeded)
    return seeded
  }

  fun saveWorkspace(workspace: DevSprintWorkspace) {
    val json = serializeWorkspace(workspace, indentSpaces = 0)
    prefs.edit().putString(KEY_WORKSPACE_JSON, json).apply()
  }

  fun exportFormattedJson(workspace: DevSprintWorkspace): String =
    serializeWorkspace(workspace, indentSpaces = 2)

  fun importFromJson(rawJson: String): Result<DevSprintWorkspace> =
    try {
      val parsed = parseWorkspaceJson(rawJson)
        ?: return Result.failure(IllegalArgumentException("Invalid DevSprint JSON schema."))
      saveWorkspace(parsed)
      Result.success(parsed)
    } catch (e: Exception) {
      Result.failure(e)
    }

  fun resetToDemoData(): DevSprintWorkspace {
    val seeded = createSeedWorkspace()
    saveWorkspace(seeded)
    return seeded
  }

  fun formatRelativeDate(daysOffsetFromToday: Int): String {
    val cal = Calendar.getInstance()
    cal.add(Calendar.DAY_OF_YEAR, daysOffsetFromToday)
    return dateFormatter.format(cal.time)
  }

  fun computeRemainingDays(endDateStr: String): Int {
    return try {
      val end = dateFormatter.parse(endDateStr) ?: return 0
      val todayCal =
        Calendar.getInstance().apply {
          set(Calendar.HOUR_OF_DAY, 0)
          set(Calendar.MINUTE, 0)
          set(Calendar.SECOND, 0)
          set(Calendar.MILLISECOND, 0)
        }
      val endCal =
        Calendar.getInstance().apply {
          time = end
          set(Calendar.HOUR_OF_DAY, 0)
          set(Calendar.MINUTE, 0)
          set(Calendar.SECOND, 0)
          set(Calendar.MILLISECOND, 0)
        }
      val diffMillis = endCal.timeInMillis - todayCal.timeInMillis
      (diffMillis / MILLIS_PER_DAY).toInt().coerceAtLeast(0)
    } catch (_: Exception) {
      0
    }
  }

  fun serializeWorkspace(workspace: DevSprintWorkspace, indentSpaces: Int = 2): String {
    val root = JSONObject()

    val settingsObj =
      JSONObject().apply {
        put("darkMode", workspace.appSettings.darkMode)
        put("compactView", workspace.appSettings.compactView)
        put("defaultPriority", workspace.appSettings.defaultPriority.code)
        put("sprintDurationDays", workspace.appSettings.sprintDurationDays)
      }
    root.put("appSettings", settingsObj)

    val sprintObj =
      JSONObject().apply {
        put("id", workspace.activeSprint.id)
        put("name", workspace.activeSprint.name)
        put("goal", workspace.activeSprint.goal)
        put("startDate", workspace.activeSprint.startDate)
        put("endDate", workspace.activeSprint.endDate)
      }
    root.put("activeSprint", sprintObj)

    val tasksArr = JSONArray()
    workspace.tasks.forEach { task ->
      val taskObj =
        JSONObject().apply {
          put("id", task.id)
          put("title", task.title)
          put("description", task.description)
          put("type", task.type.jsonValue)
          put("priority", task.priority.code)
          put("module", task.module)
          put("storyPoints", task.storyPoints)
          put("status", task.status.jsonValue)
          put("dueDate", task.dueDate)
        }
      tasksArr.put(taskObj)
    }
    root.put("tasks", tasksArr)

    return if (indentSpaces > 0) root.toString(indentSpaces) else root.toString()
  }

  fun parseWorkspaceJson(raw: String): DevSprintWorkspace? {
    return try {
      val root = JSONObject(raw)
      val settingsObj = root.optJSONObject("appSettings")
      val appSettings =
        AppSettings(
          darkMode = settingsObj?.optBoolean("darkMode", true) ?: true,
          compactView = settingsObj?.optBoolean("compactView", false) ?: false,
          defaultPriority =
            Priority.fromJson(settingsObj?.optString("defaultPriority", "P2") ?: "P2"),
          sprintDurationDays =
            (settingsObj?.optInt("sprintDurationDays", 14) ?: 14).coerceIn(7, 30),
        )

      val sprintObj = root.getJSONObject("activeSprint")
      val activeSprint =
        SprintInfo(
          id = sprintObj.optString("id", "SPRINT-2026-24"),
          name = sprintObj.optString("name", "Sprint 24 — Core Security & Auth"),
          goal = sprintObj.optString("goal", ""),
          startDate = sprintObj.optString("startDate", formatRelativeDate(-5)),
          endDate = sprintObj.optString("endDate", formatRelativeDate(9)),
        )

      val tasksArr = root.getJSONArray("tasks")
      val tasks = buildList {
        for (i in 0 until tasksArr.length()) {
          val obj = tasksArr.getJSONObject(i)
          val id = obj.optString("id", "DEV-${101 + i}")
          val title = obj.optString("title", "").trim()
          if (title.isEmpty()) continue
          add(
            TaskItem(
              id = id,
              title = title,
              description = obj.optString("description", ""),
              type = TaskType.fromJson(obj.optString("type", "FEATURE")),
              priority = Priority.fromJson(obj.optString("priority", "P2")),
              module = obj.optString("module", "CoreModule"),
              storyPoints = obj.optInt("storyPoints", 3).coerceIn(1, 21),
              status = TaskStatus.fromJson(obj.optString("status", "BACKLOG")),
              dueDate = obj.optString("dueDate", activeSprint.endDate),
            )
          )
        }
      }

      DevSprintWorkspace(appSettings = appSettings, activeSprint = activeSprint, tasks = tasks)
    } catch (_: Exception) {
      null
    }
  }

  private fun createSeedWorkspace(): DevSprintWorkspace {
    val startDate = formatRelativeDate(-5)
    val endDate = formatRelativeDate(9)
    val activeSprint =
      SprintInfo(
        id = "SPRINT-2026-24",
        name = "Sprint 24 — Core Security & Auth",
        goal =
          "Migrate authentication pipeline to OAuth2 PKCE, harden offline storage encryption, and resolve critical UI rendering bottlenecks.",
        startDate = startDate,
        endDate = endDate,
      )

    val demoTasks =
      listOf(
        TaskItem(
          id = "DEV-101",
          title = "Fix OAuth token refresh race condition",
          description =
            "Concurrent API requests trigger duplicate token refresh flows causing unexpected 401 unauthenticated errors.",
          type = TaskType.BUG,
          priority = Priority.P0,
          module = "AuthModule",
          storyPoints = 5,
          status = TaskStatus.IN_PROGRESS,
          dueDate = formatRelativeDate(2),
        ),
        TaskItem(
          id = "DEV-102",
          title = "Implement Jetpack Compose Settings Screen",
          description =
            "Construct offline configuration interface adhering to the dark tech design system tokens and JSON export.",
          type = TaskType.FEATURE,
          priority = Priority.P2,
          module = "UI Components",
          storyPoints = 3,
          status = TaskStatus.CODE_REVIEW,
          dueDate = formatRelativeDate(4),
        ),
        TaskItem(
          id = "DEV-103",
          title = "Eliminate bitmap memory leak in Sprint timeline",
          description =
            "Recycled avatar buffers retained across configuration changes in LazyColumn view holders under low memory.",
          type = TaskType.BUG,
          priority = Priority.P1,
          module = "Frontend / Mobile",
          storyPoints = 5,
          status = TaskStatus.IN_PROGRESS,
          dueDate = formatRelativeDate(1),
        ),
        TaskItem(
          id = "DEV-104",
          title = "Harden PKCE verifier entropy & SHA-256 challenge",
          description =
            "Ensure SecureRandom 256-bit code verifier generation passes OWASP Mobile MASVS cryptographic audit.",
          type = TaskType.BACKEND,
          priority = Priority.P0,
          module = "AuthModule",
          storyPoints = 8,
          status = TaskStatus.QA_TESTING,
          dueDate = formatRelativeDate(3),
        ),
        TaskItem(
          id = "DEV-105",
          title = "Build deterministic CI/CD signed AAB release lane",
          description =
            "Configure reproducible Gradle release builds with strict zero-permission manifest verification checks.",
          type = TaskType.DEVOPS,
          priority = Priority.P1,
          module = "CI/CD Pipeline",
          storyPoints = 5,
          status = TaskStatus.BACKLOG,
          dueDate = formatRelativeDate(6),
        ),
        TaskItem(
          id = "DEV-106",
          title = "Add regression suite for offline JSON schema migration",
          description =
            "Validate round-trip org.json serialization and malformed payload recovery across v1.0 schema fixtures.",
          type = TaskType.FEATURE,
          priority = Priority.P2,
          module = "QA & Automation",
          storyPoints = 3,
          status = TaskStatus.QA_TESTING,
          dueDate = formatRelativeDate(5),
        ),
        TaskItem(
          id = "DEV-107",
          title = "Optimize LazyColumn recomposition keys on Tasks board",
          description =
            "Add stable ticket ID keys and derivedStateOf filters to maintain locked 120Hz scroll frame pacing.",
          type = TaskType.FRONTEND,
          priority = Priority.P3,
          module = "UI Components",
          storyPoints = 2,
          status = TaskStatus.DONE,
          dueDate = formatRelativeDate(-1),
        ),
        TaskItem(
          id = "DEV-108",
          title = "Rate-limit local audit log compaction worker",
          description =
            "Throttle background JSON compaction on older devices to stay under 15ms main-thread budget.",
          type = TaskType.BACKEND,
          priority = Priority.P2,
          module = "APIGateway",
          storyPoints = 3,
          status = TaskStatus.DONE,
          dueDate = formatRelativeDate(-2),
        ),
        TaskItem(
          id = "DEV-109",
          title = "Automate P0 blocker smoke tests in QA harness",
          description =
            "Add headless Robolectric coverage for status pipeline transitions and sprint rollover edge cases.",
          type = TaskType.FEATURE,
          priority = Priority.P1,
          module = "QA & Automation",
          storyPoints = 5,
          status = TaskStatus.CODE_REVIEW,
          dueDate = formatRelativeDate(4),
        ),
        TaskItem(
          id = "DEV-110",
          title = "Audit Dark Tech contrast ratios for WCAG AA",
          description =
            "Verify Crisp White and Muted Slate legibility against Slate Dark cards on OLED displays.",
          type = TaskType.FRONTEND,
          priority = Priority.P3,
          module = "Frontend / Mobile",
          storyPoints = 2,
          status = TaskStatus.DONE,
          dueDate = formatRelativeDate(-3),
        ),
      )

    return DevSprintWorkspace(
      appSettings = AppSettings(),
      activeSprint = activeSprint,
      tasks = demoTasks,
    )
  }

  companion object {
    private const val PREFS_NAME = "devsprint_offline_prefs"
    private const val KEY_WORKSPACE_JSON = "workspace_json"
    private const val MILLIS_PER_DAY = 86_400_000L
  }
}
