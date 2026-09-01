package com.hsact.sunplanner.ui.components

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
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
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.focus.focusRequester
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.platform.LocalSoftwareKeyboardController
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.compose.ui.zIndex
import com.hsact.sunplanner.R
import com.hsact.sunplanner.data.responses.Location
import com.hsact.sunplanner.data.utils.LocationUtils
import com.hsact.sunplanner.ui.mainscreen.MainScreenIntents
import com.hsact.sunplanner.ui.mainscreen.MainViewModel
import kotlinx.coroutines.FlowPreview

@OptIn(FlowPreview::class)
private val minCityLetters = 2

@OptIn(ExperimentalMaterial3Api::class, FlowPreview::class)
@Composable
fun LocationSearch(
    viewModel: MainViewModel,
    query: String,
    onQueryChange: (String) -> Unit,
    onCitySelected: (Location) -> Unit,
    isSearchExpanded: Boolean,
    onSearchExpandedChange: (Boolean) -> Unit
) {
    val queryOrigin = remember { mutableStateOf(query) }
    val keyboardController = LocalSoftwareKeyboardController.current
    val focusRequester = remember { FocusRequester() }
    val focusManager = LocalFocusManager.current

    LaunchedEffect(isSearchExpanded) {
        if (isSearchExpanded) {
            queryOrigin.value = query
            onQueryChange("")
        } else {
            if (query.isBlank()) {
                onQueryChange(queryOrigin.value)
            }
            focusManager.clearFocus()
        }
    }

    Box(modifier = Modifier.zIndex(1f)) {
        SearchBar(
            inputField = {
                SearchBarDefaults.InputField(
                    query = query,
                    onQueryChange = {
                        onQueryChange(it)
                        if (it.length >= minCityLetters) {
                            viewModel.handleIntent(MainScreenIntents.FetchCityList(it))
                        }
                    },
                    onSearch = {
                        keyboardController?.hide()
                        focusManager.clearFocus()
                    },
                    expanded = isSearchExpanded,
                    onExpandedChange = onSearchExpandedChange,
                    placeholder = { Text(stringResource(R.string.search_hint)) },
                    leadingIcon = {
                        Icon(
                            imageVector = Icons.Default.Search,
                            contentDescription = null
                        )
                    },
                    modifier = Modifier.focusRequester(focusRequester)
                )
            },
            expanded = isSearchExpanded,
            onExpandedChange = onSearchExpandedChange,
            modifier = Modifier.fillMaxWidth()
        ) {
            CityList(viewModel, onCitySelected, onSearchExpandedChange)
        }
    }
}

@OptIn(FlowPreview::class)
@Composable
private fun CityList(
    viewModel: MainViewModel, 
    onCitySelected: (Location) -> Unit,
    onSearchExpandedChange: (Boolean) -> Unit
) {
    val searchDataUI by viewModel.mainUiState.collectAsState()
    if (searchDataUI.cities.isNotEmpty()) {
        LazyColumn(
            modifier = Modifier.fillMaxWidth()
        ) {
            items(searchDataUI.cities) { city ->
                CityCard(city, onCitySelected, onSearchExpandedChange)
            }
        }
    } else if (searchDataUI.cityName.length >= minCityLetters) {
        Text(stringResource(R.string.no_cities), modifier = Modifier.padding(16.dp))
    } else {
        Text(stringResource(R.string.enter_city_hint), modifier = Modifier.padding(16.dp))
    }
}

@Composable
private fun CityCard(
    city: Location, 
    onCityClick: (Location) -> Unit,
    onSearchExpandedChange: (Boolean) -> Unit
) {
    val keyboardController = LocalSoftwareKeyboardController.current
    val focusManager = LocalFocusManager.current
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp, vertical = 4.dp)
            .clickable {
                keyboardController?.hide()
                focusManager.clearFocus()
                onSearchExpandedChange(false)
                onCityClick(city)
            }
    ) {
        Text(
            text = LocationUtils.buildCityFullName(city),
            modifier = Modifier.padding(16.dp),
            style = MaterialTheme.typography.bodyLarge
        )
    }
}
