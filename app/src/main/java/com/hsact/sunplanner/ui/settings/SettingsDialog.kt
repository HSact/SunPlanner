package com.hsact.sunplanner.ui.settings

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.SegmentedButton
import androidx.compose.material3.SegmentedButtonDefaults
import androidx.compose.material3.SingleChoiceSegmentedButtonRow
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import com.hsact.sunplanner.R
import com.hsact.sunplanner.ui.components.DropdownPicker
import com.hsact.sunplanner.ui.settings.modes.LanguageMode
import com.hsact.sunplanner.ui.settings.modes.ThemeMode
import com.hsact.sunplanner.ui.settings.modes.indexToLanguageMode
import com.hsact.sunplanner.ui.settings.modes.indexToThemeMode
import com.hsact.sunplanner.ui.settings.modes.toIndex
import com.hsact.sunplanner.ui.settings.modes.unitModes.indexToPrecipitationUnitMode
import com.hsact.sunplanner.ui.settings.modes.unitModes.indexToTemperatureUnitMode
import com.hsact.sunplanner.ui.settings.modes.unitModes.indexToWindSpeedUnitMode
import com.hsact.sunplanner.ui.settings.modes.unitModes.toIndex

@Composable
fun SettingsDialog(
    viewModel: SettingsViewModel,
    onApplyTheme: (ThemeMode) -> Unit,
    onChangeLanguage: (LanguageMode) -> Unit,
    onDismiss: () -> Unit
) {
    AlertDialog(
        onDismissRequest = onDismiss,
        confirmButton = {
            TextButton(onClick = {
                if (viewModel.uiState.value.selectedLanguage != viewModel.uiState.value.currentLanguage) {
                    onChangeLanguage(viewModel.uiState.value.selectedLanguage)
                }
                viewModel.handleIntent(SettingsIntents.ApplySettings)
                onDismiss()
            }) {
                Text("OK")
            }
        },
        modifier = Modifier.fillMaxWidth(),
        dismissButton = {
            TextButton(onClick = onDismiss) {
                Text(stringResource(R.string.cancel))
            }
        },
        title = {
            Text(stringResource(R.string.settings))
        },
        text = {
            DialogContainer(viewModel, onApplyTheme)
        }
    )
}

