package com.hsact.sunplanner.ui.utils

import android.annotation.SuppressLint
import android.content.Context
import android.location.Geocoder
import android.util.Log
import com.google.android.gms.location.LocationServices
import com.google.android.gms.location.Priority
import com.google.android.gms.tasks.CancellationTokenSource
import com.hsact.sunplanner.data.responses.Location
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.tasks.await
import kotlinx.coroutines.withContext
import kotlinx.coroutines.withTimeoutOrNull
import kotlin.time.Duration.Companion.seconds
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
        val cts = CancellationTokenSource()
        Log.d("AppLocationManager", "getCurrentLocation: Started")
        return try {
            withTimeoutOrNull(20.seconds) {
                Log.d("AppLocationManager", "getCurrentLocation: Requesting coordinates from FusedLocationClient...")
                val location = fusedLocationClient.getCurrentLocation(
                    Priority.PRIORITY_HIGH_ACCURACY, // Back to High Accuracy for testing
                    cts.token
                ).await()

                if (location == null) {
                    Log.w("AppLocationManager", "getCurrentLocation: FusedLocationClient returned null")
                    return@withTimeoutOrNull null
                }

                Log.d("AppLocationManager", "getCurrentLocation: Got coordinates: ${location.latitude}, ${location.longitude}")

                val cityResult = withContext(Dispatchers.IO) {
                    try {
                        Log.d("AppLocationManager", "getCurrentLocation: Starting reverse geocoding on IO thread...")
                        val geocoder = Geocoder(context, Locale.getDefault())
                        @Suppress("DEPRECATION")
                        val addresses = geocoder.getFromLocation(location.latitude, location.longitude, 1)

                        if (addresses.isNullOrEmpty()) {
                            Log.w("AppLocationManager", "getCurrentLocation: Geocoder returned no addresses")
                            null
                        } else {
                            val first = addresses[0]
                            val city = first.locality ?: first.subAdminArea ?: first.adminArea ?: "Unknown City"
                            val country = first.countryName ?: ""
                            Log.d("AppLocationManager", "getCurrentLocation: Geocoding successful: $city, $country")
                            Location(
                                name = city,
                                latitude = location.latitude,
                                longitude = location.longitude,
                                country = country,
                                id = 0
                            )
                        }
                    } catch (e: Exception) {
                        Log.e("AppLocationManager", "getCurrentLocation: Geocoding failed", e)
                        null
                    }
                }
                
                // Fallback to coordinate-based name if geocoding fails but we have coordinates
                cityResult ?: Location(
                    name = String.format(Locale.US, "%.4f, %.4f", location.latitude, location.longitude),
                    latitude = location.latitude,
                    longitude = location.longitude,
                    id = 0
                )
            }
        } catch (e: Exception) {
            Log.e("AppLocationManager", "getCurrentLocation: Global exception", e)
            null
        } finally {
            cts.cancel()
            Log.d("AppLocationManager", "getCurrentLocation: Finished")
        }
    }
}
