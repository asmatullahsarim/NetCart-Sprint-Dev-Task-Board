package com.example

import android.graphics.Color as AndroidColor
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.SystemBarStyle
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.togetherWith
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.navigationBars
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Assignment
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.RocketLaunch
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material.icons.outlined.Assignment
import androidx.compose.material.icons.outlined.Home
import androidx.compose.material.icons.outlined.RocketLaunch
import androidx.compose.material.icons.outlined.Settings
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.NavigationBarItemDefaults
import androidx.compose.material3.NavigationRail
import androidx.compose.material3.NavigationRailItem
import androidx.compose.material3.NavigationRailItemDefaults
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Snackbar
import androidx.compose.material3.SnackbarDuration
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.model.Screen
import com.example.ui.components.CreateEditTaskBottomSheet
import com.example.ui.screens.HomeScreen
import com.example.ui.screens.SettingsScreen
import com.example.ui.screens.SplashScreen
import com.example.ui.screens.SprintsScreen
import com.example.ui.screens.TasksScreen
import com.example.ui.theme.DevSprintTheme
import com.example.viewmodel.DevSprintViewModel

class MainActivity : ComponentActivity() {
  override fun onCreate(savedInstanceState: Bundle?) {
    super.onCreate(savedInstanceState)
    enableEdgeToEdge(
      statusBarStyle = SystemBarStyle.dark(AndroidColor.TRANSPARENT),
      navigationBarStyle = SystemBarStyle.dark(AndroidColor.TRANSPARENT),
    )

    setContent {
      val viewModel: DevSprintViewModel = viewModel()
      val uiState by viewModel.uiState.collectAsStateWithLifecycle()
      val isDarkMode = uiState.workspace.appSettings.darkMode

      LaunchedEffect(isDarkMode, uiState.currentScreen) {
        val useDarkBars = isDarkMode || uiState.currentScreen is Screen.Splash
        val barStyle =
          if (useDarkBars) {
            SystemBarStyle.dark(AndroidColor.TRANSPARENT)
          } else {
            SystemBarStyle.light(AndroidColor.TRANSPARENT, AndroidColor.TRANSPARENT)
          }
        enableEdgeToEdge(statusBarStyle = barStyle, navigationBarStyle = barStyle)
      }

      DevSprintTheme(darkTheme = isDarkMode) {
        if (uiState.currentScreen is Screen.Splash) {
          SplashScreen(onTimeout = viewModel::onSplashCompleted)
        } else {
          DevSprintMainShell(viewModel = viewModel)
        }
      }
    }
  }
}

private data class NavDestination(
  val screen: Screen,
  val labelRes: Int,
  val selectedIcon: ImageVector,
  val unselectedIcon: ImageVector,
  val testTag: String,
)

