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
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import com.hsact.sunplanner.R
import com.hsact.sunplanner.ui.mainscreen.searchUiKit.DropDownPicker

class SettingsDialog (val viewModel: SettingsViewModel) {
    @Composable
    fun ShowDialog(
        onDismiss: () -> Unit
    ) {
        AlertDialog(
            onDismissRequest = onDismiss,
            confirmButton = {
                TextButton(onClick = onDismiss) {
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
        Column {
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text("Theme")
                var selectedIndex by remember { mutableIntStateOf(0) }
                val options = listOf("Auto", "Day", "Night")

                SingleChoiceSegmentedButtonRow(
                    modifier = Modifier.padding(start = 20.dp),
                ) {
                    options.forEachIndexed { index, label ->
                        SegmentedButton(
                            shape = SegmentedButtonDefaults.itemShape(
                                index = index,
                                count = options.size
                            ),
                            onClick = { selectedIndex = index },
                            selected = index == selectedIndex,
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
                Text("Language")
                DropDownPicker().ItemsDropdown(
                    "",
                    listOf<String>("English", "Russian"),
                    selected = "English",
                    onSelected = {},
                    modifier = Modifier.padding(start = 20.dp)
                )
            }
        }
    }
}