package it.fast4x.rigallery.feature_node.domain.model

import androidx.compose.runtime.Composable
import androidx.compose.runtime.Stable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.res.stringResource
import it.fast4x.rigallery.R
import it.fast4x.rigallery.core.Settings.Misc.rememberExifDateFormat
import it.fast4x.rigallery.feature_node.presentation.util.ExifMetadata
import it.fast4x.rigallery.feature_node.presentation.util.getDate

@Stable
data class MediaDateCaption(
    val date: String,
    val deviceInfo: String? = null,
    val description: String
)

@Composable
fun rememberMediaDateCaption(
    exifMetadata: ExifMetadata?,
    media: Media
): MediaDateCaption {
    val deviceInfo = remember(exifMetadata) { exifMetadata?.lensDescription }
    val defaultDesc = stringResource(R.string.image_add_description)
    val description = remember(exifMetadata) { exifMetadata?.imageDescription ?: defaultDesc }
    val currentDateFormat by rememberExifDateFormat()
    return remember(media, currentDateFormat) {
        MediaDateCaption(
            date = media.definedTimestamp.getDate(currentDateFormat),
            deviceInfo = deviceInfo,
            description = description
        )
    }
}

@Composable
fun rememberMediaDateCaption(
    exifMetadata: ExifMetadata?,
    media: Media.UriMedia
): MediaDateCaption {
    val deviceInfo = remember(exifMetadata) { exifMetadata?.lensDescription }
    val defaultDesc = stringResource(R.string.image_add_description)
    val description = remember(exifMetadata) { exifMetadata?.imageDescription ?: defaultDesc }
    val currentDateFormat by rememberExifDateFormat()
    return remember(media, currentDateFormat) {
        MediaDateCaption(
            date = media.definedTimestamp.getDate(currentDateFormat),
            deviceInfo = deviceInfo,
            description = description
        )
    }
}