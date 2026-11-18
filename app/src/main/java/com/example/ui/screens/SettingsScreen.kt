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
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ContentCopy
import androidx.compose.material.icons.filled.Download
import androidx.compose.material.icons.filled.RestartAlt
import androidx.compose.material.icons.filled.Shield
import androidx.compose.material.icons.filled.Upload
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExposedDropdownMenuBox
import androidx.compose.material3.ExposedDropdownMenuDefaults
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.MenuAnchorType
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Switch
import androidx.compose.material3.SwitchDefaults
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
import androidx.compose.ui.platform.LocalClipboardManager
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.example.R
import com.example.model.Priority
import com.example.ui.theme.DevSprintTheme
import com.example.viewmodel.DevSprintUiState

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SettingsScreen(
  uiState: DevSprintUiState,
  onToggleDarkMode: (Boolean) -> Unit,
  onToggleCompactView: (Boolean) -> Unit,
  onSelectDefaultPriority: (Priority) -> Unit,
  onSelectSprintDuration: (Int) -> Unit,
  onExportJson: () -> String,
  onImportJson: (String) -> Boolean,
  onResetToDemo: () -> Unit,
  onNavigateBackHome: () -> Unit,
  modifier: Modifier = Modifier,
) {
  BackHandler(onBack = onNavigateBackHome)

  val colors = DevSprintTheme.colors
  val settings = uiState.workspace.appSettings
  val clipboardManager = LocalClipboardManager.current

  var priorityExpanded by remember { mutableStateOf(false) }
  var durationExpanded by remember { mutableStateOf(false) }

  var exportedJsonDialogText by remember { mutableStateOf<String?>(null) }
  var copiedFeedback by remember { mutableStateOf(false) }
  var showImportDialog by remember { mutableStateOf(false) }
  var importJsonInput by remember { mutableStateOf("") }
  var importError by remember { mutableStateOf(false) }
  var showResetConfirmDialog by remember { mutableStateOf(false) }

  val textFieldColors =
    OutlinedTextFieldDefaults.colors(
      focusedBorderColor = colors.accentCyan,
      unfocusedBorderColor = colors.borderSubtle,
      focusedLabelColor = colors.accentCyan,
      unfocusedLabelColor = colors.textSecondary,
      focusedTextColor = colors.textPrimary,
      unfocusedTextColor = colors.textPrimary,
    )

  Box(modifier = modifier.fillMaxSize(), contentAlignment = Alignment.TopCenter) {
    LazyColumn(
      modifier = Modifier.widthIn(max = 760.dp).fillMaxSize(),
      contentPadding = PaddingValues(start = 16.dp, end = 16.dp, top = 12.dp, bottom = 32.dp),
      verticalArrangement = Arrangement.spacedBy(16.dp),
    ) {
      // Privacy Verification Card
      item {
        Card(
          modifier = Modifier.fillMaxWidth().testTag("privacy_verification_card"),
          shape = RoundedCornerShape(14.dp),
          colors = CardDefaults.cardColors(containerColor = colors.successContainer),
          border = BorderStroke(1.dp, colors.successEmerald.copy(alpha = 0.5f)),
        ) {
          Row(
            modifier = Modifier.padding(16.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(12.dp),
          ) {
            Box(
              modifier =
                Modifier
                  .size(40.dp)
                  .clip(CircleShape)
                  .background(colors.successEmerald.copy(alpha = 0.2f)),
              contentAlignment = Alignment.Center,
            ) {
              Icon(
                imageVector = Icons.Filled.Shield,
                contentDescription = stringResource(R.string.cd_privacy_shield),
                tint = colors.successEmerald,
                modifier = Modifier.size(22.dp),
              )
            }
            Column(verticalArrangement = Arrangement.spacedBy(4.dp)) {
              Text(
                text = stringResource(R.string.privacy_badge_title),
                style = MaterialTheme.typography.titleSmall.copy(fontWeight = FontWeight.Bold),
                color = colors.successEmerald,
              )
              Text(
                text = stringResource(R.string.privacy_badge_body),
                style = MaterialTheme.typography.bodySmall,
                color = colors.textPrimary,
              )
            }
          }
        }
      }

      // Display Preferences
      item {
        Card(
          modifier = Modifier.fillMaxWidth(),
          shape = RoundedCornerShape(14.dp),
          colors = CardDefaults.cardColors(containerColor = colors.surfaceCard),
          border = BorderStroke(1.dp, colors.borderSubtle),
        ) {
          Column(
            modifier = Modifier.padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(14.dp),
          ) {
            Text(
              text = stringResource(R.string.settings_display).uppercase(),
              style = MaterialTheme.typography.labelSmall,
              color = colors.accentCyan,
            )

            SettingSwitchRow(
              title = stringResource(R.string.settings_dark_mode),
              subtitle = stringResource(R.string.settings_dark_mode_desc),
              checked = settings.darkMode,
              onCheckedChange = onToggleDarkMode,
              testTag = "dark_mode_switch",
            )

            HorizontalDivider(color = colors.borderSubtle)

            SettingSwitchRow(
              title = stringResource(R.string.settings_compact_view),
              subtitle = stringResource(R.string.settings_compact_view_desc),
              checked = settings.compactView,
              onCheckedChange = onToggleCompactView,
              testTag = "compact_view_switch",
            )
          }
        }
      }

      // Workflow Configuration
      item {
        Card(
          modifier = Modifier.fillMaxWidth(),
          shape = RoundedCornerShape(14.dp),
          colors = CardDefaults.cardColors(containerColor = colors.surfaceCard),
          border = BorderStroke(1.dp, colors.borderSubtle),
        ) {
          Column(
            modifier = Modifier.padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(14.dp),
          ) {
            Text(
              text = stringResource(R.string.settings_workflow).uppercase(),
              style = MaterialTheme.typography.labelSmall,
              color = colors.accentCyan,
            )

            // Default Priority Dropdown
            ExposedDropdownMenuBox(
              expanded = priorityExpanded,
              onExpandedChange = { priorityExpanded = it },
            ) {
              OutlinedTextField(
                value = settings.defaultPriority.label,
                onValueChange = {},
                readOnly = true,
                label = { Text(stringResource(R.string.settings_default_priority)) },
                trailingIcon = {
                  ExposedDropdownMenuDefaults.TrailingIcon(expanded = priorityExpanded)
                },
                colors = textFieldColors,
                modifier =
                  Modifier
                    .fillMaxWidth()
                    .menuAnchor(MenuAnchorType.PrimaryNotEditable),
              )
              ExposedDropdownMenu(
                expanded = priorityExpanded,
                onDismissRequest = { priorityExpanded = false },
                modifier = Modifier.background(colors.surfaceElevated),
              ) {
                Priority.entries.forEach { priority ->
                  DropdownMenuItem(
                    text = { Text(priority.label, color = colors.textPrimary) },
                    onClick = {
                      onSelectDefaultPriority(priority)
                      priorityExpanded = false
                    },
                  )
                }
              }
            }

            // Default Sprint Duration Dropdown
            val durations = listOf(7, 14, 21, 30)
            ExposedDropdownMenuBox(
              expanded = durationExpanded,
              onExpandedChange = { durationExpanded = it },
            ) {
              OutlinedTextField(
                value = "${settings.sprintDurationDays} Days",
                onValueChange = {},
                readOnly = true,
                label = { Text(stringResource(R.string.settings_default_sprint_duration)) },
                trailingIcon = {
                  ExposedDropdownMenuDefaults.TrailingIcon(expanded = durationExpanded)
                },
                colors = textFieldColors,
                modifier =
                  Modifier
                    .fillMaxWidth()
                    .menuAnchor(MenuAnchorType.PrimaryNotEditable),
              )
              ExposedDropdownMenu(
                expanded = durationExpanded,
                onDismissRequest = { durationExpanded = false },
                modifier = Modifier.background(colors.surfaceElevated),
              ) {
                durations.forEach { days ->
                  DropdownMenuItem(
                    text = { Text("$days Days", color = colors.textPrimary) },
                    onClick = {
                      onSelectSprintDuration(days)
                      durationExpanded = false
                    },
                  )
                }
              }
            }
          }
        }
      }

      // Data Management Panel
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
            Text(
              text = stringResource(R.string.settings_data_mgmt).uppercase(),
              style = MaterialTheme.typography.labelSmall,
              color = colors.accentCyan,
            )

            Row(
              modifier = Modifier.fillMaxWidth(),
              horizontalArrangement = Arrangement.spacedBy(10.dp),
            ) {
              Button(
                onClick = {
                  copiedFeedback = false
                  exportedJsonDialogText = onExportJson()
                },
                modifier = Modifier.weight(1f).testTag("export_json_button"),
                shape = RoundedCornerShape(10.dp),
                colors =
                  ButtonDefaults.buttonColors(
                    containerColor = colors.accentCyan,
                    contentColor = if (colors.isDark) colors.background else Color.White,
                  ),
              ) {
                Icon(
                  imageVector = Icons.Filled.Upload,
                  contentDescription = null,
                  modifier = Modifier.size(16.dp),
                )
                Spacer(modifier = Modifier.width(6.dp))
                Text(
                  text = stringResource(R.string.settings_export_json),
                  style = MaterialTheme.typography.labelMedium.copy(fontWeight = FontWeight.Bold),
                )
              }

              OutlinedButton(
                onClick = {
                  importError = false
                  importJsonInput = ""
                  showImportDialog = true
                },
                modifier = Modifier.weight(1f).testTag("import_json_button"),
                shape = RoundedCornerShape(10.dp),
                border = BorderStroke(1.dp, colors.accentCyan.copy(alpha = 0.6f)),
                colors = ButtonDefaults.outlinedButtonColors(contentColor = colors.accentCyan),
              ) {
                Icon(
                  imageVector = Icons.Filled.Download,
                  contentDescription = null,
                  modifier = Modifier.size(16.dp),
                )
                Spacer(modifier = Modifier.width(6.dp))
                Text(
                  text = stringResource(R.string.settings_import_json),
                  style = MaterialTheme.typography.labelMedium.copy(fontWeight = FontWeight.Bold),
                )
              }
            }

            OutlinedButton(
              onClick = { showResetConfirmDialog = true },
              modifier = Modifier.fillMaxWidth().testTag("reset_demo_button"),
              shape = RoundedCornerShape(10.dp),
              border = BorderStroke(1.dp, colors.criticalCrimson.copy(alpha = 0.55f)),
              colors = ButtonDefaults.outlinedButtonColors(contentColor = colors.criticalCrimson),
            ) {
              Icon(
                imageVector = Icons.Filled.RestartAlt,
                contentDescription = null,
                modifier = Modifier.size(18.dp),
              )
              Spacer(modifier = Modifier.width(6.dp))
              Text(
                text = stringResource(R.string.settings_reset_demo),
                style = MaterialTheme.typography.labelLarge,
              )
            }
          }
        }
      }
    }
  }

  // Export JSON Preview & Copy Dialog
  exportedJsonDialogText?.let { jsonText ->
    AlertDialog(
      onDismissRequest = { exportedJsonDialogText = null },
      containerColor = colors.surfaceCard,
      titleContentColor = colors.textPrimary,
      textContentColor = colors.textSecondary,
      title = { Text("Local Workspace JSON") },
      text = {
        Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
          Text(
            text = "Stored strictly on-device via org.json. Copy to back up locally.",
            style = MaterialTheme.typography.bodySmall,
          )
          Box(
            modifier =
              Modifier
                .fillMaxWidth()
                .height(220.dp)
                .clip(RoundedCornerShape(10.dp))
                .background(colors.background)
                .padding(10.dp)
                .verticalScroll(rememberScrollState()),
          ) {
            Text(
              text = jsonText,
              style = MaterialTheme.typography.bodySmall.copy(fontFamily = FontFamily.Monospace),
              color = colors.accentCyan,
            )
          }
          if (copiedFeedback) {
            Text(
              text = "Copied JSON to clipboard.",
              style = MaterialTheme.typography.labelMedium,
              color = colors.successEmerald,
            )
          }
        }
      },
      confirmButton = {
        Button(
          onClick = {
            clipboardManager.setText(AnnotatedString(jsonText))
            copiedFeedback = true
          },
          shape = RoundedCornerShape(10.dp),
          colors =
            ButtonDefaults.buttonColors(
              containerColor = colors.accentCyan,
              contentColor = if (colors.isDark) colors.background else Color.White,
            ),
        ) {
          Icon(
            imageVector = Icons.Filled.ContentCopy,
            contentDescription = null,
            modifier = Modifier.size(16.dp),
          )
          Spacer(modifier = Modifier.width(6.dp))
          Text("Copy JSON")
        }
      },
      dismissButton = {
        TextButton(onClick = { exportedJsonDialogText = null }) {
          Text("Close", color = colors.textSecondary)
        }
      },
    )
  }

  // Import JSON Dialog
  if (showImportDialog) {
    AlertDialog(
      onDismissRequest = { showImportDialog = false },
      containerColor = colors.surfaceCard,
      titleContentColor = colors.textPrimary,
      textContentColor = colors.textSecondary,
      title = { Text("Import Local JSON") },
      text = {
        Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
          Text(
            text = "Paste a valid DevSprint JSON workspace payload below.",
            style = MaterialTheme.typography.bodySmall,
          )
          OutlinedTextField(
            value = importJsonInput,
            onValueChange = {
              importJsonInput = it
              importError = false
            },
            placeholder = { Text("{\"appSettings\": ..., \"activeSprint\": ..., \"tasks\": [...]}") },
            isError = importError,
            supportingText =
              if (importError) {
                { Text("Invalid DevSprint JSON schema", color = colors.criticalCrimson) }
              } else null,
            minLines = 5,
            maxLines = 8,
            colors = textFieldColors,
            modifier = Modifier.fillMaxWidth(),
          )
        }
      },
      confirmButton = {
        Button(
          onClick = {
            val ok = onImportJson(importJsonInput)
            if (ok) {
              showImportDialog = false
            } else {
              importError = true
            }
          },
          shape = RoundedCornerShape(10.dp),
          colors =
            ButtonDefaults.buttonColors(
              containerColor = colors.accentCyan,
              contentColor = if (colors.isDark) colors.background else Color.White,
            ),
        ) {
          Text("Validate & Import")
        }
      },
      dismissButton = {
        TextButton(onClick = { showImportDialog = false }) {
          Text(stringResource(R.string.action_cancel), color = colors.textSecondary)
        }
      },
    )
  }

  // Reset Confirmation Dialog
  if (showResetConfirmDialog) {
    AlertDialog(
      onDismissRequest = { showResetConfirmDialog = false },
      containerColor = colors.surfaceCard,
      titleContentColor = colors.textPrimary,
      textContentColor = colors.textSecondary,
      title = { Text("Reset to Demo Data?") },
      text = {
        Text(
          "This will replace your current local sprint and tasks with the default Sprint 24 engineering dataset.",
          style = MaterialTheme.typography.bodyMedium,
        )
      },
      confirmButton = {
        Button(
          onClick = {
            showResetConfirmDialog = false
            onResetToDemo()
          },
          shape = RoundedCornerShape(10.dp),
          colors = ButtonDefaults.buttonColors(containerColor = colors.criticalCrimson),
        ) {
          Text("Reset Data", color = Color.White)
        }
      },
      dismissButton = {
        TextButton(onClick = { showResetConfirmDialog = false }) {
          Text(stringResource(R.string.action_cancel), color = colors.textSecondary)
        }
      },
    )
  }
}

