package it.fast4x.rigallery.feature_node.presentation.ignored.setup

import it.fast4x.rigallery.feature_node.domain.model.Album

sealed class IgnoredType {

    data class SELECTION(val selectedAlbum: Album?) : IgnoredType()

    data class REGEX(val regex: String) : IgnoredType()
}