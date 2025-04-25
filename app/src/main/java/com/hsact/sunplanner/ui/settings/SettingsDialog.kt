package com.hsact.sunplanner.ui.settings

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.SegmentedButton
import androidx.compose.material3.SegmentedButtonDefaults
import androidx.compose.material3.SingleChoiceSegmentedButtonRow
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import com.hsact.sunplanner.R
import com.hsact.sunplanner.ui.DropDownPicker

class SettingsDialog (val viewModel: SettingsViewModel) {
    @Composable
    fun ShowDialog(
        onDismiss: () -> Unit
    ) {
        AlertDialog(
            onDismissRequest = onDismiss,
            confirmButton = {
                TextButton(onClick = {
                    viewModel.handleIntent(SettingsIntents.ApplySettings)
                    onDismiss()}) {
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
                DialogContainer(viewModel)
            }
        )
    }

    @Composable
    private fun DialogContainer(viewModel: SettingsViewModel) {
        var selectedThemeIndex by remember { mutableIntStateOf(0) }
        val themeChoices = LocalContext.current.resources.getStringArray(R.array.theme_choices).toList()
        var selectedLanguageIndex by remember { mutableIntStateOf(0) }
        val languageChoices = LocalContext.current.resources.getStringArray(R.array.language_choices).toList()
        Column {
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(stringResource(R.string.theme))
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
                                viewModel.handleIntent(SettingsIntents.UpdateTheme(indexToThemeMode(index)))
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
                    .align(Alignment.CenterHorizontally),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(stringResource(R.string.language))
                DropDownPicker().ItemsDropdown(
                    "",
                    languageChoices,
                    selected = languageChoices[selectedLanguageIndex],
                    onSelected = {
                        selectedLanguageIndex = languageChoices.indexOf(it)
                        viewModel.handleIntent(SettingsIntents.UpdateLanguage(indexToLanguageMode(selectedLanguageIndex)))
                    },
                    modifier = Modifier.padding(start = 20.dp)
                )
            }
        }
    }
}