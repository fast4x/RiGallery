package it.fast4x.rigallery.feature_node.domain.model.editor;

import android.os.Parcelable
import androidx.annotation.Keep
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.Adjust
import androidx.compose.material.icons.outlined.Crop
import androidx.compose.material.icons.outlined.Draw
import androidx.compose.material.icons.outlined.Filter
import androidx.compose.material.icons.outlined.Transform
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.res.stringResource
import it.fast4x.rigallery.R
import kotlinx.parcelize.IgnoredOnParcel
import kotlinx.parcelize.Parcelize
import kotlinx.serialization.Serializable

@Keep
@Serializable
@Parcelize
enum class EditorItems : Parcelable {
    Transform,
    Adjust,
    Filters,
    Markup;

    @get:Composable
    val translatedName : String
        get() = when (this) {
            Transform -> stringResource(R.string.transform)
            Adjust -> stringResource(R.string.adjust)
            Filters -> stringResource(R.string.filters)
            Markup -> stringResource(R.string.markup)
        }

    @IgnoredOnParcel
    val icon: ImageVector
        get() = when (this) {
            Transform -> Icons.Outlined.Transform
            Adjust -> Icons.Outlined.Adjust
            Filters -> Icons.Outlined.Filter
            Markup -> Icons.Outlined.Draw
        }
}