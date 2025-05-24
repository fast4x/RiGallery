package it.fast4x.rigallery.feature_node.presentation.statistics.data

data class MediaInYear(
    val year: Int,
    val value: Int = 0
)

data class MediaTypeInYear(
    val year: Int,
    val images: Long = 0,
    val videos: Long = 0
)

data class MediaType(
    val images: Long = 0,
    val videos: Long = 0
)

data class MediaTypes(
    val mimeType: String = "",
    val value: Long = 0
)