@Composable
private fun DialogContainer(viewModel: SettingsViewModel, onApplyTheme: (ThemeMode) -> Unit) {
    val uiState by viewModel.uiState.collectAsState()
    var selectedThemeIndex by remember(uiState.currentTheme) { mutableIntStateOf(viewModel.uiState.value.currentTheme.toIndex()) }
    val themeChoices =
        LocalContext.current.resources.getStringArray(R.array.theme_choices).toList()
    var selectedLanguageIndex = remember(uiState.currentLanguage) {
        mutableIntStateOf(uiState.currentLanguage.toIndex())
    }
    val languageChoices =
        LocalContext.current.resources.getStringArray(R.array.language_choices).toList()
    val dotsChoices =
        LocalContext.current.resources.getStringArray(R.array.dots_display_choices).toList()
    var selectedDotsOption = remember(uiState.currentDotsOption) {
        mutableIntStateOf(uiState.currentDotsOption)
    }

    val tempUnitChoices =
        LocalContext.current.resources.getStringArray(R.array.temp_unit_choices).toList()
    var selectedTempUnitIndex = remember(uiState.currentTemperatureUnit) {
        mutableIntStateOf(uiState.currentTemperatureUnit.toIndex())
    }

    val windUnitChoices =
        LocalContext.current.resources.getStringArray(R.array.speed_unit_choices).toList()
    var selectedWindUnitIndex = remember(uiState.currentWindSpeedUnit) {
        mutableIntStateOf(uiState.currentWindSpeedUnit.toIndex())
    }

    val precipitationUnitChoices =
        LocalContext.current.resources.getStringArray(R.array.precipitation_unit_choices)
            .toList()
    var selectedPrecipitationUnitIndex = remember(uiState.currentPrecipitationUnit) {
        mutableIntStateOf(uiState.currentPrecipitationUnit.toIndex())
    }
    Column {
        Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(stringResource(R.string.theme))
            Spacer(modifier = Modifier.weight(1f))
            SingleChoiceSegmentedButtonRow(
                modifier = Modifier.padding(start = 20.dp),
            ) {
                themeChoices.forEachIndexed { index, label ->
                    SegmentedButton(
                        shape = SegmentedButtonDefaults.itemShape(
                            index = index,
                            count = themeChoices.size
                        ),
                        onClick = {
                            selectedThemeIndex = index
                            viewModel.handleIntent(
                                SettingsIntents.UpdateTheme(
                                    indexToThemeMode(
                                        index
                                    )
                                )
                            )
                            onApplyTheme(viewModel.uiState.value.selectedTheme)
                        },
                        selected = index == selectedThemeIndex,
                        label = { Text(label) }
                    )
                }
            }
        }
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(top = 15.dp)
                .align(Alignment.CenterHorizontally),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(stringResource(R.string.language))
            Spacer(modifier = Modifier.weight(1f))
            DropdownPicker(
                "",
                languageChoices,
                selected = languageChoices[selectedLanguageIndex.intValue],
                onSelected = {
                    selectedLanguageIndex.intValue = languageChoices.indexOf(it)
                    viewModel.handleIntent(
                        SettingsIntents.UpdateLanguage(
                            indexToLanguageMode(
                                selectedLanguageIndex.intValue
                            )
                        )
                    )
                },
                modifier = Modifier.padding(start = 20.dp)
            )
        }
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(top = 20.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(stringResource(R.string.show_graph_dots))
            Spacer(modifier = Modifier.weight(1f))
            SegmentedButtonUnitPicker(
                viewModel,
                dotsChoices,
                selectedDotsOption
            ) { index ->
                SettingsIntents.UpdateDotsOption(index)
            }
        }
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(top = 20.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(stringResource(R.string.temperature))
            Spacer(modifier = Modifier.weight(1f))
            SegmentedButtonUnitPicker(
                viewModel,
                tempUnitChoices,
                selectedTempUnitIndex
            ) { index ->
                SettingsIntents.UpdateTemperatureUnit(indexToTemperatureUnitMode(index))
            }
        }
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(top = 20.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(stringResource(R.string.wind))
            Spacer(modifier = Modifier.weight(1f))
            SegmentedButtonUnitPicker(
                viewModel,
                windUnitChoices,
                selectedWindUnitIndex
            ) { index ->
                SettingsIntents.UpdateWindSpeedUnit(indexToWindSpeedUnitMode(index))
            }
        }
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(top = 20.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(stringResource(R.string.precipitation))
            Spacer(modifier = Modifier.weight(1f))
            SegmentedButtonUnitPicker(
                viewModel,
                precipitationUnitChoices,
                selectedPrecipitationUnitIndex
            ) { index ->
                SettingsIntents.UpdatePrecipitationUnit(indexToPrecipitationUnitMode(index))
            }
        }
    }
}

@Composable
private fun SegmentedButtonUnitPicker(
    viewModel: SettingsViewModel,
    choices: List<String>,
    selectedIndex: MutableState<Int>,
    onIndexSelected: (Int) -> SettingsIntents
) {
    SingleChoiceSegmentedButtonRow(
        modifier = Modifier.padding(start = 20.dp),
    ) {
        choices.forEachIndexed { index, label ->
            SegmentedButton(
                shape = SegmentedButtonDefaults.itemShape(
                    index = index,
                    count = choices.size
                ),
                onClick = {
                    selectedIndex.value = index
                    viewModel.handleIntent(onIndexSelected(index))
                },
                selected = index == selectedIndex.value,
                label = {
                    Text(
                        text = label,
                        maxLines = 1,
                        overflow = TextOverflow.Clip,
                        style = MaterialTheme.typography.bodySmall
                    )
                }
            )
        }
    }
}