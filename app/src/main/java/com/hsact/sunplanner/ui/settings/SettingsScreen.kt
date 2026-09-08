package com.hsact.sunplanner.ui.settings

import android.content.Intent
import android.net.Uri
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.selection.toggleable
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.DeleteForever
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.filled.Palette
import androidx.compose.material.icons.filled.Straighten
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.ListItem
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SegmentedButton
import androidx.compose.material3.SegmentedButtonDefaults
import androidx.compose.material3.SingleChoiceSegmentedButtonRow
import androidx.compose.material3.Surface
import androidx.compose.material3.Switch
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import com.hsact.sunplanner.BuildConfig
import com.hsact.sunplanner.R
import com.hsact.sunplanner.domain.model.LanguageMode
import com.hsact.sunplanner.domain.model.PrecipitationUnitMode
import com.hsact.sunplanner.domain.model.TemperatureUnitMode
import com.hsact.sunplanner.domain.model.ThemeMode
import com.hsact.sunplanner.domain.model.WindSpeedUnitMode
import com.hsact.sunplanner.ui.components.DropdownPicker
import com.hsact.sunplanner.ui.utils.stringArrayResource

/**
 * The main entry point for the Settings screen.
 *
 * @param viewModel The [SettingsViewModel] providing state and intent handling.
 * @param onBack Callback to navigate back.
 * @param onApplyTheme Callback to apply the selected theme mode to the app.
 * @param onChangeLanguage Callback to apply the selected language mode to the app.
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SettingsScreen(
    viewModel: SettingsViewModel,
    onBack: () -> Unit,
    onApplyTheme: (ThemeMode) -> Unit,
    onChangeLanguage: (LanguageMode) -> Unit
) {
    val uiState by viewModel.uiState.collectAsState()
    val context = LocalContext.current
    val donationUrl = stringResource(R.string.donation_url)

    LaunchedEffect(Unit) {
        viewModel.setAppVersion(BuildConfig.VERSION_NAME)
    }

    if (uiState.isClearCacheDialogOpen) {
        AlertDialog(
            onDismissRequest = {
                viewModel.handleIntent(
                    SettingsIntents.SetClearCacheDialogVisible(
                        false
                    )
                )
            },
            title = { Text(stringResource(R.string.data_management)) },
            text = { Text(stringResource(R.string.confirm_clear_cache)) },
            confirmButton = {
                TextButton(
                    onClick = { viewModel.handleIntent(SettingsIntents.ClearCache) },
                    colors = ButtonDefaults.textButtonColors(contentColor = MaterialTheme.colorScheme.error)
                ) {
                    Text(stringResource(R.string.clear))
                }
            },
            dismissButton = {
                TextButton(onClick = {
                    viewModel.handleIntent(
                        SettingsIntents.SetClearCacheDialogVisible(
                            false
                        )
                    )
                }) {
                    Text(stringResource(R.string.cancel))
                }
            }
        )
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text(stringResource(R.string.settings)) },
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = null)
                    }
                }
            )
        }
    ) { innerPadding ->
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
        ) {
            item {
                SettingsSectionHeader(
                    stringResource(R.string.appearance),
                    Icons.Default.Palette
                )
            }

            item {
                LanguageSetting(
                    currentLanguage = uiState.language,
                    onLanguageSelected = {
                        viewModel.handleIntent(SettingsIntents.UpdateLanguage(it))
                        onChangeLanguage(it)
                    }
                )
            }

            item {
                ThemeSetting(
                    currentTheme = uiState.theme,
                    onThemeSelected = {
                        viewModel.handleIntent(SettingsIntents.UpdateTheme(it))
                        onApplyTheme(it)
                    }
                )
            }

            item {
                ToggleSetting(
                    label = stringResource(R.string.show_graph_dots),
                    checked = uiState.isDotsVisible,
                    onCheckedChange = { viewModel.handleIntent(SettingsIntents.UpdateDotsOption(it)) }
                )
            }

            item {
                ToggleSetting(
                    label = stringResource(R.string.curved_edges),
                    checked = uiState.isEdgesCurved,
                    onCheckedChange = { viewModel.handleIntent(SettingsIntents.UpdateCurveOption(it)) }
                )
            }

            item { HorizontalDivider(modifier = Modifier.padding(vertical = 8.dp)) }
            item { SettingsSectionHeader(stringResource(R.string.units), Icons.Default.Straighten) }

            item {
                UnitSetting(
                    label = stringResource(R.string.temperature),
                    options = stringArrayResource(R.array.temp_unit_choices).toList(),
                    selectedIndex = uiState.temperatureUnit.toIndex(),
                    onOptionSelected = {
                        viewModel.handleIntent(
                            SettingsIntents.UpdateTemperatureUnit(
                                TemperatureUnitMode.fromIndex(it)
                            )
                        )
                    }
                )
            }

            item {
                UnitSetting(
                    label = stringResource(R.string.wind_speed),
                    options = stringArrayResource(R.array.speed_unit_choices).toList(),
                    selectedIndex = uiState.windSpeedUnit.toIndex(),
                    onOptionSelected = {
                        viewModel.handleIntent(
                            SettingsIntents.UpdateWindSpeedUnit(
                                WindSpeedUnitMode.fromIndex(it)
                            )
                        )
                    }
                )
            }

            item {
                UnitSetting(
                    label = stringResource(R.string.precipitation),
                    options = stringArrayResource(R.array.precipitation_unit_choices).toList(),
                    selectedIndex = uiState.precipitationUnit.toIndex(),
                    onOptionSelected = {
                        viewModel.handleIntent(
                            SettingsIntents.UpdatePrecipitationUnit(
                                PrecipitationUnitMode.fromIndex(it)
                            )
                        )
                    }
                )
            }

            item { HorizontalDivider(modifier = Modifier.padding(vertical = 8.dp)) }
            item {
                SettingsSectionHeader(
                    stringResource(R.string.data_management),
                    Icons.Default.DeleteForever
                )
            }

            item {
                ListItem(
                    headlineContent = {
                        Text(
                            stringResource(R.string.clear_data),
                            color = MaterialTheme.colorScheme.error
                        )
                    },
                    supportingContent = { Text(stringResource(R.string.clear_data_desc)) },
                    trailingContent = {
                        Button(
                            onClick = {
                                viewModel.handleIntent(
                                    SettingsIntents.SetClearCacheDialogVisible(
                                        true
                                    )
                                )
                            },
                            colors = ButtonDefaults.buttonColors(
                                containerColor = MaterialTheme.colorScheme.errorContainer,
                                contentColor = MaterialTheme.colorScheme.onErrorContainer
                            )
                        ) {
                            Text(stringResource(R.string.clear))
                        }
                    }
                )
            }

            item { HorizontalDivider(modifier = Modifier.padding(vertical = 8.dp)) }
            item { SettingsSectionHeader(stringResource(R.string.about_app), Icons.Default.Info) }

            item {
                AboutSection(
                    version = uiState.appVersion,
                    onDonateClick = {
                        val intent = Intent(Intent.ACTION_VIEW, Uri.parse(donationUrl))
                        context.startActivity(intent)
                    }
                )
            }

            item { Spacer(modifier = Modifier.height(32.dp)) }
        }
    }
}

/**
 * Renders a header for a settings section.
 *
 * @param title The title of the section.
 * @param icon The icon to display next to the title.
 */