@Composable
private fun SettingSwitchRow(
  title: String,
  subtitle: String,
  checked: Boolean,
  onCheckedChange: (Boolean) -> Unit,
  testTag: String,
) {
  val colors = DevSprintTheme.colors

  Row(
    modifier = Modifier.fillMaxWidth(),
    horizontalArrangement = Arrangement.SpaceBetween,
    verticalAlignment = Alignment.CenterVertically,
  ) {
    Column(
      modifier = Modifier.weight(1f).padding(end = 12.dp),
      verticalArrangement = Arrangement.spacedBy(2.dp),
    ) {
      Text(
        text = title,
        style = MaterialTheme.typography.titleSmall,
        color = colors.textPrimary,
      )
      Text(
        text = subtitle,
        style = MaterialTheme.typography.bodySmall,
        color = colors.textSecondary,
      )
    }
    Switch(
      checked = checked,
      onCheckedChange = onCheckedChange,
      modifier = Modifier.testTag(testTag),
      colors =
        SwitchDefaults.colors(
          checkedThumbColor = if (colors.isDark) colors.background else Color.White,
          checkedTrackColor = colors.accentCyan,
          uncheckedThumbColor = colors.textSecondary,
          uncheckedTrackColor = colors.surfaceElevated,
          uncheckedBorderColor = colors.borderSubtle,
        ),
    )
  }
}
