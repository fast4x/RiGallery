package it.fast4x.rigallery.core.enums

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.DateRange
import androidx.compose.material.icons.outlined.TextFields
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.res.stringResource
import it.fast4x.rigallery.R

enum class AlbumsSortOrder {
    Date,
    Name;

    val title: String
        @Composable
        get() = when(this) {
            Date -> stringResource(R.string.filter_type_date)
            Name -> stringResource(R.string.filter_type_name)
        }

    val icon: ImageVector
        get() = when (this) {
            Date -> Icons.Filled.DateRange
            Name -> Icons.Outlined.TextFields
        }

}