package com.hsact.sunplanner.ui.utils

import androidx.annotation.ArrayRes
import androidx.compose.runtime.Composable
import androidx.compose.runtime.ReadOnlyComposable
import androidx.compose.ui.platform.LocalResources

/**
 * Returns a string array resource.
 *
 * @param id The resource identifier of the string array.
 * @return The string array associated with the resource.
 */
@Composable
@ReadOnlyComposable
fun stringArrayResource(@ArrayRes id: Int): Array<String> {
    return LocalResources.current.getStringArray(id)
}
