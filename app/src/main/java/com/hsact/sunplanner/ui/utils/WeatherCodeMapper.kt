package com.hsact.sunplanner.ui.utils

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AcUnit
import androidx.compose.material.icons.filled.CloudQueue
import androidx.compose.material.icons.filled.FilterDrama
import androidx.compose.material.icons.filled.Grain
import androidx.compose.material.icons.filled.QuestionMark
import androidx.compose.material.icons.filled.Thunderstorm
import androidx.compose.material.icons.filled.Umbrella
import androidx.compose.material.icons.filled.WbSunny
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.res.stringResource
import com.hsact.sunplanner.R

/**
 * Utility for mapping WMO weather codes to icons and localized descriptions.
 */
object WeatherCodeMapper {

    @Composable
    fun getIcon(code: Int): ImageVector {
        return when (code) {
            0 -> Icons.Default.WbSunny
            1, 2, 3 -> Icons.Default.CloudQueue
            45, 48 -> Icons.Default.FilterDrama // Foggy proxy
            51, 53, 55, 56, 57 -> Icons.Default.Grain // Drizzle
            61, 63, 65, 66, 67 -> Icons.Default.Umbrella // Rainy
            71, 73, 75, 77 -> Icons.Default.AcUnit // Snowy
            80, 81, 82 -> Icons.Default.Grain // Shower
            85, 86 -> Icons.Default.AcUnit
            95, 96, 99 -> Icons.Default.Thunderstorm
            else -> Icons.Default.QuestionMark
        }
    }

    @Composable
    fun getDescription(code: Int): String {
        return when (code) {
            0 -> stringResource(R.string.weather_clear)
            1, 2, 3 -> stringResource(R.string.weather_cloudy)
            45, 48 -> stringResource(R.string.weather_foggy)
            51, 53, 55 -> stringResource(R.string.weather_drizzle)
            61, 63, 65 -> stringResource(R.string.weather_rainy)
            71, 73, 75 -> stringResource(R.string.weather_snowy)
            95, 96, 99 -> stringResource(R.string.weather_thunder)
            else -> stringResource(R.string.weather_unknown)
        }
    }
}
