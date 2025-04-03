package com.hsact.sunplanner.mainscreen

import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.interaction.collectIsFocusedAsState
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.Button
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.SearchBar
import androidx.compose.material3.SearchBarDefaults
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Shape
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.hsact.sunplanner.ui.theme.SunPlannerTheme

class MainScreenUI {
    @Composable
    fun MainScreen(modifier: Modifier = Modifier, viewModel: MainViewModel) {
        var cityName by remember { mutableStateOf("") }
        var years by remember { mutableIntStateOf(1) }
        var startYear by remember { mutableStateOf("") }
        var endYear by remember { mutableStateOf("") }
        var startDate by remember { mutableStateOf("") }
        var endDate by remember { mutableStateOf("") }
        val searchDataUI by viewModel.searchDataUI.collectAsState()

        //viewModel.fetchWeatherByCity(cityName, startDate, endDate)
        //viewModel.fetchCityList(cityName)

        Column(
            modifier = modifier
                .fillMaxSize()
        ) {
            Row {
                SearchCityBar(viewModel)
            }
            Row(
                modifier = Modifier
                    .padding(top = 10.dp)
            ) {
                OutlinedTextField(
                    modifier = Modifier
                        .weight(1f),
                    value = startYear,
                    onValueChange = { startYear = it },
                    label = { Text("Start date") }
                )
                OutlinedTextField(
                    modifier = Modifier
                        .weight(1f)
                        .padding(start = 10.dp),
                    value = endYear,
                    onValueChange = { endYear = it },
                    label = { Text("End date") }
                )
            }
            Row(
                modifier = Modifier
                    .padding(top = 10.dp)
            ) {
                OutlinedTextField(
                    modifier = Modifier
                        .weight(1f),
                    value = startDate,
                    onValueChange = { startDate = it },
                    label = { Text("Start date") }
                )
                OutlinedTextField(
                    modifier = Modifier
                        .weight(1f)
                        .padding(start = 10.dp),
                    value = endDate,
                    onValueChange = { endDate = it },
                    label = { Text("End date") }
                )
            }
            Row(
                modifier = Modifier
                    .padding(top = 10.dp)
            ) {
                Button(
                    onClick = {},
                    modifier = Modifier
                        .weight(1f)
                ) {
                    Text("Search")
                }
            }
            Row(
                modifier = Modifier
                    .fillMaxWidth()
            )
            {
                CityList(viewModel)
            }
        }
    }

    @OptIn(ExperimentalMaterial3Api::class)
    @Composable
    fun SearchCityBar(viewModel: MainViewModel) {
        var isSearchExpanded by remember { mutableStateOf(false) }
        var query by remember { mutableStateOf("") }
        val interactionSource = remember { MutableInteractionSource() }
        val isFocused by interactionSource.collectIsFocusedAsState()
        val searchBarShape: Shape = MaterialTheme.shapes.extraLarge

        LaunchedEffect(isFocused) {
            isSearchExpanded = isFocused
        }

        SearchBar(
            inputField = {
                TextField(
                    value = query,
                    onValueChange = { query = it
                        isSearchExpanded = query.isNotEmpty() || isFocused
                        if (query.length > 2)
                        {
                            viewModel.fetchCityList(query)
                        }
                    },
                    modifier = Modifier.fillMaxWidth(),
                    shape = searchBarShape,
                    placeholder = { Text("City/Town") },
                    leadingIcon = { Icon(imageVector = Icons.Default.Search,
                        contentDescription = "Search")
                    },
                    colors = TextFieldDefaults.colors(
                        unfocusedIndicatorColor = Color.Transparent,
                        focusedIndicatorColor = Color.Transparent),
                    interactionSource = interactionSource
                )
            },
            expanded = isSearchExpanded,
            onExpandedChange = { isSearchExpanded = it },
            modifier = Modifier,
            shape = searchBarShape,
            colors = SearchBarDefaults.colors(),
            /*tonalElevation = 6.dp,
            shadowElevation = 4.dp*/
        ) {
            Column {
                CityList(viewModel)
            }
        }
    }

    @Composable
    fun CityList(viewModel: MainViewModel) {
        val searchDataUI by viewModel.searchDataUI.collectAsState()
        if (searchDataUI.cities.isNotEmpty()) {
            LazyColumn {
                items(searchDataUI.cities.size) { index ->
                    Text(text = (searchDataUI.cities[index].name + ", "
                            + searchDataUI.cities[index].country), modifier = Modifier.padding(8.dp))
                }
            }
        }
        else {
            Text("No cities available.", modifier = Modifier.padding(8.dp))
        }
    }

    /*@Composable
    private fun RowScope.YearsPicker(years: Int, onYearsChange: (Int) -> Unit) {
        Row(
            modifier = Modifier
                .weight(0.3f)
                .padding(start = 10.dp)

                .padding(horizontal = 4.dp, vertical = 8.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            IconButton(
                onClick = { if (years > 1) onYearsChange(years - 1) },
                enabled = years > 1
            ) {
                Icon(Icons.Default.KeyboardArrowDown, contentDescription = "Decrease")
            }
            Text(text = years.toString(), fontSize = 18.sp)
            IconButton(
                onClick = { if (years < 10) onYearsChange(years + 1) },
                enabled = years < 10
            ) {
                Icon(Icons.Default.KeyboardArrowUp, contentDescription = "Increase")
            }
        }
    }*/

    @Preview(showBackground = true)
    @Composable
    fun MainScreenPreview() {
        SunPlannerTheme {
            MainScreen(viewModel = MainViewModel())
        }
    }
}