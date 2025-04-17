package com.hsact.sunplanner.ui.mainscreen.searchUiKit

import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.interaction.collectIsFocusedAsState
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.Card
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.SearchBar
import androidx.compose.material3.SearchBarDefaults
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.FocusManager
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.focus.focusRequester
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Shape
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.platform.LocalSoftwareKeyboardController
import androidx.compose.ui.unit.dp
import androidx.compose.ui.zIndex
import com.hsact.sunplanner.data.utils.LocationUtils
import com.hsact.sunplanner.data.responses.Location
import com.hsact.sunplanner.ui.mainscreen.MainViewModel

class SearchUI {
    private val minCityLetters = 2
    private lateinit var focusManager: FocusManager

    @OptIn(ExperimentalMaterial3Api::class)
    @Composable
    fun SearchCityBar(
        viewModel: MainViewModel,
        query: String,
        onQueryChange: (String) -> Unit,
        onCitySelected: (Location) -> Unit,
        isSearchExpanded: Boolean,
        onSearchExpandedChange: (Boolean) -> Unit
    ) {
        val interactionSource = remember { MutableInteractionSource() }
        val isFocused by interactionSource.collectIsFocusedAsState()
        val focusRequester = remember { FocusRequester() }
        focusManager = LocalFocusManager.current
        val searchBarShape: Shape = MaterialTheme.shapes.extraLarge

        LaunchedEffect(isFocused) {
            if (isSearchExpanded != isFocused) {
                onSearchExpandedChange(isFocused)
            }
        }
        Box(modifier = Modifier.zIndex(1f))
        {
            SearchBar(
                inputField = {
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                    ) {
                        TextField(
                            value = query,
                            onValueChange = {
                                onQueryChange(it)
                                onSearchExpandedChange(it.isNotEmpty() || isFocused)
                                if (it.length >= minCityLetters) {
                                    viewModel.fetchCityList(it)
                                }
                            },
                            modifier = Modifier
                                .fillMaxWidth()
                                .focusRequester(focusRequester),
                            shape = searchBarShape,
                            placeholder = { Text("City/Town") },
                            leadingIcon = {
                                Icon(
                                    imageVector = Icons.Default.Search,
                                    contentDescription = "Search"
                                )
                            },
                            colors = TextFieldDefaults.colors(
                                unfocusedIndicatorColor = Color.Transparent,
                                focusedIndicatorColor = Color.Transparent
                            ),
                            interactionSource = interactionSource
                        )
                    }
                },
                expanded = isSearchExpanded,
                onExpandedChange = onSearchExpandedChange,
                modifier = Modifier,
                shape = searchBarShape,
                colors = SearchBarDefaults.colors(),
                //colors = SearchBarDefaults.colors(containerColor = Color.Transparent),
                //tonalElevation = 0.dp,
                //shadowElevation = 0.dp
            ) {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                ) {
                    Column {
                        CityList(viewModel, onCitySelected, onSearchExpandedChange)
                    }
                }
            }
        }
    }

    @Composable
    private fun CityList(
        viewModel: MainViewModel, onCitySelected: (Location) -> Unit,
        onSearchExpandedChange: (Boolean) -> Unit
    ) {
        val searchDataUI by viewModel.searchDataUI.collectAsState()
        if (searchDataUI.cities.isNotEmpty()) {
            LazyColumn(
                modifier = Modifier
                    .fillMaxWidth()
            ) {
                items(searchDataUI.cities) { city ->
                    CityCard(city, onCityClick = onCitySelected, onSearchExpandedChange)
                }
            }
        } else if (searchDataUI.cityName.length >= minCityLetters) {
            Text("No cities available.", modifier = Modifier.padding(8.dp))
        } else {
            Text("Enter at least a two letters of a city name", modifier = Modifier.padding(8.dp))
        }
    }

    @Composable
    private fun CityCard(
        city: Location, onCityClick: (Location) -> Unit,
        onSearchExpandedChange: (Boolean) -> Unit
    ) {
        val keyboardController = LocalSoftwareKeyboardController.current
        Card(
            modifier = Modifier
                .fillMaxWidth()
                .padding(start = 5.dp, end = 5.dp, bottom = 5.dp)
                .clickable {
                    keyboardController?.hide()
                    focusManager.clearFocus(force = true)
                    onSearchExpandedChange(false)
                    onCityClick(city)
                }
        ) {
            Text(
                LocationUtils.buildCityFullName(city),
                modifier = Modifier.padding(8.dp)
            )
        }
    }
}