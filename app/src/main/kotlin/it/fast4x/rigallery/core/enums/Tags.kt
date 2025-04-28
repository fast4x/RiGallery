package it.fast4x.rigallery.core.enums

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.DoneAll
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.filled.Image
import androidx.compose.material.icons.filled.VideoFile
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import it.fast4x.rigallery.core.enums.MediaType.All
import it.fast4x.rigallery.core.enums.MediaType.Images

enum class TagsType {
    Favorite,
    Image,
    Video;

    val color: Color
        get() = when (this) {
            Favorite -> Color.Red
            Image -> Color.Blue
            Video -> Color.Green
        }

    val tag: String
        get() = when (this) {
            //NO TRANSLATE
            Favorite -> "#favorite"
            Image -> "#image"
            Video -> "#video"
        }

    val icon: ImageVector
        get() = when (this) {
            Favorite -> Icons.Filled.Favorite
            Image -> Icons.Filled.Image
            Video -> Icons.Filled.VideoFile
        }
}