package it.fast4x.rigallery.core.enums

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.DoneAll
import androidx.compose.material.icons.outlined.Image
import androidx.compose.material.icons.outlined.VideoFile
import androidx.compose.ui.graphics.vector.ImageVector

enum class MediaType {
    Images,
    Video,
    All;

    val title: String
        get() = when(this) {
            Images -> "Images"
            Video -> "Video"
            All -> "All"
        }

    val icon: ImageVector
        get() = when (this) {
            Images -> Icons.Outlined.Image
            Video -> Icons.Outlined.VideoFile
            All -> Icons.Outlined.DoneAll
        }

}