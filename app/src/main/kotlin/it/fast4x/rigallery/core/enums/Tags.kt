package it.fast4x.rigallery.core.enums

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.filled.Image
import androidx.compose.material.icons.filled.LocationOff
import androidx.compose.material.icons.filled.LocationOn
import androidx.compose.material.icons.filled.PanoramaHorizontal
import androidx.compose.material.icons.filled.PanoramaVertical
import androidx.compose.material.icons.filled.Rotate90DegreesCw
import androidx.compose.material.icons.filled.VerticalAlignBottom
import androidx.compose.material.icons.filled.VideoFile
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.res.stringResource
import it.fast4x.rigallery.R
import it.fast4x.rigallery.core.enums.TagsType.Horizontal
import it.fast4x.rigallery.core.enums.TagsType.Rotated180
import it.fast4x.rigallery.core.enums.TagsType.Rotated270
import it.fast4x.rigallery.core.enums.TagsType.Rotated90
import it.fast4x.rigallery.core.enums.TagsType.Vertical
import it.fast4x.rigallery.core.enums.TagsType.Video

enum class TagsType {
    Favorite,
    Image,
    Video,
    Rotated90,
    Rotated180,
    Rotated270,
    Vertical,
    Horizontal;

    val color: Color
        get() = when (this) {
            Favorite -> Color.Red
            Image -> Color.Blue
            Video -> Color.Green
            Rotated90 -> Color.Yellow
            Rotated180 -> Color.Cyan
            Rotated270 -> Color.Magenta
            Vertical -> Color.Black
            Horizontal -> Color.Gray
        }

    val tag: String
        @Composable
        get() = when (this) {
            Favorite -> "#${stringResource(R.string.tag_favorite)}"
            Image -> "#${stringResource(R.string.tag_image)}"
            Video -> "#${stringResource(R.string.tag_video)}"
            Rotated90 -> "#${stringResource(R.string.tag_rotated90)}"
            Rotated180 -> "#${stringResource(R.string.tag_rotated180)}"
            Rotated270 -> "#${stringResource(R.string.tag_rotated270)}"
            Vertical -> "#${stringResource(R.string.tag_vertical)}"
            Horizontal -> "#${stringResource(R.string.tag_horizontal)}"
        }

    val icon: ImageVector
        get() = when (this) {
            Favorite -> Icons.Filled.Favorite
            Image -> Icons.Filled.Image
            Video -> Icons.Filled.VideoFile
            Rotated90 -> Icons.Filled.Rotate90DegreesCw
            Rotated180 -> Icons.Filled.Rotate90DegreesCw
            Rotated270 -> Icons.Filled.Rotate90DegreesCw
            Vertical -> Icons.Filled.PanoramaVertical
            Horizontal -> Icons.Filled.PanoramaHorizontal
        }
}

enum class MetadataTagsType {
    WithLocation,
    WithoutLocation;

    val color: Color
        get() = when (this) {
            WithLocation -> Color.DarkGray
            WithoutLocation -> Color.LightGray
        }

    val tag: String
        @Composable
        get() = when (this) {
            WithLocation -> "#${stringResource(R.string.tag_withlocation)}"
            WithoutLocation -> "#${stringResource(R.string.tag_withoutlocation)}"
        }

    val icon: ImageVector
        get() = when (this) {
            WithLocation -> Icons.Filled.LocationOn
            WithoutLocation -> Icons.Filled.LocationOff
        }
}