package it.fast4x.rigallery.core.enums

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Audiotrack
import androidx.compose.material.icons.filled.DoneAll
import androidx.compose.material.icons.filled.Image
import androidx.compose.material.icons.filled.VideoFile
import androidx.compose.ui.graphics.vector.ImageVector
import it.fast4x.rigallery.core.enums.MediaType.Video

enum class MediaType2 {
    Music,
    Video,
    All;

    val title: String
        get() = when(this) {
            Music -> "Music"
            Video -> "Video"
            All -> "All"
        }

    val icon: ImageVector
        get() = when (this) {
            Music-> Icons.Filled.Audiotrack
            Video -> Icons.Filled.VideoFile
            All -> Icons.Filled.DoneAll
        }

}