@Composable
private fun SettingsSectionHeader(title: String, icon: ImageVector) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(16.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Icon(
            icon,
            contentDescription = null,
            tint = MaterialTheme.colorScheme.primary,
            modifier = Modifier.size(20.dp)
        )
        Spacer(modifier = Modifier.width(12.dp))
        Text(
            text = title,
            style = MaterialTheme.typography.titleSmall,
            color = MaterialTheme.colorScheme.primary,
            fontWeight = FontWeight.Bold
        )
    }
}

/**
 * Renders the language selection setting.
 *
 * @param currentLanguage The currently active [LanguageMode].
 * @param onLanguageSelected Callback for language selection.
 */
@Composable
private fun LanguageSetting(
    currentLanguage: LanguageMode,
    onLanguageSelected: (LanguageMode) -> Unit
) {
    val choices = stringArrayResource(R.array.language_choices).toList()
    ListItem(
        headlineContent = { Text(stringResource(R.string.language)) },
        trailingContent = {
            DropdownPicker(
                label = "",
                list = choices,
                selected = choices[currentLanguage.toIndex()],
                onSelected = { onLanguageSelected(LanguageMode.fromIndex(choices.indexOf(it))) },
                modifier = Modifier.width(150.dp),
                isCompact = true
            )
        }
    )
}

