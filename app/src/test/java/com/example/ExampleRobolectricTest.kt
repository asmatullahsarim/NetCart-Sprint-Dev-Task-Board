package com.example

import android.content.Context
import android.content.pm.PackageManager
import androidx.test.core.app.ApplicationProvider
import com.example.data.LocalDataManager
import com.example.model.Priority
import com.example.model.TaskStatus
import com.example.model.TaskType
import com.example.viewmodel.DevSprintViewModel
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner
import org.robolectric.annotation.Config

@RunWith(RobolectricTestRunner::class)
@Config(sdk = [36])
class ExampleRobolectricTest {

  @Test
  fun `app name matches NetCart Sprint Dev Task Board`() {
    val context = ApplicationProvider.getApplicationContext<Context>()
    val appName = context.getString(R.string.app_name)
    assertEquals("NetCart Sprint Dev Task Board", appName)
  }

  @Test
  fun `manifest declares zero network permissions`() {
    val context = ApplicationProvider.getApplicationContext<Context>()
    val pkgInfo =
      context.packageManager.getPackageInfo(
        context.packageName,
        PackageManager.GET_PERMISSIONS,
      )
    val permissions = pkgInfo.requestedPermissions?.toList() ?: emptyList()
    assertFalse(permissions.contains(android.Manifest.permission.INTERNET))
    assertFalse(permissions.contains(android.Manifest.permission.ACCESS_NETWORK_STATE))
  }

  @Test
  fun `local json round trip preserves workspace and tasks`() {
    val context = ApplicationProvider.getApplicationContext<Context>()
    val manager = LocalDataManager(context)
    val seeded = manager.resetToDemoData()
    assertTrue(seeded.tasks.isNotEmpty())

    val exported = manager.exportFormattedJson(seeded)
    val imported = manager.importFromJson(exported)
    assertTrue(imported.isSuccess)
    assertEquals(seeded.tasks.size, imported.getOrThrow().tasks.size)
    assertEquals(seeded.activeSprint.id, imported.getOrThrow().activeSprint.id)
  }

  @Test
  fun `view model creates advances and rolls over sprint cleanly`() {
    val app = ApplicationProvider.getApplicationContext<android.app.Application>()
    val vm = DevSprintViewModel(app)
    vm.resetToDemoData()

    val initialCount = vm.uiState.value.tasks.size
    vm.saveTask(
      editingId = null,
      title = "Verify offline PKCE store",
      description = "Ensure zero network calls.",
      type = TaskType.BACKEND,
      priority = Priority.P0,
      module = "AuthModule",
      storyPoints = 5,
      status = TaskStatus.BACKLOG,
      dueDate = "2026-11-30",
    )
    val afterCreate = vm.uiState.value.tasks
    assertEquals(initialCount + 1, afterCreate.size)
    val created = afterCreate.first()
    assertEquals(TaskStatus.BACKLOG, created.status)

    vm.advanceTaskStatus(created.id)
    assertEquals(
      TaskStatus.IN_PROGRESS,
      vm.uiState.value.tasks.first { it.id == created.id }.status,
    )

    vm.closeCurrentSprintAndRollOver("Next Sprint Focus")
    assertEquals("Next Sprint Focus", vm.uiState.value.workspace.activeSprint.goal)
    assertTrue(vm.uiState.value.tasks.all { it.status == TaskStatus.BACKLOG })
  }
}
