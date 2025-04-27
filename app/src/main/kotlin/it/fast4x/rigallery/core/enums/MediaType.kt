package it.fast4x.rigallery.core.enums

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Audiotrack
import androidx.compose.material.icons.filled.DoneAll
import androidx.compose.material.icons.filled.Image
import androidx.compose.material.icons.filled.VideoFile
import androidx.compose.material3.Icon
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
            Images -> Icons.Filled.Image
            Video -> Icons.Filled.VideoFile
            All -> Icons.Filled.DoneAll
        }

}