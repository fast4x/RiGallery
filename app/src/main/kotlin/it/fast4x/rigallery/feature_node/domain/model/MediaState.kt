package it.fast4x.rigallery.feature_node.domain.model

import androidx.compose.runtime.Stable
import it.fast4x.rigallery.feature_node.domain.util.isImage
import it.fast4x.rigallery.feature_node.domain.util.isMusic
import it.fast4x.rigallery.feature_node.domain.util.isVideo

@Stable
data class MediaState<Type: Media>(
    val media: List<Type> = emptyList(),
    val mappedMedia: List<MediaItem<Type>> = emptyList(),
    val mappedMediaWithMonthly: List<MediaItem<Type>> = emptyList(),
    val headers: List<MediaItem.Header<Type>> = emptyList(),
    val dateHeader: String = "",
    val error: String = "",
    val isLoading: Boolean = true
) {
    fun imagesCount(): Int = media.count { it.isImage }
    fun videosCount(): Int = media.count { it.isVideo }
    fun musicCount(): Int = media.count{it.isMusic}
    fun mediaCount(): Int = media.count()
}