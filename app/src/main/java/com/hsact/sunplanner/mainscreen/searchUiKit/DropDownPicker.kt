package com.hsact.sunplanner.mainscreen.searchUiKit

import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExposedDropdownMenuBox
import androidx.compose.material3.ExposedDropdownMenuDefaults
import androidx.compose.material3.MenuAnchorType
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier

class DropDownPicker {
    @OptIn(ExperimentalMaterial3Api::class)
    @Composable
    fun <T> YearDropdown(
        label: String,
        list: List<T>,
        selectedYear: T,
        onYearSelected: (T) -> Unit,
        modifier: Modifier = Modifier
    ) {
        var expanded by remember { mutableStateOf(false) }
        ExposedDropdownMenuBox(
            modifier = modifier,
            expanded = expanded,
            onExpandedChange = { expanded = it }
        ) {
            OutlinedTextField(
                value = if (selectedYear!=0) selectedYear.toString() else "",
                onValueChange = {},
                readOnly = true,
                label = { Text(label) },
                trailingIcon = {
                    ExposedDropdownMenuDefaults.TrailingIcon(expanded = expanded)
                },
                modifier = modifier
                    .menuAnchor(MenuAnchorType.PrimaryNotEditable, enabled = true)
            )
            ExposedDropdownMenu(
                expanded = expanded,
                onDismissRequest = { expanded = false }
            ) {
                list.forEach { item ->
                    DropdownMenuItem(
                        text = { Text(item.toString()) },
                        onClick = {
                            onYearSelected(item)
                            expanded = false
                        }
                    )
                }
            }
        }
    }
}