/**
 * Renders the theme selection setting using a segmented button row.
 *
 * @param currentTheme The currently active [ThemeMode].
 * @param onThemeSelected Callback for theme selection.
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun ThemeSetting(currentTheme: ThemeMode, onThemeSelected: (ThemeMode) -> Unit) {
    val choices = stringArrayResource(R.array.theme_choices).toList()
    ListItem(
        headlineContent = { Text(stringResource(R.string.theme)) },
        trailingContent = {
            SingleChoiceSegmentedButtonRow {
                choices.forEachIndexed { index, label ->
                    SegmentedButton(
                        selected = index == currentTheme.toIndex(),
                        onClick = { onThemeSelected(ThemeMode.fromIndex(index)) },
                        shape = SegmentedButtonDefaults.itemShape(index, choices.size),
                        label = { Text(label, maxLines = 1) },
                        icon = {}
                    )
                }
            }
        }
    )
}

/**
 * Renders a generic toggle switch setting.
 * The entire row is clickable to toggle the switch.
 *
 * @param label The text label for the setting.
 * @param checked The current state of the switch.
 * @param onCheckedChange Callback for toggle state changes.
 */
@Composable
private fun ToggleSetting(label: String, checked: Boolean, onCheckedChange: (Boolean) -> Unit) {
    ListItem(
        modifier = Modifier
            .fillMaxWidth()
            .toggleable(
                value = checked,
                onValueChange = { onCheckedChange(it) },
                role = Role.Switch
            ),
        headlineContent = { Text(label) },
        trailingContent = {
            Switch(
                checked = checked,
                onCheckedChange = null // Managed by the parent ListItem toggleable modifier
            )
        }
    )
}

/**
 * Renders a unit selection setting.
 * Uses a [DropdownPicker] if there are more than 2 options, otherwise uses a Segmented Button.
 *
 * @param label The label for the unit setting.
 * @param options List of unit names/symbols.
 * @param selectedIndex The index of the currently selected option.
 * @param onOptionSelected Callback invoked with the index of the new selection.
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun UnitSetting(
    label: String,
    options: List<String>,
    selectedIndex: Int,
    onOptionSelected: (Int) -> Unit
) {
    ListItem(
        headlineContent = { Text(label) },
        trailingContent = {
            if (options.size > 2) {
                DropdownPicker(
                    label = "",
                    list = options,
                    selected = options.getOrNull(selectedIndex),
                    onSelected = { onOptionSelected(options.indexOf(it)) },
                    modifier = Modifier.width(120.dp),
                    isCompact = true
                )
            } else {
                SingleChoiceSegmentedButtonRow {
                    options.forEachIndexed { index, opt ->
                        SegmentedButton(
                            selected = index == selectedIndex,
                            onClick = { onOptionSelected(index) },
                            shape = SegmentedButtonDefaults.itemShape(index, options.size),
                            label = { Text(opt) },
                            icon = {}
                        )
                    }
                }
            }
        }
    )
}

/**
 * Renders the "About" section with app description and donation button.
 *
 * @param version The current app version string.
 * @param onDonateClick Callback for the donation button.
 */
@Composable
private fun AboutSection(version: String, onDonateClick: () -> Unit) {
    Column(modifier = Modifier.padding(16.dp)) {
        Text(
            text = stringResource(R.string.app_description),
            style = MaterialTheme.typography.bodyMedium,
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )
        Spacer(modifier = Modifier.height(16.dp))
        Surface(
            color = MaterialTheme.colorScheme.primaryContainer.copy(alpha = 0.3f),
            shape = MaterialTheme.shapes.medium
        ) {
            Column(modifier = Modifier.padding(16.dp)) {
                Text(
                    text = stringResource(R.string.support_project),
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold
                )
                Spacer(modifier = Modifier.height(8.dp))
                Button(
                    onClick = onDonateClick,
                    modifier = Modifier.fillMaxWidth(),
                    colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.primary)
                ) {
                    Icon(
                        Icons.Default.Favorite,
                        contentDescription = null,
                        modifier = Modifier.size(18.dp)
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(stringResource(R.string.donate))
                }
            }
        }
        Spacer(modifier = Modifier.height(24.dp))
        Text(
            text = stringResource(R.string.version, version),
            modifier = Modifier.fillMaxWidth(),
            textAlign = TextAlign.Center,
            style = MaterialTheme.typography.labelSmall,
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )
    }
}
