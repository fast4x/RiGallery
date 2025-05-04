package it.fast4x.rigallery.feature_node.domain.model

import android.content.Context
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.Stable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.core.net.toUri
import androidx.exifinterface.media.ExifInterface
import dagger.hilt.android.qualifiers.ApplicationContext
import it.fast4x.rigallery.feature_node.presentation.util.ExifMetadata
import it.fast4x.rigallery.feature_node.presentation.util.formattedAddress
import it.fast4x.rigallery.feature_node.presentation.util.getExifInterface
import it.fast4x.rigallery.feature_node.presentation.util.getFromLocationCompat
import it.fast4x.rigallery.feature_node.presentation.util.getGeocoder
import it.fast4x.rigallery.feature_node.presentation.util.getLocation
import it.fast4x.rigallery.feature_node.presentation.util.printWarning
import it.fast4x.rigallery.feature_node.presentation.util.rememberGeocoder
import it.fast4x.rigallery.feature_node.presentation.util.uriToPath
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.runBlocking
import kotlinx.coroutines.withContext
import java.io.IOException

@Stable
data class LocationData(
    val latitude: Double,
    val longitude: Double,
    val location: String
)

@Composable
fun rememberLocationData(
    exifMetadata: ExifMetadata?,
    media: Media
): LocationData? {
    val geocoder = rememberGeocoder()
    var locationName by remember { mutableStateOf(exifMetadata?.formattedCords) }
    LaunchedEffect(geocoder, exifMetadata) {
        withContext(Dispatchers.IO) {
            if (exifMetadata?.gpsLatLong != null) {
                geocoder?.getLocation(
                    exifMetadata.gpsLatLong[0],
                    exifMetadata.gpsLatLong[1]
                ) { address ->
                    address?.let {
                        val addressName = it.formattedAddress
                        if (addressName.isNotEmpty()) {
                            locationName = addressName
                        }
                    }
                }
            }
        }
    }
    return remember(media, exifMetadata, locationName) {
        exifMetadata?.let {
            it.gpsLatLong?.let { latLong ->
                LocationData(
                    latitude = latLong[0],
                    longitude = latLong[1],
                    location = locationName ?: "Unknown"
                )
            }
        }
    }
}

fun getLocationData(
    @ApplicationContext context: Context,
    media: Media,
    onLocationFound: (LocationData?) -> Unit
) {
    val geoCoder = getGeocoder(context)
    val exifMetadata = ExifMetadata(ExifInterface(media.path))

    println("getLocationData: exifMetadata.formattedCords: ${exifMetadata.formattedCords}")

    if (exifMetadata.gpsLatLong != null) {
        geoCoder?.getFromLocationCompat(
            exifMetadata.gpsLatLong[0],
            exifMetadata.gpsLatLong[1],
            1,
            {
                onLocationFound(
                    LocationData(
                        latitude = exifMetadata.gpsLatLong[0],
                        longitude = exifMetadata.gpsLatLong[1],
                        location = it.firstOrNull()?.formattedAddress.toString()
                    )
                )
            }
        ) { errorCode, errorMessage, errorDetails ->
            printWarning("getLocationData: Error: $errorCode, $errorMessage, $errorDetails")

        }
    } else {
        onLocationFound(null)
    }

//    geoCoder?.getLocation(
//        exifMetadata.gpsLatLong[0],
//        exifMetadata.gpsLatLong[1]
//    ) { address ->
//        //println("getLocationData: address: $address")
//
//        println("getLocationData: addressName: ${address?.formattedAddress}")
//
//        locationData = LocationData(
//            latitude = exifMetadata.gpsLatLong[0],
//            longitude = exifMetadata.gpsLatLong[1],
//            location = address?.formattedAddress.toString()
//        )
//    }

//    println("getLocationData: locationData: $locationData")
//
    //return locationData
}