package com.hsact.sunplanner.ui.utils

import android.annotation.SuppressLint
import android.content.Context
import android.location.Geocoder
import android.util.Log
import com.google.android.gms.location.LocationServices
import com.google.android.gms.location.Priority
import com.hsact.sunplanner.data.responses.Location
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.tasks.await
import java.util.Locale
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class AppLocationManager @Inject constructor(
    @ApplicationContext private val context: Context
) {
    private val fusedLocationClient = LocationServices.getFusedLocationProviderClient(context)

    @SuppressLint("MissingPermission")
    suspend fun getCurrentLocation(): Location? {
        return try {
            val location = fusedLocationClient.getCurrentLocation(
                Priority.PRIORITY_BALANCED_POWER_ACCURACY,
                null
            ).await() ?: return null

            val geocoder = Geocoder(context, Locale.getDefault())

            @Suppress("DEPRECATION")
            val addresses = geocoder.getFromLocation(location.latitude, location.longitude, 1)

            val city = addresses?.firstOrNull()?.locality ?: "Unknown City"
            val country = addresses?.firstOrNull()?.countryName ?: ""

            Location(
                name = city,
                latitude = location.latitude,
                longitude = location.longitude,
                country = country,
                id = 0 // Dummy ID
            )
        } catch (e: Exception) {
            Log.e("AppLocationManager", "Error getting current location", e)
            null
        }
    }
}