private val mainDestinations =
  listOf(
    NavDestination(
      screen = Screen.Home,
      labelRes = R.string.nav_home,
      selectedIcon = Icons.Filled.Home,
      unselectedIcon = Icons.Outlined.Home,
      testTag = "nav_home",
    ),
    NavDestination(
      screen = Screen.Tasks,
      labelRes = R.string.nav_tasks,
      selectedIcon = Icons.Filled.Assignment,
      unselectedIcon = Icons.Outlined.Assignment,
      testTag = "nav_tasks",
    ),
    NavDestination(
      screen = Screen.Sprints,
      labelRes = R.string.nav_sprints,
      selectedIcon = Icons.Filled.RocketLaunch,
      unselectedIcon = Icons.Outlined.RocketLaunch,
      testTag = "nav_sprints",
    ),
    NavDestination(
      screen = Screen.Settings,
      labelRes = R.string.nav_settings,
      selectedIcon = Icons.Filled.Settings,
      unselectedIcon = Icons.Outlined.Settings,
      testTag = "nav_settings",
    ),
  )

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun DevSprintMainShell(viewModel: DevSprintViewModel) {
  val uiState by viewModel.uiState.collectAsStateWithLifecycle()
  val colors = DevSprintTheme.colors
  val snackbarHostState = remember { SnackbarHostState() }

  LaunchedEffect(uiState.statusBannerMessage) {
    val message = uiState.statusBannerMessage
    if (!message.isNullOrBlank()) {
      snackbarHostState.showSnackbar(message = message, duration = SnackbarDuration.Short)
      viewModel.clearStatusBanner()
    }
  }

  val showFab = uiState.currentScreen is Screen.Home || uiState.currentScreen is Screen.Tasks

  BoxWithConstraints(modifier = Modifier.fillMaxSize().background(colors.background)) {
    val useNavigationRail = maxWidth >= 600.dp

    Row(modifier = Modifier.fillMaxSize()) {
      if (useNavigationRail) {
        NavigationRail(
          containerColor = colors.surfaceCard,
          contentColor = colors.textPrimary,
          modifier = Modifier.fillMaxHeight(),
        ) {
          Spacer(modifier = Modifier.weight(0.1f))
          mainDestinations.forEach { dest ->
            val selected = uiState.currentScreen == dest.screen
            NavigationRailItem(
              selected = selected,
              onClick = { viewModel.navigateTo(dest.screen) },
              modifier = Modifier.testTag(dest.testTag),
              icon = {
                Icon(
                  imageVector = if (selected) dest.selectedIcon else dest.unselectedIcon,
                  contentDescription = stringResource(dest.labelRes),
                )
              },
              label = {
                Text(
                  text = stringResource(dest.labelRes),
                  style = MaterialTheme.typography.labelSmall,
                )
              },
              colors =
                NavigationRailItemDefaults.colors(
                  selectedIconColor = colors.accentCyan,
                  selectedTextColor = colors.accentCyan,
                  indicatorColor = colors.accentCyanContainer,
                  unselectedIconColor = colors.textSecondary,
                  unselectedTextColor = colors.textSecondary,
                ),
            )
          }
          Spacer(modifier = Modifier.weight(1f))
        }
      }

      Scaffold(
        modifier = Modifier.weight(1f).fillMaxHeight(),
        containerColor = colors.background,
        contentWindowInsets = WindowInsets(0, 0, 0, 0),
        snackbarHost = {
          SnackbarHost(hostState = snackbarHostState) { data ->
            Snackbar(
              snackbarData = data,
              containerColor = colors.surfaceElevated,
              contentColor = colors.textPrimary,
              actionColor = colors.accentCyan,
              shape = RoundedCornerShape(10.dp),
            )
          }
        },
        topBar = {
          Column(modifier = Modifier.background(colors.background)) {
            TopAppBar(
              colors =
                TopAppBarDefaults.topAppBarColors(
                  containerColor = colors.background,
                  titleContentColor = colors.textPrimary,
                ),
              title = {
                Row(
                  verticalAlignment = Alignment.CenterVertically,
                  horizontalArrangement = Arrangement.spacedBy(10.dp),
                ) {
                  Image(
                    painter = painterResource(id = R.drawable.ic_netcart_foreground),
                    contentDescription = stringResource(R.string.cd_app_logo),
                    contentScale = ContentScale.Crop,
                    modifier = Modifier.size(30.dp).clip(RoundedCornerShape(8.dp)),
                  )
                  Text(
                    text = stringResource(R.string.app_short_name),
                    style = MaterialTheme.typography.titleLarge,
                    color = colors.textPrimary,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis,
                  )
                }
              },
              actions = {
                // Active sprint badge
                Surface(
                  modifier =
                    Modifier
                      .padding(end = 4.dp)
                      .clip(RoundedCornerShape(10.dp))
                      .clickable(role = Role.Button) { viewModel.navigateTo(Screen.Sprints) },
                  shape = RoundedCornerShape(10.dp),
                  color = colors.successContainer,
                  border = BorderStroke(1.dp, colors.successEmerald.copy(alpha = 0.45f)),
                ) {
                  Text(
                    text = uiState.workspace.activeSprint.badgeLabel,
                    style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.Bold),
                    color = colors.successEmerald,
                    modifier = Modifier.padding(horizontal = 10.dp, vertical = 5.dp),
                  )
                }

                // Quick search trigger
                IconButton(
                  onClick = {
                    if (uiState.currentScreen !is Screen.Home && uiState.currentScreen !is Screen.Tasks) {
                      viewModel.navigateTo(Screen.Tasks)
                      if (!uiState.isSearchVisible) viewModel.toggleSearch()
                    } else {
                      viewModel.toggleSearch()
                    }
                  },
                  modifier = Modifier.testTag("search_toggle_button"),
                ) {
                  Icon(
                    imageVector =
                      if (uiState.isSearchVisible) Icons.Filled.Close else Icons.Filled.Search,
                    contentDescription =
                      if (uiState.isSearchVisible) stringResource(R.string.cd_close_search)
                      else stringResource(R.string.cd_search),
                    tint = if (uiState.isSearchVisible) colors.accentCyan else colors.textPrimary,
                  )
                }
              },
            )

            AnimatedVisibility(visible = uiState.isSearchVisible) {
              OutlinedTextField(
                value = uiState.searchQuery,
                onValueChange = viewModel::updateSearchQuery,
                placeholder = {
                  Text(
                    text = stringResource(R.string.search_placeholder),
                    style = MaterialTheme.typography.bodyMedium,
                    color = colors.textSecondary,
                  )
                },
                leadingIcon = {
                  Icon(
                    imageVector = Icons.Filled.Search,
                    contentDescription = null,
                    tint = colors.accentCyan,
                  )
                },
                trailingIcon = {
                  if (uiState.searchQuery.isNotEmpty()) {
                    IconButton(onClick = { viewModel.updateSearchQuery("") }) {
                      Icon(
                        imageVector = Icons.Filled.Close,
                        contentDescription = stringResource(R.string.cd_close_search),
                        tint = colors.textSecondary,
                      )
                    }
                  }
                },
                singleLine = true,
                shape = RoundedCornerShape(12.dp),
                colors =
                  OutlinedTextFieldDefaults.colors(
                    focusedBorderColor = colors.accentCyan,
                    unfocusedBorderColor = colors.borderSubtle,
                    focusedContainerColor = colors.surfaceCard,
                    unfocusedContainerColor = colors.surfaceCard,
                    focusedTextColor = colors.textPrimary,
                    unfocusedTextColor = colors.textPrimary,
                  ),
                modifier =
                  Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp, vertical = 6.dp)
                    .testTag("quick_search_input"),
              )
            }
            HorizontalDivider(color = colors.borderSubtle.copy(alpha = 0.6f))
          }
        },
        bottomBar = {
          if (!useNavigationRail) {
            Column {
              HorizontalDivider(color = colors.borderSubtle)
              NavigationBar(
                containerColor = colors.surfaceCard,
                contentColor = colors.textPrimary,
                windowInsets = WindowInsets.navigationBars,
              ) {
                mainDestinations.forEach { dest ->
                  val selected = uiState.currentScreen == dest.screen
                  NavigationBarItem(
                    selected = selected,
                    onClick = { viewModel.navigateTo(dest.screen) },
                    modifier = Modifier.testTag(dest.testTag),
                    icon = {
                      Icon(
                        imageVector = if (selected) dest.selectedIcon else dest.unselectedIcon,
                        contentDescription = stringResource(dest.labelRes),
                      )
                    },
                    label = {
                      Text(
                        text = stringResource(dest.labelRes),
                        style =
                          MaterialTheme.typography.labelSmall.copy(
                            fontWeight = if (selected) FontWeight.Bold else FontWeight.Medium
                          ),
                      )
                    },
                    colors =
                      NavigationBarItemDefaults.colors(
                        selectedIconColor = colors.accentCyan,
                        selectedTextColor = colors.accentCyan,
                        indicatorColor = colors.accentCyanContainer,
                        unselectedIconColor = colors.textSecondary,
                        unselectedTextColor = colors.textSecondary,
                      ),
                  )
                }
              }
            }
          }
        },
        floatingActionButton = {
          if (showFab) {
            FloatingActionButton(
              onClick = viewModel::openCreateTaskSheet,
              modifier = Modifier.testTag("create_task_fab"),
              shape = RoundedCornerShape(16.dp),
              containerColor = colors.accentCyan,
              contentColor = if (colors.isDark) colors.background else Color.White,
            ) {
              Icon(
                imageVector = Icons.Filled.Add,
                contentDescription = stringResource(R.string.cd_create_task),
              )
            }
          }
        },
      ) { innerPadding ->
        Box(modifier = Modifier.fillMaxSize().padding(innerPadding)) {
          AnimatedContent(
            targetState = uiState.currentScreen,
            transitionSpec = { fadeIn() togetherWith fadeOut() },
            label = "screen_transition",
          ) { screen ->
            when (screen) {
              Screen.Home ->
                HomeScreen(
                  uiState = uiState,
                  onNavigateToTasksStatus = viewModel::navigateToTasksWithStatus,
                  onTaskClick = viewModel::openEditTaskSheet,
                  onAdvanceStatus = viewModel::advanceTaskStatus,
                )
              Screen.Tasks ->
                TasksScreen(
                  uiState = uiState,
                  onSelectCategory = viewModel::selectCategoryFilter,
                  onSelectStatusTab = viewModel::selectStatusTab,
                  onTaskClick = viewModel::openEditTaskSheet,
                  onAdvanceStatus = viewModel::advanceTaskStatus,
                  onNavigateBackHome = { viewModel.navigateTo(Screen.Home) },
                )
              Screen.Sprints ->
                SprintsScreen(
                  uiState = uiState,
                  onCloseSprintAndRollOver = viewModel::closeCurrentSprintAndRollOver,
                  onNavigateBackHome = { viewModel.navigateTo(Screen.Home) },
                )
              Screen.Settings ->
                SettingsScreen(
                  uiState = uiState,
                  onToggleDarkMode = viewModel::updateDarkMode,
                  onToggleCompactView = viewModel::updateCompactView,
                  onSelectDefaultPriority = viewModel::updateDefaultPriority,
                  onSelectSprintDuration = viewModel::updateSprintDurationDays,
                  onExportJson = viewModel::exportWorkspaceJson,
                  onImportJson = viewModel::importWorkspaceJson,
                  onResetToDemo = viewModel::resetToDemoData,
                  onNavigateBackHome = { viewModel.navigateTo(Screen.Home) },
                )
              Screen.Splash -> Unit
            }
          }
        }
      }
    }
  }

  if (uiState.isTaskSheetOpen) {
    CreateEditTaskBottomSheet(
      editingTask = uiState.editingTask,
      defaultPriority = uiState.workspace.appSettings.defaultPriority,
      defaultDueDate = viewModel.defaultDueDateForNewTask(),
      onDismiss = viewModel::dismissTaskSheet,
      onSave = viewModel::saveTask,
      onDelete = if (uiState.editingTask != null) viewModel::deleteTask else null,
    )
  }
}
