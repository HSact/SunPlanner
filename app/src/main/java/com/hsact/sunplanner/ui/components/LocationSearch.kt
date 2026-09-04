package com.hsact.sunplanner.ui.components

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.LocationOn
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.ListItem
import androidx.compose.material3.ListItemDefaults
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
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.focus.focusRequester
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.platform.LocalSoftwareKeyboardController
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
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

/**
 * A search component that allows users to look up and select locations.
 * 
 * Features:
 * - Real-time city search as the user types (starts after 2 characters).
 * - Full-screen mode when focused, showing a detailed list of results.
 * - Integration with the main application state for loading indicators.
 * - Support for additional actions (e.g., GPS, Bookmarks) via the [actions] slot.
 * - Automatic management of keyboard and focus.
 *
 * @param viewModel The [MainViewModel] used to fetch city lists and handle state.
 * @param query The current search text.
 * @param onQueryChange Callback invoked when the search text changes.
 * @param onCitySelected Callback invoked when a user selects a city from the list.
 * @param isSearchExpanded Whether the search bar is currently in full-screen expanded mode.
 * @param onSearchExpandedChange Callback to update the expansion state.
 * @param modifier Modifier for the search bar container.
 * @param placeholder Hint text shown when the search bar is empty.
 * @param actions Optional composable slot for icons/buttons inside the search bar's trailing area.
 */
@OptIn(ExperimentalMaterial3Api::class, FlowPreview::class)
@Composable
fun LocationSearch(
    viewModel: MainViewModel,
    query: String,
    onQueryChange: (String) -> Unit,
    onCitySelected: (Location) -> Unit,
    isSearchExpanded: Boolean,
    onSearchExpandedChange: (Boolean) -> Unit,
    modifier: Modifier = Modifier,
    placeholder: String = stringResource(R.string.search_hint),
    actions: @Composable (() -> Unit)? = null
) {
    val state by viewModel.mainUiState.collectAsState()
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

    Box(modifier = modifier.zIndex(1f)) {
        SearchBar(
            inputField = {
                SearchBarDefaults.InputField(
                    query = query,
                    onQueryChange = {
                        onQueryChange(it)
                        viewModel.handleIntent(MainScreenIntents.UpdateCityName(it))
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
                    placeholder = { Text(placeholder) },
                    leadingIcon = {
                        Icon(
                            imageVector = Icons.Default.Search,
                            contentDescription = null
                        )
                    },
                    trailingIcon = {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            if (state.isSearchingCities && isSearchExpanded) {
                                CircularProgressIndicator(
                                    modifier = Modifier.size(24.dp),
                                    strokeWidth = 2.dp,
                                    color = MaterialTheme.colorScheme.primary
                                )
                                Spacer(modifier = Modifier.width(8.dp))
                            }
                            if (query.isNotEmpty() && isSearchExpanded) {
                                IconButton(onClick = { onQueryChange("") }) {
                                    Icon(Icons.Default.Close, contentDescription = null)
                                }
                            }
                            if (!isSearchExpanded) {
                                actions?.invoke()
                            }
                        }
                    },
                    modifier = Modifier.focusRequester(focusRequester)
                )
            },
            expanded = isSearchExpanded,
            onExpandedChange = onSearchExpandedChange,
            modifier = Modifier.fillMaxWidth(),
            windowInsets = if (isSearchExpanded) SearchBarDefaults.windowInsets else WindowInsets(
                0,
                0,
                0,
                0
            )
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

    ListItem(
        headlineContent = {
            Text(
                text = city.name,
                style = MaterialTheme.typography.bodyLarge,
                fontWeight = FontWeight.Bold
            )
        },
        supportingContent = {
            Text(
                text = LocationUtils.buildCitySecondaryName(city),
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
        },
        leadingContent = {
            Icon(
                imageVector = Icons.Default.LocationOn,
                contentDescription = null,
                tint = MaterialTheme.colorScheme.primary,
                modifier = Modifier.size(20.dp)
            )
        },
        modifier = Modifier
            .fillMaxWidth()
            .clickable {
                keyboardController?.hide()
                focusManager.clearFocus()
                onSearchExpandedChange(false)
                onCityClick(city)
            },
        colors = ListItemDefaults.colors(containerColor = Color.Transparent)
    )
    HorizontalDivider(
        modifier = Modifier.padding(horizontal = 16.dp),
        thickness = 0.5.dp,
        color = MaterialTheme.colorScheme.outlineVariant
    )
}
