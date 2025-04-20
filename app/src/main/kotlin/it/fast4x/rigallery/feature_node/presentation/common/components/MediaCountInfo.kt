package it.fast4x.rigallery.feature_node.presentation.common.components

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.Image
import androidx.compose.material.icons.outlined.VideoFile
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.State
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import it.fast4x.rigallery.feature_node.domain.model.Media
import it.fast4x.rigallery.feature_node.domain.model.MediaState

@Composable
fun <T: Media> MediaCountInfo(
    mediaState: State<MediaState<T>>,
) {
    Row(
        horizontalArrangement = Arrangement.spacedBy(5.dp),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        Icon(
            imageVector = Icons.Outlined.Image,
            contentDescription = null
        )
        Text(text = mediaState.value.imagesCount().toString(),
            fontStyle = MaterialTheme.typography.labelSmall.fontStyle)

        Icon(
            imageVector = Icons.Outlined.VideoFile,
            contentDescription = null
        )
        Text(text = mediaState.value.videosCount().toString(),
            fontStyle = MaterialTheme.typography.labelSmall.fontStyle)
    }
}