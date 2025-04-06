package it.fast4x.rigallery.feature_node.presentation.ignored

import android.os.Parcelable
import androidx.compose.runtime.Immutable
import it.fast4x.rigallery.feature_node.domain.model.IgnoredAlbum
import kotlinx.parcelize.Parcelize

@Immutable
@Parcelize
data class IgnoredState(
    val albums: List<IgnoredAlbum> = emptyList()
): Parcelable
