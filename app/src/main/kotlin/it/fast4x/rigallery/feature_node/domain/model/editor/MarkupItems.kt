package it.fast4x.rigallery.feature_node.domain.model.editor

import android.os.Parcelable
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.res.stringResource
import it.fast4x.rigallery.R
import it.fast4x.rigallery.ui.core.icons.InkHighlighter
import it.fast4x.rigallery.ui.core.icons.InkMarker
import it.fast4x.rigallery.ui.core.icons.Ink_Eraser
import it.fast4x.rigallery.ui.core.icons.Stylus
import kotlinx.parcelize.IgnoredOnParcel
import kotlinx.parcelize.Parcelize
import kotlinx.serialization.Serializable
import it.fast4x.rigallery.ui.core.Icons as DotIcons

@Serializable
@Parcelize
enum class MarkupItems : Parcelable {
    Stylus,
    Highlighter,
    Marker,
    Eraser;

    @get:Composable
    val translatedName get() = when (this) {
        Stylus -> stringResource(R.string.type_stylus)
        Highlighter -> stringResource(R.string.type_highlighter)
        Marker -> stringResource(R.string.type_marker)
        Eraser -> stringResource(R.string.type_erase)
    }

    @IgnoredOnParcel
    val icon: ImageVector
        get() = when (this) {
            Stylus -> DotIcons.Stylus
            Highlighter -> DotIcons.InkHighlighter
            Marker -> DotIcons.InkMarker
            Eraser -> DotIcons.Ink_Eraser
        }
}