package it.fast4x.rigallery.feature_node.presentation.util

import android.content.Context
import android.location.Address
import android.location.Geocoder
import android.os.Build
import androidx.annotation.RequiresApi
import androidx.compose.runtime.Composable
import androidx.compose.ui.platform.LocalContext
import androidx.core.text.isDigitsOnly
import java.io.IOException

@Composable
fun rememberGeocoder(): Geocoder? {
    val geocoder = Geocoder(LocalContext.current)
    return if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU && Geocoder.isPresent())
        geocoder else null
}

fun getGeocoder(context: Context): Geocoder? {
    val geocoder = Geocoder(context)
    return if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU && Geocoder.isPresent())
        geocoder else null
}

fun Geocoder.getLocation(lat: Double, long: Double, onLocationFound: (Address?) -> Unit) {
    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
        getFromLocation(
            /* latitude = */ lat, /* longitude = */ long, /* maxResults = */ 1
        ) {
            if (it.isEmpty()) onLocationFound(null)
            else onLocationFound(it.first())
        }
    } else {
        onLocationFound(null)
    }
}

fun Geocoder.getFromLocationCompat(
    latitude: Double,
    longitude: Double,
    maxResults: Int,
    processAddresses: (addresses: List<Address>) -> Unit,
    onError: (errorCode: String, errorMessage: String?, errorDetails: Any?) -> Unit,
) {
    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
        Compat33.geocoderGetFromLocation(this, latitude, longitude, maxResults, processAddresses, onError)
    } else {
        try {
            @Suppress("deprecation")
            val addresses = getFromLocation(latitude, longitude, maxResults) ?: ArrayList()
            processAddresses(addresses)
        } catch (e: IOException) {
            // `grpc failed`, etc.
            onError("getAddress-network", "failed to get address because of network issues", e.message)
        } catch (e: Exception) {
            onError("getAddress-exception", "failed to get address", e.message)
        }
    }
}

val Address.formattedAddress: String get() {
    val address = "${subLocality ?: locality}, $countryName".trim()
    return address
}

//val Address.formattedAddress: String get() {
//    var address = ""
//    if (!featureName.isNullOrBlank() && !featureName.isDigitsOnly()) address += featureName
//    else if (!subLocality.isNullOrBlank()) address += subLocality
//    if (!locality.isNullOrBlank()) {
//        address += if (address.isEmpty()) locality
//        else ", $locality"
//    }
//    if (!countryName.isNullOrBlank()) {
//        address += if (address.isEmpty()) countryName
//        else ", $countryName"
//    }
//
//    return address
//}

val Address.locationTag: String get() =
    if (!featureName.isNullOrBlank() && !featureName.isDigitsOnly()) featureName
    else if (!subLocality.isNullOrBlank()) subLocality
    else locality


@RequiresApi(Build.VERSION_CODES.TIRAMISU)
object Compat33 {
    fun geocoderGetFromLocation(
        geocoder: Geocoder,
        latitude: Double,
        longitude: Double,
        maxResults: Int,
        processAddresses: (addresses: List<Address>) -> Unit,
        onError: (errorCode: String, errorMessage: String?, errorDetails: Any?) -> Unit,
    ) {
        geocoder.getFromLocation(latitude, longitude, maxResults, object : Geocoder.GeocodeListener {
            override fun onGeocode(addresses: List<Address?>) = processAddresses(addresses.filterNotNull())

            override fun onError(errorMessage: String?) {
                onError("getAddress-asyncerror", "failed to get address", errorMessage)
            }
        })
    }
}